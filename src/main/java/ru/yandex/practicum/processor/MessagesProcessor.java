package ru.yandex.practicum.processor;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Message;
import ru.yandex.practicum.serdes.MessageSerdes;

import java.util.stream.Collectors;

import static ru.yandex.practicum.service.impl.MessageServiceImpl.CENSORED_WORDS;

@Component
public class MessagesProcessor {

    @Value("${topic.messages.name}")
    private String messageTopicName;

    @Value("${topic.filtered-messages.name}")
    private String filteredMessageTopicName;

    @Autowired
    private BlockedUserProcessor blockedUserProcessor;

    @Autowired
    private StreamsBuilder streamsBuilder;

    @PostConstruct
    public void init() {
        process(); // запуск построения топологии после инициализации
    }

    public void process() {

        MessageSerdes messageSerdes = new MessageSerdes();

        streamsBuilder
                .stream(messageTopicName, Consumed.with(Serdes.String(), messageSerdes))
                .filter((id, message) -> blockedUserProcessor.isMessageFromNotBlockedUser(message))
                .mapValues(this::transformMessage)
                .to(filteredMessageTopicName, Produced.with(Serdes.String(), messageSerdes));
    }

    private Message transformMessage(Message message) {
        Message handledMessage = new Message();

        handledMessage.setId(message.getId());
        handledMessage.setUserId(message.getUserId());
        handledMessage.setRecipientId(message.getRecipientId());
        handledMessage.setTimestamp(message.getTimestamp());
        if (!CENSORED_WORDS.isEmpty()) {
            handledMessage.setMessage(message.getMessage().replaceAll(compileRegexString(), "***"));
        } else {
            handledMessage.setMessage(message.getMessage());
        }

        return handledMessage;
    }

    private String compileRegexString() {
        return CENSORED_WORDS.stream()
                .map(word -> "\\b" + word + "\\b")
                .collect(Collectors.joining("|"));
    }
}

