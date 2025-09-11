package ru.yandex.practicum.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ru.yandex.practicum.model.CustomerStatistics;

public interface CustomerStatisticsRepository extends ElasticsearchRepository<CustomerStatistics, String> {
}
