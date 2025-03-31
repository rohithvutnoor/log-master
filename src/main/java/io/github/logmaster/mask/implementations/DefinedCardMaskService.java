package io.github.logmaster.mask.implementations;

import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefinedCardMaskService implements LogMask {

    private final LogObjectMapperUtil logObjectMapperUtil;

    @Override
    public String mask (Object message) {
        if (message instanceof String text) {
            return getMaskedAccountOrCardNumber(text);
        }
        return logObjectMapperUtil.toStringMasked(message);
    }
    private String getMaskedAccountOrCardNumber (String unmaskedValue) {
        return unmaskedValue;
    }
}
