package net.bytebuddy.implementation.bytecode.member;


import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaConstant;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.utility.JavaConstant.MethodType.of;


public class HandleInvocationTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testInvocationDecreasingStack() throws Exception {
        JavaConstant.MethodType methodType = of(void.class, Object.class);
        StackManipulation stackManipulation = new HandleInvocation(methodType);
        Assert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        Assert.assertThat(size.getSizeImpact(), CoreMatchers.is((-1)));
        Assert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeExact", "(Ljava/lang/Object;)V", false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test
    public void testInvocationIncreasingStack() throws Exception {
        JavaConstant.MethodType methodType = of(Object.class);
        StackManipulation stackManipulation = new HandleInvocation(methodType);
        Assert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        Assert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        Assert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeExact", "()Ljava/lang/Object;", false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

