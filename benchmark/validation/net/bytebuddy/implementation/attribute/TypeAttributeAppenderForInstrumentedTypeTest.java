package net.bytebuddy.implementation.attribute;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeList;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypeReference;

import static net.bytebuddy.implementation.attribute.TypeAttributeAppender.ForInstrumentedType.INSTANCE;


public class TypeAttributeAppenderForInstrumentedTypeTest extends AbstractTypeAttributeAppenderTest {
    @Test
    public void testAnnotationNoRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testAnnotationByteCodeRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testAnnotationClassFileRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testSuperClassTypeAnnotationNoRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(instrumentedType.getSuperClass()).thenReturn(simpleAnnotatedType);
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testSuperClassTypeAnnotationByteCodeRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(instrumentedType.getSuperClass()).thenReturn(simpleAnnotatedType);
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newSuperTypeReference((-1)).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testSuperClassTypeAnnotationClassFileRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(instrumentedType.getSuperClass()).thenReturn(simpleAnnotatedType);
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newSuperTypeReference((-1)).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testInterfaceTypeAnnotationNoRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(simpleAnnotatedType));
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testInterfaceTypeAnnotationRuntimeRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(simpleAnnotatedType));
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newSuperTypeReference(0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testInterfaceTypeAnnotations() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(simpleAnnotatedType));
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newSuperTypeReference(0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testTypeVariableTypeAnnotationNoRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Explicit(annotatedTypeVariable));
        Mockito.when(annotatedTypeVariable.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(annotatedTypeVariableBound.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testTypeVariableTypeAnnotationRuntimeRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Explicit(annotatedTypeVariable));
        Mockito.when(annotatedTypeVariable.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(annotatedTypeVariableBound.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newTypeParameterReference(TypeReference.CLASS_TYPE_PARAMETER, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newTypeParameterBoundReference(TypeReference.CLASS_TYPE_PARAMETER_BOUND, 0, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testTypeVariableTypeAnnotations() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Explicit(annotatedTypeVariable));
        Mockito.when(annotatedTypeVariable.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(annotatedTypeVariableBound.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newTypeParameterReference(TypeReference.CLASS_TYPE_PARAMETER, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newTypeParameterBoundReference(TypeReference.CLASS_TYPE_PARAMETER_BOUND, 0, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getSuperClass();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }
}

