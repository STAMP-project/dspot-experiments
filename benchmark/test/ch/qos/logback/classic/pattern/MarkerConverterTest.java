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
package ch.qos.logback.classic.pattern;


import ch.qos.logback.classic.LoggerContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;


public class MarkerConverterTest {
    LoggerContext lc;

    MarkerConverter converter;

    // use a different facotry for each test so that they are independent
    IMarkerFactory markerFactory = new BasicMarkerFactory();

    @Test
    public void testWithNullMarker() {
        String result = converter.convert(createLoggingEvent(null));
        Assert.assertEquals("", result);
    }

    @Test
    public void testWithMarker() {
        String name = "test";
        Marker marker = markerFactory.getMarker(name);
        String result = converter.convert(createLoggingEvent(marker));
        Assert.assertEquals(name, result);
    }

    @Test
    public void testWithOneChildMarker() {
        Marker marker = markerFactory.getMarker("test");
        marker.add(markerFactory.getMarker("child"));
        String result = converter.convert(createLoggingEvent(marker));
        Assert.assertEquals("test [ child ]", result);
    }

    @Test
    public void testWithSeveralChildMarker() {
        Marker marker = markerFactory.getMarker("testParent");
        marker.add(markerFactory.getMarker("child1"));
        marker.add(markerFactory.getMarker("child2"));
        marker.add(markerFactory.getMarker("child3"));
        String result = converter.convert(createLoggingEvent(marker));
        Assert.assertEquals("testParent [ child1, child2, child3 ]", result);
    }
}

