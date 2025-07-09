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
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.Topology;
//import org.apache.kafka.streams.processor.api.Processor;
//import org.apache.kafka.streams.processor.api.ProcessorContext;
//import org.apache.kafka.streams.processor.api.Record;
//
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Properties;
//import java.util.concurrent.CountDownLatch;
//
//public class StreamProcessorExample {
//    private static final String INPUT_TOPIC = "input-topic";
//    private static final String OUTPUT_TOPIC = "output-topic";
//    private static final String BOOTSTRAP_SERVERS = "kafka-0:9094";
//
//    public static void main(String[] args) {
//        try {
//            // Создаем топики
//            createTopics();
//
//            // Создаем и запускаем Kafka Streams приложение
//            KafkaStreams streams = buildAndStartStreamsApplication();
//            final CountDownLatch latch = new CountDownLatch(1);
//
//            // Обработка завершения работы
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                streams.close();
//                latch.countDown();
//                System.out.println("Приложение Kafka Streams остановлено");
//            }));
//
//            // Отправляем тестовые данные
//            produceTestData();
//
//            // Запускаем потребителя в отдельном потоке для чтения результатов
//            Thread consumerThread = new Thread(() -> consumeOutputData());
//            consumerThread.start();
//
//            // Ожидаем сигнала завершения
//            latch.await();
//        } catch (Throwable e) {
//            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
//
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
//    private static KafkaStreams buildAndStartStreamsApplication() {
//        // Настройка свойств Kafka Streams
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "processor-api-example");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//
//        // Создание топологии с использованием низкоуровневого Processor API
//        final Topology topology = new Topology();
//
//        // Определяем источник данных (входной топик)
//        topology.addSource("Source", INPUT_TOPIC);
//
//        // Добавляем процессор, который будет обрабатывать сообщения
//        topology.addProcessor("Process", UppercaseProcessor::new, "Source");
//
//        // Определяем выходной топик
//        topology.addSink("Sink", OUTPUT_TOPIC, "Process");
//
//        System.out.println("Топология: " + topology.describe());
//
//        // Создаем и запускаем Kafka Streams приложение
//        final KafkaStreams streams = new KafkaStreams(topology, props);
//        streams.start();
//
//        System.out.println("Приложение Kafka Streams запущено");
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
//            // Отправляем тестовые сообщения
//            String[] messages = {
//                    "привет, мир!",
//                    "kafka streams processor api",
//                    "пример использования процессора",
//                    "низкоуровневый api kafka streams"
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
//        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "processor-output-consumer");
//        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
//            consumer.subscribe(Collections.singletonList(OUTPUT_TOPIC));
//            System.out.println("Потребитель подписан на топик: " + OUTPUT_TOPIC);
//
//            // Читаем результаты в течение некоторого времени
//            int pollCount = 0;
//            int maxPolls = 10;
//
//            while (pollCount < maxPolls) {
//                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
//
//                if (records.count() > 0) {
//                    System.out.println("=== Получены преобразованные сообщения: ===");
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
//
//            System.out.println("Потребитель завершил чтение из " + OUTPUT_TOPIC);
//        }
//    }
//
//    // Пользовательский процессор для преобразования сообщений в верхний регистр
//    public static class UppercaseProcessor implements Processor<String, String, String, String> {
//        private ProcessorContext<String, String> context;
//
//        @Override
//        public void init(ProcessorContext<String, String> context) {
//            this.context = context;
//        }
//
//        @Override
//        public void process(Record<String, String> record) {
//            System.out.println("Обработка сообщения: ключ=" + record.key() + ", значение=" + record.value());
//
//            // Преобразование значения в верхний регистр
//            String uppercaseValue = record.value().toUpperCase();
//
//            // Создание нового сообщения с тем же ключом, но преобразованным значением
//            Record<String, String> newRecord = new Record<>(
//                    record.key(),
//                    uppercaseValue,
//                    record.timestamp()
//            );
//
//            // Отправка преобразованного сообщения дальше по топологии
//            context.forward(newRecord);
//            System.out.println("Сообщение преобразовано: ключ=" + record.key() + ", новое значение=" + uppercaseValue);
//        }
//
//        @Override
//        public void close() {
//            // Ресурсы для освобождения при закрытии процессора
//        }
//    }
//}
