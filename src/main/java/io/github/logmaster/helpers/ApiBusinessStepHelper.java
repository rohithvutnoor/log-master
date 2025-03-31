package io.github.logmaster.helpers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ConfigurationProperties(prefix = "api")
public class ApiBusinessStepHelper {
    private Map<String, String> details = new HashMap<>();

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public String getBusinessDescription(String method, String requestPath) {

        String lookupKey = method.toLowerCase() + "." + requestPath;
        List<Map.Entry<String, String>> sortedEntries = details.entrySet().stream().

                filter(e -> e.getKey() != null && e.getKey().
                        contains("/") && e.getKey().endsWith(".desc"))
                        .
                sorted(Comparator.comparingInt((Map.Entry<String, String> e) -> {
                    String key = e.getKey();
                    int wildcardCount = (int) key.chars().filter(ch -> ch == '*').count();
                    return -key.length() + wildcardCount * 10;
                })).toList();
        for (Map.Entry<String, String> entry : sortedEntries) {
            String descKey = entry.getKey();

            String patternKey = descKey.substring(0, descKey.length() - 5).toLowerCase();

            String regex = "^" + patternKey.replace("/", "\\/")
                    .replace("**", ".*")
                    .replace("*", "[^/]+") + "$";
            if (lookupKey.matches(regex) && (StringUtils.isNotBlank(entry.getValue()))) {
                return entry.getValue();
            }
        }
        return getDefaultBusinessDescription(method, requestPath);
    }

    public String getSubsystemBusinessDescription(HttpHeaders headers, String method, String requestPath) {

        String serviceDesc = headers.getFirst("serviceDescription");
        if (StringUtils.isNotBlank(serviceDesc)) {
            return serviceDesc;
        }
        return getDefaultBusinessDescription(method, requestPath);
    }

    private String getDefaultBusinessDescription(String method, String path) {

        String cleanedPath = path.split("\\?")[0];

        String[] segments = cleanedPath.split("/");

        List<String> words = new ArrayList<>();
        for (int i = segments.length - 1; i >= 0 && words.size() < 2; i--) {
            String segment = segments[i];
            if (segment != null && !segment.isBlank() && segment.matches("^[a-zA-Z]+$")) {
                words.add(0, capitalize(segment));
            }
        }


        if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase(" PATCH")) {
            Collections.reverse(words);
        } else {
            words.add(0, capitalize(method));
        }
        return String.join(" ", words);
    }

    private String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
