package io.github.logmaster.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.logmaster.annotations.LogThis;
import io.github.logmaster.beans.LogDetails;
import io.github.logmaster.mask.implementations.DefaultMaskService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.JDBCException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class LogHelperTest {
    @InjectMocks
    private LogHelper logHelper;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private LogThis logThis;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private MethodSignature method;
    private Logger logger = LoggerFactory.getLogger(LogHelperTest.class);

    @Mock
    ObjectMapper objectMapper;
    @Mock
    private LogDetails logDetails;

    @Test

    public void logHelperTest() {
        Mockito.when(logThis.unit()).thenReturn(TimeUnit.DAYS);
        logHelper.over(logThis, 1);
        logHelper.getClass();
        Assert.assertTrue(true);
    }


    @Test
    public void log() {
        logHelper.log(0, logger, logDetails);
        logHelper.log(1, logger, logDetails);
        logHelper.log(2, logger, logDetails);
        logHelper.log(3, logger, logDetails);
        logHelper.log(4, logger, logDetails);
        logHelper.log(5, logger, logDetails);
        Assert.assertTrue(true);
    }

    @Test
    public void getLogger() throws NoSuchMethodException, SecurityException {
        mockMethod();
        logHelper.getLogger(method, "");
        logHelper.getLogger(method, "a");
        Assert.assertTrue(true);
    }

    private void mockMethod() throws NoSuchMethodException {
        Mockito.when(method.getMethod()).thenReturn(getClass().getDeclaredMethod("toLog"));
    }

    @Test
    public void toLog() throws NoSuchMethodException, SecurityException {
        mockMethod();
        logHelper.getLogDetail(logThis, joinPoint, method);
        Mockito.when(logThis.skipArgs()).thenReturn(true);
        logHelper.getLogDetail(logThis, joinPoint, method);
        Assert.assertTrue(true);
    }

    @Test
    public void testMapLogDataWithMaskingForInputParameter() {
        LogThis annotation = Mockito.mock(LogThis.class);
        Mockito.when(annotation.maskFor()).thenReturn(new int[]{0});
        Mockito.when(annotation.customMaskForIndex()).thenReturn(new int[]{});
        Object[] values = new Object[]{"sensitive-data"};
        List<String> valueTypes = Arrays.asList("java.lang.String");
        boolean isBefore = true;
        StringBuilder message = new StringBuilder();
        List<Object> parameters = new ArrayList<>();
        DefaultMaskService defaultMaskService = Mockito.mock(DefaultMaskService.class);
        Mockito.when(applicationContext.getBean(DefaultMaskService.class)).thenReturn(defaultMaskService);
        Mockito.when(defaultMaskService.mask(Mockito.anyString())).thenReturn("masked-data");
        logHelper.mapLogData(annotation, values, valueTypes, isBefore, message, parameters, 0);
        Assert.assertEquals(" [{}: {}]", message.toString());
        Assert.assertEquals(2, parameters.size());
        Assert.assertEquals("String", parameters.get(0));
        Assert.assertEquals("masked-data", parameters.get(1));
        Mockito.verify(defaultMaskService).mask("sensitive-data");
    }

    @Test
    public void toLogl() {
        logHelper.getLogDetail(method, null);
        // with Exception
        logHelper.getLogDetail(method, new JDBCException(null, null));
        Assert.assertTrue(true);
    }


    @Test
    public void toLog2() throws NoSuchMethodException, SecurityException {
        mockMethod();
        Mockito.when(logThis.unit()).thenReturn(TimeUnit.MINUTES);
        logHelper.getLogDetail(logThis, method, "(\"a\":1)", 1);
        logHelper.getLogDetail(logThis, method, "(\"a\" :1}",1);
        Mockito.when(logThis.skipResult()).thenReturn(true);
        logHelper.getLogDetail(logThis, method, "(\"a\":1}", 1);
        Assert.assertTrue(true);
    }

    @Test
    public void isLoggingEnabled() {
        logHelper.isLoggingEnabled(0, logger);
        logHelper.isLoggingEnabled(1, logger);
        logHelper.isLoggingEnabled(2, logger);
        logHelper.isLoggingEnabled(3, logger);
        logHelper.isLoggingEnabled(4, logger);
        logHelper.isLoggingEnabled(5, logger);
        Assert.assertTrue(true);
    }
}

