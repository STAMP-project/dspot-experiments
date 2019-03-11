package com.annimon.stream.function;


import IndexedFunction.Util;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedFunction}.
 *
 * @see com.annimon.stream.function.IndexedFunction
 */
public class IndexedFunctionTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testApply() {
        Assert.assertEquals("x", IndexedFunctionTest.charPlusIndexToString.apply(1, 'w'));
        Assert.assertEquals("k", IndexedFunctionTest.charPlusIndexToString.apply(10, 'a'));
        Assert.assertEquals("5", IndexedFunctionTest.charPlusIndexToString.apply((-1), '6'));
    }

    @Test
    public void testWrap() {
        IndexedFunction<Object, String> function = Util.wrap(Functions.convertToString());
        Assert.assertEquals("60", function.apply(0, 60));
        Assert.assertEquals("A", function.apply(10, ((char) (65))));
    }

    private static final IndexedFunction<Character, String> charPlusIndexToString = new IndexedFunction<Character, String>() {
        @Override
        public String apply(int index, Character t) {
            return Character.toString(((char) (t + index)));
        }
    };
}

