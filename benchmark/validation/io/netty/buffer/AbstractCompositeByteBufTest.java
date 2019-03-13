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


import PooledByteBufAllocator.DEFAULT;
import io.netty.util.ReferenceCountUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static UnpooledByteBufAllocator.DEFAULT;


/**
 * An abstract test class for composite channel buffers
 */
public abstract class AbstractCompositeByteBufTest extends AbstractByteBufTest {
    private static final ByteBufAllocator ALLOC = DEFAULT;

    private final ByteOrder order;

    protected AbstractCompositeByteBufTest(ByteOrder order) {
        if (order == null) {
            throw new NullPointerException("order");
        }
        this.order = order;
    }

    /**
     * Tests the "getBufferFor" method
     */
    @Test
    public void testComponentAtOffset() {
        CompositeByteBuf buf = ((CompositeByteBuf) (Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5 }, new byte[]{ 4, 5, 6, 7, 8, 9, 26 })));
        // Ensure that a random place will be fine
        Assert.assertEquals(5, buf.componentAtOffset(2).capacity());
        // Loop through each byte
        byte index = 0;
        while (index < (buf.capacity())) {
            ByteBuf _buf = buf.componentAtOffset((index++));
            Assert.assertNotNull(_buf);
            Assert.assertTrue(((_buf.capacity()) > 0));
            Assert.assertNotNull(_buf.getByte(0));
            Assert.assertNotNull(_buf.getByte(((_buf.readableBytes()) - 1)));
        } 
        buf.release();
    }

    @Test
    public void testToComponentIndex() {
        CompositeByteBuf buf = ((CompositeByteBuf) (Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5 }, new byte[]{ 4, 5, 6, 7, 8, 9, 26 }, new byte[]{ 10, 9, 8, 7, 6, 5, 33 })));
        // spot checks
        Assert.assertEquals(0, buf.toComponentIndex(4));
        Assert.assertEquals(1, buf.toComponentIndex(5));
        Assert.assertEquals(2, buf.toComponentIndex(15));
        // Loop through each byte
        byte index = 0;
        while (index < (buf.capacity())) {
            int cindex = buf.toComponentIndex((index++));
            Assert.assertTrue(((cindex >= 0) && (cindex < (buf.numComponents()))));
        } 
        buf.release();
    }

    @Test
    public void testToByteIndex() {
        CompositeByteBuf buf = ((CompositeByteBuf) (Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5 }, new byte[]{ 4, 5, 6, 7, 8, 9, 26 }, new byte[]{ 10, 9, 8, 7, 6, 5, 33 })));
        // spot checks
        Assert.assertEquals(0, buf.toByteIndex(0));
        Assert.assertEquals(5, buf.toByteIndex(1));
        Assert.assertEquals(12, buf.toByteIndex(2));
        buf.release();
    }

    @Test
    public void testDiscardReadBytes3() {
        ByteBuf a;
        ByteBuf b;
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 0, 5).order(order), Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 5, 5).order(order));
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
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        // now discard
        a.discardReadBytes();
        b.discardReadBytes();
        Assert.assertEquals(a.readerIndex(), b.readerIndex());
        Assert.assertEquals(a.writerIndex(), b.writerIndex());
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.resetReaderIndex();
        b.resetReaderIndex();
        Assert.assertEquals(a.readerIndex(), b.readerIndex());
        a.resetWriterIndex();
        b.resetWriterIndex();
        Assert.assertEquals(a.writerIndex(), b.writerIndex());
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
    }

    @Test
    public void testAutoConsolidation() {
        CompositeByteBuf buf = Unpooled.compositeBuffer(2);
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 1 }));
        Assert.assertEquals(1, buf.numComponents());
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 2, 3 }));
        Assert.assertEquals(2, buf.numComponents());
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 4, 5, 6 }));
        Assert.assertEquals(1, buf.numComponents());
        Assert.assertTrue(buf.hasArray());
        Assert.assertNotNull(buf.array());
        Assert.assertEquals(0, buf.arrayOffset());
        buf.release();
    }

    @Test
    public void testCompositeToSingleBuffer() {
        CompositeByteBuf buf = Unpooled.compositeBuffer(3);
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }));
        Assert.assertEquals(1, buf.numComponents());
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 4 }));
        Assert.assertEquals(2, buf.numComponents());
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 5, 6 }));
        Assert.assertEquals(3, buf.numComponents());
        // NOTE: hard-coding 6 here, since it seems like addComponent doesn't bump the writer index.
        // I'm unsure as to whether or not this is correct behavior
        ByteBuffer nioBuffer = buf.nioBuffer(0, 6);
        byte[] bytes = nioBuffer.array();
        Assert.assertEquals(6, bytes.length);
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 4, 5, 6 }, bytes);
        buf.release();
    }

    @Test
    public void testFullConsolidation() {
        CompositeByteBuf buf = Unpooled.compositeBuffer(Integer.MAX_VALUE);
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 1 }));
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 2, 3 }));
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 4, 5, 6 }));
        buf.consolidate();
        Assert.assertEquals(1, buf.numComponents());
        Assert.assertTrue(buf.hasArray());
        Assert.assertNotNull(buf.array());
        Assert.assertEquals(0, buf.arrayOffset());
        buf.release();
    }

    @Test
    public void testRangedConsolidation() {
        CompositeByteBuf buf = Unpooled.compositeBuffer(Integer.MAX_VALUE);
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 1 }));
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 2, 3 }));
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 4, 5, 6 }));
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 7, 8, 9, 10 }));
        buf.consolidate(1, 2);
        Assert.assertEquals(3, buf.numComponents());
        Assert.assertEquals(Unpooled.wrappedBuffer(new byte[]{ 1 }), buf.component(0));
        Assert.assertEquals(Unpooled.wrappedBuffer(new byte[]{ 2, 3, 4, 5, 6 }), buf.component(1));
        Assert.assertEquals(Unpooled.wrappedBuffer(new byte[]{ 7, 8, 9, 10 }), buf.component(2));
        buf.release();
    }

    @Test
    public void testCompositeWrappedBuffer() {
        ByteBuf header = Unpooled.buffer(12).order(order);
        ByteBuf payload = Unpooled.buffer(512).order(order);
        header.writeBytes(new byte[12]);
        payload.writeBytes(new byte[512]);
        ByteBuf buffer = Unpooled.wrappedBuffer(header, payload);
        Assert.assertEquals(12, header.readableBytes());
        Assert.assertEquals(512, payload.readableBytes());
        Assert.assertEquals((12 + 512), buffer.readableBytes());
        Assert.assertEquals(2, buffer.nioBufferCount());
        buffer.release();
    }

    @Test
    public void testSeveralBuffersEquals() {
        ByteBuf a;
        ByteBuf b;
        // XXX Same tests with several buffers in wrappedCheckedBuffer
        // Different length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1 }).order(order), Unpooled.wrappedBuffer(new byte[]{ 2 }).order(order));
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Same content, same firstIndex, short length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1 }).order(order), Unpooled.wrappedBuffer(new byte[]{ 2 }).order(order), Unpooled.wrappedBuffer(new byte[]{ 3 }).order(order));
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Same content, different firstIndex, short length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4 }, 1, 2).order(order), Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4 }, 3, 1).order(order));
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Different content, same firstIndex, short length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2 }).order(order), Unpooled.wrappedBuffer(new byte[]{ 4 }).order(order));
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Different content, different firstIndex, short length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 4, 5 }, 1, 2).order(order), Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 4, 5 }, 3, 1).order(order));
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Same content, same firstIndex, long length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order), Unpooled.wrappedBuffer(new byte[]{ 4, 5, 6 }).order(order), Unpooled.wrappedBuffer(new byte[]{ 7, 8, 9, 10 }).order(order));
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Same content, different firstIndex, long length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 1, 5).order(order), Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 6, 5).order(order));
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Different content, same firstIndex, long length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 6 }).order(order), Unpooled.wrappedBuffer(new byte[]{ 7, 8, 5, 9, 10 }).order(order));
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        // Different content, different firstIndex, long length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 1, 5).order(order), Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 6, 5).order(order));
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
    }

    @Test
    public void testWrappedBuffer() {
        ByteBuf a = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(ByteBuffer.allocateDirect(16)));
        Assert.assertEquals(16, a.capacity());
        a.release();
        a = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order));
        ByteBuf b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[][]{ new byte[]{ 1, 2, 3 } }).order(order));
        Assert.assertEquals(a, b);
        a.release();
        b.release();
        a = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order));
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1 }, new byte[]{ 2 }, new byte[]{ 3 }).order(order));
        Assert.assertEquals(a, b);
        a.release();
        b.release();
        a = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order));
        b = Unpooled.wrappedBuffer(new ByteBuf[]{ Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order) });
        Assert.assertEquals(a, b);
        a.release();
        b.release();
        a = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order));
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1 }).order(order), Unpooled.wrappedBuffer(new byte[]{ 2 }).order(order), Unpooled.wrappedBuffer(new byte[]{ 3 }).order(order));
        Assert.assertEquals(a, b);
        a.release();
        b.release();
        a = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 })).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new ByteBuffer[]{ ByteBuffer.wrap(new byte[]{ 1, 2, 3 }) }));
        Assert.assertEquals(a, b);
        a.release();
        b.release();
        a = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order));
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(ByteBuffer.wrap(new byte[]{ 1 }), ByteBuffer.wrap(new byte[]{ 2 }), ByteBuffer.wrap(new byte[]{ 3 })));
        Assert.assertEquals(a, b);
        a.release();
        b.release();
    }

    @Test
    public void testWrittenBuffersEquals() {
        // XXX Same tests than testEquals with written AggregateChannelBuffers
        ByteBuf a;
        ByteBuf b;
        ByteBuf c;
        // Different length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1 }, new byte[1])).order(order);
        c = Unpooled.wrappedBuffer(new byte[]{ 2 }).order(order);
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 1));
        b.writeBytes(c);
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        c.release();
        // Same content, same firstIndex, short length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1 }, new byte[2])).order(order);
        c = Unpooled.wrappedBuffer(new byte[]{ 2 }).order(order);
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 2));
        b.writeBytes(c);
        c.release();
        c = Unpooled.wrappedBuffer(new byte[]{ 3 }).order(order);
        b.writeBytes(c);
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        c.release();
        // Same content, different firstIndex, short length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4 }, 1, 3)).order(order);
        c = Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4 }, 3, 1).order(order);
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 1));
        b.writeBytes(c);
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        c.release();
        // Different content, same firstIndex, short length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2 }, new byte[1])).order(order);
        c = Unpooled.wrappedBuffer(new byte[]{ 4 }).order(order);
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 1));
        b.writeBytes(c);
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        c.release();
        // Different content, different firstIndex, short length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 4, 5 }, 1, 3)).order(order);
        c = Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 4, 5 }, 3, 1).order(order);
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 1));
        b.writeBytes(c);
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        c.release();
        // Same content, same firstIndex, long length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3 }, new byte[7])).order(order);
        c = Unpooled.wrappedBuffer(new byte[]{ 4, 5, 6 }).order(order);
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 7));
        b.writeBytes(c);
        c.release();
        c = Unpooled.wrappedBuffer(new byte[]{ 7, 8, 9, 10 }).order(order);
        b.writeBytes(c);
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        c.release();
        // Same content, different firstIndex, long length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 1, 10)).order(order);
        c = Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }, 6, 5).order(order);
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 5));
        b.writeBytes(c);
        Assert.assertTrue(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        c.release();
        // Different content, same firstIndex, long length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 6 }, new byte[5])).order(order);
        c = Unpooled.wrappedBuffer(new byte[]{ 7, 8, 5, 9, 10 }).order(order);
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 5));
        b.writeBytes(c);
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        c.release();
        // Different content, different firstIndex, long length.
        a = Unpooled.wrappedBuffer(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }).order(order);
        b = Unpooled.wrappedBuffer(Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 1, 10)).order(order);
        c = Unpooled.wrappedBuffer(new byte[]{ 0, 1, 2, 3, 4, 6, 7, 8, 5, 9, 10, 11 }, 6, 5).order(order);
        // to enable writeBytes
        b.writerIndex(((b.writerIndex()) - 5));
        b.writeBytes(c);
        Assert.assertFalse(ByteBufUtil.equals(a, b));
        a.release();
        b.release();
        c.release();
    }

    @Test
    public void testEmptyBuffer() {
        ByteBuf b = Unpooled.wrappedBuffer(new byte[]{ 1, 2 }, new byte[]{ 3, 4 });
        b.readBytes(new byte[4]);
        b.readBytes(EMPTY_BYTES);
        b.release();
    }

    // Test for https://github.com/netty/netty/issues/1060
    @Test
    public void testReadWithEmptyCompositeBuffer() {
        ByteBuf buf = Unpooled.compositeBuffer();
        int n = 65;
        for (int i = 0; i < n; i++) {
            buf.writeByte(1);
            Assert.assertEquals(1, buf.readByte());
        }
        buf.release();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testComponentMustBeDuplicate() {
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        buf.addComponent(Unpooled.buffer(4, 6).setIndex(1, 3));
        Assert.assertThat(buf.component(0), CoreMatchers.is(CoreMatchers.instanceOf(AbstractDerivedByteBuf.class)));
        Assert.assertThat(buf.component(0).capacity(), CoreMatchers.is(4));
        Assert.assertThat(buf.component(0).maxCapacity(), CoreMatchers.is(6));
        Assert.assertThat(buf.component(0).readableBytes(), CoreMatchers.is(2));
        buf.release();
    }

    @Test
    public void testReferenceCounts1() {
        ByteBuf c1 = Unpooled.buffer().writeByte(1);
        ByteBuf c2 = Unpooled.buffer().writeByte(2).retain();
        ByteBuf c3 = Unpooled.buffer().writeByte(3).retain(2);
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        Assert.assertThat(buf.refCnt(), CoreMatchers.is(1));
        buf.addComponents(c1, c2, c3);
        Assert.assertThat(buf.refCnt(), CoreMatchers.is(1));
        // Ensure that c[123]'s refCount did not change.
        Assert.assertThat(c1.refCnt(), CoreMatchers.is(1));
        Assert.assertThat(c2.refCnt(), CoreMatchers.is(2));
        Assert.assertThat(c3.refCnt(), CoreMatchers.is(3));
        Assert.assertThat(buf.component(0).refCnt(), CoreMatchers.is(1));
        Assert.assertThat(buf.component(1).refCnt(), CoreMatchers.is(2));
        Assert.assertThat(buf.component(2).refCnt(), CoreMatchers.is(3));
        c3.release(2);
        c2.release();
        buf.release();
    }

    @Test
    public void testReferenceCounts2() {
        ByteBuf c1 = Unpooled.buffer().writeByte(1);
        ByteBuf c2 = Unpooled.buffer().writeByte(2).retain();
        ByteBuf c3 = Unpooled.buffer().writeByte(3).retain(2);
        CompositeByteBuf bufA = Unpooled.compositeBuffer();
        bufA.addComponents(c1, c2, c3).writerIndex(3);
        CompositeByteBuf bufB = Unpooled.compositeBuffer();
        bufB.addComponents(bufA);
        // Ensure that bufA.refCnt() did not change.
        Assert.assertThat(bufA.refCnt(), CoreMatchers.is(1));
        // Ensure that c[123]'s refCnt did not change.
        Assert.assertThat(c1.refCnt(), CoreMatchers.is(1));
        Assert.assertThat(c2.refCnt(), CoreMatchers.is(2));
        Assert.assertThat(c3.refCnt(), CoreMatchers.is(3));
        // This should decrease bufA.refCnt().
        bufB.release();
        Assert.assertThat(bufB.refCnt(), CoreMatchers.is(0));
        // Ensure bufA.refCnt() changed.
        Assert.assertThat(bufA.refCnt(), CoreMatchers.is(0));
        // Ensure that c[123]'s refCnt also changed due to the deallocation of bufA.
        Assert.assertThat(c1.refCnt(), CoreMatchers.is(0));
        Assert.assertThat(c2.refCnt(), CoreMatchers.is(1));
        Assert.assertThat(c3.refCnt(), CoreMatchers.is(2));
        c3.release(2);
        c2.release();
    }

    @Test
    public void testReferenceCounts3() {
        ByteBuf c1 = Unpooled.buffer().writeByte(1);
        ByteBuf c2 = Unpooled.buffer().writeByte(2).retain();
        ByteBuf c3 = Unpooled.buffer().writeByte(3).retain(2);
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        Assert.assertThat(buf.refCnt(), CoreMatchers.is(1));
        List<ByteBuf> components = new ArrayList<ByteBuf>();
        Collections.addAll(components, c1, c2, c3);
        buf.addComponents(components);
        // Ensure that c[123]'s refCount did not change.
        Assert.assertThat(c1.refCnt(), CoreMatchers.is(1));
        Assert.assertThat(c2.refCnt(), CoreMatchers.is(2));
        Assert.assertThat(c3.refCnt(), CoreMatchers.is(3));
        Assert.assertThat(buf.component(0).refCnt(), CoreMatchers.is(1));
        Assert.assertThat(buf.component(1).refCnt(), CoreMatchers.is(2));
        Assert.assertThat(buf.component(2).refCnt(), CoreMatchers.is(3));
        c3.release(2);
        c2.release();
        buf.release();
    }

    @Test
    public void testNestedLayout() {
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        buf.addComponent(Unpooled.compositeBuffer().addComponent(Unpooled.wrappedBuffer(new byte[]{ 1, 2 })).addComponent(Unpooled.wrappedBuffer(new byte[]{ 3, 4 })).slice(1, 2));
        ByteBuffer[] nioBuffers = buf.nioBuffers(0, 2);
        Assert.assertThat(nioBuffers.length, CoreMatchers.is(2));
        Assert.assertThat(nioBuffers[0].remaining(), CoreMatchers.is(1));
        Assert.assertThat(nioBuffers[0].get(), CoreMatchers.is(((byte) (2))));
        Assert.assertThat(nioBuffers[1].remaining(), CoreMatchers.is(1));
        Assert.assertThat(nioBuffers[1].get(), CoreMatchers.is(((byte) (3))));
        buf.release();
    }

    @Test
    public void testRemoveLastComponent() {
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 1, 2 }));
        Assert.assertEquals(1, buf.numComponents());
        buf.removeComponent(0);
        Assert.assertEquals(0, buf.numComponents());
        buf.release();
    }

    @Test
    public void testCopyEmpty() {
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        Assert.assertEquals(0, buf.numComponents());
        ByteBuf copy = buf.copy();
        Assert.assertEquals(0, copy.readableBytes());
        buf.release();
        copy.release();
    }

    @Test
    public void testDuplicateEmpty() {
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        Assert.assertEquals(0, buf.numComponents());
        Assert.assertEquals(0, buf.duplicate().readableBytes());
        buf.release();
    }

    @Test
    public void testRemoveLastComponentWithOthersLeft() {
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 1, 2 }));
        buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 1, 2 }));
        Assert.assertEquals(2, buf.numComponents());
        buf.removeComponent(1);
        Assert.assertEquals(1, buf.numComponents());
        buf.release();
    }

    @Test
    public void testRemoveComponents() {
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        for (int i = 0; i < 10; i++) {
            buf.addComponent(Unpooled.wrappedBuffer(new byte[]{ 1, 2 }));
        }
        Assert.assertEquals(10, buf.numComponents());
        Assert.assertEquals(20, buf.capacity());
        buf.removeComponents(4, 3);
        Assert.assertEquals(7, buf.numComponents());
        Assert.assertEquals(14, buf.capacity());
        buf.release();
    }

    @Test
    public void testGatheringWritesHeap() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWrites(Unpooled.buffer().order(order), Unpooled.buffer().order(order));
    }

    @Test
    public void testGatheringWritesDirect() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWrites(Unpooled.directBuffer().order(order), Unpooled.directBuffer().order(order));
    }

    @Test
    public void testGatheringWritesMixes() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWrites(Unpooled.buffer().order(order), Unpooled.directBuffer().order(order));
    }

    @Test
    public void testGatheringWritesHeapPooled() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWrites(DEFAULT.heapBuffer().order(order), DEFAULT.heapBuffer().order(order));
    }

    @Test
    public void testGatheringWritesDirectPooled() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWrites(DEFAULT.directBuffer().order(order), DEFAULT.directBuffer().order(order));
    }

    @Test
    public void testGatheringWritesMixesPooled() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWrites(DEFAULT.heapBuffer().order(order), DEFAULT.directBuffer().order(order));
    }

    @Test
    public void testGatheringWritesPartialHeap() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(Unpooled.buffer().order(order), Unpooled.buffer().order(order), false);
    }

    @Test
    public void testGatheringWritesPartialDirect() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(Unpooled.directBuffer().order(order), Unpooled.directBuffer().order(order), false);
    }

    @Test
    public void testGatheringWritesPartialMixes() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(Unpooled.buffer().order(order), Unpooled.directBuffer().order(order), false);
    }

    @Test
    public void testGatheringWritesPartialHeapSlice() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(Unpooled.buffer().order(order), Unpooled.buffer().order(order), true);
    }

    @Test
    public void testGatheringWritesPartialDirectSlice() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(Unpooled.directBuffer().order(order), Unpooled.directBuffer().order(order), true);
    }

    @Test
    public void testGatheringWritesPartialMixesSlice() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(Unpooled.buffer().order(order), Unpooled.directBuffer().order(order), true);
    }

    @Test
    public void testGatheringWritesPartialHeapPooled() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(DEFAULT.heapBuffer().order(order), DEFAULT.heapBuffer().order(order), false);
    }

    @Test
    public void testGatheringWritesPartialDirectPooled() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(DEFAULT.directBuffer().order(order), DEFAULT.directBuffer().order(order), false);
    }

    @Test
    public void testGatheringWritesPartialMixesPooled() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(DEFAULT.heapBuffer().order(order), DEFAULT.directBuffer().order(order), false);
    }

    @Test
    public void testGatheringWritesPartialHeapPooledSliced() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(DEFAULT.heapBuffer().order(order), DEFAULT.heapBuffer().order(order), true);
    }

    @Test
    public void testGatheringWritesPartialDirectPooledSliced() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(DEFAULT.directBuffer().order(order), DEFAULT.directBuffer().order(order), true);
    }

    @Test
    public void testGatheringWritesPartialMixesPooledSliced() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesPartial(DEFAULT.heapBuffer().order(order), DEFAULT.directBuffer().order(order), true);
    }

    @Test
    public void testGatheringWritesSingleHeap() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesSingleBuf(Unpooled.buffer().order(order));
    }

    @Test
    public void testGatheringWritesSingleDirect() throws Exception {
        AbstractCompositeByteBufTest.testGatheringWritesSingleBuf(Unpooled.directBuffer().order(order));
    }

    @Override
    @Test
    public void testInternalNioBuffer() {
        // ignore
    }

    @Test
    public void testisDirectMultipleBufs() {
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        Assert.assertFalse(buf.isDirect());
        buf.addComponent(Unpooled.directBuffer().writeByte(1));
        Assert.assertTrue(buf.isDirect());
        buf.addComponent(Unpooled.directBuffer().writeByte(1));
        Assert.assertTrue(buf.isDirect());
        buf.addComponent(Unpooled.buffer().writeByte(1));
        Assert.assertFalse(buf.isDirect());
        buf.release();
    }

    // See https://github.com/netty/netty/issues/1976
    @Test
    public void testDiscardSomeReadBytes() {
        CompositeByteBuf cbuf = Unpooled.compositeBuffer();
        int len = 8 * 4;
        for (int i = 0; i < len; i += 4) {
            ByteBuf buf = Unpooled.buffer().writeInt(i);
            cbuf.capacity(cbuf.writerIndex()).addComponent(buf).writerIndex((i + 4));
        }
        cbuf.writeByte(1);
        byte[] me = new byte[len];
        cbuf.readBytes(me);
        cbuf.readByte();
        cbuf.discardSomeReadBytes();
        cbuf.release();
    }

    @Test
    public void testAddEmptyBufferRelease() {
        CompositeByteBuf cbuf = Unpooled.compositeBuffer();
        ByteBuf buf = Unpooled.buffer();
        Assert.assertEquals(1, buf.refCnt());
        cbuf.addComponent(buf);
        Assert.assertEquals(1, buf.refCnt());
        cbuf.release();
        Assert.assertEquals(0, buf.refCnt());
    }

    @Test
    public void testAddEmptyBuffersRelease() {
        CompositeByteBuf cbuf = Unpooled.compositeBuffer();
        ByteBuf buf = Unpooled.buffer();
        ByteBuf buf2 = Unpooled.buffer().writeInt(1);
        ByteBuf buf3 = Unpooled.buffer();
        Assert.assertEquals(1, buf.refCnt());
        Assert.assertEquals(1, buf2.refCnt());
        Assert.assertEquals(1, buf3.refCnt());
        cbuf.addComponents(buf, buf2, buf3);
        Assert.assertEquals(1, buf.refCnt());
        Assert.assertEquals(1, buf2.refCnt());
        Assert.assertEquals(1, buf3.refCnt());
        cbuf.release();
        Assert.assertEquals(0, buf.refCnt());
        Assert.assertEquals(0, buf2.refCnt());
        Assert.assertEquals(0, buf3.refCnt());
    }

    @Test
    public void testAddEmptyBufferInMiddle() {
        CompositeByteBuf cbuf = Unpooled.compositeBuffer();
        ByteBuf buf1 = Unpooled.buffer().writeByte(((byte) (1)));
        cbuf.addComponent(true, buf1);
        cbuf.addComponent(true, Unpooled.EMPTY_BUFFER);
        ByteBuf buf3 = Unpooled.buffer().writeByte(((byte) (2)));
        cbuf.addComponent(true, buf3);
        Assert.assertEquals(2, cbuf.readableBytes());
        Assert.assertEquals(((byte) (1)), cbuf.readByte());
        Assert.assertEquals(((byte) (2)), cbuf.readByte());
        Assert.assertSame(Unpooled.EMPTY_BUFFER, cbuf.internalComponent(1));
        Assert.assertNotSame(Unpooled.EMPTY_BUFFER, cbuf.internalComponentAtOffset(1));
        cbuf.release();
    }

    @Test
    public void testInsertEmptyBufferInMiddle() {
        CompositeByteBuf cbuf = Unpooled.compositeBuffer();
        ByteBuf buf1 = Unpooled.buffer().writeByte(((byte) (1)));
        cbuf.addComponent(true, buf1);
        ByteBuf buf2 = Unpooled.buffer().writeByte(((byte) (2)));
        cbuf.addComponent(true, buf2);
        // insert empty one between the first two
        cbuf.addComponent(true, 1, Unpooled.EMPTY_BUFFER);
        Assert.assertEquals(2, cbuf.readableBytes());
        Assert.assertEquals(((byte) (1)), cbuf.readByte());
        Assert.assertEquals(((byte) (2)), cbuf.readByte());
        Assert.assertEquals(2, cbuf.capacity());
        Assert.assertEquals(3, cbuf.numComponents());
        byte[] dest = new byte[2];
        // should skip over the empty one, not throw a java.lang.Error :)
        cbuf.getBytes(0, dest);
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, dest);
        cbuf.release();
    }

    @Test
    public void testIterator() {
        CompositeByteBuf cbuf = Unpooled.compositeBuffer();
        cbuf.addComponent(Unpooled.EMPTY_BUFFER);
        cbuf.addComponent(Unpooled.EMPTY_BUFFER);
        Iterator<ByteBuf> it = cbuf.iterator();
        Assert.assertTrue(it.hasNext());
        Assert.assertSame(Unpooled.EMPTY_BUFFER, it.next());
        Assert.assertTrue(it.hasNext());
        Assert.assertSame(Unpooled.EMPTY_BUFFER, it.next());
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // Expected
        }
        cbuf.release();
    }

    @Test
    public void testEmptyIterator() {
        CompositeByteBuf cbuf = Unpooled.compositeBuffer();
        Iterator<ByteBuf> it = cbuf.iterator();
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // Expected
        }
        cbuf.release();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorConcurrentModificationAdd() {
        CompositeByteBuf cbuf = Unpooled.compositeBuffer();
        cbuf.addComponent(Unpooled.EMPTY_BUFFER);
        Iterator<ByteBuf> it = cbuf.iterator();
        cbuf.addComponent(Unpooled.EMPTY_BUFFER);
        Assert.assertTrue(it.hasNext());
        try {
            it.next();
        } finally {
            cbuf.release();
        }
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorConcurrentModificationRemove() {
        CompositeByteBuf cbuf = Unpooled.compositeBuffer();
        cbuf.addComponent(Unpooled.EMPTY_BUFFER);
        Iterator<ByteBuf> it = cbuf.iterator();
        cbuf.removeComponent(0);
        Assert.assertTrue(it.hasNext());
        try {
            it.next();
        } finally {
            cbuf.release();
        }
    }

    @Test
    public void testReleasesItsComponents() {
        ByteBuf buffer = DEFAULT.buffer();// 1

        buffer.writeBytes(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        ByteBuf s1 = buffer.readSlice(2).retain();// 2

        ByteBuf s2 = s1.readSlice(2).retain();// 3

        ByteBuf s3 = s2.readSlice(2).retain();// 4

        ByteBuf s4 = s3.readSlice(2).retain();// 5

        ByteBuf composite = DEFAULT.compositeBuffer().addComponent(s1).addComponents(s2, s3, s4).order(ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(1, composite.refCnt());
        Assert.assertEquals(5, buffer.refCnt());
        // releasing composite should release the 4 components
        ReferenceCountUtil.release(composite);
        Assert.assertEquals(0, composite.refCnt());
        Assert.assertEquals(1, buffer.refCnt());
        // last remaining ref to buffer
        ReferenceCountUtil.release(buffer);
        Assert.assertEquals(0, buffer.refCnt());
    }

    @Test
    public void testReleasesItsComponents2() {
        // It is important to use a pooled allocator here to ensure
        // the slices returned by readRetainedSlice are of type
        // PooledSlicedByteBuf, which maintains an independent refcount
        // (so that we can be sure to cover this case)
        ByteBuf buffer = DEFAULT.buffer();// 1

        buffer.writeBytes(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        // use readRetainedSlice this time - produces different kind of slices
        ByteBuf s1 = buffer.readRetainedSlice(2);// 2

        ByteBuf s2 = s1.readRetainedSlice(2);// 3

        ByteBuf s3 = s2.readRetainedSlice(2);// 4

        ByteBuf s4 = s3.readRetainedSlice(2);// 5

        ByteBuf composite = Unpooled.compositeBuffer().addComponent(s1).addComponents(s2, s3, s4).order(ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(1, composite.refCnt());
        Assert.assertEquals(2, buffer.refCnt());
        // releasing composite should release the 4 components
        composite.release();
        Assert.assertEquals(0, composite.refCnt());
        Assert.assertEquals(1, buffer.refCnt());
        // last remaining ref to buffer
        buffer.release();
        Assert.assertEquals(0, buffer.refCnt());
    }

    @Test
    public void testReleasesOnShrink() {
        ByteBuf b1 = Unpooled.buffer(2).writeShort(1);
        ByteBuf b2 = Unpooled.buffer(2).writeShort(2);
        // composite takes ownership of s1 and s2
        ByteBuf composite = Unpooled.compositeBuffer().addComponents(b1, b2);
        Assert.assertEquals(4, composite.capacity());
        // reduce capacity down to two, will drop the second component
        composite.capacity(2);
        Assert.assertEquals(2, composite.capacity());
        // releasing composite should release the components
        composite.release();
        Assert.assertEquals(0, composite.refCnt());
        Assert.assertEquals(0, b1.refCnt());
        Assert.assertEquals(0, b2.refCnt());
    }

    @Test
    public void testAllocatorIsSameWhenCopy() {
        testAllocatorIsSameWhenCopy(false);
    }

    @Test
    public void testAllocatorIsSameWhenCopyUsingIndexAndLength() {
        testAllocatorIsSameWhenCopy(true);
    }

    @Test
    public void testDecomposeMultiple() {
        AbstractCompositeByteBufTest.testDecompose(150, 500, 3);
    }

    @Test
    public void testDecomposeOne() {
        AbstractCompositeByteBufTest.testDecompose(310, 50, 1);
    }

    @Test
    public void testDecomposeNone() {
        AbstractCompositeByteBufTest.testDecompose(310, 0, 0);
    }

    @Test
    public void testComponentsLessThanLowerBound() {
        try {
            new CompositeByteBuf(AbstractCompositeByteBufTest.ALLOC, true, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("maxNumComponents: 0 (expected: >= 1)", e.getMessage());
        }
    }

    @Test
    public void testComponentsEqualToLowerBound() {
        AbstractCompositeByteBufTest.assertCompositeBufCreated(1);
    }

    @Test
    public void testComponentsGreaterThanLowerBound() {
        AbstractCompositeByteBufTest.assertCompositeBufCreated(5);
    }
}

