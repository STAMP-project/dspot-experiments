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
package org.axonframework.commandhandling.distributed.commandfilter;


import AcceptAll.INSTANCE;
import org.axonframework.commandhandling.CommandMessage;
import org.junit.Assert;
import org.junit.Test;


public class CommandFilterTest {
    @Test
    public void testAcceptAll() {
        CommandMessage<Object> testCommand = new org.axonframework.commandhandling.GenericCommandMessage(new Object());
        Assert.assertTrue(INSTANCE.matches(testCommand));
        Assert.assertFalse(INSTANCE.negate().matches(testCommand));
        Assert.assertTrue(INSTANCE.or(DenyAll.INSTANCE).matches(testCommand));
        Assert.assertFalse(INSTANCE.and(DenyAll.INSTANCE).matches(testCommand));
    }

    @Test
    public void testDenyAll() {
        CommandMessage<Object> testCommand = new org.axonframework.commandhandling.GenericCommandMessage(new Object());
        Assert.assertFalse(DenyAll.INSTANCE.matches(testCommand));
        Assert.assertTrue(DenyAll.INSTANCE.negate().matches(testCommand));
        Assert.assertTrue(DenyAll.INSTANCE.or(INSTANCE).matches(testCommand));
        Assert.assertFalse(DenyAll.INSTANCE.and(INSTANCE).matches(testCommand));
    }

    @Test
    public void testCommandNameFilter() {
        CommandMessage<Object> testCommand = new org.axonframework.commandhandling.GenericCommandMessage(new org.axonframework.messaging.GenericMessage(new Object()), "acceptable");
        CommandNameFilter filterAcceptable = new CommandNameFilter("acceptable");
        CommandNameFilter filterOther = new CommandNameFilter("other");
        Assert.assertTrue(filterAcceptable.matches(testCommand));
        Assert.assertFalse(filterAcceptable.negate().matches(testCommand));
        Assert.assertFalse(filterOther.matches(testCommand));
        Assert.assertTrue(filterOther.negate().matches(testCommand));
        Assert.assertTrue(filterOther.or(filterAcceptable).matches(testCommand));
        Assert.assertTrue(filterAcceptable.or(filterOther).matches(testCommand));
        Assert.assertFalse(filterOther.and(filterAcceptable).matches(testCommand));
        Assert.assertFalse(filterAcceptable.and(filterOther).matches(testCommand));
        Assert.assertFalse(filterOther.or(DenyAll.INSTANCE).matches(testCommand));
        Assert.assertTrue(filterOther.or(INSTANCE).matches(testCommand));
        Assert.assertFalse(filterOther.and(DenyAll.INSTANCE).matches(testCommand));
        Assert.assertFalse(filterOther.and(INSTANCE).matches(testCommand));
        Assert.assertTrue(filterAcceptable.or(DenyAll.INSTANCE).matches(testCommand));
        Assert.assertTrue(filterAcceptable.or(INSTANCE).matches(testCommand));
        Assert.assertFalse(filterAcceptable.and(DenyAll.INSTANCE).matches(testCommand));
        Assert.assertTrue(filterAcceptable.and(INSTANCE).matches(testCommand));
    }

    @Test
    public void testDenyCommandNameFilter() {
        CommandMessage<Object> testCommand = new org.axonframework.commandhandling.GenericCommandMessage(new org.axonframework.messaging.GenericMessage(new Object()), "acceptable");
        DenyCommandNameFilter filterAcceptable = new DenyCommandNameFilter("acceptable");
        DenyCommandNameFilter filterOther = new DenyCommandNameFilter("other");
        Assert.assertFalse(filterAcceptable.matches(testCommand));
        Assert.assertTrue(filterAcceptable.negate().matches(testCommand));
        Assert.assertTrue(filterOther.matches(testCommand));
        Assert.assertFalse(filterOther.negate().matches(testCommand));
        Assert.assertTrue(filterOther.or(filterAcceptable).matches(testCommand));
        Assert.assertTrue(filterAcceptable.or(filterOther).matches(testCommand));
        Assert.assertFalse(filterOther.and(filterAcceptable).matches(testCommand));
        Assert.assertFalse(filterAcceptable.and(filterOther).matches(testCommand));
        Assert.assertTrue(filterOther.or(DenyAll.INSTANCE).matches(testCommand));
        Assert.assertTrue(filterOther.or(INSTANCE).matches(testCommand));
        Assert.assertFalse(filterOther.and(DenyAll.INSTANCE).matches(testCommand));
        Assert.assertTrue(filterOther.and(INSTANCE).matches(testCommand));
        Assert.assertFalse(filterAcceptable.or(DenyAll.INSTANCE).matches(testCommand));
        Assert.assertTrue(filterAcceptable.or(INSTANCE).matches(testCommand));
        Assert.assertFalse(filterAcceptable.and(DenyAll.INSTANCE).matches(testCommand));
        Assert.assertFalse(filterAcceptable.and(INSTANCE).matches(testCommand));
    }
}

