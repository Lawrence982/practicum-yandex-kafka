//package ru.yandex.practicum;
//
//import org.apache.kafka.clients.admin.AdminClient;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.apache.kafka.common.utils.Bytes;
//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.StoreQueryParameters;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.*;
//import org.apache.kafka.streams.state.KeyValueStore;
//import org.apache.kafka.streams.state.QueryableStoreTypes;
//import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
//
//import java.util.Arrays;
//import java.util.Properties;
//import java.util.concurrent.CountDownLatch;
//import java.util.regex.Pattern;
//
////CREATE TABLE word_count_table (
////        key STRING PRIMARY KEY,
////        value STRING
////) WITH (
////KAFKA_TOPIC='word-count-output',
////VALUE_FORMAT='DELIMITED'
////        );
//
//public class WordCountStateStore {
//    private static final String BOOTSTRAP_SERVERS = "kafka-0:9092";
//    private static final String INPUT_TOPIC = "word-count-input";
//    private static final String OUTPUT_TOPIC = "word-count-output";
//    private static final String WORD_COUNT_STORE = "word-count-store";
//
//    private static final Pattern PATTERN = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
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
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//
//
//        // Строим топологию
//        StreamsBuilder builder = new StreamsBuilder();
//
//        // Поток входных текстовых данных
//        KStream<String, String> textLines = builder.stream(
//                INPUT_TOPIC,
//                Consumed.with(Serdes.String(), Serdes.String())
//        );
//
//        // Разбиваем текст на слова и подсчитываем их
//        KTable<String, Long> wordCounts = textLines
//                // Разбиваем каждую строку на слова (фильтруем пустые слова)
//                .flatMapValues(value -> Arrays.asList(PATTERN.split(value.toLowerCase())))
//                .filter((key, word) -> !word.isEmpty())
//
//                // Изменяем ключ, чтобы он был словом (для группировки)
//                .groupBy((key, word) -> word, Grouped.with(Serdes.String(), Serdes.String()))
//
//                // Подсчитываем вхождения каждого слова с использованием state store
//                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(WORD_COUNT_STORE)
//                        .withKeySerde(Serdes.String())
//                        .withValueSerde(Serdes.Long()));
//
//        // Преобразуем значения Long в String для записи в выходной топик
//        wordCounts.toStream()
//                .mapValues(count -> Long.toString(count))
//                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
//
//        // Запускаем приложение
//        KafkaStreams streams = new KafkaStreams(builder.build(), props);
//
//        try {
//            // Очищаем локальные state store перед запуском (для тестирования)
//            streams.cleanUp();
//
//            streams.start();
//            System.out.println("Word Count приложение запущено");
//
//            // Демонстрация запросов к state store
//            Thread.sleep(15000); // Даем время на обработку сообщений
//            queryStateStore(streams);
//
//        } catch (Throwable e) {
//            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
//
//    /**
//     * Выполняет запросы к state store для получения статистики слов
//     */
//    private static void queryStateStore(KafkaStreams streams) {
//        // Ожидаем, пока состояние потока не станет RUNNING
//        try {
//            // Ждем, пока streams не перейдет в состояние RUNNING
//            waitForStateStoreToBeReady(streams);
//
//            System.out.println("\nТекущие результаты подсчета слов из state store:");
//
//            ReadOnlyKeyValueStore<String, Long> keyValueStore = streams.store(
//                    StoreQueryParameters.fromNameAndType(WORD_COUNT_STORE, QueryableStoreTypes.keyValueStore())
//            );
//
//            // Выводим несколько примеров слов
//            String[] sampleWords = {"kafka", "streams", "процессор", "топология", "хранилище"};
//            for (String word : sampleWords) {
//                Long count = keyValueStore.get(word);
//                System.out.println(word + ": " + (count != null ? count : 0));
//            }
//
//            System.out.println("\nВсе слова в хранилище:");
//            keyValueStore.all().forEachRemaining(pair ->
//                    System.out.println(pair.key + ": " + pair.value)
//            );
//
//        } catch (Exception e) {
//            System.err.println("Ошибка при чтении state store: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Ожидает, пока state store не будет готово к запросам
//     */
//    private static void waitForStateStoreToBeReady(KafkaStreams streams) throws InterruptedException {
//        // Максимальное время ожидания и интервал проверки
//        final long MAX_WAIT_MS = 60000; // 60 секунд
//        final long RETRY_INTERVAL_MS = 1000; // 1 секунда
//
//        long startTime = System.currentTimeMillis();
//        long endTime = startTime + MAX_WAIT_MS;
//
//        // Проверяем состояние потока с интервалом
//        while (System.currentTimeMillis() < endTime) {
//            if (streams.state() == KafkaStreams.State.RUNNING) {
//                // Пробуем получить доступ к хранилищу
//                try {
//                    streams.store(StoreQueryParameters.fromNameAndType(
//                            WORD_COUNT_STORE, QueryableStoreTypes.keyValueStore()));
//                    System.out.println("State store готово к запросам");
//                    return; // Хранилище готово
//                } catch (Exception e) {
//                    // Хранилище еще не готово, продолжаем ожидание
//                    System.out.println("Ожидание готовности state store... (" +
//                            (System.currentTimeMillis() - startTime) / 1000 + " сек)");
//                }
//            } else {
//                System.out.println("Ожидание перехода потока в состояние RUNNING... Текущее состояние: " +
//                        streams.state());
//            }
//
//            // Ждем перед следующей проверкой
//            Thread.sleep(RETRY_INTERVAL_MS);
//        }
//
//        throw new RuntimeException("Превышено время ожидания готовности state store");
//    }
//
//    /**
//     * Создает топики для приложения
//     */
//    private static void createTopics() {
//        Properties adminProps = new Properties();
//        adminProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
//
//        try (AdminClient adminClient = AdminClient.create(adminProps)) {
//            NewTopic inputTopic = new NewTopic(INPUT_TOPIC, 1, (short) 1);
//            NewTopic outputTopic = new NewTopic(OUTPUT_TOPIC, 1, (short) 1);
//
//            adminClient.createTopics(Arrays.asList(inputTopic, outputTopic)).all().get();
//            System.out.println("Топики созданы: " + INPUT_TOPIC + ", " + OUTPUT_TOPIC);
//        } catch (Exception e) {
//            System.err.println("Ошибка при создании топиков (возможно, они уже существуют): " + e.getMessage());
//        }
//    }
//
//    /**
//     * Загружает тестовые данные во входной топик
//     */
//    private static void loadTestData() {
//        Properties producerProps = new Properties();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//
//        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
//            String[] testMessages = {
//                    "Kafka Streams предоставляет простой и лёгкий клиентский API для обработки потоковых данных",
//                    "Kafka Streams обеспечивает обработку потоков с точно-однократной семантикой и локальным хранением состояния",
//                    "State Store в Kafka Streams позволяет хранить и запрашивать данные локально в каждой задаче",
//                    "Kafka Streams поддерживает оконную обработку и соединение различных потоков данных",
//                    "Топология процессора определяет граф потока данных в Kafka Streams",
//                    "Хранилище состояния может быть использовано для поиска данных в реальном времени",
//                    "Каждый процессор топологии выполняет операцию над поступающими записями"
//            };
//
//            for (int i = 0; i < testMessages.length; i++) {
//                String key = "message-" + i;
//                producer.send(new ProducerRecord<>(INPUT_TOPIC, key, testMessages[i]));
//                System.out.println("Отправлено тестовое сообщение " + (i + 1));
//            }
//
//            producer.flush();
//            System.out.println("Все тестовые сообщения отправлены");
//        }
//    }
//}
//
