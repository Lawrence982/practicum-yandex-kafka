package ru.yandex.practicum.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.CustomerStatistics;
import ru.yandex.practicum.repository.CustomerStatisticsRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerAggregator {

    @Autowired
    private CustomerStatisticsRepository repository;

    public void applyIncrements(long batchId, List<RowAggregation> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        var customerToNewStatistic = groupByProductForCustomer(rows);
        Collection<String> customerIds = customerToNewStatistic.keySet();
        var existingStatistics = getExistingStatisticByIds(customerIds);
        List<CustomerStatistics> toSave = new ArrayList<>(customerToNewStatistic.size());
        customerToNewStatistic.forEach((customerId, newStatistic) -> {
            CustomerStatistics customerStatistic = existingStatistics.getOrDefault(customerId, new CustomerStatistics(customerId));
            if (customerStatistic.getLastAppliedBatchId() == null  || customerStatistic.getLastAppliedBatchId() != batchId) {
                newStatistic.forEach((productId, statistic) ->
                        customerStatistic.addProductStatistic(productId, statistic.getProductName(), statistic.getCount()));
                customerStatistic.setLastAppliedBatchId(batchId);
                if (customerStatistic.getVersion() != null) {
                    customerStatistic.setVersion(customerStatistic.getVersion() + 1);
                }
                toSave.add(customerStatistic);
            }
        });
        saveAllWithRetry(toSave, 3);
    }

    private Map<String, Map<String, RowAggregation>> groupByProductForCustomer(List<RowAggregation> rows) {
        return rows.stream()
                .collect(Collectors.groupingBy(
                        r -> r.customerId,
                        Collectors.toMap(
                                row -> row.productId,
                                Function.identity(),
                                (first, second) ->
                                        new RowAggregation(
                                                first.customerId,
                                                first.productId,
                                                first.productName,
                                                first.count + second.count
                                        )
                        )));
    }

    private Map<String, CustomerStatistics> getExistingStatisticByIds(Collection<String> ids) {
        Map<String, CustomerStatistics> existing = new HashMap<>();
        repository.findAllById(ids).forEach(stat -> {
            existing.put(stat.getId(), stat);
        });
        return existing;
    }

    private void saveAllWithRetry(List<CustomerStatistics> toSave, int maxRetries) {
        int attempt = 0;
        List<CustomerStatistics> pending = toSave;
        while (!pending.isEmpty() && attempt <= maxRetries) {
            try {
                repository.saveAll(pending);
                return;
            } catch (OptimisticLockingFailureException e) {
                pending = pending.stream()
                        .map(this::getUpdated)
                        .filter(Objects::nonNull)
                        .toList();
                attempt++;
            }
        }
        if (!pending.isEmpty()) {
            throw new OptimisticLockingFailureException("Failed to save some docs after retries: " + pending.size());
        }
    }

    private CustomerStatistics getUpdated(CustomerStatistics newStatistic) {
        Optional<CustomerStatistics> savedOpt = repository.findById(newStatistic.getId());
        if (savedOpt.isEmpty()) {
            return newStatistic;
        }
        CustomerStatistics saved = savedOpt.get();
        if (Objects.equals(saved.getLastAppliedBatchId(), newStatistic.getLastAppliedBatchId())) {
            return null;
        }
        newStatistic.getProducts().forEach((id, statistic) ->
                saved.addProductStatistic(id, statistic.getProductName(), statistic.getCount()));
        saved.setLastAppliedBatchId(newStatistic.getLastAppliedBatchId());
        return saved;
    }
}
