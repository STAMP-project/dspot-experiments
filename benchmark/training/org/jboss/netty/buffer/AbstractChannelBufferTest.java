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


import ChannelBufferIndexFinder.CR;
import ChannelBufferIndexFinder.LF;
import ChannelBufferIndexFinder.NUL;
import CharsetUtil.ISO_8859_1;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * An abstract test class for channel buffers
 */
public abstract class AbstractChannelBufferTest {
    private static final int CAPACITY = 4096;// Must be even


    private static final int BLOCK_SIZE = 128;

    private long seed;

    private Random random;

    private ChannelBuffer buffer;

    @Test
    public void initialState() {
        Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.capacity());
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
            buffer.writerIndex(((AbstractChannelBufferTest.CAPACITY) / 2));
        } catch (IndexOutOfBoundsException e) {
            Assert.fail();
        }
        buffer.readerIndex((((AbstractChannelBufferTest.CAPACITY) * 3) / 2));
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
            buffer.writerIndex(AbstractChannelBufferTest.CAPACITY);
            buffer.readerIndex(AbstractChannelBufferTest.CAPACITY);
        } catch (IndexOutOfBoundsException e) {
            Assert.fail();
        }
        buffer.writerIndex(((buffer.capacity()) + 1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void writerIndexBoundaryCheck3() {
        try {
            buffer.writerIndex(AbstractChannelBufferTest.CAPACITY);
            buffer.readerIndex(((AbstractChannelBufferTest.CAPACITY) / 2));
        } catch (IndexOutOfBoundsException e) {
            Assert.fail();
        }
        buffer.writerIndex(((AbstractChannelBufferTest.CAPACITY) / 4));
    }

    @Test
    public void writerIndexBoundaryCheck4() {
        buffer.writerIndex(0);
        buffer.readerIndex(0);
        buffer.writerIndex(AbstractChannelBufferTest.CAPACITY);
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
        buffer.getBytes((-1), new byte[0]);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getByteArrayBoundaryCheck2() {
        buffer.getBytes((-1), new byte[0], 0, 0);
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
        buffer.setIndex((-1), AbstractChannelBufferTest.CAPACITY);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setIndexBoundaryCheck2() {
        buffer.setIndex(((AbstractChannelBufferTest.CAPACITY) / 2), ((AbstractChannelBufferTest.CAPACITY) / 4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setIndexBoundaryCheck3() {
        buffer.setIndex(0, ((AbstractChannelBufferTest.CAPACITY) + 1));
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
        for (int i = 0; i < ((buffer.capacity()) - 1); i += 2) {
            short value = ((short) (random.nextInt()));
            buffer.setShort(i, value);
        }
        random.setSeed(seed);
        for (int i = 0; i < ((buffer.capacity()) - 1); i += 2) {
            short value = ((short) (random.nextInt()));
            Assert.assertEquals(value, buffer.getShort(i));
        }
    }

    @Test
    public void testRandomUnsignedShortAccess() {
        for (int i = 0; i < ((buffer.capacity()) - 1); i += 2) {
            short value = ((short) (random.nextInt()));
            buffer.setShort(i, value);
        }
        random.setSeed(seed);
        for (int i = 0; i < ((buffer.capacity()) - 1); i += 2) {
            int value = (random.nextInt()) & 65535;
            Assert.assertEquals(value, buffer.getUnsignedShort(i));
        }
    }

    @Test
    public void testRandomMediumAccess() {
        for (int i = 0; i < ((buffer.capacity()) - 2); i += 3) {
            int value = random.nextInt();
            buffer.setMedium(i, value);
        }
        random.setSeed(seed);
        for (int i = 0; i < ((buffer.capacity()) - 2); i += 3) {
            int value = ((random.nextInt()) << 8) >> 8;
            Assert.assertEquals(value, buffer.getMedium(i));
        }
    }

    @Test
    public void testRandomUnsignedMediumAccess() {
        for (int i = 0; i < ((buffer.capacity()) - 2); i += 3) {
            int value = random.nextInt();
            buffer.setMedium(i, value);
        }
        random.setSeed(seed);
        for (int i = 0; i < ((buffer.capacity()) - 2); i += 3) {
            int value = (random.nextInt()) & 16777215;
            Assert.assertEquals(value, buffer.getUnsignedMedium(i));
        }
    }

    @Test
    public void testRandomIntAccess() {
        for (int i = 0; i < ((buffer.capacity()) - 3); i += 4) {
            int value = random.nextInt();
            buffer.setInt(i, value);
        }
        random.setSeed(seed);
        for (int i = 0; i < ((buffer.capacity()) - 3); i += 4) {
            int value = random.nextInt();
            Assert.assertEquals(value, buffer.getInt(i));
        }
    }

    @Test
    public void testRandomUnsignedIntAccess() {
        for (int i = 0; i < ((buffer.capacity()) - 3); i += 4) {
            int value = random.nextInt();
            buffer.setInt(i, value);
        }
        random.setSeed(seed);
        for (int i = 0; i < ((buffer.capacity()) - 3); i += 4) {
            long value = (random.nextInt()) & 4294967295L;
            Assert.assertEquals(value, buffer.getUnsignedInt(i));
        }
    }

    @Test
    public void testRandomLongAccess() {
        for (int i = 0; i < ((buffer.capacity()) - 7); i += 8) {
            long value = random.nextLong();
            buffer.setLong(i, value);
        }
        random.setSeed(seed);
        for (int i = 0; i < ((buffer.capacity()) - 7); i += 8) {
            long value = random.nextLong();
            Assert.assertEquals(value, buffer.getLong(i));
        }
    }

    @Test
    public void testSetZero() {
        buffer.clear();
        while (buffer.writable()) {
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
            Assert.assertTrue(buffer.writable());
            buffer.writeByte(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.writable());
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.readable());
            Assert.assertEquals(value, buffer.readByte());
        }
        Assert.assertEquals(buffer.capacity(), buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.readable());
        Assert.assertFalse(buffer.writable());
    }

    @Test
    public void testSequentialUnsignedByteAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.writable());
            buffer.writeByte(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.writable());
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i++) {
            int value = (random.nextInt()) & 255;
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.readable());
            Assert.assertEquals(value, buffer.readUnsignedByte());
        }
        Assert.assertEquals(buffer.capacity(), buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.readable());
        Assert.assertFalse(buffer.writable());
    }

    @Test
    public void testSequentialShortAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i += 2) {
            short value = ((short) (random.nextInt()));
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.writable());
            buffer.writeShort(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.writable());
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i += 2) {
            short value = ((short) (random.nextInt()));
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.readable());
            Assert.assertEquals(value, buffer.readShort());
        }
        Assert.assertEquals(buffer.capacity(), buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.readable());
        Assert.assertFalse(buffer.writable());
    }

    @Test
    public void testSequentialUnsignedShortAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i += 2) {
            short value = ((short) (random.nextInt()));
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.writable());
            buffer.writeShort(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.writable());
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i += 2) {
            int value = (random.nextInt()) & 65535;
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.readable());
            Assert.assertEquals(value, buffer.readUnsignedShort());
        }
        Assert.assertEquals(buffer.capacity(), buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.readable());
        Assert.assertFalse(buffer.writable());
    }

    @Test
    public void testSequentialMediumAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) / 3) * 3); i += 3) {
            int value = random.nextInt();
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.writable());
            buffer.writeMedium(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals((((buffer.capacity()) / 3) * 3), buffer.writerIndex());
        Assert.assertEquals(((buffer.capacity()) % 3), buffer.writableBytes());
        random.setSeed(seed);
        for (int i = 0; i < (((buffer.capacity()) / 3) * 3); i += 3) {
            int value = ((random.nextInt()) << 8) >> 8;
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.readable());
            Assert.assertEquals(value, buffer.readMedium());
        }
        Assert.assertEquals((((buffer.capacity()) / 3) * 3), buffer.readerIndex());
        Assert.assertEquals((((buffer.capacity()) / 3) * 3), buffer.writerIndex());
        Assert.assertEquals(0, buffer.readableBytes());
        Assert.assertEquals(((buffer.capacity()) % 3), buffer.writableBytes());
    }

    @Test
    public void testSequentialUnsignedMediumAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) / 3) * 3); i += 3) {
            int value = (random.nextInt()) & 16777215;
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.writable());
            buffer.writeMedium(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals((((buffer.capacity()) / 3) * 3), buffer.writerIndex());
        Assert.assertEquals(((buffer.capacity()) % 3), buffer.writableBytes());
        random.setSeed(seed);
        for (int i = 0; i < (((buffer.capacity()) / 3) * 3); i += 3) {
            int value = (random.nextInt()) & 16777215;
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.readable());
            Assert.assertEquals(value, buffer.readUnsignedMedium());
        }
        Assert.assertEquals((((buffer.capacity()) / 3) * 3), buffer.readerIndex());
        Assert.assertEquals((((buffer.capacity()) / 3) * 3), buffer.writerIndex());
        Assert.assertEquals(0, buffer.readableBytes());
        Assert.assertEquals(((buffer.capacity()) % 3), buffer.writableBytes());
    }

    @Test
    public void testSequentialIntAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i += 4) {
            int value = random.nextInt();
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.writable());
            buffer.writeInt(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.writable());
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i += 4) {
            int value = random.nextInt();
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.readable());
            Assert.assertEquals(value, buffer.readInt());
        }
        Assert.assertEquals(buffer.capacity(), buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.readable());
        Assert.assertFalse(buffer.writable());
    }

    @Test
    public void testSequentialUnsignedIntAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i += 4) {
            int value = random.nextInt();
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.writable());
            buffer.writeInt(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.writable());
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i += 4) {
            long value = (random.nextInt()) & 4294967295L;
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.readable());
            Assert.assertEquals(value, buffer.readUnsignedInt());
        }
        Assert.assertEquals(buffer.capacity(), buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.readable());
        Assert.assertFalse(buffer.writable());
    }

    @Test
    public void testSequentialLongAccess() {
        buffer.writerIndex(0);
        for (int i = 0; i < (buffer.capacity()); i += 8) {
            long value = random.nextLong();
            Assert.assertEquals(i, buffer.writerIndex());
            Assert.assertTrue(buffer.writable());
            buffer.writeLong(value);
        }
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.writable());
        random.setSeed(seed);
        for (int i = 0; i < (buffer.capacity()); i += 8) {
            long value = random.nextLong();
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertTrue(buffer.readable());
            Assert.assertEquals(value, buffer.readLong());
        }
        Assert.assertEquals(buffer.capacity(), buffer.readerIndex());
        Assert.assertEquals(buffer.capacity(), buffer.writerIndex());
        Assert.assertFalse(buffer.readable());
        Assert.assertFalse(buffer.writable());
    }

    @Test
    public void testByteArrayTransfer() {
        byte[] value = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(value);
            buffer.setBytes(i, value, random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE), AbstractChannelBufferTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            buffer.getBytes(i, value, valueOffset, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue[j], value[j]);
            }
        }
    }

    @Test
    public void testRandomByteArrayTransfer1() {
        byte[] value = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(value);
            buffer.setBytes(i, value);
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            buffer.getBytes(i, value);
            for (int j = 0; j < (AbstractChannelBufferTest.BLOCK_SIZE); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value[j]);
            }
        }
    }

    @Test
    public void testRandomByteArrayTransfer2() {
        byte[] value = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(value);
            buffer.setBytes(i, value, random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE), AbstractChannelBufferTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            buffer.getBytes(i, value, valueOffset, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value[j]);
            }
        }
    }

    @Test
    public void testRandomHeapBufferTransfer1() {
        byte[] valueContent = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
        ChannelBuffer value = wrappedBuffer(valueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setIndex(0, AbstractChannelBufferTest.BLOCK_SIZE);
            buffer.setBytes(i, value);
            Assert.assertEquals(AbstractChannelBufferTest.BLOCK_SIZE, value.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.BLOCK_SIZE, value.writerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            value.clear();
            buffer.getBytes(i, value);
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.BLOCK_SIZE, value.writerIndex());
            for (int j = 0; j < (AbstractChannelBufferTest.BLOCK_SIZE); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
        }
    }

    @Test
    public void testRandomHeapBufferTransfer2() {
        byte[] valueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer value = wrappedBuffer(valueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            buffer.setBytes(i, value, random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE), AbstractChannelBufferTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            buffer.getBytes(i, value, valueOffset, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
        }
    }

    @Test
    public void testRandomDirectBufferTransfer() {
        byte[] tmp = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer value = directBuffer(((AbstractChannelBufferTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(tmp);
            value.setBytes(0, tmp, 0, value.capacity());
            buffer.setBytes(i, value, random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE), AbstractChannelBufferTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        ChannelBuffer expectedValue = directBuffer(((AbstractChannelBufferTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(tmp);
            expectedValue.setBytes(0, tmp, 0, expectedValue.capacity());
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            buffer.getBytes(i, value, valueOffset, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
        }
    }

    @Test
    public void testRandomByteBufferTransfer() {
        ByteBuffer value = ByteBuffer.allocate(((AbstractChannelBufferTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(value.array());
            value.clear().position(random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE));
            value.limit(((value.position()) + (AbstractChannelBufferTest.BLOCK_SIZE)));
            buffer.setBytes(i, value);
        }
        random.setSeed(seed);
        ByteBuffer expectedValue = ByteBuffer.allocate(((AbstractChannelBufferTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue.array());
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            value.clear().position(valueOffset).limit((valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)));
            buffer.getBytes(i, value);
            Assert.assertEquals((valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)), value.position());
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.get(j), value.get(j));
            }
        }
    }

    @Test
    public void testSequentialByteArrayTransfer1() {
        byte[] value = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(value);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value);
            for (int j = 0; j < (AbstractChannelBufferTest.BLOCK_SIZE); j++) {
                Assert.assertEquals(expectedValue[j], value[j]);
            }
        }
    }

    @Test
    public void testSequentialByteArrayTransfer2() {
        byte[] value = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(value);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            int readerIndex = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            buffer.writeBytes(value, readerIndex, AbstractChannelBufferTest.BLOCK_SIZE);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value, valueOffset, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue[j], value[j]);
            }
        }
    }

    @Test
    public void testSequentialHeapBufferTransfer1() {
        byte[] valueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer value = wrappedBuffer(valueContent);
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value, random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE), AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(valueContent.length, value.writerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value, valueOffset, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(valueContent.length, value.writerIndex());
        }
    }

    @Test
    public void testSequentialHeapBufferTransfer2() {
        byte[] valueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer value = wrappedBuffer(valueContent);
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            int readerIndex = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            value.readerIndex(readerIndex);
            value.writerIndex((readerIndex + (AbstractChannelBufferTest.BLOCK_SIZE)));
            buffer.writeBytes(value);
            Assert.assertEquals((readerIndex + (AbstractChannelBufferTest.BLOCK_SIZE)), value.writerIndex());
            Assert.assertEquals(value.writerIndex(), value.readerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            value.readerIndex(valueOffset);
            value.writerIndex(valueOffset);
            buffer.readBytes(value, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(valueOffset, value.readerIndex());
            Assert.assertEquals((valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)), value.writerIndex());
        }
    }

    @Test
    public void testSequentialDirectBufferTransfer1() {
        byte[] valueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer value = directBuffer(((AbstractChannelBufferTest.BLOCK_SIZE) * 2));
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setBytes(0, valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value, random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE), AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(0, value.writerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            value.setBytes(0, valueContent);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value, valueOffset, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(0, value.writerIndex());
        }
    }

    @Test
    public void testSequentialDirectBufferTransfer2() {
        byte[] valueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer value = directBuffer(((AbstractChannelBufferTest.BLOCK_SIZE) * 2));
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setBytes(0, valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            int readerIndex = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            value.readerIndex(0);
            value.writerIndex((readerIndex + (AbstractChannelBufferTest.BLOCK_SIZE)));
            value.readerIndex(readerIndex);
            buffer.writeBytes(value);
            Assert.assertEquals((readerIndex + (AbstractChannelBufferTest.BLOCK_SIZE)), value.writerIndex());
            Assert.assertEquals(value.writerIndex(), value.readerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            value.setBytes(0, valueContent);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            value.readerIndex(valueOffset);
            value.writerIndex(valueOffset);
            buffer.readBytes(value, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(valueOffset, value.readerIndex());
            Assert.assertEquals((valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)), value.writerIndex());
        }
    }

    @Test
    public void testSequentialByteBufferBackedHeapBufferTransfer1() {
        byte[] valueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer value = wrappedBuffer(ByteBuffer.allocate(((AbstractChannelBufferTest.BLOCK_SIZE) * 2)));
        value.writerIndex(0);
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setBytes(0, valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value, random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE), AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(0, value.writerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            value.setBytes(0, valueContent);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            buffer.readBytes(value, valueOffset, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(0, value.readerIndex());
            Assert.assertEquals(0, value.writerIndex());
        }
    }

    @Test
    public void testSequentialByteBufferBackedHeapBufferTransfer2() {
        byte[] valueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer value = wrappedBuffer(ByteBuffer.allocate(((AbstractChannelBufferTest.BLOCK_SIZE) * 2)));
        value.writerIndex(0);
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(valueContent);
            value.setBytes(0, valueContent);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            int readerIndex = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            value.readerIndex(0);
            value.writerIndex((readerIndex + (AbstractChannelBufferTest.BLOCK_SIZE)));
            value.readerIndex(readerIndex);
            buffer.writeBytes(value);
            Assert.assertEquals((readerIndex + (AbstractChannelBufferTest.BLOCK_SIZE)), value.writerIndex());
            Assert.assertEquals(value.writerIndex(), value.readerIndex());
        }
        random.setSeed(seed);
        byte[] expectedValueContent = new byte[(AbstractChannelBufferTest.BLOCK_SIZE) * 2];
        ChannelBuffer expectedValue = wrappedBuffer(expectedValueContent);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValueContent);
            value.setBytes(0, valueContent);
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            value.readerIndex(valueOffset);
            value.writerIndex(valueOffset);
            buffer.readBytes(value, AbstractChannelBufferTest.BLOCK_SIZE);
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.getByte(j), value.getByte(j));
            }
            Assert.assertEquals(valueOffset, value.readerIndex());
            Assert.assertEquals((valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)), value.writerIndex());
        }
    }

    @Test
    public void testSequentialByteBufferTransfer() {
        buffer.writerIndex(0);
        ByteBuffer value = ByteBuffer.allocate(((AbstractChannelBufferTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(value.array());
            value.clear().position(random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE));
            value.limit(((value.position()) + (AbstractChannelBufferTest.BLOCK_SIZE)));
            buffer.writeBytes(value);
        }
        random.setSeed(seed);
        ByteBuffer expectedValue = ByteBuffer.allocate(((AbstractChannelBufferTest.BLOCK_SIZE) * 2));
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue.array());
            int valueOffset = random.nextInt(AbstractChannelBufferTest.BLOCK_SIZE);
            value.clear().position(valueOffset).limit((valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)));
            buffer.readBytes(value);
            Assert.assertEquals((valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)), value.position());
            for (int j = valueOffset; j < (valueOffset + (AbstractChannelBufferTest.BLOCK_SIZE)); j++) {
                Assert.assertEquals(expectedValue.get(j), value.get(j));
            }
        }
    }

    @Test
    public void testSequentialCopiedBufferTransfer1() {
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            byte[] value = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
            random.nextBytes(value);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            ChannelBuffer actualValue = buffer.readBytes(AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(wrappedBuffer(expectedValue), actualValue);
            // Make sure if it is a copied buffer.
            actualValue.setByte(0, ((byte) ((actualValue.getByte(0)) + 1)));
            Assert.assertFalse(((buffer.getByte(i)) == (actualValue.getByte(0))));
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSequentialCopiedBufferTransfer2() {
        buffer.clear();
        buffer.writeZero(buffer.capacity());
        try {
            buffer.readBytes(CR);
            Assert.fail();
        } catch (NoSuchElementException e) {
            // Expected
        }
        Assert.assertSame(EMPTY_BUFFER, buffer.readBytes(NUL));
        buffer.clear();
        buffer.writeBytes(new byte[]{ 1, 2, 3, 4, 0 });
        ChannelBuffer copy = buffer.readBytes(NUL);
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3, 4 }), copy);
        // Make sure if it is a copied buffer.
        copy.setByte(0, ((byte) ((copy.getByte(0)) + 1)));
        Assert.assertFalse(((buffer.getByte(0)) == (copy.getByte(0))));
    }

    @Test
    public void testSequentialSlice1() {
        buffer.writerIndex(0);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            byte[] value = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
            random.nextBytes(value);
            Assert.assertEquals(0, buffer.readerIndex());
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(value);
        }
        random.setSeed(seed);
        byte[] expectedValue = new byte[AbstractChannelBufferTest.BLOCK_SIZE];
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            random.nextBytes(expectedValue);
            Assert.assertEquals(i, buffer.readerIndex());
            Assert.assertEquals(AbstractChannelBufferTest.CAPACITY, buffer.writerIndex());
            ChannelBuffer actualValue = buffer.readSlice(AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(wrappedBuffer(expectedValue), actualValue);
            // Make sure if it is a sliced buffer.
            actualValue.setByte(0, ((byte) ((actualValue.getByte(0)) + 1)));
            Assert.assertEquals(buffer.getByte(i), actualValue.getByte(0));
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSequentialSlice2() {
        buffer.clear();
        buffer.writeZero(buffer.capacity());
        try {
            buffer.readSlice(CR);
            Assert.fail();
        } catch (NoSuchElementException e) {
            // Expected
        }
        Assert.assertSame(EMPTY_BUFFER, buffer.readSlice(NUL));
        buffer.clear();
        buffer.writeBytes(new byte[]{ 1, 2, 3, 4, 0 });
        ChannelBuffer slice = buffer.readSlice(NUL);
        Assert.assertEquals(wrappedBuffer(new byte[]{ 1, 2, 3, 4 }), slice);
        // Make sure if it is a sliced buffer.
        slice.setByte(0, ((byte) ((slice.getByte(0)) + 1)));
        Assert.assertEquals(buffer.getByte(0), slice.getByte(0));
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
        while (buffer.writable()) {
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
        ChannelBuffer copy = copiedBuffer(buffer);
        // Make sure there's no effect if called when readerIndex is 0.
        buffer.readerIndex(((AbstractChannelBufferTest.CAPACITY) / 4));
        buffer.markReaderIndex();
        buffer.writerIndex(((AbstractChannelBufferTest.CAPACITY) / 3));
        buffer.markWriterIndex();
        buffer.readerIndex(0);
        buffer.writerIndex(((AbstractChannelBufferTest.CAPACITY) / 2));
        buffer.discardReadBytes();
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(((AbstractChannelBufferTest.CAPACITY) / 2), buffer.writerIndex());
        Assert.assertEquals(copy.slice(0, ((AbstractChannelBufferTest.CAPACITY) / 2)), buffer.slice(0, ((AbstractChannelBufferTest.CAPACITY) / 2)));
        buffer.resetReaderIndex();
        Assert.assertEquals(((AbstractChannelBufferTest.CAPACITY) / 4), buffer.readerIndex());
        buffer.resetWriterIndex();
        Assert.assertEquals(((AbstractChannelBufferTest.CAPACITY) / 3), buffer.writerIndex());
        // Make sure bytes after writerIndex is not copied.
        buffer.readerIndex(1);
        buffer.writerIndex(((AbstractChannelBufferTest.CAPACITY) / 2));
        buffer.discardReadBytes();
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals((((AbstractChannelBufferTest.CAPACITY) / 2) - 1), buffer.writerIndex());
        Assert.assertEquals(copy.slice(1, (((AbstractChannelBufferTest.CAPACITY) / 2) - 1)), buffer.slice(0, (((AbstractChannelBufferTest.CAPACITY) / 2) - 1)));
        if (discardReadBytesDoesNotMoveWritableBytes()) {
            // If writable bytes were copied, the test should fail to avoid unnecessary memory bandwidth consumption.
            Assert.assertFalse(copy.slice(((AbstractChannelBufferTest.CAPACITY) / 2), ((AbstractChannelBufferTest.CAPACITY) / 2)).equals(buffer.slice((((AbstractChannelBufferTest.CAPACITY) / 2) - 1), ((AbstractChannelBufferTest.CAPACITY) / 2))));
        } else {
            Assert.assertEquals(copy.slice(((AbstractChannelBufferTest.CAPACITY) / 2), ((AbstractChannelBufferTest.CAPACITY) / 2)), buffer.slice((((AbstractChannelBufferTest.CAPACITY) / 2) - 1), ((AbstractChannelBufferTest.CAPACITY) / 2)));
        }
        // Marks also should be relocated.
        buffer.resetReaderIndex();
        Assert.assertEquals((((AbstractChannelBufferTest.CAPACITY) / 4) - 1), buffer.readerIndex());
        buffer.resetWriterIndex();
        Assert.assertEquals((((AbstractChannelBufferTest.CAPACITY) / 3) - 1), buffer.writerIndex());
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
        ChannelBuffer copy = copiedBuffer(buffer);
        // Discard the first (CAPACITY / 2 - 1) bytes.
        buffer.setIndex((((AbstractChannelBufferTest.CAPACITY) / 2) - 1), ((AbstractChannelBufferTest.CAPACITY) - 1));
        buffer.discardReadBytes();
        Assert.assertEquals(0, buffer.readerIndex());
        Assert.assertEquals(((AbstractChannelBufferTest.CAPACITY) / 2), buffer.writerIndex());
        for (int i = 0; i < ((AbstractChannelBufferTest.CAPACITY) / 2); i++) {
            Assert.assertEquals(copy.slice(((((AbstractChannelBufferTest.CAPACITY) / 2) - 1) + i), (((AbstractChannelBufferTest.CAPACITY) / 2) - i)), buffer.slice(i, (((AbstractChannelBufferTest.CAPACITY) / 2) - i)));
        }
    }

    @Test
    public void testStreamTransfer1() throws Exception {
        byte[] expected = new byte[buffer.capacity()];
        random.nextBytes(expected);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            ByteArrayInputStream in = new ByteArrayInputStream(expected, i, AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(AbstractChannelBufferTest.BLOCK_SIZE, buffer.setBytes(i, in, AbstractChannelBufferTest.BLOCK_SIZE));
            Assert.assertEquals((-1), buffer.setBytes(i, in, 0));
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            buffer.getBytes(i, out, AbstractChannelBufferTest.BLOCK_SIZE);
        }
        Assert.assertTrue(Arrays.equals(expected, out.toByteArray()));
    }

    @Test
    public void testStreamTransfer2() throws Exception {
        byte[] expected = new byte[buffer.capacity()];
        random.nextBytes(expected);
        buffer.clear();
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            ByteArrayInputStream in = new ByteArrayInputStream(expected, i, AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals(i, buffer.writerIndex());
            buffer.writeBytes(in, AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals((i + (AbstractChannelBufferTest.BLOCK_SIZE)), buffer.writerIndex());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            Assert.assertEquals(i, buffer.readerIndex());
            buffer.readBytes(out, AbstractChannelBufferTest.BLOCK_SIZE);
            Assert.assertEquals((i + (AbstractChannelBufferTest.BLOCK_SIZE)), buffer.readerIndex());
        }
        Assert.assertTrue(Arrays.equals(expected, out.toByteArray()));
    }

    @Test
    public void testCopy() {
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            buffer.setByte(i, value);
        }
        final int readerIndex = (AbstractChannelBufferTest.CAPACITY) / 3;
        final int writerIndex = ((AbstractChannelBufferTest.CAPACITY) * 2) / 3;
        buffer.setIndex(readerIndex, writerIndex);
        // Make sure all properties are copied.
        ChannelBuffer copy = buffer.copy();
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
    }

    @Test
    public void testDuplicate() {
        for (int i = 0; i < (buffer.capacity()); i++) {
            byte value = ((byte) (random.nextInt()));
            buffer.setByte(i, value);
        }
        final int readerIndex = (AbstractChannelBufferTest.CAPACITY) / 3;
        final int writerIndex = ((AbstractChannelBufferTest.CAPACITY) * 2) / 3;
        buffer.setIndex(readerIndex, writerIndex);
        // Make sure all properties are copied.
        ChannelBuffer duplicate = buffer.duplicate();
        Assert.assertEquals(buffer.readerIndex(), duplicate.readerIndex());
        Assert.assertEquals(buffer.writerIndex(), duplicate.writerIndex());
        Assert.assertEquals(buffer.capacity(), duplicate.capacity());
        Assert.assertSame(buffer.order(), duplicate.order());
        for (int i = 0; i < (duplicate.capacity()); i++) {
            Assert.assertEquals(buffer.getByte(i), duplicate.getByte(i));
        }
        // Make sure the buffer content is shared.
        buffer.setByte(readerIndex, ((byte) ((buffer.getByte(readerIndex)) + 1)));
        Assert.assertEquals(buffer.getByte(readerIndex), duplicate.getByte(readerIndex));
        duplicate.setByte(1, ((byte) ((duplicate.getByte(1)) + 1)));
        Assert.assertEquals(buffer.getByte(1), duplicate.getByte(1));
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
    public void testEquals() {
        Assert.assertFalse(buffer.equals(null));
        Assert.assertFalse(buffer.equals(new Object()));
        byte[] value = new byte[32];
        buffer.setIndex(0, value.length);
        random.nextBytes(value);
        buffer.setBytes(0, value);
        Assert.assertEquals(buffer, wrappedBuffer(BIG_ENDIAN, value));
        Assert.assertEquals(buffer, wrappedBuffer(LITTLE_ENDIAN, value));
        (value[0])++;
        Assert.assertFalse(buffer.equals(wrappedBuffer(BIG_ENDIAN, value)));
        Assert.assertFalse(buffer.equals(wrappedBuffer(LITTLE_ENDIAN, value)));
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
        Assert.assertEquals(0, buffer.compareTo(wrappedBuffer(BIG_ENDIAN, value)));
        Assert.assertEquals(0, buffer.compareTo(wrappedBuffer(LITTLE_ENDIAN, value)));
        (value[0])++;
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(BIG_ENDIAN, value))) < 0));
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(LITTLE_ENDIAN, value))) < 0));
        value[0] -= 2;
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(BIG_ENDIAN, value))) > 0));
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(LITTLE_ENDIAN, value))) > 0));
        (value[0])++;
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(BIG_ENDIAN, value, 0, 31))) > 0));
        Assert.assertTrue(((buffer.compareTo(wrappedBuffer(LITTLE_ENDIAN, value, 0, 31))) > 0));
        Assert.assertTrue(((buffer.slice(0, 31).compareTo(wrappedBuffer(BIG_ENDIAN, value))) < 0));
        Assert.assertTrue(((buffer.slice(0, 31).compareTo(wrappedBuffer(LITTLE_ENDIAN, value))) < 0));
    }

    @Test
    public void testToString() {
        buffer.clear();
        buffer.writeBytes(copiedBuffer("Hello, World!", ISO_8859_1));
        Assert.assertEquals("Hello, World!", buffer.toString(ISO_8859_1));
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
    public void testToByteBuffer1() {
        byte[] value = new byte[buffer.capacity()];
        random.nextBytes(value);
        buffer.clear();
        buffer.writeBytes(value);
        Assert.assertEquals(ByteBuffer.wrap(value), buffer.toByteBuffer());
    }

    @Test
    public void testToByteBuffer2() {
        byte[] value = new byte[buffer.capacity()];
        random.nextBytes(value);
        buffer.clear();
        buffer.writeBytes(value);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            Assert.assertEquals(ByteBuffer.wrap(value, i, AbstractChannelBufferTest.BLOCK_SIZE), buffer.toByteBuffer(i, AbstractChannelBufferTest.BLOCK_SIZE));
        }
    }

    @Test
    public void testToByteBuffer3() {
        Assert.assertEquals(buffer.order(), buffer.toByteBuffer().order());
    }

    @Test
    public void testToByteBuffers1() {
        byte[] value = new byte[buffer.capacity()];
        random.nextBytes(value);
        buffer.clear();
        buffer.writeBytes(value);
        ByteBuffer[] nioBuffers = buffer.toByteBuffers();
        int length = 0;
        for (ByteBuffer b : nioBuffers) {
            length += b.remaining();
        }
        ByteBuffer nioBuffer = ByteBuffer.allocate(length);
        for (ByteBuffer b : nioBuffers) {
            nioBuffer.put(b);
        }
        nioBuffer.flip();
        Assert.assertEquals(ByteBuffer.wrap(value), nioBuffer);
    }

    @Test
    public void testToByteBuffers2() {
        byte[] value = new byte[buffer.capacity()];
        random.nextBytes(value);
        buffer.clear();
        buffer.writeBytes(value);
        for (int i = 0; i < (((buffer.capacity()) - (AbstractChannelBufferTest.BLOCK_SIZE)) + 1); i += AbstractChannelBufferTest.BLOCK_SIZE) {
            ByteBuffer[] nioBuffers = buffer.toByteBuffers(i, AbstractChannelBufferTest.BLOCK_SIZE);
            ByteBuffer nioBuffer = ByteBuffer.allocate(AbstractChannelBufferTest.BLOCK_SIZE);
            for (ByteBuffer b : nioBuffers) {
                nioBuffer.put(b);
            }
            nioBuffer.flip();
            Assert.assertEquals(ByteBuffer.wrap(value, i, AbstractChannelBufferTest.BLOCK_SIZE), nioBuffer);
        }
    }

    @Test
    public void testSkipBytes1() {
        buffer.setIndex(((AbstractChannelBufferTest.CAPACITY) / 4), ((AbstractChannelBufferTest.CAPACITY) / 2));
        buffer.skipBytes(((AbstractChannelBufferTest.CAPACITY) / 4));
        Assert.assertEquals((((AbstractChannelBufferTest.CAPACITY) / 4) * 2), buffer.readerIndex());
        try {
            buffer.skipBytes((((AbstractChannelBufferTest.CAPACITY) / 4) + 1));
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Should remain unchanged.
        Assert.assertEquals((((AbstractChannelBufferTest.CAPACITY) / 4) * 2), buffer.readerIndex());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSkipBytes2() {
        buffer.clear();
        buffer.writeZero(buffer.capacity());
        try {
            buffer.skipBytes(LF);
            Assert.fail();
        } catch (NoSuchElementException e) {
            // Expected
        }
        buffer.skipBytes(NUL);
        Assert.assertEquals(0, buffer.readerIndex());
        buffer.clear();
        buffer.writeBytes(new byte[]{ 1, 2, 3, 4, 0 });
        buffer.skipBytes(NUL);
        Assert.assertEquals(4, buffer.readerIndex());
    }

    @Test
    public void testHashCode() {
        ChannelBuffer elemA = buffer(15);
        ChannelBuffer elemB = directBuffer(15);
        elemA.writeBytes(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5 });
        elemB.writeBytes(new byte[]{ 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 9 });
        Set<ChannelBuffer> set = new HashSet<ChannelBuffer>();
        set.add(elemA);
        set.add(elemB);
        Assert.assertEquals(2, set.size());
        Assert.assertTrue(set.contains(elemA.copy()));
        Assert.assertTrue(set.contains(elemB.copy()));
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
    }

    // Test case for https://github.com/netty/netty/issues/325
    @Test
    public void testDiscardAllReadBytes() {
        buffer.writerIndex(buffer.capacity());
        buffer.readerIndex(buffer.writerIndex());
        buffer.discardReadBytes();
    }
}

