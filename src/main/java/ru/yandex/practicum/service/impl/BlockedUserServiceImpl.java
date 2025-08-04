//package ru.yandex.practicum.service.impl;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.clients.producer.RecordMetadata;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//import ru.yandex.practicum.model.BlockedUser;
//import ru.yandex.practicum.service.BlockedUserService;
//
//import java.util.List;
//
//@Slf4j
//@Service
//public class BlockedUserServiceImpl implements BlockedUserService {
//
//    @Value("${topic.blocked-users.name}")
//    private String blockedUsersTopicName;
//
//    @Autowired
//    @Qualifier("kafkaTemplate")
//    private KafkaTemplate<String, BlockedUser> kafkaTemplate;
//
//    @Override
//    public void blockUser(BlockedUser blockedUser) {
//        log.info("Sending message: {}", blockedUser);
//
//        ProducerRecord<String, BlockedUser> record =
//                new ProducerRecord<>(blockedUsersTopicName, blockedUser.getUserId(), blockedUser);
//
//        kafkaTemplate.send(record).whenComplete((res, ex) -> {
//            if (ex != null) {
//                log.error("Failed to send message: {}", ex.getMessage());
//            } else {
//                RecordMetadata recordMeta = res.getRecordMetadata();
//                log.debug("Message has been sent to {}", recordMeta.topic());
//            }
//        });
//    }
//
//    @Override
//    public void blockUserBatch(List<BlockedUser> blockedUsers) {
//        for (BlockedUser user: blockedUsers) {
//            blockUser(user);
//        }
//    }
//}
