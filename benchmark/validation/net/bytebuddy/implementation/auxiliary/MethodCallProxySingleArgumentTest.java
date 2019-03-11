package net.bytebuddy.implementation.auxiliary;


import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;
import net.bytebuddy.test.utility.CallTraceable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MethodCallProxySingleArgumentTest<T extends CallTraceable> extends AbstractMethodCallProxyTest {
    private static final String STRING_VALUE = "foo";

    private static final boolean BOOLEAN_VALUE = true;

    private static final byte BYTE_VALUE = 42;

    private static final short SHORT_VALUE = 42;

    private static final char CHAR_VALUE = '@';

    private static final int INT_VALUE = 42;

    private static final long LONG_VALUE = 42L;

    private static final float FLOAT_VALUE = 42.0F;

    private static final double DOUBLE_VALUE = 42.0;

    private static final Object NULL_VALUE = null;

    private final Object value;

    private final Class<T> targetType;

    private final Class<?> valueType;

    public MethodCallProxySingleArgumentTest(Object value, Class<T> targetType, Class<?> valueType) {
        this.value = value;
        this.targetType = targetType;
        this.valueType = valueType;
    }

    @Test
    public void testRunMethod() throws Exception {
        Class<?> auxiliaryType = proxyOnlyDeclaredMethodOf(targetType);
        Constructor<?> constructor = auxiliaryType.getDeclaredConstructor(targetType, valueType);
        constructor.setAccessible(true);
        T proxiedInstance = targetType.getDeclaredConstructor().newInstance();
        ((Runnable) (constructor.newInstance(proxiedInstance, value))).run();
        proxiedInstance.assertOnlyCall(AbstractMethodCallProxyTest.FOO, value);
    }

    @Test
    public void testCallMethod() throws Exception {
        Class<?> auxiliaryType = proxyOnlyDeclaredMethodOf(targetType);
        Constructor<?> constructor = auxiliaryType.getDeclaredConstructor(targetType, valueType);
        constructor.setAccessible(true);
        T proxiedInstance = targetType.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(((Callable<?>) (constructor.newInstance(proxiedInstance, value))).call(), CoreMatchers.is(value));
        proxiedInstance.assertOnlyCall(AbstractMethodCallProxyTest.FOO, value);
    }

    @SuppressWarnings("unused")
    public static class StringTarget extends CallTraceable {
        public String foo(String s) {
            register(AbstractMethodCallProxyTest.FOO, s);
            return s;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanTarget extends CallTraceable {
        public boolean foo(boolean b) {
            register(AbstractMethodCallProxyTest.FOO, b);
            return b;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteTarget extends CallTraceable {
        public byte foo(byte b) {
            register(AbstractMethodCallProxyTest.FOO, b);
            return b;
        }
    }

    @SuppressWarnings("unused")
    public static class ShortTarget extends CallTraceable {
        public short foo(short s) {
            register(AbstractMethodCallProxyTest.FOO, s);
            return s;
        }
    }

    @SuppressWarnings("unused")
    public static class CharTarget extends CallTraceable {
        public char foo(char c) {
            register(AbstractMethodCallProxyTest.FOO, c);
            return c;
        }
    }

    @SuppressWarnings("unused")
    public static class IntTarget extends CallTraceable {
        public int foo(int i) {
            register(AbstractMethodCallProxyTest.FOO, i);
            return i;
        }
    }

    @SuppressWarnings("unused")
    public static class LongTarget extends CallTraceable {
        public long foo(long l) {
            register(AbstractMethodCallProxyTest.FOO, l);
            return l;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatTarget extends CallTraceable {
        public float foo(float f) {
            register(AbstractMethodCallProxyTest.FOO, f);
            return f;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleTarget extends CallTraceable {
        public double foo(double d) {
            register(AbstractMethodCallProxyTest.FOO, d);
            return d;
        }
    }

    @SuppressWarnings("unused")
    public static class NullTarget extends CallTraceable {
        public void foo(Void v) {
            register(AbstractMethodCallProxyTest.FOO, v);
        }
    }
}

