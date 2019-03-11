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


import ch.qos.logback.core.joran.util.House;
import ch.qos.logback.core.joran.util.Window;
import org.junit.Assert;
import org.junit.Test;


public class DefaultNestedComponentRegistryTest {
    DefaultNestedComponentRegistry registry = new DefaultNestedComponentRegistry();

    @Test
    public void smoke() {
        String propertyName = "window";
        registry.add(House.class, propertyName, Window.class);
        Class<?> result = registry.findDefaultComponentType(House.class, propertyName);
        Assert.assertEquals(Window.class, result);
    }

    @Test
    public void absent() {
        registry.add(House.class, "a", Window.class);
        Class<?> result = registry.findDefaultComponentType(House.class, "other");
        Assert.assertNull(result);
    }
}

