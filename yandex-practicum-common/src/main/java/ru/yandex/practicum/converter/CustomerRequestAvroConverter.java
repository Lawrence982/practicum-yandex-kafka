package ru.yandex.practicum.converter;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.avro.CustomerRequest;
import ru.yandex.practicum.model.avro.ProductInfo;

import java.util.List;

@Service
public class CustomerRequestAvroConverter {

    public  CustomerRequest toAvro(List<Product> products, String customerId) {
        return CustomerRequest.newBuilder()
                .setCustomerId(customerId)
                .setProducts(products
                        .stream()
                        .map(product -> ProductInfo.newBuilder()
                                .setProductId(product.getProductId())
                                .setProductName(product.getName())
                                .setCategory(product.getCategory())
                                .build())
                        .toList())
                .build();
    }
}
