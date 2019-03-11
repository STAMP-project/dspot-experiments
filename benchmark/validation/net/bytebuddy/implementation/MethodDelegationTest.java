package net.bytebuddy.implementation;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
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
public class MethodDelegationTest<T extends CallTraceable> {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

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

    public MethodDelegationTest(Class<T> sourceType, Class<?> targetType, Class<?>[] parameterTypes, Object[] arguments, Matcher<?> matcher) {
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
        this.matcher = matcher;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStaticMethodBinding() throws Exception {
        DynamicType.Loaded<T> loaded = new ByteBuddy().subclass(sourceType).method(ElementMatchers.isDeclaredBy(sourceType)).intercept(MethodDelegation.to(targetType)).make().load(sourceType.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        T instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(sourceType)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(sourceType));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodDelegationTest.FOO, parameterTypes).invoke(instance, arguments), ((Matcher) (matcher)));
        instance.assertZeroCalls();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStaticFieldBinding() throws Exception {
        DynamicType.Loaded<T> loaded = new ByteBuddy().subclass(sourceType).method(ElementMatchers.isDeclaredBy(sourceType)).intercept(MethodDelegation.withDefaultConfiguration().filter(ElementMatchers.isDeclaredBy(targetType)).to(targetType.getDeclaredConstructor().newInstance())).make().load(sourceType.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        T instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(sourceType)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(sourceType));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodDelegationTest.FOO, parameterTypes).invoke(instance, arguments), ((Matcher) (matcher)));
        instance.assertZeroCalls();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInstanceFieldBinding() throws Exception {
        DynamicType.Loaded<T> loaded = new ByteBuddy().subclass(sourceType).defineField(MethodDelegationTest.QUX, targetType, Visibility.PUBLIC).method(ElementMatchers.isDeclaredBy(sourceType)).intercept(MethodDelegation.withDefaultConfiguration().filter(ElementMatchers.isDeclaredBy(targetType)).toField(MethodDelegationTest.QUX)).make().load(sourceType.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        T instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Field field = loaded.getLoaded().getDeclaredField(MethodDelegationTest.QUX);
        MatcherAssert.assertThat(field.getModifiers(), CoreMatchers.is(Modifier.PUBLIC));
        MatcherAssert.assertThat(field.getType(), CoreMatchers.<Class<?>>is(targetType));
        field.setAccessible(true);
        field.set(instance, targetType.getDeclaredConstructor().newInstance());
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(sourceType)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(sourceType));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodDelegationTest.FOO, parameterTypes).invoke(instance, arguments), ((Matcher) (matcher)));
        instance.assertZeroCalls();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodReturnBinding() throws Exception {
        DynamicType.Loaded<T> loaded = new ByteBuddy().subclass(sourceType).defineField(MethodDelegationTest.QUX, targetType, Visibility.PUBLIC).defineMethod(MethodDelegationTest.QUX, targetType, Visibility.PUBLIC).intercept(FieldAccessor.ofField(MethodDelegationTest.QUX)).method(ElementMatchers.isDeclaredBy(sourceType)).intercept(toMethodReturnOf(MethodDelegationTest.QUX)).make().load(sourceType.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        T instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Field field = loaded.getLoaded().getDeclaredField(MethodDelegationTest.QUX);
        MatcherAssert.assertThat(field.getModifiers(), CoreMatchers.is(Modifier.PUBLIC));
        MatcherAssert.assertThat(field.getType(), CoreMatchers.<Class<?>>is(targetType));
        field.setAccessible(true);
        field.set(instance, targetType.getDeclaredConstructor().newInstance());
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(sourceType)));
        MatcherAssert.assertThat(instance, CoreMatchers.instanceOf(sourceType));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethod(MethodDelegationTest.FOO, parameterTypes).invoke(instance, arguments), ((Matcher) (matcher)));
        instance.assertZeroCalls();
    }

    public static class BooleanSource extends CallTraceable {
        public boolean foo(boolean b) {
            register(MethodDelegationTest.FOO);
            return b;
        }
    }

    public static class BooleanTarget {
        public static boolean bar(boolean b) {
            return !b;
        }

        public boolean qux(boolean b) {
            return MethodDelegationTest.BooleanTarget.bar(b);
        }
    }

    public static class ByteSource extends CallTraceable {
        public byte foo(byte b) {
            register(MethodDelegationTest.FOO);
            return b;
        }
    }

    public static class ByteTarget {
        public static byte bar(byte b) {
            return ((byte) (b * (MethodDelegationTest.BYTE_MULTIPLICATOR)));
        }

        public byte qux(byte b) {
            return MethodDelegationTest.ByteTarget.bar(b);
        }
    }

    public static class ShortSource extends CallTraceable {
        public short foo(short s) {
            register(MethodDelegationTest.FOO);
            return s;
        }
    }

    public static class ShortTarget {
        public static short bar(short s) {
            return ((short) (s * (MethodDelegationTest.SHORT_MULTIPLICATOR)));
        }

        public short qux(short s) {
            return MethodDelegationTest.ShortTarget.bar(s);
        }
    }

    public static class CharSource extends CallTraceable {
        public char foo(char s) {
            register(MethodDelegationTest.FOO);
            return s;
        }
    }

    public static class CharTarget {
        public static char bar(char c) {
            return ((char) (c * (MethodDelegationTest.CHAR_MULTIPLICATOR)));
        }

        public char qux(char c) {
            return MethodDelegationTest.CharTarget.bar(c);
        }
    }

    public static class IntSource extends CallTraceable {
        public int foo(int i) {
            register(MethodDelegationTest.FOO);
            return i;
        }
    }

    public static class IntTarget {
        public static int bar(int i) {
            return i * (MethodDelegationTest.INT_MULTIPLICATOR);
        }

        public int qux(int i) {
            return MethodDelegationTest.IntTarget.bar(i);
        }
    }

    public static class LongSource extends CallTraceable {
        public long foo(long l) {
            register(MethodDelegationTest.FOO);
            return l;
        }
    }

    public static class LongTarget {
        public static long bar(long l) {
            return l * (MethodDelegationTest.LONG_MULTIPLICATOR);
        }

        public long qux(long l) {
            return MethodDelegationTest.LongTarget.bar(l);
        }
    }

    public static class FloatSource extends CallTraceable {
        public float foo(float f) {
            register(MethodDelegationTest.FOO);
            return f;
        }
    }

    public static class FloatTarget {
        public static float bar(float f) {
            return f * (MethodDelegationTest.FLOAT_MULTIPLICATOR);
        }

        public float qux(float f) {
            return MethodDelegationTest.FloatTarget.bar(f);
        }
    }

    public static class DoubleSource extends CallTraceable {
        public double foo(double d) {
            register(MethodDelegationTest.FOO);
            return d;
        }
    }

    public static class DoubleTarget {
        public static double bar(double d) {
            return d * (MethodDelegationTest.DOUBLE_MULTIPLICATOR);
        }

        public double qux(double d) {
            return MethodDelegationTest.DoubleTarget.bar(d);
        }
    }

    public static class VoidSource extends CallTraceable {
        public void foo() {
            register(MethodDelegationTest.FOO);
        }
    }

    public static class VoidTarget {
        public static void bar() {
            /* empty */
        }

        public void qux() {
            MethodDelegationTest.VoidTarget.bar();
        }
    }

    public static class StringSource extends CallTraceable {
        public String foo(String s) {
            register(MethodDelegationTest.FOO);
            return s;
        }
    }

    public static class StringTarget {
        public static String bar(String s) {
            return s + (MethodDelegationTest.BAR);
        }

        public String qux(String s) {
            return MethodDelegationTest.StringTarget.bar(s);
        }
    }
}

