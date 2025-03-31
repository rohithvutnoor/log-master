package io.github.logmaster.mask.implementations;

import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MobileMaskService implements LogMask {
    @Value("${mask.expression.length.mobile:4}")
    private int length;

    private final LogObjectMapperUtil logObjectMapperUtil;
    public static final String SINGLE_DOT = "•";

    @Override
    public String mask(Object message) {
        if (message instanceof String text) {
            return getMaskedMobile(text);
        }
        return logObjectMapperUtil.toStringMasked(message);
    }

    private String getMaskedMobile(String mobileNumber) {
        if (StringUtils.isBlank(mobileNumber) || mobileNumber.length() <= length) {
            return mobileNumber;
        }
        String mask = SINGLE_DOT.repeat(mobileNumber.length() - length);
        return mask + mobileNumber.substring(mobileNumber.length() - length);
    }
}

