package io.github.logmaster.annotations;

import io.github.logmaster.mask.implementations.DefaultMaskService;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LogThis {
    int ERROR = 0;
    int WARN = 1;
    int INFO = 2;
    int DEBUG = 3;
    int TRACE = 4;

    int value() default 2;

    int limit() default 1;

    TimeUnit unit() default TimeUnit.MINUTES;

    boolean trim() default false;

    boolean before() default true;

    Class<? extends Throwable>[] ignore() default {};

    boolean skipResult() default false;

    boolean skipArgs() default false;

    boolean logString() default false;

    int precision() default 2;

    String name() default "";

    boolean maskResult() default true;

    int[] maskFor() default {};

    Class<?>[] customMaskForService() default {};

    int[] customMaskForIndex() default {};

    Class<?> resultMaskService() default DefaultMaskService.class;

    int[] encryptFor() default {};

    boolean encryptResult() default false;
}
