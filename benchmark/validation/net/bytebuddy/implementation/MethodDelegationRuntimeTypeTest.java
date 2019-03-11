package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationRuntimeTypeTest {
    private static final String FOO = "FOO";

    @Test
    public void testRuntimeType() throws Exception {
        DynamicType.Loaded<MethodDelegationRuntimeTypeTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationRuntimeTypeTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationRuntimeTypeTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationRuntimeTypeTest.Bar.class)).make().load(MethodDelegationRuntimeTypeTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationRuntimeTypeTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationRuntimeTypeTest.FOO), CoreMatchers.is(MethodDelegationRuntimeTypeTest.FOO));
    }

    public static class Foo {
        public String foo(Object o) {
            return ((String) (o));
        }
    }

    public static class Bar {
        @RuntimeType
        public static Object foo(@RuntimeType
        String s) {
            return s;
        }
    }
}

