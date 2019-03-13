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


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * An abstract test class for composite channel buffers
 */
public abstract class AbstractCompositeChannelBufferTest extends AbstractChannelBufferTest {
    private final ByteOrder order;

    protected AbstractCompositeChannelBufferTest(ByteOrder order) {
        if (order == null) {
            throw new NullPointerException("order");
        }
        this.order = order;
    }

    private List<ChannelBuffer> buffers;

    private ChannelBuffer buffer;

    @Test
    public void testGetBuffer() {
        CompositeChannelBuffer buf = ((CompositeChannelBuffer) (wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5 }, new byte[]{ 4, 5, 6, 7, 8, 9, 26 })));
        // Ensure that a random place will be fine
        Assert.assertEquals(5, buf.getBuffer(2).capacity());
        // Loop through each byte
        byte index = 0;
        while (index < (buf.capacity())) {
            ChannelBuffer _buf = buf.getBuffer((index++));
            Assert.assertNotNull(_buf);
            Assert.assertTrue(((_buf.capacity()) > 0));
            Assert.assertNotNull(_buf.getByte(0));
            Assert.assertNotNull(_buf.getByte(((_buf.readableBytes()) - 1)));
        } 
    }

    @Test
    public void testDiscardReadBytes3() {
        ChannelBuffer a;
        ChannelBuffer b;
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 0, 5), wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 5, 5));
        a.skipBytes(6);
        a.markReaderIndex();
        b.skipBytes(6);
        b.markReaderIndex();
        Assert.assertEquals(a.readerIndex(), b.readerIndex());
        a.readerIndex(((a.readerIndex()) - 1));
        b.readerIndex(((b.readerIndex()) - 1));
        Assert.assertEquals(a.readerIndex(), b.readerIndex());
        a.writerIndex(((a.writerIndex()) - 1));
        a.markWriterIndex();
        b.writerIndex(((b.writerIndex()) - 1));
        b.markWriterIndex();
        Assert.assertEquals(a.writerIndex(), b.writerIndex());
        a.writerIndex(((a.writerIndex()) + 1));
        b.writerIndex(((b.writerIndex()) + 1));
        Assert.assertEquals(a.writerIndex(), b.writerIndex());
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // now discard
        a.discardReadBytes();
        b.discardReadBytes();
        Assert.assertEquals(a.readerIndex(), b.readerIndex());
        Assert.assertEquals(a.writerIndex(), b.writerIndex());
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        a.resetReaderIndex();
        b.resetReaderIndex();
        Assert.assertEquals(a.readerIndex(), b.readerIndex());
        a.resetWriterIndex();
        b.resetWriterIndex();
        Assert.assertEquals(a.writerIndex(), b.writerIndex());
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
    }

    @Test
    public void testCompositeWrappedBuffer() {
        ChannelBuffer header = dynamicBuffer(order, 12);
        ChannelBuffer payload = dynamicBuffer(order, 512);
        header.writeBytes(new byte[12]);
        payload.writeBytes(new byte[512]);
        ChannelBuffer buffer = wrappedBuffer(header, payload);
        Assert.assertEquals(12, header.readableBytes());
        Assert.assertEquals(512, payload.readableBytes());
        Assert.assertEquals((12 + 512), buffer.readableBytes());
        Assert.assertEquals((12 + 512), buffer.toByteBuffer(0, (12 + 512)).remaining());
    }

    @Test
    public void testSeveralBuffersEquals() {
        ChannelBuffer a;
        ChannelBuffer b;
        // XXX Same tests with several buffers in wrappedCheckedBuffer
        // Different length.
        a = wrappedBuffer(order, new byte[]{ 1 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1 }), wrappedBuffer(order, new byte[]{ 2 }));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, same firstIndex, short length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1 }), wrappedBuffer(order, new byte[]{ 2 }), wrappedBuffer(order, new byte[]{ 3 }));
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, different firstIndex, short length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4 }, 1, 2), wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4 }, 3, 1));
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, same firstIndex, short length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2 }), wrappedBuffer(order, new byte[]{ 4 }));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, different firstIndex, short length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 0, 1, 2, 4, 5 }, 1, 2), wrappedBuffer(order, new byte[]{ 0, 1, 2, 4, 5 }, 3, 1));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, same firstIndex, long length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3 }), wrappedBuffer(order, new byte[]{ 4, 5, 6 }), wrappedBuffer(order, new byte[]{ 7, 8, 9, 10 }));
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, different firstIndex, long length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 1, 5), wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 6, 5));
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, same firstIndex, long length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 6 }), wrappedBuffer(order, new byte[]{ 7, 8, 5, 9, 10 }));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, different firstIndex, long length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 1, 5), wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 6, 5));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
    }

    @Test
    public void testWrappedBuffer() {
        Assert.assertEquals(16, wrappedBuffer(wrappedBuffer(ByteBuffer.allocateDirect(16))).capacity());
        Assert.assertEquals(wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3 })), wrappedBuffer(wrappedBuffer(order, new byte[][]{ new byte[]{ 1, 2, 3 } })));
        Assert.assertEquals(wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3 })), wrappedBuffer(wrappedBuffer(order, new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 })));
        Assert.assertEquals(wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3 })), wrappedBuffer(new ChannelBuffer[]{ wrappedBuffer(order, new byte[]{ 1, 2, 3 }) }));
        Assert.assertEquals(wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3 })), wrappedBuffer(wrappedBuffer(order, new byte[]{ 1 }), wrappedBuffer(order, new byte[]{ 2 }), wrappedBuffer(order, new byte[]{ 3 })));
        Assert.assertEquals(wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3 })), wrappedBuffer(wrappedBuffer(new ByteBuffer[]{ ByteBuffer.wrap(new byte[]{ 1, 2, 3 }) })));
        Assert.assertEquals(wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3 })), wrappedBuffer(wrappedBuffer(ByteBuffer.wrap(new byte[]{ 1 }), ByteBuffer.wrap(new byte[]{ 2 }), ByteBuffer.wrap(new byte[]{ 3 }))));
    }

    @Test
    public void testWrittenBuffersEquals() {
        // XXX Same tests than testEquals with written AggregateChannelBuffers
        ChannelBuffer a;
        ChannelBuffer b;
        // Different length.
        a = wrappedBuffer(order, new byte[]{ 1 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1 }, new byte[1]));
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 1));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 2 }));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, same firstIndex, short length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1 }, new byte[2]));
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 2));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 2 }));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 3 }));
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, different firstIndex, short length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4 }, 1, 3));
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 1));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4 }, 3, 1));
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, same firstIndex, short length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2 }, new byte[1]));
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 1));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 4 }));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, different firstIndex, short length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 0, 1, 2, 4, 5 }, 1, 3));
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 1));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 0, 1, 2, 4, 5 }, 3, 1));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, same firstIndex, long length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3 }, new byte[7]));
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 7));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 4, 5, 6 }));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 7, 8, 9, 10 }));
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Same content, different firstIndex, long length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 1, 10));
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 5));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 6, 5));
        Assert.assertTrue(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, same firstIndex, long length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 6 }, new byte[5]));
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 5));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 7, 8, 5, 9, 10 }));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
        // Different content, different firstIndex, long length.
        a = wrappedBuffer(order, new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        b = wrappedBuffer(wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 1, 10));
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 5));
        b.writeBytes(wrappedBuffer(order, new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 6, 5));
        Assert.assertFalse(ChannelBuffers.ChannelBuffers.equals(a, b));
    }

    // Test for #474
    @Test
    public void testEmptyBuffer() {
        ChannelBuffer b = wrappedBuffer(order, new byte[]{ 1, 2 }, new byte[]{ 3, 4 });
        b.readBytes(new byte[4]);
        b.readBytes(new byte[0]);
    }
}

