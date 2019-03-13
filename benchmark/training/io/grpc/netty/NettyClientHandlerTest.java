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


import Http2Error.CANCEL;
import Http2Error.REFUSED_STREAM;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import NettyClientStream.TransportState;
import NettyClientTransport.Listener;
import StatsTraceContext.NOOP;
import Status.CANCELLED;
import Status.Code.UNAVAILABLE;
import Status.DEADLINE_EXCEEDED;
import Status.INTERNAL;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.internal.ClientStreamListener;
import io.grpc.internal.ClientStreamListener.RpcProgress;
import io.grpc.internal.ClientTransport;
import io.grpc.internal.KeepAliveManager;
import io.grpc.internal.TransportTracer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Stream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link NettyClientHandler}.
 */
@RunWith(JUnit4.class)
public class NettyClientHandlerTest extends NettyHandlerTestBase<NettyClientHandler> {
    private TransportState streamTransportState;

    private Http2Headers grpcHeaders;

    private long nanoTime;// backs a ticker, for testing ping round-trip time measurement


    private int maxHeaderListSize = Integer.MAX_VALUE;

    private int streamId = 3;

    private ClientTransportLifecycleManager lifecycleManager;

    private KeepAliveManager mockKeepAliveManager = null;

    private List<String> setKeepaliveManagerFor = ImmutableList.of("cancelShouldSucceed", "sendFrameShouldSucceed", "channelShutdownShouldCancelBufferedStreams", "createIncrementsIdsForActualAndBufferdStreams", "dataPingAckIsRecognized");

    private Runnable tooManyPingsRunnable = new Runnable() {
        @Override
        public void run() {
        }
    };

    @Rule
    public TestName testNameRule = new TestName();

    @Mock
    private Listener listener;

    @Mock
    private ClientStreamListener streamListener;

    private final Queue<InputStream> streamListenerMessageQueue = new LinkedList<>();

