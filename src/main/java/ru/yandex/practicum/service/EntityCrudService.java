package ru.yandex.practicum.service;

import java.util.List;

public interface EntityCrudService<Key, Entity> {
    Entity findById(Key id);

    List<Entity> findAll();

    Entity create(Entity entity);

    Entity update(Key id, Entity changes);

    void delete(Key id);
}
