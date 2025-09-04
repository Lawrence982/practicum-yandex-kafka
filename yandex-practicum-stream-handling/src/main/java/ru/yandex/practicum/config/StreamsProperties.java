package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.streams")
public class StreamsProperties {
    String applicationId;
    Map<String, String> properties;
}
