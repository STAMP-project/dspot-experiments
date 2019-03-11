/**
 * Copyright 2015 The Netty Project
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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link StreamBufferingEncoder}.
 */
public class StreamBufferingEncoderTest {
    private StreamBufferingEncoder encoder;

    private Http2Connection connection;

    @Mock
    private Http2FrameWriter writer;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private Channel channel;

    @Mock
    private Unsafe unsafe;

    @Mock
    private ChannelConfig config;

    @Mock
    private EventExecutor executor;

    @Test
    public void multipleWritesToActiveStream() {
        encoder.writeSettingsAck(ctx, newPromise());
        encoderWriteHeaders(3, newPromise());
        Assert.assertEquals(0, encoder.numBufferedStreams());
        ByteBuf data = StreamBufferingEncoderTest.data();
        final int expectedBytes = (data.readableBytes()) * 3;
        encoder.writeData(ctx, 3, data, 0, false, newPromise());
        encoder.writeData(ctx, 3, StreamBufferingEncoderTest.data(), 0, false, newPromise());
        encoder.writeData(ctx, 3, StreamBufferingEncoderTest.data(), 0, false, newPromise());
        encoderWriteHeaders(3, newPromise());
        writeVerifyWriteHeaders(Mockito.times(2), 3);
        // Contiguous data writes are coalesced
        ArgumentCaptor<ByteBuf> bufCaptor = ArgumentCaptor.forClass(ByteBuf.class);
        Mockito.verify(writer, Mockito.times(1)).writeData(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(3), bufCaptor.capture(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.any(ChannelPromise.class));
        Assert.assertEquals(expectedBytes, bufCaptor.getValue().readableBytes());
    }

    @Test
    public void ensureCanCreateNextStreamWhenStreamCloses() {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(1);
        encoderWriteHeaders(3, newPromise());
        Assert.assertEquals(0, encoder.numBufferedStreams());
        // This one gets buffered.
        encoderWriteHeaders(5, newPromise());
        Assert.assertEquals(1, connection.numActiveStreams());
        Assert.assertEquals(1, encoder.numBufferedStreams());
        // Now prevent us from creating another stream.
        setMaxConcurrentStreams(0);
        // Close the previous stream.
        connection.stream(3).close();
        // Ensure that no streams are currently active and that only the HEADERS from the first
        // stream were written.
        writeVerifyWriteHeaders(Mockito.times(1), 3);
        writeVerifyWriteHeaders(Mockito.never(), 5);
        Assert.assertEquals(0, connection.numActiveStreams());
        Assert.assertEquals(1, encoder.numBufferedStreams());
    }

    @Test
    public void alternatingWritesToActiveAndBufferedStreams() {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(1);
        encoderWriteHeaders(3, newPromise());
        Assert.assertEquals(0, encoder.numBufferedStreams());
        encoderWriteHeaders(5, newPromise());
        Assert.assertEquals(1, connection.numActiveStreams());
        Assert.assertEquals(1, encoder.numBufferedStreams());
        encoder.writeData(ctx, 3, EMPTY_BUFFER, 0, false, newPromise());
        writeVerifyWriteHeaders(Mockito.times(1), 3);
        encoder.writeData(ctx, 5, EMPTY_BUFFER, 0, false, newPromise());
        Mockito.verify(writer, Mockito.never()).writeData(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(5), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false), ArgumentMatchers.eq(newPromise()));
    }

    @Test
    public void bufferingNewStreamFailsAfterGoAwayReceived() throws Http2Exception {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(0);
        connection.goAwayReceived(1, 8, EMPTY_BUFFER);
        ChannelPromise promise = newPromise();
        encoderWriteHeaders(3, promise);
        Assert.assertEquals(0, encoder.numBufferedStreams());
        Assert.assertTrue(promise.isDone());
        Assert.assertFalse(promise.isSuccess());
    }

    @Test
    public void receivingGoAwayFailsBufferedStreams() throws Http2Exception {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(5);
        int streamId = 3;
        List<ChannelFuture> futures = new ArrayList<ChannelFuture>();
        for (int i = 0; i < 9; i++) {
            futures.add(encoderWriteHeaders(streamId, newPromise()));
            streamId += 2;
        }
        Assert.assertEquals(4, encoder.numBufferedStreams());
        connection.goAwayReceived(11, 8, EMPTY_BUFFER);
        Assert.assertEquals(5, connection.numActiveStreams());
        int failCount = 0;
        for (ChannelFuture f : futures) {
            if ((f.cause()) != null) {
                failCount++;
            }
        }
        Assert.assertEquals(9, failCount);
        Assert.assertEquals(0, encoder.numBufferedStreams());
    }

