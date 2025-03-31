package io.github.logmaster.configuration.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.logmaster.configuration.mask.MaskSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ObjectMapperConfigurationTest {
    @Test
    public void testCreateMaskingMapper() {
        MaskSerializer mockMaskSerializer = mock(MaskSerializer.class);
        ObjectMapperConfiguration config = new ObjectMapperConfiguration();
        ObjectMapper maskingMapper = config.createMaskingMapper(mockMaskSerializer);
        Assert.assertNotNull(maskingMapper);
    }

    @Test
    public void testObjectMapper() {
        ObjectMapperConfiguration config = new ObjectMapperConfiguration();
        ObjectMapper objectMapper = config.objectMapper();
        Assert.assertNotNull(objectMapper);
    }
}
