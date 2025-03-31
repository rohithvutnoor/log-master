package io.github.logmaster.configuration.rest;


import io.github.logmaster.convertor.ClientHttpMessageStateConverter;
import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.helpers.TraceIdKeyFinder;
import io.github.logmaster.interceptor.ClientHttpRequestLogInterceptor;
import io.github.logmaster.mask.implementations.DefaultMaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestTemplateConfig {
    @Bean("loggableRestTemplate")
    public RestTemplate restTemplate(DefaultMaskService defaultMaskService, ClientHttpRequestLogInterceptor interceptor, ApiBusinessStepHelper apiBusinessStepHelper,
                                     TraceIdKeyFinder traceIdKeyFinder) {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            converters.add(new ClientHttpMessageStateConverter<>(converter, defaultMaskService, apiBusinessStepHelper, traceIdKeyFinder));
        }
        restTemplate.getInterceptors().add(interceptor);
        restTemplate.setMessageConverters(converters);
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory()));
        return restTemplate;
    }
}



