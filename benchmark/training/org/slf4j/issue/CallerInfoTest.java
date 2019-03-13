package org.slf4j.issue;


import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.event.EventConstants;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.SubstituteLogger;
import org.slf4j.helpers.SubstituteServiceProvider;
import org.slf4j.jul.ListHandler;


public class CallerInfoTest {
    Level oldLevel;

    Logger root = Logger.getLogger("");

    ListHandler listHandler = new ListHandler();

    @Test
    public void testCallerInfo() {
        org.slf4j.Logger logger = LoggerFactory.getLogger("bla");
        logger.debug("hello");
        List<LogRecord> recordList = listHandler.recordList;
        Assert.assertEquals(1, recordList.size());
        LogRecord logRecod = recordList.get(0);
        Assert.assertEquals(CallerInfoTest.class.getName(), logRecod.getSourceClassName());
    }

    @Test
    public void testPostInitializationCallerInfoWithSubstituteLogger() {
        org.slf4j.Logger logger = LoggerFactory.getLogger("bla");
        SubstituteLogger substituteLogger = new SubstituteLogger("bla", null, false);
        substituteLogger.setDelegate(logger);
        substituteLogger.debug("hello");
        List<LogRecord> recordList = listHandler.recordList;
        Assert.assertEquals(1, recordList.size());
        LogRecord logRecod = recordList.get(0);
        Assert.assertEquals(CallerInfoTest.class.getName(), logRecod.getSourceClassName());
    }

    // In this case we KNOW that we CANNOT KNOW the caller
    @Test
    public void testIntraInitializationCallerInfoWithSubstituteLogger() throws InterruptedException {
        SubstituteServiceProvider substituteServiceProvider = new SubstituteServiceProvider();
        String loggerName = "bkla";
        substituteServiceProvider.getLoggerFactory().getLogger(loggerName);
        SubstituteLogger substituteLogger = substituteServiceProvider.getSubstituteLoggerFactory().getLoggers().get(0);
        Assert.assertEquals(loggerName, substituteLogger.getName());
        substituteLogger.debug("jello");
        org.slf4j.Logger logger = LoggerFactory.getLogger(loggerName);
        substituteLogger.setDelegate(logger);
        final LinkedBlockingQueue<SubstituteLoggingEvent> queue = substituteServiceProvider.getSubstituteLoggerFactory().getEventQueue();
        SubstituteLoggingEvent substituteLoggingEvent = queue.take();
        Assert.assertTrue(substituteLogger.isDelegateEventAware());
        substituteLogger.log(substituteLoggingEvent);
        List<LogRecord> recordList = listHandler.recordList;
        Assert.assertEquals(1, recordList.size());
        LogRecord logRecod = recordList.get(0);
        Assert.assertEquals(EventConstants.NA_SUBST, logRecod.getSourceClassName());
    }
}

