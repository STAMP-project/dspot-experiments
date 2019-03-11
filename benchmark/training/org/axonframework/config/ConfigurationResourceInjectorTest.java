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


import javax.inject.Inject;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.junit.Assert;
import org.junit.Test;


public class ConfigurationResourceInjectorTest {
    private Configuration configuration;

    private ConfigurationResourceInjector testSubject;

    @Test
    public void testInjectorHasResource() {
        ConfigurationResourceInjectorTest.Saga saga = new ConfigurationResourceInjectorTest.Saga();
        testSubject.injectResources(saga);
        Assert.assertSame(configuration.commandBus(), saga.commandBus);
        Assert.assertSame(configuration.commandGateway(), saga.commandGateway);
        Assert.assertSame(configuration.eventStore(), saga.eventStore);
        Assert.assertNull(saga.inexistent);
    }

    public static class Saga {
        @Inject
        private CommandBus commandBus;

        @Inject
        private String inexistent;

        private CommandGateway commandGateway;

        private EventStore eventStore;

        @Inject
        public void setCommandGateway(CommandGateway commandGateway) {
            this.commandGateway = commandGateway;
        }

        @Inject
        public void setEventStore(EventStore eventStore) {
            this.eventStore = eventStore;
        }
    }
}

