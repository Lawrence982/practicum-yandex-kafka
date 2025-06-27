package ru.yandex.practicum.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Product;

import java.util.List;

@Slf4j
@Component
public class BatchMessageConsumer {

    @Autowired
    private RecordBuffer recordBuffer;

    @KafkaListener(topics = "${product.topic.name}", containerFactory = "batchKafkaListenerContainerFactory")
    public void handle(ConsumerRecords<String, Product> records,
                       Consumer<String, Object> consumer) {
        recordBuffer.addRecords(records);
        if (recordBuffer.size() >= 10) {
            handleMessages(recordBuffer.getMessages());
            consumer.commitSync(recordBuffer.getOffsetMap());
            recordBuffer.clear();
        }
    }

    private void handleMessages(List<Product> messages) {
        log.info("Start of batch messages ({})", messages.size());
        messages.forEach(message -> log.info("Message in batch: {}", message));
        log.info("End of batch");
    }
}
