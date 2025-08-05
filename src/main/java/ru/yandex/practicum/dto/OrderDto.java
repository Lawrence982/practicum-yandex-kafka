package ru.yandex.practicum.dto;

import java.time.LocalDate;

public record OrderDto(Long id, Long userId, String productName, String email, Integer quantity, LocalDate orderDate) {
}
