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


import Coin.CENT;
import Coin.COIN;
import Coin.MICROCOIN;
import Coin.MILLICOIN;
import Coin.ZERO;
import MonetaryFormat.BTC;
import MonetaryFormat.MBTC;
import MonetaryFormat.UBTC;
import java.util.Locale;
import org.bitcoinj.core.Coin;
import org.junit.Assert;
import org.junit.Test;

import static MonetaryFormat.SYMBOL_BTC;


public class MonetaryFormatTest {
    private static final MonetaryFormat NO_CODE = BTC.noCode();

    @Test
    public void testSigns() throws Exception {
        Assert.assertEquals("-1.00", MonetaryFormatTest.NO_CODE.format(COIN.negate()).toString());
        Assert.assertEquals("@1.00", MonetaryFormatTest.NO_CODE.negativeSign('@').format(COIN.negate()).toString());
        Assert.assertEquals("1.00", MonetaryFormatTest.NO_CODE.format(COIN).toString());
        Assert.assertEquals("+1.00", MonetaryFormatTest.NO_CODE.positiveSign('+').format(COIN).toString());
    }

    @Test
    public void testDigits() throws Exception {
        Assert.assertEquals("??.????????", MonetaryFormatTest.NO_CODE.digits('\u0660').format(Coin.valueOf(1234567890L)).toString());
    }

    @Test
    public void testDecimalMark() throws Exception {
        Assert.assertEquals("1.00", MonetaryFormatTest.NO_CODE.format(COIN).toString());
        Assert.assertEquals("1,00", MonetaryFormatTest.NO_CODE.decimalMark(',').format(COIN).toString());
    }

    @Test
    public void testGrouping() throws Exception {
        Assert.assertEquals("0.1", format(Coin.parseCoin("0.1"), 0, 1, 2, 3));
        Assert.assertEquals("0.010", format(Coin.parseCoin("0.01"), 0, 1, 2, 3));
        Assert.assertEquals("0.001", format(Coin.parseCoin("0.001"), 0, 1, 2, 3));
        Assert.assertEquals("0.000100", format(Coin.parseCoin("0.0001"), 0, 1, 2, 3));
        Assert.assertEquals("0.000010", format(Coin.parseCoin("0.00001"), 0, 1, 2, 3));
        Assert.assertEquals("0.000001", format(Coin.parseCoin("0.000001"), 0, 1, 2, 3));
    }

    @Test
    public void btcRounding() throws Exception {
        Assert.assertEquals("0", format(Coin.ZERO, 0, 0));
        Assert.assertEquals("0.00", format(Coin.ZERO, 0, 2));
        Assert.assertEquals("1", format(Coin.COIN, 0, 0));
        Assert.assertEquals("1.0", format(Coin.COIN, 0, 1));
        Assert.assertEquals("1.00", format(Coin.COIN, 0, 2, 2));
        Assert.assertEquals("1.00", format(Coin.COIN, 0, 2, 2, 2));
        Assert.assertEquals("1.00", format(Coin.COIN, 0, 2, 2, 2, 2));
        Assert.assertEquals("1.000", format(Coin.COIN, 0, 3));
        Assert.assertEquals("1.0000", format(Coin.COIN, 0, 4));
        final Coin justNot = Coin.COIN.subtract(Coin.SATOSHI);
        Assert.assertEquals("1", format(justNot, 0, 0));
        Assert.assertEquals("1.0", format(justNot, 0, 1));
        Assert.assertEquals("1.00", format(justNot, 0, 2, 2));
        Assert.assertEquals("1.00", format(justNot, 0, 2, 2, 2));
        Assert.assertEquals("0.99999999", format(justNot, 0, 2, 2, 2, 2));
        Assert.assertEquals("1.000", format(justNot, 0, 3));
        Assert.assertEquals("1.0000", format(justNot, 0, 4));
        final Coin slightlyMore = Coin.COIN.add(Coin.SATOSHI);
        Assert.assertEquals("1", format(slightlyMore, 0, 0));
        Assert.assertEquals("1.0", format(slightlyMore, 0, 1));
        Assert.assertEquals("1.00", format(slightlyMore, 0, 2, 2));
        Assert.assertEquals("1.00", format(slightlyMore, 0, 2, 2, 2));
        Assert.assertEquals("1.00000001", format(slightlyMore, 0, 2, 2, 2, 2));
        Assert.assertEquals("1.000", format(slightlyMore, 0, 3));
        Assert.assertEquals("1.0000", format(slightlyMore, 0, 4));
        final Coin pivot = Coin.COIN.add(Coin.SATOSHI.multiply(5));
        Assert.assertEquals("1.00000005", format(pivot, 0, 8));
        Assert.assertEquals("1.00000005", format(pivot, 0, 7, 1));
        Assert.assertEquals("1.0000001", format(pivot, 0, 7));
        final Coin value = Coin.valueOf(1122334455667788L);
        Assert.assertEquals("11223345", format(value, 0, 0));
        Assert.assertEquals("11223344.6", format(value, 0, 1));
        Assert.assertEquals("11223344.5567", format(value, 0, 2, 2));
        Assert.assertEquals("11223344.556678", format(value, 0, 2, 2, 2));
        Assert.assertEquals("11223344.55667788", format(value, 0, 2, 2, 2, 2));
        Assert.assertEquals("11223344.557", format(value, 0, 3));
        Assert.assertEquals("11223344.5567", format(value, 0, 4));
    }

