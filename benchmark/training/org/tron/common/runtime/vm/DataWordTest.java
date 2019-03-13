/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.common.runtime.vm;


import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;


@Slf4j
public class DataWordTest {
    @Test
    public void testAddPerformance() {
        boolean enabled = false;
        if (enabled) {
            byte[] one = new byte[]{ 1, 49, 84, 65, 1, 49, 84, 65, 1, 49, 84, 65, 1, 49, 84, 65, 1, 49, 84, 65, 1, 49, 84, 65, 1, 49, 84, 65, 1, 49, 84, 65 };// Random value

            int ITERATIONS = 10000000;
            long now1 = System.currentTimeMillis();
            for (int i = 0; i < ITERATIONS; i++) {
                DataWord x = new DataWord(one);
                x.add(x);
            }
            System.out.println((("Add1: " + ((System.currentTimeMillis()) - now1)) + "ms"));
            long now2 = System.currentTimeMillis();
            for (int i = 0; i < ITERATIONS; i++) {
                DataWord x = new DataWord(one);
                x.add2(x);
            }
            System.out.println((("Add2: " + ((System.currentTimeMillis()) - now2)) + "ms"));
        } else {
            System.out.println("ADD performance test is disabled.");
        }
    }

    @Test
    public void testAdd2() {
        byte[] two = new byte[32];
        two[31] = ((byte) (255));// 0x000000000000000000000000000000000000000000000000000000000000ff

        DataWord x = new DataWord(two);
        x.add(new DataWord(two));
        System.out.println(Hex.toHexString(x.getData()));
        DataWord y = new DataWord(two);
        y.add2(new DataWord(two));
        System.out.println(Hex.toHexString(y.getData()));
    }

    @Test
    public void testAdd3() {
        byte[] three = new byte[32];
        for (int i = 0; i < (three.length); i++) {
            three[i] = ((byte) (255));
        }
        DataWord x = new DataWord(three);
        x.add(new DataWord(three));
        Assert.assertEquals(32, x.getData().length);
        System.out.println(Hex.toHexString(x.getData()));
        // FAIL
        // DataWord y = new DataWord(three);
        // y.add2(new DataWord(three));
        // System.out.println(Hex.toHexString(y.getData()));
    }

    @Test
    public void testMod() {
        String expected = "000000000000000000000000000000000000000000000000000000000000001a";
        byte[] one = new byte[32];
        one[31] = 30;// 0x000000000000000000000000000000000000000000000000000000000000001e

        byte[] two = new byte[32];
        for (int i = 0; i < (two.length); i++) {
            two[i] = ((byte) (255));
        }
        two[31] = 86;// 0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff56

        DataWord x = new DataWord(one);// System.out.println(x.value());

        DataWord y = new DataWord(two);// System.out.println(y.value());

        y.mod(x);
        Assert.assertEquals(32, y.getData().length);
        Assert.assertEquals(expected, Hex.toHexString(y.getData()));
    }

    @Test
    public void testMul() {
        byte[] one = new byte[32];
        one[31] = 1;// 0x0000000000000000000000000000000000000000000000000000000000000001

        byte[] two = new byte[32];
        two[11] = 1;// 0x0000000000000000000000010000000000000000000000000000000000000000

        DataWord x = new DataWord(one);// System.out.println(x.value());

        DataWord y = new DataWord(two);// System.out.println(y.value());

        x.mul(y);
        Assert.assertEquals(32, y.getData().length);
        Assert.assertEquals("0000000000000000000000010000000000000000000000000000000000000000", Hex.toHexString(y.getData()));
    }

    @Test
    public void testMulOverflow() {
        byte[] one = new byte[32];
        one[30] = 1;// 0x0000000000000000000000000000000000000000000000000000000000000100

        byte[] two = new byte[32];
        two[0] = 1;// 0x1000000000000000000000000000000000000000000000000000000000000000

        DataWord x = new DataWord(one);// System.out.println(x.value());

        DataWord y = new DataWord(two);// System.out.println(y.value());

        x.mul(y);
        Assert.assertEquals(32, y.getData().length);
        Assert.assertEquals("0100000000000000000000000000000000000000000000000000000000000000", Hex.toHexString(y.getData()));
    }

