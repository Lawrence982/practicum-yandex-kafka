package ru.yandex.practicum.converter;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.avro.Product;

@Service
public class ProductAvroConverter {

    public Product toAvroProduct(ru.yandex.practicum.model.Product product) {
        return Product.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setSku(product.getSku())
                .build();
    }

    public ru.yandex.practicum.model.Product fromAvroProduct(Product product) {
        return ru.yandex.practicum.model.Product.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .build();
    }
}
