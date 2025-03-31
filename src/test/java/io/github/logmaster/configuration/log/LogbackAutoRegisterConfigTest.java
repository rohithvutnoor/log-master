package io.github.logmaster.configuration.log;

import ch.qos.logback.classic.LoggerContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class LogbackAutoRegisterConfigTest {
    @InjectMocks
    LogbackAutoRegisterConfig logbackAutoRegisterConfig;
    @Mock
    LogTurboFilter logTurboFilter;
    @Mock
    ApplicationContext context;

    @After
    public void tearDown() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
    }

    @Test
    public void test() {
        logbackAutoRegisterConfig.setApplicationContext(context);
        Assert.assertTrue(true);
    }
}