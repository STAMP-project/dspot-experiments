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
package io.netty.handler.codec.spdy;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


public class SpdyFrameDecoderTest {
    private static final Random RANDOM = new Random();

    private final SpdyFrameDecoderDelegate delegate = Mockito.mock(SpdyFrameDecoderDelegate.class);

    private final SpdyFrameDecoderTest.TestSpdyFrameDecoderDelegate testDelegate = new SpdyFrameDecoderTest.TestSpdyFrameDecoderDelegate();

    private SpdyFrameDecoder decoder;

    private final class TestSpdyFrameDecoderDelegate implements SpdyFrameDecoderDelegate {
        private final Queue<ByteBuf> buffers = new ArrayDeque<ByteBuf>();

        @Override
        public void readDataFrame(int streamId, boolean last, ByteBuf data) {
            delegate.readDataFrame(streamId, last, data);
            buffers.add(data);
        }

        @Override
        public void readSynStreamFrame(int streamId, int associatedToStreamId, byte priority, boolean last, boolean unidirectional) {
            delegate.readSynStreamFrame(streamId, associatedToStreamId, priority, last, unidirectional);
        }

        @Override
        public void readSynReplyFrame(int streamId, boolean last) {
            delegate.readSynReplyFrame(streamId, last);
        }

        @Override
        public void readRstStreamFrame(int streamId, int statusCode) {
            delegate.readRstStreamFrame(streamId, statusCode);
        }

        @Override
        public void readSettingsFrame(boolean clearPersisted) {
            delegate.readSettingsFrame(clearPersisted);
        }

        @Override
        public void readSetting(int id, int value, boolean persistValue, boolean persisted) {
            delegate.readSetting(id, value, persistValue, persisted);
        }

        @Override
        public void readSettingsEnd() {
            delegate.readSettingsEnd();
        }

        @Override
        public void readPingFrame(int id) {
            delegate.readPingFrame(id);
        }

        @Override
        public void readGoAwayFrame(int lastGoodStreamId, int statusCode) {
            delegate.readGoAwayFrame(lastGoodStreamId, statusCode);
        }

        @Override
        public void readHeadersFrame(int streamId, boolean last) {
            delegate.readHeadersFrame(streamId, last);
        }

        @Override
        public void readWindowUpdateFrame(int streamId, int deltaWindowSize) {
            delegate.readWindowUpdateFrame(streamId, deltaWindowSize);
        }

        @Override
        public void readHeaderBlock(ByteBuf headerBlock) {
            delegate.readHeaderBlock(headerBlock);
            buffers.add(headerBlock);
        }

        @Override
        public void readHeaderBlockEnd() {
            delegate.readHeaderBlockEnd();
        }

        @Override
        public void readFrameError(String message) {
            delegate.readFrameError(message);
        }

        void releaseAll() {
            for (; ;) {
                ByteBuf buf = buffers.poll();
                if (buf == null) {
                    return;
                }
                buf.release();
            }
        }
    }