    @Test
    public void mBtcRounding() throws Exception {
        Assert.assertEquals("0", format(Coin.ZERO, 3, 0));
        Assert.assertEquals("0.00", format(Coin.ZERO, 3, 2));
        Assert.assertEquals("1000", format(Coin.COIN, 3, 0));
        Assert.assertEquals("1000.0", format(Coin.COIN, 3, 1));
        Assert.assertEquals("1000.00", format(Coin.COIN, 3, 2));
        Assert.assertEquals("1000.00", format(Coin.COIN, 3, 2, 2));
        Assert.assertEquals("1000.000", format(Coin.COIN, 3, 3));
        Assert.assertEquals("1000.0000", format(Coin.COIN, 3, 4));
        final Coin justNot = Coin.COIN.subtract(Coin.SATOSHI.multiply(10));
        Assert.assertEquals("1000", format(justNot, 3, 0));
        Assert.assertEquals("1000.0", format(justNot, 3, 1));
        Assert.assertEquals("1000.00", format(justNot, 3, 2));
        Assert.assertEquals("999.9999", format(justNot, 3, 2, 2));
        Assert.assertEquals("1000.000", format(justNot, 3, 3));
        Assert.assertEquals("999.9999", format(justNot, 3, 4));
        final Coin slightlyMore = Coin.COIN.add(Coin.SATOSHI.multiply(10));
        Assert.assertEquals("1000", format(slightlyMore, 3, 0));
        Assert.assertEquals("1000.0", format(slightlyMore, 3, 1));
        Assert.assertEquals("1000.00", format(slightlyMore, 3, 2));
        Assert.assertEquals("1000.000", format(slightlyMore, 3, 3));
        Assert.assertEquals("1000.0001", format(slightlyMore, 3, 2, 2));
        Assert.assertEquals("1000.0001", format(slightlyMore, 3, 4));
        final Coin pivot = Coin.COIN.add(Coin.SATOSHI.multiply(50));
        Assert.assertEquals("1000.0005", format(pivot, 3, 4));
        Assert.assertEquals("1000.0005", format(pivot, 3, 3, 1));
        Assert.assertEquals("1000.001", format(pivot, 3, 3));
        final Coin value = Coin.valueOf(1122334455667788L);
        Assert.assertEquals("11223344557", format(value, 3, 0));
        Assert.assertEquals("11223344556.7", format(value, 3, 1));
        Assert.assertEquals("11223344556.68", format(value, 3, 2));
        Assert.assertEquals("11223344556.6779", format(value, 3, 2, 2));
        Assert.assertEquals("11223344556.678", format(value, 3, 3));
        Assert.assertEquals("11223344556.6779", format(value, 3, 4));
    }

