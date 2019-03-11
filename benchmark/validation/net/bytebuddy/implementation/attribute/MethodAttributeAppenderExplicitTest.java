package net.bytebuddy.implementation.attribute;


import java.lang.annotation.Annotation;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

import static net.bytebuddy.implementation.attribute.MethodAttributeAppender.Explicit.of;


public class MethodAttributeAppenderExplicitTest extends AbstractMethodAttributeAppenderTest {
    private static final int PARAMETER_INDEX = 0;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private ParameterDescription parameterDescription;

    @Test
    public void testAnnotationAppenderNoRetention() throws Exception {
        new MethodAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance())).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(methodDescription);
    }

    @Test
    public void testAnnotationAppenderRuntimeRetention() throws Exception {
        new MethodAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance())).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(methodDescription);
    }

    @Test
    public void testAnnotationAppenderByteCodeRetention() throws Exception {
        new MethodAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance())).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(methodDescription);
    }

    @Test
    public void testAnnotationAppenderForParameterNoRetention() throws Exception {
        new MethodAttributeAppender.Explicit(MethodAttributeAppenderExplicitTest.PARAMETER_INDEX, new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance())).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verify(methodDescription).getParameters();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test
    public void testAnnotationAppenderForParameterRuntimeRetention() throws Exception {
        new MethodAttributeAppender.Explicit(MethodAttributeAppenderExplicitTest.PARAMETER_INDEX, new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance())).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitParameterAnnotation(MethodAttributeAppenderExplicitTest.PARAMETER_INDEX, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(methodDescription).getParameters();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test
    public void testAnnotationAppenderForParameterByteCodeRetention() throws Exception {
        new MethodAttributeAppender.Explicit(MethodAttributeAppenderExplicitTest.PARAMETER_INDEX, new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance())).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitParameterAnnotation(MethodAttributeAppenderExplicitTest.PARAMETER_INDEX, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(methodDescription).getParameters();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnnotationAppenderNotEnoughParameters() throws Exception {
        new MethodAttributeAppender.Explicit(((MethodAttributeAppenderExplicitTest.PARAMETER_INDEX) + 1), new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance())).apply(methodVisitor, methodDescription, annotationValueFilter);
    }

    @Test
    public void testFactory() throws Exception {
        MethodAttributeAppender.Explicit methodAttributeAppender = new MethodAttributeAppender.Explicit(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        MatcherAssert.assertThat(methodAttributeAppender.make(instrumentedType), CoreMatchers.sameInstance(((MethodAttributeAppender) (methodAttributeAppender))));
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    @Test
    public void testOfMethod() throws Exception {
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        Mockito.when(parameterDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        MethodAttributeAppender methodAttributeAppender = of(methodDescription).make(instrumentedType);
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitAnnotation(Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verify(methodVisitor).visitParameterAnnotation(MethodAttributeAppenderExplicitTest.PARAMETER_INDEX, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(methodDescription, Mockito.times(2)).getParameters();
        Mockito.verify(methodDescription).getDeclaredAnnotations();
        Mockito.verifyNoMoreInteractions(methodDescription);
        Mockito.verify(parameterDescription).getIndex();
        Mockito.verify(parameterDescription).getDeclaredAnnotations();
        Mockito.verifyNoMoreInteractions(parameterDescription);
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    public @interface SimpleAnnotation {
        String value();

        class Instance implements MethodAttributeAppenderExplicitTest.SimpleAnnotation {
            private final String value;

            public Instance(String value) {
                this.value = value;
            }

            public String value() {
                return value;
            }

            public Class<? extends Annotation> annotationType() {
                return MethodAttributeAppenderExplicitTest.SimpleAnnotation.class;
            }
        }
    }
}

