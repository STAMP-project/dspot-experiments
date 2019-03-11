package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER_PERSISTENT;


public class AdviceJsrRetTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testJsrRetByteCodes() throws Exception {
        Class<?> type = new ByteBuddy(ClassFileVersion.JAVA_V4).subclass(Object.class).defineMethod(AdviceJsrRetTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceJsrRetTest.JsrRetMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceJsrRetTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceJsrRetTest.FOO))));
        Class<?> advised = new ByteBuddy().redefine(type).visit(Advice.to(AdviceJsrRetTest.JsrAdvice.class).on(ElementMatchers.named(AdviceJsrRetTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(advised.getDeclaredMethod(AdviceJsrRetTest.FOO).invoke(advised.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceJsrRetTest.BAR))));
    }

    @SuppressWarnings("all")
    private static class JsrAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false)
        String value) {
            value = AdviceJsrRetTest.BAR;
        }
    }

    private static class JsrRetMethod implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label target = new Label();
            methodVisitor.visitJumpInsn(Opcodes.JSR, target);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitLabel(target);
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 2);
            methodVisitor.visitLdcInsn(AdviceJsrRetTest.FOO);
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 1);
            methodVisitor.visitVarInsn(Opcodes.RET, 2);
            return new ByteCodeAppender.Size(1, 3);
        }
    }
}

