package io.github.logmaster.configuration.general;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

import java.util.Map;

public class MDCDecorator implements TaskDecorator {
    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                contextMap.forEach(MDC::put);
                runnable.run();
            } finally {
                String appName = MDC.get("app_name");
                MDC.clear();
                MDC.put("appName", appName);
            }
        };
    }
}