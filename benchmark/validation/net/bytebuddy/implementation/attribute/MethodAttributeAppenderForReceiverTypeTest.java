package net.bytebuddy.implementation.attribute;


import net.bytebuddy.description.annotation.AnnotationList;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypeReference;


public class MethodAttributeAppenderForReceiverTypeTest extends AbstractMethodAttributeAppenderTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testReceiverTypeAnnotationNoRetention() throws Exception {
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        new MethodAttributeAppender.ForReceiverType(simpleAnnotatedType).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(methodDescription);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodAnnotationRuntimeRetention() throws Exception {
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        new MethodAttributeAppender.ForReceiverType(simpleAnnotatedType).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeReference(TypeReference.METHOD_RECEIVER).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(methodDescription);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMethodAnnotationClassFileRetention() throws Exception {
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        new MethodAttributeAppender.ForReceiverType(simpleAnnotatedType).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeReference(TypeReference.METHOD_RECEIVER).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(methodDescription);
    }
}

