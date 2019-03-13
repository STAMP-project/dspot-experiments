package net.bytebuddy.implementation.attribute;


import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodAttributeAppenderFactoryCompoundTest extends AbstractMethodAttributeAppenderTest {
    @Mock
    private MethodAttributeAppender.Factory firstFactory;

    @Mock
    private MethodAttributeAppender.Factory secondFactory;

    @Mock
    private MethodAttributeAppender first;

    @Mock
    private MethodAttributeAppender second;

    @Test
    public void testApplication() throws Exception {
        MethodAttributeAppender methodAttributeAppender = new MethodAttributeAppender.Factory.Compound(firstFactory, secondFactory).make(instrumentedType);
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(first).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(second);
        Mockito.verifyZeroInteractions(instrumentedType);
    }
}

