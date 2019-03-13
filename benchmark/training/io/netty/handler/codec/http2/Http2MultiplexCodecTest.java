/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http2;


import Http2Error.CANCEL;
import Http2Error.INTERNAL_ERROR;
import Http2Error.NO_ERROR;
import HttpMethod.GET;
import HttpScheme.HTTPS;
import Unpooled.EMPTY_BUFFER;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http2.Http2Exception.StreamException;
import io.netty.util.AsciiString;
import io.netty.util.AttributeKey;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static Http2Error.PROTOCOL_ERROR;
import static Http2Error.STREAM_CLOSED;


/**
 * Unit tests for {@link Http2MultiplexCodec}.
 */
public class Http2MultiplexCodecTest {
    private final Http2Headers request = new DefaultHttp2Headers().method(GET.asciiName()).scheme(HTTPS.name()).authority(new AsciiString("example.org")).path(new AsciiString("/foo"));

    private EmbeddedChannel parentChannel;

    private Http2FrameWriter frameWriter;

    private Http2FrameInboundWriter frameInboundWriter;

    private TestChannelInitializer childChannelInitializer;

    private Http2MultiplexCodec codec;

    private static final int initialRemoteStreamWindow = 1024;

    // TODO(buchgr): Flush from child channel
    // TODO(buchgr): ChildChannel.childReadComplete()
    // TODO(buchgr): GOAWAY Logic
    // TODO(buchgr): Test ChannelConfig.setMaxMessagesPerRead
    @Test
    public void writeUnknownFrame() {
        Http2StreamChannel childChannel = newOutboundStream(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                ctx.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()));
                ctx.writeAndFlush(new DefaultHttp2UnknownFrame(((byte) (99)), new Http2Flags()));
                ctx.fireChannelActive();
            }
        });
        Assert.assertTrue(childChannel.isActive());
        parentChannel.runPendingTasks();
        Mockito.verify(frameWriter).writeFrame(ArgumentMatchers.eq(codec.ctx), ArgumentMatchers.eq(((byte) (99))), Http2MultiplexCodecTest.eqStreamId(childChannel), ArgumentMatchers.any(Http2Flags.class), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void readUnkownFrame() {
        LastInboundHandler handler = new LastInboundHandler();
        Http2StreamChannel channel = newInboundStream(3, true, handler);
        frameInboundWriter.writeInboundFrame(((byte) (99)), channel.stream().id(), new Http2Flags(), EMPTY_BUFFER);
        // header frame and unknown frame
        Http2MultiplexCodecTest.verifyFramesMultiplexedToCorrectChannel(channel, handler, 2);
        Channel childChannel = newOutboundStream(new ChannelInboundHandlerAdapter());
        Assert.assertTrue(childChannel.isActive());
    }

    @Test
    public void headerAndDataFramesShouldBeDelivered() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel channel = newInboundStream(3, false, inboundHandler);
        Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(request).stream(channel.stream());
        Http2DataFrame dataFrame1 = new DefaultHttp2DataFrame(Http2TestUtil.bb("hello")).stream(channel.stream());
        Http2DataFrame dataFrame2 = new DefaultHttp2DataFrame(Http2TestUtil.bb("world")).stream(channel.stream());
        Assert.assertTrue(inboundHandler.isChannelActive());
        frameInboundWriter.writeInboundData(channel.stream().id(), Http2TestUtil.bb("hello"), 0, false);
        frameInboundWriter.writeInboundData(channel.stream().id(), Http2TestUtil.bb("world"), 0, false);
        Assert.assertEquals(headersFrame, inboundHandler.readInbound());
        Http2TestUtil.assertEqualsAndRelease(dataFrame1, inboundHandler.<Http2Frame>readInbound());
        Http2TestUtil.assertEqualsAndRelease(dataFrame2, inboundHandler.<Http2Frame>readInbound());
        Assert.assertNull(inboundHandler.readInbound());
    }

    @Test
    public void framesShouldBeMultiplexed() {
        LastInboundHandler handler1 = new LastInboundHandler();
        Http2StreamChannel channel1 = newInboundStream(3, false, handler1);
        LastInboundHandler handler2 = new LastInboundHandler();
        Http2StreamChannel channel2 = newInboundStream(5, false, handler2);
        LastInboundHandler handler3 = new LastInboundHandler();
        Http2StreamChannel channel3 = newInboundStream(11, false, handler3);
        Http2MultiplexCodecTest.verifyFramesMultiplexedToCorrectChannel(channel1, handler1, 1);
        Http2MultiplexCodecTest.verifyFramesMultiplexedToCorrectChannel(channel2, handler2, 1);
        Http2MultiplexCodecTest.verifyFramesMultiplexedToCorrectChannel(channel3, handler3, 1);
        frameInboundWriter.writeInboundData(channel2.stream().id(), Http2TestUtil.bb("hello"), 0, false);
        frameInboundWriter.writeInboundData(channel1.stream().id(), Http2TestUtil.bb("foo"), 0, true);
        frameInboundWriter.writeInboundData(channel2.stream().id(), Http2TestUtil.bb("world"), 0, true);
        frameInboundWriter.writeInboundData(channel3.stream().id(), Http2TestUtil.bb("bar"), 0, true);
        Http2MultiplexCodecTest.verifyFramesMultiplexedToCorrectChannel(channel1, handler1, 1);
        Http2MultiplexCodecTest.verifyFramesMultiplexedToCorrectChannel(channel2, handler2, 2);
        Http2MultiplexCodecTest.verifyFramesMultiplexedToCorrectChannel(channel3, handler3, 1);
    }

    @Test
    public void inboundDataFrameShouldUpdateLocalFlowController() throws Http2Exception {
        Http2LocalFlowController flowController = Mockito.mock(Http2LocalFlowController.class);
        codec.connection().local().flowController(flowController);
        LastInboundHandler handler = new LastInboundHandler();
        final Http2StreamChannel channel = newInboundStream(3, false, handler);
        ByteBuf tenBytes = Http2TestUtil.bb("0123456789");
        frameInboundWriter.writeInboundData(channel.stream().id(), tenBytes, 0, true);
        // Verify we marked the bytes as consumed
        Mockito.verify(flowController).consumeBytes(ArgumentMatchers.argThat(new ArgumentMatcher<Http2Stream>() {
            @Override
            public boolean matches(Http2Stream http2Stream) {
                return (http2Stream.id()) == (channel.stream().id());
            }
        }), ArgumentMatchers.eq(10));
        // headers and data frame
        Http2MultiplexCodecTest.verifyFramesMultiplexedToCorrectChannel(channel, handler, 2);
    }

    @Test
    public void unhandledHttp2FramesShouldBePropagated() {
        Http2PingFrame pingFrame = new DefaultHttp2PingFrame(0);
        frameInboundWriter.writeInboundPing(false, 0);
        Assert.assertEquals(parentChannel.readInbound(), pingFrame);
        DefaultHttp2GoAwayFrame goAwayFrame = new DefaultHttp2GoAwayFrame(1, parentChannel.alloc().buffer().writeLong(8));
        frameInboundWriter.writeInboundGoAway(0, goAwayFrame.errorCode(), goAwayFrame.content().retainedDuplicate());
        Http2GoAwayFrame frame = parentChannel.readInbound();
        Http2TestUtil.assertEqualsAndRelease(frame, goAwayFrame);
    }

    @Test
    public void channelReadShouldRespectAutoRead() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel childChannel = newInboundStream(3, false, inboundHandler);
        Assert.assertTrue(childChannel.config().isAutoRead());
        Http2HeadersFrame headersFrame = inboundHandler.readInbound();
        Assert.assertNotNull(headersFrame);
        childChannel.config().setAutoRead(false);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("hello world"), 0, false);
        Http2DataFrame dataFrame0 = inboundHandler.readInbound();
        Assert.assertNotNull(dataFrame0);
        release(dataFrame0);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("foo"), 0, false);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("bar"), 0, false);
        Assert.assertNull(inboundHandler.readInbound());
        childChannel.config().setAutoRead(true);
        Http2MultiplexCodecTest.verifyFramesMultiplexedToCorrectChannel(childChannel, inboundHandler, 2);
    }

    @Test
    public void readInChannelReadWithoutAutoRead() {
        useReadWithoutAutoRead(false);
    }

    @Test
    public void readInChannelReadCompleteWithoutAutoRead() {
        useReadWithoutAutoRead(true);
    }

    /**
     * A child channel for a HTTP/2 stream in IDLE state (that is no headers sent or received),
     * should not emit a RST_STREAM frame on close, as this is a connection error of type protocol error.
     */
    @Test
    public void idleOutboundStreamShouldNotWriteResetFrameOnClose() {
        LastInboundHandler handler = new LastInboundHandler();
        Channel childChannel = newOutboundStream(handler);
        Assert.assertTrue(childChannel.isActive());
        childChannel.close();
        parentChannel.runPendingTasks();
        Assert.assertFalse(childChannel.isOpen());
        Assert.assertFalse(childChannel.isActive());
        Assert.assertNull(parentChannel.readOutbound());
    }

    @Test
    public void outboundStreamShouldWriteResetFrameOnClose_headersSent() {
        ChannelHandler handler = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                ctx.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()));
                ctx.fireChannelActive();
            }
        };
        Http2StreamChannel childChannel = newOutboundStream(handler);
        Assert.assertTrue(childChannel.isActive());
        childChannel.close();
        Mockito.verify(frameWriter).writeRstStream(eqMultiplexCodecCtx(), Http2MultiplexCodecTest.eqStreamId(childChannel), ArgumentMatchers.eq(CANCEL.code()), Http2TestUtil.anyChannelPromise());
    }

    @Test
    public void outboundStreamShouldNotWriteResetFrameOnClose_IfStreamDidntExist() {
        Mockito.when(frameWriter.writeHeaders(eqMultiplexCodecCtx(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ChannelPromise.class))).thenAnswer(new Answer<ChannelFuture>() {
            private boolean headersWritten;

            @Override
            public ChannelFuture answer(InvocationOnMock invocationOnMock) {
                // We want to fail to write the first headers frame. This is what happens if the connection
                // refuses to allocate a new stream due to having received a GOAWAY.
                if (!(headersWritten)) {
                    headersWritten = true;
                    return ((ChannelPromise) (invocationOnMock.getArgument(8))).setFailure(new Exception("boom"));
                }
                return setSuccess();
            }
        });
        Http2StreamChannel childChannel = newOutboundStream(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                ctx.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()));
                ctx.fireChannelActive();
            }
        });
        Assert.assertFalse(childChannel.isActive());
        childChannel.close();
        parentChannel.runPendingTasks();
        // The channel was never active so we should not generate a RST frame.
        Mockito.verify(frameWriter, Mockito.never()).writeRstStream(eqMultiplexCodecCtx(), Http2MultiplexCodecTest.eqStreamId(childChannel), ArgumentMatchers.anyLong(), Http2TestUtil.anyChannelPromise());
        Assert.assertTrue(parentChannel.outboundMessages().isEmpty());
    }

    @Test
    public void inboundRstStreamFireChannelInactive() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel channel = newInboundStream(3, false, inboundHandler);
        Assert.assertTrue(inboundHandler.isChannelActive());
        frameInboundWriter.writeInboundRstStream(channel.stream().id(), INTERNAL_ERROR.code());
        Assert.assertFalse(inboundHandler.isChannelActive());
        // A RST_STREAM frame should NOT be emitted, as we received a RST_STREAM.
        Mockito.verify(frameWriter, Mockito.never()).writeRstStream(eqMultiplexCodecCtx(), Http2MultiplexCodecTest.eqStreamId(channel), ArgumentMatchers.anyLong(), Http2TestUtil.anyChannelPromise());
    }

    @Test(expected = StreamException.class)
    public void streamExceptionTriggersChildChannelExceptionAndClose() throws Exception {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel channel = newInboundStream(3, false, inboundHandler);
        Assert.assertTrue(channel.isActive());
        StreamException cause = new StreamException(channel.stream().id(), PROTOCOL_ERROR, "baaam!");
        parentChannel.pipeline().fireExceptionCaught(cause);
        Assert.assertFalse(channel.isActive());
        inboundHandler.checkException();
    }

    @Test(expected = ClosedChannelException.class)
    public void streamClosedErrorTranslatedToClosedChannelExceptionOnWrites() throws Exception {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        final Http2StreamChannel childChannel = newOutboundStream(inboundHandler);
        Assert.assertTrue(childChannel.isActive());
        Http2Headers headers = new DefaultHttp2Headers();
        Mockito.when(frameWriter.writeHeaders(eqMultiplexCodecCtx(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(headers), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ChannelPromise.class))).thenAnswer(new Answer<ChannelFuture>() {
            @Override
            public ChannelFuture answer(InvocationOnMock invocationOnMock) {
                return setFailure(new StreamException(childChannel.stream().id(), STREAM_CLOSED, "Stream Closed"));
            }
        });
        ChannelFuture future = childChannel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()));
        parentChannel.flush();
        Assert.assertFalse(childChannel.isActive());
        Assert.assertFalse(childChannel.isOpen());
        inboundHandler.checkException();
        future.syncUninterruptibly();
    }

    @Test
    public void creatingWritingReadingAndClosingOutboundStreamShouldWork() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel childChannel = newOutboundStream(inboundHandler);
        Assert.assertTrue(childChannel.isActive());
        Assert.assertTrue(inboundHandler.isChannelActive());
        // Write to the child channel
        Http2Headers headers = new DefaultHttp2Headers().scheme("https").method("GET").path("/foo.txt");
        childChannel.writeAndFlush(new DefaultHttp2HeadersFrame(headers));
        // Read from the child channel
        frameInboundWriter.writeInboundHeaders(childChannel.stream().id(), headers, 0, false);
        Http2HeadersFrame headersFrame = inboundHandler.readInbound();
        Assert.assertNotNull(headersFrame);
        Assert.assertEquals(headers, headersFrame.headers());
        // Close the child channel.
        childChannel.close();
        parentChannel.runPendingTasks();
        // An active outbound stream should emit a RST_STREAM frame.
        Mockito.verify(frameWriter).writeRstStream(eqMultiplexCodecCtx(), Http2MultiplexCodecTest.eqStreamId(childChannel), ArgumentMatchers.anyLong(), Http2TestUtil.anyChannelPromise());
        Assert.assertFalse(childChannel.isOpen());
        Assert.assertFalse(childChannel.isActive());
        Assert.assertFalse(inboundHandler.isChannelActive());
    }

    // Test failing the promise of the first headers frame of an outbound stream. In practice this error case would most
    // likely happen due to the max concurrent streams limit being hit or the channel running out of stream identifiers.
    // 
    @Test(expected = Http2NoMoreStreamIdsException.class)
    public void failedOutboundStreamCreationThrowsAndClosesChannel() throws Exception {
        LastInboundHandler handler = new LastInboundHandler();
        Http2StreamChannel childChannel = newOutboundStream(handler);
        Assert.assertTrue(childChannel.isActive());
        Http2Headers headers = new DefaultHttp2Headers();
        Mockito.when(frameWriter.writeHeaders(eqMultiplexCodecCtx(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(headers), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ChannelPromise.class))).thenAnswer(new Answer<ChannelFuture>() {
            @Override
            public ChannelFuture answer(InvocationOnMock invocationOnMock) {
                return ((ChannelPromise) (invocationOnMock.getArgument(8))).setFailure(new Http2NoMoreStreamIdsException());
            }
        });
        ChannelFuture future = childChannel.writeAndFlush(new DefaultHttp2HeadersFrame(headers));
        parentChannel.flush();
        Assert.assertFalse(childChannel.isActive());
        Assert.assertFalse(childChannel.isOpen());
        handler.checkException();
        future.syncUninterruptibly();
    }

    @Test
    public void channelClosedWhenCloseListenerCompletes() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel childChannel = newInboundStream(3, false, inboundHandler);
        Assert.assertTrue(childChannel.isOpen());
        Assert.assertTrue(childChannel.isActive());
        final AtomicBoolean channelOpen = new AtomicBoolean(true);
        final AtomicBoolean channelActive = new AtomicBoolean(true);
        // Create a promise before actually doing the close, because otherwise we would be adding a listener to a future
        // that is already completed because we are using EmbeddedChannel which executes code in the JUnit thread.
        ChannelPromise p = childChannel.newPromise();
        p.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                channelOpen.set(future.channel().isOpen());
                channelActive.set(future.channel().isActive());
            }
        });
        childChannel.close(p).syncUninterruptibly();
        Assert.assertFalse(channelOpen.get());
        Assert.assertFalse(channelActive.get());
        Assert.assertFalse(childChannel.isActive());
    }

    @Test
    public void channelClosedWhenChannelClosePromiseCompletes() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel childChannel = newInboundStream(3, false, inboundHandler);
        Assert.assertTrue(childChannel.isOpen());
        Assert.assertTrue(childChannel.isActive());
        final AtomicBoolean channelOpen = new AtomicBoolean(true);
        final AtomicBoolean channelActive = new AtomicBoolean(true);
        childChannel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                channelOpen.set(future.channel().isOpen());
                channelActive.set(future.channel().isActive());
            }
        });
        childChannel.close().syncUninterruptibly();
        Assert.assertFalse(channelOpen.get());
        Assert.assertFalse(channelActive.get());
        Assert.assertFalse(childChannel.isActive());
    }

    @Test
    public void channelClosedWhenWriteFutureFails() {
        final Queue<ChannelPromise> writePromises = new ArrayDeque<ChannelPromise>();
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel childChannel = newInboundStream(3, false, inboundHandler);
        Assert.assertTrue(childChannel.isOpen());
        Assert.assertTrue(childChannel.isActive());
        final AtomicBoolean channelOpen = new AtomicBoolean(true);
        final AtomicBoolean channelActive = new AtomicBoolean(true);
        Http2Headers headers = new DefaultHttp2Headers();
        Mockito.when(frameWriter.writeHeaders(eqMultiplexCodecCtx(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(headers), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ChannelPromise.class))).thenAnswer(new Answer<ChannelFuture>() {
            @Override
            public ChannelFuture answer(InvocationOnMock invocationOnMock) {
                ChannelPromise promise = invocationOnMock.getArgument(8);
                writePromises.offer(promise);
                return promise;
            }
        });
        ChannelFuture f = childChannel.writeAndFlush(new DefaultHttp2HeadersFrame(headers));
        Assert.assertFalse(f.isDone());
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                channelOpen.set(future.channel().isOpen());
                channelActive.set(future.channel().isActive());
            }
        });
        ChannelPromise first = writePromises.poll();
        first.setFailure(new ClosedChannelException());
        f.awaitUninterruptibly();
        Assert.assertFalse(channelOpen.get());
        Assert.assertFalse(channelActive.get());
        Assert.assertFalse(childChannel.isActive());
    }

    @Test
    public void channelClosedTwiceMarksPromiseAsSuccessful() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel childChannel = newInboundStream(3, false, inboundHandler);
        Assert.assertTrue(childChannel.isOpen());
        Assert.assertTrue(childChannel.isActive());
        childChannel.close().syncUninterruptibly();
        childChannel.close().syncUninterruptibly();
        Assert.assertFalse(childChannel.isOpen());
        Assert.assertFalse(childChannel.isActive());
    }

    @Test
    public void settingChannelOptsAndAttrs() {
        AttributeKey<String> key = AttributeKey.newInstance("foo");
        Channel childChannel = newOutboundStream(new ChannelInboundHandlerAdapter());
        childChannel.config().setAutoRead(false).setWriteSpinCount(1000);
        childChannel.attr(key).set("bar");
        Assert.assertFalse(childChannel.config().isAutoRead());
        Assert.assertEquals(1000, childChannel.config().getWriteSpinCount());
        Assert.assertEquals("bar", childChannel.attr(key).get());
    }

    @Test
    public void outboundFlowControlWritability() {
        Http2StreamChannel childChannel = newOutboundStream(new ChannelInboundHandlerAdapter());
        Assert.assertTrue(childChannel.isActive());
        Assert.assertTrue(childChannel.isWritable());
        childChannel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()));
        parentChannel.flush();
        // Test for initial window size
        Assert.assertEquals(Http2MultiplexCodecTest.initialRemoteStreamWindow, childChannel.config().getWriteBufferHighWaterMark());
        Assert.assertTrue(childChannel.isWritable());
        childChannel.write(new DefaultHttp2DataFrame(Unpooled.buffer().writeZero(((16 * 1024) * 1024))));
        Assert.assertFalse(childChannel.isWritable());
    }

    @Test
    public void writabilityAndFlowControl() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel childChannel = newInboundStream(3, false, inboundHandler);
        Assert.assertEquals("", inboundHandler.writabilityStates());
        Assert.assertTrue(childChannel.isWritable());
        // HEADERS frames are not flow controlled, so they should not affect the flow control window.
        childChannel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()));
        codec.onHttp2StreamWritabilityChanged(codec.ctx, childChannel.stream(), true);
        Assert.assertTrue(childChannel.isWritable());
        Assert.assertEquals("", inboundHandler.writabilityStates());
        codec.onHttp2StreamWritabilityChanged(codec.ctx, childChannel.stream(), true);
        Assert.assertTrue(childChannel.isWritable());
        Assert.assertEquals("", inboundHandler.writabilityStates());
        codec.onHttp2StreamWritabilityChanged(codec.ctx, childChannel.stream(), false);
        Assert.assertFalse(childChannel.isWritable());
        Assert.assertEquals("false", inboundHandler.writabilityStates());
        codec.onHttp2StreamWritabilityChanged(codec.ctx, childChannel.stream(), false);
        Assert.assertFalse(childChannel.isWritable());
        Assert.assertEquals("false", inboundHandler.writabilityStates());
    }

    @Test
    public void channelClosedWhenInactiveFired() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel childChannel = newInboundStream(3, false, inboundHandler);
        final AtomicBoolean channelOpen = new AtomicBoolean(false);
        final AtomicBoolean channelActive = new AtomicBoolean(false);
        Assert.assertTrue(childChannel.isOpen());
        Assert.assertTrue(childChannel.isActive());
        childChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                channelOpen.set(ctx.channel().isOpen());
                channelActive.set(ctx.channel().isActive());
                super.channelInactive(ctx);
            }
        });
        childChannel.close().syncUninterruptibly();
        Assert.assertFalse(channelOpen.get());
        Assert.assertFalse(channelActive.get());
    }

    @Test
    public void channelInactiveHappensAfterExceptionCaughtEvents() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicInteger exceptionCaught = new AtomicInteger((-1));
        final AtomicInteger channelInactive = new AtomicInteger((-1));
        final AtomicInteger channelUnregistered = new AtomicInteger((-1));
        Http2StreamChannel childChannel = newOutboundStream(new ChannelInboundHandlerAdapter() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                ctx.close();
                throw new Exception("exception");
            }
        });
        childChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                channelInactive.set(count.getAndIncrement());
                super.channelInactive(ctx);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                exceptionCaught.set(count.getAndIncrement());
                super.exceptionCaught(ctx, cause);
            }

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                channelUnregistered.set(count.getAndIncrement());
                super.channelUnregistered(ctx);
            }
        });
        childChannel.pipeline().fireUserEventTriggered(new Object());
        parentChannel.runPendingTasks();
        // The events should have happened in this order because the inactive and deregistration events
        // get deferred as they do in the AbstractChannel.
        Assert.assertEquals(0, exceptionCaught.get());
        Assert.assertEquals(1, channelInactive.get());
        Assert.assertEquals(2, channelUnregistered.get());
    }

    @Test
    public void callUnsafeCloseMultipleTimes() {
        LastInboundHandler inboundHandler = new LastInboundHandler();
        Http2StreamChannel childChannel = newInboundStream(3, false, inboundHandler);
        childChannel.unsafe().close(childChannel.voidPromise());
        ChannelPromise promise = childChannel.newPromise();
        childChannel.unsafe().close(promise);
        promise.syncUninterruptibly();
        childChannel.closeFuture().syncUninterruptibly();
    }

    @Test
    public void endOfStreamDoesNotDiscardData() {
        AtomicInteger numReads = new AtomicInteger(1);
        final AtomicBoolean shouldDisableAutoRead = new AtomicBoolean();
        LastInboundHandler.Consumer<ChannelHandlerContext> ctxConsumer = new LastInboundHandler.Consumer<ChannelHandlerContext>() {
            @Override
            public void accept(ChannelHandlerContext obj) {
                if (shouldDisableAutoRead.get()) {
                    obj.channel().config().setAutoRead(false);
                }
            }
        };
        LastInboundHandler inboundHandler = new LastInboundHandler(ctxConsumer);
        Http2StreamChannel childChannel = newInboundStream(3, false, numReads, inboundHandler);
        childChannel.config().setAutoRead(false);
        Http2DataFrame dataFrame1 = new DefaultHttp2DataFrame(Http2TestUtil.bb("1")).stream(childChannel.stream());
        Http2DataFrame dataFrame2 = new DefaultHttp2DataFrame(Http2TestUtil.bb("2")).stream(childChannel.stream());
        Http2DataFrame dataFrame3 = new DefaultHttp2DataFrame(Http2TestUtil.bb("3")).stream(childChannel.stream());
        Http2DataFrame dataFrame4 = new DefaultHttp2DataFrame(Http2TestUtil.bb("4")).stream(childChannel.stream());
        Assert.assertEquals(new DefaultHttp2HeadersFrame(request).stream(childChannel.stream()), inboundHandler.readInbound());
        ChannelHandler readCompleteSupressHandler = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) {
                // We want to simulate the parent channel calling channelRead and delay calling channelReadComplete.
            }
        };
        parentChannel.pipeline().addFirst(readCompleteSupressHandler);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("1"), 0, false);
        Http2TestUtil.assertEqualsAndRelease(dataFrame1, inboundHandler.<Http2DataFrame>readInbound());
        // Deliver frames, and then a stream closed while read is inactive.
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("2"), 0, false);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("3"), 0, false);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("4"), 0, false);
        shouldDisableAutoRead.set(true);
        childChannel.config().setAutoRead(true);
        numReads.set(1);
        frameInboundWriter.writeInboundRstStream(childChannel.stream().id(), NO_ERROR.code());
        // Detecting EOS should flush all pending data regardless of read calls.
        Http2TestUtil.assertEqualsAndRelease(dataFrame2, inboundHandler.<Http2DataFrame>readInbound());
        Http2TestUtil.assertEqualsAndRelease(dataFrame3, inboundHandler.<Http2DataFrame>readInbound());
        Http2TestUtil.assertEqualsAndRelease(dataFrame4, inboundHandler.<Http2DataFrame>readInbound());
        Http2ResetFrame resetFrame = inboundHandler.readInbound();
        Assert.assertEquals(childChannel.stream(), resetFrame.stream());
        Assert.assertEquals(NO_ERROR.code(), resetFrame.errorCode());
        Assert.assertNull(inboundHandler.readInbound());
        // Now we want to call channelReadComplete and simulate the end of the read loop.
        parentChannel.pipeline().remove(readCompleteSupressHandler);
        parentChannel.flushInbound();
        childChannel.closeFuture().syncUninterruptibly();
    }

    @Test
    public void childQueueIsDrainedAndNewDataIsDispatchedInParentReadLoopAutoRead() {
        AtomicInteger numReads = new AtomicInteger(1);
        final AtomicInteger channelReadCompleteCount = new AtomicInteger(0);
        final AtomicBoolean shouldDisableAutoRead = new AtomicBoolean();
        LastInboundHandler.Consumer<ChannelHandlerContext> ctxConsumer = new LastInboundHandler.Consumer<ChannelHandlerContext>() {
            @Override
            public void accept(ChannelHandlerContext obj) {
                channelReadCompleteCount.incrementAndGet();
                if (shouldDisableAutoRead.get()) {
                    obj.channel().config().setAutoRead(false);
                }
            }
        };
        LastInboundHandler inboundHandler = new LastInboundHandler(ctxConsumer);
        Http2StreamChannel childChannel = newInboundStream(3, false, numReads, inboundHandler);
        childChannel.config().setAutoRead(false);
        Http2DataFrame dataFrame1 = new DefaultHttp2DataFrame(Http2TestUtil.bb("1")).stream(childChannel.stream());
        Http2DataFrame dataFrame2 = new DefaultHttp2DataFrame(Http2TestUtil.bb("2")).stream(childChannel.stream());
        Http2DataFrame dataFrame3 = new DefaultHttp2DataFrame(Http2TestUtil.bb("3")).stream(childChannel.stream());
        Http2DataFrame dataFrame4 = new DefaultHttp2DataFrame(Http2TestUtil.bb("4")).stream(childChannel.stream());
        Assert.assertEquals(new DefaultHttp2HeadersFrame(request).stream(childChannel.stream()), inboundHandler.readInbound());
        ChannelHandler readCompleteSupressHandler = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) {
                // We want to simulate the parent channel calling channelRead and delay calling channelReadComplete.
            }
        };
        parentChannel.pipeline().addFirst(readCompleteSupressHandler);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("1"), 0, false);
        Http2TestUtil.assertEqualsAndRelease(dataFrame1, inboundHandler.<Http2DataFrame>readInbound());
        // We want one item to be in the queue, and allow the numReads to be larger than 1. This will ensure that
        // when beginRead() is called the child channel is added to the readPending queue of the parent channel.
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("2"), 0, false);
        numReads.set(10);
        shouldDisableAutoRead.set(true);
        childChannel.config().setAutoRead(true);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("3"), 0, false);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("4"), 0, false);
        // Detecting EOS should flush all pending data regardless of read calls.
        Http2TestUtil.assertEqualsAndRelease(dataFrame2, inboundHandler.<Http2DataFrame>readInbound());
        Http2TestUtil.assertEqualsAndRelease(dataFrame3, inboundHandler.<Http2DataFrame>readInbound());
        Http2TestUtil.assertEqualsAndRelease(dataFrame4, inboundHandler.<Http2DataFrame>readInbound());
        Assert.assertNull(inboundHandler.readInbound());
        // Now we want to call channelReadComplete and simulate the end of the read loop.
        parentChannel.pipeline().remove(readCompleteSupressHandler);
        parentChannel.flushInbound();
        // 3 = 1 for initialization + 1 for read when auto read was off + 1 for when auto read was back on
        Assert.assertEquals(3, channelReadCompleteCount.get());
    }

    @Test
    public void childQueueIsDrainedAndNewDataIsDispatchedInParentReadLoopNoAutoRead() {
        final AtomicInteger numReads = new AtomicInteger(1);
        final AtomicInteger channelReadCompleteCount = new AtomicInteger(0);
        final AtomicBoolean shouldDisableAutoRead = new AtomicBoolean();
        LastInboundHandler.Consumer<ChannelHandlerContext> ctxConsumer = new LastInboundHandler.Consumer<ChannelHandlerContext>() {
            @Override
            public void accept(ChannelHandlerContext obj) {
                channelReadCompleteCount.incrementAndGet();
                if (shouldDisableAutoRead.get()) {
                    obj.channel().config().setAutoRead(false);
                }
            }
        };
        final LastInboundHandler inboundHandler = new LastInboundHandler(ctxConsumer);
        Http2StreamChannel childChannel = newInboundStream(3, false, numReads, inboundHandler);
        childChannel.config().setAutoRead(false);
        Http2DataFrame dataFrame1 = new DefaultHttp2DataFrame(Http2TestUtil.bb("1")).stream(childChannel.stream());
        Http2DataFrame dataFrame2 = new DefaultHttp2DataFrame(Http2TestUtil.bb("2")).stream(childChannel.stream());
        Http2DataFrame dataFrame3 = new DefaultHttp2DataFrame(Http2TestUtil.bb("3")).stream(childChannel.stream());
        Http2DataFrame dataFrame4 = new DefaultHttp2DataFrame(Http2TestUtil.bb("4")).stream(childChannel.stream());
        Assert.assertEquals(new DefaultHttp2HeadersFrame(request).stream(childChannel.stream()), inboundHandler.readInbound());
        ChannelHandler readCompleteSupressHandler = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                // We want to simulate the parent channel calling channelRead and delay calling channelReadComplete.
            }
        };
        parentChannel.pipeline().addFirst(readCompleteSupressHandler);
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("1"), 0, false);
        Http2TestUtil.assertEqualsAndRelease(dataFrame1, inboundHandler.<Http2Frame>readInbound());
        // We want one item to be in the queue, and allow the numReads to be larger than 1. This will ensure that
        // when beginRead() is called the child channel is added to the readPending queue of the parent channel.
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("2"), 0, false);
        numReads.set(2);
        childChannel.read();
        Http2TestUtil.assertEqualsAndRelease(dataFrame2, inboundHandler.<Http2Frame>readInbound());
        Assert.assertNull(inboundHandler.readInbound());
        // This is the second item that was read, this should be the last until we call read() again. This should also
        // notify of readComplete().
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("3"), 0, false);
        Http2TestUtil.assertEqualsAndRelease(dataFrame3, inboundHandler.<Http2Frame>readInbound());
        frameInboundWriter.writeInboundData(childChannel.stream().id(), Http2TestUtil.bb("4"), 0, false);
        Assert.assertNull(inboundHandler.readInbound());
        childChannel.read();
        Http2TestUtil.assertEqualsAndRelease(dataFrame4, inboundHandler.<Http2Frame>readInbound());
        Assert.assertNull(inboundHandler.readInbound());
        // Now we want to call channelReadComplete and simulate the end of the read loop.
        parentChannel.pipeline().remove(readCompleteSupressHandler);
        parentChannel.flushInbound();
        // 3 = 1 for initialization + 1 for first read of 2 items + 1 for second read of 2 items +
        // 1 for parent channel readComplete
        Assert.assertEquals(4, channelReadCompleteCount.get());
    }
}

