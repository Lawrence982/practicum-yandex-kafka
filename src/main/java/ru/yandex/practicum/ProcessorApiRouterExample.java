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
//
//import java.util.Arrays;
//import java.util.Properties;
//import java.util.concurrent.CountDownLatch;
//
//public class ProcessorApiRouterExample {
//    private static final String BOOTSTRAP_SERVERS = "kafka-0:9094";
//    private static final String INPUT_TOPIC = "router-input";
//    private static final String OUTPUT_TOPIC = "router-output";
//
//    public static void main(String[] args) {
//        // Создаем топики
//        createTopics();
//
//        // Отправляем тестовые сообщения
//        produceTestMessages();
//
//        // Настройка свойств Kafka Streams
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "processor-router-example");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//
//        // Создание топологии с условной маршрутизацией
//        final Topology topology = new Topology();
//
//        // Добавляем источник данных (входной топик)
//        topology.addSource("Source", INPUT_TOPIC);
//
//        // Первый процессор-маршрутизатор
//        topology.addProcessor("Processor0", RouterProcessor::new, "Source");
//
//        // Два процессора для разных типов данных
//        topology.addProcessor("Processor1", FirstConditionProcessor::new, "Processor0");
//        topology.addProcessor("Processor2", SeconConditionProcessor::new, "Processor0");
//
//        // Финальный процессор, объединяющий результаты
//        topology.addProcessor("Processor3", FinalProcessor::new, "Processor1", "Processor2");
//
//        // Добавляем выходной топик
//        topology.addSink("Sink", OUTPUT_TOPIC, "Processor3");
//
//        // Выводим описание топологии
//        System.out.println("Топология: " + topology.describe());
//
//        // Создаем и запускаем Kafka Streams приложение
//        final KafkaStreams streams = new KafkaStreams(topology, props);
//        streams.start();
//
//    }
//
//    // Первый процессор-маршрутизатор, определяет куда направить сообщение
//    public static class RouterProcessor implements Processor<String, String, String, String> {
//        private ProcessorContext<String, String> context;
//
//        @Override
//        public void init(ProcessorContext<String, String> context) {
//            this.context = context;
//        }
//
//        @Override
//        public void process(Record<String, String> record) {
//            try {
//                // Пытаемся распарсить значение как число
//                Integer.parseInt(record.value());
//                // Если это число, отправляем в Processor1
//                context.forward(record, "Processor1");
//                System.out.println("RouterProcessor: Отправка сообщения с ключом " +
//                        record.key() + " в Processor1");
//            } catch (NumberFormatException e) {
//                // Если это не число, отправляем в Processor2
//                context.forward(record, "Processor2");
//                System.out.println("RouterProcessor: Отправка сообщения с ключом " +
//                        record.key() + " в Processor2");
//            }
//        }
//
//        @Override
//        public void close() {
//            // Ничего не делаем при закрытии
//        }
//    }
//
//    // Процессор для обработки числовых данных
//    public static class FirstConditionProcessor implements Processor<String, String, String, String> {
//        private ProcessorContext<String, String> context;
//
//        @Override
//        public void init(ProcessorContext<String, String> context) {
//            this.context = context;
//        }
//
//        @Override
//        public void process(Record<String, String> record) {
//            // Преобразуем число (умножаем на 2)
//            int value = Integer.parseInt(record.value());
//            int processedValue = value * 2;
//
//            // Создаем новую запись с результатом
//            Record<String, String> newRecord = new Record<>(
//                    record.key(),
//                    "NUM:" + processedValue,
//                    record.timestamp()
//            );
//
//            // Направляем в следующий процессор
//            context.forward(newRecord);
//            System.out.println("NumberProcessor: Обработано число " + value +
//                    " -> " + processedValue);
//        }
//
//        @Override
//        public void close() {
//            // Ничего не делаем при закрытии
//        }
//    }
//
//    // Процессор для обработки текстовых данных
//    public static class SeconConditionProcessor implements Processor<String, String, String, String> {
//        private ProcessorContext<String, String> context;
//
//        @Override
//        public void init(ProcessorContext<String, String> context) {
//            this.context = context;
//        }
//
//        @Override
//        public void process(Record<String, String> record) {
//            // Преобразуем текст (переводим в верхний регистр)
//            String value = record.value();
//            String processedValue = value.toUpperCase();
//
//            // Создаем новую запись с результатом
//            Record<String, String> newRecord = new Record<>(
//                    record.key(),
//                    "TEXT:" + processedValue,
//                    record.timestamp()
//            );
//
//            // Направляем в следующий процессор
//            context.forward(newRecord);
//            System.out.println("TextProcessor: Обработан текст " + value +
//                    " -> " + processedValue);
//        }
//
//        @Override
//        public void close() {
//            // Ничего не делаем при закрытии
//        }
//    }
//
//    // Финальный процессор, объединяющий результаты
//    public static class FinalProcessor implements Processor<String, String, String, String> {
//        private ProcessorContext<String, String> context;
//
//        @Override
//        public void init(ProcessorContext<String, String> context) {
//            this.context = context;
//        }
//
//        @Override
//        public void process(Record<String, String> record) {
//            // Добавляем пометку, что сообщение прошло через финальный процессор
//            Record<String, String> newRecord = new Record<>(
//                    record.key(),
//                    "FINAL_RESULT:" + record.value(),
//                    record.timestamp()
//            );
//
//            // Направляем в выходной топик
//            context.forward(newRecord);
//            System.out.println("FinalProcessor: Сообщение обработано: " + newRecord.value());
//        }
//
//        @Override
//        public void close() {
//            // Ничего не делаем при закрытии
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
//    private static void produceTestMessages() {
//        Properties producerProps = new Properties();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//
//        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
//            // Отправляем числовые сообщения
//            producer.send(new ProducerRecord<>(INPUT_TOPIC, "key1", "100"));
//            producer.send(new ProducerRecord<>(INPUT_TOPIC, "key2", "200"));
//
//            // Отправляем текстовые сообщения
//            producer.send(new ProducerRecord<>(INPUT_TOPIC, "key3", "привет"));
//            producer.send(new ProducerRecord<>(INPUT_TOPIC, "key4", "кафка"));
//
//            producer.flush();
//            System.out.println("Тестовые сообщения отправлены в " + INPUT_TOPIC);
//        }
//    }
//}
//
