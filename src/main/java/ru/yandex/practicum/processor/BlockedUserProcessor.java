package ru.yandex.practicum.processor;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.BlockedUser;
import ru.yandex.practicum.model.Message;
import ru.yandex.practicum.serdes.BlockedUsersSerdes;

@Slf4j
@Component
public class BlockedUserProcessor {

    @Value("${topic.blocked-users.name}")
    private String blockedUsersTopicName;

    @Value("${store.blocked-users.name}")
    private String blockedUsersStoreName;

    @Autowired
    private StreamsBuilder streamsBuilder;

    @Autowired
    private StreamsBuilderFactoryBean factoryBean;

    @PostConstruct
    public void init() {
        process(); // запуск построения топологии после инициализации
    }

    public void process() {
        // Создаём персистентный стор
        KeyValueBytesStoreSupplier blockedUserStore =
                Stores.persistentKeyValueStore(blockedUsersStoreName);

        streamsBuilder
                .stream(blockedUsersTopicName, Consumed.with(Serdes.String(), new BlockedUsersSerdes()))
                .map((id, blockedUser) ->
                        KeyValue.pair(
                                createBlockedUserStoreKey(blockedUser.getBlockedUserId(), blockedUser.getUserId()),
                                blockedUser
                        )
                )
                .peek((id, blockedUser) -> log.info("Store blocker user: {}", blockedUser))
                .toTable(
                        Materialized.<String, BlockedUser>as(blockedUserStore)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new BlockedUsersSerdes())
                );
    }

    public boolean isMessageFromNotBlockedUser(Message message) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

        ReadOnlyKeyValueStore<String, BlockedUser> blockedUsers = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        blockedUsersStoreName,
                        QueryableStoreTypes.keyValueStore()
                )
        );

        String blockedUserStoreKey = createBlockedUserStoreKey(message.getUserId(), message.getRecipientId());
        BlockedUser blockedUser = blockedUsers.get(blockedUserStoreKey);

        return blockedUser == null;
    }

    private String createBlockedUserStoreKey(String senderId, String recipientId) {
        return senderId + "-" + recipientId;
    }
}
