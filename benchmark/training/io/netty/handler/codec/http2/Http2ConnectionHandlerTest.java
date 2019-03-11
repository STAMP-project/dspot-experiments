/**
 * Copyright 2014 The Netty Project
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


import Http2Error.INTERNAL_ERROR;
import Http2Error.PROTOCOL_ERROR;
import HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2CodecUtil.SimpleChannelPromiseAggregator;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static Http2ConnectionPrefaceAndSettingsFrameWrittenEvent.INSTANCE;


/**
 * Tests for {@link Http2ConnectionHandler}
 */
public class Http2ConnectionHandlerTest {
    private static final int STREAM_ID = 1;

    private static final int NON_EXISTANT_STREAM_ID = 13;

    private Http2ConnectionHandler handler;

    private ChannelPromise promise;

    private ChannelPromise voidPromise;

    @Mock
    private Http2Connection connection;

    @Mock
    private Http2RemoteFlowController remoteFlow;

    @Mock
    private Http2LocalFlowController localFlow;

    @Mock
    private Http2Connection.Endpoint<Http2RemoteFlowController> remote;

    @Mock
    private Http2RemoteFlowController remoteFlowController;

    @Mock
    private Http2Connection.Endpoint<Http2LocalFlowController> local;

    @Mock
    private Http2LocalFlowController localFlowController;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private EventExecutor executor;

    @Mock
    private Channel channel;

    @Mock
    private ChannelPipeline pipeline;

    @Mock
    private ChannelFuture future;

    @Mock
    private Http2Stream stream;

    @Mock
    private Http2ConnectionDecoder decoder;

    @Mock
    private Http2ConnectionEncoder encoder;

    @Mock
    private Http2FrameWriter frameWriter;

    private String goAwayDebugCap;

    @Test
    public void onHttpServerUpgradeWithoutHandlerAdded() throws Exception {
        handler = new Http2ConnectionHandlerBuilder().frameListener(new Http2FrameAdapter()).server(true).build();
        try {
            handler.onHttpServerUpgrade(new Http2Settings());
            Assert.fail();
        } catch (Http2Exception e) {
            Assert.assertEquals(INTERNAL_ERROR, e.error());
        }
    }

    @Test
    public void onHttpClientUpgradeWithoutHandlerAdded() throws Exception {
        handler = new Http2ConnectionHandlerBuilder().frameListener(new Http2FrameAdapter()).server(false).build();
        try {
            handler.onHttpClientUpgrade();
            Assert.fail();
        } catch (Http2Exception e) {
            Assert.assertEquals(INTERNAL_ERROR, e.error());
        }
    }

    @Test
    public void clientShouldveSentPrefaceAndSettingsFrameWhenUserEventIsTriggered() throws Exception {
        Mockito.when(connection.isServer()).thenReturn(false);
        Mockito.when(channel.isActive()).thenReturn(false);
        handler = newHandler();
        Mockito.when(channel.isActive()).thenReturn(true);
        final Http2ConnectionPrefaceAndSettingsFrameWrittenEvent evt = INSTANCE;
        final AtomicBoolean verified = new AtomicBoolean(false);
        final Answer verifier = new Answer() {
            @Override
            public Object answer(final InvocationOnMock in) throws Throwable {
                Assert.assertTrue(in.getArgument(0).equals(evt));// sanity check...

                Mockito.verify(ctx).write(ArgumentMatchers.eq(Http2CodecUtil.connectionPrefaceBuf()));
                Mockito.verify(encoder).writeSettings(ArgumentMatchers.eq(ctx), ArgumentMatchers.any(Http2Settings.class), ArgumentMatchers.any(ChannelPromise.class));
                verified.set(true);
                return null;
            }
        };
        Mockito.doAnswer(verifier).when(ctx).fireUserEventTriggered(evt);
        handler.channelActive(ctx);
        Assert.assertTrue(verified.get());
    }

    @Test
    public void clientShouldSendClientPrefaceStringWhenActive() throws Exception {
        Mockito.when(connection.isServer()).thenReturn(false);
        Mockito.when(channel.isActive()).thenReturn(false);
        handler = newHandler();
        Mockito.when(channel.isActive()).thenReturn(true);
        handler.channelActive(ctx);
        Mockito.verify(ctx).write(ArgumentMatchers.eq(Http2CodecUtil.connectionPrefaceBuf()));
    }

