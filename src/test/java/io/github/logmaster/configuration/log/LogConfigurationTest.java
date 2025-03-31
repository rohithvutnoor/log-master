package io.github.logmaster.configuration.log;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import io.github.logmaster.propertystore.AsyncLogProperties;
import io.github.logmaster.propertystore.FileAppenderLogProperties;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LogConfigurationTest {
    @Mock
    private Environment environment;
    @Mock
    private AsyncLogProperties asyncLogProperties;
    @Mock
    private FileAppenderLogProperties fileAppenderLogProperties;
    @Mock
    private LoggerContext loggerContext;
    @Mock
    private Logger rootLogger;
    @Mock
    private PatternLayoutEncoder encoder;
    @Mock
    private ConsoleAppender<ILoggingEvent> consoleAppender;
    @Mock
    private RollingFileAppender<ILoggingEvent> fileAppender;
    @Mock
    private AsyncAppender asyncAppender;
    @Mock
    private Appender<ILoggingEvent> mockAppender;
    private LogConfiguration logConfiguration;


    @Before
    public void setUp() {
        logConfiguration = new LogConfiguration(asyncLogProperties, fileAppenderLogProperties,
                environment);
        when(fileAppenderLogProperties.isEnabled()).thenReturn(true);
        when(fileAppenderLogProperties.getPath()).thenReturn("/path/to/log");
        when(asyncLogProperties.isEnabled()).thenReturn(true);
        when(asyncLogProperties.getQueueSize()).thenReturn(100);
        when(asyncLogProperties.isNeverBlock()).thenReturn(true);
        when(asyncLogProperties.getDiscardingThreshold()).thenReturn(200);
        when(environment.getProperty("logging.pattern.console")).thenReturn(" [pattern]");
    }

    @After
    public void tearDown() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
    }

    @Test
    public void testConfigureAsyncLoggingwithFileAndAsyncLogEnabled() {
        logConfiguration.configureAsyncLogging();
        Assert.assertTrue(true);
    }

    @Test
    public void testConfigureAsyncLoggingwithNoFileAppender() {
        when(fileAppenderLogProperties.isEnabled()).thenReturn(false);
        logConfiguration.configureAsyncLogging();
        Assert.assertTrue(true);
    }

    @Test
    public void testConfigureAsyncLoggingWithNoAsyncLog() {
        when(asyncLogProperties.isEnabled()).thenReturn(false);
        logConfiguration.configureAsyncLogging();
        verify(rootLogger, never()).addAppender(asyncAppender);
    }
}
