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
package org.axonframework.eventhandling;


import java.lang.reflect.Method;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class TimestampParameterResolverFactoryTest {
    private TimestampParameterResolverFactory testSubject;

    private Timestamp annotation;

    private Method instantMethod;

    private Method temporalMethod;

    private Method stringMethod;

    private Method nonAnnotatedInstantMethod;

    @Test
    public void testResolvesToDateTimeWhenAnnotated() {
        ParameterResolver resolver = testSubject.createInstance(instantMethod, instantMethod.getParameters(), 0);
        final EventMessage<Object> message = GenericEventMessage.asEventMessage("test");
        Assert.assertTrue(resolver.matches(message));
        Assert.assertEquals(message.getTimestamp(), resolver.resolveParameterValue(message));
    }

    @Test
    public void testResolvesToReadableInstantWhenAnnotated() {
        ParameterResolver resolver = testSubject.createInstance(temporalMethod, temporalMethod.getParameters(), 0);
        final EventMessage<Object> message = GenericEventMessage.asEventMessage("test");
        Assert.assertTrue(resolver.matches(message));
        Assert.assertEquals(message.getTimestamp(), resolver.resolveParameterValue(message));
    }

    @Test
    public void testIgnoredWhenNotAnnotated() {
        ParameterResolver resolver = testSubject.createInstance(nonAnnotatedInstantMethod, nonAnnotatedInstantMethod.getParameters(), 0);
        Assert.assertNull(resolver);
    }

    @Test
    public void testIgnoredWhenWrongType() {
        ParameterResolver resolver = testSubject.createInstance(stringMethod, stringMethod.getParameters(), 0);
        Assert.assertNull(resolver);
    }
}

