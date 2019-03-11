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


public class ByteCodeAppenderSimpleTest {
    private static final int STACK_SIZE = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private StackManipulation first;

    @Mock
    private StackManipulation second;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testApplication() throws Exception {
        ByteCodeAppender byteCodeAppender = new ByteCodeAppender.Simple(first, second);
        ByteCodeAppender.Size size = byteCodeAppender.apply(methodVisitor, implementationContext, methodDescription);
        MatcherAssert.assertThat(size.getLocalVariableSize(), CoreMatchers.is(ByteCodeAppenderSimpleTest.STACK_SIZE));
        MatcherAssert.assertThat(size.getOperandStackSize(), CoreMatchers.is(0));
        Mockito.verify(first).apply(methodVisitor, implementationContext);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).apply(methodVisitor, implementationContext);
        Mockito.verifyNoMoreInteractions(second);
    }
}

