package ru.yandex.practicum.service.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.database.entity.User;

@Service
public class UserService extends AbstractEntityCrudService<Long, User> {

    public UserService(JpaRepository<User, Long> userRepository) {
        super(userRepository);
    }

    @Override
    protected void updateSavedEntity(User changes, User savedEntity) {
        setIfNotNull(changes.getName(), savedEntity::setName);
        setIfNotNull(changes.getEmail(), savedEntity::setEmail);
    }
}