    @Test
    public void cancelBufferedStreamShouldChangeClientStreamStatus() throws Exception {
        // Force the stream to be buffered.
        receiveMaxConcurrentStreams(0);
        // Create a new stream with id 3.
        ChannelFuture createFuture = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        Assert.assertEquals(3, streamTransportState.id());
        // Cancel the stream.
        cancelStream(CANCELLED);
        Assert.assertTrue(createFuture.isSuccess());
        Mockito.verify(streamListener).closed(ArgumentMatchers.eq(CANCELLED), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void createStreamShouldSucceed() throws Exception {
        createStream();
        verifyWrite().writeHeaders(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(3), ArgumentMatchers.eq(grpcHeaders), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void cancelShouldSucceed() throws Exception {
        createStream();
        cancelStream(CANCELLED);
        verifyWrite().writeRstStream(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL.code()), ArgumentMatchers.any(ChannelPromise.class));
        Mockito.verify(mockKeepAliveManager, Mockito.times(1)).onTransportActive();// onStreamActive

        Mockito.verify(mockKeepAliveManager, Mockito.times(1)).onTransportIdle();// onStreamClosed

        Mockito.verifyNoMoreInteractions(mockKeepAliveManager);
    }

    @Test
    public void cancelDeadlineExceededShouldSucceed() throws Exception {
        createStream();
        cancelStream(DEADLINE_EXCEEDED);
        verifyWrite().writeRstStream(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL.code()), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void cancelWhileBufferedShouldSucceed() throws Exception {
        // Force the stream to be buffered.
        receiveMaxConcurrentStreams(0);
        ChannelFuture createFuture = createStream();
        Assert.assertFalse(createFuture.isDone());
        ChannelFuture cancelFuture = cancelStream(CANCELLED);
        Assert.assertTrue(cancelFuture.isSuccess());
        Assert.assertTrue(createFuture.isDone());
        Assert.assertTrue(createFuture.isSuccess());
    }

    /**
     * Although nobody is listening to an exception should it occur during cancel(), we don't want an
     * exception to be thrown because it would negatively impact performance, and we don't want our
     * users working around around such performance issues.
     */
    @Test
    public void cancelTwiceShouldSucceed() throws Exception {
        createStream();
        cancelStream(CANCELLED);
        verifyWrite().writeRstStream(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL.code()), ArgumentMatchers.any(ChannelPromise.class));
        ChannelFuture future = cancelStream(CANCELLED);
        Assert.assertTrue(future.isSuccess());
    }

    @Test
    public void cancelTwiceDifferentReasons() throws Exception {
        createStream();
        cancelStream(DEADLINE_EXCEEDED);
        verifyWrite().writeRstStream(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL.code()), ArgumentMatchers.any(ChannelPromise.class));
        ChannelFuture future = cancelStream(CANCELLED);
        Assert.assertTrue(future.isSuccess());
    }

    @Test
    public void sendFrameShouldSucceed() throws Exception {
        createStream();
        // Send a frame and verify that it was written.
        ChannelFuture future = enqueue(new SendGrpcFrameCommand(streamTransportState, content(), true));
        Assert.assertTrue(future.isSuccess());
        verifyWrite().writeData(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(3), ArgumentMatchers.eq(content()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true), ArgumentMatchers.any(ChannelPromise.class));
        Mockito.verify(mockKeepAliveManager, Mockito.times(1)).onTransportActive();// onStreamActive

        Mockito.verifyNoMoreInteractions(mockKeepAliveManager);
    }

    @Test
    public void sendForUnknownStreamShouldFail() throws Exception {
        ChannelFuture future = enqueue(new SendGrpcFrameCommand(streamTransportState, content(), true));
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isSuccess());
    }

    @Test
    public void inboundShouldForwardToStream() throws Exception {
        createStream();
        // Read a headers frame first.
        Http2Headers headers = new DefaultHttp2Headers().status(Utils.STATUS_OK).set(Utils.CONTENT_TYPE_HEADER, Utils.CONTENT_TYPE_GRPC).set(as("magic"), as("value"));
        ByteBuf headersFrame = headersFrame(3, headers);
        channelRead(headersFrame);
        ArgumentCaptor<Metadata> captor = ArgumentCaptor.forClass(Metadata.class);
        Mockito.verify(streamListener).headersRead(captor.capture());
        Assert.assertEquals("value", captor.getValue().get(Key.of("magic", ASCII_STRING_MARSHALLER)));
        streamTransportState.requestMessagesFromDeframer(1);
        // Create a data frame and then trigger the handler to read it.
        ByteBuf frame = grpcDataFrame(3, false, contentAsArray());
        channelRead(frame);
        InputStream message = streamListenerMessageQueue.poll();
        Assert.assertArrayEquals(ByteBufUtil.getBytes(content()), ByteStreams.toByteArray(message));
        message.close();
        Assert.assertNull("no additional message expected", streamListenerMessageQueue.poll());
    }

    @Test
    public void receivedGoAwayShouldCancelBufferedStream() throws Exception {
        // Force the stream to be buffered.
        receiveMaxConcurrentStreams(0);
        ChannelFuture future = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        channelRead(goAwayFrame(0));
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isSuccess());
        Status status = Status.fromThrowable(future.cause());
        Assert.assertEquals(UNAVAILABLE, status.getCode());
        Assert.assertEquals("HTTP/2 error code: NO_ERROR\nReceived Goaway", status.getDescription());
    }

