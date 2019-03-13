/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker;


import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link LogRecordMatcher}.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("AssertionFailureIgnored")
public class LogRecordMatcherTest {
    @Test
    public void testMatchingLogRecord() {
        String msg = "any message";
        LogRecord record = new LogRecord(Level.INFO, msg);
        Assert.assertThat(record, LogRecordMatcher.hasLog(msg));
        Assert.assertThat(record, LogRecordMatcher.hasLog(Level.INFO, msg));
    }

    @Test
    public void testMatchesSubstring() {
        LogRecord record = new LogRecord(Level.INFO, "hello world");
        Assert.assertThat(record, LogRecordMatcher.hasLog("hello"));
        Assert.assertThat(record, LogRecordMatcher.hasLog("world"));
        Assert.assertThat("Should match empty substring", record, LogRecordMatcher.hasLog(""));
    }

    @Test
    public void testMatchesCollection() {
        Set<LogRecord> records = Sets.newHashSet(new LogRecord(Level.SEVERE, "error"));
        Assert.assertThat(records, LogRecordMatcher.hasLogItem(Level.SEVERE, "error"));
    }

    @Test
    public void testLevelMismatch() {
        LogRecord record = new LogRecord(Level.WARNING, "abc");
        try {
            Assert.assertThat(record, LogRecordMatcher.hasLog(Level.CONFIG, ""));
        } catch (AssertionError e) {
            return;
        }
        Assert.fail("Expected exception not thrown");
    }

    @Test
    public void testMessageMismatch() {
        LogRecord record = new LogRecord(Level.INFO, "foo");
        try {
            Assert.assertThat(record, LogRecordMatcher.hasLog("bar"));
        } catch (AssertionError e) {
            return;
        }
        Assert.fail("Expected exception not thrown");
    }

    @Test
    public void testFailureDescription() {
        LogRecord record = new LogRecord(Level.WARNING, "foo");
        try {
            Assert.assertThat(record, LogRecordMatcher.hasLog(Level.INFO, "bar"));
        } catch (AssertionError e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Expected: level is <INFO> and message containing <bar>"));
            return;
        }
        Assert.fail("Expected exception not thrown");
    }
}

