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
package org.apache.flink.runtime.checkpoint;


import JobStatus.FINISHED;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.jobgraph.JobStatus;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.state.SharedStateRegistry;
import org.apache.flink.runtime.state.testutils.TestCompletedCheckpointStorageLocation;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for basic {@link CompletedCheckpointStore} contract.
 */
public abstract class CompletedCheckpointStoreTest extends TestLogger {
    // ---------------------------------------------------------------------------------------------
    /**
     * Tests that at least one checkpoint needs to be retained.
     */
    @Test(expected = Exception.class)
    public void testExceptionOnNoRetainedCheckpoints() throws Exception {
        createCompletedCheckpoints(0);
    }

    /**
     * Tests adding and getting a checkpoint.
     */
    @Test
    public void testAddAndGetLatestCheckpoint() throws Exception {
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        CompletedCheckpointStore checkpoints = createCompletedCheckpoints(4);
        // Empty state
        Assert.assertEquals(0, checkpoints.getNumberOfRetainedCheckpoints());
        Assert.assertEquals(0, checkpoints.getAllCheckpoints().size());
        CompletedCheckpointStoreTest.TestCompletedCheckpoint[] expected = new CompletedCheckpointStoreTest.TestCompletedCheckpoint[]{ CompletedCheckpointStoreTest.createCheckpoint(0, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(1, sharedStateRegistry) };
        // Add and get latest
        checkpoints.addCheckpoint(expected[0]);
        Assert.assertEquals(1, checkpoints.getNumberOfRetainedCheckpoints());
        verifyCheckpoint(expected[0], checkpoints.getLatestCheckpoint());
        checkpoints.addCheckpoint(expected[1]);
        Assert.assertEquals(2, checkpoints.getNumberOfRetainedCheckpoints());
        verifyCheckpoint(expected[1], checkpoints.getLatestCheckpoint());
    }

    /**
     * Tests that adding more checkpoints than retained discards the correct checkpoints (using
     * the correct class loader).
     */
    @Test
    public void testAddCheckpointMoreThanMaxRetained() throws Exception {
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        CompletedCheckpointStore checkpoints = createCompletedCheckpoints(1);
        CompletedCheckpointStoreTest.TestCompletedCheckpoint[] expected = new CompletedCheckpointStoreTest.TestCompletedCheckpoint[]{ CompletedCheckpointStoreTest.createCheckpoint(0, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(1, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(2, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(3, sharedStateRegistry) };
        // Add checkpoints
        checkpoints.addCheckpoint(expected[0]);
        Assert.assertEquals(1, checkpoints.getNumberOfRetainedCheckpoints());
        for (int i = 1; i < (expected.length); i++) {
            Collection<OperatorState> taskStates = getOperatorStates().values();
            checkpoints.addCheckpoint(expected[i]);
            // The ZooKeeper implementation discards asynchronously
            expected[(i - 1)].awaitDiscard();
            Assert.assertTrue(expected[(i - 1)].isDiscarded());
            Assert.assertEquals(1, checkpoints.getNumberOfRetainedCheckpoints());
        }
    }

    /**
     * Tests that
     * <ul>
     * <li>{@link CompletedCheckpointStore#getLatestCheckpoint()} returns <code>null</code>,</li>
     * <li>{@link CompletedCheckpointStore#getAllCheckpoints()} returns an empty list,</li>
     * <li>{@link CompletedCheckpointStore#getNumberOfRetainedCheckpoints()} returns 0.</li>
     * </ul>
     */
    @Test
    public void testEmptyState() throws Exception {
        CompletedCheckpointStore checkpoints = createCompletedCheckpoints(1);
        Assert.assertNull(checkpoints.getLatestCheckpoint());
        Assert.assertEquals(0, checkpoints.getAllCheckpoints().size());
        Assert.assertEquals(0, checkpoints.getNumberOfRetainedCheckpoints());
    }

    /**
     * Tests that all added checkpoints are returned.
     */
    @Test
    public void testGetAllCheckpoints() throws Exception {
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        CompletedCheckpointStore checkpoints = createCompletedCheckpoints(4);
        CompletedCheckpointStoreTest.TestCompletedCheckpoint[] expected = new CompletedCheckpointStoreTest.TestCompletedCheckpoint[]{ CompletedCheckpointStoreTest.createCheckpoint(0, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(1, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(2, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(3, sharedStateRegistry) };
        for (CompletedCheckpointStoreTest.TestCompletedCheckpoint checkpoint : expected) {
            checkpoints.addCheckpoint(checkpoint);
        }
        List<CompletedCheckpoint> actual = checkpoints.getAllCheckpoints();
        Assert.assertEquals(expected.length, actual.size());
        for (int i = 0; i < (expected.length); i++) {
            Assert.assertEquals(expected[i], actual.get(i));
        }
    }

    /**
     * Tests that all checkpoints are discarded (using the correct class loader).
     */
    @Test
    public void testDiscardAllCheckpoints() throws Exception {
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        CompletedCheckpointStore checkpoints = createCompletedCheckpoints(4);
        CompletedCheckpointStoreTest.TestCompletedCheckpoint[] expected = new CompletedCheckpointStoreTest.TestCompletedCheckpoint[]{ CompletedCheckpointStoreTest.createCheckpoint(0, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(1, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(2, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(3, sharedStateRegistry) };
        for (CompletedCheckpointStoreTest.TestCompletedCheckpoint checkpoint : expected) {
            checkpoints.addCheckpoint(checkpoint);
        }
        checkpoints.shutdown(FINISHED);
        // Empty state
        Assert.assertNull(checkpoints.getLatestCheckpoint());
        Assert.assertEquals(0, checkpoints.getAllCheckpoints().size());
        Assert.assertEquals(0, checkpoints.getNumberOfRetainedCheckpoints());
        // All have been discarded
        for (CompletedCheckpointStoreTest.TestCompletedCheckpoint checkpoint : expected) {
            // The ZooKeeper implementation discards asynchronously
            checkpoint.awaitDiscard();
            Assert.assertTrue(checkpoint.isDiscarded());
        }
    }

    /**
     * A test {@link CompletedCheckpoint}. We want to verify that the correct class loader is
     * used when discarding. Spying on a regular {@link CompletedCheckpoint} instance with
     * Mockito doesn't work, because it it breaks serializability.
     */
    protected static class TestCompletedCheckpoint extends CompletedCheckpoint {
        private static final long serialVersionUID = 4211419809665983026L;

        private boolean isDiscarded;

        // Latch for test variants which discard asynchronously
        private final transient CountDownLatch discardLatch = new CountDownLatch(1);

        public TestCompletedCheckpoint(JobID jobId, long checkpointId, long timestamp, Map<OperatorID, OperatorState> operatorGroupState, CheckpointProperties props) {
            super(jobId, checkpointId, timestamp, Long.MAX_VALUE, operatorGroupState, null, props, new TestCompletedCheckpointStorageLocation());
        }

        @Override
        public boolean discardOnSubsume() throws Exception {
            if (super.discardOnSubsume()) {
                discard();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean discardOnShutdown(JobStatus jobStatus) throws Exception {
            if (super.discardOnShutdown(jobStatus)) {
                discard();
                return true;
            } else {
                return false;
            }
        }

        void discard() {
            if (!(isDiscarded)) {
                this.isDiscarded = true;
                if ((discardLatch) != null) {
                    discardLatch.countDown();
                }
            }
        }

        public boolean isDiscarded() {
            return isDiscarded;
        }

        public void awaitDiscard() throws InterruptedException {
            if ((discardLatch) != null) {
                discardLatch.await();
            }
        }

        public boolean awaitDiscard(long timeout) throws InterruptedException {
            if ((discardLatch) != null) {
                return discardLatch.await(timeout, TimeUnit.MILLISECONDS);
            } else {
                return false;
            }
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            CompletedCheckpointStoreTest.TestCompletedCheckpoint that = ((CompletedCheckpointStoreTest.TestCompletedCheckpoint) (o));
            return (getJobId().equals(getJobId())) && ((getCheckpointID()) == (getCheckpointID()));
        }

        @Override
        public int hashCode() {
            return (getJobId().hashCode()) + ((int) (getCheckpointID()));
        }
    }

    public static class TestOperatorSubtaskState extends OperatorSubtaskState {
        private static final long serialVersionUID = 522580433699164230L;

        boolean registered;

        boolean discarded;

        public TestOperatorSubtaskState() {
            super();
            this.registered = false;
            this.discarded = false;
        }

        @Override
        public void discardState() {
            super.discardState();
            Assert.assertFalse(discarded);
            discarded = true;
            registered = false;
        }

        @Override
        public void registerSharedStates(SharedStateRegistry sharedStateRegistry) {
            super.registerSharedStates(sharedStateRegistry);
            Assert.assertFalse(discarded);
            registered = true;
        }

        public void reset() {
            registered = false;
            discarded = false;
        }

        public boolean isRegistered() {
            return registered;
        }

        public boolean isDiscarded() {
            return discarded;
        }
    }
}

