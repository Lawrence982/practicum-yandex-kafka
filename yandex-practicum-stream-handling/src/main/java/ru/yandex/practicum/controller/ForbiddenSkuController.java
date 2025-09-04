package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.api.ForbiddenSkuService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/forbiddenSku")
public class ForbiddenSkuController {

    @Autowired
    private ForbiddenSkuService forbiddenSkuService;

    @PostMapping
    public ResponseEntity<Set<String>> addForbiddenSku(@RequestBody List<String> skuList) {
        Set<String> result = forbiddenSkuService.addForbiddenSku(skuList);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping
    public ResponseEntity<Set<String>> deleteForbiddenSku(@RequestBody List<String> skuList) {
        Set<String> result = forbiddenSkuService.deleteForbiddenSku(skuList);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
