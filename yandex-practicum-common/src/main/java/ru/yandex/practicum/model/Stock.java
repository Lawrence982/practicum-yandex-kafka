package ru.yandex.practicum.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stock {
    private Integer available;
    private Integer reserved;
}
