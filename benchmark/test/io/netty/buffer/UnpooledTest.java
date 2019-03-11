/**
 * Copyright 2012 The Netty Project
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
package io.netty.buffer;


import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests channel buffers
 */
public class UnpooledTest {
    private static final ByteBuf[] EMPTY_BYTE_BUFS = new ByteBuf[0];

    private static final byte[][] EMPTY_BYTES_2D = new byte[0][];

    @Test
    public void testCompositeWrappedBuffer() {
        ByteBuf header = buffer(12);
        ByteBuf payload = buffer(512);
        header.writeBytes(new byte[12]);
        payload.writeBytes(new byte[512]);
        ByteBuf buffer = wrappedBuffer(header, payload);
        Assert.assertEquals(12, header.readableBytes());
        Assert.assertEquals(512, payload.readableBytes());
        Assert.assertEquals((12 + 512), buffer.readableBytes());
        Assert.assertEquals(2, buffer.nioBufferCount());
        buffer.release();
    }

    @Test
    public void testHashCode() {
        Map<byte[], Integer> map = new LinkedHashMap<byte[], Integer>();
        map.put(EMPTY_BYTES, 1);
        map.put(new byte[]{ 1 }, 32);
        map.put(new byte[]{ 2 }, 33);
        map.put(new byte[]{ 0, 1 }, 962);
        map.put(new byte[]{ 1, 2 }, 994);
        map.put(new byte[]{ 0, 1, 2, 3, 4, 5 }, 63504931);
        map.put(new byte[]{ 6, 7, 8, 9, 0, 1 }, ((int) (97180294697L)));
        map.put(new byte[]{ -1, -1, -1, ((byte) (225)) }, 1);
        for (Map.Entry<byte[], Integer> e : map.entrySet()) {
            ByteBuf buffer = wrappedBuffer(e.getKey());
            Assert.assertEquals(e.getValue().intValue(), ByteBufUtil.hashCode(buffer));
            buffer.release();
        }
    }

