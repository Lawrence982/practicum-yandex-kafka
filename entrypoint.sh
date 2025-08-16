#!/bin/sh
set -e

## Запуск оригинального entrypoint
/etc/confluent/docker/run "$@" &
PID=$!

# Ждем, когда брокер станет готов к управлению ACL
until kafka-broker-api-versions --bootstrap-server kafka-0:9093 --command-config /etc/kafka/properties/client.properties; do
  echo "Waiting for Kafka to be ready..."
  sleep 1
done

# Применяем ACL в нужной последовательности
kafka-acls --bootstrap-server kafka-0:9093 --add --allow-principal User:'consumer1' --operation READ --group kafka-security-message --command-config /etc/kafka/properties/client.properties
kafka-acls --bootstrap-server kafka-0:9093 --add --allow-principal User:'consumer1' --operation DESCRIBE --group kafka-security-message --command-config /etc/kafka/properties/client.properties
kafka-acls --bootstrap-server kafka-0:9093 --add --allow-principal User:'consumer1' --operation READ --topic topic-1 --command-config /etc/kafka/properties/client.properties

## Ждем завершения фонового процесса
wait $PID