package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Message;

import java.util.List;

public interface MessageService {

    Message sendMessage(Message message);

    List<Message> sendMessageBatch(List<Message> messages);

}
