package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.StubValue;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationStubValueTest {
    @Test
    public void testVoidMethod() throws Exception {
        DynamicType.Loaded<MethodDelegationStubValueTest.VoidMethod> loaded = new ByteBuddy().subclass(MethodDelegationStubValueTest.VoidMethod.class).method(ElementMatchers.isDeclaredBy(MethodDelegationStubValueTest.VoidMethod.class)).intercept(MethodDelegation.to(new MethodDelegationStubValueTest.Interceptor(null))).make().load(MethodDelegationStubValueTest.VoidMethod.class.getClassLoader(), WRAPPER);
        MethodDelegationStubValueTest.VoidMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        instance.foo();
    }

    @Test
    public void testReference() throws Exception {
        DynamicType.Loaded<MethodDelegationStubValueTest.ReferenceMethod> loaded = new ByteBuddy().subclass(MethodDelegationStubValueTest.ReferenceMethod.class).method(ElementMatchers.isDeclaredBy(MethodDelegationStubValueTest.ReferenceMethod.class)).intercept(MethodDelegation.to(new MethodDelegationStubValueTest.Interceptor(null))).make().load(MethodDelegationStubValueTest.ReferenceMethod.class.getClassLoader(), WRAPPER);
        MethodDelegationStubValueTest.ReferenceMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testLongValue() throws Exception {
        DynamicType.Loaded<MethodDelegationStubValueTest.LongMethod> loaded = new ByteBuddy().subclass(MethodDelegationStubValueTest.LongMethod.class).method(ElementMatchers.isDeclaredBy(MethodDelegationStubValueTest.LongMethod.class)).intercept(MethodDelegation.to(new MethodDelegationStubValueTest.Interceptor(0L))).make().load(MethodDelegationStubValueTest.LongMethod.class.getClassLoader(), WRAPPER);
        MethodDelegationStubValueTest.LongMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(0L));
    }

    @Test
    public void tesIntegerValue() throws Exception {
        DynamicType.Loaded<MethodDelegationStubValueTest.IntegerMethod> loaded = new ByteBuddy().subclass(MethodDelegationStubValueTest.IntegerMethod.class).method(ElementMatchers.isDeclaredBy(MethodDelegationStubValueTest.IntegerMethod.class)).intercept(MethodDelegation.to(new MethodDelegationStubValueTest.Interceptor(0))).make().load(MethodDelegationStubValueTest.LongMethod.class.getClassLoader(), WRAPPER);
        MethodDelegationStubValueTest.IntegerMethod instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(0));
    }

    public static class VoidMethod {
        public void foo() {
            throw new AssertionError();
        }
    }

    public static class ReferenceMethod {
        public Object foo() {
            throw new AssertionError();
        }
    }

    public static class LongMethod {
        public long foo() {
            throw new AssertionError();
        }
    }

    public static class IntegerMethod {
        public int foo() {
            throw new AssertionError();
        }
    }

    public static class Interceptor {
        private final Object expectedValue;

        public Interceptor(Object expectedValue) {
            this.expectedValue = expectedValue;
        }

        @RuntimeType
        public Object intercept(@StubValue
        Object value) {
            if ((expectedValue) == null) {
                MatcherAssert.assertThat(value, CoreMatchers.nullValue());
            } else {
                MatcherAssert.assertThat(value, CoreMatchers.is(expectedValue));
            }
            return value;
        }
    }
}

