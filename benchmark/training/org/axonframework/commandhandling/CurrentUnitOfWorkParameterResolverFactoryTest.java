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
package org.axonframework.commandhandling;


import java.lang.reflect.Method;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class CurrentUnitOfWorkParameterResolverFactoryTest {
    private CurrentUnitOfWorkParameterResolverFactory testSubject;

    private Method method;

    @Test
    public void testCreateInstance() throws Exception {
        Method someMethod = getClass().getMethod("someMethod", UnitOfWork.class);
        Assert.assertNull(testSubject.createInstance(method, method.getParameters(), 0));
        Assert.assertSame(testSubject, testSubject.createInstance(someMethod, someMethod.getParameters(), 0));
    }

    @Test
    public void testResolveParameterValue() {
        DefaultUnitOfWork.startAndGet(null);
        try {
            Assert.assertSame(CurrentUnitOfWork.get(), testSubject.resolveParameterValue(Mockito.mock(GenericCommandMessage.class)));
        } finally {
            CurrentUnitOfWork.get().rollback();
        }
    }

    @Test
    public void testResolveParameterValueWithoutActiveUnitOfWork() {
        Assert.assertNull(testSubject.resolveParameterValue(Mockito.mock(GenericCommandMessage.class)));
    }

    @Test
    public void testMatches() {
        Assert.assertTrue(testSubject.matches(Mockito.mock(GenericCommandMessage.class)));
        DefaultUnitOfWork.startAndGet(null);
        try {
            Assert.assertTrue(testSubject.matches(Mockito.mock(Message.class)));
            Assert.assertTrue(testSubject.matches(Mockito.mock(EventMessage.class)));
            Assert.assertTrue(testSubject.matches(Mockito.mock(GenericEventMessage.class)));
            Assert.assertTrue(testSubject.matches(Mockito.mock(GenericCommandMessage.class)));
        } finally {
            CurrentUnitOfWork.get().rollback();
        }
    }
}

