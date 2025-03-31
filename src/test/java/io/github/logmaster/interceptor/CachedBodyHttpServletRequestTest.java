package io.github.logmaster.interceptor;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CachedBodyHttpServletRequestTest {
    private HttpServletRequest mockRequest;
    private CachedBodyHttpServletRequest cachedRequest;
    private static final String REQUEST_BODY = "Test request body";

    @Before
    public void setUp() throws IOException {
        mockRequest = mock(HttpServletRequest.class);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(REQUEST_BODY.getBytes(StandardCharsets.UTF_8));


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

        when(mockRequest.getInputStream()).thenReturn(mockInputStream);

        cachedRequest = new CachedBodyHttpServletRequest(mockRequest);

    }

    @Test
    public void testCachedBodyIsStoredCorrectly() throws IOException {
        String cachedBody = new String(cachedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String cachedBodyAsString = cachedRequest.getCachedBodyAsString();
        BufferedReader reader = cachedRequest.getReader();
        assertEquals(REQUEST_BODY, cachedBody);
    }

    @Test
    public void testMultipleReadsReturnSameData() throws IOException {
        String firstRead = new String(cachedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String secondRead = new String(cachedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(REQUEST_BODY, firstRead);
        assertEquals(REQUEST_BODY, secondRead);
    }

    @Test
    public void testIsFinishedReturnsTrueAfterReading() throws IOException {
        ServletInputStream inputStream = cachedRequest.getInputStream();
        while (!inputStream.isFinished()) {
            inputStream.read();
        }
        assertTrue(inputStream.isFinished());
    }

    @Test
    public void testIsReadyAlwaysReturnsTrue() throws IOException {
        ServletInputStream inputStream = cachedRequest.getInputStream();
        assertTrue(inputStream.isReady());
    }

    @Test
    public void testSetReadListenerDoesNotThrowException() {
        ServletInputStream inputStream = cachedRequest.getInputStream();
        assertDoesNotThrow(() -> inputStream.setReadListener(mock(ReadListener.class)));
    }
}