package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class AdviceSizeConversionTest {
    private static final String FOO = "foo";

    private static final int NUMERIC = 42;

    private final Class<?> target;

    private final Class<?> parameter;

    private final Object input;

    private final Object output;

    public AdviceSizeConversionTest(Class<?> target, Class<?> parameter, Object input, Object output) {
        this.target = target;
        this.parameter = parameter;
        this.input = input;
        this.output = output;
    }

    @Test
    public void testAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(target).visit(Advice.to(AdviceSizeConversionTest.class).on(ElementMatchers.named(AdviceSizeConversionTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceSizeConversionTest.FOO, parameter).invoke(type.getDeclaredConstructor().newInstance(), input), CoreMatchers.is(output));
    }

    public static class IntToFloat {
        public float foo(int value) {
            return ((float) (value));
        }
    }

    public static class IntToLong {
        public long foo(int value) {
            return ((long) (value));
        }
    }

    public static class IntToDouble {
        public double foo(int value) {
            return ((double) (value));
        }
    }

    public static class FloatToInt {
        public int foo(float value) {
            return ((int) (value));
        }
    }

    public static class FloatToLong {
        public long foo(float value) {
            return ((long) (value));
        }
    }

    public static class FloatToDouble {
        public double foo(float value) {
            return ((double) (value));
        }
    }

    public static class LongToInt {
        public int foo(long value) {
            return ((int) (value));
        }
    }

    public static class LongToFloat {
        public float foo(long value) {
            return ((float) (value));
        }
    }

    public static class LongToDouble {
        public double foo(long value) {
            return ((double) (value));
        }
    }

    public static class DoubleToInt {
        public int foo(double value) {
            return ((int) (value));
        }
    }

    public static class DoubleToFloat {
        public float foo(double value) {
            return ((float) (value));
        }
    }

    public static class DoubleToLong {
        public long foo(double value) {
            return ((long) (value));
        }
    }
}

