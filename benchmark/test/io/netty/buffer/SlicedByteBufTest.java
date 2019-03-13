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


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests sliced channel buffers
 */
public class SlicedByteBufTest extends AbstractByteBufTest {
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullInConstructor() {
        new SlicedByteBuf(null, 0, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testInternalNioBuffer() {
        super.testInternalNioBuffer();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testDuplicateReadGatheringByteChannelMultipleThreads() throws Exception {
        super.testDuplicateReadGatheringByteChannelMultipleThreads();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testSliceReadGatheringByteChannelMultipleThreads() throws Exception {
        super.testSliceReadGatheringByteChannelMultipleThreads();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testDuplicateReadOutputStreamMultipleThreads() throws Exception {
        super.testDuplicateReadOutputStreamMultipleThreads();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testSliceReadOutputStreamMultipleThreads() throws Exception {
        super.testSliceReadOutputStreamMultipleThreads();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testDuplicateBytesInArrayMultipleThreads() throws Exception {
        super.testDuplicateBytesInArrayMultipleThreads();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testSliceBytesInArrayMultipleThreads() throws Exception {
        super.testSliceBytesInArrayMultipleThreads();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testNioBufferExposeOnlyRegion() {
        super.testNioBufferExposeOnlyRegion();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testGetReadOnlyDirectDst() {
        super.testGetReadOnlyDirectDst();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testGetReadOnlyHeapDst() {
        super.testGetReadOnlyHeapDst();
    }

    @Test
    @Override
    public void testLittleEndianWithExpand() {
        // ignore for SlicedByteBuf
    }

    @Test
    @Override
    public void testReadBytes() {
        // ignore for SlicedByteBuf
    }

    @Test
    @Override
    public void testForEachByteDesc2() {
        // Ignore for SlicedByteBuf
    }

    @Test
    @Override
    public void testForEachByte2() {
        // Ignore for SlicedByteBuf
    }

    @Test
    public void testReaderIndexAndMarks() {
        ByteBuf wrapped = Unpooled.buffer(16);
        try {
            wrapped.writerIndex(14);
            wrapped.readerIndex(2);
            wrapped.markWriterIndex();
            wrapped.markReaderIndex();
            ByteBuf slice = wrapped.slice(4, 4);
            Assert.assertEquals(0, slice.readerIndex());
            Assert.assertEquals(4, slice.writerIndex());
            slice.readerIndex(((slice.readerIndex()) + 1));
            slice.resetReaderIndex();
            Assert.assertEquals(0, slice.readerIndex());
            slice.writerIndex(((slice.writerIndex()) - 1));
            slice.resetWriterIndex();
            Assert.assertEquals(0, slice.writerIndex());
        } finally {
            wrapped.release();
        }
    }

    @Test
    public void sliceEmptyNotLeak() {
        ByteBuf buffer = Unpooled.buffer(8).retain();
        Assert.assertEquals(2, buffer.refCnt());
        ByteBuf slice1 = buffer.slice();
        Assert.assertEquals(2, slice1.refCnt());
        ByteBuf slice2 = slice1.slice();
        Assert.assertEquals(2, slice2.refCnt());
        Assert.assertFalse(slice2.release());
        Assert.assertEquals(1, buffer.refCnt());
        Assert.assertEquals(1, slice1.refCnt());
        Assert.assertEquals(1, slice2.refCnt());
        Assert.assertTrue(slice2.release());
        Assert.assertEquals(0, buffer.refCnt());
        Assert.assertEquals(0, slice1.refCnt());
        Assert.assertEquals(0, slice2.refCnt());
    }

    @Override
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBytesByteBuffer() {
        byte[] bytes = new byte[]{ 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        // Ensure destination buffer is bigger then what is wrapped in the ByteBuf.
        ByteBuffer nioBuffer = ByteBuffer.allocate(((bytes.length) + 1));
        ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(bytes).slice(0, ((bytes.length) - 1));
        try {
            wrappedBuffer.getBytes(wrappedBuffer.readerIndex(), nioBuffer);
        } finally {
            wrappedBuffer.release();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testWriteUsAsciiCharSequenceExpand() {
        super.testWriteUsAsciiCharSequenceExpand();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testWriteUtf8CharSequenceExpand() {
        super.testWriteUtf8CharSequenceExpand();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testWriteIso88591CharSequenceExpand() {
        super.testWriteIso88591CharSequenceExpand();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @Override
    public void testWriteUtf16CharSequenceExpand() {
        super.testWriteUtf16CharSequenceExpand();
    }

    @Test
    public void ensureWritableWithEnoughSpaceShouldNotThrow() {
        ByteBuf slice = newBuffer(10);
        ByteBuf unwrapped = slice.unwrap();
        unwrapped.writerIndex(((unwrapped.writerIndex()) + 5));
        slice.writerIndex(slice.readerIndex());
        // Run ensureWritable and verify this doesn't change any indexes.
        int originalWriterIndex = slice.writerIndex();
        int originalReadableBytes = slice.readableBytes();
        slice.ensureWritable((originalWriterIndex - (slice.writerIndex())));
        Assert.assertEquals(originalWriterIndex, slice.writerIndex());
        Assert.assertEquals(originalReadableBytes, slice.readableBytes());
        slice.release();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void ensureWritableWithNotEnoughSpaceShouldThrow() {
        ByteBuf slice = newBuffer(10);
        ByteBuf unwrapped = slice.unwrap();
        unwrapped.writerIndex(((unwrapped.writerIndex()) + 5));
        try {
            slice.ensureWritable(1);
            Assert.fail();
        } finally {
            slice.release();
        }
    }
}

