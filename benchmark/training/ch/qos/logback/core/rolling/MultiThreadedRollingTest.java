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
package ch.qos.logback.core.rolling;


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.contention.RunnableWithCounterAndDone;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.EnvUtilForTests;
import ch.qos.logback.core.testUtil.RandomUtil;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Test;


public class MultiThreadedRollingTest {
    static final int NUM_THREADS = 10;

    static final int TOTAL_DURATION = 600;

    RunnableWithCounterAndDone[] runnableArray;

    Encoder<Object> encoder;

    Context context = new ContextBase();

    static String VERIFY_SH = "verify.sh";

    int diff = RandomUtil.getPositiveInt();

    String outputDirStr = (((CoreTestConstants.OUTPUT_DIR_PREFIX) + "multi-") + (diff)) + "/";

    RollingFileAppender<Object> rfa = new RollingFileAppender<Object>();

    String pathToBash = EnvUtilForTests.getPathToBash();

    OutputStream scriptOS;

    @Test
    public void multiThreadedTimedBased() throws IOException, InterruptedException {
        setUpTimeBasedTriggeringPolicy(rfa);
        executeHarness(MultiThreadedRollingTest.TOTAL_DURATION, false);
        printScriptForTimeBased();
        verify();
    }

    @Test
    public void multiThreadedSizeBased() throws IOException, InterruptedException {
        setUpSizeBasedTriggeringPolicy(rfa);
        // on a fast machine with a fast hard disk, if the tests runs for too
        // long the MAX_WINDOW_SIZE is reached, resulting in data loss which
        // we cannot test for.
        executeHarness(MultiThreadedRollingTest.TOTAL_DURATION, true);
        int numFiles = testFileCount();
        printScriptForSizeBased(numFiles);
        verify();
    }

    static class RFARunnable extends RunnableWithCounterAndDone {
        RollingFileAppender<Object> rfa;

        int id;

        boolean withInducedDelay;

        RFARunnable(int id, RollingFileAppender<Object> rfa, boolean withInducedDelay) {
            this.id = id;
            this.rfa = rfa;
            this.withInducedDelay = withInducedDelay;
        }

        public void run() {
            while (!(isDone())) {
                (counter)++;
                rfa.doAppend((((id) + " ") + (counter)));
                if ((((counter) % 64) == 0) && (withInducedDelay)) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            } 
        }
    }
}

