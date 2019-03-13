package com.annimon.stream.function;


import IndexedBiFunction.Util;
import com.annimon.stream.Functions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code IndexedBiFunction}.
 *
 * @see com.annimon.stream.function.IndexedBiFunction
 */
public class IndexedBiFunctionTest {
    private static final boolean TO_UPPER = true;

    private static final boolean TO_LOWER = false;

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testApply() {
        Assert.assertEquals(Character.valueOf('Y'), IndexedBiFunctionTest.charPlusIndexChangeCase.apply(1, 'x', IndexedBiFunctionTest.TO_UPPER));
        Assert.assertEquals(Character.valueOf('z'), IndexedBiFunctionTest.charPlusIndexChangeCase.apply(2, 'X', IndexedBiFunctionTest.TO_LOWER));
    }

    @Test
    public void testWrap() {
        final IndexedBiFunction<Integer, Integer, Integer> addition = Util.wrap(Functions.addition());
        Assert.assertEquals(Integer.valueOf(20), addition.apply(0, 10, 10));
        Assert.assertEquals(Integer.valueOf(10), addition.apply(1000, 0, 10));
    }

    private static final IndexedBiFunction<Character, Boolean, Character> charPlusIndexChangeCase = new IndexedBiFunction<Character, Boolean, Character>() {
        @Override
        public Character apply(int index, Character value, Boolean flag) {
            final char ch = ((char) (index + value));
            if (flag)
                return Character.toUpperCase(ch);

            return Character.toLowerCase(ch);
        }
    };
}

