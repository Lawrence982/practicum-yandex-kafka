package ru.yandex.practicum.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Message {

    private UUID id;
    private String userId;
    private String recipientId;
    private String message;
    private LocalDateTime timestamp;

}
