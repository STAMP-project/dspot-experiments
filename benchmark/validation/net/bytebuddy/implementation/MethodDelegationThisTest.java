package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationThisTest {
    @Test
    public void testThis() throws Exception {
        DynamicType.Loaded<MethodDelegationThisTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationThisTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationThisTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationThisTest.Bar.class)).make().load(MethodDelegationThisTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationThisTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(), CoreMatchers.is(((Object) (instance))));
    }

    public static class Foo {
        public Object foo() {
            return null;
        }
    }

    public static class Bar {
        public static Object qux(@This
        MethodDelegationThisTest.Foo foo) {
            return foo;
        }

        public static Object baz(@This
        Void v) {
            return v;
        }
    }
}

