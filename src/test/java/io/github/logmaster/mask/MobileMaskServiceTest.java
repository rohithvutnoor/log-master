package io.github.logmaster.mask;

import io.github.logmaster.mask.implementations.MobileMaskService;
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
public class MobileMaskServiceTest {
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    @InjectMocks
    private MobileMaskService mobileMaskService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(mobileMaskService, "length", 4);
    }

    @Test
    public void testMask_WithString() {

        String input = "9876543210";
        String expected = "••••••3210";
        String result = mobileMaskService.mask(input);

        assertEquals(expected, result);
    }

    @Test
    public void testMask_ShortNumber() {
        String input = "123";
        String result = mobileMaskService.mask(input);
        assertEquals("123", result);
    }

    @Test
    public void testMask_Null() {
        String result = mobileMaskService.mask("");
        assertEquals(result,"");
    }

    @Test
    public void testMask_NonStringObject() {
        Object input = new HashMap<>();
        when(logObjectMapperUtil.toStringMasked(input)).thenReturn("maskedObject");
        String result = mobileMaskService.mask(input);
        assertEquals("maskedObject", result);
        verify(logObjectMapperUtil).toStringMasked(input);
    }
}


