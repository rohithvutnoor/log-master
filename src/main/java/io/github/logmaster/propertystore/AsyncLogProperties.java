package io.github.logmaster.propertystore;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:log-master.properties")
@ConfigurationProperties(prefix = "log.async")
public class AsyncLogProperties {
    private boolean enabled = true;
    private int queueSize = 1024;
    private boolean neverBlock = true;
    private int discardingThreshold = 0;
}
