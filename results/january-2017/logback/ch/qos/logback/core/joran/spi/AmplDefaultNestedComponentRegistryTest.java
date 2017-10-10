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


public class AmplDefaultNestedComponentRegistryTest {
    ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry registry = new ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry();

    @org.junit.Before
    public void setUp() throws java.lang.Exception {
    }

    @org.junit.After
    public void tearDown() throws java.lang.Exception {
    }

    @org.junit.Test
    public void smoke() {
        java.lang.String propertyName = "window";
        registry.add(ch.qos.logback.core.joran.util.House.class, propertyName, java.awt.Window.class);
        java.lang.Class<?> result = registry.findDefaultComponentType(ch.qos.logback.core.joran.util.House.class, propertyName);
        org.junit.Assert.assertEquals(java.awt.Window.class, result);
    }

    @org.junit.Test
    public void absent() {
        registry.add(ch.qos.logback.core.joran.util.House.class, "a", java.awt.Window.class);
        java.lang.Class<?> result = registry.findDefaultComponentType(ch.qos.logback.core.joran.util.House.class, "other");
        org.junit.Assert.assertNull(result);
    }
}

