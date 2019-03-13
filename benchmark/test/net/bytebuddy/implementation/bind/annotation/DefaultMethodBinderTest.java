package net.bytebuddy.implementation.bind.annotation;


import java.lang.reflect.Method;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.annotation.DefaultMethod.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class DefaultMethodBinderTest extends AbstractAnnotationBinderTest<DefaultMethod> {
    public DefaultMethodBinderTest() {
        super(DefaultMethod.class);
    }

    @Mock
    private TypeDescription targetType;

    @Mock
    private TypeDescription interfaceType;

    @Mock
    private TypeDescription.Generic genericTargetType;

    @Mock
    private TypeDescription.Generic genericInterfaceType;

    @Mock
    private MethodDescription.SignatureToken token;

    @Mock
    private Implementation.SpecialMethodInvocation specialMethodInvocation;

    @Test(expected = IllegalStateException.class)
    public void testBindNoMethodParameter() throws Exception {
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBind() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        Mockito.when(implementationTarget.invokeDefault(token)).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        Mockito.when(annotation.targetType()).thenReturn(((Class) (void.class)));
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBindNoInterface() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(implementationTarget.invokeDefault(token)).thenReturn(specialMethodInvocation);
        Mockito.when(annotation.targetType()).thenReturn(((Class) (void.class)));
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBindExplicit() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        Mockito.when(implementationTarget.invokeDefault(token, of(Runnable.class))).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        Mockito.when(annotation.targetType()).thenReturn(((Class) (Runnable.class)));
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(genericInterfaceType, genericInterfaceType));
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testBindExplicitNoInterface() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        Mockito.when(annotation.targetType()).thenReturn(((Class) (Void.class)));
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBindIllegalFallback() throws Exception {
        Mockito.when(targetType.isAssignableFrom(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        Mockito.when(annotation.nullIfImpossible()).thenReturn(true);
        Mockito.when(implementationTarget.invokeDefault(token)).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(false);
        Mockito.when(annotation.targetType()).thenReturn(((Class) (void.class)));
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

