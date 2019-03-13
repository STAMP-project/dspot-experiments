package net.bytebuddy.implementation.bytecode;


import net.bytebuddy.description.type.TypeDefinition;
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
public class DuplicationTest {
    private final StackSize stackSize;

    private final int opcode;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDefinition typeDefinition;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public DuplicationTest(StackSize stackSize, int opcode) {
        this.stackSize = stackSize;
        this.opcode = opcode;
    }

    @Test
    public void testDuplication() throws Exception {
        StackManipulation stackManipulation = Duplication.of(typeDefinition);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(stackSize.getSize()));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(stackSize.getSize()));
        if ((stackSize) != (StackSize.ZERO)) {
            Mockito.verify(methodVisitor).visitInsn(opcode);
        }
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