    @Test
    public void testEquals() {
        ByteBuf a;
        ByteBuf b;
        // Different length.
        a = wrappedBuffer(new byte[]{ 1 });
        b = wrappedBuffer(new byte[]{ 1, 2 });
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Same content, same firstIndex, short length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(new byte[]{ 1, 2, 3 });
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Same content, different firstIndex, short length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4 }, 1, 3);
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Different content, same firstIndex, short length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(new byte[]{ 1, 2, 4 });
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Different content, different firstIndex, short length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(new byte[]{ 0, 1, 2, 4, 5 }, 1, 3);
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Same content, same firstIndex, long length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Same content, different firstIndex, long length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 1, 10);
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Different content, same firstIndex, long length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 6, 7, 8, 5, 9, 10 });
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Different content, different firstIndex, long length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 1, 10);
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
    }

    @Test
    public void testCompare() {
        List<ByteBuf> expected = new ArrayList<ByteBuf>();
        expected.add(wrappedBuffer(new byte[]{ 1 }));
        expected.add(wrappedBuffer(new byte[]{ 1, 2 }));
        expected.add(wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }));
        expected.add(wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }));
        expected.add(wrappedBuffer(new byte[]{ 2 }));
        expected.add(wrappedBuffer(new byte[]{ 2, 3 }));
        expected.add(wrappedBuffer(new byte[]{ 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }));
        expected.add(wrappedBuffer(new byte[]{ 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 }));
        expected.add(wrappedBuffer(new byte[]{ 2, 3, 4 }, 1, 1));
        expected.add(wrappedBuffer(new byte[]{ 1, 2, 3, 4 }, 2, 2));
        expected.add(wrappedBuffer(new byte[]{ 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 }, 1, 10));
        expected.add(wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 }, 2, 12));
        expected.add(wrappedBuffer(new byte[]{ 2, 3, 4, 5 }, 2, 1));
        expected.add(wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5 }, 3, 2));
        expected.add(wrappedBuffer(new byte[]{ 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 }, 2, 10));
        expected.add(wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 }, 3, 12));
        for (int i = 0; i < (expected.size()); i++) {
            for (int j = 0; j < (expected.size()); j++) {
                if (i == j) {
                    Assert.assertEquals(0, ByteBufUtil.compare(expected.get(i), expected.get(j)));
                } else
                    if (i < j) {
                        Assert.assertTrue(((ByteBufUtil.compare(expected.get(i), expected.get(j))) < 0));
                    } else {
                        Assert.assertTrue(((ByteBufUtil.compare(expected.get(i), expected.get(j))) > 0));
                    }

            }
        }
        for (ByteBuf buffer : expected) {
            buffer.release();
        }
    }

    @Test
    public void shouldReturnEmptyBufferWhenLengthIsZero() {
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(EMPTY_BYTES));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(new byte[8], 0, 0));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(new byte[8], 8, 0));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(ByteBuffer.allocateDirect(0)));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(EMPTY_BUFFER));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(UnpooledTest.EMPTY_BYTES_2D));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(new byte[][]{ EMPTY_BYTES }));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(EMPTY_BYTE_BUFFERS));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(new ByteBuffer[]{ ByteBuffer.allocate(0) }));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(ByteBuffer.allocate(0), ByteBuffer.allocate(0)));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(UnpooledTest.EMPTY_BYTE_BUFS));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(new ByteBuf[]{ buffer(0) }));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, wrappedBuffer(buffer(0), buffer(0)));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(EMPTY_BYTES));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(new byte[8], 0, 0));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(new byte[8], 8, 0));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(ByteBuffer.allocateDirect(0)));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(EMPTY_BUFFER));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(UnpooledTest.EMPTY_BYTES_2D));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(new byte[][]{ EMPTY_BYTES }));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(EMPTY_BYTE_BUFFERS));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(new ByteBuffer[]{ ByteBuffer.allocate(0) }));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(ByteBuffer.allocate(0), ByteBuffer.allocate(0)));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(UnpooledTest.EMPTY_BYTE_BUFS));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(new ByteBuf[]{ buffer(0) }));
        UnpooledTest.assertSameAndRelease(EMPTY_BUFFER, copiedBuffer(buffer(0), buffer(0)));
    }

    @Test
    public void testCompare2() {
        ByteBuf expected = wrappedBuffer(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) });
        ByteBuf actual = wrappedBuffer(new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) });
        Assert.assertTrue(((ByteBufUtil.compare(expected, actual)) > 0));
        expected.release();
        actual.release();
        expected = wrappedBuffer(new byte[]{ ((byte) (255)) });
        actual = wrappedBuffer(new byte[]{ ((byte) (0)) });
        Assert.assertTrue(((ByteBufUtil.compare(expected, actual)) > 0));
        expected.release();
        actual.release();
    }

    @Test
    public void shouldAllowEmptyBufferToCreateCompositeBuffer() {
        ByteBuf buf = wrappedBuffer(EMPTY_BUFFER, wrappedBuffer(new byte[16]).order(LITTLE_ENDIAN), EMPTY_BUFFER);
        try {
            Assert.assertEquals(16, buf.capacity());
        } finally {
            buf.release();
        }
    }

    @Test
    public void testWrappedBuffer() {
        ByteBuf buffer = wrappedBuffer(ByteBuffer.allocateDirect(16));
        Assert.assertEquals(16, buffer.capacity());
        buffer.release();
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(new byte[][]{ new byte[]{ 1, 2, 3 } }));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(new ByteBuf[]{ wrappedBuffer(new byte[]{ 1, 2, 3 }) }));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(wrappedBuffer(new byte[]{ 1 }), wrappedBuffer(new byte[]{ 2 }), wrappedBuffer(new byte[]{ 3 })));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(new ByteBuffer[]{ ByteBuffer.wrap(new byte[]{ 1, 2, 3 }) }));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(ByteBuffer.wrap(new byte[]{ 1 }), ByteBuffer.wrap(new byte[]{ 2 }), ByteBuffer.wrap(new byte[]{ 3 })));
    }

    @Test
    public void testSingleWrappedByteBufReleased() {
        ByteBuf buf = buffer(12).writeByte(0);
        ByteBuf wrapped = wrappedBuffer(buf);
        Assert.assertTrue(wrapped.release());
        Assert.assertEquals(0, buf.refCnt());
    }

    @Test
    public void testSingleUnReadableWrappedByteBufReleased() {
        ByteBuf buf = buffer(12);
        ByteBuf wrapped = wrappedBuffer(buf);
        Assert.assertFalse(wrapped.release());// EMPTY_BUFFER cannot be released

        Assert.assertEquals(0, buf.refCnt());
    }

    @Test
    public void testMultiByteBufReleased() {
        ByteBuf buf1 = buffer(12).writeByte(0);
        ByteBuf buf2 = buffer(12).writeByte(0);
        ByteBuf wrapped = wrappedBuffer(16, buf1, buf2);
        Assert.assertTrue(wrapped.release());
        Assert.assertEquals(0, buf1.refCnt());
        Assert.assertEquals(0, buf2.refCnt());
    }

    @Test
    public void testMultiUnReadableByteBufReleased() {
        ByteBuf buf1 = buffer(12);
        ByteBuf buf2 = buffer(12);
        ByteBuf wrapped = wrappedBuffer(16, buf1, buf2);
        Assert.assertFalse(wrapped.release());// EMPTY_BUFFER cannot be released

        Assert.assertEquals(0, buf1.refCnt());
        Assert.assertEquals(0, buf2.refCnt());
    }

    @Test
    public void testCopiedBuffer() {
        ByteBuf copied = copiedBuffer(ByteBuffer.allocateDirect(16));
        Assert.assertEquals(16, copied.capacity());
        copied.release();
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(new byte[][]{ new byte[]{ 1, 2, 3 } }));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(new ByteBuf[]{ wrappedBuffer(new byte[]{ 1, 2, 3 }) }));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(wrappedBuffer(new byte[]{ 1 }), wrappedBuffer(new byte[]{ 2 }), wrappedBuffer(new byte[]{ 3 })));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(new ByteBuffer[]{ ByteBuffer.wrap(new byte[]{ 1, 2, 3 }) }));
        UnpooledTest.assertEqualsAndRelease(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(ByteBuffer.wrap(new byte[]{ 1 }), ByteBuffer.wrap(new byte[]{ 2 }), ByteBuffer.wrap(new byte[]{ 3 })));
    }

    @Test
    public void testHexDump() {
        Assert.assertEquals("", ByteBufUtil.hexDump(EMPTY_BUFFER));
        ByteBuf buffer = wrappedBuffer(new byte[]{ 18, 52, 86 });
        Assert.assertEquals("123456", ByteBufUtil.hexDump(buffer));
        buffer.release();
        buffer = wrappedBuffer(new byte[]{ 18, 52, 86, 120, ((byte) (144)), ((byte) (171)), ((byte) (205)), ((byte) (239)) });
        Assert.assertEquals("1234567890abcdef", ByteBufUtil.hexDump(buffer));
        buffer.release();
    }

    @Test
    public void testSwapMedium() {
        Assert.assertEquals(5649426, ByteBufUtil.swapMedium(1193046));
        Assert.assertEquals(128, ByteBufUtil.swapMedium(8388608));
    }

    @Test
    public void testUnmodifiableBuffer() throws Exception {
        ByteBuf buf = unmodifiableBuffer(buffer(16));
        try {
            buf.discardReadBytes();
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            buf.setByte(0, ((byte) (0)));
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            buf.setBytes(0, EMPTY_BUFFER, 0, 0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            buf.setBytes(0, EMPTY_BYTES, 0, 0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            buf.setBytes(0, ByteBuffer.allocate(0));
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            buf.setShort(0, ((short) (0)));
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            buf.setMedium(0, 0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            buf.setInt(0, 0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            buf.setLong(0, 0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        InputStream inputStream = Mockito.mock(InputStream.class);
        try {
            buf.setBytes(0, inputStream, 0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        Mockito.verifyZeroInteractions(inputStream);
        ScatteringByteChannel scatteringByteChannel = Mockito.mock(ScatteringByteChannel.class);
        try {
            buf.setBytes(0, scatteringByteChannel, 0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        Mockito.verifyZeroInteractions(scatteringByteChannel);
        buf.release();
    }

    @Test
    public void testWrapSingleInt() {
        ByteBuf buffer = copyInt(42);
        Assert.assertEquals(4, buffer.capacity());
        Assert.assertEquals(42, buffer.readInt());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
    }

    @Test
    public void testWrapInt() {
        ByteBuf buffer = copyInt(1, 4);
        Assert.assertEquals(8, buffer.capacity());
        Assert.assertEquals(1, buffer.readInt());
        Assert.assertEquals(4, buffer.readInt());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
        buffer = copyInt(null);
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
        buffer = copyInt(new int[]{  });
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
    }

    @Test
    public void testWrapSingleShort() {
        ByteBuf buffer = copyShort(42);
        Assert.assertEquals(2, buffer.capacity());
        Assert.assertEquals(42, buffer.readShort());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
    }

    @Test
    public void testWrapShortFromShortArray() {
        ByteBuf buffer = copyShort(new short[]{ 1, 4 });
        Assert.assertEquals(4, buffer.capacity());
        Assert.assertEquals(1, buffer.readShort());
        Assert.assertEquals(4, buffer.readShort());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
        buffer = copyShort(((short[]) (null)));
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
        buffer = copyShort(new short[]{  });
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
    }

    @Test
    public void testWrapShortFromIntArray() {
        ByteBuf buffer = copyShort(1, 4);
        Assert.assertEquals(4, buffer.capacity());
        Assert.assertEquals(1, buffer.readShort());
        Assert.assertEquals(4, buffer.readShort());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
        buffer = copyShort(((int[]) (null)));
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
        buffer = copyShort(new int[]{  });
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
    }

    @Test
    public void testWrapSingleMedium() {
        ByteBuf buffer = copyMedium(42);
        Assert.assertEquals(3, buffer.capacity());
        Assert.assertEquals(42, buffer.readMedium());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
    }

    @Test
    public void testWrapMedium() {
        ByteBuf buffer = copyMedium(1, 4);
        Assert.assertEquals(6, buffer.capacity());
        Assert.assertEquals(1, buffer.readMedium());
        Assert.assertEquals(4, buffer.readMedium());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
        buffer = copyMedium(null);
        Assert.assertEquals(0, copyMedium(null).capacity());
        buffer.release();
        buffer = copyMedium(new int[]{  });
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
    }

    @Test
    public void testWrapSingleLong() {
        ByteBuf buffer = copyLong(42);
        Assert.assertEquals(8, buffer.capacity());
        Assert.assertEquals(42, buffer.readLong());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
    }

    @Test
    public void testWrapLong() {
        ByteBuf buffer = copyLong(1, 4);
        Assert.assertEquals(16, buffer.capacity());
        Assert.assertEquals(1, buffer.readLong());
        Assert.assertEquals(4, buffer.readLong());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
        buffer = copyLong(null);
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
        buffer = copyLong(new long[]{  });
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
    }

    @Test
    public void testWrapSingleFloat() {
        ByteBuf buffer = copyFloat(42);
        Assert.assertEquals(4, buffer.capacity());
        Assert.assertEquals(42, buffer.readFloat(), 0.01);
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
    }

    @Test
    public void testWrapFloat() {
        ByteBuf buffer = copyFloat(1, 4);
        Assert.assertEquals(8, buffer.capacity());
        Assert.assertEquals(1, buffer.readFloat(), 0.01);
        Assert.assertEquals(4, buffer.readFloat(), 0.01);
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
        buffer = copyFloat(null);
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
        buffer = copyFloat(new float[]{  });
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
    }

    @Test
    public void testWrapSingleDouble() {
        ByteBuf buffer = copyDouble(42);
        Assert.assertEquals(8, buffer.capacity());
        Assert.assertEquals(42, buffer.readDouble(), 0.01);
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
    }

    @Test
    public void testWrapDouble() {
        ByteBuf buffer = copyDouble(1, 4);
        Assert.assertEquals(16, buffer.capacity());
        Assert.assertEquals(1, buffer.readDouble(), 0.01);
        Assert.assertEquals(4, buffer.readDouble(), 0.01);
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
        buffer = copyDouble(null);
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
        buffer = copyDouble(new double[]{  });
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
    }

    @Test
    public void testWrapBoolean() {
        ByteBuf buffer = copyBoolean(true, false);
        Assert.assertEquals(2, buffer.capacity());
        Assert.assertTrue(buffer.readBoolean());
        Assert.assertFalse(buffer.readBoolean());
        Assert.assertFalse(buffer.isReadable());
        buffer.release();
        buffer = copyBoolean(null);
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
        buffer = copyBoolean(new boolean[]{  });
        Assert.assertEquals(0, buffer.capacity());
        buffer.release();
    }

    @Test
    public void wrappedReadOnlyDirectBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(12);
        for (int i = 0; i < 12; i++) {
            buffer.put(((byte) (i)));
        }
        buffer.flip();
        ByteBuf wrapped = wrappedBuffer(buffer.asReadOnlyBuffer());
        for (int i = 0; i < 12; i++) {
            Assert.assertEquals(((byte) (i)), wrapped.readByte());
        }
        wrapped.release();
    }

    @Test(expected = IllegalArgumentException.class)
    public void skipBytesNegativeLength() {
        ByteBuf buf = buffer(8);
        try {
            buf.skipBytes((-1));
        } finally {
            buf.release();
        }
    }

    // See https://github.com/netty/netty/issues/5597
    @Test
    public void testWrapByteBufArrayStartsWithNonReadable() {
        ByteBuf buffer1 = buffer(8);
        ByteBuf buffer2 = buffer(8).writeZero(8);// Ensure the ByteBuf is readable.

        ByteBuf buffer3 = buffer(8);
        ByteBuf buffer4 = buffer(8).writeZero(8);// Ensure the ByteBuf is readable.

        ByteBuf wrapped = wrappedBuffer(buffer1, buffer2, buffer3, buffer4);
        Assert.assertEquals(16, wrapped.readableBytes());
        Assert.assertTrue(wrapped.release());
        Assert.assertEquals(0, buffer1.refCnt());
        Assert.assertEquals(0, buffer2.refCnt());
        Assert.assertEquals(0, buffer3.refCnt());
        Assert.assertEquals(0, buffer4.refCnt());
        Assert.assertEquals(0, wrapped.refCnt());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBytesByteBuffer() {
        byte[] bytes = new byte[]{ 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        // Ensure destination buffer is bigger then what is wrapped in the ByteBuf.
        ByteBuffer nioBuffer = ByteBuffer.allocate(((bytes.length) + 1));
        ByteBuf wrappedBuffer = wrappedBuffer(bytes);
        try {
            wrappedBuffer.getBytes(wrappedBuffer.readerIndex(), nioBuffer);
        } finally {
            wrappedBuffer.release();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBytesByteBuffer2() {
        byte[] bytes = new byte[]{ 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        // Ensure destination buffer is bigger then what is wrapped in the ByteBuf.
        ByteBuffer nioBuffer = ByteBuffer.allocate(((bytes.length) + 1));
        ByteBuf wrappedBuffer = wrappedBuffer(bytes, 0, bytes.length);
        try {
            wrappedBuffer.getBytes(wrappedBuffer.readerIndex(), nioBuffer);
        } finally {
            wrappedBuffer.release();
        }
    }
}

