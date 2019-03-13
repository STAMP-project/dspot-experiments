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


import java.util.List;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.concurrent.Executors;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.ExecutionVertex;
import org.apache.flink.runtime.jobgraph.JobStatus;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.messages.checkpoint.AcknowledgeCheckpoint;
import org.apache.flink.runtime.state.KeyedStateHandle;
import org.apache.flink.runtime.state.OperatorStateHandle;
import org.apache.flink.runtime.state.OperatorStreamStateHandle;
import org.apache.flink.runtime.state.SharedStateRegistry;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static CheckpointRetentionPolicy.NEVER_RETAIN_AFTER_TERMINATION;


@RunWith(PowerMockRunner.class)
@PrepareForTest(PendingCheckpoint.class)
public class CheckpointCoordinatorFailureTest extends TestLogger {
    /**
     * Tests that a failure while storing a completed checkpoint in the completed checkpoint store
     * will properly fail the originating pending checkpoint and clean upt the completed checkpoint.
     */
    @Test
    public void testFailingCompletedCheckpointStoreAdd() throws Exception {
        JobID jid = new JobID();
        final ExecutionAttemptID executionAttemptId = new ExecutionAttemptID();
        final ExecutionVertex vertex = CheckpointCoordinatorTest.mockExecutionVertex(executionAttemptId);
        final long triggerTimestamp = 1L;
        // set up the coordinator and validate the initial state
        CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex }, new ExecutionVertex[]{ vertex }, new ExecutionVertex[]{ vertex }, new StandaloneCheckpointIDCounter(), new CheckpointCoordinatorFailureTest.FailingCompletedCheckpointStore(), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        coord.triggerCheckpoint(triggerTimestamp, false);
        Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
        PendingCheckpoint pendingCheckpoint = coord.getPendingCheckpoints().values().iterator().next();
        Assert.assertFalse(pendingCheckpoint.isDiscarded());
        final long checkpointId = coord.getPendingCheckpoints().keySet().iterator().next();
        KeyedStateHandle managedKeyedHandle = Mockito.mock(KeyedStateHandle.class);
        KeyedStateHandle rawKeyedHandle = Mockito.mock(KeyedStateHandle.class);
        OperatorStateHandle managedOpHandle = Mockito.mock(OperatorStreamStateHandle.class);
        OperatorStateHandle rawOpHandle = Mockito.mock(OperatorStreamStateHandle.class);
        final OperatorSubtaskState operatorSubtaskState = Mockito.spy(new OperatorSubtaskState(managedOpHandle, rawOpHandle, managedKeyedHandle, rawKeyedHandle));
        TaskStateSnapshot subtaskState = Mockito.spy(new TaskStateSnapshot());
        subtaskState.putSubtaskStateByOperatorID(new OperatorID(), operatorSubtaskState);
        Mockito.when(subtaskState.getSubtaskStateByOperatorID(OperatorID.fromJobVertexID(vertex.getJobvertexId()))).thenReturn(operatorSubtaskState);
        AcknowledgeCheckpoint acknowledgeMessage = new AcknowledgeCheckpoint(jid, executionAttemptId, checkpointId, new CheckpointMetrics(), subtaskState);
        try {
            coord.receiveAcknowledgeMessage(acknowledgeMessage);
            Assert.fail(("Expected a checkpoint exception because the completed checkpoint store could not " + "store the completed checkpoint."));
        } catch (CheckpointException e) {
            // ignore because we expected this exception
        }
        // make sure that the pending checkpoint has been discarded after we could not complete it
        Assert.assertTrue(pendingCheckpoint.isDiscarded());
        // make sure that the subtask state has been discarded after we could not complete it.
        Mockito.verify(operatorSubtaskState).discardState();
        Mockito.verify(operatorSubtaskState.getManagedOperatorState().iterator().next()).discardState();
        Mockito.verify(operatorSubtaskState.getRawOperatorState().iterator().next()).discardState();
        Mockito.verify(operatorSubtaskState.getManagedKeyedState().iterator().next()).discardState();
        Mockito.verify(operatorSubtaskState.getRawKeyedState().iterator().next()).discardState();
    }

    private static final class FailingCompletedCheckpointStore implements CompletedCheckpointStore {
        @Override
        public void recover() throws Exception {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public void addCheckpoint(CompletedCheckpoint checkpoint) throws Exception {
            throw new Exception("The failing completed checkpoint store failed again... :-(");
        }

        @Override
        public CompletedCheckpoint getLatestCheckpoint() throws Exception {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public void shutdown(JobStatus jobStatus) throws Exception {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public List<CompletedCheckpoint> getAllCheckpoints() throws Exception {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public int getNumberOfRetainedCheckpoints() {
            return -1;
        }

        @Override
        public int getMaxNumberOfRetainedCheckpoints() {
            return 1;
        }

        @Override
        public boolean requiresExternalizedCheckpoints() {
            return false;
        }
    }
}

