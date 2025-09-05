package ru.yandex.practicum.converter;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.avro.Image;

@Service
public class ImageAvroConverter implements AvroConverter<Image, ru.yandex.practicum.model.Image> {

    @Override
    public Image toAvro(ru.yandex.practicum.model.Image image) {
        if (image == null) {
            return null;
        }
        return Image.newBuilder()
                .setUrl(image.getUrl())
                .setAlt(image.getAlt())
                .build();
    }

    @Override
    public ru.yandex.practicum.model.Image fromAvro(Image image) {
        if (image == null) {
            return null;
        }
        return ru.yandex.practicum.model.Image.builder()
                .url(image.getUrl())
                .alt(image.getAlt())
                .build();
    }
}
