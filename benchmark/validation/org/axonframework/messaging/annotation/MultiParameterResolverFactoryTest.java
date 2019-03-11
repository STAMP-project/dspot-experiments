/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.messaging.annotation;


import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import org.axonframework.common.Priority;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.messaging.Message;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 * @author Nakul Mishra
 */
public class MultiParameterResolverFactoryTest {
    private ParameterResolverFactory mockFactory1;

    private ParameterResolverFactory mockFactory2;

    private ParameterResolver mockResolver1;

    private ParameterResolver mockResolver2;

    private MultiParameterResolverFactory testSubject;

    @Test
    public void testResolversQueriedInOrderProvided() throws Exception {
        Method equals = getClass().getMethod("equals", Object.class);
        ParameterResolver factory = testSubject.createInstance(equals, equals.getParameters(), 0);
        Assert.assertFalse(factory.matches(null));
        InOrder inOrder = Mockito.inOrder(mockFactory1, mockFactory2, mockResolver1, mockResolver2);
        inOrder.verify(mockFactory1).createInstance(ArgumentMatchers.any(Executable.class), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        inOrder.verify(mockResolver1).matches(ArgumentMatchers.any());
        Mockito.verify(mockFactory2, Mockito.never()).createInstance(ArgumentMatchers.any(Executable.class), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.verify(mockResolver2, Mockito.never()).matches(ArgumentMatchers.any(Message.class));
    }

    @Test
    public void testFirstMatchingResolverMayReturnValue() throws Exception {
        Method equals = getClass().getMethod("equals", Object.class);
        final EventMessage<Object> message = GenericEventMessage.asEventMessage("test");
        Mockito.when(mockFactory1.createInstance(ArgumentMatchers.any(Executable.class), ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenReturn(null);
        Mockito.when(mockResolver2.matches(message)).thenReturn(true);
        Mockito.when(mockResolver2.resolveParameterValue(message)).thenReturn("Resolved");
        ParameterResolver factory = testSubject.createInstance(equals, equals.getParameters(), 0);
        Assert.assertTrue(factory.matches(message));
        Assert.assertEquals("Resolved", factory.resolveParameterValue(message));
        Mockito.verify(mockResolver1, Mockito.never()).resolveParameterValue(ArgumentMatchers.any(Message.class));
    }

    @Test
    public void testNestedParameterResolversAreOrdered() {
        final MultiParameterResolverFactoryTest.LowPrioParameterResolverFactory lowPrio = new MultiParameterResolverFactoryTest.LowPrioParameterResolverFactory();
        final MultiParameterResolverFactoryTest.HighPrioParameterResolverFactory highPrio = new MultiParameterResolverFactoryTest.HighPrioParameterResolverFactory();
        testSubject = MultiParameterResolverFactory.ordered(mockFactory1, new MultiParameterResolverFactory(lowPrio, mockFactory2), new MultiParameterResolverFactory(highPrio));
        Assert.assertEquals(Arrays.asList(highPrio, mockFactory1, mockFactory2, lowPrio), testSubject.getDelegates());
    }

    @Priority(Priority.LOW)
    private static class LowPrioParameterResolverFactory extends MultiParameterResolverFactoryTest.AbstractNoopParameterResolverFactory {}

    @Priority(Priority.HIGH)
    private static class HighPrioParameterResolverFactory extends MultiParameterResolverFactoryTest.AbstractNoopParameterResolverFactory {}

    private static class AbstractNoopParameterResolverFactory implements ParameterResolverFactory {
        @Override
        public ParameterResolver createInstance(Executable executable, Parameter[] parameters, int parameterIndex) {
            return null;
        }
    }
}

