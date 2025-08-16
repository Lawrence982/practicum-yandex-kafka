package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "topic")
public class TopicsProperties {

    private Topic first;
    private Topic second;

    @Getter
    @Setter
    static class Topic {
        private String name;
        private Integer partitions;
        private Integer replicas;
        private String minInsyncReplicas;
    }
}
