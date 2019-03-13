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


import EmptyHttp2Headers.INSTANCE;
import Http2Stream.State.CLOSED;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link DefaultHttp2ConnectionDecoder}.
 */
public class DefaultHttp2ConnectionDecoderTest {
    private static final int STREAM_ID = 3;

    private static final int PUSH_STREAM_ID = 2;

    private static final int STREAM_DEPENDENCY_ID = 5;

    private static final int STATE_RECV_HEADERS = 1;

    private static final int STATE_RECV_TRAILERS = 1 << 1;

    private Http2ConnectionDecoder decoder;

    private ChannelPromise promise;

    @Mock
    private Http2Connection connection;

    @Mock
    private Http2Connection.Endpoint<Http2RemoteFlowController> remote;

    @Mock
    private Http2Connection.Endpoint<Http2LocalFlowController> local;

    @Mock
    private Http2LocalFlowController localFlow;

    @Mock
    private Http2RemoteFlowController remoteFlow;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private Channel channel;

    @Mock
    private ChannelFuture future;

    @Mock
    private Http2Stream stream;

    @Mock
    private Http2Stream pushStream;

    @Mock
    private Http2FrameListener listener;

    @Mock
    private Http2FrameReader reader;

    @Mock
    private Http2FrameWriter writer;

    @Mock
    private Http2ConnectionEncoder encoder;

    @Mock
    private Http2LifecycleManager lifecycleManager;

