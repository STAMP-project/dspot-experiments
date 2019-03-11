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
package org.apache.beam.runners.samza.adapter;


import BoundedWindow.TIMESTAMP_MIN_VALUE;
import UnboundedSourceSystem.Consumer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import org.apache.beam.sdk.coders.Coder;
import org.apache.samza.Partition;
import org.apache.samza.system.SystemStreamPartition;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link UnboundedSourceSystem}.
 */
public class UnboundedSourceSystemTest {
    // A reasonable time to wait to get all messages from the source assuming no blocking.
    private static final long DEFAULT_TIMEOUT_MILLIS = 1000;

    private static final long DEFAULT_WATERMARK_TIMEOUT_MILLIS = 1000;

    private static final SystemStreamPartition DEFAULT_SSP = new SystemStreamPartition("default-system", "default-system", new Partition(0));

    private static final Coder<TestCheckpointMark> CHECKPOINT_MARK_CODER = TestUnboundedSource.createBuilder().build().getCheckpointMarkCoder();

    @Test
    public void testConsumerStartStop() throws IOException, InterruptedException {
        final TestUnboundedSource<String> source = TestUnboundedSource.<String>createBuilder().build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source);
        consumer.register(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(0));
        consumer.start();
        Assert.assertEquals(Collections.EMPTY_LIST, UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testConsumeOneMessage() throws IOException, InterruptedException {
        final TestUnboundedSource<String> source = TestUnboundedSource.<String>createBuilder().addElements("test").build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source);
        consumer.register(UnboundedSourceSystemTest.DEFAULT_SSP, null);
        consumer.start();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(0), "test", TIMESTAMP_MIN_VALUE)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testAdvanceTimestamp() throws IOException, InterruptedException {
        final Instant timestamp = Instant.now();
        final TestUnboundedSource<String> source = TestUnboundedSource.<String>createBuilder().addElements("before").setTimestamp(timestamp).addElements("after").build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source);
        consumer.register(UnboundedSourceSystemTest.DEFAULT_SSP, null);
        consumer.start();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(0), "before", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(1), "after", timestamp)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testConsumeMultipleMessages() throws IOException, InterruptedException {
        final Instant timestamp = Instant.now();
        final TestUnboundedSource<String> source = TestUnboundedSource.<String>createBuilder().setTimestamp(timestamp).addElements("test", "a", "few", "messages").build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source);
        consumer.register(UnboundedSourceSystemTest.DEFAULT_SSP, null);
        consumer.start();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(0), "test", timestamp), TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(1), "a", timestamp), TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(2), "few", timestamp), TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(3), "messages", timestamp)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testAdvanceWatermark() throws IOException, InterruptedException {
        final Instant now = Instant.now();
        final Instant nowPlusOne = now.plus(1L);
        final TestUnboundedSource<String> source = TestUnboundedSource.<String>createBuilder().setTimestamp(now).addElements("first").setTimestamp(nowPlusOne).addElements("second").advanceWatermarkTo(now).build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source);
        consumer.register(UnboundedSourceSystemTest.DEFAULT_SSP, null);
        consumer.start();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(0), "first", now), TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(1), "second", nowPlusOne), TestSourceHelpers.createWatermarkMessage(UnboundedSourceSystemTest.DEFAULT_SSP, now)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_WATERMARK_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testMultipleAdvanceWatermark() throws IOException, InterruptedException {
        final Instant now = Instant.now();
        final Instant nowPlusOne = now.plus(1L);
        final Instant nowPlusTwo = now.plus(2L);
        final TestUnboundedSource<String> source = // will output the first watermark
        TestUnboundedSource.<String>createBuilder().setTimestamp(now).addElements("first").advanceWatermarkTo(now).noElements().setTimestamp(nowPlusOne).addElements("second").setTimestamp(nowPlusTwo).addElements("third").advanceWatermarkTo(nowPlusOne).build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source);
        consumer.register(UnboundedSourceSystemTest.DEFAULT_SSP, null);
        consumer.start();
        // consume to the first watermark
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(0), "first", now), TestSourceHelpers.createWatermarkMessage(UnboundedSourceSystemTest.DEFAULT_SSP, now)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_WATERMARK_TIMEOUT_MILLIS));
        // consume to the second watermark
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(1), "second", nowPlusOne), TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(2), "third", nowPlusTwo), TestSourceHelpers.createWatermarkMessage(UnboundedSourceSystemTest.DEFAULT_SSP, nowPlusOne)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_WATERMARK_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testReaderThrowsAtStart() throws Exception {
        final IOException exception = new IOException("Expected exception");
        final TestUnboundedSource<String> source = TestUnboundedSource.<String>createBuilder().addException(exception).build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source);
        consumer.register(UnboundedSourceSystemTest.DEFAULT_SSP, null);
        consumer.start();
        TestSourceHelpers.expectWrappedException(exception, () -> UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testReaderThrowsAtAdvance() throws Exception {
        final IOException exception = new IOException("Expected exception");
        final TestUnboundedSource<String> source = TestUnboundedSource.<String>createBuilder().addElements("test", "a", "few", "good", "messages", "then", "...").addException(exception).build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source);
        consumer.register(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(0));
        consumer.start();
        TestSourceHelpers.expectWrappedException(exception, () -> UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testTimeout() throws Exception {
        final CountDownLatch advanceLatch = new CountDownLatch(1);
        final Instant now = Instant.now();
        final Instant nowPlusOne = now.plus(1);
        final TestUnboundedSource<String> source = TestUnboundedSource.<String>createBuilder().setTimestamp(now).addElements("before").addLatch(advanceLatch).setTimestamp(nowPlusOne).addElements("after").advanceWatermarkTo(nowPlusOne).build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source);
        consumer.register(UnboundedSourceSystemTest.DEFAULT_SSP, null);
        consumer.start();
        Assert.assertEquals(Collections.singletonList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(0), "before", now)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        advanceLatch.countDown();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.offset(1), "after", nowPlusOne), TestSourceHelpers.createWatermarkMessage(UnboundedSourceSystemTest.DEFAULT_SSP, nowPlusOne)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.DEFAULT_SSP, UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testRestartFromCheckpoint() throws IOException, InterruptedException {
        final TestUnboundedSource.SplittableBuilder<String> builder = TestUnboundedSource.<String>createSplits(3);
        builder.forSplit(0).addElements("split-0");
        builder.forSplit(1).addElements("split-1");
        builder.forSplit(2).addElements("split-2");
        final TestUnboundedSource<String> source = builder.build();
        final Consumer<String, TestCheckpointMark> consumer = createConsumer(source, 3);
        consumer.register(UnboundedSourceSystemTest.ssp(0), UnboundedSourceSystemTest.offset(10));
        consumer.register(UnboundedSourceSystemTest.ssp(1), UnboundedSourceSystemTest.offset(5));
        consumer.register(UnboundedSourceSystemTest.ssp(2), UnboundedSourceSystemTest.offset(8));
        consumer.start();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.ssp(0), UnboundedSourceSystemTest.offset(11), "split-0", TIMESTAMP_MIN_VALUE)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.ssp(0), UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.ssp(1), UnboundedSourceSystemTest.offset(6), "split-1", TIMESTAMP_MIN_VALUE)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.ssp(1), UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(UnboundedSourceSystemTest.ssp(2), UnboundedSourceSystemTest.offset(9), "split-2", TIMESTAMP_MIN_VALUE)), UnboundedSourceSystemTest.consumeUntilTimeoutOrWatermark(consumer, UnboundedSourceSystemTest.ssp(2), UnboundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }
}

