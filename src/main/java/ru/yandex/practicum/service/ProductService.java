package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Product;

import java.util.List;

public interface ProductService {
    Product createAndNotify(Product product);

    List<Product> createBatchAndNotify(Product template, Integer amount);
}
