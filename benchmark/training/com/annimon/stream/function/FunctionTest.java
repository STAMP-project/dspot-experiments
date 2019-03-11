package com.annimon.stream.function;


import Function.Util;
import com.annimon.stream.Functions;
import java.io.IOException;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code Function}.
 *
 * @see com.annimon.stream.function.Function
 */
public class FunctionTest {
    @Test
    public void testApplyCharacterToString() {
        Assert.assertEquals("w", FunctionTest.toString.apply('w'));
        Assert.assertEquals("0", FunctionTest.toString.apply(((char) (48))));
    }

    @Test
    public void testApplyToUpperCase() {
        Assert.assertEquals("JAVA", FunctionTest.toUpperCase.apply("java"));
    }

    @Test
    public void testAndThen() {
        Function<Character, String> function = Util.andThen(FunctionTest.toString, FunctionTest.toUpperCase);
        Assert.assertEquals("W", function.apply('w'));
        Assert.assertEquals("A", function.apply(((char) (65))));
    }

    @Test
    public void testCompose() {
        Function<Character, String> function = Util.compose(FunctionTest.toUpperCase, FunctionTest.toString);
        Assert.assertEquals("W", function.apply('w'));
        Assert.assertEquals("A", function.apply(((char) (65))));
    }

    @Test
    public void testSafe() {
        Function<Boolean, Integer> function = Util.safe(new ThrowableFunction<Boolean, Integer, Throwable>() {
            @Override
            public Integer apply(Boolean value) throws IOException {
                return FunctionTest.unsafeFunction(value);
            }
        });
        Assert.assertEquals(10, ((int) (function.apply(false))));
        Assert.assertNull(function.apply(true));
    }

    @Test
    public void testSafeWithResultIfFailed() {
        Function<Object, String> function = Util.safe(new ThrowableFunction<Object, String, Throwable>() {
            @Override
            public String apply(Object value) {
                return value.toString();
            }
        }, "default");
        Assert.assertEquals("10", function.apply(10));
        Assert.assertEquals("default", function.apply(null));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(Util.class, hasOnlyPrivateConstructors());
    }

    private static final Function<Character, String> toString = Functions.<Character>convertToString();

    private static final Function<String, String> toUpperCase = new Function<String, String>() {
        @Override
        public String apply(String value) {
            return value.toUpperCase(Locale.ENGLISH);
        }
    };
}

