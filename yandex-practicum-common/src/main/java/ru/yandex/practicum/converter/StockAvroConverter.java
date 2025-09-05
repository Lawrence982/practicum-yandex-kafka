package ru.yandex.practicum.converter;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.avro.Stock;

@Service
public class StockAvroConverter implements AvroConverter<Stock, ru.yandex.practicum.model.Stock>{

    @Override
    public Stock toAvro(ru.yandex.practicum.model.Stock stock) {
        if (stock == null) {
            return null;
        }
        return Stock.newBuilder()
                .setAvailable(stock.getAvailable())
                .setReserved(stock.getReserved())
                .build();
    }

    @Override
    public ru.yandex.practicum.model.Stock fromAvro(Stock stock) {
        if (stock == null) {
            return null;
        }
        return ru.yandex.practicum.model.Stock.builder()
                .available(stock.getAvailable())
                .reserved(stock.getReserved())
                .build();
    }
}
