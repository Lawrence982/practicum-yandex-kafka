package ru.yandex.practicum.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.service.api.CommandService;

import java.util.List;

@Service
public class CommandServiceImpl implements CommandService {

    @Override
    public List<Product> searchByName(String name) {
        return List.of();
    }

    @Override
    public List<Product> getProductRecommendations() {
        return List.of();
    }
}
