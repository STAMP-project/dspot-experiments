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
package ch.qos.logback.access.sift;


import ch.qos.logback.access.jetty.JettyFixtureBase;
import ch.qos.logback.access.jetty.RequestLogImpl;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.util.StatusPrinter;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class SiftingAppenderTest {
    static final String PREFIX = "src/test/input/jetty/";

    static int RANDOM_SERVER_PORT = RandomUtil.getRandomServerPort();

    JettyFixtureBase jettyFixture;

    RequestLogImpl rli = new RequestLogImpl();

    @Test
    public void invokingDifferentPathShouldBeSiftedAccordingly() throws Exception {
        rli.setFileName(((SiftingAppenderTest.PREFIX) + "sifting.xml"));
        jettyFixture.start();
        invokeServer("/");
        invokeServer("/x");
        invokeServer("/x");
        invokeServer("/y");
        StatusPrinter.print(rli);
        SiftingAppender siftingAppender = ((SiftingAppender) (rli.getAppender("SIFTING")));
        Set<String> keySet = siftingAppender.getAppenderTracker().allKeys();
        Assert.assertEquals(3, keySet.size());
        Set<String> witnessSet = new LinkedHashSet<String>();
        witnessSet.add("NA");
        witnessSet.add("x");
        witnessSet.add("y");
        Assert.assertEquals(witnessSet, keySet);
        check(siftingAppender, "NA", 1);
        check(siftingAppender, "x", 2);
        check(siftingAppender, "y", 1);
    }
}

