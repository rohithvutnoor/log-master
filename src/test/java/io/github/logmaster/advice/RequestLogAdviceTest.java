package io.github.logmaster.advice;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.logmaster.utils.LogObjectMapperUtil;
import io.github.logmaster.utils.MemoryAppender;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestLogAdviceTest {
    @InjectMocks
    private RequestLogAdvice requestLogAdvice;
    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    private MemoryAppender memoryAppender;

    @After
    public void tearDown() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
    }

    @Before
    public void beforeEach() {
        Logger logger = (Logger)
                LoggerFactory.getLogger("io.github.logmaster.advice.RequestLogAdvice");
        logger.setLevel(Level.INFO);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @Test
    public void testAfterBodyRead() throws Exception {
        when(logObjectMapperUtil.toStringMasked(any())).thenReturn("{\"mobile\": \"XXXXXX2312\"}");
        Object actual = requestLogAdvice.afterBodyRead(" {\"mobile\":\"912312312\"}", null, null,
                null, null);
        List<ILoggingEvent> logEvents = memoryAppender.getLoggedEvents();
        ILoggingEvent logEvent = logEvents.get(0);
        Assert.assertEquals("[Incoming] Request Body           : {\"mobile\": \"XXXXXX2312\"}", logEvent.getFormattedMessage());
    }

    @Test
    public void testSupports() {
        boolean supports = requestLogAdvice.supports(null, null, null);
        Assert.assertTrue(supports);
    }
}
