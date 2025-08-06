package ru.yandex.practicum.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OrderDto(Long id, Long userId, String productName, Integer quantity, Instant orderDate) {
}
