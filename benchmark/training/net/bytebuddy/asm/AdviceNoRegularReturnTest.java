package net.bytebuddy.asm;


import java.lang.reflect.InvocationTargetException;
import junit.framework.TestCase;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class AdviceNoRegularReturnTest {
    private static final String FOO = "foo";

    private final Class<?> type;

    public AdviceNoRegularReturnTest(Class<?> type) {
        this.type = type;
    }

    @Test
    public void testNoRegularReturnWithSkip() throws Exception {
        Class<?> type = new ByteBuddy().redefine(this.type).visit(Advice.to(AdviceNoRegularReturnTest.EnterAdviceSkip.class).on(ElementMatchers.named(AdviceNoRegularReturnTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceNoRegularReturnTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
    }

    @Test
    public void testNoRegularReturnWithoutHandler() throws Exception {
        Class<?> type = new ByteBuddy().redefine(this.type).visit(Advice.to(AdviceNoRegularReturnTest.ExitAdviceWithoutHandler.class).on(ElementMatchers.named(AdviceNoRegularReturnTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceNoRegularReturnTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
    }

    @Test
    public void testNoRegularReturnWithHandler() throws Exception {
        Class<?> type = new ByteBuddy().redefine(this.type).visit(Advice.to(AdviceNoRegularReturnTest.ExitAdviceWithHandler.class).on(ElementMatchers.named(AdviceNoRegularReturnTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceNoRegularReturnTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
    }

    private static class EnterAdviceSkip {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static boolean enter() {
            return false;
        }
    }

    private static class ExitAdviceWithoutHandler {
        @Advice.OnMethodExit
        private static void exit() {
            /* empty */
        }
    }

    private static class ExitAdviceWithHandler {
        @Advice.OnMethodExit(onThrowable = RuntimeException.class)
        private static void exit() {
            /* empty */
        }
    }

    public static class VoidSample {
        public void foo() {
            throw new RuntimeException();
        }
    }

    public static class BooleanSample {
        public boolean foo() {
            throw new RuntimeException();
        }
    }

    public static class ByteSample {
        public byte foo() {
            throw new RuntimeException();
        }
    }

    public static class ShortSample {
        public short foo() {
            throw new RuntimeException();
        }
    }

    public static class CharacterSample {
        public char foo() {
            throw new RuntimeException();
        }
    }

    public static class IntegerSample {
        public int foo() {
            throw new RuntimeException();
        }
    }

    public static class LongSample {
        public long foo() {
            throw new RuntimeException();
        }
    }

    public static class FloatSample {
        public float foo() {
            throw new RuntimeException();
        }
    }

    public static class DoubleSample {
        public double foo() {
            throw new RuntimeException();
        }
    }

    public static class ReferenceSample {
        public Object foo() {
            throw new RuntimeException();
        }
    }
}

