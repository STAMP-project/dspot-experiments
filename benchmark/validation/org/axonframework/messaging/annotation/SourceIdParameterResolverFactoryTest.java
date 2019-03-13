package org.axonframework.messaging.annotation;


import java.lang.reflect.Method;
import java.util.UUID;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.eventhandling.GenericDomainEventMessage;
import org.junit.Assert;
import org.junit.Test;


public class SourceIdParameterResolverFactoryTest {
    private SourceIdParameterResolverFactory testSubject;

    private Method sourceIdMethod;

    private Method nonAnnotatedMethod;

    private Method integerMethod;

    @SuppressWarnings("unchecked")
    @Test
    public void testResolvesToAggregateIdentifierWhenAnnotatedForDomainEventMessage() {
        ParameterResolver resolver = testSubject.createInstance(sourceIdMethod, sourceIdMethod.getParameters(), 0);
        final GenericDomainEventMessage<Object> eventMessage = new GenericDomainEventMessage("test", UUID.randomUUID().toString(), 0L, null);
        Assert.assertTrue(resolver.matches(eventMessage));
        Assert.assertEquals(eventMessage.getAggregateIdentifier(), resolver.resolveParameterValue(eventMessage));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDoesNotMatchWhenAnnotatedForCommandMessage() {
        ParameterResolver resolver = testSubject.createInstance(sourceIdMethod, sourceIdMethod.getParameters(), 0);
        CommandMessage<Object> commandMessage = GenericCommandMessage.asCommandMessage("test");
        Assert.assertFalse(resolver.matches(commandMessage));
    }

    @Test
    public void testIgnoredWhenNotAnnotated() {
        ParameterResolver resolver = testSubject.createInstance(nonAnnotatedMethod, nonAnnotatedMethod.getParameters(), 0);
        Assert.assertNull(resolver);
    }

    @Test
    public void testIgnoredWhenWrongType() {
        ParameterResolver resolver = testSubject.createInstance(integerMethod, integerMethod.getParameters(), 0);
        Assert.assertNull(resolver);
    }
}

