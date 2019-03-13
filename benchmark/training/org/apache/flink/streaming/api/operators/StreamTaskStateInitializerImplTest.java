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
package org.apache.flink.streaming.api.operators;


import OperatorStateHandle.Mode;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import javax.annotation.Nonnull;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.core.fs.CloseableRegistry;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.runtime.checkpoint.JobManagerTaskRestore;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.runtime.checkpoint.TaskStateSnapshot;
import org.apache.flink.runtime.checkpoint.savepoint.CheckpointTestUtils;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.query.TaskKvStateRegistry;
import org.apache.flink.runtime.state.AbstractKeyedStateBackend;
import org.apache.flink.runtime.state.CheckpointStorage;
import org.apache.flink.runtime.state.CompletedCheckpointStorageLocation;
import org.apache.flink.runtime.state.KeyGroupRange;
import org.apache.flink.runtime.state.KeyGroupStatePartitionStreamProvider;
import org.apache.flink.runtime.state.KeyedStateHandle;
import org.apache.flink.runtime.state.OperatorStateBackend;
import org.apache.flink.runtime.state.OperatorStateHandle;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.StatePartitionStreamProvider;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.runtime.state.ttl.TtlTimeProvider;
import org.apache.flink.util.CloseableIterable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test for {@link StreamTaskStateInitializerImpl}.
 */
