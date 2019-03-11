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
package org.apache.flink.contrib.streaming.state;


import RocksDBKeyedStateBackend.MERGE_OPERATOR_NAME;
import RocksDBStateBackend.PriorityQueueStateType;
import StringSerializer.INSTANCE;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.RunnableFuture;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.core.fs.CloseableRegistry;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.query.TaskKvStateRegistry;
import org.apache.flink.runtime.state.AbstractKeyedStateBackend;
import org.apache.flink.runtime.state.AbstractStateBackend;
import org.apache.flink.runtime.state.IncrementalRemoteKeyedStateHandle;
import org.apache.flink.runtime.state.KeyGroupRange;
import org.apache.flink.runtime.state.KeyedStateHandle;
import org.apache.flink.runtime.state.LocalRecoveryConfig;
import org.apache.flink.runtime.state.SharedStateRegistry;
import org.apache.flink.runtime.state.SnapshotResult;
import org.apache.flink.runtime.state.StateBackendTestBase;
import org.apache.flink.runtime.state.StateHandleID;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.runtime.state.ttl.TtlTimeProvider;
import org.apache.flink.runtime.util.BlockerCheckpointStreamFactory;
import org.apache.flink.runtime.util.BlockingCheckpointOutputStream;
import org.apache.flink.util.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksObject;
import org.rocksdb.Snapshot;


/**
 * Tests for the partitioned state part of {@link RocksDBStateBackend}.
 */
@RunWith(Parameterized.class)
public class RocksDBStateBackendTest extends StateBackendTestBase<RocksDBStateBackend> {
    private OneShotLatch blocker;

    private OneShotLatch waiter;

    private BlockerCheckpointStreamFactory testStreamFactory;

    private RocksDBKeyedStateBackend<Integer> keyedStateBackend;

    private List<RocksObject> allCreatedCloseables;

    private ValueState<Integer> testState1;

    private ValueState<String> testState2;

    @Parameterized.Parameter
    public boolean enableIncrementalCheckpointing;

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    // Store it because we need it for the cleanup test.
    private String dbPath;

    private RocksDB db = null;

    private File instanceBasePath = null;

    private ColumnFamilyHandle defaultCFHandle = null;

    private ColumnFamilyOptions columnOptions = null;

    private DBOptions dbOptions = null;

    @Test
    public void testCorrectMergeOperatorSet() throws Exception {
        prepareRocksDB();
        final ColumnFamilyOptions columnFamilyOptions = spy(new ColumnFamilyOptions());
        RocksDBKeyedStateBackend<Integer> test = null;
        try (DBOptions options = new DBOptions().setCreateIfMissing(true)) {
            ExecutionConfig executionConfig = new ExecutionConfig();
            test = new RocksDBKeyedStateBackendBuilder("test", Thread.currentThread().getContextClassLoader(), tempFolder.newFolder(), options, ( stateName) -> columnFamilyOptions, mock(TaskKvStateRegistry.class), IntSerializer.INSTANCE, 1, new KeyGroupRange(0, 0), executionConfig, mock(LocalRecoveryConfig.class), PriorityQueueStateType.HEAP, TtlTimeProvider.DEFAULT, new UnregisteredMetricsGroup(), Collections.emptyList(), AbstractStateBackend.getCompressionDecorator(executionConfig), db, defaultCFHandle, new CloseableRegistry()).build();
            ValueStateDescriptor<String> stubState1 = new ValueStateDescriptor("StubState-1", StringSerializer.INSTANCE);
            test.createInternalState(INSTANCE, stubState1);
            ValueStateDescriptor<String> stubState2 = new ValueStateDescriptor("StubState-2", StringSerializer.INSTANCE);
            test.createInternalState(INSTANCE, stubState2);
            // The default CF is pre-created so sum up to 2 times (once for each stub state)
            Mockito.verify(columnFamilyOptions, Mockito.times(2)).setMergeOperatorName(MERGE_OPERATOR_NAME);
        } finally {
            if (test != null) {
                IOUtils.closeQuietly(test);
                test.dispose();
            }
            columnFamilyOptions.close();
        }
    }

