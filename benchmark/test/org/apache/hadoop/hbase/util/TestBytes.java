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


import Bytes.BYTES_COMPARATOR;
import Bytes.SIZEOF_INT;
import Bytes.SIZEOF_LONG;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.io.WritableUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestBytes extends TestCase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBytes.class);

    public void testShort() throws Exception {
        TestBytes.testShort(false);
    }

    public void testShortUnsafe() throws Exception {
        TestBytes.testShort(true);
    }

    public void testNullHashCode() {
        byte[] b = null;
        Exception ee = null;
        try {
            Bytes.hashCode(b);
        } catch (Exception e) {
            ee = e;
        }
        TestCase.assertNotNull(ee);
    }

    public void testAdd() throws Exception {
        byte[] a = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        byte[] b = new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        byte[] c = new byte[]{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
        byte[] d = new byte[]{ 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
        byte[] result1 = Bytes.add(a, b, c);
        byte[] result2 = Bytes.add(new byte[][]{ a, b, c });
        TestCase.assertEquals(0, Bytes.compareTo(result1, result2));
        byte[] result4 = Bytes.add(result1, d);
        byte[] result5 = Bytes.add(new byte[][]{ result1, d });
        TestCase.assertEquals(0, Bytes.compareTo(result1, result2));
    }

    public void testSplit() throws Exception {
        byte[] lowest = Bytes.toBytes("AAA");
        byte[] middle = Bytes.toBytes("CCC");
        byte[] highest = Bytes.toBytes("EEE");
        byte[][] parts = Bytes.split(lowest, highest, 1);
        for (int i = 0; i < (parts.length); i++) {
            System.out.println(Bytes.toString(parts[i]));
        }
        TestCase.assertEquals(3, parts.length);
        TestCase.assertTrue(Bytes.equals(parts[1], middle));
        // Now divide into three parts.  Change highest so split is even.
        highest = Bytes.toBytes("DDD");
        parts = Bytes.split(lowest, highest, 2);
        for (int i = 0; i < (parts.length); i++) {
            System.out.println(Bytes.toString(parts[i]));
        }
        TestCase.assertEquals(4, parts.length);
        // Assert that 3rd part is 'CCC'.
        TestCase.assertTrue(Bytes.equals(parts[2], middle));
    }

    public void testSplit2() throws Exception {
        // More split tests.
        byte[] lowest = Bytes.toBytes("http://A");
        byte[] highest = Bytes.toBytes("http://z");
        byte[] middle = Bytes.toBytes("http://]");
        byte[][] parts = Bytes.split(lowest, highest, 1);
        for (int i = 0; i < (parts.length); i++) {
            System.out.println(Bytes.toString(parts[i]));
        }
        TestCase.assertEquals(3, parts.length);
        TestCase.assertTrue(Bytes.equals(parts[1], middle));
    }

    public void testSplit3() throws Exception {
        // Test invalid split cases
        byte[] low = new byte[]{ 1, 1, 1 };
        byte[] high = new byte[]{ 1, 1, 3 };
        // If swapped, should throw IAE
        try {
            Bytes.split(high, low, 1);
            TestCase.assertTrue("Should not be able to split if low > high", false);
        } catch (IllegalArgumentException iae) {
            // Correct
        }
        // Single split should work
        byte[][] parts = Bytes.split(low, high, 1);
        for (int i = 0; i < (parts.length); i++) {
            System.out.println(((("" + i) + " -> ") + (Bytes.toStringBinary(parts[i]))));
        }
        TestCase.assertTrue(("Returned split should have 3 parts but has " + (parts.length)), ((parts.length) == 3));
        // If split more than once, use additional byte to split
        parts = Bytes.split(low, high, 2);
        TestCase.assertTrue("Split with an additional byte", (parts != null));
        TestCase.assertEquals(parts.length, ((low.length) + 1));
        // Split 0 times should throw IAE
        try {
            parts = Bytes.split(low, high, 0);
            TestCase.assertTrue("Should not be able to split 0 times", false);
        } catch (IllegalArgumentException iae) {
            // Correct
        }
    }

    public void testToInt() throws Exception {
        int[] ints = new int[]{ -1, 123, Integer.MIN_VALUE, Integer.MAX_VALUE };
        for (int i = 0; i < (ints.length); i++) {
            byte[] b = Bytes.toBytes(ints[i]);
            TestCase.assertEquals(ints[i], Bytes.toInt(b));
            byte[] b2 = bytesWithOffset(b);
            TestCase.assertEquals(ints[i], Bytes.toInt(b2, 1));
            TestCase.assertEquals(ints[i], Bytes.toInt(b2, 1, SIZEOF_INT));
        }
    }

    public void testToLong() throws Exception {
        long[] longs = new long[]{ -1L, 123L, Long.MIN_VALUE, Long.MAX_VALUE };
        for (int i = 0; i < (longs.length); i++) {
            byte[] b = Bytes.toBytes(longs[i]);
            TestCase.assertEquals(longs[i], Bytes.toLong(b));
            byte[] b2 = bytesWithOffset(b);
            TestCase.assertEquals(longs[i], Bytes.toLong(b2, 1));
            TestCase.assertEquals(longs[i], Bytes.toLong(b2, 1, SIZEOF_LONG));
        }
    }

    public void testToFloat() throws Exception {
        float[] floats = new float[]{ -1.0F, 123.123F, Float.MAX_VALUE };
        for (int i = 0; i < (floats.length); i++) {
            byte[] b = Bytes.toBytes(floats[i]);
            TestCase.assertEquals(floats[i], Bytes.toFloat(b), 0.0F);
            byte[] b2 = bytesWithOffset(b);
            TestCase.assertEquals(floats[i], Bytes.toFloat(b2, 1), 0.0F);
        }
    }

    public void testToDouble() throws Exception {
        double[] doubles = new double[]{ Double.MIN_VALUE, Double.MAX_VALUE };
        for (int i = 0; i < (doubles.length); i++) {
            byte[] b = Bytes.toBytes(doubles[i]);
            TestCase.assertEquals(doubles[i], Bytes.toDouble(b), 0.0);
            byte[] b2 = bytesWithOffset(b);
            TestCase.assertEquals(doubles[i], Bytes.toDouble(b2, 1), 0.0);
        }
    }

    public void testToBigDecimal() throws Exception {
        BigDecimal[] decimals = new BigDecimal[]{ new BigDecimal("-1"), new BigDecimal("123.123"), new BigDecimal("123123123123") };
        for (int i = 0; i < (decimals.length); i++) {
            byte[] b = Bytes.toBytes(decimals[i]);
            TestCase.assertEquals(decimals[i], Bytes.toBigDecimal(b));
            byte[] b2 = bytesWithOffset(b);
            TestCase.assertEquals(decimals[i], Bytes.toBigDecimal(b2, 1, b.length));
        }
    }

    public void testToBytesForByteBuffer() {
        byte[] array = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        ByteBuffer target = ByteBuffer.wrap(array);
        target.position(2);
        target.limit(7);
        byte[] actual = Bytes.toBytes(target);
        byte[] expected = new byte[]{ 0, 1, 2, 3, 4, 5, 6 };
        TestCase.assertTrue(Arrays.equals(expected, actual));
        TestCase.assertEquals(2, target.position());
        TestCase.assertEquals(7, target.limit());
        ByteBuffer target2 = target.slice();
        TestCase.assertEquals(0, target2.position());
        TestCase.assertEquals(5, target2.limit());
        byte[] actual2 = Bytes.toBytes(target2);
        byte[] expected2 = new byte[]{ 2, 3, 4, 5, 6 };
        TestCase.assertTrue(Arrays.equals(expected2, actual2));
        TestCase.assertEquals(0, target2.position());
        TestCase.assertEquals(5, target2.limit());
    }

    public void testGetBytesForByteBuffer() {
        byte[] array = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        ByteBuffer target = ByteBuffer.wrap(array);
        target.position(2);
        target.limit(7);
        byte[] actual = Bytes.getBytes(target);
        byte[] expected = new byte[]{ 2, 3, 4, 5, 6 };
        TestCase.assertTrue(Arrays.equals(expected, actual));
        TestCase.assertEquals(2, target.position());
        TestCase.assertEquals(7, target.limit());
    }

    public void testReadAsVLong() throws Exception {
        long[] longs = new long[]{ -1L, 123L, Long.MIN_VALUE, Long.MAX_VALUE };
        for (int i = 0; i < (longs.length); i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(baos);
            WritableUtils.writeVLong(output, longs[i]);
            byte[] long_bytes_no_offset = baos.toByteArray();
            TestCase.assertEquals(longs[i], Bytes.readAsVLong(long_bytes_no_offset, 0));
            byte[] long_bytes_with_offset = bytesWithOffset(long_bytes_no_offset);
            TestCase.assertEquals(longs[i], Bytes.readAsVLong(long_bytes_with_offset, 1));
        }
    }

    public void testToStringBinaryForBytes() {
        byte[] array = new byte[]{ '0', '9', 'a', 'z', 'A', 'Z', '@', 1 };
        String actual = Bytes.toStringBinary(array);
        String expected = "09azAZ@\\x01";
        TestCase.assertEquals(expected, actual);
        String actual2 = Bytes.toStringBinary(array, 2, 3);
        String expected2 = "azA";
        TestCase.assertEquals(expected2, actual2);
    }

    public void testToStringBinaryForArrayBasedByteBuffer() {
        byte[] array = new byte[]{ '0', '9', 'a', 'z', 'A', 'Z', '@', 1 };
        ByteBuffer target = ByteBuffer.wrap(array);
        String actual = Bytes.toStringBinary(target);
        String expected = "09azAZ@\\x01";
        TestCase.assertEquals(expected, actual);
    }

    public void testToStringBinaryForReadOnlyByteBuffer() {
        byte[] array = new byte[]{ '0', '9', 'a', 'z', 'A', 'Z', '@', 1 };
        ByteBuffer target = ByteBuffer.wrap(array).asReadOnlyBuffer();
        String actual = Bytes.toStringBinary(target);
        String expected = "09azAZ@\\x01";
        TestCase.assertEquals(expected, actual);
    }

    public void testBinarySearch() throws Exception {
        byte[][] arr = new byte[][]{ new byte[]{ 1 }, new byte[]{ 3 }, new byte[]{ 5 }, new byte[]{ 7 }, new byte[]{ 9 }, new byte[]{ 11 }, new byte[]{ 13 }, new byte[]{ 15 } };
        byte[] key1 = new byte[]{ 3, 1 };
        byte[] key2 = new byte[]{ 4, 9 };
        byte[] key2_2 = new byte[]{ 4 };
        byte[] key3 = new byte[]{ 5, 11 };
        byte[] key4 = new byte[]{ 0 };
        byte[] key5 = new byte[]{ 2 };
        TestCase.assertEquals(1, Bytes.binarySearch(arr, key1, 0, 1));
        TestCase.assertEquals(0, Bytes.binarySearch(arr, key1, 1, 1));
        TestCase.assertEquals((-(2 + 1)), Arrays.binarySearch(arr, key2_2, BYTES_COMPARATOR));
        TestCase.assertEquals((-(2 + 1)), Bytes.binarySearch(arr, key2, 0, 1));
        TestCase.assertEquals(4, Bytes.binarySearch(arr, key2, 1, 1));
        TestCase.assertEquals(2, Bytes.binarySearch(arr, key3, 0, 1));
        TestCase.assertEquals(5, Bytes.binarySearch(arr, key3, 1, 1));
        TestCase.assertEquals((-1), Bytes.binarySearch(arr, key4, 0, 1));
        TestCase.assertEquals((-2), Bytes.binarySearch(arr, key5, 0, 1));
        // Search for values to the left and to the right of each item in the array.
        for (int i = 0; i < (arr.length); ++i) {
            TestCase.assertEquals((-(i + 1)), Bytes.binarySearch(arr, new byte[]{ ((byte) ((arr[i][0]) - 1)) }, 0, 1));
            TestCase.assertEquals((-(i + 2)), Bytes.binarySearch(arr, new byte[]{ ((byte) ((arr[i][0]) + 1)) }, 0, 1));
        }
    }

    public void testToStringBytesBinaryReversible() {
        // let's run test with 1000 randomly generated byte arrays
        Random rand = new Random(System.currentTimeMillis());
        byte[] randomBytes = new byte[1000];
        for (int i = 0; i < 1000; i++) {
            rand.nextBytes(randomBytes);
            verifyReversibleForBytes(randomBytes);
        }
        // some specific cases
        verifyReversibleForBytes(new byte[]{  });
        verifyReversibleForBytes(new byte[]{ '\\', 'x', 'A', 'D' });
        verifyReversibleForBytes(new byte[]{ '\\', 'x', 'A', 'D', '\\' });
    }

    public void testStartsWith() {
        TestCase.assertTrue(Bytes.startsWith(Bytes.toBytes("hello"), Bytes.toBytes("h")));
        TestCase.assertTrue(Bytes.startsWith(Bytes.toBytes("hello"), Bytes.toBytes("")));
        TestCase.assertTrue(Bytes.startsWith(Bytes.toBytes("hello"), Bytes.toBytes("hello")));
        TestCase.assertFalse(Bytes.startsWith(Bytes.toBytes("hello"), Bytes.toBytes("helloworld")));
        TestCase.assertFalse(Bytes.startsWith(Bytes.toBytes(""), Bytes.toBytes("hello")));
    }

    public void testIncrementBytes() throws IOException {
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes(10, 1));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes(12, 123435445));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes(124634654, 1));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes(10005460, 5005645));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes(1, (-1)));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes(10, (-1)));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes(10, (-5)));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes(1005435000, (-5)));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes(10, (-43657655)));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes((-1), 1));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes((-26), 5034520));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes((-10657200), 5));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes((-12343250), 45376475));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes((-10), (-5)));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes((-12343250), (-5)));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes((-12), (-34565445)));
        TestCase.assertTrue(TestBytes.checkTestIncrementBytes((-1546543452), (-34565445)));
    }

    public void testFixedSizeString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        Bytes.writeStringFixedSize(dos, "Hello", 5);
        Bytes.writeStringFixedSize(dos, "World", 18);
        Bytes.writeStringFixedSize(dos, "", 9);
        try {
            // Use a long dash which is three bytes in UTF-8. If encoding happens
            // using ISO-8859-1, this will fail.
            Bytes.writeStringFixedSize(dos, "Too\u2013Long", 9);
            TestCase.fail("Exception expected");
        } catch (IOException ex) {
            TestCase.assertEquals(("Trying to write 10 bytes (Too\\xE2\\x80\\x93Long) into a field of " + "length 9"), ex.getMessage());
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInputStream dis = new DataInputStream(bais);
        TestCase.assertEquals("Hello", Bytes.readStringFixedSize(dis, 5));
        TestCase.assertEquals("World", Bytes.readStringFixedSize(dis, 18));
        TestCase.assertEquals("", Bytes.readStringFixedSize(dis, 9));
    }

    public void testCopy() throws Exception {
        byte[] bytes = Bytes.toBytes("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        byte[] copy = Bytes.copy(bytes);
        TestCase.assertFalse((bytes == copy));
        TestCase.assertTrue(Bytes.equals(bytes, copy));
    }

    public void testToBytesBinaryTrailingBackslashes() throws Exception {
        try {
            Bytes.toBytesBinary("abc\\x00\\x01\\");
        } catch (StringIndexOutOfBoundsException ex) {
            TestCase.fail(("Illegal string access: " + (ex.getMessage())));
        }
    }

    public void testToStringBinary_toBytesBinary_Reversable() throws Exception {
        String bytes = Bytes.toStringBinary(Bytes.toBytes(2.17));
        TestCase.assertEquals(2.17, Bytes.toDouble(Bytes.toBytesBinary(bytes)), 0);
    }

    public void testUnsignedBinarySearch() {
        byte[] bytes = new byte[]{ 0, 5, 123, 127, -128, -100, -1 };
        Assert.assertEquals(1, Bytes.unsignedBinarySearch(bytes, 0, bytes.length, ((byte) (5))));
        Assert.assertEquals(3, Bytes.unsignedBinarySearch(bytes, 0, bytes.length, ((byte) (127))));
        Assert.assertEquals(4, Bytes.unsignedBinarySearch(bytes, 0, bytes.length, ((byte) (-128))));
        Assert.assertEquals(5, Bytes.unsignedBinarySearch(bytes, 0, bytes.length, ((byte) (-100))));
        Assert.assertEquals(6, Bytes.unsignedBinarySearch(bytes, 0, bytes.length, ((byte) (-1))));
        Assert.assertEquals(((-1) - 1), Bytes.unsignedBinarySearch(bytes, 0, bytes.length, ((byte) (2))));
        Assert.assertEquals(((-6) - 1), Bytes.unsignedBinarySearch(bytes, 0, bytes.length, ((byte) (-5))));
    }

    public void testUnsignedIncrement() {
        byte[] a = Bytes.toBytes(0);
        int a2 = Bytes.toInt(Bytes.unsignedCopyAndIncrement(a), 0);
        Assert.assertTrue((a2 == 1));
        byte[] b = Bytes.toBytes((-1));
        byte[] actual = Bytes.unsignedCopyAndIncrement(b);
        Assert.assertNotSame(b, actual);
        byte[] expected = new byte[]{ 1, 0, 0, 0, 0 };
        Assert.assertArrayEquals(expected, actual);
        byte[] c = Bytes.toBytes(255);// should wrap to the next significant byte

        int c2 = Bytes.toInt(Bytes.unsignedCopyAndIncrement(c), 0);
        Assert.assertTrue((c2 == 256));
    }

    public void testIndexOf() {
        byte[] array = Bytes.toBytes("hello");
        TestCase.assertEquals(1, Bytes.indexOf(array, ((byte) ('e'))));
        TestCase.assertEquals(4, Bytes.indexOf(array, ((byte) ('o'))));
        TestCase.assertEquals((-1), Bytes.indexOf(array, ((byte) ('a'))));
        TestCase.assertEquals(0, Bytes.indexOf(array, Bytes.toBytes("hel")));
        TestCase.assertEquals(2, Bytes.indexOf(array, Bytes.toBytes("ll")));
        TestCase.assertEquals((-1), Bytes.indexOf(array, Bytes.toBytes("hll")));
    }

    public void testContains() {
        byte[] array = Bytes.toBytes("hello world");
        TestCase.assertTrue(Bytes.contains(array, ((byte) ('e'))));
        TestCase.assertTrue(Bytes.contains(array, ((byte) ('d'))));
        TestCase.assertFalse(Bytes.contains(array, ((byte) ('a'))));
        TestCase.assertTrue(Bytes.contains(array, Bytes.toBytes("world")));
        TestCase.assertTrue(Bytes.contains(array, Bytes.toBytes("ello")));
        TestCase.assertFalse(Bytes.contains(array, Bytes.toBytes("owo")));
    }

    public void testZero() {
        byte[] array = Bytes.toBytes("hello");
        Bytes.zero(array);
        for (int i = 0; i < (array.length); i++) {
            TestCase.assertEquals(0, array[i]);
        }
        array = Bytes.toBytes("hello world");
        Bytes.zero(array, 2, 7);
        TestCase.assertFalse(((array[0]) == 0));
        TestCase.assertFalse(((array[1]) == 0));
        for (int i = 2; i < 9; i++) {
            TestCase.assertEquals(0, array[i]);
        }
        for (int i = 9; i < (array.length); i++) {
            TestCase.assertFalse(((array[i]) == 0));
        }
    }

    public void testPutBuffer() {
        byte[] b = new byte[100];
        for (byte i = 0; i < 100; i++) {
            Bytes.putByteBuffer(b, i, ByteBuffer.wrap(new byte[]{ i }));
        }
        for (byte i = 0; i < 100; i++) {
            Assert.assertEquals(i, b[i]);
        }
    }

    public void testToFromHex() {
        List<String> testStrings = new ArrayList<>(8);
        testStrings.addAll(Arrays.asList(new String[]{ "", "00", "A0", "ff", "FFffFFFFFFFFFF", "12", "0123456789abcdef", "283462839463924623984692834692346ABCDFEDDCA0" }));
        for (String testString : testStrings) {
            byte[] byteData = Bytes.fromHex(testString);
            Assert.assertEquals(((testString.length()) / 2), byteData.length);
            String result = Bytes.toHex(byteData);
            Assert.assertTrue(testString.equalsIgnoreCase(result));
        }
        List<byte[]> testByteData = new ArrayList<>(5);
        testByteData.addAll(Arrays.asList(new byte[][]{ new byte[0], new byte[1], new byte[10], new byte[]{ 1, 2, 3, 4, 5 }, new byte[]{ ((byte) (255)) } }));
        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            byte[] bytes = new byte[r.nextInt(100)];
            r.nextBytes(bytes);
            testByteData.add(bytes);
        }
        for (byte[] testData : testByteData) {
            String hexString = Bytes.toHex(testData);
            Assert.assertEquals(((testData.length) * 2), hexString.length());
            byte[] result = Bytes.fromHex(hexString);
            Assert.assertArrayEquals(testData, result);
        }
    }
}

