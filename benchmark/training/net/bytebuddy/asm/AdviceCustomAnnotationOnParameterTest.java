package net.bytebuddy.asm;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class AdviceCustomAnnotationOnParameterTest {
    private static final String FOO = "foo";

    private final Class<?> target;

    private final Class<?> argumentType;

    private final Object expected;

    public AdviceCustomAnnotationOnParameterTest(Class<?> target, Class<?> argumentType, Object expected) {
        this.target = target;
        this.argumentType = argumentType;
        this.expected = expected;
    }

    @Test
    public void testPrimitiveField() throws Exception {
        Class<?> type = new ByteBuddy().redefine(target).visit(Advice.withCustomMapping().bind(AdviceCustomAnnotationOnParameterTest.ArgumentValue.class, new MethodDescription.ForLoadedMethod(target.getDeclaredMethod(AdviceCustomAnnotationOnParameterTest.FOO, argumentType)).getParameters().getOnly()).to(target).on(ElementMatchers.named(AdviceCustomAnnotationOnParameterTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceCustomAnnotationOnParameterTest.FOO, argumentType).invoke(type.getDeclaredConstructor().newInstance(), expected), CoreMatchers.is(expected));
    }

    @Test
    public void testBoxedField() throws Exception {
        Class<?> type = new ByteBuddy().redefine(target).visit(Advice.withCustomMapping().bind(AdviceCustomAnnotationOnParameterTest.ArgumentValue.class, new MethodDescription.ForLoadedMethod(target.getDeclaredMethod(AdviceCustomAnnotationOnParameterTest.FOO, argumentType)).getParameters().getOnly()).to(AdviceCustomAnnotationOnParameterTest.BoxedFieldAdvice.class).on(ElementMatchers.named(AdviceCustomAnnotationOnParameterTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceCustomAnnotationOnParameterTest.FOO, argumentType).invoke(type.getDeclaredConstructor().newInstance(), expected), CoreMatchers.is(expected));
    }

    public static class BoxedFieldAdvice {
        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        Object value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ArgumentValue {}

    public static class BooleanValue {
        public Object foo(boolean value) {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        boolean value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class ByteValue {
        public Object foo(byte value) {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        byte value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class ShortValue {
        public Object foo(short value) {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        short value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class CharacterValue {
        public Object foo(char value) {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        char value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class IntegerValue {
        public Object foo(int value) {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        int value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class LongValue {
        public Object foo(long value) {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        long value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class FloatValue {
        public Object foo(float value) {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        float value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class DoubleValue {
        public Object foo(double value) {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        double value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class ReferenceValue {
        public Object foo(String value) {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnParameterTest.ArgumentValue
        String value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }
}

