package ru.yandex.practicum.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
@Document(indexName = "customer-statistics")
public class CustomerStatistics {
    @Id
    private String id;
    @Version
    private Long version;

    // products[productId] -> {count, productName}
    private Map<String, ProductInfo> products;

    // защита от повторной обработки одного и того же микробатча
    private Long lastAppliedBatchId;

    public CustomerStatistics(String id) {
        this.id = id;
    }

    @Data
    public static class ProductInfo {
        private long count;
        private String productName;
    }

    public void addProductStatistic(String productId, String productName, long count) {
        if (products == null) {
            products = new HashMap<>();
        }
        ProductInfo productInfo = products.get(productId);
        if (productInfo == null) {
            productInfo = new ProductInfo();
            productInfo.setProductName(productName);
            products.put(productId, productInfo);
        }
        productInfo.setCount(productInfo.getCount() + count);
    }
}
