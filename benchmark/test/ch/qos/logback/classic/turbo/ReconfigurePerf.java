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


import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.contention.MultiThreadedHarness;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.File;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class ReconfigurePerf {
    static final int THREAD_COUNT = 500;

    // final static int LOOP_LEN = 1000 * 1000;
    // the space in the file name mandated by
    // http://jira.qos.ch/browse/LBCORE-119
    static final String CONF_FILE_AS_STR = (ClassicTestConstants.INPUT_PREFIX) + "turbo/scan_perf.xml";

    // it actually takes time for Windows to propagate file modification changes
    // values below 100 milliseconds can be problematic the same propagation
    // latency occurs in Linux but is even larger (>600 ms)
    static final int DEFAULT_SLEEP_BETWEEN_UPDATES = 110;

    int sleepBetweenUpdates = ReconfigurePerf.DEFAULT_SLEEP_BETWEEN_UPDATES;

    static int numberOfCycles = 100;

    static int totalTestDuration;

    LoggerContext loggerContext = new LoggerContext();

    Logger logger = loggerContext.getLogger(this.getClass());

    MultiThreadedHarness harness;

    // Tests whether ConfigurationAction is installing ReconfigureOnChangeFilter
    @Test
    public void scan1() throws JoranException, IOException, InterruptedException {
        File file = new File(ReconfigurePerf.CONF_FILE_AS_STR);
        configure(file);
        System.out.println("Running scan1()");
        doRun();
    }
}

