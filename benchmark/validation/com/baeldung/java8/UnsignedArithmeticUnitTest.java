package com.baeldung.java8;


import org.junit.Assert;
import org.junit.Test;


public class UnsignedArithmeticUnitTest {
    @Test
    public void whenDoublingALargeByteNumber_thenOverflow() {
        byte b1 = 100;
        byte b2 = ((byte) (b1 << 1));
        Assert.assertEquals((-56), b2);
    }

    @Test
    public void whenComparingNumbers_thenNegativeIsInterpretedAsUnsigned() {
        int positive = Integer.MAX_VALUE;
        int negative = Integer.MIN_VALUE;
        int signedComparison = Integer.compare(positive, negative);
        Assert.assertEquals(1, signedComparison);
        int unsignedComparison = Integer.compareUnsigned(positive, negative);
        Assert.assertEquals((-1), unsignedComparison);
        Assert.assertEquals(negative, (positive + 1));
    }

    @Test
    public void whenDividingNumbers_thenNegativeIsInterpretedAsUnsigned() {
        int positive = Integer.MAX_VALUE;
        int negative = Integer.MIN_VALUE;
        Assert.assertEquals((-1), (negative / positive));
        Assert.assertEquals(1, Integer.divideUnsigned(negative, positive));
        Assert.assertEquals((-1), (negative % positive));
        Assert.assertEquals(1, Integer.remainderUnsigned(negative, positive));
    }

    @Test
    public void whenParsingNumbers_thenNegativeIsInterpretedAsUnsigned() {
        Throwable thrown = catchThrowable(() -> Integer.parseInt("2147483648"));
        assertThat(thrown).isInstanceOf(NumberFormatException.class);
        Assert.assertEquals(((Integer.MAX_VALUE) + 1), Integer.parseUnsignedInt("2147483648"));
    }

    @Test
    public void whenFormattingNumbers_thenNegativeIsInterpretedAsUnsigned() {
        String signedString = Integer.toString(Integer.MIN_VALUE);
        Assert.assertEquals("-2147483648", signedString);
        String unsignedString = Integer.toUnsignedString(Integer.MIN_VALUE);
        Assert.assertEquals("2147483648", unsignedString);
    }
}

