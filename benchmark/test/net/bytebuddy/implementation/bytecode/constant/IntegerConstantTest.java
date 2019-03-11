package net.bytebuddy.implementation.bytecode.constant;


import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


@RunWith(Parameterized.class)
public class IntegerConstantTest {
    private final int value;

    private final IntegerConstantTest.PushType pushType;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public IntegerConstantTest(int value, IntegerConstantTest.PushType pushType) {
        this.value = value;
        this.pushType = pushType;
    }

    @Test
    public void testBiPush() throws Exception {
        StackManipulation integerConstant = IntegerConstant.forValue(value);
        MatcherAssert.assertThat(integerConstant.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = integerConstant.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        pushType.verifyInstruction(methodVisitor, value);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    private enum PushType {

        BIPUSH,
        SIPUSH,
        LDC;
        private void verifyInstruction(MethodVisitor methodVisitor, int value) {
            switch (this) {
                case BIPUSH :
                    Mockito.verify(methodVisitor).visitIntInsn(Opcodes.BIPUSH, value);
                    break;
                case SIPUSH :
                    Mockito.verify(methodVisitor).visitIntInsn(Opcodes.SIPUSH, value);
                    break;
                case LDC :
                    Mockito.verify(methodVisitor).visitLdcInsn(value);
                    break;
                default :
                    throw new AssertionError();
            }
        }
    }
}

