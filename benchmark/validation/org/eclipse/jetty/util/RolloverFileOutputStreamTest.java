/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.toolchain.test.FS;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(WorkDirExtension.class)
public class RolloverFileOutputStreamTest {
    public WorkDir testingDir;

    /**
     * <a href="Issue #1507">https://github.com/eclipse/jetty.project/issues/1507</a>
     */
    @Test
    public void testMidnightRolloverCalc_PDT_Issue1507() {
        ZoneId zone = RolloverFileOutputStreamTest.toZoneId("PST");
        ZonedDateTime initialDate = RolloverFileOutputStreamTest.toDateTime("2017.04.26-08:00:00.0 PM PDT", zone);
        ZonedDateTime midnight = RolloverFileOutputStream.toMidnight(initialDate);
        MatcherAssert.assertThat("Midnight", RolloverFileOutputStreamTest.toString(midnight), CoreMatchers.is("2017.04.27-12:00:00.0 AM PDT"));
        Object[][] expected = new Object[][]{ new Object[]{ "2017.04.27-12:00:00.0 AM PDT", 14400000L }, new Object[]{ "2017.04.28-12:00:00.0 AM PDT", 86400000L }, new Object[]{ "2017.04.29-12:00:00.0 AM PDT", 86400000L }, new Object[]{ "2017.04.30-12:00:00.0 AM PDT", 86400000L }, new Object[]{ "2017.05.01-12:00:00.0 AM PDT", 86400000L }, new Object[]{ "2017.05.02-12:00:00.0 AM PDT", 86400000L } };
        assertSequence(initialDate, expected);
    }

    @Test
    public void testMidnightRolloverCalc_PST_DST_Start() {
        ZoneId zone = RolloverFileOutputStreamTest.toZoneId("PST");
        ZonedDateTime initialDate = RolloverFileOutputStreamTest.toDateTime("2016.03.10-01:23:45.0 PM PST", zone);
        ZonedDateTime midnight = RolloverFileOutputStream.toMidnight(initialDate);
        MatcherAssert.assertThat("Midnight", RolloverFileOutputStreamTest.toString(midnight), CoreMatchers.is("2016.03.11-12:00:00.0 AM PST"));
        Object[][] expected = new Object[][]{ new Object[]{ "2016.03.12-12:00:00.0 AM PST", 86400000L }, new Object[]{ "2016.03.13-12:00:00.0 AM PST", 86400000L }, new Object[]{ "2016.03.14-12:00:00.0 AM PDT", 82800000L }// the short day
        // the short day
        // the short day
        , new Object[]{ "2016.03.15-12:00:00.0 AM PDT", 86400000L }, new Object[]{ "2016.03.16-12:00:00.0 AM PDT", 86400000L } };
        assertSequence(midnight, expected);
    }

    @Test
    public void testMidnightRolloverCalc_PST_DST_End() {
        ZoneId zone = RolloverFileOutputStreamTest.toZoneId("PST");
        ZonedDateTime initialDate = RolloverFileOutputStreamTest.toDateTime("2016.11.03-11:22:33.0 AM PDT", zone);
        ZonedDateTime midnight = RolloverFileOutputStream.toMidnight(initialDate);
        MatcherAssert.assertThat("Midnight", RolloverFileOutputStreamTest.toString(midnight), CoreMatchers.is("2016.11.04-12:00:00.0 AM PDT"));
        Object[][] expected = new Object[][]{ new Object[]{ "2016.11.05-12:00:00.0 AM PDT", 86400000L }, new Object[]{ "2016.11.06-12:00:00.0 AM PDT", 86400000L }, new Object[]{ "2016.11.07-12:00:00.0 AM PST", 90000000L }// the long day
        // the long day
        // the long day
        , new Object[]{ "2016.11.08-12:00:00.0 AM PST", 86400000L }, new Object[]{ "2016.11.09-12:00:00.0 AM PST", 86400000L } };
        assertSequence(midnight, expected);
    }

