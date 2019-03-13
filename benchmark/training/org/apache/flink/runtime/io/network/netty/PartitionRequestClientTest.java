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
package org.apache.flink.runtime.io.network.netty;


import org.apache.flink.runtime.io.network.ConnectionID;
import org.apache.flink.runtime.io.network.buffer.BufferPool;
import org.apache.flink.runtime.io.network.buffer.NetworkBufferPool;
import org.apache.flink.runtime.io.network.netty.NettyMessage.PartitionRequest;
import org.apache.flink.runtime.io.network.partition.consumer.RemoteInputChannel;
import org.apache.flink.runtime.io.network.partition.consumer.SingleInputGate;
import org.apache.flink.shaded.netty4.io.netty.channel.embedded.EmbeddedChannel;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link PartitionRequestClient}.
 */
public class PartitionRequestClientTest {
    @Test
    public void testRetriggerPartitionRequest() throws Exception {
        final long deadline = (System.currentTimeMillis()) + 30000L;// 30 secs

        final PartitionRequestClientHandler handler = new PartitionRequestClientHandler();
        final EmbeddedChannel channel = new EmbeddedChannel(handler);
        final PartitionRequestClient client = new PartitionRequestClient(channel, handler, Mockito.mock(ConnectionID.class), Mockito.mock(PartitionRequestClientFactory.class));
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(10, 32);
        final SingleInputGate inputGate = PartitionRequestClientHandlerTest.createSingleInputGate();
        final RemoteInputChannel inputChannel = PartitionRequestClientHandlerTest.createRemoteInputChannel(inputGate, client, 1, 2);
        try {
            final BufferPool bufferPool = networkBufferPool.createBufferPool(6, 6);
            inputGate.setBufferPool(bufferPool);
            final int numExclusiveBuffers = 2;
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            // first subpartition request
            inputChannel.requestSubpartition(0);
            Assert.assertTrue(channel.isWritable());
            Object readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(PartitionRequest.class));
            Assert.assertEquals(inputChannel.getInputChannelId(), ((PartitionRequest) (readFromOutbound)).receiverId);
            Assert.assertEquals(numExclusiveBuffers, ((PartitionRequest) (readFromOutbound)).credit);
            // retrigger subpartition request, e.g. due to failures
            inputGate.retriggerPartitionRequest(inputChannel.getPartitionId().getPartitionId());
            runAllScheduledPendingTasks(channel, deadline);
            readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(PartitionRequest.class));
            Assert.assertEquals(inputChannel.getInputChannelId(), ((PartitionRequest) (readFromOutbound)).receiverId);
            Assert.assertEquals(numExclusiveBuffers, ((PartitionRequest) (readFromOutbound)).credit);
            // retrigger subpartition request once again, e.g. due to failures
            inputGate.retriggerPartitionRequest(inputChannel.getPartitionId().getPartitionId());
            runAllScheduledPendingTasks(channel, deadline);
            readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(PartitionRequest.class));
            Assert.assertEquals(inputChannel.getInputChannelId(), ((PartitionRequest) (readFromOutbound)).receiverId);
            Assert.assertEquals(numExclusiveBuffers, ((PartitionRequest) (readFromOutbound)).credit);
            Assert.assertNull(channel.readOutbound());
        } finally {
            // Release all the buffer resources
            inputGate.releaseAllResources();
            networkBufferPool.destroyAllBufferPools();
            networkBufferPool.destroy();
        }
    }

    @Test
    public void testDoublePartitionRequest() throws Exception {
        final PartitionRequestClientHandler handler = new PartitionRequestClientHandler();
        final EmbeddedChannel channel = new EmbeddedChannel(handler);
        final PartitionRequestClient client = new PartitionRequestClient(channel, handler, Mockito.mock(ConnectionID.class), Mockito.mock(PartitionRequestClientFactory.class));
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(10, 32);
        final SingleInputGate inputGate = PartitionRequestClientHandlerTest.createSingleInputGate();
        final RemoteInputChannel inputChannel = PartitionRequestClientHandlerTest.createRemoteInputChannel(inputGate, client);
        try {
            final BufferPool bufferPool = networkBufferPool.createBufferPool(6, 6);
            inputGate.setBufferPool(bufferPool);
            final int numExclusiveBuffers = 2;
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            inputChannel.requestSubpartition(0);
            // The input channel should only send one partition request
            Assert.assertTrue(channel.isWritable());
            Object readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(PartitionRequest.class));
            Assert.assertEquals(inputChannel.getInputChannelId(), ((PartitionRequest) (readFromOutbound)).receiverId);
            Assert.assertEquals(numExclusiveBuffers, ((PartitionRequest) (readFromOutbound)).credit);
            Assert.assertNull(channel.readOutbound());
        } finally {
            // Release all the buffer resources
            inputGate.releaseAllResources();
            networkBufferPool.destroyAllBufferPools();
            networkBufferPool.destroy();
        }
    }
}

