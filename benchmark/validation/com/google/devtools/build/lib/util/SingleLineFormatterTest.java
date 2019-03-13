/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.util;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class SingleLineFormatterTest {
    private static final ZonedDateTime TIMESTAMP = ZonedDateTime.of(2017, 4, 1, 17, 3, 43, 0, ZoneOffset.UTC).plus(142, ChronoUnit.MILLIS);

    @Test
    public void testFormat() {
        LogRecord logRecord = SingleLineFormatterTest.createLogRecord(Level.SEVERE, SingleLineFormatterTest.TIMESTAMP);
        assertThat(new SingleLineFormatter().format(logRecord)).isEqualTo("170401 17:03:43.142:X 543 [SomeSourceClass.aSourceMethod] some message\n");
    }

    @Test
    public void testLevel() {
        LogRecord logRecord = SingleLineFormatterTest.createLogRecord(Level.WARNING, SingleLineFormatterTest.TIMESTAMP);
        String formatted = new SingleLineFormatter().format(logRecord);
        assertThat(formatted).contains("W");
        assertThat(formatted).doesNotContain("X");
    }

    @Test
    public void testTime() {
        LogRecord logRecord = SingleLineFormatterTest.createLogRecord(Level.SEVERE, ZonedDateTime.of(1999, 11, 30, 3, 4, 5, 0, ZoneOffset.UTC).plus(722, ChronoUnit.MILLIS));
        assertThat(new SingleLineFormatter().format(logRecord)).contains("991130 03:04:05.722");
    }

    @Test
    public void testStackTrace() {
        LogRecord logRecord = SingleLineFormatterTest.createLogRecord(Level.SEVERE, SingleLineFormatterTest.TIMESTAMP, new RuntimeException("something wrong"));
        assertThat(new SingleLineFormatter().format(logRecord)).startsWith(("170401 17:03:43.142:XT 543 [SomeSourceClass.aSourceMethod] some message\n" + ("java.lang.RuntimeException: something wrong\n" + "\tat com.google.devtools.build.lib.util.SingleLineFormatterTest.testStackTrace")));
    }
}

