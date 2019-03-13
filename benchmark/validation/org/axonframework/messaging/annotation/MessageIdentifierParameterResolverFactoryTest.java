/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.messaging.annotation;


import java.lang.reflect.Method;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.junit.Assert;
import org.junit.Test;


public class MessageIdentifierParameterResolverFactoryTest {
    private MessageIdentifierParameterResolverFactory testSubject;

    private Method messageIdentifierMethod;

    private Method nonAnnotatedMethod;

    private Method integerMethod;

    @SuppressWarnings("unchecked")
    @Test
    public void testResolvesToMessageIdentifierWhenAnnotatedForEventMessage() {
        ParameterResolver resolver = testSubject.createInstance(messageIdentifierMethod, messageIdentifierMethod.getParameters(), 0);
        final EventMessage<Object> eventMessage = GenericEventMessage.asEventMessage("test");
        Assert.assertTrue(resolver.matches(eventMessage));
        Assert.assertEquals(eventMessage.getIdentifier(), resolver.resolveParameterValue(eventMessage));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testResolvesToMessageIdentifierWhenAnnotatedForCommandMessage() {
        ParameterResolver resolver = testSubject.createInstance(messageIdentifierMethod, messageIdentifierMethod.getParameters(), 0);
        CommandMessage<Object> commandMessage = GenericCommandMessage.asCommandMessage("test");
        Assert.assertTrue(resolver.matches(commandMessage));
        Assert.assertEquals(commandMessage.getIdentifier(), resolver.resolveParameterValue(commandMessage));
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

