package com.baeldung.logback;


import ch.qos.logback.classic.Logger;
import org.junit.Assert;
import org.junit.Test;


public class MapAppenderIntegrationTest {
    private Logger rootLogger;

    @Test
    public void whenLoggerEmitsLoggingEvent_thenAppenderReceivesEvent() throws Exception {
        rootLogger.info("Test from {}", this.getClass().getSimpleName());
        MapAppender appender = ((MapAppender) (rootLogger.getAppender("map")));
        Assert.assertEquals(appender.getEventMap().size(), 1);
    }

    @Test
    public void givenNoPrefixSet_whenLoggerEmitsEvent_thenAppenderReceivesNoEvent() throws Exception {
        rootLogger.info("Test from {}", this.getClass().getSimpleName());
        MapAppender appender = ((MapAppender) (rootLogger.getAppender("badMap")));
        Assert.assertEquals(appender.getEventMap().size(), 0);
    }
}

