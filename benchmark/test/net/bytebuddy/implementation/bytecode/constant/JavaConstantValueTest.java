package net.bytebuddy.implementation.bytecode.constant;


import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaConstant;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;


public class JavaConstantValueTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private JavaConstant javaConstant;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testMethodHandle() throws Exception {
        Mockito.when(javaConstant.asConstantPoolValue()).thenReturn(JavaConstantValueTest.FOO);
        StackManipulation stackManipulation = new JavaConstantValue(javaConstant);
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(javaConstant).asConstantPoolValue();
        Mockito.verifyNoMoreInteractions(javaConstant);
        Mockito.verify(methodVisitor).visitLdcInsn(JavaConstantValueTest.FOO);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

