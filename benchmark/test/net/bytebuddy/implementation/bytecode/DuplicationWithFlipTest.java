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
public class DuplicationWithFlipTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDefinition top;

    @Mock
    private TypeDefinition second;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    private final StackSize topSize;

    private final StackSize secondSize;

    private final int opcode;

    public DuplicationWithFlipTest(StackSize topSize, StackSize secondSize, int opcode) {
        this.topSize = topSize;
        this.secondSize = secondSize;
        this.opcode = opcode;
    }

    @Test
    public void testFlip() throws Exception {
        StackManipulation stackManipulation = Duplication.of(top).flipOver(second);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(topSize.getSize()));
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(topSize.getSize()));
        Mockito.verify(methodVisitor).visitInsn(opcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

