package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.annotation.Default.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class DefaultBinderTest extends AbstractAnnotationBinderTest<Default> {
    @Mock
    private TypeDescription targetType;

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private TypeList.Generic interfaces;

    @Mock
    private TypeList rawInterfaces;

    public DefaultBinderTest() {
        super(Default.class);
    }

    @Test
    public void testAssignableBinding() throws Exception {
        Mockito.doReturn(void.class).when(annotation).proxyType();
        Mockito.when(targetType.isInterface()).thenReturn(true);
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(rawInterfaces.contains(targetType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testIllegalBindingNonDeclaredInterface() throws Exception {
        Mockito.doReturn(void.class).when(annotation).proxyType();
        Mockito.when(targetType.isInterface()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalBindingStatic() throws Exception {
        Mockito.doReturn(void.class).when(annotation).proxyType();
        Mockito.when(targetType.isInterface()).thenReturn(true);
        Mockito.when(source.isStatic()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testNonInterfaceProxyType() throws Exception {
        Mockito.doReturn(void.class).when(annotation).proxyType();
        Mockito.when(targetType.isInterface()).thenReturn(false);
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonInterfaceExplicitType() throws Exception {
        Mockito.doReturn(Void.class).when(annotation).proxyType();
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }
}

