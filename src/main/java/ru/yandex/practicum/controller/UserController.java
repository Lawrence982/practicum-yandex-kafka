package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.database.entity.User;
import ru.yandex.practicum.service.EntityCrudService;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractEntityController<Long, User> {

    public UserController(EntityCrudService<Long, User> userService) {
        super(userService);
    }
}