package ru.yandex.practicum.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.service.api.ForbiddenSkuService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ForbiddenSkuServiceImpl implements ForbiddenSkuService {

    public final static Set<String> FORBIDDEN_SKU = new HashSet<>();

    @Override
    public boolean isSkuForbidden(String sku) {
        log.info("checking sku = {}", sku);
        return sku != null && FORBIDDEN_SKU.contains(sku.toLowerCase());
    }

    @Override
    public Set<String> addForbiddenSku(List<String> skuList) {
        for (String sku : skuList) {
            FORBIDDEN_SKU.add(sku.toLowerCase());
        }
        return FORBIDDEN_SKU;
    }

    @Override
    public Set<String> deleteForbiddenSku(List<String> skuList) {
        for (String sku : skuList) {
            FORBIDDEN_SKU.remove(sku.toLowerCase());
        }
        return FORBIDDEN_SKU;
    }
}
