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


import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.rolling.testUtil.ScaffoldingForRollingTests;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.FixedRateInvocationGate;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TimeBasedRollingWithArchiveRemoval_Test extends ScaffoldingForRollingTests {
    String MONTHLY_DATE_PATTERN = "yyyy-MM";

    String MONTHLY_CRONOLOG_DATE_PATTERN = "yyyy/MM";

    final String DAILY_CRONOLOG_DATE_PATTERN = "yyyy/MM/dd";

    RollingFileAppender<Object> rfa = new RollingFileAppender<Object>();

    TimeBasedRollingPolicy<Object> tbrp = new TimeBasedRollingPolicy<Object>();

    // by default tbfnatp is an instance of DefaultTimeBasedFileNamingAndTriggeringPolicy
    TimeBasedFileNamingAndTriggeringPolicy<Object> tbfnatp = new DefaultTimeBasedFileNamingAndTriggeringPolicy<Object>();

    StatusChecker checker = new StatusChecker(context);

    static long MILLIS_IN_MINUTE = 60 * 1000;

    static long MILLIS_IN_HOUR = 60 * (TimeBasedRollingWithArchiveRemoval_Test.MILLIS_IN_MINUTE);

    static long MILLIS_IN_DAY = 24 * (TimeBasedRollingWithArchiveRemoval_Test.MILLIS_IN_HOUR);

    static long MILLIS_IN_MONTH = ((long) ((365.242199 / 12) * (TimeBasedRollingWithArchiveRemoval_Test.MILLIS_IN_DAY)));

    static int MONTHS_IN_YEAR = 12;

    // Wed Mar 23 23:07:05 CET 2016
    static final long WED_2016_03_23_T_230705_CET = 1458770825333L;

    static final long THU_2016_03_17_T_230330_CET = 1458252210975L;

    int slashCount = 0;

    int ticksPerPeriod = 216;

    ConfigParameters cp;// initialized in setup


    FixedRateInvocationGate fixedRateInvocationGate = new FixedRateInvocationGate(((ticksPerPeriod) / 2));

    // test that the number of files at the end of the test is same as the expected number taking into account end dates
    // near the beginning of a new year. This test has been run in a loop with start date varying over a two years
    // with success.
    @Test
    public void monthlyRolloverOverManyPeriods() {
        this.slashCount = computeSlashCount(MONTHLY_CRONOLOG_DATE_PATTERN);
        int maxHistory = 2;
        int simulatedNumberOfPeriods = 30;
        String fileNamePattern = (((randomOutputDir) + "/%d{") + (MONTHLY_CRONOLOG_DATE_PATTERN)) + "}/clean.txt.zip";
        cp.maxHistory(maxHistory).fileNamePattern(fileNamePattern).simulatedNumberOfPeriods(simulatedNumberOfPeriods).periodDurationInMillis(TimeBasedRollingWithArchiveRemoval_Test.MILLIS_IN_MONTH);
        long startTime = currentTime;
        long endTime = logOverMultiplePeriods(cp);
        System.out.println(("randomOutputDir:" + (randomOutputDir)));
        System.out.println(((("start:" + startTime) + ", end=") + endTime));
        int differenceInMonths = RollingCalendar.diffInMonths(startTime, endTime);
        System.out.println(("differenceInMonths:" + differenceInMonths));
        Calendar startTimeAsCalendar = Calendar.getInstance();
        startTimeAsCalendar.setTimeInMillis(startTime);
        int indexOfStartPeriod = startTimeAsCalendar.get(Calendar.MONTH);
        boolean withExtraFolder = extraFolder(differenceInMonths, TimeBasedRollingWithArchiveRemoval_Test.MONTHS_IN_YEAR, indexOfStartPeriod, maxHistory);
        checkFileCount(expectedCountWithFolders(maxHistory, withExtraFolder));
    }

    @Test
    public void checkCrossedPeriodsWithDSTBarrier() {
        long SAT_2016_03_26_T_230705_CET = (TimeBasedRollingWithArchiveRemoval_Test.WED_2016_03_23_T_230705_CET) + (3 * (CoreConstants.MILLIS_IN_ONE_DAY));
        System.out.println(("SAT_2016_03_26_T_230705_CET " + (new Date(SAT_2016_03_26_T_230705_CET))));
        long MON_2016_03_28_T_000705_CET = SAT_2016_03_26_T_230705_CET + (CoreConstants.MILLIS_IN_ONE_DAY);
        System.out.println(("MON_2016_03_28_T_000705_CET " + (new Date(MON_2016_03_28_T_000705_CET))));
        int result = computeCrossedDayBarriers(SAT_2016_03_26_T_230705_CET, MON_2016_03_28_T_000705_CET, "CET");
        Assert.assertEquals(2, result);
    }

    @Test
    public void checkCleanupForBasicDailyRollover() {
        cp.maxHistory(20).simulatedNumberOfPeriods((20 * 3)).startInactivity(0).numInactivityPeriods(0);
        generateDailyRolloverAndCheckFileCount(cp);
    }

    @Test
    public void checkCleanupForBasicDailyRolloverWithSizeCap() {
        long bytesOutputPerPeriod = 15984;
        int sizeInUnitsOfBytesPerPeriod = 2;
        cp.maxHistory(5).simulatedNumberOfPeriods(10).sizeCap(((sizeInUnitsOfBytesPerPeriod * bytesOutputPerPeriod) + 1000));
        generateDailyRollover(cp);
        StatusPrinter.print(context);
        checkFileCount((sizeInUnitsOfBytesPerPeriod + 1));
    }

    @Test
    public void checkThatSmallTotalSizeCapLeavesAtLeastOneArhcive() {
        long WED_2016_03_23_T_131345_CET = (TimeBasedRollingWithArchiveRemoval_Test.WED_2016_03_23_T_230705_CET) - (10 * (CoreConstants.MILLIS_IN_ONE_HOUR));
        // long bytesOutputPerPeriod = 15984;
        cp = new ConfigParameters(WED_2016_03_23_T_131345_CET);
        final int verySmallCapSize = 1;
        cp.maxHistory(5).simulatedNumberOfPeriods(3).sizeCap(verySmallCapSize);
        generateDailyRollover(cp);
        StatusPrinter.print(context);
        checkFileCountAtMost(1);
    }

    @Test
    public void checkCleanupForBasicDailyRolloverWithMaxSize() {
        cp.maxHistory(6).simulatedNumberOfPeriods(30).startInactivity(10).numInactivityPeriods(1);
        generateDailyRolloverAndCheckFileCount(cp);
    }

    // Since the duration of a month (in seconds) varies from month to month, tests with inactivity period must
    // be conducted with daily rollover not monthly
    @Test
    public void checkCleanupForDailyRollover_15Periods() {
        cp.maxHistory(5).simulatedNumberOfPeriods(15).startInactivity(6).numInactivityPeriods(3);
        generateDailyRolloverAndCheckFileCount(cp);
    }

    @Test
    public void checkCleanupForDailyRolloverWithInactivity_30Periods() {
        // / -------
        cp.maxHistory(2).simulatedNumberOfPeriods(30).startInactivity(3).numInactivityPeriods(1);
        generateDailyRolloverAndCheckFileCount(cp);
    }

    @Test
    public void checkCleanupForDailyRolloverWithInactivity_10Periods() {
        this.currentTime = TimeBasedRollingWithArchiveRemoval_Test.THU_2016_03_17_T_230330_CET;
        cp.maxHistory(6).simulatedNumberOfPeriods(10).startInactivity(2).numInactivityPeriods(2);
        generateDailyRolloverAndCheckFileCount(cp);
    }

    @Test
    public void checkCleanupForDailyRolloverWithSecondPhase() {
        slashCount = computeSlashCount(CoreConstants.DAILY_DATE_PATTERN);
        int maxHistory = 5;
        String fileNamePattern = (((randomOutputDir) + "clean-%d{") + (CoreConstants.DAILY_DATE_PATTERN)) + "}.txt";
        ConfigParameters cp0 = new ConfigParameters(currentTime).maxHistory(maxHistory).fileNamePattern(fileNamePattern).simulatedNumberOfPeriods((maxHistory * 2));
        long endTime = logOverMultiplePeriods(cp0);
        ConfigParameters cp1 = new ConfigParameters((endTime + ((TimeBasedRollingWithArchiveRemoval_Test.MILLIS_IN_DAY) * 10))).maxHistory(maxHistory).fileNamePattern(fileNamePattern).simulatedNumberOfPeriods(maxHistory);
        logOverMultiplePeriods(cp1);
        checkFileCount(expectedCountWithoutFolders(maxHistory));
    }

    @Test
    public void dailyRolloverWithCronologPattern() {
        this.slashCount = computeSlashCount(DAILY_CRONOLOG_DATE_PATTERN);
        String fileNamePattern = (((randomOutputDir) + "/%d{") + (DAILY_CRONOLOG_DATE_PATTERN)) + "}/clean.txt.zip";
        cp.maxHistory(8).fileNamePattern(fileNamePattern).simulatedNumberOfPeriods((8 * 3));
        logOverMultiplePeriods(cp);
        int expectedDirMin = 9 + (slashCount);
        int expectDirMax = (expectedDirMin + 1) + 1;
        expectedFileAndDirCount(9, expectedDirMin, expectDirMax);
    }

    @Test
    public void dailySizeBasedRolloverWithoutCap() {
        SizeAndTimeBasedFNATP<Object> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP<Object>();
        sizeAndTimeBasedFNATP.invocationGate = fixedRateInvocationGate;
        sizeAndTimeBasedFNATP.setMaxFileSize(new FileSize(10000));
        tbfnatp = sizeAndTimeBasedFNATP;
        this.slashCount = computeSlashCount(CoreConstants.DAILY_DATE_PATTERN);
        String fileNamePattern = (((randomOutputDir) + "/%d{") + (CoreConstants.DAILY_DATE_PATTERN)) + "}-clean.%i.zip";
        cp.maxHistory(5).fileNamePattern(fileNamePattern).simulatedNumberOfPeriods((5 * 4));
        logOverMultiplePeriods(cp);
        checkPatternCompliance(((5 + 1) + (slashCount)), "\\d{4}-\\d{2}-\\d{2}-clean(\\.\\d)(.zip)?");
    }

    @Test
    public void dailySizeBasedRolloverWithSizeCap() {
        SizeAndTimeBasedFNATP<Object> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP<Object>();
        sizeAndTimeBasedFNATP.invocationGate = new FixedRateInvocationGate(((ticksPerPeriod) / 8));
        long bytesPerPeriod = 17000;
        long fileSize = bytesPerPeriod / 5;
        int expectedFileCount = 10;
        long sizeCap = expectedFileCount * fileSize;
        sizeAndTimeBasedFNATP.setMaxFileSize(new FileSize(fileSize));
        tbfnatp = sizeAndTimeBasedFNATP;
        this.slashCount = computeSlashCount(CoreConstants.DAILY_DATE_PATTERN);
        // 2016-03-05 00:14:39 CET
        long simulatedTime = 1457133279186L;
        ConfigParameters params = new ConfigParameters(simulatedTime);
        String fileNamePattern = (((randomOutputDir) + "/%d{") + (CoreConstants.DAILY_DATE_PATTERN)) + "}-clean.%i";
        params.maxHistory(60).fileNamePattern(fileNamePattern).simulatedNumberOfPeriods(10).sizeCap(sizeCap);
        logOverMultiplePeriods(params);
        List<File> foundFiles = findFilesByPattern("\\d{4}-\\d{2}-\\d{2}-clean(\\.\\d)");
        Collections.sort(foundFiles, new Comparator<File>() {
            public int compare(File f0, File f1) {
                String s0 = f0.getName().toString();
                String s1 = f1.getName().toString();
                return s0.compareTo(s1);
            }
        });
        System.out.print(foundFiles);
        StatusPrinter.print(context);
        checkFileCount((expectedFileCount - 1));
    }

    @Test
    public void dailyChronologSizeBasedRollover() {
        SizeAndTimeBasedFNATP<Object> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP<Object>();
        sizeAndTimeBasedFNATP.setMaxFileSize(new FileSize(10000));
        sizeAndTimeBasedFNATP.invocationGate = fixedRateInvocationGate;
        tbfnatp = sizeAndTimeBasedFNATP;
        slashCount = 1;
        String fileNamePattern = (((randomOutputDir) + "/%d{") + (CoreConstants.DAILY_DATE_PATTERN)) + "}/clean.%i.zip";
        cp.maxHistory(5).fileNamePattern(fileNamePattern).simulatedNumberOfPeriods((5 * 3));
        logOverMultiplePeriods(cp);
        checkDirPatternCompliance(6);
    }

    @Test
    public void dailyChronologSizeBasedRolloverWithSecondPhase() {
        SizeAndTimeBasedFNATP<Object> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP<Object>();
        sizeAndTimeBasedFNATP.setMaxFileSize(new FileSize(10000));
        sizeAndTimeBasedFNATP.invocationGate = fixedRateInvocationGate;
        tbfnatp = sizeAndTimeBasedFNATP;
        this.slashCount = 1;
        String fileNamePattern = (((randomOutputDir) + "/%d{") + (CoreConstants.DAILY_DATE_PATTERN)) + "}/clean.%i";
        int maxHistory = 5;
        cp.maxHistory(maxHistory).fileNamePattern(fileNamePattern).simulatedNumberOfPeriods(3);
        long endTime = logOverMultiplePeriods(cp);
        int simulatedNumberOfPeriods = maxHistory * 4;
        ConfigParameters cp1 = new ConfigParameters((endTime + ((TimeBasedRollingWithArchiveRemoval_Test.MILLIS_IN_DAY) * 7))).maxHistory(maxHistory).fileNamePattern(fileNamePattern).simulatedNumberOfPeriods(simulatedNumberOfPeriods);
        logOverMultiplePeriods(cp1);
        checkDirPatternCompliance((maxHistory + 1));
    }

    @Test
    public void cleanHistoryOnStart() {
        long simulatedTime = TimeBasedRollingWithArchiveRemoval_Test.WED_2016_03_23_T_230705_CET;
        System.out.println(new Date(simulatedTime));
        String fileNamePattern = (((randomOutputDir) + "clean-%d{") + (CoreConstants.DAILY_DATE_PATTERN)) + "}.txt";
        int maxHistory = 3;
        for (int i = 0; i <= 5; i++) {
            logTwiceAndStop(simulatedTime, fileNamePattern, maxHistory);
            simulatedTime += TimeBasedRollingWithArchiveRemoval_Test.MILLIS_IN_DAY;
        }
        StatusPrinter.print(context);
        checkFileCount(expectedCountWithoutFolders(maxHistory));
    }

    @Test
    public void cleanHistoryOnStartWithDayPattern() {
        long simulatedTime = TimeBasedRollingWithArchiveRemoval_Test.WED_2016_03_23_T_230705_CET;
        String fileNamePattern = (randomOutputDir) + "clean-%d{yyyy-MM-dd}.txt";
        int maxHistory = 3;
        for (int i = 0; i <= 5; i++) {
            logTwiceAndStop(simulatedTime, fileNamePattern, maxHistory);
            simulatedTime += TimeBasedRollingWithArchiveRemoval_Test.MILLIS_IN_DAY;
        }
        StatusPrinter.print(context);
        checkFileCount(expectedCountWithoutFolders(maxHistory));
    }

    boolean DO_CLEAN_HISTORY_ON_START = true;

    boolean DO_NOT_CLEAN_HISTORY_ON_START = false;
}

