# Описание проекта
Проект представляет собой Spring Boot приложение, написанное на Java и использующее Maven для автоматизации сборки.

Основное назначение приложения - создание и прослушивание kafka топика, конфигурация консьюмера и продьюсера, и вывод сообщений в терминал.

Для работы с Kafka использовалась библиотека spring-kafka (org.springframework.kafka).

Для создания образа используется Dockerfile, который разворачивается с помощью Docker Compose.

Помимо Java приложения в Docker Compose конфигурируются такие сервисы как Apache NiFi и Apache Hadoop.

Apache NiFi принимает поток данных из топика Kafka, модернизирует входные сообщение и сохраняет обновленный JSON в Apache Hadoop.

# 1. Развёртывание и настройка Kafka-кластера в Yandex Cloud

## Kafka кластер:

В рамках проекта в Yandex Cloud был развернут Kafka кластер состоящий из 3 брокеров, работающих в комбинированном режиме
в качестве брокеров и контроллеров. 

<p>
 <img src="./screens/Screenshot_1.png" alt="qr"/>
</p>

<p>
 <img src="./screens/Screenshot_2.png" alt="qr"/>
</p>

<p>
 <img src="./screens/Screenshot_3.png" alt="qr"/>
</p>

<p>
 <img src="./screens/Screenshot_4.png" alt="qr"/>
</p>

Кластер не является отказоустойчивым, так как хост брокер присутствует только в одной из зон доступности. Такой подход 
выбран намерено, чтобы сэкономить выделенный грант. В реальном продакшене следовало не использовать комбинированный режим,
это позволило бы присутствовать на всех зонах доступности, тем самым обеспечивая отказоустойчивость. По причине экономии
так же была выбрана конфигурация машины с типом Burstable - ВМ с неполной гарантированной долей vCPU.

## Конфигурация топика:

<p>
 <img src="./screens/Screenshot_5.png" alt="qr"/>
</p>

<p>
 <img src="./screens/Screenshot_6.png" alt="qr"/>
</p>

Скриншот ответа вызова curl http://localhost:8081/subjects и curl -X GET http://localhost:8081/subjects/<название_схемы>/versions


<p>
 <img src="./screens/Screenshot_7.png" alt="qr"/>
</p>

Вывод команды kafka-topics.sh --describe. Удобный формат для просмотра представлен в файле [topic_describe.json](/logs/topic_describe.json)

<p>
 <img src="./screens/Screenshot_8.png" alt="qr"/>
</p>

Файл схемы для Schema Registry представлен в файле [Notification.avsc](/src/main/resources/Notification.avsc)

## Скриншоты, подтверждающие успешную передачу и чтение сообщений:

<p>
 <img src="./screens/Screenshot_9.png" alt="qr"/>
</p>

<p>
 <img src="./screens/Screenshot_10.png" alt="qr"/>
</p>

<p>
 <img src="./screens/Screenshot_11.png" alt="qr"/>
</p>

# 2. Интеграция Kafka с внешними системами Apache NiFi / Hadoop

## Схема Flow

<p>
 <img src="./screens/Screenshot_12.png" alt="qr"/>
</p>

## Конфигурация ConsumeKafkaRecord_2_0 процессора:

<p>
 <img src="./screens/Screenshot_13.png" alt="qr"/>
</p>

## Конфигурация JoltTransformJSON процессора:

<p>
 <img src="./screens/Screenshot_14.png" alt="qr"/>
</p>

## Конфигурация PutHDFS процессора:

<p>
 <img src="./screens/Screenshot_15.png" alt="qr"/>
</p>

## NiFi Flow Configuration:

<p>
 <img src="./screens/Screenshot_16.png" alt="qr"/>
</p>

## Скриншоты, подтверждающие модернизацию и запись сообщений в Hadoop:

<p>
 <img src="./screens/Screenshot_17.png" alt="qr"/>
</p>

<p>
 <img src="./screens/Screenshot_18.png" alt="qr"/>
</p>