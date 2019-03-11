package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationIgnoreForBindingTest {
    private static final String FOO = "FOO";

    private static final String BAR = "bar";

    @Test
    public void testIgnoreForBinding() throws Exception {
        DynamicType.Loaded<MethodDelegationIgnoreForBindingTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationIgnoreForBindingTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationIgnoreForBindingTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationIgnoreForBindingTest.Bar.class)).make().load(MethodDelegationIgnoreForBindingTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationIgnoreForBindingTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodDelegationIgnoreForBindingTest.FOO));
    }

    public static class Foo {
        public String foo() {
            return null;
        }
    }

    public static class Bar {
        public static String bar() {
            return MethodDelegationIgnoreForBindingTest.FOO;
        }

        @IgnoreForBinding
        public static String qux() {
            return MethodDelegationIgnoreForBindingTest.BAR;
        }
    }
}

