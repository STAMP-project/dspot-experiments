package net.bytebuddy.implementation.bind.annotation;


import java.lang.reflect.Method;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.annotation.SuperMethod.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class SuperMethodBinderTest extends AbstractAnnotationBinderTest<SuperMethod> {
    public SuperMethodBinderTest() {
        super(SuperMethod.class);
    }

    @Mock
    private TypeDescription targetType;

    @Mock
    private TypeDescription.Generic genericTargetType;

    @Mock
    private MethodDescription.SignatureToken token;

    @Mock
    private Implementation.SpecialMethodInvocation specialMethodInvocation;

    @Test(expected = IllegalStateException.class)
    public void testBindNoMethodParameter() throws Exception {
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test
    public void testBind() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        Mockito.when(implementationTarget.invokeSuper(token)).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testBindDefaultFallback() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        Mockito.when(annotation.fallbackToDefault()).thenReturn(true);
        Mockito.when(implementationTarget.invokeDominant(token)).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testBindIllegal() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        Mockito.when(implementationTarget.invokeSuper(token)).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(false);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testBindIllegalFallback() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        Mockito.when(annotation.nullIfImpossible()).thenReturn(true);
        Mockito.when(implementationTarget.invokeSuper(token)).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(false);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testNoMethod() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(false);
        Mockito.when(annotation.nullIfImpossible()).thenReturn(false);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testNoMethodFallback() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(false);
        Mockito.when(annotation.nullIfImpossible()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }
}

