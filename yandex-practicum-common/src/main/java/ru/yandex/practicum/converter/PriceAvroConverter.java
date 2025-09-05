package ru.yandex.practicum.converter;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.avro.Price;

@Service
public class PriceAvroConverter implements AvroConverter<Price, ru.yandex.practicum.model.Price> {

    @Override
    public Price toAvro(ru.yandex.practicum.model.Price price) {
        if (price == null) {
            return null;
        }
        return Price.newBuilder()
                .setAmount(price.getAmount())
                .setCurrency(price.getCurrency())
                .build();
    }

    @Override
    public ru.yandex.practicum.model.Price fromAvro(Price price) {
        if (price == null) {
            return null;
        }
        return ru.yandex.practicum.model.Price.builder()
                .amount(price.getAmount())
                .currency(price.getCurrency())
                .build();
    }
}
