package net.bytebuddy.implementation.bytecode.member;


import java.util.Arrays;
import java.util.Collections;
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
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class MethodInvocationDynamicTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Mock
    private TypeDescription returnType;

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

    @Mock
    private Object argument;

    @Test
    public void testDynamicStaticBootstrap() throws Exception {
        Mockito.when(isInvokeBootstrap()).thenReturn(true);
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).dynamic(MethodInvocationDynamicTest.FOO, returnType, Arrays.asList(firstType, secondType), Collections.singletonList(argument));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitInvokeDynamicInsn(MethodInvocationDynamicTest.FOO, (((("(" + (MethodInvocationDynamicTest.FOO)) + (MethodInvocationDynamicTest.BAR)) + ")") + (MethodInvocationDynamicTest.QUX)), new Handle(Opcodes.H_INVOKESTATIC, MethodInvocationDynamicTest.BAR, MethodInvocationDynamicTest.QUX, MethodInvocationDynamicTest.BAZ, false), argument);
    }

    @Test
    public void testDynamicConstructorBootstrap() throws Exception {
        Mockito.when(isInvokeBootstrap()).thenReturn(true);
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).dynamic(MethodInvocationDynamicTest.FOO, returnType, Arrays.asList(firstType, secondType), Collections.singletonList(argument));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitInvokeDynamicInsn(MethodInvocationDynamicTest.FOO, (((("(" + (MethodInvocationDynamicTest.FOO)) + (MethodInvocationDynamicTest.BAR)) + ")") + (MethodInvocationDynamicTest.QUX)), new Handle(Opcodes.H_NEWINVOKESPECIAL, MethodInvocationDynamicTest.BAR, MethodInvocationDynamicTest.QUX, MethodInvocationDynamicTest.BAZ, false), argument);
    }

    @Test
    public void testIllegalBootstrap() throws Exception {
        StackManipulation stackManipulation = MethodInvocation.invoke(methodDescription).dynamic(MethodInvocationDynamicTest.FOO, returnType, Arrays.asList(firstType, secondType), Collections.singletonList(argument));
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(false));
    }
}

