package io.github.logmaster.mask.implementations;

import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class EMailMaskService implements LogMask {

    @Value ("${mask.expression.length.email:3}")
    private int length;

    public static final String SINGLE_DOT = "•"; private final LogObjectMapperUtil logObjectMapperUtil;

    @Override
    public String mask (Object message) {
        if (message instanceof String text) {
            return getMaskedEmail(text);
        }
        return logObjectMapperUtil.toStringMasked(message);
    }
    public String getMaskedEmail (String email) {
        if (StringUtils.isBlank(email) || !email.contains("@")) {
            return email;
        }
        StringBuilder formattedEmail = new StringBuilder();
        String baseEmail = email.substring(0, email.indexOf("@"));
        if (baseEmail.length() >= 5) {
            formattedEmail.append(email, 0, 2);
            formattedEmail.append(StringUtils.repeat(SINGLE_DOT, 3));
            if (baseEmail.length() > 5) {
                formattedEmail.append(email, 5, baseEmail.length());
            }
        } else if (baseEmail.length() == 3 || baseEmail.length() == 4) {
            formattedEmail.append(email, 0, 1);
            formattedEmail.append(StringUtils.repeat(SINGLE_DOT, 2));
            if (baseEmail.length() == 4) {
                formattedEmail.append(email, 3, baseEmail.length());
            }
        } else if (baseEmail.length() == 1 || baseEmail.length() == 2) {
            formattedEmail.append(StringUtils.repeat(SINGLE_DOT, 1));
            if (baseEmail.length() == 2) {
                formattedEmail.append(email, 1, 2);
            }
        }
        return formattedEmail.append(email, email.indexOf("@"), email.length()).toString();
    }
}
