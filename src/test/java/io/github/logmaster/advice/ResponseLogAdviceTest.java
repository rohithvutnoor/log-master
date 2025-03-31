package io.github.logmaster.advice;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.utils.LogObjectMapperUtil;
import io.github.logmaster.utils.MemoryAppender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResponseLogAdviceTest {

    @InjectMocks
    private ResponseLogAdvice responseLogAdvice;
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    @Mock
    private ApiBusinessStepHelper apiBusinessStepHelper;
    @Mock
    ServletServerHttpRequest servletServerHttpRequest;
    @Mock
    ServletServerHttpResponse servletServerHttpResponse;
    private MemoryAppender memoryAppender;

    @Before
    public void beforeEach() {
        Logger logger = (Logger)
                LoggerFactory.getLogger("io.github.logmaster.advice.ResponseLogAdvice");
        logger.setLevel(Level.INFO);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @After
    public void tearDown() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
    }

    @Test
    public void testLogRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        request.setAttribute("incomingUrl", "/api/test");
        request.setAttribute("step", "API Test");
        HttpServletResponse response = mock(HttpServletResponse.class);
        response.setStatus(200);
        when(servletServerHttpRequest.getServletRequest()).thenReturn(request);
        when(servletServerHttpResponse.getServletResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn("id=1");
        Map<String, String> details = new HashMap<>();
        details.put("post.[/api/test].success", "code:2000");
        when(apiBusinessStepHelper.getDetails()).thenReturn(details);
        when(logObjectMapperUtil.toString(any())).thenReturn("{\"code\":\"2000\"}");
        Object actual = responseLogAdvice.beforeBodyWrite("{\"code\":\"2000\"}", null, null,
                null, servletServerHttpRequest, servletServerHttpResponse);
        Assert.assertEquals("{\"code\":\"2000\"}", actual);
    }

    @Test
    public void testLogRequestInvalidBody() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        request.setAttribute("incomingUrl", "/api/test");
        request.setAttribute("step", "API Test");
        HttpServletResponse response = mock(HttpServletResponse.class);
        response.setStatus(200);
        when(servletServerHttpRequest.getServletRequest()).thenReturn(request);
        when(servletServerHttpResponse.getServletResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn("id=1");
        Map<String, String> details = new HashMap<>();
        details.put("post.[/api/test].success", "code:2000");
        when(apiBusinessStepHelper.getDetails()).thenReturn(details);
        when(logObjectMapperUtil.toString(any())).thenReturn("{\"code\"}");
        Object actual = responseLogAdvice.beforeBodyWrite("{\"code\":\"2000\"}", null, null,
                null, servletServerHttpRequest, servletServerHttpResponse);
        Assert.assertEquals(7, memoryAppender.getLoggedEvents().size());
        Assert.assertEquals("{\"code\":\"2000\"}", actual);
    }

    @Test
    public void testLogRequestInvalidRegex() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        request.setAttribute("incomingUrl", "/api/test");
        request.setAttribute("step", "API Test");
        HttpServletResponse response = mock(HttpServletResponse.class);
        response.setStatus(200);
        when(servletServerHttpRequest.getServletRequest()).thenReturn(request);
        when(servletServerHttpResponse.getServletResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn("id=1");
        Map<String,
                String> details = new HashMap<>();
        details.put("post.[/api/test].success",
                "code: 2000:1:1");
        when(apiBusinessStepHelper.getDetails()).thenReturn(details);
        when(logObjectMapperUtil.toString(any())).thenReturn("{\"code\"}");
        Object actual = responseLogAdvice.beforeBodyWrite("{\"code\":\"2000\"}", null, null,
                null, servletServerHttpRequest, servletServerHttpResponse);
        Assert.assertEquals(7, memoryAppender.getLoggedEvents().size());
        Assert.assertEquals("{\"code\":\"2000\"}", actual);
    }

    @Test
    public void testLogRequestFailure() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(servletServerHttpRequest.getServletRequest()).thenReturn(request);
        when(servletServerHttpResponse.getServletResponse()).thenReturn(response);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getStatus()).thenReturn(500);
        when(apiBusinessStepHelper.getDetails()).thenReturn(new HashMap<>());
        when(logObjectMapperUtil.toString(any())).thenReturn("{\"code\":\"2000\"}");
        Object actual = responseLogAdvice.beforeBodyWrite("{\"code\":\"2000\"}", null, null,
                null, servletServerHttpRequest, servletServerHttpResponse);
        Assert.assertEquals(6, memoryAppender.getLoggedEvents().size());
        Assert.assertEquals("{\"code\":\"2000\"}", actual);
    }

    @Test
    public void testSupports() {
        boolean supports = responseLogAdvice.supports(null, null);
        Assert.assertTrue(supports);
    }
}

