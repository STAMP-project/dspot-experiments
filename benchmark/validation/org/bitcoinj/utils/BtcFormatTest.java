/**
 * Copyright 2014 Adam Mackler
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


import BtcAutoFormat.Style.CODE;
import BtcAutoFormat.Style.SYMBOL;
import BtcFixedFormat.REPEATING_DOUBLETS;
import BtcFixedFormat.REPEATING_PLACES;
import BtcFixedFormat.REPEATING_TRIPLETS;
import Coin.CENT;
import Coin.MICROCOIN;
import Coin.MILLICOIN;
import java.math.BigDecimal;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Set;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.text.NumberFormat.Field.CURRENCY;
import static java.text.NumberFormat.Field.DECIMAL_SEPARATOR;
import static java.text.NumberFormat.Field.FRACTION;
import static java.text.NumberFormat.Field.GROUPING_SEPARATOR;
import static java.text.NumberFormat.Field.INTEGER;


@RunWith(Parameterized.class)
public class BtcFormatTest {
    public BtcFormatTest(Locale defaultLocale) {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void prefixTest() {
        // prefix b/c symbol is prefixed
        BtcFormat usFormat = BtcFormat.getSymbolInstance(Locale.US);
        Assert.assertEquals("?1.00", usFormat.format(COIN));
        Assert.assertEquals("?1.01", usFormat.format(101000000));
        Assert.assertEquals("??0.01", usFormat.format(1000));
        Assert.assertEquals("??1,011.00", usFormat.format(101100000));
        Assert.assertEquals("??1,000.01", usFormat.format(100001000));
        Assert.assertEquals("??1,000,001.00", usFormat.format(100000100));
        Assert.assertEquals("??1,000,000.10", usFormat.format(100000010));
        Assert.assertEquals("??1,000,000.01", usFormat.format(100000001));
        Assert.assertEquals("??1.00", usFormat.format(100));
        Assert.assertEquals("??0.10", usFormat.format(10));
        Assert.assertEquals("??0.01", usFormat.format(1));
    }

    @Test
    public void defaultLocaleTest() {
        Assert.assertEquals(("Default Locale is " + (Locale.getDefault().toString())), BtcFormat.getInstance().pattern(), BtcFormat.getInstance(Locale.getDefault()).pattern());
        Assert.assertEquals(("Default Locale is " + (Locale.getDefault().toString())), BtcFormat.getCodeInstance().pattern(), BtcFormat.getCodeInstance(Locale.getDefault()).pattern());
    }

    @Test
    public void symbolCollisionTest() {
        Locale[] locales = BtcFormat.getAvailableLocales();
        for (int i = 0; i < (locales.length); ++i) {
            String cs = ((DecimalFormat) (NumberFormat.getCurrencyInstance(locales[i]))).getDecimalFormatSymbols().getCurrencySymbol();
            if (cs.contains("?")) {
                BtcFormat bf = BtcFormat.getSymbolInstance(locales[i]);
                String coin = bf.format(COIN);
                Assert.assertTrue(coin.contains("?"));
                Assert.assertFalse(coin.contains("?"));
                String milli = bf.format(valueOf(10000));
                Assert.assertTrue(milli.contains("??"));
                Assert.assertFalse(milli.contains("?"));
                String micro = bf.format(valueOf(100));
                Assert.assertTrue(micro.contains("??"));
                Assert.assertFalse(micro.contains("?"));
                BtcFormat ff = BtcFormat.builder().scale(0).locale(locales[i]).pattern("?#.#").build();
                Assert.assertEquals("?", symbol());
                Assert.assertEquals("?", ff.coinSymbol());
                coin = ff.format(COIN);
                Assert.assertTrue(coin.contains("?"));
                Assert.assertFalse(coin.contains("?"));
                BtcFormat mlff = BtcFormat.builder().scale(3).locale(locales[i]).pattern("?#.#").build();
                Assert.assertEquals("??", symbol());
                Assert.assertEquals("?", mlff.coinSymbol());
                milli = mlff.format(valueOf(10000));
                Assert.assertTrue(milli.contains("??"));
                Assert.assertFalse(milli.contains("?"));
                BtcFormat mcff = BtcFormat.builder().scale(6).locale(locales[i]).pattern("?#.#").build();
                Assert.assertEquals("??", symbol());
                Assert.assertEquals("?", mcff.coinSymbol());
                micro = mcff.format(valueOf(100));
                Assert.assertTrue(micro.contains("??"));
                Assert.assertFalse(micro.contains("?"));
            }
            if (cs.contains("?")) {
                // NB: We don't know of any such existing locale, but check anyway.
                BtcFormat bf = BtcFormat.getInstance(locales[i]);
                String coin = bf.format(COIN);
                Assert.assertTrue(coin.contains("?"));
                Assert.assertFalse(coin.contains("?"));
                String milli = bf.format(valueOf(10000));
                Assert.assertTrue(milli.contains("??"));
                Assert.assertFalse(milli.contains("?"));
                String micro = bf.format(valueOf(100));
                Assert.assertTrue(micro.contains("??"));
                Assert.assertFalse(micro.contains("?"));
            }
        }
    }

    @Test
    public void columnAlignmentTest() {
        BtcFormat germany = BtcFormat.getCoinInstance(2, REPEATING_PLACES);
        char separator = germany.symbols().getDecimalSeparator();
        Coin[] rows = new Coin[]{ NetworkParameters.MAX_MONEY, NetworkParameters.MAX_MONEY.subtract(SATOSHI), Coin.parseCoin("1234"), COIN, COIN.add(SATOSHI), COIN.subtract(SATOSHI), COIN.divide(1000).add(SATOSHI), COIN.divide(1000), COIN.divide(1000).subtract(SATOSHI), valueOf(100), valueOf(1000), valueOf(10000), SATOSHI };
        FieldPosition fp = new FieldPosition(DECIMAL_SEPARATOR);
        String[] output = new String[rows.length];
        int[] indexes = new int[rows.length];
        int maxIndex = 0;
        for (int i = 0; i < (rows.length); i++) {
            output[i] = germany.format(rows[i], new StringBuffer(), fp).toString();
            indexes[i] = fp.getBeginIndex();
            if ((indexes[i]) > maxIndex)
                maxIndex = indexes[i];

        }
        for (int i = 0; i < (output.length); i++) {
            // uncomment to watch printout
            // System.out.println(repeat(" ", (maxIndex - indexes[i])) + output[i]);
            Assert.assertEquals(output[i].indexOf(separator), indexes[i]);
        }
    }

    @Test
    public void repeatingPlaceTest() {
        BtcFormat mega = BtcFormat.getInstance((-6), Locale.US);
        Coin value = NetworkParameters.MAX_MONEY.subtract(SATOSHI);
        Assert.assertEquals("20.99999999999999", mega.format(value, 0, REPEATING_PLACES));
        Assert.assertEquals("20.99999999999999", mega.format(value, 0, REPEATING_PLACES));
        Assert.assertEquals("20.99999999999999", mega.format(value, 1, REPEATING_PLACES));
        Assert.assertEquals("20.99999999999999", mega.format(value, 2, REPEATING_PLACES));
        Assert.assertEquals("20.99999999999999", mega.format(value, 3, REPEATING_PLACES));
        Assert.assertEquals("20.99999999999999", mega.format(value, 0, REPEATING_DOUBLETS));
        Assert.assertEquals("20.99999999999999", mega.format(value, 1, REPEATING_DOUBLETS));
        Assert.assertEquals("20.99999999999999", mega.format(value, 2, REPEATING_DOUBLETS));
        Assert.assertEquals("20.99999999999999", mega.format(value, 3, REPEATING_DOUBLETS));
        Assert.assertEquals("20.99999999999999", mega.format(value, 0, REPEATING_TRIPLETS));
        Assert.assertEquals("20.99999999999999", mega.format(value, 1, REPEATING_TRIPLETS));
        Assert.assertEquals("20.99999999999999", mega.format(value, 2, REPEATING_TRIPLETS));
        Assert.assertEquals("20.99999999999999", mega.format(value, 3, REPEATING_TRIPLETS));
        Assert.assertEquals("1.00000005", BtcFormat.getCoinInstance(Locale.US).format(COIN.add(Coin.valueOf(5)), 0, REPEATING_PLACES));
    }

    @Test
    public void characterIteratorTest() {
        BtcFormat usFormat = BtcFormat.getInstance(Locale.US);
        AttributedCharacterIterator i = usFormat.formatToCharacterIterator(parseCoin("1234.5"));
        Set<AttributedCharacterIterator.Attribute> a = i.getAllAttributeKeys();
        Assert.assertTrue("Missing currency attribute", a.contains(CURRENCY));
        Assert.assertTrue("Missing integer attribute", a.contains(INTEGER));
        Assert.assertTrue("Missing fraction attribute", a.contains(FRACTION));
        Assert.assertTrue("Missing decimal separator attribute", a.contains(DECIMAL_SEPARATOR));
        Assert.assertTrue("Missing grouping separator attribute", a.contains(GROUPING_SEPARATOR));
        Assert.assertTrue("Missing currency attribute", a.contains(CURRENCY));
        char c;
        i = BtcFormat.getCodeInstance(Locale.US).formatToCharacterIterator(new BigDecimal("0.19246362747414458"));
        // formatted as "?BTC 192,463.63"
        Assert.assertEquals(0, i.getBeginIndex());
        Assert.assertEquals(15, i.getEndIndex());
        int n = 0;
        for (c = i.first(); (i.getAttribute(CURRENCY)) != null; c = i.next()) {
            n++;
        }
        Assert.assertEquals(4, n);
        n = 0;
        for (i.next(); ((i.getAttribute(INTEGER)) != null) && ((i.getAttribute(GROUPING_SEPARATOR)) != (GROUPING_SEPARATOR)); c = i.next()) {
            n++;
        }
        Assert.assertEquals(3, n);
        Assert.assertEquals(INTEGER, i.getAttribute(INTEGER));
        n = 0;
        for (c = i.next(); (i.getAttribute(INTEGER)) != null; c = i.next()) {
            n++;
        }
        Assert.assertEquals(3, n);
        Assert.assertEquals(DECIMAL_SEPARATOR, i.getAttribute(DECIMAL_SEPARATOR));
        n = 0;
        for (c = i.next(); c != (CharacterIterator.DONE); c = i.next()) {
            n++;
            Assert.assertNotNull(i.getAttribute(FRACTION));
        }
        Assert.assertEquals(2, n);
        // immutability check
        BtcFormat fa = BtcFormat.getSymbolInstance(Locale.US);
        BtcFormat fb = BtcFormat.getSymbolInstance(Locale.US);
        Assert.assertEquals(fa, fb);
        Assert.assertEquals(fa.hashCode(), fb.hashCode());
        fa.formatToCharacterIterator(COIN.multiply(1000000));
        Assert.assertEquals(fa, fb);
        Assert.assertEquals(fa.hashCode(), fb.hashCode());
        fb.formatToCharacterIterator(COIN.divide(1000000));
        Assert.assertEquals(fa, fb);
        Assert.assertEquals(fa.hashCode(), fb.hashCode());
    }

    @Test
    public void parseMetricTest() throws ParseException {
        BtcFormat cp = BtcFormat.getCodeInstance(Locale.US);
        BtcFormat sp = BtcFormat.getSymbolInstance(Locale.US);
        // coin
        Assert.assertEquals(parseCoin("1"), cp.parseObject("BTC 1.00"));
        Assert.assertEquals(parseCoin("1"), sp.parseObject("BTC1.00"));
        Assert.assertEquals(parseCoin("1"), cp.parseObject("? 1.00"));
        Assert.assertEquals(parseCoin("1"), sp.parseObject("?1.00"));
        Assert.assertEquals(parseCoin("1"), cp.parseObject("B? 1.00"));
        Assert.assertEquals(parseCoin("1"), sp.parseObject("B?1.00"));
        Assert.assertEquals(parseCoin("1"), cp.parseObject("? 1.00"));
        Assert.assertEquals(parseCoin("1"), sp.parseObject("?1.00"));
        // milli
        Assert.assertEquals(parseCoin("0.001"), cp.parseObject("mBTC 1.00"));
        Assert.assertEquals(parseCoin("0.001"), sp.parseObject("mBTC1.00"));
        Assert.assertEquals(parseCoin("0.001"), cp.parseObject("m? 1.00"));
        Assert.assertEquals(parseCoin("0.001"), sp.parseObject("m?1.00"));
        Assert.assertEquals(parseCoin("0.001"), cp.parseObject("mB? 1.00"));
        Assert.assertEquals(parseCoin("0.001"), sp.parseObject("mB?1.00"));
        Assert.assertEquals(parseCoin("0.001"), cp.parseObject("m? 1.00"));
        Assert.assertEquals(parseCoin("0.001"), sp.parseObject("m?1.00"));
        Assert.assertEquals(parseCoin("0.001"), cp.parseObject("?BTC 1.00"));
        Assert.assertEquals(parseCoin("0.001"), sp.parseObject("?BTC1.00"));
        Assert.assertEquals(parseCoin("0.001"), cp.parseObject("?? 1.00"));
        Assert.assertEquals(parseCoin("0.001"), sp.parseObject("??1.00"));
        Assert.assertEquals(parseCoin("0.001"), cp.parseObject("?B? 1.00"));
        Assert.assertEquals(parseCoin("0.001"), sp.parseObject("?B?1.00"));
        Assert.assertEquals(parseCoin("0.001"), cp.parseObject("?? 1.00"));
        Assert.assertEquals(parseCoin("0.001"), sp.parseObject("??1.00"));
        // micro
        Assert.assertEquals(parseCoin("0.000001"), cp.parseObject("uBTC 1.00"));
        Assert.assertEquals(parseCoin("0.000001"), sp.parseObject("uBTC1.00"));
        Assert.assertEquals(parseCoin("0.000001"), cp.parseObject("u? 1.00"));
        Assert.assertEquals(parseCoin("0.000001"), sp.parseObject("u?1.00"));
        Assert.assertEquals(parseCoin("0.000001"), cp.parseObject("uB? 1.00"));
        Assert.assertEquals(parseCoin("0.000001"), sp.parseObject("uB?1.00"));
        Assert.assertEquals(parseCoin("0.000001"), cp.parseObject("u? 1.00"));
        Assert.assertEquals(parseCoin("0.000001"), sp.parseObject("u?1.00"));
        Assert.assertEquals(parseCoin("0.000001"), cp.parseObject("?BTC 1.00"));
        Assert.assertEquals(parseCoin("0.000001"), sp.parseObject("?BTC1.00"));
        Assert.assertEquals(parseCoin("0.000001"), cp.parseObject("?? 1.00"));
        Assert.assertEquals(parseCoin("0.000001"), sp.parseObject("??1.00"));
        Assert.assertEquals(parseCoin("0.000001"), cp.parseObject("?B? 1.00"));
        Assert.assertEquals(parseCoin("0.000001"), sp.parseObject("?B?1.00"));
        Assert.assertEquals(parseCoin("0.000001"), cp.parseObject("?? 1.00"));
        Assert.assertEquals(parseCoin("0.000001"), sp.parseObject("??1.00"));
        // satoshi
        Assert.assertEquals(parseCoin("0.00000001"), cp.parseObject("uBTC 0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), sp.parseObject("uBTC0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), cp.parseObject("u? 0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), sp.parseObject("u?0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), cp.parseObject("uB? 0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), sp.parseObject("uB?0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), cp.parseObject("u? 0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), sp.parseObject("u?0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), cp.parseObject("?BTC 0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), sp.parseObject("?BTC0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), cp.parseObject("?? 0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), sp.parseObject("??0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), cp.parseObject("?B? 0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), sp.parseObject("?B?0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), cp.parseObject("?? 0.01"));
        Assert.assertEquals(parseCoin("0.00000001"), sp.parseObject("??0.01"));
        // cents
        Assert.assertEquals(parseCoin("0.01234567"), cp.parseObject("cBTC 1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), sp.parseObject("cBTC1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), cp.parseObject("c? 1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), sp.parseObject("c?1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), cp.parseObject("cB? 1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), sp.parseObject("cB?1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), cp.parseObject("c? 1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), sp.parseObject("c?1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), cp.parseObject("?BTC 1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), sp.parseObject("?BTC1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), cp.parseObject("?? 1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), sp.parseObject("??1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), cp.parseObject("?B? 1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), sp.parseObject("?B?1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), cp.parseObject("?? 1.234567"));
        Assert.assertEquals(parseCoin("0.01234567"), sp.parseObject("??1.234567"));
        // dekacoins
        Assert.assertEquals(parseCoin("12.34567"), cp.parseObject("daBTC 1.234567"));
        Assert.assertEquals(parseCoin("12.34567"), sp.parseObject("daBTC1.234567"));
        Assert.assertEquals(parseCoin("12.34567"), cp.parseObject("da? 1.234567"));
        Assert.assertEquals(parseCoin("12.34567"), sp.parseObject("da?1.234567"));
        Assert.assertEquals(parseCoin("12.34567"), cp.parseObject("daB? 1.234567"));
        Assert.assertEquals(parseCoin("12.34567"), sp.parseObject("daB?1.234567"));
        Assert.assertEquals(parseCoin("12.34567"), cp.parseObject("da? 1.234567"));
        Assert.assertEquals(parseCoin("12.34567"), sp.parseObject("da?1.234567"));
        // hectocoins
        Assert.assertEquals(parseCoin("123.4567"), cp.parseObject("hBTC 1.234567"));
        Assert.assertEquals(parseCoin("123.4567"), sp.parseObject("hBTC1.234567"));
        Assert.assertEquals(parseCoin("123.4567"), cp.parseObject("h? 1.234567"));
        Assert.assertEquals(parseCoin("123.4567"), sp.parseObject("h?1.234567"));
        Assert.assertEquals(parseCoin("123.4567"), cp.parseObject("hB? 1.234567"));
        Assert.assertEquals(parseCoin("123.4567"), sp.parseObject("hB?1.234567"));
        Assert.assertEquals(parseCoin("123.4567"), cp.parseObject("h? 1.234567"));
        Assert.assertEquals(parseCoin("123.4567"), sp.parseObject("h?1.234567"));
        // kilocoins
        Assert.assertEquals(parseCoin("1234.567"), cp.parseObject("kBTC 1.234567"));
        Assert.assertEquals(parseCoin("1234.567"), sp.parseObject("kBTC1.234567"));
        Assert.assertEquals(parseCoin("1234.567"), cp.parseObject("k? 1.234567"));
        Assert.assertEquals(parseCoin("1234.567"), sp.parseObject("k?1.234567"));
        Assert.assertEquals(parseCoin("1234.567"), cp.parseObject("kB? 1.234567"));
        Assert.assertEquals(parseCoin("1234.567"), sp.parseObject("kB?1.234567"));
        Assert.assertEquals(parseCoin("1234.567"), cp.parseObject("k? 1.234567"));
        Assert.assertEquals(parseCoin("1234.567"), sp.parseObject("k?1.234567"));
        // megacoins
        Assert.assertEquals(parseCoin("1234567"), cp.parseObject("MBTC 1.234567"));
        Assert.assertEquals(parseCoin("1234567"), sp.parseObject("MBTC1.234567"));
        Assert.assertEquals(parseCoin("1234567"), cp.parseObject("M? 1.234567"));
        Assert.assertEquals(parseCoin("1234567"), sp.parseObject("M?1.234567"));
        Assert.assertEquals(parseCoin("1234567"), cp.parseObject("MB? 1.234567"));
        Assert.assertEquals(parseCoin("1234567"), sp.parseObject("MB?1.234567"));
        Assert.assertEquals(parseCoin("1234567"), cp.parseObject("M? 1.234567"));
        Assert.assertEquals(parseCoin("1234567"), sp.parseObject("M?1.234567"));
    }

    @Test
    public void parsePositionTest() {
        BtcFormat usCoded = BtcFormat.getCodeInstance(Locale.US);
        // Test the field constants
        FieldPosition intField = new FieldPosition(INTEGER);
        Assert.assertEquals("987,654,321", usCoded.format(valueOf(98765432123L), new StringBuffer(), intField).substring(intField.getBeginIndex(), intField.getEndIndex()));
        FieldPosition fracField = new FieldPosition(FRACTION);
        Assert.assertEquals("23", usCoded.format(valueOf(98765432123L), new StringBuffer(), fracField).substring(fracField.getBeginIndex(), fracField.getEndIndex()));
        // for currency we use a locale that puts the units at the end
        BtcFormat de = BtcFormat.getSymbolInstance(Locale.GERMANY);
        BtcFormat deCoded = BtcFormat.getCodeInstance(Locale.GERMANY);
        FieldPosition currField = new FieldPosition(CURRENCY);
        Assert.assertEquals("??", de.format(valueOf(98765432123L), new StringBuffer(), currField).substring(currField.getBeginIndex(), currField.getEndIndex()));
        Assert.assertEquals("?BTC", deCoded.format(valueOf(98765432123L), new StringBuffer(), currField).substring(currField.getBeginIndex(), currField.getEndIndex()));
        Assert.assertEquals("??", de.format(valueOf(98765432000L), new StringBuffer(), currField).substring(currField.getBeginIndex(), currField.getEndIndex()));
        Assert.assertEquals("mBTC", deCoded.format(valueOf(98765432000L), new StringBuffer(), currField).substring(currField.getBeginIndex(), currField.getEndIndex()));
        Assert.assertEquals("?", de.format(valueOf(98765000000L), new StringBuffer(), currField).substring(currField.getBeginIndex(), currField.getEndIndex()));
        Assert.assertEquals("BTC", deCoded.format(valueOf(98765000000L), new StringBuffer(), currField).substring(currField.getBeginIndex(), currField.getEndIndex()));
    }

    @Test
    public void coinScaleTest() throws Exception {
        BtcFormat coinFormat = BtcFormat.getCoinInstance(Locale.US);
        Assert.assertEquals("1.00", coinFormat.format(Coin.COIN));
        Assert.assertEquals("-1.00", coinFormat.format(Coin.COIN.negate()));
        Assert.assertEquals(Coin.parseCoin("1"), coinFormat.parseObject("1.00"));
        Assert.assertEquals(valueOf(1000000), coinFormat.parseObject("0.01"));
        Assert.assertEquals(Coin.parseCoin("1000"), coinFormat.parseObject("1,000.00"));
        Assert.assertEquals(Coin.parseCoin("1000"), coinFormat.parseObject("1000"));
    }

    @Test
    public void millicoinScaleTest() throws Exception {
        BtcFormat coinFormat = BtcFormat.getMilliInstance(Locale.US);
        Assert.assertEquals("1,000.00", coinFormat.format(Coin.COIN));
        Assert.assertEquals("-1,000.00", coinFormat.format(Coin.COIN.negate()));
        Assert.assertEquals(Coin.parseCoin("0.001"), coinFormat.parseObject("1.00"));
        Assert.assertEquals(valueOf(1000), coinFormat.parseObject("0.01"));
        Assert.assertEquals(Coin.parseCoin("1"), coinFormat.parseObject("1,000.00"));
        Assert.assertEquals(Coin.parseCoin("1"), coinFormat.parseObject("1000"));
    }

    @Test
    public void microcoinScaleTest() throws Exception {
        BtcFormat coinFormat = BtcFormat.getMicroInstance(Locale.US);
        Assert.assertEquals("1,000,000.00", coinFormat.format(Coin.COIN));
        Assert.assertEquals("-1,000,000.00", coinFormat.format(Coin.COIN.negate()));
        Assert.assertEquals("1,000,000.10", coinFormat.format(Coin.COIN.add(valueOf(10))));
        Assert.assertEquals(Coin.parseCoin("0.000001"), coinFormat.parseObject("1.00"));
        Assert.assertEquals(valueOf(1), coinFormat.parseObject("0.01"));
        Assert.assertEquals(Coin.parseCoin("0.001"), coinFormat.parseObject("1,000.00"));
        Assert.assertEquals(Coin.parseCoin("0.001"), coinFormat.parseObject("1000"));
    }

    @Test
    public void testGrouping() throws Exception {
        BtcFormat usCoin = BtcFormat.getInstance(0, Locale.US, 1, 2, 3);
        Assert.assertEquals("0.1", usCoin.format(Coin.parseCoin("0.1")));
        Assert.assertEquals("0.010", usCoin.format(Coin.parseCoin("0.01")));
        Assert.assertEquals("0.001", usCoin.format(Coin.parseCoin("0.001")));
        Assert.assertEquals("0.000100", usCoin.format(Coin.parseCoin("0.0001")));
        Assert.assertEquals("0.000010", usCoin.format(Coin.parseCoin("0.00001")));
        Assert.assertEquals("0.000001", usCoin.format(Coin.parseCoin("0.000001")));
        // no more than two fractional decimal places for the default coin-denomination
        Assert.assertEquals("0.01", BtcFormat.getCoinInstance(Locale.US).format(Coin.parseCoin("0.005")));
        BtcFormat usMilli = BtcFormat.getInstance(3, Locale.US, 1, 2, 3);
        Assert.assertEquals("0.1", usMilli.format(Coin.parseCoin("0.0001")));
        Assert.assertEquals("0.010", usMilli.format(Coin.parseCoin("0.00001")));
        Assert.assertEquals("0.001", usMilli.format(Coin.parseCoin("0.000001")));
        // even though last group is 3, that would result in fractional satoshis, which we don't do
        Assert.assertEquals("0.00010", usMilli.format(Coin.valueOf(10)));
        Assert.assertEquals("0.00001", usMilli.format(Coin.valueOf(1)));
        BtcFormat usMicro = BtcFormat.getInstance(6, Locale.US, 1, 2, 3);
        Assert.assertEquals("0.1", usMicro.format(Coin.valueOf(10)));
        // even though second group is 2, that would result in fractional satoshis, which we don't do
        Assert.assertEquals("0.01", usMicro.format(Coin.valueOf(1)));
    }

    /* These just make sure factory methods don't raise exceptions.
    Other tests inspect their return values.
     */
    @Test
    public void factoryTest() {
        BtcFormat coded = BtcFormat.getInstance(0, 1, 2, 3);
        BtcFormat.getInstance(CODE);
        BtcAutoFormat symbolic = ((BtcAutoFormat) (BtcFormat.getInstance(SYMBOL)));
        Assert.assertEquals(2, symbolic.fractionPlaces());
        BtcFormat.getInstance(CODE, 3);
        Assert.assertEquals(3, fractionPlaces());
        BtcFormat.getInstance(SYMBOL, Locale.US, 3);
        BtcFormat.getInstance(CODE, Locale.US);
        BtcFormat.getInstance(SYMBOL, Locale.US);
        BtcFormat.getCoinInstance(2, REPEATING_PLACES);
        BtcFormat.getMilliInstance(1, 2, 3);
        BtcFormat.getInstance(2);
        BtcFormat.getInstance(2, Locale.US);
        BtcFormat.getCodeInstance(3);
        BtcFormat.getSymbolInstance(3);
        BtcFormat.getCodeInstance(Locale.US, 3);
        BtcFormat.getSymbolInstance(Locale.US, 3);
        try {
            BtcFormat.getInstance(((SMALLEST_UNIT_EXPONENT) + 1));
            Assert.fail("should not have constructed an instance with denomination less than satoshi");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void factoryArgumentsTest() {
        Locale locale;
        if (Locale.getDefault().equals(Locale.GERMANY))
            locale = Locale.FRANCE;
        else
            locale = Locale.GERMANY;

        Assert.assertEquals(BtcFormat.getInstance(), BtcFormat.getCodeInstance());
        Assert.assertEquals(BtcFormat.getInstance(locale), BtcFormat.getCodeInstance(locale));
        Assert.assertEquals(BtcFormat.getInstance(CODE), BtcFormat.getCodeInstance());
        Assert.assertEquals(BtcFormat.getInstance(SYMBOL), BtcFormat.getSymbolInstance());
        Assert.assertEquals(BtcFormat.getInstance(CODE, 3), BtcFormat.getCodeInstance(3));
        Assert.assertEquals(BtcFormat.getInstance(SYMBOL, 3), BtcFormat.getSymbolInstance(3));
        Assert.assertEquals(BtcFormat.getInstance(CODE, locale), BtcFormat.getCodeInstance(locale));
        Assert.assertEquals(BtcFormat.getInstance(SYMBOL, locale), BtcFormat.getSymbolInstance(locale));
        Assert.assertEquals(BtcFormat.getInstance(CODE, locale, 3), BtcFormat.getCodeInstance(locale, 3));
        Assert.assertEquals(BtcFormat.getInstance(SYMBOL, locale, 3), BtcFormat.getSymbolInstance(locale, 3));
        Assert.assertEquals(BtcFormat.getCoinInstance(), BtcFormat.getInstance(0));
        Assert.assertEquals(BtcFormat.getMilliInstance(), BtcFormat.getInstance(3));
        Assert.assertEquals(BtcFormat.getMicroInstance(), BtcFormat.getInstance(6));
        Assert.assertEquals(BtcFormat.getCoinInstance(3), BtcFormat.getInstance(0, 3));
        Assert.assertEquals(BtcFormat.getMilliInstance(3), BtcFormat.getInstance(3, 3));
        Assert.assertEquals(BtcFormat.getMicroInstance(3), BtcFormat.getInstance(6, 3));
        Assert.assertEquals(BtcFormat.getCoinInstance(3, 4, 5), BtcFormat.getInstance(0, 3, 4, 5));
        Assert.assertEquals(BtcFormat.getMilliInstance(3, 4, 5), BtcFormat.getInstance(3, 3, 4, 5));
        Assert.assertEquals(BtcFormat.getMicroInstance(3, 4, 5), BtcFormat.getInstance(6, 3, 4, 5));
        Assert.assertEquals(BtcFormat.getCoinInstance(locale), BtcFormat.getInstance(0, locale));
        Assert.assertEquals(BtcFormat.getMilliInstance(locale), BtcFormat.getInstance(3, locale));
        Assert.assertEquals(BtcFormat.getMicroInstance(locale), BtcFormat.getInstance(6, locale));
        Assert.assertEquals(BtcFormat.getCoinInstance(locale, 4, 5), BtcFormat.getInstance(0, locale, 4, 5));
        Assert.assertEquals(BtcFormat.getMilliInstance(locale, 4, 5), BtcFormat.getInstance(3, locale, 4, 5));
        Assert.assertEquals(BtcFormat.getMicroInstance(locale, 4, 5), BtcFormat.getInstance(6, locale, 4, 5));
    }

    @Test
    public void autoDecimalTest() {
        BtcFormat codedZero = BtcFormat.getCodeInstance(Locale.US, 0);
        BtcFormat symbolZero = BtcFormat.getSymbolInstance(Locale.US, 0);
        Assert.assertEquals("?1", symbolZero.format(COIN));
        Assert.assertEquals("BTC 1", codedZero.format(COIN));
        Assert.assertEquals("??1,000,000", symbolZero.format(COIN.subtract(SATOSHI)));
        Assert.assertEquals("?BTC 1,000,000", codedZero.format(COIN.subtract(SATOSHI)));
        Assert.assertEquals("??1,000,000", symbolZero.format(COIN.subtract(Coin.valueOf(50))));
        Assert.assertEquals("?BTC 1,000,000", codedZero.format(COIN.subtract(Coin.valueOf(50))));
        Assert.assertEquals("??999,999", symbolZero.format(COIN.subtract(Coin.valueOf(51))));
        Assert.assertEquals("?BTC 999,999", codedZero.format(COIN.subtract(Coin.valueOf(51))));
        Assert.assertEquals("?1,000", symbolZero.format(COIN.multiply(1000)));
        Assert.assertEquals("BTC 1,000", codedZero.format(COIN.multiply(1000)));
        Assert.assertEquals("??1", symbolZero.format(Coin.valueOf(100)));
        Assert.assertEquals("?BTC 1", codedZero.format(Coin.valueOf(100)));
        Assert.assertEquals("??1", symbolZero.format(Coin.valueOf(50)));
        Assert.assertEquals("?BTC 1", codedZero.format(Coin.valueOf(50)));
        Assert.assertEquals("??0", symbolZero.format(Coin.valueOf(49)));
        Assert.assertEquals("?BTC 0", codedZero.format(Coin.valueOf(49)));
        Assert.assertEquals("??0", symbolZero.format(Coin.valueOf(1)));
        Assert.assertEquals("?BTC 0", codedZero.format(Coin.valueOf(1)));
        Assert.assertEquals("??500,000", symbolZero.format(Coin.valueOf(49999999)));
        Assert.assertEquals("?BTC 500,000", codedZero.format(Coin.valueOf(49999999)));
        Assert.assertEquals("??499,500", symbolZero.format(Coin.valueOf(49950000)));
        Assert.assertEquals("?BTC 499,500", codedZero.format(Coin.valueOf(49950000)));
        Assert.assertEquals("??499,500", symbolZero.format(Coin.valueOf(49949999)));
        Assert.assertEquals("?BTC 499,500", codedZero.format(Coin.valueOf(49949999)));
        Assert.assertEquals("??500,490", symbolZero.format(Coin.valueOf(50049000)));
        Assert.assertEquals("?BTC 500,490", codedZero.format(Coin.valueOf(50049000)));
        Assert.assertEquals("??500,490", symbolZero.format(Coin.valueOf(50049001)));
        Assert.assertEquals("?BTC 500,490", codedZero.format(Coin.valueOf(50049001)));
        Assert.assertEquals("??500,000", symbolZero.format(Coin.valueOf(49999950)));
        Assert.assertEquals("?BTC 500,000", codedZero.format(Coin.valueOf(49999950)));
        Assert.assertEquals("??499,999", symbolZero.format(Coin.valueOf(49999949)));
        Assert.assertEquals("?BTC 499,999", codedZero.format(Coin.valueOf(49999949)));
        Assert.assertEquals("??500,000", symbolZero.format(Coin.valueOf(50000049)));
        Assert.assertEquals("?BTC 500,000", codedZero.format(Coin.valueOf(50000049)));
        Assert.assertEquals("??500,001", symbolZero.format(Coin.valueOf(50000050)));
        Assert.assertEquals("?BTC 500,001", codedZero.format(Coin.valueOf(50000050)));
        BtcFormat codedTwo = BtcFormat.getCodeInstance(Locale.US, 2);
        BtcFormat symbolTwo = BtcFormat.getSymbolInstance(Locale.US, 2);
        Assert.assertEquals("?1.00", symbolTwo.format(COIN));
        Assert.assertEquals("BTC 1.00", codedTwo.format(COIN));
        Assert.assertEquals("??999,999.99", symbolTwo.format(COIN.subtract(SATOSHI)));
        Assert.assertEquals("?BTC 999,999.99", codedTwo.format(COIN.subtract(SATOSHI)));
        Assert.assertEquals("?1,000.00", symbolTwo.format(COIN.multiply(1000)));
        Assert.assertEquals("BTC 1,000.00", codedTwo.format(COIN.multiply(1000)));
        Assert.assertEquals("??1.00", symbolTwo.format(Coin.valueOf(100)));
        Assert.assertEquals("?BTC 1.00", codedTwo.format(Coin.valueOf(100)));
        Assert.assertEquals("??0.50", symbolTwo.format(Coin.valueOf(50)));
        Assert.assertEquals("?BTC 0.50", codedTwo.format(Coin.valueOf(50)));
        Assert.assertEquals("??0.49", symbolTwo.format(Coin.valueOf(49)));
        Assert.assertEquals("?BTC 0.49", codedTwo.format(Coin.valueOf(49)));
        Assert.assertEquals("??0.01", symbolTwo.format(Coin.valueOf(1)));
        Assert.assertEquals("?BTC 0.01", codedTwo.format(Coin.valueOf(1)));
        BtcFormat codedThree = BtcFormat.getCodeInstance(Locale.US, 3);
        BtcFormat symbolThree = BtcFormat.getSymbolInstance(Locale.US, 3);
        Assert.assertEquals("?1.000", symbolThree.format(COIN));
        Assert.assertEquals("BTC 1.000", codedThree.format(COIN));
        Assert.assertEquals("??999,999.99", symbolThree.format(COIN.subtract(SATOSHI)));
        Assert.assertEquals("?BTC 999,999.99", codedThree.format(COIN.subtract(SATOSHI)));
        Assert.assertEquals("?1,000.000", symbolThree.format(COIN.multiply(1000)));
        Assert.assertEquals("BTC 1,000.000", codedThree.format(COIN.multiply(1000)));
        Assert.assertEquals("??0.001", symbolThree.format(Coin.valueOf(100)));
        Assert.assertEquals("mBTC 0.001", codedThree.format(Coin.valueOf(100)));
        Assert.assertEquals("??0.50", symbolThree.format(Coin.valueOf(50)));
        Assert.assertEquals("?BTC 0.50", codedThree.format(Coin.valueOf(50)));
        Assert.assertEquals("??0.49", symbolThree.format(Coin.valueOf(49)));
        Assert.assertEquals("?BTC 0.49", codedThree.format(Coin.valueOf(49)));
        Assert.assertEquals("??0.01", symbolThree.format(Coin.valueOf(1)));
        Assert.assertEquals("?BTC 0.01", codedThree.format(Coin.valueOf(1)));
    }

    @Test
    public void symbolsCodesTest() {
        BtcFixedFormat coin = ((BtcFixedFormat) (BtcFormat.getCoinInstance(Locale.US)));
        Assert.assertEquals("BTC", coin.code());
        Assert.assertEquals("?", coin.symbol());
        BtcFixedFormat cent = ((BtcFixedFormat) (BtcFormat.getInstance(2, Locale.US)));
        Assert.assertEquals("cBTC", cent.code());
        Assert.assertEquals("??", cent.symbol());
        BtcFixedFormat milli = ((BtcFixedFormat) (BtcFormat.getInstance(3, Locale.US)));
        Assert.assertEquals("mBTC", milli.code());
        Assert.assertEquals("??", milli.symbol());
        BtcFixedFormat micro = ((BtcFixedFormat) (BtcFormat.getInstance(6, Locale.US)));
        Assert.assertEquals("?BTC", micro.code());
        Assert.assertEquals("??", micro.symbol());
        BtcFixedFormat deka = ((BtcFixedFormat) (BtcFormat.getInstance((-1), Locale.US)));
        Assert.assertEquals("daBTC", deka.code());
        Assert.assertEquals("da?", deka.symbol());
        BtcFixedFormat hecto = ((BtcFixedFormat) (BtcFormat.getInstance((-2), Locale.US)));
        Assert.assertEquals("hBTC", hecto.code());
        Assert.assertEquals("h?", hecto.symbol());
        BtcFixedFormat kilo = ((BtcFixedFormat) (BtcFormat.getInstance((-3), Locale.US)));
        Assert.assertEquals("kBTC", kilo.code());
        Assert.assertEquals("k?", kilo.symbol());
        BtcFixedFormat mega = ((BtcFixedFormat) (BtcFormat.getInstance((-6), Locale.US)));
        Assert.assertEquals("MBTC", mega.code());
        Assert.assertEquals("M?", mega.symbol());
        BtcFixedFormat noSymbol = ((BtcFixedFormat) (BtcFormat.getInstance(4, Locale.US)));
        try {
            noSymbol.symbol();
            Assert.fail("non-standard denomination has no symbol()");
        } catch (IllegalStateException e) {
        }
        try {
            noSymbol.code();
            Assert.fail("non-standard denomination has no code()");
        } catch (IllegalStateException e) {
        }
        BtcFixedFormat symbolCoin = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(0).symbol("B\u20e6").build()));
        Assert.assertEquals("BTC", symbolCoin.code());
        Assert.assertEquals("B?", symbolCoin.symbol());
        BtcFixedFormat symbolCent = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(2).symbol("B\u20e6").build()));
        Assert.assertEquals("cBTC", symbolCent.code());
        Assert.assertEquals("?B?", symbolCent.symbol());
        BtcFixedFormat symbolMilli = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(3).symbol("B\u20e6").build()));
        Assert.assertEquals("mBTC", symbolMilli.code());
        Assert.assertEquals("?B?", symbolMilli.symbol());
        BtcFixedFormat symbolMicro = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(6).symbol("B\u20e6").build()));
        Assert.assertEquals("?BTC", symbolMicro.code());
        Assert.assertEquals("?B?", symbolMicro.symbol());
        BtcFixedFormat symbolDeka = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-1)).symbol("B\u20e6").build()));
        Assert.assertEquals("daBTC", symbolDeka.code());
        Assert.assertEquals("daB?", symbolDeka.symbol());
        BtcFixedFormat symbolHecto = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-2)).symbol("B\u20e6").build()));
        Assert.assertEquals("hBTC", symbolHecto.code());
        Assert.assertEquals("hB?", symbolHecto.symbol());
        BtcFixedFormat symbolKilo = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-3)).symbol("B\u20e6").build()));
        Assert.assertEquals("kBTC", symbolKilo.code());
        Assert.assertEquals("kB?", symbolKilo.symbol());
        BtcFixedFormat symbolMega = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-6)).symbol("B\u20e6").build()));
        Assert.assertEquals("MBTC", symbolMega.code());
        Assert.assertEquals("MB?", symbolMega.symbol());
        BtcFixedFormat codeCoin = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(0).code("XBT").build()));
        Assert.assertEquals("XBT", codeCoin.code());
        Assert.assertEquals("?", codeCoin.symbol());
        BtcFixedFormat codeCent = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(2).code("XBT").build()));
        Assert.assertEquals("cXBT", codeCent.code());
        Assert.assertEquals("??", codeCent.symbol());
        BtcFixedFormat codeMilli = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(3).code("XBT").build()));
        Assert.assertEquals("mXBT", codeMilli.code());
        Assert.assertEquals("??", codeMilli.symbol());
        BtcFixedFormat codeMicro = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(6).code("XBT").build()));
        Assert.assertEquals("?XBT", codeMicro.code());
        Assert.assertEquals("??", codeMicro.symbol());
        BtcFixedFormat codeDeka = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-1)).code("XBT").build()));
        Assert.assertEquals("daXBT", codeDeka.code());
        Assert.assertEquals("da?", codeDeka.symbol());
        BtcFixedFormat codeHecto = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-2)).code("XBT").build()));
        Assert.assertEquals("hXBT", codeHecto.code());
        Assert.assertEquals("h?", codeHecto.symbol());
        BtcFixedFormat codeKilo = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-3)).code("XBT").build()));
        Assert.assertEquals("kXBT", codeKilo.code());
        Assert.assertEquals("k?", codeKilo.symbol());
        BtcFixedFormat codeMega = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-6)).code("XBT").build()));
        Assert.assertEquals("MXBT", codeMega.code());
        Assert.assertEquals("M?", codeMega.symbol());
        BtcFixedFormat symbolCodeCoin = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(0).symbol("B\u20e6").code("XBT").build()));
        Assert.assertEquals("XBT", symbolCodeCoin.code());
        Assert.assertEquals("B?", symbolCodeCoin.symbol());
        BtcFixedFormat symbolCodeCent = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(2).symbol("B\u20e6").code("XBT").build()));
        Assert.assertEquals("cXBT", symbolCodeCent.code());
        Assert.assertEquals("?B?", symbolCodeCent.symbol());
        BtcFixedFormat symbolCodeMilli = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(3).symbol("B\u20e6").code("XBT").build()));
        Assert.assertEquals("mXBT", symbolCodeMilli.code());
        Assert.assertEquals("?B?", symbolCodeMilli.symbol());
        BtcFixedFormat symbolCodeMicro = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale(6).symbol("B\u20e6").code("XBT").build()));
        Assert.assertEquals("?XBT", symbolCodeMicro.code());
        Assert.assertEquals("?B?", symbolCodeMicro.symbol());
        BtcFixedFormat symbolCodeDeka = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-1)).symbol("B\u20e6").code("XBT").build()));
        Assert.assertEquals("daXBT", symbolCodeDeka.code());
        Assert.assertEquals("daB?", symbolCodeDeka.symbol());
        BtcFixedFormat symbolCodeHecto = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-2)).symbol("B\u20e6").code("XBT").build()));
        Assert.assertEquals("hXBT", symbolCodeHecto.code());
        Assert.assertEquals("hB?", symbolCodeHecto.symbol());
        BtcFixedFormat symbolCodeKilo = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-3)).symbol("B\u20e6").code("XBT").build()));
        Assert.assertEquals("kXBT", symbolCodeKilo.code());
        Assert.assertEquals("kB?", symbolCodeKilo.symbol());
        BtcFixedFormat symbolCodeMega = ((BtcFixedFormat) (BtcFormat.builder().locale(Locale.US).scale((-6)).symbol("B\u20e6").code("XBT").build()));
        Assert.assertEquals("MXBT", symbolCodeMega.code());
        Assert.assertEquals("MB?", symbolCodeMega.symbol());
    }

    /* copied from CoinFormatTest.java and modified */
    @Test
    public void parse() throws Exception {
        BtcFormat coin = BtcFormat.getCoinInstance(Locale.US);
        Assert.assertEquals(Coin.COIN, coin.parseObject("1"));
        Assert.assertEquals(Coin.COIN, coin.parseObject("1."));
        Assert.assertEquals(Coin.COIN, coin.parseObject("1.0"));
        Assert.assertEquals(Coin.COIN, BtcFormat.getCoinInstance(Locale.GERMANY).parseObject("1,0"));
        Assert.assertEquals(Coin.COIN, coin.parseObject("01.0000000000"));
        // TODO work with express positive sign
        // assertEquals(Coin.COIN, coin.parseObject("+1.0"));
        Assert.assertEquals(Coin.COIN.negate(), coin.parseObject("-1"));
        Assert.assertEquals(Coin.COIN.negate(), coin.parseObject("-1.0"));
        Assert.assertEquals(CENT, coin.parseObject(".01"));
        BtcFormat milli = BtcFormat.getMilliInstance(Locale.US);
        Assert.assertEquals(MILLICOIN, milli.parseObject("1"));
        Assert.assertEquals(MILLICOIN, milli.parseObject("1.0"));
        Assert.assertEquals(MILLICOIN, milli.parseObject("01.0000000000"));
        // TODO work with express positive sign
        // assertEquals(Coin.MILLICOIN, milli.parseObject("+1.0"));
        Assert.assertEquals(MILLICOIN.negate(), milli.parseObject("-1"));
        Assert.assertEquals(MILLICOIN.negate(), milli.parseObject("-1.0"));
        BtcFormat micro = BtcFormat.getMicroInstance(Locale.US);
        Assert.assertEquals(MICROCOIN, micro.parseObject("1"));
        Assert.assertEquals(MICROCOIN, micro.parseObject("1.0"));
        Assert.assertEquals(MICROCOIN, micro.parseObject("01.0000000000"));
        // TODO work with express positive sign
        // assertEquals(Coin.MICROCOIN, micro.parseObject("+1.0"));
        Assert.assertEquals(MICROCOIN.negate(), micro.parseObject("-1"));
        Assert.assertEquals(MICROCOIN.negate(), micro.parseObject("-1.0"));
    }

    /* Copied (and modified) from CoinFormatTest.java */
    @Test
    public void btcRounding() throws Exception {
        BtcFormat coinFormat = BtcFormat.getCoinInstance(Locale.US);
        Assert.assertEquals("0", BtcFormat.getCoinInstance(Locale.US, 0).format(ZERO));
        Assert.assertEquals("0", coinFormat.format(ZERO, 0));
        Assert.assertEquals("0.00", BtcFormat.getCoinInstance(Locale.US, 2).format(ZERO));
        Assert.assertEquals("0.00", coinFormat.format(ZERO, 2));
        Assert.assertEquals("1", BtcFormat.getCoinInstance(Locale.US, 0).format(COIN));
        Assert.assertEquals("1", coinFormat.format(COIN, 0));
        Assert.assertEquals("1.0", BtcFormat.getCoinInstance(Locale.US, 1).format(COIN));
        Assert.assertEquals("1.0", coinFormat.format(COIN, 1));
        Assert.assertEquals("1.00", BtcFormat.getCoinInstance(Locale.US, 2, 2).format(COIN));
        Assert.assertEquals("1.00", coinFormat.format(COIN, 2, 2));
        Assert.assertEquals("1.00", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2).format(COIN));
        Assert.assertEquals("1.00", coinFormat.format(COIN, 2, 2, 2));
        Assert.assertEquals("1.00", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2, 2).format(COIN));
        Assert.assertEquals("1.00", coinFormat.format(COIN, 2, 2, 2, 2));
        Assert.assertEquals("1.000", BtcFormat.getCoinInstance(Locale.US, 3).format(COIN));
        Assert.assertEquals("1.000", coinFormat.format(COIN, 3));
        Assert.assertEquals("1.0000", BtcFormat.getCoinInstance(Locale.US, 4).format(COIN));
        Assert.assertEquals("1.0000", coinFormat.format(COIN, 4));
        final Coin justNot = COIN.subtract(SATOSHI);
        Assert.assertEquals("1", BtcFormat.getCoinInstance(Locale.US, 0).format(justNot));
        Assert.assertEquals("1", coinFormat.format(justNot, 0));
        Assert.assertEquals("1.0", BtcFormat.getCoinInstance(Locale.US, 1).format(justNot));
        Assert.assertEquals("1.0", coinFormat.format(justNot, 1));
        final Coin justNotUnder = Coin.valueOf(99995000);
        Assert.assertEquals("1.00", BtcFormat.getCoinInstance(Locale.US, 2, 2).format(justNot));
        Assert.assertEquals("1.00", coinFormat.format(justNot, 2, 2));
        Assert.assertEquals("1.00", BtcFormat.getCoinInstance(Locale.US, 2, 2).format(justNotUnder));
        Assert.assertEquals("1.00", coinFormat.format(justNotUnder, 2, 2));
        Assert.assertEquals("1.00", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2).format(justNot));
        Assert.assertEquals("1.00", coinFormat.format(justNot, 2, 2, 2));
        Assert.assertEquals("0.999950", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2).format(justNotUnder));
        Assert.assertEquals("0.999950", coinFormat.format(justNotUnder, 2, 2, 2));
        Assert.assertEquals("0.99999999", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2, 2).format(justNot));
        Assert.assertEquals("0.99999999", coinFormat.format(justNot, 2, 2, 2, 2));
        Assert.assertEquals("0.99999999", BtcFormat.getCoinInstance(Locale.US, 2, BtcFixedFormat.REPEATING_DOUBLETS).format(justNot));
        Assert.assertEquals("0.99999999", coinFormat.format(justNot, 2, BtcFixedFormat.REPEATING_DOUBLETS));
        Assert.assertEquals("0.999950", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2, 2).format(justNotUnder));
        Assert.assertEquals("0.999950", coinFormat.format(justNotUnder, 2, 2, 2, 2));
        Assert.assertEquals("0.999950", BtcFormat.getCoinInstance(Locale.US, 2, BtcFixedFormat.REPEATING_DOUBLETS).format(justNotUnder));
        Assert.assertEquals("0.999950", coinFormat.format(justNotUnder, 2, BtcFixedFormat.REPEATING_DOUBLETS));
        Assert.assertEquals("1.000", BtcFormat.getCoinInstance(Locale.US, 3).format(justNot));
        Assert.assertEquals("1.000", coinFormat.format(justNot, 3));
        Assert.assertEquals("1.0000", BtcFormat.getCoinInstance(Locale.US, 4).format(justNot));
        Assert.assertEquals("1.0000", coinFormat.format(justNot, 4));
        final Coin slightlyMore = COIN.add(SATOSHI);
        Assert.assertEquals("1", BtcFormat.getCoinInstance(Locale.US, 0).format(slightlyMore));
        Assert.assertEquals("1", coinFormat.format(slightlyMore, 0));
        Assert.assertEquals("1.0", BtcFormat.getCoinInstance(Locale.US, 1).format(slightlyMore));
        Assert.assertEquals("1.0", coinFormat.format(slightlyMore, 1));
        Assert.assertEquals("1.00", BtcFormat.getCoinInstance(Locale.US, 2, 2).format(slightlyMore));
        Assert.assertEquals("1.00", coinFormat.format(slightlyMore, 2, 2));
        Assert.assertEquals("1.00", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2).format(slightlyMore));
        Assert.assertEquals("1.00", coinFormat.format(slightlyMore, 2, 2, 2));
        Assert.assertEquals("1.00000001", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2, 2).format(slightlyMore));
        Assert.assertEquals("1.00000001", coinFormat.format(slightlyMore, 2, 2, 2, 2));
        Assert.assertEquals("1.00000001", BtcFormat.getCoinInstance(Locale.US, 2, BtcFixedFormat.REPEATING_DOUBLETS).format(slightlyMore));
        Assert.assertEquals("1.00000001", coinFormat.format(slightlyMore, 2, BtcFixedFormat.REPEATING_DOUBLETS));
        Assert.assertEquals("1.000", BtcFormat.getCoinInstance(Locale.US, 3).format(slightlyMore));
        Assert.assertEquals("1.000", coinFormat.format(slightlyMore, 3));
        Assert.assertEquals("1.0000", BtcFormat.getCoinInstance(Locale.US, 4).format(slightlyMore));
        Assert.assertEquals("1.0000", coinFormat.format(slightlyMore, 4));
        final Coin pivot = COIN.add(SATOSHI.multiply(5));
        Assert.assertEquals("1.00000005", BtcFormat.getCoinInstance(Locale.US, 8).format(pivot));
        Assert.assertEquals("1.00000005", coinFormat.format(pivot, 8));
        Assert.assertEquals("1.00000005", BtcFormat.getCoinInstance(Locale.US, 7, 1).format(pivot));
        Assert.assertEquals("1.00000005", coinFormat.format(pivot, 7, 1));
        Assert.assertEquals("1.0000001", BtcFormat.getCoinInstance(Locale.US, 7).format(pivot));
        Assert.assertEquals("1.0000001", coinFormat.format(pivot, 7));
        final Coin value = Coin.valueOf(1122334455667788L);
        Assert.assertEquals("11,223,345", BtcFormat.getCoinInstance(Locale.US, 0).format(value));
        Assert.assertEquals("11,223,345", coinFormat.format(value, 0));
        Assert.assertEquals("11,223,344.6", BtcFormat.getCoinInstance(Locale.US, 1).format(value));
        Assert.assertEquals("11,223,344.6", coinFormat.format(value, 1));
        Assert.assertEquals("11,223,344.5567", BtcFormat.getCoinInstance(Locale.US, 2, 2).format(value));
        Assert.assertEquals("11,223,344.5567", coinFormat.format(value, 2, 2));
        Assert.assertEquals("11,223,344.556678", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2).format(value));
        Assert.assertEquals("11,223,344.556678", coinFormat.format(value, 2, 2, 2));
        Assert.assertEquals("11,223,344.55667788", BtcFormat.getCoinInstance(Locale.US, 2, 2, 2, 2).format(value));
        Assert.assertEquals("11,223,344.55667788", coinFormat.format(value, 2, 2, 2, 2));
        Assert.assertEquals("11,223,344.55667788", BtcFormat.getCoinInstance(Locale.US, 2, BtcFixedFormat.REPEATING_DOUBLETS).format(value));
        Assert.assertEquals("11,223,344.55667788", coinFormat.format(value, 2, BtcFixedFormat.REPEATING_DOUBLETS));
        Assert.assertEquals("11,223,344.557", BtcFormat.getCoinInstance(Locale.US, 3).format(value));
        Assert.assertEquals("11,223,344.557", coinFormat.format(value, 3));
        Assert.assertEquals("11,223,344.5567", BtcFormat.getCoinInstance(Locale.US, 4).format(value));
        Assert.assertEquals("11,223,344.5567", coinFormat.format(value, 4));
        BtcFormat megaFormat = BtcFormat.getInstance((-6), Locale.US);
        Assert.assertEquals("21.00", megaFormat.format(NetworkParameters.MAX_MONEY));
        Assert.assertEquals("21", megaFormat.format(NetworkParameters.MAX_MONEY, 0));
        Assert.assertEquals("11.22334455667788", megaFormat.format(value, 0, BtcFixedFormat.REPEATING_DOUBLETS));
        Assert.assertEquals("11.223344556677", megaFormat.format(Coin.valueOf(1122334455667700L), 0, BtcFixedFormat.REPEATING_DOUBLETS));
        Assert.assertEquals("11.22334455667788", megaFormat.format(value, 0, BtcFixedFormat.REPEATING_TRIPLETS));
        Assert.assertEquals("11.223344556677", megaFormat.format(Coin.valueOf(1122334455667700L), 0, BtcFixedFormat.REPEATING_TRIPLETS));
    }

    /* Warning: these tests assume the state of Locale data extant on the platform on which
    they were written: openjdk 7u21-2.3.9-5
     */
    @Test
    public void equalityTest() throws Exception {
        // First, autodenominator
        Assert.assertEquals(BtcFormat.getInstance(), BtcFormat.getInstance());
        Assert.assertEquals(BtcFormat.getInstance().hashCode(), BtcFormat.getInstance().hashCode());
        Assert.assertNotEquals(BtcFormat.getCodeInstance(), BtcFormat.getSymbolInstance());
        Assert.assertNotEquals(BtcFormat.getCodeInstance().hashCode(), BtcFormat.getSymbolInstance().hashCode());
        Assert.assertEquals(BtcFormat.getSymbolInstance(5), BtcFormat.getSymbolInstance(5));
        Assert.assertEquals(BtcFormat.getSymbolInstance(5).hashCode(), BtcFormat.getSymbolInstance(5).hashCode());
        Assert.assertNotEquals(BtcFormat.getSymbolInstance(5), BtcFormat.getSymbolInstance(4));
        Assert.assertNotEquals(BtcFormat.getSymbolInstance(5).hashCode(), BtcFormat.getSymbolInstance(4).hashCode());
        /* The underlying formatter is mutable, and its currency code
        and symbol may be reset each time a number is
        formatted or parsed.  Here we check to make sure that state is
        ignored when comparing for equality
         */
        // when formatting
        BtcAutoFormat a = ((BtcAutoFormat) (BtcFormat.getSymbolInstance(Locale.US)));
        BtcAutoFormat b = ((BtcAutoFormat) (BtcFormat.getSymbolInstance(Locale.US)));
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        a.format(COIN.multiply(1000000));
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        b.format(COIN.divide(1000000));
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        // when parsing
        a = ((BtcAutoFormat) (BtcFormat.getSymbolInstance(Locale.US)));
        b = ((BtcAutoFormat) (BtcFormat.getSymbolInstance(Locale.US)));
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        a.parseObject("mBTC2");
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        b.parseObject("??4.35");
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        // FRANCE and GERMANY have different pattterns
        Assert.assertNotEquals(BtcFormat.getInstance(Locale.FRANCE).hashCode(), BtcFormat.getInstance(Locale.GERMANY).hashCode());
        // TAIWAN and CHINA differ only in the Locale and Currency, i.e. the patterns and symbols are
        // all the same (after setting the currency symbols to bitcoins)
        Assert.assertNotEquals(BtcFormat.getInstance(Locale.TAIWAN), BtcFormat.getInstance(Locale.CHINA));
        // but they hash the same because of the DecimalFormatSymbols.hashCode() implementation
        Assert.assertEquals(BtcFormat.getSymbolInstance(4), BtcFormat.getSymbolInstance(4));
        Assert.assertEquals(BtcFormat.getSymbolInstance(4).hashCode(), BtcFormat.getSymbolInstance(4).hashCode());
        Assert.assertNotEquals(BtcFormat.getSymbolInstance(4), BtcFormat.getSymbolInstance(5));
        Assert.assertNotEquals(BtcFormat.getSymbolInstance(4).hashCode(), BtcFormat.getSymbolInstance(5).hashCode());
        // Fixed-denomination
        Assert.assertEquals(BtcFormat.getCoinInstance(), BtcFormat.getCoinInstance());
        Assert.assertEquals(BtcFormat.getCoinInstance().hashCode(), BtcFormat.getCoinInstance().hashCode());
        Assert.assertEquals(BtcFormat.getMilliInstance(), BtcFormat.getMilliInstance());
        Assert.assertEquals(BtcFormat.getMilliInstance().hashCode(), BtcFormat.getMilliInstance().hashCode());
        Assert.assertEquals(BtcFormat.getMicroInstance(), BtcFormat.getMicroInstance());
        Assert.assertEquals(BtcFormat.getMicroInstance().hashCode(), BtcFormat.getMicroInstance().hashCode());
        Assert.assertEquals(BtcFormat.getInstance((-6)), BtcFormat.getInstance((-6)));
        Assert.assertEquals(BtcFormat.getInstance((-6)).hashCode(), BtcFormat.getInstance((-6)).hashCode());
        Assert.assertNotEquals(BtcFormat.getCoinInstance(), BtcFormat.getMilliInstance());
        Assert.assertNotEquals(BtcFormat.getCoinInstance().hashCode(), BtcFormat.getMilliInstance().hashCode());
        Assert.assertNotEquals(BtcFormat.getCoinInstance(), BtcFormat.getMicroInstance());
        Assert.assertNotEquals(BtcFormat.getCoinInstance().hashCode(), BtcFormat.getMicroInstance().hashCode());
        Assert.assertNotEquals(BtcFormat.getMilliInstance(), BtcFormat.getMicroInstance());
        Assert.assertNotEquals(BtcFormat.getMilliInstance().hashCode(), BtcFormat.getMicroInstance().hashCode());
        Assert.assertNotEquals(BtcFormat.getInstance(SMALLEST_UNIT_EXPONENT), BtcFormat.getInstance(((SMALLEST_UNIT_EXPONENT) - 1)));
        Assert.assertNotEquals(BtcFormat.getInstance(SMALLEST_UNIT_EXPONENT).hashCode(), BtcFormat.getInstance(((SMALLEST_UNIT_EXPONENT) - 1)).hashCode());
        Assert.assertNotEquals(BtcFormat.getCoinInstance(Locale.TAIWAN), BtcFormat.getCoinInstance(Locale.CHINA));
        Assert.assertNotEquals(BtcFormat.getCoinInstance(2, 3), BtcFormat.getCoinInstance(2, 4));
        Assert.assertNotEquals(BtcFormat.getCoinInstance(2, 3).hashCode(), BtcFormat.getCoinInstance(2, 4).hashCode());
        Assert.assertNotEquals(BtcFormat.getCoinInstance(2, 3), BtcFormat.getCoinInstance(2, 3, 3));
        Assert.assertNotEquals(BtcFormat.getCoinInstance(2, 3).hashCode(), BtcFormat.getCoinInstance(2, 3, 3).hashCode());
    }

    @Test
    public void patternDecimalPlaces() {
        /* The pattern format provided by DecimalFormat includes specification of fractional digits,
        but we ignore that because we have alternative mechanism for specifying that..
         */
        BtcFormat f = BtcFormat.builder().locale(Locale.US).scale(3).pattern("?? #.0").fractionDigits(3).build();
        Assert.assertEquals("Millicoin-format BTC #.000;BTC -#.000", f.toString());
        Assert.assertEquals("mBTC 1000.000", f.format(COIN));
    }
}

