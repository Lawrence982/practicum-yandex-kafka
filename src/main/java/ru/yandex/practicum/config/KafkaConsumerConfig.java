//package ru.yandex.practicum.config;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.config.SaslConfigs;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import ru.yandex.practicum.dto.Notification;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.apache.kafka.clients.admin.AdminClientConfig.SECURITY_PROTOCOL_CONFIG;
//import static org.apache.kafka.common.config.SslConfigs.*;
//
//@Configuration
//public class KafkaConsumerConfig {
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServices;
//
//    @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages}")
//    private String trustedPackages;
//
//    @Value("${spring.kafka.consumer.properties.spring.json.value.default.type}")
//    private String defaultType;
//
//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;
//
//    @Value("${spring.kafka.properties.sasl.mechanism}")
//    private String saslMechanism;
//
//    @Value("${spring.kafka.properties.security.protocol}")
//    private String securityProtocol;
//
//    @Value("${spring.kafka.ssl.key-password}")
//    private String keyPassword;
//
//    @Bean
//    public ConsumerFactory<String, Notification> firstTopicConsumerFactory() {
//        Map<String, Object> props = prepareCommonConsumerConfig(groupId);
//
//        props.put(SaslConfigs.SASL_JAAS_CONFIG,
//                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"consumer1\" password=\"admin-secret\";");
//
//        return new DefaultKafkaConsumerFactory<>(props);
//    }
//
//    @Bean
//    public ConsumerFactory<String, Notification> secondTopicConsumerFactory() {
//        Map<String, Object> props = prepareCommonConsumerConfig(groupId);
//
//        props.put(SaslConfigs.SASL_JAAS_CONFIG,
//                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"consumer2\" password=\"admin-secret\";");
//
//        return new DefaultKafkaConsumerFactory<>(props);
//    }
//
//    @Bean
//    ConcurrentKafkaListenerContainerFactory<String, Notification> firstTopicKafkaListenerContainerFactory(@Qualifier("firstTopicConsumerFactory") ConsumerFactory<String, Notification> firstTopicConsumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<String, Notification> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(firstTopicConsumerFactory);
//
//        return factory;
//    }
//
//    @Bean
//    ConcurrentKafkaListenerContainerFactory<String, Notification> secondTopicKafkaListenerContainerFactory(@Qualifier("secondTopicConsumerFactory") ConsumerFactory<String, Notification> secondTopicConsumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<String, Notification> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(secondTopicConsumerFactory);
//
//        return factory;
//    }
//
//    private Map<String, Object> prepareCommonConsumerConfig(String groupId) {
//        Map<String, Object> config = new HashMap<>();
//
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServices); // сервера брокеров
//        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // десериализация ключа
//        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class); // десериализатор для обработки ошибок, возникающих при десериализации сообщений
//        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);  // десериализация велью
//        config.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages); // список доверенных пакетов, из которых разрешено десериализовать объекты
//        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, defaultType); // список доверенных пакетов, из которых разрешено десериализовать объекты
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); // айди консьюмер группы
//
//        config.put(SECURITY_PROTOCOL_CONFIG, securityProtocol);
//        config.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
//
//        config.put(SSL_TRUSTSTORE_LOCATION_CONFIG, "/opt/yandex-kafka/kafka-0-creds/kafka.truststore.jks");
//        config.put(SSL_TRUSTSTORE_PASSWORD_CONFIG, keyPassword);
//        config.put(SSL_KEYSTORE_LOCATION_CONFIG, "/opt/yandex-kafka/kafka-0-creds/kafka.keystore.jks");
//        config.put(SSL_KEYSTORE_PASSWORD_CONFIG, keyPassword);
//
//        return config;
//    }
//
//}
