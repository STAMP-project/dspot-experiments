package com.annimon.stream.function;


import IntFunction.Util;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IntFunction}.
 *
 * @see IntFunction
 */
public class IntFunctionTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testSafe() {
        IntFunction<String> function = Util.<String>safe(new IntFunctionTest.UnsafeFunction());
        Assert.assertThat(function.apply(10), Matchers.is("10"));
        Assert.assertThat(function.apply((-5)), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testSafeWithResultIfFailed() {
        IntFunction<String> function = Util.safe(new IntFunctionTest.UnsafeFunction(), "default");
        Assert.assertThat(function.apply(10), Matchers.is("10"));
        Assert.assertThat(function.apply((-5)), Matchers.is("default"));
    }

    private static class UnsafeFunction implements ThrowableIntFunction<String, Throwable> {
        @Override
        public String apply(int value) throws IOException {
            if (value < 0) {
                throw new IOException();
            }
            return Integer.toString(value);
        }
    }
}

