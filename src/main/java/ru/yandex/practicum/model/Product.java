package ru.yandex.practicum.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Product {

    private UUID id;
    private String name;

}