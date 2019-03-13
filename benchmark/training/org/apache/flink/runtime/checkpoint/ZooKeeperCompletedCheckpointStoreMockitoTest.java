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


import CuratorEventType.DELETE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.utils.EnsurePath;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.concurrent.Executors;
import org.apache.flink.runtime.state.RetrievableStateHandle;
import org.apache.flink.runtime.state.testutils.TestCompletedCheckpointStorageLocation;
import org.apache.flink.runtime.zookeeper.RetrievableStateStorageHelper;
import org.apache.flink.runtime.zookeeper.ZooKeeperStateHandleStore;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Mockito based tests for the {@link ZooKeeperStateHandleStore}.
 */
public class ZooKeeperCompletedCheckpointStoreMockitoTest extends TestLogger {
    /**
     * Tests that the completed checkpoint store can retrieve all checkpoints stored in ZooKeeper
     * and ignores those which cannot be retrieved via their state handles.
     *
     * <p>We have a timeout in case the ZooKeeper store get's into a deadlock/livelock situation.
     */
    @Test(timeout = 50000)
    public void testCheckpointRecovery() throws Exception {
        final JobID jobID = new JobID();
        final long checkpoint1Id = 1L;
        final long checkpoint2Id = 2;
        final List<Tuple2<RetrievableStateHandle<CompletedCheckpoint>, String>> checkpointsInZooKeeper = new ArrayList<>(4);
        final Collection<Long> expectedCheckpointIds = new HashSet<>(2);
        expectedCheckpointIds.add(1L);
        expectedCheckpointIds.add(2L);
        final RetrievableStateHandle<CompletedCheckpoint> failingRetrievableStateHandle = Mockito.mock(RetrievableStateHandle.class);
        Mockito.when(failingRetrievableStateHandle.retrieveState()).thenThrow(new IOException("Test exception"));
        final RetrievableStateHandle<CompletedCheckpoint> retrievableStateHandle1 = Mockito.mock(RetrievableStateHandle.class);
        Mockito.when(retrievableStateHandle1.retrieveState()).then(( invocation) -> new CompletedCheckpoint(jobID, checkpoint1Id, 1L, 1L, new HashMap<>(), null, CheckpointProperties.forCheckpoint(CheckpointRetentionPolicy.NEVER_RETAIN_AFTER_TERMINATION), new TestCompletedCheckpointStorageLocation()));
        final RetrievableStateHandle<CompletedCheckpoint> retrievableStateHandle2 = Mockito.mock(RetrievableStateHandle.class);
        Mockito.when(retrievableStateHandle2.retrieveState()).then(( invocation) -> new CompletedCheckpoint(jobID, checkpoint2Id, 2L, 2L, new HashMap<>(), null, CheckpointProperties.forCheckpoint(CheckpointRetentionPolicy.NEVER_RETAIN_AFTER_TERMINATION), new TestCompletedCheckpointStorageLocation()));
        checkpointsInZooKeeper.add(Tuple2.of(retrievableStateHandle1, "/foobar1"));
        checkpointsInZooKeeper.add(Tuple2.of(failingRetrievableStateHandle, "/failing1"));
        checkpointsInZooKeeper.add(Tuple2.of(retrievableStateHandle2, "/foobar2"));
        checkpointsInZooKeeper.add(Tuple2.of(failingRetrievableStateHandle, "/failing2"));
        final CuratorFramework client = Mockito.mock(CuratorFramework.class, Mockito.RETURNS_DEEP_STUBS);
        final RetrievableStateStorageHelper<CompletedCheckpoint> storageHelperMock = Mockito.mock(RetrievableStateStorageHelper.class);
        ZooKeeperStateHandleStore<CompletedCheckpoint> zooKeeperStateHandleStoreMock = Mockito.spy(new ZooKeeperStateHandleStore(client, storageHelperMock));
        Mockito.doReturn(checkpointsInZooKeeper).when(zooKeeperStateHandleStoreMock).getAllAndLock();
        final int numCheckpointsToRetain = 1;
        // Mocking for the delete operation on the CuratorFramework client
        // It assures that the callback is executed synchronously
        final EnsurePath ensurePathMock = Mockito.mock(EnsurePath.class);
        final CuratorEvent curatorEventMock = Mockito.mock(CuratorEvent.class);
        Mockito.when(curatorEventMock.getType()).thenReturn(DELETE);
        Mockito.when(curatorEventMock.getResultCode()).thenReturn(0);
        Mockito.when(client.newNamespaceAwareEnsurePath(ArgumentMatchers.anyString())).thenReturn(ensurePathMock);
        Mockito.when(client.delete().inBackground(ArgumentMatchers.any(BackgroundCallback.class), ArgumentMatchers.any(Executor.class))).thenAnswer(new Answer<ErrorListenerPathable<Void>>() {
            @Override
            public ErrorListenerPathable<Void> answer(InvocationOnMock invocation) throws Throwable {
                final BackgroundCallback callback = ((BackgroundCallback) (invocation.getArguments()[0]));
                ErrorListenerPathable<Void> result = Mockito.mock(ErrorListenerPathable.class);
                Mockito.when(result.forPath(ArgumentMatchers.anyString())).thenAnswer(new Answer<Void>() {
                    @Override
                    public Void answer(InvocationOnMock invocation) throws Throwable {
                        callback.processResult(client, curatorEventMock);
                        return null;
                    }
                });
                return result;
            }
        });
        ZooKeeperCompletedCheckpointStore zooKeeperCompletedCheckpointStore = new ZooKeeperCompletedCheckpointStore(numCheckpointsToRetain, zooKeeperStateHandleStoreMock, Executors.directExecutor());
        zooKeeperCompletedCheckpointStore.recover();
        CompletedCheckpoint latestCompletedCheckpoint = zooKeeperCompletedCheckpointStore.getLatestCheckpoint();
        // check that we return the latest retrievable checkpoint
        // this should remove the latest checkpoint because it is broken
        Assert.assertEquals(checkpoint2Id, latestCompletedCheckpoint.getCheckpointID());
        // this should remove the second broken checkpoint because we're iterating over all checkpoints
        List<CompletedCheckpoint> completedCheckpoints = zooKeeperCompletedCheckpointStore.getAllCheckpoints();
        Collection<Long> actualCheckpointIds = new HashSet(completedCheckpoints.size());
        for (CompletedCheckpoint completedCheckpoint : completedCheckpoints) {
            actualCheckpointIds.add(completedCheckpoint.getCheckpointID());
        }
        Assert.assertEquals(expectedCheckpointIds, actualCheckpointIds);
        // check that we did not discard any of the state handles
        Mockito.verify(retrievableStateHandle1, Mockito.never()).discardState();
        Mockito.verify(retrievableStateHandle2, Mockito.never()).discardState();
        // Make sure that we also didn't discard any of the broken handles. Only when checkpoints
        // are subsumed should they be discarded.
        Mockito.verify(failingRetrievableStateHandle, Mockito.never()).discardState();
    }

