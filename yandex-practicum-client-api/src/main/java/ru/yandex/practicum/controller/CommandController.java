package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.service.api.CommandService;

import java.util.List;

@RestController
@RequestMapping("/command")
public class CommandController {

    @Autowired
    CommandService commandService;

    @GetMapping("/product-search/{name}")
    public ResponseEntity<List<Product>> search(@PathVariable String name) {
        List<Product> products = commandService.searchByName(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(products);
    }

    @GetMapping("/product-recommendations")
    public ResponseEntity<List<Product>> getRecommendations() {
        List<Product> products = commandService.getProductRecommendations();
        return ResponseEntity.status(HttpStatus.CREATED).body(products);
    }

}
