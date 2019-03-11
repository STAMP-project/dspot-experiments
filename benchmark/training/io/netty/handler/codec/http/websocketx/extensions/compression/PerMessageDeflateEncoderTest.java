/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;


import DeflateDecoder.FRAME_TAIL;
import WebSocketExtension.RSV3;
import ZlibWrapper.NONE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class PerMessageDeflateEncoderTest {
    private static final Random random = new Random();

    @Test
    public void testCompressedFrame() {
        EmbeddedChannel encoderChannel = new EmbeddedChannel(new PerMessageDeflateEncoder(9, 15, false));
        EmbeddedChannel decoderChannel = new EmbeddedChannel(ZlibCodecFactory.newZlibDecoder(NONE));
        // initialize
        byte[] payload = new byte[300];
        PerMessageDeflateEncoderTest.random.nextBytes(payload);
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(true, WebSocketExtension.RSV3, Unpooled.wrappedBuffer(payload));
        // execute
        encoderChannel.writeOutbound(frame);
        BinaryWebSocketFrame compressedFrame = encoderChannel.readOutbound();
        // test
        Assert.assertNotNull(compressedFrame);
        Assert.assertNotNull(compressedFrame.content());
        Assert.assertTrue((compressedFrame instanceof BinaryWebSocketFrame));
        Assert.assertEquals(((WebSocketExtension.RSV1) | (WebSocketExtension.RSV3)), compressedFrame.rsv());
        decoderChannel.writeInbound(compressedFrame.content());
        decoderChannel.writeInbound(FRAME_TAIL);
        ByteBuf uncompressedPayload = decoderChannel.readInbound();
        Assert.assertEquals(300, uncompressedPayload.readableBytes());
        byte[] finalPayload = new byte[300];
        uncompressedPayload.readBytes(finalPayload);
        Assert.assertTrue(Arrays.equals(finalPayload, payload));
        uncompressedPayload.release();
    }

    @Test
    public void testAlreadyCompressedFrame() {
        EmbeddedChannel encoderChannel = new EmbeddedChannel(new PerMessageDeflateEncoder(9, 15, false));
        // initialize
        byte[] payload = new byte[300];
        PerMessageDeflateEncoderTest.random.nextBytes(payload);
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(true, ((WebSocketExtension.RSV3) | (WebSocketExtension.RSV1)), Unpooled.wrappedBuffer(payload));
        // execute
        encoderChannel.writeOutbound(frame);
        BinaryWebSocketFrame newFrame = encoderChannel.readOutbound();
        // test
        Assert.assertNotNull(newFrame);
        Assert.assertNotNull(newFrame.content());
        Assert.assertTrue((newFrame instanceof BinaryWebSocketFrame));
        Assert.assertEquals(((WebSocketExtension.RSV3) | (WebSocketExtension.RSV1)), newFrame.rsv());
        Assert.assertEquals(300, newFrame.content().readableBytes());
        byte[] finalPayload = new byte[300];
        newFrame.content().readBytes(finalPayload);
        Assert.assertTrue(Arrays.equals(finalPayload, payload));
        newFrame.release();
    }

    @Test
    public void testFramementedFrame() {
        EmbeddedChannel encoderChannel = new EmbeddedChannel(new PerMessageDeflateEncoder(9, 15, false));
        EmbeddedChannel decoderChannel = new EmbeddedChannel(ZlibCodecFactory.newZlibDecoder(NONE));
        // initialize
        byte[] payload1 = new byte[100];
        PerMessageDeflateEncoderTest.random.nextBytes(payload1);
        byte[] payload2 = new byte[100];
        PerMessageDeflateEncoderTest.random.nextBytes(payload2);
        byte[] payload3 = new byte[100];
        PerMessageDeflateEncoderTest.random.nextBytes(payload3);
        BinaryWebSocketFrame frame1 = new BinaryWebSocketFrame(false, WebSocketExtension.RSV3, Unpooled.wrappedBuffer(payload1));
        ContinuationWebSocketFrame frame2 = new ContinuationWebSocketFrame(false, WebSocketExtension.RSV3, Unpooled.wrappedBuffer(payload2));
        ContinuationWebSocketFrame frame3 = new ContinuationWebSocketFrame(true, WebSocketExtension.RSV3, Unpooled.wrappedBuffer(payload3));
        // execute
        encoderChannel.writeOutbound(frame1);
        encoderChannel.writeOutbound(frame2);
        encoderChannel.writeOutbound(frame3);
        BinaryWebSocketFrame compressedFrame1 = encoderChannel.readOutbound();
        ContinuationWebSocketFrame compressedFrame2 = encoderChannel.readOutbound();
        ContinuationWebSocketFrame compressedFrame3 = encoderChannel.readOutbound();
        // test
        Assert.assertNotNull(compressedFrame1);
        Assert.assertNotNull(compressedFrame2);
        Assert.assertNotNull(compressedFrame3);
        Assert.assertEquals(((WebSocketExtension.RSV1) | (WebSocketExtension.RSV3)), compressedFrame1.rsv());
        Assert.assertEquals(RSV3, compressedFrame2.rsv());
        Assert.assertEquals(RSV3, compressedFrame3.rsv());
        Assert.assertFalse(compressedFrame1.isFinalFragment());
        Assert.assertFalse(compressedFrame2.isFinalFragment());
        Assert.assertTrue(compressedFrame3.isFinalFragment());
        decoderChannel.writeInbound(compressedFrame1.content());
        ByteBuf uncompressedPayload1 = decoderChannel.readInbound();
        byte[] finalPayload1 = new byte[100];
        uncompressedPayload1.readBytes(finalPayload1);
        Assert.assertTrue(Arrays.equals(finalPayload1, payload1));
        uncompressedPayload1.release();
        decoderChannel.writeInbound(compressedFrame2.content());
        ByteBuf uncompressedPayload2 = decoderChannel.readInbound();
        byte[] finalPayload2 = new byte[100];
        uncompressedPayload2.readBytes(finalPayload2);
        Assert.assertTrue(Arrays.equals(finalPayload2, payload2));
        uncompressedPayload2.release();
        decoderChannel.writeInbound(compressedFrame3.content());
        decoderChannel.writeInbound(FRAME_TAIL);
        ByteBuf uncompressedPayload3 = decoderChannel.readInbound();
        byte[] finalPayload3 = new byte[100];
        uncompressedPayload3.readBytes(finalPayload3);
        Assert.assertTrue(Arrays.equals(finalPayload3, payload3));
        uncompressedPayload3.release();
    }
}

