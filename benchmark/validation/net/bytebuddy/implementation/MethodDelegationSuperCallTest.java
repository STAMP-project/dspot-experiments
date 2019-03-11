package net.bytebuddy.implementation;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationSuperCallTest {
    private static final String SINGLE_DEFAULT_METHOD = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    private static final String CONFLICTING_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodConflictingInterface";

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testRunnableSuperCall() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperCallTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationSuperCallTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperCallTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.RunnableClass.class)).make().load(MethodDelegationSuperCallTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperCallTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.value, CoreMatchers.is(MethodDelegationSuperCallTest.BAR));
        instance.foo();
        MatcherAssert.assertThat(instance.value, CoreMatchers.is(MethodDelegationSuperCallTest.FOO));
    }

    @Test
    public void testCallableSuperCall() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperCallTest.Bar> loaded = new ByteBuddy().subclass(MethodDelegationSuperCallTest.Bar.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperCallTest.Bar.class)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.CallableClass.class)).make().load(MethodDelegationSuperCallTest.Bar.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperCallTest.Bar instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.bar(), CoreMatchers.is(MethodDelegationSuperCallTest.FOO));
    }

    @Test
    public void testVoidToNonVoidSuperCall() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperCallTest.VoidTest> loaded = new ByteBuddy().subclass(MethodDelegationSuperCallTest.VoidTest.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperCallTest.VoidTest.class)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.NonVoidTarget.class)).make().load(MethodDelegationSuperCallTest.VoidTest.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperCallTest.VoidTest instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo();
        instance.assertOnlyCall(MethodDelegationSuperCallTest.FOO);
    }

    @Test
    public void testRuntimeTypeSuperCall() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperCallTest.RuntimeTypeTest> loaded = new ByteBuddy().subclass(MethodDelegationSuperCallTest.RuntimeTypeTest.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperCallTest.RuntimeTypeTest.class)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.RuntimeTypeTarget.class)).make().load(MethodDelegationSuperCallTest.RuntimeTypeTest.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperCallTest.RuntimeTypeTest instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodDelegationSuperCallTest.FOO));
    }

    @Test
    public void testSerializableProxy() throws Exception {
        DynamicType.Loaded<MethodDelegationSuperCallTest.Bar> loaded = new ByteBuddy().subclass(MethodDelegationSuperCallTest.Bar.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperCallTest.Bar.class)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.SerializationCheck.class)).make().load(MethodDelegationSuperCallTest.Bar.class.getClassLoader(), WRAPPER);
        MethodDelegationSuperCallTest.Bar instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.bar(), CoreMatchers.is(MethodDelegationSuperCallTest.FOO));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodFallback() throws Exception {
        DynamicType.Loaded<?> loaded = new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationSuperCallTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.NonVoidTarget.class)).make().load(getClass().getClassLoader(), WRAPPER);
        Object instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        Method method = loaded.getLoaded().getMethod(MethodDelegationSuperCallTest.FOO);
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) (MethodDelegationSuperCallTest.FOO))));
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodFallbackDisabled() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationSuperCallTest.SINGLE_DEFAULT_METHOD)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.NoFallback.class)).make();
    }

    @Test(expected = IllegalArgumentException.class)
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodFallbackAmbiguous() throws Exception {
        new ByteBuddy().subclass(Object.class).implement(Class.forName(MethodDelegationSuperCallTest.SINGLE_DEFAULT_METHOD), Class.forName(MethodDelegationSuperCallTest.CONFLICTING_INTERFACE)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.NonVoidTarget.class)).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAbstractMethodNonBindable() throws Exception {
        new ByteBuddy().subclass(MethodDelegationSuperCallTest.Qux.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperCallTest.Qux.class)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.CallableClass.class)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongTypeThrowsException() throws Exception {
        new ByteBuddy().subclass(MethodDelegationSuperCallTest.Bar.class).method(ElementMatchers.isDeclaredBy(MethodDelegationSuperCallTest.Bar.class)).intercept(MethodDelegation.to(MethodDelegationSuperCallTest.IllegalAnnotation.class)).make();
    }

    public static class Foo {
        public String value = MethodDelegationSuperCallTest.BAR;

        public void foo() {
            value = MethodDelegationSuperCallTest.FOO;
        }
    }

    public static class RunnableClass {
        public static void foo(@SuperCall
        Runnable runnable) {
            MatcherAssert.assertThat(runnable, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            runnable.run();
        }
    }

    public static class Bar {
        public String bar() {
            return MethodDelegationSuperCallTest.FOO;
        }
    }

    public static class CallableClass {
        public static String bar(@SuperCall
        Callable<String> callable) throws Exception {
            MatcherAssert.assertThat(callable, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            return callable.call();
        }
    }

    public abstract static class Qux {
        public abstract String bar();
    }

    public static class VoidTest extends CallTraceable {
        public void foo() {
            register(MethodDelegationSuperCallTest.FOO);
        }
    }

    public static class NonVoidTarget {
        public static Object foo(@SuperCall
        Callable<?> zuper) throws Exception {
            return zuper.call();
        }
    }

    public static class RuntimeTypeTest {
        public String foo() {
            return MethodDelegationSuperCallTest.FOO;
        }
    }

    public static class RuntimeTypeTarget {
        @RuntimeType
        public static Object foo(@SuperCall
        Callable<?> zuper) throws Exception {
            return zuper.call();
        }
    }

    public static class IllegalAnnotation {
        public static String bar(@SuperCall
        String value) throws Exception {
            return value;
        }
    }

    public static class SerializationCheck {
        public static String bar(@SuperCall(serializableProxy = true)
        Callable<String> callable) throws Exception {
            MatcherAssert.assertThat(callable, CoreMatchers.instanceOf(Serializable.class));
            return callable.call();
        }
    }

    @SuppressWarnings("unused")
    public static class NoFallback {
        @RuntimeType
        public static Object foo(@SuperCall(fallbackToDefault = false)
        Callable<?> zuper) throws Exception {
            return null;
        }
    }
}

