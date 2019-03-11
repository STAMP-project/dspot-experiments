package com.kickstarter.libs.utils;


import com.kickstarter.libs.NumberOptions;
import java.math.RoundingMode;
import java.util.Locale;
import junit.framework.TestCase;


public final class NumberUtilsTest extends TestCase {
    public void testFlooredPercentage() {
        TestCase.assertEquals("50%", NumberUtils.flooredPercentage(50.0F));
        TestCase.assertEquals("99%", NumberUtils.flooredPercentage(99.99F));
        TestCase.assertEquals("0%", NumberUtils.flooredPercentage(0.01F));
        TestCase.assertEquals("1,000%", NumberUtils.flooredPercentage(1000.0F));
    }

    public void testFlooredPercentage_withFrenchLocale() {
        TestCase.assertEquals("50 %", NumberUtils.flooredPercentage(50.0F, Locale.FRENCH));
    }

    public void testFlooredPercentage_withGermanLocale() {
        TestCase.assertEquals("1.000%", NumberUtils.flooredPercentage(1000.0F, Locale.GERMAN));
    }

    public void testFormatNumber_int() {
        TestCase.assertEquals("100", NumberUtils.format(100));
        TestCase.assertEquals("1,000", NumberUtils.format(1000));
    }

    public void testFormatNumber_intWithGermanyLocale() {
        TestCase.assertEquals("1.000", NumberUtils.format(1000, Locale.GERMANY));
    }

    public void testFormatNumber_float() {
        TestCase.assertEquals("100", NumberUtils.format(100.0F));
        TestCase.assertEquals("1,000", NumberUtils.format(1000.0F));
        TestCase.assertEquals("1,001", NumberUtils.format(1000.6F));
    }

    public void testFormatNumber_floatRounding() {
        TestCase.assertEquals("1", NumberUtils.format(1.1F));
        TestCase.assertEquals("1", NumberUtils.format(1.5F));
        TestCase.assertEquals("2", NumberUtils.format(2.5F));
        TestCase.assertEquals("2", NumberUtils.format(1.51F));
        TestCase.assertEquals("1", NumberUtils.format(1.9F, NumberOptions.builder().roundingMode(RoundingMode.DOWN).build()));
        TestCase.assertEquals("2", NumberUtils.format(1.1F, NumberOptions.builder().roundingMode(RoundingMode.UP).build()));
    }

    public void testFormatNumber_floatWithPrecision() {
        TestCase.assertEquals("100.12", NumberUtils.format(100.12F, NumberOptions.builder().precision(2).build()));
        TestCase.assertEquals("100.2", NumberUtils.format(100.16F, NumberOptions.builder().precision(1).build()));
        TestCase.assertEquals("100.00", NumberUtils.format(100.0F, NumberOptions.builder().precision(2).build()));
    }

