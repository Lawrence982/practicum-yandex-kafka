package ru.yandex.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.Notification;

@Slf4j
@Component
@KafkaListener(topics = "${topic.first.name}", containerFactory = "firstTopicKafkaListenerContainerFactory")
public class FirstConsumer {

    @KafkaHandler
    public void handle(@Payload Notification notification) {
        log.info("First consumer received notification: {}", notification);
    }
}
