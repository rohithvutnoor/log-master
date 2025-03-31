package io.github.logmaster.utils;

import io.github.logmaster.annotations.LogThis;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AnnotationUtilTest {
    @Test
    public void testGetDefaultAnnotationValues() {
        LogThis defaultAnnotation = AnnotationUtil.getDefaultAnnotation();
        assertEquals(LogThis.class, defaultAnnotation.annotationType());
        assertEquals(LogThis.INFO, defaultAnnotation.value());
        assertEquals(1, defaultAnnotation.limit());
        assertEquals(TimeUnit.MINUTES, defaultAnnotation.unit());
        assertFalse(defaultAnnotation.trim());
        assertTrue(defaultAnnotation.before());
        assertArrayEquals(new Class[0], defaultAnnotation.ignore());
        assertFalse(defaultAnnotation.skipResult());
        assertFalse(defaultAnnotation.skipArgs());
        assertFalse(defaultAnnotation.logString());
        assertEquals(0, defaultAnnotation.precision());
        assertEquals("", defaultAnnotation.name());
        assertFalse(defaultAnnotation.maskResult());
        assertArrayEquals(new int[0], defaultAnnotation.maskFor());
        assertArrayEquals(new Class[0], defaultAnnotation.customMaskForService());
        assertArrayEquals(new int[0], defaultAnnotation.customMaskForIndex());
        assertNull(defaultAnnotation.resultMaskService());
        assertArrayEquals(new int[0], defaultAnnotation.encryptFor());
        assertFalse(defaultAnnotation.encryptResult());
    }
}

