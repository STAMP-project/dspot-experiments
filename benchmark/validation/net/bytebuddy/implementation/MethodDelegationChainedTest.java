package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationChainedTest {
    private static final String FOO = "foo";

    @Test
    public void testChainingVoid() throws Exception {
        MethodDelegationChainedTest.VoidInterceptor voidInterceptor = new MethodDelegationChainedTest.VoidInterceptor();
        DynamicType.Loaded<MethodDelegationChainedTest.Foo> dynamicType = new ByteBuddy().subclass(MethodDelegationChainedTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationChainedTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().filter(ElementMatchers.isDeclaredBy(MethodDelegationChainedTest.VoidInterceptor.class)).to(voidInterceptor).andThen(new Implementation.Simple(new TextConstant(MethodDelegationChainedTest.FOO), MethodReturn.REFERENCE))).make().load(MethodDelegationChainedTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(MethodDelegationChainedTest.FOO));
        MatcherAssert.assertThat(voidInterceptor.intercepted, CoreMatchers.is(true));
    }

    @Test
    public void testChainingNonVoid() throws Exception {
        MethodDelegationChainedTest.NonVoidInterceptor nonVoidInterceptor = new MethodDelegationChainedTest.NonVoidInterceptor();
        DynamicType.Loaded<MethodDelegationChainedTest.Foo> dynamicType = new ByteBuddy().subclass(MethodDelegationChainedTest.Foo.class).method(ElementMatchers.isDeclaredBy(MethodDelegationChainedTest.Foo.class)).intercept(MethodDelegation.withDefaultConfiguration().filter(ElementMatchers.isDeclaredBy(MethodDelegationChainedTest.NonVoidInterceptor.class)).to(nonVoidInterceptor).andThen(new Implementation.Simple(new TextConstant(MethodDelegationChainedTest.FOO), MethodReturn.REFERENCE))).make().load(MethodDelegationChainedTest.Foo.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(dynamicType.getLoaded().getDeclaredConstructor().newInstance().foo(), CoreMatchers.is(MethodDelegationChainedTest.FOO));
        MatcherAssert.assertThat(nonVoidInterceptor.intercepted, CoreMatchers.is(true));
    }

    public static class Foo {
        public String foo() {
            return null;
        }
    }

    public class VoidInterceptor {
        private boolean intercepted = false;

        public void intercept() {
            intercepted = true;
        }
    }

    public class NonVoidInterceptor {
        private boolean intercepted = false;

        public Integer intercept() {
            intercepted = true;
            return 0;
        }
    }
}

