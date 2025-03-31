package io.github.logmaster.configuration.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.FilterReply;
import io.github.logmaster.mask.implementations.DefaultMaskService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LogTurboFilterTest {

    @Mock
    private DefaultMaskService defaultMaskService;
    @Mock
    private Logger logger;
    @Mock
    private Marker marker;
    @Mock
    private Throwable throwable;

    private LogTurboFilter logTurboFilter;

    @Mock
    private LoggerContext loggerContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        logTurboFilter = new LogTurboFilter(defaultMaskService);
        when(logger.getLoggerContext()).thenReturn(loggerContext);
    }

    @After
    public void tearDown() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
    }

    @Test
    public void testDecideWithSensitiveMarker() {
        String data = "Sensitive data";
        String[] params = {"param1", "param2"};
        when(marker.getName()).thenReturn("SENSITIVE");
        when(defaultMaskService.mask(data)).thenReturn("Masked data");
        when(defaultMaskService.mask("param1")).thenReturn("maskedParam1");
        when(defaultMaskService.mask("param2")).thenReturn("maskedParam2");
        FilterReply result = logTurboFilter.decide(marker, logger, Level.INFO, data, params, null);
        assertEquals(FilterReply.DENY, result);
        verify(defaultMaskService).mask(data);
        verify(defaultMaskService).mask("param1");
        verify(defaultMaskService).mask("param2");
    }


    @Test
    public void testDecideWithNonSensitiveMarker() {
        String data = "Non-sensitive data";
        when(marker.getName()).thenReturn("NON_SENSITIVE");
        FilterReply result = logTurboFilter.decide(marker, logger, Level.INFO, data, null, null);
        assertEquals(FilterReply.NEUTRAL, result);
        verify(defaultMaskService, never()).mask(any());
    }
}


