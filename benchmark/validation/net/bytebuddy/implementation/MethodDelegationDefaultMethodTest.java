package net.bytebuddy.implementation;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.DefaultMethod;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationDefaultMethodTest {
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
    public void testCallableDefaultCall() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationDefaultMethodTest.SampleClass.class)).make().load(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultMethodTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultMethodTest.FOO))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testCallableDefaultCallNoCache() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationDefaultMethodTest.SampleClassNoCache.class)).make().load(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultMethodTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultMethodTest.FOO))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testCallableDefaultCallPrivileged() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationDefaultMethodTest.SampleClassPrivileged.class)).make().load(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD).getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getAuxiliaryTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultMethodTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultMethodTest.FOO))));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(8)
    public void testImplicitAmbiguousDefaultCallIsBoundToFirst() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD), Class.forName(MethodDelegationDefaultMethodTest.CONFLICTING_INTERFACE)).defineMethod(MethodDelegationDefaultMethodTest.FOO, Object.class, Visibility.PUBLIC).intercept(MethodDelegation.to(MethodDelegationDefaultMethodTest.SampleClass.class)).make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testExplicitDefaultCall() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD), Class.forName(MethodDelegationDefaultMethodTest.CONFLICTING_INTERFACE)).intercept(MethodDelegation.to(Class.forName(MethodDelegationDefaultMethodTest.PREFERRING_INTERCEPTOR))).make().load(getClass().getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultMethodTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultMethodTest.FOO))));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testExplicitDefaultCallToOtherInterface() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD), Class.forName(MethodDelegationDefaultMethodTest.CONFLICTING_INTERFACE)).intercept(MethodDelegation.to(Class.forName(MethodDelegationDefaultMethodTest.CONFLICTING_PREFERRING_INTERCEPTOR))).make().load(getClass().getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationDefaultMethodTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationDefaultMethodTest.QUX))));
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(8)
    public void testIllegalDefaultCallThrowsException() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationDefaultMethodTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationDefaultMethodTest.IllegalAnnotation.class)).make();
    }

    public static class SampleClass {
        public static String bar(@DefaultMethod
        Method method, @This
        Object target) throws Exception {
            if (!(Modifier.isPublic(method.getModifiers()))) {
                throw new AssertionError();
            }
            return ((String) (method.invoke(target)));
        }
    }

    public static class SampleClassNoCache {
        public static String bar(@DefaultMethod(cached = false)
        Method method, @This
        Object target) throws Exception {
            if (!(Modifier.isPublic(method.getModifiers()))) {
                throw new AssertionError();
            }
            return ((String) (method.invoke(target)));
        }
    }

    public static class SampleClassPrivileged {
        public static String bar(@DefaultMethod(privileged = true)
        Method method, @This
        Object target) throws Exception {
            if (!(Modifier.isPublic(method.getModifiers()))) {
                throw new AssertionError();
            }
            return ((String) (method.invoke(target)));
        }
    }

    public static class IllegalAnnotation {
        public static String bar(@DefaultMethod
        String value) throws Exception {
            return value;
        }
    }
}

