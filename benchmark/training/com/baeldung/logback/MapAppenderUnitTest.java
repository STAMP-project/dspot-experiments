package com.baeldung.logback;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;


public class MapAppenderUnitTest {
    private LoggerContext ctx;

    private MapAppender mapAppender = new MapAppender();

    private LoggingEvent event;

    @Test
    public void whenPrefixIsNull_thenMapAppenderDoesNotLog() throws Exception {
        mapAppender.setPrefix(null);
        mapAppender.append(event);
        Assert.assertTrue(mapAppender.getEventMap().isEmpty());
    }

    @Test
    public void whenPrefixIsEmpty_thenMapAppenderDoesNotLog() throws Exception {
        mapAppender.setPrefix("");
        mapAppender.append(event);
        Assert.assertTrue(mapAppender.getEventMap().isEmpty());
    }

    @Test
    public void whenLogMessageIsEmitted_thenMapAppenderReceivesMessage() throws Exception {
        mapAppender.append(event);
        Assert.assertEquals(mapAppender.getEventMap().size(), 1);
        mapAppender.getEventMap().forEach(( k, v) -> assertTrue(k.startsWith("prefix")));
    }
}

