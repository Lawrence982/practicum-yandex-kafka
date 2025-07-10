package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.Message;
import ru.yandex.practicum.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        Message createdProduct = messageService.sendMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Message>> sendMessageBatch(@RequestBody List<Message> messages) {
        List<Message> result = messageService.sendMessageBatch(messages);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
