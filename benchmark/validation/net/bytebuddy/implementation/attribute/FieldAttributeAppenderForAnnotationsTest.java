package net.bytebuddy.implementation.attribute;


import net.bytebuddy.description.annotation.AnnotationList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;


public class FieldAttributeAppenderForAnnotationsTest extends AbstractFieldAttributeAppenderTest {
    @Test
    public void testAnnotationAppenderNoRetention() throws Exception {
        new FieldAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance())).apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(fieldVisitor);
        Mockito.verifyZeroInteractions(fieldDescription);
    }

    @Test
    public void testAnnotationAppenderRuntimeRetention() throws Exception {
        new FieldAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance())).apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verify(fieldVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(fieldVisitor);
        Mockito.verifyZeroInteractions(fieldDescription);
    }

    @Test
    public void testAnnotationAppenderByteCodeRetention() throws Exception {
        new FieldAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance())).apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verify(fieldVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(fieldVisitor);
        Mockito.verifyZeroInteractions(fieldDescription);
    }

    @Test
    public void testFactory() throws Exception {
        FieldAttributeAppender.Explicit fieldAttributeAppender = new FieldAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        MatcherAssert.assertThat(fieldAttributeAppender.make(instrumentedType), CoreMatchers.sameInstance(((FieldAttributeAppender) (fieldAttributeAppender))));
    }

    public @interface SimpleAnnotation {
        String value();
    }
}