    @Test
    public void testDiv() {
        byte[] one = new byte[32];
        one[30] = 1;
        one[31] = 44;// 0x000000000000000000000000000000000000000000000000000000000000012c

        byte[] two = new byte[32];
        two[31] = 15;// 0x000000000000000000000000000000000000000000000000000000000000000f

        DataWord x = new DataWord(one);
        DataWord y = new DataWord(two);
        x.div(y);
        Assert.assertEquals(32, x.getData().length);
        Assert.assertEquals("0000000000000000000000000000000000000000000000000000000000000014", Hex.toHexString(x.getData()));
    }

    @Test
    public void testDivZero() {
        byte[] one = new byte[32];
        one[30] = 5;// 0x0000000000000000000000000000000000000000000000000000000000000500

        byte[] two = new byte[32];
        DataWord x = new DataWord(one);
        DataWord y = new DataWord(two);
        x.div(y);
        Assert.assertEquals(32, x.getData().length);
        Assert.assertTrue(x.isZero());
    }

    @Test
    public void testSDivNegative() {
        // one is -300 as 256-bit signed integer:
        byte[] one = Hex.decode("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffed4");
        byte[] two = new byte[32];
        two[31] = 15;
        DataWord x = new DataWord(one);
        DataWord y = new DataWord(two);
        x.sDiv(y);
        Assert.assertEquals(32, x.getData().length);
        Assert.assertEquals("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffec", x.toString());
    }

    @Test
    public void testPow() {
        BigInteger x = BigInteger.valueOf(Integer.MAX_VALUE);
        BigInteger y = BigInteger.valueOf(1000);
        BigInteger result1 = x.modPow(x, y);
        BigInteger result2 = DataWordTest.pow(x, y);
        System.out.println(result1);
        System.out.println(result2);
    }

    @Test
    public void testSignExtend1() {
        DataWord x = new DataWord(Hex.decode("f2"));
        byte k = 0;
        String expected = "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff2";
        x.signExtend(k);
        System.out.println(x.toString());
        Assert.assertEquals(expected, x.toString());
    }

    @Test
    public void testSignExtend2() {
        DataWord x = new DataWord(Hex.decode("f2"));
        byte k = 1;
        String expected = "00000000000000000000000000000000000000000000000000000000000000f2";
        x.signExtend(k);
        System.out.println(x.toString());
        Assert.assertEquals(expected, x.toString());
    }

    @Test
    public void testSignExtend3() {
        byte k = 1;
        DataWord x = new DataWord(Hex.decode("0f00ab"));
        String expected = "00000000000000000000000000000000000000000000000000000000000000ab";
        x.signExtend(k);
        System.out.println(x.toString());
        Assert.assertEquals(expected, x.toString());
    }

    @Test
    public void testSignExtend4() {
        byte k = 1;
        DataWord x = new DataWord(Hex.decode("ffff"));
        String expected = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
        x.signExtend(k);
        System.out.println(x.toString());
        Assert.assertEquals(expected, x.toString());
    }

    @Test
    public void testSignExtend5() {
        byte k = 3;
        DataWord x = new DataWord(Hex.decode("ffffffff"));
        String expected = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
        x.signExtend(k);
        System.out.println(x.toString());
        Assert.assertEquals(expected, x.toString());
    }

    @Test
    public void testSignExtend6() {
        byte k = 3;
        DataWord x = new DataWord(Hex.decode("ab02345678"));
        String expected = "0000000000000000000000000000000000000000000000000000000002345678";
        x.signExtend(k);
        System.out.println(x.toString());
        Assert.assertEquals(expected, x.toString());
    }

