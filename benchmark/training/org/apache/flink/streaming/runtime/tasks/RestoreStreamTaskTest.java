/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.tasks;


import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.runtime.checkpoint.JobManagerTaskRestore;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.runtime.checkpoint.StateAssignmentOperation;
import org.apache.flink.runtime.checkpoint.TaskStateSnapshot;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.state.StateInitializationContext;
import org.apache.flink.runtime.state.StateSnapshotContext;
import org.apache.flink.streaming.api.operators.AbstractStreamOperator;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests ensuring correct behaviour of {@link org.apache.flink.runtime.state.ManagedInitializationContext#isRestored}
 * method.
 */
public class RestoreStreamTaskTest extends TestLogger {
    private static final Set<OperatorID> RESTORED_OPERATORS = ConcurrentHashMap.newKeySet();

    @Test
    public void testRestore() throws Exception {
        OperatorID headOperatorID = new OperatorID(42L, 42L);
        OperatorID tailOperatorID = new OperatorID(44L, 44L);
        JobManagerTaskRestore restore = createRunAndCheckpointOperatorChain(headOperatorID, new RestoreStreamTaskTest.CounterOperator(), tailOperatorID, new RestoreStreamTaskTest.CounterOperator(), Optional.empty());
        TaskStateSnapshot stateHandles = restore.getTaskStateSnapshot();
        Assert.assertEquals(2, stateHandles.getSubtaskStateMappings().size());
        createRunAndCheckpointOperatorChain(headOperatorID, new RestoreStreamTaskTest.CounterOperator(), tailOperatorID, new RestoreStreamTaskTest.CounterOperator(), Optional.of(restore));
        Assert.assertEquals(new java.util.HashSet(Arrays.asList(headOperatorID, tailOperatorID)), RestoreStreamTaskTest.RESTORED_OPERATORS);
    }

    @Test
    public void testRestoreHeadWithNewId() throws Exception {
        OperatorID tailOperatorID = new OperatorID(44L, 44L);
        JobManagerTaskRestore restore = createRunAndCheckpointOperatorChain(new OperatorID(42L, 42L), new RestoreStreamTaskTest.CounterOperator(), tailOperatorID, new RestoreStreamTaskTest.CounterOperator(), Optional.empty());
        TaskStateSnapshot stateHandles = restore.getTaskStateSnapshot();
        Assert.assertEquals(2, stateHandles.getSubtaskStateMappings().size());
        createRunAndCheckpointOperatorChain(new OperatorID(4242L, 4242L), new RestoreStreamTaskTest.CounterOperator(), tailOperatorID, new RestoreStreamTaskTest.CounterOperator(), Optional.of(restore));
        Assert.assertEquals(Collections.singleton(tailOperatorID), RestoreStreamTaskTest.RESTORED_OPERATORS);
    }

    @Test
    public void testRestoreTailWithNewId() throws Exception {
        OperatorID headOperatorID = new OperatorID(42L, 42L);
        JobManagerTaskRestore restore = createRunAndCheckpointOperatorChain(headOperatorID, new RestoreStreamTaskTest.CounterOperator(), new OperatorID(44L, 44L), new RestoreStreamTaskTest.CounterOperator(), Optional.empty());
        TaskStateSnapshot stateHandles = restore.getTaskStateSnapshot();
        Assert.assertEquals(2, stateHandles.getSubtaskStateMappings().size());
        createRunAndCheckpointOperatorChain(headOperatorID, new RestoreStreamTaskTest.CounterOperator(), new OperatorID(4444L, 4444L), new RestoreStreamTaskTest.CounterOperator(), Optional.of(restore));
        Assert.assertEquals(Collections.singleton(headOperatorID), RestoreStreamTaskTest.RESTORED_OPERATORS);
    }

    @Test
    public void testRestoreAfterScaleUp() throws Exception {
        OperatorID headOperatorID = new OperatorID(42L, 42L);
        OperatorID tailOperatorID = new OperatorID(44L, 44L);
        JobManagerTaskRestore restore = createRunAndCheckpointOperatorChain(headOperatorID, new RestoreStreamTaskTest.CounterOperator(), tailOperatorID, new RestoreStreamTaskTest.CounterOperator(), Optional.empty());
        TaskStateSnapshot stateHandles = restore.getTaskStateSnapshot();
        Assert.assertEquals(2, stateHandles.getSubtaskStateMappings().size());
        // test empty state in case of scale up
        OperatorSubtaskState emptyHeadOperatorState = StateAssignmentOperation.operatorSubtaskStateFrom(new org.apache.flink.runtime.jobgraph.OperatorInstanceID(0, headOperatorID), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        stateHandles.putSubtaskStateByOperatorID(headOperatorID, emptyHeadOperatorState);
        createRunAndCheckpointOperatorChain(headOperatorID, new RestoreStreamTaskTest.CounterOperator(), tailOperatorID, new RestoreStreamTaskTest.CounterOperator(), Optional.of(restore));
        Assert.assertEquals(new java.util.HashSet(Arrays.asList(headOperatorID, tailOperatorID)), RestoreStreamTaskTest.RESTORED_OPERATORS);
    }

    @Test
    public void testRestoreWithoutState() throws Exception {
        OperatorID headOperatorID = new OperatorID(42L, 42L);
        OperatorID tailOperatorID = new OperatorID(44L, 44L);
        JobManagerTaskRestore restore = createRunAndCheckpointOperatorChain(headOperatorID, new RestoreStreamTaskTest.StatelessOperator(), tailOperatorID, new RestoreStreamTaskTest.CounterOperator(), Optional.empty());
        TaskStateSnapshot stateHandles = restore.getTaskStateSnapshot();
        Assert.assertEquals(2, stateHandles.getSubtaskStateMappings().size());
        createRunAndCheckpointOperatorChain(headOperatorID, new RestoreStreamTaskTest.StatelessOperator(), tailOperatorID, new RestoreStreamTaskTest.CounterOperator(), Optional.of(restore));
        Assert.assertEquals(new java.util.HashSet(Arrays.asList(headOperatorID, tailOperatorID)), RestoreStreamTaskTest.RESTORED_OPERATORS);
    }

    private abstract static class RestoreWatchOperator<IN, OUT> extends AbstractStreamOperator<OUT> implements OneInputStreamOperator<IN, OUT> {
        @Override
        public void initializeState(StateInitializationContext context) throws Exception {
            if (context.isRestored()) {
                RestoreStreamTaskTest.RESTORED_OPERATORS.add(RestoreStreamTaskTest.RestoreWatchOperator.getOperatorID());
            }
        }
    }

    /**
     * Operator that counts processed messages and keeps result on state.
     */
    private static class CounterOperator extends RestoreStreamTaskTest.RestoreWatchOperator<String, String> {
        private static final long serialVersionUID = 2048954179291813243L;

        private ListState<Long> counterState;

        private long counter = 0;

        @Override
        public void processElement(StreamRecord<String> element) throws Exception {
            (counter)++;
            output.collect(element);
        }

        @Override
        public void initializeState(StateInitializationContext context) throws Exception {
            super.initializeState(context);
            counterState = context.getOperatorStateStore().getListState(new org.apache.flink.api.common.state.ListStateDescriptor("counter-state", LongSerializer.INSTANCE));
            if (context.isRestored()) {
                for (Long value : counterState.get()) {
                    counter += value;
                }
                counterState.clear();
            }
        }

        @Override
        public void snapshotState(StateSnapshotContext context) throws Exception {
            counterState.add(counter);
        }
    }

    /**
     * Operator that does nothing except counting state restorations.
     */
    private static class StatelessOperator extends RestoreStreamTaskTest.RestoreWatchOperator<String, String> {
        private static final long serialVersionUID = 2048954179291813244L;

        @Override
        public void processElement(StreamRecord<String> element) throws Exception {
            output.collect(element);
        }

        @Override
        public void snapshotState(StateSnapshotContext context) throws Exception {
        }
    }
}

