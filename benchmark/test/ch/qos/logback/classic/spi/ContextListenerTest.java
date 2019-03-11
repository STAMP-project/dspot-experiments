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


import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import ch.qos.logback.classic.LoggerContext;
import org.junit.Assert;
import org.junit.Test;

import static ch.qos.logback.classic.spi.BasicContextListener.UpdateType.RESET;
import static ch.qos.logback.classic.spi.BasicContextListener.UpdateType.START;
import static ch.qos.logback.classic.spi.BasicContextListener.UpdateType.STOP;


public class ContextListenerTest {
    LoggerContext context;

    BasicContextListener listener;

    @Test
    public void testNotifyOnReset() {
        context.reset();
        Assert.assertEquals(RESET, listener.updateType);
        Assert.assertEquals(listener.context, context);
    }

    @Test
    public void testNotifyOnStopResistant() {
        listener.setResetResistant(true);
        context.stop();
        Assert.assertEquals(STOP, listener.updateType);
        Assert.assertEquals(listener.context, context);
    }

    @Test
    public void testNotifyOnStopNotResistant() {
        context.stop();
        Assert.assertEquals(RESET, listener.updateType);
        Assert.assertEquals(listener.context, context);
    }

    @Test
    public void testNotifyOnStart() {
        context.start();
        Assert.assertEquals(START, listener.updateType);
        Assert.assertEquals(listener.context, context);
    }

    @Test
    public void testLevelChange() {
        checkLevelChange("a", INFO);
        checkLevelChange("a.b", ERROR);
        checkLevelChange("a.b.c", DEBUG);
    }
}

