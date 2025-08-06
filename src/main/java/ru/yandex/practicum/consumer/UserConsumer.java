package ru.yandex.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.DebeziumDto;
import ru.yandex.practicum.dto.UserDto;
import ru.yandex.practicum.util.PayloadExtractor;

@Slf4j
@Component
@KafkaListener(topics = "${topic.users.name}")
public class UserConsumer {

    @Autowired
    PayloadExtractor payloadExtractor;

    @KafkaHandler
    public void handle(@Payload DebeziumDto debeziumDto) {
        UserDto user = payloadExtractor.extract(debeziumDto, UserDto.class);

        if (user != null) {
            log.info("Consumer received user: {}", user);
        } else {
            log.info("User was deleted");
        }
    }
}
