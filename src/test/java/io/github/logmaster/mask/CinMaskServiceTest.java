package io.github.logmaster.mask;

import io.github.logmaster.mask.implementations.CinMaskService;
import io.github.logmaster.utils.LogObjectMapperUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CinMaskServiceTest {
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    @InjectMocks
    private CinMaskService cinMaskService;

    @Before
    public void setUp() {
        // Manually set the value of maskLength for testing purposes
        ReflectionTestUtils.setField(cinMaskService, "maskLength", 4);
    }

    @Test
    public void testMask_ValidCIN() {
        String unMaskedCIN = "123456789";
        String expectedMaskedCIN = "1234XXXXX";
        String result = cinMaskService.mask(unMaskedCIN);
        assertEquals(expectedMaskedCIN, result);
    }

    @Test
    public void testMask_ShortCIN() {
        String unMaskedCIN = "1234"; // Length equal to maskLength, no masking needed
        String result = cinMaskService.mask(unMaskedCIN);

        assertEquals(unMaskedCIN, result); // Since length is equal to maskLength, no masking should occur
    }

    @Test
    public void testMask_EmptyCIN() {

        String unMaskedCIN = "";
        String result = cinMaskService.mask(unMaskedCIN);

        assertEquals("", result); // Empty string should return as-is
    }

    @Test
    public void testMask_NulICIN() {
        String result = cinMaskService.mask(null);
        assertNull(result); // Null should return null
    }

    @Test
    public void testMask_ObjectInput() {
        Object obj = new HashMap<>();

        when(logObjectMapperUtil.toStringMasked(obj)).thenReturn("maskedObject");
        String result = cinMaskService.mask(obj);

        assertEquals("maskedObject", result); // Should call logObjectMapperUtil and return maskedobject

        verify(logObjectMapperUtil).toStringMasked(obj);
    }
}