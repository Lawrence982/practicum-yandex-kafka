package ru.yandex.practicum.serdes;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.yandex.practicum.model.User;

public class BlockedUsersSerdes extends Serdes.WrapperSerde<User> {

    public BlockedUsersSerdes() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(User.class));
    }
}
