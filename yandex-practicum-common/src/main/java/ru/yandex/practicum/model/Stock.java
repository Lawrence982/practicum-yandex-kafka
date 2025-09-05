package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Stock {
    private Integer available;
    private Integer reserved;
}
