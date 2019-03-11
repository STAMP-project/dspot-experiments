package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;


@RunWith(Parameterized.class)
public class AdviceBoxedReturnAssignmentTest {
    private static final String FOO = "foo";

    private static final byte NUMERIC_VALUE = 42;

    private final Class<?> type;

    private final Object expected;

    public AdviceBoxedReturnAssignmentTest(Class<?> type, Object expected) {
        this.type = type;
        this.expected = expected;
    }

    @Test
    public void testAssignment() throws Exception {
        Class<?> dynamicType = new ByteBuddy().redefine(type).visit(Advice.to(type).on(ElementMatchers.named(AdviceBoxedReturnAssignmentTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AdviceBoxedReturnAssignmentTest.FOO).invoke(dynamicType.getDeclaredConstructor().newInstance()), CoreMatchers.is(expected));
    }

    @SuppressWarnings("all")
    public static class VoidAssignment {
        public void foo() {
            /* empty */
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = AdviceBoxedReturnAssignmentTest.FOO;
        }
    }

    @SuppressWarnings("all")
    public static class BooleanAssignment {
        public boolean foo() {
            return false;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = true;
        }
    }

    @SuppressWarnings("all")
    public static class ByteAssignment {
        public byte foo() {
            return 0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = AdviceBoxedReturnAssignmentTest.NUMERIC_VALUE;
        }
    }

    @SuppressWarnings("all")
    public static class ShortAssignment {
        public short foo() {
            return 0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = ((short) (AdviceBoxedReturnAssignmentTest.NUMERIC_VALUE));
        }
    }

    @SuppressWarnings("all")
    public static class CharacterAssignment {
        public char foo() {
            return 0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = ((char) (AdviceBoxedReturnAssignmentTest.NUMERIC_VALUE));
        }
    }

    @SuppressWarnings("all")
    public static class IntegerAssignment {
        public int foo() {
            return 0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = ((int) (AdviceBoxedReturnAssignmentTest.NUMERIC_VALUE));
        }
    }

    @SuppressWarnings("all")
    public static class LongAssignment {
        public long foo() {
            return 0L;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = ((long) (AdviceBoxedReturnAssignmentTest.NUMERIC_VALUE));
        }
    }

    @SuppressWarnings("all")
    public static class FloatAssignment {
        public float foo() {
            return 0.0F;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = ((float) (AdviceBoxedReturnAssignmentTest.NUMERIC_VALUE));
        }
    }

    @SuppressWarnings("all")
    public static class DoubleAssignment {
        public double foo() {
            return 0.0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = ((double) (AdviceBoxedReturnAssignmentTest.NUMERIC_VALUE));
        }
    }

    @SuppressWarnings("all")
    public static class ReferenceAssignment {
        public String foo() {
            return null;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = AdviceBoxedReturnAssignmentTest.FOO;
        }
    }
}