    @Test
    public void uBtcRounding() throws Exception {
        Assert.assertEquals("0", format(Coin.ZERO, 6, 0));
        Assert.assertEquals("0.00", format(Coin.ZERO, 6, 2));
        Assert.assertEquals("1000000", format(Coin.COIN, 6, 0));
        Assert.assertEquals("1000000", format(Coin.COIN, 6, 0, 2));
        Assert.assertEquals("1000000.0", format(Coin.COIN, 6, 1));
        Assert.assertEquals("1000000.00", format(Coin.COIN, 6, 2));
        final Coin justNot = Coin.COIN.subtract(Coin.SATOSHI);
        Assert.assertEquals("1000000", format(justNot, 6, 0));
        Assert.assertEquals("999999.99", format(justNot, 6, 0, 2));
        Assert.assertEquals("1000000.0", format(justNot, 6, 1));
        Assert.assertEquals("999999.99", format(justNot, 6, 2));
        final Coin slightlyMore = Coin.COIN.add(Coin.SATOSHI);
        Assert.assertEquals("1000000", format(slightlyMore, 6, 0));
        Assert.assertEquals("1000000.01", format(slightlyMore, 6, 0, 2));
        Assert.assertEquals("1000000.0", format(slightlyMore, 6, 1));
        Assert.assertEquals("1000000.01", format(slightlyMore, 6, 2));
        final Coin pivot = Coin.COIN.add(Coin.SATOSHI.multiply(5));
        Assert.assertEquals("1000000.05", format(pivot, 6, 2));
        Assert.assertEquals("1000000.05", format(pivot, 6, 0, 2));
        Assert.assertEquals("1000000.1", format(pivot, 6, 1));
        Assert.assertEquals("1000000.1", format(pivot, 6, 0, 1));
        final Coin value = Coin.valueOf(1122334455667788L);
        Assert.assertEquals("11223344556678", format(value, 6, 0));
        Assert.assertEquals("11223344556677.88", format(value, 6, 2));
        Assert.assertEquals("11223344556677.9", format(value, 6, 1));
        Assert.assertEquals("11223344556677.88", format(value, 6, 2));
    }

    @Test
    public void repeatOptionalDecimals() {
        Assert.assertEquals("0.00000001", formatRepeat(Coin.SATOSHI, 2, 4));
        Assert.assertEquals("0.00000010", formatRepeat(Coin.SATOSHI.multiply(10), 2, 4));
        Assert.assertEquals("0.01", formatRepeat(Coin.CENT, 2, 4));
        Assert.assertEquals("0.10", formatRepeat(Coin.CENT.multiply(10), 2, 4));
        Assert.assertEquals("0", formatRepeat(Coin.SATOSHI, 2, 2));
        Assert.assertEquals("0", formatRepeat(Coin.SATOSHI.multiply(10), 2, 2));
        Assert.assertEquals("0.01", formatRepeat(Coin.CENT, 2, 2));
        Assert.assertEquals("0.10", formatRepeat(Coin.CENT.multiply(10), 2, 2));
        Assert.assertEquals("0", formatRepeat(Coin.CENT, 2, 0));
        Assert.assertEquals("0", formatRepeat(Coin.CENT.multiply(10), 2, 0));
    }

    @Test
    public void standardCodes() throws Exception {
        Assert.assertEquals("BTC 0.00", BTC.format(ZERO).toString());
        Assert.assertEquals("mBTC 0.00", MBTC.format(ZERO).toString());
        Assert.assertEquals("?BTC 0", UBTC.format(ZERO).toString());
    }

    @Test
    public void standardSymbol() throws Exception {
        Assert.assertEquals(((SYMBOL_BTC) + " 0.00"), new MonetaryFormat(true).format(ZERO).toString());
    }

    @Test
    public void customCode() throws Exception {
        Assert.assertEquals("dBTC 0", UBTC.code(1, "dBTC").shift(1).format(ZERO).toString());
    }

    /**
     * Test clearing all codes, and then setting codes after clearing.
     */
    @Test
    public void noCode() throws Exception {
        Assert.assertEquals("0", UBTC.noCode().shift(0).format(ZERO).toString());
        // Ensure that inserting a code after codes are wiped, works
        Assert.assertEquals("dBTC 0", UBTC.noCode().code(1, "dBTC").shift(1).format(ZERO).toString());
    }

    @Test
    public void codeOrientation() throws Exception {
        Assert.assertEquals("BTC 0.00", BTC.prefixCode().format(ZERO).toString());
        Assert.assertEquals("0.00 BTC", BTC.postfixCode().format(ZERO).toString());
    }

    @Test
    public void codeSeparator() throws Exception {
        Assert.assertEquals("BTC@0.00", BTC.codeSeparator('@').format(ZERO).toString());
    }

    @Test(expected = NumberFormatException.class)
    public void missingCode() throws Exception {
        UBTC.shift(1).format(ZERO);
    }

