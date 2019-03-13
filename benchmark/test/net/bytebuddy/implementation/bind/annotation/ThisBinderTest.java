package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.annotation.This.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class ThisBinderTest extends AbstractAnnotationBinderTest<This> {
    @Mock
    private TypeDescription.Generic parameterType;

    @Mock
    private TypeDescription.Generic genericInstrumentedType;

    public ThisBinderTest() {
        super(This.class);
    }

    @Test
    public void testLegalBinding() throws Exception {
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(target.getType()).thenReturn(parameterType);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(assigner).assign(genericInstrumentedType, parameterType, STATIC);
        Mockito.verifyNoMoreInteractions(assigner);
        Mockito.verify(target, Mockito.atLeast(1)).getType();
        Mockito.verify(target, Mockito.never()).getDeclaredAnnotations();
    }

    @Test
    public void testLegalBindingRuntimeType() throws Exception {
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(target.getType()).thenReturn(parameterType);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, DYNAMIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(assigner).assign(genericInstrumentedType, parameterType, DYNAMIC);
        Mockito.verifyNoMoreInteractions(assigner);
        Mockito.verify(target, Mockito.atLeast(1)).getType();
        Mockito.verify(target, Mockito.never()).getDeclaredAnnotations();
    }

    @Test
    public void testIllegalBinding() throws Exception {
        Mockito.when(stackManipulation.isValid()).thenReturn(false);
        Mockito.when(target.getType()).thenReturn(parameterType);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(assigner.assign(ArgumentMatchers.any(TypeDescription.Generic.class), ArgumentMatchers.any(TypeDescription.Generic.class), ArgumentMatchers.any(Assigner.Typing.class))).thenReturn(StackManipulation.Illegal.INSTANCE);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
        Mockito.verify(assigner).assign(genericInstrumentedType, parameterType, STATIC);
        Mockito.verifyNoMoreInteractions(assigner);
        Mockito.verify(target, Mockito.atLeast(1)).getType();
        Mockito.verify(target, Mockito.never()).getDeclaredAnnotations();
    }

    @Test
    public void testOptionalBinding() throws Exception {
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(annotation.optional()).thenReturn(true);
        Mockito.when(source.isStatic()).thenReturn(true);
        Mockito.when(target.getType()).thenReturn(parameterType);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(annotation).optional();
        Mockito.verify(source, Mockito.atLeast(1)).isStatic();
        Mockito.verifyZeroInteractions(assigner);
    }

    @Test
    public void testStaticMethodIllegal() throws Exception {
        Mockito.when(target.getType()).thenReturn(parameterType);
        Mockito.when(source.isStatic()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testPrimitiveType() throws Exception {
        Mockito.when(parameterType.isPrimitive()).thenReturn(true);
        Mockito.when(target.getType()).thenReturn(parameterType);
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testArrayType() throws Exception {
        Mockito.when(parameterType.isArray()).thenReturn(true);
        Mockito.when(target.getType()).thenReturn(parameterType);
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }
}

