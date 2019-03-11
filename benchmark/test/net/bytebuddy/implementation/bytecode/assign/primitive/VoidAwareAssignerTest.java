package net.bytebuddy.implementation.bytecode.assign.primitive;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class VoidAwareAssignerTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic sourceTypeDescription;

    @Mock
    private TypeDescription.Generic targetTypeDescription;

    @Mock
    private Assigner chainedAssigner;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testAssignVoidToVoid() throws Exception {
        Mockito.when(sourceTypeDescription.represents(void.class)).thenReturn(true);
        Mockito.when(targetTypeDescription.represents(void.class)).thenReturn(true);
        Assigner voidAwareAssigner = new VoidAwareAssigner(chainedAssigner);
        StackManipulation stackManipulation = voidAwareAssigner.assign(sourceTypeDescription, targetTypeDescription, STATIC);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verifyZeroInteractions(chainedAssigner);
    }

    @Test
    public void testAssignNonVoidToNonVoid() throws Exception {
        Assigner voidAwareAssigner = new VoidAwareAssigner(chainedAssigner);
        StackManipulation chainedStackManipulation = Mockito.mock(StackManipulation.class);
        Mockito.when(chainedAssigner.assign(sourceTypeDescription, targetTypeDescription, STATIC)).thenReturn(chainedStackManipulation);
        StackManipulation stackManipulation = voidAwareAssigner.assign(sourceTypeDescription, targetTypeDescription, STATIC);
        MatcherAssert.assertThat(stackManipulation, CoreMatchers.is(chainedStackManipulation));
        Mockito.verify(chainedAssigner).assign(sourceTypeDescription, targetTypeDescription, STATIC);
        Mockito.verifyNoMoreInteractions(chainedAssigner);
    }
}

