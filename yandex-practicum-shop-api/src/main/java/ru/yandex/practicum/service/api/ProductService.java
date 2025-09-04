package ru.yandex.practicum.service.api;

import ru.yandex.practicum.model.Product;

import java.util.List;

public interface ProductService {

    void sendProduct(Product product);

    void sendProductBatch(List<Product> products);
}
