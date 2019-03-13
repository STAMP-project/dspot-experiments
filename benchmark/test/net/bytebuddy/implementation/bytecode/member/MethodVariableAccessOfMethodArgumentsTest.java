package net.bytebuddy.implementation.bytecode.member;


import java.util.Arrays;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterList;
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


public class MethodVariableAccessOfMethodArgumentsTest {
    private static final String FOO = "foo";

    private static final int PARAMETER_STACK_SIZE = 2;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Mock
    private MethodDescription.InDefinedShape bridgeMethod;

    @Mock
    private TypeDescription declaringType;

    @Mock
    private TypeDescription firstRawParameterType;

    @Mock
    private TypeDescription secondRawParameterType;

    @Mock
    private TypeDescription.Generic firstParameterType;

    @Mock
    private TypeDescription.Generic secondParameterType;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Test
    public void testStaticMethod() throws Exception {
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        StackManipulation stackManipulation = MethodVariableAccess.allArgumentsOf(methodDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 1);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testNonStaticMethod() throws Exception {
        StackManipulation stackManipulation = MethodVariableAccess.allArgumentsOf(methodDescription);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 1);
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 2);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testStaticMethodWithPrepending() throws Exception {
        Mockito.when(methodDescription.isStatic()).thenReturn(true);
        StackManipulation stackManipulation = MethodVariableAccess.allArgumentsOf(methodDescription).prependThisReference();
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 1);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testNonStaticMethodWithPrepending() throws Exception {
        StackManipulation stackManipulation = MethodVariableAccess.allArgumentsOf(methodDescription).prependThisReference();
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(((MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE) + 1)));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(((MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE) + 1)));
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 0);
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 1);
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 2);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testBridgeMethodWithoutCasting() throws Exception {
        Mockito.when(bridgeMethod.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(bridgeMethod, Arrays.asList(firstParameterType, secondParameterType)));
        StackManipulation stackManipulation = MethodVariableAccess.allArgumentsOf(methodDescription).asBridgeOf(bridgeMethod);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 1);
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 2);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testBridgeMethodWithCasting() throws Exception {
        Mockito.when(secondRawParameterType.asErasure()).thenReturn(secondRawParameterType);
        Mockito.when(bridgeMethod.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(bridgeMethod, secondParameterType, secondParameterType));
        StackManipulation stackManipulation = MethodVariableAccess.allArgumentsOf(methodDescription).asBridgeOf(bridgeMethod);
        MatcherAssert.assertThat(stackManipulation.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = stackManipulation.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(MethodVariableAccessOfMethodArgumentsTest.PARAMETER_STACK_SIZE));
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 1);
        Mockito.verify(methodVisitor).visitTypeInsn(Opcodes.CHECKCAST, MethodVariableAccessOfMethodArgumentsTest.FOO);
        Mockito.verify(methodVisitor).visitVarInsn(Opcodes.ALOAD, 2);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

