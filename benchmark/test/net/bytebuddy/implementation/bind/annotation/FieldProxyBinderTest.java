package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;
import static net.bytebuddy.implementation.bind.annotation.FieldProxy.Binder.BEAN_PROPERTY;
import static net.bytebuddy.implementation.bind.annotation.FieldProxy.Binder.FieldResolver.Unresolved.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class FieldProxyBinderTest extends AbstractAnnotationBinderTest<FieldProxy> {
    private static final String FOO = "foo";

    @Mock
    private MethodDescription.InDefinedShape getterMethod;

    @Mock
    private MethodDescription.InDefinedShape setterMethod;

    @Mock
    private TypeDescription setterType;

    @Mock
    private TypeDescription getterType;

    @Mock
    private TypeDescription fieldType;

    @Mock
    private TypeDescription.Generic genericSetterType;

    @Mock
    private TypeDescription.Generic genericGetterType;

    @Mock
    private TypeDescription.Generic genericFieldType;

    @Mock
    private FieldDescription.InDefinedShape fieldDescription;

    public FieldProxyBinderTest() {
        super(FieldProxy.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldOfArrayThrowsException() throws Exception {
        Mockito.doReturn(Object[].class).when(annotation).declaringType();
        new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldOfPrimitiveThrowsException() throws Exception {
        Mockito.doReturn(int.class).when(annotation).declaringType();
        new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalType() throws Exception {
        Mockito.doReturn(FieldProxyBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldProxyBinderTest.FOO);
        TypeDescription targetType = Mockito.mock(TypeDescription.class);
        TypeDescription.Generic genericTargetType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(genericTargetType.asErasure()).thenReturn(targetType);
        Mockito.when(target.getType()).thenReturn(genericTargetType);
        Mockito.when(instrumentedType.isAssignableTo(of(FieldProxyBinderTest.Foo.class))).thenReturn(true);
        new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test
    public void testGetterForImplicitNamedFieldInHierarchy() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericGetterType);
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(BEAN_PROPERTY);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(source.getReturnType()).thenReturn(genericFieldType);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription.InDefinedShape>());
        Mockito.when(source.getName()).thenReturn("getFoo");
        Mockito.when(source.getActualName()).thenReturn("getFoo");
        Mockito.when(source.getInternalName()).thenReturn("getFoo");
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testGetterForExplicitNamedFieldInHierarchy() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericGetterType);
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(source.getReturnType()).thenReturn(genericFieldType);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription.InDefinedShape>());
        Mockito.when(source.getName()).thenReturn("getFoo");
        Mockito.when(source.getInternalName()).thenReturn("getFoo");
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testGetterForImplicitNamedFieldInNamedType() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericGetterType);
        Mockito.doReturn(FieldProxyBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(instrumentedType.isAssignableTo(of(FieldProxyBinderTest.Foo.class))).thenReturn(true);
        Mockito.when(annotation.value()).thenReturn(BEAN_PROPERTY);
        Mockito.when(fieldDescription.getInternalName()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(source.getReturnType()).thenReturn(genericFieldType);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription.InDefinedShape>());
        Mockito.when(source.getName()).thenReturn("getFoo");
        Mockito.when(source.getActualName()).thenReturn("getFoo");
        Mockito.when(source.getInternalName()).thenReturn("getFoo");
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testGetterForExplicitNamedFieldInNamedType() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericGetterType);
        Mockito.doReturn(FieldProxyBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(instrumentedType.isAssignableTo(of(FieldProxyBinderTest.Foo.class))).thenReturn(true);
        Mockito.when(annotation.value()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(fieldDescription.getInternalName()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(source.getReturnType()).thenReturn(genericFieldType);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription.InDefinedShape>());
        Mockito.when(source.getName()).thenReturn("getFoo");
        Mockito.when(source.getInternalName()).thenReturn("getFoo");
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testSetterForImplicitNamedFieldInHierarchy() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericSetterType);
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(BEAN_PROPERTY);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(source.getReturnType()).thenReturn(VOID);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(source, fieldType));
        Mockito.when(source.getActualName()).thenReturn("setFoo");
        Mockito.when(source.getInternalName()).thenReturn("setFoo");
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testSetterForExplicitNamedFieldInHierarchy() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericSetterType);
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(source.getReturnType()).thenReturn(VOID);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(source, fieldType));
        Mockito.when(source.getName()).thenReturn("setFoo");
        Mockito.when(source.getInternalName()).thenReturn("setFoo");
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testSetterForImplicitNamedFieldInNamedType() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericSetterType);
        Mockito.doReturn(FieldProxyBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(instrumentedType.isAssignableTo(of(FieldProxyBinderTest.Foo.class))).thenReturn(true);
        Mockito.when(annotation.value()).thenReturn(BEAN_PROPERTY);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(source.getReturnType()).thenReturn(VOID);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(source, fieldType));
        Mockito.when(source.getName()).thenReturn("setFoo");
        Mockito.when(source.getActualName()).thenReturn("setFoo");
        Mockito.when(source.getInternalName()).thenReturn("setFoo");
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testSetterForExplicitNamedFieldInNamedType() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericSetterType);
        Mockito.doReturn(FieldProxyBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(instrumentedType.isAssignableTo(of(FieldProxyBinderTest.Foo.class))).thenReturn(true);
        Mockito.when(annotation.value()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldProxyBinderTest.FOO);
        Mockito.when(source.getReturnType()).thenReturn(VOID);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(source, fieldType));
        Mockito.when(source.getName()).thenReturn("setFoo");
        Mockito.when(source.getInternalName()).thenReturn("setFoo");
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testDefiningTypeNotAssignable() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericSetterType);
        Mockito.doReturn(FieldProxyBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(instrumentedType.isAssignableTo(of(FieldProxyBinderTest.Foo.class))).thenReturn(false);
        MethodDelegationBinder.ParameterBinding<?> binding = new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testDefiningTypePrimitive() throws Exception {
        Mockito.when(target.getType()).thenReturn(genericSetterType);
        Mockito.doReturn(int.class).when(annotation).declaringType();
        new FieldProxy.Binder(getterMethod, setterMethod).bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testUnresolvedResolverNoProxyType() throws Exception {
        INSTANCE.getProxyType();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnresolvedResolverNoApplication() throws Exception {
        INSTANCE.apply(Mockito.mock(DynamicType.Builder.class), Mockito.mock(FieldDescription.class), Mockito.mock(Assigner.class), Mockito.mock(MethodAccessorFactory.class));
    }

    public static class Foo {
        public FieldProxyBinderTest.Foo foo;
    }
}

