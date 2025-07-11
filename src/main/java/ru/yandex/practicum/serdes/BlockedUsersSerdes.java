package ru.yandex.practicum.serdes;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.yandex.practicum.model.BlockedUser;

public class BlockedUsersSerdes extends Serdes.WrapperSerde<BlockedUser> {

    public BlockedUsersSerdes() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(BlockedUser.class));
    }
}
