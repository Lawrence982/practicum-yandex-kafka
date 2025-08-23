package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@Setter
public class Notification {
    UUID id;

    @NotBlank
    String name;
}
