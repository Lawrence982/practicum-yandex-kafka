package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.service.EntityCrudService;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractEntityCrudService<Key, Entity> implements EntityCrudService<Key, Entity> {

    private final JpaRepository<Entity, Key> entityRepository;

    @Override
    public Entity findById(Key id) {
        return entityRepository.findById(id).orElse(null);
    }

    @Override
    public List<Entity> findAll() {
        return entityRepository.findAll();
    }

    @Override
    @Transactional
    public Entity create(Entity entity) {
        return entityRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public List<Entity> createAll(List<Entity> entity) {
        return entityRepository.saveAllAndFlush(entity);
    }

    @Override
    @Transactional
    public Entity update(Key id, Entity changes) {
        Optional<Entity> optSaved = entityRepository.findById(id);
        if (optSaved.isEmpty()) {
            return null;
        }
        Entity saved = optSaved.get();
        updateSavedEntity(changes, saved);
        return entityRepository.saveAndFlush(saved);
    }

    @Override
    @Transactional
    public void delete(Key id) {
        entityRepository.deleteById(id);
    }

    protected abstract void updateSavedEntity(Entity changes, Entity savedEntity);

    protected <Parameter> void setIfNotNull(Parameter value, Consumer<Parameter> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
