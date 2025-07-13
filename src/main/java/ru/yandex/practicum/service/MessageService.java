package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Message;

import java.util.List;
import java.util.Set;

public interface MessageService {

    Message sendMessage(Message message);

    List<Message> sendMessageBatch(List<Message> messages);

    Set<String> addCensoredWords(List<String> words);

    Set<String> deleteCensoredWords(List<String> words);
}
