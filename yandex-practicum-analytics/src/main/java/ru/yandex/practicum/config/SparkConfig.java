package ru.yandex.practicum.config;

import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Value("${app.spark.appName}")
    private String appName;

    @Value("${app.spark.master}")
    private String master;

    @Value("${app.hdfs.host}")
    private String hdfsHost;

    @Bean(destroyMethod = "stop")
    public SparkSession spark() {
        return SparkSession.builder()
                .appName(appName)
                .master(master)
                .config("spark.hadoop.fs.defaultFS", hdfsHost)
                .config("spark.ui.enabled", "false")
                .config("spark.sql.shuffle.partitions", "200")
                .config("spark.driver.extraJavaOptions",
                        String.join(" ",
                                "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
                                "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
                                "--add-opens=java.base/java.nio=ALL-UNNAMED",
                                "--add-opens=java.base/java.lang=ALL-UNNAMED",
                                "--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED",
                                "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED"
                        )
                )
                .getOrCreate();
    }
}
