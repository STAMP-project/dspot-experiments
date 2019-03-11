package net.bytebuddy.implementation.attribute;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeList;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;


public class TypeAttributeAppenderExplicitTest extends AbstractTypeAttributeAppenderTest {
    @Test
    public void testAnnotationNoRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        new TypeAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance())).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    @Test
    public void testAnnotationByteCodeRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        new TypeAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance())).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    @Test
    public void testAnnotationClassFileRetention() throws Exception {
        Mockito.when(instrumentedType.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getInterfaces()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(instrumentedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        new TypeAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance())).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(classVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    public @interface SimpleAnnotation {
        String value();
    }
}

