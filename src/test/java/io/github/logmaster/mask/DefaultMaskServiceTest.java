package io.github.logmaster.mask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.logmaster.helpers.MaskHelper;
import io.github.logmaster.mask.implementations.DefaultMaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMaskServiceTest {
    @Mock
    private MaskHelper maskHelper;
    @Mock
    @Qualifier("maskingObjectMapper")
    private ObjectMapper maskingObjectMapper;
    @InjectMocks
    private DefaultMaskService defaultMaskService;

    @Test
    public void testMask_StringData() {
        String unmaskedText = "Sensitive Data";
        String expectedMaskedText = "Masked Data";
        when(maskHelper.mask(unmaskedText)).thenReturn(expectedMaskedText);
        String result = defaultMaskService.mask(unmaskedText);
        assertEquals(expectedMaskedText, result);
    }


    @Test
    public void testMask_StringDataException() throws JsonProcessingException {

        String unmaskedText = "Sensitive Data";
        String expectedMaskedText = "{}";
        JsonProcessingException jsonProcessingException = mock(JsonProcessingException.class);

        doThrow(jsonProcessingException).when(maskingObjectMapper).writeValueAsString(any());

        String result = defaultMaskService.mask(new HashMap<>());

        assertEquals(expectedMaskedText, result);
    }

    @Test
    public void testMask_NullData() {

        String result = defaultMaskService.mask(null);

        assertEquals("", result); // Null should return an empty string

    }

    @Test
    public void testMask_ObjectData() throws JsonProcessingException {

        MyClass myObject = new MyClass("John", "Doe");
        String jsonObject = "{\"firstName\": \"John\", \"lastName\": \"Doe\"}";

        when(maskingObjectMapper.writeValueAsString(myObject)).thenReturn(jsonObject);

        String result = defaultMaskService.mask(myObject);

        assertEquals(jsonObject, result);
    }

    public static class MyClass {

        private String firstName;
        private String lastName;

        public MyClass(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public String toString() {
            return "MyClass [firstName=" + firstName + ", lastName=" + lastName + "]";
        }
    }
}
