package io.github.logmaster.configuration.general;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.MDC;

import java.util.Map;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class MDCDecoratorTest {
    @Test
    public void test() {
        try (var mockedMDC = Mockito.mockStatic(MDC.class)) {
            mockedMDC.when(MDC::getCopyOfContextMap).thenReturn(Map.of("user", "test"));
            mockedMDC.when(() -> MDC.get("user")).thenReturn("test");
            Runnable mockRunnable = mock(Runnable.class);
            MDCDecorator decorator = new MDCDecorator();
            decorator.decorate(mockRunnable).run();
            mockedMDC.verify(() -> MDC.put("user", "test"));
            mockedMDC.verify(MDC::clear);
        }
    }
}
