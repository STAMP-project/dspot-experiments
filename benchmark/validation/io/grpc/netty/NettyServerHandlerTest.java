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


import Attributes.EMPTY;
import Code.CANCELLED;
import Code.INTERNAL;
import Code.UNIMPLEMENTED;
import Code.UNKNOWN;
import Http2Error.CANCEL;
import Http2Error.ENHANCE_YOUR_CALM;
import Http2Error.NO_ERROR;
import InternalStatus.CODE_KEY;
import InternalStatus.MESSAGE_KEY;
import NettyServerHandler.GRACEFUL_SHUTDOWN_PING;
import ServerStreamTracer.Factory;
import StreamListener.MessageProducer;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.truth.Truth;
import io.grpc.Attributes;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StreamTracer;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.KeepAliveManager;
import io.grpc.internal.ServerStream;
import io.grpc.internal.ServerStreamListener;
import io.grpc.internal.ServerTransportListener;
import io.grpc.internal.StatsTraceContext;
import io.grpc.internal.testing.TestServerStreamTracer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.util.AsciiString;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static KeepAliveEnforcer.MAX_PING_STRIKES;
import static org.mockito.ArgumentMatchers.any;


/**
 * Unit tests for {@link NettyServerHandler}.
 */
@RunWith(JUnit4.class)
public class NettyServerHandlerTest extends NettyHandlerTestBase<NettyServerHandler> {
    @Rule
    public final TestRule globalTimeout = new DisableOnDebug(Timeout.seconds(10));

    private static final int STREAM_ID = 3;

    private static final AsciiString HTTP_FAKE_METHOD = AsciiString.of("FAKE");

    @Mock
    private ServerStreamListener streamListener;

    @Mock
    private Factory streamTracerFactory;

    private final ServerTransportListener transportListener = Mockito.mock(ServerTransportListener.class, AdditionalAnswers.delegatesTo(new NettyServerHandlerTest.ServerTransportListenerImpl()));

    private final TestServerStreamTracer streamTracer = new TestServerStreamTracer();

    private NettyServerStream stream;

    private KeepAliveManager spyKeepAliveManager;

    final Queue<InputStream> streamListenerMessageQueue = new LinkedList<>();

    private int maxConcurrentStreams = Integer.MAX_VALUE;

    private int maxHeaderListSize = Integer.MAX_VALUE;

    private boolean permitKeepAliveWithoutCalls = true;

    private long permitKeepAliveTimeInNanos = 0;

    private long maxConnectionIdleInNanos = NettyServerBuilder.MAX_CONNECTION_IDLE_NANOS_DISABLED;

    private long maxConnectionAgeInNanos = NettyServerBuilder.MAX_CONNECTION_AGE_NANOS_DISABLED;

    private long maxConnectionAgeGraceInNanos = NettyServerBuilder.MAX_CONNECTION_AGE_GRACE_NANOS_INFINITE;

    private long keepAliveTimeInNanos = DEFAULT_SERVER_KEEPALIVE_TIME_NANOS;

    private long keepAliveTimeoutInNanos = DEFAULT_SERVER_KEEPALIVE_TIMEOUT_NANOS;

    private class ServerTransportListenerImpl implements ServerTransportListener {
        @Override
        public void streamCreated(ServerStream stream, String method, Metadata headers) {
            stream.setListener(streamListener);
        }

        @Override
        public Attributes transportReady(Attributes attributes) {
            return Attributes.EMPTY;
        }

        @Override
        public void transportTerminated() {
        }
    }

    @Test
    public void transportReadyDelayedUntilConnectionPreface() throws Exception {
        initChannel(new io.grpc.netty.GrpcHttp2HeadersUtils.GrpcHttp2ServerHeadersDecoder(GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE));
        /* securityInfo= */
        handler().handleProtocolNegotiationCompleted(EMPTY, null);
        Mockito.verify(transportListener, Mockito.never()).transportReady(any(Attributes.class));
        // Simulate receipt of the connection preface
        channelRead(Http2CodecUtil.connectionPrefaceBuf());
        channelRead(serializeSettings(new Http2Settings()));
        Mockito.verify(transportListener).transportReady(any(Attributes.class));
    }

