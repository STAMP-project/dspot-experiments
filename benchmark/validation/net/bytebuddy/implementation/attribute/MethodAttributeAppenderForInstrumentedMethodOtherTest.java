package net.bytebuddy.implementation.attribute;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypeReference;

import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;
import static net.bytebuddy.implementation.attribute.MethodAttributeAppender.ForInstrumentedMethod.EXCLUDING_RECEIVER;
import static net.bytebuddy.implementation.attribute.MethodAttributeAppender.ForInstrumentedMethod.INCLUDING_RECEIVER;


public class MethodAttributeAppenderForInstrumentedMethodOtherTest extends AbstractMethodAttributeAppenderTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testReceiverTypeTypeAnnotationsIgnored() throws Exception {
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getReceiverType()).thenReturn(simpleAnnotatedType);
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        EXCLUDING_RECEIVER.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verify(methodDescription).getDeclaredAnnotations();
        Mockito.verify(methodDescription).getParameters();
        Mockito.verify(methodDescription).getReturnType();
        Mockito.verify(methodDescription).getExceptionTypes();
        Mockito.verify(methodDescription).getTypeVariables();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReceiverTypeTypeAnnotationsNoRetention() throws Exception {
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getReceiverType()).thenReturn(simpleAnnotatedType);
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Qux.Instance()));
        INCLUDING_RECEIVER.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verify(methodDescription).getDeclaredAnnotations();
        Mockito.verify(methodDescription).getParameters();
        Mockito.verify(methodDescription).getReturnType();
        Mockito.verify(methodDescription).getExceptionTypes();
        Mockito.verify(methodDescription).getTypeVariables();
        Mockito.verify(methodDescription).getReceiverType();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReceiverTypeTypeAnnotationsRuntimeRetention() throws Exception {
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getReceiverType()).thenReturn(simpleAnnotatedType);
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.Baz.Instance()));
        INCLUDING_RECEIVER.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeReference(TypeReference.METHOD_RECEIVER).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.Baz.class), true);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(methodDescription).getDeclaredAnnotations();
        Mockito.verify(methodDescription).getParameters();
        Mockito.verify(methodDescription).getReturnType();
        Mockito.verify(methodDescription).getExceptionTypes();
        Mockito.verify(methodDescription).getTypeVariables();
        Mockito.verify(methodDescription).getReceiverType();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReceiverTypeTypeAnnotationsClassFileRetention() throws Exception {
        Mockito.when(methodDescription.getParameters()).thenReturn(((ParameterList) (new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription>())));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getExceptionTypes()).thenReturn(new TypeList.Generic.Empty());
        Mockito.when(methodDescription.getDeclaredAnnotations()).thenReturn(new AnnotationList.Empty());
        Mockito.when(methodDescription.getReceiverType()).thenReturn(simpleAnnotatedType);
        Mockito.when(simpleAnnotatedType.getDeclaredAnnotations()).thenReturn(new AnnotationList.ForLoadedAnnotations(new AbstractAttributeAppenderTest.QuxBaz.Instance()));
        INCLUDING_RECEIVER.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(methodVisitor).visitTypeAnnotation(TypeReference.newTypeReference(TypeReference.METHOD_RECEIVER).getValue(), null, Type.getDescriptor(AbstractAttributeAppenderTest.QuxBaz.class), false);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verify(methodDescription).getDeclaredAnnotations();
        Mockito.verify(methodDescription).getParameters();
        Mockito.verify(methodDescription).getReturnType();
        Mockito.verify(methodDescription).getExceptionTypes();
        Mockito.verify(methodDescription).getTypeVariables();
        Mockito.verify(methodDescription).getReceiverType();
        Mockito.verifyNoMoreInteractions(methodDescription);
    }

    @Test
    public void testIncludingFactory() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        MatcherAssert.assertThat(INCLUDING_RECEIVER.make(typeDescription), CoreMatchers.is(((MethodAttributeAppender) (INCLUDING_RECEIVER))));
    }

    @Test
    public void testExcludingFactory() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        MatcherAssert.assertThat(EXCLUDING_RECEIVER.make(typeDescription), CoreMatchers.is(((MethodAttributeAppender) (EXCLUDING_RECEIVER))));
    }
}

