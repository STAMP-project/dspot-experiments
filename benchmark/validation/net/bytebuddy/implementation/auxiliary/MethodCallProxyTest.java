package net.bytebuddy.implementation.auxiliary;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import net.bytebuddy.test.utility.CallTraceable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class MethodCallProxyTest extends AbstractMethodCallProxyTest {
    private static final long LONG_VALUE = 42L;

    private static final String String_VALUE = "BAR";

    private static final int INT_VALUE = 21;

    private static final boolean BOOLEAN_VALUE = true;

    @Test
    public void testNoParameterMethod() throws Exception {
        Class<?> auxiliaryType = proxyOnlyDeclaredMethodOf(MethodCallProxyTest.NoParameterMethod.class);
        Constructor<?> constructor = auxiliaryType.getDeclaredConstructor(MethodCallProxyTest.NoParameterMethod.class);
        constructor.setAccessible(true);
        MethodCallProxyTest.NoParameterMethod runnableProxied = new MethodCallProxyTest.NoParameterMethod();
        ((Runnable) (constructor.newInstance(runnableProxied))).run();
        runnableProxied.assertOnlyCall(AbstractMethodCallProxyTest.FOO, runnableProxied);
        MethodCallProxyTest.NoParameterMethod callableProxied = new MethodCallProxyTest.NoParameterMethod();
        MatcherAssert.assertThat(((Callable<?>) (constructor.newInstance(callableProxied))).call(), CoreMatchers.nullValue());
        callableProxied.assertOnlyCall(AbstractMethodCallProxyTest.FOO, callableProxied);
    }

    @Test
    public void testStaticMethod() throws Exception {
        Class<?> auxiliaryType = proxyOnlyDeclaredMethodOf(MethodCallProxyTest.StaticMethod.class);
        Constructor<?> constructor = auxiliaryType.getDeclaredConstructor();
        constructor.setAccessible(true);
        ((Runnable) (constructor.newInstance())).run();
        MethodCallProxyTest.StaticMethod.CALL_TRACEABLE.assertOnlyCall(AbstractMethodCallProxyTest.FOO);
        MethodCallProxyTest.StaticMethod.CALL_TRACEABLE.reset();
        MatcherAssert.assertThat(((Callable<?>) (constructor.newInstance())).call(), CoreMatchers.nullValue());
        MethodCallProxyTest.StaticMethod.CALL_TRACEABLE.assertOnlyCall(AbstractMethodCallProxyTest.FOO);
        MethodCallProxyTest.StaticMethod.CALL_TRACEABLE.reset();
    }

    @Test
    public void testMultipleParameterMethod() throws Exception {
        Class<?> auxiliaryType = proxyOnlyDeclaredMethodOf(MethodCallProxyTest.MultipleParameterMethod.class);
        Constructor<?> constructor = auxiliaryType.getDeclaredConstructor(MethodCallProxyTest.MultipleParameterMethod.class, long.class, String.class, int.class, boolean.class);
        constructor.setAccessible(true);
        MethodCallProxyTest.MultipleParameterMethod runnableProxied = new MethodCallProxyTest.MultipleParameterMethod();
        Object[] runnableArguments = new Object[]{ runnableProxied, MethodCallProxyTest.LONG_VALUE, MethodCallProxyTest.String_VALUE, MethodCallProxyTest.INT_VALUE, MethodCallProxyTest.BOOLEAN_VALUE };
        ((Runnable) (constructor.newInstance(runnableArguments))).run();
        runnableProxied.assertOnlyCall(AbstractMethodCallProxyTest.FOO, runnableArguments);
        MethodCallProxyTest.MultipleParameterMethod callableProxied = new MethodCallProxyTest.MultipleParameterMethod();
        Object[] callableArguments = new Object[]{ callableProxied, MethodCallProxyTest.LONG_VALUE, MethodCallProxyTest.String_VALUE, MethodCallProxyTest.INT_VALUE, MethodCallProxyTest.BOOLEAN_VALUE };
        MatcherAssert.assertThat(((Callable<?>) (constructor.newInstance(callableArguments))).call(), CoreMatchers.nullValue());
        callableProxied.assertOnlyCall(AbstractMethodCallProxyTest.FOO, callableArguments);
    }

    @Test
    public void testNonGenericParameter() throws Exception {
        Class<?> auxiliaryType = proxyOnlyDeclaredMethodOf(MethodCallProxyTest.GenericType.class);
        MatcherAssert.assertThat(auxiliaryType.getTypeParameters().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(auxiliaryType.getDeclaredMethod("call").getGenericReturnType(), CoreMatchers.is(((Type) (Object.class))));
        MatcherAssert.assertThat(auxiliaryType.getDeclaredFields()[1].getGenericType(), CoreMatchers.is(((Type) (Object.class))));
        MatcherAssert.assertThat(auxiliaryType.getDeclaredFields()[2].getGenericType(), CoreMatchers.is(((Type) (Number.class))));
    }

    @SuppressWarnings("unused")
    public static class NoParameterMethod extends CallTraceable {
        public void foo() {
            register(AbstractMethodCallProxyTest.FOO, this);
        }
    }

    @SuppressWarnings("unused")
    public static class StaticMethod extends CallTraceable {
        public static final CallTraceable CALL_TRACEABLE = new CallTraceable();

        public static void foo() {
            MethodCallProxyTest.StaticMethod.CALL_TRACEABLE.register(AbstractMethodCallProxyTest.FOO);
        }
    }

    @SuppressWarnings("unused")
    public static class MultipleParameterMethod extends CallTraceable {
        public void foo(long l, String s, int i, boolean b) {
            register(AbstractMethodCallProxyTest.FOO, this, l, s, i, b);
        }
    }

    @SuppressWarnings("unused")
    public static class GenericType<T, S extends Number> {
        T foo(T t, S s) {
            return t;
        }
    }
}

