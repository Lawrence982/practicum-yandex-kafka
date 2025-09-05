package ru.yandex.practicum.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Specification {
    private String weight;
    private String dimensions;
    private String batteryLife;
    private String waterResistance;
}


