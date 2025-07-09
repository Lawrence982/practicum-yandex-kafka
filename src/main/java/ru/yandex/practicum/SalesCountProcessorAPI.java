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
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.Topology;
//import org.apache.kafka.streams.processor.api.Processor;
//import org.apache.kafka.streams.processor.api.ProcessorContext;
//import org.apache.kafka.streams.processor.api.Record;
//import org.apache.kafka.streams.state.*;
//
//import java.util.*;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
///**
// * Пример использования Kafka Streams Processor API с глобальным хранилищем
// * для учета продаж товаров
// */
//public class SalesCountProcessorAPI {
//    // Конфигурация
//    private static final String BOOTSTRAP_SERVERS = "kafka-0:9092";
//    private static final String SALES_TOPIC = "product-sales";
//    private static final String GLOBAL_STORE_NAME = "sales-count-store";
//
//    public static void main(String[] args) {
//        try {
//            // Создаем топики
//            createTopics();
//
//            // Создаем и запускаем Kafka Streams приложение
//            KafkaStreams streams = buildStreamsApplication();
//            final CountDownLatch latch = new CountDownLatch(1);
//
//            // Обработка завершения работы
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                System.out.println("Завершение работы приложения...");
//                streams.close();
//                latch.countDown();
//            }));
//
//            // Запускаем Kafka Streams
//            streams.start();
//            System.out.println("Приложение Sales Processor запущено");
//
//            // Ждем, пока приложение полностью запустится
//            waitUntilKafkaStreamsIsRunning(streams);
//
//            // Отправляем тестовые данные о продажах
//            sendSampleSalesData();
//
//            // Даем время на обработку данных
//            System.out.println("Ожидание обработки данных...");
//            TimeUnit.SECONDS.sleep(5);
//
//            // Запрашиваем данные из глобального хранилища
//            querySalesCountStore(streams);
//
//            // Ожидаем сигнала завершения
//            latch.await();
//
//        } catch (Exception e) {
//            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
//
//    /**
//     * Создает необходимые топики в Kafka
//     */
//    private static void createTopics() throws Exception {
//        Properties adminProps = new Properties();
//        adminProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
//
//        try (AdminClient adminClient = AdminClient.create(adminProps)) {
//            // Проверяем существование топика
//            Set<String> existingTopics = adminClient.listTopics().names().get();
//
//            if (!existingTopics.contains(SALES_TOPIC)) {
//                // Создаем топик для данных о продажах
//                NewTopic salesTopic = new NewTopic(SALES_TOPIC, 1, (short) 1);
//
//                adminClient.createTopics(Collections.singletonList(salesTopic)).all().get();
//                System.out.println("Топик " + SALES_TOPIC + " создан");
//            } else {
//                System.out.println("Топик " + SALES_TOPIC + " уже существует");
//            }
//        }
//    }
//
//    /**
//     * Создает и настраивает приложение Kafka Streams с использованием Processor API
//     */
//    private static KafkaStreams buildStreamsApplication() {
//        // Настройка свойств Kafka Streams
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sales-count-processor");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        // Отключаем кеширование для наглядности
//        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
//
//        // Создаем топологию с использованием Processor API
//        Topology topology = new Topology();
//
//        // УДАЛЕНЫ ДВЕ СТРОКИ:
//        // topology.addSource(
//        //         "SalesSource",         // Имя источника
//        //         SALES_TOPIC            // Топик-источник
//        // );
//
//        // Создаем билдер для глобального хранилища счетчиков продаж
//        StoreBuilder<KeyValueStore<String, Long>> storeBuilder = Stores.keyValueStoreBuilder(
//                Stores.persistentKeyValueStore(GLOBAL_STORE_NAME),
//                Serdes.String(),
//                Serdes.Long()
//        ).withLoggingDisabled(); // Отключаем логирование для глобального хранилища
//
//        // Добавляем хранилище к топологии
//        topology.addGlobalStore(
//                storeBuilder,                   // Билдер хранилища
//                "SalesCountSource",             // Имя для источника глобального хранилища
//                Serdes.String().deserializer(), // Десериализатор ключа
//                Serdes.String().deserializer(), // Десериализатор значения
//                SALES_TOPIC,                    // Входной топик для хранилища
//                "SalesCountProcessor",          // Имя процессора
//                () -> new SalesCountProcessor() // Поставщик процессора
//        );
//
//        System.out.println("Топология: " + topology.describe());
//
//        // Создаем приложение Kafka Streams
//        return new KafkaStreams(topology, props);
//    }
//
//    /**
//     * Процессор для обработки данных о продажах и обновления счетчиков в глобальном хранилище
//     */
//    public static class SalesCountProcessor implements Processor<String, String, Void, Void> {
//        private ProcessorContext<Void, Void> context;
//        private KeyValueStore<String, Long> salesCountStore;
//
//        @Override
//        public void init(ProcessorContext<Void, Void> context) {
//            this.context = context;
//            // Получаем доступ к глобальному хранилищу
//            this.salesCountStore = context.getStateStore(GLOBAL_STORE_NAME);
//            System.out.println("Инициализирован процессор подсчета продаж");
//        }
//
//        @Override
//        public void process(Record<String, String> record) {
//            String productId = record.key();
//            String salesCountStr = record.value();
//
//            if (salesCountStr != null) {
//                try {
//                    // Преобразуем количество проданных единиц из строки в число
//                    long salesCount = Long.parseLong(salesCountStr);
//
//                    // Получаем текущее значение счетчика из хранилища
//                    Long currentTotal = salesCountStore.get(productId);
//                    if (currentTotal == null) {
//                        currentTotal = 0L;
//                    }
//
//                    // Увеличиваем счетчик на количество проданных единиц
//                    long newTotal = currentTotal + salesCount;
//
//                    // Обновляем значение в хранилище
//                    salesCountStore.put(productId, newTotal);
//
//                    System.out.println("Обновлены данные о продажах для товара " + productId +
//                            ": +" + salesCount + " единиц, всего: " + newTotal);
//
//                } catch (NumberFormatException e) {
//                    System.err.println("Ошибка при обработке количества продаж: " + e.getMessage());
//                }
//            }
//        }
//
//        @Override
//        public void close() {
//            // Закрытие ресурсов (если необходимо)
//        }
//    }
//
//    /**
//     * Отправляет тестовые данные о продажах товаров
//     */
//    private static void sendSampleSalesData() {
//        Properties producerProps = new Properties();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//
//        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
//            // Отправляем тестовые данные о продажах
//            System.out.println("Отправка тестовых данных о продажах...");
//
//            // Отправляем данные о продажах (ID товара, количество проданных единиц)
//            producer.send(new ProducerRecord<>(SALES_TOPIC, "product-1", "10"));
//            producer.send(new ProducerRecord<>(SALES_TOPIC, "product-2", "5"));
//            producer.send(new ProducerRecord<>(SALES_TOPIC, "product-3", "8"));
//            producer.send(new ProducerRecord<>(SALES_TOPIC, "product-1", "3"));  // Еще продажи для product-1
//            producer.send(new ProducerRecord<>(SALES_TOPIC, "product-4", "12"));
//            producer.send(new ProducerRecord<>(SALES_TOPIC, "product-2", "7"));  // Еще продажи для product-2
//
//            producer.flush();
//            System.out.println("Тестовые данные о продажах успешно отправлены");
//
//        } catch (Exception e) {
//            System.err.println("Ошибка при отправке тестовых данных: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Запрашивает и выводит данные о продажах из глобального хранилища
//     */
//    private static void querySalesCountStore(KafkaStreams streams) {
//        try {
//            // Получаем доступ к глобальному хранилищу
//            ReadOnlyKeyValueStore<String, Long> salesCountStore = streams.store(
//                    org.apache.kafka.streams.StoreQueryParameters.fromNameAndType(
//                            GLOBAL_STORE_NAME,
//                            org.apache.kafka.streams.state.QueryableStoreTypes.<String, Long>keyValueStore()
//                    )
//            );
//
//            System.out.println("\n=== Общее количество проданных единиц по товарам ===");
//
//            // Получаем все данные о продажах
//            KeyValueIterator<String, Long> all = salesCountStore.all();
//
//            while (all.hasNext()) {
//                org.apache.kafka.streams.KeyValue<String, Long> next = all.next();
//                String productId = next.key;
//                Long totalSales = next.value;
//
//                System.out.println("Товар: " + productId + ", Всего продано: " + totalSales + " единиц");
//            }
//
//            all.close();
//
//        } catch (Exception e) {
//            System.err.println("Ошибка при запросе к хранилищу: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Ожидает, пока Kafka Streams перейдет в состояние RUNNING
//     */
//    private static void waitUntilKafkaStreamsIsRunning(KafkaStreams streams) throws Exception {
//        int maxRetries = 10;
//        int retryIntervalMs = 1000;
//        int attempt = 0;
//
//        while (attempt < maxRetries) {
//            if (streams.state() == KafkaStreams.State.RUNNING) {
//                System.out.println("Kafka Streams успешно запущен");
//                return;
//            }
//
//            System.out.println("Ожидание запуска Kafka Streams... Текущее состояние: " + streams.state());
//            Thread.sleep(retryIntervalMs);
//            attempt++;
//        }
//
//        throw new RuntimeException("Превышено время ожидания запуска Kafka Streams");
//    }
//}
