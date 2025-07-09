//package ru.yandex.practicum;
//
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.StoreQueryParameters;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.Consumed;
//import org.apache.kafka.streams.kstream.KStream;
//import org.apache.kafka.streams.kstream.KTable;
//import org.apache.kafka.streams.kstream.Materialized;
//import org.apache.kafka.streams.state.QueryableStoreTypes;
//import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
//import java.util.Arrays;
//import java.util.Properties;
//
//import static org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
//
//public class InteractiveQueryExample{
//    public static void main(String[] args) {
//        // Конфигурация Kafka Streams
//        // Устанавливаем параметры для Kafka Streams приложения, включая ID приложения, сериализаторы и адрес Kafka.
//        Properties config = new Properties();
//        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-to-table-example");
//        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-0:9094");
//
//        // Создание топологии
//        StreamsBuilder builder = new StreamsBuilder();
//
//        // Создание потока для подсчёта слов
//        // Читаем данные из Kafka топика "input-topic" в поток KStream.
//        KStream<String, String> stream = builder.stream("input-topic", Consumed.with(Serdes.String(), Serdes.String()));
//
//        // Подсчёт слов и сохранение в состоянии
//        // Преобразуем строки в слова и затем подсчитываем их, сохраняя результаты в состоянии (KTable).
//        KTable<String, Long> wordCounts = stream
//                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
//                .groupBy((key, word) -> word)
//                .count(Materialized.as("word-count-store"));
//
//        // Печать результатов в консоль (только для проверки)
//        // Преобразуем KTable в поток и выводим результаты подсчёта в консоль
//        wordCounts.toStream().foreach((key, value) -> System.out.println("Word: " + key + ", Count: " + value));
//
//        // Построение и запуск приложения
//        KafkaStreams streams = new KafkaStreams(builder.build(), config);
//
//        // Устанавливаем обработчик необработанных исключений, чтобы при ошибках поток останавливался корректно.
//        streams.setUncaughtExceptionHandler(exception -> {
//            System.err.println(exception);
//            exception.printStackTrace();
//            return SHUTDOWN_CLIENT;
//        });
//        // Очищаем состояние Kafka Streams перед запуском
//        streams.cleanUp();
//        streams.start();
//
//        // Пример использования хранилища
//        try {
//            while (streams.state() != KafkaStreams.State.RUNNING) {
//                Thread.sleep(5000); // Ждём инициализации состояния
//                System.out.println(streams.state());
//                if (streams.state() == KafkaStreams.State.ERROR || streams.state() == KafkaStreams.State.NOT_RUNNING) {
//                    throw new RuntimeException("Stream is not started");
//                }
//            }
//
//            // Получение хранилища для чтения подсчитанных данных
//            // Запрашиваем хранилище с именем "word-count-store" для извлечения данных.
//            ReadOnlyKeyValueStore<String, Long> keyValueStore =
//                    streams.store(
//                            StoreQueryParameters.fromNameAndType("word-count-store", QueryableStoreTypes.keyValueStore())
//                    );
//
//            System.out.println("Слово 'example' встречается: " + keyValueStore.get("example") + " раз.");
//        } catch (Exception e) {
//            System.err.println("Ошибка: " + e.getMessage());
//        }
//        streams.close();
//    }
//}