    @Test
    public void sendFrameShouldSucceed() throws Exception {
        manualSetUp();
        createStream();
        // Send a frame and verify that it was written.
        ChannelFuture future = enqueue(new SendGrpcFrameCommand(stream.transportState(), content(), false));
        Assert.assertTrue(future.isSuccess());
        verifyWrite().writeData(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(content()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void streamTracerCreated() throws Exception {
        manualSetUp();
        createStream();
        Mockito.verify(streamTracerFactory).newServerStreamTracer(ArgumentMatchers.eq("foo/bar"), ArgumentMatchers.any(Metadata.class));
        StatsTraceContext statsTraceCtx = stream.statsTraceContext();
        List<StreamTracer> tracers = statsTraceCtx.getTracersForTest();
        Assert.assertEquals(1, tracers.size());
        Assert.assertSame(streamTracer, tracers.get(0));
    }

    @Test
    public void inboundDataWithEndStreamShouldForwardToStreamListener() throws Exception {
        manualSetUp();
        inboundDataShouldForwardToStreamListener(true);
    }

    @Test
    public void inboundDataShouldForwardToStreamListener() throws Exception {
        manualSetUp();
        inboundDataShouldForwardToStreamListener(false);
    }

    @Test
    public void clientHalfCloseShouldForwardToStreamListener() throws Exception {
        manualSetUp();
        createStream();
        stream.request(1);
        channelRead(emptyGrpcFrame(NettyServerHandlerTest.STREAM_ID, true));
        Mockito.verify(streamListener, Mockito.atLeastOnce()).messagesAvailable(ArgumentMatchers.any(MessageProducer.class));
        InputStream message = streamListenerMessageQueue.poll();
        Assert.assertArrayEquals(new byte[0], ByteStreams.toByteArray(message));
        Assert.assertNull("no additional message expected", streamListenerMessageQueue.poll());
        Mockito.verify(streamListener).halfClosed();
        Mockito.verify(streamListener, Mockito.atLeastOnce()).onReady();
        Mockito.verifyNoMoreInteractions(streamListener);
    }

    @Test
    public void clientCancelShouldForwardToStreamListener() throws Exception {
        manualSetUp();
        createStream();
        channelRead(rstStreamFrame(NettyServerHandlerTest.STREAM_ID, ((int) (CANCEL.code()))));
        ArgumentCaptor<Status> statusCap = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(streamListener).closed(statusCap.capture());
        Assert.assertEquals(CANCELLED, statusCap.getValue().getCode());
        Truth.assertThat(statusCap.getValue().getDescription()).contains("RST_STREAM");
        Mockito.verify(streamListener, Mockito.atLeastOnce()).onReady();
        Assert.assertNull("no messages expected", streamListenerMessageQueue.poll());
    }

    @Test
    public void streamErrorShouldNotCloseChannel() throws Exception {
        manualSetUp();
        createStream();
        stream.request(1);
        // When a DATA frame is read, throw an exception. It will be converted into an
        // Http2StreamException.
        RuntimeException e = new RuntimeException("Fake Exception");
        Mockito.doThrow(e).when(streamListener).messagesAvailable(ArgumentMatchers.any(MessageProducer.class));
        // Read a DATA frame to trigger the exception.
        channelRead(emptyGrpcFrame(NettyServerHandlerTest.STREAM_ID, true));
        // Verify that the channel was NOT closed.
        Assert.assertTrue(channel().isOpen());
        // Verify the stream was closed.
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(streamListener).closed(captor.capture());
        Assert.assertEquals(e, captor.getValue().asException().getCause());
        Assert.assertEquals(UNKNOWN, captor.getValue().getCode());
    }

    @Test
    public void closeShouldGracefullyCloseChannel() throws Exception {
        manualSetUp();
        handler().close(ctx(), newPromise());
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.isA(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(GRACEFUL_SHUTDOWN_PING), ArgumentMatchers.isA(ChannelPromise.class));
        channelRead(/* ack= */
        pingFrame(true, GRACEFUL_SHUTDOWN_PING));
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.isA(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // Verify that the channel was closed.
        Assert.assertFalse(channel().isOpen());
    }

    @Test
    public void exceptionCaughtShouldCloseConnection() throws Exception {
        manualSetUp();
        handler().exceptionCaught(ctx(), new RuntimeException("fake exception"));
        // TODO(nmittler): EmbeddedChannel does not currently invoke the channelInactive processing,
        // so exceptionCaught() will not close streams properly in this test.
        // Once https://github.com/netty/netty/issues/4316 is resolved, we should also verify that
        // any open streams are closed properly.
        Assert.assertFalse(channel().isOpen());
    }

    @Test
    public void channelInactiveShouldCloseStreams() throws Exception {
        manualSetUp();
        createStream();
        handler().channelInactive(ctx());
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(streamListener).closed(captor.capture());
        Assert.assertFalse(captor.getValue().isOk());
    }

    @Test
    public void shouldAdvertiseMaxConcurrentStreams() throws Exception {
        maxConcurrentStreams = 314;
        manualSetUp();
        ArgumentCaptor<Http2Settings> captor = ArgumentCaptor.forClass(Http2Settings.class);
        verifyWrite().writeSettings(ArgumentMatchers.any(ChannelHandlerContext.class), captor.capture(), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertEquals(maxConcurrentStreams, captor.getValue().maxConcurrentStreams().intValue());
    }

    @Test
    public void shouldAdvertiseMaxHeaderListSize() throws Exception {
        maxHeaderListSize = 123;
        manualSetUp();
        ArgumentCaptor<Http2Settings> captor = ArgumentCaptor.forClass(Http2Settings.class);
        verifyWrite().writeSettings(ArgumentMatchers.any(ChannelHandlerContext.class), captor.capture(), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertEquals(maxHeaderListSize, captor.getValue().maxHeaderListSize().intValue());
    }

    @Test
    public void connectionWindowShouldBeOverridden() throws Exception {
        flowControlWindow = 1048576;// 1MiB

        manualSetUp();
        Http2Stream connectionStream = connection().connectionStream();
        Http2LocalFlowController localFlowController = connection().local().flowController();
        int actualInitialWindowSize = localFlowController.initialWindowSize(connectionStream);
        int actualWindowSize = localFlowController.windowSize(connectionStream);
        Assert.assertEquals(flowControlWindow, actualWindowSize);
        Assert.assertEquals(flowControlWindow, actualInitialWindowSize);
    }

    @Test
    public void cancelShouldSendRstStream() throws Exception {
        manualSetUp();
        createStream();
        enqueue(new CancelServerStreamCommand(stream.transportState(), Status.DEADLINE_EXCEEDED));
        verifyWrite().writeRstStream(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(stream.transportState().id()), ArgumentMatchers.eq(CANCEL.code()), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void headersWithInvalidContentTypeShouldFail() throws Exception {
        manualSetUp();
        Http2Headers headers = new DefaultHttp2Headers().method(Utils.HTTP_METHOD).set(Utils.CONTENT_TYPE_HEADER, new AsciiString("application/bad", Charsets.UTF_8)).set(Utils.TE_HEADER, Utils.TE_TRAILERS).path(new AsciiString("/foo/bar"));
        ByteBuf headersFrame = headersFrame(NettyServerHandlerTest.STREAM_ID, headers);
        channelRead(headersFrame);
        Http2Headers responseHeaders = new DefaultHttp2Headers().set(CODE_KEY.name(), String.valueOf(INTERNAL.value())).set(MESSAGE_KEY.name(), "Content-Type 'application/bad' is not supported").status(("" + 415)).set(Utils.CONTENT_TYPE_HEADER, "text/plain; encoding=utf-8");
        verifyWrite().writeHeaders(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(responseHeaders), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void headersWithInvalidMethodShouldFail() throws Exception {
        manualSetUp();
        Http2Headers headers = new DefaultHttp2Headers().method(NettyServerHandlerTest.HTTP_FAKE_METHOD).set(Utils.CONTENT_TYPE_HEADER, Utils.CONTENT_TYPE_GRPC).path(new AsciiString("/foo/bar"));
        ByteBuf headersFrame = headersFrame(NettyServerHandlerTest.STREAM_ID, headers);
        channelRead(headersFrame);
        Http2Headers responseHeaders = new DefaultHttp2Headers().set(CODE_KEY.name(), String.valueOf(INTERNAL.value())).set(MESSAGE_KEY.name(), "Method 'FAKE' is not supported").status(("" + 405)).set(Utils.CONTENT_TYPE_HEADER, "text/plain; encoding=utf-8");
        verifyWrite().writeHeaders(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(responseHeaders), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void headersWithMissingPathShouldFail() throws Exception {
        manualSetUp();
        Http2Headers headers = new DefaultHttp2Headers().method(Utils.HTTP_METHOD).set(Utils.CONTENT_TYPE_HEADER, Utils.CONTENT_TYPE_GRPC);
        ByteBuf headersFrame = headersFrame(NettyServerHandlerTest.STREAM_ID, headers);
        channelRead(headersFrame);
        Http2Headers responseHeaders = new DefaultHttp2Headers().set(CODE_KEY.name(), String.valueOf(UNIMPLEMENTED.value())).set(MESSAGE_KEY.name(), "Expected path but is missing").status(("" + 404)).set(Utils.CONTENT_TYPE_HEADER, "text/plain; encoding=utf-8");
        verifyWrite().writeHeaders(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(responseHeaders), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void headersWithInvalidPathShouldFail() throws Exception {
        manualSetUp();
        Http2Headers headers = new DefaultHttp2Headers().method(Utils.HTTP_METHOD).set(Utils.CONTENT_TYPE_HEADER, Utils.CONTENT_TYPE_GRPC).path(new AsciiString("foo/bar"));
        ByteBuf headersFrame = headersFrame(NettyServerHandlerTest.STREAM_ID, headers);
        channelRead(headersFrame);
        Http2Headers responseHeaders = new DefaultHttp2Headers().set(CODE_KEY.name(), String.valueOf(UNIMPLEMENTED.value())).set(MESSAGE_KEY.name(), "Expected path to start with /: foo/bar").status(("" + 404)).set(Utils.CONTENT_TYPE_HEADER, "text/plain; encoding=utf-8");
        verifyWrite().writeHeaders(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(responseHeaders), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void headersSupportExtensionContentType() throws Exception {
        manualSetUp();
        Http2Headers headers = new DefaultHttp2Headers().method(Utils.HTTP_METHOD).set(Utils.CONTENT_TYPE_HEADER, new AsciiString("application/grpc+json", Charsets.UTF_8)).set(Utils.TE_HEADER, Utils.TE_TRAILERS).path(new AsciiString("/foo/bar"));
        ByteBuf headersFrame = headersFrame(NettyServerHandlerTest.STREAM_ID, headers);
        channelRead(headersFrame);
        ArgumentCaptor<NettyServerStream> streamCaptor = ArgumentCaptor.forClass(NettyServerStream.class);
        ArgumentCaptor<String> methodCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(transportListener).streamCreated(streamCaptor.capture(), methodCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        stream = streamCaptor.getValue();
    }

    @Test
    public void keepAliveManagerOnDataReceived_headersRead() throws Exception {
        manualSetUp();
        ByteBuf headersFrame = headersFrame(NettyServerHandlerTest.STREAM_ID, new DefaultHttp2Headers());
        channelRead(headersFrame);
        Mockito.verify(spyKeepAliveManager).onDataReceived();
        Mockito.verify(spyKeepAliveManager, Mockito.never()).onTransportTermination();
    }

    @Test
    public void keepAliveManagerOnDataReceived_dataRead() throws Exception {
        manualSetUp();
        createStream();
        Mockito.verify(spyKeepAliveManager).onDataReceived();// received headers

        channelRead(grpcDataFrame(NettyServerHandlerTest.STREAM_ID, false, contentAsArray()));
        Mockito.verify(spyKeepAliveManager, Mockito.times(2)).onDataReceived();
        channelRead(grpcDataFrame(NettyServerHandlerTest.STREAM_ID, false, contentAsArray()));
        Mockito.verify(spyKeepAliveManager, Mockito.times(3)).onDataReceived();
        Mockito.verify(spyKeepAliveManager, Mockito.never()).onTransportTermination();
    }

    @Test
    public void keepAliveManagerOnDataReceived_rstStreamRead() throws Exception {
        manualSetUp();
        createStream();
        Mockito.verify(spyKeepAliveManager).onDataReceived();// received headers

        channelRead(rstStreamFrame(NettyServerHandlerTest.STREAM_ID, ((int) (CANCEL.code()))));
        Mockito.verify(spyKeepAliveManager, Mockito.times(2)).onDataReceived();
        Mockito.verify(spyKeepAliveManager, Mockito.never()).onTransportTermination();
    }

    @Test
    public void keepAliveManagerOnDataReceived_pingRead() throws Exception {
        manualSetUp();
        channelRead(/* isAck */
        pingFrame(false, 1234L));
        Mockito.verify(spyKeepAliveManager).onDataReceived();
        Mockito.verify(spyKeepAliveManager, Mockito.never()).onTransportTermination();
    }

    @Test
    public void keepAliveManagerOnDataReceived_pingActRead() throws Exception {
        manualSetUp();
        channelRead(/* isAck */
        pingFrame(true, 1234L));
        Mockito.verify(spyKeepAliveManager).onDataReceived();
        Mockito.verify(spyKeepAliveManager, Mockito.never()).onTransportTermination();
    }

    @Test
    public void keepAliveManagerOnTransportTermination() throws Exception {
        manualSetUp();
        handler().channelInactive(handler().ctx());
        Mockito.verify(spyKeepAliveManager).onTransportTermination();
    }

    @Test
    public void keepAliveManager_pingSent() throws Exception {
        keepAliveTimeInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        keepAliveTimeoutInNanos = TimeUnit.MINUTES.toNanos(30L);
        manualSetUp();
        Assert.assertEquals(0, transportTracer.getStats().keepAlivesSent);
        fakeClock().forwardNanos(keepAliveTimeInNanos);
        Assert.assertEquals(1, transportTracer.getStats().keepAlivesSent);
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(57005L), ArgumentMatchers.any(ChannelPromise.class));
        spyKeepAliveManager.onDataReceived();
        fakeClock().forwardTime(10L, TimeUnit.MILLISECONDS);
        verifyWrite(Mockito.times(2)).writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(57005L), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
    }

    @Test
    public void keepAliveManager_pingTimeout() throws Exception {
        /* nanoseconds */
        keepAliveTimeInNanos = 123L;
        /* nanoseconds */
        keepAliveTimeoutInNanos = 456L;
        manualSetUp();
        fakeClock().forwardNanos(keepAliveTimeInNanos);
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardNanos(keepAliveTimeoutInNanos);
        Assert.assertTrue((!(channel().isOpen())));
    }

    @Test
    public void keepAliveEnforcer_enforcesPings() throws Exception {
        permitKeepAliveWithoutCalls = false;
        permitKeepAliveTimeInNanos = TimeUnit.HOURS.toNanos(1);
        manualSetUp();
        for (int i = 0; i < ((MAX_PING_STRIKES) + 1); i++) {
            channelRead(/* isAck */
            pingFrame(false, 1L));
        }
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ENHANCE_YOUR_CALM.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertFalse(channel().isActive());
    }

    @Test
    public void keepAliveEnforcer_sendingDataResetsCounters() throws Exception {
        permitKeepAliveWithoutCalls = false;
        permitKeepAliveTimeInNanos = TimeUnit.HOURS.toNanos(1);
        manualSetUp();
        createStream();
        Http2Headers headers = Utils.convertServerHeaders(new Metadata());
        ChannelFuture future = enqueue(SendResponseHeadersCommand.createHeaders(stream.transportState(), headers));
        future.get();
        for (int i = 0; i < 10; i++) {
            future = enqueue(new SendGrpcFrameCommand(stream.transportState(), content().retainedSlice(), false));
            future.get();
            channel().releaseOutbound();
            channelRead(/* isAck */
            pingFrame(false, 1L));
        }
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(ENHANCE_YOUR_CALM.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void keepAliveEnforcer_initialIdle() throws Exception {
        permitKeepAliveWithoutCalls = false;
        permitKeepAliveTimeInNanos = 0;
        manualSetUp();
        for (int i = 0; i < ((MAX_PING_STRIKES) + 1); i++) {
            channelRead(/* isAck */
            pingFrame(false, 1L));
        }
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ENHANCE_YOUR_CALM.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertFalse(channel().isActive());
    }

    @Test
    public void keepAliveEnforcer_noticesActive() throws Exception {
        permitKeepAliveWithoutCalls = false;
        permitKeepAliveTimeInNanos = 0;
        manualSetUp();
        createStream();
        for (int i = 0; i < 10; i++) {
            channelRead(/* isAck */
            pingFrame(false, 1L));
        }
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(ENHANCE_YOUR_CALM.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void keepAliveEnforcer_noticesInactive() throws Exception {
        permitKeepAliveWithoutCalls = false;
        permitKeepAliveTimeInNanos = 0;
        manualSetUp();
        createStream();
        channelRead(rstStreamFrame(NettyServerHandlerTest.STREAM_ID, ((int) (CANCEL.code()))));
        for (int i = 0; i < ((MAX_PING_STRIKES) + 1); i++) {
            channelRead(/* isAck */
            pingFrame(false, 1L));
        }
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(ENHANCE_YOUR_CALM.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertFalse(channel().isActive());
    }

    @Test
    public void noGoAwaySentBeforeMaxConnectionIdleReached() throws Exception {
        maxConnectionIdleInNanos = TimeUnit.MINUTES.toNanos(30L);
        manualSetUp();
        fakeClock().forwardTime(20, TimeUnit.MINUTES);
        // GO_AWAY not sent yet
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
    }

    @Test
    public void maxConnectionIdle_goAwaySent_pingAck() throws Exception {
        maxConnectionIdleInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        manualSetUp();
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardNanos(maxConnectionIdleInNanos);
        // first GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // ping sent
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(40715087873L), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        channelRead(/* isAck */
        pingFrame(true, 57005L));// irrelevant ping Ack

        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
        channelRead(/* isAck */
        pingFrame(true, 40715087873L));
        // second GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // channel closed
        Assert.assertTrue((!(channel().isOpen())));
    }

    @Test
    public void maxConnectionIdle_goAwaySent_pingTimeout() throws Exception {
        maxConnectionIdleInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        manualSetUp();
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardNanos(maxConnectionIdleInNanos);
        // first GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // ping sent
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(40715087873L), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardTime(10, TimeUnit.SECONDS);
        // second GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // channel closed
        Assert.assertTrue((!(channel().isOpen())));
    }

    @Test
    public void maxConnectionIdle_activeThenRst_pingAck() throws Exception {
        maxConnectionIdleInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        manualSetUp();
        createStream();
        fakeClock().forwardNanos(maxConnectionIdleInNanos);
        // GO_AWAY not sent when active
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
        channelRead(rstStreamFrame(NettyServerHandlerTest.STREAM_ID, ((int) (CANCEL.code()))));
        fakeClock().forwardNanos(maxConnectionIdleInNanos);
        // first GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // ping sent
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(40715087873L), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardTime(10, TimeUnit.SECONDS);
        // second GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // channel closed
        Assert.assertTrue((!(channel().isOpen())));
    }

    @Test
    public void maxConnectionIdle_activeThenRst_pingTimeoutk() throws Exception {
        maxConnectionIdleInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        manualSetUp();
        createStream();
        fakeClock().forwardNanos(maxConnectionIdleInNanos);
        // GO_AWAY not sent when active
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
        channelRead(rstStreamFrame(NettyServerHandlerTest.STREAM_ID, ((int) (CANCEL.code()))));
        fakeClock().forwardNanos(maxConnectionIdleInNanos);
        // first GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // ping sent
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(40715087873L), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
        channelRead(/* isAck */
        pingFrame(true, 40715087873L));
        // second GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // channel closed
        Assert.assertTrue((!(channel().isOpen())));
    }

    @Test
    public void noGoAwaySentBeforeMaxConnectionAgeReached() throws Exception {
        maxConnectionAgeInNanos = TimeUnit.MINUTES.toNanos(30L);
        manualSetUp();
        fakeClock().forwardTime(20, TimeUnit.MINUTES);
        // GO_AWAY not sent yet
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
    }

    @Test
    public void maxConnectionAge_goAwaySent_pingAck() throws Exception {
        maxConnectionAgeInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        manualSetUp();
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardNanos(maxConnectionAgeInNanos);
        // first GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // ping sent
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(40715087873L), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        channelRead(/* isAck */
        pingFrame(true, 57005L));// irrelevant ping Ack

        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
        channelRead(/* isAck */
        pingFrame(true, 40715087873L));
        // second GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // channel closed
        Assert.assertTrue((!(channel().isOpen())));
    }

    @Test
    public void maxConnectionAge_goAwaySent_pingTimeout() throws Exception {
        maxConnectionAgeInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        manualSetUp();
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardNanos(maxConnectionAgeInNanos);
        // first GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // ping sent
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(40715087873L), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardTime(10, TimeUnit.SECONDS);
        // second GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // channel closed
        Assert.assertTrue((!(channel().isOpen())));
    }

    @Test
    public void maxConnectionAgeGrace_channelStillOpenDuringGracePeriod() throws Exception {
        maxConnectionAgeInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        maxConnectionAgeGraceInNanos = TimeUnit.MINUTES.toNanos(30L);
        manualSetUp();
        createStream();
        fakeClock().forwardNanos(maxConnectionAgeInNanos);
        // first GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // ping sent
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(40715087873L), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        fakeClock().forwardTime(20, TimeUnit.MINUTES);
        // second GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // channel not closed yet
        Assert.assertTrue(channel().isOpen());
    }

    @Test
    public void maxConnectionAgeGrace_channelClosedAfterGracePeriod_withPingTimeout() throws Exception {
        maxConnectionAgeInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        maxConnectionAgeGraceInNanos = TimeUnit.MINUTES.toNanos(30L);// greater than ping timeout

        manualSetUp();
        createStream();
        fakeClock().forwardNanos(maxConnectionAgeInNanos);
        // first GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // ping sent
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(40715087873L), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        fakeClock().forwardNanos(TimeUnit.SECONDS.toNanos(10));
        // second GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        fakeClock().forwardNanos(((maxConnectionAgeGraceInNanos) - 2));
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardTime(2, TimeUnit.MILLISECONDS);
        // channel closed
        Assert.assertTrue((!(channel().isOpen())));
    }

    @Test
    public void maxConnectionAgeGrace_channelClosedAfterGracePeriod_withPingAck() throws Exception {
        maxConnectionAgeInNanos = TimeUnit.MILLISECONDS.toNanos(10L);
        maxConnectionAgeGraceInNanos = TimeUnit.MINUTES.toNanos(30L);// greater than ping timeout

        manualSetUp();
        createStream();
        fakeClock().forwardNanos(maxConnectionAgeInNanos);
        // first GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(Integer.MAX_VALUE), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        // ping sent
        verifyWrite().writePing(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(false), ArgumentMatchers.eq(40715087873L), ArgumentMatchers.any(ChannelPromise.class));
        verifyWrite(Mockito.never()).writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        long pingRoundTripMillis = 100;// less than ping timeout

        fakeClock().forwardTime(pingRoundTripMillis, TimeUnit.MILLISECONDS);
        channelRead(/* isAck */
        pingFrame(true, 40715087873L));
        // second GO_AWAY sent
        verifyWrite().writeGoAway(ArgumentMatchers.eq(ctx()), ArgumentMatchers.eq(NettyServerHandlerTest.STREAM_ID), ArgumentMatchers.eq(NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        fakeClock().forwardNanos(((maxConnectionAgeGraceInNanos) - (TimeUnit.MILLISECONDS.toNanos(2))));
        Assert.assertTrue(channel().isOpen());
        fakeClock().forwardTime(2, TimeUnit.MILLISECONDS);
        // channel closed
        Assert.assertTrue((!(channel().isOpen())));
    }
}

