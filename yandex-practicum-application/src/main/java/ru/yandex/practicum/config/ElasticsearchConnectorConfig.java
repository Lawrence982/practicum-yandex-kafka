package ru.yandex.practicum.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class ElasticsearchConnectorConfig {

    private final ESConnectorProperties connectorProperties;
    private final KafkaConnectUrls kafkaConnectUrls;
    private final RestTemplate restTemplate;

    public ElasticsearchConnectorConfig(ESConnectorProperties connectorProperties,
                                        KafkaConnectUrls kafkaConnectUrls,
                                        RestTemplateBuilder builder) {
        this.connectorProperties = connectorProperties;
        this.kafkaConnectUrls = kafkaConnectUrls;
        this.restTemplate = builder.build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> properties = connectorProperties.getProperties();
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(properties, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    kafkaConnectUrls.getConfig(),
                    HttpMethod.PUT,
                    entity,
                    String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Kafka connector config has been send");
            } else {
                log.warn("Kafka connector config has not been send");
            }
        } catch (Exception ex) {
            log.error("There is an error during sending kafka connector config", ex);
        }
    }
}
