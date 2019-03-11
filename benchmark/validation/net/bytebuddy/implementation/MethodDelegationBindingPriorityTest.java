package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationBindingPriorityTest {
    private static final String FOO = "FOO";

    private static final String BAR = "bar";

    private static final int PRIORITY = 10;

    @Test
    public void testBindingPriority() throws Exception {
        DynamicType.Loaded<MethodDelegationBindingPriorityTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationBindingPriorityTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationBindingPriorityTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationBindingPriorityTest.Bar.class)).make().load(MethodDelegationBindingPriorityTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationBindingPriorityTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(MethodDelegationBindingPriorityTest.FOO));
    }

    public static class Foo {
        public String foo() {
            return null;
        }
    }

    public static class Bar {
        @BindingPriority(MethodDelegationBindingPriorityTest.PRIORITY)
        public static String bar() {
            return MethodDelegationBindingPriorityTest.FOO;
        }

        public static String qux() {
            return MethodDelegationBindingPriorityTest.BAR;
        }
    }
}