    @Test
    public void receivedGoAwayShouldRefuseLaterStreamId() throws Exception {
        ChannelFuture future = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        channelRead(goAwayFrame(((streamId) - 1)));
        Mockito.verify(streamListener).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.eq(REFUSED), ArgumentMatchers.any(Metadata.class));
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void receivedGoAwayShouldNotAffectEarlyStreamId() throws Exception {
        ChannelFuture future = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        channelRead(goAwayFrame(streamId));
        Mockito.verify(streamListener, Mockito.never()).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        Mockito.verify(streamListener, Mockito.never()).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(RpcProgress.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void receivedResetWithRefuseCode() throws Exception {
        ChannelFuture future = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        channelRead(rstStreamFrame(streamId, ((int) (REFUSED_STREAM.code()))));
        Mockito.verify(streamListener).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.eq(REFUSED), ArgumentMatchers.any(Metadata.class));
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void receivedResetWithCanceCode() throws Exception {
        ChannelFuture future = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        channelRead(rstStreamFrame(streamId, ((int) (CANCEL.code()))));
        Mockito.verify(streamListener).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.eq(PROCESSED), ArgumentMatchers.any(Metadata.class));
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void receivedGoAwayShouldFailUnknownStreams() throws Exception {
        enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        // Read a GOAWAY that indicates our stream was never processed by the server.
        channelRead(/* Cancel */
        goAwayFrame(0, 8, Unpooled.copiedBuffer("this is a test", Charsets.UTF_8)));
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(streamListener).closed(captor.capture(), ArgumentMatchers.same(REFUSED), ArgumentMatchers.notNull(Metadata.class));
        Assert.assertEquals(CANCELLED.getCode(), captor.getValue().getCode());
        Assert.assertEquals("HTTP/2 error code: CANCEL\nReceived Goaway\nthis is a test", captor.getValue().getDescription());
    }

    @Test
    public void receivedGoAwayShouldFailUnknownBufferedStreams() throws Exception {
        receiveMaxConcurrentStreams(0);
        ChannelFuture future = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        // Read a GOAWAY that indicates our stream was never processed by the server.
        channelRead(/* Cancel */
        goAwayFrame(0, 8, Unpooled.copiedBuffer("this is a test", Charsets.UTF_8)));
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isSuccess());
        Status status = Status.fromThrowable(future.cause());
        Assert.assertEquals(CANCELLED.getCode(), status.getCode());
        Assert.assertEquals("HTTP/2 error code: CANCEL\nReceived Goaway\nthis is a test", status.getDescription());
    }

    @Test
    public void receivedGoAwayShouldFailNewStreams() throws Exception {
        // Read a GOAWAY that indicates our stream was never processed by the server.
        channelRead(/* Cancel */
        goAwayFrame(0, 8, Unpooled.copiedBuffer("this is a test", Charsets.UTF_8)));
        // Now try to create a stream.
        ChannelFuture future = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isSuccess());
        Status status = Status.fromThrowable(future.cause());
        Assert.assertEquals(CANCELLED.getCode(), status.getCode());
        Assert.assertEquals("HTTP/2 error code: CANCEL\nReceived Goaway\nthis is a test", status.getDescription());
    }

    // This test is not as useful as it looks, because the HTTP/2 Netty code catches and doesn't
    // propagate exceptions during the onGoAwayReceived callback.
    @Test
    public void receivedGoAway_notUtf8() throws Exception {
        // 0xFF is never permitted in UTF-8. 0xF0 should have 3 continuations following, and 0x0a isn't
        // a continuation.
        channelRead(/* ENHANCE_YOUR_CALM */
        goAwayFrame(0, 11, Unpooled.copiedBuffer(new byte[]{ ((byte) (255)), ((byte) (240)), ((byte) (10)) })));
    }

    @Test
    public void receivedGoAway_enhanceYourCalmWithoutTooManyPings() throws Exception {
        final AtomicBoolean b = new AtomicBoolean();
        tooManyPingsRunnable = new Runnable() {
            @Override
            public void run() {
                b.set(true);
            }
        };
        setUp();
        channelRead(/* ENHANCE_YOUR_CALM */
        goAwayFrame(0, 11, Unpooled.copiedBuffer("not_many_pings", Charsets.UTF_8)));
        Assert.assertFalse(b.get());
    }

    @Test
    public void receivedGoAway_enhanceYourCalmWithTooManyPings() throws Exception {
        final AtomicBoolean b = new AtomicBoolean();
        tooManyPingsRunnable = new Runnable() {
            @Override
            public void run() {
                b.set(true);
            }
        };
        setUp();
        channelRead(/* ENHANCE_YOUR_CALM */
        goAwayFrame(0, 11, Unpooled.copiedBuffer("too_many_pings", Charsets.UTF_8)));
        Assert.assertTrue(b.get());
    }

