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


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.Assert;
import org.junit.Test;


public class LoggerComparatorTest {
    LoggerComparator comparator = new LoggerComparator();

    LoggerContext lc = new LoggerContext();

    Logger root = lc.getLogger("root");

    Logger a = lc.getLogger("a");

    Logger b = lc.getLogger("b");

    @Test
    public void testSmoke() {
        Assert.assertEquals(0, comparator.compare(a, a));
        Assert.assertEquals((-1), comparator.compare(a, b));
        Assert.assertEquals(1, comparator.compare(b, a));
        Assert.assertEquals((-1), comparator.compare(root, a));
        // following two tests failed before bug #127 was fixed
        Assert.assertEquals(1, comparator.compare(a, root));
        Assert.assertEquals(0, comparator.compare(root, root));
    }
}

