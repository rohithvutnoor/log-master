package io.github.logmaster.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.logmaster.beans.LogDetails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogObjectMapperUtilTest {
    @InjectMocks
    private LogObjectMapperUtil logObjectMapperUtil;
    @Mock
    @Qualifier("maskingObjectMapper")
    ObjectMapper maskingObjectMapper;

    @Test
    public void testJsonSerializationAndDeserialization() throws JsonProcessingException {
        LogDetails testObject = LogDetails.builder().message("code").parameters(null).build();
        when(maskingObjectMapper.writeValueAsString(any())).thenReturn("");
        String string = logObjectMapperUtil.toString(testObject);
        String stringMasked = logObjectMapperUtil.toStringMasked(testObject);
        LogDetails log = logObjectMapperUtil.toObject("", LogDetails.class);
        LogDetails logNull = logObjectMapperUtil.toObject(null, LogDetails.class);
        String logNull2 = logObjectMapperUtil.toString(null);
        LogDetails logNull3 = logObjectMapperUtil.toObject("a", null);
        assertEquals(string, stringMasked);
        assertNull(logNull);
        assertNull(logNull3);
    }

    @Test
    public void testJsonSerializationAndDeserializationException() throws JsonProcessingException {
        LogDetails testObject = LogDetails.builder().message("code").parameters(null).build();
        JsonProcessingException exception = mock(JsonProcessingException.class);
        when(maskingObjectMapper.writeValueAsString(any())).thenThrow(exception);
        when(maskingObjectMapper.readValue(anyString(), eq(LogDetails.class))).thenThrow(exception);
        String string = logObjectMapperUtil.toString(testObject);
        String stringMasked = logObjectMapperUtil.toStringMasked(testObject);
        LogDetails logNull = logObjectMapperUtil.toObject(" {\"a\":1}", LogDetails.class);
        assertEquals(string, stringMasked);
        assertNull(logNull);
    }
}
