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
import org.apache.flink.runtime.checkpoint.decline.AlignmentLimitExceededException;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.io.network.partition.consumer.BufferOrEvent;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;


/**
 * Tests for the barrier buffer's maximum limit of buffered/spilled bytes.
 */
public class BarrierBufferAlignmentLimitTest {
    private static final int PAGE_SIZE = 512;

    private static final Random RND = new Random();

    private static IOManager ioManager;

    // ------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------
    /**
     * This tests that a single alignment that buffers too much data cancels.
     */
    @Test
    public void testBreakCheckpointAtAlignmentLimit() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // some initial buffers
        BarrierBufferAlignmentLimitTest.createBuffer(1, 100), BarrierBufferAlignmentLimitTest.createBuffer(2, 70), BarrierBufferAlignmentLimitTest.createBuffer(0, 42), BarrierBufferAlignmentLimitTest.createBuffer(2, 111), // starting a checkpoint
        BarrierBufferAlignmentLimitTest.createBarrier(7, 1), BarrierBufferAlignmentLimitTest.createBuffer(1, 100), BarrierBufferAlignmentLimitTest.createBuffer(2, 200), BarrierBufferAlignmentLimitTest.createBuffer(1, 300), BarrierBufferAlignmentLimitTest.createBuffer(0, 50), BarrierBufferAlignmentLimitTest.createBarrier(7, 0), BarrierBufferAlignmentLimitTest.createBuffer(2, 100), BarrierBufferAlignmentLimitTest.createBuffer(0, 100), BarrierBufferAlignmentLimitTest.createBuffer(1, 200), BarrierBufferAlignmentLimitTest.createBuffer(0, 200), // this buffer makes the alignment spill too large
        BarrierBufferAlignmentLimitTest.createBuffer(0, 101), // additional data
        BarrierBufferAlignmentLimitTest.createBuffer(0, 100), BarrierBufferAlignmentLimitTest.createBuffer(1, 100), BarrierBufferAlignmentLimitTest.createBuffer(2, 100), // checkpoint completes - this should not result in a "completion notification"
        BarrierBufferAlignmentLimitTest.createBarrier(7, 2), // trailing buffers
        BarrierBufferAlignmentLimitTest.createBuffer(0, 100), BarrierBufferAlignmentLimitTest.createBuffer(1, 100), BarrierBufferAlignmentLimitTest.createBuffer(2, 100) };
        // the barrier buffer has a limit that only 1000 bytes may be spilled in alignment
        MockInputGate gate = new MockInputGate(BarrierBufferAlignmentLimitTest.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = new BarrierBuffer(gate, new BufferSpiller(BarrierBufferAlignmentLimitTest.ioManager, gate.getPageSize()), 1000);
        AbstractInvokable toNotify = Mockito.mock(AbstractInvokable.class);
        buffer.registerCheckpointEventHandler(toNotify);
        // validating the sequence of buffers
        BarrierBufferAlignmentLimitTest.check(sequence[0], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[1], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[2], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[3], buffer.getNextNonBlocked());
        // start of checkpoint
        long startTs = System.nanoTime();
        BarrierBufferAlignmentLimitTest.check(sequence[6], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[8], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[10], buffer.getNextNonBlocked());
        // trying to pull the next makes the alignment overflow - so buffered buffers are replayed
        BarrierBufferAlignmentLimitTest.check(sequence[5], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.validateAlignmentTime(startTs, buffer);
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(7L), ArgumentMatchers.any(AlignmentLimitExceededException.class));
        // playing back buffered events
        BarrierBufferAlignmentLimitTest.check(sequence[7], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[11], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[12], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[13], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[14], buffer.getNextNonBlocked());
        // the additional data
        BarrierBufferAlignmentLimitTest.check(sequence[15], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[16], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[17], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[19], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[20], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[21], buffer.getNextNonBlocked());
        // no call for a completed checkpoint must have happened
        Mockito.verify(toNotify, Mockito.times(0)).triggerCheckpointOnBarrier(ArgumentMatchers.any(CheckpointMetaData.class), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
        BarrierBufferAlignmentLimitTest.checkNoTempFilesRemain();
    }

    /**
     * This tests the following case:
     *   - an alignment starts
     *   - barriers from a second checkpoint queue before the first completes
     *   - together they are larger than the threshold
     *   - after the first checkpoint (with second checkpoint data queued) aborts, the second completes.
     */
    @Test
    public void testAlignmentLimitWithQueuedAlignments() throws Exception {
        BufferOrEvent[] sequence = new BufferOrEvent[]{ // some initial buffers
        BarrierBufferAlignmentLimitTest.createBuffer(1, 100), BarrierBufferAlignmentLimitTest.createBuffer(2, 70), // starting a checkpoint
        BarrierBufferAlignmentLimitTest.createBarrier(3, 2), BarrierBufferAlignmentLimitTest.createBuffer(1, 100), BarrierBufferAlignmentLimitTest.createBuffer(2, 100), BarrierBufferAlignmentLimitTest.createBarrier(3, 0), BarrierBufferAlignmentLimitTest.createBuffer(0, 100), BarrierBufferAlignmentLimitTest.createBuffer(1, 100), // queue some data from the next checkpoint
        BarrierBufferAlignmentLimitTest.createBarrier(4, 0), BarrierBufferAlignmentLimitTest.createBuffer(0, 100), BarrierBufferAlignmentLimitTest.createBuffer(0, 120), BarrierBufferAlignmentLimitTest.createBuffer(1, 100), // this one makes the alignment overflow
        BarrierBufferAlignmentLimitTest.createBuffer(2, 100), // checkpoint completed
        BarrierBufferAlignmentLimitTest.createBarrier(3, 1), // more for the next checkpoint
        BarrierBufferAlignmentLimitTest.createBarrier(4, 1), BarrierBufferAlignmentLimitTest.createBuffer(0, 100), BarrierBufferAlignmentLimitTest.createBuffer(1, 100), BarrierBufferAlignmentLimitTest.createBuffer(2, 100), // next checkpoint completes
        BarrierBufferAlignmentLimitTest.createBarrier(4, 2), // trailing data
        BarrierBufferAlignmentLimitTest.createBuffer(0, 100), BarrierBufferAlignmentLimitTest.createBuffer(1, 100), BarrierBufferAlignmentLimitTest.createBuffer(2, 100) };
        // the barrier buffer has a limit that only 1000 bytes may be spilled in alignment
        MockInputGate gate = new MockInputGate(BarrierBufferAlignmentLimitTest.PAGE_SIZE, 3, Arrays.asList(sequence));
        BarrierBuffer buffer = new BarrierBuffer(gate, new BufferSpiller(BarrierBufferAlignmentLimitTest.ioManager, gate.getPageSize()), 500);
        AbstractInvokable toNotify = Mockito.mock(AbstractInvokable.class);
        buffer.registerCheckpointEventHandler(toNotify);
        // validating the sequence of buffers
        long startTs;
        BarrierBufferAlignmentLimitTest.check(sequence[0], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[1], buffer.getNextNonBlocked());
        // start of checkpoint
        startTs = System.nanoTime();
        BarrierBufferAlignmentLimitTest.check(sequence[3], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[7], buffer.getNextNonBlocked());
        // next checkpoint also in progress
        BarrierBufferAlignmentLimitTest.check(sequence[11], buffer.getNextNonBlocked());
        // checkpoint alignment aborted due to too much data
        BarrierBufferAlignmentLimitTest.check(sequence[4], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.validateAlignmentTime(startTs, buffer);
        Mockito.verify(toNotify, Mockito.times(1)).abortCheckpointOnBarrier(ArgumentMatchers.eq(3L), ArgumentMatchers.any(AlignmentLimitExceededException.class));
        // replay buffered data - in the middle, the alignment for checkpoint 4 starts
        BarrierBufferAlignmentLimitTest.check(sequence[6], buffer.getNextNonBlocked());
        startTs = System.nanoTime();
        BarrierBufferAlignmentLimitTest.check(sequence[12], buffer.getNextNonBlocked());
        // only checkpoint 4 is pending now - the last checkpoint 3 barrier will not trigger success
        BarrierBufferAlignmentLimitTest.check(sequence[17], buffer.getNextNonBlocked());
        // checkpoint 4 completed - check and validate buffered replay
        BarrierBufferAlignmentLimitTest.check(sequence[9], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.validateAlignmentTime(startTs, buffer);
        Mockito.verify(toNotify, Mockito.times(1)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferAlignmentLimitTest.CheckpointMatcher(4L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        BarrierBufferAlignmentLimitTest.check(sequence[10], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[15], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[16], buffer.getNextNonBlocked());
        // trailing data
        BarrierBufferAlignmentLimitTest.check(sequence[19], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[20], buffer.getNextNonBlocked());
        BarrierBufferAlignmentLimitTest.check(sequence[21], buffer.getNextNonBlocked());
        // only checkpoint 4 was successfully completed, not checkpoint 3
        Mockito.verify(toNotify, Mockito.times(0)).triggerCheckpointOnBarrier(MockitoHamcrest.argThat(new BarrierBufferAlignmentLimitTest.CheckpointMatcher(3L)), ArgumentMatchers.any(CheckpointOptions.class), ArgumentMatchers.any(CheckpointMetrics.class));
        Assert.assertNull(buffer.getNextNonBlocked());
        Assert.assertNull(buffer.getNextNonBlocked());
        buffer.cleanup();
        BarrierBufferAlignmentLimitTest.checkNoTempFilesRemain();
    }

    /**
     * A validation matcher for checkpoint metadata against checkpoint IDs.
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

