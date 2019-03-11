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


import Http2CodecUtil.HTTP_UPGRADE_STREAM_ID;
import Http2Error.NO_ERROR;
import Http2FrameStreamEvent.Type.State;
import HttpMethod.GET;
import HttpResponseStatus.OK;
import HttpScheme.HTTPS;
import State.CLOSED;
import State.HALF_CLOSED_REMOTE;
import State.IDLE;
import State.OPEN;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeEvent;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.Http2Exception.StreamException;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ReflectionUtil;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static Http2Error.INTERNAL_ERROR;
import static Http2Error.NO_ERROR;


/**
 * Unit tests for {@link Http2FrameCodec}.
 */
public class Http2FrameCodecTest {
    // For verifying outbound frames
    private Http2FrameWriter frameWriter;

    private Http2FrameCodec frameCodec;

    private EmbeddedChannel channel;

    // For injecting inbound frames
    private Http2FrameInboundWriter frameInboundWriter;

    private LastInboundHandler inboundHandler;

    private final Http2Headers request = new DefaultHttp2Headers().method(GET.asciiName()).scheme(HTTPS.name()).authority(new AsciiString("example.org")).path(new AsciiString("/foo"));

    private final Http2Headers response = new DefaultHttp2Headers().status(OK.codeAsText());

    @Test
    public void stateChanges() throws Exception {
        frameInboundWriter.writeInboundHeaders(1, request, 31, true);
        Http2Stream stream = frameCodec.connection().stream(1);
        Assert.assertNotNull(stream);
        Assert.assertEquals(HALF_CLOSED_REMOTE, stream.state());
        Http2FrameStreamEvent event = inboundHandler.readInboundMessageOrUserEvent();
        Assert.assertEquals(HALF_CLOSED_REMOTE, event.stream().state());
        Http2StreamFrame inboundFrame = inboundHandler.readInbound();
        Http2FrameStream stream2 = inboundFrame.stream();
        Assert.assertNotNull(stream2);
        Assert.assertEquals(1, stream2.id());
        Assert.assertEquals(inboundFrame, new DefaultHttp2HeadersFrame(request, true, 31).stream(stream2));
        Assert.assertNull(inboundHandler.readInbound());
        channel.writeOutbound(new DefaultHttp2HeadersFrame(response, true, 27).stream(stream2));
        Mockito.verify(frameWriter).writeHeaders(eqFrameCodecCtx(), ArgumentMatchers.eq(1), ArgumentMatchers.eq(response), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(27), ArgumentMatchers.eq(true), Http2TestUtil.anyChannelPromise());
        Mockito.verify(frameWriter, Mockito.never()).writeRstStream(eqFrameCodecCtx(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), Http2TestUtil.anyChannelPromise());
        Assert.assertEquals(CLOSED, stream.state());
        event = inboundHandler.readInboundMessageOrUserEvent();
        Assert.assertEquals(CLOSED, event.stream().state());
        Assert.assertTrue(channel.isActive());
    }

    @Test
    public void headerRequestHeaderResponse() throws Exception {
        frameInboundWriter.writeInboundHeaders(1, request, 31, true);
        Http2Stream stream = frameCodec.connection().stream(1);
        Assert.assertNotNull(stream);
        Assert.assertEquals(HALF_CLOSED_REMOTE, stream.state());
        Http2StreamFrame inboundFrame = inboundHandler.readInbound();
        Http2FrameStream stream2 = inboundFrame.stream();
        Assert.assertNotNull(stream2);
        Assert.assertEquals(1, stream2.id());
        Assert.assertEquals(inboundFrame, new DefaultHttp2HeadersFrame(request, true, 31).stream(stream2));
        Assert.assertNull(inboundHandler.readInbound());
        channel.writeOutbound(new DefaultHttp2HeadersFrame(response, true, 27).stream(stream2));
        Mockito.verify(frameWriter).writeHeaders(eqFrameCodecCtx(), ArgumentMatchers.eq(1), ArgumentMatchers.eq(response), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(27), ArgumentMatchers.eq(true), Http2TestUtil.anyChannelPromise());
        Mockito.verify(frameWriter, Mockito.never()).writeRstStream(eqFrameCodecCtx(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), Http2TestUtil.anyChannelPromise());
        Assert.assertEquals(CLOSED, stream.state());
        Assert.assertTrue(channel.isActive());
    }

