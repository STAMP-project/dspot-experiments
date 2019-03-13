package com.baeldung.string;


import org.junit.Test;


public class DoubleToStringUnitTest {
    private static final double DOUBLE_VALUE = 3.56;

    private static final String TRUNCATED_DOUBLE = "3";

    private static final String ROUNDED_UP_DOUBLE = "4";

    @Test
    public void truncateByCastTest() {
        assertThat(DoubleToString.truncateByCast(DoubleToStringUnitTest.DOUBLE_VALUE)).isEqualTo(DoubleToStringUnitTest.TRUNCATED_DOUBLE);
    }

    @Test
    public void roundingWithStringFormatTest() {
        assertThat(DoubleToString.roundWithStringFormat(DoubleToStringUnitTest.DOUBLE_VALUE)).isEqualTo(DoubleToStringUnitTest.ROUNDED_UP_DOUBLE);
    }

    @Test
    public void truncateWithNumberFormatTest() {
        assertThat(DoubleToString.truncateWithNumberFormat(DoubleToStringUnitTest.DOUBLE_VALUE)).isEqualTo(DoubleToStringUnitTest.TRUNCATED_DOUBLE);
    }

    @Test
    public void roundWithNumberFormatTest() {
        assertThat(DoubleToString.roundWithNumberFormat(DoubleToStringUnitTest.DOUBLE_VALUE)).isEqualTo(DoubleToStringUnitTest.ROUNDED_UP_DOUBLE);
    }

    @Test
    public void truncateWithDecimalFormatTest() {
        assertThat(DoubleToString.truncateWithDecimalFormat(DoubleToStringUnitTest.DOUBLE_VALUE)).isEqualTo(DoubleToStringUnitTest.TRUNCATED_DOUBLE);
    }

    @Test
    public void roundWithDecimalFormatTest() {
        assertThat(DoubleToString.roundWithDecimalFormat(DoubleToStringUnitTest.DOUBLE_VALUE)).isEqualTo(DoubleToStringUnitTest.ROUNDED_UP_DOUBLE);
    }
}

