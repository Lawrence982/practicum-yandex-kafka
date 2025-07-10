package ru.yandex.practicum.processor;

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
import ru.yandex.practicum.model.Message;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.serdes.MessageSerdes;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MessagesProcessor {

    @Value("${topic.messages.name}")
    private String messageTopicName;

    @Value("${topic.filtered-messages.name}")
    private String filteredMessageTopicName;

    @Autowired
    private BlockedUserProcessor blockedUserProcessor;

    //todo autowired real forbidden words storage
    private final Set<String> forbiddenWords = Set.of("forbidden", "evil", "bad");

    @Autowired
    public void process(StreamsBuilder streamsBuilder, StreamsBuilderFactoryBean factoryBean) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

        ReadOnlyKeyValueStore<String, User> blockedUsers = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        "blocked-user-store",
                        QueryableStoreTypes.keyValueStore()
                )
        );

        MessageSerdes messageSerdes = new MessageSerdes();

        streamsBuilder
                .stream(messageTopicName, Consumed.with(Serdes.String(), messageSerdes))
                .filter((id, message) -> isNotBlocked(message, blockedUsers))
                .mapValues(this::transformMessage)
                .to(filteredMessageTopicName, Produced.with(Serdes.String(), messageSerdes));
    }

    private boolean isNotBlocked(Message message, ReadOnlyKeyValueStore<String, User> blockedUsers) {
        String blockedUserStoreKey =
                blockedUserProcessor.createBlockedUserStoreKey(message.getUserId(), message.getRecipientId());
        User user = blockedUsers.get(blockedUserStoreKey);
        return user == null;
    }

    private Message transformMessage(Message message) {
        Message handledMessage = new Message();

        handledMessage.setId(message.getId());
        handledMessage.setUserId(message.getUserId());
        handledMessage.setRecipientId(message.getRecipientId());
        handledMessage.setTimestamp(message.getTimestamp());
        handledMessage.setMessage(message.getMessage().replaceAll(compileRegexString(), "***"));

        return handledMessage;
    }

    private String compileRegexString() {
        return forbiddenWords.stream()
                .map(word -> "\\b" + word + "\\b")
                .collect(Collectors.joining("|"));
    }
}
