package net.bytebuddy.asm;


import OpenedClassReader.ASM_API;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import junit.framework.TestCase;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;


@RunWith(Parameterized.class)
public class AdviceTypeTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String ENTER = "enter";

    private static final String EXIT = "exit";

    private static final String exception = "exception";

    private static final String FIELD = "field";

    private static final String MUTATED = "mutated";

    private static final String STATIC_FIELD = "staticField";

    private static final String MUTATED_STATIC_FIELD = "mutatedStatic";

    private static final byte VALUE = 42;

    private static final boolean BOOLEAN = true;

    private static final byte NUMERIC_DEFAULT = 0;

    private final Class<?> advice;

    private final Class<?> type;

    private final Serializable value;

    public AdviceTypeTest(Class<?> advice, Class<?> type, Serializable value) {
        this.advice = advice;
        this.type = type;
        this.value = value;
    }

    @Test
    public void testAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(advice).visit(new AdviceTypeTest.SerializationAssertion()).visit(Advice.withCustomMapping().bind(AdviceTypeTest.CustomAnnotation.class, value).to(advice).on(ElementMatchers.named(AdviceTypeTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTypeTest.FOO, this.type, this.type).invoke(type.getDeclaredConstructor().newInstance(), value, value), CoreMatchers.is(((Object) (value))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTypeTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTypeTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithException() throws Exception {
        Class<?> type = new ByteBuddy().redefine(advice).visit(new AdviceTypeTest.SerializationAssertion()).visit(Advice.withCustomMapping().bind(AdviceTypeTest.CustomAnnotation.class, value).to(advice).on(ElementMatchers.named(AdviceTypeTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        type.getDeclaredField(AdviceTypeTest.exception).set(null, true);
        try {
            MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTypeTest.BAR, this.type, this.type).invoke(type.getDeclaredConstructor().newInstance(), value, value), CoreMatchers.is(((Object) (value))));
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTypeTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTypeTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithProperty() throws Exception {
        if ((type) == (Void.class)) {
            return;// No void property on annotations.

        }
        Class<?> type = new ByteBuddy().redefine(advice).visit(new AdviceTypeTest.SerializationAssertion()).visit(Advice.withCustomMapping().bindProperty(AdviceTypeTest.CustomAnnotation.class, ((this.type.getSimpleName().toLowerCase(Locale.US)) + "Value")).to(advice).on(ElementMatchers.named(AdviceTypeTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTypeTest.FOO, this.type, this.type).invoke(type.getDeclaredConstructor().newInstance(), value, value), CoreMatchers.is(((Object) (value))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTypeTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTypeTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @SuppressWarnings("unused")
    public static class VoidInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        public void foo(Void ignoredArgument, Void ignoredMutableArgument) {
            /* empty */
        }

        public void bar(Void ignoredArgument, Void ignoredMutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static void enter(@Advice.AllArguments
        Object[] boxed, @Advice.StubValue
        Object stubValue, @Advice.Local(net.bytebuddy.asm.FOO.class)
        Void local, @AdviceTypeTest.CustomAnnotation
        Void custom) {
            if ((((boxed.length) != 2) || ((boxed[0]) != null)) || ((boxed[1]) != null)) {
                throw new AssertionError();
            }
            if (stubValue != null) {
                throw new AssertionError();
            }
            if (local != null) {
                throw new AssertionError();
            }
            if (custom != null) {
                throw new AssertionError();
            }
            (AdviceTypeTest.VoidInlineAdvice.enter)++;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static void exit(@Advice.Thrown
        Throwable throwable, @Advice.Return(typing = DYNAMIC)
        Object boxedReturn, @Advice.AllArguments
        Object[] boxed, @Advice.Local(net.bytebuddy.asm.FOO.class)
        Void local, @AdviceTypeTest.CustomAnnotation
        Void custom) {
            if (!(AdviceTypeTest.VoidInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null)) {
                throw new AssertionError();
            }
            if (boxedReturn != null) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || ((boxed[0]) != null)) || ((boxed[1]) != null)) {
                throw new AssertionError();
            }
            if (local != null) {
                throw new AssertionError();
            }
            if (custom != null) {
                throw new AssertionError();
            }
            (AdviceTypeTest.VoidInlineAdvice.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class VoidDelegatingAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        public void foo(Void ignoredArgument, Void ignored) {
            /* empty */
        }

        public void bar(Void ignoredArgument, Void ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static void enter(@Advice.AllArguments
        Object[] boxed, @Advice.StubValue
        Object stubValue, @AdviceTypeTest.CustomAnnotation
        Void custom) {
            if ((((boxed.length) != 2) || ((boxed[0]) != null)) || ((boxed[1]) != null)) {
                throw new AssertionError();
            }
            if (stubValue != null) {
                throw new AssertionError();
            }
            if (custom != null) {
                throw new AssertionError();
            }
            (AdviceTypeTest.VoidDelegatingAdvice.enter)++;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static void exit(@Advice.Thrown
        Throwable throwable, @Advice.Return(typing = DYNAMIC)
        Object boxedReturn, @Advice.AllArguments
        Object[] boxed, @AdviceTypeTest.CustomAnnotation
        Void custom) {
            if (!(AdviceTypeTest.VoidDelegatingAdvice.exception ? throwable instanceof RuntimeException : throwable == null)) {
                throw new AssertionError();
            }
            if (boxedReturn != null) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || ((boxed[0]) != null)) || ((boxed[1]) != null)) {
                throw new AssertionError();
            }
            if (custom != null) {
                throw new AssertionError();
            }
            (AdviceTypeTest.VoidDelegatingAdvice.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private boolean field = AdviceTypeTest.BOOLEAN;

        private boolean mutated = AdviceTypeTest.BOOLEAN;

        private static boolean staticField = AdviceTypeTest.BOOLEAN;

        private static boolean mutatedStatic = AdviceTypeTest.BOOLEAN;

        public boolean foo(boolean argument, boolean mutableArgument) {
            return argument;
        }

        public boolean bar(boolean argument, boolean mutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static boolean enter(@Advice.Unused
        boolean value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        boolean argument, @Advice.Argument(value = 1, readOnly = false)
        boolean mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        boolean field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        boolean staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        boolean mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        boolean mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        boolean local, @AdviceTypeTest.CustomAnnotation
        boolean custom) {
            if (value) {
                throw new AssertionError();
            }
            value = AdviceTypeTest.BOOLEAN;
            if (value) {
                throw new AssertionError();
            }
            if (((Boolean) (stubValue))) {
                throw new AssertionError();
            }
            if ((!argument) || (!mutableArgument)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.BOOLEAN)))) || (!(boxed[1].equals(AdviceTypeTest.BOOLEAN)))) {
                throw new AssertionError();
            }
            mutableArgument = false;
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.BOOLEAN)))) || (!(boxed[1].equals(false)))) {
                throw new AssertionError();
            }
            if ((((!field) || (!mutated)) || (!staticField)) || (!mutatedStatic)) {
                throw new AssertionError();
            }
            mutated = mutatedStatic = false;
            if (local) {
                throw new AssertionError();
            }
            local = true;
            if (!local) {
                throw new AssertionError();
            }
            if (!custom) {
                throw new AssertionError();
            }
            (AdviceTypeTest.BooleanInlineAdvice.enter)++;
            return AdviceTypeTest.BOOLEAN;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static boolean exit(@Advice.Return
        boolean result, @Advice.StubValue
        Object stubValue, @Advice.Enter
        boolean enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        boolean argument, @Advice.AllArguments
        Object[] boxed, @Advice.Argument(value = 1, readOnly = false)
        boolean mutableArgument, @Advice.FieldValue(AdviceTypeTest.FIELD)
        boolean field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        boolean staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        boolean mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        boolean mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        boolean local, @AdviceTypeTest.CustomAnnotation
        boolean custom) {
            if (((result == (AdviceTypeTest.BooleanInlineAdvice.exception)) || (!enter)) || (!(AdviceTypeTest.BooleanInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (boxedReturn.equals(AdviceTypeTest.BooleanInlineAdvice.exception)) {
                throw new AssertionError();
            }
            if ((!argument) || mutableArgument) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.BOOLEAN)))) || (!(boxed[1].equals(false)))) {
                throw new AssertionError();
            }
            if ((((!field) || mutated) || (!staticField)) || mutatedStatic) {
                throw new AssertionError();
            }
            if (!local) {
                throw new AssertionError();
            }
            if (!custom) {
                throw new AssertionError();
            }
            (AdviceTypeTest.BooleanInlineAdvice.exit)++;
            return AdviceTypeTest.BOOLEAN;
        }
    }

    @SuppressWarnings("unused")
    public static class BooleanDelegationAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private boolean field = AdviceTypeTest.BOOLEAN;

        private static boolean staticField = AdviceTypeTest.BOOLEAN;

        public boolean foo(boolean argument, boolean ignored) {
            return argument;
        }

        public boolean bar(boolean argument, boolean ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static boolean enter(@Advice.Unused
        boolean value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        boolean argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        boolean field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        boolean staticField, @AdviceTypeTest.CustomAnnotation
        boolean custom) {
            if (value) {
                throw new AssertionError();
            }
            if (((Boolean) (stubValue))) {
                throw new AssertionError();
            }
            if (!argument) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.BOOLEAN)))) || (!(boxed[1].equals(AdviceTypeTest.BOOLEAN)))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.BOOLEAN)))) || (!(boxed[1].equals(AdviceTypeTest.BOOLEAN)))) {
                throw new AssertionError();
            }
            if ((!field) || (!staticField)) {
                throw new AssertionError();
            }
            if (!custom) {
                throw new AssertionError();
            }
            (AdviceTypeTest.BooleanDelegationAdvice.enter)++;
            return AdviceTypeTest.BOOLEAN;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static boolean exit(@Advice.Return
        boolean result, @Advice.Enter
        boolean enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        boolean argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        boolean field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        boolean staticField, @AdviceTypeTest.CustomAnnotation
        boolean custom) {
            if (((result == (AdviceTypeTest.BooleanDelegationAdvice.exception)) || (!enter)) || (!(AdviceTypeTest.BooleanDelegationAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (boxedReturn.equals(AdviceTypeTest.BooleanDelegationAdvice.exception)) {
                throw new AssertionError();
            }
            if (!argument) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.BOOLEAN)))) || (!(boxed[1].equals(AdviceTypeTest.BOOLEAN)))) {
                throw new AssertionError();
            }
            if ((!field) || (!staticField)) {
                throw new AssertionError();
            }
            if (!custom) {
                throw new AssertionError();
            }
            (AdviceTypeTest.BooleanDelegationAdvice.exit)++;
            return AdviceTypeTest.BOOLEAN;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private byte field = AdviceTypeTest.VALUE;

        private byte mutated = AdviceTypeTest.VALUE;

        private static byte staticField = AdviceTypeTest.VALUE;

        private static byte mutatedStatic = AdviceTypeTest.VALUE;

        public byte foo(byte argument, byte mutableArgument) {
            return argument;
        }

        public byte bar(byte argument, byte mutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static byte enter(@Advice.Unused
        byte value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        byte argument, @Advice.Argument(value = 1, readOnly = false)
        byte mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        byte field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        byte staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        byte mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        byte mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        byte local, @AdviceTypeTest.CustomAnnotation
        byte custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            value = AdviceTypeTest.VALUE;
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Byte) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.VALUE)))) || (!(boxed[1].equals(AdviceTypeTest.VALUE)))) {
                throw new AssertionError();
            }
            mutableArgument = (AdviceTypeTest.VALUE) * 2;
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.VALUE)))) || (!(boxed[1].equals(((byte) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != (AdviceTypeTest.VALUE))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            mutated = mutatedStatic = (AdviceTypeTest.VALUE) * 2;
            if (local != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            local = AdviceTypeTest.VALUE;
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ByteInlineAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static byte exit(@Advice.Return
        byte result, @Advice.Enter
        byte enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        byte argument, @Advice.Argument(value = 1, readOnly = false)
        byte mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        byte field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        byte staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        byte mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        byte mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        byte local, @AdviceTypeTest.CustomAnnotation
        byte custom) {
            if (((result != (AdviceTypeTest.ByteInlineAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.ByteInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals((AdviceTypeTest.ByteInlineAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE)))) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.VALUE)))) || (!(boxed[1].equals(((byte) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != ((AdviceTypeTest.VALUE) * 2))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ByteInlineAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class ByteDelegationAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private byte field = AdviceTypeTest.VALUE;

        private static byte staticField = AdviceTypeTest.VALUE;

        public byte foo(byte argument, byte ignored) {
            return argument;
        }

        public byte bar(byte argument, byte ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static byte enter(@Advice.Unused
        byte value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        byte argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        byte field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        byte staticField, @AdviceTypeTest.CustomAnnotation
        byte custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Byte) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.VALUE)))) || (!(boxed[1].equals(AdviceTypeTest.VALUE)))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ByteDelegationAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static byte exit(@Advice.Return
        byte result, @Advice.Enter
        byte enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        byte argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        byte field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        byte staticField, @AdviceTypeTest.CustomAnnotation
        byte custom) {
            if (((result != (AdviceTypeTest.ByteDelegationAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.ByteDelegationAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals((AdviceTypeTest.ByteDelegationAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE)))) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.VALUE)))) || (!(boxed[1].equals(AdviceTypeTest.VALUE)))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ByteDelegationAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class ShortInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private short field = AdviceTypeTest.VALUE;

        private short mutated = AdviceTypeTest.VALUE;

        private static short staticField = AdviceTypeTest.VALUE;

        private static short mutatedStatic = AdviceTypeTest.VALUE;

        public short foo(short argument, short mutableArgument) {
            return argument;
        }

        public short bar(short argument, short mutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static short enter(@Advice.Unused
        short value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        short argument, @Advice.Argument(value = 1, readOnly = false)
        short mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        short field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        short staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        short mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        short mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        short local, @AdviceTypeTest.CustomAnnotation
        short custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            value = AdviceTypeTest.VALUE;
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Short) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((short) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((short) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            mutableArgument = (AdviceTypeTest.VALUE) * 2;
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((short) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((short) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != (AdviceTypeTest.VALUE))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            mutated = mutatedStatic = (AdviceTypeTest.VALUE) * 2;
            if (local != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            local = AdviceTypeTest.VALUE;
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ShortInlineAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static short exit(@Advice.Return
        short result, @Advice.Enter
        short enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        short argument, @Advice.Argument(value = 1, readOnly = false)
        short mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        short field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        short staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        short mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        short mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        short local, @AdviceTypeTest.CustomAnnotation
        short custom) {
            if (((result != (AdviceTypeTest.ShortInlineAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.ShortInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((short) (AdviceTypeTest.ShortInlineAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((short) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((short) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != ((AdviceTypeTest.VALUE) * 2))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ShortInlineAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class ShortDelegationAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private short field = AdviceTypeTest.VALUE;

        private static short staticField = AdviceTypeTest.VALUE;

        public short foo(short argument, short ignored) {
            return argument;
        }

        public short bar(short argument, short ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static short enter(@Advice.Unused
        short value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        short argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        short field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        short staticField, @AdviceTypeTest.CustomAnnotation
        short custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Short) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((short) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((short) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ShortDelegationAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static short exit(@Advice.Return
        short result, @Advice.Enter
        short enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        short argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        short field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        short staticField, @AdviceTypeTest.CustomAnnotation
        short custom) {
            if (((result != (AdviceTypeTest.ShortDelegationAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.ShortDelegationAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((short) (AdviceTypeTest.ShortDelegationAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((short) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((short) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ShortDelegationAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private char field = AdviceTypeTest.VALUE;

        private char mutated = AdviceTypeTest.VALUE;

        private static char staticField = AdviceTypeTest.VALUE;

        private static char mutatedStatic = AdviceTypeTest.VALUE;

        public char foo(char argument, char mutableArgument) {
            return argument;
        }

        public char bar(char argument, char mutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static char enter(@Advice.Unused
        char value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        char argument, @Advice.Argument(value = 1, readOnly = false)
        char mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        char field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        char staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        char mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        char mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        char local, @AdviceTypeTest.CustomAnnotation
        char custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            value = AdviceTypeTest.VALUE;
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Character) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((char) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((char) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            mutableArgument = (AdviceTypeTest.VALUE) * 2;
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((char) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((char) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != (AdviceTypeTest.VALUE))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            mutated = mutatedStatic = (AdviceTypeTest.VALUE) * 2;
            if (local != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            local = AdviceTypeTest.VALUE;
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.CharacterInlineAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static char exit(@Advice.Return
        char result, @Advice.Enter
        char enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        char argument, @Advice.Argument(value = 1, readOnly = false)
        char mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        char field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        char staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        char mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        char mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        char local, @AdviceTypeTest.CustomAnnotation
        char custom) {
            if (((result != (AdviceTypeTest.CharacterInlineAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.CharacterInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((char) (AdviceTypeTest.CharacterInlineAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((char) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((char) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != ((AdviceTypeTest.VALUE) * 2))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.CharacterInlineAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class CharacterDelegationAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private char field = AdviceTypeTest.VALUE;

        private static char staticField = AdviceTypeTest.VALUE;

        public char foo(char argument, char ignored) {
            return argument;
        }

        public char bar(char argument, char ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static char enter(@Advice.Unused
        char value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        char argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        char field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        char staticField, @AdviceTypeTest.CustomAnnotation
        char custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Character) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((char) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((char) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.CharacterDelegationAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static char exit(@Advice.Return
        char result, @Advice.Enter
        char enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        char argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        char field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        char staticField, @AdviceTypeTest.CustomAnnotation
        char custom) {
            if (((result != (AdviceTypeTest.CharacterDelegationAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.CharacterDelegationAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((char) (AdviceTypeTest.CharacterDelegationAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((char) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((char) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.CharacterDelegationAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private int field = AdviceTypeTest.VALUE;

        private int mutated = AdviceTypeTest.VALUE;

        private static int staticField = AdviceTypeTest.VALUE;

        private static int mutatedStatic = AdviceTypeTest.VALUE;

        public int foo(int argument, int mutableArgument) {
            return argument;
        }

        public int bar(int argument, int mutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static int enter(@Advice.Unused
        int value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        int argument, @Advice.Argument(value = 1, readOnly = false)
        int mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        int field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        int staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        int mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        int mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        int local, @AdviceTypeTest.CustomAnnotation
        int custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            value = AdviceTypeTest.VALUE;
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Integer) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((int) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((int) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            mutableArgument = (AdviceTypeTest.VALUE) * 2;
            mutableArgument++;
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((int) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals((((AdviceTypeTest.VALUE) * 2) + 1))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != (AdviceTypeTest.VALUE))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            mutated = mutatedStatic = (AdviceTypeTest.VALUE) * 2;
            mutated++;
            mutatedStatic++;
            if (local != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            local = AdviceTypeTest.VALUE;
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            local++;
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.IntegerInlineAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static int exit(@Advice.Return
        int result, @Advice.Enter
        int enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        int argument, @Advice.Argument(value = 1, readOnly = false)
        int mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        int field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        int staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        int mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        int mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        int local, @AdviceTypeTest.CustomAnnotation
        int custom) {
            if (((result != (AdviceTypeTest.IntegerInlineAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.IntegerInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((int) (AdviceTypeTest.IntegerInlineAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != (((AdviceTypeTest.VALUE) * 2) + 1))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((int) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals((((AdviceTypeTest.VALUE) * 2) + 1))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != (((AdviceTypeTest.VALUE) * 2) + 1))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != (((AdviceTypeTest.VALUE) * 2) + 1))) {
                throw new AssertionError();
            }
            if (local != ((AdviceTypeTest.VALUE) + 1)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.IntegerInlineAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class IntegerDelegationAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private int field = AdviceTypeTest.VALUE;

        private static int staticField = AdviceTypeTest.VALUE;

        public int foo(int argument, int ignored) {
            return argument;
        }

        public int bar(int argument, int ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static int enter(@Advice.Unused
        int value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        int argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        int field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        int staticField, @AdviceTypeTest.CustomAnnotation
        int custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Integer) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((int) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((int) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.IntegerDelegationAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static int exit(@Advice.Return
        int result, @Advice.Enter
        int enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        int argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        int field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        int staticField, @AdviceTypeTest.CustomAnnotation
        int custom) {
            if (((result != (AdviceTypeTest.IntegerDelegationAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.IntegerDelegationAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((int) (AdviceTypeTest.IntegerDelegationAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((int) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((int) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.IntegerDelegationAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class LongInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private long field = AdviceTypeTest.VALUE;

        private long mutated = AdviceTypeTest.VALUE;

        private static long staticField = AdviceTypeTest.VALUE;

        private static long mutatedStatic = AdviceTypeTest.VALUE;

        public long foo(long argument, long mutableArgument) {
            return argument;
        }

        public long bar(long argument, long mutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static long enter(@Advice.Unused
        long value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        long argument, @Advice.Argument(value = 1, readOnly = false)
        long mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        long field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        long staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        long mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        long mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        long local, @AdviceTypeTest.CustomAnnotation
        long custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            value = AdviceTypeTest.VALUE;
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Long) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((long) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((long) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            mutableArgument = (AdviceTypeTest.VALUE) * 2;
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((long) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((long) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != (AdviceTypeTest.VALUE))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            mutated = mutatedStatic = (AdviceTypeTest.VALUE) * 2;
            if (local != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            local = AdviceTypeTest.VALUE;
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.LongInlineAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static long exit(@Advice.Return
        long result, @Advice.Enter
        long enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        long argument, @Advice.Argument(value = 1, readOnly = false)
        long mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        long field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        long staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        long mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        long mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        long local, @AdviceTypeTest.CustomAnnotation
        long custom) {
            if (((result != (AdviceTypeTest.LongInlineAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.LongInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((long) (AdviceTypeTest.LongInlineAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((long) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((long) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != ((AdviceTypeTest.VALUE) * 2))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.LongInlineAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class LongDelegationAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private long field = AdviceTypeTest.VALUE;

        private static long staticField = AdviceTypeTest.VALUE;

        public long foo(long argument, long ignored) {
            return argument;
        }

        public long bar(long argument, long ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static long enter(@Advice.Unused
        long value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        long argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        long field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        long staticField, @AdviceTypeTest.CustomAnnotation
        long custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Long) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((long) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((long) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.LongDelegationAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static long exit(@Advice.Return
        long result, @Advice.Enter
        long enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        long argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        long field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        long staticField, @AdviceTypeTest.CustomAnnotation
        long custom) {
            if (((result != (AdviceTypeTest.LongDelegationAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.LongDelegationAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((long) (AdviceTypeTest.LongDelegationAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((long) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((long) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.LongDelegationAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private float field = AdviceTypeTest.VALUE;

        private float mutated = AdviceTypeTest.VALUE;

        private static float staticField = AdviceTypeTest.VALUE;

        private static float mutatedStatic = AdviceTypeTest.VALUE;

        public float foo(float argument, float mutableArgument) {
            return argument;
        }

        public float bar(float argument, float mutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static float enter(@Advice.Unused
        float value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        float argument, @Advice.Argument(value = 1, readOnly = false)
        float mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        float field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        float staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        float mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        float mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        float local, @AdviceTypeTest.CustomAnnotation
        float custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            value = AdviceTypeTest.VALUE;
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Float) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((float) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((float) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            mutableArgument = (AdviceTypeTest.VALUE) * 2;
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((float) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((float) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != (AdviceTypeTest.VALUE))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            mutated = mutatedStatic = (AdviceTypeTest.VALUE) * 2;
            if (local != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            local = AdviceTypeTest.VALUE;
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.FloatInlineAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static float exit(@Advice.Return
        float result, @Advice.Enter
        float enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        float argument, @Advice.Argument(value = 1, readOnly = false)
        float mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        float field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        float staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        float mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        float mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        float local, @AdviceTypeTest.CustomAnnotation
        float custom) {
            if (((result != (AdviceTypeTest.FloatInlineAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.FloatInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((float) (AdviceTypeTest.FloatInlineAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((float) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((float) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != ((AdviceTypeTest.VALUE) * 2))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.FloatInlineAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class FloatDelegationAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private float field = AdviceTypeTest.VALUE;

        private static float staticField = AdviceTypeTest.VALUE;

        public float foo(float argument, float ignored) {
            return argument;
        }

        public float bar(float argument, float ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static float enter(@Advice.Unused
        float value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        float argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        float field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        float staticField, @AdviceTypeTest.CustomAnnotation
        float custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Float) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((float) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((float) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.FloatDelegationAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static float exit(@Advice.Return
        float result, @Advice.Enter
        float enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        float argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        float field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        float staticField, @AdviceTypeTest.CustomAnnotation
        float custom) {
            if (((result != (AdviceTypeTest.FloatDelegationAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.FloatDelegationAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((float) (AdviceTypeTest.FloatDelegationAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((float) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((float) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.FloatDelegationAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private double field = AdviceTypeTest.VALUE;

        private double mutated = AdviceTypeTest.VALUE;

        private static double staticField = AdviceTypeTest.VALUE;

        private static double mutatedStatic = AdviceTypeTest.VALUE;

        public double foo(double argument, double mutableArgument) {
            return argument;
        }

        public double bar(double argument, double mutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static double enter(@Advice.Unused
        double value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        double argument, @Advice.Argument(value = 1, readOnly = false)
        double mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        double field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        double staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        double mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        double mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        double local, @AdviceTypeTest.CustomAnnotation
        double custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            value = AdviceTypeTest.VALUE;
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Double) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((double) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((double) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            mutableArgument = (AdviceTypeTest.VALUE) * 2;
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((double) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((double) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != (AdviceTypeTest.VALUE))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            mutated = mutatedStatic = (AdviceTypeTest.VALUE) * 2;
            if (local != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            local = AdviceTypeTest.VALUE;
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.DoubleInlineAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static double exit(@Advice.Return
        double result, @Advice.Enter
        double enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        double argument, @Advice.Argument(value = 1, readOnly = false)
        double mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        double field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        double staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        double mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        double mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        double local, @AdviceTypeTest.CustomAnnotation
        double custom) {
            if (((result != (AdviceTypeTest.DoubleInlineAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.DoubleInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((double) (AdviceTypeTest.DoubleInlineAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if ((argument != (AdviceTypeTest.VALUE)) || (mutableArgument != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((double) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((double) ((AdviceTypeTest.VALUE) * 2)))))) {
                throw new AssertionError();
            }
            if ((((field != (AdviceTypeTest.VALUE)) || (mutated != ((AdviceTypeTest.VALUE) * 2))) || (staticField != (AdviceTypeTest.VALUE))) || (mutatedStatic != ((AdviceTypeTest.VALUE) * 2))) {
                throw new AssertionError();
            }
            if (local != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.DoubleInlineAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class DoubleDelegationAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private double field = AdviceTypeTest.VALUE;

        private static double staticField = AdviceTypeTest.VALUE;

        public double foo(double argument, double ignored) {
            return argument;
        }

        public double bar(double argument, double ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static double enter(@Advice.Unused
        double value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        double argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        double field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        double staticField, @AdviceTypeTest.CustomAnnotation
        double custom) {
            if (value != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (((Double) (stubValue)) != (AdviceTypeTest.NUMERIC_DEFAULT)) {
                throw new AssertionError();
            }
            if (argument != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((double) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((double) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if ((field != (AdviceTypeTest.VALUE)) || (staticField != (AdviceTypeTest.VALUE))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.DoubleDelegationAdvice.enter)++;
            return (AdviceTypeTest.VALUE) * 2;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static double exit(@Advice.Return
        double result, @Advice.Enter
        double enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        double argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        double field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        double staticField, @AdviceTypeTest.CustomAnnotation
        double custom) {
            if (((result != (AdviceTypeTest.DoubleDelegationAdvice.exception ? 0 : AdviceTypeTest.VALUE)) || (enter != ((AdviceTypeTest.VALUE) * 2))) || (!(AdviceTypeTest.DoubleDelegationAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (!(boxedReturn.equals(((double) (AdviceTypeTest.DoubleDelegationAdvice.exception ? AdviceTypeTest.NUMERIC_DEFAULT : AdviceTypeTest.VALUE))))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(((double) (AdviceTypeTest.VALUE)))))) || (!(boxed[1].equals(((double) (AdviceTypeTest.VALUE)))))) {
                throw new AssertionError();
            }
            if (custom != (AdviceTypeTest.VALUE)) {
                throw new AssertionError();
            }
            (AdviceTypeTest.DoubleDelegationAdvice.exit)++;
            return AdviceTypeTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceInlineAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private Object field = AdviceTypeTest.FOO;

        private Object mutated = AdviceTypeTest.FOO;

        private static Object staticField = AdviceTypeTest.FOO;

        private static Object mutatedStatic = AdviceTypeTest.FOO;

        public Object foo(Object argument, Object mutableArgument) {
            return argument;
        }

        public Object bar(Object argument, Object mutableArgument) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        public static Object enter(@Advice.Unused
        Object value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        Object argument, @Advice.Argument(value = 1, readOnly = false)
        Object mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        Object field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        Object staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        Object mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        Object mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        Object local, @AdviceTypeTest.CustomAnnotation
        String custom) {
            if (value != null) {
                throw new AssertionError();
            }
            value = AdviceTypeTest.FOO;
            if (value != null) {
                throw new AssertionError();
            }
            if (stubValue != null) {
                throw new AssertionError();
            }
            if ((!(argument.equals(AdviceTypeTest.FOO))) || (!(mutableArgument.equals(AdviceTypeTest.FOO)))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.FOO)))) || (!(boxed[1].equals(AdviceTypeTest.FOO)))) {
                throw new AssertionError();
            }
            mutableArgument = AdviceTypeTest.BAR;
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.FOO)))) || (!(boxed[1].equals(AdviceTypeTest.BAR)))) {
                throw new AssertionError();
            }
            if ((((!(field.equals(AdviceTypeTest.FOO))) || (!(mutated.equals(AdviceTypeTest.FOO)))) || (!(staticField.equals(AdviceTypeTest.FOO)))) || (!(mutatedStatic.equals(AdviceTypeTest.FOO)))) {
                throw new AssertionError();
            }
            mutated = mutatedStatic = AdviceTypeTest.BAR;
            if (local != null) {
                throw new AssertionError();
            }
            local = AdviceTypeTest.BAR;
            if (!(local.equals(AdviceTypeTest.BAR))) {
                throw new AssertionError();
            }
            if (!(custom.equals(AdviceTypeTest.FOO))) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ReferenceInlineAdvice.enter)++;
            return (AdviceTypeTest.FOO) + (AdviceTypeTest.BAR);
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        public static Object exit(@Advice.Return
        Object result, @Advice.Enter
        Object enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        Object argument, @Advice.Argument(value = 1, readOnly = false)
        Object mutableArgument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        Object field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        Object staticField, @Advice.FieldValue(value = AdviceTypeTest.MUTATED, readOnly = false)
        Object mutated, @Advice.FieldValue(value = AdviceTypeTest.MUTATED_STATIC_FIELD, readOnly = false)
        Object mutatedStatic, @Advice.Local(net.bytebuddy.asm.FOO.class)
        Object local, @AdviceTypeTest.CustomAnnotation
        String custom) {
            if (((AdviceTypeTest.ReferenceInlineAdvice.exception ? result != null : !(result.equals(AdviceTypeTest.FOO))) || (!(enter.equals(((AdviceTypeTest.FOO) + (AdviceTypeTest.BAR)))))) || (!(AdviceTypeTest.ReferenceInlineAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (AdviceTypeTest.ReferenceInlineAdvice.exception ? boxedReturn != null : !(boxedReturn.equals(AdviceTypeTest.FOO))) {
                throw new AssertionError();
            }
            if ((!(argument.equals(AdviceTypeTest.FOO))) || (!(mutableArgument.equals(AdviceTypeTest.BAR)))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.FOO)))) || (!(boxed[1].equals(AdviceTypeTest.BAR)))) {
                throw new AssertionError();
            }
            if ((((!(field.equals(AdviceTypeTest.FOO))) || (!(mutated.equals(AdviceTypeTest.BAR)))) || (!(staticField.equals(AdviceTypeTest.FOO)))) || (!(mutatedStatic.equals(AdviceTypeTest.BAR)))) {
                throw new AssertionError();
            }
            if (!(local.equals(AdviceTypeTest.BAR))) {
                throw new AssertionError();
            }
            if (!(custom.equals(AdviceTypeTest.FOO))) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ReferenceInlineAdvice.exit)++;
            return AdviceTypeTest.FOO;
        }
    }

    @SuppressWarnings("unused")
    public static class ReferenceDelegationAdvice {
        public static int enter;

        public static int exit;

        public static boolean exception;

        private Object field = AdviceTypeTest.FOO;

        private static Object staticField = AdviceTypeTest.FOO;

        public Object foo(Object argument, Object ignored) {
            return argument;
        }

        public Object bar(Object argument, Object ignored) {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter(inline = false)
        public static Object enter(@Advice.Unused
        Object value, @Advice.StubValue
        Object stubValue, @Advice.Argument(0)
        Object argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        Object field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        Object staticField, @AdviceTypeTest.CustomAnnotation
        String custom) {
            if (value != null) {
                throw new AssertionError();
            }
            if (stubValue != null) {
                throw new AssertionError();
            }
            if (!(argument.equals(AdviceTypeTest.FOO))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.FOO)))) || (!(boxed[1].equals(AdviceTypeTest.FOO)))) {
                throw new AssertionError();
            }
            if ((!(field.equals(AdviceTypeTest.FOO))) || (!(staticField.equals(AdviceTypeTest.FOO)))) {
                throw new AssertionError();
            }
            if (!(custom.equals(AdviceTypeTest.FOO))) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ReferenceDelegationAdvice.enter)++;
            return (AdviceTypeTest.FOO) + (AdviceTypeTest.BAR);
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        public static Object exit(@Advice.Return
        Object result, @Advice.Enter
        Object enter, @Advice.Thrown
        Throwable throwable, @Advice.Return
        Object boxedReturn, @Advice.Argument(0)
        Object argument, @Advice.AllArguments
        Object[] boxed, @Advice.FieldValue(AdviceTypeTest.FIELD)
        Object field, @Advice.FieldValue(AdviceTypeTest.STATIC_FIELD)
        Object staticField, @AdviceTypeTest.CustomAnnotation
        String custom) {
            if (((AdviceTypeTest.ReferenceDelegationAdvice.exception ? result != null : !(result.equals(AdviceTypeTest.FOO))) || (!(enter.equals(((AdviceTypeTest.FOO) + (AdviceTypeTest.BAR)))))) || (!(AdviceTypeTest.ReferenceDelegationAdvice.exception ? throwable instanceof RuntimeException : throwable == null))) {
                throw new AssertionError();
            }
            if (AdviceTypeTest.ReferenceDelegationAdvice.exception ? boxedReturn != null : !(boxedReturn.equals(AdviceTypeTest.FOO))) {
                throw new AssertionError();
            }
            if (!(argument.equals(AdviceTypeTest.FOO))) {
                throw new AssertionError();
            }
            if ((((boxed.length) != 2) || (!(boxed[0].equals(AdviceTypeTest.FOO)))) || (!(boxed[1].equals(AdviceTypeTest.FOO)))) {
                throw new AssertionError();
            }
            if ((!(field.equals(AdviceTypeTest.FOO))) || (!(staticField.equals(AdviceTypeTest.FOO)))) {
                throw new AssertionError();
            }
            if (!(custom.equals(AdviceTypeTest.FOO))) {
                throw new AssertionError();
            }
            (AdviceTypeTest.ReferenceDelegationAdvice.exit)++;
            return AdviceTypeTest.FOO;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @SuppressWarnings("unused")
    private @interface CustomAnnotation {
        boolean booleanValue() default AdviceTypeTest.BOOLEAN;

        byte byteValue() default AdviceTypeTest.VALUE;

        short shortValue() default AdviceTypeTest.VALUE;

        char charValue() default AdviceTypeTest.VALUE;

        int intValue() default AdviceTypeTest.VALUE;

        long longValue() default AdviceTypeTest.VALUE;

        float floatValue() default AdviceTypeTest.VALUE;

        double doubleValue() default AdviceTypeTest.VALUE;

        String objectValue() default AdviceTypeTest.FOO;
    }

    private static class SerializationAssertion extends AsmVisitorWrapper.AbstractBase {
        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            return new AdviceTypeTest.SerializationAssertion.SerializationClassVisitor(classVisitor);
        }

        private static class SerializationClassVisitor extends ClassVisitor {
            public SerializationClassVisitor(ClassVisitor classVisitor) {
                super(ASM_API, classVisitor);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return new AdviceTypeTest.SerializationAssertion.SerializationMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
            }
        }

        private static class SerializationMethodVisitor extends MethodVisitor {
            public SerializationMethodVisitor(MethodVisitor methodVisitor) {
                super(ASM_API, methodVisitor);
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
                if (type.equals(Type.getInternalName(ObjectInputStream.class))) {
                    throw new AssertionError("Unexpected deserialization");
                }
                super.visitTypeInsn(opcode, type);
            }
        }
    }
}

