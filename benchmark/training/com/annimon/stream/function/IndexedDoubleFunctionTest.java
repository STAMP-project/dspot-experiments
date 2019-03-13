package com.annimon.stream.function;


import IndexedDoubleFunction.Util;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedDoubleFunction}.
 *
 * @see IndexedDoubleFunction
 */
public class IndexedDoubleFunctionTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testApply() {
        Assert.assertEquals(new com.annimon.stream.IntPair<String>(1, "4.2"), IndexedDoubleFunctionTest.wrapper.apply(1, 4.2));
        Assert.assertEquals(new com.annimon.stream.IntPair<String>(0, "0.0"), IndexedDoubleFunctionTest.wrapper.apply(0, 0.0));
    }

    @Test
    public void testWrap() {
        IndexedDoubleFunction<String> function = Util.wrap(IndexedDoubleFunctionTest.toString);
        Assert.assertEquals("60.0", function.apply(0, 60.0));
        Assert.assertEquals("-10.0", function.apply(10, (-10.0)));
    }

    private static final DoubleFunction<String> toString = Functions.convertDoubleToString();

    private static final IndexedDoubleFunction<com.annimon.stream.IntPair<String>> wrapper = new IndexedDoubleFunction<com.annimon.stream.IntPair<String>>() {
        @Override
        public com.annimon.stream.IntPair<String> apply(int index, double value) {
            return new com.annimon.stream.IntPair<String>(index, IndexedDoubleFunctionTest.toString.apply(value));
        }
    };
}

