/**
 * Copyright 2014 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.netty;


import io.grpc.internal.Stream;
import io.grpc.netty.WriteQueue.QueuedCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http2.Http2Stream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Base class for Netty stream unit tests.
 */
public abstract class NettyStreamTestBase<T extends Stream> {
    protected static final String MESSAGE = "hello world";

    protected static final int STREAM_ID = 1;

    @Mock
    protected Channel channel;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private ChannelPipeline pipeline;

    @Mock
    protected EventLoop eventLoop;

    // ChannelPromise has too many methods to implement; we stubbed all necessary methods of Future.
    @SuppressWarnings("DoNotMock")
    @Mock
    protected ChannelPromise promise;

    @Mock
    protected Http2Stream http2Stream;

    @Mock
    protected WriteQueue writeQueue;

    protected T stream;

    @Test
    public void inboundMessageShouldCallListener() throws Exception {
        request(1);
        if ((stream) instanceof NettyServerStream) {
            transportState().inboundDataReceived(NettyTestUtil.messageFrame(NettyStreamTestBase.MESSAGE), false);
        } else {
            transportState().transportDataReceived(NettyTestUtil.messageFrame(NettyStreamTestBase.MESSAGE), false);
        }
        InputStream message = listenerMessageQueue().poll();
        // Verify that inbound flow control window update has been disabled for the stream.
        Assert.assertEquals(NettyStreamTestBase.MESSAGE, NettyTestUtil.toString(message));
        Assert.assertNull("no additional message expected", listenerMessageQueue().poll());
    }

    @Test
    public void shouldBeImmediatelyReadyForData() {
        Assert.assertTrue(isReady());
    }

    @Test
    public void closedShouldNotBeReady() throws IOException {
        Assert.assertTrue(isReady());
        closeStream();
        Assert.assertFalse(isReady());
    }

    @Test
    public void notifiedOnReadyAfterWriteCompletes() throws IOException {
        sendHeadersIfServer();
        Assert.assertTrue(isReady());
        byte[] msg = largeMessage();
        // The channel.write future is set up to automatically complete, indicating that the write is
        // done.
        writeMessage(new ByteArrayInputStream(msg));
        flush();
        Assert.assertTrue(isReady());
        Mockito.verify(listener()).onReady();
    }

    @Test
    public void shouldBeReadyForDataAfterWritingSmallMessage() throws IOException {
        sendHeadersIfServer();
        // Make sure the writes don't complete so we "back up"
        ChannelPromise uncompletedPromise = new io.netty.channel.DefaultChannelPromise(channel);
        Mockito.when(writeQueue.enqueue(ArgumentMatchers.any(QueuedCommand.class), ArgumentMatchers.anyBoolean())).thenReturn(uncompletedPromise);
        Assert.assertTrue(isReady());
        byte[] msg = smallMessage();
        writeMessage(new ByteArrayInputStream(msg));
        flush();
        Assert.assertTrue(isReady());
        Mockito.verify(listener(), Mockito.never()).onReady();
    }

    @Test
    public void shouldNotBeReadyForDataAfterWritingLargeMessage() throws IOException {
        sendHeadersIfServer();
        // Make sure the writes don't complete so we "back up"
        ChannelPromise uncompletedPromise = new io.netty.channel.DefaultChannelPromise(channel);
        Mockito.when(writeQueue.enqueue(ArgumentMatchers.any(QueuedCommand.class), ArgumentMatchers.anyBoolean())).thenReturn(uncompletedPromise);
        Assert.assertTrue(isReady());
        byte[] msg = largeMessage();
        writeMessage(new ByteArrayInputStream(msg));
        flush();
        Assert.assertFalse(isReady());
        Mockito.verify(listener(), Mockito.never()).onReady();
    }
}

