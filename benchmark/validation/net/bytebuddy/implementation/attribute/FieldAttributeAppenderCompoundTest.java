package net.bytebuddy.implementation.attribute;


import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FieldAttributeAppenderCompoundTest extends AbstractFieldAttributeAppenderTest {
    @Mock
    private FieldAttributeAppender first;

    @Mock
    private FieldAttributeAppender second;

    @Test
    public void testApplication() throws Exception {
        FieldAttributeAppender fieldAttributeAppender = new FieldAttributeAppender.Compound(first, second);
        fieldAttributeAppender.apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verify(first).apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verifyNoMoreInteractions(second);
        Mockito.verifyZeroInteractions(instrumentedType);
    }
}

