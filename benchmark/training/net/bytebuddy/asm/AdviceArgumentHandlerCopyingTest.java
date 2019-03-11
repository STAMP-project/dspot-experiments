package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class AdviceArgumentHandlerCopyingTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Test
    public void testShortMethod() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceArgumentHandlerCopyingTest.ShortMethod.class).visit(Advice.to(AdviceArgumentHandlerCopyingTest.EmptyAdvice.class).on(ElementMatchers.named(AdviceArgumentHandlerCopyingTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceArgumentHandlerCopyingTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceArgumentHandlerCopyingTest.BAR), Is.is(((Object) (AdviceArgumentHandlerCopyingTest.BAR))));
    }

    @Test
    public void testLongMethod() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceArgumentHandlerCopyingTest.LongMethod.class).visit(Advice.to(AdviceArgumentHandlerCopyingTest.EmptyAdvice.class).on(ElementMatchers.named(AdviceArgumentHandlerCopyingTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceArgumentHandlerCopyingTest.FOO, String.class, String.class, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceArgumentHandlerCopyingTest.BAR, AdviceArgumentHandlerCopyingTest.QUX, AdviceArgumentHandlerCopyingTest.BAZ), Is.is(((Object) (((AdviceArgumentHandlerCopyingTest.BAR) + (AdviceArgumentHandlerCopyingTest.QUX)) + (AdviceArgumentHandlerCopyingTest.BAZ)))));
    }

    @Test
    public void testShortMethodAssignment() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceArgumentHandlerCopyingTest.ShortMethod.class).visit(Advice.to(AdviceArgumentHandlerCopyingTest.UsingAdvice.class).on(ElementMatchers.named(AdviceArgumentHandlerCopyingTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceArgumentHandlerCopyingTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceArgumentHandlerCopyingTest.BAR), Is.is(((Object) (AdviceArgumentHandlerCopyingTest.BAR))));
    }

    @Test
    public void testLongMethodAssignment() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceArgumentHandlerCopyingTest.LongMethod.class).visit(Advice.to(AdviceArgumentHandlerCopyingTest.UsingAdvice.class).on(ElementMatchers.named(AdviceArgumentHandlerCopyingTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceArgumentHandlerCopyingTest.FOO, String.class, String.class, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceArgumentHandlerCopyingTest.BAR, AdviceArgumentHandlerCopyingTest.QUX, AdviceArgumentHandlerCopyingTest.BAZ), Is.is(((Object) (((AdviceArgumentHandlerCopyingTest.BAR) + (AdviceArgumentHandlerCopyingTest.QUX)) + (AdviceArgumentHandlerCopyingTest.BAZ)))));
    }

    @Test
    public void testShortExitOnlyMethod() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceArgumentHandlerCopyingTest.ShortMethod.class).visit(Advice.to(AdviceArgumentHandlerCopyingTest.EmptyExitOnlyAdvice.class).on(ElementMatchers.named(AdviceArgumentHandlerCopyingTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceArgumentHandlerCopyingTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceArgumentHandlerCopyingTest.BAR), Is.is(((Object) (AdviceArgumentHandlerCopyingTest.BAR))));
    }

    @Test
    public void testLongExitOnlyMethod() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceArgumentHandlerCopyingTest.LongMethod.class).visit(Advice.to(AdviceArgumentHandlerCopyingTest.EmptyExitOnlyAdvice.class).on(ElementMatchers.named(AdviceArgumentHandlerCopyingTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceArgumentHandlerCopyingTest.FOO, String.class, String.class, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceArgumentHandlerCopyingTest.BAR, AdviceArgumentHandlerCopyingTest.QUX, AdviceArgumentHandlerCopyingTest.BAZ), Is.is(((Object) (((AdviceArgumentHandlerCopyingTest.BAR) + (AdviceArgumentHandlerCopyingTest.QUX)) + (AdviceArgumentHandlerCopyingTest.BAZ)))));
    }

    @Test
    public void testShortExitOnlyMethodAssignment() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceArgumentHandlerCopyingTest.ShortMethod.class).visit(Advice.to(AdviceArgumentHandlerCopyingTest.UsingExitOnlyAdvice.class).on(ElementMatchers.named(AdviceArgumentHandlerCopyingTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceArgumentHandlerCopyingTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceArgumentHandlerCopyingTest.BAR), Is.is(((Object) (AdviceArgumentHandlerCopyingTest.BAR))));
    }

    @Test
    public void testLongExitOnlyMethodAssignment() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceArgumentHandlerCopyingTest.LongMethod.class).visit(Advice.to(AdviceArgumentHandlerCopyingTest.UsingExitOnlyAdvice.class).on(ElementMatchers.named(AdviceArgumentHandlerCopyingTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceArgumentHandlerCopyingTest.FOO, String.class, String.class, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceArgumentHandlerCopyingTest.BAR, AdviceArgumentHandlerCopyingTest.QUX, AdviceArgumentHandlerCopyingTest.BAZ), Is.is(((Object) (((AdviceArgumentHandlerCopyingTest.BAR) + (AdviceArgumentHandlerCopyingTest.QUX)) + (AdviceArgumentHandlerCopyingTest.BAZ)))));
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorNotSupported() {
        new ByteBuddy().redefine(AdviceArgumentHandlerCopyingTest.EmptyAdvice.class).visit(Advice.to(AdviceArgumentHandlerCopyingTest.UsingAdvice.class).on(ElementMatchers.isConstructor())).make();
    }

    @SuppressWarnings("unused")
    public static class ShortMethod {
        public String foo(String var1) {
            String result = var1;
            var1 = null;
            return result;
        }
    }

    @SuppressWarnings("unused")
    public static class LongMethod {
        public String foo(String arg1, String arg2, String arg3) {
            String result = (arg1 + arg2) + arg3;
            arg1 = null;
            return result;
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdvice {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class UsingAdvice {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit
        private static void advice(@Advice.Argument(0)
        String arg) {
            if (!(AdviceArgumentHandlerCopyingTest.BAR.equals(arg))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyExitOnlyAdvice {
        @Advice.OnMethodExit
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class UsingExitOnlyAdvice {
        @Advice.OnMethodExit
        private static void advice(@Advice.Argument(0)
        String arg) {
            if (!(AdviceArgumentHandlerCopyingTest.BAR.equals(arg))) {
                throw new AssertionError();
            }
        }
    }
}

