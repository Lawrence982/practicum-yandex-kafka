//package ru.yandex.practicum.service.impl;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Service;
//import ru.yandex.practicum.database.entity.Order;
//
//@Service
//public class OrderService extends AbstractEntityCrudService<Long, Order> {
//
//    public OrderService(JpaRepository<Order, Long> orderRepository) {
//        super(orderRepository);
//    }
//
//    @Override
//    protected void updateSavedEntity(Order changes, Order savedEntity) {
//        setIfNotNull(changes.getOrderDate(), savedEntity::setOrderDate);
//        setIfNotNull(changes.getQuantity(), savedEntity::setQuantity);
//        setIfNotNull(changes.getProductName(), savedEntity::setProductName);
//        setIfNotNull(changes.getUserId(), savedEntity::setUserId);
//    }
//}
