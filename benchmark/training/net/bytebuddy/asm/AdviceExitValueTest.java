package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class AdviceExitValueTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String EXIT = "exit";

    private static final int VALUE = 42;

    @Test
    public void testAdviceWithEnterValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceExitValueTest.Sample.class).visit(Advice.to(AdviceExitValueTest.ExitValueAdvice.class).on(ElementMatchers.named(AdviceExitValueTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceExitValueTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceExitValueTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceExitValueTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testEnterValueSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceExitValueTest.Sample.class).visit(Advice.to(AdviceExitValueTest.ExitSubstitutionAdvice.class).on(ElementMatchers.named(AdviceExitValueTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceExitValueTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceExitValueTest.FOO))));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalEnterValueSubstitution() throws Exception {
        new ByteBuddy().redefine(AdviceExitValueTest.Sample.class).visit(Advice.to(AdviceExitValueTest.IllegalExitSubstitutionAdvice.class).on(ElementMatchers.named(AdviceExitValueTest.BAR))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonAssignableEnterValueWritable() throws Exception {
        new ByteBuddy().redefine(AdviceExitValueTest.Sample.class).visit(Advice.to(AdviceExitValueTest.NonAssignableExitWriteAdvice.class).on(ElementMatchers.named(AdviceExitValueTest.FOO))).make();
    }

    @SuppressWarnings("unused")
    public static class Sample {
        public static int exit;

        public String foo() {
            return AdviceExitValueTest.FOO;
        }

        public String bar(String argument) {
            return argument;
        }
    }

    @SuppressWarnings("unused")
    public static class ExitValueAdvice {
        @Advice.OnMethodExit
        private static int exit(@Advice.Exit
        int value) {
            if (value != 0) {
                throw new AssertionError();
            }
            (AdviceExitValueTest.Sample.exit)++;
            return AdviceExitValueTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class ExitSubstitutionAdvice {
        @Advice.OnMethodExit
        @SuppressWarnings("all")
        private static String exit(@Advice.Exit(readOnly = false)
        String value) {
            value = AdviceExitValueTest.BAR;
            if (!(value.equals(AdviceExitValueTest.BAR))) {
                throw new AssertionError();
            }
            return AdviceExitValueTest.FOO;
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalExitSubstitutionAdvice {
        @Advice.OnMethodExit
        @SuppressWarnings("all")
        private static String exit(@Advice.Exit
        String value) {
            value = AdviceExitValueTest.BAR;
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class NonAssignableExitWriteAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Exit(readOnly = false)
        Object value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class NonEqualExitAdvice {
        @Advice.OnMethodExit
        private static String exit(@Advice.Exit(readOnly = false)
        Object value) {
            throw new AssertionError();
        }
    }
}

