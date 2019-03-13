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


public class TrackingTokenParameterResolverFactoryTest {
    private Method method;

    private TrackingTokenParameterResolverFactory testSubject;

    @Test
    public void createInstance() {
        Assert.assertNull(testSubject.createInstance(method, method.getParameters(), 0));
        ParameterResolver<?> resolver = testSubject.createInstance(method, method.getParameters(), 1);
        Assert.assertNotNull(resolver);
        GenericEventMessage<String> message = new GenericEventMessage("test");
        Assert.assertFalse(resolver.matches(message));
        GlobalSequenceTrackingToken trackingToken = new GlobalSequenceTrackingToken(1L);
        GenericTrackedEventMessage<String> trackedEventMessage = new GenericTrackedEventMessage(trackingToken, message);
        Assert.assertTrue(resolver.matches(trackedEventMessage));
        Assert.assertSame(trackingToken, resolver.resolveParameterValue(trackedEventMessage));
    }
}

