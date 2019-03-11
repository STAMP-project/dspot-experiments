package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class FixedValueConstantPoolTypesTest<T extends CallTraceable> {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String STRING_VALUE = "foo";

    private static final boolean BOOLEAN_VALUE = true;

    private static final byte BYTE_VALUE = 42;

    private static final short SHORT_VALUE = 42;

    private static final char CHAR_VALUE = '@';

    private static final int INT_VALUE = 42;

    private static final long LONG_VALUE = 42L;

    private static final float FLOAT_VALUE = 42.0F;

    private static final double DOUBLE_VALUE = 42.0;

    private static final Void NULL_VALUE = null;

    private static final String STRING_DEFAULT_VALUE = "bar";

    private static final boolean BOOLEAN_DEFAULT_VALUE = false;

    private static final byte BYTE_DEFAULT_VALUE = 0;

    private static final short SHORT_DEFAULT_VALUE = 0;

    private static final char CHAR_DEFAULT_VALUE = 0;

    private static final int INT_DEFAULT_VALUE = 0;

    private static final long LONG_DEFAULT_VALUE = 0L;

    private static final float FLOAT_DEFAULT_VALUE = 0.0F;

    private static final double DOUBLE_DEFAULT_VALUE = 0.0;

    private final Object fixedValue;

    private final Class<T> helperClass;

    public FixedValueConstantPoolTypesTest(Object fixedValue, Class<T> helperClass) {
        this.fixedValue = fixedValue;
        this.helperClass = helperClass;
    }

    @Test
    public void testConstantPool() throws Exception {
        DynamicType.Loaded<T> loaded = new ByteBuddy().subclass(helperClass).method(ElementMatchers.isDeclaredBy(helperClass)).intercept(FixedValue.value(fixedValue)).make().load(helperClass.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        T instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(FixedValueConstantPoolTypesTest.StringTarget.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(helperClass));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(FixedValueConstantPoolTypesTest.FOO).invoke(instance), CoreMatchers.is(fixedValue));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(FixedValueConstantPoolTypesTest.BAR).invoke(instance), CoreMatchers.is(fixedValue));
        instance.assertZeroCalls();
    }

    @Test
    public void testStaticField() throws Exception {
        DynamicType.Loaded<T> loaded = new ByteBuddy().subclass(helperClass).method(ElementMatchers.isDeclaredBy(helperClass)).intercept(FixedValue.reference(fixedValue)).make().load(helperClass.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(((fixedValue) == null ? 0 : 1)));
        T instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(FixedValueConstantPoolTypesTest.StringTarget.class)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(helperClass));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(FixedValueConstantPoolTypesTest.FOO).invoke(instance), CoreMatchers.is(fixedValue));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(FixedValueConstantPoolTypesTest.BAR).invoke(instance), CoreMatchers.is(fixedValue));
        instance.assertZeroCalls();
    }

    @SuppressWarnings("unused")
    public static class StringTarget extends CallTraceable {
        public String foo() {
            register(FixedValueConstantPoolTypesTest.FOO);
            return FixedValueConstantPoolTypesTest.STRING_DEFAULT_VALUE;
        }

        public Object bar() {
            register(FixedValueConstantPoolTypesTest.BAR);
            return FixedValueConstantPoolTypesTest.STRING_DEFAULT_VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanTarget extends CallTraceable {
        public boolean foo() {
            register(FixedValueConstantPoolTypesTest.FOO);
            return FixedValueConstantPoolTypesTest.BOOLEAN_DEFAULT_VALUE;
        }

        public Boolean bar() {
            register(FixedValueConstantPoolTypesTest.BAR);
            return FixedValueConstantPoolTypesTest.BOOLEAN_DEFAULT_VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteTarget extends CallTraceable {
        public byte foo() {
            register(FixedValueConstantPoolTypesTest.FOO);
            return FixedValueConstantPoolTypesTest.BYTE_DEFAULT_VALUE;
        }

        public Byte bar() {
            register(FixedValueConstantPoolTypesTest.BAR);
            return FixedValueConstantPoolTypesTest.BYTE_DEFAULT_VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class ShortTarget extends CallTraceable {
        public short foo() {
            register(FixedValueConstantPoolTypesTest.FOO);
            return FixedValueConstantPoolTypesTest.SHORT_DEFAULT_VALUE;
        }

        public Short bar() {
            register(FixedValueConstantPoolTypesTest.BAR);
            return FixedValueConstantPoolTypesTest.SHORT_DEFAULT_VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class CharTarget extends CallTraceable {
        public char foo() {
            register(FixedValueConstantPoolTypesTest.FOO);
            return FixedValueConstantPoolTypesTest.CHAR_DEFAULT_VALUE;
        }

        public Character bar() {
            register(FixedValueConstantPoolTypesTest.BAR);
            return FixedValueConstantPoolTypesTest.CHAR_DEFAULT_VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class IntTarget extends CallTraceable {
        public int foo() {
            register(FixedValueConstantPoolTypesTest.FOO);
            return FixedValueConstantPoolTypesTest.INT_DEFAULT_VALUE;
        }

        public Integer bar() {
            register(FixedValueConstantPoolTypesTest.BAR);
            return FixedValueConstantPoolTypesTest.INT_DEFAULT_VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class LongTarget extends CallTraceable {
        public long foo() {
            register(FixedValueConstantPoolTypesTest.FOO);
            return FixedValueConstantPoolTypesTest.LONG_DEFAULT_VALUE;
        }

        public Long bar() {
            register(FixedValueConstantPoolTypesTest.BAR);
            return FixedValueConstantPoolTypesTest.LONG_DEFAULT_VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatTarget extends CallTraceable {
        public float foo() {
            register(FixedValueConstantPoolTypesTest.FOO);
            return FixedValueConstantPoolTypesTest.FLOAT_DEFAULT_VALUE;
        }

        public Float bar() {
            register(FixedValueConstantPoolTypesTest.BAR);
            return FixedValueConstantPoolTypesTest.FLOAT_DEFAULT_VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleTarget extends CallTraceable {
        public double foo() {
            register(FixedValueConstantPoolTypesTest.FOO);
            return FixedValueConstantPoolTypesTest.DOUBLE_DEFAULT_VALUE;
        }

        public Double bar() {
            register(FixedValueConstantPoolTypesTest.BAR);
            return FixedValueConstantPoolTypesTest.DOUBLE_DEFAULT_VALUE;
        }
    }
}

