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


import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.rolling.testUtil.ScaffoldingForRollingTests;
import ch.qos.logback.core.testUtil.StatusChecker;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


// @Test
// public void testHistoryAsFileCount() throws IOException {
// String testId = "testHistoryAsFileCount";
// int maxHistory = 10;
// initRollingFileAppender(rfa1, randomOutputDir + "~" + testId);
// sizeThreshold = 50;
// System.out.println("testHistoryAsFileCount started on "+new Date(currentTime));
// initPolicies(rfa1, tbrp1, randomOutputDir + testId + "-%d{" + DATE_PATTERN_WITH_SECONDS + "}-%i.txt",
// sizeThreshold, currentTime, 0, maxHistory, true);
// 
// incCurrentTime(100);
// tbrp1.timeBasedFileNamingAndTriggeringPolicy.setCurrentTime(currentTime);
// int runLength = 1000;
// 
// for (int i = 0; i < runLength; i++) {
// String msg = "" + i;
// rfa1.doAppend(msg);
// incCurrentTime(20);
// tbrp1.timeBasedFileNamingAndTriggeringPolicy.setCurrentTime(currentTime);
// add(tbrp1.future);
// }
// 
// Thread.yield();
// // wait for compression to finish
// waitForJobsToComplete();
// 
// assertEquals(maxHistory + 1, getFilesInDirectory(randomOutputDir).length);
// sortedContentCheck(randomOutputDir, 1000, "", 863);
// }
public class SizeAndTimeBasedFNATP_Test extends ScaffoldingForRollingTests {
    private SizeAndTimeBasedFNATP<Object> sizeAndTimeBasedFNATP = null;

    private RollingFileAppender<Object> rfa1 = new RollingFileAppender<Object>();

    private TimeBasedRollingPolicy<Object> tbrp1 = new TimeBasedRollingPolicy<Object>();

    private RollingFileAppender<Object> rfa2 = new RollingFileAppender<Object>();

    private TimeBasedRollingPolicy<Object> tbrp2 = new TimeBasedRollingPolicy<Object>();

    private EchoEncoder<Object> encoder = new EchoEncoder<Object>();

    int fileSize = 0;

    int fileIndexCounter = 0;

    int sizeThreshold = 0;

    static final boolean FIRST_PHASE_ONLY = false;

    static final boolean WITH_SECOND_PHASE = true;

    static final String DEFAULT_COMPRESSION_SUFFIX = "";

    @Test
    public void noCompression_FileSet_NoRestart_1() throws IOException, InterruptedException, ExecutionException {
        generic("test1", "toto.log", SizeAndTimeBasedFNATP_Test.FIRST_PHASE_ONLY, SizeAndTimeBasedFNATP_Test.DEFAULT_COMPRESSION_SUFFIX);
    }

    @Test
    public void noCompression_FileBlank_NoRestart_2() throws Exception {
        generic("test2", null, SizeAndTimeBasedFNATP_Test.FIRST_PHASE_ONLY, SizeAndTimeBasedFNATP_Test.DEFAULT_COMPRESSION_SUFFIX);
    }

    @Test
    public void noCompression_FileBlank_WithStopStart_3() throws Exception {
        generic("test3", null, SizeAndTimeBasedFNATP_Test.WITH_SECOND_PHASE, SizeAndTimeBasedFNATP_Test.DEFAULT_COMPRESSION_SUFFIX);
    }

    @Test
    public void noCompression_FileSet_WithStopStart_4() throws Exception {
        generic("test4", "test4.log", SizeAndTimeBasedFNATP_Test.WITH_SECOND_PHASE, SizeAndTimeBasedFNATP_Test.DEFAULT_COMPRESSION_SUFFIX);
    }

    @Test
    public void withGZCompression_FileSet_NoRestart_5() throws Exception {
        generic("test5", "toto.log", SizeAndTimeBasedFNATP_Test.FIRST_PHASE_ONLY, ".gz");
    }

    @Test
    public void withGZCompression_FileBlank_NoRestart_6() throws Exception {
        generic("test6", null, SizeAndTimeBasedFNATP_Test.FIRST_PHASE_ONLY, ".gz");
    }

    @Test
    public void withZipCompression_FileSet_NoRestart_7() throws Exception {
        generic("test7", "toto.log", SizeAndTimeBasedFNATP_Test.FIRST_PHASE_ONLY, ".zip");
        List<String> zipFiles = filterElementsInListBySuffix(".zip");
        checkZipEntryMatchesZipFilename(zipFiles);
    }

    @Test
    public void checkMissingIntToken() {
        String stem = "toto.log";
        String testId = "checkMissingIntToken";
        String compressionSuffix = "gz";
        String file = (stem != null) ? (randomOutputDir) + stem : null;
        initRollingFileAppender(rfa1, file);
        sizeThreshold = 300;
        initPolicies(rfa1, tbrp1, ((((((randomOutputDir) + testId) + "-%d{") + (ScaffoldingForRollingTests.DATE_PATTERN_WITH_SECONDS)) + "}.txt") + compressionSuffix), sizeThreshold, currentTime, 0);
        // StatusPrinter.print(context);
        Assert.assertFalse(rfa1.isStarted());
        StatusChecker checker = new StatusChecker(context);
        checker.assertContainsMatch("Missing integer token");
    }

    @Test
    public void checkDateCollision() {
        String stem = "toto.log";
        String testId = "checkDateCollision";
        String compressionSuffix = "gz";
        String file = (stem != null) ? (randomOutputDir) + stem : null;
        initRollingFileAppender(rfa1, file);
        sizeThreshold = 300;
        initPolicies(rfa1, tbrp1, ((((randomOutputDir) + testId) + "-%d{EE}.txt") + compressionSuffix), sizeThreshold, currentTime, 0);
        // StatusPrinter.print(context);
        Assert.assertFalse(rfa1.isStarted());
        StatusChecker checker = new StatusChecker(context);
        checker.assertContainsMatch("The date format in FileNamePattern");
    }
}