    @Test
    public void testReleasingSnapshotAfterBackendClosed() throws Exception {
        setupRocksKeyedStateBackend();
        try {
            RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot = keyedStateBackend.snapshot(0L, 0L, testStreamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            RocksDB spyDB = keyedStateBackend.db;
            if (!(enableIncrementalCheckpointing)) {
                Mockito.verify(spyDB, VerificationModeFactory.times(1)).getSnapshot();
                Mockito.verify(spyDB, VerificationModeFactory.times(0)).releaseSnapshot(ArgumentMatchers.any(Snapshot.class));
            }
            // Ensure every RocksObjects not closed yet
            for (RocksObject rocksCloseable : allCreatedCloseables) {
                Mockito.verify(rocksCloseable, VerificationModeFactory.times(0)).close();
            }
            snapshot.cancel(true);
            this.keyedStateBackend.dispose();
            Mockito.verify(spyDB, VerificationModeFactory.times(1)).close();
            Assert.assertEquals(true, keyedStateBackend.isDisposed());
            // Ensure every RocksObjects was closed exactly once
            for (RocksObject rocksCloseable : allCreatedCloseables) {
                Mockito.verify(rocksCloseable, VerificationModeFactory.times(1)).close();
            }
        } finally {
            keyedStateBackend.dispose();
            keyedStateBackend = null;
        }
    }

    @Test
    public void testDismissingSnapshot() throws Exception {
        setupRocksKeyedStateBackend();
        try {
            RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot = keyedStateBackend.snapshot(0L, 0L, testStreamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            snapshot.cancel(true);
            verifyRocksObjectsReleased();
        } finally {
            this.keyedStateBackend.dispose();
            this.keyedStateBackend = null;
        }
    }

    @Test
    public void testDismissingSnapshotNotRunnable() throws Exception {
        setupRocksKeyedStateBackend();
        try {
            RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot = keyedStateBackend.snapshot(0L, 0L, testStreamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            snapshot.cancel(true);
            Thread asyncSnapshotThread = new Thread(snapshot);
            asyncSnapshotThread.start();
            try {
                snapshot.get();
                Assert.fail();
            } catch (Exception ignored) {
            }
            asyncSnapshotThread.join();
            verifyRocksObjectsReleased();
        } finally {
            this.keyedStateBackend.dispose();
            this.keyedStateBackend = null;
        }
    }

    @Test
    public void testCompletingSnapshot() throws Exception {
        setupRocksKeyedStateBackend();
        try {
            RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot = keyedStateBackend.snapshot(0L, 0L, testStreamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            Thread asyncSnapshotThread = new Thread(snapshot);
            asyncSnapshotThread.start();
            waiter.await();// wait for snapshot to run

            waiter.reset();
            runStateUpdates();
            blocker.trigger();// allow checkpointing to start writing

            waiter.await();// wait for snapshot stream writing to run

            SnapshotResult<KeyedStateHandle> snapshotResult = snapshot.get();
            KeyedStateHandle keyedStateHandle = snapshotResult.getJobManagerOwnedSnapshot();
            TestCase.assertNotNull(keyedStateHandle);
            Assert.assertTrue(((keyedStateHandle.getStateSize()) > 0));
            Assert.assertEquals(2, keyedStateHandle.getKeyGroupRange().getNumberOfKeyGroups());
            for (BlockingCheckpointOutputStream stream : testStreamFactory.getAllCreatedStreams()) {
                Assert.assertTrue(stream.isClosed());
            }
            asyncSnapshotThread.join();
            verifyRocksObjectsReleased();
        } finally {
            this.keyedStateBackend.dispose();
            this.keyedStateBackend = null;
        }
    }

    @Test
    public void testCancelRunningSnapshot() throws Exception {
        setupRocksKeyedStateBackend();
        try {
            RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot = keyedStateBackend.snapshot(0L, 0L, testStreamFactory, CheckpointOptions.forCheckpointWithDefaultLocation());
            Thread asyncSnapshotThread = new Thread(snapshot);
            asyncSnapshotThread.start();
            waiter.await();// wait for snapshot to run

            waiter.reset();
            runStateUpdates();
            snapshot.cancel(true);
            blocker.trigger();// allow checkpointing to start writing

            for (BlockingCheckpointOutputStream stream : testStreamFactory.getAllCreatedStreams()) {
                Assert.assertTrue(stream.isClosed());
            }
            waiter.await();// wait for snapshot stream writing to run

            try {
                snapshot.get();
                Assert.fail();
            } catch (Exception ignored) {
            }
            asyncSnapshotThread.join();
            verifyRocksObjectsReleased();
        } finally {
            this.keyedStateBackend.dispose();
            this.keyedStateBackend = null;
        }
    }

    @Test
    public void testDisposeDeletesAllDirectories() throws Exception {
        AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(IntSerializer.INSTANCE);
        Collection<File> allFilesInDbDir = FileUtils.listFilesAndDirs(new File(dbPath), new RocksDBStateBackendTest.AcceptAllFilter(), new RocksDBStateBackendTest.AcceptAllFilter());
        try {
            ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class, null);
            kvId.initializeSerializerUnlessSet(new ExecutionConfig());
            ValueState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
            backend.setCurrentKey(1);
            state.update("Hello");
            // more than just the root directory
            Assert.assertTrue(((allFilesInDbDir.size()) > 1));
        } finally {
            IOUtils.closeQuietly(backend);
            backend.dispose();
        }
        allFilesInDbDir = FileUtils.listFilesAndDirs(new File(dbPath), new RocksDBStateBackendTest.AcceptAllFilter(), new RocksDBStateBackendTest.AcceptAllFilter());
        // just the root directory left
        Assert.assertEquals(1, allFilesInDbDir.size());
    }

    @Test
    public void testSharedIncrementalStateDeRegistration() throws Exception {
        if (enableIncrementalCheckpointing) {
            AbstractKeyedStateBackend<Integer> backend = createKeyedBackend(IntSerializer.INSTANCE);
            try {
                ValueStateDescriptor<String> kvId = new ValueStateDescriptor("id", String.class, null);
                kvId.initializeSerializerUnlessSet(new ExecutionConfig());
                ValueState<String> state = backend.getPartitionedState(VoidNamespace.INSTANCE, VoidNamespaceSerializer.INSTANCE, kvId);
                Queue<IncrementalRemoteKeyedStateHandle> previousStateHandles = new LinkedList<>();
                SharedStateRegistry sharedStateRegistry = spy(new SharedStateRegistry());
                for (int checkpointId = 0; checkpointId < 3; ++checkpointId) {
                    Mockito.reset(sharedStateRegistry);
                    backend.setCurrentKey(checkpointId);
                    state.update(("Hello-" + checkpointId));
                    RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshot = backend.snapshot(checkpointId, checkpointId, createStreamFactory(), CheckpointOptions.forCheckpointWithDefaultLocation());
                    snapshot.run();
                    SnapshotResult<KeyedStateHandle> snapshotResult = snapshot.get();
                    IncrementalRemoteKeyedStateHandle stateHandle = ((IncrementalRemoteKeyedStateHandle) (snapshotResult.getJobManagerOwnedSnapshot()));
                    Map<StateHandleID, StreamStateHandle> sharedState = new java.util.HashMap(stateHandle.getSharedState());
                    stateHandle.registerSharedStates(sharedStateRegistry);
                    for (Map.Entry<StateHandleID, StreamStateHandle> e : sharedState.entrySet()) {
                        Mockito.verify(sharedStateRegistry).registerReference(stateHandle.createSharedStateRegistryKeyFromFileName(e.getKey()), e.getValue());
                    }
                    previousStateHandles.add(stateHandle);
                    backend.notifyCheckpointComplete(checkpointId);
                    // -----------------------------------------------------------------
                    if ((previousStateHandles.size()) > 1) {
                        checkRemove(previousStateHandles.remove(), sharedStateRegistry);
                    }
                }
                while (!(previousStateHandles.isEmpty())) {
                    Mockito.reset(sharedStateRegistry);
                    checkRemove(previousStateHandles.remove(), sharedStateRegistry);
                } 
            } finally {
                IOUtils.closeQuietly(backend);
                backend.dispose();
            }
        }
    }

    private static class AcceptAllFilter implements IOFileFilter {
        @Override
        public boolean accept(File file) {
            return true;
        }

        @Override
        public boolean accept(File file, String s) {
            return true;
        }
    }
}

