package ru.yandex.practicum.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.Notification;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${topic.name}")
public class NotificationConsumer {

    private final PayloadConverter converter;

    @KafkaHandler
    public void handle(@Payload Map<String, Object> payload) {
        try {
            Notification notification = converter.toNotification(payload);
            log.info("Consumer received notification: {}", notification);
        } catch (Exception e) {
            log.error("Failed to map payload to Notification", e);
            // лога не достаточно, нужно отправить в DLQ
        }
    }
}
