package net.bytebuddy.implementation.attribute;


import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodAttributeAppenderCompoundTest extends AbstractMethodAttributeAppenderTest {
    @Mock
    private MethodAttributeAppender first;

    @Mock
    private MethodAttributeAppender second;

    @Test
    public void testApplication() throws Exception {
        MethodAttributeAppender methodAttributeAppender = new MethodAttributeAppender.Compound(first, second);
        methodAttributeAppender.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verify(first).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(second);
        Mockito.verifyZeroInteractions(instrumentedType);
    }
}

