package net.bytebuddy.implementation;


import java.lang.reflect.Field;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class MethodDelegationConstructionTest<T extends CallTraceable> {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final byte BYTE_MULTIPLICATOR = 3;

    private static final short SHORT_MULTIPLICATOR = 3;

    private static final char CHAR_MULTIPLICATOR = 3;

    private static final int INT_MULTIPLICATOR = 3;

    private static final long LONG_MULTIPLICATOR = 3L;

    private static final float FLOAT_MULTIPLICATOR = 3.0F;

    private static final double DOUBLE_MULTIPLICATOR = 3.0;

    private static final boolean DEFAULT_BOOLEAN = false;

    private static final byte DEFAULT_BYTE = 1;

    private static final short DEFAULT_SHORT = 1;

    private static final char DEFAULT_CHAR = 1;

    private static final int DEFAULT_INT = 1;

    private static final long DEFAULT_LONG = 1L;

    private static final float DEFAULT_FLOAT = 1.0F;

    private static final double DEFAULT_DOUBLE = 1.0;

    private final Class<T> sourceType;

    private final Class<?> targetType;

    private final Class<?>[] parameterTypes;

    private final Object[] arguments;

    private final Matcher<?> matcher;

    public MethodDelegationConstructionTest(Class<T> sourceType, Class<?> targetType, Class<?>[] parameterTypes, Object[] arguments, Matcher<?> matcher) {
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
        this.matcher = matcher;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstruction() throws Exception {
        DynamicType.Loaded<T> loaded = new ByteBuddy().subclass(sourceType).method(ElementMatchers.isDeclaredBy(sourceType)).intercept(MethodDelegation.toConstructor(targetType)).make().load(sourceType.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        T instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(sourceType)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(sourceType));
        Object value = loaded.getLoaded().getDeclaredMethod(MethodDelegationConstructionTest.FOO, parameterTypes).invoke(instance, arguments);
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(targetType));
        Field field = targetType.getDeclaredField("value");
        field.setAccessible(true);
        MatcherAssert.assertThat(field.get(value), ((Matcher) (matcher)));
        instance.assertZeroCalls();
    }

    public static class BooleanSource extends CallTraceable {
        @SuppressWarnings("unused")
        public MethodDelegationConstructionTest.BooleanTarget foo(boolean b) {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class BooleanTarget {
        @SuppressWarnings("unused")
        private final boolean value;

        public BooleanTarget(boolean value) {
            this.value = !value;
        }
    }

    public static class ByteSource extends CallTraceable {
        @SuppressWarnings("unused")
        public MethodDelegationConstructionTest.ByteTarget foo(byte b) {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class ByteTarget {
        @SuppressWarnings("unused")
        private final byte value;

        public ByteTarget(byte b) {
            value = MethodDelegationConstructionTest.ByteTarget.bar(b);
        }

        private static byte bar(byte b) {
            return ((byte) (b * (MethodDelegationConstructionTest.BYTE_MULTIPLICATOR)));
        }
    }

    public static class ShortSource extends CallTraceable {
        @SuppressWarnings("unused")
        public MethodDelegationConstructionTest.ShortTarget foo(short s) {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class ShortTarget {
        @SuppressWarnings("unused")
        private final short value;

        public ShortTarget(short s) {
            this.value = MethodDelegationConstructionTest.ShortTarget.bar(s);
        }

        private static short bar(short s) {
            return ((short) (s * (MethodDelegationConstructionTest.SHORT_MULTIPLICATOR)));
        }
    }

    public static class CharSource extends CallTraceable {
        @SuppressWarnings("unused")
        public MethodDelegationConstructionTest.CharTarget foo(char s) {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class CharTarget {
        @SuppressWarnings("unused")
        private final char value;

        public CharTarget(char c) {
            this.value = MethodDelegationConstructionTest.CharTarget.bar(c);
        }

        private static char bar(char c) {
            return ((char) (c * (MethodDelegationConstructionTest.CHAR_MULTIPLICATOR)));
        }
    }

    public static class IntSource extends CallTraceable {
        @SuppressWarnings("unused")
        public MethodDelegationConstructionTest.IntTarget foo(int i) {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class IntTarget {
        @SuppressWarnings("unused")
        private final int value;

        public IntTarget(int i) {
            this.value = MethodDelegationConstructionTest.IntTarget.bar(i);
        }

        private static int bar(int i) {
            return i * (MethodDelegationConstructionTest.INT_MULTIPLICATOR);
        }
    }

    public static class LongSource extends CallTraceable {
        @SuppressWarnings("unused")
        public MethodDelegationConstructionTest.LongTarget foo(long l) {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class LongTarget {
        @SuppressWarnings("unused")
        private final long value;

        public LongTarget(long l) {
            this.value = MethodDelegationConstructionTest.LongTarget.bar(l);
        }

        private static long bar(long l) {
            return l * (MethodDelegationConstructionTest.LONG_MULTIPLICATOR);
        }
    }

    public static class FloatSource extends CallTraceable {
        @SuppressWarnings("unused")
        public MethodDelegationConstructionTest.FloatTarget foo(float f) {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class FloatTarget {
        @SuppressWarnings("unused")
        private final float value;

        public FloatTarget(float f) {
            this.value = MethodDelegationConstructionTest.FloatTarget.bar(f);
        }

        private static float bar(float f) {
            return f * (MethodDelegationConstructionTest.FLOAT_MULTIPLICATOR);
        }
    }

    public static class DoubleSource extends CallTraceable {
        @SuppressWarnings("unused")
        public MethodDelegationConstructionTest.DoubleTarget foo(double d) {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class DoubleTarget {
        @SuppressWarnings("unused")
        private final double value;

        public DoubleTarget(double d) {
            this.value = MethodDelegationConstructionTest.DoubleTarget.bar(d);
        }

        public static double bar(double d) {
            return d * (MethodDelegationConstructionTest.DOUBLE_MULTIPLICATOR);
        }
    }

    public static class VoidSource extends CallTraceable {
        public MethodDelegationConstructionTest.VoidTarget foo() {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class VoidTarget {
        @SuppressWarnings("unused")
        private final Void value = null;
    }

    public static class StringSource extends CallTraceable {
        public MethodDelegationConstructionTest.StringTarget foo(String s) {
            register(MethodDelegationConstructionTest.FOO);
            return null;
        }
    }

    public static class StringTarget {
        @SuppressWarnings("unused")
        private final String value;

        public StringTarget(String s) {
            this.value = MethodDelegationConstructionTest.StringTarget.bar(s);
        }

        public static String bar(String s) {
            return s + (MethodDelegationConstructionTest.BAR);
        }
    }
}

