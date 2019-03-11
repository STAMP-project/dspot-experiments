package com.baeldung.logging.log4j2.appender;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class MapAppenderIntegrationTest {
    private Logger logger;

    @Test
    public void whenLoggerEmitsLoggingEvent_thenAppenderReceivesEvent() throws Exception {
        logger.info("Test from {}", this.getClass().getSimpleName());
        LoggerContext context = LoggerContext.getContext(false);
        Configuration config = context.getConfiguration();
        MapAppender appender = config.getAppender("MapAppender");
        Assert.assertEquals(appender.getEventMap().size(), 1);
    }
}

