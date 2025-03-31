package io.github.logmaster.utils;

import org.apache.commons.text.StringEscapeUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SanitizerUtil {
    private SanitizerUtil() {
        //default constructor
    }

    public static String sanitize(String input) {
        return sanitizeSafeString(input);
    }

    public static String sanitizeHTML(String input) {
        return input == null ? null : StringEscapeUtils.escapeHtml4(input);
    }

    public static String sanitizeXML(String input) {
        return input == null ? null : StringEscapeUtils.escapeXml10(input);
    }

    public static String sanitizeJSON(String input) {
        return input == null ? null : StringEscapeUtils.escapeJson(input);
    }

    public static String sanitizeJS(String input) {
        return input == null ? null : StringEscapeUtils.escapeEcmaScript(input);
    }

    public static String sanitizeURL(String input) {
        return input == null ? null : URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    public static Map<String, String> sanitizeHeaders(Map<String, String> headers) {
        Map<String, String> sanitized = new HashMap<>();
        if (headers == null) return sanitized;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = sanitizeSafeString(entry.getKey());
            String value = sanitizeSafeString(entry.getValue());
            sanitized.put(key, value);
        }
        return sanitized;

    }

    public static String sanitizeBody(String body) {
        return sanitizeJSON(body);
    }

    private static String sanitizeSafeString(String input) {
        return input == null ? null : StringEscapeUtils.escapeHtml4(input);
    }
}
