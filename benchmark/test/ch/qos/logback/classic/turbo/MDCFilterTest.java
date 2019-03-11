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


import FilterReply.ACCEPT;
import FilterReply.DENY;
import FilterReply.NEUTRAL;
import ch.qos.logback.core.testUtil.RandomUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.MDC;


public class MDCFilterTest {
    int diff = RandomUtil.getPositiveInt();

    String key = "myKey" + (diff);

    String value = "val" + (diff);

    private MDCFilter filter;

    @Test
    public void smoke() {
        filter.start();
        MDC.put(key, ("other" + (diff)));
        Assert.assertEquals(DENY, filter.decide(null, null, null, null, null, null));
        MDC.put(key, null);
        Assert.assertEquals(DENY, filter.decide(null, null, null, null, null, null));
        MDC.put(key, value);
        Assert.assertEquals(ACCEPT, filter.decide(null, null, null, null, null, null));
    }

    @Test
    public void testNoValueOption() {
        filter.setValue(null);
        filter.start();
        Assert.assertFalse(filter.isStarted());
        MDC.put(key, null);
        Assert.assertEquals(NEUTRAL, filter.decide(null, null, null, null, null, null));
        MDC.put(key, value);
        Assert.assertEquals(NEUTRAL, filter.decide(null, null, null, null, null, null));
    }

    @Test
    public void testNoMDCKeyOption() {
        filter.setMDCKey(null);
        filter.start();
        Assert.assertFalse(filter.isStarted());
        MDC.put(key, null);
        Assert.assertEquals(NEUTRAL, filter.decide(null, null, null, null, null, null));
        MDC.put(key, value);
        Assert.assertEquals(NEUTRAL, filter.decide(null, null, null, null, null, null));
    }
}

