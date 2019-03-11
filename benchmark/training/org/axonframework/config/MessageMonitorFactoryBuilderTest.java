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
package org.axonframework.config;


import java.util.function.BiFunction;
import org.axonframework.messaging.Message;
import org.axonframework.monitoring.MessageMonitor;
import org.junit.Assert;
import org.junit.Test;


public class MessageMonitorFactoryBuilderTest {
    private MessageMonitor<Message<?>> defaultMonitor = ( message) -> null;

    private static class A {}

    private static class B {}

    private static class C {}

    private static class D {}

    @Test
    public void validateRulesWithoutTypeHierarchy() {
        MessageMonitor<Message<?>> aMonitor = ( message) -> null;
        MessageMonitor<Message<?>> bMonitor = ( message) -> null;
        MessageMonitor<Message<?>> cMonitor = ( message) -> null;
        BiFunction<Class<?>, String, MessageMonitor<Message<?>>> factory = new MessageMonitorFactoryBuilder().add(( conf, type, name) -> defaultMonitor).add(MessageMonitorFactoryBuilderTest.A.class, ( conf, type, name) -> aMonitor).add(MessageMonitorFactoryBuilderTest.B.class, ( conf, type, name) -> bMonitor).add(MessageMonitorFactoryBuilderTest.C.class, "c", ( conf, type, name) -> cMonitor).build(null);
        // For an non-configured type, expect the default monitor
        Assert.assertEquals(defaultMonitor, factory.apply(MessageMonitorFactoryBuilderTest.D.class, "any"));
        // For a configured type, expect the configured monitor
        Assert.assertEquals(aMonitor, factory.apply(MessageMonitorFactoryBuilderTest.A.class, "any"));
        Assert.assertEquals(bMonitor, factory.apply(MessageMonitorFactoryBuilderTest.B.class, "any"));
        // For a configured name and type, expect the configured monitor only if both match
        // If no match is found, fall back to matching on type only
        Assert.assertEquals(cMonitor, factory.apply(MessageMonitorFactoryBuilderTest.C.class, "c"));
        Assert.assertEquals(defaultMonitor, factory.apply(MessageMonitorFactoryBuilderTest.C.class, "any"));
        Assert.assertEquals(defaultMonitor, factory.apply(MessageMonitorFactoryBuilderTest.D.class, "c"));
        Assert.assertEquals(aMonitor, factory.apply(MessageMonitorFactoryBuilderTest.A.class, "c"));
    }

    private interface I {}

    private static class K {}

    private static class L extends MessageMonitorFactoryBuilderTest.K {}

    private static class M extends MessageMonitorFactoryBuilderTest.L {}

    private static class N extends MessageMonitorFactoryBuilderTest.M implements MessageMonitorFactoryBuilderTest.I {}

    MessageMonitor<Message<?>> kMonitor = ( message) -> null;

    MessageMonitor<Message<?>> mMonitor = ( message) -> null;

    MessageMonitor<Message<?>> iMonitor = ( message) -> null;

    @Test
    public void validateTypeHierarchy() {
        BiFunction<Class<?>, String, MessageMonitor<Message<?>>> factory = new MessageMonitorFactoryBuilder().add(( conf, type, name) -> defaultMonitor).add(MessageMonitorFactoryBuilderTest.K.class, ( conf, type, name) -> kMonitor).add(MessageMonitorFactoryBuilderTest.M.class, ( conf, type, name) -> mMonitor).add(MessageMonitorFactoryBuilderTest.I.class, ( conf, type, name) -> iMonitor).build(null);
        validateTypeHierarchyResults(factory);
        // Repeat the same test, but with the 'add' statements reversed
        factory = new MessageMonitorFactoryBuilder().add(( conf, type, name) -> defaultMonitor).add(MessageMonitorFactoryBuilderTest.I.class, ( conf, type, name) -> iMonitor).add(MessageMonitorFactoryBuilderTest.M.class, ( conf, type, name) -> mMonitor).add(MessageMonitorFactoryBuilderTest.K.class, ( conf, type, name) -> kMonitor).build(null);
        validateTypeHierarchyResults(factory);
    }

    @Test
    public void validateMultipleClassesForSameName() {
        BiFunction<Class<?>, String, MessageMonitor<Message<?>>> factory = new MessageMonitorFactoryBuilder().add(( conf, type, name) -> defaultMonitor).add(MessageMonitorFactoryBuilderTest.K.class, "name", ( conf, type, name) -> kMonitor).add(MessageMonitorFactoryBuilderTest.M.class, "name", ( conf, type, name) -> mMonitor).build(null);
        // For a configured type and name combination, expect the configured monitor
        Assert.assertEquals(kMonitor, factory.apply(MessageMonitorFactoryBuilderTest.K.class, "name"));
        Assert.assertEquals(mMonitor, factory.apply(MessageMonitorFactoryBuilderTest.M.class, "name"));
        // For a non-configured type, expect the closest parent in the class hierarchy, or the normal default
        Assert.assertEquals(defaultMonitor, factory.apply(Object.class, "name"));
        Assert.assertEquals(kMonitor, factory.apply(MessageMonitorFactoryBuilderTest.L.class, "name"));
        Assert.assertEquals(mMonitor, factory.apply(MessageMonitorFactoryBuilderTest.N.class, "name"));
    }
}

