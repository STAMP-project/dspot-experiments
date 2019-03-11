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
public class AdviceBoxedParameterAssignmentTest {
    private static final String FOO = "foo";

    private static final byte NUMERIC_VALUE = 42;

    private final Class<?> type;

    private final Object expected;

    private final Object[] provided;

    private final Class<?>[] parameterTypes;

    public AdviceBoxedParameterAssignmentTest(Class<?> type, Object expected, Object[] provided, Class<?>[] parameterTypes) {
        this.type = type;
        this.expected = expected;
        this.provided = provided;
        this.parameterTypes = parameterTypes;
    }

    @Test
    public void testAssignment() throws Exception {
        Class<?> dynamicType = new ByteBuddy().redefine(type).visit(Advice.to(type).on(ElementMatchers.named(AdviceBoxedParameterAssignmentTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AdviceBoxedParameterAssignmentTest.FOO, parameterTypes).invoke(dynamicType.getDeclaredConstructor().newInstance(), provided), CoreMatchers.is(expected));
    }

    @SuppressWarnings("all")
    public static class VoidAssignment {
        public void foo() {
            /* empty */
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[0];
        }
    }

    @SuppressWarnings("all")
    public static class BooleanAssignment {
        public boolean foo(boolean value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ true };
        }
    }

    @SuppressWarnings("all")
    public static class ByteAssignment {
        public byte foo(byte value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ ((byte) (AdviceBoxedParameterAssignmentTest.NUMERIC_VALUE)) };
        }
    }

    @SuppressWarnings("all")
    public static class ShortAssignment {
        public short foo(short value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ ((short) (AdviceBoxedParameterAssignmentTest.NUMERIC_VALUE)) };
        }
    }

    @SuppressWarnings("all")
    public static class CharacterAssignment {
        public char foo(char value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ ((char) (AdviceBoxedParameterAssignmentTest.NUMERIC_VALUE)) };
        }
    }

    @SuppressWarnings("all")
    public static class IntegerAssignment {
        public int foo(int value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ ((int) (AdviceBoxedParameterAssignmentTest.NUMERIC_VALUE)) };
        }
    }

    @SuppressWarnings("all")
    public static class LongAssignment {
        public long foo(long value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ ((long) (AdviceBoxedParameterAssignmentTest.NUMERIC_VALUE)) };
        }
    }

    @SuppressWarnings("all")
    public static class FloatAssignment {
        public float foo(float value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ ((float) (AdviceBoxedParameterAssignmentTest.NUMERIC_VALUE)) };
        }
    }

    @SuppressWarnings("all")
    public static class DoubleAssignment {
        public double foo(double value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ ((double) (AdviceBoxedParameterAssignmentTest.NUMERIC_VALUE)) };
        }
    }

    @SuppressWarnings("all")
    public static class ReferenceAssignment {
        public String foo(String value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ AdviceBoxedParameterAssignmentTest.FOO };
        }
    }

    @SuppressWarnings("all")
    public static class ReferenceAssignmentNoCast {
        public Object foo(Object value) {
            return value;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments(readOnly = false, typing = DYNAMIC)
        Object[] value) {
            value = new Object[]{ AdviceBoxedParameterAssignmentTest.FOO };
        }
    }
}

