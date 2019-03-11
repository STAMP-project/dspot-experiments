package net.bytebuddy.implementation.attribute;


import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.attribute.TypeAttributeAppender.NoOp.INSTANCE;


public class TypeAttributeAppenderNoOpTest extends AbstractTypeAttributeAppenderTest {
    @Test
    public void testApplication() throws Exception {
        INSTANCE.apply(classVisitor, instrumentedType, annotationValueFilter);
        Mockito.verifyZeroInteractions(classVisitor);
        Mockito.verifyZeroInteractions(instrumentedType);
    }
}

