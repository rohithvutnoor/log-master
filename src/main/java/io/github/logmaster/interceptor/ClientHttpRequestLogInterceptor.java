package io.github.logmaster.interceptor;

import io.github.logmaster.beans.RestClientResponse;
import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.helpers.RestClientErrorHelper;
import io.github.logmaster.helpers.TraceIdKeyFinder;
import io.github.logmaster.utils.LogObjectMapperUtil;
import io.github.logmaster.utils.SanitizerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientHttpRequestLogInterceptor implements ClientHttpRequestInterceptor {
    private final LogObjectMapperUtil logObjectMapperUtil;
    private final ApiBusinessStepHelper apiBusinessStepHelper;
    private final TraceIdKeyFinder traceIdKeyFinder;

    @NonNull
    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body,
                                        @NonNull ClientHttpRequestExecution execution) throws IOException {
        addMetaDataToMDC(request);
        log.info("[RestClient] Request URL              : {}", SanitizerUtil.sanitizeURL(request.getURI().toString()));
        log.info("[RestClient] Request Method          : {}", request.getMethod());
        log.info("[RestClient] Request Headers         : {}", logObjectMapperUtil.toString(request.getHeaders()));
        ClientHttpResponse response = null;
        try {
            response = execution.execute(request, body);
        } catch (Exception e) {
            addMetaDataToMDC(request);
            log.info(" [RestClient] Response URL        : {}", request.getURI());
            log.info(" [RestClient] Response Method     : {})", request.getMethod());

            Throwable rootCause = e.getCause() != null ? e.getCause() : e;
            RestClientResponse handle = RestClientErrorHelper.handle(rootCause);

            log.info(" [RestClient] Response Headers    : {}", logObjectMapperUtil.toString(handle.getResponseHeaders()));
            log.info(" (RestClient] Response Status     : {}", handle.getStatusCode());
            log.info(" [RestClient] Response Body       : {})", logObjectMapperUtil.toStringMasked(handle.getResponseBody()));
            removeMetaDataFromMDC();
            throw e;
        }

        log.info(" [RestClient] Response URL            : {}", request.getURI());
        log.info(" [RestClient] Response Method         : {}", request.getMethod());
        log.info(" [RestClient] Response Headers        : {}", logObjectMapperUtil.toString(response.getHeaders()));
        log.info(" [RestClient] Response Status         : {}", response.getStatusCode());
        return response;
    }

    private void addMetaDataToMDC(HttpRequest request) {
        String subsystemBusinessDescription = apiBusinessStepHelper.getSubsystemBusinessDescription(request.getHeaders(), request.getMethod().name(), request.getURI().toString());
        MDC.put("client_api", subsystemBusinessDescription);
        String uuid = traceIdKeyFinder.extractByKeyMatch(request.getHeaders());
        MDC.put("client_trace_id", uuid);
    }

    private void removeMetaDataFromMDC() {
        MDC.remove("client_api");
        MDC.remove("client_trace_id");
    }
}
