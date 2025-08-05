package ru.yandex.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.DebeziumDto;
import ru.yandex.practicum.dto.DebeziumDto2;
import ru.yandex.practicum.dto.OrderDto;

@Slf4j
@Component
@KafkaListener(topics = "${topic.orders.name}")
public class OrderConsumer {

    @KafkaHandler
    public void handle(@Payload DebeziumDto2 debeziumDto) {

        if (debeziumDto.payload() != null) {
            log.info("Consumer received order: {}", debeziumDto.payload());
        } else {
            log.info("Order was deleted");
        }
    }
}
