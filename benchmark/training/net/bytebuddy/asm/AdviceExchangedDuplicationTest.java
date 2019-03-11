package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER_PERSISTENT;


@RunWith(Parameterized.class)
public class AdviceExchangedDuplicationTest {
    private static final String FOO = "foo";

    private static final int NUMERIC_VALUE = 42;

    private final int duplication;

    private final Class<?> valueType;

    private final Class<?> ignoredValueType;

    private final Object value;

    private final Object ignoredValue;

    public AdviceExchangedDuplicationTest(int duplication, Class<?> valueType, Class<?> ignoredValueType, Object value, Object ignoredValue) {
        this.duplication = duplication;
        this.valueType = valueType;
        this.ignoredValueType = ignoredValueType;
        this.value = value;
        this.ignoredValue = ignoredValue;
    }

    @Test
    public void testAdvice() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceExchangedDuplicationTest.FOO, valueType, Visibility.PUBLIC).intercept(new AdviceExchangedDuplicationTest.DuplicationImplementation()).make().load(getClass().getClassLoader(), WRAPPER_PERSISTENT).getLoaded();
        Class<?> redefined = new ByteBuddy().redefine(type).visit(Advice.to(AdviceExchangedDuplicationTest.class).on(ElementMatchers.named(AdviceExchangedDuplicationTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(redefined.getDeclaredMethod(AdviceExchangedDuplicationTest.FOO).invoke(redefined.getDeclaredConstructor().newInstance()), CoreMatchers.is(value));
    }

    private class DuplicationImplementation implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitLdcInsn(ignoredValue);
            methodVisitor.visitLdcInsn(value);
            methodVisitor.visitInsn(duplication);
            methodVisitor.visitInsn(((Type.getType(valueType).getSize()) == 2 ? Opcodes.POP2 : Opcodes.POP));
            methodVisitor.visitInsn(((Type.getType(ignoredValueType).getSize()) == 2 ? Opcodes.POP2 : Opcodes.POP));
            methodVisitor.visitInsn(Type.getType(valueType).getOpcode(Opcodes.IRETURN));
            return new ByteCodeAppender.Size((((Type.getType(valueType).getSize()) * 2) + (Type.getType(ignoredValueType).getSize())), instrumentedMethod.getStackSize());
        }
    }
}

