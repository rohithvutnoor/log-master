package io.github.logmaster.configuration.log;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import io.github.logmaster.enums.LogMarker;
import io.github.logmaster.mask.implementations.DefaultMaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogTurboFilter extends TurboFilter {

    private final DefaultMaskService defaultMaskService;

    public FilterReply decide(Marker marker, Logger logger, Level level, String data, Object[] params, Throwable t) {
        try {
            if (marker != null && LogMarker.SENSITIVE.getName().equals(marker.getName()) && data != null) {
                if (params != null && params.length > 0) {
                    String[] updatedParams = Arrays.stream(params)
                            .map(defaultMaskService::mask).toArray(String[]::new);
                    data = MessageFormatter.arrayFormat(data, updatedParams).getMessage();
                }
                String masked = defaultMaskService.mask(data);
                LoggingEvent maskedEvent = new LoggingEvent(Logger.FQCN, logger, level, masked, t, params);
                maskedEvent.setTimeStamp(System.currentTimeMillis());
                maskedEvent.setThreadName(Thread.currentThread().getName());
                if (t != null) {
                    maskedEvent.setThrowableProxy(new ThrowableProxy(t));
                }
                logger.callAppenders(maskedEvent);
                return FilterReply.DENY;
            }
        } catch (Exception e) {
            log.warn("Error while turbo logging sensitive masked data");
            return FilterReply.NEUTRAL;
        }
        return FilterReply.NEUTRAL;
    }
}
