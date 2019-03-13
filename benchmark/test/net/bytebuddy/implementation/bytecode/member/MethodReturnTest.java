package net.bytebuddy.implementation.bytecode.member;


import net.bytebuddy.description.type.TypeDefinition;
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
public class MethodReturnTest {
    private final Class<?> type;

    private final int opcode;

    private final int sizeChange;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private TypeDefinition typeDefinition;

    @Mock
    private Implementation.Context implementationContext;

    public MethodReturnTest(Class<?> type, int opcode, int sizeChange) {
        this.type = type;
        this.opcode = opcode;
        this.sizeChange = sizeChange;
    }

    @Test
    public void testVoidReturn() throws Exception {
        StackManipulation stackManipulation = MethodReturn.of(typeDefinition);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(((-1) * (sizeChange))));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitInsn(opcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

