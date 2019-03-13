package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
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


@RunWith(Parameterized.class)
public class AdviceDeadCodeTest {
    private static final String FOO = "foo";

    private final ClassFileVersion classFileVersion;

    public AdviceDeadCodeTest(ClassFileVersion classFileVersion) {
        this.classFileVersion = classFileVersion;
    }

    @Test
    public void testAdviceProcessesDeadCode() throws Exception {
        Class<?> type = new ByteBuddy(classFileVersion).subclass(Object.class).defineMethod(AdviceDeadCodeTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceDeadCodeTest.DeadStringAppender()).make().load(null, WRAPPER_PERSISTENT).getLoaded();
        Class<?> redefined = new ByteBuddy().redefine(type).visit(Advice.to(AdviceDeadCodeTest.ExitAdvice.class).on(ElementMatchers.named(AdviceDeadCodeTest.FOO))).make().load(null, WRAPPER).getLoaded();
        MatcherAssert.assertThat(redefined.getDeclaredMethod(AdviceDeadCodeTest.FOO).invoke(redefined.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceDeadCodeTest.FOO))));
    }

    @Test
    public void testAdviceContainsDeadCode() throws Exception {
        Class<?> advice = new ByteBuddy(classFileVersion).subclass(Object.class).defineMethod(AdviceDeadCodeTest.FOO, void.class, Ownership.STATIC).intercept(new AdviceDeadCodeTest.DeadVoidAppender()).annotateMethod(ofType(Advice.OnMethodEnter.class).define("suppress", RuntimeException.class).build()).annotateMethod(ofType(Advice.OnMethodExit.class).define("suppress", RuntimeException.class).build()).make().load(getClass().getClassLoader(), WRAPPER_PERSISTENT).getLoaded();
        Class<?> foo = new ByteBuddy(classFileVersion).subclass(Object.class).defineMethod("foo", String.class, Visibility.PUBLIC).intercept(FixedValue.value(AdviceDeadCodeTest.FOO)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        Class<?> redefined = new ByteBuddy().redefine(foo).visit(Advice.to(advice).on(ElementMatchers.named(AdviceDeadCodeTest.FOO))).make().load(null, CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(redefined, CoreMatchers.not(CoreMatchers.sameInstance(((Object) (foo)))));
        MatcherAssert.assertThat(redefined.getDeclaredMethod(AdviceDeadCodeTest.FOO).invoke(redefined.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceDeadCodeTest.FOO))));
    }

    @Test
    public void testAdviceWithExchangeDuplicationDeadCode() throws Exception {
        Class<?> type = new ByteBuddy(classFileVersion).subclass(Object.class).defineMethod(AdviceDeadCodeTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceDeadCodeTest.DeadExchangeAppender()).make().load(null, WRAPPER_PERSISTENT).getLoaded();
        Class<?> redefined = new ByteBuddy().redefine(type).visit(Advice.to(AdviceDeadCodeTest.ExitAdvice.class).on(ElementMatchers.named(AdviceDeadCodeTest.FOO))).make().load(null, WRAPPER).getLoaded();
        MatcherAssert.assertThat(redefined.getDeclaredMethod(AdviceDeadCodeTest.FOO).invoke(redefined.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceDeadCodeTest.FOO))));
    }

    @SuppressWarnings("all")
    private static class ExitAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false)
        String value) {
            value = AdviceDeadCodeTest.FOO;
        }
    }

    private static class DeadStringAppender implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitInsn(Opcodes.POP);// dead code

            methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            return new ByteCodeAppender.Size(1, instrumentedMethod.getStackSize());
        }
    }

    private static class DeadVoidAppender implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitInsn(Opcodes.POP);// dead code

            methodVisitor.visitInsn(Opcodes.RETURN);
            return new ByteCodeAppender.Size(1, instrumentedMethod.getStackSize());
        }
    }

    private static class DeadExchangeAppender implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitInsn(Opcodes.DUP_X1);// dead code

            methodVisitor.visitInsn(Opcodes.ARETURN);
            return new ByteCodeAppender.Size(1, instrumentedMethod.getStackSize());
        }
    }
}

