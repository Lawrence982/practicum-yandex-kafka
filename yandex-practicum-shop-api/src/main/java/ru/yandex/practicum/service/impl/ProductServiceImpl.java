package ru.yandex.practicum.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.converter.ProductAvroConverter;
import ru.yandex.practicum.model.avro.Product;
import ru.yandex.practicum.service.api.ProductService;

import java.util.*;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Value("${topic.products.name}")
    private String productTopicName;

    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<String, Product> kafkaTemplate;

    @Autowired
    private ProductAvroConverter converter;

    @Override
    public void sendProduct(ru.yandex.practicum.model.Product product) {
        log.info("Sending product: {}", product);

        ProducerRecord<String, Product> record =
                new ProducerRecord<>(productTopicName, product.getProductId(), converter.toAvro(product));

        kafkaTemplate.send(record).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Failed to send product: {}", ex.getMessage());
            } else {
                RecordMetadata recordMeta = res.getRecordMetadata();
                log.debug("Product has been sent to {}", recordMeta.topic());
            }
        });
    }

    @Override
    public void sendProductBatch(List<ru.yandex.practicum.model.Product> products) {
        products.forEach(this::sendProduct);
    }

}
