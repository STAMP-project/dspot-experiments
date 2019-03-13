/**
 * Copyright 2011 Thilo Planz
 * Copyright 2014 Andreas Schildbach
 * Copyright 2017 Nicola Atzei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.core;


import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class UtilsTest {
    @Test
    public void testReverseBytes() {
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 4, 5 }, Utils.reverseBytes(new byte[]{ 5, 4, 3, 2, 1 }));
    }

    @Test
    public void testMaxOfMostFreq() throws Exception {
        Assert.assertEquals(0, Utils.maxOfMostFreq());
        Assert.assertEquals(0, Utils.maxOfMostFreq(0, 0, 1));
        Assert.assertEquals(2, Utils.maxOfMostFreq(1, 1, 2, 2));
        Assert.assertEquals(1, Utils.maxOfMostFreq(1, 1, 2, 2, 1));
        Assert.assertEquals((-1), Utils.maxOfMostFreq((-1), (-1), 2, 2, (-1)));
    }

    @Test
    public void compactEncoding() throws Exception {
        Assert.assertEquals(new BigInteger("1234560000", 16), Utils.decodeCompactBits(85079126L));
        Assert.assertEquals(new BigInteger("c0de000000", 16), Utils.decodeCompactBits(100712670));
        Assert.assertEquals(85079126L, Utils.encodeCompactBits(new BigInteger("1234560000", 16)));
        Assert.assertEquals(100712670L, Utils.encodeCompactBits(new BigInteger("c0de000000", 16)));
    }

    @Test
    public void dateTimeFormat() {
        Assert.assertEquals("2014-11-16T10:54:33Z", Utils.dateTimeFormat(1416135273781L));
        Assert.assertEquals("2014-11-16T10:54:33Z", Utils.dateTimeFormat(new Date(1416135273781L)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bigIntegerToBytes_convertNegativeNumber() {
        BigInteger b = BigInteger.valueOf((-1));
        Utils.bigIntegerToBytes(b, 32);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bigIntegerToBytes_convertWithNegativeLength() {
        BigInteger b = BigInteger.valueOf(10);
        Utils.bigIntegerToBytes(b, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bigIntegerToBytes_convertWithZeroLength() {
        BigInteger b = BigInteger.valueOf(10);
        Utils.bigIntegerToBytes(b, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bigIntegerToBytes_insufficientLength() {
        BigInteger b = BigInteger.valueOf(2048);// base 2

        Utils.bigIntegerToBytes(b, 1);
    }

    @Test
    public void bigIntegerToBytes_convertZero() {
        BigInteger b = BigInteger.valueOf(0);
        byte[] expected = new byte[]{ 0 };
        byte[] actual = Utils.bigIntegerToBytes(b, 1);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void bigIntegerToBytes_singleByteSignFit() {
        BigInteger b = BigInteger.valueOf(15);
        byte[] expected = new byte[]{ 15 };
        byte[] actual = Utils.bigIntegerToBytes(b, 1);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void bigIntegerToBytes_paddedSingleByte() {
        BigInteger b = BigInteger.valueOf(15);
        byte[] expected = new byte[]{ 0, 15 };
        byte[] actual = Utils.bigIntegerToBytes(b, 2);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void bigIntegerToBytes_singleByteSignDoesNotFit() {
        BigInteger b = BigInteger.valueOf(128);// 128 (2-compl does not fit in one byte)

        byte[] expected = new byte[]{ -128 };
        // -128 == 1000_0000 (compl-2)
        byte[] actual = Utils.bigIntegerToBytes(b, 1);
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void runtime() {
        // This test assumes it is run within a Java runtime for desktop computers.
        Assert.assertTrue(((Utils.isOpenJDKRuntime()) || (Utils.isOracleJavaRuntime())));
        Assert.assertFalse(Utils.isAndroidRuntime());
    }
}

