package io.github.logmaster.mask;

import io.github.logmaster.mask.implementations.PostalCodeMaskService;
import io.github.logmaster.utils.LogObjectMapperUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostalCodeMaskServiceTest {
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    @InjectMocks
    private PostalCodeMaskService postalCodeMaskService;

    @Test
    public void testMask_AllCases() {
        // Case 1: Valid postal code
        String input = "560001";
        String expected = "••••••";
        assertEquals(expected, postalCodeMaskService.mask(input));

        // Case 2: Blank input
        String input2 = " ";
        assertEquals(input2, postalCodeMaskService.mask(input2));
        // Case 3: Null input
        assertNull(postalCodeMaskService.mask(null));
        // Case 4: Non-string input
        Object obj = new HashMap<>();
        when(logObjectMapperUtil.toStringMasked(obj)).thenReturn("maskedObj");
        assertEquals("maskedObj", postalCodeMaskService.mask(obj));
        verify(logObjectMapperUtil).toStringMasked(obj);
    }
}

