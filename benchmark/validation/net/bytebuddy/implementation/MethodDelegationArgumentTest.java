package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationArgumentTest {
    private static final String FOO = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final int BAR = 42;

    @Test
    public void testArgument() throws Exception {
        DynamicType.Loaded<MethodDelegationArgumentTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationArgumentTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationArgumentTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationArgumentTest.Bar.class)).make().load(MethodDelegationArgumentTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationArgumentTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationArgumentTest.FOO, MethodDelegationArgumentTest.BAR), CoreMatchers.is(((Object) (((MethodDelegationArgumentTest.QUX) + (MethodDelegationArgumentTest.FOO)) + (MethodDelegationArgumentTest.BAR)))));
    }

    @Test
    public void testHierarchyDelegation() throws Exception {
        new ByteBuddy().subclass(MethodDelegationArgumentTest.Baz.class).method(ElementMatchers.named("foo")).intercept(MethodDelegation.to(new MethodDelegationArgumentTest.Qux())).make().load(MethodDelegationArgumentTest.Baz.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().foo();
    }

    public static class Foo {
        public Object foo(String s, Integer i) {
            return null;
        }
    }

    public static class Bar {
        public static String qux(@Argument(1)
        Integer i, @Argument(0)
        String s) {
            return ((MethodDelegationArgumentTest.QUX) + s) + i;
        }

        public static String baz(String s, Object o) {
            return ((MethodDelegationArgumentTest.BAZ) + s) + o;
        }
    }

    public static class Baz {
        public void foo() {
        }
    }

    public static class Qux extends MethodDelegationArgumentTest.Baz {
        public void foo() {
            super.foo();
        }
    }
}

