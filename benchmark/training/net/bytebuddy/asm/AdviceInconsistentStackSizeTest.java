package net.bytebuddy.asm;


import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER_PERSISTENT;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;


@RunWith(Parameterized.class)
public class AdviceInconsistentStackSizeTest {
    private static final String FOO = "foo";

    private final Class<?> type;

    private final Serializable original;

    private final Serializable replaced;

    private final int opcode;

    public AdviceInconsistentStackSizeTest(Class<?> type, Serializable original, Serializable replaced, int opcode) {
        this.type = type;
        this.original = original;
        this.replaced = replaced;
        this.opcode = opcode;
    }

    @Test
    public void testInconsistentStackSize() throws Exception {
        Class<?> atypical = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentStackSizeTest.FOO, type, Visibility.PUBLIC).intercept(new AdviceInconsistentStackSizeTest.InconsistentSizeAppender()).make().load(null, WRAPPER_PERSISTENT).getLoaded();
        Class<?> adviced = new ByteBuddy().redefine(atypical).visit(Advice.withCustomMapping().bind(AdviceInconsistentStackSizeTest.Value.class, replaced).to(AdviceInconsistentStackSizeTest.ExitAdvice.class).on(ElementMatchers.named(AdviceInconsistentStackSizeTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(adviced.getDeclaredMethod(AdviceInconsistentStackSizeTest.FOO).invoke(adviced.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (replaced))));
    }

    @Test
    public void testInconsistentStackSizeAdvice() throws Exception {
        Class<?> advice = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentStackSizeTest.FOO, type, Ownership.STATIC).intercept(new AdviceInconsistentStackSizeTest.InconsistentSizeAppender()).annotateMethod(ofType(Advice.OnMethodEnter.class).define("suppress", RuntimeException.class).build()).annotateMethod(ofType(Advice.OnMethodExit.class).define("suppress", RuntimeException.class).build()).make().load(getClass().getClassLoader(), WRAPPER_PERSISTENT).getLoaded();
        Class<?> foo = new ByteBuddy().subclass(Object.class).defineMethod("foo", String.class, Visibility.PUBLIC).intercept(FixedValue.value(AdviceInconsistentStackSizeTest.FOO)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        Class<?> redefined = new ByteBuddy().redefine(foo).visit(Advice.to(advice).on(ElementMatchers.named(AdviceInconsistentStackSizeTest.FOO))).make().load(null, CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(redefined, CoreMatchers.not(CoreMatchers.sameInstance(((Object) (foo)))));
        MatcherAssert.assertThat(redefined.getDeclaredMethod(AdviceInconsistentStackSizeTest.FOO).invoke(redefined.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentStackSizeTest.FOO))));
    }

    @SuppressWarnings("all")
    private static class ExitAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object returned, @AdviceInconsistentStackSizeTest.Value
        Object value) {
            returned = value;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface Value {}

    private class InconsistentSizeAppender implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            if ((original) != null) {
                methodVisitor.visitLdcInsn(original);
            }
            methodVisitor.visitInsn(opcode);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, new Object[0], 0, new Object[0]);
            if ((original) != null) {
                methodVisitor.visitLdcInsn(original);
                methodVisitor.visitLdcInsn(original);
            }
            methodVisitor.visitInsn(opcode);
            return new ByteCodeAppender.Size(((StackSize.of(type).getSize()) * 2), instrumentedMethod.getStackSize());
        }
    }
}

