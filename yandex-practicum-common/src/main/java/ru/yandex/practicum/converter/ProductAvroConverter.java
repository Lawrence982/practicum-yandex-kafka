package ru.yandex.practicum.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.avro.Product;

@Service
public class ProductAvroConverter implements AvroConverter<Product, ru.yandex.practicum.model.Product> {

    @Autowired
    PriceAvroConverter priceAvroConverter;

    @Autowired
    StockAvroConverter stockAvroConverter;

    @Autowired
    ImageAvroConverter imageAvroConverter;

    @Autowired
    SpecificationAvroConverter specificationAvroConverter;

    @Override
    public Product toAvro(ru.yandex.practicum.model.Product product) {
        if (product == null) {
            return null;
        }
        return Product.newBuilder()
                .setProductId(product.getProductId())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setPrice(priceAvroConverter.toAvro(product.getPrice()))
                .setCategory(product.getCategory())
                .setBrand(product.getBrand())
                .setStock(stockAvroConverter.toAvro(product.getStock()))
                .setSku(product.getSku())
                .setTags(product.getTags())
                .setImages(imageAvroConverter.toAvro(product.getImages()))
                .setSpecifications(specificationAvroConverter.toAvro(product.getSpecifications()))
                .setCreatedAt(product.getCreatedAt())
                .setUpdatedAt(product.getUpdatedAt())
                .setIndex(product.getIndex())
                .setStoreId(product.getStoreId())
                .build();
    }

    @Override
    public ru.yandex.practicum.model.Product fromAvro(Product product) {
        if (product == null) {
            return null;
        }
        return ru.yandex.practicum.model.Product.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(priceAvroConverter.fromAvro(product.getPrice()))
                .category(product.getCategory())
                .brand(product.getBrand())
                .stock(stockAvroConverter.fromAvro(product.getStock()))
                .sku(product.getSku())
                .tags(product.getTags())
                .images(imageAvroConverter.fromAvro(product.getImages()))
                .specifications(specificationAvroConverter.fromAvro(product.getSpecifications()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .index(product.getIndex())
                .storeId(product.getStoreId())
                .build();
    }
}
