//package ru.yandex.practicum.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.yandex.practicum.model.Product;
//import ru.yandex.practicum.service.ProductService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/product")
//public class ProductController {
//
//    @Autowired
//    private ProductService productService;
//
//    @PostMapping
//    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
//        Product createdProduct = productService.createAndNotify(product);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
//    }
//
//    @PostMapping("/batch")
//    public ResponseEntity<List<Product>> createProductBatch(@RequestBody Product template,
//                                                            @RequestParam(defaultValue = "1") Integer amount) {
//        List<Product> products = productService.createBatchAndNotify(template, amount);
//        return ResponseEntity.status(HttpStatus.CREATED).body(products);
//    }
//}
