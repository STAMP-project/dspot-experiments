/**
 * Copyright 2012 The Netty Project
 * Copy from netty 4.1.32.Final
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
package org.apache.flink.runtime.io.network.buffer;


import CharsetUtil.ISO_8859_1;
import CharsetUtil.US_ASCII;
import CharsetUtil.UTF_16;
import CharsetUtil.UTF_8;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBufUtil;
import org.apache.flink.shaded.netty4.io.netty.util.ByteProcessor;
import org.apache.flink.shaded.netty4.io.netty.util.IllegalReferenceCountException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * An abstract test class for channel buffers.
 *
 * Copy from netty 4.1.32.Final.
 */
public abstract class AbstractByteBufTest extends TestLogger {
    private static final int CAPACITY = 4096;// Must be even


    private static final int BLOCK_SIZE = 128;

    private static final int JAVA_BYTEBUFFER_CONSISTENCY_ITERATIONS = 100;

    private long seed;

    private Random random;

    private ByteBuf buffer;

    @Test
    public void comparableInterfaceNotViolated() {
        Assume.assumeFalse(buffer.isReadOnly());
        buffer.writerIndex(buffer.readerIndex());
        Assume.assumeTrue(((buffer.writableBytes()) >= 4));
        buffer.writeLong(0);
        ByteBuf buffer2 = newBuffer(AbstractByteBufTest.CAPACITY);
        Assume.assumeFalse(buffer2.isReadOnly());
        buffer2.writerIndex(buffer2.readerIndex());
        // Write an unsigned integer that will cause buffer.getUnsignedInt() - buffer2.getUnsignedInt() to underflow the
        // int type and wrap around on the negative side.
        buffer2.writeLong(4026531840L);
        Assert.assertTrue(((buffer.compareTo(buffer2)) < 0));
        Assert.assertTrue(((buffer2.compareTo(buffer)) > 0));
        buffer2.release();
    }

