package ru.yandex.practicum.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@RequiredArgsConstructor
@EnableElasticsearchRepositories(basePackages
        = "ru.yandex.practicum.repository")
@ComponentScan(basePackages = { "ru.yandex.practicum" })
public class ElasticsearchConfig {

    @Value("${elasticsearch.connect-timeout:5000}")
    private int connectTimeoutMs;

    @Value("${elasticsearch.socket-timeout:30000}")
    private int socketTimeoutMs;

    @Value("${elasticsearch.connection-request-timeout:1000}")
    private int connectionRequestTimeoutMs;

    final JacksonJsonpMapper jacksonJsonpMapper;

    @Bean
    public ElasticsearchClient elasticsearchClient() {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeoutMs)
                .setSocketTimeout(socketTimeoutMs)
                .setConnectionRequestTimeout(connectionRequestTimeoutMs)
                .build();

        RestClient restClient = RestClient.builder(
                new HttpHost("elasticsearch", 9200, "http")
        ).setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultRequestConfig(requestConfig)
        ).build();

        RestClientTransport transport = new RestClientTransport(restClient, jacksonJsonpMapper);

        return new ElasticsearchClient(transport);
    }

}
