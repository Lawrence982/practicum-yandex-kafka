#!/bin/sh
set -e

## Запуск оригинального entrypoint
/etc/confluent/docker/run "$@" &
PID=$!

# Ждем, когда брокер станет готов к управлению ACL
until kafka-broker-api-versions --bootstrap-server kafka-0:7093 --command-config /etc/kafka/properties/client.properties; do
  echo "Waiting for Kafka to be ready..."
  sleep 3
done

# Применяем ACL в нужной последовательности
kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation READ --topic products-topic --command-config /etc/kafka/properties/client.properties
kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation WRITE --topic products-topic --command-config /etc/kafka/properties/client.properties

kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation READ --topic filtered-products-topic --command-config /etc/kafka/properties/client.properties
kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation WRITE --topic filtered-products-topic --command-config /etc/kafka/properties/client.properties

kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation READ --topic customer-requests-topic --command-config /etc/kafka/properties/client.properties
kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation WRITE --topic customer-requests-topic --command-config /etc/kafka/properties/client.properties

kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation READ --group practicum-kafka-streams-app --command-config /etc/kafka/properties/client.properties
kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation DESCRIBE --group practicum-kafka-streams-app --command-config /etc/kafka/properties/client.properties

kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation READ --group filtered-product --command-config /etc/kafka/properties/client.properties
kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'admin' --operation DESCRIBE --group filtered-product --command-config /etc/kafka/properties/client.properties



kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'client1' --operation READ --topic filtered-products-topic --command-config /etc/kafka/properties/client.properties

kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'client1' --operation WRITE --topic customer-requests-topic --command-config /etc/kafka/properties/client.properties

kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'client1' --operation READ --group filtered-product --command-config /etc/kafka/properties/client.properties
kafka-acls --bootstrap-server kafka-0:7093 --add --allow-principal User:'client1' --operation DESCRIBE --group filtered-product --command-config /etc/kafka/properties/client.properties



## Ждем завершения фонового процесса
wait $PID