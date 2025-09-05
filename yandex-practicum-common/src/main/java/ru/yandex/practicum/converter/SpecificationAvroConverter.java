package ru.yandex.practicum.converter;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.avro.Specification;

@Service
public class SpecificationAvroConverter implements AvroConverter<Specification, ru.yandex.practicum.model.Specification> {

    @Override
    public Specification toAvro(ru.yandex.practicum.model.Specification specification) {
        if (specification == null) {
            return null;
        }
        return Specification.newBuilder()
                .setWeight(specification.getWeight())
                .setDimensions(specification.getDimensions())
                .setBatteryLife(specification.getBatteryLife())
                .setWaterResistance(specification.getWaterResistance())
                .build();
    }

    @Override
    public ru.yandex.practicum.model.Specification fromAvro(Specification specification) {
        if (specification == null) {
            return null;
        }
        return ru.yandex.practicum.model.Specification.builder()
                .weight(specification.getWeight())
                .dimensions(specification.getDimensions())
                .batteryLife(specification.getBatteryLife())
                .waterResistance(specification.getWaterResistance())
                .build();
    }
}
