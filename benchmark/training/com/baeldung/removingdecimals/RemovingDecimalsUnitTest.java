package com.baeldung.removingdecimals;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that demonstrate some different approaches for formatting a
 * floating-point value into a {@link String} while removing the decimal part.
 */
public class RemovingDecimalsUnitTest {
    private final double doubleValue = 345.56;

    @Test
    public void whenCastToInt_thenValueIsTruncated() {
        String truncated = String.valueOf(((int) (doubleValue)));
        Assert.assertEquals("345", truncated);
    }

    @Test
    public void givenALargeDouble_whenCastToInt_thenValueIsNotTruncated() {
        double outOfIntRange = 6.00000000056E9;
        String truncationAttempt = String.valueOf(((int) (outOfIntRange)));
        Assert.assertNotEquals("6000000000", truncationAttempt);
    }

    @Test
    public void whenUsingStringFormat_thenValueIsRounded() {
        String rounded = String.format("%.0f", doubleValue);
        Assert.assertEquals("346", rounded);
    }

    @Test
    public void givenALargeDouble_whenUsingStringFormat_thenValueIsStillRounded() {
        double outOfIntRange = 6.00000000056E9;
        String rounded = String.format("%.0f", outOfIntRange);
        Assert.assertEquals("6000000001", rounded);
    }

    @Test
    public void whenUsingNumberFormat_thenValueIsRounded() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        String rounded = nf.format(doubleValue);
        Assert.assertEquals("346", rounded);
    }

    @Test
    public void whenUsingNumberFormatWithFloor_thenValueIsTruncated() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setRoundingMode(RoundingMode.FLOOR);
        String truncated = nf.format(doubleValue);
        Assert.assertEquals("345", truncated);
    }

    @Test
    public void whenUsingDecimalFormat_thenValueIsRounded() {
        DecimalFormat df = new DecimalFormat("#,###");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String rounded = df.format(doubleValue);
        Assert.assertEquals("346", rounded);
    }

    @Test
    public void whenUsingDecimalFormatWithFloor_thenValueIsTruncated() {
        DecimalFormat df = new DecimalFormat("#,###");
        df.setRoundingMode(RoundingMode.FLOOR);
        String truncated = df.format(doubleValue);
        Assert.assertEquals("345", truncated);
    }

    @Test
    public void whenUsingBigDecimalDoubleValue_thenValueIsTruncated() {
        BigDecimal big = new BigDecimal(doubleValue);
        big = big.setScale(0, RoundingMode.FLOOR);
        String truncated = big.toString();
        Assert.assertEquals("345", truncated);
    }

    @Test
    public void whenUsingBigDecimalDoubleValueWithHalfUp_thenValueIsRounded() {
        BigDecimal big = new BigDecimal(doubleValue);
        big = big.setScale(0, RoundingMode.HALF_UP);
        String truncated = big.toString();
        Assert.assertEquals("346", truncated);
    }
}

