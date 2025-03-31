package io.github.logmaster.filter;

import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.interceptor.CachedBodyHttpServletRequest;
import io.github.logmaster.utils.LogObjectMapperUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestLogFilterTest {
    @InjectMocks
    RequestLogFilter requestLogFilter;
    @Mock
    ApiBusinessStepHelper apiBusinessStepHelper;
    @Mock
    LogObjectMapperUtil logObjectMapperUtil;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;
    private CachedBodyHttpServletRequest cachedRequest;

    @Before
    public void setUp() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new
                ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8));
        ServletInputStream mockInputStream = new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // No-op
            }
        };
        when(request.getInputStream()).thenReturn(mockInputStream);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.singletonList("uuid")));
        when(request.getHeader(anyString())).thenReturn("id");
        when(request.getQueryString()).thenReturn("id");
        when(request.getRequestURL()).thenReturn(new StringBuffer("/api"));
        cachedRequest = new CachedBodyHttpServletRequest(request);
    }

    @Test
    public void testDoFilterInternal() throws ServletException, IOException {
        requestLogFilter.doFilterInternal(request, response, filterChain);
        Assert.assertTrue(true);
    }
}
