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
package ch.qos.logback.classic.turbo;


import FilterReply.DENY;
import FilterReply.NEUTRAL;
import org.junit.Assert;
import org.junit.Test;


public class DuplicateMessageFilterTest {
    @Test
    public void smoke() {
        DuplicateMessageFilter dmf = new DuplicateMessageFilter();
        dmf.setAllowedRepetitions(0);
        dmf.start();
        Assert.assertEquals(NEUTRAL, dmf.decide(null, null, null, "x", null, null));
        Assert.assertEquals(NEUTRAL, dmf.decide(null, null, null, "y", null, null));
        Assert.assertEquals(DENY, dmf.decide(null, null, null, "x", null, null));
        Assert.assertEquals(DENY, dmf.decide(null, null, null, "y", null, null));
    }

    @Test
    public void memoryLoss() {
        DuplicateMessageFilter dmf = new DuplicateMessageFilter();
        dmf.setAllowedRepetitions(1);
        dmf.setCacheSize(1);
        dmf.start();
        Assert.assertEquals(NEUTRAL, dmf.decide(null, null, null, "a", null, null));
        Assert.assertEquals(NEUTRAL, dmf.decide(null, null, null, "b", null, null));
        Assert.assertEquals(NEUTRAL, dmf.decide(null, null, null, "a", null, null));
    }

    @Test
    public void many() {
        DuplicateMessageFilter dmf = new DuplicateMessageFilter();
        dmf.setAllowedRepetitions(0);
        int cacheSize = 10;
        int margin = 2;
        dmf.setCacheSize(cacheSize);
        dmf.start();
        for (int i = 0; i < (cacheSize + margin); i++) {
            Assert.assertEquals(NEUTRAL, dmf.decide(null, null, null, ("a" + i), null, null));
        }
        for (int i = cacheSize - 1; i >= margin; i--) {
            Assert.assertEquals(DENY, dmf.decide(null, null, null, ("a" + i), null, null));
        }
        for (int i = margin - 1; i >= 0; i--) {
            Assert.assertEquals(NEUTRAL, dmf.decide(null, null, null, ("a" + i), null, null));
        }
    }

    // isXXXEnabled invokes decide with a null format
    // http://jira.qos.ch/browse/LBCLASSIC-134
    @Test
    public void nullFormat() {
        DuplicateMessageFilter dmf = new DuplicateMessageFilter();
        dmf.setAllowedRepetitions(0);
        dmf.setCacheSize(10);
        dmf.start();
        Assert.assertEquals(NEUTRAL, dmf.decide(null, null, null, null, null, null));
        Assert.assertEquals(NEUTRAL, dmf.decide(null, null, null, null, null, null));
    }
}

