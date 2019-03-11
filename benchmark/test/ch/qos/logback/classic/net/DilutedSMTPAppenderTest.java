/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.net;


import Level.DEBUG;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.spi.CyclicBufferTracker;
import org.junit.Assert;
import org.junit.Test;


public class DilutedSMTPAppenderTest {
    SMTPAppender appender;

    CyclicBufferTracker<ILoggingEvent> cbTracker;

    CyclicBuffer<ILoggingEvent> cb;

    @Test
    public void testStart() {
        Assert.assertEquals("sebastien.nospam@qos.ch%nopex", appender.getToAsListOfString().get(0));
        Assert.assertEquals("logging report", appender.getSubject());
        Assert.assertTrue(appender.isStarted());
    }

    @Test
    public void testAppendNonTriggeringEvent() {
        LoggingEvent event = new LoggingEvent();
        event.setThreadName("thead name");
        event.setLevel(DEBUG);
        appender.subAppend(cb, event);
        Assert.assertEquals(1, cb.length());
    }

    @Test
    public void testEntryConditionsCheck() {
        appender.checkEntryConditions();
        Assert.assertEquals(0, appender.getContext().getStatusManager().getCount());
    }

    @Test
    public void testTriggeringPolicy() {
        appender.setEvaluator(null);
        appender.checkEntryConditions();
        Assert.assertEquals(1, appender.getContext().getStatusManager().getCount());
    }

    @Test
    public void testEntryConditionsCheckNoLayout() {
        appender.setLayout(null);
        appender.checkEntryConditions();
        Assert.assertEquals(1, appender.getContext().getStatusManager().getCount());
    }
}

