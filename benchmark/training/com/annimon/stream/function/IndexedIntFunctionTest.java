package com.annimon.stream.function;


import IndexedIntFunction.Util;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedIntFunction}.
 *
 * @see IndexedIntFunction
 */
public class IndexedIntFunctionTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testApply() {
        Assert.assertEquals("4", IndexedIntFunctionTest.multiplyEvaluator.apply(2, 2));
        Assert.assertEquals("-20", IndexedIntFunctionTest.multiplyEvaluator.apply(10, (-2)));
        Assert.assertEquals("0", IndexedIntFunctionTest.multiplyEvaluator.apply((-1), 0));
    }

    @Test
    public void testWrap() {
        IndexedIntFunction<String> function = Util.wrap(Functions.convertIntToString());
        Assert.assertEquals("60", function.apply(0, 60));
        Assert.assertEquals("-10", function.apply(10, (-10)));
    }

    private static final IndexedIntFunction<String> multiplyEvaluator = new IndexedIntFunction<String>() {
        @Override
        public String apply(int first, int second) {
            return String.valueOf((first * second));
        }
    };
}

