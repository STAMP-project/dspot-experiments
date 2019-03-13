/**
 * Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.text;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMProperties;
import org.teavm.junit.TeaVMProperty;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@TeaVMProperties(@TeaVMProperty(key = "java.util.Locale.available", value = "en, en_US, en_GB, ru, ru_RU"))
public class DecimalFormatTest {
    private static DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);

    @Test
    public void parsesIntegerPattern() {
        DecimalFormat format = createFormat("00");
        Assert.assertEquals(2, format.getMinimumIntegerDigits());
        Assert.assertFalse(format.isDecimalSeparatorAlwaysShown());
        Assert.assertFalse(format.isGroupingUsed());
        Assert.assertEquals(0, format.getGroupingSize());
        Assert.assertEquals(0, format.getMinimumFractionDigits());
        Assert.assertEquals(0, format.getMaximumFractionDigits());
        format = createFormat("##");
        Assert.assertEquals(0, format.getMinimumIntegerDigits());
        Assert.assertFalse(format.isDecimalSeparatorAlwaysShown());
        Assert.assertFalse(format.isGroupingUsed());
        Assert.assertEquals(0, format.getGroupingSize());
        Assert.assertEquals(0, format.getMinimumFractionDigits());
        Assert.assertEquals(0, format.getMaximumFractionDigits());
        format = createFormat("#,##0");
        Assert.assertEquals(1, format.getMinimumIntegerDigits());
        Assert.assertFalse(format.isDecimalSeparatorAlwaysShown());
        Assert.assertTrue(format.isGroupingUsed());
        Assert.assertEquals(3, format.getGroupingSize());
        Assert.assertEquals(0, format.getMinimumFractionDigits());
        Assert.assertEquals(0, format.getMaximumFractionDigits());
    }

    @Test
    public void selectsLastGrouping() {
        DecimalFormat format = new DecimalFormat("#,0,000");
        Assert.assertEquals(4, format.getMinimumIntegerDigits());
        Assert.assertTrue(format.isGroupingUsed());
        Assert.assertEquals(3, format.getGroupingSize());
    }

    @Test
    public void parsesPrefixAndSuffixInPattern() {
        DecimalFormat format = createFormat("(00)");
        Assert.assertEquals(2, format.getMinimumIntegerDigits());
        Assert.assertEquals("(", format.getPositivePrefix());
        Assert.assertEquals(")", format.getPositiveSuffix());
        Assert.assertEquals("-(", format.getNegativePrefix());
        Assert.assertEquals(")", format.getNegativeSuffix());
        format = createFormat("+(00);-{#}");
        Assert.assertEquals(2, format.getMinimumIntegerDigits());
        Assert.assertEquals("+(", format.getPositivePrefix());
        Assert.assertEquals(")", format.getPositiveSuffix());
        Assert.assertEquals("-{", format.getNegativePrefix());
    }

    @Test
    public void parsesFractionalPattern() {
        DecimalFormat format = createFormat("#.");
        Assert.assertEquals(1, format.getMinimumIntegerDigits());
        Assert.assertTrue(format.isDecimalSeparatorAlwaysShown());
        Assert.assertFalse(format.isGroupingUsed());
        Assert.assertEquals(0, format.getGroupingSize());
        Assert.assertEquals(0, format.getMinimumFractionDigits());
        Assert.assertEquals(0, format.getMaximumFractionDigits());
        format = createFormat("#.00");
        Assert.assertEquals(0, format.getMinimumIntegerDigits());
        Assert.assertFalse(format.isGroupingUsed());
        Assert.assertEquals(0, format.getGroupingSize());
        Assert.assertEquals(2, format.getMinimumFractionDigits());
        Assert.assertEquals(2, format.getMaximumFractionDigits());
        format = createFormat("#.00##");
        Assert.assertEquals(0, format.getMinimumIntegerDigits());
        Assert.assertFalse(format.isGroupingUsed());
        Assert.assertEquals(0, format.getGroupingSize());
        Assert.assertEquals(2, format.getMinimumFractionDigits());
        Assert.assertEquals(4, format.getMaximumFractionDigits());
        format = createFormat("#00.00##");
        Assert.assertEquals(2, format.getMinimumIntegerDigits());
        Assert.assertFalse(format.isGroupingUsed());
        Assert.assertEquals(0, format.getGroupingSize());
        Assert.assertEquals(2, format.getMinimumFractionDigits());
        Assert.assertEquals(4, format.getMaximumFractionDigits());
        format = createFormat("#,#00.00##");
        Assert.assertEquals(2, format.getMinimumIntegerDigits());
        Assert.assertTrue(format.isGroupingUsed());
        Assert.assertEquals(3, format.getGroupingSize());
        Assert.assertEquals(2, format.getMinimumFractionDigits());
        Assert.assertEquals(4, format.getMaximumFractionDigits());
    }

    @Test
    public void parsesExponentialPattern() {
        DecimalFormat format = createFormat("##0E00");
        Assert.assertEquals(1, format.getMinimumIntegerDigits());
        Assert.assertEquals(0, format.getGroupingSize());
        Assert.assertEquals(0, format.getMinimumFractionDigits());
        Assert.assertEquals(0, format.getMaximumFractionDigits());
    }

    @Test
    public void formatsIntegerPart() {
        DecimalFormat format = createFormat("00");
        Assert.assertEquals("02", format.format(2));
        Assert.assertEquals("23", format.format(23));
        Assert.assertEquals("23", format.format(23.2));
        Assert.assertEquals("24", format.format(23.7));
    }

    @Test
    public void formatsBigIntegerPart() {
        DecimalFormat format = createFormat("00");
        Assert.assertEquals("02", format.format(new BigInteger("2")));
        Assert.assertEquals("23", format.format(new BigInteger("23")));
        Assert.assertEquals("23", format.format(new BigDecimal("23.2")));
        Assert.assertEquals("24", format.format(new BigDecimal("23.7")));
    }

    @Test
    public void formatsNumber() {
        DecimalFormat format = createFormat("0.0");
        Assert.assertEquals("23.0", format.format(23));
        Assert.assertEquals("23.2", format.format(23.2));
        Assert.assertEquals("23.2", format.format(23.23));
        Assert.assertEquals("23.3", format.format(23.27));
        Assert.assertEquals("0.0", format.format(1.0E-4));
        format = createFormat("00000000000000000000000000.0");
        Assert.assertEquals("00000000000000000000000023.0", format.format(23));
        Assert.assertEquals("00002300000000000000000000.0", format.format(2.3E21));
        Assert.assertEquals("23000000000000000000000000.0", format.format(2.3E25));
        format = createFormat("0.00000000000000000000000000");
        Assert.assertEquals("23.00000000000000000000000000", format.format(23));
        Assert.assertEquals("0.23000000000000000000000000", format.format(0.23));
        Assert.assertEquals("0.00230000000000000000000000", format.format(0.0023));
        Assert.assertEquals("0.00000000000000000000230000", format.format(2.3E-21));
        Assert.assertEquals("0.00000000000000000000000023", format.format(2.3E-25));
    }

    @Test
    public void formatsBigNumber() {
        DecimalFormat format = createFormat("0.0");
        Assert.assertEquals("23.0", format.format(BigInteger.valueOf(23)));
        Assert.assertEquals("23.2", format.format(new BigDecimal("23.2")));
        Assert.assertEquals("23.2", format.format(new BigDecimal("23.23")));
        Assert.assertEquals("23.3", format.format(new BigDecimal("23.27")));
        Assert.assertEquals("0.0", format.format(new BigDecimal("0.0001")));
        format = createFormat("00000000000000000000000000.0");
        Assert.assertEquals("00000000000000000000000023.0", format.format(new BigInteger("23")));
        Assert.assertEquals("00002300000000000000000000.0", format.format(new BigInteger("2300000000000000000000")));
        Assert.assertEquals("23000000000000000000000000.0", format.format(new BigInteger("23000000000000000000000000")));
        format = createFormat("0.00000000000000000000000000");
        Assert.assertEquals("23.00000000000000000000000000", format.format(new BigInteger("23")));
        Assert.assertEquals("0.23000000000000000000000000", format.format(new BigDecimal("0.23")));
        Assert.assertEquals("0.00230000000000000000000000", format.format(new BigDecimal("0.0023")));
        Assert.assertEquals("0.00000000000000000000230000", format.format(new BigDecimal("0.0000000000000000000023")));
        Assert.assertEquals("0.00000000000000000000000023", format.format(new BigDecimal("0.00000000000000000000000023")));
    }

    @Test
    public void formatsFractionalPart() {
        DecimalFormat format = createFormat("0.0000####");
        Assert.assertEquals("0.00001235", format.format(1.23456E-5));
        Assert.assertEquals("0.00012346", format.format(1.23456E-4));
        Assert.assertEquals("0.00123456", format.format(0.00123456));
        Assert.assertEquals("0.0123456", format.format(0.0123456));
        Assert.assertEquals("0.1200", format.format(0.12));
        Assert.assertEquals("0.1230", format.format(0.123));
        Assert.assertEquals("0.1234", format.format(0.1234));
        Assert.assertEquals("0.12345", format.format(0.12345));
        format = createFormat("0.##");
        Assert.assertEquals("23", format.format(23));
        Assert.assertEquals("2.3", format.format(2.3));
        Assert.assertEquals("0.23", format.format(0.23));
        Assert.assertEquals("0.02", format.format(0.023));
    }

    @Test
    public void roundingWorks() {
        DecimalFormat format = createFormat("0");
        format.setRoundingMode(RoundingMode.UP);
        Assert.assertEquals("3", format.format(2.3));
        Assert.assertEquals("3", format.format(2.7));
        Assert.assertEquals("-3", format.format((-2.3)));
        Assert.assertEquals("-3", format.format((-2.7)));
        format.setRoundingMode(RoundingMode.DOWN);
        Assert.assertEquals("2", format.format(2.3));
        Assert.assertEquals("2", format.format(2.7));
        Assert.assertEquals("-2", format.format((-2.3)));
        Assert.assertEquals("-2", format.format((-2.7)));
        format.setRoundingMode(RoundingMode.FLOOR);
        Assert.assertEquals("2", format.format(2.3));
        Assert.assertEquals("2", format.format(2.7));
        Assert.assertEquals("-3", format.format((-2.3)));
        Assert.assertEquals("-3", format.format((-2.7)));
        format.setRoundingMode(RoundingMode.CEILING);
        Assert.assertEquals("3", format.format(2.3));
        Assert.assertEquals("3", format.format(2.7));
        Assert.assertEquals("-2", format.format((-2.3)));
        Assert.assertEquals("-2", format.format((-2.7)));
        format.setRoundingMode(RoundingMode.HALF_DOWN);
        Assert.assertEquals("2", format.format(2.3));
        Assert.assertEquals("3", format.format(2.7));
        Assert.assertEquals("2", format.format(2.5));
        Assert.assertEquals("3", format.format(3.5));
        Assert.assertEquals("-2", format.format((-2.5)));
        Assert.assertEquals("-3", format.format((-3.5)));
        format.setRoundingMode(RoundingMode.HALF_UP);
        Assert.assertEquals("2", format.format(2.3));
        Assert.assertEquals("3", format.format(2.7));
        Assert.assertEquals("3", format.format(2.5));
        Assert.assertEquals("4", format.format(3.5));
        Assert.assertEquals("-3", format.format((-2.5)));
        Assert.assertEquals("-4", format.format((-3.5)));
        format.setRoundingMode(RoundingMode.HALF_EVEN);
        Assert.assertEquals("2", format.format(2.3));
        Assert.assertEquals("3", format.format(2.7));
        Assert.assertEquals("2", format.format(2.5));
        Assert.assertEquals("4", format.format(3.5));
        Assert.assertEquals("-2", format.format((-2.5)));
        Assert.assertEquals("-4", format.format((-3.5)));
    }

    @Test
    public void bigRoundingWorks() {
        DecimalFormat format = createFormat("0");
        format.setRoundingMode(RoundingMode.UP);
        Assert.assertEquals("3", format.format(new BigDecimal("2.3")));
        Assert.assertEquals("3", format.format(new BigDecimal("2.7")));
        Assert.assertEquals("-3", format.format(new BigDecimal("-2.3")));
        Assert.assertEquals("-3", format.format(new BigDecimal("-2.7")));
        format.setRoundingMode(RoundingMode.DOWN);
        Assert.assertEquals("2", format.format(new BigDecimal("2.3")));
        Assert.assertEquals("2", format.format(new BigDecimal("2.7")));
        Assert.assertEquals("-2", format.format(new BigDecimal("-2.3")));
        Assert.assertEquals("-2", format.format(new BigDecimal("-2.7")));
        format.setRoundingMode(RoundingMode.FLOOR);
        Assert.assertEquals("2", format.format(new BigDecimal("2.3")));
        Assert.assertEquals("2", format.format(new BigDecimal("2.7")));
        Assert.assertEquals("-3", format.format(new BigDecimal("-2.3")));
        Assert.assertEquals("-3", format.format(new BigDecimal("-2.7")));
        format.setRoundingMode(RoundingMode.CEILING);
        Assert.assertEquals("3", format.format(new BigDecimal("2.3")));
        Assert.assertEquals("3", format.format(new BigDecimal("2.7")));
        Assert.assertEquals("-2", format.format(new BigDecimal("-2.3")));
        Assert.assertEquals("-2", format.format(new BigDecimal("-2.7")));
        format.setRoundingMode(RoundingMode.HALF_DOWN);
        Assert.assertEquals("2", format.format(new BigDecimal("2.3")));
        Assert.assertEquals("3", format.format(new BigDecimal("2.7")));
        Assert.assertEquals("2", format.format(new BigDecimal("2.5")));
        Assert.assertEquals("3", format.format(new BigDecimal("3.5")));
        Assert.assertEquals("-2", format.format(new BigDecimal("-2.5")));
        Assert.assertEquals("-3", format.format(new BigDecimal("-3.5")));
        format.setRoundingMode(RoundingMode.HALF_UP);
        Assert.assertEquals("2", format.format(new BigDecimal("2.3")));
        Assert.assertEquals("3", format.format(new BigDecimal("2.7")));
        Assert.assertEquals("3", format.format(new BigDecimal("2.5")));
        Assert.assertEquals("4", format.format(new BigDecimal("3.5")));
        Assert.assertEquals("-3", format.format(new BigDecimal("-2.5")));
        Assert.assertEquals("-4", format.format(new BigDecimal("-3.5")));
        format.setRoundingMode(RoundingMode.HALF_EVEN);
        Assert.assertEquals("2", format.format(new BigDecimal("2.3")));
        Assert.assertEquals("3", format.format(new BigDecimal("2.7")));
        Assert.assertEquals("2", format.format(new BigDecimal("2.5")));
        Assert.assertEquals("4", format.format(new BigDecimal("3.5")));
        Assert.assertEquals("-2", format.format(new BigDecimal("-2.5")));
        Assert.assertEquals("-4", format.format(new BigDecimal("-3.5")));
    }

    @Test
    public void formatsWithGroups() {
        DecimalFormat format = createFormat("#,###.0");
        Assert.assertEquals("23.0", format.format(23));
        Assert.assertEquals("2,300.0", format.format(2300));
        Assert.assertEquals("2,300,000,000,000,000,000,000.0", format.format(2.3E21));
        Assert.assertEquals("23,000,000,000,000,000,000,000,000.0", format.format(2.3E25));
        format = createFormat("000,000,000,000,000,000,000");
        Assert.assertEquals("000,000,000,000,000,000,023", format.format(23));
    }

    @Test
    public void formatsBigWithGroups() {
        DecimalFormat format = createFormat("#,###.0");
        Assert.assertEquals("23.0", format.format(BigInteger.valueOf(23)));
        Assert.assertEquals("2,300.0", format.format(BigInteger.valueOf(2300)));
        Assert.assertEquals("2,300,000,000,000,000,000,000.0", format.format(new BigInteger("2300000000000000000000")));
        Assert.assertEquals("23,000,000,000,000,000,000,000,000.0", format.format(new BigInteger("23000000000000000000000000")));
        format = createFormat("000,000,000,000,000,000,000");
        Assert.assertEquals("000,000,000,000,000,000,023", format.format(BigInteger.valueOf(23)));
    }

    @Test
    public void formatsLargeValues() {
        DecimalFormat format = createFormat("0");
        Assert.assertEquals("9223372036854775807", format.format(9223372036854775807L));
        Assert.assertEquals("-9223372036854775808", format.format(-9223372036854775808L));
    }

    @Test
    public void formatsExponent() {
        DecimalFormat format = createFormat("000E0");
        Assert.assertEquals("230E-1", format.format(23));
        Assert.assertEquals("230E0", format.format(230));
        Assert.assertEquals("230E1", format.format(2300));
        Assert.assertEquals("123E1", format.format(1234));
        Assert.assertEquals("-123E1", format.format((-1234)));
        format = createFormat("0.00E0");
        Assert.assertEquals("2.00E1", format.format(20));
        Assert.assertEquals("2.30E1", format.format(23));
        Assert.assertEquals("2.30E2", format.format(230));
        Assert.assertEquals("1.23E3", format.format(1234));
        format = createFormat("000000000000000000000.00E0");
        Assert.assertEquals("230000000000000000000.00E-19", format.format(23));
        format = createFormat("0.0000000000000000000000E0");
        Assert.assertEquals("2.3000000000000000000000E1", format.format(23));
        format = createFormat("0.0##E0");
        Assert.assertEquals("1.0E0", format.format(1));
        Assert.assertEquals("1.2E1", format.format(12));
        Assert.assertEquals("1.23E2", format.format(123));
        Assert.assertEquals("1.234E3", format.format(1234));
        Assert.assertEquals("1.234E4", format.format(12345));
    }

    @Test
    public void formatsBigExponent() {
        DecimalFormat format = createFormat("000E0");
        Assert.assertEquals("230E-1", format.format(BigInteger.valueOf(23)));
        Assert.assertEquals("230E0", format.format(BigInteger.valueOf(230)));
        Assert.assertEquals("230E1", format.format(BigInteger.valueOf(2300)));
        Assert.assertEquals("123E1", format.format(BigInteger.valueOf(1234)));
        Assert.assertEquals("-123E1", format.format(BigInteger.valueOf((-1234))));
        format = createFormat("0.00E0");
        Assert.assertEquals("2.00E1", format.format(BigInteger.valueOf(20)));
        Assert.assertEquals("2.30E1", format.format(BigInteger.valueOf(23)));
        Assert.assertEquals("2.30E2", format.format(BigInteger.valueOf(230)));
        Assert.assertEquals("1.23E3", format.format(BigInteger.valueOf(1234)));
        format = createFormat("000000000000000000000.00E0");
        Assert.assertEquals("230000000000000000000.00E-19", format.format(BigInteger.valueOf(23)));
        format = createFormat("0.0000000000000000000000E0");
        Assert.assertEquals("2.3000000000000000000000E1", format.format(BigInteger.valueOf(23)));
        format = createFormat("0.0##E0");
        Assert.assertEquals("1.0E0", format.format(BigInteger.valueOf(1)));
        Assert.assertEquals("1.2E1", format.format(BigInteger.valueOf(12)));
        Assert.assertEquals("1.23E2", format.format(BigInteger.valueOf(123)));
        Assert.assertEquals("1.234E3", format.format(BigInteger.valueOf(1234)));
        Assert.assertEquals("1.234E4", format.format(BigInteger.valueOf(12345)));
    }

    @Test
    public void formatsExponentWithMultiplier() {
        DecimalFormat format = createFormat("##0.00E0");
        Assert.assertEquals("2.30E0", format.format(2.3));
        Assert.assertEquals("23.0E0", format.format(23));
        Assert.assertEquals("230E0", format.format(230));
        Assert.assertEquals("2.30E3", format.format(2300));
        Assert.assertEquals("23.0E3", format.format(23000));
    }

    @Test
    public void formatsBigExponentWithMultiplier() {
        DecimalFormat format = createFormat("##0.00E0");
        Assert.assertEquals("2.30E0", format.format(new BigDecimal("2.3")));
        Assert.assertEquals("23.0E0", format.format(new BigDecimal("23")));
        Assert.assertEquals("230E0", format.format(new BigDecimal("230")));
        Assert.assertEquals("2.30E3", format.format(new BigDecimal("2300")));
        Assert.assertEquals("23.0E3", format.format(new BigDecimal("23000")));
    }

    @Test
    public void formatsSpecialValues() {
        DecimalFormat format = createFormat("0");
        Assert.assertEquals("?", format.format(Double.POSITIVE_INFINITY));
        Assert.assertEquals("-?", format.format(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void formatsWithMultiplier() {
        DecimalFormat format = createFormat("0");
        format.setMultiplier(2);
        Assert.assertEquals("18446744073709551614", format.format(9223372036854775807L));
        Assert.assertEquals("46", format.format(BigInteger.valueOf(23)));
        format.setMultiplier(100);
        Assert.assertEquals("2300", format.format(23));
        Assert.assertEquals("2300", format.format(BigInteger.valueOf(23)));
        format = createFormat("00E0");
        format.setMultiplier(2);
        Assert.assertEquals("18E18", format.format(9223372036854775807L));
        Assert.assertEquals("46E0", format.format(BigInteger.valueOf(23)));
        format.setMultiplier(100);
        Assert.assertEquals("23E2", format.format(23));
        Assert.assertEquals("23E2", format.format(BigInteger.valueOf(23)));
    }

    @Test
    public void formatsSpecial() {
        DecimalFormat format = createFormat("0%");
        Assert.assertEquals("23%", format.format(0.23));
        format = createFormat("0?");
        Assert.assertEquals("230?", format.format(0.23));
        format = createFormat("0.00 ?");
        format.setCurrency(Currency.getInstance("RUB"));
        Assert.assertEquals("23.00 RUB", format.format(23));
    }
}

