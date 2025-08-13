//package ru.yandex.practicum.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.yandex.practicum.service.EntityCrudService;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//public abstract class AbstractEntityController<Key, Entity> {
//
//    private final EntityCrudService<Key, Entity> entityService;
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Entity> findById(@PathVariable("id") Key id) {
//        Entity entity = entityService.findById(id);
//        return okOrNotFound(entity);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Entity>> findAll() {
//        List<Entity> all = entityService.findAll();
//        return ResponseEntity.ok(all);
//    }
//
//    @PostMapping
//    public ResponseEntity<Entity> create(@RequestBody Entity entity) {
//        Entity created = entityService.create(entity);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }
//
//    @PostMapping("/batch")
//    public ResponseEntity<List<Entity>> createAll(@RequestBody List<Entity> entities) {
//        List<Entity> created = entityService.createAll(entities);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<Entity> update(@PathVariable("id") Key id, @RequestBody Entity changes) {
//        Entity updated = entityService.update(id, changes);
//        return okOrNotFound(updated);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable("id") Key id) {
//        entityService.delete(id);
//        return ResponseEntity.ok().build();
//    }
//
//    private ResponseEntity<Entity> okOrNotFound(Entity entity) {
//        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
//    }
//}
