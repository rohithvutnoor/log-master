package io.github.logmaster.advice;

import com.jayway.jsonpath.JsonPath;
import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.utils.LogObjectMapperUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.regex.Pattern;

@Slf4j
@ControllerAdvice
@Order
@RequiredArgsConstructor
public class ResponseLogAdvice implements ResponseBodyAdvice<Object> {
    private final LogObjectMapperUtil logObjectMapperUtil;
    private final ApiBusinessStepHelper apiBusinessStepHelper;

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends
            HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * @param body
     * @param returnType
     * @param selectedContentType @param selectedConverterType
     * @param request             @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType
                                          selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse
                                          response) {
        if (request instanceof ServletServerHttpRequest servletRequest && response instanceof ServletServerHttpResponse servletResponse) {
            HttpServletRequest req = servletRequest.getServletRequest();
            HttpServletResponse res = servletResponse.getServletResponse();
            String fullUrl = (String) req.getAttribute("incomingUrl");
            String step = (String) req.getAttribute("step");
            String queryString = req.getQueryString();
            if (queryString != null) {
                fullUrl += "?" + queryString;
            }
            log.info("[Outgoing] Response Url           : {}", fullUrl);
            log.info("[Outgoing] Response Method        : {}", req.getMethod());
            log.info("[Outgoing] Response Headers       : {}", logObjectMapperUtil.toString(response.getHeaders()));
            log.info("[Outgoing] Response Status        : {}", res.getStatus());
            log.info("[Outgoing] Response Body          : {}", body != null ? logObjectMapperUtil.toStringMasked(body) : null);
            String status = getServiceStatusDescription(req, res, body);
            log.info("Journey for {} has been completed {}", step, status);
        }
        return body;
    }


    public String getServiceStatusDescription(HttpServletRequest wrappedRequest, HttpServletResponse wrappedResponse, Object body) {
        boolean serviceStatus = getServiceStatus(wrappedRequest, wrappedResponse, body);
        if (serviceStatus) {
            return "successfully.";
        }
        return "with failure.";
    }

    private boolean getServiceStatus(HttpServletRequest wrappedRequest, HttpServletResponse wrappedResponse, Object body) {

        String key = wrappedRequest.getMethod().toLowerCase() + ".[" + wrappedRequest.getRequestURI() + "].success";
        if (apiBusinessStepHelper.getDetails().containsKey(key)) {
            String rawBody = logObjectMapperUtil.toString(body);
            String expression = apiBusinessStepHelper.getDetails().get(key);

            String[] parts = expression.split(":", 2);
            if (parts.length == 2) {
                String jsonPath = parts[0];
                String regex = parts[1];
                try {

                    Object jsonValue = JsonPath.read(rawBody, "$." + jsonPath);
                    if (jsonValue != null && Pattern.matches(regex, jsonValue.toString())) {
                        return true;
                    }
                } catch (Exception e) {
                    log.warn("Error while evaluating response with JSONPath for expression '{}': {}", key, e.getMessage());
                }
            }
        } else {
            return !isErrorStatus(wrappedResponse.getStatus());
        }
        return false;
    }

    public static boolean isErrorStatus(int statusCode) {
        return statusCode >= 400 && statusCode < 600;
    }
}
