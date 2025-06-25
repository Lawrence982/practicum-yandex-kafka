package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class YandexKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(YandexKafkaApplication.class, args);
    }

}
