package com.annimon.stream.function;


import DoubleFunction.Util;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code DoubleFunction}.
 *
 * @see DoubleFunction
 */
public class DoubleFunctionTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testSafe() {
        DoubleFunction<String> function = Util.<String>safe(new DoubleFunctionTest.UnsafeFunction());
        Assert.assertThat(function.apply(10.36), Matchers.is("10.0"));
        Assert.assertThat(function.apply((-5.9)), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testSafeWithResultIfFailed() {
        DoubleFunction<String> function = Util.safe(new DoubleFunctionTest.UnsafeFunction(), "default");
        Assert.assertThat(function.apply(10.36), Matchers.is("10.0"));
        Assert.assertThat(function.apply((-5.9)), Matchers.is("default"));
    }

    private static class UnsafeFunction implements ThrowableDoubleFunction<String, Throwable> {
        @Override
        public String apply(double value) throws IOException {
            if (value < 0) {
                throw new IOException();
            }
            return Double.toString(Math.floor(value));
        }
    }
}

