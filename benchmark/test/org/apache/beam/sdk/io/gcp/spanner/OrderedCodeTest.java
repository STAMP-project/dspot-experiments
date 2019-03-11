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
package org.apache.beam.sdk.io.gcp.spanner;


import com.google.auto.value.AutoValue;
import java.util.Arrays;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.primitives.Bytes;
import org.apache.beam.vendor.guava.v20_0.com.google.common.primitives.UnsignedInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static OrderedCode.ESCAPE1;
import static OrderedCode.ESCAPE2;
import static OrderedCode.FF_CHARACTER;
import static OrderedCode.INFINITY;
import static OrderedCode.NULL_CHARACTER;
import static OrderedCode.SEPARATOR;


/**
 * A set of unit tests to verify {@link OrderedCode}.
 */
@RunWith(JUnit4.class)
public class OrderedCodeTest {
    /**
     * Data for a generic coding test case with known encoded outputs.
     */
    abstract static class CodingTestCase<T> {
        /**
         * The test value.
         */
        abstract T value();

        /**
         * Test value's encoding in increasing order (obtained from the C++ implementation).
         */
        abstract String increasingBytes();

        /**
         * Test value's encoding in dencreasing order (obtained from the C++ implementation).
         */
        abstract String decreasingBytes();

        // Helper methods to implement in concrete classes.
        abstract byte[] encodeIncreasing();

        abstract byte[] encodeDecreasing();

        T decodeIncreasing() {
            return decodeIncreasing(new OrderedCode(OrderedCodeTest.bytesFromHexString(increasingBytes())));
        }

        T decodeDecreasing() {
            return decodeDecreasing(new OrderedCode(OrderedCodeTest.bytesFromHexString(decreasingBytes())));
        }

        abstract T decodeIncreasing(OrderedCode orderedCode);

        abstract T decodeDecreasing(OrderedCode orderedCode);
    }

    @AutoValue
    abstract static class UnsignedNumber extends OrderedCodeTest.CodingTestCase<Long> {
        @Override
        byte[] encodeIncreasing() {
            OrderedCode orderedCode = new OrderedCode();
            orderedCode.writeNumIncreasing(value());
            return orderedCode.getEncodedBytes();
        }

        @Override
        byte[] encodeDecreasing() {
            OrderedCode orderedCode = new OrderedCode();
            orderedCode.writeNumDecreasing(value());
            return orderedCode.getEncodedBytes();
        }

        @Override
        Long decodeIncreasing(OrderedCode orderedCode) {
            return orderedCode.readNumIncreasing();
        }

        @Override
        Long decodeDecreasing(OrderedCode orderedCode) {
            return orderedCode.readNumDecreasing();
        }

        private static OrderedCodeTest.UnsignedNumber testCase(long value, String increasingBytes, String decreasingBytes) {
            return new AutoValue_OrderedCodeTest_UnsignedNumber(value, increasingBytes, decreasingBytes);
        }

        /**
         * Test cases for unsigned numbers, in increasing (unsigned) order by value.
         */
        private static final ImmutableList<OrderedCodeTest.UnsignedNumber> TEST_CASES = ImmutableList.of(OrderedCodeTest.UnsignedNumber.testCase(0, "00", "ff"), OrderedCodeTest.UnsignedNumber.testCase(1, "0101", "fefe"), OrderedCodeTest.UnsignedNumber.testCase(33, "0121", "fede"), OrderedCodeTest.UnsignedNumber.testCase(55000, "02d6d8", "fd2927"), OrderedCodeTest.UnsignedNumber.testCase(Integer.MAX_VALUE, "047fffffff", "fb80000000"), OrderedCodeTest.UnsignedNumber.testCase(Long.MAX_VALUE, "087fffffffffffffff", "f78000000000000000"), OrderedCodeTest.UnsignedNumber.testCase(Long.MIN_VALUE, "088000000000000000", "f77fffffffffffffff"), OrderedCodeTest.UnsignedNumber.testCase((-100), "08ffffffffffffff9c", "f70000000000000063"), OrderedCodeTest.UnsignedNumber.testCase((-1), "08ffffffffffffffff", "f70000000000000000"));
    }

