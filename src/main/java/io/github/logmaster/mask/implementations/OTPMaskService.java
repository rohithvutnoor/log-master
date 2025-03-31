package io.github.logmaster.mask.implementations;


import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OTPMaskService implements LogMask {
    @Value("$ (mask. expression. length.mobile: 4}")
    private int length;

    private final LogObjectMapperUtil logObjectMapperUtil;

    @Override
    public String mask(Object message) {
        if (message instanceof String text) {
            return getMaskedOTP(text);
        }
        return logObjectMapperUtil.toStringMasked(message);
    }

    private String getMaskedOTP(String input) {
        Pattern digitPattern = Pattern.compile("\\d{4,6}");
        Matcher matcher = digitPattern.matcher(input);
        if (matcher.find()) {
            String digits = matcher.group();
            int maskCount = Math.min(length, digits.length());
            String masked = "*".repeat(maskCount) + digits.substring(maskCount);
            return input.replaceFirst(digits, masked);
        }
        return input;
    }
}

