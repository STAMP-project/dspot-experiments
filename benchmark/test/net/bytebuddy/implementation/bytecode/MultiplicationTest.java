package net.bytebuddy.implementation.bytecode;


import net.bytebuddy.implementation.Implementation;
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
public class MultiplicationTest {
    private final StackManipulation stackManipulation;

    private final StackSize stackSize;

    private final int opcodes;

    public MultiplicationTest(StackManipulation stackManipulation, StackSize stackSize, int opcodes) {
        this.stackManipulation = stackManipulation;
        this.stackSize = stackSize;
        this.opcodes = opcodes;
    }

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testMultiplication() throws Exception {
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is((-(stackSize.getSize()))));
        Mockito.verify(methodVisitor).visitInsn(opcodes);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

