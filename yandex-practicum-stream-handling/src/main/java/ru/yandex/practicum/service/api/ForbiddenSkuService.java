package ru.yandex.practicum.service.api;

import java.util.List;
import java.util.Set;

public interface ForbiddenSkuService {

    boolean isSkuForbidden(String sku);

    Set<String> addForbiddenSku(List<String> skuList);

    Set<String> deleteForbiddenSku(List<String> skuList);
}
