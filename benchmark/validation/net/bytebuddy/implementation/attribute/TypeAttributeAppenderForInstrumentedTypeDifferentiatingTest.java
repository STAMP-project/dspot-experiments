package net.bytebuddy.implementation.attribute;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypeReference;


public class TypeAttributeAppenderForInstrumentedTypeDifferentiatingTest extends AbstractTypeAttributeAppenderTest {
    @Mock
    private TypeDescription.Generic pseudoType;

    @Test
    public void testAnnotationNoRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance(), new AbstractAttributeAppenderTest.Qux.Instance()));
        new TypeAttributeAppender.ForInstrumentedType.Differentiating(1, 0, 0).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testAnnotationByteCodeRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance(), new AbstractAttributeAppenderTest.Baz.Instance()));
        new TypeAttributeAppender.ForInstrumentedType.Differentiating(1, 0, 0).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testAnnotationClassFileRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance(), new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        new TypeAttributeAppender.ForInstrumentedType.Differentiating(1, 0, 0).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testInterfaceTypeAnnotationNoRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(pseudoType, simpleAnnotatedType));
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        new TypeAttributeAppender.ForInstrumentedType.Differentiating(0, 0, 1).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testInterfaceTypeAnnotationRuntimeRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(pseudoType, simpleAnnotatedType));
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        new TypeAttributeAppender.ForInstrumentedType.Differentiating(0, 0, 1).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newSuperTypeReference(1).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testInterfaceTypeAnnotations() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Explicit(pseudoType, simpleAnnotatedType));
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        new TypeAttributeAppender.ForInstrumentedType.Differentiating(0, 0, 1).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newSuperTypeReference(1).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testTypeVariableTypeAnnotationNoRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Explicit(pseudoType, annotatedTypeVariable));
        Mockito.when(annotatedTypeVariable.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(annotatedTypeVariableBound.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        new TypeAttributeAppender.ForInstrumentedType.Differentiating(0, 1, 0).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testTypeVariableTypeAnnotationRuntimeRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Explicit(pseudoType, annotatedTypeVariable));
        Mockito.when(annotatedTypeVariable.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(annotatedTypeVariableBound.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        new TypeAttributeAppender.ForInstrumentedType.Differentiating(0, 1, 0).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newTypeParameterReference(TypeReference.CLASS_TYPE_PARAMETER, 1).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newTypeParameterBoundReference(TypeReference.CLASS_TYPE_PARAMETER_BOUND, 1, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }

    @Test
    public void testTypeVariableTypeAnnotations() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Explicit(pseudoType, annotatedTypeVariable));
        Mockito.when(annotatedTypeVariable.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(annotatedTypeVariableBound.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        new TypeAttributeAppender.ForInstrumentedType.Differentiating(0, 1, 0).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newTypeParameterReference(TypeReference.CLASS_TYPE_PARAMETER, 1).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verify(classVisitor).visitTypeAnnotation(TypeReference.newTypeParameterBoundReference(TypeReference.CLASS_TYPE_PARAMETER_BOUND, 1, 0).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(instrumentedType).getDeclaredAnnotations();
        Mockito.verify(instrumentedType).getInterfaces();
        Mockito.verify(instrumentedType).getTypeVariables();
        Mockito.verifyNoMoreInteractions(instrumentedType);
    }
}

