package ru.yandex.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.DebeziumDto;
import ru.yandex.practicum.dto.UserDto;

@Slf4j
@Component
@KafkaListener(topics = "${topic.users.name}",
        properties = {"spring.json.value.default.type=ru.yandex.practicum.dto.DebeziumDto"})
public class UserConsumer {

    @KafkaHandler
    public void handle(@Payload DebeziumDto debeziumDto) {

        log.info("Consumer received user: {}", debeziumDto.payload());
    }
}
