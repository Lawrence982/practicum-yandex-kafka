package ru.yandex.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.Notification;

@Slf4j
@Component
@KafkaListener(topics = "${topic.second.name}", containerFactory = "secondTopicKafkaListenerContainerFactory")
public class SecondConsumer {

    @KafkaHandler
    public void handle(@Payload Notification notification) {
        log.info("Second consumer received notification: {}", notification);
    }
}
