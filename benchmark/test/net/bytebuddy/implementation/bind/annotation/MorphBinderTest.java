package net.bytebuddy.implementation.bind.annotation;


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

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class MorphBinderTest extends AbstractAnnotationBinderTest<Morph> {
    @Mock
    private MethodDescription morphMethod;

    @Mock
    private MethodDescription.SignatureToken morphToken;

    @Mock
    private TypeDescription morphType;

    @Mock
    private TypeDescription defaultType;

    @Mock
    private TypeDescription.Generic genericMorphType;

    @Mock
    private MethodDescription.SignatureToken sourceToken;

    @Mock
    private Implementation.SpecialMethodInvocation specialMethodInvocation;

    public MorphBinderTest() {
        super(Morph.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalType() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericMorphType);
        Mockito.when(morphMethod.getDeclaringType()).thenReturn(Mockito.mock(TypeDescription.class));
        new Morph.Binder(morphMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test
    public void testSuperMethodCallInvalid() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericMorphType);
        Mockito.when(morphMethod.getDeclaringType()).thenReturn(morphType);
        Mockito.doReturn(void.class).when(annotation).defaultTarget();
        Mockito.when(implementationTarget.invokeSuper(sourceToken)).thenReturn(specialMethodInvocation);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = new Morph.Binder(morphMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
        Mockito.verify(specialMethodInvocation).isValid();
    }

    @Test
    public void testSuperMethodCallValid() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericMorphType);
        Mockito.when(morphMethod.getDeclaringType()).thenReturn(morphType);
        Mockito.doReturn(void.class).when(annotation).defaultTarget();
        Mockito.when(implementationTarget.invokeSuper(sourceToken)).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = new Morph.Binder(morphMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(specialMethodInvocation).isValid();
    }

    @Test
    public void testDefaultMethodCallImplicitInvalid() throws Exception {
        Mockito.when(source.asSignatureToken()).thenReturn(morphToken);
        Mockito.when(target.getType()).thenReturn(genericMorphType);
        Mockito.when(morphMethod.getDeclaringType()).thenReturn(morphType);
        Mockito.when(annotation.defaultMethod()).thenReturn(true);
        Mockito.doReturn(void.class).when(annotation).defaultTarget();
        Mockito.when(implementationTarget.invokeDefault(morphToken)).thenReturn(specialMethodInvocation);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = new Morph.Binder(morphMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
        Mockito.verify(specialMethodInvocation).isValid();
    }

    @Test
    public void testDefaultMethodCallImplicitValid() throws Exception {
        Mockito.when(source.asSignatureToken()).thenReturn(morphToken);
        Mockito.when(target.getType()).thenReturn(genericMorphType);
        Mockito.when(morphMethod.getDeclaringType()).thenReturn(morphType);
        Mockito.when(annotation.defaultMethod()).thenReturn(true);
        Mockito.doReturn(void.class).when(annotation).defaultTarget();
        Mockito.when(implementationTarget.invokeDefault(morphToken)).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = new Morph.Binder(morphMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(specialMethodInvocation).isValid();
    }

    @Test
    public void testDefaultMethodCallExplicitInvalid() throws Exception {
        Mockito.when(source.asSignatureToken()).thenReturn(morphToken);
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.ForLoadedTypes(MorphBinderTest.Foo.class));
        Mockito.when(target.getType()).thenReturn(genericMorphType);
        Mockito.when(morphMethod.getDeclaringType()).thenReturn(morphType);
        Mockito.when(annotation.defaultMethod()).thenReturn(true);
        Mockito.doReturn(MorphBinderTest.Foo.class).when(annotation).defaultTarget();
        Mockito.when(implementationTarget.invokeDefault(morphToken, of(MorphBinderTest.Foo.class))).thenReturn(specialMethodInvocation);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = new Morph.Binder(morphMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
        Mockito.verify(specialMethodInvocation).isValid();
    }

    @Test
    public void testDefaultMethodCallExplicitValid() throws Exception {
        Mockito.when(source.asSignatureToken()).thenReturn(morphToken);
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.ForLoadedTypes(MorphBinderTest.Foo.class));
        Mockito.when(target.getType()).thenReturn(genericMorphType);
        Mockito.when(morphMethod.getDeclaringType()).thenReturn(morphType);
        Mockito.when(annotation.defaultMethod()).thenReturn(true);
        Mockito.doReturn(MorphBinderTest.Foo.class).when(annotation).defaultTarget();
        Mockito.when(implementationTarget.invokeDefault(morphToken, of(MorphBinderTest.Foo.class))).thenReturn(specialMethodInvocation);
        Mockito.when(specialMethodInvocation.isValid()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = new Morph.Binder(morphMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(specialMethodInvocation).isValid();
    }

    /* empty */
    private interface Foo {}
}

