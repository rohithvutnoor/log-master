package io.github.logmaster.configuration.log;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import io.github.logmaster.propertystore.AsyncLogProperties;
import io.github.logmaster.propertystore.FileAppenderLogProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogConfiguration {
    @Value("${spring.application.name:appname}")
    private String appName;
    private final AsyncLogProperties asyncLogProperties;
    private final FileAppenderLogProperties fileAppenderLogProperties;
    private final Environment environment;

    @PostConstruct
    public void configureAsyncLogging() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(getConsolePattern());
        encoder.start();
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setName("console_appender");
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        ch.qos.logback.classic.Logger rootLogger = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAndStopAllAppenders();
        RollingFileAppender<ILoggingEvent> fileAppender = null;
        if (fileAppenderLogProperties.isEnabled()) {
            TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
            rollingPolicy.setContext(context);
            rollingPolicy.setParent(new RollingFileAppender<>());
            String name = fileAppenderLogProperties.getName();
            if (StringUtils.isBlank(name)) {
                name = appName;
            }
            rollingPolicy.setFileNamePattern(fileAppenderLogProperties.getPath() + "/" + name + "-%d{yyyy-MM-dd}.log");
            rollingPolicy.start();
            fileAppender = new RollingFileAppender<>();
            fileAppender.setName("file_appender");
            fileAppender.setContext(context);
            fileAppender.setFile(fileAppenderLogProperties.getPath() + "/" + name + ".log");
            fileAppender.setEncoder(encoder);
            fileAppender.setRollingPolicy(rollingPolicy);
            rollingPolicy.setParent(fileAppender);
            fileAppender.start();
        }
        if (asyncLogProperties.isEnabled()) {
            AsyncAppender asyncAppender = new AsyncAppender();
            asyncAppender.setContext(context);
            asyncAppender.setName("async_console_appender");
            asyncAppender.setQueueSize(asyncLogProperties.getQueueSize());
            asyncAppender.setNeverBlock(asyncLogProperties.isNeverBlock());
            asyncAppender.setDiscardingThreshold(asyncLogProperties.getDiscardingThreshold());
            asyncAppender.addAppender(consoleAppender);
            asyncAppender.start();
            rootLogger.addAppender(asyncAppender);
        } else {
            rootLogger.addAppender(consoleAppender);
            if (asyncLogProperties.isEnabled() && fileAppender != null) {
                AsyncAppender asyncFileAppender = new AsyncAppender();
                asyncFileAppender.setContext(context);
                asyncFileAppender.setName("async_file_appender");
                asyncFileAppender.setQueueSize(asyncLogProperties.getQueueSize());
                asyncFileAppender.setNeverBlock(asyncLogProperties.isNeverBlock());
                asyncFileAppender.setDiscardingThreshold(asyncLogProperties.getDiscardingThreshold());
                asyncFileAppender.addAppender(fileAppender);
                asyncFileAppender.start();
                rootLogger.addAppender(asyncFileAppender);
            } else if (fileAppender != null) {
                rootLogger.addAppender(fileAppender);
            }
        }
    }

    private String getConsolePattern() {
        String pattern = environment.getProperty("logging.pattern.console");
        return pattern != null ? pattern : "[%d(yyyy-MM-dd HH:mn:ss.SSS)] [%thread] (%-5level] [%X{app_name)] [%X(journey_track_id}] [%X{step_desc}] [%X{trace_id}] [%X{client api}] [%X{client_trace_id}] %logger{36} - %msg%ex%n";
    }
}