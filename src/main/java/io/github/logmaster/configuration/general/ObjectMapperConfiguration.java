package io.github.logmaster.configuration.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.logmaster.configuration.mask.MaskSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapperConfiguration {
    @Bean("maskingObjectMapper")
    ObjectMapper createMaskingMapper(MaskSerializer maskSerializer) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, maskSerializer);
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

