package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.annotation.SuperCall.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class SuperCallBinderTest extends AbstractAnnotationBinderTest<SuperCall> {
    @Mock
    private TypeDescription targetParameterType;

    @Mock
    private TypeDescription.Generic genericTargetParameterType;

    @Mock
    private Implementation.SpecialMethodInvocation specialMethodInvocation;

    @Mock
    private MethodDescription.SignatureToken sourceToken;

    public SuperCallBinderTest() {
        super(SuperCall.class);
    }

    @Test
    public void testValidSuperMethodCall() throws Exception {
        Mockito.when(targetParameterType.represents(ArgumentMatchers.any(Class.class))).thenReturn(true);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        Mockito.verify(implementationTarget).invokeSuper(sourceToken);
        Mockito.verifyNoMoreInteractions(implementationTarget);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testInvalidSuperMethodCall() throws Exception {
        Mockito.when(targetParameterType.represents(ArgumentMatchers.any(Class.class))).thenReturn(true);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(false);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        Mockito.verify(implementationTarget).invokeSuper(sourceToken);
        Mockito.verifyNoMoreInteractions(implementationTarget);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testInvalidSuperMethodCallNullFallback() throws Exception {
        Mockito.when(targetParameterType.represents(ArgumentMatchers.any(Class.class))).thenReturn(true);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(false);
        Mockito.when(annotation.nullIfImpossible()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        Mockito.verify(implementationTarget).invokeSuper(sourceToken);
        Mockito.verifyNoMoreInteractions(implementationTarget);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testWrongTypeThrowsException() throws Exception {
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test
    public void testConstructorIsNotInvokeable() throws Exception {
        Mockito.when(targetParameterType.represents(ArgumentMatchers.any(Class.class))).thenReturn(true);
        Mockito.when(source.isConstructor()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        Mockito.verifyZeroInteractions(implementationTarget);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testConstructorNullFallback() throws Exception {
        Mockito.when(targetParameterType.represents(ArgumentMatchers.any(Class.class))).thenReturn(true);
        Mockito.when(source.isConstructor()).thenReturn(true);
        Mockito.when(annotation.nullIfImpossible()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        Mockito.verifyZeroInteractions(implementationTarget);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }
}

