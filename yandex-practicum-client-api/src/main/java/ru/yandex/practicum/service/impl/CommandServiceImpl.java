package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.converter.CustomerRequestAvroConverter;
import ru.yandex.practicum.model.CustomerStatistics;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.avro.CustomerRequest;
import ru.yandex.practicum.repository.CustomerStatisticsRepository;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.service.api.CommandService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandServiceImpl implements CommandService {

    @Value("${topic.customer-requests.name}")
    private String customerRequestTopic;

    final ProductRepository productRepository;

    final CustomerStatisticsRepository customerStatisticsRepository;

    final KafkaTemplate<String, CustomerRequest> kafkaTemplate;

    final CustomerRequestAvroConverter customerRequestAvroConverter;

    @Override
    public List<Product> searchByName(String name, String customerId) {
        List<Product> products = productRepository.findByNameContaining(name);

        CustomerRequest customerRequest = customerRequestAvroConverter.toAvro(products, customerId);

        log.info("Sending CustomerRequest: {}", customerRequest);

        ProducerRecord<String, CustomerRequest> record =
                new ProducerRecord<>(customerRequestTopic, customerRequest.getCustomerId(), customerRequest);

        kafkaTemplate.send(record).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Failed to send product: {}", ex.getMessage());
            } else {
                RecordMetadata recordMeta = res.getRecordMetadata();
                log.debug("Product has been sent to {}", recordMeta.topic());
            }
        });
        return products;
    }

    @Override
    public List<String> getProductRecommendations(String customerId) {
        Optional<CustomerStatistics> customerStatistics = customerStatisticsRepository.findById(customerId);
        return customerStatistics.map(statistics -> statistics.getProducts().values().stream()
                .sorted(Comparator.comparing(CustomerStatistics.ProductInfo::getCount).reversed())
                .map(CustomerStatistics.ProductInfo::getProductName)
                .limit(2)
                .toList()).orElseGet(List::of);
    }
}
