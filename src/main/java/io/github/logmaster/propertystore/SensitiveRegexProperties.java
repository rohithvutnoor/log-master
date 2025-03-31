package io.github.logmaster.propertystore;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Data
@Configuration
@PropertySource("classpath:log-master.properties")
@ConfigurationProperties(prefix = "sensitive.regex")
public class SensitiveRegexProperties {
    private Map<String, String> pattern;
}