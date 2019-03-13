package net.bytebuddy.implementation;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationSuperMethodTest {
    private static final String SINGLE_DEFAULT_METHOD = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    private static final String CONFLICTING_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodConflictingInterface";

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testRunnableSuperCall() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperMethodTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperMethodTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperMethodTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperMethodTest.SampleClass.class)).make().load(MethodDelegationSuperMethodTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        MethodDelegationSuperMethodTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.value, CoreMatchers.is(MethodDelegationSuperMethodTest.BAR));
        instance.foo();
        MatcherAssert.assertThat(instance.value, CoreMatchers.is(MethodDelegationSuperMethodTest.FOO));
    }

    @Test
    public void testRunnableSuperCallNoCache() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperMethodTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperMethodTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperMethodTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperMethodTest.SampleClassNoCache.class)).make().load(MethodDelegationSuperMethodTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(0));
        MethodDelegationSuperMethodTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.value, CoreMatchers.is(MethodDelegationSuperMethodTest.BAR));
        instance.foo();
        MatcherAssert.assertThat(instance.value, CoreMatchers.is(MethodDelegationSuperMethodTest.FOO));
    }

    @Test
    public void testRunnableSuperCallWithPrivilege() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperMethodTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperMethodTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperMethodTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperMethodTest.SampleClassWithPrivilege.class)).make().load(MethodDelegationSuperMethodTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getAuxiliaryTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        MethodDelegationSuperMethodTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.value, CoreMatchers.is(MethodDelegationSuperMethodTest.BAR));
        instance.foo();
        MatcherAssert.assertThat(instance.value, CoreMatchers.is(MethodDelegationSuperMethodTest.FOO));
    }

    @Test
    public void testVoidToNonVoidSuperCall() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperMethodTest.VoidTest> loaded = new ByteBuddy().subclass(MethodDelegationSuperMethodTest.VoidTest.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperMethodTest.VoidTest.class)).intercept(MethodDelegation.to(MethodDelegationSuperMethodTest.NonVoidTarget.class)).make().load(MethodDelegationSuperMethodTest.VoidTest.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperMethodTest.VoidTest instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo();
        instance.assertOnlyCall(MethodDelegationSuperMethodTest.FOO);
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodFallback() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationSuperMethodTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationSuperMethodTest.NonVoidTarget.class)).make().load(getClass().getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationSuperMethodTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationSuperMethodTest.FOO))));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodFallbackDisabled() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationSuperMethodTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationSuperMethodTest.NoFallback.class)).make();
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodFallbackAmbiguous() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationSuperMethodTest.SINGLE_DEFAULT_METHOD), Class.forName(MethodDelegationSuperMethodTest.CONFLICTING_INTERFACE)).intercept(MethodDelegation.to(MethodDelegationSuperMethodTest.NonVoidTarget.class)).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAbstractMethodNonBindable() throws Exception {
        new ByteBuddy().subclass(MethodDelegationSuperMethodTest.Qux.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperMethodTest.Qux.class)).intercept(MethodDelegation.to(MethodDelegationSuperMethodTest.SampleClass.class)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongTypeThrowsException() throws Exception {
        new ByteBuddy().subclass(MethodDelegationSuperMethodTest.Bar.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperMethodTest.Bar.class)).intercept(MethodDelegation.to(MethodDelegationSuperMethodTest.IllegalAnnotation.class)).make();
    }

    public static class Foo {
        public String value = MethodDelegationSuperMethodTest.BAR;

        public void foo() {
            value = MethodDelegationSuperMethodTest.FOO;
        }
    }

    public static class SampleClass {
        public static void foo(@SuperMethod
        Method method, @This
        Object target) throws Exception {
            method.invoke(target);
        }
    }

    public static class SampleClassNoCache {
        public static void foo(@SuperMethod(cached = false)
        Method method, @This
        Object target) throws Exception {
            method.invoke(target);
        }
    }

    public static class SampleClassWithPrivilege {
        public static void foo(@SuperMethod(privileged = true)
        Method method, @This
        Object target) throws Exception {
            method.invoke(target);
        }
    }

    public static class Bar {
        public String bar() {
            return MethodDelegationSuperMethodTest.FOO;
        }
    }

    public abstract static class Qux {
        public abstract String bar();
    }

    public static class VoidTest extends CallTraceable {
        public void foo() {
            register(MethodDelegationSuperMethodTest.FOO);
        }
    }

    public static class NonVoidTarget {
        public static Object foo(@SuperMethod
        Method method, @This
        Object target) throws Exception {
            if (!(Modifier.isPublic(method.getModifiers()))) {
                throw new AssertionError();
            }
            return method.invoke(target);
        }
    }

    public static class IllegalAnnotation {
        public static String bar(@SuperMethod
        String value) throws Exception {
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class NoFallback {
        @RuntimeType
        public static Object foo(@SuperMethod(fallbackToDefault = false)
        Method method) throws Exception {
            return null;
        }
    }
}

