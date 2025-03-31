package io.github.logmaster.mask;

import io.github.logmaster.mask.implementations.BlockNoMaskService;
import io.github.logmaster.utils.LogObjectMapperUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BlockNoMaskServiceTest {
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    private BlockNoMaskService blockNoMaskService;

    @Before
    public void setUp() {
        blockNoMaskService = new BlockNoMaskService(logObjectMapperUtil);
    }

    @Test
    public void testMask_StringInput() {
        String input = "1234-5678/ABCD";
        String expected = "XXXX-XXXX/XXXX";
        String result = blockNoMaskService.mask(input);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testMask_StringWithSpaces() {
        String input = "12 34-56/78";
        String expected = "XX XX-XX/XX";
        String result = blockNoMaskService.mask(input);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testMask_EmptyString() {
        String input = "";
        String result = blockNoMaskService.mask(input);
        Assert.assertEquals("", result);
    }
    @Test
    public void testMask_EmptyString_() {
        String input = "#";
        String result = blockNoMaskService.mask(input);
        Assert.assertEquals("#", result);
    }

    @Test
    public void testMask_NullInput() {
        String result = blockNoMaskService.mask(null);
        Assert.assertNull(result);
    }

    @Test
    public void testMask_ObjectInput() {
        Object obj = new HashMap<>();

        when(logObjectMapperUtil.toStringMasked(obj)).thenReturn("maskedObject");
        String result = blockNoMaskService.mask(obj);
        Assert.assertEquals("maskedObject", result);

        verify(logObjectMapperUtil).toStringMasked(obj);
    }
}

