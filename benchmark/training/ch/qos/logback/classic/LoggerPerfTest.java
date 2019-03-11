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
package ch.qos.logback.classic;


import Level.DEBUG;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.testUtil.EnvUtilForTests;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


// ============================================================
@Ignore
public class LoggerPerfTest {
    static final long NANOS_IN_ONE_SEC = (1000 * 1000) * 1000L;

    static long NORMAL_RUN_LENGTH = (1 * 1000) * 1000;

    static long SHORTENED_RUN_LENGTH = 500 * 1000;

    LoggerContext lc = new LoggerContext();

    Logger lbLogger = lc.getLogger(this.getClass());

    org.slf4j.Logger logger = lbLogger;

    // ===========================================================================
    @Test
    public void durationOfDisabledLogsWith_1_NOPFilter() {
        double avg = computeDurationOfDisabledLogsWith_1_NOPFilter(1, LoggerPerfTest.NORMAL_RUN_LENGTH);
        System.out.println(("durationOfDisabledLogsWith_1_NOPFilter=" + avg));
        @SuppressWarnings("unused")
        long referencePerf = 60;
        // BogoPerf.assertDuration(avg, referencePerf, CoreConstants.REFERENCE_BIPS);
    }

    // ===========================================================================
    @Test
    public void durationOfIsDebugEnabled() {
        double avg = computedurationOfIsDebugEnabled((10 * (LoggerPerfTest.NORMAL_RUN_LENGTH)));
        System.out.println(("durationOfIsDebugEnabled=" + avg));
        @SuppressWarnings("unused")
        long referencePerf = 15;
        // BogoPerf.assertDuration(avg, referencePerf, CoreConstants.REFERENCE_BIPS);
    }

    // ===========================================================================
    @Test
    public void durationOfDisabledLog_NoParameters() {
        double avg = computeDurationOfDisabledLog_NoParameters((10 * (LoggerPerfTest.NORMAL_RUN_LENGTH)));
        System.out.println(("durationOfDisabledLog_NoParameters=" + avg));
        @SuppressWarnings("unused")
        long referencePerf = 18;
        // BogoPerf.assertDuration(avg, referencePerf, CoreConstants.REFERENCE_BIPS);
    }

    // ===========================================================================
    @Test
    public void durationOfDisabledLog_1_Parameter() {
        double avgDuration = computeDurationOfDisabledLog_1_Parameter(LoggerPerfTest.NORMAL_RUN_LENGTH);
        System.out.println(("durationOfDisabledLog_1_Parameter=" + avgDuration));
        @SuppressWarnings("unused")
        long referencePerf = 30;
        // BogoPerf.assertDuration(avgDuration, referencePerf, CoreConstants.REFERENCE_BIPS);
    }

    // ===========================================================================
    @Test
    public void durationOfEnabledLog() {
        if (EnvUtilForTests.isLinux()) {
            // the JIT on Linux behaves very differently
            return;
        }
        double avgDuration = computeDurationOfEnabledLog(LoggerPerfTest.SHORTENED_RUN_LENGTH);
        System.out.println(("durationOfEnabledLog=" + avgDuration));
        @SuppressWarnings("unused")
        long referencePerf = 800;
        // BogoPerf.assertDuration(avgDuration, referencePerf, CoreConstants.REFERENCE_BIPS);
    }

    // ===========================================================================
    @Test
    public void testThreadedLogging() throws InterruptedException {
        LoggerPerfTest.SleepAppender<ILoggingEvent> appender = new LoggerPerfTest.SleepAppender<ILoggingEvent>();
        int MILLIS_PER_CALL = 250;
        int NANOS_PER_CALL = (250 * 1000) * 1000;
        appender.setDuration(MILLIS_PER_CALL);
        appender.start();
        lbLogger.addAppender(appender);
        lbLogger.setLevel(DEBUG);
        long start;
        long end;
        int threadCount = 10;
        int iterCount = 5;
        LoggerPerfTest.TestRunner[] threads = new LoggerPerfTest.TestRunner[threadCount];
        for (int i = 0; i < (threads.length); ++i) {
            threads[i] = new LoggerPerfTest.TestRunner(logger, iterCount);
        }
        start = System.nanoTime();
        for (Thread thread : threads) {
            thread.start();
        }
        for (LoggerPerfTest.TestRunner thread : threads) {
            thread.join();
        }
        end = System.nanoTime();
        double tolerance = threadCount * 0.125;// Very little thread contention

        // should occur in this test.
        double max = ((((double) (NANOS_PER_CALL)) / (LoggerPerfTest.NANOS_IN_ONE_SEC)) * iterCount) * tolerance;
        double serialized = ((((double) (NANOS_PER_CALL)) / (LoggerPerfTest.NANOS_IN_ONE_SEC)) * iterCount) * threadCount;
        double actual = ((double) (end - start)) / (LoggerPerfTest.NANOS_IN_ONE_SEC);
        System.out.printf("Sleep duration: %,.4f seconds. Max expected: %,.4f seconds, Serialized: %,.4f\n", actual, max, serialized);
        Assert.assertTrue("Exceeded maximum expected time.", (actual < max));
    }

    // ============================================================
    private static class TestRunner extends Thread {
        private org.slf4j.Logger logger;

        private long len;

        public TestRunner(org.slf4j.Logger logger, long len) {
            this.logger = logger;
            this.len = len;
        }

        public void run() {
            Thread.yield();
            for (long i = 0; i < (len); i++) {
                logger.debug("Toto");
            }
        }
    }

    // ============================================================
    public static class SleepAppender<E> extends UnsynchronizedAppenderBase<E> {
        private static long duration = 500;

        public void setDuration(long millis) {
            LoggerPerfTest.SleepAppender.duration = millis;
        }

        @Override
        protected void append(E eventObject) {
            try {
                Thread.sleep(LoggerPerfTest.SleepAppender.duration);
            } catch (InterruptedException ie) {
                // Ignore
            }
        }
    }
}

