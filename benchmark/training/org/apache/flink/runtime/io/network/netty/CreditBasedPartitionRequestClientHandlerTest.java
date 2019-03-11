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
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.buffer.BufferListener;
import org.apache.flink.runtime.io.network.buffer.BufferPool;
import org.apache.flink.runtime.io.network.buffer.BufferProvider;
import org.apache.flink.runtime.io.network.buffer.NetworkBufferPool;
import org.apache.flink.runtime.io.network.netty.NettyMessage.AddCredit;
import org.apache.flink.runtime.io.network.netty.NettyMessage.BufferResponse;
import org.apache.flink.runtime.io.network.netty.NettyMessage.CloseRequest;
import org.apache.flink.runtime.io.network.netty.NettyMessage.ErrorResponse;
import org.apache.flink.runtime.io.network.netty.NettyMessage.PartitionRequest;
import org.apache.flink.runtime.io.network.partition.ResultPartitionID;
import org.apache.flink.runtime.io.network.partition.consumer.InputChannelID;
import org.apache.flink.runtime.io.network.partition.consumer.RemoteInputChannel;
import org.apache.flink.runtime.io.network.partition.consumer.SingleInputGate;
import org.apache.flink.runtime.io.network.util.TestBufferFactory;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.apache.flink.shaded.netty4.io.netty.channel.Channel;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelHandlerContext;
import org.apache.flink.shaded.netty4.io.netty.channel.embedded.EmbeddedChannel;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CreditBasedPartitionRequestClientHandlerTest {
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
        final CreditBasedPartitionRequestClientHandler client = new CreditBasedPartitionRequestClientHandler();
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
        final int backlog = 2;
        final BufferResponse receivedBuffer = PartitionRequestClientHandlerTest.createBufferResponse(emptyBuffer, 0, inputChannel.getInputChannelId(), backlog);
        final CreditBasedPartitionRequestClientHandler client = new CreditBasedPartitionRequestClientHandler();
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
            final CreditBasedPartitionRequestClientHandler handler = new CreditBasedPartitionRequestClientHandler();
            handler.addInputChannel(inputChannel);
            final int backlog = 2;
            final BufferResponse bufferResponse = PartitionRequestClientHandlerTest.createBufferResponse(TestBufferFactory.createBuffer(32), 0, inputChannel.getInputChannelId(), backlog);
            handler.channelRead(Mockito.mock(ChannelHandlerContext.class), bufferResponse);
            Assert.assertEquals(1, inputChannel.getNumberOfQueuedBuffers());
            Assert.assertEquals(2, inputChannel.getSenderBacklog());
        } finally {
            // Release all the buffer resources
            inputGate.releaseAllResources();
            networkBufferPool.destroyAllBufferPools();
            networkBufferPool.destroy();
        }
    }

    /**
     * Verifies that {@link RemoteInputChannel#onError(Throwable)} is called when a
     * {@link BufferResponse} is received but no available buffer in input channel.
     */
    @Test
    public void testThrowExceptionForNoAvailableBuffer() throws Exception {
        final SingleInputGate inputGate = PartitionRequestClientHandlerTest.createSingleInputGate();
        final RemoteInputChannel inputChannel = Mockito.spy(PartitionRequestClientHandlerTest.createRemoteInputChannel(inputGate));
        final CreditBasedPartitionRequestClientHandler handler = new CreditBasedPartitionRequestClientHandler();
        handler.addInputChannel(inputChannel);
        Assert.assertEquals("There should be no buffers available in the channel.", 0, inputChannel.getNumberOfAvailableBuffers());
        final BufferResponse bufferResponse = PartitionRequestClientHandlerTest.createBufferResponse(TestBufferFactory.createBuffer(TestBufferFactory.BUFFER_SIZE), 0, inputChannel.getInputChannelId(), 2);
        handler.channelRead(Mockito.mock(ChannelHandlerContext.class), bufferResponse);
        Mockito.verify(inputChannel, Mockito.times(1)).onError(ArgumentMatchers.any(IllegalStateException.class));
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
        final CreditBasedPartitionRequestClientHandler client = new CreditBasedPartitionRequestClientHandler();
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
        final CreditBasedPartitionRequestClientHandler client = new CreditBasedPartitionRequestClientHandler();
        client.addInputChannel(inputChannel);
        // Don't throw NPE
        client.cancelRequestFor(null);
        // Don't throw NPE, because channel is not active yet
        client.cancelRequestFor(inputChannel.getInputChannelId());
    }

    /**
     * Verifies that {@link RemoteInputChannel} is enqueued in the pipeline for notifying credits,
     * and verifies the behaviour of credit notification by triggering channel's writability changed.
     */
    @Test
    public void testNotifyCreditAvailable() throws Exception {
        final CreditBasedPartitionRequestClientHandler handler = new CreditBasedPartitionRequestClientHandler();
        final EmbeddedChannel channel = new EmbeddedChannel(handler);
        final PartitionRequestClient client = new PartitionRequestClient(channel, handler, Mockito.mock(ConnectionID.class), Mockito.mock(PartitionRequestClientFactory.class));
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(10, 32);
        final SingleInputGate inputGate = PartitionRequestClientHandlerTest.createSingleInputGate();
        final RemoteInputChannel inputChannel1 = PartitionRequestClientHandlerTest.createRemoteInputChannel(inputGate, client);
        final RemoteInputChannel inputChannel2 = PartitionRequestClientHandlerTest.createRemoteInputChannel(inputGate, client);
        try {
            final BufferPool bufferPool = networkBufferPool.createBufferPool(6, 6);
            inputGate.setBufferPool(bufferPool);
            final int numExclusiveBuffers = 2;
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            inputChannel1.requestSubpartition(0);
            inputChannel2.requestSubpartition(0);
            // The two input channels should send partition requests
            Assert.assertTrue(channel.isWritable());
            Object readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(PartitionRequest.class));
            Assert.assertEquals(inputChannel1.getInputChannelId(), ((PartitionRequest) (readFromOutbound)).receiverId);
            Assert.assertEquals(2, ((PartitionRequest) (readFromOutbound)).credit);
            readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(PartitionRequest.class));
            Assert.assertEquals(inputChannel2.getInputChannelId(), ((PartitionRequest) (readFromOutbound)).receiverId);
            Assert.assertEquals(2, ((PartitionRequest) (readFromOutbound)).credit);
            // The buffer response will take one available buffer from input channel, and it will trigger
            // requesting (backlog + numExclusiveBuffers - numAvailableBuffers) floating buffers
            final BufferResponse bufferResponse1 = PartitionRequestClientHandlerTest.createBufferResponse(TestBufferFactory.createBuffer(32), 0, inputChannel1.getInputChannelId(), 1);
            final BufferResponse bufferResponse2 = PartitionRequestClientHandlerTest.createBufferResponse(TestBufferFactory.createBuffer(32), 0, inputChannel2.getInputChannelId(), 1);
            handler.channelRead(Mockito.mock(ChannelHandlerContext.class), bufferResponse1);
            handler.channelRead(Mockito.mock(ChannelHandlerContext.class), bufferResponse2);
            Assert.assertEquals(2, inputChannel1.getUnannouncedCredit());
            Assert.assertEquals(2, inputChannel2.getUnannouncedCredit());
            channel.runPendingTasks();
            // The two input channels should notify credits availability via the writable channel
            readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(AddCredit.class));
            Assert.assertEquals(inputChannel1.getInputChannelId(), ((AddCredit) (readFromOutbound)).receiverId);
            Assert.assertEquals(2, ((AddCredit) (readFromOutbound)).credit);
            readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(AddCredit.class));
            Assert.assertEquals(inputChannel2.getInputChannelId(), ((AddCredit) (readFromOutbound)).receiverId);
            Assert.assertEquals(2, ((AddCredit) (readFromOutbound)).credit);
            Assert.assertNull(channel.readOutbound());
            ByteBuf channelBlockingBuffer = PartitionRequestQueueTest.blockChannel(channel);
            // Trigger notify credits availability via buffer response on the condition of an un-writable channel
            final BufferResponse bufferResponse3 = PartitionRequestClientHandlerTest.createBufferResponse(TestBufferFactory.createBuffer(32), 1, inputChannel1.getInputChannelId(), 1);
            handler.channelRead(Mockito.mock(ChannelHandlerContext.class), bufferResponse3);
            Assert.assertEquals(1, inputChannel1.getUnannouncedCredit());
            Assert.assertEquals(0, inputChannel2.getUnannouncedCredit());
            channel.runPendingTasks();
            // The input channel will not notify credits via un-writable channel
            Assert.assertFalse(channel.isWritable());
            Assert.assertNull(channel.readOutbound());
            // Flush the buffer to make the channel writable again
            channel.flush();
            Assert.assertSame(channelBlockingBuffer, channel.readOutbound());
            // The input channel should notify credits via channel's writability changed event
            Assert.assertTrue(channel.isWritable());
            readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(AddCredit.class));
            Assert.assertEquals(1, ((AddCredit) (readFromOutbound)).credit);
            Assert.assertEquals(0, inputChannel1.getUnannouncedCredit());
            Assert.assertEquals(0, inputChannel2.getUnannouncedCredit());
            // no more messages
            Assert.assertNull(channel.readOutbound());
        } finally {
            // Release all the buffer resources
            inputGate.releaseAllResources();
            networkBufferPool.destroyAllBufferPools();
            networkBufferPool.destroy();
        }
    }

    /**
     * Verifies that {@link RemoteInputChannel} is enqueued in the pipeline, but {@link AddCredit}
     * message is not sent actually when this input channel is released.
     */
    @Test
    public void testNotifyCreditAvailableAfterReleased() throws Exception {
        final CreditBasedPartitionRequestClientHandler handler = new CreditBasedPartitionRequestClientHandler();
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
            // This should send the partition request
            Object readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(PartitionRequest.class));
            Assert.assertEquals(2, ((PartitionRequest) (readFromOutbound)).credit);
            // Trigger request floating buffers via buffer response to notify credits available
            final BufferResponse bufferResponse = PartitionRequestClientHandlerTest.createBufferResponse(TestBufferFactory.createBuffer(32), 0, inputChannel.getInputChannelId(), 1);
            handler.channelRead(Mockito.mock(ChannelHandlerContext.class), bufferResponse);
            Assert.assertEquals(2, inputChannel.getUnannouncedCredit());
            // Release the input channel
            inputGate.releaseAllResources();
            // it should send a close request after releasing the input channel,
            // but will not notify credits for a released input channel.
            readFromOutbound = channel.readOutbound();
            Assert.assertThat(readFromOutbound, Matchers.instanceOf(CloseRequest.class));
            channel.runPendingTasks();
            Assert.assertNull(channel.readOutbound());
        } finally {
            // Release all the buffer resources
            inputGate.releaseAllResources();
            networkBufferPool.destroyAllBufferPools();
            networkBufferPool.destroy();
        }
    }
}

