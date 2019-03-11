package net.bytebuddy.asm;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class AdviceCustomAnnotationOnFieldTest {
    private static final String FOO = "foo";

    private final Class<?> target;

    private final Object expected;

    public AdviceCustomAnnotationOnFieldTest(Class<?> target, Object expected) {
        this.target = target;
        this.expected = expected;
    }

    @Test
    public void testPrimitiveField() throws Exception {
        Class<?> type = new ByteBuddy().redefine(target).visit(Advice.withCustomMapping().bind(AdviceCustomAnnotationOnFieldTest.FieldValue.class, new FieldDescription.ForLoadedField(target.getDeclaredField(AdviceCustomAnnotationOnFieldTest.FOO))).to(target).on(ElementMatchers.named(AdviceCustomAnnotationOnFieldTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceCustomAnnotationOnFieldTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(expected));
    }

    @Test
    public void testBoxedField() throws Exception {
        Class<?> type = new ByteBuddy().redefine(target).visit(Advice.withCustomMapping().bind(AdviceCustomAnnotationOnFieldTest.FieldValue.class, new FieldDescription.ForLoadedField(target.getDeclaredField(AdviceCustomAnnotationOnFieldTest.FOO))).to(AdviceCustomAnnotationOnFieldTest.BoxedFieldAdvice.class).on(ElementMatchers.named(AdviceCustomAnnotationOnFieldTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceCustomAnnotationOnFieldTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(expected));
    }

    public static class BoxedFieldAdvice {
        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        Object value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface FieldValue {}

    public static class BooleanValue {
        boolean foo;

        public Object foo() {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        boolean value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class ByteValue {
        byte foo;

        public Object foo() {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        byte value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class ShortValue {
        short foo;

        public Object foo() {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        short value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class CharacterValue {
        char foo;

        public Object foo() {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        char value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class IntegerValue {
        int foo;

        public Object foo() {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        int value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class LongValue {
        long foo;

        public Object foo() {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        long value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class FloatValue {
        float foo;

        public Object foo() {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        float value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class DoubleValue {
        double foo;

        public Object foo() {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        double value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }

    public static class ReferenceValue {
        String foo = AdviceCustomAnnotationOnFieldTest.FOO;

        public Object foo() {
            return null;
        }

        @Advice.OnMethodExit
        static void exit(@AdviceCustomAnnotationOnFieldTest.FieldValue
        String value, @Advice.Return(readOnly = false)
        Object returned) {
            returned = value;
        }
    }
}