    @Test
    public void serverShouldNotSendClientPrefaceStringWhenActive() throws Exception {
        Mockito.when(connection.isServer()).thenReturn(true);
        Mockito.when(channel.isActive()).thenReturn(false);
        handler = newHandler();
        Mockito.when(channel.isActive()).thenReturn(true);
        handler.channelActive(ctx);
        Mockito.verify(ctx, Mockito.never()).write(ArgumentMatchers.eq(Http2CodecUtil.connectionPrefaceBuf()));
    }

    @Test
    public void serverReceivingInvalidClientPrefaceStringShouldHandleException() throws Exception {
        Mockito.when(connection.isServer()).thenReturn(true);
        handler = newHandler();
        handler.channelRead(ctx, copiedBuffer("BAD_PREFACE", UTF_8));
        ArgumentCaptor<ByteBuf> captor = ArgumentCaptor.forClass(ByteBuf.class);
        Mockito.verify(frameWriter).writeGoAway(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()), captor.capture(), ArgumentMatchers.eq(promise));
        Assert.assertEquals(0, captor.getValue().refCnt());
    }

    @Test
    public void serverReceivingHttp1ClientPrefaceStringShouldIncludePreface() throws Exception {
        Mockito.when(connection.isServer()).thenReturn(true);
        handler = newHandler();
        handler.channelRead(ctx, copiedBuffer("GET /path HTTP/1.1", US_ASCII));
        ArgumentCaptor<ByteBuf> captor = ArgumentCaptor.forClass(ByteBuf.class);
        Mockito.verify(frameWriter).writeGoAway(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()), captor.capture(), ArgumentMatchers.eq(promise));
        Assert.assertEquals(0, captor.getValue().refCnt());
        Assert.assertTrue(goAwayDebugCap.contains("/path"));
    }

    @Test
    public void serverReceivingClientPrefaceStringFollowedByNonSettingsShouldHandleException() throws Exception {
        Mockito.when(connection.isServer()).thenReturn(true);
        handler = newHandler();
        // Create a connection preface followed by a bunch of zeros (i.e. not a settings frame).
        ByteBuf buf = Unpooled.buffer().writeBytes(Http2CodecUtil.connectionPrefaceBuf()).writeZero(10);
        handler.channelRead(ctx, buf);
        ArgumentCaptor<ByteBuf> captor = ArgumentCaptor.forClass(ByteBuf.class);
        Mockito.verify(frameWriter, Mockito.atLeastOnce()).writeGoAway(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()), captor.capture(), ArgumentMatchers.eq(promise));
        Assert.assertEquals(0, captor.getValue().refCnt());
    }

    @Test
    public void serverReceivingValidClientPrefaceStringShouldContinueReadingFrames() throws Exception {
        Mockito.when(connection.isServer()).thenReturn(true);
        handler = newHandler();
        ByteBuf prefacePlusSome = Http2ConnectionHandlerTest.addSettingsHeader(Unpooled.buffer().writeBytes(Http2CodecUtil.connectionPrefaceBuf()));
        handler.channelRead(ctx, prefacePlusSome);
        Mockito.verify(decoder, Mockito.atLeastOnce()).decodeFrame(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.<List<Object>>any());
    }

    @Test
    public void verifyChannelHandlerCanBeReusedInPipeline() throws Exception {
        Mockito.when(connection.isServer()).thenReturn(true);
        handler = newHandler();
        // Only read the connection preface...after preface is read internal state of Http2ConnectionHandler
        // is expected to change relative to the pipeline.
        ByteBuf preface = Http2CodecUtil.connectionPrefaceBuf();
        handler.channelRead(ctx, preface);
        Mockito.verify(decoder, Mockito.never()).decodeFrame(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.<List<Object>>any());
        // Now remove and add the handler...this is setting up the test condition.
        handler.handlerRemoved(ctx);
        handler.handlerAdded(ctx);
        // Now verify we can continue as normal, reading connection preface plus more.
        ByteBuf prefacePlusSome = Http2ConnectionHandlerTest.addSettingsHeader(Unpooled.buffer().writeBytes(Http2CodecUtil.connectionPrefaceBuf()));
        handler.channelRead(ctx, prefacePlusSome);
        Mockito.verify(decoder, Mockito.atLeastOnce()).decodeFrame(ArgumentMatchers.eq(ctx), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.<List<Object>>any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void channelInactiveShouldCloseStreams() throws Exception {
        handler = newHandler();
        handler.channelInactive(ctx);
        Mockito.verify(connection).close(ArgumentMatchers.any(Promise.class));
    }

    @Test
    public void connectionErrorShouldStartShutdown() throws Exception {
        handler = newHandler();
        Http2Exception e = new Http2Exception(Http2Error.PROTOCOL_ERROR);
        Mockito.when(remote.lastStreamCreated()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        handler.exceptionCaught(ctx, e);
        ArgumentCaptor<ByteBuf> captor = ArgumentCaptor.forClass(ByteBuf.class);
        Mockito.verify(frameWriter).writeGoAway(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()), captor.capture(), ArgumentMatchers.eq(promise));
        captor.getValue().release();
    }

    @Test
    public void serverShouldSend431OnHeaderSizeErrorWhenDecodingInitialHeaders() throws Exception {
        int padding = 0;
        handler = newHandler();
        Http2Exception e = new Http2Exception.HeaderListSizeException(Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR, "Header size exceeded max allowed size 8196", true);
        Mockito.when(stream.id()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(connection.isServer()).thenReturn(true);
        Mockito.when(stream.isHeadersSent()).thenReturn(false);
        Mockito.when(remote.lastStreamCreated()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(frameWriter.writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()), ArgumentMatchers.eq(promise))).thenReturn(future);
        handler.exceptionCaught(ctx, e);
        ArgumentCaptor<Http2Headers> captor = ArgumentCaptor.forClass(Http2Headers.class);
        Mockito.verify(encoder).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), captor.capture(), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise));
        Http2Headers headers = captor.getValue();
        Assert.assertEquals(REQUEST_HEADER_FIELDS_TOO_LARGE.codeAsText(), headers.status());
        Mockito.verify(frameWriter).writeRstStream(ctx, Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code(), promise);
    }

    @Test
    public void serverShouldNeverSend431HeaderSizeErrorWhenEncoding() throws Exception {
        int padding = 0;
        handler = newHandler();
        Http2Exception e = new Http2Exception.HeaderListSizeException(Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR, "Header size exceeded max allowed size 8196", false);
        Mockito.when(stream.id()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(connection.isServer()).thenReturn(true);
        Mockito.when(stream.isHeadersSent()).thenReturn(false);
        Mockito.when(remote.lastStreamCreated()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(frameWriter.writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()), ArgumentMatchers.eq(promise))).thenReturn(future);
        handler.exceptionCaught(ctx, e);
        Mockito.verify(encoder, Mockito.never()).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise));
        Mockito.verify(frameWriter).writeRstStream(ctx, Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code(), promise);
    }

    @Test
    public void clientShouldNeverSend431WhenHeadersAreTooLarge() throws Exception {
        int padding = 0;
        handler = newHandler();
        Http2Exception e = new Http2Exception.HeaderListSizeException(Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR, "Header size exceeded max allowed size 8196", true);
        Mockito.when(stream.id()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(connection.isServer()).thenReturn(false);
        Mockito.when(stream.isHeadersSent()).thenReturn(false);
        Mockito.when(remote.lastStreamCreated()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(frameWriter.writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()), ArgumentMatchers.eq(promise))).thenReturn(future);
        handler.exceptionCaught(ctx, e);
        Mockito.verify(encoder, Mockito.never()).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise));
        Mockito.verify(frameWriter).writeRstStream(ctx, Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code(), promise);
    }

    @Test
    public void prefaceUserEventProcessed() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        handler = new Http2ConnectionHandler(decoder, encoder, new Http2Settings()) {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                if (evt == (Http2ConnectionPrefaceAndSettingsFrameWrittenEvent.INSTANCE)) {
                    latch.countDown();
                }
            }
        };
        handler.handlerAdded(ctx);
        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void serverShouldNeverSend431IfHeadersAlreadySent() throws Exception {
        int padding = 0;
        handler = newHandler();
        Http2Exception e = new Http2Exception.HeaderListSizeException(Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR, "Header size exceeded max allowed size 8196", true);
        Mockito.when(stream.id()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(connection.isServer()).thenReturn(true);
        Mockito.when(stream.isHeadersSent()).thenReturn(true);
        Mockito.when(remote.lastStreamCreated()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(frameWriter.writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()), ArgumentMatchers.eq(promise))).thenReturn(future);
        handler.exceptionCaught(ctx, e);
        Mockito.verify(encoder, Mockito.never()).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise));
        Mockito.verify(frameWriter).writeRstStream(ctx, Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code(), promise);
    }

    @Test
    public void serverShouldCreateStreamIfNeededBeforeSending431() throws Exception {
        int padding = 0;
        handler = newHandler();
        Http2Exception e = new Http2Exception.HeaderListSizeException(Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR, "Header size exceeded max allowed size 8196", true);
        Mockito.when(connection.stream(Http2ConnectionHandlerTest.STREAM_ID)).thenReturn(null);
        Mockito.when(remote.createStream(Http2ConnectionHandlerTest.STREAM_ID, true)).thenReturn(stream);
        Mockito.when(stream.id()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(connection.isServer()).thenReturn(true);
        Mockito.when(stream.isHeadersSent()).thenReturn(false);
        Mockito.when(remote.lastStreamCreated()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(frameWriter.writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(PROTOCOL_ERROR.code()), ArgumentMatchers.eq(promise))).thenReturn(future);
        handler.exceptionCaught(ctx, e);
        Mockito.verify(remote).createStream(Http2ConnectionHandlerTest.STREAM_ID, true);
        Mockito.verify(encoder).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise));
        Mockito.verify(frameWriter).writeRstStream(ctx, Http2ConnectionHandlerTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code(), promise);
    }

    @Test
    public void encoderAndDecoderAreClosedOnChannelInactive() throws Exception {
        handler = newHandler();
        handler.channelActive(ctx);
        Mockito.when(channel.isActive()).thenReturn(false);
        handler.channelInactive(ctx);
        Mockito.verify(encoder).close();
        Mockito.verify(decoder).close();
    }

    @Test
    public void writeRstOnNonExistantStreamShouldSucceed() throws Exception {
        handler = newHandler();
        Mockito.when(frameWriter.writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.NON_EXISTANT_STREAM_ID), ArgumentMatchers.eq(Http2Error.STREAM_CLOSED.code()), ArgumentMatchers.eq(promise))).thenReturn(future);
        handler.resetStream(ctx, Http2ConnectionHandlerTest.NON_EXISTANT_STREAM_ID, Http2Error.STREAM_CLOSED.code(), promise);
        Mockito.verify(frameWriter).writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.NON_EXISTANT_STREAM_ID), ArgumentMatchers.eq(Http2Error.STREAM_CLOSED.code()), ArgumentMatchers.eq(promise));
    }

    @Test
    public void writeRstOnClosedStreamShouldSucceed() throws Exception {
        handler = newHandler();
        Mockito.when(stream.id()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.when(frameWriter.writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ChannelPromise.class))).thenReturn(future);
        Mockito.when(stream.state()).thenReturn(State.CLOSED);
        Mockito.when(stream.isHeadersSent()).thenReturn(true);
        // The stream is "closed" but is still known about by the connection (connection().stream(..)
        // will return the stream). We should still write a RST_STREAM frame in this scenario.
        handler.resetStream(ctx, Http2ConnectionHandlerTest.STREAM_ID, Http2Error.STREAM_CLOSED.code(), promise);
        Mockito.verify(frameWriter).writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void writeRstOnIdleStreamShouldNotWriteButStillSucceed() throws Exception {
        handler = newHandler();
        Mockito.when(stream.state()).thenReturn(State.IDLE);
        handler.resetStream(ctx, Http2ConnectionHandlerTest.STREAM_ID, Http2Error.STREAM_CLOSED.code(), promise);
        Mockito.verify(frameWriter, Mockito.never()).writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ChannelPromise.class));
        Mockito.verify(stream).close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void closeListenerShouldBeNotifiedOnlyOneTime() throws Exception {
        handler = newHandler();
        Mockito.when(future.isDone()).thenReturn(true);
        Mockito.when(future.isSuccess()).thenReturn(true);
        Mockito.doAnswer(new Answer<ChannelFuture>() {
            @Override
            public ChannelFuture answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GenericFutureListener<ChannelFuture> listener = ((GenericFutureListener<ChannelFuture>) (args[0]));
                // Simulate that all streams have become inactive by the time the future completes.
                Mockito.doAnswer(new Answer<Http2Stream>() {
                    @Override
                    public Http2Stream answer(InvocationOnMock in) throws Throwable {
                        return null;
                    }
                }).when(connection).forEachActiveStream(ArgumentMatchers.any(Http2StreamVisitor.class));
                Mockito.when(connection.numActiveStreams()).thenReturn(0);
                // Simulate the future being completed.
                listener.operationComplete(future);
                return future;
            }
        }).when(future).addListener(ArgumentMatchers.any(GenericFutureListener.class));
        handler.close(ctx, promise);
        if (future.isDone()) {
            Mockito.when(connection.numActiveStreams()).thenReturn(0);
        }
        handler.closeStream(stream, future);
        // Simulate another stream close call being made after the context should already be closed.
        handler.closeStream(stream, future);
        Mockito.verify(ctx, Mockito.times(1)).close(ArgumentMatchers.any(ChannelPromise.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canSendGoAwayFrame() throws Exception {
        ByteBuf data = Http2ConnectionHandlerTest.dummyData();
        long errorCode = INTERNAL_ERROR.code();
        Mockito.when(future.isDone()).thenReturn(true);
        Mockito.when(future.isSuccess()).thenReturn(true);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((GenericFutureListener) (invocation.getArgument(0))).operationComplete(future);
                return null;
            }
        }).when(future).addListener(ArgumentMatchers.any(GenericFutureListener.class));
        handler = newHandler();
        handler.goAway(ctx, Http2ConnectionHandlerTest.STREAM_ID, errorCode, data, promise);
        Mockito.verify(connection).goAwaySent(ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(errorCode), ArgumentMatchers.eq(data));
        Mockito.verify(frameWriter).writeGoAway(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(errorCode), ArgumentMatchers.eq(data), ArgumentMatchers.eq(promise));
        Mockito.verify(ctx).close();
        Assert.assertEquals(0, data.refCnt());
    }

    @Test
    public void canSendGoAwayFramesWithDecreasingLastStreamIds() throws Exception {
        handler = newHandler();
        ByteBuf data = Http2ConnectionHandlerTest.dummyData();
        long errorCode = INTERNAL_ERROR.code();
        handler.goAway(ctx, ((Http2ConnectionHandlerTest.STREAM_ID) + 2), errorCode, data.retain(), promise);
        Mockito.verify(frameWriter).writeGoAway(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(((Http2ConnectionHandlerTest.STREAM_ID) + 2)), ArgumentMatchers.eq(errorCode), ArgumentMatchers.eq(data), ArgumentMatchers.eq(promise));
        Mockito.verify(connection).goAwaySent(ArgumentMatchers.eq(((Http2ConnectionHandlerTest.STREAM_ID) + 2)), ArgumentMatchers.eq(errorCode), ArgumentMatchers.eq(data));
        promise = new io.netty.channel.DefaultChannelPromise(channel);
        handler.goAway(ctx, Http2ConnectionHandlerTest.STREAM_ID, errorCode, data, promise);
        Mockito.verify(frameWriter).writeGoAway(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(errorCode), ArgumentMatchers.eq(data), ArgumentMatchers.eq(promise));
        Mockito.verify(connection).goAwaySent(ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(errorCode), ArgumentMatchers.eq(data));
        Assert.assertEquals(0, data.refCnt());
    }

    @Test
    public void cannotSendGoAwayFrameWithIncreasingLastStreamIds() throws Exception {
        handler = newHandler();
        ByteBuf data = Http2ConnectionHandlerTest.dummyData();
        long errorCode = INTERNAL_ERROR.code();
        handler.goAway(ctx, Http2ConnectionHandlerTest.STREAM_ID, errorCode, data.retain(), promise);
        Mockito.verify(connection).goAwaySent(ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(errorCode), ArgumentMatchers.eq(data));
        Mockito.verify(frameWriter).writeGoAway(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2ConnectionHandlerTest.STREAM_ID), ArgumentMatchers.eq(errorCode), ArgumentMatchers.eq(data), ArgumentMatchers.eq(promise));
        // The frameWriter is only mocked, so it should not have interacted with the promise.
        Assert.assertFalse(promise.isDone());
        Mockito.when(connection.goAwaySent()).thenReturn(true);
        Mockito.when(remote.lastStreamKnownByPeer()).thenReturn(Http2ConnectionHandlerTest.STREAM_ID);
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) {
                throw new IllegalStateException();
            }
        }).when(connection).goAwaySent(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        handler.goAway(ctx, ((Http2ConnectionHandlerTest.STREAM_ID) + 2), errorCode, data, promise);
        Assert.assertTrue(promise.isDone());
        Assert.assertFalse(promise.isSuccess());
        Assert.assertEquals(0, data.refCnt());
        Mockito.verifyNoMoreInteractions(frameWriter);
    }

    @Test
    public void canSendGoAwayUsingVoidPromise() throws Exception {
        handler = newHandler();
        ByteBuf data = Http2ConnectionHandlerTest.dummyData();
        long errorCode = INTERNAL_ERROR.code();
        handler = newHandler();
        final Throwable cause = new RuntimeException("fake exception");
        Mockito.doAnswer(new Answer<ChannelFuture>() {
            @Override
            public ChannelFuture answer(InvocationOnMock invocation) throws Throwable {
                ChannelPromise promise = invocation.getArgument(4);
                Assert.assertFalse(promise.isVoid());
                // This is what DefaultHttp2FrameWriter does... I hate mocking :-(.
                SimpleChannelPromiseAggregator aggregatedPromise = new SimpleChannelPromiseAggregator(promise, channel, ImmediateEventExecutor.INSTANCE);
                aggregatedPromise.newPromise();
                aggregatedPromise.doneAllocatingPromises();
                return aggregatedPromise.setFailure(cause);
            }
        }).when(frameWriter).writeGoAway(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        handler.goAway(ctx, Http2ConnectionHandlerTest.STREAM_ID, errorCode, data, Http2TestUtil.newVoidPromise(channel));
        Mockito.verify(pipeline).fireExceptionCaught(cause);
    }

    @Test
    public void channelReadCompleteTriggersFlush() throws Exception {
        handler = newHandler();
        handler.channelReadComplete(ctx);
        Mockito.verify(ctx, Mockito.times(1)).flush();
    }

    @Test
    public void channelReadCompleteCallsReadWhenAutoReadFalse() throws Exception {
        channel.config().setAutoRead(false);
        handler = newHandler();
        handler.channelReadComplete(ctx);
        Mockito.verify(ctx, Mockito.times(1)).read();
    }

    @Test
    public void channelClosedDoesNotThrowPrefaceException() throws Exception {
        Mockito.when(connection.isServer()).thenReturn(true);
        handler = newHandler();
        Mockito.when(channel.isActive()).thenReturn(false);
        handler.channelInactive(ctx);
        Mockito.verify(frameWriter, Mockito.never()).writeGoAway(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.any(ChannelPromise.class));
        Mockito.verify(frameWriter, Mockito.never()).writeRstStream(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void writeRstStreamForUnknownStreamUsingVoidPromise() throws Exception {
        writeRstStreamUsingVoidPromise(Http2ConnectionHandlerTest.NON_EXISTANT_STREAM_ID);
    }

    @Test
    public void writeRstStreamForKnownStreamUsingVoidPromise() throws Exception {
        writeRstStreamUsingVoidPromise(Http2ConnectionHandlerTest.STREAM_ID);
    }

    @Test
    public void gracefulShutdownTimeoutTest() throws Exception {
        handler = newHandler();
        final long expectedMillis = 1234;
        handler.gracefulShutdownTimeoutMillis(expectedMillis);
        handler.close(ctx, promise);
        Mockito.verify(executor).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(expectedMillis), ArgumentMatchers.eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void gracefulShutdownIndefiniteTimeoutTest() throws Exception {
        handler = newHandler();
        handler.gracefulShutdownTimeoutMillis((-1));
        handler.close(ctx, promise);
        Mockito.verify(executor, Mockito.never()).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
    }
}

