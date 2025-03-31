package io.github.logmaster.aspect;

import io.github.logmaster.annotations.LogThis;
import io.github.logmaster.helpers.LogHelper;
import io.github.logmaster.utils.AnnotationUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.JDBCException;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final LogHelper logHelper;

    @Pointcut("execution(* *(..)) ")
    public void all() {
    }

    @Pointcut("@annotation(io.github.logmaster.annotations.LogThis)")

    public void logThisOnMethod() {
    }

    @Pointcut("@within(io.github.logmaster.annotations.LogThis)")
    public void logThisOnClass() {
    }

    @Around("all() && logThisOnMethod()")
    public Object aspectAroundMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature method = (MethodSignature) proceedingJoinPoint.getSignature();
        LogThis annotation = method.getMethod().getAnnotation(LogThis.class);
        final long start = System.nanoTime();
        int level = annotation.value();
        Logger logger = validateLogger(proceedingJoinPoint, annotation, level, method);
        try {
            final Object methodResult = proceedingJoinPoint.proceed();
            final long executionTime = System.nanoTime() - start;
            if (logHelper.over(annotation, executionTime)) {
                level = LogThis.WARN;
            }
            if (logHelper.isLoggingEnabled(level, logger)) {
                logHelper.log(level,
                        logger,
                        logHelper.getLogDetail(annotation, method,
                                methodResult, executionTime));
            }
            return methodResult;
        } catch (Exception exception) {
            level = LogThis.ERROR;
            if (logHelper.isLoggingEnabled(level, logger)) {
                Exception actualException;
                if (exception.getCause() instanceof JDBCException jdbcException) {
                    actualException = jdbcException.getSQLException();
                } else {
                    actualException = exception;
                }
                logHelper.log(level, logger, logHelper.getLogDetail(method, actualException));
            }
            throw exception;
        }
    }

    @Around("all() && logThisOnClass()")
    public Object aspectAroundClass(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        LogThis annotation = ((Class<?>) proceedingJoinPoint.getSignature().getDeclaringType()).
                getAnnotation(LogThis.class);
        final long start = System.nanoTime();
        int level = annotation.value();
        MethodSignature method = (MethodSignature) proceedingJoinPoint.getSignature();
        Logger logger = validateLogger(proceedingJoinPoint, annotation, level, method);
        try {
            final Object classResult = proceedingJoinPoint.proceed();
            final long executionTime = System.nanoTime() - start;
            if (logHelper.over(annotation, executionTime)) {
                level = LogThis.WARN;
            }
            if (logHelper.isLoggingEnabled(level, logger)) {
                logHelper.log(level, logger,
                        logHelper.getLogDetail(annotation, method,
                                classResult, executionTime));
            }
            return classResult;
        } catch (Exception exception) {
            level = LogThis.ERROR;
            if (logHelper.isLoggingEnabled(level, logger)) {
                logHelper.log(level, logger, logHelper.getLogDetail(annotation, proceedingJoinPoint, method));
            }
            throw exception;
        }
    }

    @Around("this(org.springframework.data.repository.Repository)")
    public Object aspectAroundRepository(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        LogThis annotation = AnnotationUtil.getDefaultAnnotation();
        final long start = System.nanoTime();
        int level = annotation.value();
        MethodSignature method = (MethodSignature) proceedingJoinPoint.getSignature();
        Logger logger = validateLogger(proceedingJoinPoint, annotation, level, method);
        try {
            final Object repoResult = proceedingJoinPoint.proceed();
            final long executionTime = System.nanoTime() - start;
            if (logHelper.over(annotation, executionTime)) {
                level = LogThis.WARN;
            }
            if (logHelper.isLoggingEnabled(level, logger)) {
                logHelper.log(level, logger,
                        logHelper.getLogDetail(annotation, method, repoResult, executionTime));
            }
            return repoResult;
        } catch (Exception exception) {
            level = LogThis.ERROR;
            if (logHelper.isLoggingEnabled(level, logger)) {
                Exception actualException = null;
                if (exception.getCause() instanceof JDBCException jdbcException) {
                    actualException = jdbcException.getSQLException();
                }
                logHelper.log(level, logger, logHelper.getLogDetail(method, actualException));
            }
            throw exception;
        }
    }

    private Logger validateLogger(ProceedingJoinPoint proceedingJoinPoint, LogThis annotation, int level,
                                  MethodSignature method) {
        Logger logger = logHelper.getLogger(method, annotation.name());
        if (annotation.before() && logHelper.isLoggingEnabled(level, logger)) {
            logHelper.log(level, logger, logHelper.getLogDetail(annotation, proceedingJoinPoint, method));
        }
        return logger;
    }
}
