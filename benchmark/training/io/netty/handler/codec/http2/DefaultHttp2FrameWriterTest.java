/**
 * Copyright 2017 The Netty Project
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


import Http2CodecUtil.MAX_FRAME_SIZE_LOWER_BOUND;
import Unpooled.EMPTY_BUFFER;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import static Http2CodecUtil.MAX_FRAME_SIZE_LOWER_BOUND;


/**
 * Tests for {@link DefaultHttp2FrameWriter}.
 */
public class DefaultHttp2FrameWriterTest {
    private DefaultHttp2FrameWriter frameWriter;

    private ByteBuf outbound;

    private ByteBuf expectedOutbound;

    private ChannelPromise promise;

    private Http2HeadersEncoder http2HeadersEncoder;

    @Mock
    private Channel channel;

    @Mock
    private ChannelFuture future;

    @Mock
    private ChannelHandlerContext ctx;

    @Test
    public void writeHeaders() throws Exception {
        int streamId = 1;
        Http2Headers headers = new DefaultHttp2Headers().method("GET").path("/").authority("foo.com").scheme("https");
        frameWriter.writeHeaders(ctx, streamId, headers, 0, true, promise);
        byte[] expectedPayload = headerPayload(streamId, headers);
        byte[] expectedFrameBytes = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (10))// payload length = 10
        , ((byte) (1))// payload type = 1
        , ((byte) (5))// flags = (0x01 | 0x04)
        , ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (1))// stream id = 1
         };
        expectedOutbound = Unpooled.copiedBuffer(expectedFrameBytes, expectedPayload);
        Assert.assertEquals(expectedOutbound, outbound);
    }

    @Test
    public void writeHeadersWithPadding() throws Exception {
        int streamId = 1;
        Http2Headers headers = new DefaultHttp2Headers().method("GET").path("/").authority("foo.com").scheme("https");
        frameWriter.writeHeaders(ctx, streamId, headers, 5, true, promise);
        byte[] expectedPayload = headerPayload(streamId, headers, ((byte) (4)));
        byte[] expectedFrameBytes = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (15))// payload length = 16
        , ((byte) (1))// payload type = 1
        , ((byte) (13))// flags = (0x01 | 0x04 | 0x08)
        , ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (1))// stream id = 1
         };
        expectedOutbound = Unpooled.copiedBuffer(expectedFrameBytes, expectedPayload);
        Assert.assertEquals(expectedOutbound, outbound);
    }

    @Test
    public void writeHeadersNotEndStream() throws Exception {
        int streamId = 1;
        Http2Headers headers = new DefaultHttp2Headers().method("GET").path("/").authority("foo.com").scheme("https");
        frameWriter.writeHeaders(ctx, streamId, headers, 0, false, promise);
        byte[] expectedPayload = headerPayload(streamId, headers);
        byte[] expectedFrameBytes = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (10))// payload length = 10
        , ((byte) (1))// payload type = 1
        , ((byte) (4))// flags = 0x04
        , ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (1))// stream id = 1
         };
        ByteBuf expectedOutbound = Unpooled.copiedBuffer(expectedFrameBytes, expectedPayload);
        Assert.assertEquals(expectedOutbound, outbound);
    }

    /**
     * Test large headers that exceed {@link DefaultHttp2FrameWriter#maxFrameSize}
     * the remaining headers will be sent in a CONTINUATION frame
     */
    @Test
    public void writeLargeHeaders() throws Exception {
        int streamId = 1;
        Http2Headers headers = new DefaultHttp2Headers().method("GET").path("/").authority("foo.com").scheme("https");
        headers = DefaultHttp2FrameWriterTest.dummyHeaders(headers, 20);
        http2HeadersEncoder.configuration().maxHeaderListSize(Integer.MAX_VALUE);
        frameWriter.headersConfiguration().maxHeaderListSize(Integer.MAX_VALUE);
        frameWriter.maxFrameSize(MAX_FRAME_SIZE_LOWER_BOUND);
        frameWriter.writeHeaders(ctx, streamId, headers, 0, true, promise);
        byte[] expectedPayload = headerPayload(streamId, headers);
        // First frame: HEADER(length=0x4000, flags=0x01)
        Assert.assertEquals(MAX_FRAME_SIZE_LOWER_BOUND, outbound.readUnsignedMedium());
        Assert.assertEquals(1, outbound.readByte());
        Assert.assertEquals(1, outbound.readByte());
        Assert.assertEquals(streamId, outbound.readInt());
        byte[] firstPayload = new byte[MAX_FRAME_SIZE_LOWER_BOUND];
        outbound.readBytes(firstPayload);
        int remainPayloadLength = (expectedPayload.length) - (MAX_FRAME_SIZE_LOWER_BOUND);
        // Second frame: CONTINUATION(length=remainPayloadLength, flags=0x04)
        Assert.assertEquals(remainPayloadLength, outbound.readUnsignedMedium());
        Assert.assertEquals(9, outbound.readByte());
        Assert.assertEquals(4, outbound.readByte());
        Assert.assertEquals(streamId, outbound.readInt());
        byte[] secondPayload = new byte[remainPayloadLength];
        outbound.readBytes(secondPayload);
        Assert.assertArrayEquals(Arrays.copyOfRange(expectedPayload, 0, firstPayload.length), firstPayload);
        Assert.assertArrayEquals(Arrays.copyOfRange(expectedPayload, firstPayload.length, expectedPayload.length), secondPayload);
    }

    @Test
    public void writeFrameZeroPayload() throws Exception {
        frameWriter.writeFrame(ctx, ((byte) (15)), 0, new Http2Flags(), EMPTY_BUFFER, promise);
        byte[] expectedFrameBytes = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0))// payload length
        , ((byte) (15))// payload type
        , ((byte) (0))// flags
        , ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0))// stream id
         };
        expectedOutbound = Unpooled.wrappedBuffer(expectedFrameBytes);
        Assert.assertEquals(expectedOutbound, outbound);
    }

    @Test
    public void writeFrameHasPayload() throws Exception {
        byte[] payload = new byte[]{ ((byte) (1)), ((byte) (3)), ((byte) (5)), ((byte) (7)), ((byte) (9)) };
        // will auto release after frameWriter.writeFrame succeed
        ByteBuf payloadByteBuf = Unpooled.wrappedBuffer(payload);
        frameWriter.writeFrame(ctx, ((byte) (15)), 0, new Http2Flags(), payloadByteBuf, promise);
        byte[] expectedFrameHeaderBytes = new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (5))// payload length
        , ((byte) (15))// payload type
        , ((byte) (0))// flags
        , ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0))// stream id
         };
        expectedOutbound = Unpooled.copiedBuffer(expectedFrameHeaderBytes, payload);
        Assert.assertEquals(expectedOutbound, outbound);
    }
}

