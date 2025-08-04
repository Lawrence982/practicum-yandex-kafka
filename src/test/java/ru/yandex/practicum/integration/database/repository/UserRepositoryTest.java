package ru.yandex.practicum.integration.database.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Commit;
import ru.yandex.practicum.database.repository.UserRepository;
import ru.yandex.practicum.integration.annotation.IT;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@IT
@RequiredArgsConstructor
public class UserRepositoryTest {

    private final UserRepository userRepository;

    @Test
    void checkQueries() {
        var users = userRepository.findAllByNameContaining("Alice");
        assertThat(users).hasSize(1);
    }

    @Test
    void checkUpdate() {
        var alice = userRepository.getReferenceById(3L);
        assertNotNull(alice);

        alice.setEmail("test@test.ru");

        var theSameAlice = userRepository.saveAndFlush(alice);
        assertSame("test@test.ru", theSameAlice.getEmail());
    }

}
