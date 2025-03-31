package io.github.logmaster.mask.implementations;

import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockNoMaskService implements LogMask {

    private final LogObjectMapperUtil logObjectMapperUtil;

    @Override
    public String mask(Object message) {
        if (message instanceof String text) {
            return getMaskedBlock(text);
        }
        return logObjectMapperUtil.toStringMasked(message);
    }

    private String getMaskedBlock(String unMaskBlock) {
        if (StringUtils.isNotBlank(unMaskBlock)) {
            StringBuilder maskBlock = new StringBuilder();
            int i = 0;
            while (i < unMaskBlock.length()) {
                if (unMaskBlock.charAt(i) == '#' || unMaskBlock.charAt(i) == '-' || unMaskBlock.charAt(i) == '/'
                        || unMaskBlock.charAt(i) == ' ') {
                    maskBlock.append(unMaskBlock.charAt(i));
                } else {
                    maskBlock.append('X');
                }
                i++;
            }
            return new String(maskBlock);
        }
        return unMaskBlock;
    }
}