package net.bytebuddy.implementation.attribute;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.attribute.FieldAttributeAppender.NoOp.INSTANCE;


public class FieldAttributeAppenderNoOpTest extends AbstractFieldAttributeAppenderTest {
    @Test
    public void testApplication() throws Exception {
        INSTANCE.apply(fieldVisitor, fieldDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(fieldVisitor);
        Mockito.verifyZeroInteractions(fieldDescription);
    }

    @Test
    public void testFactory() throws Exception {
        MatcherAssert.assertThat(INSTANCE.make(instrumentedType), CoreMatchers.sameInstance(((FieldAttributeAppender) (INSTANCE))));
        Mockito.verifyZeroInteractions(instrumentedType);
    }
}

