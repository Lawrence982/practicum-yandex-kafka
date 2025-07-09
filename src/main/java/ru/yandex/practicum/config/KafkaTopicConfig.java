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

    // Message Topic Configs
    @Value("${topic.messages.name}")
    private String messageTopicName;

    @Value("${topic.messages.partitions}")
    private Integer messageTopicPartitions;

    @Value("${topic.messages.replicas}")
    private Integer messageTopicReplicas;

    @Value("${topic.messages.min-insync-replicas}")
    private String messageMinInsyncReplicas;

    // Filtered Message Topic Configs
    @Value("${topic.filtered-messages.name}")
    private String filteredMessageTopicName;

    @Value("${topic.filtered-messages.partitions}")
    private Integer filteredMessageTopicPartitions;

    @Value("${topic.filtered-messages.replicas}")
    private Integer filteredMessageTopicReplicas;

    @Value("${topic.filtered-messages.min-insync-replicas}")
    private String filteredMessageMinInsyncReplicas;

    // Blocked User Topic Configs
    @Value("${topic.blocked-users.name}")
    private String blockedUserTopicName;

    @Value("${topic.blocked-users.partitions}")
    private Integer blockedUserTopicPartitions;

    @Value("${topic.blocked-users.replicas}")
    private Integer blockedUserTopicReplicas;

    @Value("${topic.blocked-users.min-insync-replicas}")
    private String blockedUserMinInsyncReplicas;

    @Bean
    NewTopic messageTopic() {
        return TopicBuilder.name(messageTopicName)
                .partitions(messageTopicPartitions)
                .replicas(messageTopicReplicas)
                // минимальное количество реплик, которые должны быть в синхронизации, прежде чем запись будет считаться успешной
                .configs(Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, messageMinInsyncReplicas))
                .build();
    }

    @Bean
    NewTopic filteredMessageTopic() {
        return TopicBuilder.name(filteredMessageTopicName)
                .partitions(filteredMessageTopicPartitions)
                .replicas(filteredMessageTopicReplicas)
                .configs(Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, filteredMessageMinInsyncReplicas))
                .build();
    }

    @Bean
    NewTopic blockedUserTopic() {
        return TopicBuilder.name(blockedUserTopicName)
                .partitions(blockedUserTopicPartitions)
                .replicas(blockedUserTopicReplicas)
                .configs(Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, blockedUserMinInsyncReplicas))
                .build();
    }
}
