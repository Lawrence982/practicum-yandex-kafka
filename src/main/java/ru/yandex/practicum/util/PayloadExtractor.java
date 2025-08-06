package ru.yandex.practicum.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.DebeziumDto;

@Component
public class PayloadExtractor {

    @Autowired
    ObjectMapper objectMapper;

    public <T> T extract(DebeziumDto wrapperDto, Class<T> type) {
        return objectMapper.convertValue(wrapperDto.payload(), type);
    }
}
