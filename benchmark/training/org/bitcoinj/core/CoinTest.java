/**
 * Copyright 2014 Andreas Schildbach
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


import org.junit.Assert;
import org.junit.Test;

import static org.bitcoinj.core.NetworkParameters.MAX_MONEY.value;


public class CoinTest {
    @Test
    public void testParseCoin() {
        // String version
        Assert.assertEquals(CENT, parseCoin("0.01"));
        Assert.assertEquals(CENT, parseCoin("1E-2"));
        Assert.assertEquals(COIN.add(CENT), parseCoin("1.01"));
        Assert.assertEquals(COIN.negate(), parseCoin("-1"));
        try {
            parseCoin("2E-20");
            Assert.fail("should not have accepted fractional satoshis");
        } catch (IllegalArgumentException expected) {
        } catch (Exception e) {
            Assert.fail("should throw IllegalArgumentException");
        }
        Assert.assertEquals(1, parseCoin("0.00000001").value);
        Assert.assertEquals(1, parseCoin("0.000000010").value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseCoinOverprecise() {
        parseCoin("0.000000011");
    }

    @Test
    public void testParseCoinInexact() {
        Assert.assertEquals(1, parseCoinInexact("0.00000001").value);
        Assert.assertEquals(1, parseCoinInexact("0.000000011").value);
    }

    @Test
    public void testValueOf() {
        // int version
        Assert.assertEquals(CENT, valueOf(0, 1));
        Assert.assertEquals(SATOSHI, valueOf(1));
        Assert.assertEquals(NEGATIVE_SATOSHI, valueOf((-1)));
        Assert.assertEquals(NetworkParameters.MAX_MONEY, valueOf(MAX_MONEY.value));
        Assert.assertEquals(NetworkParameters.MAX_MONEY.negate(), valueOf(((value) * (-1))));
        valueOf(((value) + 1));
        valueOf((((value) * (-1)) - 1));
        valueOf(Long.MAX_VALUE);
        valueOf(Long.MIN_VALUE);
        try {
            valueOf(1, (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            valueOf((-1), 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testOperators() {
        Assert.assertTrue(SATOSHI.isPositive());
        Assert.assertFalse(SATOSHI.isNegative());
        Assert.assertFalse(SATOSHI.isZero());
        Assert.assertFalse(NEGATIVE_SATOSHI.isPositive());
        Assert.assertTrue(NEGATIVE_SATOSHI.isNegative());
        Assert.assertFalse(NEGATIVE_SATOSHI.isZero());
        Assert.assertFalse(ZERO.isPositive());
        Assert.assertFalse(ZERO.isNegative());
        Assert.assertTrue(ZERO.isZero());
        Assert.assertTrue(valueOf(2).isGreaterThan(valueOf(1)));
        Assert.assertFalse(valueOf(2).isGreaterThan(valueOf(2)));
        Assert.assertFalse(valueOf(1).isGreaterThan(valueOf(2)));
        Assert.assertTrue(valueOf(1).isLessThan(valueOf(2)));
        Assert.assertFalse(valueOf(2).isLessThan(valueOf(2)));
        Assert.assertFalse(valueOf(2).isLessThan(valueOf(1)));
    }

    @Test(expected = ArithmeticException.class)
    public void testMultiplicationOverflow() {
        Coin.Coin.valueOf(Long.MAX_VALUE).multiply(2);
    }

    @Test(expected = ArithmeticException.class)
    public void testMultiplicationUnderflow() {
        Coin.Coin.valueOf(Long.MIN_VALUE).multiply(2);
    }

    @Test(expected = ArithmeticException.class)
    public void testAdditionOverflow() {
        Coin.Coin.valueOf(Long.MAX_VALUE).add(Coin.SATOSHI);
    }

    @Test(expected = ArithmeticException.class)
    public void testSubstractionUnderflow() {
        Coin.Coin.valueOf(Long.MIN_VALUE).subtract(Coin.SATOSHI);
    }

    @Test
    public void testToFriendlyString() {
        Assert.assertEquals("1.00 BTC", COIN.toFriendlyString());
        Assert.assertEquals("1.23 BTC", valueOf(1, 23).toFriendlyString());
        Assert.assertEquals("0.001 BTC", COIN.divide(1000).toFriendlyString());
        Assert.assertEquals("-1.23 BTC", valueOf(1, 23).negate().toFriendlyString());
    }

    /**
     * Test the bitcoinValueToPlainString amount formatter
     */
    @Test
    public void testToPlainString() {
        Assert.assertEquals("0.0015", Coin.Coin.valueOf(150000).toPlainString());
        Assert.assertEquals("1.23", parseCoin("1.23").toPlainString());
        Assert.assertEquals("0.1", parseCoin("0.1").toPlainString());
        Assert.assertEquals("1.1", parseCoin("1.1").toPlainString());
        Assert.assertEquals("21.12", parseCoin("21.12").toPlainString());
        Assert.assertEquals("321.123", parseCoin("321.123").toPlainString());
        Assert.assertEquals("4321.1234", parseCoin("4321.1234").toPlainString());
        Assert.assertEquals("54321.12345", parseCoin("54321.12345").toPlainString());
        Assert.assertEquals("654321.123456", parseCoin("654321.123456").toPlainString());
        Assert.assertEquals("7654321.1234567", parseCoin("7654321.1234567").toPlainString());
        Assert.assertEquals("87654321.12345678", parseCoin("87654321.12345678").toPlainString());
        // check there are no trailing zeros
        Assert.assertEquals("1", parseCoin("1.0").toPlainString());
        Assert.assertEquals("2", parseCoin("2.00").toPlainString());
        Assert.assertEquals("3", parseCoin("3.000").toPlainString());
        Assert.assertEquals("4", parseCoin("4.0000").toPlainString());
        Assert.assertEquals("5", parseCoin("5.00000").toPlainString());
        Assert.assertEquals("6", parseCoin("6.000000").toPlainString());
        Assert.assertEquals("7", parseCoin("7.0000000").toPlainString());
        Assert.assertEquals("8", parseCoin("8.00000000").toPlainString());
    }
}

