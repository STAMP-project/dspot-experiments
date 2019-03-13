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
package org.apache.flink.runtime.io.network.partition;


import ResultPartitionType.BLOCKING;
import ResultPartitionType.PIPELINED;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.io.disk.iomanager.IOManagerAsync;
import org.apache.flink.runtime.io.network.buffer.BufferBuilderTestUtils;
import org.apache.flink.runtime.taskmanager.TaskActions;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link ResultPartition}.
 */
public class ResultPartitionTest {
    /**
     * Asynchronous I/O manager.
     */
    private static final IOManager ioManager = new IOManagerAsync();

    /**
     * Tests the schedule or update consumers message sending behaviour depending on the relevant flags.
     */
    @Test
    public void testSendScheduleOrUpdateConsumersMessage() throws Exception {
        {
            // Pipelined, send message => notify
            ResultPartitionConsumableNotifier notifier = Mockito.mock(ResultPartitionConsumableNotifier.class);
            ResultPartition partition = ResultPartitionTest.createPartition(notifier, PIPELINED, true);
            partition.addBufferConsumer(BufferBuilderTestUtils.createFilledBufferConsumer(BufferBuilderTestUtils.BUFFER_SIZE), 0);
            Mockito.verify(notifier, Mockito.times(1)).notifyPartitionConsumable(ArgumentMatchers.eq(partition.getJobId()), ArgumentMatchers.eq(partition.getPartitionId()), ArgumentMatchers.any(TaskActions.class));
        }
        {
            // Pipelined, don't send message => don't notify
            ResultPartitionConsumableNotifier notifier = Mockito.mock(ResultPartitionConsumableNotifier.class);
            ResultPartition partition = ResultPartitionTest.createPartition(notifier, PIPELINED, false);
            partition.addBufferConsumer(BufferBuilderTestUtils.createFilledBufferConsumer(BufferBuilderTestUtils.BUFFER_SIZE), 0);
            Mockito.verify(notifier, Mockito.never()).notifyPartitionConsumable(ArgumentMatchers.any(JobID.class), ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.any(TaskActions.class));
        }
        {
            // Blocking, send message => don't notify
            ResultPartitionConsumableNotifier notifier = Mockito.mock(ResultPartitionConsumableNotifier.class);
            ResultPartition partition = ResultPartitionTest.createPartition(notifier, BLOCKING, true);
            partition.addBufferConsumer(BufferBuilderTestUtils.createFilledBufferConsumer(BufferBuilderTestUtils.BUFFER_SIZE), 0);
            Mockito.verify(notifier, Mockito.never()).notifyPartitionConsumable(ArgumentMatchers.any(JobID.class), ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.any(TaskActions.class));
        }
        {
            // Blocking, don't send message => don't notify
            ResultPartitionConsumableNotifier notifier = Mockito.mock(ResultPartitionConsumableNotifier.class);
            ResultPartition partition = ResultPartitionTest.createPartition(notifier, BLOCKING, false);
            partition.addBufferConsumer(BufferBuilderTestUtils.createFilledBufferConsumer(BufferBuilderTestUtils.BUFFER_SIZE), 0);
            Mockito.verify(notifier, Mockito.never()).notifyPartitionConsumable(ArgumentMatchers.any(JobID.class), ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.any(TaskActions.class));
        }
    }

    @Test
    public void testAddOnFinishedPipelinedPartition() throws Exception {
        testAddOnFinishedPartition(PIPELINED);
    }

    @Test
    public void testAddOnFinishedBlockingPartition() throws Exception {
        testAddOnFinishedPartition(BLOCKING);
    }

    @Test
    public void testAddOnReleasedPipelinedPartition() throws Exception {
        testAddOnReleasedPartition(PIPELINED);
    }

    @Test
    public void testAddOnReleasedBlockingPartition() throws Exception {
        testAddOnReleasedPartition(BLOCKING);
    }

    @Test
    public void testAddOnPipelinedPartition() throws Exception {
        testAddOnPartition(PIPELINED);
    }

    @Test
    public void testAddOnBlockingPartition() throws Exception {
        testAddOnPartition(BLOCKING);
    }

    @Test
    public void testReleaseMemoryOnBlockingPartition() throws Exception {
        testReleaseMemory(BLOCKING);
    }

    @Test
    public void testReleaseMemoryOnPipelinedPartition() throws Exception {
        testReleaseMemory(PIPELINED);
    }
}

