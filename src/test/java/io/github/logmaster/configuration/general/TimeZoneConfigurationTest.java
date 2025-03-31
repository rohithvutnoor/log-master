package io.github.logmaster.configuration.general;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.TimeZone;

@RunWith(MockitoJUnitRunner.class)
public class TimeZoneConfigurationTest {
    private TimeZoneConfiguration timeZoneConfiguration;

    @Before
    public void setUp() throws IllegalAccessException, NoSuchFieldException {
        timeZoneConfiguration = new TimeZoneConfiguration();
        Field timeStampField = TimeZoneConfiguration.class.getDeclaredField("timeStamp");
        timeStampField.setAccessible(true);
        timeStampField.set(timeZoneConfiguration, "GMT");
    }

    @Test
    public void testSetTimeZone() {
        timeZoneConfiguration.setTimeZone();
        Assert.assertEquals("GMT", TimeZone.getDefault().getID());
    }
}