    @Test
    public void testSignExtend7() {
        byte k = 3;
        DataWord x = new DataWord(Hex.decode("ab82345678"));
        String expected = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffff82345678";
        x.signExtend(k);
        System.out.println(x.toString());
        Assert.assertEquals(expected, x.toString());
    }

    @Test
    public void testSignExtend8() {
        byte k = 30;
        DataWord x = new DataWord(Hex.decode("ff34567882345678823456788234567882345678823456788234567882345678"));
        String expected = "0034567882345678823456788234567882345678823456788234567882345678";
        x.signExtend(k);
        System.out.println(x.toString());
        Assert.assertEquals(expected, x.toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSignExtendException1() {
        byte k = -1;
        DataWord x = new DataWord();
        x.signExtend(k);// should throw an exception

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSignExtendException2() {
        byte k = 32;
        DataWord x = new DataWord();
        x.signExtend(k);// should throw an exception

    }

    @Test
    public void testAddModOverflow() {
        testAddMod("9999999999999999999999999999999999999999999999999999999999999999", "8888888888888888888888888888888888888888888888888888888888888888", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        testAddMod("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
    }

    @Test
    public void testMulMod1() {
        DataWord wr = new DataWord(Hex.decode("9999999999999999999999999999999999999999999999999999999999999999"));
        DataWord w1 = new DataWord(Hex.decode("01"));
        DataWord w2 = new DataWord(Hex.decode("9999999999999999999999999999999999999999999999999999999999999998"));
        wr.mulmod(w1, w2);
        Assert.assertEquals(32, wr.getData().length);
        Assert.assertEquals("0000000000000000000000000000000000000000000000000000000000000001", Hex.toHexString(wr.getData()));
    }

    @Test
    public void testMulMod2() {
        DataWord wr = new DataWord(Hex.decode("9999999999999999999999999999999999999999999999999999999999999999"));
        DataWord w1 = new DataWord(Hex.decode("01"));
        DataWord w2 = new DataWord(Hex.decode("9999999999999999999999999999999999999999999999999999999999999999"));
        wr.mulmod(w1, w2);
        Assert.assertEquals(32, wr.getData().length);
        Assert.assertTrue(wr.isZero());
    }

    @Test
    public void testMulModZero() {
        DataWord wr = new DataWord(Hex.decode("00"));
        DataWord w1 = new DataWord(Hex.decode("9999999999999999999999999999999999999999999999999999999999999999"));
        DataWord w2 = new DataWord(Hex.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));
        wr.mulmod(w1, w2);
        Assert.assertEquals(32, wr.getData().length);
        Assert.assertTrue(wr.isZero());
    }

    @Test
    public void testMulModZeroWord1() {
        DataWord wr = new DataWord(Hex.decode("9999999999999999999999999999999999999999999999999999999999999999"));
        DataWord w1 = new DataWord(Hex.decode("00"));
        DataWord w2 = new DataWord(Hex.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));
        wr.mulmod(w1, w2);
        Assert.assertEquals(32, wr.getData().length);
        Assert.assertTrue(wr.isZero());
    }

    @Test
    public void testMulModZeroWord2() {
        DataWord wr = new DataWord(Hex.decode("9999999999999999999999999999999999999999999999999999999999999999"));
        DataWord w1 = new DataWord(Hex.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));
        DataWord w2 = new DataWord(Hex.decode("00"));
        wr.mulmod(w1, w2);
        Assert.assertEquals(32, wr.getData().length);
        Assert.assertTrue(wr.isZero());
    }

    @Test
    public void testMulModOverflow() {
        DataWord wr = new DataWord(Hex.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));
        DataWord w1 = new DataWord(Hex.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));
        DataWord w2 = new DataWord(Hex.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));
        wr.mulmod(w1, w2);
        Assert.assertEquals(32, wr.getData().length);
        Assert.assertTrue(wr.isZero());
    }
}

