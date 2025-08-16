FROM maven:3.8.6-amazoncorretto-17 AS build
COPY pom.xml /build/
WORKDIR /build
#RUN mvn dependency:go-offline
COPY src /build/src
RUN mvn package -DskipTests

#Run stage
FROM openjdk:17
ARG JAR_FILE=/build/target/*.jar
COPY --from=build $JAR_FILE /opt/yandex-kafka/app.jar
COPY kafka-0-creds /opt/yandex-kafka/kafka-0-creds
ENTRYPOINT ["sh", "-c", "sleep 10; exec java -jar /opt/yandex-kafka/app.jar"]