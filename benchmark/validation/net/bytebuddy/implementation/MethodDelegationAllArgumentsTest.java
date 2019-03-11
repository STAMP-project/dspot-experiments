package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bind.annotation.AllArguments.Assignment.SLACK;


public class MethodDelegationAllArgumentsTest {
    private static final int FOO = 42;

    private static final int BAR = 21;

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String FOOBAR = "foobar";

    @Test
    public void testStrictBindable() throws Exception {
        DynamicType.Loaded<MethodDelegationAllArgumentsTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationAllArgumentsTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationAllArgumentsTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationAllArgumentsTest.Bar.class)).make().load(MethodDelegationAllArgumentsTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationAllArgumentsTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationAllArgumentsTest.FOO, MethodDelegationAllArgumentsTest.BAR), CoreMatchers.is(((Object) (((MethodDelegationAllArgumentsTest.QUX) + (MethodDelegationAllArgumentsTest.FOO)) + (MethodDelegationAllArgumentsTest.BAR)))));
    }

    @Test
    public void testStrictBindableObjectType() throws Exception {
        DynamicType.Loaded<MethodDelegationAllArgumentsTest.Foo> loaded = new ByteBuddy().subclass(MethodDelegationAllArgumentsTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationAllArgumentsTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationAllArgumentsTest.FooBar.class)).make().load(MethodDelegationAllArgumentsTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationAllArgumentsTest.Foo instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationAllArgumentsTest.FOO, MethodDelegationAllArgumentsTest.BAR), CoreMatchers.is(((Object) (((MethodDelegationAllArgumentsTest.QUX) + (MethodDelegationAllArgumentsTest.FOO)) + (MethodDelegationAllArgumentsTest.BAR)))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStrictNonBindableThrowsException() throws Exception {
        new ByteBuddy().subclass(MethodDelegationAllArgumentsTest.Qux.class).method(ElementMatchers.isDeclaredBy(MethodDelegationAllArgumentsTest.Qux.class)).intercept(MethodDelegation.to(MethodDelegationAllArgumentsTest.BazStrict.class)).make();
    }

    @Test
    public void testSlackNonBindable() throws Exception {
        DynamicType.Loaded<MethodDelegationAllArgumentsTest.Qux> loaded = new ByteBuddy().subclass(MethodDelegationAllArgumentsTest.Qux.class).method(ElementMatchers.isDeclaredBy(MethodDelegationAllArgumentsTest.Qux.class)).intercept(MethodDelegation.to(MethodDelegationAllArgumentsTest.BazSlack.class)).make().load(MethodDelegationAllArgumentsTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationAllArgumentsTest.Qux instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationAllArgumentsTest.FOOBAR, MethodDelegationAllArgumentsTest.BAZ), CoreMatchers.is(((Object) ((MethodDelegationAllArgumentsTest.QUX) + (MethodDelegationAllArgumentsTest.BAZ)))));
    }

    @Test
    public void testIncludeSelf() throws Exception {
        DynamicType.Loaded<MethodDelegationAllArgumentsTest.Qux> loaded = new ByteBuddy().subclass(MethodDelegationAllArgumentsTest.Qux.class).method(ElementMatchers.isDeclaredBy(MethodDelegationAllArgumentsTest.Qux.class)).intercept(MethodDelegation.to(MethodDelegationAllArgumentsTest.IncludeSelf.class)).make().load(MethodDelegationAllArgumentsTest.Foo.class.getClassLoader(), WRAPPER);
        MethodDelegationAllArgumentsTest.Qux instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.foo(MethodDelegationAllArgumentsTest.QUX, MethodDelegationAllArgumentsTest.BAZ), CoreMatchers.is(((Object) (instance))));
    }

    public static class Foo {
        public Object foo(int i1, Integer i2) {
            return null;
        }
    }

    public static class Bar {
        public static String qux(@AllArguments
        int[] args) {
            return ((MethodDelegationAllArgumentsTest.QUX) + (args[0])) + (args[1]);
        }
    }

    public static class FooBar {
        public static String qux(@AllArguments
        Object args) {
            return ((MethodDelegationAllArgumentsTest.QUX) + (((Object[]) (args))[0])) + (((Object[]) (args))[1]);
        }
    }

    public static class Qux {
        public Object foo(Object o, String s) {
            return null;
        }
    }

    public static class BazStrict {
        public static String qux(@AllArguments
        String[] args) {
            MatcherAssert.assertThat(args.length, CoreMatchers.is(2));
            return ((MethodDelegationAllArgumentsTest.QUX) + (args[0])) + (args[1]);
        }
    }

    public static class BazSlack {
        public static String qux(@AllArguments(SLACK)
        String[] args) {
            MatcherAssert.assertThat(args.length, CoreMatchers.is(1));
            return (MethodDelegationAllArgumentsTest.QUX) + (args[0]);
        }
    }

    public static class IncludeSelf {
        public static Object intercept(@AllArguments(includeSelf = true)
        Object[] args) {
            MatcherAssert.assertThat(args.length, CoreMatchers.is(3));
            MatcherAssert.assertThat(args[1], CoreMatchers.is(((Object) (MethodDelegationAllArgumentsTest.QUX))));
            MatcherAssert.assertThat(args[2], CoreMatchers.is(((Object) (MethodDelegationAllArgumentsTest.BAZ))));
            return args[0];
        }
    }
}

