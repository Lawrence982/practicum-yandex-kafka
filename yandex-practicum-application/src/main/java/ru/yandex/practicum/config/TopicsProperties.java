package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "topic")
public class TopicsProperties {

    private Topic products;
    private Topic filteredProducts;
    private Topic customerRequests;

    @Getter
    @Setter
    static class Topic {
        private String name;
        private Integer partitions;
        private Integer replicas;
        private String minInsyncReplicas;
    }
}
