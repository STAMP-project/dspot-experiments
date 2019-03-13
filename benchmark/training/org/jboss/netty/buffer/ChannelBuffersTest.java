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
package org.jboss.netty.buffer;


import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests channel buffers
 */
public class ChannelBuffersTest {
    @Test
    public void testCompositeWrappedBuffer() {
        ChannelBuffer header = dynamicBuffer(12);
        ChannelBuffer payload = dynamicBuffer(512);
        header.writeBytes(new byte[12]);
        payload.writeBytes(new byte[512]);
        ChannelBuffer buffer = wrappedBuffer(header, payload);
        Assert.assertEquals(12, header.readableBytes());
        Assert.assertEquals(512, payload.readableBytes());
        Assert.assertEquals((12 + 512), buffer.readableBytes());
        Assert.assertEquals((12 + 512), buffer.toByteBuffer(0, (12 + 512)).remaining());
    }

    @Test
    public void testHashCode() {
        Map<byte[], Integer> map = new LinkedHashMap<byte[], Integer>();
        map.put(new byte[0], 1);
        map.put(new byte[]{ 1 }, 32);
        map.put(new byte[]{ 2 }, 33);
        map.put(new byte[]{ 0, 1 }, 962);
        map.put(new byte[]{ 1, 2 }, 994);
        map.put(new byte[]{ 0, 1, 2, 3, 4, 5 }, 63504931);
        map.put(new byte[]{ 6, 7, 8, 9, 0, 1 }, ((int) (97180294697L)));
        map.put(new byte[]{ -1, -1, -1, ((byte) (225)) }, 1);
        for (Map.Entry<byte[], Integer> e : map.entrySet()) {
            Assert.assertEquals(e.getValue().intValue(), ChannelBuffers.ChannelBuffers.hashCode(wrappedBuffer(e.getKey())));
        }
    }

