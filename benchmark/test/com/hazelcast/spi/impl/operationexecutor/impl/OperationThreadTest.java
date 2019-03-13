/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.operationexecutor.impl;


import Packet.Type.OPERATION;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationexecutor.OperationRunnerFactory;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationThreadTest extends OperationExecutorImpl_AbstractTest {
    @Test
    public void testOOME_whenDeserializing() throws Exception {
        handlerFactory = Mockito.mock(OperationRunnerFactory.class);
        OperationRunner handler = Mockito.mock(OperationRunner.class);
        Mockito.when(handlerFactory.createGenericRunner()).thenReturn(handler);
        Mockito.when(handlerFactory.createPartitionRunner(ArgumentMatchers.anyInt())).thenReturn(handler);
        initExecutor();
        OperationExecutorImpl_AbstractTest.DummyOperation operation = new OperationExecutorImpl_AbstractTest.DummyOperation(Operation.GENERIC_PARTITION_ID);
        Packet packet = new Packet(serializationService.toBytes(operation), operation.getPartitionId()).setPacketType(OPERATION);
        Mockito.doThrow(new OutOfMemoryError()).when(handler).run(packet);
        final int oldCount = OutOfMemoryErrorDispatcher.getOutOfMemoryErrorCount();
        executor.accept(packet);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals((oldCount + 1), OutOfMemoryErrorDispatcher.getOutOfMemoryErrorCount());
            }
        });
    }

    @Test
    public void priorityPendingCount_returnScheduleQueuePrioritySize() {
        OperationQueue mockOperationQueue = Mockito.mock(OperationQueue.class);
        Mockito.when(mockOperationQueue.prioritySize()).thenReturn(Integer.MAX_VALUE);
        PartitionOperationThread operationThread = createNewOperationThread(mockOperationQueue);
        int prioritySize = operationThread.priorityPendingCount();
        Assert.assertEquals(Integer.MAX_VALUE, prioritySize);
    }

    @Test
    public void normalPendingCount_returnScheduleQueueNormalSize() {
        OperationQueue mockOperationQueue = Mockito.mock(OperationQueue.class);
        Mockito.when(mockOperationQueue.normalSize()).thenReturn(Integer.MAX_VALUE);
        PartitionOperationThread operationThread = createNewOperationThread(mockOperationQueue);
        int normalSize = operationThread.normalPendingCount();
        Assert.assertEquals(Integer.MAX_VALUE, normalSize);
    }

    @Test
    public void executeOperation_withInvalid_partitionId() {
        final int partitionId = Integer.MAX_VALUE;
        Operation operation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation(partitionId);
        testExecute_withInvalid_partitionId(operation);
    }

    @Test
    public void executePartitionSpecificRunnable_withInvalid_partitionId() {
        final int partitionId = Integer.MAX_VALUE;
        testExecute_withInvalid_partitionId(new PartitionSpecificRunnable() {
            @Override
            public int getPartitionId() {
                return partitionId;
            }

            @Override
            public void run() {
            }
        });
    }

    @Test
    public void executePacket_withInvalid_partitionId() {
        final int partitionId = Integer.MAX_VALUE;
        Operation operation = new OperationExecutorImpl_AbstractTest.DummyPartitionOperation(partitionId);
        Packet packet = new Packet(serializationService.toBytes(operation), operation.getPartitionId()).setPacketType(OPERATION);
        testExecute_withInvalid_partitionId(packet);
    }
}

