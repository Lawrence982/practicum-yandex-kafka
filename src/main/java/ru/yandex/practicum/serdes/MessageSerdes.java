package ru.yandex.practicum.serdes;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.yandex.practicum.model.Message;

public class MessageSerdes extends Serdes.WrapperSerde<Message> {

    public MessageSerdes() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(Message.class));
    }
}