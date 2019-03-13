package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class AdviceEnterValueTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String ENTER = "enter";

    private static final String EXIT = "exit";

    private static final int VALUE = 42;

    @Test
    public void testAdviceWithEnterValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceEnterValueTest.Sample.class).visit(Advice.to(AdviceEnterValueTest.EnterValueAdvice.class).on(ElementMatchers.named(AdviceEnterValueTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceEnterValueTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceEnterValueTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceEnterValueTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceEnterValueTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testVariableMappingAdviceLarger() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceEnterValueTest.Sample.class).visit(Advice.to(AdviceEnterValueTest.AdviceWithVariableValues.class).on(ElementMatchers.named(AdviceEnterValueTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceEnterValueTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), ((((AdviceEnterValueTest.FOO) + (AdviceEnterValueTest.BAR)) + (AdviceEnterValueTest.QUX)) + (AdviceEnterValueTest.BAZ))), CoreMatchers.is(((Object) ((((AdviceEnterValueTest.FOO) + (AdviceEnterValueTest.BAR)) + (AdviceEnterValueTest.QUX)) + (AdviceEnterValueTest.BAZ)))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceEnterValueTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceEnterValueTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testVariableMappingInstrumentedLarger() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceEnterValueTest.Sample.class).visit(Advice.to(AdviceEnterValueTest.AdviceWithVariableValues.class).on(ElementMatchers.named(((AdviceEnterValueTest.QUX) + (AdviceEnterValueTest.BAZ))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(((AdviceEnterValueTest.QUX) + (AdviceEnterValueTest.BAZ))).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) ((((AdviceEnterValueTest.FOO) + (AdviceEnterValueTest.BAR)) + (AdviceEnterValueTest.QUX)) + (AdviceEnterValueTest.BAZ)))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceEnterValueTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceEnterValueTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testEnterValueSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceEnterValueTest.Sample.class).visit(Advice.to(AdviceEnterValueTest.EnterSubstitutionAdvice.class).on(ElementMatchers.named(AdviceEnterValueTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceEnterValueTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceEnterValueTest.FOO))));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalEnterValueSubstitution() throws Exception {
        new ByteBuddy().redefine(AdviceEnterValueTest.Sample.class).visit(Advice.to(AdviceEnterValueTest.IllegalEnterSubstitutionAdvice.class).on(ElementMatchers.named(AdviceEnterValueTest.BAR))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotBindEnterToEnter() throws Exception {
        Advice.to(AdviceEnterValueTest.EnterToEnterAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonAssignableEnterValue() throws Exception {
        Advice.to(AdviceEnterValueTest.NonAssignableEnterAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonAssignableEnterValueWritable() throws Exception {
        new ByteBuddy().redefine(AdviceEnterValueTest.Sample.class).visit(Advice.to(AdviceEnterValueTest.NonAssignableEnterWriteAdvice.class).on(ElementMatchers.named(AdviceEnterValueTest.FOO))).make();
    }

    @SuppressWarnings("unused")
    public static class Sample {
        private Object object;

        public static int enter;

        public static int exit;

        public static Throwable throwable;

        public String foo() {
            return AdviceEnterValueTest.FOO;
        }

        public String bar(String argument) {
            return argument;
        }

        public String quxbaz() {
            String foo = AdviceEnterValueTest.FOO;
            String bar = AdviceEnterValueTest.BAR;
            String qux = AdviceEnterValueTest.QUX;
            String baz = AdviceEnterValueTest.BAZ;
            return ((foo + bar) + qux) + baz;
        }
    }

    @SuppressWarnings("unused")
    public static class AdviceWithVariableValues {
        @Advice.OnMethodEnter
        private static int enter() {
            int foo = AdviceEnterValueTest.VALUE;
            int bar = (AdviceEnterValueTest.VALUE) * 2;
            (AdviceEnterValueTest.Sample.enter)++;
            return foo + bar;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Enter
        int enter, @Advice.Return
        String value) {
            int foo = AdviceEnterValueTest.VALUE;
            int bar = (AdviceEnterValueTest.VALUE) * 2;
            if (((foo + bar) != enter) || (!(value.equals(((((AdviceEnterValueTest.FOO) + (AdviceEnterValueTest.BAR)) + (AdviceEnterValueTest.QUX)) + (AdviceEnterValueTest.BAZ)))))) {
                throw new AssertionError();
            }
            (AdviceEnterValueTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class EnterValueAdvice {
        @Advice.OnMethodEnter
        private static int enter() {
            (AdviceEnterValueTest.Sample.enter)++;
            return AdviceEnterValueTest.VALUE;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Enter
        int value) {
            if (value != (AdviceEnterValueTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceEnterValueTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class EnterSubstitutionAdvice {
        @Advice.OnMethodEnter
        private static String enter() {
            return AdviceEnterValueTest.FOO;
        }

        @Advice.OnMethodExit
        @SuppressWarnings("all")
        private static void exit(@Advice.Enter(readOnly = false)
        String value) {
            value = AdviceEnterValueTest.BAR;
            if (!(value.equals(AdviceEnterValueTest.BAR))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalEnterSubstitutionAdvice {
        @Advice.OnMethodEnter
        private static String enter() {
            return AdviceEnterValueTest.FOO;
        }

        @Advice.OnMethodExit
        @SuppressWarnings("all")
        private static void exit(@Advice.Enter
        String value) {
            value = AdviceEnterValueTest.BAR;
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class EnterToEnterAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Enter
        Object value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class NonAssignableEnterAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Enter
        Object value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class NonAssignableEnterWriteAdvice {
        @Advice.OnMethodEnter
        private static String enter() {
            throw new AssertionError();
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Enter(readOnly = false)
        Object value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class NonEqualEnterAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Enter(readOnly = false)
        Object value) {
            throw new AssertionError();
        }
    }
}

