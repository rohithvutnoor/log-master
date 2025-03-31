package io.github.logmaster.convertor;

import io.github.logmaster.beans.LogDetails;
import io.github.logmaster.helpers.ApiBusinessStepHelper;
import io.github.logmaster.helpers.TraceIdKeyFinder;
import io.github.logmaster.mask.implementations.DefaultMaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.converter.HttpMessageConverter;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientHttpMessageStateConverterTest {

    @InjectMocks
    ClientHttpMessageStateConverter clientHttpMessageStateConverter;
    @Mock
    HttpMessageConverter converter;
    @Mock
    DefaultMaskService defaultMaskService;
    @Mock
    TraceIdKeyFinder traceIdKeyFinder;
    @Mock
    ApiBusinessStepHelper apiBusinessStepHelper;

    @Test
    public void testSuccessfulConversionToClientState() throws IOException {
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        LogDetails mockObject = mock(LogDetails.class);
        when(converter.read(any(), any())).thenReturn(mockObject);
        when(defaultMaskService.mask(any())).thenReturn("***");
        when(apiBusinessStepHelper.getSubsystemBusinessDescription(any(), anyString(), anyString())).thenReturn("testStep");
        Object result = clientHttpMessageStateConverter.read(LogDetails.class, inputMessage);
        HttpRequest request = mock(HttpRequest.class);
        when(request.getURI()).thenReturn(URI.create("/api"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
        clientHttpMessageStateConverter.addMetaDataToMDC(request);
        clientHttpMessageStateConverter.getSupportedMediaTypes();
        clientHttpMessageStateConverter.write(LogDetails.class, null, outputMessage);
        clientHttpMessageStateConverter.canWrite(LogDetails.class, null);
        clientHttpMessageStateConverter.canRead(LogDetails.class, null);
        assertNotNull(result);
        assertEquals(mockObject, result);
    }
}
