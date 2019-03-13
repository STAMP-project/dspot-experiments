package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.packaging.PackagePrivateMethod;
import org.junit.Test;


public class MethodDelegationExceptionTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNoMethod() throws Exception {
        new ByteBuddy().subclass(MethodDelegationExceptionTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationExceptionTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationExceptionTest.Bar.class)).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoVisibleMethod() throws Exception {
        new ByteBuddy().subclass(MethodDelegationExceptionTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationExceptionTest.Foo.class)).intercept(MethodDelegation.to(new PackagePrivateMethod())).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoCompatibleMethod() throws Exception {
        new ByteBuddy().subclass(MethodDelegationExceptionTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationExceptionTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationExceptionTest.Qux.class)).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArray() throws Exception {
        MethodDelegation.to(int[].class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrimitive() throws Exception {
        MethodDelegation.to(int.class);
    }

    public static class Foo {
        public void bar() {
            /* do nothing */
        }
    }

    /* empty */
    public static class Bar {}

    public static class Qux {
        public static void foo(Object o) {
            /* do nothing */
        }
    }
}

