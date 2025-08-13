package ru.yandex.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@Setter
public class Notification {
    UUID id;
    String name;
}
