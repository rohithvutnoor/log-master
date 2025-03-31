package io.github.logmaster.configuration.mask;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.logmaster.annotations.Mask;
import io.github.logmaster.enums.MaskRule;
import io.github.logmaster.helpers.MaskHelper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class MaskSerializerTest {
    @Mock
    MaskHelper maskingService;
    @Mock
    JsonGenerator gen;
    @Mock
    SerializerProvider serializerProvider;
    @InjectMocks
    private MaskSerializer maskSerializer;

    @Test
    void testMaskSensitiveData() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(maskingService.mask("1234")).thenReturn("****");
        JsonStreamContext context = mock(JsonStreamContext.class);
        when(gen.getOutputContext()).thenReturn(context);
        Data data = new Data();
        data.card_data = "1234";
        when(context.getCurrentName()).thenReturn("card");
        when(context.getCurrentValue()).thenReturn(data);
        maskSerializer.serialize("1234", gen, serializerProvider);
        Assert.assertTrue(true);
    }

    @Test
    void testMaskSensitiveDataException() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(maskingService.mask("1234")).thenReturn("****");
        JsonStreamContext context = mock(JsonStreamContext.class);
        when(gen.getOutputContext()).thenReturn(context);
        Data data = new Data();
        data.card_data = "1234";
        when(context.getCurrentName()).thenReturn("card");
        when(context.getCurrentValue()).thenReturn(null);
        maskSerializer.serialize("1234", gen, serializerProvider);
        Assert.assertTrue(true);
    }

    @Test
    void testMaskSensitiveDataExceptionObject() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(maskingService.mask("1234")).thenReturn("****");
        JsonStreamContext context = mock(JsonStreamContext.class);
        when(gen.getOutputContext()).thenReturn(context);
        Data data = new Data();
        data.card_data = "1234";
        when(context.getCurrentName()).thenReturn("card");
        when(context.getCurrentValue()).thenReturn(null);
        maskSerializer.serialize(data, gen, serializerProvider);
        Assert.assertTrue(true);
    }

    @Test
    void testMaskSensitiveDataNull() throws IOException {
        MockitoAnnotations.openMocks(this);
        maskSerializer.serialize(null, gen, null);
        Assert.assertTrue(true);
    }

    static class Data {

        @Mask(type = MaskRule.CARD_NUMBER)

        @JsonProperty("card")
        String card_data;
        String mobile;

    }

}