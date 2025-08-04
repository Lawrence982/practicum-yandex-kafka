//package ru.yandex.practicum.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ru.yandex.practicum.model.BlockedUser;
//import ru.yandex.practicum.service.BlockedUserService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/user")
//public class UserController {
//
//    @Autowired
//    private BlockedUserService blockedUserService;
//
//    @PostMapping("/block")
//    public ResponseEntity<Void> blockUser(@RequestBody BlockedUser blockedUser) {
//        blockedUserService.blockUser(blockedUser);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @PostMapping("/block/batch")
//    public ResponseEntity<Void> blockUserBatch(@RequestBody List<BlockedUser> blockedUsers) {
//        blockedUserService.blockUserBatch(blockedUsers);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//}
