package ru.yandex.practicum.http.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.http.ApiError;

@Slf4j
@RestControllerAdvice
public class NotificationControllerExceptionHandler {

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BindException.class)
    public ApiError handleExceptions(BindException ex, HttpServletRequest request) {
        log.error("Failed to return response", ex);

        return ApiError.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .code("ERR_BINDING")
                .message("Validation failed")
                .details(ex.getMessage())
                .path(request.getRequestURI()).build();
    }

}
