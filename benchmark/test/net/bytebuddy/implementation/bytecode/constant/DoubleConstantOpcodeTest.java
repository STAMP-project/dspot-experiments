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


@RunWith(Parameterized.class)
public class DoubleConstantOpcodeTest {
    private final double value;

    private final int opcode;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public DoubleConstantOpcodeTest(double value, int opcode) {
        this.value = value;
        this.opcode = opcode;
    }

    @Test
    public void testConstant() throws Exception {
        StackManipulation.Size size = DoubleConstant.forValue(value).apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(2));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(2));
        Mockito.verify(methodVisitor).visitInsn(opcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

