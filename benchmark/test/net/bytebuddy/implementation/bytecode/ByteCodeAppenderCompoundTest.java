package net.bytebuddy.implementation.bytecode;


import net.bytebuddy.description.method.MethodDescription;
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


public class ByteCodeAppenderCompoundTest {
    private static final int MINIMUM = 3;

    private static final int MAXIMUM = 5;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ByteCodeAppender first;

    @Mock
    private ByteCodeAppender second;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    private ByteCodeAppender compound;

    @Test
    public void testApplication() throws Exception {
        Mockito.when(first.apply(methodVisitor, implementationContext, methodDescription)).thenReturn(new ByteCodeAppender.Size(ByteCodeAppenderCompoundTest.MINIMUM, ByteCodeAppenderCompoundTest.MAXIMUM));
        Mockito.when(second.apply(methodVisitor, implementationContext, methodDescription)).thenReturn(new ByteCodeAppender.Size(ByteCodeAppenderCompoundTest.MAXIMUM, ByteCodeAppenderCompoundTest.MINIMUM));
        ByteCodeAppender.Size size = compound.apply(methodVisitor, implementationContext, methodDescription);
        MatcherAssert.assertThat(size.getLocalVariableSize(), CoreMatchers.is(ByteCodeAppenderCompoundTest.MAXIMUM));
        MatcherAssert.assertThat(size.getOperandStackSize(), CoreMatchers.is(ByteCodeAppenderCompoundTest.MAXIMUM));
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

