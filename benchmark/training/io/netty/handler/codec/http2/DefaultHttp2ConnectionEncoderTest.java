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


import Channel.Unsafe;
import EmptyHttp2Headers.INSTANCE;
import Http2Error.INTERNAL_ERROR;
import Http2FrameWriter.Configuration;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2RemoteFlowController.FlowControlled;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link DefaultHttp2ConnectionEncoder}
 */
public class DefaultHttp2ConnectionEncoderTest {
    private static final int STREAM_ID = 2;

    private static final int PUSH_STREAM_ID = 4;

    @Mock
    private Http2RemoteFlowController remoteFlow;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private Channel channel;

    @Mock
    private Unsafe unsafe;

    @Mock
    private ChannelPipeline pipeline;

    @Mock
    private Http2FrameWriter writer;

    @Mock
    private Configuration writerConfig;

    @Mock
    private Http2FrameSizePolicy frameSizePolicy;

    @Mock
    private Http2LifecycleManager lifecycleManager;

    private DefaultHttp2ConnectionEncoder encoder;

    private Http2Connection connection;

    private ArgumentCaptor<Http2RemoteFlowController.FlowControlled> payloadCaptor;

    private List<String> writtenData;

    private List<Integer> writtenPadding;

    private boolean streamClosed;

    @Test
    public void dataWithEndOfStreamWriteShouldSignalThatFrameWasConsumedOnError() throws Exception {
        dataWriteShouldSignalThatFrameWasConsumedOnError0(true);
    }

    @Test
    public void dataWriteShouldSignalThatFrameWasConsumedOnError() throws Exception {
        dataWriteShouldSignalThatFrameWasConsumedOnError0(false);
    }

