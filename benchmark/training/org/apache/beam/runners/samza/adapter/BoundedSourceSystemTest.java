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


import BoundedWindow.TIMESTAMP_MAX_VALUE;
import BoundedWindow.TIMESTAMP_MIN_VALUE;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.apache.samza.Partition;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link BoundedSourceSystem}.
 */
public class BoundedSourceSystemTest {
    private static final SystemStreamPartition DEFAULT_SSP = new SystemStreamPartition("default-system", "default-system", new Partition(0));

    // A reasonable time to wait to get all messages from the bounded source assuming no blocking.
    private static final long DEFAULT_TIMEOUT_MILLIS = 1000;

    @Test
    public void testConsumerStartStop() throws IOException, InterruptedException {
        final TestBoundedSource<String> source = TestBoundedSource.<String>createBuilder().build();
        final BoundedSourceSystem.Consumer<String> consumer = createConsumer(source);
        consumer.register(BoundedSourceSystemTest.DEFAULT_SSP, "0");
        consumer.start();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createWatermarkMessage(BoundedSourceSystemTest.DEFAULT_SSP, TIMESTAMP_MAX_VALUE), TestSourceHelpers.createEndOfStreamMessage(BoundedSourceSystemTest.DEFAULT_SSP)), BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.DEFAULT_SSP, BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testConsumeOneMessage() throws IOException, InterruptedException {
        final TestBoundedSource<String> source = TestBoundedSource.<String>createBuilder().addElements("test").build();
        final BoundedSourceSystem.Consumer<String> consumer = createConsumer(source);
        consumer.register(BoundedSourceSystemTest.DEFAULT_SSP, "0");
        consumer.start();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.DEFAULT_SSP, "0", "test", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createWatermarkMessage(BoundedSourceSystemTest.DEFAULT_SSP, TIMESTAMP_MAX_VALUE), TestSourceHelpers.createEndOfStreamMessage(BoundedSourceSystemTest.DEFAULT_SSP)), BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.DEFAULT_SSP, BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testAdvanceTimestamp() throws InterruptedException {
        final Instant timestamp = Instant.now();
        final TestBoundedSource<String> source = TestBoundedSource.<String>createBuilder().addElements("before").setTimestamp(timestamp).addElements("after").build();
        final BoundedSourceSystem.Consumer<String> consumer = createConsumer(source);
        consumer.register(BoundedSourceSystemTest.DEFAULT_SSP, "0");
        consumer.start();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.DEFAULT_SSP, "0", "before", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.DEFAULT_SSP, "1", "after", timestamp), TestSourceHelpers.createWatermarkMessage(BoundedSourceSystemTest.DEFAULT_SSP, TIMESTAMP_MAX_VALUE), TestSourceHelpers.createEndOfStreamMessage(BoundedSourceSystemTest.DEFAULT_SSP)), BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.DEFAULT_SSP, BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testConsumeMultipleMessages() throws IOException, InterruptedException {
        final TestBoundedSource<String> source = TestBoundedSource.<String>createBuilder().addElements("test", "a", "few", "messages").build();
        final BoundedSourceSystem.Consumer<String> consumer = createConsumer(source);
        consumer.register(BoundedSourceSystemTest.DEFAULT_SSP, "0");
        consumer.start();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.DEFAULT_SSP, "0", "test", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.DEFAULT_SSP, "1", "a", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.DEFAULT_SSP, "2", "few", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.DEFAULT_SSP, "3", "messages", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createWatermarkMessage(BoundedSourceSystemTest.DEFAULT_SSP, TIMESTAMP_MAX_VALUE), TestSourceHelpers.createEndOfStreamMessage(BoundedSourceSystemTest.DEFAULT_SSP)), BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.DEFAULT_SSP, BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testReaderThrowsAtStart() throws Exception {
        final IOException exception = new IOException("Expected exception");
        final TestBoundedSource<String> source = TestBoundedSource.<String>createBuilder().addException(exception).build();
        final BoundedSourceSystem.Consumer<String> consumer = createConsumer(source);
        consumer.register(BoundedSourceSystemTest.DEFAULT_SSP, "0");
        consumer.start();
        TestSourceHelpers.expectWrappedException(exception, () -> BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.DEFAULT_SSP, BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testReaderThrowsAtAdvance() throws Exception {
        final IOException exception = new IOException("Expected exception");
        final TestBoundedSource<String> source = TestBoundedSource.<String>createBuilder().addElements("test", "a", "few", "good", "messages", "then", "...").addException(exception).build();
        final BoundedSourceSystem.Consumer<String> consumer = createConsumer(source);
        consumer.register(BoundedSourceSystemTest.DEFAULT_SSP, "0");
        consumer.start();
        TestSourceHelpers.expectWrappedException(exception, () -> BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.DEFAULT_SSP, BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testTimeout() throws Exception {
        final CountDownLatch advanceLatch = new CountDownLatch(1);
        final TestBoundedSource<String> source = TestBoundedSource.<String>createBuilder().addElements("before").addLatch(advanceLatch).addElements("after").build();
        final BoundedSourceSystem.Consumer<String> consumer = createConsumer(source);
        consumer.register(BoundedSourceSystemTest.DEFAULT_SSP, "0");
        consumer.start();
        Assert.assertEquals(Collections.singletonList(TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.DEFAULT_SSP, "0", "before", TIMESTAMP_MIN_VALUE)), BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.DEFAULT_SSP, BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        advanceLatch.countDown();
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.DEFAULT_SSP, "1", "after", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createWatermarkMessage(BoundedSourceSystemTest.DEFAULT_SSP, TIMESTAMP_MAX_VALUE), TestSourceHelpers.createEndOfStreamMessage(BoundedSourceSystemTest.DEFAULT_SSP)), BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.DEFAULT_SSP, BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS));
        consumer.stop();
    }

    @Test
    public void testSplit() throws IOException, InterruptedException {
        final TestBoundedSource.SplittableBuilder<String> builder = TestBoundedSource.<String>createSplits(3);
        builder.forSplit(0).addElements("split-0");
        builder.forSplit(1).addElements("split-1");
        builder.forSplit(2).addElements("split-2");
        final TestBoundedSource<String> source = builder.build();
        final BoundedSourceSystem.Consumer<String> consumer = createConsumer(source, 3);
        consumer.register(BoundedSourceSystemTest.ssp(0), null);
        consumer.register(BoundedSourceSystemTest.ssp(1), null);
        consumer.register(BoundedSourceSystemTest.ssp(2), null);
        consumer.start();
        final Set<String> offsets = new HashSet<>();
        // check split0
        List<IncomingMessageEnvelope> envelopes = BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.ssp(0), BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS);
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.ssp(0), envelopes.get(0).getOffset(), "split-0", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createWatermarkMessage(BoundedSourceSystemTest.ssp(0), TIMESTAMP_MAX_VALUE), TestSourceHelpers.createEndOfStreamMessage(BoundedSourceSystemTest.ssp(0))), envelopes);
        offsets.add(envelopes.get(0).getOffset());
        // check split1
        envelopes = BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.ssp(1), BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS);
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.ssp(1), envelopes.get(0).getOffset(), "split-1", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createWatermarkMessage(BoundedSourceSystemTest.ssp(1), TIMESTAMP_MAX_VALUE), TestSourceHelpers.createEndOfStreamMessage(BoundedSourceSystemTest.ssp(1))), envelopes);
        offsets.add(envelopes.get(0).getOffset());
        // check split2
        envelopes = BoundedSourceSystemTest.consumeUntilTimeoutOrEos(consumer, BoundedSourceSystemTest.ssp(2), BoundedSourceSystemTest.DEFAULT_TIMEOUT_MILLIS);
        Assert.assertEquals(Arrays.asList(TestSourceHelpers.createElementMessage(BoundedSourceSystemTest.ssp(2), envelopes.get(0).getOffset(), "split-2", TIMESTAMP_MIN_VALUE), TestSourceHelpers.createWatermarkMessage(BoundedSourceSystemTest.ssp(2), TIMESTAMP_MAX_VALUE), TestSourceHelpers.createEndOfStreamMessage(BoundedSourceSystemTest.ssp(2))), envelopes);
        offsets.add(envelopes.get(0).getOffset());
        // check offsets
        Assert.assertEquals(Sets.newHashSet("0", "1", "2"), offsets);
        consumer.stop();
    }
}