    @Test
    public void withLocale() throws Exception {
        final Coin value = Coin.valueOf((-1234567890L));
        Assert.assertEquals("-12.34567890", MonetaryFormatTest.NO_CODE.withLocale(Locale.US).format(value).toString());
        Assert.assertEquals("-12,34567890", MonetaryFormatTest.NO_CODE.withLocale(Locale.GERMANY).format(value).toString());
    }

    @Test
    public void parse() throws Exception {
        Assert.assertEquals(COIN, MonetaryFormatTest.NO_CODE.parse("1"));
        Assert.assertEquals(COIN, MonetaryFormatTest.NO_CODE.parse("1."));
        Assert.assertEquals(COIN, MonetaryFormatTest.NO_CODE.parse("1.0"));
        Assert.assertEquals(COIN, MonetaryFormatTest.NO_CODE.decimalMark(',').parse("1,0"));
        Assert.assertEquals(COIN, MonetaryFormatTest.NO_CODE.parse("01.0000000000"));
        Assert.assertEquals(COIN, MonetaryFormatTest.NO_CODE.positiveSign('+').parse("+1.0"));
        Assert.assertEquals(COIN.negate(), MonetaryFormatTest.NO_CODE.parse("-1"));
        Assert.assertEquals(COIN.negate(), MonetaryFormatTest.NO_CODE.parse("-1.0"));
        Assert.assertEquals(CENT, MonetaryFormatTest.NO_CODE.parse(".01"));
        Assert.assertEquals(MILLICOIN, MBTC.parse("1"));
        Assert.assertEquals(MILLICOIN, MBTC.parse("1.0"));
        Assert.assertEquals(MILLICOIN, MBTC.parse("01.0000000000"));
        Assert.assertEquals(MILLICOIN, MBTC.positiveSign('+').parse("+1.0"));
        Assert.assertEquals(MILLICOIN.negate(), MBTC.parse("-1"));
        Assert.assertEquals(MILLICOIN.negate(), MBTC.parse("-1.0"));
        Assert.assertEquals(MICROCOIN, UBTC.parse("1"));
        Assert.assertEquals(MICROCOIN, UBTC.parse("1.0"));
        Assert.assertEquals(MICROCOIN, UBTC.parse("01.0000000000"));
        Assert.assertEquals(MICROCOIN, UBTC.positiveSign('+').parse("+1.0"));
        Assert.assertEquals(MICROCOIN.negate(), UBTC.parse("-1"));
        Assert.assertEquals(MICROCOIN.negate(), UBTC.parse("-1.0"));
        Assert.assertEquals(CENT, MonetaryFormatTest.NO_CODE.withLocale(new Locale("hi", "IN")).parse(".??"));// Devanagari

    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidEmpty() throws Exception {
        MonetaryFormatTest.NO_CODE.parse("");
    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidWhitespaceBefore() throws Exception {
        MonetaryFormatTest.NO_CODE.parse(" 1");
    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidWhitespaceSign() throws Exception {
        MonetaryFormatTest.NO_CODE.parse("- 1");
    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidWhitespaceAfter() throws Exception {
        MonetaryFormatTest.NO_CODE.parse("1 ");
    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidMultipleDecimalMarks() throws Exception {
        MonetaryFormatTest.NO_CODE.parse("1.0.0");
    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidDecimalMark() throws Exception {
        MonetaryFormatTest.NO_CODE.decimalMark(',').parse("1.0");
    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidPositiveSign() throws Exception {
        MonetaryFormatTest.NO_CODE.positiveSign('@').parse("+1.0");
    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidNegativeSign() throws Exception {
        MonetaryFormatTest.NO_CODE.negativeSign('@').parse("-1.0");
    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidHugeNumber() throws Exception {
        MonetaryFormatTest.NO_CODE.parse("99999999999999999999");
    }

    @Test(expected = NumberFormatException.class)
    public void parseInvalidHugeNegativeNumber() throws Exception {
        MonetaryFormatTest.NO_CODE.parse("-99999999999999999999");
    }

    private static final Fiat ONE_EURO = Fiat.parseFiat("EUR", "1");

    @Test
    public void fiat() throws Exception {
        Assert.assertEquals(MonetaryFormatTest.ONE_EURO, MonetaryFormatTest.NO_CODE.parseFiat("EUR", "1"));
    }
}

