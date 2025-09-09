package ru.yandex.practicum.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.service.api.CommandService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandServiceImpl implements CommandService {

    final ProductRepository productRepository;

    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Override
    public List<Product> getProductRecommendations() {
        return List.of();
    }
}
