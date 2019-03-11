/**
 * Copyright 2017 Google Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink.subtle;


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for Bytes
 */
@RunWith(JUnit4.class)
public class BytesTest {
    // Some test arrays for the XOR operations.
    static final byte[] EMPTY = new byte[]{  };

    static final byte[] ONE_ONE = new byte[]{ 1 };

    static final byte[] TWO_ONES = new byte[]{ 1, 1 };

    static final byte[] THREE_ONES = new byte[]{ 1, 1, 1 };

    static final byte[] THREE_ZEROES = new byte[]{ 0, 0, 0 };

    static final byte[] TWO_FF = new byte[]{ ((byte) (255)), ((byte) (255)) };

    @Test
    public void xorTwoArgsBasicTest() {
        byte[] shouldBeZeroes = Bytes.xor(BytesTest.TWO_ONES, BytesTest.TWO_ONES);
        Assert.assertEquals(0, shouldBeZeroes[0]);
        Assert.assertEquals(0, shouldBeZeroes[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void xorTwoArgsSizeMismatchA() {
        Bytes.xor(BytesTest.THREE_ZEROES, BytesTest.TWO_ONES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void xorTwoArgsSizeMismatchB() {
        Bytes.xor(BytesTest.TWO_ONES, BytesTest.THREE_ZEROES);
    }

    @Test
    public void xorThreeArgsBasicTest() {
        byte[] shouldBeZeroes = Bytes.xor(BytesTest.TWO_ONES, 0, BytesTest.TWO_ONES, 0, 2);
        Assert.assertEquals(0, shouldBeZeroes[0]);
        Assert.assertEquals(0, shouldBeZeroes[1]);
        Assert.assertEquals(2, shouldBeZeroes.length);
    }

    @Test
    public void xorThreeArgsDifferentSizes() {
        byte[] shouldBeOne = Bytes.xor(BytesTest.THREE_ZEROES, 1, BytesTest.TWO_ONES, 0, 1);
        Assert.assertEquals(1, shouldBeOne[0]);
        Assert.assertEquals(1, shouldBeOne.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void xorThreeArgsTooLong() {
        Bytes.xor(BytesTest.THREE_ZEROES, 0, BytesTest.TWO_ONES, 0, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void xorThreeArgsTooLongOffsets() {
        Bytes.xor(BytesTest.THREE_ZEROES, 3, BytesTest.TWO_ONES, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void xorThreeArgsSizeMismatchB() {
        Bytes.xor(BytesTest.TWO_ONES, BytesTest.THREE_ZEROES);
    }

    @Test
    public void xorEndBasicTest() {
        byte[] r = Bytes.xorEnd(BytesTest.THREE_ONES, BytesTest.TWO_FF);
        Assert.assertEquals(1, r[0]);
        Assert.assertEquals(((byte) (254)), r[1]);
        Assert.assertEquals(((byte) (254)), r[2]);
        Assert.assertEquals(3, r.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void xorEndSizeMismatch() {
        Bytes.xorEnd(BytesTest.TWO_ONES, BytesTest.THREE_ZEROES);
    }

    @Test
    public void xorByteBufferNegativeLength() {
        ByteBuffer output = ByteBuffer.allocate(10);
        ByteBuffer x = ByteBuffer.allocate(10);
        ByteBuffer y = ByteBuffer.allocate(10);
        try {
            Bytes.xor(output, x, y, (-1));
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected;
        }
    }

    @Test
    public void xorByteBufferLengthLargerThanOutput() {
        ByteBuffer output = ByteBuffer.allocate(9);
        ByteBuffer x = ByteBuffer.allocate(10);
        ByteBuffer y = ByteBuffer.allocate(10);
        try {
            Bytes.xor(output, x, y, 10);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected;
        }
    }

    @Test
    public void xorByteBufferLengthLargerThanFirstInput() {
        ByteBuffer output = ByteBuffer.allocate(10);
        ByteBuffer x = ByteBuffer.allocate(9);
        ByteBuffer y = ByteBuffer.allocate(10);
        try {
            Bytes.xor(output, x, y, 10);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected;
        }
    }

    @Test
    public void xorByteBufferLengthLargerThanSecondInput() {
        ByteBuffer output = ByteBuffer.allocate(10);
        ByteBuffer x = ByteBuffer.allocate(10);
        ByteBuffer y = ByteBuffer.allocate(9);
        try {
            Bytes.xor(output, x, y, 10);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected;
        }
    }

    @Test
    public void xorByteBufferBasic() {
        ByteBuffer output = ByteBuffer.allocate(10);
        ByteBuffer x = ByteBuffer.allocate(10);
        ByteBuffer y = ByteBuffer.allocate(10);
        for (int i = 0; i < 10; i++) {
            x.put(((byte) (i)));
            y.put(((byte) (i + 1)));
        }
        x.flip();
        y.flip();
        Bytes.xor(output, x, y, 10);
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(output.get(i), (i ^ (i + 1)));
        }
    }
}

