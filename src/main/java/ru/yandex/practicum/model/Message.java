package ru.yandex.practicum.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Message {

    private UUID id;
    private String user_id;
    private String recipient_id;
    private String message;
    private LocalDateTime timestamp;

}
