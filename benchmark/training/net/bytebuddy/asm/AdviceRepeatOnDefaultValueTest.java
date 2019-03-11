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
public class AdviceRepeatOnDefaultValueTest {
    private static final String FOO = "foo";

    private final Class<?> type;

    public AdviceRepeatOnDefaultValueTest(Class<?> type) {
        this.type = type;
    }

    @Test
    public void testAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(this.type).visit(Advice.to(this.type).on(ElementMatchers.named(AdviceRepeatOnDefaultValueTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceRepeatOnDefaultValueTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (3))));
    }

    @SuppressWarnings("unused")
    public static class BooleanAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static boolean enter(@Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return false;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static byte enter(@Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static short enter(@Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static char enter(@Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static int enter(@Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static long enter(@Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return 0L;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static float enter(@Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return 0.0F;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static double enter(@Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return 0.0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static Object enter(@Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return null;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static boolean enter(@Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return false;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static byte enter(@Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static short enter(@Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static char enter(@Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static int enter(@Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static long enter(@Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return 0L;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static float enter(@Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return 0.0F;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static double enter(@Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return 0.0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter
        private static Object enter(@Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return null;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static boolean enter(@Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return false;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static byte enter(@Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static short enter(@Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static char enter(@Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static int enter(@Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static long enter(@Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return 0L;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static float enter(@Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return 0.0F;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static double enter(@Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return 0.0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static Object enter(@Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return null;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static boolean enter(@Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return false;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static byte enter(@Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static short enter(@Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static char enter(@Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static int enter(@Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static long enter(@Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return 0L;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static float enter(@Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return 0.0F;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static double enter(@Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return 0.0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(suppress = Exception.class)
        private static Object enter(@Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return null;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, suppress = Exception.class, onThrowable = Exception.class)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegatingAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegatingAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegatingAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegatingAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegatingAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegatingAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegatingAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegatingAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegatingAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegatingAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false, inline = false)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegatingAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false, inline = false)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegatingAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false, inline = false)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegatingAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false, inline = false)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegatingAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false, inline = false)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegatingAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false, inline = false)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegatingAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false, inline = false)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegatingAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false, inline = false)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegatingAdviceWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, backupArguments = false, inline = false)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegatingAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static boolean enter(@Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return false;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegatingAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static byte enter(@Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegatingAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static short enter(@Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegatingAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static char enter(@Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegatingAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static int enter(@Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegatingAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static long enter(@Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return 0L;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegatingAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static float enter(@Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return 0.0F;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegatingAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static double enter(@Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return 0.0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegatingAdviceWithEnterAdvice {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static Object enter(@Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return null;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static boolean enter(@Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return false;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static byte enter(@Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static short enter(@Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static char enter(@Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static int enter(@Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static long enter(@Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return 0L;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static float enter(@Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return 0.0F;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static double enter(@Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return 0.0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackup {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false)
        private static Object enter(@Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return null;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegatingAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static boolean enter(@Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return false;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegatingAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static byte enter(@Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegatingAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static short enter(@Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegatingAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static char enter(@Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegatingAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static int enter(@Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegatingAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static long enter(@Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return 0L;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegatingAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static float enter(@Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return 0.0F;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegatingAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static double enter(@Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return 0.0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegatingAdviceWithEnterAdviceAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static Object enter(@Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return null;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static boolean enter(@Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return false;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static boolean exit(@Advice.Return
        int count, @Advice.Exit
        boolean exit) {
            if (exit) {
                throw new AssertionError();
            }
            return count >= 3;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static byte enter(@Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static byte exit(@Advice.Return
        int count, @Advice.Exit
        byte exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((byte) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static short enter(@Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static short exit(@Advice.Return
        int count, @Advice.Exit
        short exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((short) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static char enter(@Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static char exit(@Advice.Return
        int count, @Advice.Exit
        char exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return ((char) (count >= 3 ? 1 : 0));
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static int enter(@Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return 0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static int exit(@Advice.Return
        int count, @Advice.Exit
        int exit) {
            if (exit != 0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1 : 0;
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static long enter(@Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return 0L;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static long exit(@Advice.Return
        int count, @Advice.Exit
        long exit) {
            if (exit != 0L) {
                throw new AssertionError();
            }
            return count >= 3 ? 1L : 0L;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static float enter(@Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return 0.0F;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static float exit(@Advice.Return
        int count, @Advice.Exit
        float exit) {
            if (exit != 0.0F) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0F : 0.0F;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static double enter(@Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return 0.0;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static double exit(@Advice.Return
        int count, @Advice.Exit
        double exit) {
            if (exit != 0.0) {
                throw new AssertionError();
            }
            return count >= 3 ? 1.0 : 0.0;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegatingAdviceWithEnterAdviceAndWithoutArgumentBackupAndExceptionHandler {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodEnter(inline = false, suppress = Exception.class)
        private static Object enter(@Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return null;
        }

        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class, inline = false, suppress = Exception.class, onThrowable = Exception.class)
        private static Object exit(@Advice.Return
        int count, @Advice.Exit
        Object exit) {
            if (exit != null) {
                throw new AssertionError();
            }
            return count >= 3 ? AdviceRepeatOnDefaultValueTest.FOO : null;
        }
    }
}

