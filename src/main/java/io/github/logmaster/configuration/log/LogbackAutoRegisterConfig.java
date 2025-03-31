package io.github.logmaster.configuration.log;


import ch.qos.logback.classic.LoggerContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogbackAutoRegisterConfig implements ApplicationContextAware {
    private final LogTurboFilter logTurboFilter;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext ctx) throws BeansException {
        LoggerContext context = (LoggerContext) LoggerFactory
                .getILoggerFactory();
        logTurboFilter.setContext(context);
        logTurboFilter.start();
        context.addTurboFilter(logTurboFilter);
    }
}