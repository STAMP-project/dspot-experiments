package net.bytebuddy.implementation.attribute;


import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TypeAttributeAppenderCompoundTest extends AbstractTypeAttributeAppenderTest {
    @Mock
    private TypeAttributeAppender first;

    @Mock
    private TypeAttributeAppender second;

    @Test
    public void testApplication() throws Exception {
        TypeAttributeAppender typeAttributeAppender = new TypeAttributeAppender.Compound(first, second);
        typeAttributeAppender.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verify(first).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(second);
        Mockito.verifyZeroInteractions(instrumentedType);
    }
}

