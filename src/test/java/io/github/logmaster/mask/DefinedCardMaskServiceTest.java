package io.github.logmaster.mask;


import io.github.logmaster.mask.implementations.DefinedCardMaskService;
import io.github.logmaster.utils.LogObjectMapperUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinedCardMaskServiceTest {
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    @InjectMocks
    private DefinedCardMaskService definedCardMaskService;

    @Test
    public void testMask_AllCases() {
        // Case 1: Input is a string - returned as-is
        String input = "4111222233334444";
        String result = definedCardMaskService.mask(input);
        assertEquals(input, result);
        // Case 2: Input is a non-string object - use logObjectMapperUtil
        Object obj = new HashMap<>();
        when(logObjectMapperUtil.toStringMasked(obj)).thenReturn("maskedObj");
        String result2 = definedCardMaskService.mask(obj);
        assertEquals("maskedObj", result2);
        verify(logObjectMapperUtil).toStringMasked(obj);
    }
}