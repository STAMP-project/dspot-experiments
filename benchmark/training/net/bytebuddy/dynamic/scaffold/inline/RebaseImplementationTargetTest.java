package net.bytebuddy.dynamic.scaffold.inline;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.AbstractImplementationTargetTest;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.implementation.bytecode.StackManipulation.Trivial.INSTANCE;


public class RebaseImplementationTargetTest extends AbstractImplementationTargetTest {
    private static final String BAR = "bar";

    @Mock
    private MethodDescription.InDefinedShape rebasedMethod;

    @Mock
    private MethodDescription.Token rebasedToken;

    @Mock
    private MethodDescription.SignatureToken rebasedSignatureToken;

    @Mock
    private MethodRebaseResolver.Resolution resolution;

    @Mock
    private TypeDescription rawSuperClass;

    @Mock
    private TypeDescription.Generic superClass;

    @Test
    public void testNonRebasedMethodIsInvokable() throws Exception {
        Mockito.when(invokableMethod.getDeclaringType()).thenReturn(instrumentedType);
        Mockito.when(invokableMethod.isSpecializableFor(instrumentedType)).thenReturn(true);
        Mockito.when(resolution.isRebased()).thenReturn(false);
        Mockito.when(resolution.getResolvedMethod()).thenReturn(invokableMethod);
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeSuper(rebasedSignatureToken);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(specialMethodInvocation.getMethodDescription(), CoreMatchers.is(((MethodDescription) (invokableMethod))));
        MatcherAssert.assertThat(specialMethodInvocation.getTypeDescription(), CoreMatchers.is(instrumentedType));
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        StackManipulation.Size size = specialMethodInvocation.apply(methodVisitor, implementationContext);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, AbstractImplementationTargetTest.BAZ, AbstractImplementationTargetTest.FOO, AbstractImplementationTargetTest.QUX, false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
    }

    @Test
    public void testRebasedMethodIsInvokable() throws Exception {
        Mockito.when(invokableMethod.getDeclaringType()).thenReturn(instrumentedType);
        Mockito.when(resolution.isRebased()).thenReturn(true);
        Mockito.when(resolution.getResolvedMethod()).thenReturn(rebasedMethod);
        Mockito.when(resolution.getAdditionalArguments()).thenReturn(INSTANCE);
        Mockito.when(rebasedMethod.isSpecializableFor(instrumentedType)).thenReturn(true);
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeSuper(rebasedSignatureToken);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(specialMethodInvocation.getMethodDescription(), CoreMatchers.is(((MethodDescription) (rebasedMethod))));
        MatcherAssert.assertThat(specialMethodInvocation.getTypeDescription(), CoreMatchers.is(instrumentedType));
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        StackManipulation.Size size = specialMethodInvocation.apply(methodVisitor, implementationContext);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, AbstractImplementationTargetTest.BAZ, AbstractImplementationTargetTest.QUX, AbstractImplementationTargetTest.FOO, false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
    }

    @Test
    public void testRebasedConstructorIsInvokable() throws Exception {
        Mockito.when(rebasedMethod.isConstructor()).thenReturn(true);
        Mockito.when(invokableMethod.getDeclaringType()).thenReturn(instrumentedType);
        Mockito.when(resolution.isRebased()).thenReturn(true);
        Mockito.when(resolution.getResolvedMethod()).thenReturn(rebasedMethod);
        Mockito.when(resolution.getAdditionalArguments()).thenReturn(NullConstant.INSTANCE);
        Mockito.when(rebasedMethod.isSpecializableFor(instrumentedType)).thenReturn(true);
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeSuper(rebasedSignatureToken);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(specialMethodInvocation.getMethodDescription(), CoreMatchers.is(((MethodDescription) (rebasedMethod))));
        MatcherAssert.assertThat(specialMethodInvocation.getTypeDescription(), CoreMatchers.is(instrumentedType));
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        StackManipulation.Size size = specialMethodInvocation.apply(methodVisitor, implementationContext);
        Mockito.verify(methodVisitor).visitInsn(Opcodes.ACONST_NULL);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, AbstractImplementationTargetTest.BAZ, AbstractImplementationTargetTest.QUX, AbstractImplementationTargetTest.FOO, false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(1));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(1));
    }

    @Test
    public void testNonSpecializableRebaseMethodIsNotInvokable() throws Exception {
        Mockito.when(invokableMethod.getDeclaringType()).thenReturn(instrumentedType);
        Mockito.when(resolution.isRebased()).thenReturn(true);
        Mockito.when(resolution.getResolvedMethod()).thenReturn(rebasedMethod);
        Mockito.when(resolution.getAdditionalArguments()).thenReturn(INSTANCE);
        Mockito.when(rebasedMethod.isSpecializableFor(instrumentedType)).thenReturn(false);
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeSuper(rebasedSignatureToken);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testSuperTypeMethodIsInvokable() throws Exception {
        Mockito.when(invokableMethod.isSpecializableFor(rawSuperClass)).thenReturn(true);
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeSuper(invokableToken);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(true));
        MatcherAssert.assertThat(specialMethodInvocation.getMethodDescription(), CoreMatchers.is(((MethodDescription) (invokableMethod))));
        MatcherAssert.assertThat(specialMethodInvocation.getTypeDescription(), CoreMatchers.is(rawSuperClass));
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        Implementation.Context implementationContext = Mockito.mock(Implementation.Context.class);
        StackManipulation.Size size = specialMethodInvocation.apply(methodVisitor, implementationContext);
        Mockito.verify(methodVisitor).visitMethodInsn(Opcodes.INVOKESPECIAL, RebaseImplementationTargetTest.BAR, AbstractImplementationTargetTest.FOO, AbstractImplementationTargetTest.QUX, false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(0));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
    }

    @Test
    public void testNonSpecializableSuperClassMethodIsNotInvokable() throws Exception {
        Mockito.when(invokableMethod.isSpecializableFor(rawSuperClass)).thenReturn(false);
        Mockito.when(resolution.isRebased()).thenReturn(false);
        Mockito.when(resolution.getResolvedMethod()).thenReturn(invokableMethod);
        Implementation.SpecialMethodInvocation specialMethodInvocation = makeImplementationTarget().invokeSuper(invokableToken);
        MatcherAssert.assertThat(specialMethodInvocation.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testOriginType() throws Exception {
        MatcherAssert.assertThat(makeImplementationTarget().getOriginType(), CoreMatchers.is(((TypeDefinition) (instrumentedType))));
    }
}

