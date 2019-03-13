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


import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.buffer.BufferListener;
import org.apache.flink.runtime.io.network.buffer.BufferPool;
import org.apache.flink.runtime.io.network.buffer.BufferProvider;
import org.apache.flink.runtime.io.network.buffer.NetworkBufferPool;
import org.apache.flink.runtime.io.network.netty.NettyMessage.BufferResponse;
import org.apache.flink.runtime.io.network.netty.NettyMessage.ErrorResponse;
import org.apache.flink.runtime.io.network.partition.ResultPartitionID;
import org.apache.flink.runtime.io.network.partition.consumer.InputChannelID;
import org.apache.flink.runtime.io.network.partition.consumer.RemoteInputChannel;
import org.apache.flink.runtime.io.network.partition.consumer.SingleInputGate;
import org.apache.flink.runtime.io.network.util.TestBufferFactory;
import org.apache.flink.shaded.netty4.io.netty.channel.Channel;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelHandlerContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PartitionRequestClientHandlerTest {
    /**
     * Tests a fix for FLINK-1627.
     *
     * <p> FLINK-1627 discovered a race condition, which could lead to an infinite loop when a
     * receiver was cancelled during a certain time of decoding a message. The test reproduces the
     * input, which lead to the infinite loop: when the handler gets a reference to the buffer
     * provider of the receiving input channel, but the respective input channel is released (and
     * the corresponding buffer provider destroyed), the handler did not notice this.
     *
     * @see <a href="https://issues.apache.org/jira/browse/FLINK-1627">FLINK-1627</a>
     */
    @Test(timeout = 60000)
    @SuppressWarnings("unchecked")
    public void testReleaseInputChannelDuringDecode() throws Exception {
        // Mocks an input channel in a state as it was released during a decode.
        final BufferProvider bufferProvider = Mockito.mock(BufferProvider.class);
        Mockito.when(bufferProvider.requestBuffer()).thenReturn(null);
        Mockito.when(bufferProvider.isDestroyed()).thenReturn(true);
        Mockito.when(bufferProvider.addBufferListener(ArgumentMatchers.any(BufferListener.class))).thenReturn(false);
        final RemoteInputChannel inputChannel = Mockito.mock(RemoteInputChannel.class);
        Mockito.when(inputChannel.getInputChannelId()).thenReturn(new InputChannelID());
        Mockito.when(inputChannel.getBufferProvider()).thenReturn(bufferProvider);
        final BufferResponse receivedBuffer = PartitionRequestClientHandlerTest.createBufferResponse(TestBufferFactory.createBuffer(TestBufferFactory.BUFFER_SIZE), 0, inputChannel.getInputChannelId(), 2);
        final PartitionRequestClientHandler client = new PartitionRequestClientHandler();
        client.addInputChannel(inputChannel);
        client.channelRead(Mockito.mock(ChannelHandlerContext.class), receivedBuffer);
    }

    /**
     * Tests a fix for FLINK-1761.
     *
     * <p>FLINK-1761 discovered an IndexOutOfBoundsException, when receiving buffers of size 0.
     */
    @Test
    public void testReceiveEmptyBuffer() throws Exception {
        // Minimal mock of a remote input channel
        final BufferProvider bufferProvider = Mockito.mock(BufferProvider.class);
        Mockito.when(bufferProvider.requestBuffer()).thenReturn(TestBufferFactory.createBuffer(0));
        final RemoteInputChannel inputChannel = Mockito.mock(RemoteInputChannel.class);
        Mockito.when(inputChannel.getInputChannelId()).thenReturn(new InputChannelID());
        Mockito.when(inputChannel.getBufferProvider()).thenReturn(bufferProvider);
        // An empty buffer of size 0
        final Buffer emptyBuffer = TestBufferFactory.createBuffer(0);
        final int backlog = -1;
        final BufferResponse receivedBuffer = PartitionRequestClientHandlerTest.createBufferResponse(emptyBuffer, 0, inputChannel.getInputChannelId(), backlog);
        final PartitionRequestClientHandler client = new PartitionRequestClientHandler();
        client.addInputChannel(inputChannel);
        // Read the empty buffer
        client.channelRead(Mockito.mock(ChannelHandlerContext.class), receivedBuffer);
        // This should not throw an exception
        Mockito.verify(inputChannel, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(inputChannel, Mockito.times(1)).onEmptyBuffer(0, backlog);
    }

    /**
     * Verifies that {@link RemoteInputChannel#onBuffer(Buffer, int, int)} is called when a
     * {@link BufferResponse} is received.
     */
    @Test
    public void testReceiveBuffer() throws Exception {
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(10, 32);
        final SingleInputGate inputGate = PartitionRequestClientHandlerTest.createSingleInputGate();
        final RemoteInputChannel inputChannel = PartitionRequestClientHandlerTest.createRemoteInputChannel(inputGate);
        try {
            final BufferPool bufferPool = networkBufferPool.createBufferPool(8, 8);
            inputGate.setBufferPool(bufferPool);
            final int numExclusiveBuffers = 2;
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            final PartitionRequestClientHandler handler = new PartitionRequestClientHandler();
            handler.addInputChannel(inputChannel);
            final int backlog = 2;
            final BufferResponse bufferResponse = PartitionRequestClientHandlerTest.createBufferResponse(TestBufferFactory.createBuffer(32), 0, inputChannel.getInputChannelId(), backlog);
            handler.channelRead(Mockito.mock(ChannelHandlerContext.class), bufferResponse);
            Assert.assertEquals(1, inputChannel.getNumberOfQueuedBuffers());
        } finally {
            // Release all the buffer resources
            inputGate.releaseAllResources();
            networkBufferPool.destroyAllBufferPools();
            networkBufferPool.destroy();
        }
    }

    /**
     * Verifies that {@link RemoteInputChannel#onFailedPartitionRequest()} is called when a
     * {@link PartitionNotFoundException} is received.
     */
    @Test
    public void testReceivePartitionNotFoundException() throws Exception {
        // Minimal mock of a remote input channel
        final BufferProvider bufferProvider = Mockito.mock(BufferProvider.class);
        Mockito.when(bufferProvider.requestBuffer()).thenReturn(TestBufferFactory.createBuffer(0));
        final RemoteInputChannel inputChannel = Mockito.mock(RemoteInputChannel.class);
        Mockito.when(inputChannel.getInputChannelId()).thenReturn(new InputChannelID());
        Mockito.when(inputChannel.getBufferProvider()).thenReturn(bufferProvider);
        final ErrorResponse partitionNotFound = new ErrorResponse(new org.apache.flink.runtime.io.network.partition.PartitionNotFoundException(new ResultPartitionID()), inputChannel.getInputChannelId());
        final PartitionRequestClientHandler client = new PartitionRequestClientHandler();
        client.addInputChannel(inputChannel);
        // Mock channel context
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
        Mockito.when(ctx.channel()).thenReturn(Mockito.mock(Channel.class));
        client.channelActive(ctx);
        client.channelRead(ctx, partitionNotFound);
        Mockito.verify(inputChannel, Mockito.times(1)).onFailedPartitionRequest();
    }

    @Test
    public void testCancelBeforeActive() throws Exception {
        final RemoteInputChannel inputChannel = Mockito.mock(RemoteInputChannel.class);
        Mockito.when(inputChannel.getInputChannelId()).thenReturn(new InputChannelID());
        final PartitionRequestClientHandler client = new PartitionRequestClientHandler();
        client.addInputChannel(inputChannel);
        // Don't throw NPE
        client.cancelRequestFor(null);
        // Don't throw NPE, because channel is not active yet
        client.cancelRequestFor(inputChannel.getInputChannelId());
    }
}

