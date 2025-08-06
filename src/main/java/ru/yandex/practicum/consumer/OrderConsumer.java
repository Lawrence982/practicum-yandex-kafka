package ru.yandex.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.DebeziumDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.util.PayloadExtractor;

import java.util.List;

@Slf4j
@Component
@KafkaListener(topics = "${topic.orders.name}", batch = "true")
public class OrderConsumer {

    @Autowired
    PayloadExtractor payloadExtractor;

    @KafkaHandler
    public void handle(@Payload List<DebeziumDto> dtoList) {
        payloadExtractor.extract(dtoList, OrderDto.class).forEach(this::handle);
    }

    private void handle(OrderDto order) {
        if (order != null) {
            log.info("Consumer received order: {}", order);
        } else {
            log.info("Order was deleted");
        }
    }
}