    @AutoValue
    abstract static class BytesTest extends OrderedCodeTest.CodingTestCase<String> {
        @Override
        byte[] encodeIncreasing() {
            OrderedCode orderedCode = new OrderedCode();
            orderedCode.writeBytes(OrderedCodeTest.bytesFromHexString(value()));
            return orderedCode.getEncodedBytes();
        }

        @Override
        byte[] encodeDecreasing() {
            OrderedCode orderedCode = new OrderedCode();
            orderedCode.writeBytesDecreasing(OrderedCodeTest.bytesFromHexString(value()));
            return orderedCode.getEncodedBytes();
        }

        @Override
        String decodeIncreasing(OrderedCode orderedCode) {
            return OrderedCodeTest.bytesToHexString(orderedCode.readBytes());
        }

        @Override
        String decodeDecreasing(OrderedCode orderedCode) {
            return OrderedCodeTest.bytesToHexString(orderedCode.readBytesDecreasing());
        }

        private static OrderedCodeTest.BytesTest testCase(String value, String increasingBytes, String decreasingBytes) {
            return new AutoValue_OrderedCodeTest_BytesTest(value, increasingBytes, decreasingBytes);
        }

        /**
         * Test cases for byte arrays, in increasing order by value.
         */
        private static final ImmutableList<OrderedCodeTest.BytesTest> TEST_CASES = ImmutableList.of(OrderedCodeTest.BytesTest.testCase("", "0001", "fffe"), OrderedCodeTest.BytesTest.testCase("00", "00ff0001", "ff00fffe"), OrderedCodeTest.BytesTest.testCase("0000", "00ff00ff0001", "ff00ff00fffe"), OrderedCodeTest.BytesTest.testCase("0001", "00ff010001", "ff00fefffe"), OrderedCodeTest.BytesTest.testCase("0041", "00ff410001", "ff00befffe"), OrderedCodeTest.BytesTest.testCase("00ff", "00ffff000001", "ff0000fffffe"), OrderedCodeTest.BytesTest.testCase("01", "010001", "fefffe"), OrderedCodeTest.BytesTest.testCase("0100", "0100ff0001", "feff00fffe"), OrderedCodeTest.BytesTest.testCase("6f776c", "6f776c0001", "908893fffe"), OrderedCodeTest.BytesTest.testCase("ff", "ff000001", "00fffffe"), OrderedCodeTest.BytesTest.testCase("ff00", "ff0000ff0001", "00ffff00fffe"), OrderedCodeTest.BytesTest.testCase("ff01", "ff00010001", "00fffefffe"), OrderedCodeTest.BytesTest.testCase("ffff", "ff00ff000001", "00ff00fffffe"), OrderedCodeTest.BytesTest.testCase("ffffff", "ff00ff00ff000001", "00ff00ff00fffffe"));
    }

    @Test
    public void testUnsignedEncoding() {
        testEncoding(OrderedCodeTest.UnsignedNumber.TEST_CASES);
    }

    @Test
    public void testUnsignedDecoding() {
        testDecoding(OrderedCodeTest.UnsignedNumber.TEST_CASES);
    }

    @Test
    public void testUnsignedOrdering() {
        testOrdering(OrderedCodeTest.UnsignedNumber.TEST_CASES);
    }

    @Test
    public void testBytesEncoding() {
        testEncoding(OrderedCodeTest.BytesTest.TEST_CASES);
    }

    @Test
    public void testBytesDecoding() {
        testDecoding(OrderedCodeTest.BytesTest.TEST_CASES);
    }

    @Test
    public void testBytesOrdering() {
        testOrdering(OrderedCodeTest.BytesTest.TEST_CASES);
    }

