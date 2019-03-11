package net.bytebuddy.implementation.attribute;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.attribute.MethodAttributeAppender.NoOp.INSTANCE;


public class MethodAttributeAppenderNoOpTest extends AbstractMethodAttributeAppenderTest {
    @Test
    public void testApplication() throws Exception {
        INSTANCE.apply(methodVisitor, methodDescription, annotationValueFilter);
        Mockito.verifyZeroInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(methodDescription);
    }

    @Test
    public void testFactory() throws Exception {
        MatcherAssert.assertThat(INSTANCE.make(instrumentedType), CoreMatchers.is(((MethodAttributeAppender) (INSTANCE))));
        Mockito.verifyZeroInteractions(instrumentedType);
    }
}