    /**
     * Tests that the checkpoint does not exist in the store when we fail to add
     * it into the store (i.e., there exists an exception thrown by the method).
     */
    @Test
    public void testAddCheckpointWithFailedRemove() throws Exception {
        final CuratorFramework client = Mockito.mock(CuratorFramework.class, Mockito.RETURNS_DEEP_STUBS);
        final RetrievableStateStorageHelper<CompletedCheckpoint> storageHelperMock = Mockito.mock(RetrievableStateStorageHelper.class);
        ZooKeeperStateHandleStore<CompletedCheckpoint> zookeeperStateHandleStoreMock = Mockito.spy(new ZooKeeperStateHandleStore(client, storageHelperMock));
        Mockito.doAnswer(new Answer<RetrievableStateHandle<CompletedCheckpoint>>() {
            @Override
            public RetrievableStateHandle<CompletedCheckpoint> answer(InvocationOnMock invocationOnMock) throws Throwable {
                CompletedCheckpoint checkpoint = ((CompletedCheckpoint) (invocationOnMock.getArguments()[1]));
                RetrievableStateHandle<CompletedCheckpoint> retrievableStateHandle = Mockito.mock(RetrievableStateHandle.class);
                Mockito.when(retrievableStateHandle.retrieveState()).thenReturn(checkpoint);
                return retrievableStateHandle;
            }
        }).when(zookeeperStateHandleStoreMock).addAndLock(ArgumentMatchers.anyString(), ArgumentMatchers.any(CompletedCheckpoint.class));
        Mockito.doThrow(new Exception()).when(zookeeperStateHandleStoreMock).releaseAndTryRemove(ArgumentMatchers.anyString());
        final int numCheckpointsToRetain = 1;
        ZooKeeperCompletedCheckpointStore zooKeeperCompletedCheckpointStore = new ZooKeeperCompletedCheckpointStore(numCheckpointsToRetain, zookeeperStateHandleStoreMock, Executors.directExecutor());
        for (long i = 0; i <= numCheckpointsToRetain; ++i) {
            CompletedCheckpoint checkpointToAdd = Mockito.mock(CompletedCheckpoint.class);
            Mockito.doReturn(i).when(checkpointToAdd).getCheckpointID();
            Mockito.doReturn(Collections.emptyMap()).when(checkpointToAdd).getOperatorStates();
            try {
                zooKeeperCompletedCheckpointStore.addCheckpoint(checkpointToAdd);
                // The checkpoint should be in the store if we successfully add it into the store.
                List<CompletedCheckpoint> addedCheckpoints = zooKeeperCompletedCheckpointStore.getAllCheckpoints();
                Assert.assertTrue(addedCheckpoints.contains(checkpointToAdd));
            } catch (Exception e) {
                // The checkpoint should not be in the store if any exception is thrown.
                List<CompletedCheckpoint> addedCheckpoints = zooKeeperCompletedCheckpointStore.getAllCheckpoints();
                Assert.assertFalse(addedCheckpoints.contains(checkpointToAdd));
            }
        }
    }
}

