package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Price {
    private Double amount;
    private String currency;
}
