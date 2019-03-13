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
import JobStatus.SUSPENDED;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.framework.CuratorFramework;
import org.apache.flink.runtime.state.RetrievableStateHandle;
import org.apache.flink.runtime.state.SharedStateRegistry;
import org.apache.flink.runtime.zookeeper.RetrievableStateStorageHelper;
import org.apache.flink.runtime.zookeeper.ZooKeeperTestEnvironment;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for basic {@link CompletedCheckpointStore} contract and ZooKeeper state handling.
 */
public class ZooKeeperCompletedCheckpointStoreITCase extends CompletedCheckpointStoreTest {
    private static final ZooKeeperTestEnvironment ZOOKEEPER = new ZooKeeperTestEnvironment(1);

    private static final String CHECKPOINT_PATH = "/checkpoints";

    // ---------------------------------------------------------------------------------------------
    /**
     * Tests that older checkpoints are not cleaned up right away when recovering. Only after
     * another checkpointed has been completed the old checkpoints exceeding the number of
     * checkpoints to retain will be removed.
     */
    @Test
    public void testRecover() throws Exception {
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        CompletedCheckpointStore checkpoints = createCompletedCheckpoints(3);
        CompletedCheckpointStoreTest.TestCompletedCheckpoint[] expected = new CompletedCheckpointStoreTest.TestCompletedCheckpoint[]{ CompletedCheckpointStoreTest.createCheckpoint(0, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(1, sharedStateRegistry), CompletedCheckpointStoreTest.createCheckpoint(2, sharedStateRegistry) };
        // Add multiple checkpoints
        checkpoints.addCheckpoint(expected[0]);
        checkpoints.addCheckpoint(expected[1]);
        checkpoints.addCheckpoint(expected[2]);
        verifyCheckpointRegistered(getOperatorStates().values(), sharedStateRegistry);
        verifyCheckpointRegistered(getOperatorStates().values(), sharedStateRegistry);
        verifyCheckpointRegistered(getOperatorStates().values(), sharedStateRegistry);
        // All three should be in ZK
        Assert.assertEquals(3, ZooKeeperCompletedCheckpointStoreITCase.ZOOKEEPER.getClient().getChildren().forPath(ZooKeeperCompletedCheckpointStoreITCase.CHECKPOINT_PATH).size());
        Assert.assertEquals(3, checkpoints.getNumberOfRetainedCheckpoints());
        // Recover
        sharedStateRegistry.close();
        sharedStateRegistry = new SharedStateRegistry();
        checkpoints.recover();
        Assert.assertEquals(3, ZooKeeperCompletedCheckpointStoreITCase.ZOOKEEPER.getClient().getChildren().forPath(ZooKeeperCompletedCheckpointStoreITCase.CHECKPOINT_PATH).size());
        Assert.assertEquals(3, checkpoints.getNumberOfRetainedCheckpoints());
        Assert.assertEquals(expected[2], checkpoints.getLatestCheckpoint());
        List<CompletedCheckpoint> expectedCheckpoints = new ArrayList<>(3);
        expectedCheckpoints.add(expected[1]);
        expectedCheckpoints.add(expected[2]);
        expectedCheckpoints.add(CompletedCheckpointStoreTest.createCheckpoint(3, sharedStateRegistry));
        checkpoints.addCheckpoint(expectedCheckpoints.get(2));
        List<CompletedCheckpoint> actualCheckpoints = checkpoints.getAllCheckpoints();
        Assert.assertEquals(expectedCheckpoints, actualCheckpoints);
        for (CompletedCheckpoint actualCheckpoint : actualCheckpoints) {
            verifyCheckpointRegistered(actualCheckpoint.getOperatorStates().values(), sharedStateRegistry);
        }
    }

    /**
     * Tests that shutdown discards all checkpoints.
     */
    @Test
    public void testShutdownDiscardsCheckpoints() throws Exception {
        CuratorFramework client = ZooKeeperCompletedCheckpointStoreITCase.ZOOKEEPER.getClient();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        CompletedCheckpointStore store = createCompletedCheckpoints(1);
        CompletedCheckpointStoreTest.TestCompletedCheckpoint checkpoint = CompletedCheckpointStoreTest.createCheckpoint(0, sharedStateRegistry);
        store.addCheckpoint(checkpoint);
        Assert.assertEquals(1, store.getNumberOfRetainedCheckpoints());
        Assert.assertNotNull(client.checkExists().forPath(((ZooKeeperCompletedCheckpointStoreITCase.CHECKPOINT_PATH) + (ZooKeeperCompletedCheckpointStore.checkpointIdToPath(getCheckpointID())))));
        store.shutdown(FINISHED);
        Assert.assertEquals(0, store.getNumberOfRetainedCheckpoints());
        Assert.assertNull(client.checkExists().forPath(((ZooKeeperCompletedCheckpointStoreITCase.CHECKPOINT_PATH) + (ZooKeeperCompletedCheckpointStore.checkpointIdToPath(getCheckpointID())))));
        sharedStateRegistry.close();
        store.recover();
        Assert.assertEquals(0, store.getNumberOfRetainedCheckpoints());
    }

    /**
     * Tests that suspends keeps all checkpoints (so that they can be recovered
     * later by the ZooKeeper store). Furthermore, suspending a job should release
     * all locks.
     */
    @Test
    public void testSuspendKeepsCheckpoints() throws Exception {
        CuratorFramework client = ZooKeeperCompletedCheckpointStoreITCase.ZOOKEEPER.getClient();
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        CompletedCheckpointStore store = createCompletedCheckpoints(1);
        CompletedCheckpointStoreTest.TestCompletedCheckpoint checkpoint = CompletedCheckpointStoreTest.createCheckpoint(0, sharedStateRegistry);
        store.addCheckpoint(checkpoint);
        Assert.assertEquals(1, store.getNumberOfRetainedCheckpoints());
        Assert.assertNotNull(client.checkExists().forPath(((ZooKeeperCompletedCheckpointStoreITCase.CHECKPOINT_PATH) + (ZooKeeperCompletedCheckpointStore.checkpointIdToPath(getCheckpointID())))));
        store.shutdown(SUSPENDED);
        Assert.assertEquals(0, store.getNumberOfRetainedCheckpoints());
        final String checkpointPath = (ZooKeeperCompletedCheckpointStoreITCase.CHECKPOINT_PATH) + (ZooKeeperCompletedCheckpointStore.checkpointIdToPath(getCheckpointID()));
        Stat stat = client.checkExists().forPath(checkpointPath);
        Assert.assertNotNull("The checkpoint node should exist.", stat);
        Assert.assertEquals("The checkpoint node should not be locked.", 0, stat.getNumChildren());
        // Recover again
        sharedStateRegistry.close();
        store.recover();
        CompletedCheckpoint recovered = store.getLatestCheckpoint();
        Assert.assertEquals(checkpoint, recovered);
    }

    /**
     * FLINK-6284
     *
     * Tests that the latest recovered checkpoint is the one with the highest checkpoint id
     */
    @Test
    public void testLatestCheckpointRecovery() throws Exception {
        final int numCheckpoints = 3;
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        CompletedCheckpointStore checkpointStore = createCompletedCheckpoints(numCheckpoints);
        List<CompletedCheckpoint> checkpoints = new ArrayList<>(numCheckpoints);
        checkpoints.add(CompletedCheckpointStoreTest.createCheckpoint(9, sharedStateRegistry));
        checkpoints.add(CompletedCheckpointStoreTest.createCheckpoint(10, sharedStateRegistry));
        checkpoints.add(CompletedCheckpointStoreTest.createCheckpoint(11, sharedStateRegistry));
        for (CompletedCheckpoint checkpoint : checkpoints) {
            checkpointStore.addCheckpoint(checkpoint);
        }
        sharedStateRegistry.close();
        checkpointStore.recover();
        CompletedCheckpoint latestCheckpoint = checkpointStore.getLatestCheckpoint();
        Assert.assertEquals(checkpoints.get(((checkpoints.size()) - 1)), latestCheckpoint);
    }

    /**
     * FLINK-6612
     *
     * Checks that a concurrent checkpoint completion won't discard a checkpoint which has been
     * recovered by a different completed checkpoint store.
     */
    @Test
    public void testConcurrentCheckpointOperations() throws Exception {
        final int numberOfCheckpoints = 1;
        final long waitingTimeout = 50L;
        ZooKeeperCompletedCheckpointStore zkCheckpointStore1 = createCompletedCheckpoints(numberOfCheckpoints);
        ZooKeeperCompletedCheckpointStore zkCheckpointStore2 = createCompletedCheckpoints(numberOfCheckpoints);
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        CompletedCheckpointStoreTest.TestCompletedCheckpoint completedCheckpoint = CompletedCheckpointStoreTest.createCheckpoint(1, sharedStateRegistry);
        // complete the first checkpoint
        zkCheckpointStore1.addCheckpoint(completedCheckpoint);
        // recover the checkpoint by a different checkpoint store
        sharedStateRegistry.close();
        sharedStateRegistry = new SharedStateRegistry();
        zkCheckpointStore2.recover();
        CompletedCheckpoint recoveredCheckpoint = zkCheckpointStore2.getLatestCheckpoint();
        Assert.assertTrue((recoveredCheckpoint instanceof CompletedCheckpointStoreTest.TestCompletedCheckpoint));
        CompletedCheckpointStoreTest.TestCompletedCheckpoint recoveredTestCheckpoint = ((CompletedCheckpointStoreTest.TestCompletedCheckpoint) (recoveredCheckpoint));
        // Check that the recovered checkpoint is not yet discarded
        Assert.assertFalse(recoveredTestCheckpoint.isDiscarded());
        // complete another checkpoint --> this should remove the first checkpoint from the store
        // because the number of retained checkpoints == 1
        CompletedCheckpointStoreTest.TestCompletedCheckpoint completedCheckpoint2 = CompletedCheckpointStoreTest.createCheckpoint(2, sharedStateRegistry);
        zkCheckpointStore1.addCheckpoint(completedCheckpoint2);
        List<CompletedCheckpoint> allCheckpoints = zkCheckpointStore1.getAllCheckpoints();
        // check that we have removed the first checkpoint from zkCompletedStore1
        Assert.assertEquals(Collections.singletonList(completedCheckpoint2), allCheckpoints);
        // lets wait a little bit to see that no discard operation will be executed
        Assert.assertFalse("The checkpoint should not have been discarded.", recoveredTestCheckpoint.awaitDiscard(waitingTimeout));
        // check that we have not discarded the first completed checkpoint
        Assert.assertFalse(recoveredTestCheckpoint.isDiscarded());
        CompletedCheckpointStoreTest.TestCompletedCheckpoint completedCheckpoint3 = CompletedCheckpointStoreTest.createCheckpoint(3, sharedStateRegistry);
        // this should release the last lock on completedCheckpoint and thus discard it
        zkCheckpointStore2.addCheckpoint(completedCheckpoint3);
        // the checkpoint should be discarded eventually because there is no lock on it anymore
        recoveredTestCheckpoint.awaitDiscard();
    }

    static class HeapStateStorageHelper implements RetrievableStateStorageHelper<CompletedCheckpoint> {
        @Override
        public RetrievableStateHandle<CompletedCheckpoint> store(CompletedCheckpoint state) throws Exception {
            return new ZooKeeperCompletedCheckpointStoreITCase.HeapRetrievableStateHandle(state);
        }
    }

    static class HeapRetrievableStateHandle<T extends Serializable> implements RetrievableStateHandle<T> {
        private static final long serialVersionUID = -268548467968932L;

        private static AtomicInteger nextKey = new AtomicInteger(0);

        private static HashMap<Integer, Object> stateMap = new HashMap<>();

        private final int key;

        public HeapRetrievableStateHandle(T state) {
            key = ZooKeeperCompletedCheckpointStoreITCase.HeapRetrievableStateHandle.nextKey.getAndIncrement();
            ZooKeeperCompletedCheckpointStoreITCase.HeapRetrievableStateHandle.stateMap.put(key, state);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T retrieveState() {
            return ((T) (ZooKeeperCompletedCheckpointStoreITCase.HeapRetrievableStateHandle.stateMap.get(key)));
        }

        @Override
        public void discardState() throws Exception {
            ZooKeeperCompletedCheckpointStoreITCase.HeapRetrievableStateHandle.stateMap.remove(key);
        }

        @Override
        public long getStateSize() {
            return 0;
        }
    }
}

