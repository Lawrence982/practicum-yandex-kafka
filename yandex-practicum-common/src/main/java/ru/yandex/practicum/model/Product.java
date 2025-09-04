package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class Product {

    private String id;
    private String name;
    private String description;
    private Price price;
    private String category;
    private String brand;
    private Stock stock;
    private String sku;
    private List<String> tags;
    private List<Image> images;
    private List<Specification> specifications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String index;
    private String storeId;

}
