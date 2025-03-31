package io.github.logmaster.enums;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author rohithvutnoor
 */
public class LogMarker {
    public static final Marker SENSITIVE = MarkerFactory.getMarker("SENSITIVE");

    private LogMarker() {
        //default constructor
    }
}
