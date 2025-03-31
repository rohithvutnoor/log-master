package io.github.logmaster.helpers;

import io.github.logmaster.annotations.LogThis;
import io.github.logmaster.beans.LogDetails;
import io.github.logmaster.mask.implementations.DefaultMaskService;
import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LogHelper {
    public static final Logger logger = LoggerFactory.getLogger(LogHelper.class);
    private final ApplicationContext applicationContext;
    private final LogObjectMapperUtil logObjectMapperUtil;

    public void log(final int level, final Logger logger, final LogDetails log) {
        String message = log.getMessage();
        Object[] parameters = log.getParameters();

        if (level == LogThis.ERROR) {
            logger.error(message, parameters);
        } else if (level == LogThis.WARN) {
            logger.warn(message, parameters);
        } else if (level == LogThis.INFO) {
            logger.info(message, parameters);
        } else if (level == LogThis.DEBUG) {
            logger.debug(message, parameters);
        } else if (level == LogThis.TRACE) {
            logger.trace(message, parameters);
        }
    }

    public boolean over(final LogThis annotation, final long nano) {
        return nano > annotation.unit().toNanos(annotation.limit());
    }

    public Logger getLogger(final MethodSignature method, final CharSequence name) {
        final Object source;
        if (name.length() == 0) {
            source = method.getMethod().getDeclaringClass();
        } else {
            source = name;
        }
        return getLogger(source);
    }

    private static Logger getLogger(Object source) {
        Logger logger;
        if (source instanceof Class) {
            logger = LoggerFactory.getLogger((Class<?>) source);
        } else if (source instanceof String stringSource) {
            logger = LoggerFactory.getLogger(stringSource);
        } else {
            logger = LoggerFactory.getLogger(source.getClass());
        }
        return logger;
    }

    public LogDetails getLogDetail(LogThis annotation, ProceedingJoinPoint joinPoint,
                                   MethodSignature method) {
        StringBuilder message = new StringBuilder();
        List<Object> parameters = new ArrayList<>();
        message.append(" [start] #{}() invoked with parameters:");
        parameters.add(method.getName());
        if (annotation.skipArgs()) {
            message.append(" skipped arguments!");
        } else {
            logData(annotation, joinPoint.getArgs(), getArgumentTypes(method.getMethod()), true, message, parameters);

        }
        return LogDetails.builder().message(message.toString()).parameters(parameters.toArray()).build();
    }

    private List<String> getArgumentTypes(Method method) {
        return Arrays.stream(method.getParameterTypes()).map(Class::getTypeName).toList();
    }

    public LogDetails getLogDetail(LogThis annotation, MethodSignature method, Object result,
                                   long executionTime) {
        StringBuilder message = new StringBuilder();
        List<Object> parameters = new ArrayList<>();
        message.append(" [end] #{} () executed with return value:");
        parameters.add(method.getName());
        if (annotation.skipResult()) {
            message.append(" skipped result!");
        } else {
            logData(annotation, new Object[]{result}, getReturnType(method.getMethod()), false, message, parameters);
        }
        message.append(" in {} ms");
        parameters.add(TimeUnit.NANOSECONDS.toMillis(executionTime));
        if (over(annotation, executionTime)) {
            message.append(" too slow!!");
        }
        return LogDetails.builder().message(message.toString()).parameters(parameters.toArray()).build();
    }

    public LogDetails getLogDetail(MethodSignature method, Exception exception) {
        List<Object> parameters = new ArrayList<>();
        String message = "[end] #{}() thrown {} exception at {} #{}() line no {} with message: {}";
        parameters.add(method.getName());
        if (null != exception) {
            String absoluteName = getAbsoluteName(exception.getClass().getName());
            parameters.add(absoluteName);
            final StackTraceElement[] traces = exception.getStackTrace();
            if (traces.length > 0) {
                final StackTraceElement trace = traces[0];
                parameters.add(trace.getClassName());
                parameters.add(trace.getMethodName());
                parameters.add(trace.getLineNumber());
            }
            parameters.add(exception.getMessage());
        }

        return LogDetails.builder().message(message).parameters(parameters.toArray()).build();
    }

    private List<String> getReturnType(Method method) {
        return List.of(method.getReturnType().getTypeName());
    }

    private String getAbsoluteName(String value) {
        return value.substring(value.lastIndexOf('.') + 1);
    }

    private void logData(LogThis annotation, Object[] values, List<String> valueTypes,
                         boolean isBefore, StringBuilder message,
                         List<Object> parameters) {
        if (values != null && values.length > 0) {
            for (int index = 0; index < values.length; index++) {
                mapLogData(annotation, values, valueTypes, isBefore, message, parameters, index);
            }
        } else {
            message.append(" [Empty]");
        }
    }

    public void mapLogData(LogThis annotation, Object[] values, List<String> valueTypes,
                           boolean isBefore, StringBuilder message,
                           List<Object> parameters, int index) {
        Object value = values[index];
        if (value == null) {
            value = "null";
        } else {
            if (isBefore) {
                if (modifyFor((index), annotation.maskFor())) {
                    value = parameterMasking(annotation, index, value);
                }
            } else {
                value = maskResultOnVerify(annotation, value);
            }
        }
        if (!(value instanceof String)) {
            value = logObjectMapperUtil.toString(value);
        }
        message.append(index == 0 ? " [{}: {}" : ", {}: {}");
        if (index == values.length - 1) {
            message.append("]");
        }
        parameters.add(valueTypes.get(index).replaceAll("^.*\\.", ""));
        parameters.add(value);
    }

    private Object parameterMasking(LogThis annotation, int index, Object value) {
        if (value instanceof String data) {
            value = stringMasking(annotation, index, value, data);
        } else {
            value = objectMasking(annotation, index, value);
        }
        return value;
    }

    private Object stringMasking(LogThis annotation, int index, Object value, String data) {
        LogMask logMask = applicationContext.getBean(DefaultMaskService.class);
        if (anyCustomServiceIndexForThisParameter(index, annotation.customMaskForIndex())) {
            int customIndex = getCustomIndex(index, annotation.customMaskForIndex());
            if (customIndex != -1) {
                logMask = (LogMask) applicationContext.getBean(annotation.customMaskForService()[customIndex]);
                value = logMask.mask(data);
            }
        } else {
            value = logMask.mask(data);
        }
        return value;
    }

    private Object maskResultOnVerify(LogThis annotation, Object value) {
        if (annotation.maskResult()) {
            if (value instanceof String data) {
                LogMask logMask = (LogMask) applicationContext.getBean(annotation.resultMaskService());
                value = logMask.mask(data);
            } else {
                LogMask logMask = (LogMask) applicationContext.getBean(annotation.resultMaskService());
                value = logMask.mask(value);
            }
        }
        return value;
    }

    private Object objectMasking(LogThis annotation, int index, Object value) {
        if (anyCustomServiceIndexForThisParameter(index, annotation.customMaskForIndex())) {
            int customIndex = getCustomIndex(index, annotation.customMaskForIndex());
            if (customIndex != -1) {
                LogMask logMask = (LogMask) applicationContext.getBean(annotation.customMaskForService()[customIndex]);
                value = logMask.mask(value);
            }
        } else {
            value = logObjectMapperUtil.toStringMasked(value);
        }
        return value;
    }

    private boolean anyCustomServiceIndexForThisParameter(int index, int[] input) {
        if (null == input || input.length == 0) return false;
        return Arrays.stream(input).boxed().toList().contains(index);
    }

    private int getCustomIndex(int index, int[] input) {
        for (int i = 0; i < input.length; i++) {
            if (index == i) {
                return i;
            }
        }
        return -1;
    }

    private boolean modifyFor(int ind, int[] list) {
        if (null == list || list.length == 0) return false;
        return Arrays.stream(list).boxed().toList().contains(ind);
    }

    public boolean isLoggingEnabled(final int level, final Logger logger) {
        switch (level) {
            case LogThis.ERROR -> {
                return logger.isErrorEnabled();
            }
            case LogThis.WARN -> {
                return logger.isWarnEnabled();
            }
            case LogThis.INFO -> {
                return logger.isInfoEnabled();
            }
            case LogThis.DEBUG -> {
                return logger.isDebugEnabled();
            }
            case LogThis.TRACE -> {
                return logger.isTraceEnabled();
            }
            default -> {
                return false;
            }
        }
    }
}

