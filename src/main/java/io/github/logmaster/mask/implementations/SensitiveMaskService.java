package io.github.logmaster.mask.implementations;

import io.github.logmaster.helpers.MaskHelper;
import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.propertystore.SensitiveRegexProperties;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SensitiveMaskService implements LogMask {
    private final SensitiveRegexProperties regexProperties;
    private final LogObjectMapperUtil logObjectMapperUtil;
    private final MaskHelper maskHelper;

    @Override
    public String mask(Object message) {
        if (message instanceof String text) {
            return findAllSensitiveDataObjectsAndMask(text);
        }
        return logObjectMapperUtil.toStringMasked(message);
    }

    public String findAllSensitiveDataObjectsAndMask(String data) {
        String maskedValue = data;
        for (Map.Entry<String, String> entry : regexProperties.getPattern().entrySet()) {
            String key = entry.getKey();
            String regex = entry.getValue();
            if (regex != null && !regex.isEmpty()) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(maskedValue);
                StringBuilder sb = new StringBuilder();
                while (matcher.find()) {
                    String original  = matcher.group();
                    String masked = maskHelper.mask(original, key);
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(masked));
                    matcher.appendTail(sb);
                    maskedValue = sb.toString();
                }
            }
        }
        return maskedValue;
    }
}