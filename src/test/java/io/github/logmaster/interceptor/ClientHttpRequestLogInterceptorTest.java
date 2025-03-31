package io.github.logmaster.interceptor;

import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.helpers.TraceIdKeyFinder;
import io.github.logmaster.utils.LogObjectMapperUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientHttpRequestLogInterceptorTest {
    @InjectMocks
    ClientHttpRequestLogInterceptor clientHttpRequestLogInterceptor;
    @Mock
    ClientHttpRequestExecution clientHttpRequestExecution;
    @Mock
    ApiBusinessStepHelper apiBusinessStepHelper;
    @Mock
    TraceIdKeyFinder traceIdKeyFinder;
    @Mock
    LogObjectMapperUtil logObjectMapperUtil;

    @Test
    public void testIntercept() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getURI()).thenReturn(URI.create("/test"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(apiBusinessStepHelper.getSubsystemBusinessDescription(any(), anyString(), anyString())).thenReturn("desc");
        when(logObjectMapperUtil.toString(any())).thenReturn("data");
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(clientHttpRequestExecution.execute(any(), any())).thenReturn(response);
        ClientHttpResponse intercept = clientHttpRequestLogInterceptor.intercept(request,
                "".getBytes(), clientHttpRequestExecution);
        Assert.assertNotNull(intercept);
    }

    @Test
    public void testInterceptException() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getURI()).thenReturn(URI.create("/test"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(apiBusinessStepHelper.getSubsystemBusinessDescription(any(), anyString(), anyString())).thenReturn("desc");
        when(logObjectMapperUtil.toString(any())).thenReturn("data");
        ConnectException connectException = mock(ConnectException.class);
        when(clientHttpRequestExecution.execute(any(), any())).thenThrow(connectException);
        Assert.assertThrows(ConnectException.class, () -> clientHttpRequestLogInterceptor.intercept(request, "".getBytes(), clientHttpRequestExecution));
    }
}

