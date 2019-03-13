package net.bytebuddy.implementation;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.DefaultCall;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationDefaultCallTest {
    private static final String FOO = "foo";

    private static final String QUX = "qux";

    private static final String SINGLE_DEFAULT_METHOD = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    private static final String CONFLICTING_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodConflictingInterface";

    private static final String PREFERRING_INTERCEPTOR = "net.bytebuddy.test.precompiled.SingleDefaultMethodPreferringInterceptor";

    private static final String CONFLICTING_PREFERRING_INTERCEPTOR = "net.bytebuddy.test.precompiled.SingleDefaultMethodConflictingPreferringInterceptor";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @JavaVersionRule.Enforce(8)
    public void testRunnableDefaultCall() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationDefaultCallTest.RunnableClass.class)).make().load(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultCallTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultCallTest.QUX))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testCallableDefaultCall() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationDefaultCallTest.CallableClass.class)).make().load(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultCallTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultCallTest.FOO))));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(8)
    public void testImplicitAmbiguousDefaultCallIsBoundToFirst() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD), Class.forName(MethodDelegationDefaultCallTest.CONFLICTING_INTERFACE)).defineMethod(MethodDelegationDefaultCallTest.FOO, Object.class, Visibility.PUBLIC).intercept(MethodDelegation.to(MethodDelegationDefaultCallTest.CallableClass.class)).make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testExplicitDefaultCall() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD), Class.forName(MethodDelegationDefaultCallTest.CONFLICTING_INTERFACE)).intercept(MethodDelegation.to(Class.forName(MethodDelegationDefaultCallTest.PREFERRING_INTERCEPTOR))).make().load(getClass().getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultCallTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultCallTest.FOO))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testExplicitDefaultCallToOtherInterface() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD), Class.forName(MethodDelegationDefaultCallTest.CONFLICTING_INTERFACE)).intercept(MethodDelegation.to(Class.forName(MethodDelegationDefaultCallTest.CONFLICTING_PREFERRING_INTERCEPTOR))).make().load(getClass().getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultCallTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultCallTest.QUX))));
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(8)
    public void testIllegalDefaultCallThrowsException() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationDefaultCallTest.IllegalAnnotation.class)).make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testSerializableProxy() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationDefaultCallTest.SerializationCheck.class)).make().load(Class.forName(MethodDelegationDefaultCallTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultCallTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultCallTest.FOO))));
    }

    public static class RunnableClass {
        public static Object foo(@DefaultCall
        Runnable runnable) {
            MatcherAssert.assertThat(runnable, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            runnable.run();
            return MethodDelegationDefaultCallTest.QUX;
        }
    }

    public static class CallableClass {
        public static String bar(@DefaultCall
        Callable<String> callable) throws Exception {
            MatcherAssert.assertThat(callable, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return callable.call();
        }
    }

    public static class IllegalAnnotation {
        public static String bar(@DefaultCall
        String value) throws Exception {
            return value;
        }
    }

    public static class SerializationCheck {
        public static String bar(@DefaultCall(serializableProxy = true)
        Callable<String> callable) throws Exception {
            MatcherAssert.assertThat(callable, CoreMatchers.instanceOf(Serializable.class));
            return callable.call();
        }
    }
}

