CREATE STREAM messages_stream (
       id          STRING,
       userId      INT,
       recipientId INT,
       message     STRING,
       timestamp   STRING
) WITH (
       KAFKA_TOPIC='messages-topic',
       VALUE_FORMAT='JSON',
       PARTITIONS=3
);

SELECT *
FROM messages_stream
EMIT CHANGES
LIMIT 5;

-- общее количество отправленных сообщений
CREATE TABLE total_message_count AS
SELECT 1, COUNT(*) AS count
FROM messages_stream
GROUP BY 1
EMIT CHANGES;


SELECT count
FROM total_message_count
EMIT CHANGES
LIMIT 1;

-- количество уникальных получателей сообщений
CREATE TABLE unique_recipients_count AS
SELECT 1, COUNT_DISTINCT(recipientId) AS count
FROM messages_stream
GROUP BY 1
EMIT CHANGES;

SELECT count
FROM unique_recipients_count
EMIT CHANGES
LIMIT 1;

-- количество сообщений, отправленных каждым пользователем и количество уникальных получателей для каждого пользователя
CREATE TABLE user_statistics AS
SELECT userId,
       COUNT(*) AS message_count,
       COUNT_DISTINCT(recipientId) AS unique_recipient
FROM messages_stream
GROUP BY userId
EMIT CHANGES;

SELECT *
FROM user_statistics
EMIT CHANGES;