    @Test
    public void initialState() {
        Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.capacity());
        Assert.assertEquals(0, buffer.readerIndex());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void readerIndexBoundaryCheck1() {
        try {
            buffer.writerIndex(0);
        } catch (IndexOutOfBoundsException e) {
            Assert.fail();
        }
        buffer.readerIndex((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void readerIndexBoundaryCheck2() {
        try {
            buffer.writerIndex(buffer.capacity());
        } catch (IndexOutOfBoundsException e) {
            Assert.fail();
        }
        buffer.readerIndex(((buffer.capacity()) + 1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void readerIndexBoundaryCheck3() {
        try {
            buffer.writerIndex(((AbstractByteBufTest.CAPACITY) / 2));
        } catch (IndexOutOfBoundsException e) {
            Assert.fail();
        }
        buffer.readerIndex((((AbstractByteBufTest.CAPACITY) * 3) / 2));
    }

    @Test
    public void readerIndexBoundaryCheck4() {
        buffer.writerIndex(0);
        buffer.readerIndex(0);
        buffer.writerIndex(buffer.capacity());
        buffer.readerIndex(buffer.capacity());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void writerIndexBoundaryCheck1() {
        buffer.writerIndex((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void writerIndexBoundaryCheck2() {
        try {
            buffer.writerIndex(AbstractByteBufTest.CAPACITY);
            buffer.readerIndex(AbstractByteBufTest.CAPACITY);
        } catch (IndexOutOfBoundsException e) {
            Assert.fail();
        }
        buffer.writerIndex(((buffer.capacity()) + 1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void writerIndexBoundaryCheck3() {
        try {
            buffer.writerIndex(AbstractByteBufTest.CAPACITY);
            buffer.readerIndex(((AbstractByteBufTest.CAPACITY) / 2));
        } catch (IndexOutOfBoundsException e) {
            Assert.fail();
        }
        buffer.writerIndex(((AbstractByteBufTest.CAPACITY) / 4));
    }

    @Test
    public void writerIndexBoundaryCheck4() {
        buffer.writerIndex(0);
        buffer.readerIndex(0);
        buffer.writerIndex(AbstractByteBufTest.CAPACITY);
        buffer.writeBytes(ByteBuffer.wrap(EMPTY_BYTES));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanBoundaryCheck1() {
        buffer.getBoolean((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getBooleanBoundaryCheck2() {
        buffer.getBoolean(buffer.capacity());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteBoundaryCheck1() {
        buffer.getByte((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteBoundaryCheck2() {
        buffer.getByte(buffer.capacity());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getShortBoundaryCheck1() {
        buffer.getShort((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getShortBoundaryCheck2() {
        buffer.getShort(((buffer.capacity()) - 1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getMediumBoundaryCheck1() {
        buffer.getMedium((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getMediumBoundaryCheck2() {
        buffer.getMedium(((buffer.capacity()) - 2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIntBoundaryCheck1() {
        buffer.getInt((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getIntBoundaryCheck2() {
        buffer.getInt(((buffer.capacity()) - 3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getLongBoundaryCheck1() {
        buffer.getLong((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getLongBoundaryCheck2() {
        buffer.getLong(((buffer.capacity()) - 7));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteArrayBoundaryCheck1() {
        buffer.getBytes((-1), EMPTY_BYTES);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteArrayBoundaryCheck2() {
        buffer.getBytes((-1), EMPTY_BYTES, 0, 0);
    }

    @Test
    public void getByteArrayBoundaryCheck3() {
        byte[] dst = new byte[4];
        buffer.setInt(0, 16909060);
        try {
            buffer.getBytes(0, dst, (-1), 4);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Success
        }
        // No partial copy is expected.
        Assert.assertEquals(0, dst[0]);
        Assert.assertEquals(0, dst[1]);
        Assert.assertEquals(0, dst[2]);
        Assert.assertEquals(0, dst[3]);
    }

    @Test
    public void getByteArrayBoundaryCheck4() {
        byte[] dst = new byte[4];
        buffer.setInt(0, 16909060);
        try {
            buffer.getBytes(0, dst, 1, 4);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Success
        }
        // No partial copy is expected.
        Assert.assertEquals(0, dst[0]);
        Assert.assertEquals(0, dst[1]);
        Assert.assertEquals(0, dst[2]);
        Assert.assertEquals(0, dst[3]);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteBufferBoundaryCheck() {
        buffer.getBytes((-1), ByteBuffer.allocate(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void copyBoundaryCheck1() {
        buffer.copy((-1), 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void copyBoundaryCheck2() {
        buffer.copy(0, ((buffer.capacity()) + 1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void copyBoundaryCheck3() {
        buffer.copy(((buffer.capacity()) + 1), 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void copyBoundaryCheck4() {
        buffer.copy(buffer.capacity(), 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setIndexBoundaryCheck1() {
        buffer.setIndex((-1), AbstractByteBufTest.CAPACITY);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setIndexBoundaryCheck2() {
        buffer.setIndex(((AbstractByteBufTest.CAPACITY) / 2), ((AbstractByteBufTest.CAPACITY) / 4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setIndexBoundaryCheck3() {
        buffer.setIndex(0, ((AbstractByteBufTest.CAPACITY) + 1));
    }

    @Test
    public void getByteBufferState() {
        ByteBuffer dst = ByteBuffer.allocate(4);
        dst.position(1);
        dst.limit(3);
        buffer.setByte(0, ((byte) (1)));
        buffer.setByte(1, ((byte) (2)));
        buffer.setByte(2, ((byte) (3)));
        buffer.setByte(3, ((byte) (4)));
        buffer.getBytes(1, dst);
        Assert.assertEquals(3, dst.position());
        Assert.assertEquals(3, dst.limit());
        dst.clear();
        Assert.assertEquals(0, dst.get(0));
        Assert.assertEquals(2, dst.get(1));
        Assert.assertEquals(3, dst.get(2));
        Assert.assertEquals(0, dst.get(3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getDirectByteBufferBoundaryCheck() {
        buffer.getBytes((-1), ByteBuffer.allocateDirect(0));
    }

    @Test
    public void getDirectByteBufferState() {
        ByteBuffer dst = ByteBuffer.allocateDirect(4);
        dst.position(1);
        dst.limit(3);
        buffer.setByte(0, ((byte) (1)));
        buffer.setByte(1, ((byte) (2)));
        buffer.setByte(2, ((byte) (3)));
        buffer.setByte(3, ((byte) (4)));
        buffer.getBytes(1, dst);
        Assert.assertEquals(3, dst.position());
        Assert.assertEquals(3, dst.limit());
        dst.clear();
        Assert.assertEquals(0, dst.get(0));
        Assert.assertEquals(2, dst.get(1));
        Assert.assertEquals(3, dst.get(2));
        Assert.assertEquals(0, dst.get(3));
    }

    @Test
    public void testRandomByteAccess() {
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            buffer.setByte(i, value);
        }
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            Assert.assertEquals(value, buffer.getByte(i));
        }
    }

    @Test
    public void testRandomUnsignedByteAccess() {
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            buffer.setByte(i, value);
        }
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i++) {
            int value = (random.nextInt()) & 255;
            Assert.assertEquals(value, buffer.getUnsignedByte(i));
        }
    }

    @Test
    public void testRandomShortAccess() {
        testRandomShortAccess(true);
    }

    @Test
    public void testRandomShortLEAccess() {
        testRandomShortAccess(false);
    }

    @Test
    public void testShortConsistentWithByteBuffer() {
        testShortConsistentWithByteBuffer(true, true);
        testShortConsistentWithByteBuffer(true, false);
        testShortConsistentWithByteBuffer(false, true);
        testShortConsistentWithByteBuffer(false, false);
    }

    @Test
    public void testRandomUnsignedShortAccess() {
        testRandomUnsignedShortAccess(true);
    }

    @Test
    public void testRandomUnsignedShortLEAccess() {
        testRandomUnsignedShortAccess(false);
    }

    @Test
    public void testRandomMediumAccess() {
        testRandomMediumAccess(true);
    }

    @Test
    public void testRandomMediumLEAccess() {
        testRandomMediumAccess(false);
    }

    @Test
    public void testRandomUnsignedMediumAccess() {
        testRandomUnsignedMediumAccess(true);
    }

    @Test
    public void testRandomUnsignedMediumLEAccess() {
        testRandomUnsignedMediumAccess(false);
    }

    @Test
    public void testMediumConsistentWithByteBuffer() {
        testMediumConsistentWithByteBuffer(true, true);
        testMediumConsistentWithByteBuffer(true, false);
        testMediumConsistentWithByteBuffer(false, true);
        testMediumConsistentWithByteBuffer(false, false);
    }

    @Test
    public void testRandomIntAccess() {
        testRandomIntAccess(true);
    }

    @Test
    public void testRandomIntLEAccess() {
        testRandomIntAccess(false);
    }

    @Test
    public void testIntConsistentWithByteBuffer() {
        testIntConsistentWithByteBuffer(true, true);
        testIntConsistentWithByteBuffer(true, false);
        testIntConsistentWithByteBuffer(false, true);
        testIntConsistentWithByteBuffer(false, false);
    }

    @Test
    public void testRandomUnsignedIntAccess() {
        testRandomUnsignedIntAccess(true);
    }

    @Test
    public void testRandomUnsignedIntLEAccess() {
        testRandomUnsignedIntAccess(false);
    }

    @Test
    public void testRandomLongAccess() {
        testRandomLongAccess(true);
    }

    @Test
    public void testRandomLongLEAccess() {
        testRandomLongAccess(false);
    }

    @Test
    public void testLongConsistentWithByteBuffer() {
        testLongConsistentWithByteBuffer(true, true);
        testLongConsistentWithByteBuffer(true, false);
        testLongConsistentWithByteBuffer(false, true);
        testLongConsistentWithByteBuffer(false, false);
    }

    @Test
    public void testRandomFloatAccess() {
        testRandomFloatAccess(true);
    }

    @Test
    public void testRandomFloatLEAccess() {
        testRandomFloatAccess(false);
    }

    @Test
    public void testRandomDoubleAccess() {
        testRandomDoubleAccess(true);
    }

    @Test
    public void testRandomDoubleLEAccess() {
        testRandomDoubleAccess(false);
    }

    @Test
    public void testSetZero() {
        buffer.clear();
        while (buffer.isWritable()) {
            buffer.writeByte(((byte) (255)));
        } 
        for (int i = 0; i < (buffer.capacity());) {
            int length = Math.min(((buffer.capacity()) - i), random.nextInt(32));
            buffer.setZero(i, length);
            i += length;
        }
        for (int i = 0; i < (buffer.capacity()); i++) {
            Assert.assertEquals(0, buffer.getByte(i));
        }
    }

    @Test
    public void testSequentialByteAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.isWritable());
            buffer.writeByte(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.isWritable());
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.isReadable());
            Assert.assertEquals(value, buffer.readByte());
        }
        Assert.assertEquals(buffer.capacity(), buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.isReadable());
        Assert.assertFalse(buffer.isWritable());
    }

    @Test
    public void testSequentialUnsignedByteAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.isWritable());
            buffer.writeByte(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.isWritable());
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i++) {
            int value = (random.nextInt()) & 255;
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.isReadable());
            Assert.assertEquals(value, buffer.readUnsignedByte());
        }
        Assert.assertEquals(buffer.capacity(), buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.isReadable());
        Assert.assertFalse(buffer.isWritable());
    }

    @Test
    public void testSequentialShortAccess() {
        testSequentialShortAccess(true);
    }

    @Test
    public void testSequentialShortLEAccess() {
        testSequentialShortAccess(false);
    }

    @Test
    public void testSequentialUnsignedShortAccess() {
        testSequentialUnsignedShortAccess(true);
    }

    @Test
    public void testSequentialUnsignedShortLEAccess() {
        testSequentialUnsignedShortAccess(true);
    }

    @Test
    public void testSequentialMediumAccess() {
        testSequentialMediumAccess(true);
    }

    @Test
    public void testSequentialMediumLEAccess() {
        testSequentialMediumAccess(false);
    }

    @Test
    public void testSequentialUnsignedMediumAccess() {
        testSequentialUnsignedMediumAccess(true);
    }

    @Test
    public void testSequentialUnsignedMediumLEAccess() {
        testSequentialUnsignedMediumAccess(false);
    }

    @Test
    public void testSequentialIntAccess() {
        testSequentialIntAccess(true);
    }

    @Test
    public void testSequentialIntLEAccess() {
        testSequentialIntAccess(false);
    }

    @Test
    public void testSequentialUnsignedIntAccess() {
        testSequentialUnsignedIntAccess(true);
    }

    @Test
    public void testSequentialUnsignedIntLEAccess() {
        testSequentialUnsignedIntAccess(false);
    }

    @Test
    public void testSequentialLongAccess() {
        testSequentialLongAccess(true);
    }

    @Test
    public void testSequentialLongLEAccess() {
        testSequentialLongAccess(false);
    }

    @Test
    public void testByteArrayTransfer() {
        byte[] value = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(value);
            buffer.setBytes(i, value, random.nextInt(AbstractByteBufTest.BLOCK_SIZE), AbstractByteBufTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            buffer.getBytes(i, value, valueOffset, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue[j], value[j]);
            }
        }
    }

    @Test
    public void testRandomByteArrayTransfer1() {
        byte[] value = new byte[AbstractByteBufTest.BLOCK_SIZE];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(value);
            buffer.setBytes(i, value);
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[AbstractByteBufTest.BLOCK_SIZE];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            buffer.getBytes(i, value);
            for (int j = 0; j < (AbstractByteBufTest.BLOCK_SIZE); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value[j]);
            }
        }
    }

    @Test
    public void testRandomByteArrayTransfer2() {
        byte[] value = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(value);
            buffer.setBytes(i, value, random.nextInt(AbstractByteBufTest.BLOCK_SIZE), AbstractByteBufTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            buffer.getBytes(i, value, valueOffset, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value[j]);
            }
        }
    }

    @Test
    public void testRandomHeapBufferTransfer1() {
        byte[] valueContent = new byte[AbstractByteBufTest.BLOCK_SIZE];
        ByteBuf value = wrappedBuffer(valueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setIndex(0, AbstractByteBufTest.BLOCK_SIZE);
            buffer.setBytes(i, value);
            Assert.assertEquals(AbstractByteBufTest.BLOCK_SIZE, value.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.BLOCK_SIZE, value.writerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[AbstractByteBufTest.BLOCK_SIZE];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            value.clear();
            buffer.getBytes(i, value);
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.BLOCK_SIZE, value.writerIndex());
            for (int j = 0; j < (AbstractByteBufTest.BLOCK_SIZE); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
        }
    }

    @Test
    public void testRandomHeapBufferTransfer2() {
        byte[] valueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf value = wrappedBuffer(valueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            buffer.setBytes(i, value, random.nextInt(AbstractByteBufTest.BLOCK_SIZE), AbstractByteBufTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            buffer.getBytes(i, value, valueOffset, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
        }
    }

    @Test
    public void testRandomDirectBufferTransfer() {
        byte[] tmp = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf value = directBuffer(((AbstractByteBufTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(tmp);
            value.setBytes(0, tmp, 0, value.capacity());
            buffer.setBytes(i, value, random.nextInt(AbstractByteBufTest.BLOCK_SIZE), AbstractByteBufTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        ByteBuf expectedValue = directBuffer(((AbstractByteBufTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(tmp);
            expectedValue.setBytes(0, tmp, 0, expectedValue.capacity());
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            buffer.getBytes(i, value, valueOffset, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
        }
        value.release();
        expectedValue.release();
    }

    @Test
    public void testRandomByteBufferTransfer() {
        ByteBuffer value = ByteBuffer.allocate(((AbstractByteBufTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(value.array());
            value.clear().position(random.nextInt(AbstractByteBufTest.BLOCK_SIZE));
            value.limit(((value.position()) + (AbstractByteBufTest.BLOCK_SIZE)));
            buffer.setBytes(i, value);
        }
        random.setSeed(seed);
        ByteBuffer expectedValue = ByteBuffer.allocate(((AbstractByteBufTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue.array());
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            value.clear().position(valueOffset).limit((valueOffset + (AbstractByteBufTest.BLOCK_SIZE)));
            buffer.getBytes(i, value);
            Assert.assertEquals((valueOffset + (AbstractByteBufTest.BLOCK_SIZE)), value.position());
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.get(j), value.get(j));
            }
        }
    }

    @Test
    public void testSequentialByteArrayTransfer1() {
        byte[] value = new byte[AbstractByteBufTest.BLOCK_SIZE];
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(value);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[AbstractByteBufTest.BLOCK_SIZE];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value);
            for (int j = 0; j < (AbstractByteBufTest.BLOCK_SIZE); j++) {
                Assert.assertEquals(expectedValue[j], value[j]);
            }
        }
    }

    @Test
    public void testSequentialByteArrayTransfer2() {
        byte[] value = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(value);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            int readerIndex = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            buffer.writeBytes(value, readerIndex, AbstractByteBufTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value, valueOffset, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue[j], value[j]);
            }
        }
    }

    @Test
    public void testSequentialHeapBufferTransfer1() {
        byte[] valueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf value = wrappedBuffer(valueContent);
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value, random.nextInt(AbstractByteBufTest.BLOCK_SIZE), AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(valueContent.length, value.writerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value, valueOffset, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(valueContent.length, value.writerIndex());
        }
    }

    @Test
    public void testSequentialHeapBufferTransfer2() {
        byte[] valueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf value = wrappedBuffer(valueContent);
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            int readerIndex = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            value.readerIndex(readerIndex);
            value.writerIndex((readerIndex + (AbstractByteBufTest.BLOCK_SIZE)));
            buffer.writeBytes(value);
            Assert.assertEquals((readerIndex + (AbstractByteBufTest.BLOCK_SIZE)), value.writerIndex());
            Assert.assertEquals(value.writerIndex(), value.readerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            value.readerIndex(valueOffset);
            value.writerIndex(valueOffset);
            buffer.readBytes(value, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(valueOffset, value.readerIndex());
            Assert.assertEquals((valueOffset + (AbstractByteBufTest.BLOCK_SIZE)), value.writerIndex());
        }
    }

    @Test
    public void testSequentialDirectBufferTransfer1() {
        byte[] valueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf value = directBuffer(((AbstractByteBufTest.BLOCK_SIZE) * 2));
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setBytes(0, valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value, random.nextInt(AbstractByteBufTest.BLOCK_SIZE), AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(0, value.writerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            value.setBytes(0, valueContent);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value, valueOffset, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(0, value.writerIndex());
        }
        value.release();
        expectedValue.release();
    }

    @Test
    public void testSequentialDirectBufferTransfer2() {
        byte[] valueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf value = directBuffer(((AbstractByteBufTest.BLOCK_SIZE) * 2));
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setBytes(0, valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            int readerIndex = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            value.readerIndex(0);
            value.writerIndex((readerIndex + (AbstractByteBufTest.BLOCK_SIZE)));
            value.readerIndex(readerIndex);
            buffer.writeBytes(value);
            Assert.assertEquals((readerIndex + (AbstractByteBufTest.BLOCK_SIZE)), value.writerIndex());
            Assert.assertEquals(value.writerIndex(), value.readerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            value.setBytes(0, valueContent);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            value.readerIndex(valueOffset);
            value.writerIndex(valueOffset);
            buffer.readBytes(value, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(valueOffset, value.readerIndex());
            Assert.assertEquals((valueOffset + (AbstractByteBufTest.BLOCK_SIZE)), value.writerIndex());
        }
        value.release();
        expectedValue.release();
    }

    @Test
    public void testSequentialByteBufferBackedHeapBufferTransfer1() {
        byte[] valueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf value = wrappedBuffer(ByteBuffer.allocate(((AbstractByteBufTest.BLOCK_SIZE) * 2)));
        value.writerIndex(0);
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setBytes(0, valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value, random.nextInt(AbstractByteBufTest.BLOCK_SIZE), AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(0, value.writerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            value.setBytes(0, valueContent);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value, valueOffset, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(0, value.writerIndex());
        }
    }

    @Test
    public void testSequentialByteBufferBackedHeapBufferTransfer2() {
        byte[] valueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf value = wrappedBuffer(ByteBuffer.allocate(((AbstractByteBufTest.BLOCK_SIZE) * 2)));
        value.writerIndex(0);
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setBytes(0, valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            int readerIndex = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            value.readerIndex(0);
            value.writerIndex((readerIndex + (AbstractByteBufTest.BLOCK_SIZE)));
            value.readerIndex(readerIndex);
            buffer.writeBytes(value);
            Assert.assertEquals((readerIndex + (AbstractByteBufTest.BLOCK_SIZE)), value.writerIndex());
            Assert.assertEquals(value.writerIndex(), value.readerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractByteBufTest.BLOCK_SIZE) * 2];
        ByteBuf expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            value.setBytes(0, valueContent);
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            value.readerIndex(valueOffset);
            value.writerIndex(valueOffset);
            buffer.readBytes(value, AbstractByteBufTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(valueOffset, value.readerIndex());
            Assert.assertEquals((valueOffset + (AbstractByteBufTest.BLOCK_SIZE)), value.writerIndex());
        }
    }

    @Test
    public void testSequentialByteBufferTransfer() {
        buffer.writerIndex(0);
        ByteBuffer value = ByteBuffer.allocate(((AbstractByteBufTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(value.array());
            value.clear().position(random.nextInt(AbstractByteBufTest.BLOCK_SIZE));
            value.limit(((value.position()) + (AbstractByteBufTest.BLOCK_SIZE)));
            buffer.writeBytes(value);
        }
        random.setSeed(seed);
        ByteBuffer expectedValue = ByteBuffer.allocate(((AbstractByteBufTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue.array());
            int valueOffset = random.nextInt(AbstractByteBufTest.BLOCK_SIZE);
            value.clear().position(valueOffset).limit((valueOffset + (AbstractByteBufTest.BLOCK_SIZE)));
            buffer.readBytes(value);
            Assert.assertEquals((valueOffset + (AbstractByteBufTest.BLOCK_SIZE)), value.position());
            for (int j = valueOffset; j < (valueOffset + (AbstractByteBufTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.get(j), value.get(j));
            }
        }
    }

    @Test
    public void testSequentialCopiedBufferTransfer1() {
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            byte[] value = new byte[AbstractByteBufTest.BLOCK_SIZE];
            random.nextBytes(value);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[AbstractByteBufTest.BLOCK_SIZE];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            ByteBuf actualValue = buffer.readBytes(AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(wrappedBuffer(expectedValue), actualValue);
            // Make sure if it is a copied buffer.
            actualValue.setByte(0, ((byte) ((actualValue.getByte(0)) + 1)));
            Assert.assertFalse(((buffer.getByte(i)) == (actualValue.getByte(0))));
            actualValue.release();
        }
    }

    @Test
    public void testSequentialSlice1() {
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            byte[] value = new byte[AbstractByteBufTest.BLOCK_SIZE];
            random.nextBytes(value);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[AbstractByteBufTest.BLOCK_SIZE];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractByteBufTest.CAPACITY, buffer.writerIndex());
            ByteBuf actualValue = buffer.readSlice(AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(buffer.order(), actualValue.order());
            Assert.assertEquals(wrappedBuffer(expectedValue), actualValue);
            // Make sure if it is a sliced buffer.
            actualValue.setByte(0, ((byte) ((actualValue.getByte(0)) + 1)));
            Assert.assertEquals(buffer.getByte(i), actualValue.getByte(0));
        }
    }

    @Test
    public void testWriteZero() {
        try {
            buffer.writeZero((-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
        buffer.clear();
        while (buffer.isWritable()) {
            buffer.writeByte(((byte) (255)));
        } 
        buffer.clear();
        for (int i = 0; i < (buffer.capacity());) {
            int length = Math.min(((buffer.capacity()) - i), random.nextInt(32));
            buffer.writeZero(length);
            i += length;
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        for (int i = 0; i < (buffer.capacity()); i++) {
            Assert.assertEquals(0, buffer.getByte(i));
        }
    }

    @Test
    public void testDiscardReadBytes() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i += 4) {
            buffer.writeInt(i);
        }
        ByteBuf copy = copiedBuffer(buffer);
        // Make sure there's no effect if called when readerIndex is 0.
        buffer.readerIndex(((AbstractByteBufTest.CAPACITY) / 4));
        buffer.markReaderIndex();
        buffer.writerIndex(((AbstractByteBufTest.CAPACITY) / 3));
        buffer.markWriterIndex();
        buffer.readerIndex(0);
        buffer.writerIndex(((AbstractByteBufTest.CAPACITY) / 2));
        buffer.discardReadBytes();
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(((AbstractByteBufTest.CAPACITY) / 2), buffer.writerIndex());
        Assert.assertEquals(copy.slice(0, ((AbstractByteBufTest.CAPACITY) / 2)), buffer.slice(0, ((AbstractByteBufTest.CAPACITY) / 2)));
        buffer.resetReaderIndex();
        Assert.assertEquals(((AbstractByteBufTest.CAPACITY) / 4), buffer.readerIndex());
        buffer.resetWriterIndex();
        Assert.assertEquals(((AbstractByteBufTest.CAPACITY) / 3), buffer.writerIndex());
        // Make sure bytes after writerIndex is not copied.
        buffer.readerIndex(1);
        buffer.writerIndex(((AbstractByteBufTest.CAPACITY) / 2));
        buffer.discardReadBytes();
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals((((AbstractByteBufTest.CAPACITY) / 2) - 1), buffer.writerIndex());
        Assert.assertEquals(copy.slice(1, (((AbstractByteBufTest.CAPACITY) / 2) - 1)), buffer.slice(0, (((AbstractByteBufTest.CAPACITY) / 2) - 1)));
        if (discardReadBytesDoesNotMoveWritableBytes()) {
            // If writable bytes were copied, the test should fail to avoid unnecessary memory bandwidth consumption.
            Assert.assertFalse(copy.slice(((AbstractByteBufTest.CAPACITY) / 2), ((AbstractByteBufTest.CAPACITY) / 2)).equals(buffer.slice((((AbstractByteBufTest.CAPACITY) / 2) - 1), ((AbstractByteBufTest.CAPACITY) / 2))));
        } else {
            Assert.assertEquals(copy.slice(((AbstractByteBufTest.CAPACITY) / 2), ((AbstractByteBufTest.CAPACITY) / 2)), buffer.slice((((AbstractByteBufTest.CAPACITY) / 2) - 1), ((AbstractByteBufTest.CAPACITY) / 2)));
        }
        // Marks also should be relocated.
        buffer.resetReaderIndex();
        Assert.assertEquals((((AbstractByteBufTest.CAPACITY) / 4) - 1), buffer.readerIndex());
        buffer.resetWriterIndex();
        Assert.assertEquals((((AbstractByteBufTest.CAPACITY) / 3) - 1), buffer.writerIndex());
        copy.release();
    }

    /**
     * The similar test case with {@link #testDiscardReadBytes()} but this one
     * discards a large chunk at once.
     */
    @Test
    public void testDiscardReadBytes2() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i++) {
            buffer.writeByte(((byte) (i)));
        }
        ByteBuf copy = copiedBuffer(buffer);
        // Discard the first (CAPACITY / 2 - 1) bytes.
        buffer.setIndex((((AbstractByteBufTest.CAPACITY) / 2) - 1), ((AbstractByteBufTest.CAPACITY) - 1));
        buffer.discardReadBytes();
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(((AbstractByteBufTest.CAPACITY) / 2), buffer.writerIndex());
        for (int i = 0; i < ((AbstractByteBufTest.CAPACITY) / 2); i++) {
            Assert.assertEquals(copy.slice(((((AbstractByteBufTest.CAPACITY) / 2) - 1) + i), (((AbstractByteBufTest.CAPACITY) / 2) - i)), buffer.slice(i, (((AbstractByteBufTest.CAPACITY) / 2) - i)));
        }
        copy.release();
    }

    @Test
    public void testStreamTransfer1() throws Exception {
        byte[] expected = new byte[buffer.capacity()];
        random.nextBytes(expected);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            ByteArrayInputStream in = new ByteArrayInputStream(expected, i, AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(AbstractByteBufTest.BLOCK_SIZE, buffer.setBytes(i, in, AbstractByteBufTest.BLOCK_SIZE));
            Assert.assertEquals((-1), buffer.setBytes(i, in, 0));
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            buffer.getBytes(i, out, AbstractByteBufTest.BLOCK_SIZE);
        }
        Assert.assertTrue(Arrays.equals(expected, out.toByteArray()));
    }

    @Test
    public void testStreamTransfer2() throws Exception {
        byte[] expected = new byte[buffer.capacity()];
        random.nextBytes(expected);
        buffer.clear();
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            ByteArrayInputStream in = new ByteArrayInputStream(expected, i, AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(in, AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals((i + (AbstractByteBufTest.BLOCK_SIZE)), buffer.writerIndex());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            Assert.assertEquals(i, buffer.readerIndex());
            buffer.readBytes(out, AbstractByteBufTest.BLOCK_SIZE);
            Assert.assertEquals((i + (AbstractByteBufTest.BLOCK_SIZE)), buffer.readerIndex());
        }
        Assert.assertTrue(Arrays.equals(expected, out.toByteArray()));
    }

    @Test
    public void testCopy() {
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            buffer.setByte(i, value);
        }
        final int readerIndex = (AbstractByteBufTest.CAPACITY) / 3;
        final int writerIndex = ((AbstractByteBufTest.CAPACITY) * 2) / 3;
        buffer.setIndex(readerIndex, writerIndex);
        // Make sure all properties are copied.
        ByteBuf copy = buffer.copy();
        Assert.assertEquals(0, copy.readerIndex());
        Assert.assertEquals(buffer.readableBytes(), copy.writerIndex());
        Assert.assertEquals(buffer.readableBytes(), copy.capacity());
        Assert.assertSame(buffer.order(), copy.order());
        for (int i = 0; i < (copy.capacity()); i++) {
            Assert.assertEquals(buffer.getByte((i + readerIndex)), copy.getByte(i));
        }
        // Make sure the buffer content is independent from each other.
        buffer.setByte(readerIndex, ((byte) ((buffer.getByte(readerIndex)) + 1)));
        Assert.assertTrue(((buffer.getByte(readerIndex)) != (copy.getByte(0))));
        copy.setByte(1, ((byte) ((copy.getByte(1)) + 1)));
        Assert.assertTrue(((buffer.getByte((readerIndex + 1))) != (copy.getByte(1))));
        copy.release();
    }

    @Test
    public void testDuplicate() {
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            buffer.setByte(i, value);
        }
        final int readerIndex = (AbstractByteBufTest.CAPACITY) / 3;
        final int writerIndex = ((AbstractByteBufTest.CAPACITY) * 2) / 3;
        buffer.setIndex(readerIndex, writerIndex);
        // Make sure all properties are copied.
        ByteBuf duplicate = buffer.duplicate();
        Assert.assertSame(buffer.order(), duplicate.order());
        Assert.assertEquals(buffer.readableBytes(), duplicate.readableBytes());
        Assert.assertEquals(0, buffer.compareTo(duplicate));
        // Make sure the buffer content is shared.
        buffer.setByte(readerIndex, ((byte) ((buffer.getByte(readerIndex)) + 1)));
        Assert.assertEquals(buffer.getByte(readerIndex), duplicate.getByte(duplicate.readerIndex()));
        duplicate.setByte(duplicate.readerIndex(), ((byte) ((duplicate.getByte(duplicate.readerIndex())) + 1)));
        Assert.assertEquals(buffer.getByte(readerIndex), duplicate.getByte(duplicate.readerIndex()));
    }

    @Test
    public void testSliceEndianness() throws Exception {
        Assert.assertEquals(buffer.order(), buffer.slice(0, buffer.capacity()).order());
        Assert.assertEquals(buffer.order(), buffer.slice(0, ((buffer.capacity()) - 1)).order());
        Assert.assertEquals(buffer.order(), buffer.slice(1, ((buffer.capacity()) - 1)).order());
        Assert.assertEquals(buffer.order(), buffer.slice(1, ((buffer.capacity()) - 2)).order());
    }

    @Test
    public void testSliceIndex() throws Exception {
        Assert.assertEquals(0, buffer.slice(0, buffer.capacity()).readerIndex());
        Assert.assertEquals(0, buffer.slice(0, ((buffer.capacity()) - 1)).readerIndex());
        Assert.assertEquals(0, buffer.slice(1, ((buffer.capacity()) - 1)).readerIndex());
        Assert.assertEquals(0, buffer.slice(1, ((buffer.capacity()) - 2)).readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.slice(0, buffer.capacity()).writerIndex());
        Assert.assertEquals(((buffer.capacity()) - 1), buffer.slice(0, ((buffer.capacity()) - 1)).writerIndex());
        Assert.assertEquals(((buffer.capacity()) - 1), buffer.slice(1, ((buffer.capacity()) - 1)).writerIndex());
        Assert.assertEquals(((buffer.capacity()) - 2), buffer.slice(1, ((buffer.capacity()) - 2)).writerIndex());
    }

    @Test
    public void testRetainedSliceIndex() throws Exception {
        ByteBuf retainedSlice = buffer.retainedSlice(0, buffer.capacity());
        Assert.assertEquals(0, retainedSlice.readerIndex());
        retainedSlice.release();
        retainedSlice = buffer.retainedSlice(0, ((buffer.capacity()) - 1));
        Assert.assertEquals(0, retainedSlice.readerIndex());
        retainedSlice.release();
        retainedSlice = buffer.retainedSlice(1, ((buffer.capacity()) - 1));
        Assert.assertEquals(0, retainedSlice.readerIndex());
        retainedSlice.release();
        retainedSlice = buffer.retainedSlice(1, ((buffer.capacity()) - 2));
        Assert.assertEquals(0, retainedSlice.readerIndex());
        retainedSlice.release();
        retainedSlice = buffer.retainedSlice(0, buffer.capacity());
        Assert.assertEquals(buffer.capacity(), retainedSlice.writerIndex());
        retainedSlice.release();
        retainedSlice = buffer.retainedSlice(0, ((buffer.capacity()) - 1));
        Assert.assertEquals(((buffer.capacity()) - 1), retainedSlice.writerIndex());
        retainedSlice.release();
        retainedSlice = buffer.retainedSlice(1, ((buffer.capacity()) - 1));
        Assert.assertEquals(((buffer.capacity()) - 1), retainedSlice.writerIndex());
        retainedSlice.release();
        retainedSlice = buffer.retainedSlice(1, ((buffer.capacity()) - 2));
        Assert.assertEquals(((buffer.capacity()) - 2), retainedSlice.writerIndex());
        retainedSlice.release();
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void testEquals() {
        Assert.assertFalse(buffer.equals(null));
        Assert.assertFalse(buffer.equals(new Object()));
        byte[] value = new byte[32];
        buffer.setIndex(0, value.length);
        random.nextBytes(value);
        buffer.setBytes(0, value);
        Assert.assertEquals(buffer, wrappedBuffer(value));
        Assert.assertEquals(buffer, wrappedBuffer(value).order(LITTLE_ENDIAN));
        (value[0])++;
        Assert.assertFalse(buffer.equals(wrappedBuffer(value)));
        Assert.assertFalse(buffer.equals(wrappedBuffer(value).order(LITTLE_ENDIAN)));
    }

    @Test
    public void testCompareTo() {
        try {
            buffer.compareTo(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        // Fill the random stuff
        byte[] value = new byte[32];
        random.nextBytes(value);
        // Prevent overflow / underflow
        if ((value[0]) == 0) {
            (value[0])++;
        } else
            if ((value[0]) == (-1)) {
                (value[0])--;
            }

        buffer.setIndex(0, value.length);
        buffer.setBytes(0, value);
        Assert.assertEquals(0, buffer.compareTo(wrappedBuffer(value)));
        Assert.assertEquals(0, buffer.compareTo(wrappedBuffer(value).order(LITTLE_ENDIAN)));
        (value[0])++;
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(value))) < 0));
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(value).order(LITTLE_ENDIAN))) < 0));
        value[0] -= 2;
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(value))) > 0));
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(value).order(LITTLE_ENDIAN))) > 0));
        (value[0])++;
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(value, 0, 31))) > 0));
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(value, 0, 31).order(LITTLE_ENDIAN))) > 0));
        Assert.assertTrue(((buffer.slice(0, 31).compareTo(wrappedBuffer(value))) < 0));
        Assert.assertTrue(((buffer.slice(0, 31).compareTo(wrappedBuffer(value).order(LITTLE_ENDIAN))) < 0));
        ByteBuf retainedSlice = buffer.retainedSlice(0, 31);
        Assert.assertTrue(((retainedSlice.compareTo(wrappedBuffer(value))) < 0));
        retainedSlice.release();
        retainedSlice = buffer.retainedSlice(0, 31);
        Assert.assertTrue(((retainedSlice.compareTo(wrappedBuffer(value).order(LITTLE_ENDIAN))) < 0));
        retainedSlice.release();
    }

    @Test
    public void testCompareTo2() {
        byte[] bytes = new byte[]{ 1, 2, 3, 4 };
        byte[] bytesReversed = new byte[]{ 4, 3, 2, 1 };
        ByteBuf buf1 = newBuffer(4).clear().writeBytes(bytes).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuf buf2 = newBuffer(4).clear().writeBytes(bytesReversed).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuf buf3 = newBuffer(4).clear().writeBytes(bytes).order(ByteOrder.BIG_ENDIAN);
        ByteBuf buf4 = newBuffer(4).clear().writeBytes(bytesReversed).order(ByteOrder.BIG_ENDIAN);
        try {
            Assert.assertEquals(buf1.compareTo(buf2), buf3.compareTo(buf4));
            Assert.assertEquals(buf2.compareTo(buf1), buf4.compareTo(buf3));
            Assert.assertEquals(buf1.compareTo(buf3), buf2.compareTo(buf4));
            Assert.assertEquals(buf3.compareTo(buf1), buf4.compareTo(buf2));
        } finally {
            buf1.release();
            buf2.release();
            buf3.release();
            buf4.release();
        }
    }

    @Test
    public void testToString() {
        ByteBuf copied = copiedBuffer("Hello, World!", ISO_8859_1);
        buffer.clear();
        buffer.writeBytes(copied);
        Assert.assertEquals("Hello, World!", buffer.toString(ISO_8859_1));
        copied.release();
    }

    @Test(timeout = 10000)
    public void testToStringMultipleThreads() throws Throwable {
        buffer.clear();
        buffer.writeBytes("Hello, World!".getBytes(ISO_8859_1));
        final AtomicInteger counter = new AtomicInteger(30000);
        final AtomicReference<Throwable> errorRef = new AtomicReference<Throwable>();
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (((errorRef.get()) == null) && ((counter.decrementAndGet()) > 0)) {
                            Assert.assertEquals("Hello, World!", buffer.toString(ISO_8859_1));
                        } 
                    } catch (Throwable cause) {
                        errorRef.compareAndSet(null, cause);
                    }
                }
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        Throwable error = errorRef.get();
        if (error != null) {
            throw error;
        }
    }

    @Test
    public void testIndexOf() {
        buffer.clear();
        buffer.writeByte(((byte) (1)));
        buffer.writeByte(((byte) (2)));
        buffer.writeByte(((byte) (3)));
        buffer.writeByte(((byte) (2)));
        buffer.writeByte(((byte) (1)));
        Assert.assertEquals((-1), buffer.indexOf(1, 4, ((byte) (1))));
        Assert.assertEquals((-1), buffer.indexOf(4, 1, ((byte) (1))));
        Assert.assertEquals(1, buffer.indexOf(1, 4, ((byte) (2))));
        Assert.assertEquals(3, buffer.indexOf(4, 1, ((byte) (2))));
    }

    @Test
    public void testNioBuffer1() {
        Assume.assumeTrue(((buffer.nioBufferCount()) == 1));
        byte[] value = new byte[buffer.capacity()];
        random.nextBytes(value);
        buffer.clear();
        buffer.writeBytes(value);
        AbstractByteBufTest.assertRemainingEquals(ByteBuffer.wrap(value), buffer.nioBuffer());
    }

    @Test
    public void testToByteBuffer2() {
        Assume.assumeTrue(((buffer.nioBufferCount()) == 1));
        byte[] value = new byte[buffer.capacity()];
        random.nextBytes(value);
        buffer.clear();
        buffer.writeBytes(value);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractByteBufTest.BLOCK_SIZE)) + 1); i += AbstractByteBufTest.BLOCK_SIZE) {
            AbstractByteBufTest.assertRemainingEquals(ByteBuffer.wrap(value, i, AbstractByteBufTest.BLOCK_SIZE), buffer.nioBuffer(i, AbstractByteBufTest.BLOCK_SIZE));
        }
    }

    @Test
    public void testToByteBuffer3() {
        Assume.assumeTrue(((buffer.nioBufferCount()) == 1));
        Assert.assertEquals(buffer.order(), buffer.nioBuffer().order());
    }

    @Test
    public void testSkipBytes1() {
        buffer.setIndex(((AbstractByteBufTest.CAPACITY) / 4), ((AbstractByteBufTest.CAPACITY) / 2));
        buffer.skipBytes(((AbstractByteBufTest.CAPACITY) / 4));
        Assert.assertEquals((((AbstractByteBufTest.CAPACITY) / 4) * 2), buffer.readerIndex());
        try {
            buffer.skipBytes((((AbstractByteBufTest.CAPACITY) / 4) + 1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Should remain unchanged.
        Assert.assertEquals((((AbstractByteBufTest.CAPACITY) / 4) * 2), buffer.readerIndex());
    }

    @Test
    public void testHashCode() {
        ByteBuf elemA = buffer(15);
        ByteBuf elemB = directBuffer(15);
        elemA.writeBytes(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5 });
        elemB.writeBytes(new byte[]{ 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 9 });
        Set<ByteBuf> set = new HashSet<ByteBuf>();
        set.add(elemA);
        set.add(elemB);
        Assert.assertEquals(2, set.size());
        ByteBuf elemACopy = elemA.copy();
        Assert.assertTrue(set.contains(elemACopy));
        ByteBuf elemBCopy = elemB.copy();
        Assert.assertTrue(set.contains(elemBCopy));
        buffer.clear();
        buffer.writeBytes(elemA.duplicate());
        Assert.assertTrue(set.remove(buffer));
        Assert.assertFalse(set.contains(elemA));
        Assert.assertEquals(1, set.size());
        buffer.clear();
        buffer.writeBytes(elemB.duplicate());
        Assert.assertTrue(set.remove(buffer));
        Assert.assertFalse(set.contains(elemB));
        Assert.assertEquals(0, set.size());
        elemA.release();
        elemB.release();
        elemACopy.release();
        elemBCopy.release();
    }

    // Test case for https://github.com/netty/netty/issues/325
    @Test
    public void testDiscardAllReadBytes() {
        buffer.writerIndex(buffer.capacity());
        buffer.readerIndex(buffer.writerIndex());
        buffer.discardReadBytes();
    }

    @Test
    public void testForEachByte() {
        buffer.clear();
        for (int i = 0; i < (AbstractByteBufTest.CAPACITY); i++) {
            buffer.writeByte((i + 1));
        }
        final AtomicInteger lastIndex = new AtomicInteger();
        buffer.setIndex(((AbstractByteBufTest.CAPACITY) / 4), (((AbstractByteBufTest.CAPACITY) * 3) / 4));
        Assert.assertThat(buffer.forEachByte(new ByteProcessor() {
            int i = (AbstractByteBufTest.CAPACITY) / 4;

            @Override
            public boolean process(byte value) throws Exception {
                Assert.assertThat(value, CoreMatchers.is(((byte) ((i) + 1))));
                lastIndex.set(i);
                (i)++;
                return true;
            }
        }), CoreMatchers.is((-1)));
        Assert.assertThat(lastIndex.get(), CoreMatchers.is(((((AbstractByteBufTest.CAPACITY) * 3) / 4) - 1)));
    }

    @Test
    public void testForEachByteAbort() {
        buffer.clear();
        for (int i = 0; i < (AbstractByteBufTest.CAPACITY); i++) {
            buffer.writeByte((i + 1));
        }
        final int stop = (AbstractByteBufTest.CAPACITY) / 2;
        Assert.assertThat(buffer.forEachByte(((AbstractByteBufTest.CAPACITY) / 3), ((AbstractByteBufTest.CAPACITY) / 3), new ByteProcessor() {
            int i = (AbstractByteBufTest.CAPACITY) / 3;

            @Override
            public boolean process(byte value) throws Exception {
                Assert.assertThat(value, CoreMatchers.is(((byte) ((i) + 1))));
                if ((i) == stop) {
                    return false;
                }
                (i)++;
                return true;
            }
        }), CoreMatchers.is(stop));
    }

    @Test
    public void testForEachByteDesc() {
        buffer.clear();
        for (int i = 0; i < (AbstractByteBufTest.CAPACITY); i++) {
            buffer.writeByte((i + 1));
        }
        final AtomicInteger lastIndex = new AtomicInteger();
        Assert.assertThat(buffer.forEachByteDesc(((AbstractByteBufTest.CAPACITY) / 4), (((AbstractByteBufTest.CAPACITY) * 2) / 4), new ByteProcessor() {
            int i = (((AbstractByteBufTest.CAPACITY) * 3) / 4) - 1;

            @Override
            public boolean process(byte value) throws Exception {
                Assert.assertThat(value, CoreMatchers.is(((byte) ((i) + 1))));
                lastIndex.set(i);
                (i)--;
                return true;
            }
        }), CoreMatchers.is((-1)));
        Assert.assertThat(lastIndex.get(), CoreMatchers.is(((AbstractByteBufTest.CAPACITY) / 4)));
    }

    @Test
    public void testInternalNioBuffer() {
        testInternalNioBuffer(128);
        testInternalNioBuffer(1024);
        testInternalNioBuffer((4 * 1024));
        testInternalNioBuffer((64 * 1024));
        testInternalNioBuffer(((32 * 1024) * 1024));
        testInternalNioBuffer(((64 * 1024) * 1024));
    }

    @Test
    public void testDuplicateReadGatheringByteChannelMultipleThreads() throws Exception {
        testReadGatheringByteChannelMultipleThreads(false);
    }

    @Test
    public void testSliceReadGatheringByteChannelMultipleThreads() throws Exception {
        testReadGatheringByteChannelMultipleThreads(true);
    }

    @Test
    public void testDuplicateReadOutputStreamMultipleThreads() throws Exception {
        testReadOutputStreamMultipleThreads(false);
    }

    @Test
    public void testSliceReadOutputStreamMultipleThreads() throws Exception {
        testReadOutputStreamMultipleThreads(true);
    }

    @Test
    public void testDuplicateBytesInArrayMultipleThreads() throws Exception {
        testBytesInArrayMultipleThreads(false);
    }

    @Test
    public void testSliceBytesInArrayMultipleThreads() throws Exception {
        testBytesInArrayMultipleThreads(true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void readByteThrowsIndexOutOfBoundsException() {
        final ByteBuf buffer = newBuffer(8);
        try {
            buffer.writeByte(0);
            Assert.assertEquals(((byte) (0)), buffer.readByte());
            buffer.readByte();
        } finally {
            buffer.release();
        }
    }

    @Test
    @SuppressWarnings("ForLoopThatDoesntUseLoopVariable")
    public void testNioBufferExposeOnlyRegion() {
        final ByteBuf buffer = newBuffer(8);
        byte[] data = new byte[8];
        random.nextBytes(data);
        buffer.writeBytes(data);
        ByteBuffer nioBuf = buffer.nioBuffer(1, ((data.length) - 2));
        Assert.assertEquals(0, nioBuf.position());
        Assert.assertEquals(6, nioBuf.remaining());
        for (int i = 1; nioBuf.hasRemaining(); i++) {
            Assert.assertEquals(data[i], nioBuf.get());
        }
        buffer.release();
    }

    @Test
    public void ensureWritableWithForceDoesNotThrow() {
        ensureWritableDoesNotThrow(true);
    }

    @Test
    public void ensureWritableWithOutForceDoesNotThrow() {
        ensureWritableDoesNotThrow(false);
    }

    // See:
    // - https://github.com/netty/netty/issues/2587
    // - https://github.com/netty/netty/issues/2580
    @Test
    public void testLittleEndianWithExpand() {
        ByteBuf buffer = newBuffer(0).order(LITTLE_ENDIAN);
        buffer.writeInt(305419896);
        Assert.assertEquals("78563412", ByteBufUtil.hexDump(buffer));
        buffer.release();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testDiscardReadBytesAfterRelease() {
        releasedBuffer().discardReadBytes();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testDiscardSomeReadBytesAfterRelease() {
        releasedBuffer().discardSomeReadBytes();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testEnsureWritableAfterRelease() {
        releasedBuffer().ensureWritable(16);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetBooleanAfterRelease() {
        releasedBuffer().getBoolean(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetByteAfterRelease() {
        releasedBuffer().getByte(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetUnsignedByteAfterRelease() {
        releasedBuffer().getUnsignedByte(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetShortAfterRelease() {
        releasedBuffer().getShort(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetShortLEAfterRelease() {
        releasedBuffer().getShortLE(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetUnsignedShortAfterRelease() {
        releasedBuffer().getUnsignedShort(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetUnsignedShortLEAfterRelease() {
        releasedBuffer().getUnsignedShortLE(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetMediumAfterRelease() {
        releasedBuffer().getMedium(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetMediumLEAfterRelease() {
        releasedBuffer().getMediumLE(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetUnsignedMediumAfterRelease() {
        releasedBuffer().getUnsignedMedium(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetIntAfterRelease() {
        releasedBuffer().getInt(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetIntLEAfterRelease() {
        releasedBuffer().getIntLE(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetUnsignedIntAfterRelease() {
        releasedBuffer().getUnsignedInt(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetUnsignedIntLEAfterRelease() {
        releasedBuffer().getUnsignedIntLE(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetLongAfterRelease() {
        releasedBuffer().getLong(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetLongLEAfterRelease() {
        releasedBuffer().getLongLE(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetCharAfterRelease() {
        releasedBuffer().getChar(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetFloatAfterRelease() {
        releasedBuffer().getFloat(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetFloatLEAfterRelease() {
        releasedBuffer().getFloatLE(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetDoubleAfterRelease() {
        releasedBuffer().getDouble(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetDoubleLEAfterRelease() {
        releasedBuffer().getDoubleLE(0);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetBytesAfterRelease() {
        ByteBuf buffer = buffer(8);
        try {
            releasedBuffer().getBytes(0, buffer);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetBytesAfterRelease2() {
        ByteBuf buffer = buffer();
        try {
            releasedBuffer().getBytes(0, buffer, 1);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetBytesAfterRelease3() {
        ByteBuf buffer = buffer();
        try {
            releasedBuffer().getBytes(0, buffer, 0, 1);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetBytesAfterRelease4() {
        releasedBuffer().getBytes(0, new byte[8]);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetBytesAfterRelease5() {
        releasedBuffer().getBytes(0, new byte[8], 0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetBytesAfterRelease6() {
        releasedBuffer().getBytes(0, ByteBuffer.allocate(8));
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetBytesAfterRelease7() throws IOException {
        releasedBuffer().getBytes(0, new ByteArrayOutputStream(), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testGetBytesAfterRelease8() throws IOException {
        releasedBuffer().getBytes(0, new AbstractByteBufTest.DevNullGatheringByteChannel(), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetBooleanAfterRelease() {
        releasedBuffer().setBoolean(0, true);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetByteAfterRelease() {
        releasedBuffer().setByte(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetShortAfterRelease() {
        releasedBuffer().setShort(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetShortLEAfterRelease() {
        releasedBuffer().setShortLE(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetMediumAfterRelease() {
        releasedBuffer().setMedium(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetMediumLEAfterRelease() {
        releasedBuffer().setMediumLE(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetIntAfterRelease() {
        releasedBuffer().setInt(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetIntLEAfterRelease() {
        releasedBuffer().setIntLE(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetLongAfterRelease() {
        releasedBuffer().setLong(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetLongLEAfterRelease() {
        releasedBuffer().setLongLE(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetCharAfterRelease() {
        releasedBuffer().setChar(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetFloatAfterRelease() {
        releasedBuffer().setFloat(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetDoubleAfterRelease() {
        releasedBuffer().setDouble(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetBytesAfterRelease() {
        ByteBuf buffer = buffer();
        try {
            releasedBuffer().setBytes(0, buffer);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetBytesAfterRelease2() {
        ByteBuf buffer = buffer();
        try {
            releasedBuffer().setBytes(0, buffer, 1);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetBytesAfterRelease3() {
        ByteBuf buffer = buffer();
        try {
            releasedBuffer().setBytes(0, buffer, 0, 1);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetUsAsciiCharSequenceAfterRelease() {
        testSetCharSequenceAfterRelease0(US_ASCII);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetIso88591CharSequenceAfterRelease() {
        testSetCharSequenceAfterRelease0(ISO_8859_1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetUtf8CharSequenceAfterRelease() {
        testSetCharSequenceAfterRelease0(UTF_8);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetUtf16CharSequenceAfterRelease() {
        testSetCharSequenceAfterRelease0(UTF_16);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetBytesAfterRelease4() {
        releasedBuffer().setBytes(0, new byte[8]);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetBytesAfterRelease5() {
        releasedBuffer().setBytes(0, new byte[8], 0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetBytesAfterRelease6() {
        releasedBuffer().setBytes(0, ByteBuffer.allocate(8));
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetBytesAfterRelease7() throws IOException {
        releasedBuffer().setBytes(0, new ByteArrayInputStream(new byte[8]), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetBytesAfterRelease8() throws IOException {
        releasedBuffer().setBytes(0, new AbstractByteBufTest.TestScatteringByteChannel(), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSetZeroAfterRelease() {
        releasedBuffer().setZero(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBooleanAfterRelease() {
        releasedBuffer().readBoolean();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadByteAfterRelease() {
        releasedBuffer().readByte();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadUnsignedByteAfterRelease() {
        releasedBuffer().readUnsignedByte();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadShortAfterRelease() {
        releasedBuffer().readShort();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadShortLEAfterRelease() {
        releasedBuffer().readShortLE();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadUnsignedShortAfterRelease() {
        releasedBuffer().readUnsignedShort();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadUnsignedShortLEAfterRelease() {
        releasedBuffer().readUnsignedShortLE();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadMediumAfterRelease() {
        releasedBuffer().readMedium();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadMediumLEAfterRelease() {
        releasedBuffer().readMediumLE();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadUnsignedMediumAfterRelease() {
        releasedBuffer().readUnsignedMedium();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadUnsignedMediumLEAfterRelease() {
        releasedBuffer().readUnsignedMediumLE();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadIntAfterRelease() {
        releasedBuffer().readInt();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadIntLEAfterRelease() {
        releasedBuffer().readIntLE();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadUnsignedIntAfterRelease() {
        releasedBuffer().readUnsignedInt();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadUnsignedIntLEAfterRelease() {
        releasedBuffer().readUnsignedIntLE();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadLongAfterRelease() {
        releasedBuffer().readLong();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadLongLEAfterRelease() {
        releasedBuffer().readLongLE();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadCharAfterRelease() {
        releasedBuffer().readChar();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadFloatAfterRelease() {
        releasedBuffer().readFloat();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadFloatLEAfterRelease() {
        releasedBuffer().readFloatLE();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadDoubleAfterRelease() {
        releasedBuffer().readDouble();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadDoubleLEAfterRelease() {
        releasedBuffer().readDoubleLE();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease() {
        releasedBuffer().readBytes(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease2() {
        ByteBuf buffer = buffer(8);
        try {
            releasedBuffer().readBytes(buffer);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease3() {
        ByteBuf buffer = buffer(8);
        try {
            releasedBuffer().readBytes(buffer);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease4() {
        ByteBuf buffer = buffer(8);
        try {
            releasedBuffer().readBytes(buffer, 0, 1);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease5() {
        releasedBuffer().readBytes(new byte[8]);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease6() {
        releasedBuffer().readBytes(new byte[8], 0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease7() {
        releasedBuffer().readBytes(ByteBuffer.allocate(8));
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease8() throws IOException {
        releasedBuffer().readBytes(new ByteArrayOutputStream(), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease9() throws IOException {
        releasedBuffer().readBytes(new ByteArrayOutputStream(), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testReadBytesAfterRelease10() throws IOException {
        releasedBuffer().readBytes(new AbstractByteBufTest.DevNullGatheringByteChannel(), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteBooleanAfterRelease() {
        releasedBuffer().writeBoolean(true);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteByteAfterRelease() {
        releasedBuffer().writeByte(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteShortAfterRelease() {
        releasedBuffer().writeShort(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteShortLEAfterRelease() {
        releasedBuffer().writeShortLE(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteMediumAfterRelease() {
        releasedBuffer().writeMedium(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteMediumLEAfterRelease() {
        releasedBuffer().writeMediumLE(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteIntAfterRelease() {
        releasedBuffer().writeInt(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteIntLEAfterRelease() {
        releasedBuffer().writeIntLE(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteLongAfterRelease() {
        releasedBuffer().writeLong(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteLongLEAfterRelease() {
        releasedBuffer().writeLongLE(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteCharAfterRelease() {
        releasedBuffer().writeChar(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteFloatAfterRelease() {
        releasedBuffer().writeFloat(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteFloatLEAfterRelease() {
        releasedBuffer().writeFloatLE(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteDoubleAfterRelease() {
        releasedBuffer().writeDouble(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteDoubleLEAfterRelease() {
        releasedBuffer().writeDoubleLE(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteBytesAfterRelease() {
        ByteBuf buffer = buffer(8);
        try {
            releasedBuffer().writeBytes(buffer);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteBytesAfterRelease2() {
        ByteBuf buffer = copiedBuffer(new byte[8]);
        try {
            releasedBuffer().writeBytes(buffer, 1);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteBytesAfterRelease3() {
        ByteBuf buffer = buffer(8);
        try {
            releasedBuffer().writeBytes(buffer, 0, 1);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteBytesAfterRelease4() {
        releasedBuffer().writeBytes(new byte[8]);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteBytesAfterRelease5() {
        releasedBuffer().writeBytes(new byte[8], 0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteBytesAfterRelease6() {
        releasedBuffer().writeBytes(ByteBuffer.allocate(8));
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteBytesAfterRelease7() throws IOException {
        releasedBuffer().writeBytes(new ByteArrayInputStream(new byte[8]), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteBytesAfterRelease8() throws IOException {
        releasedBuffer().writeBytes(new AbstractByteBufTest.TestScatteringByteChannel(), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteZeroAfterRelease() throws IOException {
        releasedBuffer().writeZero(1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteUsAsciiCharSequenceAfterRelease() {
        testWriteCharSequenceAfterRelease0(US_ASCII);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteIso88591CharSequenceAfterRelease() {
        testWriteCharSequenceAfterRelease0(ISO_8859_1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteUtf8CharSequenceAfterRelease() {
        testWriteCharSequenceAfterRelease0(UTF_8);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testWriteUtf16CharSequenceAfterRelease() {
        testWriteCharSequenceAfterRelease0(UTF_16);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testForEachByteAfterRelease() {
        releasedBuffer().forEachByte(new AbstractByteBufTest.TestByteProcessor());
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testForEachByteAfterRelease1() {
        releasedBuffer().forEachByte(0, 1, new AbstractByteBufTest.TestByteProcessor());
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testForEachByteDescAfterRelease() {
        releasedBuffer().forEachByteDesc(new AbstractByteBufTest.TestByteProcessor());
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testForEachByteDescAfterRelease1() {
        releasedBuffer().forEachByteDesc(0, 1, new AbstractByteBufTest.TestByteProcessor());
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testCopyAfterRelease() {
        releasedBuffer().copy();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testCopyAfterRelease1() {
        releasedBuffer().copy();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testNioBufferAfterRelease() {
        releasedBuffer().nioBuffer();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testNioBufferAfterRelease1() {
        releasedBuffer().nioBuffer(0, 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testInternalNioBufferAfterRelease() {
        ByteBuf releasedBuffer = releasedBuffer();
        releasedBuffer.internalNioBuffer(releasedBuffer.readerIndex(), 1);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testNioBuffersAfterRelease() {
        releasedBuffer().nioBuffers();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testNioBuffersAfterRelease2() {
        releasedBuffer().nioBuffers(0, 1);
    }

    @Test
    public void testArrayAfterRelease() {
        ByteBuf buf = releasedBuffer();
        if (buf.hasArray()) {
            try {
                buf.array();
                Assert.fail();
            } catch (IllegalReferenceCountException e) {
                // expected
            }
        }
    }

    @Test
    public void testMemoryAddressAfterRelease() {
        ByteBuf buf = releasedBuffer();
        if (buf.hasMemoryAddress()) {
            try {
                buf.memoryAddress();
                Assert.fail();
            } catch (IllegalReferenceCountException e) {
                // expected
            }
        }
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSliceAfterRelease() {
        releasedBuffer().slice();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testSliceAfterRelease2() {
        releasedBuffer().slice(0, 1);
    }

    @Test
    public void testSliceAfterReleaseRetainedSlice() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedSlice(0, 1);
        AbstractByteBufTest.assertSliceFailAfterRelease(buf, buf2);
    }

    @Test
    public void testSliceAfterReleaseRetainedSliceDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedSlice(0, 1);
        ByteBuf buf3 = buf2.duplicate();
        AbstractByteBufTest.assertSliceFailAfterRelease(buf, buf2, buf3);
    }

    @Test
    public void testSliceAfterReleaseRetainedSliceRetainedDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedSlice(0, 1);
        ByteBuf buf3 = buf2.retainedDuplicate();
        AbstractByteBufTest.assertSliceFailAfterRelease(buf, buf2, buf3);
    }

    @Test
    public void testSliceAfterReleaseRetainedDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedDuplicate();
        AbstractByteBufTest.assertSliceFailAfterRelease(buf, buf2);
    }

    @Test
    public void testSliceAfterReleaseRetainedDuplicateSlice() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedDuplicate();
        ByteBuf buf3 = buf2.slice(0, 1);
        AbstractByteBufTest.assertSliceFailAfterRelease(buf, buf2, buf3);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testRetainedSliceAfterRelease() {
        releasedBuffer().retainedSlice();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testRetainedSliceAfterRelease2() {
        releasedBuffer().retainedSlice(0, 1);
    }

    @Test
    public void testRetainedSliceAfterReleaseRetainedSlice() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedSlice(0, 1);
        AbstractByteBufTest.assertRetainedSliceFailAfterRelease(buf, buf2);
    }

    @Test
    public void testRetainedSliceAfterReleaseRetainedSliceDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedSlice(0, 1);
        ByteBuf buf3 = buf2.duplicate();
        AbstractByteBufTest.assertRetainedSliceFailAfterRelease(buf, buf2, buf3);
    }

    @Test
    public void testRetainedSliceAfterReleaseRetainedSliceRetainedDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedSlice(0, 1);
        ByteBuf buf3 = buf2.retainedDuplicate();
        AbstractByteBufTest.assertRetainedSliceFailAfterRelease(buf, buf2, buf3);
    }

    @Test
    public void testRetainedSliceAfterReleaseRetainedDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedDuplicate();
        AbstractByteBufTest.assertRetainedSliceFailAfterRelease(buf, buf2);
    }

    @Test
    public void testRetainedSliceAfterReleaseRetainedDuplicateSlice() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedDuplicate();
        ByteBuf buf3 = buf2.slice(0, 1);
        AbstractByteBufTest.assertRetainedSliceFailAfterRelease(buf, buf2, buf3);
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testDuplicateAfterRelease() {
        releasedBuffer().duplicate();
    }

    @Test(expected = IllegalReferenceCountException.class)
    public void testRetainedDuplicateAfterRelease() {
        releasedBuffer().retainedDuplicate();
    }

    @Test
    public void testDuplicateAfterReleaseRetainedSliceDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedSlice(0, 1);
        ByteBuf buf3 = buf2.duplicate();
        AbstractByteBufTest.assertDuplicateFailAfterRelease(buf, buf2, buf3);
    }

    @Test
    public void testDuplicateAfterReleaseRetainedDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedDuplicate();
        AbstractByteBufTest.assertDuplicateFailAfterRelease(buf, buf2);
    }

    @Test
    public void testDuplicateAfterReleaseRetainedDuplicateSlice() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedDuplicate();
        ByteBuf buf3 = buf2.slice(0, 1);
        AbstractByteBufTest.assertDuplicateFailAfterRelease(buf, buf2, buf3);
    }

    @Test
    public void testRetainedDuplicateAfterReleaseRetainedDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedDuplicate();
        AbstractByteBufTest.assertRetainedDuplicateFailAfterRelease(buf, buf2);
    }

    @Test
    public void testRetainedDuplicateAfterReleaseDuplicate() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.duplicate();
        AbstractByteBufTest.assertRetainedDuplicateFailAfterRelease(buf, buf2);
    }

    @Test
    public void testRetainedDuplicateAfterReleaseRetainedSlice() {
        ByteBuf buf = newBuffer(1);
        ByteBuf buf2 = buf.retainedSlice(0, 1);
        AbstractByteBufTest.assertRetainedDuplicateFailAfterRelease(buf, buf2);
    }

    @Test
    public void testSliceRelease() {
        ByteBuf buf = newBuffer(8);
        Assert.assertEquals(1, buf.refCnt());
        Assert.assertTrue(buf.slice().release());
        Assert.assertEquals(0, buf.refCnt());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadSliceOutOfBounds() {
        testReadSliceOutOfBounds(false);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadRetainedSliceOutOfBounds() {
        testReadSliceOutOfBounds(true);
    }

    @Test
    public void testWriteUsAsciiCharSequenceExpand() {
        testWriteCharSequenceExpand(US_ASCII);
    }

    @Test
    public void testWriteUtf8CharSequenceExpand() {
        testWriteCharSequenceExpand(UTF_8);
    }

    @Test
    public void testWriteIso88591CharSequenceExpand() {
        testWriteCharSequenceExpand(ISO_8859_1);
    }

    @Test
    public void testWriteUtf16CharSequenceExpand() {
        testWriteCharSequenceExpand(UTF_16);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetUsAsciiCharSequenceNoExpand() {
        testSetCharSequenceNoExpand(US_ASCII);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetUtf8CharSequenceNoExpand() {
        testSetCharSequenceNoExpand(UTF_8);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetIso88591CharSequenceNoExpand() {
        testSetCharSequenceNoExpand(ISO_8859_1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetUtf16CharSequenceNoExpand() {
        testSetCharSequenceNoExpand(UTF_16);
    }

    @Test
    public void testSetUsAsciiCharSequence() {
        testSetGetCharSequence(US_ASCII);
    }

    @Test
    public void testSetUtf8CharSequence() {
        testSetGetCharSequence(UTF_8);
    }

    @Test
    public void testSetIso88591CharSequence() {
        testSetGetCharSequence(ISO_8859_1);
    }

    @Test
    public void testSetUtf16CharSequence() {
        testSetGetCharSequence(UTF_16);
    }

    private static final CharBuffer EXTENDED_ASCII_CHARS;

    private static final CharBuffer ASCII_CHARS;

    static {
        char[] chars = new char[256];
        for (char c = 0; c < (chars.length); c++) {
            chars[c] = c;
        }
        EXTENDED_ASCII_CHARS = CharBuffer.wrap(chars);
        ASCII_CHARS = CharBuffer.wrap(chars, 0, 128);
    }

    @Test
    public void testWriteReadUsAsciiCharSequence() {
        testWriteReadCharSequence(US_ASCII);
    }

    @Test
    public void testWriteReadUtf8CharSequence() {
        testWriteReadCharSequence(UTF_8);
    }

    @Test
    public void testWriteReadIso88591CharSequence() {
        testWriteReadCharSequence(ISO_8859_1);
    }

    @Test
    public void testWriteReadUtf16CharSequence() {
        testWriteReadCharSequence(UTF_16);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRetainedSliceIndexOutOfBounds() {
        testSliceOutOfBounds(true, true, true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRetainedSliceLengthOutOfBounds() {
        testSliceOutOfBounds(true, true, false);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMixedSliceAIndexOutOfBounds() {
        testSliceOutOfBounds(true, false, true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMixedSliceALengthOutOfBounds() {
        testSliceOutOfBounds(true, false, false);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMixedSliceBIndexOutOfBounds() {
        testSliceOutOfBounds(false, true, true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMixedSliceBLengthOutOfBounds() {
        testSliceOutOfBounds(false, true, false);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSliceIndexOutOfBounds() {
        testSliceOutOfBounds(false, false, true);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSliceLengthOutOfBounds() {
        testSliceOutOfBounds(false, false, false);
    }

    @Test
    public void testRetainedSliceAndRetainedDuplicateContentIsExpected() {
        ByteBuf buf = newBuffer(8).resetWriterIndex();
        ByteBuf expected1 = newBuffer(6).resetWriterIndex();
        ByteBuf expected2 = newBuffer(5).resetWriterIndex();
        ByteBuf expected3 = newBuffer(4).resetWriterIndex();
        ByteBuf expected4 = newBuffer(3).resetWriterIndex();
        buf.writeBytes(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8 });
        expected1.writeBytes(new byte[]{ 2, 3, 4, 5, 6, 7 });
        expected2.writeBytes(new byte[]{ 3, 4, 5, 6, 7 });
        expected3.writeBytes(new byte[]{ 4, 5, 6, 7 });
        expected4.writeBytes(new byte[]{ 5, 6, 7 });
        ByteBuf slice1 = buf.retainedSlice(((buf.readerIndex()) + 1), 6);
        Assert.assertEquals(0, slice1.compareTo(expected1));
        Assert.assertEquals(0, slice1.compareTo(buf.slice(((buf.readerIndex()) + 1), 6)));
        // Simulate a handler that releases the original buffer, and propagates a slice.
        buf.release();
        // Advance the reader index on the slice.
        slice1.readByte();
        ByteBuf dup1 = slice1.retainedDuplicate();
        Assert.assertEquals(0, dup1.compareTo(expected2));
        Assert.assertEquals(0, dup1.compareTo(slice1.duplicate()));
        // Advance the reader index on dup1.
        dup1.readByte();
        ByteBuf dup2 = dup1.duplicate();
        Assert.assertEquals(0, dup2.compareTo(expected3));
        // Advance the reader index on dup2.
        dup2.readByte();
        ByteBuf slice2 = dup2.retainedSlice(dup2.readerIndex(), 3);
        Assert.assertEquals(0, slice2.compareTo(expected4));
        Assert.assertEquals(0, slice2.compareTo(dup2.slice(dup2.readerIndex(), 3)));
        // Cleanup the expected buffers used for testing.
        Assert.assertTrue(expected1.release());
        Assert.assertTrue(expected2.release());
        Assert.assertTrue(expected3.release());
        Assert.assertTrue(expected4.release());
        slice2.release();
        dup2.release();
        Assert.assertEquals(slice2.refCnt(), dup2.refCnt());
        Assert.assertEquals(dup2.refCnt(), dup1.refCnt());
        // The handler is now done with the original slice
        Assert.assertTrue(slice1.release());
        // Reference counting may be shared, or may be independently tracked, but at this point all buffers should
        // be deallocated and have a reference count of 0.
        Assert.assertEquals(0, buf.refCnt());
        Assert.assertEquals(0, slice1.refCnt());
        Assert.assertEquals(0, slice2.refCnt());
        Assert.assertEquals(0, dup1.refCnt());
        Assert.assertEquals(0, dup2.refCnt());
    }

    @Test
    public void testRetainedDuplicateAndRetainedSliceContentIsExpected() {
        ByteBuf buf = newBuffer(8).resetWriterIndex();
        ByteBuf expected1 = newBuffer(6).resetWriterIndex();
        ByteBuf expected2 = newBuffer(5).resetWriterIndex();
        ByteBuf expected3 = newBuffer(4).resetWriterIndex();
        buf.writeBytes(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8 });
        expected1.writeBytes(new byte[]{ 2, 3, 4, 5, 6, 7 });
        expected2.writeBytes(new byte[]{ 3, 4, 5, 6, 7 });
        expected3.writeBytes(new byte[]{ 5, 6, 7 });
        ByteBuf dup1 = buf.retainedDuplicate();
        Assert.assertEquals(0, dup1.compareTo(buf));
        Assert.assertEquals(0, dup1.compareTo(buf.slice()));
        // Simulate a handler that releases the original buffer, and propagates a slice.
        buf.release();
        // Advance the reader index on the dup.
        dup1.readByte();
        ByteBuf slice1 = dup1.retainedSlice(dup1.readerIndex(), 6);
        Assert.assertEquals(0, slice1.compareTo(expected1));
        Assert.assertEquals(0, slice1.compareTo(slice1.duplicate()));
        // Advance the reader index on slice1.
        slice1.readByte();
        ByteBuf dup2 = slice1.duplicate();
        Assert.assertEquals(0, dup2.compareTo(slice1));
        // Advance the reader index on dup2.
        dup2.readByte();
        ByteBuf slice2 = dup2.retainedSlice(((dup2.readerIndex()) + 1), 3);
        Assert.assertEquals(0, slice2.compareTo(expected3));
        Assert.assertEquals(0, slice2.compareTo(dup2.slice(((dup2.readerIndex()) + 1), 3)));
        // Cleanup the expected buffers used for testing.
        Assert.assertTrue(expected1.release());
        Assert.assertTrue(expected2.release());
        Assert.assertTrue(expected3.release());
        slice2.release();
        slice1.release();
        Assert.assertEquals(slice2.refCnt(), dup2.refCnt());
        Assert.assertEquals(dup2.refCnt(), slice1.refCnt());
        // The handler is now done with the original slice
        Assert.assertTrue(dup1.release());
        // Reference counting may be shared, or may be independently tracked, but at this point all buffers should
        // be deallocated and have a reference count of 0.
        Assert.assertEquals(0, buf.refCnt());
        Assert.assertEquals(0, slice1.refCnt());
        Assert.assertEquals(0, slice2.refCnt());
        Assert.assertEquals(0, dup1.refCnt());
        Assert.assertEquals(0, dup2.refCnt());
    }

    @Test
    public void testRetainedSliceContents() {
        testSliceContents(true);
    }

    @Test
    public void testMultipleLevelRetainedSlice1() {
        testMultipleLevelRetainedSliceWithNonRetained(true, true);
    }

    @Test
    public void testMultipleLevelRetainedSlice2() {
        testMultipleLevelRetainedSliceWithNonRetained(true, false);
    }

    @Test
    public void testMultipleLevelRetainedSlice3() {
        testMultipleLevelRetainedSliceWithNonRetained(false, true);
    }

    @Test
    public void testMultipleLevelRetainedSlice4() {
        testMultipleLevelRetainedSliceWithNonRetained(false, false);
    }

    @Test
    public void testRetainedSliceReleaseOriginal1() {
        testSliceReleaseOriginal(true, true);
    }

    @Test
    public void testRetainedSliceReleaseOriginal2() {
        testSliceReleaseOriginal(true, false);
    }

    @Test
    public void testRetainedSliceReleaseOriginal3() {
        testSliceReleaseOriginal(false, true);
    }

    @Test
    public void testRetainedSliceReleaseOriginal4() {
        testSliceReleaseOriginal(false, false);
    }

    @Test
    public void testRetainedDuplicateReleaseOriginal1() {
        testDuplicateReleaseOriginal(true, true);
    }

    @Test
    public void testRetainedDuplicateReleaseOriginal2() {
        testDuplicateReleaseOriginal(true, false);
    }

    @Test
    public void testRetainedDuplicateReleaseOriginal3() {
        testDuplicateReleaseOriginal(false, true);
    }

    @Test
    public void testRetainedDuplicateReleaseOriginal4() {
        testDuplicateReleaseOriginal(false, false);
    }

    @Test
    public void testMultipleRetainedSliceReleaseOriginal1() {
        testMultipleRetainedSliceReleaseOriginal(true, true);
    }

    @Test
    public void testMultipleRetainedSliceReleaseOriginal2() {
        testMultipleRetainedSliceReleaseOriginal(true, false);
    }

    @Test
    public void testMultipleRetainedSliceReleaseOriginal3() {
        testMultipleRetainedSliceReleaseOriginal(false, true);
    }

    @Test
    public void testMultipleRetainedSliceReleaseOriginal4() {
        testMultipleRetainedSliceReleaseOriginal(false, false);
    }

    @Test
    public void testMultipleRetainedDuplicateReleaseOriginal1() {
        testMultipleRetainedDuplicateReleaseOriginal(true, true);
    }

    @Test
    public void testMultipleRetainedDuplicateReleaseOriginal2() {
        testMultipleRetainedDuplicateReleaseOriginal(true, false);
    }

    @Test
    public void testMultipleRetainedDuplicateReleaseOriginal3() {
        testMultipleRetainedDuplicateReleaseOriginal(false, true);
    }

    @Test
    public void testMultipleRetainedDuplicateReleaseOriginal4() {
        testMultipleRetainedDuplicateReleaseOriginal(false, false);
    }

    @Test
    public void testSliceContents() {
        testSliceContents(false);
    }

    @Test
    public void testRetainedDuplicateContents() {
        testDuplicateContents(true);
    }

    @Test
    public void testDuplicateContents() {
        testDuplicateContents(false);
    }

    @Test
    public void testDuplicateCapacityChange() {
        testDuplicateCapacityChange(false);
    }

    @Test
    public void testRetainedDuplicateCapacityChange() {
        testDuplicateCapacityChange(true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSliceCapacityChange() {
        testSliceCapacityChange(false);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRetainedSliceCapacityChange() {
        testSliceCapacityChange(true);
    }

    @Test
    public void testRetainedSliceUnreleasable1() {
        testRetainedSliceUnreleasable(true, true);
    }

    @Test
    public void testRetainedSliceUnreleasable2() {
        testRetainedSliceUnreleasable(true, false);
    }

    @Test
    public void testRetainedSliceUnreleasable3() {
        testRetainedSliceUnreleasable(false, true);
    }

    @Test
    public void testRetainedSliceUnreleasable4() {
        testRetainedSliceUnreleasable(false, false);
    }

    @Test
    public void testReadRetainedSliceUnreleasable1() {
        testReadRetainedSliceUnreleasable(true, true);
    }

    @Test
    public void testReadRetainedSliceUnreleasable2() {
        testReadRetainedSliceUnreleasable(true, false);
    }

    @Test
    public void testReadRetainedSliceUnreleasable3() {
        testReadRetainedSliceUnreleasable(false, true);
    }

    @Test
    public void testReadRetainedSliceUnreleasable4() {
        testReadRetainedSliceUnreleasable(false, false);
    }

    @Test
    public void testRetainedDuplicateUnreleasable1() {
        testRetainedDuplicateUnreleasable(true, true);
    }

    @Test
    public void testRetainedDuplicateUnreleasable2() {
        testRetainedDuplicateUnreleasable(true, false);
    }

    @Test
    public void testRetainedDuplicateUnreleasable3() {
        testRetainedDuplicateUnreleasable(false, true);
    }

    @Test
    public void testRetainedDuplicateUnreleasable4() {
        testRetainedDuplicateUnreleasable(false, false);
    }

    @Test
    public void testDuplicateRelease() {
        ByteBuf buf = newBuffer(8);
        Assert.assertEquals(1, buf.refCnt());
        Assert.assertTrue(buf.duplicate().release());
        Assert.assertEquals(0, buf.refCnt());
    }

    // Test-case trying to reproduce:
    // https://github.com/netty/netty/issues/2843
    @Test
    public void testRefCnt() throws Exception {
        testRefCnt0(false);
    }

    // Test-case trying to reproduce:
    // https://github.com/netty/netty/issues/2843
    @Test
    public void testRefCnt2() throws Exception {
        testRefCnt0(true);
    }

    @Test
    public void testEmptyNioBuffers() throws Exception {
        ByteBuf buffer = newBuffer(8);
        buffer.clear();
        Assert.assertFalse(buffer.isReadable());
        ByteBuffer[] nioBuffers = buffer.nioBuffers();
        Assert.assertEquals(1, nioBuffers.length);
        Assert.assertFalse(nioBuffers[0].hasRemaining());
        buffer.release();
    }

    @Test
    public void testGetReadOnlyDirectDst() {
        testGetReadOnlyDst(true);
    }

    @Test
    public void testGetReadOnlyHeapDst() {
        testGetReadOnlyDst(false);
    }

    @Test
    public void testReadBytesAndWriteBytesWithFileChannel() throws IOException {
        File file = File.createTempFile("file-channel", ".tmp");
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            FileChannel channel = randomAccessFile.getChannel();
            // channelPosition should never be changed
            long channelPosition = channel.position();
            byte[] bytes = new byte[]{ 'a', 'b', 'c', 'd' };
            int len = bytes.length;
            ByteBuf buffer = newBuffer(len);
            buffer.resetReaderIndex();
            buffer.resetWriterIndex();
            buffer.writeBytes(bytes);
            int oldReaderIndex = buffer.readerIndex();
            Assert.assertEquals(len, buffer.readBytes(channel, 10, len));
            Assert.assertEquals((oldReaderIndex + len), buffer.readerIndex());
            Assert.assertEquals(channelPosition, channel.position());
            ByteBuf buffer2 = newBuffer(len);
            buffer2.resetReaderIndex();
            buffer2.resetWriterIndex();
            int oldWriterIndex = buffer2.writerIndex();
            Assert.assertEquals(len, buffer2.writeBytes(channel, 10, len));
            Assert.assertEquals(channelPosition, channel.position());
            Assert.assertEquals((oldWriterIndex + len), buffer2.writerIndex());
            Assert.assertEquals('a', buffer2.getByte(0));
            Assert.assertEquals('b', buffer2.getByte(1));
            Assert.assertEquals('c', buffer2.getByte(2));
            Assert.assertEquals('d', buffer2.getByte(3));
            buffer.release();
            buffer2.release();
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            file.delete();
        }
    }

    @Test
    public void testGetBytesAndSetBytesWithFileChannel() throws IOException {
        File file = File.createTempFile("file-channel", ".tmp");
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            FileChannel channel = randomAccessFile.getChannel();
            // channelPosition should never be changed
            long channelPosition = channel.position();
            byte[] bytes = new byte[]{ 'a', 'b', 'c', 'd' };
            int len = bytes.length;
            ByteBuf buffer = newBuffer(len);
            buffer.resetReaderIndex();
            buffer.resetWriterIndex();
            buffer.writeBytes(bytes);
            int oldReaderIndex = buffer.readerIndex();
            Assert.assertEquals(len, buffer.getBytes(oldReaderIndex, channel, 10, len));
            Assert.assertEquals(oldReaderIndex, buffer.readerIndex());
            Assert.assertEquals(channelPosition, channel.position());
            ByteBuf buffer2 = newBuffer(len);
            buffer2.resetReaderIndex();
            buffer2.resetWriterIndex();
            int oldWriterIndex = buffer2.writerIndex();
            Assert.assertEquals(buffer2.setBytes(oldWriterIndex, channel, 10, len), len);
            Assert.assertEquals(channelPosition, channel.position());
            Assert.assertEquals(oldWriterIndex, buffer2.writerIndex());
            Assert.assertEquals('a', buffer2.getByte(oldWriterIndex));
            Assert.assertEquals('b', buffer2.getByte((oldWriterIndex + 1)));
            Assert.assertEquals('c', buffer2.getByte((oldWriterIndex + 2)));
            Assert.assertEquals('d', buffer2.getByte((oldWriterIndex + 3)));
            buffer.release();
            buffer2.release();
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            file.delete();
        }
    }

    @Test
    public void testReadBytes() {
        ByteBuf buffer = newBuffer(8);
        byte[] bytes = new byte[8];
        buffer.writeBytes(bytes);
        ByteBuf buffer2 = buffer.readBytes(4);
        Assert.assertSame(buffer.alloc(), buffer2.alloc());
        Assert.assertEquals(4, buffer.readerIndex());
        Assert.assertTrue(buffer.release());
        Assert.assertEquals(0, buffer.refCnt());
        Assert.assertTrue(buffer2.release());
        Assert.assertEquals(0, buffer2.refCnt());
    }

    @Test
    public void testForEachByteDesc2() {
        byte[] expected = new byte[]{ 1, 2, 3, 4 };
        ByteBuf buf = newBuffer(expected.length);
        try {
            buf.writeBytes(expected);
            final byte[] bytes = new byte[expected.length];
            int i = buf.forEachByteDesc(new ByteProcessor() {
                private int index = (bytes.length) - 1;

                @Override
                public boolean process(byte value) throws Exception {
                    bytes[((index)--)] = value;
                    return true;
                }
            });
            Assert.assertEquals((-1), i);
            Assert.assertArrayEquals(expected, bytes);
        } finally {
            buf.release();
        }
    }

    @Test
    public void testForEachByte2() {
        byte[] expected = new byte[]{ 1, 2, 3, 4 };
        ByteBuf buf = newBuffer(expected.length);
        try {
            buf.writeBytes(expected);
            final byte[] bytes = new byte[expected.length];
            int i = buf.forEachByte(new ByteProcessor() {
                private int index;

                @Override
                public boolean process(byte value) throws Exception {
                    bytes[((index)++)] = value;
                    return true;
                }
            });
            Assert.assertEquals((-1), i);
            Assert.assertArrayEquals(expected, bytes);
        } finally {
            buf.release();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBytesByteBuffer() {
        byte[] bytes = new byte[]{ 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        // Ensure destination buffer is bigger then what is in the ByteBuf.
        ByteBuffer nioBuffer = ByteBuffer.allocate(((bytes.length) + 1));
        ByteBuf buffer = newBuffer(bytes.length);
        try {
            buffer.writeBytes(bytes);
            buffer.getBytes(buffer.readerIndex(), nioBuffer);
        } finally {
            buffer.release();
        }
    }

    static final class TestGatheringByteChannel implements GatheringByteChannel {
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        private final WritableByteChannel channel = Channels.newChannel(out);

        private final int limit;

        TestGatheringByteChannel(int limit) {
            this.limit = limit;
        }

        TestGatheringByteChannel() {
            this(Integer.MAX_VALUE);
        }

        @Override
        public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
            long written = 0;
            for (; offset < length; offset++) {
                written += write(srcs[offset]);
                if (written >= (limit)) {
                    break;
                }
            }
            return written;
        }

        @Override
        public long write(ByteBuffer[] srcs) throws IOException {
            return write(srcs, 0, srcs.length);
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            int oldLimit = src.limit();
            if ((limit) < (src.remaining())) {
                src.limit(((src.position()) + (limit)));
            }
            int w = channel.write(src);
            src.limit(oldLimit);
            return w;
        }

        @Override
        public boolean isOpen() {
            return channel.isOpen();
        }

        @Override
        public void close() throws IOException {
            channel.close();
        }

        public byte[] writtenBytes() {
            return out.toByteArray();
        }
    }

    private static final class DevNullGatheringByteChannel implements GatheringByteChannel {
        @Override
        public long write(ByteBuffer[] srcs, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long write(ByteBuffer[] srcs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int write(ByteBuffer src) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class TestScatteringByteChannel implements ScatteringByteChannel {
        @Override
        public long read(ByteBuffer[] dsts, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long read(ByteBuffer[] dsts) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read(ByteBuffer dst) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class TestByteProcessor implements ByteProcessor {
        @Override
        public boolean process(byte value) throws Exception {
            return true;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCapacityEnforceMaxCapacity() {
        ByteBuf buffer = newBuffer(3, 13);
        Assert.assertEquals(13, buffer.maxCapacity());
        Assert.assertEquals(3, buffer.capacity());
        try {
            buffer.capacity(14);
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCapacityNegative() {
        ByteBuf buffer = newBuffer(3, 13);
        Assert.assertEquals(13, buffer.maxCapacity());
        Assert.assertEquals(3, buffer.capacity());
        try {
            buffer.capacity((-1));
        } finally {
            buffer.release();
        }
    }

    @Test
    public void testCapacityDecrease() {
        ByteBuf buffer = newBuffer(3, 13);
        Assert.assertEquals(13, buffer.maxCapacity());
        Assert.assertEquals(3, buffer.capacity());
        try {
            buffer.capacity(2);
            Assert.assertEquals(2, buffer.capacity());
            Assert.assertEquals(13, buffer.maxCapacity());
        } finally {
            buffer.release();
        }
    }

    @Test
    public void testCapacityIncrease() {
        ByteBuf buffer = newBuffer(3, 13);
        Assert.assertEquals(13, buffer.maxCapacity());
        Assert.assertEquals(3, buffer.capacity());
        try {
            buffer.capacity(4);
            Assert.assertEquals(4, buffer.capacity());
            Assert.assertEquals(13, buffer.maxCapacity());
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testReaderIndexLargerThanWriterIndex() {
        String content1 = "hello";
        String content2 = "world";
        int length = (content1.length()) + (content2.length());
        ByteBuf buffer = newBuffer(length);
        buffer.setIndex(0, 0);
        buffer.writeCharSequence(content1, US_ASCII);
        buffer.markWriterIndex();
        buffer.skipBytes(content1.length());
        buffer.writeCharSequence(content2, US_ASCII);
        buffer.skipBytes(content2.length());
        Assert.assertTrue(((buffer.readerIndex()) <= (buffer.writerIndex())));
        try {
            buffer.resetWriterIndex();
        } finally {
            buffer.release();
        }
    }
}