    @Test
    public void sendingGoAwayShouldNotFailStreams() {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(1);
        Mockito.when(writer.writeHeaders(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ChannelPromise.class))).thenAnswer(successAnswer());
        Mockito.when(writer.writeHeaders(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ChannelPromise.class))).thenAnswer(successAnswer());
        ChannelFuture f1 = encoderWriteHeaders(3, newPromise());
        Assert.assertEquals(0, encoder.numBufferedStreams());
        ChannelFuture f2 = encoderWriteHeaders(5, newPromise());
        Assert.assertEquals(1, encoder.numBufferedStreams());
        ChannelFuture f3 = encoderWriteHeaders(7, newPromise());
        Assert.assertEquals(2, encoder.numBufferedStreams());
        ByteBuf empty = Unpooled.buffer(0);
        encoder.writeGoAway(ctx, 3, Http2Error.CANCEL.code(), empty, newPromise());
        Assert.assertEquals(1, connection.numActiveStreams());
        Assert.assertEquals(2, encoder.numBufferedStreams());
        Assert.assertFalse(f1.isDone());
        Assert.assertFalse(f2.isDone());
        Assert.assertFalse(f3.isDone());
    }

    @Test
    public void endStreamDoesNotFailBufferedStream() {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(0);
        encoderWriteHeaders(3, newPromise());
        Assert.assertEquals(1, encoder.numBufferedStreams());
        encoder.writeData(ctx, 3, EMPTY_BUFFER, 0, true, newPromise());
        Assert.assertEquals(0, connection.numActiveStreams());
        Assert.assertEquals(1, encoder.numBufferedStreams());
        // Simulate that we received a SETTINGS frame which
        // increased MAX_CONCURRENT_STREAMS to 1.
        setMaxConcurrentStreams(1);
        encoder.writeSettingsAck(ctx, newPromise());
        Assert.assertEquals(1, connection.numActiveStreams());
        Assert.assertEquals(0, encoder.numBufferedStreams());
        Assert.assertEquals(State.HALF_CLOSED_LOCAL, connection.stream(3).state());
    }

    @Test
    public void rstStreamClosesBufferedStream() {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(0);
        encoderWriteHeaders(3, newPromise());
        Assert.assertEquals(1, encoder.numBufferedStreams());
        ChannelPromise rstStreamPromise = newPromise();
        encoder.writeRstStream(ctx, 3, Http2Error.CANCEL.code(), rstStreamPromise);
        Assert.assertTrue(rstStreamPromise.isSuccess());
        Assert.assertEquals(0, encoder.numBufferedStreams());
    }

    @Test
    public void bufferUntilActiveStreamsAreReset() throws Exception {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(1);
        encoderWriteHeaders(3, newPromise());
        Assert.assertEquals(0, encoder.numBufferedStreams());
        encoderWriteHeaders(5, newPromise());
        Assert.assertEquals(1, encoder.numBufferedStreams());
        encoderWriteHeaders(7, newPromise());
        Assert.assertEquals(2, encoder.numBufferedStreams());
        writeVerifyWriteHeaders(Mockito.times(1), 3);
        writeVerifyWriteHeaders(Mockito.never(), 5);
        writeVerifyWriteHeaders(Mockito.never(), 7);
        encoder.writeRstStream(ctx, 3, Http2Error.CANCEL.code(), newPromise());
        connection.remote().flowController().writePendingBytes();
        writeVerifyWriteHeaders(Mockito.times(1), 5);
        writeVerifyWriteHeaders(Mockito.never(), 7);
        Assert.assertEquals(1, connection.numActiveStreams());
        Assert.assertEquals(1, encoder.numBufferedStreams());
        encoder.writeRstStream(ctx, 5, Http2Error.CANCEL.code(), newPromise());
        connection.remote().flowController().writePendingBytes();
        writeVerifyWriteHeaders(Mockito.times(1), 7);
        Assert.assertEquals(1, connection.numActiveStreams());
        Assert.assertEquals(0, encoder.numBufferedStreams());
        encoder.writeRstStream(ctx, 7, Http2Error.CANCEL.code(), newPromise());
        Assert.assertEquals(0, connection.numActiveStreams());
        Assert.assertEquals(0, encoder.numBufferedStreams());
    }

    @Test
    public void bufferUntilMaxStreamsIncreased() {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(2);
        encoderWriteHeaders(3, newPromise());
        encoderWriteHeaders(5, newPromise());
        encoderWriteHeaders(7, newPromise());
        encoderWriteHeaders(9, newPromise());
        Assert.assertEquals(2, encoder.numBufferedStreams());
        writeVerifyWriteHeaders(Mockito.times(1), 3);
        writeVerifyWriteHeaders(Mockito.times(1), 5);
        writeVerifyWriteHeaders(Mockito.never(), 7);
        writeVerifyWriteHeaders(Mockito.never(), 9);
        // Simulate that we received a SETTINGS frame which
        // increased MAX_CONCURRENT_STREAMS to 5.
        setMaxConcurrentStreams(5);
        encoder.writeSettingsAck(ctx, newPromise());
        Assert.assertEquals(0, encoder.numBufferedStreams());
        writeVerifyWriteHeaders(Mockito.times(1), 7);
        writeVerifyWriteHeaders(Mockito.times(1), 9);
        encoderWriteHeaders(11, newPromise());
        writeVerifyWriteHeaders(Mockito.times(1), 11);
        Assert.assertEquals(5, connection.local().numActiveStreams());
    }

    @Test
    public void bufferUntilSettingsReceived() throws Http2Exception {
        int initialLimit = Http2CodecUtil.SMALLEST_MAX_CONCURRENT_STREAMS;
        int numStreams = initialLimit * 2;
        for (int ix = 0, nextStreamId = 3; ix < numStreams; ++ix , nextStreamId += 2) {
            encoderWriteHeaders(nextStreamId, newPromise());
            if (ix < initialLimit) {
                writeVerifyWriteHeaders(Mockito.times(1), nextStreamId);
            } else {
                writeVerifyWriteHeaders(Mockito.never(), nextStreamId);
            }
        }
        Assert.assertEquals((numStreams / 2), encoder.numBufferedStreams());
        // Simulate that we received a SETTINGS frame.
        setMaxConcurrentStreams((initialLimit * 2));
        Assert.assertEquals(0, encoder.numBufferedStreams());
        Assert.assertEquals(numStreams, connection.local().numActiveStreams());
    }

    @Test
    public void bufferUntilSettingsReceivedWithNoMaxConcurrentStreamValue() throws Http2Exception {
        int initialLimit = Http2CodecUtil.SMALLEST_MAX_CONCURRENT_STREAMS;
        int numStreams = initialLimit * 2;
        for (int ix = 0, nextStreamId = 3; ix < numStreams; ++ix , nextStreamId += 2) {
            encoderWriteHeaders(nextStreamId, newPromise());
            if (ix < initialLimit) {
                writeVerifyWriteHeaders(Mockito.times(1), nextStreamId);
            } else {
                writeVerifyWriteHeaders(Mockito.never(), nextStreamId);
            }
        }
        Assert.assertEquals((numStreams / 2), encoder.numBufferedStreams());
        // Simulate that we received an empty SETTINGS frame.
        encoder.remoteSettings(new Http2Settings());
        Assert.assertEquals(0, encoder.numBufferedStreams());
        Assert.assertEquals(numStreams, connection.local().numActiveStreams());
    }

    @Test
    public void exhaustedStreamsDoNotBuffer() throws Http2Exception {
        // Write the highest possible stream ID for the client.
        // This will cause the next stream ID to be negative.
        encoderWriteHeaders(Integer.MAX_VALUE, newPromise());
        // Disallow any further streams.
        setMaxConcurrentStreams(0);
        // Simulate numeric overflow for the next stream ID.
        ChannelFuture f = encoderWriteHeaders((-1), newPromise());
        // Verify that the write fails.
        Assert.assertNotNull(f.cause());
    }

    @Test
    public void closedBufferedStreamReleasesByteBuf() {
        encoder.writeSettingsAck(ctx, newPromise());
        setMaxConcurrentStreams(0);
        ByteBuf data = Mockito.mock(ByteBuf.class);
        ChannelFuture f1 = encoderWriteHeaders(3, newPromise());
        Assert.assertEquals(1, encoder.numBufferedStreams());
        ChannelFuture f2 = encoder.writeData(ctx, 3, data, 0, false, newPromise());
        ChannelPromise rstPromise = Mockito.mock(ChannelPromise.class);
        encoder.writeRstStream(ctx, 3, Http2Error.CANCEL.code(), rstPromise);
        Assert.assertEquals(0, encoder.numBufferedStreams());
        Mockito.verify(rstPromise).setSuccess();
        Assert.assertTrue(f1.isSuccess());
        Assert.assertTrue(f2.isSuccess());
        Mockito.verify(data).release();
    }

    @Test
    public void closeShouldCancelAllBufferedStreams() throws Http2Exception {
        encoder.writeSettingsAck(ctx, newPromise());
        connection.local().maxActiveStreams(0);
        ChannelFuture f1 = encoderWriteHeaders(3, newPromise());
        ChannelFuture f2 = encoderWriteHeaders(5, newPromise());
        ChannelFuture f3 = encoderWriteHeaders(7, newPromise());
        encoder.close();
        Assert.assertNotNull(f1.cause());
        Assert.assertNotNull(f2.cause());
        Assert.assertNotNull(f3.cause());
    }

    @Test
    public void headersAfterCloseShouldImmediatelyFail() {
        encoder.writeSettingsAck(ctx, newPromise());
        encoder.close();
        ChannelFuture f = encoderWriteHeaders(3, newPromise());
        Assert.assertNotNull(f.cause());
    }
}

