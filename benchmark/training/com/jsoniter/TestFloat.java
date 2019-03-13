package com.jsoniter;


import java.io.IOException;
import java.math.BigDecimal;
import junit.framework.TestCase;


public class TestFloat extends TestCase {
    private boolean isStreaming;

    public void test_positive_negative() throws IOException {
        // positive
        TestCase.assertEquals(12.3F, parseFloat("12.3,"));
        TestCase.assertEquals(729212.0F, parseFloat("729212.0233,"));
        TestCase.assertEquals(12.3, parseDouble("12.3,"));
        TestCase.assertEquals(729212.0233, parseDouble("729212.0233,"));
        // negative
        TestCase.assertEquals((-12.3F), parseFloat("-12.3,"));
        TestCase.assertEquals((-12.3), parseDouble("-12.3,"));
    }

    public void test_long_double() throws IOException {
        double d = JsonIterator.deserialize("4593560419846153055", double.class);
        TestCase.assertEquals(4.5935604198461532E18, d, 0.1);
    }

    public void test_ieee_754() throws IOException {
        TestCase.assertEquals(0.00123F, parseFloat("123e-5,"));
        TestCase.assertEquals(0.00123, parseDouble("123e-5,"));
    }

    public void test_decimal_places() throws IOException {
        TestCase.assertEquals(Long.MAX_VALUE, parseFloat("9223372036854775807,"), 0.01F);
        TestCase.assertEquals(Long.MAX_VALUE, parseDouble("9223372036854775807,"), 0.01F);
        TestCase.assertEquals(Long.MIN_VALUE, parseDouble("-9223372036854775808,"), 0.01F);
        TestCase.assertEquals(9.923372E18F, parseFloat("9923372036854775807,"), 0.01F);
        TestCase.assertEquals((-9.923372E18F), parseFloat("-9923372036854775808,"), 0.01F);
        TestCase.assertEquals(9.923372036854776E18, parseDouble("9923372036854775807,"), 0.01F);
        TestCase.assertEquals((-9.923372036854776E18), parseDouble("-9923372036854775808,"), 0.01F);
        TestCase.assertEquals(720368.56F, parseFloat("720368.54775807,"), 0.01F);
        TestCase.assertEquals((-720368.56F), parseFloat("-720368.54775807,"), 0.01F);
        TestCase.assertEquals(720368.54775807, parseDouble("720368.54775807,"), 0.01F);
        TestCase.assertEquals((-720368.54775807), parseDouble("-720368.54775807,"), 0.01F);
        TestCase.assertEquals(72036.85F, parseFloat("72036.854775807,"), 0.01F);
        TestCase.assertEquals(72036.854775807, parseDouble("72036.854775807,"), 0.01F);
        TestCase.assertEquals(720368.56F, parseFloat("720368.547758075,"), 0.01F);
        TestCase.assertEquals(720368.54775807, parseDouble("720368.547758075,"), 0.01F);
    }

    public void test_combination_of_dot_and_exponent() throws IOException {
        double v = JsonIterator.parse("8.37377E9").readFloat();
        TestCase.assertEquals(Double.valueOf("8.37377E9"), v, 1000.0);
    }

    public void testBigDecimal() {
        BigDecimal number = JsonIterator.deserialize("100.1", BigDecimal.class);
        TestCase.assertEquals(new BigDecimal("100.1"), number);
    }

    public void testChooseDouble() {
        Object number = JsonIterator.deserialize("1.1", Object.class);
        TestCase.assertEquals(1.1, number);
        number = JsonIterator.deserialize("1.0", Object.class);
        TestCase.assertEquals(1.0, number);
    }

    public void testInfinity() {
        TestCase.assertTrue(((JsonIterator.deserialize("\"-infinity\"", Double.class)) == (Double.NEGATIVE_INFINITY)));
        TestCase.assertTrue(((JsonIterator.deserialize("\"-infinity\"", Float.class)) == (Float.NEGATIVE_INFINITY)));
        TestCase.assertTrue(((JsonIterator.deserialize("\"infinity\"", Double.class)) == (Double.POSITIVE_INFINITY)));
        TestCase.assertTrue(((JsonIterator.deserialize("\"infinity\"", Float.class)) == (Float.POSITIVE_INFINITY)));
    }
}

