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


package ch.qos.logback.core.joran.spi;


public class AmplNoAutoStartUtilTest {
    @org.junit.Test
    public void commonObject() {
        java.lang.Object o = new java.lang.Object();
        org.junit.Assert.assertTrue(ch.qos.logback.core.joran.spi.NoAutoStartUtil.notMarkedWithNoAutoStart(o));
    }

    @org.junit.Test
    public void markedWithNoAutoStart() {
        ch.qos.logback.core.joran.spi.DoNotAutoStart o = new ch.qos.logback.core.joran.spi.DoNotAutoStart();
        org.junit.Assert.assertFalse(ch.qos.logback.core.joran.spi.NoAutoStartUtil.notMarkedWithNoAutoStart(o));
    }
}

