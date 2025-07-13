package ru.yandex.practicum.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Message;
import ru.yandex.practicum.service.MessageService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Value("${topic.messages.name}")
    private String messageTopicName;

    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<String, Message> kafkaTemplate;

    public final static Set<String> CENSORED_WORDS = new HashSet<>();

    @Override
    public Message sendMessage(Message message) {
        message.setId(UUID.randomUUID());
        message.setTimestamp(LocalDateTime.now());
        log.info("Sending message: {}", message);

        ProducerRecord<String, Message> record =
                new ProducerRecord<>(messageTopicName, message.getId().toString(), message);

        kafkaTemplate.send(record).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Failed to send message: {}", ex.getMessage());
            } else {
                RecordMetadata recordMeta = res.getRecordMetadata();
                log.debug("Message has been sent to {}", recordMeta.topic());
            }
        });

        return message;
    }

    @Override
    public List<Message> sendMessageBatch(List<Message> messages) {
        List<Message> result = new ArrayList<>(messages);
        for (Message message : messages) {
            result.add(sendMessage(message));
        }
        return result;
    }

    @Override
    public Set<String> addCensoredWords(List<String> words) {
        for (String word : words) {
            CENSORED_WORDS.add(word.toLowerCase());
        }
        return CENSORED_WORDS;
    }

    @Override
    public Set<String> deleteCensoredWords(List<String> words) {
        for (String word : words) {
            CENSORED_WORDS.remove(word.toLowerCase());
        }
        return CENSORED_WORDS;
    }

}
