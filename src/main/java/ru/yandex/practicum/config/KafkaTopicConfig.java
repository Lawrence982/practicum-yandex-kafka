package ru.yandex.practicum.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(TopicsProperties.class)
public class KafkaTopicConfig {

    @Autowired
    private TopicsProperties topicsProperties;

    @Bean
    NewTopic messageTopic() {
        return createTopic(topicsProperties.getMessages());
    }

    @Bean
    NewTopic filteredMessageTopic() {
        return createTopic(topicsProperties.getFilteredMessages());
    }

    @Bean
    NewTopic blockedUserTopic() {
        return createTopic(topicsProperties.getBlockedUsers());
    }

    private NewTopic createTopic(TopicsProperties.Topic topic) {
        return TopicBuilder.name(topic.getName())
                .partitions(topic.getPartitions())
                .replicas(topic.getReplicas())
                // минимальное количество реплик, которые должны быть в синхронизации, прежде чем запись будет считаться успешной
                .configs(Map.of(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, topic.getMinInsyncReplicas()))
                .build();
    }
}
