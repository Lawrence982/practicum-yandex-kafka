FROM confluentinc/cp-kafka-connect:7.7.1
# Скопировать JMX Exporter в докер образ
COPY ../etc/jmx_exporter/jmx_prometheus_javaagent-0.18.0.jar /opt/jmx_prometheus_javaagent-0.18.0.jar
# Скопировать настройки JMX Exporter в докер образ
COPY kafka-connect.yml /opt/kafka-connect.yml