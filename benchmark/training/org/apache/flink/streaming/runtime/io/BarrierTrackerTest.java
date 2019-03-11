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
import org.apache.flink.runtime.checkpoint.CheckpointMetaData;
import org.apache.flink.runtime.checkpoint.CheckpointMetrics;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.io.network.api.CancelCheckpointMarker;
import org.apache.flink.runtime.io.network.api.CheckpointBarrier;
import org.apache.flink.runtime.io.network.partition.consumer.BufferOrEvent;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.operators.testutils.DummyEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the behavior of the barrier tracker.
 */
public class BarrierTrackerTest {
    private static final int PAGE_SIZE = 512;

    @Test
    public void testSingleChannelNoBarriers() {
        try {
            BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0) };
            MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 1, Arrays.asList(sequence));
            BarrierTracker tracker = new BarrierTracker(gate);
            for (BufferOrEvent boe : sequence) {
                Assert.assertEquals(boe, tracker.getNextNonBlocked());
            }
            Assert.assertNull(tracker.getNextNonBlocked());
            Assert.assertNull(tracker.getNextNonBlocked());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMultiChannelNoBarriers() {
        try {
            BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(3), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(2) };
            MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 4, Arrays.asList(sequence));
            BarrierTracker tracker = new BarrierTracker(gate);
            for (BufferOrEvent boe : sequence) {
                Assert.assertEquals(boe, tracker.getNextNonBlocked());
            }
            Assert.assertNull(tracker.getNextNonBlocked());
            Assert.assertNull(tracker.getNextNonBlocked());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSingleChannelWithBarriers() {
        try {
            BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(1, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(2, 0), BarrierTrackerTest.createBarrier(3, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(4, 0), BarrierTrackerTest.createBarrier(5, 0), BarrierTrackerTest.createBarrier(6, 0), BarrierTrackerTest.createBuffer(0) };
            MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 1, Arrays.asList(sequence));
            BarrierTracker tracker = new BarrierTracker(gate);
            BarrierTrackerTest.CheckpointSequenceValidator validator = new BarrierTrackerTest.CheckpointSequenceValidator(1, 2, 3, 4, 5, 6);
            tracker.registerCheckpointEventHandler(validator);
            for (BufferOrEvent boe : sequence) {
                if ((boe.isBuffer()) || ((boe.getEvent().getClass()) != (CheckpointBarrier.class))) {
                    Assert.assertEquals(boe, tracker.getNextNonBlocked());
                }
            }
            Assert.assertNull(tracker.getNextNonBlocked());
            Assert.assertNull(tracker.getNextNonBlocked());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSingleChannelWithSkippedBarriers() {
        try {
            BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(1, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(3, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(4, 0), BarrierTrackerTest.createBarrier(6, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(7, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(10, 0), BarrierTrackerTest.createBuffer(0) };
            MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 1, Arrays.asList(sequence));
            BarrierTracker tracker = new BarrierTracker(gate);
            BarrierTrackerTest.CheckpointSequenceValidator validator = new BarrierTrackerTest.CheckpointSequenceValidator(1, 3, 4, 6, 7, 10);
            tracker.registerCheckpointEventHandler(validator);
            for (BufferOrEvent boe : sequence) {
                if ((boe.isBuffer()) || ((boe.getEvent().getClass()) != (CheckpointBarrier.class))) {
                    Assert.assertEquals(boe, tracker.getNextNonBlocked());
                }
            }
            Assert.assertNull(tracker.getNextNonBlocked());
            Assert.assertNull(tracker.getNextNonBlocked());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMultiChannelWithBarriers() {
        try {
            BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(1, 1), BarrierTrackerTest.createBarrier(1, 2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBarrier(1, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(2, 0), BarrierTrackerTest.createBarrier(2, 1), BarrierTrackerTest.createBarrier(2, 2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(3, 2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(3, 0), BarrierTrackerTest.createBarrier(3, 1), BarrierTrackerTest.createBarrier(4, 1), BarrierTrackerTest.createBarrier(4, 2), BarrierTrackerTest.createBarrier(4, 0), BarrierTrackerTest.createBuffer(0) };
            MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 3, Arrays.asList(sequence));
            BarrierTracker tracker = new BarrierTracker(gate);
            BarrierTrackerTest.CheckpointSequenceValidator validator = new BarrierTrackerTest.CheckpointSequenceValidator(1, 2, 3, 4);
            tracker.registerCheckpointEventHandler(validator);
            for (BufferOrEvent boe : sequence) {
                if ((boe.isBuffer()) || ((boe.getEvent().getClass()) != (CheckpointBarrier.class))) {
                    Assert.assertEquals(boe, tracker.getNextNonBlocked());
                }
            }
            Assert.assertNull(tracker.getNextNonBlocked());
            Assert.assertNull(tracker.getNextNonBlocked());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMultiChannelSkippingCheckpoints() {
        try {
            BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(1, 1), BarrierTrackerTest.createBarrier(1, 2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBarrier(1, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(2, 0), BarrierTrackerTest.createBarrier(2, 1), BarrierTrackerTest.createBarrier(2, 2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(3, 2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(2), // jump to checkpoint 4
            BarrierTrackerTest.createBarrier(4, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(4, 1), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBarrier(4, 2), BarrierTrackerTest.createBuffer(0) };
            MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 3, Arrays.asList(sequence));
            BarrierTracker tracker = new BarrierTracker(gate);
            BarrierTrackerTest.CheckpointSequenceValidator validator = new BarrierTrackerTest.CheckpointSequenceValidator(1, 2, 4);
            tracker.registerCheckpointEventHandler(validator);
            for (BufferOrEvent boe : sequence) {
                if ((boe.isBuffer()) || ((boe.getEvent().getClass()) != (CheckpointBarrier.class))) {
                    Assert.assertEquals(boe, tracker.getNextNonBlocked());
                }
            }
            Assert.assertNull(tracker.getNextNonBlocked());
            Assert.assertNull(tracker.getNextNonBlocked());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * This test validates that the barrier tracker does not immediately
     * discard a pending checkpoint as soon as it sees a barrier from a
     * later checkpoint from some channel.
     *
     * <p>This behavior is crucial, otherwise topologies where different inputs
     * have different latency (and that latency is close to or higher than the
     * checkpoint interval) may skip many checkpoints, or fail to complete a
     * checkpoint all together.
     */
    @Test
    public void testCompleteCheckpointsOnLateBarriers() {
        try {
            BufferOrEvent[] sequence = new BufferOrEvent[]{ // checkpoint 2
            BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(2, 1), BarrierTrackerTest.createBarrier(2, 0), BarrierTrackerTest.createBarrier(2, 2), // incomplete checkpoint 3
            BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(3, 1), BarrierTrackerTest.createBarrier(3, 2), // some barriers from checkpoint 4
            BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(4, 2), BarrierTrackerTest.createBarrier(4, 1), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(2), // last barrier from checkpoint 3
            BarrierTrackerTest.createBarrier(3, 0), // complete checkpoint 4
            BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(4, 0), // regular checkpoint 5
            BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(5, 1), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(5, 0), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBarrier(5, 2), // checkpoint 6 (incomplete),
            BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBarrier(6, 1), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(6, 0), // checkpoint 7, with early barriers for checkpoints 8 and 9
            BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBarrier(7, 1), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(7, 2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(8, 2), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(8, 1), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBarrier(9, 1), // complete checkpoint 7, first barriers from checkpoint 10
            BarrierTrackerTest.createBarrier(7, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(9, 2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(10, 2), // complete checkpoint 8 and 9
            BarrierTrackerTest.createBarrier(8, 0), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(9, 0), // trailing data
            BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(2) };
            MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 3, Arrays.asList(sequence));
            BarrierTracker tracker = new BarrierTracker(gate);
            BarrierTrackerTest.CheckpointSequenceValidator validator = new BarrierTrackerTest.CheckpointSequenceValidator(2, 3, 4, 5, 7, 8, 9);
            tracker.registerCheckpointEventHandler(validator);
            for (BufferOrEvent boe : sequence) {
                if ((boe.isBuffer()) || ((boe.getEvent().getClass()) != (CheckpointBarrier.class))) {
                    Assert.assertEquals(boe, tracker.getNextNonBlocked());
                }
            }
            Assert.assertNull(tracker.getNextNonBlocked());
            Assert.assertNull(tracker.getNextNonBlocked());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSingleChannelAbortCheckpoint() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(1, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(2, 0), BarrierTrackerTest.createCancellationBarrier(4, 0), BarrierTrackerTest.createBarrier(5, 0), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createCancellationBarrier(6, 0), BarrierTrackerTest.createBuffer(0) };
        MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 1, Arrays.asList(sequence));
        BarrierTracker tracker = new BarrierTracker(gate);
        // negative values mean an expected cancellation call!
        BarrierTrackerTest.CheckpointSequenceValidator validator = new BarrierTrackerTest.CheckpointSequenceValidator(1, 2, (-4), 5, (-6));
        tracker.registerCheckpointEventHandler(validator);
        for (BufferOrEvent boe : sequence) {
            if (boe.isBuffer()) {
                Assert.assertEquals(boe, tracker.getNextNonBlocked());
            }
            Assert.assertTrue(tracker.isEmpty());
        }
        Assert.assertNull(tracker.getNextNonBlocked());
        Assert.assertNull(tracker.getNextNonBlocked());
    }

    @Test
    public void testMultiChannelAbortCheckpoint() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // some buffers and a successful checkpoint
        BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(1, 1), BarrierTrackerTest.createBarrier(1, 2), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBarrier(1, 0), // aborted on last barrier
        BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(2, 0), BarrierTrackerTest.createBarrier(2, 2), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createCancellationBarrier(2, 1), // successful checkpoint
        BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBarrier(3, 1), BarrierTrackerTest.createBarrier(3, 2), BarrierTrackerTest.createBarrier(3, 0), // abort on first barrier
        BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createCancellationBarrier(4, 1), BarrierTrackerTest.createBarrier(4, 2), BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBarrier(4, 0), // another successful checkpoint
        BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createBuffer(2), BarrierTrackerTest.createBarrier(5, 2), BarrierTrackerTest.createBarrier(5, 1), BarrierTrackerTest.createBarrier(5, 0), // abort multiple cancellations and a barrier after the cancellations
        BarrierTrackerTest.createBuffer(0), BarrierTrackerTest.createBuffer(1), BarrierTrackerTest.createCancellationBarrier(6, 1), BarrierTrackerTest.createCancellationBarrier(6, 2), BarrierTrackerTest.createBarrier(6, 0), BarrierTrackerTest.createBuffer(0) };
        MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierTracker tracker = new BarrierTracker(gate);
        // negative values mean an expected cancellation call!
        BarrierTrackerTest.CheckpointSequenceValidator validator = new BarrierTrackerTest.CheckpointSequenceValidator(1, (-2), 3, (-4), 5, (-6));
        tracker.registerCheckpointEventHandler(validator);
        for (BufferOrEvent boe : sequence) {
            if (boe.isBuffer()) {
                Assert.assertEquals(boe, tracker.getNextNonBlocked());
            }
        }
        Assert.assertTrue(tracker.isEmpty());
        Assert.assertNull(tracker.getNextNonBlocked());
        Assert.assertNull(tracker.getNextNonBlocked());
        Assert.assertTrue(tracker.isEmpty());
    }

    /**
     * Tests that each checkpoint is only aborted once in case of an interleaved cancellation
     * barrier arrival of two consecutive checkpoints.
     */
    @Test
    public void testInterleavedCancellationBarriers() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ BarrierTrackerTest.createBarrier(1L, 0), BarrierTrackerTest.createCancellationBarrier(2L, 0), BarrierTrackerTest.createCancellationBarrier(1L, 1), BarrierTrackerTest.createCancellationBarrier(2L, 1), BarrierTrackerTest.createCancellationBarrier(1L, 2), BarrierTrackerTest.createCancellationBarrier(2L, 2), BarrierTrackerTest.createBuffer(0) };
        MockInputGate gate = new MockInputGate(BarrierTrackerTest.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierTracker tracker = new BarrierTracker(gate);
        AbstractInvokable statefulTask = Mockito.mock(AbstractInvokable.class);
        tracker.registerCheckpointEventHandler(statefulTask);
        for (BufferOrEvent boe : sequence) {
            if ((boe.isBuffer()) || (((boe.getEvent().getClass()) != (CheckpointBarrier.class)) && ((boe.getEvent().getClass()) != (CancelCheckpointMarker.class)))) {
                Assert.assertEquals(boe, tracker.getNextNonBlocked());
            }
        }
        Mockito.verify(statefulTask, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(1L), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(statefulTask, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(2L), ArgumentMatchers.any(Throwable.class));
    }

    // ------------------------------------------------------------------------
    // Testing Mocks
    // ------------------------------------------------------------------------
    private static class CheckpointSequenceValidator extends AbstractInvokable {
        private final long[] checkpointIDs;

        private int i = 0;

        private CheckpointSequenceValidator(long... checkpointIDs) {
            super(new DummyEnvironment("test", 1, 0));
            this.checkpointIDs = checkpointIDs;
        }

        @Override
        public void invoke() {
            throw new UnsupportedOperationException("should never be called");
        }

        @Override
        public boolean triggerCheckpoint(CheckpointMetaData checkpointMetaData, CheckpointOptions checkpointOptions) throws Exception {
            throw new UnsupportedOperationException("should never be called");
        }

        @Override
        public void triggerCheckpointOnBarrier(CheckpointMetaData checkpointMetaData, CheckpointOptions checkpointOptions, CheckpointMetrics checkpointMetrics) throws Exception {
            Assert.assertTrue("More checkpoints than expected", ((i) < (checkpointIDs.length)));
            final long expectedId = checkpointIDs[((i)++)];
            if (expectedId >= 0) {
                Assert.assertEquals("wrong checkpoint id", expectedId, checkpointMetaData.getCheckpointId());
                Assert.assertTrue(((checkpointMetaData.getTimestamp()) > 0));
            } else {
                Assert.fail("got 'triggerCheckpointOnBarrier()' when expecting an 'abortCheckpointOnBarrier()'");
            }
        }

        @Override
        public void abortCheckpointOnBarrier(long checkpointId, Throwable cause) {
            Assert.assertTrue("More checkpoints than expected", ((i) < (checkpointIDs.length)));
            final long expectedId = checkpointIDs[((i)++)];
            if (expectedId < 0) {
                Assert.assertEquals("wrong checkpoint id for checkpoint abort", (-expectedId), checkpointId);
            } else {
                Assert.fail("got 'abortCheckpointOnBarrier()' when expecting an 'triggerCheckpointOnBarrier()'");
            }
        }

        @Override
        public void notifyCheckpointComplete(long checkpointId) throws Exception {
            throw new UnsupportedOperationException("should never be called");
        }
    }
}

