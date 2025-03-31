package io.github.logmaster.mask.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.logmaster.helpers.MaskHelper;
import io.github.logmaster.mask.interfaces.LogMask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultMaskService implements LogMask {

    private final MaskHelper maskHelper;

    private final ObjectMapper maskingObjectMapper;

    public DefaultMaskService(MaskHelper maskHelper, @Qualifier("maskingObjectMapper") ObjectMapper maskingObjectMapper) {
        this.maskHelper = maskHelper;
        this.maskingObjectMapper = maskingObjectMapper;
    }

    @Override
    public String mask(Object data) {
        if (data == null) return "";
        if (data instanceof String text) {
            return maskHelper.mask(text);
        }
        try {
            return maskingObjectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error while masking object: {}", e.getMessage());
        }
        return data.toString();
    }
}

