package net.bytebuddy.asm;


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
public class AdviceSkipOnNonDefaultValueTest {
    private static final String FOO = "foo";

    private final Class<?> type;

    private final Object value;

    public AdviceSkipOnNonDefaultValueTest(Class<?> type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Test
    public void testAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(this.type).visit(Advice.to(this.type).on(ElementMatchers.named(AdviceSkipOnNonDefaultValueTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceSkipOnNonDefaultValueTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(value));
    }

    @SuppressWarnings("unused")
    public static class BooleanAdvice {
        public boolean foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static boolean enter() {
            return true;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        boolean value) {
            if (value) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ByteAdvice {
        public byte foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static byte enter() {
            return 42;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        byte value) {
            if (value != 0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ShortAdvice {
        public short foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static short enter() {
            return 42;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        short value) {
            if (value != 0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterAdvice {
        public char foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static char enter() {
            return 42;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        char value) {
            if (value != 0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerAdvice {
        public int foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static int enter() {
            return 42;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        int value) {
            if (value != 0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class LongAdvice {
        public long foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static long enter() {
            return 42L;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        long value) {
            if (value != 0L) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class FloatAdvice {
        public float foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static float enter() {
            return 42.0F;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        float value) {
            if (value != 0.0F) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleAdvice {
        public double foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static double enter() {
            return 42.0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        double value) {
            if (value != 0.0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceAdvice {
        public Object foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static Object enter() {
            return new Object();
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        Object value) {
            if (value != null) {
                throw new AssertionError("Equality");
            }
        }
    }

    @SuppressWarnings("unused")
    public static class VoidAdvice {
        public void foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static boolean enter() {
            return true;
        }

        @Advice.OnMethodExit
        private static void exit() {
            /* do nothing */
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegateAdvice {
        public boolean foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static boolean enter() {
            return true;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        boolean value) {
            if (value) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegateAdvice {
        public byte foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static byte enter() {
            return 42;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        byte value) {
            if (value != 0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegateAdvice {
        public short foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static short enter() {
            return 42;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        short value) {
            if (value != 0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegateAdvice {
        public char foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static char enter() {
            return 42;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        char value) {
            if (value != 0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegateAdvice {
        public int foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static int enter() {
            return 42;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        int value) {
            if (value != 0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegateAdvice {
        public long foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static long enter() {
            return 42L;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        long value) {
            if (value != 0L) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegateAdvice {
        public float foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static float enter() {
            return 42.0F;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        float value) {
            if (value != 0.0F) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegateAdvice {
        public double foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static double enter() {
            return 42.0;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        double value) {
            if (value != 0.0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegateAdvice {
        public Object foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static Object enter() {
            return new Object();
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        Object value) {
            if (value != null) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class VoidDelegateAdvice {
        public void foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static boolean enter() {
            return true;
        }

        @Advice.OnMethodExit
        private static void exit() {
            /* do nothing */
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanWithOutExitAdvice {
        public boolean foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static boolean enter() {
            return true;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteWithOutExitAdvice {
        public byte foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static byte enter() {
            return 42;
        }
    }

    @SuppressWarnings("unused")
    public static class ShortWithOutExitAdvice {
        public short foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static short enter() {
            return 42;
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterWithOutExitAdvice {
        public char foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static char enter() {
            return 42;
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerWithOutExitAdvice {
        public int foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static int enter() {
            return 42;
        }
    }

    @SuppressWarnings("unused")
    public static class LongWithOutExitAdvice {
        public long foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static long enter() {
            return 42L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatWithOutExitAdvice {
        public float foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static float enter() {
            return 42.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleWithOutExitAdvice {
        public double foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static double enter() {
            return 42.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceWithOutExitAdvice {
        public Object foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static Object enter() {
            return new Object();
        }
    }

    @SuppressWarnings("unused")
    public static class VoidWithOutExitAdvice {
        public void foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static boolean enter() {
            return true;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegateWithOutExitAdvice {
        public boolean foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static boolean enter() {
            return true;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegateWithOutExitAdvice {
        public byte foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static byte enter() {
            return 42;
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegateWithOutExitAdvice {
        public short foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static short enter() {
            return 42;
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegateWithOutExitAdvice {
        public char foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static char enter() {
            return 42;
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegateWithOutExitAdvice {
        public int foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static int enter() {
            return 42;
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegateWithOutExitAdvice {
        public long foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static long enter() {
            return 42L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegateWithOutExitAdvice {
        public float foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static float enter() {
            return 42.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegateWithOutExitAdvice {
        public double foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static double enter() {
            return 42.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegateWithOutExitAdvice {
        public Object foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static Object enter() {
            return new Object();
        }
    }

    @SuppressWarnings("unused")
    public static class VoidDelegateWithOutExitAdvice {
        public void foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static boolean enter() {
            return true;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanNoSkipAdvice {
        public boolean foo() {
            return true;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static boolean enter() {
            return false;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        boolean value) {
            if (!value) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ByteNoSkipAdvice {
        public byte foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static byte enter() {
            return 0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        byte value) {
            if (value != 42) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ShortNoSkipAdvice {
        public short foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static short enter() {
            return 0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        short value) {
            if (value != 42) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterNoSkipAdvice {
        public char foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static char enter() {
            return 0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        char value) {
            if (value != 42) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerNoSkipAdvice {
        public int foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static int enter() {
            return 0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        int value) {
            if (value != 42) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class LongNoSkipAdvice {
        public long foo() {
            return 42L;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static long enter() {
            return 0L;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        long value) {
            if (value != 42L) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class FloatNoSkipAdvice {
        public float foo() {
            return 42.0F;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static float enter() {
            return 0.0F;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        float value) {
            if (value != 42.0F) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleNoSkipAdvice {
        public double foo() {
            return 42.0;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static double enter() {
            return 0.0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        double value) {
            if (value != 42.0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceNoSkipAdvice {
        public Object foo() {
            return AdviceSkipOnNonDefaultValueTest.FOO;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static Object enter() {
            return null;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        Object value) {
            if (!(value.equals(AdviceSkipOnNonDefaultValueTest.FOO))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class VoidNoSkipAdvice {
        public void foo() {
            /* do nothing */
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static boolean enter() {
            return false;
        }

        @Advice.OnMethodExit
        private static void exit() {
            /* do nothing */
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegateNoSkipAdvice {
        public boolean foo() {
            return true;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static boolean enter() {
            return false;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        boolean value) {
            if (!value) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegateNoSkipAdvice {
        public byte foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static byte enter() {
            return 0;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        byte value) {
            if (value != 42) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegateNoSkipAdvice {
        public short foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static short enter() {
            return 0;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        short value) {
            if (value != 42) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegateNoSkipAdvice {
        public char foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static char enter() {
            return 0;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        char value) {
            if (value != 42) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegateNoSkipAdvice {
        public int foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static int enter() {
            return 0;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        int value) {
            if (value != 42) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegateNoSkipAdvice {
        public long foo() {
            return 42L;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static long enter() {
            return 0L;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        long value) {
            if (value != 42L) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegateNoSkipAdvice {
        public float foo() {
            return 42.0F;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static float enter() {
            return 0.0F;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        float value) {
            if (value != 42.0F) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegateNoSkipAdvice {
        public double foo() {
            return 42.0;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static double enter() {
            return 0.0;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        double value) {
            if (value != 42.0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegateNoSkipAdvice {
        public Object foo() {
            return AdviceSkipOnNonDefaultValueTest.FOO;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static Object enter() {
            return null;
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return
        Object value) {
            if (!(value.equals(AdviceSkipOnNonDefaultValueTest.FOO))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class VoidDelegateNoSkipAdvice {
        public void foo() {
            /* do nothing */
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static boolean enter() {
            return false;
        }

        @Advice.OnMethodExit
        private static void exit() {
            /* do nothing */
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanWithOutExitNoSkipAdvice {
        public boolean foo() {
            return true;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static boolean enter() {
            return false;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteWithOutExitNoSkipAdvice {
        public byte foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static byte enter() {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public static class ShortWithOutExitNoSkipAdvice {
        public short foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static short enter() {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterWithOutExitNoSkipAdvice {
        public char foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static char enter() {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerWithOutExitNoSkipAdvice {
        public int foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static int enter() {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongWithOutExitNoSkipAdvice {
        public long foo() {
            return 42L;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static long enter() {
            return 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatWithOutExitNoSkipAdvice {
        public float foo() {
            return 42.0F;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static float enter() {
            return 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleWithOutExitNoSkipAdvice {
        public double foo() {
            return 42.0;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static double enter() {
            return 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceWithOutExitNoSkipAdvice {
        public Object foo() {
            return AdviceSkipOnNonDefaultValueTest.FOO;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static Object enter() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class VoidWithOutExitNoSkipAdvice {
        public void foo() {
            /* do nothing */
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static boolean enter() {
            return false;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegateWithOutExitNoSkipAdvice {
        public boolean foo() {
            return true;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static boolean enter() {
            return false;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegateWithOutExitNoSkipAdvice {
        public byte foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static byte enter() {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegateWithOutExitNoSkipAdvice {
        public short foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static short enter() {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegateWithOutExitNoSkipAdvice {
        public char foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static char enter() {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegateWithOutExitNoSkipAdvice {
        public int foo() {
            return 42;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static int enter() {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegateWithOutExitNoSkipAdvice {
        public long foo() {
            return 42L;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static long enter() {
            return 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegateWithOutExitNoSkipAdvice {
        public float foo() {
            return 42.0F;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static float enter() {
            return 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegateWithOutExitNoSkipAdvice {
        public double foo() {
            return 42.0;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static double enter() {
            return 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegateWithOutExitNoSkipAdvice {
        public Object foo() {
            return AdviceSkipOnNonDefaultValueTest.FOO;
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static Object enter() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class VoidDelegateWithOutExitNoSkipAdvice {
        public void foo() {
            /* do nothing */
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, inline = false)
        private static boolean enter() {
            return false;
        }
    }
}

