package com.annimon.stream.function;


import BiFunction.Util;
import com.annimon.stream.Functions;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code BiFunction}.
 *
 * @see com.annimon.stream.function.BiFunction
 */
public class BiFunctionTest {
    private static final boolean TO_UPPER = true;

    private static final boolean TO_LOWER = false;

    private static final boolean INCREMENT = true;

    private static final boolean IDENTITY = false;

    @Test
    public void testApplyCharacterToString() {
        Assert.assertEquals("w", BiFunctionTest.toString.apply('w', BiFunctionTest.TO_LOWER));
        Assert.assertEquals("x", BiFunctionTest.toString.apply('w', BiFunctionTest.TO_UPPER));
    }

    @Test
    public void testApplyAsciiToString() {
        Assert.assertEquals("0", BiFunctionTest.toString.apply(((char) (48)), BiFunctionTest.TO_LOWER));
        Assert.assertEquals("1", BiFunctionTest.toString.apply(((char) (48)), BiFunctionTest.TO_UPPER));
    }

    @Test
    public void testApplyChangeCase() {
        Assert.assertEquals("JAVA", BiFunctionTest.changeCase.apply("JAva", BiFunctionTest.TO_UPPER));
        Assert.assertEquals("java", BiFunctionTest.changeCase.apply("JAva", BiFunctionTest.TO_LOWER));
    }

    @Test
    public void testAndThen() {
        BiFunction<Character, Boolean, Integer> function = Util.andThen(BiFunctionTest.toString, Functions.stringToInteger());
        Assert.assertEquals(0, ((int) (function.apply('0', BiFunctionTest.IDENTITY))));
        Assert.assertEquals(1, ((int) (function.apply('0', BiFunctionTest.INCREMENT))));
    }

    @Test
    public void testReverse() {
        BiFunction<Boolean, String, String> function = Util.reverse(BiFunctionTest.changeCase);
        Assert.assertEquals("JAVA", function.apply(BiFunctionTest.TO_UPPER, "JAva"));
        Assert.assertEquals("java", function.apply(BiFunctionTest.TO_LOWER, "JAva"));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static final BiFunction<Character, Boolean, String> toString = new BiFunction<Character, Boolean, String>() {
        @Override
        public String apply(Character value, Boolean increment) {
            final char character = ((char) ((increment) ? value + 1 : value));
            return String.valueOf(character);
        }
    };

    private static final BiFunction<String, Boolean, String> changeCase = new BiFunction<String, Boolean, String>() {
        @Override
        public String apply(String value, Boolean flag) {
            if (flag)
                return value.toUpperCase(Locale.ENGLISH);

            return value.toLowerCase(Locale.ENGLISH);
        }
    };
}