    @Test
    public void cancelStreamShouldCreateAndThenFailBufferedStream() throws Exception {
        receiveMaxConcurrentStreams(0);
        enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        Assert.assertEquals(3, streamTransportState.id());
        cancelStream(CANCELLED);
        Mockito.verify(streamListener).closed(ArgumentMatchers.eq(CANCELLED), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void channelShutdownShouldCancelBufferedStreams() throws Exception {
        // Force a stream to get added to the pending queue.
        receiveMaxConcurrentStreams(0);
        ChannelFuture future = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        handler().channelInactive(ctx());
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isSuccess());
        Mockito.verify(mockKeepAliveManager, Mockito.times(1)).onTransportTermination();// channelInactive

        Mockito.verifyNoMoreInteractions(mockKeepAliveManager);
    }

    @Test
    public void channelShutdownShouldFailInFlightStreams() throws Exception {
        createStream();
        handler().channelInactive(ctx());
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(streamListener).closed(captor.capture(), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.notNull(Metadata.class));
        Assert.assertEquals(Status.UNAVAILABLE.getCode(), captor.getValue().getCode());
    }

    @Test
    public void connectionWindowShouldBeOverridden() throws Exception {
        flowControlWindow = 1048576;// 1MiB

        setUp();
        Http2Stream connectionStream = connection().connectionStream();
        Http2LocalFlowController localFlowController = connection().local().flowController();
        int actualInitialWindowSize = localFlowController.initialWindowSize(connectionStream);
        int actualWindowSize = localFlowController.windowSize(connectionStream);
        Assert.assertEquals(flowControlWindow, actualWindowSize);
        Assert.assertEquals(flowControlWindow, actualInitialWindowSize);
        Assert.assertEquals(1048576, actualWindowSize);
    }

    @Test
    public void createIncrementsIdsForActualAndBufferdStreams() throws Exception {
        receiveMaxConcurrentStreams(2);
        enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        Assert.assertEquals(3, streamTransportState.id());
        streamTransportState = new NettyClientHandlerTest.TransportStateImpl(handler(), channel().eventLoop(), DEFAULT_MAX_MESSAGE_SIZE, transportTracer);
        streamTransportState.setListener(streamListener);
        enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        Assert.assertEquals(5, streamTransportState.id());
        streamTransportState = new NettyClientHandlerTest.TransportStateImpl(handler(), channel().eventLoop(), DEFAULT_MAX_MESSAGE_SIZE, transportTracer);
        streamTransportState.setListener(streamListener);
        enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, streamTransportState));
        Assert.assertEquals(7, streamTransportState.id());
        Mockito.verify(mockKeepAliveManager, Mockito.times(1)).onTransportActive();// onStreamActive

