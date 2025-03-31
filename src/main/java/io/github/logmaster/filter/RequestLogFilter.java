package io.github.logmaster.filter;

import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.interceptor.CachedBodyHttpServletRequest;
import io.github.logmaster.utils.LogObjectMapperUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLogFilter extends OncePerRequestFilter {
    @Value("$ {spring.application.name:my-application}")
    private String appName;
    private final ApiBusinessStepHelper apiBusinessStepHelper;
    private final LogObjectMapperUtil logObjectMapperUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
        ContentCachingResponseWrapper wrappedResponse = new
                ContentCachingResponseWrapper(response);
        String step = apiBusinessStepHelper.getBusinessDescription(wrappedRequest.getMethod(),
                wrappedRequest.getRequestURI());
        addMetaDataToAttributes(wrappedRequest, step);
        addMetaDataToMDC(wrappedRequest, step);
        log.info("Journey for {} started", step);
        logRequest(wrappedRequest);
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        wrappedResponse.copyBodyToResponse();
    }

    private void addMetaDataToAttributes(CachedBodyHttpServletRequest request, String step) {
        String requestURL = request.getRequestURL().toString();
        request.setAttribute("incomingUrl", requestURL);
        request.setAttribute("step", step);
    }

    private void addMetaDataToMDC(CachedBodyHttpServletRequest wrappedRequest, String step) {
        String uuid = wrappedRequest.getHeader("x-uuid");
        String journeyTrackId = wrappedRequest.getHeader("x-journey_track_id");
        MDC.put("app_name", appName);
        MDC.put("journey_track_id", journeyTrackId);
        MDC.put("trace_id", uuid);
        MDC.put("step_desc", step);
    }

    private void logRequest(CachedBodyHttpServletRequest request) throws IOException {
        log.info("[Incoming] Request Url           : {}", getCompletePath(request));
        log.info("[Incoming] Request Method        : {}", request.getMethod());
        Map<String, String> headers = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(name ->
                        name, request::getHeader));
        log.info("[Incoming] Request Headers       : {}", logObjectMapperUtil.toString(headers));
        request.getInputStream().readAllBytes();
    }

    private static String getCompletePath(CachedBodyHttpServletRequest request) {
        String fullUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (queryString != null) {
            fullUrl += "?" + queryString;
        }
        return fullUrl;
    }
}

