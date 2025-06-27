package ru.yandex.practicum.consumer;

import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RecordBuffer {

    @Value("${product.topic.name}")
    private String topicName;

    @Getter
    private final List<Product> messages = new ArrayList<>();

    private final Map<Integer, Long> partitionToOffset = new HashMap<>();

    public int size() {
        return messages.size();
    }

    public void addRecords(ConsumerRecords<String, Product> records) {
        records.forEach(record -> {
            messages.add(record.value());
            partitionToOffset.merge(record.partition(), record.offset(), Math::max);
        });
    }

    public void clear() {
        messages.clear();
        partitionToOffset.clear();
    }

    public Map<TopicPartition, OffsetAndMetadata> getOffsetMap() {
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        partitionToOffset.forEach((partition, offset) -> {
            offsets.put(new TopicPartition(topicName, partition), new OffsetAndMetadata(offset + 1));
        });
        return offsets;
    }
}
