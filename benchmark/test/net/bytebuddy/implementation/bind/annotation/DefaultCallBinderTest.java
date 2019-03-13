package net.bytebuddy.implementation.bind.annotation;


import java.io.Serializable;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.annotation.DefaultCall.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class DefaultCallBinderTest extends AbstractAnnotationBinderTest<DefaultCall> {
    private static final Class<?> NON_INTERFACE_TYPE = Object.class;

    private static final Class<?> INTERFACE_TYPE = Serializable.class;

    private static final Class<?> VOID_TYPE = void.class;

    @Mock
    private TypeDescription targetParameterType;

    @Mock
    private TypeDescription firstInterface;

    @Mock
    private TypeDescription secondInterface;

    @Mock
    private TypeDescription.Generic genericTargetParameterType;

    @Mock
    private TypeDescription.Generic firstGenericInterface;

    @Mock
    private TypeDescription.Generic secondGenericInterface;

    @Mock
    private MethodDescription.SignatureToken token;

    @Mock
    private Implementation.SpecialMethodInvocation specialMethodInvocation;

    public DefaultCallBinderTest() {
        super(DefaultCall.class);
    }

    @Test
    public void testImplicitLookupIsUnique() throws Exception {
        Mockito.when(targetParameterType.represents(ArgumentMatchers.any(Class.class))).thenReturn(true);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true, false);
        Mockito.doReturn(DefaultCallBinderTest.VOID_TYPE).when(annotation).targetType();
        Mockito.when(source.asSignatureToken()).thenReturn(token);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(implementationTarget).invokeDefault(token);
        Mockito.verifyNoMoreInteractions(implementationTarget);
    }

    @Test
    public void testImplicitLookupIsAmbiguousNullFallback() throws Exception {
        Mockito.when(targetParameterType.represents(ArgumentMatchers.any(Class.class))).thenReturn(true);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true, false);
        Mockito.doReturn(DefaultCallBinderTest.VOID_TYPE).when(annotation).targetType();
        Mockito.when(source.asSignatureToken()).thenReturn(token);
        Mockito.when(source.isSpecializableFor(firstInterface)).thenReturn(true);
        Mockito.when(source.isSpecializableFor(secondInterface)).thenReturn(true);
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(firstInterface, secondInterface));
        Mockito.when(annotation.nullIfImpossible()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(implementationTarget).invokeDefault(token);
        Mockito.verifyNoMoreInteractions(implementationTarget);
    }

    @Test
    public void testExplicitLookup() throws Exception {
        Mockito.when(targetParameterType.represents(ArgumentMatchers.any(Class.class))).thenReturn(true);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        Mockito.doReturn(DefaultCallBinderTest.INTERFACE_TYPE).when(annotation).targetType();
        Mockito.when(source.asSignatureToken()).thenReturn(token);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(implementationTarget).invokeDefault(token, of(DefaultCallBinderTest.INTERFACE_TYPE));
        Mockito.verifyNoMoreInteractions(implementationTarget);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonInterfaceTarget() throws Exception {
        Mockito.when(targetParameterType.represents(ArgumentMatchers.any(Class.class))).thenReturn(true);
        Mockito.doReturn(DefaultCallBinderTest.NON_INTERFACE_TYPE).when(annotation).targetType();
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalAnnotatedValue() throws Exception {
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

