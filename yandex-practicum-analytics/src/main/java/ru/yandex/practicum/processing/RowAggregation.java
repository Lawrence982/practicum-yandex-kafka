package ru.yandex.practicum.processing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RowAggregation {
    String customerId;
    String productId;
    String productName;
    long count;
}
