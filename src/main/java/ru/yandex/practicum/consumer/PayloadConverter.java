//package ru.yandex.practicum.consumer;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import ru.yandex.practicum.dto.Notification;
//
//@Service
//@RequiredArgsConstructor
//public class PayloadConverter {
//
//    private final ObjectMapper objectMapper;
//
//    public Notification toNotification(Object object) {
//        return objectMapper.convertValue(object, Notification.class);
//    }
//}
