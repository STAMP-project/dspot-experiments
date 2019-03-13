package net.bytebuddy.implementation.attribute;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypeReference;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.implementation.attribute.FieldAttributeAppender.ForInstrumentedField.INSTANCE;


public class FieldAttributeAppenderForInstrumentedFieldTest extends AbstractFieldAttributeAppenderTest {
    @Test
    public void testFactory() throws Exception {
        Assert.assertThat(INSTANCE.make(instrumentedType), CoreMatchers.is(((FieldAttributeAppender) (INSTANCE))));
    }

    @Test
    public void testAnnotationAppenderNoRetention() throws Exception {
        Mockito.when(fieldDescription.getType()).thenReturn(OBJECT);
        Mockito.when(fieldDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        INSTANCE.apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(fieldVisitor);
        Mockito.verify(fieldDescription).getDeclaredAnnotations();
        Mockito.verify(fieldDescription).getType();
        Mockito.verifyNoMoreInteractions(fieldDescription);
    }

    @Test
    public void testAnnotationAppenderRuntimeRetention() throws Exception {
        Mockito.when(fieldDescription.getType()).thenReturn(OBJECT);
        Mockito.when(fieldDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        INSTANCE.apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verify(fieldVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(fieldVisitor);
        Mockito.verify(fieldDescription).getDeclaredAnnotations();
        Mockito.verify(fieldDescription).getType();
        Mockito.verifyNoMoreInteractions(fieldDescription);
    }

    @Test
    public void testAnnotationAppenderByteCodeRetention() throws Exception {
        Mockito.when(fieldDescription.getType()).thenReturn(OBJECT);
        Mockito.when(fieldDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        INSTANCE.apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verify(fieldVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(fieldVisitor);
        Mockito.verify(fieldDescription).getDeclaredAnnotations();
        Mockito.verify(fieldDescription).getType();
        Mockito.verifyNoMoreInteractions(fieldDescription);
    }

    @Test
    public void testFieldTypeTypeAnnotationNoRetention() throws Exception {
        Mockito.when(fieldDescription.getType()).thenReturn(simpleAnnotatedType);
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        Mockito.when(fieldDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        INSTANCE.apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(fieldVisitor);
        Mockito.verify(fieldDescription).getDeclaredAnnotations();
        Mockito.verify(fieldDescription).getType();
        Mockito.verifyNoMoreInteractions(fieldDescription);
    }

    @Test
    public void testFieldTypeTypeAnnotationRuntimeRetention() throws Exception {
        Mockito.when(fieldDescription.getType()).thenReturn(simpleAnnotatedType);
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(fieldDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        INSTANCE.apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verify(fieldVisitor).visitTypeAnnotation(TypeReference.newTypeReference(TypeReference.FIELD).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(fieldVisitor);
        Mockito.verify(fieldDescription).getDeclaredAnnotations();
        Mockito.verify(fieldDescription).getType();
        Mockito.verifyNoMoreInteractions(fieldDescription);
    }

    @Test
    public void testFieldTypeTypeAnnotationByteCodeRetention() throws Exception {
        Mockito.when(fieldDescription.getType()).thenReturn(simpleAnnotatedType);
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        Mockito.when(fieldDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        INSTANCE.apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verify(fieldVisitor).visitTypeAnnotation(TypeReference.newTypeReference(TypeReference.FIELD).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(fieldVisitor);
        Mockito.verify(fieldDescription).getDeclaredAnnotations();
        Mockito.verify(fieldDescription).getType();
        Mockito.verifyNoMoreInteractions(fieldDescription);
    }
}

