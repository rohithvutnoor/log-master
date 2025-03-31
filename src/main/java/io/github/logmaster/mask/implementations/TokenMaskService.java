package io.github.logmaster.mask.implementations;

import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenMaskService implements LogMask {
    @Value("${mask.expression.length.token:10}")
    private int length;
    private final LogObjectMapperUtil logObjectMapperUtil;

    @Override
    public String mask(Object message) {
        if (message instanceof String text) {
            return maskJwtToken(text);
        }
        return logObjectMapperUtil.toStringMasked(message);
    }

    private String maskJwtToken(String token) {
        if (StringUtils.isBlank(token)) return token;
        int visibleLength = Math.min(length, token.length());
        return token.substring(0, visibleLength) + "********";
    }
}
