package net.bytebuddy.implementation.bytecode;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
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


public class TypeCreationTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testTypeCreation() throws Exception {
        StackManipulation stackManipulation = TypeCreation.of(typeDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitTypeInsn(Opcodes.NEW, TypeCreationTest.FOO);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypeCreationArray() throws Exception {
        Mockito.when(typeDescription.isArray()).thenReturn(true);
        TypeCreation.of(typeDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypeCreationPrimitive() throws Exception {
        Mockito.when(typeDescription.isPrimitive()).thenReturn(true);
        TypeCreation.of(typeDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypeCreationAbstract() throws Exception {
        Mockito.when(typeDescription.isAbstract()).thenReturn(true);
        TypeCreation.of(typeDescription);
    }
}

