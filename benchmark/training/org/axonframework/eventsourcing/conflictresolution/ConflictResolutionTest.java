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
package org.axonframework.eventsourcing.conflictresolution;


import NoConflictResolver.INSTANCE;
import java.lang.reflect.Method;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.junit.Assert;
import org.junit.Test;


public class ConflictResolutionTest {
    private Method method;

    private ConflictResolution subject;

    private ConflictResolver conflictResolver;

    private CommandMessage<String> commandMessage = new org.axonframework.commandhandling.GenericCommandMessage("test");

    @Test
    public void testFactoryMethod() {
        Assert.assertNotNull(subject.createInstance(method, method.getParameters(), 1));
        Assert.assertNull(subject.createInstance(method, method.getParameters(), 0));
    }

    @Test
    public void testResolve() {
        ConflictResolution.initialize(conflictResolver);
        Assert.assertFalse(subject.matches(GenericEventMessage.asEventMessage("testEvent")));
        Assert.assertTrue(subject.matches(commandMessage));
        Assert.assertSame(conflictResolver, ConflictResolution.getConflictResolver());
        Assert.assertSame(conflictResolver, subject.resolveParameterValue(commandMessage));
    }

    @Test
    public void testResolveWithoutInitializationReturnsNoConflictsResolver() {
        Assert.assertTrue(subject.matches(commandMessage));
        Assert.assertSame(INSTANCE, subject.resolveParameterValue(commandMessage));
    }
}

