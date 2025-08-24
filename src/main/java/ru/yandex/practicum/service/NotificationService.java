package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.avro.Notification;

import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    @Value("${topic.name}")
    private String topicName;

    @Autowired
    KafkaTemplate<String, Notification> kafkaTemplate;

    public void sendNotification(ru.yandex.practicum.dto.Notification notification) {

        notification.setId(UUID.randomUUID());
        log.info("Sending notification: {}", notification);

        // конвертация POJO → Avro SpecificRecord
        Notification avroNotification = Notification.newBuilder()
                .setId(notification.getId().toString())
                .setName(notification.getName())
                .build();

        ProducerRecord<String, Notification> record =
                new ProducerRecord<>(topicName, avroNotification.getId(), avroNotification);

        kafkaTemplate.send(record)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message: {}", ex.getMessage());
                    } else {
                        RecordMetadata recordMeta = res.getRecordMetadata();
                        log.debug("Message has been sent to {}", recordMeta.topic());
                    }
                });
    }
}
