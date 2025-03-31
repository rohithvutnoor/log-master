package io.github.logmaster.mask;

import io.github.logmaster.helpers.MaskHelper;
import io.github.logmaster.mask.implementations.SensitiveMaskService;
import io.github.logmaster.propertystore.SensitiveRegexProperties;
import io.github.logmaster.utils.LogObjectMapperUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SensitiveMaskServiceTest {
    @Mock
    private SensitiveRegexProperties regexProperties;
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    @Mock
    private MaskHelper maskHelper;
    @InjectMocks
    private SensitiveMaskService sensitiveMaskService;

    @Before
    public void setUp() {
        // Example: mask PAN numbers like ABCDE1234F with key "pan"
        when(regexProperties.getPattern()).thenReturn(
                Map.of("pan", "[A-Z]{5}[0-9]{4}[A-Z]{1}"));
    }

    @Test
    public void testMask_AllCases() {
        // Case 1: Matching pattern in string
        String input = "Customer PAN is ABCDE1234F";
        String maskedValue = "XXXXX1234X";
        when(maskHelper.mask("ABCDE1234F", "pan")).thenReturn(maskedValue);
        String resultl = sensitiveMaskService.mask(input);
        assertEquals("Customer PAN is XXXXX1234X", resultl);
        // Case 2: Non-matching string
        String input2 = "No sensitive data here";
        String result2 = sensitiveMaskService.mask(input2);
        assertEquals(input2, result2);
        // Case 3: Null regex map (simulate empty config)
        when(regexProperties.getPattern()).thenReturn(Map.of());
        String result3 = sensitiveMaskService.mask("Text with nothing sensitive");
        assertEquals("Text with nothing sensitive", result3);
        // Case 4: Non-string object
        Object nonString = new Object();
        when(logObjectMapperUtil.toStringMasked(nonString)).thenReturn("maskedObj");
        String result4 = sensitiveMaskService.mask(nonString);
        assertEquals("maskedObj", result4);
        verify(logObjectMapperUtil).toStringMasked(nonString);
        when(regexProperties.getPattern()).thenReturn(
                Map.of("pan", ""));
        String result5 = sensitiveMaskService.mask("123");
        assertEquals("123", result5);
        verify(logObjectMapperUtil).toStringMasked(nonString);
    }
}


