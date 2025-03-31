package io.github.logmaster.helpers;

import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.propertystore.MaskServiceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaskHelper {

    private final ApplicationContext applicationContext;
    private final MaskServiceProperties maskServiceProperties;

    public String mask(String unMaskedValue) {
        return mask(unMaskedValue, "sensitive");
    }

    public String mask(String unMaskedValue, String maskType) {
        Map<String, String> serviceDetails = maskServiceProperties.getService();
        String serviceName = serviceDetails.get(maskType);
        if (StringUtils.isNotBlank(serviceName)) {
            try {
                LogMask logMask = (LogMask)
                        applicationContext.getBean(Class.forName(serviceName));
                return logMask.mask(unMaskedValue);
            } catch (ClassNotFoundException e) {
                log.warn("Mask service class not found or implementation is incorrect for maskType: {} ", maskType);
                return unMaskedValue;
            }
        }
        log.warn("Mask service class not found or implementation is incorrect for maskType: {}", maskType);
        return unMaskedValue;
    }
}