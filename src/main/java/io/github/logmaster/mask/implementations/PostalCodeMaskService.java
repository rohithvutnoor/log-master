package io.github.logmaster.mask.implementations;


import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostalCodeMaskService implements LogMask {
    private final LogObjectMapperUtil logObjectMapperUtil;

    public static final String SINGLE_DOT = "•";

    @Override
    public String mask(Object message) {
        if (message instanceof String text) {
            return getMaskedPostalCode(text);
        }
        return logObjectMapperUtil.toStringMasked(message);
    }

    private String getMaskedPostalCode(String unMaskPostalCode) {
        if (StringUtils.isNotBlank(unMaskPostalCode)) {
            return SINGLE_DOT.repeat(unMaskPostalCode.length());
        }
        return unMaskPostalCode;
    }
}

