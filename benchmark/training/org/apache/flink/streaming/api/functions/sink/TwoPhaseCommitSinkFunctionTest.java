/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.api.functions.sink;


import VoidSerializer.INSTANCE;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.streaming.util.ContentDump;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TwoPhaseCommitSinkFunction}.
 */
public class TwoPhaseCommitSinkFunctionTest {
    private TwoPhaseCommitSinkFunctionTest.ContentDumpSinkFunction sinkFunction;

    private OneInputStreamOperatorTestHarness<String, Object> harness;

    private AtomicBoolean throwException = new AtomicBoolean();

    private ContentDump targetDirectory;

    private ContentDump tmpDirectory;

    private TwoPhaseCommitSinkFunctionTest.SettableClock clock;

    private Logger logger;

    private AppenderSkeleton testAppender;

    private List<LoggingEvent> loggingEvents;

    @Test
    public void testNotifyOfCompletedCheckpoint() throws Exception {
        harness.open();
        harness.processElement("42", 0);
        harness.snapshot(0, 1);
        harness.processElement("43", 2);
        harness.snapshot(1, 3);
        harness.processElement("44", 4);
        harness.snapshot(2, 5);
        harness.notifyOfCompletedCheckpoint(1);
        assertExactlyOnce(Arrays.asList("42", "43"));
        Assert.assertEquals(2, tmpDirectory.listFiles().size());// one for checkpointId 2 and second for the currentTransaction

    }

    @Test
    public void testFailBeforeNotify() throws Exception {
        harness.open();
        harness.processElement("42", 0);
        harness.snapshot(0, 1);
        harness.processElement("43", 2);
        OperatorSubtaskState snapshot = harness.snapshot(1, 3);
        tmpDirectory.setWritable(false);
        try {
            harness.processElement("44", 4);
            harness.snapshot(2, 5);
            Assert.fail("something should fail");
        } catch (Exception ex) {
            if (!((ex.getCause()) instanceof ContentDump.NotWritableException)) {
                throw ex;
            }
            // ignore
        }
        closeTestHarness();
        tmpDirectory.setWritable(true);
        setUpTestHarness();
        harness.initializeState(snapshot);
        assertExactlyOnce(Arrays.asList("42", "43"));
        closeTestHarness();
        Assert.assertEquals(0, tmpDirectory.listFiles().size());
    }

    @Test
    public void testIgnoreCommitExceptionDuringRecovery() throws Exception {
        clock.setEpochMilli(0);
        harness.open();
        harness.processElement("42", 0);
        final OperatorSubtaskState snapshot = harness.snapshot(0, 1);
        harness.notifyOfCompletedCheckpoint(1);
        throwException.set(true);
        closeTestHarness();
        setUpTestHarness();
        final long transactionTimeout = 1000;
        setTransactionTimeout(transactionTimeout);
        ignoreFailuresAfterTransactionTimeout();
        try {
            harness.initializeState(snapshot);
            Assert.fail("Expected exception not thrown");
        } catch (RuntimeException e) {
            Assert.assertEquals("Expected exception", e.getMessage());
        }
        clock.setEpochMilli((transactionTimeout + 1));
        harness.initializeState(snapshot);
        assertExactlyOnce(Collections.singletonList("42"));
    }

    @Test
    public void testLogTimeoutAlmostReachedWarningDuringCommit() throws Exception {
        clock.setEpochMilli(0);
        final long transactionTimeout = 1000;
        final double warningRatio = 0.5;
        setTransactionTimeout(transactionTimeout);
        enableTransactionTimeoutWarnings(warningRatio);
        harness.open();
        harness.snapshot(0, 1);
        final long elapsedTime = ((long) ((((double) (transactionTimeout)) * warningRatio) + 2));
        clock.setEpochMilli(elapsedTime);
        harness.notifyOfCompletedCheckpoint(1);
        final List<String> logMessages = loggingEvents.stream().map(LoggingEvent::getRenderedMessage).collect(Collectors.toList());
        Assert.assertThat(logMessages, CoreMatchers.hasItem(CoreMatchers.containsString(("has been open for 502 ms. " + "This is close to or even exceeding the transaction timeout of 1000 ms."))));
    }

