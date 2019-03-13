package com.annimon.stream.function;


import LongFunction.Util;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code LongFunction}.
 *
 * @see LongFunction
 */
public class LongFunctionTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testSafe() {
        LongFunction<String> function = Util.<String>safe(new LongFunctionTest.UnsafeFunction());
        Assert.assertThat(function.apply(10L), Matchers.is("10"));
        Assert.assertThat(function.apply((-5L)), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testSafeWithResultIfFailed() {
        LongFunction<String> function = Util.safe(new LongFunctionTest.UnsafeFunction(), "default");
        Assert.assertThat(function.apply(10L), Matchers.is("10"));
        Assert.assertThat(function.apply((-5L)), Matchers.is("default"));
    }

    private static class UnsafeFunction implements ThrowableLongFunction<String, Throwable> {
        @Override
        public String apply(long value) throws IOException {
            if (value < 0) {
                throw new IOException();
            }
            return Long.toString(value);
        }
    }
}

