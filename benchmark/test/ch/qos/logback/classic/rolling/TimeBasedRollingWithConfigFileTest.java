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
package ch.qos.logback.classic.rolling;


import CoreConstants.SIZE_AND_TIME_BASED_FNATP_IS_DEPRECATED;
import Logger.ROOT_LOGGER_NAME;
import Status.WARN;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.testUtil.ScaffoldingForRollingTests;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.StatusPrinter;
import org.junit.Assert;
import org.junit.Test;


public class TimeBasedRollingWithConfigFileTest extends ScaffoldingForRollingTests {
    LoggerContext lc = new LoggerContext();

    StatusChecker statusChecker = new StatusChecker(lc);

    Logger logger = lc.getLogger(this.getClass());

    int fileSize = 0;

    int fileIndexCounter = -1;

    int sizeThreshold;

    @Test
    public void basic() throws Exception {
        String testId = "basic";
        lc.putProperty("testId", testId);
        loadConfig(((((ClassicTestConstants.JORAN_INPUT_PREFIX) + "rolling/") + testId) + ".xml"));
        statusChecker.assertIsErrorFree();
        Logger root = lc.getLogger(ROOT_LOGGER_NAME);
        expectedFilenameList.add((((randomOutputDir) + "z") + testId));
        RollingFileAppender<ILoggingEvent> rfa = ((RollingFileAppender<ILoggingEvent>) (root.getAppender("ROLLING")));
        TimeBasedRollingPolicy<ILoggingEvent> tprp = ((TimeBasedRollingPolicy<ILoggingEvent>) (rfa.getTriggeringPolicy()));
        TimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> tbnatp = tprp.getTimeBasedFileNamingAndTriggeringPolicy();
        String prefix = "Hello---";
        int runLength = 4;
        for (int i = 0; i < runLength; i++) {
            logger.debug((prefix + i));
            addExpectedFileNamedIfItsTime_ByDate(randomOutputDir, testId, false);
            incCurrentTime(500);
            tbnatp.setCurrentTime(currentTime);
        }
        existenceCheck(expectedFilenameList);
        sortedContentCheck(randomOutputDir, runLength, prefix);
    }

    @Test
    public void depratedSizeAndTimeBasedFNATPWarning() throws Exception {
        String testId = "depratedSizeAndTimeBasedFNATPWarning";
        lc.putProperty("testId", testId);
        loadConfig(((((ClassicTestConstants.JORAN_INPUT_PREFIX) + "rolling/") + testId) + ".xml"));
        StatusPrinter.print(lc);
        statusChecker.assertContainsMatch(WARN, SIZE_AND_TIME_BASED_FNATP_IS_DEPRECATED);
    }

    @Test
    public void timeAndSize() throws Exception {
        String testId = "timeAndSize";
        lc.putProperty("testId", testId);
        String prefix = "Hello-----";
        // the number of times the log file will be written to before time based
        // roll-over occurs
        int approxWritesPerPeriod = 64;
        sizeThreshold = (prefix.length()) * approxWritesPerPeriod;
        lc.putProperty("sizeThreshold", ("" + (sizeThreshold)));
        loadConfig(((((ClassicTestConstants.JORAN_INPUT_PREFIX) + "rolling/") + testId) + ".xml"));
        StatusPrinter.print(lc);
        // Test http://jira.qos.ch/browse/LOGBACK-1236
        statusChecker.assertNoMatch(SIZE_AND_TIME_BASED_FNATP_IS_DEPRECATED);
        Logger root = lc.getLogger(ROOT_LOGGER_NAME);
        expectedFilenameList.add((((randomOutputDir) + "z") + testId));
        RollingFileAppender<ILoggingEvent> rfa = ((RollingFileAppender<ILoggingEvent>) (root.getAppender("ROLLING")));
        statusChecker.assertIsErrorFree();
        TimeBasedRollingPolicy<ILoggingEvent> tprp = ((TimeBasedRollingPolicy<ILoggingEvent>) (rfa.getTriggeringPolicy()));
        TimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> tbnatp = tprp.getTimeBasedFileNamingAndTriggeringPolicy();
        int timeIncrement = 1000 / approxWritesPerPeriod;
        int runLength = approxWritesPerPeriod * 3;
        for (int i = 0; i < runLength; i++) {
            String msg = prefix + i;
            logger.debug(msg);
            addExpectedFileNamedIfItsTime(testId, msg, false);
            incCurrentTime(timeIncrement);
            tbnatp.setCurrentTime(currentTime);
        }
        sortedContentCheck(randomOutputDir, runLength, prefix);
        int eCount = existenceCount(expectedFilenameList);
        // for various reasons, it is extremely difficult to have the files
        // match exactly the expected archive files. Thus, we aim for
        // an approximate match
        Assert.assertTrue(((("exitenceCount=" + eCount) + ", expectedFilenameList.size=") + (expectedFilenameList.size())), ((eCount >= 4) && (eCount > ((expectedFilenameList.size()) / 2))));
    }

    @Test
    public void timeAndSizeWithoutIntegerToken() throws Exception {
        String testId = "timeAndSizeWithoutIntegerToken";
        loadConfig(((((ClassicTestConstants.JORAN_INPUT_PREFIX) + "rolling/") + testId) + ".xml"));
        Logger root = lc.getLogger(ROOT_LOGGER_NAME);
        expectedFilenameList.add((((randomOutputDir) + "z") + testId));
        RollingFileAppender<ILoggingEvent> rfa = ((RollingFileAppender<ILoggingEvent>) (root.getAppender("ROLLING")));
        StatusPrinter.print(lc);
        statusChecker.assertContainsMatch("Missing integer token");
        Assert.assertFalse(rfa.isStarted());
    }

    // see also LOGBACK-1176
    @Test
    public void timeAndSizeWithoutMaxFileSize() throws Exception {
        String testId = "timeAndSizeWithoutMaxFileSize";
        loadConfig(((((ClassicTestConstants.JORAN_INPUT_PREFIX) + "rolling/") + testId) + ".xml"));
        Logger root = lc.getLogger(ROOT_LOGGER_NAME);
        // expectedFilenameList.add(randomOutputDir + "z" + testId);
        RollingFileAppender<ILoggingEvent> rfa = ((RollingFileAppender<ILoggingEvent>) (root.getAppender("ROLLING")));
        // statusChecker.assertContainsMatch("Missing integer token");
        Assert.assertFalse(rfa.isStarted());
        StatusPrinter.print(lc);
    }

    @Test
    public void totalSizeCapSmallerThanMaxFileSize() throws Exception {
        String testId = "totalSizeCapSmallerThanMaxFileSize";
        lc.putProperty("testId", testId);
        loadConfig(((((ClassicTestConstants.JORAN_INPUT_PREFIX) + "rolling/") + testId) + ".xml"));
        Logger root = lc.getLogger(ROOT_LOGGER_NAME);
        // expectedFilenameList.add(randomOutputDir + "z" + testId);
        RollingFileAppender<ILoggingEvent> rfa = ((RollingFileAppender<ILoggingEvent>) (root.getAppender("ROLLING")));
        statusChecker.assertContainsMatch("totalSizeCap of \\[\\d* \\w*\\] is smaller than maxFileSize \\[\\d* \\w*\\] which is non-sensical");
        Assert.assertFalse(rfa.isStarted());
    }
}

