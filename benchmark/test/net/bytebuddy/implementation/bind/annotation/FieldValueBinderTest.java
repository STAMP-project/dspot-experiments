package net.bytebuddy.implementation.bind.annotation;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;
import static net.bytebuddy.implementation.bind.annotation.FieldValue.Binder.Delegate.BEAN_PROPERTY;
import static net.bytebuddy.implementation.bind.annotation.FieldValue.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class FieldValueBinderTest extends AbstractAnnotationBinderTest<FieldValue> {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Mock
    private FieldDescription.InDefinedShape fieldDescription;

    @Mock
    private TypeDescription.Generic fieldType;

    @Mock
    private TypeDescription.Generic targetType;

    @Mock
    private TypeDescription rawFieldType;

    public FieldValueBinderTest() {
        super(FieldValue.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldOfArrayThrowsException() throws Exception {
        Mockito.doReturn(Object[].class).when(annotation).declaringType();
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldOfPrimitiveThrowsException() throws Exception {
        Mockito.doReturn(int.class).when(annotation).declaringType();
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }

    @Test
    public void testLegalAssignment() throws Exception {
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(new FieldList.Explicit<FieldDescription.InDefinedShape>(fieldDescription));
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testIllegalAssignmentNonAssignable() throws Exception {
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(new FieldList.Explicit<FieldDescription.InDefinedShape>(fieldDescription));
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(false);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalAssignmentStaticMethod() throws Exception {
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(new FieldList.Explicit<FieldDescription.InDefinedShape>(fieldDescription));
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(source.isStatic()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testLegalAssignmentStaticMethodStaticField() throws Exception {
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(new FieldList.Explicit<FieldDescription.InDefinedShape>(fieldDescription));
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(source.isStatic()).thenReturn(true);
        Mockito.when(fieldDescription.isStatic()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testIllegalAssignmentNoField() throws Exception {
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(new FieldList.Empty<FieldDescription.InDefinedShape>());
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIllegalAssignmentNonVisible() throws Exception {
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(((FieldList) (new FieldList.Explicit<FieldDescription>(fieldDescription))));
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(false);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testLegalAssignmentExplicitType() throws Exception {
        Mockito.doReturn(FieldValueBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(instrumentedType.isAssignableTo(of(FieldValueBinderTest.Foo.class))).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testIllegalAssignmentExplicitTypeNonAssignable() throws Exception {
        Mockito.doReturn(FieldValueBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(instrumentedType.isAssignableTo(of(FieldValueBinderTest.Foo.class))).thenReturn(false);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalAssignmentExplicitTypeNonAssignableFieldType() throws Exception {
        Mockito.doReturn(FieldValueBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(instrumentedType.isAssignableTo(of(FieldValueBinderTest.Foo.class))).thenReturn(false);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testIllegalAssignmentExplicitTypeStaticMethod() throws Exception {
        Mockito.doReturn(FieldValueBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(source.isStatic()).thenReturn(true);
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(instrumentedType.isAssignableTo(of(FieldValueBinderTest.Foo.class))).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testLegalAssignmentExplicitTypeStaticMethodStaticField() throws Exception {
        Mockito.doReturn(FieldValueBinderTest.FooStatic.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(source.isStatic()).thenReturn(true);
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(instrumentedType.isAssignableTo(of(FieldValueBinderTest.FooStatic.class))).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testIllegalAssignmentExplicitTypeNoField() throws Exception {
        Mockito.doReturn(FieldValueBinderTest.Foo.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(FieldValueBinderTest.BAR);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(instrumentedType.isAssignableTo(of(FieldValueBinderTest.Foo.class))).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testGetterNameDiscovery() throws Exception {
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(BEAN_PROPERTY);
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(new FieldList.Explicit<FieldDescription.InDefinedShape>(fieldDescription));
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(source.getInternalName()).thenReturn("getFoo");
        Mockito.when(source.getActualName()).thenReturn("getFoo");
        Mockito.when(source.getReturnType()).thenReturn(OBJECT);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription.InDefinedShape>());
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testGetterNameDiscoveryBoolean() throws Exception {
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(BEAN_PROPERTY);
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(new FieldList.Explicit<FieldDescription.InDefinedShape>(fieldDescription));
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(source.getInternalName()).thenReturn("isFoo");
        Mockito.when(source.getActualName()).thenReturn("isFoo");
        Mockito.when(source.getReturnType()).thenReturn(of(boolean.class));
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription.InDefinedShape>());
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testSetterNameDiscovery() throws Exception {
        Mockito.doReturn(void.class).when(annotation).declaringType();
        Mockito.when(annotation.value()).thenReturn(BEAN_PROPERTY);
        Mockito.when(instrumentedType.getDeclaredFields()).thenReturn(new FieldList.Explicit<FieldDescription.InDefinedShape>(fieldDescription));
        Mockito.when(fieldDescription.getActualName()).thenReturn(FieldValueBinderTest.FOO);
        Mockito.when(fieldDescription.isVisibleTo(instrumentedType)).thenReturn(true);
        Mockito.when(target.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(stackManipulation.isValid()).thenReturn(true);
        Mockito.when(source.getInternalName()).thenReturn("setFoo");
        Mockito.when(source.getActualName()).thenReturn("setFoo");
        Mockito.when(source.getReturnType()).thenReturn(VOID);
        Mockito.when(source.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(source, OBJECT));
        MethodDelegationBinder.ParameterBinding<?> binding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(binding.isValid(), CoreMatchers.is(true));
    }

    public static class Foo {
        public String foo;
    }

    public static class FooStatic {
        public static String foo;
    }
}

