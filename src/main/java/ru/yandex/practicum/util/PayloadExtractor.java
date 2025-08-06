package ru.yandex.practicum.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.DebeziumDto;

import java.util.List;
import java.util.stream.Stream;

@Component
public class PayloadExtractor {

    @Autowired
    ObjectMapper objectMapper;

    public <T> T extract(DebeziumDto wrapperDto, Class<T> type) {
        return objectMapper.convertValue(wrapperDto.payload(), type);
    }

    public <T> Stream<T> extract(List<DebeziumDto> listDto, Class<T> type) {
        return listDto.stream().map(dto -> extract(dto, type));
    }
}
