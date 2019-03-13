package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationCheckedExceptionTest {
    @Test(expected = Exception.class)
    public void testUndeclaredCheckedException() throws Exception {
        new ByteBuddy().subclass(MethodDelegationCheckedExceptionTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationCheckedExceptionTest.Foo.class)).intercept(MethodDelegation.to(MethodDelegationCheckedExceptionTest.Foo.class)).make().load(MethodDelegationCheckedExceptionTest.Foo.class.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().bar();
    }

    @SuppressWarnings("unused")
    public static class Foo {
        public static void doThrow() throws Exception {
            throw new Exception();
        }

        public void bar() {
            /* do nothing */
        }
    }
}

