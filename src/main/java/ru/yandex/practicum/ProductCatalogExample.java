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
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.StoreQueryParameters;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.KTable;
//import org.apache.kafka.streams.kstream.Materialized;
//import org.apache.kafka.streams.state.KeyValueIterator;
//import org.apache.kafka.streams.state.KeyValueStore;
//import org.apache.kafka.streams.state.QueryableStoreTypes;
//import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
//
//import java.util.Arrays;
//import java.util.Properties;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//public class ProductCatalogExample {
//    private static final String BOOTSTRAP_SERVERS = "kafka-0:9092";
//    private static final String PRODUCT_CATALOG_TOPIC = "product-catalog";
//    private static final String PRODUCT_STORE = "product-catalog-store";
//
//    public static void main(String[] args) {
//        // Создаем топик
//        createTopics();
//
//        // Настройка Kafka Streams
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-catalog-app");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//
//        // Строим топологию
//        StreamsBuilder builder = new StreamsBuilder();
//
//        // Создаем KTable из топика каталога продуктов
//        KTable<String, String> productCatalog = builder.table(
//                PRODUCT_CATALOG_TOPIC,
//                Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as(PRODUCT_STORE)
//                        .withKeySerde(Serdes.String())
//                        .withValueSerde(Serdes.String())
//        );
//
//        // Запускаем приложение
//        KafkaStreams streams = new KafkaStreams(builder.build(), props);
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        // Обработка завершения работы
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            streams.close();
//            latch.countDown();
//        }));
//
//        try {
//            // Очищаем локальное хранилище перед запуском
//            streams.cleanUp();
//
//            streams.start();
//            System.out.println("Приложение каталога продуктов запущено");
//
//            // Загружаем тестовые данные о продуктах
//            System.out.println("\nДобавление товаров в каталог...");
//            loadProductData();
//
//            // Ждем, пока данные обработаются
//            waitForStateStoreToBeReady(streams);
//
//            // Показываем текущий каталог товаров
//            System.out.println("\nТекущий каталог товаров:");
//            printProductCatalog(streams);
//
//            // Удаляем товар, который больше не продается
//            System.out.println("\nУдаление товара с ID 'P103' (Смартфон XYZ S5), который снят с продажи...");
//            removeProduct("P103");
//
//            // Ждем обработки данных
//            TimeUnit.SECONDS.sleep(3);
//
//            // Показываем обновленный каталог
//            System.out.println("\nОбновленный каталог товаров после удаления:");
//            printProductCatalog(streams);
//
//            // Проверяем, что товар исчез из каталога через прямое обращение к хранилищу
//            checkProductExists(streams, "P103");
//
//            latch.await();
//        } catch (Throwable e) {
//            System.err.println("Ошибка в приложении: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//        System.exit(0);
//    }
//
//    // Загружает информацию о продуктах в топик
//    private static void loadProductData() throws Exception {
//        Properties producerProps = new Properties();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//
//        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
//
//            // Отправляем данные в топик
//            producer.send(new ProducerRecord<>(PRODUCT_CATALOG_TOPIC, "P101", "laptop"));
//            producer.send(new ProducerRecord<>(PRODUCT_CATALOG_TOPIC, "P102", "headphones"));
//            producer.send(new ProducerRecord<>(PRODUCT_CATALOG_TOPIC, "P103", "smartphone"));
//            producer.send(new ProducerRecord<>(PRODUCT_CATALOG_TOPIC, "P104", "tablet"));
//
//            producer.flush();
//            System.out.println("Данные о товарах загружены в топик");
//        }
//    }
//
//    // Удаляет товар из каталога, отправляя null-значение
//    private static void removeProduct(String productId) {
//        Properties producerProps = new Properties();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//
//        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
//            // Отправляем null-значение для указанного ключа, что приведет к удалению записи из KTable
//            producer.send(new ProducerRecord<>(PRODUCT_CATALOG_TOPIC, productId, null));
//
//            producer.flush();
//            System.out.println("Отправлено сообщение для удаления товара с ID: " + productId);
//        }
//    }
//
//    // Выводит содержимое каталога продуктов
//    private static void printProductCatalog(KafkaStreams streams) {
//        try {
//            ReadOnlyKeyValueStore<String, String> store = streams.store(
//                    StoreQueryParameters.fromNameAndType(PRODUCT_STORE, QueryableStoreTypes.keyValueStore())
//            );
//
//            KeyValueIterator<String, String> iterator = store.all();
//            boolean isEmpty = true;
//
//            while (iterator.hasNext()) {
//                isEmpty = false;
//                KeyValue<String, String> entry = iterator.next();
//
//                System.out.println("ID: " + entry.key +
//                        ", Название: " + entry.value);
//            }
//
//            if (isEmpty) {
//                System.out.println("Каталог пуст.");
//            }
//
//            iterator.close();
//        } catch (Exception e) {
//            System.err.println("Ошибка при чтении каталога: " + e.getMessage());
//        }
//    }
//
//    // Проверяет наличие конкретного товара
//    private static void checkProductExists(KafkaStreams streams, String productId) {
//        try {
//            ReadOnlyKeyValueStore<String, String> store = streams.store(
//                    StoreQueryParameters.fromNameAndType(PRODUCT_STORE, QueryableStoreTypes.keyValueStore())
//            );
//
//            String productData = store.get(productId);
//
//            if (productData == null) {
//                System.out.println("\nТовар с ID '" + productId + "' отсутствует в каталоге - успешно удален.");
//            } else {
//                System.out.println("\nТовар с ID '" + productId + "' все еще присутствует в каталоге: " + productData);
//            }
//        } catch (Exception e) {
//            System.err.println("Ошибка при проверке наличия товара: " + e.getMessage());
//        }
//    }
//
//    // Создает топик
//    private static void createTopics() {
//        Properties adminProps = new Properties();
//        adminProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
//
//        try (AdminClient adminClient = AdminClient.create(adminProps)) {
//            // Создаем топик с политикой компактирования для оптимальной работы с KTable
//            NewTopic productTopic = new NewTopic(PRODUCT_CATALOG_TOPIC, 1, (short) 1);
//
//            // Настраиваем топик на компактирование, чтобы удаленные записи правильно обрабатывались
//            productTopic.configs(java.util.Collections.singletonMap("cleanup.policy", "compact"));
//
//            adminClient.createTopics(Arrays.asList(productTopic)).all().get();
//            System.out.println("Топик " + PRODUCT_CATALOG_TOPIC + " создан с политикой компактирования");
//        } catch (Exception e) {
//            System.err.println("Ошибка при создании топика (возможно, он уже существует): " + e.getMessage());
//        }
//    }
//
//    // Ожидает готовности state store
//    private static void waitForStateStoreToBeReady(KafkaStreams streams) throws InterruptedException {
//        final long MAX_WAIT_MS = 60000;
//        final long RETRY_INTERVAL_MS = 1000;
//
//        long startTime = System.currentTimeMillis();
//        long endTime = startTime + MAX_WAIT_MS;
//
//        while (System.currentTimeMillis() < endTime) {
//            if (streams.state() == KafkaStreams.State.RUNNING) {
//                try {
//                    streams.store(StoreQueryParameters.fromNameAndType(
//                            PRODUCT_STORE, QueryableStoreTypes.keyValueStore()));
//                    System.out.println("State store готово к запросам");
//                    return;
//                } catch (Exception e) {
//                    System.out.println("Ожидание готовности state store... (" +
//                            (System.currentTimeMillis() - startTime) / 1000 + " сек)");
//                }
//            } else {
//                System.out.println("Ожидание состояния RUNNING... Текущее состояние: " + streams.state());
//            }
//
//            Thread.sleep(RETRY_INTERVAL_MS);
//        }
//
//        throw new RuntimeException("Превышено время ожидания готовности state store");
//    }
//}
//
