package io.github.logmaster.utils;

import io.github.logmaster.annotations.LogThis;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

public class AnnotationUtil {
    private AnnotationUtil() {
        // Default Constructor
    }

    public static LogThis getDefaultAnnotation() {
        return new LogThis() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return LogThis.class;
            }

            @Override
            public int value() {
                return LogThis.INFO;
            }

            @Override
            public int limit() {
                return 1;
            }

            @Override
            public TimeUnit unit() {
                return TimeUnit.MINUTES;
            }

            @Override
            public boolean trim() {
                return false;
            }

            @Override
            public boolean before() {
                return true;
            }

            @Override
            public Class<? extends Throwable>[] ignore() {
                return new Class[0];
            }

            @Override
            public boolean skipResult() {
                return false;
            }

            @Override
            public boolean skipArgs() {
                return false;
            }


            @Override
            public boolean logString() {
                return false;
            }

            @Override
            public int precision() {
                return 0;
            }

            @Override
            public String name() {
                return "";
            }

            @Override
            public boolean maskResult() {
                return false;
            }

            @Override
            public int[] maskFor() {
                return new int[0];
            }


            @Override
            public Class<?>[] customMaskForService() {
                return new Class[0];
            }

            @Override
            public int[] customMaskForIndex() {
                return new int[0];
            }

            public Class<?> resultMaskService() {
                return null;
            }

            @Override
            public int[] encryptFor() {
                return new int[0];
            }

            @Override
            public boolean encryptResult() {
                return false;
            }
        };
    }
}

