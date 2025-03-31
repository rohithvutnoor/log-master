package io.github.logmaster.helpers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TraceIdKeyFinderTest {
    @InjectMocks
    private TraceIdKeyFinder traceIdKeyFinder;
    @Mock
    private List<String> defaultKeys;
    @Mock
    private List<String> keys;
    private Map<String, String> headers;

    @Before
    public void setUp() {
        defaultKeys = Arrays.asList("defaultKey1", "defaultKey2");
        keys = Arrays.asList("key1", "key2");
        headers = new HashMap<>();
        headers.put("key1", "value1");
        headers.put("defaultKey2", "value2");
        traceIdKeyFinder = new TraceIdKeyFinder();
        traceIdKeyFinder.setKeys(keys);
        traceIdKeyFinder.setDefaultKeys(defaultKeys);
    }

    @Test
    public void testExtractByKeyMatchWithMatchingKey() {
        Map<String, String> headers = new HashMap<>();
        headers.put("key1", "value1");
        String result = traceIdKeyFinder.extractByKeyMatch(headers);
        assertEquals("value1", result);
    }

    @Test
    public void testExtractByKeyMatchWithNoMatchingKeyInKeysButMatchInDefault() {

        Map<String, String> headers = new HashMap<>();
        headers.put("defaultKey2", "value2");
        String result = traceIdKeyFinder.extractByKeyMatch(headers);

        assertNull(result);
    }

    @Test
    public void testExtractByKeyMatchWithNoMatchingKey() {
        Map<String, String> headers = new HashMap<>();
        headers.put("nonExistentKey", "value");
        String result = traceIdKeyFinder.extractByKeyMatch(headers);
        assertNull(result);
    }

    @Test
    public void testExtractByKeyMatchHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("key1", "value1");
        String result = traceIdKeyFinder.extractByKeyMatch(httpHeaders);
        assertEquals("value1", result);
    }

    @Test
    public void testExtractByKeyMatchHttpHeadersWithNoMatch() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("nonExistentKey", "value");
        String result = traceIdKeyFinder.extractByKeyMatch(httpHeaders);
        String result2 = traceIdKeyFinder.extractByKeyMatch(Map.of());
        assertEquals("", result);
        traceIdKeyFinder.getDefaultKeys();
        traceIdKeyFinder.getKeys();
    }
}



