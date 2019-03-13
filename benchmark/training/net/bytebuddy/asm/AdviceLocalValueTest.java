package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class AdviceLocalValueTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String ENTER = "enter";

    private static final String EXIT = "exit";

    @Test
    public void testAdviceWithLocalValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceLocalValueTest.Sample.class).visit(Advice.to(AdviceLocalValueTest.LocalValueAdvice.class).on(ElementMatchers.named(AdviceLocalValueTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceLocalValueTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceLocalValueTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceLocalValueTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceLocalValueTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithTwoLocalValues() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceLocalValueTest.Sample.class).visit(Advice.to(AdviceLocalValueTest.LocalValueTwoParametersAdvice.class).on(ElementMatchers.named(AdviceLocalValueTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceLocalValueTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceLocalValueTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceLocalValueTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceLocalValueTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @SuppressWarnings("unused")
    public static class Sample {
        public static int enter;

        public static int exit;

        public String foo() {
            return AdviceLocalValueTest.FOO;
        }
    }

    @SuppressWarnings("unused")
    public static class LocalValueAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Local(net.bytebuddy.asm.FOO.class)
        Object foo) {
            if (foo != null) {
                throw new AssertionError();
            }
            foo = AdviceLocalValueTest.FOO;
            if (!(foo.equals(AdviceLocalValueTest.FOO))) {
                throw new AssertionError();
            }
            (AdviceLocalValueTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Local(net.bytebuddy.asm.FOO.class)
        Object foo) {
            if (!(foo.equals(AdviceLocalValueTest.FOO))) {
                throw new AssertionError();
            }
            foo = AdviceLocalValueTest.BAR;
            if (!(foo.equals(AdviceLocalValueTest.BAR))) {
                throw new AssertionError();
            }
            (AdviceLocalValueTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class LocalValueTwoParametersAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Local(net.bytebuddy.asm.FOO.class)
        Object foo, @Advice.Local(net.bytebuddy.asm.BAR.class)
        Object bar) {
            if ((foo != null) || (bar != null)) {
                throw new AssertionError();
            }
            foo = AdviceLocalValueTest.FOO;
            bar = AdviceLocalValueTest.BAR;
            if ((!(foo.equals(AdviceLocalValueTest.FOO))) || (!(bar.equals(AdviceLocalValueTest.BAR)))) {
                throw new AssertionError();
            }
            (AdviceLocalValueTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Local(net.bytebuddy.asm.FOO.class)
        Object foo, @Advice.Local(net.bytebuddy.asm.BAR.class)
        Object bar) {
            if ((!(foo.equals(AdviceLocalValueTest.FOO))) || (!(bar.equals(AdviceLocalValueTest.BAR)))) {
                throw new AssertionError();
            }
            foo = AdviceLocalValueTest.BAR;
            bar = AdviceLocalValueTest.FOO;
            if ((!(foo.equals(AdviceLocalValueTest.BAR))) || (!(bar.equals(AdviceLocalValueTest.FOO)))) {
                throw new AssertionError();
            }
            (AdviceLocalValueTest.Sample.exit)++;
        }
    }
}

