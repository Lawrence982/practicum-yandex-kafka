package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Image {
    private String url;
    private String alt;
}
