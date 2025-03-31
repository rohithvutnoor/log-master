package io.github.logmaster.mask;

import io.github.logmaster.mask.implementations.OTPMaskService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OTPMaskServiceTest {
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    @InjectMocks
    private OTPMaskService otpMaskService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(otpMaskService, "length", 3);
    }

    @Test
    public void testMask_WithOTP6Digit() {
        String input = "Your OTP is 123456";
        String expected = "Your OTP is ***456";
        String result = otpMaskService.mask(input);
        assertEquals(expected, result);
    }


    @Test
    public void testMask_WithOTP_4Digit() {
        String input = "Code: 9876";
        String expected = "Code: ***6";
        String result = otpMaskService.mask(input);
        assertEquals(expected, result);
    }

    @Test
    public void testMask_NoDigits() {
        String input = "No code here";
        String result = otpMaskService.mask(input);

        assertEquals(input, result);
    }

    @Test
    public void testMask_NonStringObject() {

        Object input = new HashMap<>();

        when(logObjectMapperUtil.toStringMasked(input)).

                thenReturn("maskedobj");
        String result = otpMaskService.mask(input);

        assertEquals("maskedobj", result);

        verify(logObjectMapperUtil).toStringMasked(input);
    }
}
