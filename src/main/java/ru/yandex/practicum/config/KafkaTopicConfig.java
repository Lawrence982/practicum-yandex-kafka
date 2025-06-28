package ru.yandex.practicum.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${product.topic.name}")
    private String productTopicName;

    @Value("${product.topic.partitions}")
    private Integer productTopicPartitions;

    @Value("${product.topic.replicas}")
    private Integer productTopicReplicas;

    @Value("${product.topic.min-insync-replicas}")
    private String minInsyncReplicas;

    @Bean
    NewTopic productTopic() {
        return TopicBuilder.name(productTopicName)
                .partitions(productTopicPartitions)
                .replicas(productTopicReplicas)
                // минимальное количество реплик, которые должны быть в синхронизации, прежде чем запись будет считаться успешной
                .configs(Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, minInsyncReplicas))
                .build();
    }
}