        Mockito.verifyNoMoreInteractions(mockKeepAliveManager);
    }

    @Test
    public void exhaustedStreamsShouldFail() throws Exception {
        streamId = Integer.MAX_VALUE;
        setUp();
        Assert.assertNull(lifecycleManager.getShutdownStatus());
        // Create the MAX_INT stream.
        ChannelFuture future = createStream();
        Assert.assertTrue(future.isSuccess());
        NettyClientHandlerTest.TransportStateImpl newStreamTransportState = new NettyClientHandlerTest.TransportStateImpl(handler(), channel().eventLoop(), DEFAULT_MAX_MESSAGE_SIZE, transportTracer);
        // This should fail - out of stream IDs.
        future = enqueue(NettyClientHandlerTest.newCreateStreamCommand(grpcHeaders, newStreamTransportState));
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isSuccess());
        Status status = lifecycleManager.getShutdownStatus();
        Assert.assertNotNull(status);
        Assert.assertTrue(("status does not reference 'exhausted': " + status), status.getDescription().contains("exhausted"));
    }

    @Test
    public void nonExistentStream() throws Exception {
        Status status = INTERNAL.withDescription("zz");
        lifecycleManager.notifyShutdown(status);
        // Stream creation can race with the transport shutting down, with the create command already
        // enqueued.
        ChannelFuture future1 = createStream();
        future1.await();
        Assert.assertNotNull(future1.cause());
        assertThat(Status.fromThrowable(future1.cause()).getCode()).isEqualTo(status.getCode());
        ChannelFuture future2 = enqueue(new CancelClientStreamCommand(streamTransportState, status));
        future2.sync();
    }

    @Test
    public void ping() throws Exception {
        NettyClientHandlerTest.PingCallbackImpl callback1 = new NettyClientHandlerTest.PingCallbackImpl();
        Assert.assertEquals(0, transportTracer.getStats().keepAlivesSent);
        sendPing(callback1);
        Assert.assertEquals(1, transportTracer.getStats().keepAlivesSent);
        // add'l ping will be added as listener to outstanding operation
        NettyClientHandlerTest.PingCallbackImpl callback2 = new NettyClientHandlerTest.PingCallbackImpl();
        sendPing(callback2);
        Assert.assertEquals(1, transportTracer.getStats().keepAlivesSent);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(long.class);
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), captor.capture(), ArgumentMatchers.any(ChannelPromise.class));
        // getting a bad ack won't cause the callback to be invoked
        long pingPayload = captor.getValue();
        // to compute bad payload, read the good payload and subtract one
        long badPingPayload = pingPayload - 1;
        channelRead(pingFrame(true, badPingPayload));
        // operation not complete because ack was wrong
        Assert.assertEquals(0, callback1.invocationCount);
        Assert.assertEquals(0, callback2.invocationCount);
        nanoTime += 10101;
        // reading the proper response should complete the future
        channelRead(pingFrame(true, pingPayload));
        Assert.assertEquals(1, callback1.invocationCount);
        Assert.assertEquals(10101, callback1.roundTripTime);
        Assert.assertNull(callback1.failureCause);
        // callback2 piggy-backed on same operation
        Assert.assertEquals(1, callback2.invocationCount);
        Assert.assertEquals(10101, callback2.roundTripTime);
        Assert.assertNull(callback2.failureCause);
        // now that previous ping is done, next request starts a new operation
        callback1 = new NettyClientHandlerTest.PingCallbackImpl();
        Assert.assertEquals(1, transportTracer.getStats().keepAlivesSent);
        sendPing(callback1);
        Assert.assertEquals(2, transportTracer.getStats().keepAlivesSent);
        Assert.assertEquals(0, callback1.invocationCount);
    }

    @Test
    public void ping_failsWhenChannelCloses() throws Exception {
        NettyClientHandlerTest.PingCallbackImpl callback = new NettyClientHandlerTest.PingCallbackImpl();
        sendPing(callback);
        Assert.assertEquals(0, callback.invocationCount);
        handler().channelInactive(ctx());
        // ping failed on channel going inactive
        Assert.assertEquals(1, callback.invocationCount);
        Assert.assertTrue(((callback.failureCause) instanceof StatusException));
        Assert.assertEquals(UNAVAILABLE, getStatus().getCode());
        // A failed ping is still counted
        Assert.assertEquals(1, transportTracer.getStats().keepAlivesSent);
    }

    @Test
    public void oustandingUserPingShouldNotInteractWithDataPing() throws Exception {
        createStream();
        handler().setAutoTuneFlowControl(true);
        NettyClientHandlerTest.PingCallbackImpl callback = new NettyClientHandlerTest.PingCallbackImpl();
        Assert.assertEquals(0, transportTracer.getStats().keepAlivesSent);
        sendPing(callback);
        Assert.assertEquals(1, transportTracer.getStats().keepAlivesSent);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(long.class);
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), captor.capture(), ArgumentMatchers.any(ChannelPromise.class));
        long payload = captor.getValue();
        channelRead(grpcDataFrame(3, false, contentAsArray()));
        long pingData = handler().flowControlPing().payload();
        channelRead(pingFrame(true, pingData));
        Assert.assertEquals(1, handler().flowControlPing().getPingReturn());
        Assert.assertEquals(0, callback.invocationCount);
        channelRead(pingFrame(true, payload));
        Assert.assertEquals(1, handler().flowControlPing().getPingReturn());
        Assert.assertEquals(1, callback.invocationCount);
        Assert.assertEquals(1, transportTracer.getStats().keepAlivesSent);
    }

    @Test
    public void exceptionCaughtShouldCloseConnection() throws Exception {
        handler().exceptionCaught(ctx(), new RuntimeException("fake exception"));
        // TODO(nmittler): EmbeddedChannel does not currently invoke the channelInactive processing,
        // so exceptionCaught() will not close streams properly in this test.
        // Once https://github.com/netty/netty/issues/4316 is resolved, we should also verify that
        // any open streams are closed properly.
        Assert.assertFalse(channel().isOpen());
    }

    private static class PingCallbackImpl implements ClientTransport.PingCallback {
        int invocationCount;

        long roundTripTime;

        Throwable failureCause;

        @Override
        public void onSuccess(long roundTripTimeNanos) {
            (invocationCount)++;
            this.roundTripTime = roundTripTimeNanos;
        }

        @Override
        public void onFailure(Throwable cause) {
            (invocationCount)++;
            this.failureCause = cause;
        }
    }

    private static class TransportStateImpl extends NettyClientStream.TransportState {
        public TransportStateImpl(NettyClientHandler handler, EventLoop eventLoop, int maxMessageSize, TransportTracer transportTracer) {
            super(handler, eventLoop, maxMessageSize, NOOP, transportTracer);
        }

        @Override
        protected Status statusFromFailedFuture(ChannelFuture f) {
            return Utils.statusFromThrowable(f.cause());
        }
    }
}

