package ru.yandex.practicum.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import ru.yandex.practicum.database.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, RevisionRepository<Order, Long, Integer> {
}