    @Test
    public void flowControlShouldBeResilientToMissingStreams() throws Http2Exception {
        Http2Connection conn = new DefaultHttp2Connection(true);
        Http2ConnectionEncoder enc = new DefaultHttp2ConnectionEncoder(conn, new DefaultHttp2FrameWriter());
        Http2ConnectionDecoder dec = new DefaultHttp2ConnectionDecoder(conn, enc, new DefaultHttp2FrameReader());
        Http2FrameCodec codec = new Http2FrameCodec(enc, dec, new Http2Settings());
        EmbeddedChannel em = new EmbeddedChannel(codec);
        // We call #consumeBytes on a stream id which has not been seen yet to emulate the case
        // where a stream is deregistered which in reality can happen in response to a RST.
        Assert.assertFalse(codec.consumeBytes(1, 1));
        Assert.assertTrue(em.finishAndReleaseAll());
    }

    @Test
    public void entityRequestEntityResponse() throws Exception {
        frameInboundWriter.writeInboundHeaders(1, request, 0, false);
        Http2Stream stream = frameCodec.connection().stream(1);
        Assert.assertNotNull(stream);
        Assert.assertEquals(OPEN, stream.state());
        Http2HeadersFrame inboundHeaders = inboundHandler.readInbound();
        Http2FrameStream stream2 = inboundHeaders.stream();
        Assert.assertNotNull(stream2);
        Assert.assertEquals(1, stream2.id());
        Assert.assertEquals(new DefaultHttp2HeadersFrame(request, false).stream(stream2), inboundHeaders);
        Assert.assertNull(inboundHandler.readInbound());
        ByteBuf hello = Http2TestUtil.bb("hello");
        frameInboundWriter.writeInboundData(1, hello, 31, true);
        Http2DataFrame inboundData = inboundHandler.readInbound();
        Http2DataFrame expected = new DefaultHttp2DataFrame(Http2TestUtil.bb("hello"), true, 31).stream(stream2);
        Http2TestUtil.assertEqualsAndRelease(expected, inboundData);
        Assert.assertNull(inboundHandler.readInbound());
        channel.writeOutbound(new DefaultHttp2HeadersFrame(response, false).stream(stream2));
        Mockito.verify(frameWriter).writeHeaders(eqFrameCodecCtx(), ArgumentMatchers.eq(1), ArgumentMatchers.eq(response), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), Http2TestUtil.anyChannelPromise());
        channel.writeOutbound(new DefaultHttp2DataFrame(Http2TestUtil.bb("world"), true, 27).stream(stream2));
        ArgumentCaptor<ByteBuf> outboundData = ArgumentCaptor.forClass(ByteBuf.class);
        Mockito.verify(frameWriter).writeData(eqFrameCodecCtx(), ArgumentMatchers.eq(1), outboundData.capture(), ArgumentMatchers.eq(27), ArgumentMatchers.eq(true), Http2TestUtil.anyChannelPromise());
        ByteBuf bb = Http2TestUtil.bb("world");
        Assert.assertEquals(bb, outboundData.getValue());
        Assert.assertEquals(1, refCnt());
        bb.release();
        outboundData.getValue().release();
        Mockito.verify(frameWriter, Mockito.never()).writeRstStream(eqFrameCodecCtx(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), Http2TestUtil.anyChannelPromise());
        Assert.assertTrue(channel.isActive());
    }

    @Test
    public void sendRstStream() throws Exception {
        frameInboundWriter.writeInboundHeaders(3, request, 31, true);
        Http2Stream stream = frameCodec.connection().stream(3);
        Assert.assertNotNull(stream);
        Assert.assertEquals(HALF_CLOSED_REMOTE, stream.state());
        Http2HeadersFrame inboundHeaders = inboundHandler.readInbound();
        Assert.assertNotNull(inboundHeaders);
        Assert.assertTrue(inboundHeaders.isEndStream());
        Http2FrameStream stream2 = inboundHeaders.stream();
        Assert.assertNotNull(stream2);
        Assert.assertEquals(3, stream2.id());
        channel.writeOutbound(/* non-standard error */
        new DefaultHttp2ResetFrame(314).stream(stream2));
        Mockito.verify(frameWriter).writeRstStream(eqFrameCodecCtx(), ArgumentMatchers.eq(3), ArgumentMatchers.eq(314L), Http2TestUtil.anyChannelPromise());
        Assert.assertEquals(CLOSED, stream.state());
        Assert.assertTrue(channel.isActive());
    }

    @Test
    public void receiveRstStream() throws Exception {
        frameInboundWriter.writeInboundHeaders(3, request, 31, false);
        Http2Stream stream = frameCodec.connection().stream(3);
        Assert.assertNotNull(stream);
        Assert.assertEquals(OPEN, stream.state());
        Http2HeadersFrame expectedHeaders = new DefaultHttp2HeadersFrame(request, false, 31);
        Http2HeadersFrame actualHeaders = inboundHandler.readInbound();
        Assert.assertEquals(expectedHeaders.stream(actualHeaders.stream()), actualHeaders);
        frameInboundWriter.writeInboundRstStream(3, NO_ERROR.code());
        Http2ResetFrame expectedRst = new DefaultHttp2ResetFrame(NO_ERROR).stream(actualHeaders.stream());
        Http2ResetFrame actualRst = inboundHandler.readInbound();
        Assert.assertEquals(expectedRst, actualRst);
        Assert.assertNull(inboundHandler.readInbound());
    }

    @Test
    public void sendGoAway() throws Exception {
        frameInboundWriter.writeInboundHeaders(3, request, 31, false);
        Http2Stream stream = frameCodec.connection().stream(3);
        Assert.assertNotNull(stream);
        Assert.assertEquals(OPEN, stream.state());
        ByteBuf debugData = Http2TestUtil.bb("debug");
        ByteBuf expected = debugData.copy();
        Http2GoAwayFrame goAwayFrame = new DefaultHttp2GoAwayFrame(NO_ERROR.code(), debugData);
        goAwayFrame.setExtraStreamIds(2);
        channel.writeOutbound(goAwayFrame);
        Mockito.verify(frameWriter).writeGoAway(eqFrameCodecCtx(), ArgumentMatchers.eq(7), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.eq(expected), Http2TestUtil.anyChannelPromise());
        Assert.assertEquals(1, debugData.refCnt());
        Assert.assertEquals(OPEN, stream.state());
        Assert.assertTrue(channel.isActive());
        expected.release();
        debugData.release();
    }

    @Test
    public void receiveGoaway() throws Exception {
        ByteBuf debugData = Http2TestUtil.bb("foo");
        frameInboundWriter.writeInboundGoAway(2, NO_ERROR.code(), debugData);
        Http2GoAwayFrame expectedFrame = new DefaultHttp2GoAwayFrame(2, NO_ERROR.code(), Http2TestUtil.bb("foo"));
        Http2GoAwayFrame actualFrame = inboundHandler.readInbound();
        Http2TestUtil.assertEqualsAndRelease(expectedFrame, actualFrame);
        Assert.assertNull(inboundHandler.readInbound());
    }

    @Test
    public void unknownFrameTypeShouldThrowAndBeReleased() throws Exception {
        class UnknownHttp2Frame extends AbstractReferenceCounted implements Http2Frame {
            @Override
            public String name() {
                return "UNKNOWN";
            }

            @Override
            protected void deallocate() {
            }

            @Override
            public ReferenceCounted touch(Object hint) {
                return this;
            }
        }
        UnknownHttp2Frame frame = new UnknownHttp2Frame();
        Assert.assertEquals(1, refCnt());
        ChannelFuture f = channel.write(frame);
        f.await();
        Assert.assertTrue(f.isDone());
        Assert.assertFalse(f.isSuccess());
        Assert.assertThat(f.cause(), Matchers.instanceOf(UnsupportedMessageTypeException.class));
        Assert.assertEquals(0, refCnt());
    }

    @Test
    public void goAwayLastStreamIdOverflowed() throws Exception {
        frameInboundWriter.writeInboundHeaders(5, request, 31, false);
        Http2Stream stream = frameCodec.connection().stream(5);
        Assert.assertNotNull(stream);
        Assert.assertEquals(OPEN, stream.state());
        ByteBuf debugData = Http2TestUtil.bb("debug");
        Http2GoAwayFrame goAwayFrame = new DefaultHttp2GoAwayFrame(NO_ERROR.code(), debugData.slice());
        goAwayFrame.setExtraStreamIds(Integer.MAX_VALUE);
        channel.writeOutbound(goAwayFrame);
        // When the last stream id computation overflows, the last stream id should just be set to 2^31 - 1.
        Mockito.verify(frameWriter).writeGoAway(eqFrameCodecCtx(), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.eq(debugData), Http2TestUtil.anyChannelPromise());
        Assert.assertEquals(1, debugData.refCnt());
        Assert.assertEquals(OPEN, stream.state());
        Assert.assertTrue(channel.isActive());
    }

    @Test
    public void streamErrorShouldFireExceptionForInbound() throws Exception {
        frameInboundWriter.writeInboundHeaders(3, request, 31, false);
        Http2Stream stream = frameCodec.connection().stream(3);
        Assert.assertNotNull(stream);
        StreamException streamEx = new StreamException(3, INTERNAL_ERROR, "foo");
        channel.pipeline().fireExceptionCaught(streamEx);
        Http2FrameStreamEvent event = inboundHandler.readInboundMessageOrUserEvent();
        Assert.assertEquals(State, event.type());
        Assert.assertEquals(OPEN, event.stream().state());
        Http2HeadersFrame headersFrame = inboundHandler.readInboundMessageOrUserEvent();
        Assert.assertNotNull(headersFrame);
        try {
            inboundHandler.checkException();
            Assert.fail("stream exception expected");
        } catch (Http2FrameStreamException e) {
            Assert.assertEquals(streamEx, e.getCause());
        }
        Assert.assertNull(inboundHandler.readInboundMessageOrUserEvent());
    }

    @Test
    public void streamErrorShouldNotFireExceptionForOutbound() throws Exception {
        frameInboundWriter.writeInboundHeaders(3, request, 31, false);
        Http2Stream stream = frameCodec.connection().stream(3);
        Assert.assertNotNull(stream);
        StreamException streamEx = new StreamException(3, INTERNAL_ERROR, "foo");
        frameCodec.onError(frameCodec.ctx, true, streamEx);
        Http2FrameStreamEvent event = inboundHandler.readInboundMessageOrUserEvent();
        Assert.assertEquals(State, event.type());
        Assert.assertEquals(OPEN, event.stream().state());
        Http2HeadersFrame headersFrame = inboundHandler.readInboundMessageOrUserEvent();
        Assert.assertNotNull(headersFrame);
        // No exception expected
        inboundHandler.checkException();
        Assert.assertNull(inboundHandler.readInboundMessageOrUserEvent());
    }

    @Test
    public void windowUpdateFrameDecrementsConsumedBytes() throws Exception {
        frameInboundWriter.writeInboundHeaders(3, request, 31, false);
        Http2Connection connection = frameCodec.connection();
        Http2Stream stream = connection.stream(3);
        Assert.assertNotNull(stream);
        ByteBuf data = Unpooled.buffer(100).writeZero(100);
        frameInboundWriter.writeInboundData(3, data, 0, false);
        Http2HeadersFrame inboundHeaders = inboundHandler.readInbound();
        Assert.assertNotNull(inboundHeaders);
        Assert.assertNotNull(inboundHeaders.stream());
        Http2FrameStream stream2 = inboundHeaders.stream();
        int before = connection.local().flowController().unconsumedBytes(stream);
        ChannelFuture f = channel.write(new DefaultHttp2WindowUpdateFrame(100).stream(stream2));
        int after = connection.local().flowController().unconsumedBytes(stream);
        Assert.assertEquals(100, (before - after));
        Assert.assertTrue(f.isSuccess());
    }

    @Test
    public void windowUpdateMayFail() throws Exception {
        frameInboundWriter.writeInboundHeaders(3, request, 31, false);
        Http2Connection connection = frameCodec.connection();
        Http2Stream stream = connection.stream(3);
        Assert.assertNotNull(stream);
        Http2HeadersFrame inboundHeaders = inboundHandler.readInbound();
        Assert.assertNotNull(inboundHeaders);
        Http2FrameStream stream2 = inboundHeaders.stream();
        // Fails, cause trying to return too many bytes to the flow controller
        ChannelFuture f = channel.write(new DefaultHttp2WindowUpdateFrame(100).stream(stream2));
        Assert.assertTrue(f.isDone());
        Assert.assertFalse(f.isSuccess());
        Assert.assertThat(f.cause(), Matchers.instanceOf(Http2Exception.class));
    }

    @Test
    public void inboundWindowUpdateShouldBeForwarded() throws Exception {
        frameInboundWriter.writeInboundHeaders(3, request, 31, false);
        frameInboundWriter.writeInboundWindowUpdate(3, 100);
        // Connection-level window update
        frameInboundWriter.writeInboundWindowUpdate(0, 100);
        Http2HeadersFrame headersFrame = inboundHandler.readInbound();
        Assert.assertNotNull(headersFrame);
        Http2WindowUpdateFrame windowUpdateFrame = inboundHandler.readInbound();
        Assert.assertNotNull(windowUpdateFrame);
        Assert.assertEquals(3, windowUpdateFrame.stream().id());
        Assert.assertEquals(100, windowUpdateFrame.windowSizeIncrement());
        // Window update for the connection should not be forwarded.
        Assert.assertNull(inboundHandler.readInbound());
    }

    @Test
    public void streamZeroWindowUpdateIncrementsConnectionWindow() throws Http2Exception {
        Http2Connection connection = frameCodec.connection();
        Http2LocalFlowController localFlow = connection.local().flowController();
        int initialWindowSizeBefore = localFlow.initialWindowSize();
        Http2Stream connectionStream = connection.connectionStream();
        int connectionWindowSizeBefore = localFlow.windowSize(connectionStream);
        // We only replenish the flow control window after the amount consumed drops below the following threshold.
        // We make the threshold very "high" so that window updates will be sent when the delta is relatively small.
        ((DefaultHttp2LocalFlowController) (localFlow)).windowUpdateRatio(connectionStream, 0.999F);
        int windowUpdate = 1024;
        channel.write(new DefaultHttp2WindowUpdateFrame(windowUpdate));
        // The initial window size is only changed by Http2Settings, so it shouldn't change.
        Assert.assertEquals(initialWindowSizeBefore, localFlow.initialWindowSize());
        // The connection window should be increased by the delta amount.
        Assert.assertEquals((connectionWindowSizeBefore + windowUpdate), localFlow.windowSize(connectionStream));
    }

    @Test
    public void windowUpdateDoesNotOverflowConnectionWindow() {
        Http2Connection connection = frameCodec.connection();
        Http2LocalFlowController localFlow = connection.local().flowController();
        int initialWindowSizeBefore = localFlow.initialWindowSize();
        channel.write(new DefaultHttp2WindowUpdateFrame(Integer.MAX_VALUE));
        // The initial window size is only changed by Http2Settings, so it shouldn't change.
        Assert.assertEquals(initialWindowSizeBefore, localFlow.initialWindowSize());
        // The connection window should be increased by the delta amount.
        Assert.assertEquals(Integer.MAX_VALUE, localFlow.windowSize(connection.connectionStream()));
    }

    @Test
    public void writeUnknownFrame() {
        final Http2FrameStream stream = frameCodec.newStream();
        ByteBuf buffer = Unpooled.buffer().writeByte(1);
        DefaultHttp2UnknownFrame unknownFrame = new DefaultHttp2UnknownFrame(((byte) (20)), new Http2Flags().ack(true), buffer);
        unknownFrame.stream(stream);
        channel.write(unknownFrame);
        Mockito.verify(frameWriter).writeFrame(eqFrameCodecCtx(), ArgumentMatchers.eq(unknownFrame.frameType()), ArgumentMatchers.eq(unknownFrame.stream().id()), ArgumentMatchers.eq(unknownFrame.flags()), ArgumentMatchers.eq(buffer), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void sendSettingsFrame() {
        Http2Settings settings = new Http2Settings();
        channel.write(new DefaultHttp2SettingsFrame(settings));
        Mockito.verify(frameWriter).writeSettings(eqFrameCodecCtx(), ArgumentMatchers.same(settings), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test(timeout = 5000)
    public void newOutboundStream() {
        final Http2FrameStream stream = frameCodec.newStream();
        Assert.assertNotNull(stream);
        Assert.assertFalse(Http2CodecUtil.isStreamIdValid(stream.id()));
        final Promise<Void> listenerExecuted = new io.netty.util.concurrent.DefaultPromise<Void>(GlobalEventExecutor.INSTANCE);
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers(), false).stream(stream)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Assert.assertTrue(future.isSuccess());
                Assert.assertTrue(Http2CodecUtil.isStreamIdValid(stream.id()));
                listenerExecuted.setSuccess(null);
            }
        });
        ByteBuf data = Unpooled.buffer().writeZero(100);
        ChannelFuture f = channel.writeAndFlush(new DefaultHttp2DataFrame(data).stream(stream));
        Assert.assertTrue(f.isSuccess());
        listenerExecuted.syncUninterruptibly();
        Assert.assertTrue(listenerExecuted.isSuccess());
    }

    @Test
    public void newOutboundStreamsShouldBeBuffered() throws Exception {
        setUp(Http2FrameCodecBuilder.forServer().encoderEnforceMaxConcurrentStreams(true), new Http2Settings().maxConcurrentStreams(1));
        Http2FrameStream stream1 = frameCodec.newStream();
        Http2FrameStream stream2 = frameCodec.newStream();
        ChannelPromise promise1 = channel.newPromise();
        ChannelPromise promise2 = channel.newPromise();
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()).stream(stream1), promise1);
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()).stream(stream2), promise2);
        Assert.assertTrue(Http2CodecUtil.isStreamIdValid(stream1.id()));
        channel.runPendingTasks();
        Assert.assertTrue(Http2CodecUtil.isStreamIdValid(stream2.id()));
        Assert.assertTrue(promise1.syncUninterruptibly().isSuccess());
        Assert.assertFalse(promise2.isDone());
        // Increase concurrent streams limit to 2
        frameInboundWriter.writeInboundSettings(new Http2Settings().maxConcurrentStreams(2));
        channel.flush();
        Assert.assertTrue(promise2.syncUninterruptibly().isSuccess());
    }

    @Test
    public void multipleNewOutboundStreamsShouldBeBuffered() throws Exception {
        // We use a limit of 1 and then increase it step by step.
        setUp(Http2FrameCodecBuilder.forServer().encoderEnforceMaxConcurrentStreams(true), new Http2Settings().maxConcurrentStreams(1));
        Http2FrameStream stream1 = frameCodec.newStream();
        Http2FrameStream stream2 = frameCodec.newStream();
        Http2FrameStream stream3 = frameCodec.newStream();
        ChannelPromise promise1 = channel.newPromise();
        ChannelPromise promise2 = channel.newPromise();
        ChannelPromise promise3 = channel.newPromise();
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()).stream(stream1), promise1);
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()).stream(stream2), promise2);
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()).stream(stream3), promise3);
        Assert.assertTrue(Http2CodecUtil.isStreamIdValid(stream1.id()));
        channel.runPendingTasks();
        Assert.assertTrue(Http2CodecUtil.isStreamIdValid(stream2.id()));
        Assert.assertTrue(promise1.syncUninterruptibly().isSuccess());
        Assert.assertFalse(promise2.isDone());
        Assert.assertFalse(promise3.isDone());
        // Increase concurrent streams limit to 2
        frameInboundWriter.writeInboundSettings(new Http2Settings().maxConcurrentStreams(2));
        channel.flush();
        // As we increased the limit to 2 we should have also succeed the second frame.
        Assert.assertTrue(promise2.syncUninterruptibly().isSuccess());
        Assert.assertFalse(promise3.isDone());
        frameInboundWriter.writeInboundSettings(new Http2Settings().maxConcurrentStreams(3));
        channel.flush();
        // With the max streams of 3 all streams should be succeed now.
        Assert.assertTrue(promise3.syncUninterruptibly().isSuccess());
        Assert.assertFalse(channel.finishAndReleaseAll());
    }

    @Test
    public void streamIdentifiersExhausted() throws Http2Exception {
        int maxServerStreamId = (Integer.MAX_VALUE) - 1;
        Assert.assertNotNull(frameCodec.connection().local().createStream(maxServerStreamId, false));
        Http2FrameStream stream = frameCodec.newStream();
        Assert.assertNotNull(stream);
        ChannelPromise writePromise = channel.newPromise();
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()).stream(stream), writePromise);
        Assert.assertThat(writePromise.cause(), Matchers.instanceOf(Http2NoMoreStreamIdsException.class));
    }

    @Test
    public void receivePing() throws Http2Exception {
        frameInboundWriter.writeInboundPing(false, 12345L);
        Http2PingFrame pingFrame = inboundHandler.readInbound();
        Assert.assertNotNull(pingFrame);
        Assert.assertEquals(12345, pingFrame.content());
        Assert.assertFalse(pingFrame.ack());
    }

    @Test
    public void sendPing() {
        channel.writeAndFlush(new DefaultHttp2PingFrame(12345));
        Mockito.verify(frameWriter).writePing(eqFrameCodecCtx(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(12345L), Http2TestUtil.anyChannelPromise());
    }

    @Test
    public void receiveSettings() throws Http2Exception {
        Http2Settings settings = new Http2Settings().maxConcurrentStreams(1);
        frameInboundWriter.writeInboundSettings(settings);
        Http2SettingsFrame settingsFrame = inboundHandler.readInbound();
        Assert.assertNotNull(settingsFrame);
        Assert.assertEquals(settings, settingsFrame.settings());
    }

    @Test
    public void sendSettings() {
        Http2Settings settings = new Http2Settings().maxConcurrentStreams(1);
        channel.writeAndFlush(new DefaultHttp2SettingsFrame(settings));
        Mockito.verify(frameWriter).writeSettings(eqFrameCodecCtx(), ArgumentMatchers.eq(settings), Http2TestUtil.anyChannelPromise());
    }

    @Test
    public void iterateActiveStreams() throws Exception {
        setUp(Http2FrameCodecBuilder.forServer().encoderEnforceMaxConcurrentStreams(true), new Http2Settings().maxConcurrentStreams(1));
        frameInboundWriter.writeInboundHeaders(3, request, 0, false);
        Http2HeadersFrame headersFrame = inboundHandler.readInbound();
        Assert.assertNotNull(headersFrame);
        Http2FrameStream activeInbond = headersFrame.stream();
        Http2FrameStream activeOutbound = frameCodec.newStream();
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()).stream(activeOutbound));
        Http2FrameStream bufferedOutbound = frameCodec.newStream();
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()).stream(bufferedOutbound));
        @SuppressWarnings("unused")
        Http2FrameStream idleStream = frameCodec.newStream();
        final Set<Http2FrameStream> activeStreams = new HashSet<Http2FrameStream>();
        frameCodec.forEachActiveStream(new Http2FrameStreamVisitor() {
            @Override
            public boolean visit(Http2FrameStream stream) {
                activeStreams.add(stream);
                return true;
            }
        });
        Assert.assertEquals(2, activeStreams.size());
        Set<Http2FrameStream> expectedStreams = new HashSet<Http2FrameStream>();
        expectedStreams.add(activeInbond);
        expectedStreams.add(activeOutbound);
        Assert.assertEquals(expectedStreams, activeStreams);
    }

    @Test
    public void streamShouldBeOpenInListener() {
        final Http2FrameStream stream2 = frameCodec.newStream();
        Assert.assertEquals(IDLE, stream2.state());
        final AtomicBoolean listenerExecuted = new AtomicBoolean();
        channel.writeAndFlush(new DefaultHttp2HeadersFrame(new DefaultHttp2Headers()).stream(stream2)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Assert.assertTrue(future.isSuccess());
                Assert.assertEquals(OPEN, stream2.state());
                listenerExecuted.set(true);
            }
        });
        Assert.assertTrue(listenerExecuted.get());
    }

    @Test
    public void upgradeEventNoRefCntError() throws Exception {
        frameInboundWriter.writeInboundHeaders(HTTP_UPGRADE_STREAM_ID, request, 31, false);
        // Using reflect as the constructor is package-private and the class is final.
        Constructor<UpgradeEvent> constructor = UpgradeEvent.class.getDeclaredConstructor(CharSequence.class, FullHttpRequest.class);
        // Check if we could make it accessible which may fail on java9.
        Assume.assumeTrue(((ReflectionUtil.trySetAccessible(constructor, true)) == null));
        HttpServerUpgradeHandler.UpgradeEvent upgradeEvent = constructor.newInstance("HTTP/2", new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"));
        channel.pipeline().fireUserEventTriggered(upgradeEvent);
        Assert.assertEquals(1, upgradeEvent.refCnt());
    }

    @Test
    public void upgradeWithoutFlowControlling() throws Exception {
        channel.pipeline().addAfter(frameCodec.ctx.name(), null, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof Http2DataFrame) {
                    // Simulate consuming the frame and update the flow-controller.
                    Http2DataFrame data = ((Http2DataFrame) (msg));
                    ctx.writeAndFlush(new DefaultHttp2WindowUpdateFrame(data.initialFlowControlledBytes()).stream(data.stream())).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            Throwable cause = future.cause();
                            if (cause != null) {
                                ctx.fireExceptionCaught(cause);
                            }
                        }
                    });
                }
                ReferenceCountUtil.release(msg);
            }
        });
        frameInboundWriter.writeInboundHeaders(HTTP_UPGRADE_STREAM_ID, request, 31, false);
        // Using reflect as the constructor is package-private and the class is final.
        Constructor<UpgradeEvent> constructor = UpgradeEvent.class.getDeclaredConstructor(CharSequence.class, FullHttpRequest.class);
        // Check if we could make it accessible which may fail on java9.
        Assume.assumeTrue(((ReflectionUtil.trySetAccessible(constructor, true)) == null));
        String longString = new String(new char[70000]).replace("\u0000", "*");
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/", Http2TestUtil.bb(longString));
        HttpServerUpgradeHandler.UpgradeEvent upgradeEvent = constructor.newInstance("HTTP/2", request);
        channel.pipeline().fireUserEventTriggered(upgradeEvent);
    }
}

