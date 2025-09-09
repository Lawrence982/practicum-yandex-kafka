package ru.yandex.practicum.service.api;

import ru.yandex.practicum.model.Product;

import java.util.List;

public interface CommandService {

    List<Product> searchByName(String name, String customerId);

    List<Product> getProductRecommendations();
}
