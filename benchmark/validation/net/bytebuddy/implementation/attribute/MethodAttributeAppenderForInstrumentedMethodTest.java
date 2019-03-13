package net.bytebuddy.implementation.attribute;


import java.lang.annotation.RetentionPolicy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypeReference;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;


@RunWith(Parameterized.class)
public class MethodAttributeAppenderForInstrumentedMethodTest extends AbstractMethodAttributeAppenderTest {
    private final MethodAttributeAppender methodAttributeAppender;

    public MethodAttributeAppenderForInstrumentedMethodTest(MethodAttributeAppender methodAttributeAppender) {
        this.methodAttributeAppender = methodAttributeAppender;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodAnnotationNoRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodAnnotationRuntimeRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodAnnotationClassFileRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodParameterAnnotationNoRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getType()).thenReturn(OBJECT);
        Mockito.when(parameterDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Explicit<ParameterDescription>(parameterDescription))));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodParameterAnnotationRuntimeRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getType()).thenReturn(OBJECT);
        Mockito.when(parameterDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Explicit<ParameterDescription>(parameterDescription))));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitParameterAnnotation(0, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodParameterAnnotationClassFileRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getType()).thenReturn(OBJECT);
        Mockito.when(parameterDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Explicit<ParameterDescription>(parameterDescription))));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitParameterAnnotation(0, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodReturnTypeTypeAnnotationNoRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(simpleAnnotatedType);
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodReturnTypeTypeAnnotationRuntimeRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(simpleAnnotatedType);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeReference(TypeReference.METHOD_RETURN).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodReturnTypeTypeAnnotationClassFileRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(simpleAnnotatedType);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeReference(TypeReference.METHOD_RETURN).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodExceptionTypeTypeAnnotationNoRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Explicit(simpleAnnotatedType));
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodExceptionTypeTypeAnnotationRuntimeRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Explicit(simpleAnnotatedType));
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newExceptionReference(0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodExceptionTypeTypeAnnotationClassFileRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Explicit(simpleAnnotatedType));
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newExceptionReference(0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodParameterTypeTypeAnnotationNoRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(parameterDescription.getType()).thenReturn(simpleAnnotatedType);
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Explicit<ParameterDescription>(parameterDescription))));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodParameterTypeTypeAnnotationRuntimeRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(parameterDescription.getType()).thenReturn(simpleAnnotatedType);
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Explicit<ParameterDescription>(parameterDescription))));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newFormalParameterReference(0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodParameterTypeTypeAnnotationClassFileRetention() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(parameterDescription.getType()).thenReturn(simpleAnnotatedType);
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Explicit<ParameterDescription>(parameterDescription))));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newFormalParameterReference(0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTypeVariableTypeAnnotationNoRetention() throws Exception {
        Mockito.when(annotatedTypeVariable.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(annotatedTypeVariableBound.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Explicit(annotatedTypeVariable));
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTypeVariableTypeAnnotationRuntimeRetention() throws Exception {
        Mockito.when(annotatedTypeVariable.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(annotatedTypeVariableBound.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Explicit(annotatedTypeVariable));
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeParameterReference(TypeReference.METHOD_TYPE_PARAMETER, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeParameterBoundReference(TypeReference.METHOD_TYPE_PARAMETER_BOUND, 0, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyZeroInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTypeVariableTypeAnnotations() throws Exception {
        Mockito.when(annotatedTypeVariable.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(annotatedTypeVariableBound.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Explicit(annotatedTypeVariable));
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeParameterReference(TypeReference.METHOD_TYPE_PARAMETER, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeParameterBoundReference(TypeReference.METHOD_TYPE_PARAMETER_BOUND, 0, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testJdkTypeIsFiltered() throws Exception {
        Mockito.when(annotationValueFilter.isRelevant(ArgumentMatchers.any(AnnotationDescription.class), ArgumentMatchers.any(MethodDescription.InDefinedShape.class))).thenReturn(true);
        AnnotationDescription annotationDescription = Mockito.mock(AnnotationDescription.class);
        TypeDescription annotationType = Mockito.mock(TypeDescription.class);
        Mockito.when(annotationType.getDeclaredMethods()).thenReturn(new MethodList.Empty<MethodDescription.InDefinedShape>());
        Mockito.when(annotationDescription.getRetention()).thenReturn(RetentionPolicy.RUNTIME);
        Mockito.when(annotationDescription.getAnnotationType()).thenReturn(annotationType);
        Mockito.when(annotationType.getActualName()).thenReturn("jdk.internal.Sample");
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Explicit(annotationDescription));
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
    }
}

