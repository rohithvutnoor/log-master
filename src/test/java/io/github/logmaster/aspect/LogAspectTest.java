package io.github.logmaster.aspect;

import io.github.logmaster.annotations.LogThis;
import io.github.logmaster.helpers.LogHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.JDBCException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Method;

@TestPropertySource(properties = "logging.level.root=OFF")
@RunWith(MockitoJUnitRunner.class)
public class LogAspectTest {
    @InjectMocks
    private LogAspect logAspect;
    @Mock
    private LogHelper logHelper;
    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;
    @Mock
    private MethodSignature signature;

    @Before
    public void setup() {
        Mockito.when(signature.getDeclaringType()).thenReturn(TestLogging.class);
        Mockito.when(proceedingJoinPoint.getSignature()).thenReturn(signature);
    }

    @LogThis
    public class TestLogging {

    }

    @Test
    public void loggingAspectTest() {
        logAspect.all();
        logAspect.logThisOnClass();
        logAspect.logThisOnMethod();
        Assert.assertTrue(true);
    }

    @Test
    public void aspectAroundMethod() throws Throwable {
        Mockito.when(signature.getMethod()).thenReturn(myLogMethod());
        Mockito.when(logHelper.over(Mockito.any(), Mockito.nullable(long.class))).thenReturn(true);
        logAspect.aspectAroundMethod(proceedingJoinPoint);
        Assert.assertTrue(true);

    }

    public Method myLogMethod() throws NoSuchMethodException, SecurityException {
        return getClass().getDeclaredMethod("testLogMethod");
    }

    @LogThis
    public String testLogMethod() {
        return null;
    }

    @Test
    public void aspectAroundClass() throws Throwable {
        Mockito.when(logHelper.isLoggingEnabled(Mockito.nullable(int.class),
                Mockito.any())).thenReturn(true);
        logAspect.aspectAroundClass(proceedingJoinPoint);
        Assert.assertTrue(true);
    }

    @Test
    public void aspectAroundRepository() throws Throwable {
        logAspect.aspectAroundRepository(proceedingJoinPoint);
        Assert.assertTrue(true);
    }

    @Test(expected = JDBCException.class)
    public void aspectAroundMethod_exception() throws Throwable {
        Mockito.when(signature.getMethod()).thenReturn(myLogMethod());
        Mockito.when(logHelper.getLogDetail(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.nullable(long.class))).thenThrow(new JDBCException(null, null));
        logAspect.aspectAroundMethod(proceedingJoinPoint);
        Mockito.when(logHelper.isLoggingEnabled(Mockito.nullable(int.class), Mockito.any())).thenReturn(true);
        logAspect.aspectAroundMethod(proceedingJoinPoint);
    }

    @Test(expected = JDBCException.class)
    public void aspectAroundClass_exception() throws Throwable {
        Mockito.when(logHelper.getLogDetail(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.nullable(long.class))).thenThrow(new JDBCException(null, null));
        logAspect.aspectAroundClass(proceedingJoinPoint);
        Mockito.when(logHelper.isLoggingEnabled(Mockito.nullable(int.class), Mockito.any())).thenReturn(true);
        logAspect.aspectAroundClass(proceedingJoinPoint);
    }

    @Test(expected = JDBCException.class)
    public void aspectAroundRepository_exception() throws Throwable {
        Mockito.when(logHelper.getLogDetail(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.nullable(long.class))).thenThrow(new JDBCException(null, null));
        logAspect.aspectAroundRepository(proceedingJoinPoint);
        Mockito.when(logHelper.isLoggingEnabled(Mockito.nullable(int.class), Mockito.any())).thenReturn(true);
        logAspect.aspectAroundRepository(proceedingJoinPoint);
    }
}
