package io.github.logmaster.mask.implementations;

import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CinMaskService implements LogMask {

    public static final String X = "X";

    @Value("${mask.expression.length.cin:4}")
    private int maskLength;

    private final LogObjectMapperUtil logObjectMapperUtil;

    @Override
    public String mask(Object message) {
        if (message instanceof String text) {
            return getMaskedCIN(text);
        }
        return logObjectMapperUtil.toStringMasked(message);
    }

    private String getMaskedCIN(String unMaskedCINVal) {
        if (StringUtils.isNotBlank(unMaskedCINVal)) {
            StringBuilder maskCIN = new StringBuilder();
            maskCIN.append(unMaskedCINVal, 0, maskLength);
            String strTemp = unMaskedCINVal.substring(maskLength);
            int i = 0;
            while (i < strTemp.length()) {
                maskCIN.append(X);
                i++;
            }
            return new String(maskCIN);
        }
        return unMaskedCINVal;
    }
}


