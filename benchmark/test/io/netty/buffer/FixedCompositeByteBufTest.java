/**
 * Copyright 2013 The Netty Project
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class FixedCompositeByteBufTest {
    @Test(expected = ReadOnlyBufferException.class)
    public void testSetBoolean() {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setBoolean(0, true);
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetByte() {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setByte(0, 1);
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetBytesWithByteBuf() {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        ByteBuf src = wrappedBuffer(new byte[4]);
        try {
            buf.setBytes(0, src);
        } finally {
            buf.release();
            src.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetBytesWithByteBuffer() {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setBytes(0, ByteBuffer.wrap(new byte[4]));
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetBytesWithInputStream() throws IOException {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setBytes(0, new ByteArrayInputStream(new byte[4]), 4);
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetBytesWithChannel() throws IOException {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setBytes(0, new ScatteringByteChannel() {
                @Override
                public long read(ByteBuffer[] dsts, int offset, int length) {
                    return 0;
                }

                @Override
                public long read(ByteBuffer[] dsts) {
                    return 0;
                }

                @Override
                public int read(ByteBuffer dst) {
                    return 0;
                }

                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public void close() {
                }
            }, 4);
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetChar() throws IOException {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setChar(0, 'b');
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetDouble() throws IOException {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setDouble(0, 1);
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetFloat() throws IOException {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setFloat(0, 1);
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetInt() throws IOException {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setInt(0, 1);
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetLong() {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setLong(0, 1);
        } finally {
            buf.release();
        }
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testSetMedium() throws IOException {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(wrappedBuffer(new byte[8]));
        try {
            buf.setMedium(0, 1);
        } finally {
            buf.release();
        }
    }

    @Test
    public void testGatheringWritesHeap() throws Exception {
        FixedCompositeByteBufTest.testGatheringWrites(buffer(), buffer());
    }

    @Test
    public void testGatheringWritesDirect() throws Exception {
        FixedCompositeByteBufTest.testGatheringWrites(directBuffer(), directBuffer());
    }

    @Test
    public void testGatheringWritesMixes() throws Exception {
        FixedCompositeByteBufTest.testGatheringWrites(buffer(), directBuffer());
    }

    @Test
    public void testGatheringWritesHeapPooled() throws Exception {
        FixedCompositeByteBufTest.testGatheringWrites(DEFAULT.heapBuffer(), DEFAULT.heapBuffer());
    }

    @Test
    public void testGatheringWritesDirectPooled() throws Exception {
        FixedCompositeByteBufTest.testGatheringWrites(DEFAULT.directBuffer(), DEFAULT.directBuffer());
    }

    @Test
    public void testGatheringWritesMixesPooled() throws Exception {
        FixedCompositeByteBufTest.testGatheringWrites(DEFAULT.heapBuffer(), DEFAULT.directBuffer());
    }

    @Test
    public void testGatheringWritesPartialHeap() throws Exception {
        FixedCompositeByteBufTest.testGatheringWritesPartial(buffer(), buffer());
    }

    @Test
    public void testGatheringWritesPartialDirect() throws Exception {
        FixedCompositeByteBufTest.testGatheringWritesPartial(directBuffer(), directBuffer());
    }

    @Test
    public void testGatheringWritesPartialMixes() throws Exception {
        FixedCompositeByteBufTest.testGatheringWritesPartial(buffer(), directBuffer());
    }

    @Test
    public void testGatheringWritesPartialHeapPooled() throws Exception {
        FixedCompositeByteBufTest.testGatheringWritesPartial(DEFAULT.heapBuffer(), DEFAULT.heapBuffer());
    }

    @Test
    public void testGatheringWritesPartialDirectPooled() throws Exception {
        FixedCompositeByteBufTest.testGatheringWritesPartial(DEFAULT.directBuffer(), DEFAULT.directBuffer());
    }

    @Test
    public void testGatheringWritesPartialMixesPooled() throws Exception {
        FixedCompositeByteBufTest.testGatheringWritesPartial(DEFAULT.heapBuffer(), DEFAULT.directBuffer());
    }

    @Test
    public void testGatheringWritesSingleHeap() throws Exception {
        FixedCompositeByteBufTest.testGatheringWritesSingleBuf(buffer());
    }

    @Test
    public void testGatheringWritesSingleDirect() throws Exception {
        FixedCompositeByteBufTest.testGatheringWritesSingleBuf(directBuffer());
    }

    @Test
    public void testCopyingToOtherBuffer() {
        ByteBuf buf1 = directBuffer(10);
        ByteBuf buf2 = buffer(10);
        ByteBuf buf3 = directBuffer(10);
        buf1.writeBytes("a".getBytes(Charset.defaultCharset()));
        buf2.writeBytes("b".getBytes(Charset.defaultCharset()));
        buf3.writeBytes("c".getBytes(Charset.defaultCharset()));
        ByteBuf composite = unmodifiableBuffer(buf1, buf2, buf3);
        ByteBuf copy = directBuffer(3);
        ByteBuf copy2 = buffer(3);
        copy.setBytes(0, composite, 0, 3);
        copy2.setBytes(0, composite, 0, 3);
        copy.writerIndex(3);
        copy2.writerIndex(3);
        Assert.assertEquals(0, ByteBufUtil.compare(copy, composite));
        Assert.assertEquals(0, ByteBufUtil.compare(copy2, composite));
        Assert.assertEquals(0, ByteBufUtil.compare(copy, copy2));
        copy.release();
        copy2.release();
        composite.release();
    }

    @Test
    public void testCopyingToOutputStream() throws IOException {
        ByteBuf buf1 = directBuffer(10);
        ByteBuf buf2 = buffer(10);
        ByteBuf buf3 = directBuffer(10);
        buf1.writeBytes("a".getBytes(Charset.defaultCharset()));
        buf2.writeBytes("b".getBytes(Charset.defaultCharset()));
        buf3.writeBytes("c".getBytes(Charset.defaultCharset()));
        ByteBuf composite = unmodifiableBuffer(buf1, buf2, buf3);
        ByteBuf copy = directBuffer(3);
        ByteBuf copy2 = buffer(3);
        OutputStream copyStream = new ByteBufOutputStream(copy);
        OutputStream copy2Stream = new ByteBufOutputStream(copy2);
        try {
            composite.getBytes(0, copyStream, 3);
            composite.getBytes(0, copy2Stream, 3);
            Assert.assertEquals(0, ByteBufUtil.compare(copy, composite));
            Assert.assertEquals(0, ByteBufUtil.compare(copy2, composite));
            Assert.assertEquals(0, ByteBufUtil.compare(copy, copy2));
        } finally {
            copy.release();
            copy2.release();
            copyStream.close();
            copy2Stream.close();
            composite.release();
        }
    }

    @Test
    public void testExtractNioBuffers() {
        ByteBuf buf1 = directBuffer(10);
        ByteBuf buf2 = buffer(10);
        ByteBuf buf3 = directBuffer(10);
        buf1.writeBytes("a".getBytes(Charset.defaultCharset()));
        buf2.writeBytes("b".getBytes(Charset.defaultCharset()));
        buf3.writeBytes("c".getBytes(Charset.defaultCharset()));
        ByteBuf composite = unmodifiableBuffer(buf1, buf2, buf3);
        ByteBuffer[] byteBuffers = composite.nioBuffers(0, 3);
        Assert.assertEquals(3, byteBuffers.length);
        Assert.assertEquals(1, byteBuffers[0].limit());
        Assert.assertEquals(1, byteBuffers[1].limit());
        Assert.assertEquals(1, byteBuffers[2].limit());
        composite.release();
    }

    @Test
    public void testEmptyArray() {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(new ByteBuf[0]);
        buf.release();
    }

    @Test
    public void testHasMemoryAddressWithSingleBuffer() {
        ByteBuf buf1 = directBuffer(10);
        if (!(buf1.hasMemoryAddress())) {
            buf1.release();
            return;
        }
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(buf1);
        Assert.assertTrue(buf.hasMemoryAddress());
        Assert.assertEquals(buf1.memoryAddress(), buf.memoryAddress());
        buf.release();
    }

    @Test
    public void testHasMemoryAddressWhenEmpty() {
        Assume.assumeTrue(EMPTY_BUFFER.hasMemoryAddress());
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(new ByteBuf[0]);
        Assert.assertTrue(buf.hasMemoryAddress());
        Assert.assertEquals(EMPTY_BUFFER.memoryAddress(), buf.memoryAddress());
        buf.release();
    }

    @Test
    public void testHasNoMemoryAddressWhenMultipleBuffers() {
        ByteBuf buf1 = directBuffer(10);
        if (!(buf1.hasMemoryAddress())) {
            buf1.release();
            return;
        }
        ByteBuf buf2 = directBuffer(10);
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(buf1, buf2);
        Assert.assertFalse(buf.hasMemoryAddress());
        try {
            buf.memoryAddress();
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
            // expected
        } finally {
            buf.release();
        }
    }

    @Test
    public void testHasArrayWithSingleBuffer() {
        ByteBuf buf1 = buffer(10);
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(buf1);
        Assert.assertTrue(buf.hasArray());
        Assert.assertArrayEquals(buf1.array(), buf.array());
        buf.release();
    }

    @Test
    public void testHasArrayWhenEmpty() {
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(new ByteBuf[0]);
        Assert.assertTrue(buf.hasArray());
        Assert.assertArrayEquals(EMPTY_BUFFER.array(), buf.array());
        buf.release();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testHasNoArrayWhenMultipleBuffers() {
        ByteBuf buf1 = buffer(10);
        ByteBuf buf2 = buffer(10);
        ByteBuf buf = FixedCompositeByteBufTest.newBuffer(buf1, buf2);
        Assert.assertFalse(buf.hasArray());
        try {
            buf.array();
            Assert.fail();
        } finally {
            buf.release();
        }
    }
}

