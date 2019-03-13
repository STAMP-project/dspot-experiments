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
package ch.qos.logback.classic.issue.logback416;


import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.contention.MultiThreadedHarness;
import ch.qos.logback.core.contention.RunnableWithCounterAndDone;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentSiftingTest {
    static final int THREAD_COUNT = 5;

    static String FOLDER_PREFIX = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "sift/";

    LoggerContext loggerContext = new LoggerContext();

    protected Logger logger = loggerContext.getLogger(this.getClass().getName());

    protected Logger root = loggerContext.getLogger(ROOT_LOGGER_NAME);

    int totalTestDuration = 50;

    MultiThreadedHarness harness = new MultiThreadedHarness(totalTestDuration);

    RunnableWithCounterAndDone[] runnableArray = buildRunnableArray();

    @Test
    public void concurrentAccess() throws JoranException, InterruptedException {
        configure(((ConcurrentSiftingTest.FOLDER_PREFIX) + "logback_416.xml"));
        harness.execute(runnableArray);
        Assert.assertEquals(1, InstanceCountingAppender.INSTANCE_COUNT.get());
    }
}

