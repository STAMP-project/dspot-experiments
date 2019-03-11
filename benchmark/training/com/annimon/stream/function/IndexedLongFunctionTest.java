package com.annimon.stream.function;


import IndexedLongFunction.Util;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedLongFunction}.
 *
 * @see IndexedLongFunction
 */
public class IndexedLongFunctionTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testApply() {
        Assert.assertEquals(new com.annimon.stream.IntPair<String>(1, "42"), IndexedLongFunctionTest.wrapper.apply(1, 42L));
        Assert.assertEquals(new com.annimon.stream.IntPair<String>(0, "0"), IndexedLongFunctionTest.wrapper.apply(0, 0L));
    }

    @Test
    public void testWrap() {
        IndexedLongFunction<String> function = Util.wrap(IndexedLongFunctionTest.toString);
        Assert.assertEquals("60", function.apply(0, 60L));
        Assert.assertEquals("-10", function.apply(10, (-10L)));
    }

    private static final LongFunction<String> toString = Functions.convertLongToString();

    private static final IndexedLongFunction<com.annimon.stream.IntPair<String>> wrapper = new IndexedLongFunction<com.annimon.stream.IntPair<String>>() {
        @Override
        public com.annimon.stream.IntPair<String> apply(int index, long value) {
            return new com.annimon.stream.IntPair<String>(index, IndexedLongFunctionTest.toString.apply(value));
        }
    };
}

