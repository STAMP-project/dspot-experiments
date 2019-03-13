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
package org.apache.flink.streaming.runtime.io;


import java.util.Arrays;
import java.util.Random;
import org.apache.flink.runtime.checkpoint.CheckpointMetaData;
import org.apache.flink.runtime.checkpoint.CheckpointMetrics;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.checkpoint.decline.CheckpointDeclineOnCancellationBarrierException;
import org.apache.flink.runtime.checkpoint.decline.CheckpointDeclineSubsumedException;
import org.apache.flink.runtime.checkpoint.decline.InputEndOfStreamException;
import org.apache.flink.runtime.io.network.api.CheckpointBarrier;
import org.apache.flink.runtime.io.network.partition.consumer.BufferOrEvent;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.operators.testutils.DummyEnvironment;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;


/**
 * Tests for the behavior of the {@link BarrierBuffer} with different {@link BufferBlocker} implements.
 */
public abstract class BarrierBufferTestBase {
    protected static final int PAGE_SIZE = 512;

    private static final Random RND = new Random();

    private static int sizeCounter = 1;

    // ------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------
    /**
     * Validates that the buffer behaves correctly if no checkpoint barriers come,
     * for a single input channel.
     */
    @Test
    public void testSingleChannelNoBarriers() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 1, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        for (BufferOrEvent boe : sequence) {
            Assert.assertEquals(boe, buffer.getNextNonBlocked());
        }
        Assert.assertEquals(0L, buffer.getAlignmentDurationNanos());
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    /**
     * Validates that the buffer behaves correctly if no checkpoint barriers come,
     * for an input with multiple input channels.
     */
    @Test
    public void testMultiChannelNoBarriers() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0), BarrierBufferTestBase.createBuffer(3, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(3), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(1), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(2) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 4, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        for (BufferOrEvent boe : sequence) {
            Assert.assertEquals(boe, buffer.getNextNonBlocked());
        }
        Assert.assertEquals(0L, buffer.getAlignmentDurationNanos());
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    /**
     * Validates that the buffer preserved the order of elements for a
     * input with a single input channel, and checkpoint events.
     */
    @Test
    public void testSingleChannelWithBarriers() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createBarrier(3, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(4, 0), BarrierBufferTestBase.createBarrier(5, 0), BarrierBufferTestBase.createBarrier(6, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 1, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        BarrierBufferTestBase.ValidatingCheckpointHandler handler = new BarrierBufferTestBase.ValidatingCheckpointHandler();
        buffer.registerCheckpointEventHandler(handler);
        handler.setNextExpectedCheckpointId(1L);
        for (BufferOrEvent boe : sequence) {
            if ((boe.isBuffer()) || ((boe.getEvent().getClass()) != (CheckpointBarrier.class))) {
                Assert.assertEquals(boe, buffer.getNextNonBlocked());
            }
        }
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    /**
     * Validates that the buffer correctly aligns the streams for inputs with
     * multiple input channels, by buffering and blocking certain inputs.
     */
    @Test
    public void testMultiChannelWithBarriers() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // checkpoint with blocked data
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 0), // checkpoint without blocked data
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createBarrier(2, 1), BarrierBufferTestBase.createBarrier(2, 2), // checkpoint with data only from one channel
        BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 0), BarrierBufferTestBase.createBarrier(3, 1), // empty checkpoint
        BarrierBufferTestBase.createBarrier(4, 1), BarrierBufferTestBase.createBarrier(4, 2), BarrierBufferTestBase.createBarrier(4, 0), // checkpoint with blocked data in mixed order
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(5, 1), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(5, 2), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(5, 0), // some trailing data
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0), BarrierBufferTestBase.createEndOfPartition(1), BarrierBufferTestBase.createEndOfPartition(2) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        BarrierBufferTestBase.ValidatingCheckpointHandler handler = new BarrierBufferTestBase.ValidatingCheckpointHandler();
        buffer.registerCheckpointEventHandler(handler);
        handler.setNextExpectedCheckpointId(1L);
        // pre checkpoint 1
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[1], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(1L, handler.getNextExpectedCheckpointId());
        long startTs = System.nanoTime();
        // blocking while aligning for checkpoint 1
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(1L, handler.getNextExpectedCheckpointId());
        // checkpoint 1 done, returning buffered data
        BarrierBufferTestBase.check(sequence[5], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(2L, handler.getNextExpectedCheckpointId());
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        validateAlignmentBuffered(handler.getLastReportedBytesBufferedInAlignment(), sequence[5], sequence[6]);
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // pre checkpoint 2
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[11], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(2L, handler.getNextExpectedCheckpointId());
        // checkpoint 2 barriers come together
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[17], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(3L, handler.getNextExpectedCheckpointId());
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        validateAlignmentBuffered(handler.getLastReportedBytesBufferedInAlignment());
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 3 starts, data buffered
        BarrierBufferTestBase.check(sequence[20], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        validateAlignmentBuffered(handler.getLastReportedBytesBufferedInAlignment(), sequence[20], sequence[21]);
        Assert.assertEquals(4L, handler.getNextExpectedCheckpointId());
        BarrierBufferTestBase.check(sequence[21], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 4 happens without extra data
        // pre checkpoint 5
        BarrierBufferTestBase.check(sequence[27], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        validateAlignmentBuffered(handler.getLastReportedBytesBufferedInAlignment());
        Assert.assertEquals(5L, handler.getNextExpectedCheckpointId());
        BarrierBufferTestBase.check(sequence[28], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[29], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 5 aligning
        BarrierBufferTestBase.check(sequence[31], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[32], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[33], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[37], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // buffered data from checkpoint 5 alignment
        BarrierBufferTestBase.check(sequence[34], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[36], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[38], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[39], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // remaining data
        BarrierBufferTestBase.check(sequence[41], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[42], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[43], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[44], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        validateAlignmentBuffered(handler.getLastReportedBytesBufferedInAlignment(), sequence[34], sequence[36], sequence[38], sequence[39]);
        buffer.cleanup();
    }

    @Test
    public void testMultiChannelTrailingBlockedData() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 1), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(1), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(2), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        BarrierBufferTestBase.ValidatingCheckpointHandler handler = new BarrierBufferTestBase.ValidatingCheckpointHandler();
        buffer.registerCheckpointEventHandler(handler);
        handler.setNextExpectedCheckpointId(1L);
        // pre-checkpoint 1
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[1], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(1L, handler.getNextExpectedCheckpointId());
        // pre-checkpoint 2
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(2L, handler.getNextExpectedCheckpointId());
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[8], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 2 alignment
        long startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[14], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[19], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        // end of stream: remaining buffered contents
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[11], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[16], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[17], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    /**
     * Validates that the buffer correctly aligns the streams in cases
     * where some channels receive barriers from multiple successive checkpoints
     * before the pending checkpoint is complete.
     */
    @Test
    public void testMultiChannelWithQueuedFutureBarriers() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // checkpoint 1 - with blocked data
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), // checkpoint 2 - where future checkpoint barriers come before
        // the current checkpoint is complete
        BarrierBufferTestBase.createBarrier(2, 1), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createBarrier(3, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 1), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(4, 1), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), // complete checkpoint 2, send a barrier for checkpoints 4 and 5
        BarrierBufferTestBase.createBarrier(2, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(4, 0), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(5, 1), // complete checkpoint 3
        BarrierBufferTestBase.createBarrier(3, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(6, 1), // complete checkpoint 4, checkpoint 5 remains not fully triggered
        BarrierBufferTestBase.createBarrier(4, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(1), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(2), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        BarrierBufferTestBase.ValidatingCheckpointHandler handler = new BarrierBufferTestBase.ValidatingCheckpointHandler();
        buffer.registerCheckpointEventHandler(handler);
        handler.setNextExpectedCheckpointId(1L);
        // around checkpoint 1
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[1], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[5], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(2L, handler.getNextExpectedCheckpointId());
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // alignment of checkpoint 2 - buffering also some barriers for
        // checkpoints 3 and 4
        long startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[20], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[23], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 2 completed
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        BarrierBufferTestBase.check(sequence[25], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[27], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[30], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[32], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 3 completed (emit buffered)
        BarrierBufferTestBase.check(sequence[16], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[19], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[28], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // past checkpoint 3
        BarrierBufferTestBase.check(sequence[36], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[38], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 4 completed (emit buffered)
        BarrierBufferTestBase.check(sequence[22], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[26], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[31], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[33], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[39], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // past checkpoint 4, alignment for checkpoint 5
        BarrierBufferTestBase.check(sequence[42], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[45], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[46], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // abort checkpoint 5 (end of partition)
        BarrierBufferTestBase.check(sequence[37], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // start checkpoint 6 alignment
        BarrierBufferTestBase.check(sequence[47], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[48], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // end of input, emit remainder
        BarrierBufferTestBase.check(sequence[43], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[44], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    /**
     * Validates that the buffer skips over the current checkpoint if it
     * receives a barrier from a later checkpoint on a non-blocked input.
     */
    @Test
    public void testMultiChannelSkippingCheckpoints() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // checkpoint 1 - with blocked data
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), // checkpoint 2 will not complete: pre-mature barrier from checkpoint 3
        BarrierBufferTestBase.createBarrier(2, 1), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(1), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(2), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        AbstractInvokable toNotify = Mockito.mock(AbstractInvokable.class);
        buffer.registerCheckpointEventHandler(toNotify);
        long startTs;
        // initial data
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[1], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // align checkpoint 1
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(1L, buffer.getCurrentCheckpointId());
        // checkpoint done - replay buffered
        BarrierBufferTestBase.check(sequence[5], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        Mockito.verify(toNotify).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(1L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // alignment of checkpoint 2
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[15], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 2 aborted, checkpoint 3 started
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(3L, buffer.getCurrentCheckpointId());
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        Mockito.verify(toNotify).abortCheckpointOnBarrier(ArgumentMatchers.eq(2L), ArgumentMatchers.isA(CheckpointDeclineSubsumedException.class));
        BarrierBufferTestBase.check(sequence[16], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 3 alignment in progress
        BarrierBufferTestBase.check(sequence[19], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 3 aborted (end of partition)
        BarrierBufferTestBase.check(sequence[20], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify).abortCheckpointOnBarrier(ArgumentMatchers.eq(3L), ArgumentMatchers.isA(InputEndOfStreamException.class));
        // replay buffered data from checkpoint 3
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // all the remaining messages
        BarrierBufferTestBase.check(sequence[21], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[22], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[23], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[24], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    /**
     * Validates that the buffer skips over the current checkpoint if it
     * receives a barrier from a later checkpoint on a non-blocked input.
     */
    @Test
    public void testMultiChannelJumpingOverCheckpoint() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // checkpoint 1 - with blocked data
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), // checkpoint 2 will not complete: pre-mature barrier from checkpoint 3
        BarrierBufferTestBase.createBarrier(2, 1), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 1), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 0), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(4, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(1), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(2), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        BarrierBufferTestBase.ValidatingCheckpointHandler handler = new BarrierBufferTestBase.ValidatingCheckpointHandler();
        buffer.registerCheckpointEventHandler(handler);
        handler.setNextExpectedCheckpointId(1L);
        // checkpoint 1
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[1], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(1L, buffer.getCurrentCheckpointId());
        BarrierBufferTestBase.check(sequence[5], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // alignment of checkpoint 2
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(2L, buffer.getCurrentCheckpointId());
        BarrierBufferTestBase.check(sequence[15], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[19], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[21], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        long startTs = System.nanoTime();
        // checkpoint 2 aborted, checkpoint 4 started. replay buffered
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(4L, buffer.getCurrentCheckpointId());
        BarrierBufferTestBase.check(sequence[16], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[22], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // align checkpoint 4 remainder
        BarrierBufferTestBase.check(sequence[25], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[26], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        // checkpoint 4 aborted (due to end of partition)
        BarrierBufferTestBase.check(sequence[24], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[27], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[28], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[29], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[30], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    /**
     * Validates that the buffer skips over a later checkpoint if it
     * receives a barrier from an even later checkpoint on a blocked input.
     */
    @Test
    public void testMultiChannelSkippingCheckpointsViaBlockedInputs() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // checkpoint 1 - with blocked data
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), // checkpoint 2 will not complete: pre-mature barrier from checkpoint 3
        BarrierBufferTestBase.createBarrier(2, 1), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 0)// queued barrier on blocked input
        , BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(4, 1)// pre-mature barrier on blocked input
        , BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), // complete checkpoint 2
        BarrierBufferTestBase.createBarrier(2, 2), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 2)// should be ignored
        , BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(4, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(4, 2), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(1), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(2), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        // checkpoint 1
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[1], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(1L, buffer.getCurrentCheckpointId());
        BarrierBufferTestBase.check(sequence[5], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // alignment of checkpoint 2
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[22], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(2L, buffer.getCurrentCheckpointId());
        // checkpoint 2 completed
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[15], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[16], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 3 skipped, alignment for 4 started
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(4L, buffer.getCurrentCheckpointId());
        BarrierBufferTestBase.check(sequence[21], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[24], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[26], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[30], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 4 completed
        BarrierBufferTestBase.check(sequence[20], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[28], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[29], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[32], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[33], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[34], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[35], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[36], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[37], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    @Test
    public void testEarlyCleanup() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 1), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(1), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(2), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        BarrierBufferTestBase.ValidatingCheckpointHandler handler = new BarrierBufferTestBase.ValidatingCheckpointHandler();
        buffer.registerCheckpointEventHandler(handler);
        handler.setNextExpectedCheckpointId(1L);
        // pre-checkpoint 1
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[1], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(1L, handler.getNextExpectedCheckpointId());
        // pre-checkpoint 2
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(2L, handler.getNextExpectedCheckpointId());
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[8], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 2 alignment
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[14], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[19], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // end of stream: remaining buffered contents
        buffer.getNextNonBlocked();
        buffer.cleanup();
    }

    @Test
    public void testStartAlignmentWithClosedChannels() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // close some channels immediately
        BarrierBufferTestBase.createEndOfPartition(2), BarrierBufferTestBase.createEndOfPartition(1), // checkpoint without blocked data
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(3, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 3), BarrierBufferTestBase.createBarrier(2, 0), // checkpoint with blocked data
        BarrierBufferTestBase.createBuffer(3, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 3), BarrierBufferTestBase.createBuffer(3, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 0), // empty checkpoint
        BarrierBufferTestBase.createBarrier(4, 0), BarrierBufferTestBase.createBarrier(4, 3), // some data, one channel closes
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(3, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(0), // checkpoint on last remaining channel
        BarrierBufferTestBase.createBuffer(3, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(5, 3), BarrierBufferTestBase.createBuffer(3, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createEndOfPartition(3) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 4, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        // pre checkpoint 2
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[1], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[3], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[4], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 3 alignment
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(2L, buffer.getCurrentCheckpointId());
        BarrierBufferTestBase.check(sequence[8], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[11], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint 3 buffered
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(3L, buffer.getCurrentCheckpointId());
        // after checkpoint 4
        BarrierBufferTestBase.check(sequence[15], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(4L, buffer.getCurrentCheckpointId());
        BarrierBufferTestBase.check(sequence[16], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[17], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[19], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[21], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(5L, buffer.getCurrentCheckpointId());
        BarrierBufferTestBase.check(sequence[22], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    @Test
    public void testEndOfStreamWhileCheckpoint() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // one checkpoint
        BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), // some buffers
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), // start the checkpoint that will be incomplete
        BarrierBufferTestBase.createBarrier(2, 2), BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), // close one after the barrier one before the barrier
        BarrierBufferTestBase.createEndOfPartition(2), BarrierBufferTestBase.createEndOfPartition(1), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), // final end of stream
        BarrierBufferTestBase.createEndOfPartition(0) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        // data after first checkpoint
        BarrierBufferTestBase.check(sequence[3], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[4], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[5], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(1L, buffer.getCurrentCheckpointId());
        // alignment of second checkpoint
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(2L, buffer.getCurrentCheckpointId());
        // first end-of-partition encountered: checkpoint will not be completed
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[8], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[11], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[14], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // all done
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    @Test
    public void testSingleChannelAbortCheckpoint() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createCancellationBarrier(4, 0), BarrierBufferTestBase.createBarrier(5, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createCancellationBarrier(6, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 1, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        AbstractInvokable toNotify = Mockito.mock(AbstractInvokable.class);
        buffer.registerCheckpointEventHandler(toNotify);
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(1L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        Assert.assertEquals(0L, buffer.getAlignmentDurationNanos());
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(5L, buffer.getCurrentCheckpointId());
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(2L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(4L), ArgumentMatchers.any(CheckpointDeclineOnCancellationBarrierException.class));
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(5L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        Assert.assertEquals(0L, buffer.getAlignmentDurationNanos());
        BarrierBufferTestBase.check(sequence[8], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Assert.assertEquals(6L, buffer.getCurrentCheckpointId());
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(6L), ArgumentMatchers.any(CheckpointDeclineOnCancellationBarrierException.class));
        Assert.assertEquals(0L, buffer.getAlignmentDurationNanos());
        buffer.cleanup();
    }

    @Test
    public void testMultiChannelAbortCheckpoint() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // some buffers and a successful checkpoint
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), // aborted on last barrier
        BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createBarrier(2, 2), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createCancellationBarrier(2, 1), // successful checkpoint
        BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 1), BarrierBufferTestBase.createBarrier(3, 2), BarrierBufferTestBase.createBarrier(3, 0), // abort on first barrier
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createCancellationBarrier(4, 1), BarrierBufferTestBase.createBarrier(4, 2), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(4, 0), // another successful checkpoint
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(5, 2), BarrierBufferTestBase.createBarrier(5, 1), BarrierBufferTestBase.createBarrier(5, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), // abort multiple cancellations and a barrier after the cancellations
        BarrierBufferTestBase.createCancellationBarrier(6, 1), BarrierBufferTestBase.createCancellationBarrier(6, 2), BarrierBufferTestBase.createBarrier(6, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        AbstractInvokable toNotify = Mockito.mock(AbstractInvokable.class);
        buffer.registerCheckpointEventHandler(toNotify);
        long startTs;
        // successful first checkpoint, with some aligned buffers
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[1], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[5], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(1L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[8], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // canceled checkpoint on last barrier
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(2L), ArgumentMatchers.any(CheckpointDeclineOnCancellationBarrierException.class));
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // one more successful checkpoint
        BarrierBufferTestBase.check(sequence[15], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[16], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[20], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(3L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        BarrierBufferTestBase.check(sequence[21], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // this checkpoint gets immediately canceled
        BarrierBufferTestBase.check(sequence[24], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(4L), ArgumentMatchers.any(CheckpointDeclineOnCancellationBarrierException.class));
        Assert.assertEquals(0L, buffer.getAlignmentDurationNanos());
        // some buffers
        BarrierBufferTestBase.check(sequence[26], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[27], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[28], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // a simple successful checkpoint
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[32], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(5L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        BarrierBufferTestBase.check(sequence[33], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[37], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(6L), ArgumentMatchers.any(CheckpointDeclineOnCancellationBarrierException.class));
        Assert.assertEquals(0L, buffer.getAlignmentDurationNanos());
        // all done
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    @Test
    public void testAbortViaQueuedBarriers() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // starting a checkpoint
        BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBarrier(1, 2), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), // queued barrier and cancellation barrier
        BarrierBufferTestBase.createCancellationBarrier(2, 2), BarrierBufferTestBase.createBarrier(2, 1), // some intermediate buffers (some queued)
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), // complete initial checkpoint
        BarrierBufferTestBase.createBarrier(1, 0), // some buffers (none queued, since checkpoint is aborted)
        BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), // final barrier of aborted checkpoint
        BarrierBufferTestBase.createBarrier(2, 0), // some more buffers
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        AbstractInvokable toNotify = Mockito.mock(AbstractInvokable.class);
        buffer.registerCheckpointEventHandler(toNotify);
        long startTs;
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // starting first checkpoint
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[4], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[8], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // finished first checkpoint
        BarrierBufferTestBase.check(sequence[3], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(1L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        BarrierBufferTestBase.check(sequence[5], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // re-read the queued cancellation barriers
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(2L), ArgumentMatchers.any(CheckpointDeclineOnCancellationBarrierException.class));
        Assert.assertEquals(0L, buffer.getAlignmentDurationNanos());
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[14], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[16], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[17], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // no further alignment should have happened
        Assert.assertEquals(0L, buffer.getAlignmentDurationNanos());
        // no further checkpoint (abort) notifications
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(ArgumentMatchers.any(CheckpointMetaData.class), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.anyLong(), ArgumentMatchers.any(CheckpointDeclineOnCancellationBarrierException.class));
        // all done
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
    }

    /**
     * This tests the where a replay of queued checkpoint barriers meets
     * a canceled checkpoint.
     *
     * <p>The replayed newer checkpoint barrier must not try to cancel the
     * already canceled checkpoint.
     */
    @Test
    public void testAbortWhileHavingQueuedBarriers() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // starting a checkpoint
        BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(1, 1), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), // queued barrier and cancellation barrier
        BarrierBufferTestBase.createBarrier(2, 1), // some queued buffers
        BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), // cancel the initial checkpoint
        BarrierBufferTestBase.createCancellationBarrier(1, 0), // some more buffers
        BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), // ignored barrier - already canceled and moved to next checkpoint
        BarrierBufferTestBase.createBarrier(1, 2), // some more buffers
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), // complete next checkpoint regularly
        BarrierBufferTestBase.createBarrier(2, 0), BarrierBufferTestBase.createBarrier(2, 2), // some more buffers
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        AbstractInvokable toNotify = Mockito.mock(AbstractInvokable.class);
        buffer.registerCheckpointEventHandler(toNotify);
        long startTs;
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // starting first checkpoint
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[2], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[3], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[6], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // cancelled by cancellation barrier
        BarrierBufferTestBase.check(sequence[4], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        Mockito.verify(toNotify).abortCheckpointOnBarrier(ArgumentMatchers.eq(1L), ArgumentMatchers.any(CheckpointDeclineOnCancellationBarrierException.class));
        // the next checkpoint alignment starts now
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[11], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[15], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint done
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        Mockito.verify(toNotify).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(2L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        // queued data
        BarrierBufferTestBase.check(sequence[10], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[14], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // trailing data
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[19], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[20], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // all done
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
        // check overall notifications
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(ArgumentMatchers.any(CheckpointMetaData.class), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Throwable.class));
    }

    /**
     * This tests the where a cancellation barrier is received for a checkpoint already
     * canceled due to receiving a newer checkpoint barrier.
     */
    @Test
    public void testIgnoreCancelBarrierIfCheckpointSubsumed() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // starting a checkpoint
        BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBarrier(3, 1), BarrierBufferTestBase.createBarrier(3, 0), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), // newer checkpoint barrier cancels/subsumes pending checkpoint
        BarrierBufferTestBase.createBarrier(5, 2), // some queued buffers
        BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), // cancel barrier the initial checkpoint /it is already canceled)
        BarrierBufferTestBase.createCancellationBarrier(3, 2), // some more buffers
        BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), // complete next checkpoint regularly
        BarrierBufferTestBase.createBarrier(5, 0), BarrierBufferTestBase.createBarrier(5, 1), // some more buffers
        BarrierBufferTestBase.createBuffer(0, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(1, BarrierBufferTestBase.PAGE_SIZE), BarrierBufferTestBase.createBuffer(2, BarrierBufferTestBase.PAGE_SIZE) };
        MockInputGate gate = new MockInputGate(BarrierBufferTestBase.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = createBarrierHandler(gate);
        AbstractInvokable toNotify = Mockito.mock(AbstractInvokable.class);
        buffer.registerCheckpointEventHandler(toNotify);
        long startTs;
        // validate the sequence
        BarrierBufferTestBase.check(sequence[0], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // beginning of first checkpoint
        BarrierBufferTestBase.check(sequence[5], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // future barrier aborts checkpoint
        startTs = System.nanoTime();
        BarrierBufferTestBase.check(sequence[3], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(3L), ArgumentMatchers.any(CheckpointDeclineSubsumedException.class));
        BarrierBufferTestBase.check(sequence[4], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // alignment of next checkpoint
        BarrierBufferTestBase.check(sequence[8], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[9], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[12], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[13], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // checkpoint finished
        BarrierBufferTestBase.check(sequence[7], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.validateAlignmentTime(startTs, buffer.getAlignmentDurationNanos());
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferTestBase.CheckpointMatcher(5L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        BarrierBufferTestBase.check(sequence[11], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // remaining data
        BarrierBufferTestBase.check(sequence[16], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[17], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        BarrierBufferTestBase.check(sequence[18], buffer.getNextNonBlocked(), BarrierBufferTestBase.PAGE_SIZE);
        // all done
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
        // check overall notifications
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(ArgumentMatchers.any(CheckpointMetaData.class), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Throwable.class));
    }

    // ------------------------------------------------------------------------
    // Testing Mocks
    // ------------------------------------------------------------------------
    /**
     * The invokable handler used for triggering checkpoint and validation.
     */
    private static class ValidatingCheckpointHandler extends AbstractInvokable {
        private long nextExpectedCheckpointId = -1L;

        private long lastReportedBytesBufferedInAlignment = -1;

        public ValidatingCheckpointHandler() {
            super(new DummyEnvironment("test", 1, 0));
        }

        public void setNextExpectedCheckpointId(long nextExpectedCheckpointId) {
            this.nextExpectedCheckpointId = nextExpectedCheckpointId;
        }

        public long getNextExpectedCheckpointId() {
            return nextExpectedCheckpointId;
        }

        long getLastReportedBytesBufferedInAlignment() {
            return lastReportedBytesBufferedInAlignment;
        }

        @Override
        public void invoke() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean triggerCheckpoint(CheckpointMetaData checkpointMetaData, CheckpointOptions checkpointOptions) throws Exception {
            throw new UnsupportedOperationException("should never be called");
        }

        @Override
        public void triggerCheckpointOnBarrier(CheckpointMetaData checkpointMetaData, CheckpointOptions checkpointOptions, CheckpointMetrics checkpointMetrics) throws Exception {
            Assert.assertTrue("wrong checkpoint id", (((nextExpectedCheckpointId) == (-1L)) || ((nextExpectedCheckpointId) == (checkpointMetaData.getCheckpointId()))));
            Assert.assertTrue(((checkpointMetaData.getTimestamp()) > 0));
            Assert.assertTrue(((checkpointMetrics.getBytesBufferedInAlignment()) >= 0));
            Assert.assertTrue(((checkpointMetrics.getAlignmentDurationNanos()) >= 0));
            (nextExpectedCheckpointId)++;
            lastReportedBytesBufferedInAlignment = checkpointMetrics.getBytesBufferedInAlignment();
        }

        @Override
        public void abortCheckpointOnBarrier(long checkpointId, Throwable cause) {
        }

        @Override
        public void notifyCheckpointComplete(long checkpointId) throws Exception {
            throw new UnsupportedOperationException("should never be called");
        }
    }

    /**
     * The matcher used for verifying checkpoint equality.
     */
    private static class CheckpointMatcher extends BaseMatcher<CheckpointMetaData> {
        private final long checkpointId;

        CheckpointMatcher(long checkpointId) {
            this.checkpointId = checkpointId;
        }

        @Override
        public boolean matches(Object o) {
            return ((o != null) && ((o.getClass()) == (CheckpointMetaData.class))) && ((getCheckpointId()) == (checkpointId));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(("CheckpointMetaData - id = " + (checkpointId)));
        }
    }
}

