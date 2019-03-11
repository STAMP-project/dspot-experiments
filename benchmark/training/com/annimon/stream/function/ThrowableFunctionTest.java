package com.annimon.stream.function;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code ThrowableFunction}.
 *
 * @see com.annimon.stream.function.ThrowableFunction
 */
public class ThrowableFunctionTest {
    @Test
    public void testApply() {
        Assert.assertEquals(100, ((int) (ThrowableFunctionTest.toInt.apply("100"))));
    }

    @Test(expected = NumberFormatException.class)
    public void testApplyWithRuntimeException() {
        ThrowableFunctionTest.toInt.apply("oops");
    }

    private static final ThrowableFunction<String, Integer, NumberFormatException> toInt = new ThrowableFunction<String, Integer, NumberFormatException>() {
        @Override
        public Integer apply(String value) throws NumberFormatException {
            return Integer.parseInt(value);
        }
    };
}

