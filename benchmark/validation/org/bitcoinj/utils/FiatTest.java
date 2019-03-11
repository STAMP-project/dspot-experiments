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
package org.bitcoinj.utils;


import org.junit.Assert;
import org.junit.Test;


public class FiatTest {
    @Test
    public void testParseAndValueOf() {
        Assert.assertEquals(Fiat.valueOf("EUR", 10000), Fiat.parseFiat("EUR", "1"));
        Assert.assertEquals(Fiat.valueOf("EUR", 100), Fiat.parseFiat("EUR", "0.01"));
        Assert.assertEquals(Fiat.valueOf("EUR", 1), Fiat.parseFiat("EUR", "0.0001"));
        Assert.assertEquals(Fiat.valueOf("EUR", (-10000)), Fiat.parseFiat("EUR", "-1"));
    }

    @Test
    public void testParseFiat() {
        Assert.assertEquals(1, Fiat.parseFiat("EUR", "0.0001").value);
        Assert.assertEquals(1, Fiat.parseFiat("EUR", "0.00010").value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFiatOverprecise() {
        Fiat.parseFiat("EUR", "0.00011");
    }

    @Test
    public void testParseFiatInexact() {
        Assert.assertEquals(1, Fiat.parseFiatInexact("EUR", "0.0001").value);
        Assert.assertEquals(1, Fiat.parseFiatInexact("EUR", "0.00011").value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFiatInexactInvalidAmount() {
        Fiat.parseFiatInexact("USD", "33.xx");
    }

    @Test
    public void testToFriendlyString() {
        Assert.assertEquals("1.00 EUR", Fiat.parseFiat("EUR", "1").toFriendlyString());
        Assert.assertEquals("1.23 EUR", Fiat.parseFiat("EUR", "1.23").toFriendlyString());
        Assert.assertEquals("0.0010 EUR", Fiat.parseFiat("EUR", "0.001").toFriendlyString());
        Assert.assertEquals("-1.23 EUR", Fiat.parseFiat("EUR", "-1.23").toFriendlyString());
    }

    @Test
    public void testToPlainString() {
        Assert.assertEquals("0.0015", Fiat.valueOf("EUR", 15).toPlainString());
        Assert.assertEquals("1.23", Fiat.parseFiat("EUR", "1.23").toPlainString());
        Assert.assertEquals("0.1", Fiat.parseFiat("EUR", "0.1").toPlainString());
        Assert.assertEquals("1.1", Fiat.parseFiat("EUR", "1.1").toPlainString());
        Assert.assertEquals("21.12", Fiat.parseFiat("EUR", "21.12").toPlainString());
        Assert.assertEquals("321.123", Fiat.parseFiat("EUR", "321.123").toPlainString());
        Assert.assertEquals("4321.1234", Fiat.parseFiat("EUR", "4321.1234").toPlainString());
        // check there are no trailing zeros
        Assert.assertEquals("1", Fiat.parseFiat("EUR", "1.0").toPlainString());
        Assert.assertEquals("2", Fiat.parseFiat("EUR", "2.00").toPlainString());
        Assert.assertEquals("3", Fiat.parseFiat("EUR", "3.000").toPlainString());
        Assert.assertEquals("4", Fiat.parseFiat("EUR", "4.0000").toPlainString());
    }

    @Test
    public void testComparing() {
        Assert.assertTrue(Fiat.parseFiat("EUR", "1.11").isLessThan(Fiat.parseFiat("EUR", "6.66")));
        Assert.assertTrue(Fiat.parseFiat("EUR", "6.66").isGreaterThan(Fiat.parseFiat("EUR", "2.56")));
    }

    @Test
    public void testSign() {
        Assert.assertTrue(Fiat.parseFiat("EUR", "-1").isNegative());
        Assert.assertTrue(Fiat.parseFiat("EUR", "-1").negate().isPositive());
        Assert.assertTrue(Fiat.parseFiat("EUR", "1").isPositive());
        Assert.assertTrue(Fiat.parseFiat("EUR", "0.00").isZero());
    }

    @Test
    public void testCurrencyCode() {
        Assert.assertEquals("RUB", Fiat.parseFiat("RUB", "66.6").getCurrencyCode());
    }

    @Test
    public void testValueFetching() {
        Fiat fiat = Fiat.parseFiat("USD", "666");
        Assert.assertEquals(6660000, fiat.longValue());
        Assert.assertEquals("6660000", fiat.toString());
    }

    @Test
    public void testOperations() {
        Fiat fiatA = Fiat.parseFiat("USD", "666");
        Fiat fiatB = Fiat.parseFiat("USD", "2");
        Fiat sumResult = fiatA.add(fiatB);
        Assert.assertEquals(6680000, sumResult.getValue());
        Assert.assertEquals("USD", sumResult.getCurrencyCode());
        Fiat subResult = fiatA.subtract(fiatB);
        Assert.assertEquals(6640000, subResult.getValue());
        Assert.assertEquals("USD", subResult.getCurrencyCode());
        Fiat divResult = fiatA.divide(2);
        Assert.assertEquals(3330000, divResult.getValue());
        Assert.assertEquals("USD", divResult.getCurrencyCode());
        long ldivResult = fiatA.divide(fiatB);
        Assert.assertEquals(333, ldivResult);
        Fiat mulResult = fiatA.multiply(2);
        Assert.assertEquals(13320000, mulResult.getValue());
        Fiat[] fiats = fiatA.divideAndRemainder(3);
        Assert.assertEquals(2, fiats.length);
        Fiat fiat1 = fiats[0];
        Assert.assertEquals(2220000, fiat1.getValue());
        Assert.assertEquals("USD", fiat1.getCurrencyCode());
        Fiat fiat2 = fiats[1];
        Assert.assertEquals(0, fiat2.getValue());
        Assert.assertEquals("USD", fiat2.getCurrencyCode());
    }
}

