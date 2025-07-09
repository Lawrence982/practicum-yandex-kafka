//package ru.yandex.practicum;
//
//import org.apache.kafka.clients.admin.AdminClient;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.Consumed;
//import org.apache.kafka.streams.kstream.GlobalKTable;
//import org.apache.kafka.streams.kstream.KStream;
//import org.apache.kafka.streams.kstream.Produced;
//
//import java.util.Arrays;
//import java.util.Properties;
//import java.util.concurrent.CountDownLatch;
//
//public class UserSegmentKeyTransformer {
//    private static final String BOOTSTRAP_SERVERS = "kafka-0:9092";
//    private static final String PURCHASE_TOPIC = "purchase-topic";
//    private static final String USER_SEGMENT_TOPIC = "user-segment-topic";
//    private static final String ENHANCED_PURCHASE_TOPIC = "enhanced-purchase-topic";
//
//    public static void main(String[] args) {
//        // Создаем топики
//        createTopics();
//
//        // Загружаем тестовые данные
//        loadTestData();
//
//        // Настройка Kafka Streams
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "user-segment-transformer");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//
//        // Строим топологию
//        StreamsBuilder builder = new StreamsBuilder();
//
//        // Загружаем информацию о сегментах пользователей из топика в GlobalKTable
//        // Формат: ключ - userId, значение - сегмент (VIP, Standard, New)
//        GlobalKTable<String, String> userSegments = builder.globalTable(
//                USER_SEGMENT_TOPIC,
//                Consumed.with(Serdes.String(), Serdes.String())
//        );
//
//        // Поток с покупками пользователей
//        // Формат: ключ - userId, значение - данные о покупке
//        KStream<String, String> purchaseStream = builder.stream(
//                PURCHASE_TOPIC,
//                Consumed.with(Serdes.String(), Serdes.String())
//        );
//
//        // Соединяем данные о покупках с сегментами пользователей
//        // и создаем новый ключ в формате "Segment-UserId"
//        KStream<String, String> enhancedPurchaseStream = purchaseStream
//                .join(
//                        // Соединяем с глобальной таблицей сегментов
//                        userSegments,
//                        // Функция извлечения ключа для поиска в GlobalKTable
//                        (purchaseKey, purchaseValue) -> purchaseKey,
//                        // Функция соединения данных (меняем только ключ, значение оставляем прежним)
//                        (purchaseValue, segmentValue) -> new SegmentedPurchase(segmentValue, purchaseValue)
//                )
//                // Преобразуем ключ в формат "Segment-UserId"
//                .map((userId, segmentedPurchase) -> {
//                    String newKey = segmentedPurchase.segment + "-" + userId;
//                    return KeyValue.pair(newKey, segmentedPurchase.purchaseData);
//                });
//
//        // Отправляем результат в выходной топик
//        enhancedPurchaseStream.to(
//                ENHANCED_PURCHASE_TOPIC,
//                Produced.with(Serdes.String(), Serdes.String())
//        );
//
//        // Запускаем приложение
//        KafkaStreams streams = new KafkaStreams(builder.build(), props);
//        streams.start();
//    }
//
//    // Класс для хранения информации о сегменте и данных покупки
//    static class SegmentedPurchase {
//        public final String segment;
//        public final String purchaseData;
//
//        public SegmentedPurchase(String segment, String purchaseData) {
//            this.segment = segment;
//            this.purchaseData = purchaseData;
//        }
//    }
//
//    // Вспомогательные методы
//
//    private static void createTopics() {
//        Properties adminProps = new Properties();
//        adminProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
//
//        try (AdminClient adminClient = AdminClient.create(adminProps)) {
//            NewTopic purchaseTopic = new NewTopic(PURCHASE_TOPIC, 1, (short) 1);
//            NewTopic userSegmentTopic = new NewTopic(USER_SEGMENT_TOPIC, 1, (short) 1);
//            NewTopic enhancedPurchaseTopic = new NewTopic(ENHANCED_PURCHASE_TOPIC, 1, (short) 1);
//
//            adminClient.createTopics(Arrays.asList(purchaseTopic, userSegmentTopic, enhancedPurchaseTopic)).all().get();
//            System.out.println("Топики созданы");
//        } catch (Exception e) {
//            System.err.println("Ошибка при создании топиков (возможно, они уже существуют): " + e.getMessage());
//        }
//    }
//
//    private static void loadTestData() {
//        Properties producerProps = new Properties();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//
//        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
//            // Загружаем информацию о сегментах пользователей
//            producer.send(new ProducerRecord<>(USER_SEGMENT_TOPIC, "User123", "VIP"));
//            producer.send(new ProducerRecord<>(USER_SEGMENT_TOPIC, "User456", "Standard"));
//            producer.send(new ProducerRecord<>(USER_SEGMENT_TOPIC, "User789", "New"));
//
//            // Загружаем данные о покупках
//            producer.send(new ProducerRecord<>(PURCHASE_TOPIC, "User123", "iPhone 13, 999$"));
//            producer.send(new ProducerRecord<>(PURCHASE_TOPIC, "User456", "Samsung Galaxy, 799$"));
//            producer.send(new ProducerRecord<>(PURCHASE_TOPIC, "User789", "Xiaomi Redmi, 299$"));
//
//            producer.flush();
//            System.out.println("Тестовые данные загружены");
//        }
//    }
//
//    // Метод для демонстрации обработанных сообщений через консольного потребителя
//    // В реальном приложении вам бы понадобился внешний консольный потребитель или другой способ проверки
//    private static void displayProcessedMessages() {
//        System.out.println("Результаты обработки (в выходном топике должны быть сообщения с ключами):");
//        System.out.println("VIP-User123: iPhone 13, 999$");
//        System.out.println("Standard-User456: Samsung Galaxy, 799$");
//        System.out.println("New-User789: Xiaomi Redmi, 299$");
//    }
//}
