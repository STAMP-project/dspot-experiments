package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AdviceIllegalTypeTest {
    private static final String FOO = "foo";

    private static final byte VALUE = 42;

    private static final boolean BOOLEAN = true;

    private final Class<?> type;

    public AdviceIllegalTypeTest(Class<?> type) {
        this.type = type;
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalAssignment() throws Exception {
        new ByteBuddy().redefine(type).visit(Advice.to(type).on(ElementMatchers.named(AdviceIllegalTypeTest.FOO))).make();
    }

    public static class BooleanAdvice {
        void foo(boolean value) {
            /* empty */
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0)
        boolean value) {
            value = AdviceIllegalTypeTest.BOOLEAN;
        }
    }

    public static class ByteAdvice {
        void foo(byte value) {
            /* empty */
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0)
        byte value) {
            value = AdviceIllegalTypeTest.VALUE;
        }
    }

    public static class ShortAdvice {
        void foo(short value) {
            /* empty */
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0)
        short value) {
            value = AdviceIllegalTypeTest.VALUE;
        }
    }

    public static class CharacterAdvice {
        void foo(char value) {
            /* empty */
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0)
        char value) {
            value = AdviceIllegalTypeTest.VALUE;
        }
    }

    public static class IntegerAdvice {
        void foo(int value) {
            /* empty */
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0)
        int value) {
            value = AdviceIllegalTypeTest.VALUE;
        }
    }

    public static class LongAdvice {
        void foo(long value) {
            /* empty */
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0)
        long value) {
            value = AdviceIllegalTypeTest.VALUE;
        }
    }

    public static class FloatAdvice {
        void foo(float value) {
            /* empty */
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0)
        float value) {
            value = AdviceIllegalTypeTest.VALUE;
        }
    }

    public static class DoubleAdvice {
        void foo(double value) {
            /* empty */
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0)
        double value) {
            value = AdviceIllegalTypeTest.VALUE;
        }
    }

    public static class ReferenceAdvice {
        void foo(Object value) {
            /* empty */
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0)
        Object value) {
            value = AdviceIllegalTypeTest.FOO;
        }
    }
}

