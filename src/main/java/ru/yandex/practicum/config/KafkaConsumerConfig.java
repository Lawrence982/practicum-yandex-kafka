//package ru.yandex.practicum.config;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
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
//import ru.yandex.practicum.dto.DebeziumDto;
//import ru.yandex.practicum.dto.UserDto;
//
//import java.util.HashMap;
//import java.util.Map;
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
//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;
//
//    @Bean
//    ConcurrentKafkaListenerContainerFactory<String, DebeziumDto<UserDto>> userKafkaListenerContainerFactory(@Qualifier("userConsumerFactory") ConsumerFactory<String, DebeziumDto<UserDto>> singleConsumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<String, DebeziumDto<UserDto>> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(singleConsumerFactory);
//
//        return factory;
//    }
//
//    @Bean
//    ConsumerFactory<String, DebeziumDto<UserDto>> userConsumerFactory(@Qualifier("jsonDeserializer") JsonDeserializer<DebeziumDto<UserDto>> jsonDeserializer) {
//        Map<String, Object> config = prepareCommonConsumerConfig(groupId);
//
//        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), jsonDeserializer);
//    }
//
//    @Bean
//    public JsonDeserializer<DebeziumDto<UserDto>> jsonDeserializer() {
//        JsonDeserializer<DebeziumDto<UserDto>> deserializer = new JsonDeserializer<>();
//        deserializer.trustedPackages(trustedPackages);
//        deserializer.setUseTypeMapperForKey(false);
//        deserializer.setRemoveTypeHeaders(false);
//        deserializer.configure(Map.of("json.deserialize.type", "ru.yandex.practicum.dto.DebeziumDto"), false);
//        return deserializer;
//    }
//
//
//    private Map<String, Object> prepareCommonConsumerConfig(String groupId) {
//        Map<String, Object> config = new HashMap<>();
//
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServices); // сервера брокеров
//        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // десериализация ключа
//        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class); // десериализатор для обработки ошибок, возникающих при десериализации сообщений
//        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);  // десериализация велью
////        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.yandex.practicum.dto.DebeziumDto");
////        config.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages); // список доверенных пакетов, из которых разрешено десериализовать объекты
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); // айди консьюмер группы
//
//        return config;
//    }
//}
