package io.github.logmaster.helpers;

import io.github.logmaster.mask.implementations.SensitiveMaskService;
import io.github.logmaster.mask.interfaces.LogMask;
import io.github.logmaster.propertystore.MaskServiceProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MaskHelperTest {
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private MaskServiceProperties maskServiceProperties;
    @Mock
    private LogMask logMask;
    @Mock
    private SensitiveMaskService sensitiveMaskService;
    @InjectMocks
    private MaskHelper maskHelper;

    @Test
    public void testMaskWithValidMaskType() {

        String unMaskedValue = "sensitiveValue";
        String expectedMaskedValue = "maskedValue";

        when(maskServiceProperties.getService()).thenReturn(Map.of("sensitive", "io.github.logmaster.mask.implementations.SensitiveMaskService"));

        when(applicationContext.getBean(any(Class.class))).thenReturn(sensitiveMaskService);

        when(sensitiveMaskService.mask(unMaskedValue)).thenReturn(expectedMaskedValue);
        String result = maskHelper.mask(unMaskedValue);

        assertEquals(expectedMaskedValue, result);
    }

    @Test
    public void testMask_withInvalidMaskType() {

        String unMaskedValue = "sensitiveValue";

        when(maskServiceProperties.getService()).thenReturn(Map.of("sensitive", ""));
        String result = maskHelper.mask(unMaskedValue);
        assertEquals(unMaskedValue, result);
    }
}
