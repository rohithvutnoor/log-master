package io.github.logmaster.convertor;

import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.helpers.TraceIdKeyFinder;
import io.github.logmaster.mask.implementations.DefaultMaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ClientHttpMessageStateConverter<T> implements HttpMessageConverter<T> {
    private final HttpMessageConverter<T> delegate;
    private final DefaultMaskService defaultMaskService;
    private final ApiBusinessStepHelper apiBusinessStepHelper;
    private final TraceIdKeyFinder traceIdKeyFinder;

    @Override
    public boolean canRead(@NonNull Class<?> clazz, MediaType mediaType) {
        return delegate.canRead(clazz, mediaType);
    }

    @Override
    public boolean canWrite(@NonNull Class<?> clazz, MediaType mediaType) {
        return delegate.canWrite(clazz, mediaType);
    }

    @NonNull
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return delegate.getSupportedMediaTypes();
    }

    @NonNull
    public T read(@NonNull Class<? extends T> clazz, @NonNull HttpInputMessage inputMessage) throws IOException {
        T body = delegate.read(clazz, inputMessage);
        log.info(" [RestClient] Response Body :{}", defaultMaskService.mask(body));
        removeMetaDataFromMDC();
        return body;
    }

    @Override
    public void write(@NonNull T t, MediaType contentType, @NonNull HttpOutputMessage outputMessage)
            throws IOException {
        if (outputMessage instanceof HttpRequest request) {
            addMetaDataToMDC(request);
        }
        log.info(" [RestClient] Request Body : {}", defaultMaskService.mask(t));
        delegate.write(t, contentType, outputMessage);
    }

    public void addMetaDataToMDC(HttpRequest request) {
        String subsystemBusinessDescription = apiBusinessStepHelper.
                getSubsystemBusinessDescription(request.getHeaders(), request.getMethod().name(), request.getURI().toString());
        MDC.put("client_api", subsystemBusinessDescription);
        String uuid = traceIdKeyFinder.extractByKeyMatch(request.getHeaders());
        MDC.put("client_ trace_id", uuid);
    }

    private void removeMetaDataFromMDC() {
        MDC.remove("client api");
        MDC.remove("client_trace_id");
    }
}

