package ru.yandex.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.avro.Product;

@Slf4j
@Component
@KafkaListener(topics = "${topic.filtered-products.name}")
public class FilteredProductConsumer {

    @KafkaHandler
    public void handle(@Payload Product product) {
        try {
            log.info("Consumer received product: {}", product);
        } catch (Exception e) {
            log.error("Failed to map payload to product", e);
            // лога не достаточно, нужно отправить в DLQ
        }
    }

}
