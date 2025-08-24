package ru.yandex.practicum.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${topic.name}")
public class NotificationConsumer {

    @KafkaHandler
    public void handle(@Payload GenericData.Record record) {
        try {
            log.info("Consumer received notification: {}", record);
        } catch (Exception e) {
            log.error("Failed to map payload to Notification", e);
            // лога не достаточно, нужно отправить в DLQ
        }
    }
}