    @Test
    public void testMidnightRolloverCalc_Sydney_DST_Start() {
        ZoneId zone = RolloverFileOutputStreamTest.toZoneId("Australia/Sydney");
        ZonedDateTime initialDate = RolloverFileOutputStreamTest.toDateTime("2016.09.31-01:23:45.0 PM AEST", zone);
        ZonedDateTime midnight = RolloverFileOutputStream.toMidnight(initialDate);
        MatcherAssert.assertThat("Midnight", RolloverFileOutputStreamTest.toString(midnight), CoreMatchers.is("2016.10.01-12:00:00.0 AM AEST"));
        Object[][] expected = new Object[][]{ new Object[]{ "2016.10.02-12:00:00.0 AM AEST", 86400000L }, new Object[]{ "2016.10.03-12:00:00.0 AM AEDT", 82800000L }// the short day
        // the short day
        // the short day
        , new Object[]{ "2016.10.04-12:00:00.0 AM AEDT", 86400000L }, new Object[]{ "2016.10.05-12:00:00.0 AM AEDT", 86400000L }, new Object[]{ "2016.10.06-12:00:00.0 AM AEDT", 86400000L } };
        assertSequence(midnight, expected);
    }

    @Test
    public void testMidnightRolloverCalc_Sydney_DST_End() {
        ZoneId zone = RolloverFileOutputStreamTest.toZoneId("Australia/Sydney");
        ZonedDateTime initialDate = RolloverFileOutputStreamTest.toDateTime("2016.04.01-11:22:33.0 AM AEDT", zone);
        ZonedDateTime midnight = RolloverFileOutputStream.toMidnight(initialDate);
        MatcherAssert.assertThat("Midnight", RolloverFileOutputStreamTest.toString(midnight), CoreMatchers.is("2016.04.02-12:00:00.0 AM AEDT"));
        Object[][] expected = new Object[][]{ new Object[]{ "2016.04.03-12:00:00.0 AM AEDT", 86400000L }, new Object[]{ "2016.04.04-12:00:00.0 AM AEST", 90000000L }// The long day
        // The long day
        // The long day
        , new Object[]{ "2016.04.05-12:00:00.0 AM AEST", 86400000L }, new Object[]{ "2016.04.06-12:00:00.0 AM AEST", 86400000L }, new Object[]{ "2016.04.07-12:00:00.0 AM AEST", 86400000L } };
        assertSequence(midnight, expected);
    }

    @Test
    public void testFileHandling() throws Exception {
        Path testPath = testingDir.getEmptyPathDir();
        ZoneId zone = RolloverFileOutputStreamTest.toZoneId("Australia/Sydney");
        ZonedDateTime now = RolloverFileOutputStreamTest.toDateTime("2016.04.10-08:30:12.3 AM AEDT", zone);
        Path template = testPath.resolve("test-rofos-yyyy_mm_dd.log");
        String templateString = template.toAbsolutePath().toString();
        try (RolloverFileOutputStream rofos = new RolloverFileOutputStream(templateString, false, 3, TimeZone.getTimeZone(zone), null, null, now)) {
            rofos.write("TICK".getBytes());
            rofos.flush();
        }
        now = now.plus(5, ChronoUnit.MINUTES);
        try (RolloverFileOutputStream rofos = new RolloverFileOutputStream(templateString, false, 3, TimeZone.getTimeZone(zone), null, null, now)) {
            rofos.write("TOCK".getBytes());
            rofos.flush();
            String[] ls = ls(testPath);
            MatcherAssert.assertThat(ls.length, CoreMatchers.is(2));
            String backup = null;
            for (String n : ls) {
                if (!("test-rofos-2016_04_10.log".equals(n)))
                    backup = n;

            }
            MatcherAssert.assertThat(Arrays.asList(ls), Matchers.containsInAnyOrder(backup, "test-rofos-2016_04_10.log"));
            Files.setLastModifiedTime(testPath.resolve(backup), FileTime.from(now.toInstant()));
            Files.setLastModifiedTime(testPath.resolve("test-rofos-2016_04_10.log"), FileTime.from(now.toInstant()));
            ZonedDateTime time = now.minus(1, ChronoUnit.DAYS);
            for (int i = 10; (i--) > 5;) {
                String file = ("test-rofos-2016_04_0" + i) + ".log";
                Path path = testPath.resolve(file);
                FS.touch(path);
                Files.setLastModifiedTime(path, FileTime.from(time.toInstant()));
                if ((i % 2) == 0) {
                    file = ("test-rofos-2016_04_0" + i) + ".log.083512300";
                    path = testPath.resolve(file);
                    FS.touch(path);
                    Files.setLastModifiedTime(path, FileTime.from(time.toInstant()));
                    time = time.minus(1, ChronoUnit.DAYS);
                }
                file = "unrelated-" + i;
                path = testPath.resolve(file);
                FS.touch(path);
                Files.setLastModifiedTime(path, FileTime.from(time.toInstant()));
                time = time.minus(1, ChronoUnit.DAYS);
            }
            ls = ls(testPath);
            MatcherAssert.assertThat(ls.length, CoreMatchers.is(14));
            MatcherAssert.assertThat(Arrays.asList(ls), Matchers.containsInAnyOrder("test-rofos-2016_04_05.log", "test-rofos-2016_04_06.log", "test-rofos-2016_04_07.log", "test-rofos-2016_04_08.log", "test-rofos-2016_04_09.log", "test-rofos-2016_04_10.log", "test-rofos-2016_04_06.log.083512300", "test-rofos-2016_04_08.log.083512300", "test-rofos-2016_04_10.log.083512300", "unrelated-9", "unrelated-8", "unrelated-7", "unrelated-6", "unrelated-5"));
            rofos.removeOldFiles(now);
            ls = ls(testPath);
            MatcherAssert.assertThat(ls.length, CoreMatchers.is(10));
            MatcherAssert.assertThat(Arrays.asList(ls), Matchers.containsInAnyOrder("test-rofos-2016_04_08.log", "test-rofos-2016_04_09.log", "test-rofos-2016_04_10.log", "test-rofos-2016_04_08.log.083512300", "test-rofos-2016_04_10.log.083512300", "unrelated-9", "unrelated-8", "unrelated-7", "unrelated-6", "unrelated-5"));
            MatcherAssert.assertThat(readPath(testPath.resolve(backup)), CoreMatchers.is("TICK"));
            MatcherAssert.assertThat(readPath(testPath.resolve("test-rofos-2016_04_10.log")), CoreMatchers.is("TOCK"));
        }
    }

