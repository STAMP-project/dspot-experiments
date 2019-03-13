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


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.EventExecutor;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static EmptyHttp2Headers.INSTANCE;


/**
 * Tests encoding/decoding each HTTP2 frame type.
 */
public class Http2FrameRoundtripTest {
    private static final byte[] MESSAGE = "hello world".getBytes(UTF_8);

    private static final int STREAM_ID = 2147483647;

    private static final int WINDOW_UPDATE = 2147483647;

    private static final long ERROR_CODE = 4294967295L;

    @Mock
    private Http2FrameListener listener;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private EventExecutor executor;

    @Mock
    private Channel channel;

    @Mock
    private ByteBufAllocator alloc;

    private Http2FrameWriter writer;

    private Http2FrameReader reader;

    private final List<ByteBuf> needReleasing = new LinkedList<ByteBuf>();

    @Test
    public void emptyDataShouldMatch() throws Exception {
        final ByteBuf data = EMPTY_BUFFER;
        writer.writeData(ctx, Http2FrameRoundtripTest.STREAM_ID, data.slice(), 0, false, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(data), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
    }

    @Test
    public void dataShouldMatch() throws Exception {
        final ByteBuf data = Http2FrameRoundtripTest.data(10);
        writer.writeData(ctx, Http2FrameRoundtripTest.STREAM_ID, data.slice(), 1, false, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(data), ArgumentMatchers.eq(1), ArgumentMatchers.eq(false));
    }

    @Test
    public void dataWithPaddingShouldMatch() throws Exception {
        final ByteBuf data = Http2FrameRoundtripTest.data(10);
        writer.writeData(ctx, Http2FrameRoundtripTest.STREAM_ID, data.slice(), Http2CodecUtil.MAX_PADDING, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(data), ArgumentMatchers.eq(Http2CodecUtil.MAX_PADDING), ArgumentMatchers.eq(true));
    }

    @Test
    public void largeDataFrameShouldMatch() throws Exception {
        // Create a large message to force chunking.
        final ByteBuf originalData = Http2FrameRoundtripTest.data((1024 * 1024));
        final int originalPadding = 100;
        final boolean endOfStream = true;
        writer.writeData(ctx, Http2FrameRoundtripTest.STREAM_ID, originalData.slice(), originalPadding, endOfStream, ctx.newPromise());
        readFrames();
        // Verify that at least one frame was sent with eos=false and exactly one with eos=true.
        Mockito.verify(listener, Mockito.atLeastOnce()).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        Mockito.verify(listener).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(true));
        // Capture the read data and padding.
        ArgumentCaptor<ByteBuf> dataCaptor = ArgumentCaptor.forClass(ByteBuf.class);
        ArgumentCaptor<Integer> paddingCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(listener, Mockito.atLeastOnce()).onDataRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), dataCaptor.capture(), paddingCaptor.capture(), ArgumentMatchers.anyBoolean());
        // Make sure the data matches the original.
        for (ByteBuf chunk : dataCaptor.getAllValues()) {
            ByteBuf originalChunk = originalData.readSlice(chunk.readableBytes());
            Assert.assertEquals(originalChunk, chunk);
        }
        Assert.assertFalse(originalData.isReadable());
        // Make sure the padding matches the original.
        int totalReadPadding = 0;
        for (int framePadding : paddingCaptor.getAllValues()) {
            totalReadPadding += framePadding;
        }
        Assert.assertEquals(originalPadding, totalReadPadding);
    }

    @Test
    public void emptyHeadersShouldMatch() throws Exception {
        final Http2Headers headers = INSTANCE;
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, 0, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
    }

    @Test
    public void emptyHeadersWithPaddingShouldMatch() throws Exception {
        final Http2Headers headers = INSTANCE;
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, Http2CodecUtil.MAX_PADDING, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(Http2CodecUtil.MAX_PADDING), ArgumentMatchers.eq(true));
    }

    @Test
    public void binaryHeadersWithoutPriorityShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.binaryHeaders();
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, 0, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
    }

    @Test
    public void headersFrameWithoutPriorityShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.headers();
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, 0, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
    }

    @Test
    public void headersFrameWithPriorityShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.headers();
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, 4, ((short) (255)), true, 0, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(4), ArgumentMatchers.eq(((short) (255))), ArgumentMatchers.eq(true), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
    }

    @Test
    public void headersWithPaddingWithoutPriorityShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.headers();
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, Http2CodecUtil.MAX_PADDING, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(Http2CodecUtil.MAX_PADDING), ArgumentMatchers.eq(true));
    }

    @Test
    public void headersWithPaddingWithPriorityShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.headers();
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, 2, ((short) (3)), true, 1, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(2), ArgumentMatchers.eq(((short) (3))), ArgumentMatchers.eq(true), ArgumentMatchers.eq(1), ArgumentMatchers.eq(true));
    }

    @Test
    public void continuedHeadersShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.largeHeaders();
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, 2, ((short) (3)), true, 0, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(2), ArgumentMatchers.eq(((short) (3))), ArgumentMatchers.eq(true), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
    }

    @Test
    public void continuedHeadersWithPaddingShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.largeHeaders();
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, 2, ((short) (3)), true, Http2CodecUtil.MAX_PADDING, true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onHeadersRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(2), ArgumentMatchers.eq(((short) (3))), ArgumentMatchers.eq(true), ArgumentMatchers.eq(Http2CodecUtil.MAX_PADDING), ArgumentMatchers.eq(true));
    }

    @Test
    public void headersThatAreTooBigShouldFail() throws Exception {
        reader = new DefaultHttp2FrameReader(false);
        final int maxListSize = 100;
        reader.configuration().headersConfiguration().maxHeaderListSize(maxListSize, maxListSize);
        final Http2Headers headers = Http2FrameRoundtripTest.headersOfSize((maxListSize + 1));
        writer.writeHeaders(ctx, Http2FrameRoundtripTest.STREAM_ID, headers, 2, ((short) (3)), true, Http2CodecUtil.MAX_PADDING, true, ctx.newPromise());
        try {
            readFrames();
            Assert.fail();
        } catch (Http2Exception e) {
            Mockito.verify(listener, Mockito.never()).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        }
    }

    @Test
    public void emptyPushPromiseShouldMatch() throws Exception {
        final Http2Headers headers = INSTANCE;
        writer.writePushPromise(ctx, Http2FrameRoundtripTest.STREAM_ID, 2, headers, 0, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onPushPromiseRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(2), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0));
    }

    @Test
    public void pushPromiseFrameShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.headers();
        writer.writePushPromise(ctx, Http2FrameRoundtripTest.STREAM_ID, 1, headers, 5, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onPushPromiseRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(1), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(5));
    }

    @Test
    public void pushPromiseWithPaddingShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.headers();
        writer.writePushPromise(ctx, Http2FrameRoundtripTest.STREAM_ID, 2, headers, Http2CodecUtil.MAX_PADDING, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onPushPromiseRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(2), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(Http2CodecUtil.MAX_PADDING));
    }

    @Test
    public void continuedPushPromiseShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.largeHeaders();
        writer.writePushPromise(ctx, Http2FrameRoundtripTest.STREAM_ID, 2, headers, 0, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onPushPromiseRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(2), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0));
    }

    @Test
    public void continuedPushPromiseWithPaddingShouldMatch() throws Exception {
        final Http2Headers headers = Http2FrameRoundtripTest.largeHeaders();
        writer.writePushPromise(ctx, Http2FrameRoundtripTest.STREAM_ID, 2, headers, 255, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onPushPromiseRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(2), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(255));
    }

    @Test
    public void goAwayFrameShouldMatch() throws Exception {
        final String text = "test";
        final ByteBuf data = Http2FrameRoundtripTest.buf(text.getBytes());
        writer.writeGoAway(ctx, Http2FrameRoundtripTest.STREAM_ID, Http2FrameRoundtripTest.ERROR_CODE, data.slice(), ctx.newPromise());
        readFrames();
        ArgumentCaptor<ByteBuf> captor = ArgumentCaptor.forClass(ByteBuf.class);
        Mockito.verify(listener).onGoAwayRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(Http2FrameRoundtripTest.ERROR_CODE), captor.capture());
        Assert.assertEquals(data, captor.getValue());
    }

    @Test
    public void pingFrameShouldMatch() throws Exception {
        writer.writePing(ctx, false, 1234567, ctx.newPromise());
        readFrames();
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(long.class);
        Mockito.verify(listener).onPingRead(ArgumentMatchers.eq(ctx), captor.capture());
        Assert.assertEquals(1234567, ((long) (captor.getValue())));
    }

    @Test
    public void pingAckFrameShouldMatch() throws Exception {
        writer.writePing(ctx, true, 1234567, ctx.newPromise());
        readFrames();
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(long.class);
        Mockito.verify(listener).onPingAckRead(ArgumentMatchers.eq(ctx), captor.capture());
        Assert.assertEquals(1234567, ((long) (captor.getValue())));
    }

    @Test
    public void priorityFrameShouldMatch() throws Exception {
        writer.writePriority(ctx, Http2FrameRoundtripTest.STREAM_ID, 1, ((short) (1)), true, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onPriorityRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(1), ArgumentMatchers.eq(((short) (1))), ArgumentMatchers.eq(true));
    }

    @Test
    public void rstStreamFrameShouldMatch() throws Exception {
        writer.writeRstStream(ctx, Http2FrameRoundtripTest.STREAM_ID, Http2FrameRoundtripTest.ERROR_CODE, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onRstStreamRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(Http2FrameRoundtripTest.ERROR_CODE));
    }

    @Test
    public void emptySettingsFrameShouldMatch() throws Exception {
        final Http2Settings settings = new Http2Settings();
        writer.writeSettings(ctx, settings, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onSettingsRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(settings));
    }

    @Test
    public void settingsShouldStripShouldMatch() throws Exception {
        final Http2Settings settings = new Http2Settings();
        settings.pushEnabled(true);
        settings.headerTableSize(4096);
        settings.initialWindowSize(123);
        settings.maxConcurrentStreams(456);
        writer.writeSettings(ctx, settings, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onSettingsRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(settings));
    }

    @Test
    public void settingsAckShouldMatch() throws Exception {
        writer.writeSettingsAck(ctx, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onSettingsAckRead(ArgumentMatchers.eq(ctx));
    }

    @Test
    public void windowUpdateFrameShouldMatch() throws Exception {
        writer.writeWindowUpdate(ctx, Http2FrameRoundtripTest.STREAM_ID, Http2FrameRoundtripTest.WINDOW_UPDATE, ctx.newPromise());
        readFrames();
        Mockito.verify(listener).onWindowUpdateRead(ArgumentMatchers.eq(ctx), ArgumentMatchers.eq(Http2FrameRoundtripTest.STREAM_ID), ArgumentMatchers.eq(Http2FrameRoundtripTest.WINDOW_UPDATE));
    }
}