    @Test
    public void testWriteInfinity() {
        OrderedCode orderedCode = new OrderedCode();
        try {
            orderedCode.readInfinity();
            Assert.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        orderedCode.writeInfinity();
        Assert.assertTrue(orderedCode.readInfinity());
        try {
            orderedCode.readInfinity();
            Assert.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testWriteInfinityDecreasing() {
        OrderedCode orderedCode = new OrderedCode();
        try {
            orderedCode.readInfinityDecreasing();
            Assert.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        orderedCode.writeInfinityDecreasing();
        Assert.assertTrue(orderedCode.readInfinityDecreasing());
        try {
            orderedCode.readInfinityDecreasing();
            Assert.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testWriteBytes() {
        byte[] first = new byte[]{ 'a', 'b', 'c' };
        byte[] second = new byte[]{ 'd', 'e', 'f' };
        byte[] last = new byte[]{ 'x', 'y', 'z' };
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeBytes(first);
        byte[] firstEncoded = orderedCode.getEncodedBytes();
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), first));
        orderedCode.writeBytes(first);
        orderedCode.writeBytes(second);
        orderedCode.writeBytes(last);
        byte[] allEncoded = orderedCode.getEncodedBytes();
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), first));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), second));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), last));
        orderedCode = new OrderedCode(firstEncoded);
        orderedCode.writeBytes(second);
        orderedCode.writeBytes(last);
        Assert.assertTrue(Arrays.equals(orderedCode.getEncodedBytes(), allEncoded));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), first));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), second));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), last));
        orderedCode = new OrderedCode(allEncoded);
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), first));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), second));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), last));
    }

    @Test
    public void testWriteBytesDecreasing() {
        byte[] first = new byte[]{ 'a', 'b', 'c' };
        byte[] second = new byte[]{ 'd', 'e', 'f' };
        byte[] last = new byte[]{ 'x', 'y', 'z' };
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeBytesDecreasing(first);
        byte[] firstEncoded = orderedCode.getEncodedBytes();
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), first));
        orderedCode.writeBytesDecreasing(first);
        orderedCode.writeBytesDecreasing(second);
        orderedCode.writeBytesDecreasing(last);
        byte[] allEncoded = orderedCode.getEncodedBytes();
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), first));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), second));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), last));
        orderedCode = new OrderedCode(firstEncoded);
        orderedCode.writeBytesDecreasing(second);
        orderedCode.writeBytesDecreasing(last);
        Assert.assertTrue(Arrays.equals(orderedCode.getEncodedBytes(), allEncoded));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), first));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), second));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), last));
        orderedCode = new OrderedCode(allEncoded);
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), first));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), second));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytesDecreasing(), last));
    }

    @Test
    public void testWriteNumIncreasing() {
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeNumIncreasing(0);
        orderedCode.writeNumIncreasing(1);
        orderedCode.writeNumIncreasing(Long.MIN_VALUE);
        orderedCode.writeNumIncreasing(Long.MAX_VALUE);
        Assert.assertEquals(0, orderedCode.readNumIncreasing());
        Assert.assertEquals(1, orderedCode.readNumIncreasing());
        Assert.assertEquals(Long.MIN_VALUE, orderedCode.readNumIncreasing());
        Assert.assertEquals(Long.MAX_VALUE, orderedCode.readNumIncreasing());
    }

    @Test
    public void testWriteNumIncreasing_unsignedInt() {
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeNumIncreasing(UnsignedInteger.fromIntBits(0));
        orderedCode.writeNumIncreasing(UnsignedInteger.fromIntBits(1));
        orderedCode.writeNumIncreasing(UnsignedInteger.fromIntBits(Integer.MIN_VALUE));
        orderedCode.writeNumIncreasing(UnsignedInteger.fromIntBits(Integer.MAX_VALUE));
        Assert.assertEquals(0, orderedCode.readNumIncreasing());
        Assert.assertEquals(1, orderedCode.readNumIncreasing());
        Assert.assertEquals((((long) (Integer.MAX_VALUE)) + 1L), orderedCode.readNumIncreasing());
        Assert.assertEquals(Integer.MAX_VALUE, orderedCode.readNumIncreasing());
    }

    @Test
    public void testWriteNumDecreasing() {
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeNumDecreasing(0);
        orderedCode.writeNumDecreasing(1);
        orderedCode.writeNumDecreasing(Long.MIN_VALUE);
        orderedCode.writeNumDecreasing(Long.MAX_VALUE);
        Assert.assertEquals(0, orderedCode.readNumDecreasing());
        Assert.assertEquals(1, orderedCode.readNumDecreasing());
        Assert.assertEquals(Long.MIN_VALUE, orderedCode.readNumDecreasing());
        Assert.assertEquals(Long.MAX_VALUE, orderedCode.readNumDecreasing());
    }

    @Test
    public void testWriteNumDecreasing_unsignedInt() {
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeNumDecreasing(UnsignedInteger.fromIntBits(0));
        orderedCode.writeNumDecreasing(UnsignedInteger.fromIntBits(1));
        orderedCode.writeNumDecreasing(UnsignedInteger.fromIntBits(Integer.MIN_VALUE));
        orderedCode.writeNumDecreasing(UnsignedInteger.fromIntBits(Integer.MAX_VALUE));
        Assert.assertEquals(0, orderedCode.readNumDecreasing());
        Assert.assertEquals(1, orderedCode.readNumDecreasing());
        Assert.assertEquals((((long) (Integer.MAX_VALUE)) + 1L), orderedCode.readNumDecreasing());
        Assert.assertEquals(Integer.MAX_VALUE, orderedCode.readNumDecreasing());
    }

    /**
     * Assert that encoding various long values via {@link OrderedCode#writeSignedNumIncreasing(long)}
     * produces the expected bytes. Expected byte sequences were generated via the c++ (authoritative)
     * implementation of OrderedCode::WriteSignedNumIncreasing.
     */
    @Test
    public void testSignedNumIncreasing_write() {
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("003f8000000000000000", Long.MIN_VALUE);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("003f8000000000000001", ((Long.MIN_VALUE) + 1));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("077fffffff", ((Integer.MIN_VALUE) - 1L));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("0780000000", Integer.MIN_VALUE);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("0780000001", ((Integer.MIN_VALUE) + 1));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("3fbf", (-65));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("40", (-64));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("41", (-63));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("7d", (-3));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("7e", (-2));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("7f", (-1));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("80", 0);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("81", 1);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("82", 2);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("83", 3);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("bf", 63);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("c040", 64);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("c041", 65);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("f87ffffffe", ((Integer.MAX_VALUE) - 1));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("f87fffffff", Integer.MAX_VALUE);
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("f880000000", ((Integer.MAX_VALUE) + 1L));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("ffc07ffffffffffffffe", ((Long.MAX_VALUE) - 1));
        OrderedCodeTest.assertSignedNumIncreasingEncodingEquals("ffc07fffffffffffffff", Long.MAX_VALUE);
    }

    /**
     * Assert that decoding various sequences of bytes via {@link OrderedCode#readSignedNumIncreasing()} produces the expected long value. Input byte sequences
     * were generated via the c++ (authoritative) implementation of
     * OrderedCode::WriteSignedNumIncreasing.
     */
    @Test
    public void testSignedNumIncreasing_read() {
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(Long.MIN_VALUE, "003f8000000000000000");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(((Long.MIN_VALUE) + 1), "003f8000000000000001");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(((Integer.MIN_VALUE) - 1L), "077fffffff");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(Integer.MIN_VALUE, "0780000000");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(((Integer.MIN_VALUE) + 1), "0780000001");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals((-65), "3fbf");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals((-64), "40");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals((-63), "41");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals((-3), "7d");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals((-2), "7e");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals((-1), "7f");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(0, "80");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(1, "81");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(2, "82");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(3, "83");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(63, "bf");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(64, "c040");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(65, "c041");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(((Integer.MAX_VALUE) - 1), "f87ffffffe");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(Integer.MAX_VALUE, "f87fffffff");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(((Integer.MAX_VALUE) + 1L), "f880000000");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(((Long.MAX_VALUE) - 1), "ffc07ffffffffffffffe");
        OrderedCodeTest.assertDecodedSignedNumIncreasingEquals(Long.MAX_VALUE, "ffc07fffffffffffffff");
    }

    /**
     * Assert that for various long values, encoding (via {@link OrderedCode#writeSignedNumIncreasing(long)}) and then decoding (via {@link OrderedCode#readSignedNumIncreasing()}) results in the original value.
     */
    @Test
    public void testSignedNumIncreasing_writeAndRead() {
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(Long.MIN_VALUE);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(((Long.MIN_VALUE) + 1));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(((Integer.MIN_VALUE) - 1L));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(Integer.MIN_VALUE);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(((Integer.MIN_VALUE) + 1));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless((-65));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless((-64));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless((-63));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless((-3));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless((-2));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless((-1));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(0);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(1);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(2);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(3);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(63);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(64);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(65);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(((Integer.MAX_VALUE) - 1));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(Integer.MAX_VALUE);
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(((Integer.MAX_VALUE) + 1L));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(((Long.MAX_VALUE) - 1));
        OrderedCodeTest.assertSignedNumIncreasingWriteAndReadIsLossless(Long.MAX_VALUE);
    }

    /**
     * Assert that for various long values, encoding (via {@link OrderedCode#writeSignedNumDecreasing(long)}) and then decoding (via {@link OrderedCode#readSignedNumDecreasing()}) results in the original value.
     */
    @Test
    public void testSignedNumDecreasing_writeAndRead() {
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(Long.MIN_VALUE);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(((Long.MIN_VALUE) + 1));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(((Integer.MIN_VALUE) - 1L));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(Integer.MIN_VALUE);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(((Integer.MIN_VALUE) + 1));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless((-65));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless((-64));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless((-63));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless((-3));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless((-2));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless((-1));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(0);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(1);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(2);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(3);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(63);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(64);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(65);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(((Integer.MAX_VALUE) - 1));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(Integer.MAX_VALUE);
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(((Integer.MAX_VALUE) + 1L));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(((Long.MAX_VALUE) - 1));
        OrderedCodeTest.assertSignedNumDecreasingWriteAndReadIsLossless(Long.MAX_VALUE);
    }

    /**
     * Ensures that numbers encoded as "decreasing" do indeed sort in reverse order.
     */
    @Test
    public void testDecreasing() {
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeSignedNumDecreasing(10L);
        byte[] ten = orderedCode.getEncodedBytes();
        orderedCode = new OrderedCode();
        orderedCode.writeSignedNumDecreasing(20L);
        byte[] twenty = orderedCode.getEncodedBytes();
        // In decreasing order, twenty preceeds ten.
        Assert.assertTrue(((compare(twenty, ten)) < 0));
    }

    @Test
    public void testLog2Floor_Positive() {
        OrderedCode orderedCode = new OrderedCode();
        Assert.assertEquals(0, orderedCode.log2Floor(1));
        Assert.assertEquals(1, orderedCode.log2Floor(2));
        Assert.assertEquals(1, orderedCode.log2Floor(3));
        Assert.assertEquals(2, orderedCode.log2Floor(4));
        Assert.assertEquals(5, orderedCode.log2Floor(63));
        Assert.assertEquals(6, orderedCode.log2Floor(64));
        Assert.assertEquals(62, orderedCode.log2Floor(Long.MAX_VALUE));
    }

    /**
     * OrderedCode.log2Floor(long) is defined to return -1 given an input of zero (because that's what
     * Bits::Log2Floor64(uint64) does).
     */
    @Test
    public void testLog2Floor_zero() {
        OrderedCode orderedCode = new OrderedCode();
        Assert.assertEquals((-1), orderedCode.log2Floor(0));
    }

    @Test
    public void testLog2Floor_negative() {
        OrderedCode orderedCode = new OrderedCode();
        try {
            orderedCode.log2Floor((-1));
            Assert.fail("Expected an IllegalArgumentException.");
        } catch (IllegalArgumentException expected) {
            // Expected!
        }
    }

    @Test
    public void testGetSignedEncodingLength() {
        OrderedCode orderedCode = new OrderedCode();
        Assert.assertEquals(10, orderedCode.getSignedEncodingLength(Long.MIN_VALUE));
        Assert.assertEquals(10, orderedCode.getSignedEncodingLength((~(1L << 62))));
        Assert.assertEquals(9, orderedCode.getSignedEncodingLength(((~(1L << 62)) + 1)));
        Assert.assertEquals(3, orderedCode.getSignedEncodingLength((-8193)));
        Assert.assertEquals(2, orderedCode.getSignedEncodingLength((-8192)));
        Assert.assertEquals(2, orderedCode.getSignedEncodingLength((-65)));
        Assert.assertEquals(1, orderedCode.getSignedEncodingLength((-64)));
        Assert.assertEquals(1, orderedCode.getSignedEncodingLength((-2)));
        Assert.assertEquals(1, orderedCode.getSignedEncodingLength((-1)));
        Assert.assertEquals(1, orderedCode.getSignedEncodingLength(0));
        Assert.assertEquals(1, orderedCode.getSignedEncodingLength(1));
        Assert.assertEquals(1, orderedCode.getSignedEncodingLength(63));
        Assert.assertEquals(2, orderedCode.getSignedEncodingLength(64));
        Assert.assertEquals(2, orderedCode.getSignedEncodingLength(8191));
        Assert.assertEquals(3, orderedCode.getSignedEncodingLength(8192));
        Assert.assertEquals(9, ((orderedCode.getSignedEncodingLength((1L << 62))) - 1));
        Assert.assertEquals(10, orderedCode.getSignedEncodingLength((1L << 62)));
        Assert.assertEquals(10, orderedCode.getSignedEncodingLength(Long.MAX_VALUE));
    }

    @Test
    public void testWriteTrailingBytes() {
        byte[] escapeChars = new byte[]{ ESCAPE1, NULL_CHARACTER, SEPARATOR, ESCAPE2, INFINITY, FF_CHARACTER };
        byte[] anotherArray = new byte[]{ 'a', 'b', 'c', 'd', 'e' };
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeTrailingBytes(escapeChars);
        Assert.assertTrue(Arrays.equals(orderedCode.getEncodedBytes(), escapeChars));
        Assert.assertTrue(Arrays.equals(orderedCode.readTrailingBytes(), escapeChars));
        try {
            orderedCode.readInfinity();
            Assert.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        orderedCode = new OrderedCode();
        orderedCode.writeTrailingBytes(anotherArray);
        Assert.assertTrue(Arrays.equals(orderedCode.getEncodedBytes(), anotherArray));
        Assert.assertTrue(Arrays.equals(orderedCode.readTrailingBytes(), anotherArray));
    }

    @Test
    public void testMixedWrite() {
        byte[] first = new byte[]{ 'a', 'b', 'c' };
        byte[] second = new byte[]{ 'd', 'e', 'f' };
        byte[] last = new byte[]{ 'x', 'y', 'z' };
        byte[] escapeChars = new byte[]{ ESCAPE1, NULL_CHARACTER, SEPARATOR, ESCAPE2, INFINITY, FF_CHARACTER };
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeBytes(first);
        orderedCode.writeBytes(second);
        orderedCode.writeBytes(last);
        orderedCode.writeInfinity();
        orderedCode.writeNumIncreasing(0);
        orderedCode.writeNumIncreasing(1);
        orderedCode.writeNumIncreasing(Long.MIN_VALUE);
        orderedCode.writeNumIncreasing(Long.MAX_VALUE);
        orderedCode.writeSignedNumIncreasing(0);
        orderedCode.writeSignedNumIncreasing(1);
        orderedCode.writeSignedNumIncreasing(Long.MIN_VALUE);
        orderedCode.writeSignedNumIncreasing(Long.MAX_VALUE);
        orderedCode.writeTrailingBytes(escapeChars);
        byte[] allEncoded = orderedCode.getEncodedBytes();
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), first));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), second));
        Assert.assertFalse(orderedCode.readInfinity());
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), last));
        Assert.assertTrue(orderedCode.readInfinity());
        Assert.assertEquals(0, orderedCode.readNumIncreasing());
        Assert.assertEquals(1, orderedCode.readNumIncreasing());
        Assert.assertFalse(orderedCode.readInfinity());
        Assert.assertEquals(Long.MIN_VALUE, orderedCode.readNumIncreasing());
        Assert.assertEquals(Long.MAX_VALUE, orderedCode.readNumIncreasing());
        Assert.assertEquals(0, orderedCode.readSignedNumIncreasing());
        Assert.assertEquals(1, orderedCode.readSignedNumIncreasing());
        Assert.assertFalse(orderedCode.readInfinity());
        Assert.assertEquals(Long.MIN_VALUE, orderedCode.readSignedNumIncreasing());
        Assert.assertEquals(Long.MAX_VALUE, orderedCode.readSignedNumIncreasing());
        Assert.assertTrue(Arrays.equals(orderedCode.getEncodedBytes(), escapeChars));
        Assert.assertTrue(Arrays.equals(orderedCode.readTrailingBytes(), escapeChars));
        orderedCode = new OrderedCode(allEncoded);
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), first));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), second));
        Assert.assertFalse(orderedCode.readInfinity());
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), last));
        Assert.assertTrue(orderedCode.readInfinity());
        Assert.assertEquals(0, orderedCode.readNumIncreasing());
        Assert.assertEquals(1, orderedCode.readNumIncreasing());
        Assert.assertFalse(orderedCode.readInfinity());
        Assert.assertEquals(Long.MIN_VALUE, orderedCode.readNumIncreasing());
        Assert.assertEquals(Long.MAX_VALUE, orderedCode.readNumIncreasing());
        Assert.assertEquals(0, orderedCode.readSignedNumIncreasing());
        Assert.assertEquals(1, orderedCode.readSignedNumIncreasing());
        Assert.assertFalse(orderedCode.readInfinity());
        Assert.assertEquals(Long.MIN_VALUE, orderedCode.readSignedNumIncreasing());
        Assert.assertEquals(Long.MAX_VALUE, orderedCode.readSignedNumIncreasing());
        Assert.assertTrue(Arrays.equals(orderedCode.getEncodedBytes(), escapeChars));
        Assert.assertTrue(Arrays.equals(orderedCode.readTrailingBytes(), escapeChars));
    }

    @Test
    public void testEdgeCases() {
        byte[] ffChar = new byte[]{ FF_CHARACTER };
        byte[] nullChar = new byte[]{ NULL_CHARACTER };
        byte[] separatorEncoded = new byte[]{ ESCAPE1, SEPARATOR };
        byte[] ffCharEncoded = new byte[]{ ESCAPE1, NULL_CHARACTER };
        byte[] nullCharEncoded = new byte[]{ ESCAPE2, FF_CHARACTER };
        byte[] infinityEncoded = new byte[]{ ESCAPE2, INFINITY };
        OrderedCode orderedCode = new OrderedCode();
        orderedCode.writeBytes(ffChar);
        orderedCode.writeBytes(nullChar);
        orderedCode.writeInfinity();
        Assert.assertTrue(Arrays.equals(orderedCode.getEncodedBytes(), Bytes.concat(ffCharEncoded, separatorEncoded, nullCharEncoded, separatorEncoded, infinityEncoded)));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), ffChar));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), nullChar));
        Assert.assertTrue(orderedCode.readInfinity());
        orderedCode = new OrderedCode(Bytes.concat(ffCharEncoded, separatorEncoded));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), ffChar));
        orderedCode = new OrderedCode(Bytes.concat(nullCharEncoded, separatorEncoded));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), nullChar));
        byte[] invalidEncodingForRead = new byte[]{ ESCAPE2, ESCAPE2, ESCAPE1, SEPARATOR };
        orderedCode = new OrderedCode(invalidEncodingForRead);
        try {
            orderedCode.readBytes();
            Assert.fail("Should have failed.");
        } catch (Exception e) {
            // Expected
        }
        Assert.assertTrue(orderedCode.hasRemainingEncodedBytes());
    }

    @Test
    public void testHasRemainingEncodedBytes() {
        byte[] bytes = new byte[]{ 'a', 'b', 'c' };
        long number = 12345;
        // Empty
        OrderedCode orderedCode = new OrderedCode();
        Assert.assertFalse(orderedCode.hasRemainingEncodedBytes());
        // First and only field of each type.
        orderedCode.writeBytes(bytes);
        Assert.assertTrue(orderedCode.hasRemainingEncodedBytes());
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), bytes));
        Assert.assertFalse(orderedCode.hasRemainingEncodedBytes());
        orderedCode.writeNumIncreasing(number);
        Assert.assertTrue(orderedCode.hasRemainingEncodedBytes());
        Assert.assertEquals(orderedCode.readNumIncreasing(), number);
        Assert.assertFalse(orderedCode.hasRemainingEncodedBytes());
        orderedCode.writeSignedNumIncreasing(number);
        Assert.assertTrue(orderedCode.hasRemainingEncodedBytes());
        Assert.assertEquals(orderedCode.readSignedNumIncreasing(), number);
        Assert.assertFalse(orderedCode.hasRemainingEncodedBytes());
        orderedCode.writeInfinity();
        Assert.assertTrue(orderedCode.hasRemainingEncodedBytes());
        Assert.assertTrue(orderedCode.readInfinity());
        Assert.assertFalse(orderedCode.hasRemainingEncodedBytes());
        orderedCode.writeTrailingBytes(bytes);
        Assert.assertTrue(orderedCode.hasRemainingEncodedBytes());
        Assert.assertTrue(Arrays.equals(orderedCode.readTrailingBytes(), bytes));
        Assert.assertFalse(orderedCode.hasRemainingEncodedBytes());
        // Two fields of same type.
        orderedCode.writeBytes(bytes);
        orderedCode.writeBytes(bytes);
        Assert.assertTrue(orderedCode.hasRemainingEncodedBytes());
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), bytes));
        Assert.assertTrue(Arrays.equals(orderedCode.readBytes(), bytes));
        Assert.assertFalse(orderedCode.hasRemainingEncodedBytes());
    }

    @Test
    public void testOrderingInfinity() {
        OrderedCode inf = new OrderedCode();
        inf.writeInfinity();
        OrderedCode negInf = new OrderedCode();
        negInf.writeInfinityDecreasing();
        OrderedCode longValue = new OrderedCode();
        longValue.writeSignedNumIncreasing(1);
        Assert.assertTrue(((compare(inf.getEncodedBytes(), negInf.getEncodedBytes())) > 0));
        Assert.assertTrue(((compare(longValue.getEncodedBytes(), negInf.getEncodedBytes())) > 0));
        Assert.assertTrue(((compare(inf.getEncodedBytes(), longValue.getEncodedBytes())) > 0));
    }
}

