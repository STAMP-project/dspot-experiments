package com.vip.saturn.job.utils;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class LogUtilsTest {
    private static LogUtilsTest.TestLogAppender testLogAppender = new LogUtilsTest.TestLogAppender();

    private static Logger log;

    @Test
    public void info() {
        LogUtils.info(LogUtilsTest.log, "event", "this is info");
        Assert.assertEquals("[event] msg=this is info", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.info(LogUtilsTest.log, "event", "this is info {}", "arg1");
        Assert.assertEquals("[event] msg=this is info arg1", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.info(LogUtilsTest.log, "event", "this is info {} {}", "arg1", "arg2");
        Assert.assertEquals("[event] msg=this is info arg1 arg2", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.info(LogUtilsTest.log, "event", "this is info {} {}", "arg1", "arg2", new ClassNotFoundException("com.abc"));
        Assert.assertEquals("[event] msg=this is info arg1 arg2", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.abc", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
        LogUtils.info(LogUtilsTest.log, "event", "this is info {}", "arg1", new Error("com.def"));
        Assert.assertEquals("[event] msg=this is info arg1", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.def", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
        LogUtils.info(LogUtilsTest.log, "event", "this is info", new Exception("com.def"));
        Assert.assertEquals("[event] msg=this is info", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.def", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
    }

    @Test
    public void debug() {
        LogUtils.debug(LogUtilsTest.log, "event", "this is debug");
        Assert.assertEquals("[event] msg=this is debug", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.debug(LogUtilsTest.log, "event", "this is debug {}", "arg1");
        Assert.assertEquals("[event] msg=this is debug arg1", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.debug(LogUtilsTest.log, "event", "this is debug {} {}", "arg1", "arg2");
        Assert.assertEquals("[event] msg=this is debug arg1 arg2", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.debug(LogUtilsTest.log, "event", "this is debug {} {}", "arg1", "arg2", new ClassNotFoundException("com.abc"));
        Assert.assertEquals("[event] msg=this is debug arg1 arg2", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.abc", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
        LogUtils.debug(LogUtilsTest.log, "event", "this is debug {}", "arg1", new Error("com.def"));
        Assert.assertEquals("[event] msg=this is debug arg1", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.def", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
        LogUtils.debug(LogUtilsTest.log, "event", "this is debug", new Exception("com.def"));
        Assert.assertEquals("[event] msg=this is debug", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.def", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
    }

    @Test
    public void error() {
        LogUtils.error(LogUtilsTest.log, "event", "this is error");
        Assert.assertEquals("[event] msg=this is error", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.error(LogUtilsTest.log, "event", "this is error {}", "arg1");
        Assert.assertEquals("[event] msg=this is error arg1", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.error(LogUtilsTest.log, "event", "this is error {} {}", "arg1", "arg2");
        Assert.assertEquals("[event] msg=this is error arg1 arg2", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.error(LogUtilsTest.log, "event", "this is error {} {}", "arg1", "arg2", new ClassNotFoundException("com.abc"));
        Assert.assertEquals("[event] msg=this is error arg1 arg2", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.abc", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
        LogUtils.error(LogUtilsTest.log, "event", "this is error {}", "arg1", new Error("com.def"));
        Assert.assertEquals("[event] msg=this is error arg1", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.def", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
        LogUtils.error(LogUtilsTest.log, "event", "this is error", new Exception("com.def"));
        Assert.assertEquals("[event] msg=this is error", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.def", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
    }

    @Test
    public void warn() {
        LogUtils.warn(LogUtilsTest.log, "event", "this is warn");
        Assert.assertEquals("[event] msg=this is warn", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.warn(LogUtilsTest.log, "event", "this is warn {}", "arg1");
        Assert.assertEquals("[event] msg=this is warn arg1", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.warn(LogUtilsTest.log, "event", "this is warn {} {}", "arg1", "arg2");
        Assert.assertEquals("[event] msg=this is warn arg1 arg2", LogUtilsTest.testLogAppender.getLastMessage());
        LogUtils.warn(LogUtilsTest.log, "event", "this is warn {} {}", "arg1", "arg2", new ClassNotFoundException("com.abc"));
        Assert.assertEquals("[event] msg=this is warn arg1 arg2", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.abc", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
        LogUtils.warn(LogUtilsTest.log, "event", "this is warn {}", "arg1", new Error("com.def"));
        Assert.assertEquals("[event] msg=this is warn arg1", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.def", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
        LogUtils.warn(LogUtilsTest.log, "event", "this is warn", new Exception("com.def"));
        Assert.assertEquals("[event] msg=this is warn", LogUtilsTest.testLogAppender.getLastMessage());
        Assert.assertEquals("com.def", LogUtilsTest.testLogAppender.getLastEvent().getThrowableProxy().getMessage());
    }

    public static final class TestLogAppender extends AppenderBase<ILoggingEvent> {
        private List<ILoggingEvent> events = Lists.newArrayList();

        private List<String> messages = Lists.newArrayList();

        @Override
        public synchronized void doAppend(ILoggingEvent eventObject) {
            super.doAppend(eventObject);
            events.add(eventObject);
            messages.add(eventObject.getFormattedMessage());
        }

        @Override
        protected void append(ILoggingEvent eventObject) {
            events.add(eventObject);
            messages.add(eventObject.getFormattedMessage());
        }

        public void clear() {
            events.clear();
            messages.clear();
        }

        public ILoggingEvent getLastEvent() {
            if ((events.size()) == 0) {
                return null;
            }
            return events.get(((events.size()) - 1));
        }

        public List<ILoggingEvent> getEvents() {
            return events;
        }

        public List<String> getMessages() {
            return messages;
        }

        public String getLastMessage() {
            if ((messages.size()) == 0) {
                return null;
            }
            return messages.get(((messages.size()) - 1));
        }
    }
}

