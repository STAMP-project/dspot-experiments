package net.bytebuddy.implementation.bytecode.constant;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
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
public class DefaultValueTest {
    private final Class<?> type;

    private final int opcode;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private Implementation.Context implementationContext;

    public DefaultValueTest(Class<?> type, int opcode) {
        this.type = type;
        this.opcode = opcode;
    }

    @Test
    public void testDefaultValue() throws Exception {
        StackManipulation stackManipulation = DefaultValue.of(typeDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(StackSize.of(type).getSize()));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(StackSize.of(type).getSize()));
        if ((opcode) == (-1)) {
            Mockito.verifyZeroInteractions(methodVisitor);
        } else {
            Mockito.verify(methodVisitor).visitInsn(opcode);
            Mockito.verifyNoMoreInteractions(methodVisitor);
        }
    }
}

