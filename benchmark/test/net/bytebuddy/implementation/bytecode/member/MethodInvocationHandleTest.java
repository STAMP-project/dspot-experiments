package net.bytebuddy.implementation.bytecode.member;


import net.bytebuddy.description.method.MethodDescription;
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

import static net.bytebuddy.implementation.bytecode.member.MethodInvocation.HandleType.EXACT;
import static net.bytebuddy.implementation.bytecode.member.MethodInvocation.HandleType.REGULAR;


public class MethodInvocationHandleTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Mock
    private TypeDescription.Generic returnType;

    @Mock
    private TypeDescription declaringType;

    @Mock
    private TypeDescription firstType;

    @Mock
    private TypeDescription secondType;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private MethodVisitor methodVisitor;

    @Test
    public void testExactHandleStatic() throws Exception {
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).onHandle(EXACT);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is((-1)));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeExact", MethodInvocationHandleTest.BAZ, false);
    }

    @Test
    public void testExactHandleConstructor() throws Exception {
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).onHandle(EXACT);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is((-1)));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeExact", MethodInvocationHandleTest.BAZ, false);
    }

    @Test
    public void testExactHandleNonStatic() throws Exception {
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).onHandle(EXACT);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is((-1)));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeExact", (("(" + (MethodInvocationHandleTest.BAR)) + (MethodInvocationHandleTest.BAZ.substring(1))), false);
    }

    @Test
    public void testMethodNames() throws Exception {
        MatcherAssert.assertThat(EXACT.getMethodName(), CoreMatchers.is("invokeExact"));
        MatcherAssert.assertThat(REGULAR.getMethodName(), CoreMatchers.is("invoke"));
    }
}

