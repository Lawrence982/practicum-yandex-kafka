package ru.yandex.practicum.processor;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.serdes.BlockedUsersSerdes;

@Component
public class BlockedUserProcessor {

    @Value("${topic.blocked-users.name}")
    private String blockedUsersTopicName;

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {

        KeyValueBytesStoreSupplier blockedUserStore = Stores.persistentKeyValueStore("blocked-user-store");

        streamsBuilder
                .stream(blockedUsersTopicName, Consumed.with(Serdes.String(), new BlockedUsersSerdes()))
                .map((id, blockedUser) ->
                        KeyValue.pair(
                                createBlockedUserStoreKey(
                                        blockedUser.getId().toString(),
                                        blockedUser.getBlockedUserId()
                                ),
                                blockedUser))
                .toTable(Materialized.as(blockedUserStore));
    }

    public String createBlockedUserStoreKey(String senderId, String recipientId) {
        return senderId + "-" + recipientId;
    }
}
