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
public class MethodVariableAccessTest {
    private final TypeDefinition typeDefinition;

    private final int readCode;

    private final int writeCode;

    private final int size;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public MethodVariableAccessTest(Class<?> type, int readCode, int writeCode, int size) {
        typeDefinition = Mockito.mock(TypeDefinition.class);
        Mockito.when(typeDefinition.isPrimitive()).thenReturn(type.isPrimitive());
        Mockito.when(typeDefinition.represents(type)).thenReturn(true);
        this.readCode = readCode;
        this.writeCode = writeCode;
        this.size = size;
    }

    @Test
    public void testLoading() throws Exception {
        StackManipulation stackManipulation = MethodVariableAccess.of(typeDefinition).loadFrom(4);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(this.size));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(this.size));
        Mockito.verify(methodVisitor).visitVarInsn(readCode, 4);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testStoring() throws Exception {
        StackManipulation stackManipulation = MethodVariableAccess.of(typeDefinition).storeAt(4);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is((-(this.size))));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitVarInsn(writeCode, 4);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

