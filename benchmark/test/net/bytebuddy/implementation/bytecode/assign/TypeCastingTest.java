package net.bytebuddy.implementation.bytecode.assign;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class TypeCastingTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoTest = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testCasting() throws Exception {
        Mockito.when(typeDescription.getInternalName()).thenReturn(TypeCastingTest.FOO);
        StackManipulation.Size size = new TypeCasting(typeDescription).apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitTypeInsn(Opcodes.CHECKCAST, TypeCastingTest.FOO);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrimitiveCastingThrowsException() throws Exception {
        Mockito.when(typeDescription.isPrimitive()).thenReturn(true);
        TypeCasting.to(typeDescription);
    }
}