    @Test
    public void dataReadAfterGoAwaySentShouldApplyFlowControl() throws Exception {
        mockGoAwaySent();
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        int padding = 10;
        int processedBytes = (data.readableBytes()) + padding;
        mockFlowControl(processedBytes);
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, padding, true);
            Mockito.verify(localFlow).receiveFlowControlledFrame(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(data), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true));
            Mockito.verify(localFlow).consumeBytes(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(processedBytes));
            // Verify that the event was absorbed and not propagated to the observer.
            Mockito.verify(listener, Mockito.never()).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        } finally {
            data.release();
        }
    }

    @Test
    public void dataReadAfterGoAwaySentShouldAllowFramesForStreamCreatedByLocalEndpoint() throws Exception {
        mockGoAwaySentShouldAllowFramesForStreamCreatedByLocalEndpoint();
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        int padding = 10;
        int processedBytes = (data.readableBytes()) + padding;
        mockFlowControl(processedBytes);
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, padding, true);
            Mockito.verify(localFlow).receiveFlowControlledFrame(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(data), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true));
            Mockito.verify(localFlow).consumeBytes(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(processedBytes));
            // Verify that the event was absorbed and not propagated to the observer.
            Mockito.verify(listener).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        } finally {
            data.release();
        }
    }

    @Test(expected = Http2Exception.class)
    public void dataReadForUnknownStreamShouldApplyFlowControlAndFail() throws Exception {
        Mockito.when(connection.streamMayHaveExisted(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(false);
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        int padding = 10;
        int processedBytes = (data.readableBytes()) + padding;
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, padding, true);
        } finally {
            try {
                Mockito.verify(localFlow).receiveFlowControlledFrame(ArgumentMatchers.eq(((Http2Stream) (null))), ArgumentMatchers.eq(data), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true));
                Mockito.verify(localFlow).consumeBytes(ArgumentMatchers.eq(((Http2Stream) (null))), ArgumentMatchers.eq(processedBytes));
                Mockito.verify(localFlow).frameWriter(ArgumentMatchers.any(Http2FrameWriter.class));
                Mockito.verifyNoMoreInteractions(localFlow);
                Mockito.verify(listener, Mockito.never()).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
            } finally {
                data.release();
            }
        }
    }

    @Test
    public void dataReadForUnknownStreamShouldApplyFlowControl() throws Exception {
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        int padding = 10;
        int processedBytes = (data.readableBytes()) + padding;
        try {
            try {
                decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, padding, true);
                Assert.fail();
            } catch (Http2Exception e) {
                Mockito.verify(localFlow).receiveFlowControlledFrame(ArgumentMatchers.eq(((Http2Stream) (null))), ArgumentMatchers.eq(data), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true));
                Mockito.verify(localFlow).consumeBytes(ArgumentMatchers.eq(((Http2Stream) (null))), ArgumentMatchers.eq(processedBytes));
                Mockito.verify(localFlow).frameWriter(ArgumentMatchers.any(Http2FrameWriter.class));
                Mockito.verifyNoMoreInteractions(localFlow);
                // Verify that the event was absorbed and not propagated to the observer.
                Mockito.verify(listener, Mockito.never()).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
            }
        } finally {
            data.release();
        }
    }

    @Test
    public void emptyDataFrameShouldApplyFlowControl() throws Exception {
        final ByteBuf data = EMPTY_BUFFER;
        int padding = 0;
        mockFlowControl(0);
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, padding, true);
            Mockito.verify(localFlow).receiveFlowControlledFrame(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(data), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true));
            // Now we ignore the empty bytes inside consumeBytes method, so it will be called once.
            Mockito.verify(localFlow).consumeBytes(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(0));
            // Verify that the empty data event was propagated to the observer.
            Mockito.verify(listener).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(data), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true));
        } finally {
            data.release();
        }
    }

    @Test(expected = Http2Exception.class)
    public void dataReadForStreamInInvalidStateShouldThrow() throws Exception {
        // Throw an exception when checking stream state.
        Mockito.when(stream.state()).thenReturn(CLOSED);
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, 10, true);
        } finally {
            data.release();
        }
    }

    @Test
    public void dataReadAfterGoAwaySentForStreamInInvalidStateShouldIgnore() throws Exception {
        // Throw an exception when checking stream state.
        Mockito.when(stream.state()).thenReturn(CLOSED);
        mockGoAwaySent();
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, 10, true);
            Mockito.verify(localFlow).receiveFlowControlledFrame(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(data), ArgumentMatchers.eq(10), ArgumentMatchers.eq(true));
            Mockito.verify(listener, Mockito.never()).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        } finally {
            data.release();
        }
    }

    @Test
    public void dataReadAfterGoAwaySentOnUnknownStreamShouldIgnore() throws Exception {
        // Throw an exception when checking stream state.
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        mockGoAwaySent();
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, 10, true);
            Mockito.verify(localFlow).receiveFlowControlledFrame(((Http2Stream) (ArgumentMatchers.isNull())), ArgumentMatchers.eq(data), ArgumentMatchers.eq(10), ArgumentMatchers.eq(true));
            Mockito.verify(listener, Mockito.never()).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        } finally {
            data.release();
        }
    }

    @Test
    public void dataReadAfterRstStreamForStreamInInvalidStateShouldIgnore() throws Exception {
        // Throw an exception when checking stream state.
        Mockito.when(stream.state()).thenReturn(CLOSED);
        Mockito.when(stream.isResetSent()).thenReturn(true);
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, 10, true);
            Mockito.verify(localFlow).receiveFlowControlledFrame(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(data), ArgumentMatchers.eq(10), ArgumentMatchers.eq(true));
            Mockito.verify(listener, Mockito.never()).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        } finally {
            data.release();
        }
    }

    @Test
    public void dataReadWithEndOfStreamShouldcloseStreamRemote() throws Exception {
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, 10, true);
            Mockito.verify(localFlow).receiveFlowControlledFrame(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(data), ArgumentMatchers.eq(10), ArgumentMatchers.eq(true));
            Mockito.verify(lifecycleManager).closeStreamRemote(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(future));
            Mockito.verify(listener).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(data), ArgumentMatchers.eq(10), ArgumentMatchers.eq(true));
        } finally {
            data.release();
        }
    }

    @Test
    public void errorDuringDeliveryShouldReturnCorrectNumberOfBytes() throws Exception {
        final ByteBuf data = DefaultHttp2ConnectionDecoderTest.dummyData();
        final int padding = 10;
        final AtomicInteger unprocessed = new AtomicInteger(((data.readableBytes()) + padding));
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock in) throws Throwable {
                return unprocessed.get();
            }
        }).when(localFlow).unconsumedBytes(ArgumentMatchers.eq(stream));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock in) throws Throwable {
                int delta = ((Integer) (in.getArguments()[1]));
                int newValue = unprocessed.addAndGet((-delta));
                if (newValue < 0) {
                    throw new RuntimeException("Returned too many bytes");
                }
                return null;
            }
        }).when(localFlow).consumeBytes(ArgumentMatchers.eq(stream), ArgumentMatchers.anyInt());
        // When the listener callback is called, process a few bytes and then throw.
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock in) throws Throwable {
                localFlow.consumeBytes(stream, 4);
                throw new RuntimeException("Fake Exception");
            }
        }).when(listener).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(10), ArgumentMatchers.eq(true));
        try {
            decode().onDataRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, data, padding, true);
            Assert.fail("Expected exception");
        } catch (RuntimeException cause) {
            Mockito.verify(localFlow).receiveFlowControlledFrame(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(data), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true));
            Mockito.verify(lifecycleManager).closeStreamRemote(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(future));
            Mockito.verify(listener).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(data), ArgumentMatchers.eq(padding), ArgumentMatchers.eq(true));
            Assert.assertEquals(0, localFlow.unconsumedBytes(stream));
        } finally {
            data.release();
        }
    }

    @Test(expected = Http2Exception.class)
    public void headersReadForUnknownStreamShouldThrow() throws Exception {
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        decode().onHeadersRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, INSTANCE, 0, false);
    }

    @Test
    public void headersReadForStreamThatAlreadySentResetShouldBeIgnored() throws Exception {
        Mockito.when(stream.isResetSent()).thenReturn(true);
        decode().onHeadersRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, INSTANCE, 0, false);
        Mockito.verify(remote, Mockito.never()).createStream(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(stream, Mockito.never()).open(ArgumentMatchers.anyBoolean());
        // Verify that the event was absorbed and not propagated to the observer.
        Mockito.verify(listener, Mockito.never()).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(remote, Mockito.never()).createStream(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(stream, Mockito.never()).open(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void headersReadForUnknownStreamAfterGoAwayShouldBeIgnored() throws Exception {
        mockGoAwaySent();
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        decode().onHeadersRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, INSTANCE, 0, false);
        Mockito.verify(remote, Mockito.never()).createStream(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(stream, Mockito.never()).open(ArgumentMatchers.anyBoolean());
        // Verify that the event was absorbed and not propagated to the observer.
        Mockito.verify(listener, Mockito.never()).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(remote, Mockito.never()).createStream(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(stream, Mockito.never()).open(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void headersReadForUnknownStreamShouldCreateStream() throws Exception {
        final int streamId = 5;
        Mockito.when(remote.createStream(ArgumentMatchers.eq(streamId), ArgumentMatchers.anyBoolean())).thenReturn(stream);
        decode().onHeadersRead(ctx, streamId, INSTANCE, 0, false);
        Mockito.verify(remote).createStream(ArgumentMatchers.eq(streamId), ArgumentMatchers.eq(false));
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(streamId), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
    }

    @Test
    public void headersReadForUnknownStreamShouldCreateHalfClosedStream() throws Exception {
        final int streamId = 5;
        Mockito.when(remote.createStream(ArgumentMatchers.eq(streamId), ArgumentMatchers.anyBoolean())).thenReturn(stream);
        decode().onHeadersRead(ctx, streamId, INSTANCE, 0, true);
        Mockito.verify(remote).createStream(ArgumentMatchers.eq(streamId), ArgumentMatchers.eq(true));
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(streamId), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
    }

    @Test
    public void headersReadForPromisedStreamShouldHalfOpenStream() throws Exception {
        Mockito.when(stream.state()).thenReturn(State.RESERVED_REMOTE);
        decode().onHeadersRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, INSTANCE, 0, false);
        Mockito.verify(stream).open(false);
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
    }

    @Test(expected = Http2Exception.class)
    public void trailersDoNotEndStreamThrows() throws Exception {
        decode().onHeadersRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, INSTANCE, 0, false);
        // Trailers must end the stream!
        decode().onHeadersRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, INSTANCE, 0, false);
    }

    @Test(expected = Http2Exception.class)
    public void tooManyHeadersEOSThrows() throws Exception {
        tooManyHeaderThrows(true);
    }

    @Test(expected = Http2Exception.class)
    public void tooManyHeadersNoEOSThrows() throws Exception {
        tooManyHeaderThrows(false);
    }

    @Test
    public void infoHeadersAndTrailersAllowed() throws Exception {
        infoHeadersAndTrailersAllowed(true, 1);
    }

    @Test
    public void multipleInfoHeadersAndTrailersAllowed() throws Exception {
        infoHeadersAndTrailersAllowed(true, 10);
    }

    @Test(expected = Http2Exception.class)
    public void infoHeadersAndTrailersNoEOSThrows() throws Exception {
        infoHeadersAndTrailersAllowed(false, 1);
    }

    @Test(expected = Http2Exception.class)
    public void multipleInfoHeadersAndTrailersNoEOSThrows() throws Exception {
        infoHeadersAndTrailersAllowed(false, 10);
    }

    @Test
    public void headersReadForPromisedStreamShouldCloseStream() throws Exception {
        Mockito.when(stream.state()).thenReturn(State.RESERVED_REMOTE);
        decode().onHeadersRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, INSTANCE, 0, true);
        Mockito.verify(stream).open(true);
        Mockito.verify(lifecycleManager).closeStreamRemote(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(future));
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
    }

    @Test
    public void headersDependencyNotCreatedShouldCreateAndSucceed() throws Exception {
        final short weight = 1;
        decode().onHeadersRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, INSTANCE, DefaultHttp2ConnectionDecoderTest.STREAM_DEPENDENCY_ID, weight, true, 0, true);
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_DEPENDENCY_ID), ArgumentMatchers.eq(weight), ArgumentMatchers.eq(true), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
        Mockito.verify(remoteFlow).updateDependencyTree(ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_DEPENDENCY_ID), ArgumentMatchers.eq(weight), ArgumentMatchers.eq(true));
        Mockito.verify(lifecycleManager).closeStreamRemote(ArgumentMatchers.eq(stream), ArgumentMatchers.any(ChannelFuture.class));
    }

    @Test
    public void pushPromiseReadAfterGoAwaySentShouldBeIgnored() throws Exception {
        mockGoAwaySent();
        decode().onPushPromiseRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, DefaultHttp2ConnectionDecoderTest.PUSH_STREAM_ID, INSTANCE, 0);
        Mockito.verify(remote, Mockito.never()).reservePushStream(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Stream.class));
        Mockito.verify(listener, Mockito.never()).onPushPromiseRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt());
    }

    @Test
    public void pushPromiseReadAfterGoAwayShouldAllowFramesForStreamCreatedByLocalEndpoint() throws Exception {
        mockGoAwaySentShouldAllowFramesForStreamCreatedByLocalEndpoint();
        decode().onPushPromiseRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, DefaultHttp2ConnectionDecoderTest.PUSH_STREAM_ID, INSTANCE, 0);
        Mockito.verify(remote).reservePushStream(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Stream.class));
        Mockito.verify(listener).onPushPromiseRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt());
    }

    @Test(expected = Http2Exception.class)
    public void pushPromiseReadForUnknownStreamShouldThrow() throws Exception {
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        decode().onPushPromiseRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, DefaultHttp2ConnectionDecoderTest.PUSH_STREAM_ID, INSTANCE, 0);
    }

    @Test
    public void pushPromiseReadShouldSucceed() throws Exception {
        decode().onPushPromiseRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, DefaultHttp2ConnectionDecoderTest.PUSH_STREAM_ID, INSTANCE, 0);
        Mockito.verify(remote).reservePushStream(ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.PUSH_STREAM_ID), ArgumentMatchers.eq(stream));
        Mockito.verify(listener).onPushPromiseRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.PUSH_STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0));
    }

    @Test
    public void priorityReadAfterGoAwaySentShouldAllowFramesForStreamCreatedByLocalEndpoint() throws Exception {
        mockGoAwaySentShouldAllowFramesForStreamCreatedByLocalEndpoint();
        decode().onPriorityRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, 0, ((short) (255)), true);
        Mockito.verify(remoteFlow).updateDependencyTree(ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (255))), ArgumentMatchers.eq(true));
        Mockito.verify(listener).onPriorityRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void priorityReadForUnknownStreamShouldNotBeIgnored() throws Exception {
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        decode().onPriorityRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, 0, ((short) (255)), true);
        Mockito.verify(remoteFlow).updateDependencyTree(ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (255))), ArgumentMatchers.eq(true));
        Mockito.verify(listener).onPriorityRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (255))), ArgumentMatchers.eq(true));
    }

    @Test
    public void priorityReadShouldNotCreateNewStream() throws Exception {
        Mockito.when(connection.streamMayHaveExisted(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(false);
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        decode().onPriorityRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, DefaultHttp2ConnectionDecoderTest.STREAM_DEPENDENCY_ID, ((short) (255)), true);
        Mockito.verify(remoteFlow).updateDependencyTree(ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_DEPENDENCY_ID), ArgumentMatchers.eq(((short) (255))), ArgumentMatchers.eq(true));
        Mockito.verify(listener).onPriorityRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_DEPENDENCY_ID), ArgumentMatchers.eq(((short) (255))), ArgumentMatchers.eq(true));
        Mockito.verify(remote, Mockito.never()).createStream(ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.anyBoolean());
        Mockito.verify(stream, Mockito.never()).open(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void windowUpdateReadAfterGoAwaySentShouldBeIgnored() throws Exception {
        mockGoAwaySent();
        decode().onWindowUpdateRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, 10);
        Mockito.verify(remoteFlow, Mockito.never()).incrementWindowSize(ArgumentMatchers.any(Http2Stream.class), ArgumentMatchers.anyInt());
        Mockito.verify(listener, Mockito.never()).onWindowUpdateRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void windowUpdateReadAfterGoAwaySentShouldAllowFramesForStreamCreatedByLocalEndpoint() throws Exception {
        mockGoAwaySentShouldAllowFramesForStreamCreatedByLocalEndpoint();
        decode().onWindowUpdateRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, 10);
        Mockito.verify(remoteFlow).incrementWindowSize(ArgumentMatchers.any(Http2Stream.class), ArgumentMatchers.anyInt());
        Mockito.verify(listener).onWindowUpdateRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test(expected = Http2Exception.class)
    public void windowUpdateReadForUnknownStreamShouldThrow() throws Exception {
        Mockito.when(connection.streamMayHaveExisted(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(false);
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        decode().onWindowUpdateRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, 10);
    }

    @Test
    public void windowUpdateReadForUnknownStreamShouldBeIgnored() throws Exception {
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        decode().onWindowUpdateRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, 10);
        Mockito.verify(remoteFlow, Mockito.never()).incrementWindowSize(ArgumentMatchers.any(Http2Stream.class), ArgumentMatchers.anyInt());
        Mockito.verify(listener, Mockito.never()).onWindowUpdateRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void windowUpdateReadShouldSucceed() throws Exception {
        decode().onWindowUpdateRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, 10);
        Mockito.verify(remoteFlow).incrementWindowSize(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(10));
        Mockito.verify(listener).onWindowUpdateRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(10));
    }

    @Test
    public void rstStreamReadAfterGoAwayShouldSucceed() throws Exception {
        Mockito.when(connection.goAwaySent()).thenReturn(true);
        decode().onRstStreamRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code());
        Mockito.verify(lifecycleManager).closeStream(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(future));
        Mockito.verify(listener).onRstStreamRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
    }

    @Test(expected = Http2Exception.class)
    public void rstStreamReadForUnknownStreamShouldThrow() throws Exception {
        Mockito.when(connection.streamMayHaveExisted(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(false);
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        decode().onRstStreamRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code());
    }

    @Test
    public void rstStreamReadForUnknownStreamShouldBeIgnored() throws Exception {
        Mockito.when(connection.stream(DefaultHttp2ConnectionDecoderTest.STREAM_ID)).thenReturn(null);
        decode().onRstStreamRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code());
        Mockito.verify(lifecycleManager, Mockito.never()).closeStream(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(future));
        Mockito.verify(listener, Mockito.never()).onRstStreamRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
    }

    @Test
    public void rstStreamReadShouldCloseStream() throws Exception {
        decode().onRstStreamRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code());
        Mockito.verify(lifecycleManager).closeStream(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(future));
        Mockito.verify(listener).onRstStreamRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionDecoderTest.STREAM_ID), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()));
    }

    @Test(expected = Http2Exception.class)
    public void rstStreamOnIdleStreamShouldThrow() throws Exception {
        Mockito.when(stream.state()).thenReturn(State.IDLE);
        decode().onRstStreamRead(ctx, DefaultHttp2ConnectionDecoderTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code());
        Mockito.verify(lifecycleManager).closeStream(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(future));
        Mockito.verify(listener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
    }

    @Test
    public void pingReadWithAckShouldNotifyListener() throws Exception {
        decode().onPingAckRead(ctx, 0L);
        Mockito.verify(listener).onPingAckRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(0L));
    }

    @Test
    public void pingReadShouldReplyWithAck() throws Exception {
        decode().onPingRead(ctx, 0L);
        Mockito.verify(encoder).writePing(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(true), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(promise));
        Mockito.verify(listener, Mockito.never()).onPingAckRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.any(long.class));
    }

    @Test
    public void settingsReadWithAckShouldNotifyListener() throws Exception {
        decode().onSettingsAckRead(ctx);
        // Take into account the time this was called during setup().
        Mockito.verify(listener, Mockito.times(2)).onSettingsAckRead(ArgumentMatchers.eq(ctx));
    }

    @Test
    public void settingsReadShouldSetValues() throws Exception {
        Http2Settings settings = new Http2Settings();
        settings.pushEnabled(true);
        settings.initialWindowSize(123);
        settings.maxConcurrentStreams(456);
        settings.headerTableSize(789);
        decode().onSettingsRead(ctx, settings);
        Mockito.verify(encoder).remoteSettings(settings);
        Mockito.verify(listener).onSettingsRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(settings));
    }

    @Test
    public void goAwayShouldReadShouldUpdateConnectionState() throws Exception {
        decode().onGoAwayRead(ctx, 1, 2L, EMPTY_BUFFER);
        Mockito.verify(connection).goAwayReceived(ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L), ArgumentMatchers.eq(EMPTY_BUFFER));
        Mockito.verify(listener).onGoAwayRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(1), ArgumentMatchers.eq(2L), ArgumentMatchers.eq(EMPTY_BUFFER));
    }
}

