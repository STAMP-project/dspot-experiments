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
public class IntegerConstantOpcodeTest {
    private final int value;

    private final int opcode;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public IntegerConstantOpcodeTest(int value, int opcode) {
        this.value = value;
        this.opcode = opcode;
    }

    @Test
    public void testConstant() throws Exception {
        StackManipulation loading = IntegerConstant.forValue(value);
        if (((value) == 0) || ((value) == 1)) {
            MatcherAssert.assertThat(loading, CoreMatchers.is(IntegerConstant.forValue(((value) == 1))));
        }
        MatcherAssert.assertThat(loading.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = loading.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitInsn(opcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

