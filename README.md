# Описание проекта
Проект представляет собой Spring Boot приложение, написанное на Java и использующее Maven для автоматизации сборки.

Основное назначение приложения - работа с БД, прослушивание kafka топиков и вывод сообщений в терминал.

Для работы с Kafka использовалась библиотека spring-kafka (org.springframework.kafka).

Для работы с БД использовались библиотеки:
- postgresql (взаимодействие с базой данных PostgreSQL с помощью JDBC драйвер, предоставляемого PostgreSQL).
- spring-boot-starter-data-jpa (позволяет создавать таблицы на основе Java объектов, предоставляет API для выполнения CRUD операций с данными).
- spring-data-envers (отслеживание версий (ревизий) сущностей в Spring Data JPA, используя Hibernate Envers)

Для создания образа используется Dockerfile, который разворачивается с помощью Docker Compose.

# Назначение каждого компонента и их взаимосвязи

- Apache Kafka - брокер сообщений.
- Kafka UI - графический интерфейс.
- Postgres - реляционная СУБД.
- Java приложение.  
  При старте создает таблицы orders и users в БД, а также топики yandex.public.orders и yandex.public.users в kafka.
  Отправляет настройки коннектора debezium в Kafka Connect.
  Имеет rest api для выполнения CRUD операций с основными таблицами базы.
  Прослушивает созданные в kafka топики и выводит логи о полученных сообщениях в терминал.
- Kafka Connect - захватывает изменения из БД (вставки, обновления и удаления) и отправляет данные в топики kafka.
- Prometheus - собирает метрики из kafka connect.
- Grafana - создает графики для визуализации метрик передачи данных и мониторинга работоспособности Kafka Connector.

# Инструкция по запуску
Установить docker и docker compose.

Выполнить команду из корня проекта:
```
docker compose up
```
После сборки и запуска убедиться, что все приложения имеют статус UP
```
docker ps -a
```

Что будет поднято:
- Кластер Kafka на основе Bitnami образов (kafka-0, kafka-1, kafka-2).
- Kafka UI (графический интерфейс).
- Java приложение, выполняющее роль консьюмера и имеющее web api для взаимодействия с базой данных.
- Kafka Connect, выполняющее роль интеграцию Kafka с внешним хранилищем данных (базой данных postgres).
- Prometheus - система мониторинга, которая собирает метрики с Kafka Connect (:9876/metrics).
- Grafana - платформа для визуализации и анализа данных.

Для просмотра логов приложения:

```
docker compose logs app
```

# Метрики и мониторинг

В образ Kafka Connect интегрирован агент JMX Exporter от Prometheus — он помогает исследовать, как меняется пропускная способность, 
а Grafana отображает эти данные в дашборде. 

Для метрик используется сконфигуренный дашборд (Kafka Connect Overview), который доступен по ссылке http://localhost:3000
(логин admin, пароль kafka). По этим данным можем отслеживать этапы извлечения, преобразования и отправки данных.

# Пошаговые инструкции по проверке работоспособности решения

Для тестирования приложения можно использовать приложения по типу Postman.

1. Убедиться в том, что коннектор настроен и работает, можно с помощью запроса:

```
GET /connectors/pg-connector/status
```
Ожидаемый ответ:

```
{
    "name": "pg-connector",
    "connector": {
        "state": "RUNNING",
        "worker_id": "localhost:8083"
    },
    "tasks": [
        {
            "id": 0,
            "state": "RUNNING",
            "worker_id": "localhost:8083"
        }
    ],
    "type": "source"
}
```

2. Для создания, получения, изменения или удаление пользователей и ордеров можно использовать REST запросы, описанные ниже в разделе "Описание REST запросов".

Тестовые данные для запросов можно найти в файле "Kafka Unit 4.postman_collection.json".

Все изменения будут сохранены в базу данных, а затем отправлены в kafka с помощью kafka connect.


Описание REST запросов:

- Создание пользователей:
```
POST /user/batch
[
    {
        "name": "Alice",
        "email": "alice@mail.ru"
    },
    {
        "name": "Bob",
        "email": "bob@mail.ru"
    }
]
 ```

- Создание ордеров:
```
POST /order/batch
[
    {
        "userId": "{userId}",
        "productName": "Mobile Plan",
        "quantity": 2
    },
    {
        "userId": "{userId}",
        "productName": "Internet",
        "quantity": 3
    }
]
 ```
В результате выполнения запросов в БД будут созданы соответствующие сущности.
В качестве ответа будут возвращены созданные сущности с соответствующими идентификаторами (поле id).
- Получение списка пользователей
```
GET /user
```
- Получение пользователя по id
```
GET /user/{userId}
```
- Получение списка ордеров
```
GET /order
```
- Получение ордера по id
```
GET /order/{orderId}
```
- Редактирование пользователя:
```
PATCH /user/{userId}
{
      "name": "Anna",
      "email": "anna@mail.ru"
}
 ```
- Редактирование ордера:
```
PATCH /order/{orderId}
{
     "quantity": 5,
     "productName": "iPhone"
}
 ```
- Удаление пользователя
```
DELETE /user/{userId}
```
- Удаление ордера
```
DELETE /order/{orderId}
```
3. Все сообщения из kafka будут прослушаны приложением и выведены в логи.

Примеры логов:
- Создание/изменение пользователя

  r.y.practicum.consumer.UserConsumer      : Consumer received user: UserDto[id=1, name=John Doe, email=john@example.com]

- Удаление пользователя

  r.y.practicum.consumer.UserConsumer     : User was deleted

- Создание/изменение ордера

  r.y.practicum.consumer.OrderConsumer     : Consumer received order: OrderDto[id=1, userId=1, productName=Product A, quantity=2, orderDate=2025-08-07T10:21:55.035484Z]

- Удаление ордера

  r.y.practicum.consumer.OrderConsumer     : Order was deleted

4) После необходимых операций над данными посмотреть дашборд в Grafana