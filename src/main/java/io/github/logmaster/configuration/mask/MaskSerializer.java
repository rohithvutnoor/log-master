package io.github.logmaster.configuration.mask;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.logmaster.annotations.Mask;
import io.github.logmaster.helpers.MaskHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaskSerializer extends JsonSerializer<Object> {
    private final MaskHelper maskingService;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws
            IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        String fieldName = gen.getOutputContext().getCurrentName();
        Object parentObject = gen.getOutputContext().getCurrentValue();
        if (parentObject != null) {
            Field field = null;
            try {
                field = parentObject.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (Exception ex) {
                field = verifyJsonPropertyAssociation(fieldName, parentObject, field);

            }
            if (field != null && field.isAnnotationPresent(Mask.class)) {
                Mask maskAnnotation = field.getAnnotation(Mask.class);
                gen.writeString(maskingService.mask(value.toString(), maskAnnotation.type()));
                return;
            }
        }

        if (value instanceof String) {
            gen.writeString(value.toString());
        } else {
            serializers.defaultSerializeValue(value, gen);
        }
    }

    private static Field verifyJsonPropertyAssociation(String fieldName, Object parentObject,
                                                       Field field) {
        for (Field f : parentObject.getClass().getDeclaredFields()) {
            JsonProperty prop = f.getAnnotation(JsonProperty.class);
            if (prop != null && prop.value().equals(fieldName)) {
                field = f;
                break;
            }
        }
        return field;
    }
}