    @Test
    public void testEquals() {
        ChannelBuffer a;
        ChannelBuffer b;
        // Different length.
        a = wrappedBuffer(new byte[]{ 1 });
        b = wrappedBuffer(new byte[]{ 1, 2 });
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, same firstIndex, short length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(new byte[]{ 1, 2, 3 });
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, different firstIndex, short length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4 }, 1, 3);
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, same firstIndex, short length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(new byte[]{ 1, 2, 4 });
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, different firstIndex, short length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(new byte[]{ 0, 1, 2, 4, 5 }, 1, 3);
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, same firstIndex, long length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, different firstIndex, long length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 1, 10);
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, same firstIndex, long length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 6, 7, 8, 5, 9, 10 });
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, different firstIndex, long length.
        a = wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 1, 10);
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
    }

    @Test
    public void testCompare() {
        List<ChannelBuffer> expected = new ArrayList<ChannelBuffer>();
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
                    Assert.assertEquals(0, compare(expected.get(i), expected.get(j)));
                } else
                    if (i < j) {
                        Assert.assertTrue(((compare(expected.get(i), expected.get(j))) < 0));
                    } else {
                        Assert.assertTrue(((compare(expected.get(i), expected.get(j))) > 0));
                    }

            }
        }
    }

    @Test
    public void shouldReturnEmptyBufferWhenLengthIsZero() {
        Assert.assertSame(EMPTY_BUFFER, buffer(0));
        Assert.assertSame(EMPTY_BUFFER, buffer(LITTLE_ENDIAN, 0));
        Assert.assertSame(EMPTY_BUFFER, directBuffer(0));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(new byte[0]));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(LITTLE_ENDIAN, new byte[0]));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(new byte[8], 0, 0));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(LITTLE_ENDIAN, new byte[8], 0, 0));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(new byte[8], 8, 0));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(LITTLE_ENDIAN, new byte[8], 8, 0));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(ByteBuffer.allocateDirect(0)));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(EMPTY_BUFFER));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(new byte[0][]));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(new byte[][]{ new byte[0] }));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(new ByteBuffer[0]));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(new ByteBuffer[]{ ByteBuffer.allocate(0) }));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(ByteBuffer.allocate(0), ByteBuffer.allocate(0)));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(new ChannelBuffer[0]));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(new ChannelBuffer[]{ buffer(0) }));
        Assert.assertSame(EMPTY_BUFFER, wrappedBuffer(buffer(0), buffer(0)));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(new byte[0]));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(LITTLE_ENDIAN, new byte[0]));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(new byte[8], 0, 0));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(LITTLE_ENDIAN, new byte[8], 0, 0));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(new byte[8], 8, 0));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(LITTLE_ENDIAN, new byte[8], 8, 0));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(ByteBuffer.allocateDirect(0)));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(EMPTY_BUFFER));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(new byte[0][]));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(new byte[][]{ new byte[0] }));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(new ByteBuffer[0]));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(new ByteBuffer[]{ ByteBuffer.allocate(0) }));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(ByteBuffer.allocate(0), ByteBuffer.allocate(0)));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(new ChannelBuffer[0]));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(new ChannelBuffer[]{ buffer(0) }));
        Assert.assertSame(EMPTY_BUFFER, copiedBuffer(buffer(0), buffer(0)));
    }

    @Test
    public void testCompare2() {
        Assert.assertTrue(((compare(wrappedBuffer(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) }), wrappedBuffer(new byte[]{ ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) }))) > 0));
        Assert.assertTrue(((compare(wrappedBuffer(new byte[]{ ((byte) (255)) }), wrappedBuffer(new byte[]{ ((byte) (0)) }))) > 0));
    }

    @Test(expected = NullPointerException.class)
    public void shouldDisallowNullEndian1() {
        buffer(null, 0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldDisallowNullEndian2() {
        directBuffer(null, 0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldDisallowNullEndian3() {
        wrappedBuffer(null, new byte[0]);
    }

    @Test(expected = NullPointerException.class)
    public void shouldDisallowNullEndian4() {
        wrappedBuffer(null, new byte[0], 0, 0);
    }

    @Test
    public void shouldAllowEmptyBufferToCreateCompositeBuffer() {
        ChannelBuffer buf = wrappedBuffer(EMPTY_BUFFER, wrappedBuffer(LITTLE_ENDIAN, new byte[16]), EMPTY_BUFFER);
        Assert.assertEquals(16, buf.capacity());
    }

    @Test
    public void testWrappedBuffer() {
        Assert.assertEquals(16, wrappedBuffer(ByteBuffer.allocateDirect(16)).capacity());
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(new byte[][]{ new byte[]{ 1, 2, 3 } }));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(new ChannelBuffer[]{ wrappedBuffer(new byte[]{ 1, 2, 3 }) }));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(wrappedBuffer(new byte[]{ 1 }), wrappedBuffer(new byte[]{ 2 }), wrappedBuffer(new byte[]{ 3 })));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(new ByteBuffer[]{ ByteBuffer.wrap(new byte[]{ 1, 2, 3 }) }));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), wrappedBuffer(ByteBuffer.wrap(new byte[]{ 1 }), ByteBuffer.wrap(new byte[]{ 2 }), ByteBuffer.wrap(new byte[]{ 3 })));
    }

    @Test
    public void testCopiedBuffer() {
        Assert.assertEquals(16, copiedBuffer(ByteBuffer.allocateDirect(16)).capacity());
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(new byte[][]{ new byte[]{ 1, 2, 3 } }));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(new ChannelBuffer[]{ wrappedBuffer(new byte[]{ 1, 2, 3 }) }));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(wrappedBuffer(new byte[]{ 1 }), wrappedBuffer(new byte[]{ 2 }), wrappedBuffer(new byte[]{ 3 })));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(new ByteBuffer[]{ ByteBuffer.wrap(new byte[]{ 1, 2, 3 }) }));
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3 }), copiedBuffer(ByteBuffer.wrap(new byte[]{ 1 }), ByteBuffer.wrap(new byte[]{ 2 }), ByteBuffer.wrap(new byte[]{ 3 })));
    }

    @Test
    public void testHexDump() {
        Assert.assertEquals("", hexDump(EMPTY_BUFFER));
        Assert.assertEquals("123456", hexDump(wrappedBuffer(new byte[]{ 18, 52, 86 })));
        Assert.assertEquals("1234567890abcdef", hexDump(wrappedBuffer(new byte[]{ 18, 52, 86, 120, ((byte) (144)), ((byte) (171)), ((byte) (205)), ((byte) (239)) })));
    }

    @Test
    public void testSwapMedium() {
        Assert.assertEquals(5649426, swapMedium(1193046));
        Assert.assertEquals(128, swapMedium(8388608));
    }

    @Test
    public void testUnmodifiableBuffer() throws Exception {
        ChannelBuffer buf = unmodifiableBuffer(buffer(16));
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
            buf.setBytes(0, new byte[0], 0, 0);
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
        try {
            buf.setBytes(0, EasyMock.createMock(InputStream.class), 0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        try {
            buf.setBytes(0, EasyMock.createMock(ScatteringByteChannel.class), 0);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    /**
     * Test for https://github.com/netty/netty/issues/449
     */
    @Test
    public void testHexDumpOperations() {
        ChannelBuffer buffer = copiedBuffer("This is some testdata", CharsetUtil.ISO_8859_1);
        String hexDump = hexDump(buffer);
        ChannelBuffer buffer2 = hexDump(hexDump);
        Assert.assertEquals(buffer, buffer2);
        String hexDump2 = hexDump(buffer2);
        Assert.assertEquals(hexDump, hexDump2);
    }
}