    @Test
    public void dataWriteShouldSucceed() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        final ByteBuf data = DefaultHttp2ConnectionEncoderTest.dummyData();
        ChannelPromise p = newPromise();
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, true, p);
        Assert.assertEquals(8, payloadCaptor.getValue().size());
        payloadCaptor.getValue().write(ctx, 8);
        Assert.assertEquals(0, payloadCaptor.getValue().size());
        Assert.assertEquals("abcdefgh", writtenData.get(0));
        Assert.assertEquals(0, data.refCnt());
        Assert.assertTrue(p.isSuccess());
    }

    @Test
    public void dataFramesShouldMerge() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        final ByteBuf data = DefaultHttp2ConnectionEncoderTest.dummyData().retain();
        ChannelPromise promise1 = newPromise();
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, true, promise1);
        ChannelPromise promise2 = newPromise();
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, true, promise2);
        // Now merge the two payloads.
        List<FlowControlled> capturedWrites = payloadCaptor.getAllValues();
        FlowControlled mergedPayload = capturedWrites.get(0);
        mergedPayload.merge(ctx, capturedWrites.get(1));
        Assert.assertEquals(16, mergedPayload.size());
        Assert.assertFalse(promise1.isDone());
        Assert.assertFalse(promise2.isDone());
        // Write the merged payloads and verify it was written correctly.
        mergedPayload.write(ctx, 16);
        Assert.assertEquals(0, mergedPayload.size());
        Assert.assertEquals("abcdefghabcdefgh", writtenData.get(0));
        Assert.assertEquals(0, data.refCnt());
        Assert.assertTrue(promise1.isSuccess());
        Assert.assertTrue(promise2.isSuccess());
    }

    @Test
    public void dataFramesShouldMergeUseVoidPromise() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        final ByteBuf data = DefaultHttp2ConnectionEncoderTest.dummyData().retain();
        ChannelPromise promise1 = Http2TestUtil.newVoidPromise(channel);
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, true, promise1);
        ChannelPromise promise2 = Http2TestUtil.newVoidPromise(channel);
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, true, promise2);
        // Now merge the two payloads.
        List<FlowControlled> capturedWrites = payloadCaptor.getAllValues();
        FlowControlled mergedPayload = capturedWrites.get(0);
        mergedPayload.merge(ctx, capturedWrites.get(1));
        Assert.assertEquals(16, mergedPayload.size());
        Assert.assertFalse(promise1.isSuccess());
        Assert.assertFalse(promise2.isSuccess());
        // Write the merged payloads and verify it was written correctly.
        mergedPayload.write(ctx, 16);
        Assert.assertEquals(0, mergedPayload.size());
        Assert.assertEquals("abcdefghabcdefgh", writtenData.get(0));
        Assert.assertEquals(0, data.refCnt());
        // The promises won't be set since there are no listeners.
        Assert.assertFalse(promise1.isSuccess());
        Assert.assertFalse(promise2.isSuccess());
    }

    @Test
    public void dataFramesDontMergeWithHeaders() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        final ByteBuf data = DefaultHttp2ConnectionEncoderTest.dummyData().retain();
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, false, newPromise());
        Mockito.when(remoteFlow.hasFlowControlled(ArgumentMatchers.any(Http2Stream.class))).thenReturn(true);
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, INSTANCE, 0, true, newPromise());
        List<FlowControlled> capturedWrites = payloadCaptor.getAllValues();
        Assert.assertFalse(capturedWrites.get(0).merge(ctx, capturedWrites.get(1)));
    }

    @Test
    public void emptyFrameShouldSplitPadding() throws Exception {
        ByteBuf data = Unpooled.buffer(0);
        assertSplitPaddingOnEmptyBuffer(data);
        Assert.assertEquals(0, data.refCnt());
    }

    @Test
    public void writeHeadersUsingVoidPromise() throws Exception {
        final Throwable cause = new RuntimeException("fake exception");
        Mockito.when(writer.writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ChannelPromise.class))).then(new Answer<ChannelFuture>() {
            @Override
            public ChannelFuture answer(InvocationOnMock invocationOnMock) throws Throwable {
                ChannelPromise promise = invocationOnMock.getArgument(8);
                Assert.assertFalse(promise.isVoid());
                return promise.setFailure(cause);
            }
        });
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        // END_STREAM flag, so that a listener is added to the future.
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, INSTANCE, 0, true, Http2TestUtil.newVoidPromise(channel));
        Mockito.verify(writer).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ChannelPromise.class));
        // When using a void promise, the error should be propagated via the channel pipeline.
        Mockito.verify(pipeline).fireExceptionCaught(cause);
    }

    @Test
    public void headersWriteForUnknownStreamShouldCreateStream() throws Exception {
        writeAllFlowControlledFrames();
        final int streamId = 6;
        ChannelPromise promise = newPromise();
        encoder.writeHeaders(ctx, streamId, INSTANCE, 0, false, promise);
        Mockito.verify(writer).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(streamId), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.eq(promise));
        Assert.assertTrue(promise.isSuccess());
    }

    @Test
    public void headersWriteShouldOpenStreamForPush() throws Exception {
        writeAllFlowControlledFrames();
        Http2Stream parent = createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        reservePushStream(DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID, parent);
        ChannelPromise promise = newPromise();
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID, INSTANCE, 0, false, promise);
        Assert.assertEquals(State.HALF_CLOSED_REMOTE, stream(DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID).state());
        Mockito.verify(writer).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.eq(promise));
    }

    @Test
    public void trailersDoNotEndStreamThrows() {
        writeAllFlowControlledFrames();
        final int streamId = 6;
        ChannelPromise promise = newPromise();
        encoder.writeHeaders(ctx, streamId, INSTANCE, 0, false, promise);
        ChannelPromise promise2 = newPromise();
        ChannelFuture future = encoder.writeHeaders(ctx, streamId, INSTANCE, 0, false, promise2);
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isSuccess());
        Mockito.verify(writer, Mockito.times(1)).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(streamId), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.eq(promise));
    }

    @Test
    public void trailersDoNotEndStreamWithDataThrows() {
        writeAllFlowControlledFrames();
        final int streamId = 6;
        ChannelPromise promise = newPromise();
        encoder.writeHeaders(ctx, streamId, INSTANCE, 0, false, promise);
        Http2Stream stream = connection.stream(streamId);
        Mockito.when(remoteFlow.hasFlowControlled(ArgumentMatchers.eq(stream))).thenReturn(true);
        ChannelPromise promise2 = newPromise();
        ChannelFuture future = encoder.writeHeaders(ctx, streamId, INSTANCE, 0, false, promise2);
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isSuccess());
        Mockito.verify(writer, Mockito.times(1)).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(streamId), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.eq(promise));
    }

    @Test
    public void tooManyHeadersNoEOSThrows() {
        tooManyHeadersThrows(false);
    }

    @Test
    public void tooManyHeadersEOSThrows() {
        tooManyHeadersThrows(true);
    }

    @Test
    public void infoHeadersAndTrailersAllowed() throws Exception {
        infoHeadersAndTrailers(true, 1);
    }

    @Test
    public void multipleInfoHeadersAndTrailersAllowed() throws Exception {
        infoHeadersAndTrailers(true, 10);
    }

    @Test
    public void infoHeadersAndTrailersNoEOSThrows() throws Exception {
        infoHeadersAndTrailers(false, 1);
    }

    @Test
    public void multipleInfoHeadersAndTrailersNoEOSThrows() throws Exception {
        infoHeadersAndTrailers(false, 10);
    }

    @Test
    public void tooManyHeadersWithDataNoEOSThrows() {
        tooManyHeadersWithDataThrows(false);
    }

    @Test
    public void tooManyHeadersWithDataEOSThrows() {
        tooManyHeadersWithDataThrows(true);
    }

    @Test
    public void infoHeadersAndTrailersWithDataAllowed() {
        infoHeadersAndTrailersWithData(true, 1);
    }

    @Test
    public void multipleInfoHeadersAndTrailersWithDataAllowed() {
        infoHeadersAndTrailersWithData(true, 10);
    }

    @Test
    public void infoHeadersAndTrailersWithDataNoEOSThrows() {
        infoHeadersAndTrailersWithData(false, 1);
    }

    @Test
    public void multipleInfoHeadersAndTrailersWithDataNoEOSThrows() {
        infoHeadersAndTrailersWithData(false, 10);
    }

    @Test
    public void pushPromiseWriteAfterGoAwayReceivedShouldFail() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        goAwayReceived(0);
        ChannelFuture future = encoder.writePushPromise(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID, INSTANCE, 0, newPromise());
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isSuccess());
    }

    @Test
    public void pushPromiseWriteShouldReserveStream() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        ChannelPromise promise = newPromise();
        encoder.writePushPromise(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID, INSTANCE, 0, promise);
        Assert.assertEquals(State.RESERVED_LOCAL, stream(DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID).state());
        Mockito.verify(writer).writePushPromise(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(promise));
    }

    @Test
    public void priorityWriteAfterGoAwayShouldSucceed() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        goAwayReceived(Integer.MAX_VALUE);
        ChannelPromise promise = newPromise();
        encoder.writePriority(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, 0, ((short) (255)), true, promise);
        Mockito.verify(writer).writePriority(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (255))), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise));
    }

    @Test
    public void priorityWriteShouldSetPriorityForStream() throws Exception {
        ChannelPromise promise = newPromise();
        short weight = 255;
        encoder.writePriority(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, 0, weight, true, promise);
        // Verify that this did NOT create a stream object.
        Http2Stream stream = stream(DefaultHttp2ConnectionEncoderTest.STREAM_ID);
        Assert.assertNull(stream);
        Mockito.verify(writer).writePriority(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (255))), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise));
    }

    @Test
    public void priorityWriteOnPreviouslyExistingStreamShouldSucceed() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false).close();
        ChannelPromise promise = newPromise();
        short weight = 255;
        encoder.writePriority(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, 0, weight, true, promise);
        Mockito.verify(writer).writePriority(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(0), ArgumentMatchers.eq(weight), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise));
    }

    @Test
    public void priorityWriteOnPreviouslyExistingParentStreamShouldSucceed() throws Exception {
        final int parentStreamId = (DefaultHttp2ConnectionEncoderTest.STREAM_ID) + 2;
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        createStream(parentStreamId, false).close();
        ChannelPromise promise = newPromise();
        short weight = 255;
        encoder.writePriority(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, parentStreamId, weight, true, promise);
        Mockito.verify(writer).writePriority(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(parentStreamId), ArgumentMatchers.eq(weight), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise));
    }

    @Test
    public void rstStreamWriteForUnknownStreamShouldIgnore() throws Exception {
        ChannelPromise promise = newPromise();
        encoder.writeRstStream(ctx, 5, Http2Error.PROTOCOL_ERROR.code(), promise);
        Mockito.verify(writer, Mockito.never()).writeRstStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(promise));
    }

    @Test
    public void rstStreamShouldCloseStream() throws Exception {
        // Create the stream and send headers.
        writeAllFlowControlledFrames();
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, INSTANCE, 0, true, newPromise());
        // Now verify that a stream reset is performed.
        stream(DefaultHttp2ConnectionEncoderTest.STREAM_ID);
        ChannelPromise promise = newPromise();
        encoder.writeRstStream(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, Http2Error.PROTOCOL_ERROR.code(), promise);
        Mockito.verify(lifecycleManager).resetStream(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(promise));
    }

    @Test
    public void pingWriteAfterGoAwayShouldSucceed() throws Exception {
        ChannelPromise promise = newPromise();
        goAwayReceived(0);
        encoder.writePing(ctx, false, 0L, promise);
        Mockito.verify(writer).writePing(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(promise));
    }

    @Test
    public void pingWriteShouldSucceed() throws Exception {
        ChannelPromise promise = newPromise();
        encoder.writePing(ctx, false, 0L, promise);
        Mockito.verify(writer).writePing(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0L), ArgumentMatchers.eq(promise));
    }

    @Test
    public void settingsWriteAfterGoAwayShouldSucceed() throws Exception {
        goAwayReceived(0);
        ChannelPromise promise = newPromise();
        encoder.writeSettings(ctx, new Http2Settings(), promise);
        Mockito.verify(writer).writeSettings(ArgumentMatchers.eq(ctx), ArgumentMatchers.any(Http2Settings.class), ArgumentMatchers.eq(promise));
    }

    @Test
    public void settingsWriteShouldNotUpdateSettings() throws Exception {
        Http2Settings settings = new Http2Settings();
        settings.initialWindowSize(100);
        settings.maxConcurrentStreams(1000);
        settings.headerTableSize(2000);
        ChannelPromise promise = newPromise();
        encoder.writeSettings(ctx, settings, promise);
        Mockito.verify(writer).writeSettings(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(settings), ArgumentMatchers.eq(promise));
    }

    @Test
    public void dataWriteShouldCreateHalfClosedStream() throws Exception {
        writeAllFlowControlledFrames();
        Http2Stream stream = createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        ByteBuf data = DefaultHttp2ConnectionEncoderTest.dummyData();
        ChannelPromise promise = newPromise();
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data.retain(), 0, true, promise);
        Assert.assertTrue(promise.isSuccess());
        Mockito.verify(remoteFlow).addFlowControlled(ArgumentMatchers.eq(stream), ArgumentMatchers.any(FlowControlled.class));
        Mockito.verify(lifecycleManager).closeStreamLocal(stream, promise);
        Assert.assertEquals(data.toString(UTF_8), writtenData.get(0));
        data.release();
    }

    @Test
    public void headersWriteShouldHalfCloseStream() throws Exception {
        writeAllFlowControlledFrames();
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        ChannelPromise promise = newPromise();
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, INSTANCE, 0, true, promise);
        Assert.assertTrue(promise.isSuccess());
        Mockito.verify(lifecycleManager).closeStreamLocal(ArgumentMatchers.eq(stream(DefaultHttp2ConnectionEncoderTest.STREAM_ID)), ArgumentMatchers.eq(promise));
    }

    @Test
    public void headersWriteShouldHalfClosePushStream() throws Exception {
        writeAllFlowControlledFrames();
        Http2Stream parent = createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        Http2Stream stream = reservePushStream(DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID, parent);
        ChannelPromise promise = newPromise();
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.PUSH_STREAM_ID, INSTANCE, 0, true, promise);
        Assert.assertEquals(State.HALF_CLOSED_REMOTE, stream.state());
        Assert.assertTrue(promise.isSuccess());
        Mockito.verify(lifecycleManager).closeStreamLocal(ArgumentMatchers.eq(stream), ArgumentMatchers.eq(promise));
    }

    @Test
    public void headersWriteShouldHalfCloseAfterOnErrorForPreCreatedStream() throws Exception {
        final ChannelPromise promise = newPromise();
        final Throwable ex = new RuntimeException();
        // Fake an encoding error, like HPACK's HeaderListSizeException
        Mockito.when(writer.writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise))).thenAnswer(new Answer<ChannelFuture>() {
            @Override
            public ChannelFuture answer(InvocationOnMock invocation) {
                promise.setFailure(ex);
                return promise;
            }
        });
        writeAllFlowControlledFrames();
        Http2Stream stream = createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, INSTANCE, 0, true, promise);
        Assert.assertTrue(promise.isDone());
        Assert.assertFalse(promise.isSuccess());
        Assert.assertFalse(stream.isHeadersSent());
        InOrder inOrder = Mockito.inOrder(lifecycleManager);
        inOrder.verify(lifecycleManager).onError(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(true), ArgumentMatchers.eq(ex));
        inOrder.verify(lifecycleManager).closeStreamLocal(ArgumentMatchers.eq(stream(DefaultHttp2ConnectionEncoderTest.STREAM_ID)), ArgumentMatchers.eq(promise));
    }

    @Test
    public void headersWriteShouldHalfCloseAfterOnErrorForImplicitlyCreatedStream() throws Exception {
        final ChannelPromise promise = newPromise();
        final Throwable ex = new RuntimeException();
        // Fake an encoding error, like HPACK's HeaderListSizeException
        Mockito.when(writer.writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true), ArgumentMatchers.eq(promise))).thenAnswer(new Answer<ChannelFuture>() {
            @Override
            public ChannelFuture answer(InvocationOnMock invocation) {
                promise.setFailure(ex);
                return promise;
            }
        });
        writeAllFlowControlledFrames();
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, INSTANCE, 0, true, promise);
        Assert.assertTrue(promise.isDone());
        Assert.assertFalse(promise.isSuccess());
        Assert.assertFalse(stream(DefaultHttp2ConnectionEncoderTest.STREAM_ID).isHeadersSent());
        InOrder inOrder = Mockito.inOrder(lifecycleManager);
        inOrder.verify(lifecycleManager).onError(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(true), ArgumentMatchers.eq(ex));
        inOrder.verify(lifecycleManager).closeStreamLocal(ArgumentMatchers.eq(stream(DefaultHttp2ConnectionEncoderTest.STREAM_ID)), ArgumentMatchers.eq(promise));
    }

    @Test
    public void encoderDelegatesGoAwayToLifeCycleManager() {
        ChannelPromise promise = newPromise();
        encoder.writeGoAway(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, INTERNAL_ERROR.code(), null, promise);
        Mockito.verify(lifecycleManager).goAway(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(INTERNAL_ERROR.code()), ArgumentMatchers.eq(((ByteBuf) (null))), ArgumentMatchers.eq(promise));
        Mockito.verifyNoMoreInteractions(writer);
    }

    @Test
    public void dataWriteToClosedStreamShouldFail() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false).close();
        ByteBuf data = Mockito.mock(ByteBuf.class);
        ChannelPromise promise = newPromise();
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, false, promise);
        Assert.assertTrue(promise.isDone());
        Assert.assertFalse(promise.isSuccess());
        Assert.assertThat(promise.cause(), CoreMatchers.instanceOf(IllegalArgumentException.class));
        Mockito.verify(data).release();
    }

    @Test
    public void dataWriteToHalfClosedLocalStreamShouldFail() throws Exception {
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, true);
        ByteBuf data = Mockito.mock(ByteBuf.class);
        ChannelPromise promise = newPromise();
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, false, promise);
        Assert.assertTrue(promise.isDone());
        Assert.assertFalse(promise.isSuccess());
        Assert.assertThat(promise.cause(), CoreMatchers.instanceOf(IllegalStateException.class));
        Mockito.verify(data).release();
    }

    @Test
    public void canWriteDataFrameAfterGoAwaySent() throws Exception {
        Http2Stream stream = createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        connection.goAwaySent(0, 0, EMPTY_BUFFER);
        ByteBuf data = Mockito.mock(ByteBuf.class);
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, false, newPromise());
        Mockito.verify(remoteFlow).addFlowControlled(ArgumentMatchers.eq(stream), ArgumentMatchers.any(FlowControlled.class));
    }

    @Test
    public void canWriteHeaderFrameAfterGoAwaySent() throws Exception {
        writeAllFlowControlledFrames();
        createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        goAwaySent(0);
        ChannelPromise promise = newPromise();
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, INSTANCE, 0, false, promise);
        Mockito.verify(writer).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.eq(promise));
    }

    @Test
    public void canWriteDataFrameAfterGoAwayReceived() throws Exception {
        Http2Stream stream = createStream(DefaultHttp2ConnectionEncoderTest.STREAM_ID, false);
        goAwayReceived(DefaultHttp2ConnectionEncoderTest.STREAM_ID);
        ByteBuf data = Mockito.mock(ByteBuf.class);
        encoder.writeData(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, data, 0, false, newPromise());
        Mockito.verify(remoteFlow).addFlowControlled(ArgumentMatchers.eq(stream), ArgumentMatchers.any(FlowControlled.class));
    }

    @Test
    public void canWriteHeaderFrameAfterGoAwayReceived() throws Http2Exception {
        writeAllFlowControlledFrames();
        goAwayReceived(DefaultHttp2ConnectionEncoderTest.STREAM_ID);
        ChannelPromise promise = newPromise();
        encoder.writeHeaders(ctx, DefaultHttp2ConnectionEncoderTest.STREAM_ID, INSTANCE, 0, false, promise);
        Mockito.verify(writer).writeHeaders(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(DefaultHttp2ConnectionEncoderTest.STREAM_ID), ArgumentMatchers.eq(INSTANCE), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.eq(promise));
    }
}

