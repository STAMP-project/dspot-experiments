/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.util;


import HConstants.EMPTY_BYTE_ARRAY;
import HConstants.EMPTY_BYTE_BUFFER;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.io.WritableUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@Category({ MiscTests.class, SmallTests.class })
@RunWith(Parameterized.class)
public class TestByteBufferUtils {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestByteBufferUtils.class);

    private static final String UNSAFE_AVAIL_NAME = "UNSAFE_AVAIL";

    private static final String UNSAFE_UNALIGNED_NAME = "UNSAFE_UNALIGNED";

    private byte[] array;

    public TestByteBufferUtils(boolean useUnsafeIfPossible) throws Exception {
        if (useUnsafeIfPossible) {
            TestByteBufferUtils.detectAvailabilityOfUnsafe();
        } else {
            TestByteBufferUtils.disableUnsafe();
        }
    }

    private static final int MAX_VLONG_LENGTH = 9;

    private static final Collection<Long> testNumbers;

    static {
        SortedSet<Long> a = new TreeSet<>();
        for (int i = 0; i <= 63; ++i) {
            long v = (-1L) << i;
            Assert.assertTrue((v < 0));
            TestByteBufferUtils.addNumber(a, v);
            v = (1L << i) - 1;
            Assert.assertTrue((v >= 0));
            TestByteBufferUtils.addNumber(a, v);
        }
        testNumbers = Collections.unmodifiableSet(a);
        System.err.println((((("Testing variable-length long serialization using: " + (TestByteBufferUtils.testNumbers)) + " (count: ") + (TestByteBufferUtils.testNumbers.size())) + ")"));
        Assert.assertEquals(1753, TestByteBufferUtils.testNumbers.size());
        Assert.assertEquals(Long.MIN_VALUE, a.first().longValue());
        Assert.assertEquals(Long.MAX_VALUE, a.last().longValue());
    }

    @Test
    public void testReadWriteVLong() {
        for (long l : TestByteBufferUtils.testNumbers) {
            ByteBuffer b = ByteBuffer.allocate(TestByteBufferUtils.MAX_VLONG_LENGTH);
            ByteBufferUtils.writeVLong(b, l);
            b.flip();
            Assert.assertEquals(l, ByteBufferUtils.readVLong(b));
        }
    }

    @Test
    public void testConsistencyWithHadoopVLong() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for (long l : TestByteBufferUtils.testNumbers) {
            baos.reset();
            ByteBuffer b = ByteBuffer.allocate(TestByteBufferUtils.MAX_VLONG_LENGTH);
            ByteBufferUtils.writeVLong(b, l);
            String bufStr = Bytes.toStringBinary(b.array(), b.arrayOffset(), b.position());
            WritableUtils.writeVLong(dos, l);
            String baosStr = Bytes.toStringBinary(baos.toByteArray());
            Assert.assertEquals(baosStr, bufStr);
        }
    }

    /**
     * Test copying to stream from buffer.
     */
    @Test
    public void testMoveBufferToStream() {
        final int arrayOffset = 7;
        final int initialPosition = 10;
        final int endPadding = 5;
        byte[] arrayWrapper = new byte[((arrayOffset + initialPosition) + (array.length)) + endPadding];
        System.arraycopy(array, 0, arrayWrapper, (arrayOffset + initialPosition), array.length);
        ByteBuffer buffer = ByteBuffer.wrap(arrayWrapper, arrayOffset, (initialPosition + (array.length))).slice();
        Assert.assertEquals((initialPosition + (array.length)), buffer.limit());
        Assert.assertEquals(0, buffer.position());
        buffer.position(initialPosition);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ByteBufferUtils.moveBufferToStream(bos, buffer, array.length);
        } catch (IOException e) {
            Assert.fail("IOException in testCopyToStream()");
        }
        Assert.assertArrayEquals(array, bos.toByteArray());
        Assert.assertEquals((initialPosition + (array.length)), buffer.position());
    }

    /**
     * Test copying to stream from buffer with offset.
     *
     * @throws IOException
     * 		On test failure.
     */
    @Test
    public void testCopyToStreamWithOffset() throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteBufferUtils.copyBufferToStream(bos, buffer, ((array.length) / 2), ((array.length) / 2));
        byte[] returnedArray = bos.toByteArray();
        for (int i = 0; i < ((array.length) / 2); ++i) {
            int pos = ((array.length) / 2) + i;
            Assert.assertEquals(returnedArray[i], array[pos]);
        }
    }

    /**
     * Test copying data from stream.
     *
     * @throws IOException
     * 		On test failure.
     */
    @Test
    public void testCopyFromStream() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(array.length);
        ByteArrayInputStream bis = new ByteArrayInputStream(array);
        DataInputStream dis = new DataInputStream(bis);
        ByteBufferUtils.copyFromStreamToBuffer(buffer, dis, ((array.length) / 2));
        ByteBufferUtils.copyFromStreamToBuffer(buffer, dis, ((array.length) - ((array.length) / 2)));
        for (int i = 0; i < (array.length); ++i) {
            Assert.assertEquals(array[i], buffer.get(i));
        }
    }

    /**
     * Test copying from buffer.
     */
    @Test
    public void testCopyFromBuffer() {
        ByteBuffer srcBuffer = ByteBuffer.allocate(array.length);
        ByteBuffer dstBuffer = ByteBuffer.allocate(array.length);
        srcBuffer.put(array);
        ByteBufferUtils.copyFromBufferToBuffer(srcBuffer, dstBuffer, ((array.length) / 2), ((array.length) / 4));
        for (int i = 0; i < ((array.length) / 4); ++i) {
            Assert.assertEquals(srcBuffer.get((i + ((array.length) / 2))), dstBuffer.get(i));
        }
    }

    /**
     * Test 7-bit encoding of integers.
     *
     * @throws IOException
     * 		On test failure.
     */
    @Test
    public void testCompressedInt() throws IOException {
        testCompressedInt(0);
        testCompressedInt(Integer.MAX_VALUE);
        testCompressedInt(Integer.MIN_VALUE);
        for (int i = 0; i < 3; i++) {
            testCompressedInt(((128 << i) - 1));
        }
        for (int i = 0; i < 3; i++) {
            testCompressedInt((128 << i));
        }
    }

    /**
     * Test how much bytes we need to store integer.
     */
    @Test
    public void testIntFitsIn() {
        Assert.assertEquals(1, ByteBufferUtils.intFitsIn(0));
        Assert.assertEquals(1, ByteBufferUtils.intFitsIn(1));
        Assert.assertEquals(2, ByteBufferUtils.intFitsIn((1 << 8)));
        Assert.assertEquals(3, ByteBufferUtils.intFitsIn((1 << 16)));
        Assert.assertEquals(4, ByteBufferUtils.intFitsIn((-1)));
        Assert.assertEquals(4, ByteBufferUtils.intFitsIn(Integer.MAX_VALUE));
        Assert.assertEquals(4, ByteBufferUtils.intFitsIn(Integer.MIN_VALUE));
    }

    /**
     * Test how much bytes we need to store long.
     */
    @Test
    public void testLongFitsIn() {
        Assert.assertEquals(1, ByteBufferUtils.longFitsIn(0));
        Assert.assertEquals(1, ByteBufferUtils.longFitsIn(1));
        Assert.assertEquals(3, ByteBufferUtils.longFitsIn((1L << 16)));
        Assert.assertEquals(5, ByteBufferUtils.longFitsIn((1L << 32)));
        Assert.assertEquals(8, ByteBufferUtils.longFitsIn((-1)));
        Assert.assertEquals(8, ByteBufferUtils.longFitsIn(Long.MIN_VALUE));
        Assert.assertEquals(8, ByteBufferUtils.longFitsIn(Long.MAX_VALUE));
    }

    /**
     * Test if we are comparing equal bytes.
     */
    @Test
    public void testArePartEqual() {
        byte[] array = new byte[]{ 1, 2, 3, 4, 5, 1, 2, 3, 4 };
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Assert.assertTrue(ByteBufferUtils.arePartsEqual(buffer, 0, 4, 5, 4));
        Assert.assertTrue(ByteBufferUtils.arePartsEqual(buffer, 1, 2, 6, 2));
        Assert.assertFalse(ByteBufferUtils.arePartsEqual(buffer, 1, 2, 6, 3));
        Assert.assertFalse(ByteBufferUtils.arePartsEqual(buffer, 1, 3, 6, 2));
        Assert.assertFalse(ByteBufferUtils.arePartsEqual(buffer, 0, 3, 6, 3));
    }

    /**
     * Test serializing int to bytes
     */
    @Test
    public void testPutInt() {
        testPutInt(0);
        testPutInt(Integer.MAX_VALUE);
        for (int i = 0; i < 3; i++) {
            testPutInt(((128 << i) - 1));
        }
        for (int i = 0; i < 3; i++) {
            testPutInt((128 << i));
        }
    }

    @Test
    public void testToBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put(new byte[]{ 0, 1, 2, 3, 4 });
        Assert.assertEquals(5, buffer.position());
        Assert.assertEquals(5, buffer.limit());
        byte[] copy = ByteBufferUtils.toBytes(buffer, 2);
        Assert.assertArrayEquals(new byte[]{ 2, 3, 4 }, copy);
        Assert.assertEquals(5, buffer.position());
        Assert.assertEquals(5, buffer.limit());
    }

    @Test
    public void testToPrimitiveTypes() {
        ByteBuffer buffer = ByteBuffer.allocate(15);
        long l = 988L;
        int i = 135;
        short s = 7;
        buffer.putLong(l);
        buffer.putShort(s);
        buffer.putInt(i);
        Assert.assertEquals(l, ByteBufferUtils.toLong(buffer, 0));
        Assert.assertEquals(s, ByteBufferUtils.toShort(buffer, 8));
        Assert.assertEquals(i, ByteBufferUtils.toInt(buffer, 10));
    }

    @Test
    public void testCopyFromArrayToBuffer() {
        byte[] b = new byte[15];
        b[0] = -1;
        long l = 988L;
        int i = 135;
        short s = 7;
        Bytes.putLong(b, 1, l);
        Bytes.putShort(b, 9, s);
        Bytes.putInt(b, 11, i);
        ByteBuffer buffer = ByteBuffer.allocate(14);
        ByteBufferUtils.copyFromArrayToBuffer(buffer, b, 1, 14);
        buffer.rewind();
        Assert.assertEquals(l, buffer.getLong());
        Assert.assertEquals(s, buffer.getShort());
        Assert.assertEquals(i, buffer.getInt());
    }

    @Test
    public void testCopyFromSrcToDestWithThreads() throws InterruptedException {
        List<byte[]> words = Arrays.asList(Bytes.toBytes("with"), Bytes.toBytes("great"), Bytes.toBytes("power"), Bytes.toBytes("comes"), Bytes.toBytes("great"), Bytes.toBytes("responsibility"));
        List<Integer> lengthes = words.stream().map(( v) -> v.length).collect(Collectors.toList());
        List<Integer> offsets = new ArrayList<>(words.size());
        for (int i = 0; i != (words.size()); ++i) {
            offsets.add(words.subList(0, i).stream().mapToInt(( v) -> v.length).sum());
        }
        int totalSize = words.stream().mapToInt(( v) -> v.length).sum();
        byte[] fullContent = new byte[totalSize];
        int offset = 0;
        for (byte[] w : words) {
            offset = Bytes.putBytes(fullContent, offset, w, 0, w.length);
        }
        // test copyFromBufferToArray
        for (ByteBuffer input : Arrays.asList(ByteBuffer.allocateDirect(totalSize), ByteBuffer.allocate(totalSize))) {
            words.forEach(input::put);
            byte[] output = new byte[totalSize];
            testCopyFromSrcToDestWithThreads(input, output, lengthes, offsets);
        }
        // test copyFromArrayToBuffer
        for (ByteBuffer output : Arrays.asList(ByteBuffer.allocateDirect(totalSize), ByteBuffer.allocate(totalSize))) {
            byte[] input = fullContent;
            testCopyFromSrcToDestWithThreads(input, output, lengthes, offsets);
        }
        // test copyFromBufferToBuffer
        for (ByteBuffer input : Arrays.asList(ByteBuffer.allocateDirect(totalSize), ByteBuffer.allocate(totalSize))) {
            words.forEach(input::put);
            for (ByteBuffer output : Arrays.asList(ByteBuffer.allocateDirect(totalSize), ByteBuffer.allocate(totalSize))) {
                testCopyFromSrcToDestWithThreads(input, output, lengthes, offsets);
            }
        }
    }

    @Test
    public void testCopyFromBufferToArray() {
        ByteBuffer buffer = ByteBuffer.allocate(15);
        buffer.put(((byte) (-1)));
        long l = 988L;
        int i = 135;
        short s = 7;
        buffer.putShort(s);
        buffer.putInt(i);
        buffer.putLong(l);
        byte[] b = new byte[15];
        ByteBufferUtils.copyFromBufferToArray(b, buffer, 1, 1, 14);
        Assert.assertEquals(s, Bytes.toShort(b, 1));
        Assert.assertEquals(i, Bytes.toInt(b, 3));
        Assert.assertEquals(l, Bytes.toLong(b, 7));
    }

    @Test
    public void testRelativeCopyFromBuffertoBuffer() {
        ByteBuffer bb1 = ByteBuffer.allocate(135);
        ByteBuffer bb2 = ByteBuffer.allocate(135);
        TestByteBufferUtils.fillBB(bb1, ((byte) (5)));
        ByteBufferUtils.copyFromBufferToBuffer(bb1, bb2);
        Assert.assertTrue(((bb1.position()) == (bb2.position())));
        Assert.assertTrue(((bb1.limit()) == (bb2.limit())));
        bb1 = ByteBuffer.allocateDirect(135);
        bb2 = ByteBuffer.allocateDirect(135);
        TestByteBufferUtils.fillBB(bb1, ((byte) (5)));
        ByteBufferUtils.copyFromBufferToBuffer(bb1, bb2);
        Assert.assertTrue(((bb1.position()) == (bb2.position())));
        Assert.assertTrue(((bb1.limit()) == (bb2.limit())));
    }

    @Test
    public void testCompareTo() {
        ByteBuffer bb1 = ByteBuffer.allocate(135);
        ByteBuffer bb2 = ByteBuffer.allocate(135);
        byte[] b = new byte[71];
        TestByteBufferUtils.fillBB(bb1, ((byte) (5)));
        TestByteBufferUtils.fillBB(bb2, ((byte) (5)));
        TestByteBufferUtils.fillArray(b, ((byte) (5)));
        Assert.assertEquals(0, ByteBufferUtils.compareTo(bb1, 0, bb1.remaining(), bb2, 0, bb2.remaining()));
        Assert.assertTrue(((ByteBufferUtils.compareTo(bb1, 0, bb1.remaining(), b, 0, b.length)) > 0));
        bb2.put(134, ((byte) (6)));
        Assert.assertTrue(((ByteBufferUtils.compareTo(bb1, 0, bb1.remaining(), bb2, 0, bb2.remaining())) < 0));
        bb2.put(6, ((byte) (4)));
        Assert.assertTrue(((ByteBufferUtils.compareTo(bb1, 0, bb1.remaining(), bb2, 0, bb2.remaining())) > 0));
        // Assert reverse comparing BB and bytearray works.
        ByteBuffer bb3 = ByteBuffer.allocate(135);
        TestByteBufferUtils.fillBB(bb3, ((byte) (0)));
        byte[] b3 = new byte[135];
        TestByteBufferUtils.fillArray(b3, ((byte) (1)));
        int result = ByteBufferUtils.compareTo(b3, 0, b3.length, bb3, 0, bb3.remaining());
        Assert.assertTrue((result > 0));
        result = ByteBufferUtils.compareTo(bb3, 0, bb3.remaining(), b3, 0, b3.length);
        Assert.assertTrue((result < 0));
        byte[] b4 = Bytes.toBytes("123");
        ByteBuffer bb4 = ByteBuffer.allocate((10 + (b4.length)));
        for (int i = 10; i < (bb4.capacity()); ++i) {
            bb4.put(i, b4[(i - 10)]);
        }
        result = ByteBufferUtils.compareTo(b4, 0, b4.length, bb4, 10, b4.length);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testEquals() {
        byte[] a = Bytes.toBytes("http://A");
        ByteBuffer bb = ByteBuffer.wrap(a);
        Assert.assertTrue(ByteBufferUtils.equals(EMPTY_BYTE_BUFFER, 0, 0, EMPTY_BYTE_BUFFER, 0, 0));
        Assert.assertFalse(ByteBufferUtils.equals(EMPTY_BYTE_BUFFER, 0, 0, bb, 0, a.length));
        Assert.assertFalse(ByteBufferUtils.equals(bb, 0, 0, EMPTY_BYTE_BUFFER, 0, a.length));
        Assert.assertTrue(ByteBufferUtils.equals(bb, 0, a.length, bb, 0, a.length));
        Assert.assertTrue(ByteBufferUtils.equals(EMPTY_BYTE_BUFFER, 0, 0, EMPTY_BYTE_ARRAY, 0, 0));
        Assert.assertFalse(ByteBufferUtils.equals(EMPTY_BYTE_BUFFER, 0, 0, a, 0, a.length));
        Assert.assertFalse(ByteBufferUtils.equals(bb, 0, a.length, EMPTY_BYTE_ARRAY, 0, 0));
        Assert.assertTrue(ByteBufferUtils.equals(bb, 0, a.length, a, 0, a.length));
    }
}

