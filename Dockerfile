FROM maven:3.8.6-amazoncorretto-17 AS build
WORKDIR /build

COPY pom.xml .
#RUN mvn dependency:go-offline
COPY yandex-practicum-application/pom.xml yandex-practicum-application/pom.xml
COPY yandex-practicum-application/src yandex-practicum-application/src

COPY yandex-practicum-client-api/pom.xml yandex-practicum-client-api/pom.xml
COPY yandex-practicum-client-api/src yandex-practicum-client-api/src

COPY yandex-practicum-common/pom.xml yandex-practicum-common/pom.xml
COPY yandex-practicum-common/src yandex-practicum-common/src

COPY yandex-practicum-shop-api/pom.xml yandex-practicum-shop-api/pom.xml
COPY yandex-practicum-shop-api/src yandex-practicum-shop-api/src

COPY yandex-practicum-stream-handling/pom.xml yandex-practicum-stream-handling/pom.xml
COPY yandex-practicum-stream-handling/src yandex-practicum-stream-handling/src

COPY yandex-practicum-analytics/pom.xml yandex-practicum-analytics/pom.xml
COPY yandex-practicum-analytics/src yandex-practicum-analytics/src

RUN mvn package -DskipTests

#Run stage
FROM openjdk:17
ARG JAR_FILE=/build/yandex-practicum-application/target/*.jar
COPY --from=build $JAR_FILE /opt/yandex-kafka/app.jar
COPY creds/cluster-0/kafka-0-creds /opt/yandex-kafka/kafka-0-creds
ENTRYPOINT ["java", "-jar", "/opt/yandex-kafka/app.jar"]