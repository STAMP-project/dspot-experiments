package net.bytebuddy.implementation.bytecode;


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


public class StackManipulationCompoundTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private StackManipulation first;

    @Mock
    private StackManipulation second;

    @Test
    public void testIsValid() throws Exception {
        Mockito.when(first.isValid()).thenReturn(true);
        Mockito.when(second.isValid()).thenReturn(true);
        MatcherAssert.assertThat(new StackManipulation.Compound(first, second).isValid(), CoreMatchers.is(true));
        Mockito.verify(first).isValid();
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).isValid();
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testIsInvalid() throws Exception {
        Mockito.when(first.isValid()).thenReturn(true);
        Mockito.when(second.isValid()).thenReturn(false);
        MatcherAssert.assertThat(new StackManipulation.Compound(first, second).isValid(), CoreMatchers.is(false));
        Mockito.verify(first).isValid();
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).isValid();
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testApplication() throws Exception {
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        Mockito.when(first.apply(methodVisitor, implementationContext)).thenReturn(new StackManipulation.Size(2, 3));
        Mockito.when(second.apply(methodVisitor, implementationContext)).thenReturn(new StackManipulation.Size(2, 3));
        StackManipulation.Size size = new StackManipulation.Compound(first, second).apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(4));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(5));
        Mockito.verify(first).apply(methodVisitor, implementationContext);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).apply(methodVisitor, implementationContext);
        Mockito.verifyNoMoreInteractions(second);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

