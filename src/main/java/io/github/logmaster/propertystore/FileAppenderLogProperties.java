package io.github.logmaster.propertystore;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:log-master.properties")
@ConfigurationProperties(prefix = "log.appender.file")
public class FileAppenderLogProperties {
    private boolean enabled = false;
    private String path = "";
    private String name = "";
}

