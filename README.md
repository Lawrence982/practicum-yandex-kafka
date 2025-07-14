# Описание проекта
Проект представляет собой Spring Boot приложение, написанное на Java и использующее Maven для автоматизации сборки.

Основное назначение приложения - фильтрация и обработка сообщений, поступающих из kafka топика messages, 
а также сохранение обработанных сообщений в топике filtered_messages.

Для работы с Kafka использовалась библиотека spring-kafka (org.springframework.kafka).
Для работы с потоками Kafka использовалась библиотека kafka-streams (org.apache.kafka)

Для создания образа используется Dockerfile, который разворачивается с помощью Docker Compose.

# Классы и структура

- deployment/compose.yaml - файл конфигурации, описывающий контейнеры, которые будут запущены в Docker.
- Dockerfile - инструкции для создания Docker образа приложения. 
- pom.xml - файл конфигурации Maven, определяет структуру проекта и его зависимости.
- src/main/resources/application.yaml - файл конфигурации spring boot для хранения настроек.
- src/main/java/ru/yandex/practicum/model/BlockedUser.java - структура сообщения для работы с Kafka топиком blocked_users.
- src/main/java/ru/yandex/practicum/model/Message.java - структура сообщения для работы с Kafka топиками messages и filtered_messages.
- src/main/java/ru/yandex/practicum/config/KafkaTopicConfig.java - конфигурация для автоматического создания топиков.
- src/main/java/ru/yandex/practicum/config/TopicsProperties.java - класс для удобного хранения конфигураций топиков.
- src/main/java/ru/yandex/practicum/consumer/FilteredMessageConsumer.java - класс для получения уже отфильтрованных и обработанных сообщений из Kafka.
- src/main/java/ru/yandex/practicum/processor - классы для задания логики работы Kafka стримов.
- src/main/java/ru/yandex/practicum/serdes - классы для сериализации/десериализации сообщений при работе с Kafka стримами.
- src/main/java/ru/yandex/practicum/controller - класс для удобного тестирования работы приложения через REST запросы.
- src/main/java/ru/yandex/practicum/service - классы для отправки сообщений в Kafka топики.


# Принцип работы

Логику работы потока для заблокированных пользователей можно увидеть в классе BlockedUserProcessor.
Здесь создается поток, который слушает kafka топик blocked_users, а затем сохраняет сообщения из этого топика в 
персистентное хранилище blocked-user-store с ключом в формате "id заблокированного пользователя"-"id блокирующего пользователя". 

Логику работы потока для отправленных сообщений можно увидеть в классе MessagesProcessor.
Здесь создается поток, который: 
1. Слушает kafka топик messages.
2. Отфильтровывает сообщения, если в персистентном хранилище blocked-user-store существует запись с ключом "id отправителя"-"id получателя".
3. Заменяет все слова в тексте сообщения из списка запрещенных на строку "***".
4. Сохраняет преобразованные и отфильрованные сообщения в kafka топик filtered_messages.

Все запрещенные слова хранятся во внутреннем множестве класса MessageServiceImpl. 
Для их редактирования используются REST запросы класса MessageController. 

# Инструкция по запуску
Установить docker и docker compose.

Выполнить команду из корня проекта:
```
docker compose -f ./deployment/compose.yaml up
```
После сборки и запуска убедиться, что все приложения имеют статус UP
```
docker ps -a
```

Что будет поднято:
- Кластер Kafka на основе Bitnami образов (kafka-0, kafka-1, kafka-2).
- Kafka UI (графический интерфейс).
- Java приложение, выполняющее потоковую обработку сообщений из Kafka.
- ksqldb сервер

Для просмотра логов приложения:

```
docker compose logs app-1
```

# Инструкция по тестированию

Для тестирования приложения можно использовать приложения по типу Postman. 
Пример отправки с тестовыми данными можно найти в файле "Kafka Unit 2.postman_collection.json".

Описание REST запросов:
- Для того чтобы заблокировать одного или нескольких пользователей, можно использовать REST запросы:
```
POST /user/block
{
    "userId": "id of blocked user",
    "blockedUserId": "id of blocking user",
    "reason": "reason for blocking"
}
 ```
```
POST /user/block/batch
[
    {
        "userId": "id of blocked user",
        "blockedUserId": "id of blocking user",
        "reason": "reason for blocking"
    },
    {
        "userId": "id of blocked user",
        "blockedUserId": "id of blocking user",
        "reason": "reason for blocking"
    }
]
 ```
В результате выполнения этих запросов в kafka топик blocked_users будут отправлены соответствующие сообщения.

- Для добавления или удаления слов из списка запрещенных в сообщениях слов можно использовать REST запросы:
```
POST /message/censoredWord
[
    "first fordidden word",
    "second fordidden word"
]
 ```
```
DELETE /message/censoredWord
[
    "first fordidden word",
    "second fordidden word"
]
 ```
- Для удобной отправки одного или нескольких сообщений в kafka топик messages можно использовать REST запросы:
```
POST /message
{
    "userId": "id of sender",
    "recipientId": "id of recipient",
    "message": "text of message"
}
 ```
```
POST /message/batch
[
    {
        "userId": "id of sender",
        "recipientId": "id of recipient",
        "message": "text of message"
    },
    {
        "userId": "id of sender",
        "recipientId": "id of recipient",
        "message": "text of message"
    }
]
 ```