package net.bytebuddy.implementation.bytecode.assign;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class InstanceCheckTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private TypeDescription typeDescription;

    @Test
    public void testInstanceCheck() {
        Mockito.when(typeDescription.getInternalName()).thenReturn(InstanceCheckTest.FOO);
        StackManipulation stackManipulation = InstanceCheck.of(typeDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), Is.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), Is.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), Is.is(0));
        Mockito.verify(typeDescription).isPrimitive();
        Mockito.verify(typeDescription).getInternalName();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(methodVisitor).visitTypeInsn(Opcodes.INSTANCEOF, InstanceCheckTest.FOO);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstanceCheckPrimitiveIllegal() {
        Mockito.when(typeDescription.isPrimitive()).thenReturn(true);
        InstanceCheck.of(typeDescription);
    }
}

