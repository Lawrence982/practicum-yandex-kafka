package ru.yandex.practicum.processor;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.avro.Product;
import ru.yandex.practicum.service.api.ForbiddenSkuService;

@Component
public class ProductProcessor {

    @Value("${topic.products.name}")
    private String productTopicName;

    @Value("${topic.filtered-products.name}")
    private String filteredProductTopicName;

    @Autowired
    private ForbiddenSkuService forbiddenSkuService;

    @Autowired
    private StreamsBuilder streamsBuilder;

    @Autowired
    private SpecificAvroSerde<Product> specificAvroSerde;

    @PostConstruct
    public void init() {
        process(); // запуск построения топологии после инициализации
    }

    public void process() {
        streamsBuilder
                .stream(productTopicName, Consumed.with(Serdes.String(), specificAvroSerde))
                .filter((id, product) -> !forbiddenSkuService.isSkuForbidden(product.getSku()))
                .to(filteredProductTopicName, Produced.with(Serdes.String(), specificAvroSerde));
    }
}

