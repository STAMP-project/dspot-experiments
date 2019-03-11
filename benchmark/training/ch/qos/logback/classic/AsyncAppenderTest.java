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
package ch.qos.logback.classic;


import ch.qos.logback.classic.net.testObjectBuilders.LoggingEventBuilderInContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.testUtil.RandomUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.MDC;


/**
 *
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Torsten Juergeleit
 */
public class AsyncAppenderTest {
    String thisClassName = this.getClass().getName();

    LoggerContext context = new LoggerContext();

    AsyncAppender asyncAppender = new AsyncAppender();

    ListAppender<ILoggingEvent> listAppender = new ListAppender<ILoggingEvent>();

    OnConsoleStatusListener onConsoleStatusListener = new OnConsoleStatusListener();

    LoggingEventBuilderInContext builder = new LoggingEventBuilderInContext(context, thisClassName, UnsynchronizedAppenderBase.class.getName());

    int diff = RandomUtil.getPositiveInt();

    @Test
    public void eventWasPreparedForDeferredProcessing() {
        asyncAppender.addAppender(listAppender);
        asyncAppender.start();
        String k = "k" + (diff);
        MDC.put(k, "v");
        asyncAppender.doAppend(builder.build(diff));
        MDC.clear();
        asyncAppender.stop();
        Assert.assertFalse(asyncAppender.isStarted());
        // check the event
        Assert.assertEquals(1, listAppender.list.size());
        ILoggingEvent e = listAppender.list.get(0);
        // check that MDC values were correctly retained
        Assert.assertEquals("v", e.getMDCPropertyMap().get(k));
        Assert.assertFalse(e.hasCallerData());
    }

    @Test
    public void settingIncludeCallerDataPropertyCausedCallerDataToBeIncluded() {
        asyncAppender.addAppender(listAppender);
        asyncAppender.setIncludeCallerData(true);
        asyncAppender.start();
        asyncAppender.doAppend(builder.build(diff));
        asyncAppender.stop();
        // check the event
        Assert.assertEquals(1, listAppender.list.size());
        ILoggingEvent e = listAppender.list.get(0);
        Assert.assertTrue(e.hasCallerData());
        StackTraceElement ste = e.getCallerData()[0];
        Assert.assertEquals(thisClassName, ste.getClassName());
    }
}

