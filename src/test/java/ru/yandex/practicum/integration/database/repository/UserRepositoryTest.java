//package ru.yandex.practicum.integration.database.repository;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.database.repository.UserRepository;
//import ru.yandex.practicum.integration.IntegrationTestBase;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertSame;
//
//@RequiredArgsConstructor
//public class UserRepositoryTest extends IntegrationTestBase {
//
//    private final UserRepository userRepository;
//
//    @Test
//    void checkQueries() {
//        var users = userRepository.findAllByNameContaining("Alice");
//        assertThat(users).hasSize(1);
//    }
//
//    @Test
//    void checkUpdate() {
//        var alice = userRepository.getReferenceById(3L);
//        assertNotNull(alice);
//
//        alice.setEmail("test@test.ru");
//
//        var theSameAlice = userRepository.saveAndFlush(alice);
//        assertSame("test@test.ru", theSameAlice.getEmail());
//    }
//
//}
