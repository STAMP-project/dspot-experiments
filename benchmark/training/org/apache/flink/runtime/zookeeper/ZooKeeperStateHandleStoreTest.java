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
package org.apache.flink.runtime.zookeeper;


import HighAvailabilityOptions.HA_ZOOKEEPER_QUORUM;
import HighAvailabilityOptions.HA_ZOOKEEPER_ROOT;
import HighAvailabilityOptions.ZOOKEEPER_SESSION_TIMEOUT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.curator.framework.CuratorFramework;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.RetrievableStateHandle;
import org.apache.flink.runtime.util.ZooKeeperUtils;
import org.apache.flink.util.InstantiationUtil;
import org.apache.flink.util.TestLogger;
import org.apache.zookeeper.data.Stat;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for basic {@link ZooKeeperStateHandleStore} behaviour.
 *
 * <p> Tests include:
 * <ul>
 * <li>Expected usage of operations</li>
 * <li>Correct ordering of ZooKeeper and state handle operations</li>
 * </ul>
 */
public class ZooKeeperStateHandleStoreTest extends TestLogger {
    private static final ZooKeeperTestEnvironment ZOOKEEPER = new ZooKeeperTestEnvironment(1);

    /**
     * Tests add operation with lock.
     */
    @Test
    public void testAddAndLock() throws Exception {
        ZooKeeperStateHandleStoreTest.LongStateStorage longStateStorage = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), longStateStorage);
        // Config
        final String pathInZooKeeper = "/testAdd";
        final Long state = 1239712317L;
        // Test
        store.addAndLock(pathInZooKeeper, state);
        // Verify
        // State handle created
        Assert.assertEquals(1, store.getAllAndLock().size());
        Assert.assertEquals(state, store.getAndLock(pathInZooKeeper).retrieveState());
        // Path created and is persistent
        Stat stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(pathInZooKeeper);
        Assert.assertNotNull(stat);
        Assert.assertEquals(0, stat.getEphemeralOwner());
        List<String> children = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().getChildren().forPath(pathInZooKeeper);
        // there should be one child which is the lock
        Assert.assertEquals(1, children.size());
        stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(((pathInZooKeeper + '/') + (children.get(0))));
        Assert.assertNotNull(stat);
        // check that the child is an ephemeral node
        Assert.assertNotEquals(0, stat.getEphemeralOwner());
        // Data is equal
        @SuppressWarnings("unchecked")
        Long actual = ((RetrievableStateHandle<Long>) (InstantiationUtil.deserializeObject(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().getData().forPath(pathInZooKeeper), ClassLoader.getSystemClassLoader()))).retrieveState();
        Assert.assertEquals(state, actual);
    }

    /**
     * Tests that an existing path throws an Exception.
     */
    @Test(expected = Exception.class)
    public void testAddAlreadyExistingPath() throws Exception {
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateHandleProvider);
        ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().create().forPath("/testAddAlreadyExistingPath");
        store.addAndLock("/testAddAlreadyExistingPath", 1L);
        // writing to the state storage should have succeeded
        Assert.assertEquals(1, stateHandleProvider.getStateHandles());
        // the created state handle should have been cleaned up if the add operation failed
        Assert.assertEquals(1, stateHandleProvider.getStateHandles().get(0).getNumberOfDiscardCalls());
    }

    /**
     * Tests that the created state handle is discarded if ZooKeeper create fails.
     */
    @Test
    public void testAddDiscardStateHandleAfterFailure() throws Exception {
        // Setup
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        CuratorFramework client = Mockito.spy(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient());
        Mockito.when(client.inTransaction().create()).thenThrow(new RuntimeException("Expected test Exception."));
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(client, stateHandleProvider);
        // Config
        final String pathInZooKeeper = "/testAddDiscardStateHandleAfterFailure";
        final Long state = 81282227L;
        try {
            // Test
            store.addAndLock(pathInZooKeeper, state);
            Assert.fail("Did not throw expected exception");
        } catch (Exception ignored) {
        }
        // Verify
        // State handle created and discarded
        Assert.assertEquals(1, stateHandleProvider.getStateHandles().size());
        Assert.assertEquals(state, stateHandleProvider.getStateHandles().get(0).retrieveState());
        Assert.assertEquals(1, stateHandleProvider.getStateHandles().get(0).getNumberOfDiscardCalls());
    }

    /**
     * Tests that a state handle is replaced.
     */
    @Test
    public void testReplace() throws Exception {
        // Setup
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateHandleProvider);
        // Config
        final String pathInZooKeeper = "/testReplace";
        final Long initialState = 30968470898L;
        final Long replaceState = 88383776661L;
        // Test
        store.addAndLock(pathInZooKeeper, initialState);
        store.replace(pathInZooKeeper, 0, replaceState);
        // Verify
        // State handles created
        Assert.assertEquals(2, stateHandleProvider.getStateHandles().size());
        Assert.assertEquals(initialState, stateHandleProvider.getStateHandles().get(0).retrieveState());
        Assert.assertEquals(replaceState, stateHandleProvider.getStateHandles().get(1).retrieveState());
        // Path created and is persistent
        Stat stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(pathInZooKeeper);
        Assert.assertNotNull(stat);
        Assert.assertEquals(0, stat.getEphemeralOwner());
        // Data is equal
        @SuppressWarnings("unchecked")
        Long actual = ((RetrievableStateHandle<Long>) (InstantiationUtil.deserializeObject(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().getData().forPath(pathInZooKeeper), ClassLoader.getSystemClassLoader()))).retrieveState();
        Assert.assertEquals(replaceState, actual);
    }

    /**
     * Tests that a non existing path throws an Exception.
     */
    @Test(expected = Exception.class)
    public void testReplaceNonExistingPath() throws Exception {
        RetrievableStateStorageHelper<Long> stateStorage = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateStorage);
        store.replace("/testReplaceNonExistingPath", 0, 1L);
    }

    /**
     * Tests that the replace state handle is discarded if ZooKeeper setData fails.
     */
    @Test
    public void testReplaceDiscardStateHandleAfterFailure() throws Exception {
        // Setup
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        CuratorFramework client = Mockito.spy(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient());
        Mockito.when(client.setData()).thenThrow(new RuntimeException("Expected test Exception."));
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(client, stateHandleProvider);
        // Config
        final String pathInZooKeeper = "/testReplaceDiscardStateHandleAfterFailure";
        final Long initialState = 30968470898L;
        final Long replaceState = 88383776661L;
        // Test
        store.addAndLock(pathInZooKeeper, initialState);
        try {
            store.replace(pathInZooKeeper, 0, replaceState);
            Assert.fail("Did not throw expected exception");
        } catch (Exception ignored) {
        }
        // Verify
        // State handle created and discarded
        Assert.assertEquals(2, stateHandleProvider.getStateHandles().size());
        Assert.assertEquals(initialState, stateHandleProvider.getStateHandles().get(0).retrieveState());
        Assert.assertEquals(replaceState, stateHandleProvider.getStateHandles().get(1).retrieveState());
        Assert.assertEquals(1, stateHandleProvider.getStateHandles().get(1).getNumberOfDiscardCalls());
        // Initial value
        @SuppressWarnings("unchecked")
        Long actual = ((RetrievableStateHandle<Long>) (InstantiationUtil.deserializeObject(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().getData().forPath(pathInZooKeeper), ClassLoader.getSystemClassLoader()))).retrieveState();
        Assert.assertEquals(initialState, actual);
    }

    /**
     * Tests get operation.
     */
    @Test
    public void testGetAndExists() throws Exception {
        // Setup
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateHandleProvider);
        // Config
        final String pathInZooKeeper = "/testGetAndExists";
        final Long state = 311222268470898L;
        // Test
        Assert.assertEquals((-1), store.exists(pathInZooKeeper));
        store.addAndLock(pathInZooKeeper, state);
        RetrievableStateHandle<Long> actual = store.getAndLock(pathInZooKeeper);
        // Verify
        Assert.assertEquals(state, actual.retrieveState());
        Assert.assertTrue(((store.exists(pathInZooKeeper)) >= 0));
    }

    /**
     * Tests that a non existing path throws an Exception.
     */
    @Test(expected = Exception.class)
    public void testGetNonExistingPath() throws Exception {
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateHandleProvider);
        store.getAndLock("/testGetNonExistingPath");
    }

    /**
     * Tests that all added state is returned.
     */
    @Test
    public void testGetAll() throws Exception {
        // Setup
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateHandleProvider);
        // Config
        final String pathInZooKeeper = "/testGetAll";
        final Set<Long> expected = new HashSet<>();
        expected.add(311222268470898L);
        expected.add(132812888L);
        expected.add(27255442L);
        expected.add(11122233124L);
        // Test
        for (long val : expected) {
            store.addAndLock((pathInZooKeeper + val), val);
        }
        for (Tuple2<RetrievableStateHandle<Long>, String> val : store.getAllAndLock()) {
            Assert.assertTrue(expected.remove(val.f0.retrieveState()));
        }
        Assert.assertEquals(0, expected.size());
    }

    /**
     * Tests that the state is returned sorted.
     */
    @Test
    public void testGetAllSortedByName() throws Exception {
        // Setup
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateHandleProvider);
        // Config
        final String basePath = "/testGetAllSortedByName";
        final Long[] expected = new Long[]{ 311222268470898L, 132812888L, 27255442L, 11122233124L };
        // Test
        for (long val : expected) {
            final String pathInZooKeeper = String.format("%s%016d", basePath, val);
            store.addAndLock(pathInZooKeeper, val);
        }
        List<Tuple2<RetrievableStateHandle<Long>, String>> actual = store.getAllAndLock();
        Assert.assertEquals(expected.length, actual.size());
        // bring the elements in sort order
        Arrays.sort(expected);
        Collections.sort(actual, Comparator.comparing(( o) -> o.f1));
        for (int i = 0; i < (expected.length); i++) {
            Assert.assertEquals(expected[i], actual.get(i).f0.retrieveState());
        }
    }

    /**
     * Tests that state handles are correctly removed.
     */
    @Test
    public void testRemove() throws Exception {
        // Setup
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateHandleProvider);
        // Config
        final String pathInZooKeeper = "/testRemove";
        final Long state = 27255442L;
        store.addAndLock(pathInZooKeeper, state);
        final int numberOfGlobalDiscardCalls = ZooKeeperStateHandleStoreTest.LongRetrievableStateHandle.getNumberOfGlobalDiscardCalls();
        // Test
        store.releaseAndTryRemove(pathInZooKeeper);
        // Verify discarded
        Assert.assertEquals(0, ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().getChildren().forPath("/").size());
        Assert.assertEquals((numberOfGlobalDiscardCalls + 1), ZooKeeperStateHandleStoreTest.LongRetrievableStateHandle.getNumberOfGlobalDiscardCalls());
    }

    /**
     * Tests that all state handles are correctly discarded.
     */
    @Test
    public void testReleaseAndTryRemoveAll() throws Exception {
        // Setup
        ZooKeeperStateHandleStoreTest.LongStateStorage stateHandleProvider = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateHandleProvider);
        // Config
        final String pathInZooKeeper = "/testDiscardAll";
        final Set<Long> expected = new HashSet<>();
        expected.add(311222268470898L);
        expected.add(132812888L);
        expected.add(27255442L);
        expected.add(11122233124L);
        // Test
        for (long val : expected) {
            store.addAndLock((pathInZooKeeper + val), val);
        }
        store.releaseAndTryRemoveAll();
        // Verify all discarded
        Assert.assertEquals(0, ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().getChildren().forPath("/").size());
    }

    /**
     * Tests that the ZooKeeperStateHandleStore can handle corrupted data by releasing and trying to remove the
     * respective ZooKeeper ZNodes.
     */
    @Test
    public void testCorruptedData() throws Exception {
        ZooKeeperStateHandleStoreTest.LongStateStorage stateStorage = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> store = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), stateStorage);
        final Collection<Long> input = new HashSet<>();
        input.add(1L);
        input.add(2L);
        input.add(3L);
        for (Long aLong : input) {
            store.addAndLock(("/" + aLong), aLong);
        }
        // corrupt one of the entries
        ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().setData().forPath(("/" + 2), new byte[2]);
        List<Tuple2<RetrievableStateHandle<Long>, String>> allEntries = store.getAllAndLock();
        Collection<Long> expected = new HashSet<>(input);
        expected.remove(2L);
        Collection<Long> actual = new HashSet<>(expected.size());
        for (Tuple2<RetrievableStateHandle<Long>, String> entry : allEntries) {
            actual.add(entry.f0.retrieveState());
        }
        Assert.assertEquals(expected, actual);
    }

    /**
     * FLINK-6612
     *
     * Tests that a concurrent delete operation cannot succeed if another instance holds a lock on the specified
     * node.
     */
    @Test
    public void testConcurrentDeleteOperation() throws Exception {
        ZooKeeperStateHandleStoreTest.LongStateStorage longStateStorage = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> zkStore1 = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), longStateStorage);
        ZooKeeperStateHandleStore<Long> zkStore2 = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), longStateStorage);
        final String statePath = "/state";
        zkStore1.addAndLock(statePath, 42L);
        RetrievableStateHandle<Long> stateHandle = zkStore2.getAndLock(statePath);
        // this should not remove the referenced node because we are still holding a state handle
        // reference via zkStore2
        zkStore1.releaseAndTryRemove(statePath);
        // sanity check
        Assert.assertEquals(42L, ((long) (stateHandle.retrieveState())));
        Stat nodeStat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(statePath);
        Assert.assertNotNull("NodeStat should not be null, otherwise the referenced node does not exist.", nodeStat);
        zkStore2.releaseAndTryRemove(statePath);
        nodeStat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(statePath);
        Assert.assertNull("NodeState should be null, because the referenced node should no longer exist.", nodeStat);
    }

    /**
     * FLINK-6612
     *
     * Tests that getAndLock removes a created lock if the RetrievableStateHandle cannot be retrieved
     * (e.g. deserialization problem).
     */
    @Test
    public void testLockCleanupWhenGetAndLockFails() throws Exception {
        ZooKeeperStateHandleStoreTest.LongStateStorage longStateStorage = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> zkStore1 = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), longStateStorage);
        ZooKeeperStateHandleStore<Long> zkStore2 = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), longStateStorage);
        final String path = "/state";
        zkStore1.addAndLock(path, 42L);
        final byte[] corruptedData = new byte[]{ 1, 2 };
        // corrupt the data
        ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().setData().forPath(path, corruptedData);
        try {
            zkStore2.getAndLock(path);
            Assert.fail("Should fail because we cannot deserialize the node's data");
        } catch (IOException ignored) {
            // expected to fail
        }
        // check that there is no lock node left
        String lockNodePath = zkStore2.getLockPath(path);
        Stat stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(lockNodePath);
        // zkStore2 should not have created a lock node
        Assert.assertNull("zkStore2 should not have created a lock node.", stat);
        Collection<String> children = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().getChildren().forPath(path);
        // there should be exactly one lock node from zkStore1
        Assert.assertEquals(1, children.size());
        zkStore1.releaseAndTryRemove(path);
        stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(path);
        Assert.assertNull("The state node should have been removed.", stat);
    }

    /**
     * FLINK-6612
     *
     * Tests that lock nodes will be released if the client dies.
     */
    @Test
    public void testLockCleanupWhenClientTimesOut() throws Exception {
        ZooKeeperStateHandleStoreTest.LongStateStorage longStateStorage = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        Configuration configuration = new Configuration();
        configuration.setString(HA_ZOOKEEPER_QUORUM, ZooKeeperStateHandleStoreTest.ZOOKEEPER.getConnectString());
        configuration.setInteger(ZOOKEEPER_SESSION_TIMEOUT, 100);
        configuration.setString(HA_ZOOKEEPER_ROOT, "timeout");
        try (CuratorFramework client = ZooKeeperUtils.startCuratorFramework(configuration);CuratorFramework client2 = ZooKeeperUtils.startCuratorFramework(configuration)) {
            ZooKeeperStateHandleStore<Long> zkStore = new ZooKeeperStateHandleStore(client, longStateStorage);
            final String path = "/state";
            zkStore.addAndLock(path, 42L);
            // this should delete all ephemeral nodes
            client.close();
            Stat stat = client2.checkExists().forPath(path);
            // check that our state node still exists
            Assert.assertNotNull(stat);
            Collection<String> children = client2.getChildren().forPath(path);
            // check that the lock node has been released
            Assert.assertEquals(0, children.size());
        }
    }

    /**
     * FLINK-6612
     *
     * Tests that we can release a locked state handles in the ZooKeeperStateHandleStore.
     */
    @Test
    public void testRelease() throws Exception {
        ZooKeeperStateHandleStoreTest.LongStateStorage longStateStorage = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> zkStore = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), longStateStorage);
        final String path = "/state";
        zkStore.addAndLock(path, 42L);
        final String lockPath = zkStore.getLockPath(path);
        Stat stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(lockPath);
        Assert.assertNotNull("Expected an existing lock", stat);
        zkStore.release(path);
        stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(path);
        // release should have removed the lock child
        Assert.assertEquals("Expected no lock nodes as children", 0, stat.getNumChildren());
        zkStore.releaseAndTryRemove(path);
        stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(path);
        Assert.assertNull("State node should have been removed.", stat);
    }

    /**
     * FLINK-6612
     *
     * Tests that we can release all locked state handles in the ZooKeeperStateHandleStore
     */
    @Test
    public void testReleaseAll() throws Exception {
        ZooKeeperStateHandleStoreTest.LongStateStorage longStateStorage = new ZooKeeperStateHandleStoreTest.LongStateStorage();
        ZooKeeperStateHandleStore<Long> zkStore = new ZooKeeperStateHandleStore(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), longStateStorage);
        final Collection<String> paths = Arrays.asList("/state1", "/state2", "/state3");
        for (String path : paths) {
            zkStore.addAndLock(path, 42L);
        }
        for (String path : paths) {
            Stat stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(zkStore.getLockPath(path));
            Assert.assertNotNull("Expecte and existing lock.", stat);
        }
        zkStore.releaseAll();
        for (String path : paths) {
            Stat stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath(path);
            Assert.assertEquals(0, stat.getNumChildren());
        }
        zkStore.releaseAndTryRemoveAll();
        Stat stat = ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient().checkExists().forPath("/");
        Assert.assertEquals(0, stat.getNumChildren());
    }

    @Test
    public void testDeleteAllShouldRemoveAllPaths() throws Exception {
        final ZooKeeperStateHandleStore<Long> zkStore = new ZooKeeperStateHandleStore(ZooKeeperUtils.useNamespaceAndEnsurePath(ZooKeeperStateHandleStoreTest.ZOOKEEPER.getClient(), "/path"), new ZooKeeperStateHandleStoreTest.LongStateStorage());
        zkStore.addAndLock("/state", 1L);
        zkStore.deleteChildren();
        Assert.assertThat(zkStore.getAllPaths(), Matchers.is(Matchers.empty()));
    }

    // ---------------------------------------------------------------------------------------------
    // Simple test helpers
    // ---------------------------------------------------------------------------------------------
    private static class LongStateStorage implements RetrievableStateStorageHelper<Long> {
        private final List<ZooKeeperStateHandleStoreTest.LongRetrievableStateHandle> stateHandles = new ArrayList<>();

        @Override
        public RetrievableStateHandle<Long> store(Long state) throws Exception {
            ZooKeeperStateHandleStoreTest.LongRetrievableStateHandle stateHandle = new ZooKeeperStateHandleStoreTest.LongRetrievableStateHandle(state);
            stateHandles.add(stateHandle);
            return stateHandle;
        }

        List<ZooKeeperStateHandleStoreTest.LongRetrievableStateHandle> getStateHandles() {
            return stateHandles;
        }
    }

    private static class LongRetrievableStateHandle implements RetrievableStateHandle<Long> {
        private static final long serialVersionUID = -3555329254423838912L;

        private static int numberOfGlobalDiscardCalls = 0;

        private final Long state;

        private int numberOfDiscardCalls = 0;

        public LongRetrievableStateHandle(Long state) {
            this.state = state;
        }

        @Override
        public Long retrieveState() {
            return state;
        }

        @Override
        public void discardState() throws Exception {
            (ZooKeeperStateHandleStoreTest.LongRetrievableStateHandle.numberOfGlobalDiscardCalls)++;
            (numberOfDiscardCalls)++;
        }

        @Override
        public long getStateSize() {
            return 0;
        }

        int getNumberOfDiscardCalls() {
            return numberOfDiscardCalls;
        }

        public static int getNumberOfGlobalDiscardCalls() {
            return ZooKeeperStateHandleStoreTest.LongRetrievableStateHandle.numberOfGlobalDiscardCalls;
        }
    }
}

