package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.database.entity.Order;
import ru.yandex.practicum.service.EntityCrudService;

@RestController
@RequestMapping("/order")
public class OrderController extends AbstractEntityController<Long, Order> {

    public OrderController(EntityCrudService<Long, Order> orderService) {
        super(orderService);
    }
}
