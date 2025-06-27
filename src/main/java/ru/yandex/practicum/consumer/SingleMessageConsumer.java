package ru.yandex.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Product;

@Slf4j
@Component
@KafkaListener(topics = "${product.topic.name}", containerFactory = "singleKafkaListenerContainerFactory")
public class SingleMessageConsumer {

    @KafkaHandler
    public void handle(@Payload Product product) {
        log.info("Single consumer received message: {}", product);
    }
}
