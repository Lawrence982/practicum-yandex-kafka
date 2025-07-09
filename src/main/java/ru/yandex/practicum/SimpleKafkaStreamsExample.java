//package ru.yandex.practicum;
//
//import org.apache.kafka.clients.admin.AdminClient;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.KStream;
//
//
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Properties;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutionException;
//
//public class SimpleKafkaStreamsExample {
//    private static final String INPUT_TOPIC = "input-topic";
//    private static final String OUTPUT_TOPIC = "output-topic";
//    private static final String BOOTSTRAP_SERVERS = "kafka-0:9094";
//
//    public static void main(String[] args) {
//        try {
//            // Создаем топики
//            createTopics();
//
//            // Запускаем Kafka Streams приложение
//            KafkaStreams streams = buildAndStartStreamsApplication();
//
//            // Создаем объект для ожидания завершения приложения
//            CountDownLatch latch = new CountDownLatch(1);
//
//            // Отправляем тестовые данные
//            produceTestData();
//
//            // Запускаем потребителя в отдельном потоке для чтения результатов
//            Thread consumerThread = new Thread(() -> consumeOutputData());
//            consumerThread.start();
//
//            // Завершение работы при получении сигнала
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                System.out.println("Shutting down Kafka Streams application...");
//                streams.close();
//                latch.countDown();
//            }));
//
//            // Ожидаем завершения работы
//            latch.await();
//
//        } catch (Exception e) {
//            System.err.println("Ошибка при запуске Kafka Streams приложения: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private static void createTopics() {
//        Properties adminProps = new Properties();
//        adminProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
//
//        try (AdminClient adminClient = AdminClient.create(adminProps)) {
//            // Создаем топики с 1 разделом
//            NewTopic inputTopic = new NewTopic(INPUT_TOPIC, 1, (short) 1);
//            NewTopic outputTopic = new NewTopic(OUTPUT_TOPIC, 1, (short) 1);
//
//            adminClient.createTopics(Arrays.asList(inputTopic, outputTopic)).all().get();
//            System.out.println("Топики созданы: " + INPUT_TOPIC + ", " + OUTPUT_TOPIC);
//        } catch (InterruptedException | ExecutionException e) {
//            System.err.println("Ошибка при создании топиков (возможно они уже существуют): " + e.getMessage());
//        }
//    }
//
//    private static KafkaStreams buildAndStartStreamsApplication() {
//        // Конфигурация Kafka Streams
//        Properties config = new Properties();
//        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "simple-kafka-streams-app");
//        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//
//
//        // Создание топологии
//        StreamsBuilder builder = new StreamsBuilder();
//
//        KStream<String, String> inputStream = builder.stream(INPUT_TOPIC);
//
//        // Обработка данных - преобразуем в верхний регистр
//        KStream<String, String> processedStream = inputStream.mapValues(value -> {
//            String upperCaseValue = value.toUpperCase();
//            System.out.println("Обработка: " + value + " -> " + upperCaseValue);
//            return upperCaseValue;
//        });
//
//        // Отправка обработанных данных в другой топик
//        processedStream.to(OUTPUT_TOPIC);
//
//        // Инициализация и запуск Kafka Streams
//        KafkaStreams streams = new KafkaStreams(builder.build(), config);
//        streams.start();
//
//        System.out.println("Kafka Streams приложение запущено успешно.");
//        return streams;
//    }
//
//    private static void produceTestData() {
//        Properties producerProps = new Properties();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//
//        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
//            // Отправляем несколько тестовых сообщений
//            String[] messages = {
//                    "привет, мир!",
//                    "kafka streams",
//                    "простой пример",
//                    "преобразование в верхний регистр"
//            };
//
//            for (int i = 0; i < messages.length; i++) {
//                String key = "key-" + i;
//                String value = messages[i];
//
//                ProducerRecord<String, String> record = new ProducerRecord<>(INPUT_TOPIC, key, value);
//                producer.send(record, (metadata, exception) -> {
//                    if (exception == null) {
//                        System.out.println("Отправлено сообщение: ключ=" + key + ", значение=" + value);
//                    } else {
//                        System.err.println("Ошибка при отправке сообщения: " + exception.getMessage());
//                    }
//                });
//            }
//
//            producer.flush();
//            System.out.println("Все тестовые сообщения отправлены в " + INPUT_TOPIC);
//        }
//    }
//
//    private static void consumeOutputData() {
//        Properties consumerProps = new Properties();
//        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "output-consumer-group");
//        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
//            consumer.subscribe(Collections.singletonList(OUTPUT_TOPIC));
//            System.out.println("Потребитель подписан на топик: " + OUTPUT_TOPIC);
//
//            // Чтение результатов в течение некоторого времени
//            int pollCount = 0;
//            int maxPolls = 10;
//
//            while (pollCount < maxPolls) {
//                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
//
//                if (records.count() > 0) {
//                    System.out.println("=== Получены обработанные сообщения: ===");
//                    for (ConsumerRecord<String, String> record : records) {
//                        System.out.println("Ключ: " + record.key() +
//                                ", Значение: " + record.value() +
//                                ", Раздел: " + record.partition() +
//                                ", Смещение: " + record.offset());
//                    }
//                }
//
//                pollCount++;
//            }
//        }
//    }
//}
