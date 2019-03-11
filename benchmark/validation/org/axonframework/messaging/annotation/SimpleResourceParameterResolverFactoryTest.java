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
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.junit.Assert;
import org.junit.Test;


public class SimpleResourceParameterResolverFactoryTest {
    private static final String TEST_RESOURCE = "testResource";

    private static final Long TEST_RESOURCE2 = 42L;

    private SimpleResourceParameterResolverFactory testSubject;

    private Method messageHandlingMethodWithResourceParameter;

    private Method messageHandlingMethodWithResource2Parameter;

    private Method messageHandlingMethodWithoutResourceParameter;

    private Method messageHandlingMethodWithResourceParameterOfDifferentType;

    @SuppressWarnings("unchecked")
    @Test
    public void testResolvesToResourceWhenMessageHandlingMethodHasResourceParameter() {
        ParameterResolver resolver = testSubject.createInstance(messageHandlingMethodWithResourceParameter, messageHandlingMethodWithResourceParameter.getParameters(), 1);
        final EventMessage<Object> eventMessage = GenericEventMessage.asEventMessage("test");
        Assert.assertTrue(resolver.matches(eventMessage));
        Assert.assertEquals(SimpleResourceParameterResolverFactoryTest.TEST_RESOURCE, resolver.resolveParameterValue(eventMessage));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testResolvesToResourceWhenMessageHandlingMethodHasAnotherResourceParameter() {
        ParameterResolver resolver = testSubject.createInstance(messageHandlingMethodWithResource2Parameter, messageHandlingMethodWithResource2Parameter.getParameters(), 1);
        final EventMessage<Object> eventMessage = GenericEventMessage.asEventMessage("test");
        Assert.assertTrue(resolver.matches(eventMessage));
        Assert.assertEquals(SimpleResourceParameterResolverFactoryTest.TEST_RESOURCE2, resolver.resolveParameterValue(eventMessage));
    }

    @Test
    public void testIgnoredWhenMessageHandlingMethodHasNoResourceParameter() {
        ParameterResolver resolver = testSubject.createInstance(messageHandlingMethodWithoutResourceParameter, messageHandlingMethodWithoutResourceParameter.getParameters(), 0);
        Assert.assertNull(resolver);
    }

    @Test
    public void testIgnoredWhenMessageHandlingMethodHasResourceParameterOfDifferentType() {
        ParameterResolver resolver = testSubject.createInstance(messageHandlingMethodWithResourceParameterOfDifferentType, messageHandlingMethodWithResourceParameterOfDifferentType.getParameters(), 1);
        Assert.assertNull(resolver);
    }
}

