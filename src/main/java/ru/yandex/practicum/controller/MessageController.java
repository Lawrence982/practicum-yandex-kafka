//package ru.yandex.practicum.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.yandex.practicum.model.Message;
//import ru.yandex.practicum.model.Response;
//import ru.yandex.practicum.service.MessageService;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//
//@RestController
//@RequestMapping("/message")
//public class MessageController {
//
//    @Autowired
//    private MessageService messageService;
//
//    @PostMapping
//    public ResponseEntity<Response> sendMessage(@RequestBody Message message) {
//        Message sentMessage = messageService.sendMessage(message);
//        Response response = sentMessage == null ? new Response(Collections.emptyList()) : new Response(List.of(sentMessage));
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @PostMapping("/batch")
//    public ResponseEntity<Response> sendMessageBatch(@RequestBody List<Message> messages) {
//        List<Message> sentMessages = messageService.sendMessageBatch(messages);
//        return ResponseEntity.status(HttpStatus.CREATED).body(new Response(sentMessages));
//    }
//
//    @PostMapping("/censoredWord")
//    public ResponseEntity<Set<String>> addCensoredWords(@RequestBody List<String> words) {
//        Set<String> result = messageService.addCensoredWords(words);
//        return ResponseEntity.status(HttpStatus.CREATED).body(result);
//    }
//
//    @DeleteMapping("/censoredWord")
//    public ResponseEntity<Set<String>> deleteCensoredWords(@RequestBody List<String> words) {
//        Set<String> result = messageService.deleteCensoredWords(words);
//        return ResponseEntity.status(HttpStatus.CREATED).body(result);
//    }
//
//}
