package ru.yandex.practicum.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServices;

    @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
    private String trustedPackages;


    @Bean
    ConsumerFactory<String, Object> singleConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(prepareCommonConsumerConfig("single-group"));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> singleKafkaListenerContainerFactory(@Qualifier("singleConsumerFactory") ConsumerFactory<String, Object> singleConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(singleConsumerFactory);

        return factory;
    }

    @Bean
    ConsumerFactory<String, Object> batchConsumerFactory() {
        Map<String, Object> config = prepareCommonConsumerConfig("batch-group");
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1500);  // минимальный объём данных (в байтах), который консьюмер должен получить за один запрос к брокеру
        config.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 10000); // максимальное время ожидания для получения данных от брокера
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // отключает автоматический коммит оффсетов (для батч консьюмера)

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory(@Qualifier("batchConsumerFactory") ConsumerFactory<String, Object> batchConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(batchConsumerFactory);
        factory.setBatchListener(true);  // включает батч режим (для получения пачки сообщений)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE); // режим для немедлеменного ручного коммита оффсетов
        factory.setConcurrency(1);  // однопоточный режим

        return factory;
    }


    private Map<String, Object> prepareCommonConsumerConfig(String groupId) {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServices); // сервера брокеров
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // десериализация ключа
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class); // десериализатор для обработки ошибок, возникающих при десериализации сообщений
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);  // десериализация велью
        config.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages); // список доверенных пакетов, из которых разрешено десериализовать объекты
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); // айди консьюмер группы

        return config;
    }
}
