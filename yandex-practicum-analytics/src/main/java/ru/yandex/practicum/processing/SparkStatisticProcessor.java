package ru.yandex.practicum.processing;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

@Slf4j
@Component
public class SparkStatisticProcessor {

    @Autowired
    private SparkSession spark;

    @Autowired
    private CustomerAggregator customerAggregator;

    @Value("${app.hdfs.dataPath}")
    private String dataPath;

    @Value("${app.hdfs.checkpointPath}")
    private String checkpointPath;

    @Value("${app.spark.trigger}")
    private String triggerEvery;

    @EventListener(ApplicationReadyEvent.class)
    public void process() throws TimeoutException {
        createDirectoryInHadoopIfNotExist(dataPath);
        createDirectoryInHadoopIfNotExist(checkpointPath);

        Dataset<Row> input = spark.readStream()
                .schema(createCustomerRequestSchema())
                .json(dataPath);

        input.writeStream()
                .trigger(Trigger.ProcessingTime(triggerEvery))
                .option("checkpointLocation", checkpointPath)
                .foreachBatch((batch, batchId) -> {
                    Dataset<Row> grouped = groupByProductForCustomer(batch);
                    List<RowAggregation> rows = transformToRowAggregation(grouped).collectAsList();
                    customerAggregator.applyIncrements(batchId, rows);
                })
                .start();
    }

    private void createDirectoryInHadoopIfNotExist(String directoryName) {
        Path path = new Path(directoryName);
        try (FileSystem fileSystem = FileSystem.get(spark.sparkContext().hadoopConfiguration())) {
            if (fileSystem.mkdirs(path)) {
                log.debug("Directory {} created in hdfs", directoryName);
            } else {
                log.warn("Directory {} could not be created in hdfs", directoryName);
            }
        } catch (IOException e) {
            log.error("Cannot create directory in hdfs for path {}", directoryName, e);
            throw new RuntimeException(e);
        }
    }

    private Dataset<Row> groupByProductForCustomer(Dataset<Row> batch) {
        return batch
                .withColumn("p", explode(col("products")))
                .select(
                        col("customerId"),
                        col("p.productId").alias("productId"),
                        col("p.productName").alias("productName")
                )
                .groupBy(col("customerId"), col("productId"), col("productName"))
                .agg(count(lit(1)).alias("cnt"));
    }

    private Dataset<RowAggregation> transformToRowAggregation(Dataset<Row> rowDataset) {
        return rowDataset.select(
                col("customerId").as("customerId"),
                col("productId").as("productId"),
                col("productName").as("productName"),
                col("cnt").as("count")
        ).as(Encoders.bean(RowAggregation.class));
    }

    private StructType createCustomerRequestSchema() {
        StructType productSchema = new StructType()
                .add("productId", DataTypes.StringType)
                .add("productName", DataTypes.StringType)
                .add("category", DataTypes.StringType);

        return new StructType()
                .add("customerId", DataTypes.StringType)
                .add("products", DataTypes.createArrayType(productSchema));
    }
}
