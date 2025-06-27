package ru.yandex.practicum.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Value("${product.topic.name}")
    private String productTopicName;

    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<String, Product> kafkaTemplate;

    @Override
    public Product createAndNotify(Product product) {
        product.setId(UUID.randomUUID());
        log.info("Sending product: {}", product);

        ProducerRecord<String, Product> record =
                new ProducerRecord<>(productTopicName, product.getId().toString(), product);

        kafkaTemplate.send(record).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Failed to send message: {}", ex.getMessage());
            } else {
                RecordMetadata recordMeta = res.getRecordMetadata();
                log.debug("Message has been sent to {}", recordMeta.topic());
            }
        });

        return product;
    }

    @Override
    public List<Product> createBatchAndNotify(Product template, Integer amount) {
        List<Product> products = new ArrayList<>(amount);
        for (int num = 1; num <= amount; num++) {
            Product product = new Product();
            product.setName("%s #%d".formatted(template.getName(), num));
            products.add(createAndNotify(product));
        }
        return products;
    }

}
