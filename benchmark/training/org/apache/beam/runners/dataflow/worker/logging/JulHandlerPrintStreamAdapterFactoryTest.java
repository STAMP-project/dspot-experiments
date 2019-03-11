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
package org.apache.beam.runners.dataflow.worker.logging;


import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.beam.runners.dataflow.worker.LogRecordMatcher;
import org.apache.beam.runners.dataflow.worker.LogSaver;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link JulHandlerPrintStreamAdapterFactory}.
 */
@RunWith(JUnit4.class)
public class JulHandlerPrintStreamAdapterFactoryTest {
    private static final String LOGGER_NAME = "test";

    private LogSaver handler;

    @Test
    public void testLogWarningOnFirstUsage() {
        PrintStream printStream = createPrintStreamAdapter();
        printStream.println("partial");
        Assert.assertThat(handler.getLogs(), LogRecordMatcher.hasLogItem(Level.WARNING, "Please use a logger instead of System.out or System.err"));
    }

    @Test
    public void testLogOnNewLine() {
        PrintStream printStream = createPrintStreamAdapter();
        printStream.println("blah");
        Assert.assertThat(handler.getLogs(), LogRecordMatcher.hasLogItem(Level.INFO, "blah"));
    }

    @Test
    public void testLogRecordMetadata() {
        PrintStream printStream = JulHandlerPrintStreamAdapterFactory.create(handler, "fooLogger", Level.WARNING);
        printStream.println("anyMessage");
        Assert.assertThat(handler.getLogs(), Matchers.not(Matchers.empty()));
        LogRecord log = Iterables.get(handler.getLogs(), 0);
        Assert.assertThat(log.getLevel(), Matchers.is(Level.WARNING));
        Assert.assertThat(log.getLoggerName(), Matchers.is("fooLogger"));
    }

    @Test
    public void testLogOnlyUptoNewLine() {
        PrintStream printStream = createPrintStreamAdapter();
        printStream.println("blah");
        printStream.print("foo");
        Assert.assertThat(handler.getLogs(), LogRecordMatcher.hasLogItem("blah"));
        Assert.assertThat(handler.getLogs(), Matchers.not(LogRecordMatcher.hasLogItem("foo")));
    }

    @Test
    public void testLogMultiLine() {
        PrintStream printStream = createPrintStreamAdapter();
        printStream.format("blah%nfoo%n");
        Assert.assertThat(handler.getLogs(), LogRecordMatcher.hasLogItem("blah"));
        Assert.assertThat(handler.getLogs(), LogRecordMatcher.hasLogItem("foo"));
    }

    @Test
    public void testDontLogIfNoNewLine() {
        PrintStream printStream = createPrintStreamAdapter();
        printStream.print("blah");
        Assert.assertThat(handler.getLogs(), Matchers.not(LogRecordMatcher.hasLogItem("blah")));
    }

    @Test
    public void testLogOnFlush() {
        PrintStream printStream = createPrintStreamAdapter();
        printStream.print("blah");
        printStream.flush();
        Assert.assertThat(handler.getLogs(), LogRecordMatcher.hasLogItem("blah"));
    }

    @Test
    public void testLogOnClose() {
        try (PrintStream printStream = createPrintStreamAdapter()) {
            printStream.print("blah");
        }
        Assert.assertThat(handler.getLogs(), LogRecordMatcher.hasLogItem("blah"));
    }
}

