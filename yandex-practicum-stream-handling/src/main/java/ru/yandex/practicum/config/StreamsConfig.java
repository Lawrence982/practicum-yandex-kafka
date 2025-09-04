package ru.yandex.practicum.config;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.model.avro.Product;

@Configuration
@EnableConfigurationProperties(StreamsProperties.class)
public class StreamsConfig {

    @Autowired
    private StreamsProperties streamsProperties;

    @Bean
    SpecificAvroSerde<Product> specificAvroSerde() {
        SpecificAvroSerde<Product> productSerdes = new SpecificAvroSerde<>();
        productSerdes.configure(streamsProperties.getProperties(), false);
        return productSerdes;
    }
}
