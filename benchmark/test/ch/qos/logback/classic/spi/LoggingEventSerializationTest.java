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
package ch.qos.logback.classic.spi;


import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;


public class LoggingEventSerializationTest {
    LoggerContext loggerContext;

    Logger logger;

    ByteArrayOutputStream bos;

    ObjectOutputStream oos;

    ObjectInputStream inputStream;

    PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();

    @Test
    public void smoke() throws Exception {
        ILoggingEvent event = createLoggingEvent();
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
    }

    @Test
    public void context() throws Exception {
        loggerContext.putProperty("testKey", "testValue");
        ILoggingEvent event = createLoggingEvent();
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
        Assert.assertNotNull(remoteEvent.getLoggerName());
        Assert.assertEquals(ROOT_LOGGER_NAME, remoteEvent.getLoggerName());
        LoggerContextVO loggerContextRemoteView = remoteEvent.getLoggerContextVO();
        Assert.assertNotNull(loggerContextRemoteView);
        Assert.assertEquals("testContext", loggerContextRemoteView.getName());
        Map<String, String> props = loggerContextRemoteView.getPropertyMap();
        Assert.assertNotNull(props);
        Assert.assertEquals("testValue", props.get("testKey"));
    }

    @Test
    public void MDC() throws Exception {
        MDC.put("key", "testValue");
        ILoggingEvent event = createLoggingEvent();
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
        Map<String, String> MDCPropertyMap = remoteEvent.getMDCPropertyMap();
        Assert.assertEquals("testValue", MDCPropertyMap.get("key"));
    }

    @Test
    public void updatedMDC() throws Exception {
        MDC.put("key", "testValue");
        ILoggingEvent event1 = createLoggingEvent();
        Serializable s1 = pst.transform(event1);
        oos.writeObject(s1);
        MDC.put("key", "updatedTestValue");
        ILoggingEvent event2 = createLoggingEvent();
        Serializable s2 = pst.transform(event2);
        oos.writeObject(s2);
        // create the input stream based on the ouput stream
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        inputStream = new ObjectInputStream(bis);
        // skip over one object
        inputStream.readObject();
        ILoggingEvent remoteEvent2 = ((ILoggingEvent) (inputStream.readObject()));
        // We observe the second logging event. It should provide us with
        // the updated MDC property.
        Map<String, String> MDCPropertyMap = remoteEvent2.getMDCPropertyMap();
        Assert.assertEquals("updatedTestValue", MDCPropertyMap.get("key"));
    }

    @Test
    public void nonSerializableParameters() throws Exception {
        LoggingEvent event = createLoggingEvent();
        LuckyCharms lucky0 = new LuckyCharms(0);
        event.setArgumentArray(new Object[]{ lucky0, null });
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
        Object[] aa = remoteEvent.getArgumentArray();
        Assert.assertNotNull(aa);
        Assert.assertEquals(2, aa.length);
        Assert.assertEquals("LC(0)", aa[0]);
        Assert.assertNull(aa[1]);
    }

    @Test
    public void testWithThrowable() throws Exception {
        Throwable throwable = new Throwable("just testing");
        LoggingEvent event = createLoggingEventWithThrowable(throwable);
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
    }

    @Test
    public void testWithMarker() throws Exception {
        Marker marker = MarkerFactory.getMarker("A_MARKER");
        LoggingEvent event = createLoggingEvent();
        event.setMarker(marker);
        Assert.assertNotNull(event.getMarker());
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
        Assert.assertNotNull(remoteEvent.getMarker());
        Assert.assertEquals(marker, remoteEvent.getMarker());
    }

    @Test
    public void testWithTwoMarkers() throws Exception {
        Marker marker = MarkerFactory.getMarker("A_MARKER");
        Marker marker2 = MarkerFactory.getMarker("B_MARKER");
        marker.add(marker2);
        LoggingEvent event = createLoggingEvent();
        event.setMarker(marker);
        Assert.assertNotNull(event.getMarker());
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
        Assert.assertNotNull(remoteEvent.getMarker());
        Assert.assertEquals(marker, remoteEvent.getMarker());
    }

    @Test
    public void testWithCallerData() throws Exception {
        LoggingEvent event = createLoggingEvent();
        event.getCallerData();
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
    }

    @Test
    public void extendendeThrowable() throws Exception {
        LoggingEvent event = createLoggingEvent();
        Throwable throwable = new Throwable("just testing");
        ThrowableProxy tp = new ThrowableProxy(throwable);
        event.setThrowableProxy(tp);
        tp.calculatePackagingData();
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
    }

    @Test
    public void serializeLargeArgs() throws Exception {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            buffer.append("X");
        }
        String largeString = buffer.toString();
        Object[] argArray = new Object[]{ new LuckyCharms(2), largeString };
        LoggingEvent event = createLoggingEvent();
        event.setArgumentArray(argArray);
        ILoggingEvent remoteEvent = writeAndRead(event);
        checkForEquality(event, remoteEvent);
        Object[] aa = remoteEvent.getArgumentArray();
        Assert.assertNotNull(aa);
        Assert.assertEquals(2, aa.length);
        String stringBack = ((String) (aa[1]));
        Assert.assertEquals(largeString, stringBack);
    }
}

