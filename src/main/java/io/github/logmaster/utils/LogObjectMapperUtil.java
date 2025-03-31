package io.github.logmaster.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogObjectMapperUtil {
    private final ObjectMapper objectMapper;
    private final ObjectMapper maskingObjectMapper;

    public LogObjectMapperUtil(ObjectMapper objectMapper, @Qualifier("maskingObjectMapper") ObjectMapper maskingObjectMapper) {
        this.objectMapper = objectMapper;
        this.maskingObjectMapper = maskingObjectMapper;
    }

    public String toString(Object data) {
        if (null == data) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error while converting from object to string", e);
        }
        return data.toString();
    }

    public <T> T toObject(String data, Class<T> tClass) {
        if (StringUtils.isNotBlank(data) && tClass != null) {
            try {
                return objectMapper.readValue(data, tClass);
            } catch (JsonProcessingException e) {
                log.error("Error while converting from string to object", e);
            }
        }
        return null;
    }

    public String toStringMasked(Object data) {
        try {
            return maskingObjectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error while converting & masking from object to string", e);
        }
        return toString(data);
    }
}

