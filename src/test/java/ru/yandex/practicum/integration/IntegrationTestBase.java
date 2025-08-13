//package ru.yandex.practicum.integration;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.jdbc.Sql;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.utility.DockerImageName;
//import ru.yandex.practicum.integration.annotation.IT;
//
//@IT
//@Sql({
//        "classpath:sql/data.sql"
//})
//public abstract class IntegrationTestBase {
//
//    private static final DockerImageName myImage = DockerImageName.parse("debezium/postgres:16").asCompatibleSubstituteFor("postgres");
//    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(myImage);
//
//    @BeforeAll
//    static void runContainer() {
//        container.start();
//    }
//
//    @DynamicPropertySource
//    static void postgresProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", container::getJdbcUrl);
//    }
//
//}
