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
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;


public class MarkerFilterTest {
    static String TOTO = "TOTO";

    static String COMPOSITE = "COMPOSITE";

    Marker totoMarker = MarkerFactory.getMarker(MarkerFilterTest.TOTO);

    @Test
    public void testNoMarker() {
        MarkerFilter mkt = new MarkerFilter();
        mkt.start();
        Assert.assertFalse(mkt.isStarted());
        Assert.assertEquals(NEUTRAL, mkt.decide(totoMarker, null, null, null, null, null));
        Assert.assertEquals(NEUTRAL, mkt.decide(null, null, null, null, null, null));
    }

    @Test
    public void testBasic() {
        MarkerFilter mkt = new MarkerFilter();
        mkt.setMarker(MarkerFilterTest.TOTO);
        mkt.setOnMatch("ACCEPT");
        mkt.setOnMismatch("DENY");
        mkt.start();
        Assert.assertTrue(mkt.isStarted());
        Assert.assertEquals(DENY, mkt.decide(null, null, null, null, null, null));
        Assert.assertEquals(ACCEPT, mkt.decide(totoMarker, null, null, null, null, null));
    }

    @Test
    public void testComposite() {
        String compositeMarkerName = MarkerFilterTest.COMPOSITE;
        Marker compositeMarker = MarkerFactory.getMarker(compositeMarkerName);
        compositeMarker.add(totoMarker);
        MarkerFilter mkt = new MarkerFilter();
        mkt.setMarker(MarkerFilterTest.TOTO);
        mkt.setOnMatch("ACCEPT");
        mkt.setOnMismatch("DENY");
        mkt.start();
        Assert.assertTrue(mkt.isStarted());
        Assert.assertEquals(DENY, mkt.decide(null, null, null, null, null, null));
        Assert.assertEquals(ACCEPT, mkt.decide(totoMarker, null, null, null, null, null));
        Assert.assertEquals(ACCEPT, mkt.decide(compositeMarker, null, null, null, null, null));
    }
}

