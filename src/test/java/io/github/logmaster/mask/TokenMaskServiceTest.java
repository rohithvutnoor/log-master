package io.github.logmaster.mask;

import io.github.logmaster.mask.implementations.TokenMaskService;
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
public class TokenMaskServiceTest {
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    @InjectMocks
    private TokenMaskService tokenMaskService;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(tokenMaskService, "length", 6);
    }

    @Test
    public void testMask_AllCases() {
        // Case 1: Normal token string
        String token = "abcdef1234567890";
        String expected = "abcdef********";
        assertEquals(expected, tokenMaskService.mask(token));
        // Case 2: Token shorter than visible length
        String shortToken = "abc";
        String expectedShort = "abc********";
        assertEquals(expectedShort, tokenMaskService.mask(shortToken));
        // Case 3: Blank token
        String blankToken = " ";
        assertEquals(blankToken, tokenMaskService.mask(blankToken));
        // Case 4: Null input
        assertNull(tokenMaskService.mask(null));
        // Case 5: Non-string object
        Object obj = new HashMap<>();
        when(logObjectMapperUtil.toStringMasked(obj)).thenReturn("maskedobj");
        assertEquals("maskedobj", tokenMaskService.mask(obj));
        verify(logObjectMapperUtil).toStringMasked(obj);
    }
}
