package net.bytebuddy.implementation;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MethodDelegationMethodReturnTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testDelegationToMethodReturn() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(MethodDelegationMethodReturnTest.SampleTarget.class).method(ElementMatchers.named(MethodDelegationMethodReturnTest.BAR)).intercept(toMethodReturnOf(MethodDelegationMethodReturnTest.FOO)).make().load(MethodDelegationMethodReturnTest.SampleTarget.class.getClassLoader(), WRAPPER).getLoaded().getConstructor().newInstance().bar(), CoreMatchers.is(42));
    }

    @Test
    public void testDelegationToMethodReturnStatic() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(MethodDelegationMethodReturnTest.SampleTargetStatic.class).defineMethod(MethodDelegationMethodReturnTest.FOO, MethodDelegationMethodReturnTest.SampleDelegation.class, Ownership.STATIC).intercept(FixedValue.value(new MethodDelegationMethodReturnTest.SampleDelegation(42))).method(ElementMatchers.named(MethodDelegationMethodReturnTest.BAR)).intercept(toMethodReturnOf(MethodDelegationMethodReturnTest.FOO)).make().load(MethodDelegationMethodReturnTest.SampleTargetStatic.class.getClassLoader(), WRAPPER).getLoaded().getConstructor().newInstance().bar(), CoreMatchers.is(42));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalTargetNoMatch() throws Exception {
        new ByteBuddy().subclass(MethodDelegationMethodReturnTest.IllegalTarget.class).method(ElementMatchers.named(MethodDelegationMethodReturnTest.BAR)).intercept(toMethodReturnOf(MethodDelegationMethodReturnTest.FOO)).make();
    }

    public static class SampleTarget {
        public MethodDelegationMethodReturnTest.SampleDelegation foo() {
            return new MethodDelegationMethodReturnTest.SampleDelegation(42);
        }

        public int bar() {
            throw new AssertionError();
        }
    }

    public static class SampleTargetStatic {
        public int bar() {
            throw new AssertionError();
        }
    }

    public static class IllegalTarget {
        public MethodDelegationMethodReturnTest.SampleDelegation foo(int value) {
            return new MethodDelegationMethodReturnTest.SampleDelegation(value);
        }

        public int bar() {
            throw new AssertionError();
        }
    }

    public static class SampleDelegation {
        private final int value;

        public SampleDelegation(int value) {
            this.value = value;
        }

        public int foo() {
            return value;
        }
    }
}

