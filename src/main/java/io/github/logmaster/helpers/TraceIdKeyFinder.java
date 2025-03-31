package io.github.logmaster.helpers;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "uuid.matcher")
@PropertySource("classpath:log-master.properties")
public class TraceIdKeyFinder {
    private List<String> defaultKeys;
    private List<String> keys;

    public String extractByKeyMatch(Map<String, String> headers) {
        String value = findMatch(headers, keys);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return findMatch(headers, defaultKeys);
    }

    public String extractByKeyMatch(HttpHeaders headers) {
        if (headers != null) {
            String result = extractByKeyMatch(headers.toSingleValueMap());
            if (StringUtils.isNotBlank(result)) {
                return result;
            }
        }
        return "";
    }

    private String findMatch(Map<String, String> headers, List<String> patterns) {
        if (patterns == null) return null;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (patterns.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return null;
    }
}