    @Test
    public void testSpdyDataFrame() throws Exception {
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        byte flags = 0;
        int length = 1024;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeDataFrameHeader(buf, streamId, flags, length);
        for (int i = 0; i < 256; i++) {
            buf.writeInt(SpdyFrameDecoderTest.RANDOM.nextInt());
        }
        decoder.decode(buf);
        Mockito.verify(delegate).readDataFrame(streamId, false, buf.slice(SpdyCodecUtil.SPDY_HEADER_SIZE, length));
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testEmptySpdyDataFrame() throws Exception {
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        byte flags = 0;
        int length = 0;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeDataFrameHeader(buf, streamId, flags, length);
        decoder.decode(buf);
        Mockito.verify(delegate).readDataFrame(streamId, false, Unpooled.EMPTY_BUFFER);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testLastSpdyDataFrame() throws Exception {
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        byte flags = 1;// FLAG_FIN

        int length = 0;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeDataFrameHeader(buf, streamId, flags, length);
        decoder.decode(buf);
        Mockito.verify(delegate).readDataFrame(streamId, true, Unpooled.EMPTY_BUFFER);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testUnknownSpdyDataFrameFlags() throws Exception {
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        byte flags = ((byte) (254));// should ignore any unknown flags

        int length = 0;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeDataFrameHeader(buf, streamId, flags, length);
        decoder.decode(buf);
        Mockito.verify(delegate).readDataFrame(streamId, false, Unpooled.EMPTY_BUFFER);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testIllegalSpdyDataFrameStreamId() throws Exception {
        int streamId = 0;// illegal stream identifier

        byte flags = 0;
        int length = 0;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeDataFrameHeader(buf, streamId, flags, length);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(((String) (any())));
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testPipelinedSpdyDataFrames() throws Exception {
        int streamId1 = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int streamId2 = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        byte flags = 0;
        int length = 0;
        ByteBuf buf = Unpooled.buffer((2 * ((SpdyCodecUtil.SPDY_HEADER_SIZE) + length)));
        SpdyFrameDecoderTest.encodeDataFrameHeader(buf, streamId1, flags, length);
        SpdyFrameDecoderTest.encodeDataFrameHeader(buf, streamId2, flags, length);
        decoder.decode(buf);
        Mockito.verify(delegate).readDataFrame(streamId1, false, Unpooled.EMPTY_BUFFER);
        Mockito.verify(delegate).readDataFrame(streamId2, false, Unpooled.EMPTY_BUFFER);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdySynStreamFrame() throws Exception {
        short type = 1;
        byte flags = 0;
        int length = 10;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int associatedToStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        byte priority = ((byte) ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 7));
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(associatedToStreamId);
        buf.writeByte((priority << 5));
        buf.writeByte(0);
        decoder.decode(buf);
        Mockito.verify(delegate).readSynStreamFrame(streamId, associatedToStreamId, priority, false, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testLastSpdySynStreamFrame() throws Exception {
        short type = 1;
        byte flags = 1;// FLAG_FIN

        int length = 10;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int associatedToStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        byte priority = ((byte) ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 7));
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(associatedToStreamId);
        buf.writeByte((priority << 5));
        buf.writeByte(0);
        decoder.decode(buf);
        Mockito.verify(delegate).readSynStreamFrame(streamId, associatedToStreamId, priority, true, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testUnidirectionalSpdySynStreamFrame() throws Exception {
        short type = 1;
        byte flags = 2;// FLAG_UNIDIRECTIONAL

        int length = 10;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int associatedToStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        byte priority = ((byte) ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 7));
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(associatedToStreamId);
        buf.writeByte((priority << 5));
        buf.writeByte(0);
        decoder.decode(buf);
        Mockito.verify(delegate).readSynStreamFrame(streamId, associatedToStreamId, priority, false, true);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testIndependentSpdySynStreamFrame() throws Exception {
        short type = 1;
        byte flags = 0;
        int length = 10;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int associatedToStreamId = 0;// independent of all other streams

        byte priority = ((byte) ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 7));
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(associatedToStreamId);
        buf.writeByte((priority << 5));
        buf.writeByte(0);
        decoder.decode(buf);
        Mockito.verify(delegate).readSynStreamFrame(streamId, associatedToStreamId, priority, false, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testUnknownSpdySynStreamFrameFlags() throws Exception {
        short type = 1;
        byte flags = ((byte) (252));// undefined flags

        int length = 10;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int associatedToStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        byte priority = ((byte) ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 7));
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(associatedToStreamId);
        buf.writeByte((priority << 5));
        buf.writeByte(0);
        decoder.decode(buf);
        Mockito.verify(delegate).readSynStreamFrame(streamId, associatedToStreamId, priority, false, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testReservedSpdySynStreamFrameBits() throws Exception {
        short type = 1;
        byte flags = 0;
        int length = 10;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int associatedToStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        byte priority = ((byte) ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 7));
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt((streamId | -2147483648));// should ignore reserved bit

        buf.writeInt((associatedToStreamId | -2147483648));// should ignore reserved bit

        buf.writeByte(((priority << 5) | 31));// should ignore reserved bits

        buf.writeByte(255);// should ignore reserved bits

        decoder.decode(buf);
        Mockito.verify(delegate).readSynStreamFrame(streamId, associatedToStreamId, priority, false, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdySynStreamFrameLength() throws Exception {
        short type = 1;
        byte flags = 0;
        int length = 8;// invalid length

        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int associatedToStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(associatedToStreamId);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testIllegalSpdySynStreamFrameStreamId() throws Exception {
        short type = 1;
        byte flags = 0;
        int length = 10;
        int streamId = 0;// invalid stream identifier

        int associatedToStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        byte priority = ((byte) ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 7));
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(associatedToStreamId);
        buf.writeByte((priority << 5));
        buf.writeByte(0);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdySynStreamFrameHeaderBlock() throws Exception {
        short type = 1;
        byte flags = 0;
        int length = 10;
        int headerBlockLength = 1024;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int associatedToStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        byte priority = ((byte) ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 7));
        ByteBuf buf = Unpooled.buffer((((SpdyCodecUtil.SPDY_HEADER_SIZE) + length) + headerBlockLength));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, (length + headerBlockLength));
        buf.writeInt(streamId);
        buf.writeInt(associatedToStreamId);
        buf.writeByte((priority << 5));
        buf.writeByte(0);
        ByteBuf headerBlock = Unpooled.buffer(headerBlockLength);
        for (int i = 0; i < 256; i++) {
            headerBlock.writeInt(SpdyFrameDecoderTest.RANDOM.nextInt());
        }
        decoder.decode(buf);
        decoder.decode(headerBlock);
        Mockito.verify(delegate).readSynStreamFrame(streamId, associatedToStreamId, priority, false, false);
        Mockito.verify(delegate).readHeaderBlock(headerBlock.slice(0, headerBlock.writerIndex()));
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        Assert.assertFalse(headerBlock.isReadable());
        buf.release();
        headerBlock.release();
    }

    @Test
    public void testSpdySynReplyFrame() throws Exception {
        short type = 2;
        byte flags = 0;
        int length = 4;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        decoder.decode(buf);
        Mockito.verify(delegate).readSynReplyFrame(streamId, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testLastSpdySynReplyFrame() throws Exception {
        short type = 2;
        byte flags = 1;// FLAG_FIN

        int length = 4;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        decoder.decode(buf);
        Mockito.verify(delegate).readSynReplyFrame(streamId, true);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testUnknownSpdySynReplyFrameFlags() throws Exception {
        short type = 2;
        byte flags = ((byte) (254));// undefined flags

        int length = 4;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        decoder.decode(buf);
        Mockito.verify(delegate).readSynReplyFrame(streamId, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testReservedSpdySynReplyFrameBits() throws Exception {
        short type = 2;
        byte flags = 0;
        int length = 4;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt((streamId | -2147483648));// should ignore reserved bit

        decoder.decode(buf);
        Mockito.verify(delegate).readSynReplyFrame(streamId, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdySynReplyFrameLength() throws Exception {
        short type = 2;
        byte flags = 0;
        int length = 0;// invalid length

        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testIllegalSpdySynReplyFrameStreamId() throws Exception {
        short type = 2;
        byte flags = 0;
        int length = 4;
        int streamId = 0;// invalid stream identifier

        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdySynReplyFrameHeaderBlock() throws Exception {
        short type = 2;
        byte flags = 0;
        int length = 4;
        int headerBlockLength = 1024;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer((((SpdyCodecUtil.SPDY_HEADER_SIZE) + length) + headerBlockLength));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, (length + headerBlockLength));
        buf.writeInt(streamId);
        ByteBuf headerBlock = Unpooled.buffer(headerBlockLength);
        for (int i = 0; i < 256; i++) {
            headerBlock.writeInt(SpdyFrameDecoderTest.RANDOM.nextInt());
        }
        decoder.decode(buf);
        decoder.decode(headerBlock);
        Mockito.verify(delegate).readSynReplyFrame(streamId, false);
        Mockito.verify(delegate).readHeaderBlock(headerBlock.slice(0, headerBlock.writerIndex()));
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        Assert.assertFalse(headerBlock.isReadable());
        buf.release();
        headerBlock.release();
    }

    @Test
    public void testSpdyRstStreamFrame() throws Exception {
        short type = 3;
        byte flags = 0;
        int length = 8;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int statusCode = (SpdyFrameDecoderTest.RANDOM.nextInt()) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readRstStreamFrame(streamId, statusCode);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testReservedSpdyRstStreamFrameBits() throws Exception {
        short type = 3;
        byte flags = 0;
        int length = 8;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int statusCode = (SpdyFrameDecoderTest.RANDOM.nextInt()) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt((streamId | -2147483648));// should ignore reserved bit

        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readRstStreamFrame(streamId, statusCode);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdyRstStreamFrameFlags() throws Exception {
        short type = 3;
        byte flags = ((byte) (255));// invalid flags

        int length = 8;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int statusCode = (SpdyFrameDecoderTest.RANDOM.nextInt()) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdyRstStreamFrameLength() throws Exception {
        short type = 3;
        byte flags = 0;
        int length = 12;// invalid length

        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int statusCode = (SpdyFrameDecoderTest.RANDOM.nextInt()) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testIllegalSpdyRstStreamFrameStreamId() throws Exception {
        short type = 3;
        byte flags = 0;
        int length = 8;
        int streamId = 0;// invalid stream identifier

        int statusCode = (SpdyFrameDecoderTest.RANDOM.nextInt()) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testIllegalSpdyRstStreamFrameStatusCode() throws Exception {
        short type = 3;
        byte flags = 0;
        int length = 8;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        int statusCode = 0;// invalid status code

        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdySettingsFrame() throws Exception {
        short type = 4;
        byte flags = 0;
        int numSettings = 2;
        int length = (8 * numSettings) + 4;
        byte idFlags = 0;
        int id = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 16777215;
        int value = SpdyFrameDecoderTest.RANDOM.nextInt();
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(numSettings);
        for (int i = 0; i < numSettings; i++) {
            buf.writeByte(idFlags);
            buf.writeMedium(id);
            buf.writeInt(value);
        }
        delegate.readSettingsEnd();
        decoder.decode(buf);
        Mockito.verify(delegate).readSettingsFrame(false);
        Mockito.verify(delegate, Mockito.times(numSettings)).readSetting(id, value, false, false);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testEmptySpdySettingsFrame() throws Exception {
        short type = 4;
        byte flags = 0;
        int numSettings = 0;
        int length = (8 * numSettings) + 4;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(numSettings);
        decoder.decode(buf);
        Mockito.verify(delegate).readSettingsFrame(false);
        Mockito.verify(delegate).readSettingsEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdySettingsFrameClearFlag() throws Exception {
        short type = 4;
        byte flags = 1;// FLAG_SETTINGS_CLEAR_SETTINGS

        int numSettings = 0;
        int length = (8 * numSettings) + 4;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(numSettings);
        decoder.decode(buf);
        Mockito.verify(delegate).readSettingsFrame(true);
        Mockito.verify(delegate).readSettingsEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdySettingsPersistValues() throws Exception {
        short type = 4;
        byte flags = 0;
        int numSettings = 1;
        int length = (8 * numSettings) + 4;
        byte idFlags = 1;// FLAG_SETTINGS_PERSIST_VALUE

        int id = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 16777215;
        int value = SpdyFrameDecoderTest.RANDOM.nextInt();
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(numSettings);
        for (int i = 0; i < numSettings; i++) {
            buf.writeByte(idFlags);
            buf.writeMedium(id);
            buf.writeInt(value);
        }
        delegate.readSettingsEnd();
        decoder.decode(buf);
        Mockito.verify(delegate).readSettingsFrame(false);
        Mockito.verify(delegate, Mockito.times(numSettings)).readSetting(id, value, true, false);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdySettingsPersistedValues() throws Exception {
        short type = 4;
        byte flags = 0;
        int numSettings = 1;
        int length = (8 * numSettings) + 4;
        byte idFlags = 2;// FLAG_SETTINGS_PERSISTED

        int id = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 16777215;
        int value = SpdyFrameDecoderTest.RANDOM.nextInt();
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(numSettings);
        for (int i = 0; i < numSettings; i++) {
            buf.writeByte(idFlags);
            buf.writeMedium(id);
            buf.writeInt(value);
        }
        delegate.readSettingsEnd();
        decoder.decode(buf);
        Mockito.verify(delegate).readSettingsFrame(false);
        Mockito.verify(delegate, Mockito.times(numSettings)).readSetting(id, value, false, true);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testUnknownSpdySettingsFrameFlags() throws Exception {
        short type = 4;
        byte flags = ((byte) (254));// undefined flags

        int numSettings = 0;
        int length = (8 * numSettings) + 4;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(numSettings);
        decoder.decode(buf);
        Mockito.verify(delegate).readSettingsFrame(false);
        Mockito.verify(delegate).readSettingsEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testUnknownSpdySettingsFlags() throws Exception {
        short type = 4;
        byte flags = 0;
        int numSettings = 1;
        int length = (8 * numSettings) + 4;
        byte idFlags = ((byte) (252));// undefined flags

        int id = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 16777215;
        int value = SpdyFrameDecoderTest.RANDOM.nextInt();
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(numSettings);
        for (int i = 0; i < numSettings; i++) {
            buf.writeByte(idFlags);
            buf.writeMedium(id);
            buf.writeInt(value);
        }
        delegate.readSettingsEnd();
        decoder.decode(buf);
        Mockito.verify(delegate).readSettingsFrame(false);
        Mockito.verify(delegate, Mockito.times(numSettings)).readSetting(id, value, false, false);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdySettingsFrameLength() throws Exception {
        short type = 4;
        byte flags = 0;
        int numSettings = 2;
        int length = (8 * numSettings) + 8;// invalid length

        byte idFlags = 0;
        int id = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 16777215;
        int value = SpdyFrameDecoderTest.RANDOM.nextInt();
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(numSettings);
        for (int i = 0; i < numSettings; i++) {
            buf.writeByte(idFlags);
            buf.writeMedium(id);
            buf.writeInt(value);
        }
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdySettingsFrameNumSettings() throws Exception {
        short type = 4;
        byte flags = 0;
        int numSettings = 2;
        int length = (8 * numSettings) + 4;
        byte idFlags = 0;
        int id = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 16777215;
        int value = SpdyFrameDecoderTest.RANDOM.nextInt();
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(0);// invalid num_settings

        for (int i = 0; i < numSettings; i++) {
            buf.writeByte(idFlags);
            buf.writeMedium(id);
            buf.writeInt(value);
        }
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testDiscardUnknownFrame() throws Exception {
        short type = 5;
        byte flags = ((byte) (255));
        int length = 8;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeLong(SpdyFrameDecoderTest.RANDOM.nextLong());
        decoder.decode(buf);
        Mockito.verifyZeroInteractions(delegate);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testDiscardUnknownEmptyFrame() throws Exception {
        short type = 5;
        byte flags = ((byte) (255));
        int length = 0;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        decoder.decode(buf);
        Mockito.verifyZeroInteractions(delegate);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testProgressivelyDiscardUnknownEmptyFrame() throws Exception {
        short type = 5;
        byte flags = ((byte) (255));
        int segment = 4;
        int length = 2 * segment;
        ByteBuf header = Unpooled.buffer(SpdyCodecUtil.SPDY_HEADER_SIZE);
        ByteBuf segment1 = Unpooled.buffer(segment);
        ByteBuf segment2 = Unpooled.buffer(segment);
        SpdyFrameDecoderTest.encodeControlFrameHeader(header, type, flags, length);
        segment1.writeInt(SpdyFrameDecoderTest.RANDOM.nextInt());
        segment2.writeInt(SpdyFrameDecoderTest.RANDOM.nextInt());
        decoder.decode(header);
        decoder.decode(segment1);
        decoder.decode(segment2);
        Mockito.verifyZeroInteractions(delegate);
        Assert.assertFalse(header.isReadable());
        Assert.assertFalse(segment1.isReadable());
        Assert.assertFalse(segment2.isReadable());
        header.release();
        segment1.release();
        segment2.release();
    }

    @Test
    public void testSpdyPingFrame() throws Exception {
        short type = 6;
        byte flags = 0;
        int length = 4;
        int id = SpdyFrameDecoderTest.RANDOM.nextInt();
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(id);
        decoder.decode(buf);
        Mockito.verify(delegate).readPingFrame(id);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testUnknownSpdyPingFrameFlags() throws Exception {
        short type = 6;
        byte flags = ((byte) (255));// undefined flags

        int length = 4;
        int id = SpdyFrameDecoderTest.RANDOM.nextInt();
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(id);
        decoder.decode(buf);
        Mockito.verify(delegate).readPingFrame(id);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdyPingFrameLength() throws Exception {
        short type = 6;
        byte flags = 0;
        int length = 8;// invalid length

        int id = SpdyFrameDecoderTest.RANDOM.nextInt();
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(id);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdyGoAwayFrame() throws Exception {
        short type = 7;
        byte flags = 0;
        int length = 8;
        int lastGoodStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        int statusCode = (SpdyFrameDecoderTest.RANDOM.nextInt()) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(lastGoodStreamId);
        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readGoAwayFrame(lastGoodStreamId, statusCode);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testUnknownSpdyGoAwayFrameFlags() throws Exception {
        short type = 7;
        byte flags = ((byte) (255));// undefined flags

        int length = 8;
        int lastGoodStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        int statusCode = (SpdyFrameDecoderTest.RANDOM.nextInt()) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(lastGoodStreamId);
        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readGoAwayFrame(lastGoodStreamId, statusCode);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testReservedSpdyGoAwayFrameBits() throws Exception {
        short type = 7;
        byte flags = 0;
        int length = 8;
        int lastGoodStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        int statusCode = (SpdyFrameDecoderTest.RANDOM.nextInt()) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt((lastGoodStreamId | -2147483648));// should ignore reserved bit

        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readGoAwayFrame(lastGoodStreamId, statusCode);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdyGoAwayFrameLength() throws Exception {
        short type = 7;
        byte flags = 0;
        int length = 12;// invalid length

        int lastGoodStreamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        int statusCode = (SpdyFrameDecoderTest.RANDOM.nextInt()) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(lastGoodStreamId);
        buf.writeInt(statusCode);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdyHeadersFrame() throws Exception {
        short type = 8;
        byte flags = 0;
        int length = 4;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        decoder.decode(buf);
        Mockito.verify(delegate).readHeadersFrame(streamId, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testLastSpdyHeadersFrame() throws Exception {
        short type = 8;
        byte flags = 1;// FLAG_FIN

        int length = 4;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        decoder.decode(buf);
        Mockito.verify(delegate).readHeadersFrame(streamId, true);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testUnknownSpdyHeadersFrameFlags() throws Exception {
        short type = 8;
        byte flags = ((byte) (254));// undefined flags

        int length = 4;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        decoder.decode(buf);
        Mockito.verify(delegate).readHeadersFrame(streamId, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testReservedSpdyHeadersFrameBits() throws Exception {
        short type = 8;
        byte flags = 0;
        int length = 4;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt((streamId | -2147483648));// should ignore reserved bit

        decoder.decode(buf);
        Mockito.verify(delegate).readHeadersFrame(streamId, false);
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdyHeadersFrameLength() throws Exception {
        short type = 8;
        byte flags = 0;
        int length = 0;// invalid length

        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdyHeadersFrameStreamId() throws Exception {
        short type = 8;
        byte flags = 0;
        int length = 4;
        int streamId = 0;// invalid stream identifier

        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testSpdyHeadersFrameHeaderBlock() throws Exception {
        short type = 8;
        byte flags = 0;
        int length = 4;
        int headerBlockLength = 1024;
        int streamId = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, (length + headerBlockLength));
        buf.writeInt(streamId);
        ByteBuf headerBlock = Unpooled.buffer(headerBlockLength);
        for (int i = 0; i < 256; i++) {
            headerBlock.writeInt(SpdyFrameDecoderTest.RANDOM.nextInt());
        }
        decoder.decode(buf);
        decoder.decode(headerBlock);
        Mockito.verify(delegate).readHeadersFrame(streamId, false);
        Mockito.verify(delegate).readHeaderBlock(headerBlock.slice(0, headerBlock.writerIndex()));
        Mockito.verify(delegate).readHeaderBlockEnd();
        Assert.assertFalse(buf.isReadable());
        Assert.assertFalse(headerBlock.isReadable());
        buf.release();
        headerBlock.release();
    }

    @Test
    public void testSpdyWindowUpdateFrame() throws Exception {
        short type = 9;
        byte flags = 0;
        int length = 8;
        int streamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        int deltaWindowSize = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(deltaWindowSize);
        decoder.decode(buf);
        Mockito.verify(delegate).readWindowUpdateFrame(streamId, deltaWindowSize);
        Assert.assertFalse(buf.isReadable());
    }

    @Test
    public void testUnknownSpdyWindowUpdateFrameFlags() throws Exception {
        short type = 9;
        byte flags = ((byte) (255));// undefined flags

        int length = 8;
        int streamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        int deltaWindowSize = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(deltaWindowSize);
        decoder.decode(buf);
        Mockito.verify(delegate).readWindowUpdateFrame(streamId, deltaWindowSize);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testReservedSpdyWindowUpdateFrameBits() throws Exception {
        short type = 9;
        byte flags = 0;
        int length = 8;
        int streamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        int deltaWindowSize = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt((streamId | -2147483648));// should ignore reserved bit

        buf.writeInt((deltaWindowSize | -2147483648));// should ignore reserved bit

        decoder.decode(buf);
        Mockito.verify(delegate).readWindowUpdateFrame(streamId, deltaWindowSize);
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testInvalidSpdyWindowUpdateFrameLength() throws Exception {
        short type = 9;
        byte flags = 0;
        int length = 12;// invalid length

        int streamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        int deltaWindowSize = ((SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647) | 1;
        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(deltaWindowSize);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }

    @Test
    public void testIllegalSpdyWindowUpdateFrameDeltaWindowSize() throws Exception {
        short type = 9;
        byte flags = 0;
        int length = 8;
        int streamId = (SpdyFrameDecoderTest.RANDOM.nextInt()) & 2147483647;
        int deltaWindowSize = 0;// invalid delta window size

        ByteBuf buf = Unpooled.buffer(((SpdyCodecUtil.SPDY_HEADER_SIZE) + length));
        SpdyFrameDecoderTest.encodeControlFrameHeader(buf, type, flags, length);
        buf.writeInt(streamId);
        buf.writeInt(deltaWindowSize);
        decoder.decode(buf);
        Mockito.verify(delegate).readFrameError(anyString());
        Assert.assertFalse(buf.isReadable());
        buf.release();
    }
}

