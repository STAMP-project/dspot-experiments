package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.annotation.Super.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class SuperBinderTest extends AbstractAnnotationBinderTest<Super> {
    @Mock
    private TypeDescription targetType;

    @Mock
    private TypeDescription.Generic genericTargetType;

    @Mock
    private Super.Instantiation instantiation;

    public SuperBinderTest() {
        super(Super.class);
    }

    @Test
    public void testAssignableBinding() throws Exception {
        Mockito.doReturn(void.class).when(annotation).proxyType();
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(instrumentedType.isAssignableTo(targetType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(instantiation).proxyFor(targetType, implementationTarget, annotationDescription);
    }

    @Test
    public void testIllegalBindingForNonAssignableType() throws Exception {
        Mockito.doReturn(void.class).when(annotation).proxyType();
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalBindingStaticMethod() throws Exception {
        Mockito.doReturn(void.class).when(annotation).proxyType();
        Mockito.when(source.isStatic()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testPrimitiveParameterType() throws Exception {
        Mockito.when(genericTargetType.isPrimitive()).thenReturn(true);
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testArrayParameterType() throws Exception {
        Mockito.when(genericTargetType.isArray()).thenReturn(true);
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testPrimitiveProxyType() throws Exception {
        Mockito.doReturn(int.class).when(annotation).proxyType();
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testArrayProxyType() throws Exception {
        Mockito.doReturn(Object[].class).when(annotation).proxyType();
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonAssignableType() throws Exception {
        Mockito.doReturn(Void.class).when(annotation).proxyType();
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testFinalProxyType() throws Exception {
        Mockito.doReturn(void.class).when(annotation).proxyType();
        Mockito.when(targetType.isFinal()).thenReturn(true);
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }
}