    @Test
    public void testLogTimeoutAlmostReachedWarningDuringRecovery() throws Exception {
        clock.setEpochMilli(0);
        final long transactionTimeout = 1000;
        final double warningRatio = 0.5;
        setTransactionTimeout(transactionTimeout);
        enableTransactionTimeoutWarnings(warningRatio);
        harness.open();
        final OperatorSubtaskState snapshot = harness.snapshot(0, 1);
        final long elapsedTime = ((long) ((((double) (transactionTimeout)) * warningRatio) + 2));
        clock.setEpochMilli(elapsedTime);
        closeTestHarness();
        setUpTestHarness();
        setTransactionTimeout(transactionTimeout);
        enableTransactionTimeoutWarnings(warningRatio);
        harness.initializeState(snapshot);
        harness.open();
        final List<String> logMessages = loggingEvents.stream().map(LoggingEvent::getRenderedMessage).collect(Collectors.toList());
        closeTestHarness();
        Assert.assertThat(logMessages, CoreMatchers.hasItem(CoreMatchers.containsString(("has been open for 502 ms. " + "This is close to or even exceeding the transaction timeout of 1000 ms."))));
    }

    private class ContentDumpSinkFunction extends TwoPhaseCommitSinkFunction<String, TwoPhaseCommitSinkFunctionTest.ContentTransaction, Void> {
        public ContentDumpSinkFunction() {
            super(new org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer(TwoPhaseCommitSinkFunctionTest.ContentTransaction.class, new ExecutionConfig()), INSTANCE, clock);
        }

        @Override
        protected void invoke(TwoPhaseCommitSinkFunctionTest.ContentTransaction transaction, String value, Context context) throws Exception {
            transaction.tmpContentWriter.write(value);
        }

        @Override
        protected TwoPhaseCommitSinkFunctionTest.ContentTransaction beginTransaction() throws Exception {
            return new TwoPhaseCommitSinkFunctionTest.ContentTransaction(tmpDirectory.createWriter(UUID.randomUUID().toString()));
        }

        @Override
        protected void preCommit(TwoPhaseCommitSinkFunctionTest.ContentTransaction transaction) throws Exception {
            transaction.tmpContentWriter.flush();
            transaction.tmpContentWriter.close();
        }

        @Override
        protected void commit(TwoPhaseCommitSinkFunctionTest.ContentTransaction transaction) {
            if (throwException.get()) {
                throw new RuntimeException("Expected exception");
            }
            ContentDump.move(transaction.tmpContentWriter.getName(), tmpDirectory, targetDirectory);
        }

        @Override
        protected void abort(TwoPhaseCommitSinkFunctionTest.ContentTransaction transaction) {
            transaction.tmpContentWriter.close();
            tmpDirectory.delete(transaction.tmpContentWriter.getName());
        }
    }

    private static class ContentTransaction {
        private ContentDump.ContentWriter tmpContentWriter;

        public ContentTransaction(ContentDump.ContentWriter tmpContentWriter) {
            this.tmpContentWriter = tmpContentWriter;
        }

        @Override
        public String toString() {
            return String.format("ContentTransaction[%s]", tmpContentWriter.getName());
        }
    }

    private static class SettableClock extends Clock {
        private final ZoneId zoneId;

        private long epochMilli;

        private SettableClock() {
            this.zoneId = ZoneOffset.UTC;
        }

        public SettableClock(ZoneId zoneId, long epochMilli) {
            this.zoneId = zoneId;
            this.epochMilli = epochMilli;
        }

        public void setEpochMilli(long epochMilli) {
            this.epochMilli = epochMilli;
        }

        @Override
        public ZoneId getZone() {
            return zoneId;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            if (zone.equals(this.zoneId)) {
                return this;
            }
            return new TwoPhaseCommitSinkFunctionTest.SettableClock(zone, epochMilli);
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(epochMilli);
        }
    }
}