public class StreamTaskStateInitializerImplTest {
    @Test
    public void testNoRestore() throws Exception {
        MemoryStateBackend stateBackend = Mockito.spy(new MemoryStateBackend(1024));
        // No job manager provided state to restore
        StreamTaskStateInitializer streamTaskStateManager = streamTaskStateManager(stateBackend, null, true);
        OperatorID operatorID = new OperatorID(47L, 11L);
        AbstractStreamOperator<?> streamOperator = Mockito.mock(AbstractStreamOperator.class);
        Mockito.when(streamOperator.getOperatorID()).thenReturn(operatorID);
        TypeSerializer<?> typeSerializer = new IntSerializer();
        CloseableRegistry closeableRegistry = new CloseableRegistry();
        StreamOperatorStateContext stateContext = streamTaskStateManager.streamOperatorStateContext(streamOperator.getOperatorID(), streamOperator.getClass().getSimpleName(), streamOperator, typeSerializer, closeableRegistry, new UnregisteredMetricsGroup());
        OperatorStateBackend operatorStateBackend = stateContext.operatorStateBackend();
        AbstractKeyedStateBackend<?> keyedStateBackend = stateContext.keyedStateBackend();
        InternalTimeServiceManager<?> timeServiceManager = stateContext.internalTimerServiceManager();
        CloseableIterable<KeyGroupStatePartitionStreamProvider> keyedStateInputs = stateContext.rawKeyedStateInputs();
        CloseableIterable<StatePartitionStreamProvider> operatorStateInputs = stateContext.rawOperatorStateInputs();
        Assert.assertEquals(false, stateContext.isRestored());
        Assert.assertNotNull(operatorStateBackend);
        Assert.assertNotNull(keyedStateBackend);
        Assert.assertNotNull(timeServiceManager);
        Assert.assertNotNull(keyedStateInputs);
        Assert.assertNotNull(operatorStateInputs);
        StreamTaskStateInitializerImplTest.checkCloseablesRegistered(closeableRegistry, operatorStateBackend, keyedStateBackend, keyedStateInputs, operatorStateInputs);
        Assert.assertFalse(keyedStateInputs.iterator().hasNext());
        Assert.assertFalse(operatorStateInputs.iterator().hasNext());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWithRestore() throws Exception {
        StateBackend mockingBackend = Mockito.spy(new StateBackend() {
            @Override
            public CompletedCheckpointStorageLocation resolveCheckpoint(String pointer) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public CheckpointStorage createCheckpointStorage(JobID jobId) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public <K> AbstractKeyedStateBackend<K> createKeyedStateBackend(Environment env, JobID jobID, String operatorIdentifier, TypeSerializer<K> keySerializer, int numberOfKeyGroups, KeyGroupRange keyGroupRange, TaskKvStateRegistry kvStateRegistry, TtlTimeProvider ttlTimeProvider, MetricGroup metricGroup, @Nonnull
            Collection<KeyedStateHandle> stateHandles, CloseableRegistry cancelStreamRegistry) throws Exception {
                return Mockito.mock(AbstractKeyedStateBackend.class);
            }

            @Override
            public OperatorStateBackend createOperatorStateBackend(Environment env, String operatorIdentifier, @Nonnull
            Collection<OperatorStateHandle> stateHandles, CloseableRegistry cancelStreamRegistry) throws Exception {
                return Mockito.mock(OperatorStateBackend.class);
            }
        });
        OperatorID operatorID = new OperatorID(47L, 11L);
        TaskStateSnapshot taskStateSnapshot = new TaskStateSnapshot();
        Random random = new Random(66);
        OperatorSubtaskState operatorSubtaskState = new OperatorSubtaskState(new org.apache.flink.runtime.state.OperatorStreamStateHandle(Collections.singletonMap("a", new OperatorStateHandle.StateMetaInfo(new long[]{ 0, 10 }, Mode.SPLIT_DISTRIBUTE)), CheckpointTestUtils.createDummyStreamStateHandle(random)), new org.apache.flink.runtime.state.OperatorStreamStateHandle(Collections.singletonMap("_default_", new OperatorStateHandle.StateMetaInfo(new long[]{ 0, 20, 30 }, Mode.SPLIT_DISTRIBUTE)), CheckpointTestUtils.createDummyStreamStateHandle(random)), CheckpointTestUtils.createDummyKeyGroupStateHandle(random), CheckpointTestUtils.createDummyKeyGroupStateHandle(random));
        taskStateSnapshot.putSubtaskStateByOperatorID(operatorID, operatorSubtaskState);
        JobManagerTaskRestore jobManagerTaskRestore = new JobManagerTaskRestore(0L, taskStateSnapshot);
        StreamTaskStateInitializer streamTaskStateManager = streamTaskStateManager(mockingBackend, jobManagerTaskRestore, false);
        AbstractStreamOperator<?> streamOperator = Mockito.mock(AbstractStreamOperator.class);
        Mockito.when(streamOperator.getOperatorID()).thenReturn(operatorID);
        TypeSerializer<?> typeSerializer = new IntSerializer();
        CloseableRegistry closeableRegistry = new CloseableRegistry();
        StreamOperatorStateContext stateContext = streamTaskStateManager.streamOperatorStateContext(streamOperator.getOperatorID(), streamOperator.getClass().getSimpleName(), streamOperator, typeSerializer, closeableRegistry, new UnregisteredMetricsGroup());
        OperatorStateBackend operatorStateBackend = stateContext.operatorStateBackend();
        AbstractKeyedStateBackend<?> keyedStateBackend = stateContext.keyedStateBackend();
        InternalTimeServiceManager<?> timeServiceManager = stateContext.internalTimerServiceManager();
        CloseableIterable<KeyGroupStatePartitionStreamProvider> keyedStateInputs = stateContext.rawKeyedStateInputs();
        CloseableIterable<StatePartitionStreamProvider> operatorStateInputs = stateContext.rawOperatorStateInputs();
        Assert.assertEquals(true, stateContext.isRestored());
        Assert.assertNotNull(operatorStateBackend);
        Assert.assertNotNull(keyedStateBackend);
        // this is deactivated on purpose so that it does not attempt to consume the raw keyed state.
        Assert.assertNull(timeServiceManager);
        Assert.assertNotNull(keyedStateInputs);
        Assert.assertNotNull(operatorStateInputs);
        int count = 0;
        for (KeyGroupStatePartitionStreamProvider keyedStateInput : keyedStateInputs) {
            ++count;
        }
        Assert.assertEquals(1, count);
        count = 0;
        for (StatePartitionStreamProvider operatorStateInput : operatorStateInputs) {
            ++count;
        }
        Assert.assertEquals(3, count);
        StreamTaskStateInitializerImplTest.checkCloseablesRegistered(closeableRegistry, operatorStateBackend, keyedStateBackend, keyedStateInputs, operatorStateInputs);
    }
}