    @Test
    public void testRollover() throws Exception {
        Path testPath = testingDir.getEmptyPathDir();
        ZoneId zone = RolloverFileOutputStreamTest.toZoneId("Australia/Sydney");
        ZonedDateTime now = RolloverFileOutputStreamTest.toDateTime("2016.04.10-11:59:55.0 PM AEDT", zone);
        Path template = testPath.resolve("test-rofos-yyyy_mm_dd.log");
        String templateString = template.toAbsolutePath().toString();
        try (RolloverFileOutputStream rofos = new RolloverFileOutputStream(templateString, false, 0, TimeZone.getTimeZone(zone), null, null, now)) {
            rofos.write("BEFORE".getBytes());
            rofos.flush();
            String[] ls = ls(testPath);
            MatcherAssert.assertThat(ls.length, CoreMatchers.is(1));
            MatcherAssert.assertThat(ls[0], CoreMatchers.is("test-rofos-2016_04_10.log"));
            TimeUnit.SECONDS.sleep(10);
            rofos.write("AFTER".getBytes());
            ls = ls(testPath);
            MatcherAssert.assertThat(ls.length, CoreMatchers.is(2));
            for (String n : ls) {
                String content = readPath(testPath.resolve(n));
                if ("test-rofos-2016_04_10.log".equals(n)) {
                    MatcherAssert.assertThat(content, CoreMatchers.is("BEFORE"));
                } else {
                    MatcherAssert.assertThat(content, CoreMatchers.is("AFTER"));
                }
            }
        }
    }

    @Test
    public void testRolloverBackup() throws Exception {
        Path testPath = testingDir.getEmptyPathDir();
        ZoneId zone = RolloverFileOutputStreamTest.toZoneId("Australia/Sydney");
        ZonedDateTime now = RolloverFileOutputStreamTest.toDateTime("2016.04.10-11:59:55.0 PM AEDT", zone);
        Path template = testPath.resolve("test-rofosyyyy_mm_dd.log");
        String templateString = template.toAbsolutePath().toString();
        try (RolloverFileOutputStream rofos = new RolloverFileOutputStream(templateString, false, 0, TimeZone.getTimeZone(zone), "", null, now)) {
            rofos.write("BEFORE".getBytes());
            rofos.flush();
            String[] ls = ls(testPath);
            MatcherAssert.assertThat("File List.length", ls.length, CoreMatchers.is(1));
            MatcherAssert.assertThat(ls[0], CoreMatchers.is("test-rofos.log"));
            TimeUnit.SECONDS.sleep(10);
            rofos.write("AFTER".getBytes());
            ls = ls(testPath);
            MatcherAssert.assertThat("File List.length", ls.length, CoreMatchers.is(2));
            for (String n : ls) {
                String content = readPath(testPath.resolve(n));
                if ("test-rofos.log".equals(n)) {
                    MatcherAssert.assertThat(content, CoreMatchers.is("AFTER"));
                } else {
                    MatcherAssert.assertThat(content, CoreMatchers.is("BEFORE"));
                }
            }
        }
    }
}

