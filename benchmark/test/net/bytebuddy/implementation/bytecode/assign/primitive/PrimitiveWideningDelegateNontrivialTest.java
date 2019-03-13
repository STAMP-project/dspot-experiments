package net.bytebuddy.implementation.bytecode.assign.primitive;


import net.bytebuddy.description.type.TypeDescription;
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
public class PrimitiveWideningDelegateNontrivialTest {
    private final Class<?> sourceType;

    private final Class<?> targetType;

    private final int sizeChange;

    private final int opcode;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription sourceTypeDescription;

    @Mock
    private TypeDescription targetTypeDescription;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public PrimitiveWideningDelegateNontrivialTest(Class<?> sourceType, Class<?> targetType, int sizeChange, int opcode) {
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.sizeChange = sizeChange;
        this.opcode = opcode;
    }

    @Test
    public void testWideningConversion() throws Exception {
        StackManipulation stackManipulation = PrimitiveWideningDelegate.forPrimitive(sourceTypeDescription).widenTo(targetTypeDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(sizeChange));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(Math.max(0, sizeChange)));
        Mockito.verify(methodVisitor).visitInsn(opcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

