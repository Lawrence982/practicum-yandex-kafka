package ru.yandex.practicum.converter;

import org.apache.avro.specific.SpecificRecord;

import java.util.List;

public interface AvroConverter<Avro extends SpecificRecord, Dto> {

    Avro toAvro(Dto dto);

    Dto fromAvro(Avro avro);

    default List<Avro> toAvro(List<Dto> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::toAvro)
                .toList();
    }

    default List<Dto> fromAvro(List<Avro> avroList) {
        if (avroList == null) {
            return null;
        }
        return avroList.stream()
                .map(this::fromAvro)
                .toList();
    }
}
