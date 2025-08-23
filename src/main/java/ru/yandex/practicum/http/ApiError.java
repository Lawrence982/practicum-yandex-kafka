package ru.yandex.practicum.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    String code;
    int status;
    String message;
    String details;
    String path;
}