    public void testFormatNumber_floatWithBucket() {
        TestCase.assertEquals("100", NumberUtils.format(100.0F, NumberOptions.builder().bucketAbove(100.0F).build()));
        TestCase.assertEquals("100", NumberUtils.format(100.0F, NumberOptions.builder().bucketAbove(1000.0F).build()));
        TestCase.assertEquals("1K", NumberUtils.format(1000.0F, NumberOptions.builder().bucketAbove(1000.0F).build()));
        TestCase.assertEquals("10K", NumberUtils.format(10000.0F, NumberOptions.builder().bucketAbove(1000.0F).build()));
        TestCase.assertEquals("100K", NumberUtils.format(100000.0F, NumberOptions.builder().bucketAbove(1000.0F).build()));
        TestCase.assertEquals("1,000K", NumberUtils.format(1000000.0F, NumberOptions.builder().bucketAbove(1000.0F).build()));
        TestCase.assertEquals("100", NumberUtils.format(100.0F, NumberOptions.builder().bucketAbove(10000.0F).build()));
        TestCase.assertEquals("1,000", NumberUtils.format(1000.0F, NumberOptions.builder().bucketAbove(10000.0F).build()));
        TestCase.assertEquals("10K", NumberUtils.format(10000.0F, NumberOptions.builder().bucketAbove(10000.0F).build()));
        TestCase.assertEquals("100K", NumberUtils.format(100000.0F, NumberOptions.builder().bucketAbove(10000.0F).build()));
        TestCase.assertEquals("1,000K", NumberUtils.format(1000000.0F, NumberOptions.builder().bucketAbove(10000.0F).build()));
        TestCase.assertEquals("100", NumberUtils.format(100.0F, NumberOptions.builder().bucketAbove(100000.0F).build()));
        TestCase.assertEquals("1,000", NumberUtils.format(1000.0F, NumberOptions.builder().bucketAbove(100000.0F).build()));
        TestCase.assertEquals("10,000", NumberUtils.format(10000.0F, NumberOptions.builder().bucketAbove(100000.0F).build()));
        TestCase.assertEquals("100K", NumberUtils.format(100000.0F, NumberOptions.builder().bucketAbove(100000.0F).build()));
        TestCase.assertEquals("1,000K", NumberUtils.format(1000000.0F, NumberOptions.builder().bucketAbove(100000.0F).build()));
        TestCase.assertEquals("100", NumberUtils.format(100.0F, NumberOptions.builder().bucketAbove(1000000.0F).build()));
        TestCase.assertEquals("1,000", NumberUtils.format(1000.0F, NumberOptions.builder().bucketAbove(1000000.0F).build()));
        TestCase.assertEquals("10,000", NumberUtils.format(10000.0F, NumberOptions.builder().bucketAbove(1000000.0F).build()));
        TestCase.assertEquals("100,000", NumberUtils.format(100000.0F, NumberOptions.builder().bucketAbove(1000000.0F).build()));
        TestCase.assertEquals("1M", NumberUtils.format(1000000.0F, NumberOptions.builder().bucketAbove(1000000.0F).build()));
        TestCase.assertEquals("111", NumberUtils.format(111.0F, NumberOptions.builder().bucketAbove(1000.0F).bucketPrecision(1).build()));
        TestCase.assertEquals("111.00", NumberUtils.format(111.0F, NumberOptions.builder().bucketAbove(111.0F).bucketPrecision(1).precision(2).build()));
        TestCase.assertEquals("1.1K", NumberUtils.format(1111.0F, NumberOptions.builder().bucketAbove(1000.0F).bucketPrecision(1).build()));
        TestCase.assertEquals("1.1K", NumberUtils.format(1111.0F, NumberOptions.builder().bucketAbove(1000.0F).bucketPrecision(1).precision(2).build()));
        TestCase.assertEquals("11.1K", NumberUtils.format(11111.0F, NumberOptions.builder().bucketAbove(1000.0F).bucketPrecision(1).build()));
        TestCase.assertEquals("111.1K", NumberUtils.format(111111.0F, NumberOptions.builder().bucketAbove(1000.0F).bucketPrecision(1).build()));
        TestCase.assertEquals("1,111.1K", NumberUtils.format(1111111.0F, NumberOptions.builder().bucketAbove(1000.0F).bucketPrecision(1).build()));
    }

    public void testFormatNumber_floatWithCurrency() {
        TestCase.assertEquals("$100", NumberUtils.format(100.0F, NumberOptions.builder().currencySymbol("$").build()));
        TestCase.assertEquals("?100", NumberUtils.format(100.0F, NumberOptions.builder().currencySymbol("?").build()));
        TestCase.assertEquals("$100 CAD", NumberUtils.format(100.0F, NumberOptions.builder().currencySymbol("$").currencyCode("CAD").build()));
    }

    public void testFormatNumber_floatWithCurrencyAndGermanyLocale() {
        TestCase.assertEquals("100 $", NumberUtils.format(100.0F, NumberOptions.builder().currencySymbol("$").build(), Locale.GERMANY));
        TestCase.assertEquals("1.000", NumberUtils.format(1000.0F, NumberOptions.builder().build(), Locale.GERMANY));
        TestCase.assertEquals("100,12", NumberUtils.format(100.12F, NumberOptions.builder().precision(2).build(), Locale.GERMANY));
    }
}

