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
package org.apache.flink.runtime.io.network;


import ResultPartitionType.BLOCKING;
import ResultPartitionType.PIPELINED;
import ResultPartitionType.PIPELINED_BOUNDED;
import TaskManagerOptions.NETWORK_BUFFERS_PER_CHANNEL;
import java.io.IOException;
import org.apache.flink.runtime.io.network.partition.ResultPartition;
import org.apache.flink.runtime.io.network.partition.consumer.SingleInputGate;
import org.apache.flink.runtime.taskmanager.Task;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


/**
 * Various tests for the {@link NetworkEnvironment} class.
 */
@RunWith(Parameterized.class)
public class NetworkEnvironmentTest {
    private static final int numBuffers = 1024;

    private static final int memorySegmentSize = 128;

    @Parameterized.Parameter
    public boolean enableCreditBasedFlowControl;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Verifies that {@link NetworkEnvironment#registerTask(Task)} sets up (un)bounded buffer pool
     * instances for various types of input and output channels.
     */
    @Test
    public void testRegisterTaskUsesBoundedBuffers() throws Exception {
        final NetworkEnvironment network = new NetworkEnvironment(NetworkEnvironmentTest.numBuffers, NetworkEnvironmentTest.memorySegmentSize, 0, 0, 2, 8, enableCreditBasedFlowControl);
        // result partitions
        ResultPartition rp1 = NetworkEnvironmentTest.createResultPartition(PIPELINED, 2);
        ResultPartition rp2 = NetworkEnvironmentTest.createResultPartition(BLOCKING, 2);
        ResultPartition rp3 = NetworkEnvironmentTest.createResultPartition(PIPELINED_BOUNDED, 2);
        ResultPartition rp4 = NetworkEnvironmentTest.createResultPartition(PIPELINED_BOUNDED, 8);
        final ResultPartition[] resultPartitions = new ResultPartition[]{ rp1, rp2, rp3, rp4 };
        // input gates
        SingleInputGate ig1 = createSingleInputGate(PIPELINED, 2);
        SingleInputGate ig2 = createSingleInputGate(BLOCKING, 2);
        SingleInputGate ig3 = createSingleInputGate(PIPELINED_BOUNDED, 2);
        SingleInputGate ig4 = createSingleInputGate(PIPELINED_BOUNDED, 8);
        final SingleInputGate[] inputGates = new SingleInputGate[]{ ig1, ig2, ig3, ig4 };
        // overall task to register
        Task task = Mockito.mock(Task.class);
        Mockito.when(task.getProducedPartitions()).thenReturn(resultPartitions);
        Mockito.when(task.getAllInputGates()).thenReturn(inputGates);
        network.registerTask(task);
        // verify buffer pools for the result partitions
        Assert.assertEquals(rp1.getNumberOfSubpartitions(), rp1.getBufferPool().getNumberOfRequiredMemorySegments());
        Assert.assertEquals(rp2.getNumberOfSubpartitions(), rp2.getBufferPool().getNumberOfRequiredMemorySegments());
        Assert.assertEquals(rp3.getNumberOfSubpartitions(), rp3.getBufferPool().getNumberOfRequiredMemorySegments());
        Assert.assertEquals(rp4.getNumberOfSubpartitions(), rp4.getBufferPool().getNumberOfRequiredMemorySegments());
        Assert.assertEquals(Integer.MAX_VALUE, rp1.getBufferPool().getMaxNumberOfMemorySegments());
        Assert.assertEquals(Integer.MAX_VALUE, rp2.getBufferPool().getMaxNumberOfMemorySegments());
        Assert.assertEquals(((2 * 2) + 8), rp3.getBufferPool().getMaxNumberOfMemorySegments());
        Assert.assertEquals(((8 * 2) + 8), rp4.getBufferPool().getMaxNumberOfMemorySegments());
        // verify buffer pools for the input gates (NOTE: credit-based uses minimum required buffers
        // for exclusive buffers not managed by the buffer pool)
        Assert.assertEquals((enableCreditBasedFlowControl ? 0 : 2), ig1.getBufferPool().getNumberOfRequiredMemorySegments());
        Assert.assertEquals((enableCreditBasedFlowControl ? 0 : 2), ig2.getBufferPool().getNumberOfRequiredMemorySegments());
        Assert.assertEquals((enableCreditBasedFlowControl ? 0 : 2), ig3.getBufferPool().getNumberOfRequiredMemorySegments());
        Assert.assertEquals((enableCreditBasedFlowControl ? 0 : 8), ig4.getBufferPool().getNumberOfRequiredMemorySegments());
        Assert.assertEquals(Integer.MAX_VALUE, ig1.getBufferPool().getMaxNumberOfMemorySegments());
        Assert.assertEquals(Integer.MAX_VALUE, ig2.getBufferPool().getMaxNumberOfMemorySegments());
        Assert.assertEquals((enableCreditBasedFlowControl ? 8 : (2 * 2) + 8), ig3.getBufferPool().getMaxNumberOfMemorySegments());
        Assert.assertEquals((enableCreditBasedFlowControl ? 8 : (8 * 2) + 8), ig4.getBufferPool().getMaxNumberOfMemorySegments());
        int invokations = (enableCreditBasedFlowControl) ? 1 : 0;
        Mockito.verify(ig1, Mockito.times(invokations)).assignExclusiveSegments(network.getNetworkBufferPool(), 2);
        Mockito.verify(ig2, Mockito.times(invokations)).assignExclusiveSegments(network.getNetworkBufferPool(), 2);
        Mockito.verify(ig3, Mockito.times(invokations)).assignExclusiveSegments(network.getNetworkBufferPool(), 2);
        Mockito.verify(ig4, Mockito.times(invokations)).assignExclusiveSegments(network.getNetworkBufferPool(), 2);
        for (ResultPartition rp : resultPartitions) {
            rp.release();
        }
        for (SingleInputGate ig : inputGates) {
            ig.releaseAllResources();
        }
        network.shutdown();
    }

    /**
     * Verifies that {@link NetworkEnvironment#registerTask(Task)} sets up (un)bounded buffer pool
     * instances for various types of input and output channels working with the bare minimum of
     * required buffers.
     */
    @Test
    public void testRegisterTaskWithLimitedBuffers() throws Exception {
        final int bufferCount;
        // outgoing: 1 buffer per channel (always)
        if (!(enableCreditBasedFlowControl)) {
            // incoming: 1 buffer per channel
            bufferCount = 20;
        } else {
            // incoming: 2 exclusive buffers per channel
            bufferCount = 10 + (10 * (NETWORK_BUFFERS_PER_CHANNEL.defaultValue()));
        }
        testRegisterTaskWithLimitedBuffers(bufferCount);
    }

    /**
     * Verifies that {@link NetworkEnvironment#registerTask(Task)} fails if the bare minimum of
     * required buffers is not available (we are one buffer short).
     */
    @Test
    public void testRegisterTaskWithInsufficientBuffers() throws Exception {
        final int bufferCount;
        // outgoing: 1 buffer per channel (always)
        if (!(enableCreditBasedFlowControl)) {
            // incoming: 1 buffer per channel
            bufferCount = 19;
        } else {
            // incoming: 2 exclusive buffers per channel
            bufferCount = (10 + (10 * (NETWORK_BUFFERS_PER_CHANNEL.defaultValue()))) - 1;
        }
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Insufficient number of network buffers");
        testRegisterTaskWithLimitedBuffers(bufferCount);
    }
}

