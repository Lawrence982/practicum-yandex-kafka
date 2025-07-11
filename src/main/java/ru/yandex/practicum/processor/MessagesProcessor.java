package ru.yandex.practicum.processor;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.BlockedUser;
import ru.yandex.practicum.model.Message;
import ru.yandex.practicum.serdes.MessageSerdes;
import ru.yandex.practicum.service.MessageService;

import java.util.stream.Collectors;

import static ru.yandex.practicum.service.impl.MessageServiceImpl.censoredWords;

@Component
public class MessagesProcessor {

    @Value("${topic.messages.name}")
    private String messageTopicName;

    @Value("${topic.filtered-messages.name}")
    private String filteredMessageTopicName;

    @Value("${store.blocked-users.name}")
    private String blockedUsersStoreName;

    @Autowired
    private BlockedUserProcessor blockedUserProcessor;

    @Autowired
    private StreamsBuilder streamsBuilder;

    @Autowired
    private StreamsBuilderFactoryBean factoryBean;

    public void process() {

        MessageSerdes messageSerdes = new MessageSerdes();

        streamsBuilder
                .stream(messageTopicName, Consumed.with(Serdes.String(), messageSerdes))
                .filter((id, message) -> isNotBlocked(message))
                .mapValues(this::transformMessage)
                .to(filteredMessageTopicName, Produced.with(Serdes.String(), messageSerdes));
    }

    private boolean isNotBlocked(Message message) {

        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

        ReadOnlyKeyValueStore<String, BlockedUser> blockedUsers = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        blockedUsersStoreName,
                        QueryableStoreTypes.keyValueStore()
                )
        );

        String blockedUserStoreKey =
                blockedUserProcessor.createBlockedUserStoreKey(message.getUserId(), message.getRecipientId());
        BlockedUser blockedUser = blockedUsers.get(blockedUserStoreKey);
        return blockedUser == null;
    }

    private Message transformMessage(Message message) {
        Message handledMessage = new Message();

        handledMessage.setId(message.getId());
        handledMessage.setUserId(message.getUserId());
        handledMessage.setRecipientId(message.getRecipientId());
        handledMessage.setTimestamp(message.getTimestamp());
        if (!censoredWords.isEmpty()) {
            handledMessage.setMessage(message.getMessage().replaceAll(compileRegexString(), "***"));
        } else {
            handledMessage.setMessage(message.getMessage());
        }

        return handledMessage;
    }

    private String compileRegexString() {
        return censoredWords.stream()
                .map(word -> "\\b" + word + "\\b")
                .collect(Collectors.joining("|"));
    }

    @PostConstruct
    public void init() {
        process(); // запуск построения топологии после инициализации
    }
}


//package ru.yandex.practicum.processor;
//
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.kstream.Consumed;
//import org.apache.kafka.streams.kstream.Produced;
//import org.apache.kafka.streams.processor.api.Processor;
//import org.apache.kafka.streams.processor.api.ProcessorContext;
//import org.apache.kafka.streams.processor.api.ProcessorSupplier;
//import org.apache.kafka.streams.processor.api.Record;
//import org.apache.kafka.streams.state.KeyValueStore;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.model.BlockedUser;
//import ru.yandex.practicum.model.Message;
//import ru.yandex.practicum.serdes.MessageSerdes;
//
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
//@DependsOn("blockedUserProcessor")
//public class MessagesProcessor {
//
//    @Value("${topic.messages.name}")
//    private String messageTopicName;
//
//    @Value("${topic.filtered-messages.name}")
//    private String filteredMessageTopicName;
//
//    @Autowired
//    private StreamsBuilder streamsBuilder;
//
//    public void process() {
//        log.info("Building messages processing topology...");
//        MessageSerdes messageSerdes = new MessageSerdes();
//
//        streamsBuilder
//                .stream(messageTopicName, Consumed.with(Serdes.String(), messageSerdes))
//                .process(new ProcessorSupplier<String, Message, String, Message>() {
//                    @Override
//                    public Processor<String, Message, String, Message> get() {
//                        return new BlockedUserFilterProcessor();
//                    }
//                }, "blocked-user-store")
//                .to(filteredMessageTopicName, Produced.with(Serdes.String(), messageSerdes));
//    }
//
//    // Внутренний класс для обработки сообщений
//    private class BlockedUserFilterProcessor implements Processor<String, Message, String, Message> {
//        private ProcessorContext<String, Message> context;
//        private KeyValueStore<String, BlockedUser> blockedUsersStore;
//
//        @Override
//        public void init(ProcessorContext<String, Message> context) {
//            this.context = context;
//            this.blockedUsersStore = context.getStateStore("blocked-user-store");
//        }
//
//        @Override
//        public void process(org.apache.kafka.streams.processor.api.Record<String, Message> record) {
//            String storeKey = createBlockedUserStoreKey(record.value().getUserId(), record.value().getRecipientId());
//            BlockedUser blockedUser = blockedUsersStore.get(storeKey);
//
//            if (blockedUser == null) {
//                Message transformedMessage = transformMessage(record.value());
//                context.forward(new Record<>(record.key(), transformedMessage, record.timestamp()));
//            }
//        }
//
//        @Override
//        public void close() {
//            // Освобождение ресурсов (если нужно)
//        }
//    }
//
//    private String createBlockedUserStoreKey(String senderId, String recipientId) {
//        return senderId + "-" + recipientId;
//    }
//
//    private Message transformMessage(Message message) {
//        Message handledMessage = new Message();
//        handledMessage.setId(message.getId());
//        handledMessage.setUserId(message.getUserId());
//        handledMessage.setRecipientId(message.getRecipientId());
//        handledMessage.setTimestamp(message.getTimestamp());
//        handledMessage.setMessage(message.getMessage().replaceAll(compileRegexString(), "***"));
//        return handledMessage;
//    }
//
//    private String compileRegexString() {
//        Set<String> forbiddenWords = Set.of("forbidden", "evil", "bad");
//        return forbiddenWords.stream()
//                .map(word -> "\\b" + word + "\\b")
//                .collect(Collectors.joining("|"));
//    }
//
//    @PostConstruct
//    public void init() {
//        process(); // запуск построения топологии после инициализации
//    }
//}

