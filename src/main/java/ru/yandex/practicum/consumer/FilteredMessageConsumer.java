//package ru.yandex.practicum.consumer;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaHandler;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.model.Message;
//
//@Slf4j
//@Component
//@KafkaListener(topics = "${topic.filtered-messages.name}")
//public class FilteredMessageConsumer {
//
//    @KafkaHandler
//    public void handle(@Payload Message message) {
//        log.info("Consumer received filtered message: {}", message);
//    }
//}
