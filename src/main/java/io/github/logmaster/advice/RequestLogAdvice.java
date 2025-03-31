package io.github.logmaster.advice;

import io.github.logmaster.utils.LogObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class RequestLogAdvice extends RequestBodyAdviceAdapter {
    private final LogObjectMapperUtil logObjectMapperUtil;

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter,
                            @NonNull Type targetType,
                            @NonNull Class<?
                                    extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @NonNull
    @Override
    public Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage,
                                @NonNull MethodParameter parameter,
                                @NonNull Type targetType, @NonNull Class<? extends
                    HttpMessageConverter<?>>
                                        converterType) {
        log.info("[Incoming] Request Body           : {}", logObjectMapperUtil.toStringMasked(body));
        return body;
    }
}

