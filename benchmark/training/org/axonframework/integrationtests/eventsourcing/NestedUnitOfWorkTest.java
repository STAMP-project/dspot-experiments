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
package org.axonframework.integrationtests.eventsourcing;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.axonframework.modelling.command.EntityId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.junit.Assert;
import org.junit.Test;


public class NestedUnitOfWorkTest {
    @Test
    public void testStagedEventsLoadInCorrectOrder() {
        Configuration config = DefaultConfigurer.defaultConfiguration().configureAggregate(NestedUnitOfWorkTest.TestAggregate.class).registerCommandHandler(( x) -> new org.axonframework.integrationtests.eventsourcing.Handler()).configureEmbeddedEventStore(( x) -> new InMemoryEventStorageEngine()).registerComponent(List.class, ( c) -> new CopyOnWriteArrayList()).buildConfiguration();
        config.start();
        CommandGateway gw = config.commandGateway();
        gw.sendAndWait(new NestedUnitOfWorkTest.Create("1"));
        gw.sendAndWait(new NestedUnitOfWorkTest.Test1());
        gw.sendAndWait(new NestedUnitOfWorkTest.ShowItems("1", "from-eventstore"));
        config.shutdown();
        Assert.assertEquals(Arrays.asList("pre-rollback-first", "pre-rollback-second", "post-rollback-first", "post-rollback-second", "from-eventstore-first", "from-eventstore-second"), config.getComponent(List.class));
    }

    static class TestAggregate {
        @EntityId
        String id;

        List<String> items = new ArrayList<>();

        @CommandHandler
        public TestAggregate(NestedUnitOfWorkTest.Create cmd) {
            apply(cmd);
        }

        private TestAggregate() {
        }

        @EventSourcingHandler
        public void on(NestedUnitOfWorkTest.Create evt) {
            id = evt.id;
        }

        @CommandHandler
        public void handle(NestedUnitOfWorkTest.Add cmd, CommandGateway gw) {
            apply(cmd);
        }

        @EventSourcingHandler
        public void on(NestedUnitOfWorkTest.Add evt) {
            items.add(evt.item);
        }

        @CommandHandler
        public void handle(NestedUnitOfWorkTest.ShowItems cmd, List<String> order) {
            items.forEach(( i) -> order.add((((cmd.message) + "-") + i)));
        }
    }

    static class Create {
        @TargetAggregateIdentifier
        String id;

        public Create(String id) {
            this.id = id;
        }
    }

    static class Add {
        @TargetAggregateIdentifier
        String id;

        String item;

        public Add(String id, String item) {
            this.id = id;
            this.item = item;
        }
    }

    static class ShowItems {
        @TargetAggregateIdentifier
        String id;

        String message;

        public ShowItems(String id, String message) {
            this.id = id;
            this.message = message;
        }
    }

    static class Test1 {}

    static class Test2 {}

    static class Oops {}

    static class Handler {
        @CommandHandler
        public void handle(NestedUnitOfWorkTest.Test1 cmd, CommandGateway gw) {
            gw.sendAndWait(new NestedUnitOfWorkTest.Add("1", "first"));
            gw.sendAndWait(new NestedUnitOfWorkTest.Test2());
        }

        @CommandHandler
        public void handle(NestedUnitOfWorkTest.Test2 cmd, CommandGateway gw) {
            gw.sendAndWait(new NestedUnitOfWorkTest.Add("1", "second"));
            gw.sendAndWait(new NestedUnitOfWorkTest.ShowItems("1", "pre-rollback"));
            try {
                gw.sendAndWait(new NestedUnitOfWorkTest.Oops());
                Assert.fail("Command should have failed");
            } catch (RuntimeException e) {
                // expected
            }
            gw.sendAndWait(new NestedUnitOfWorkTest.ShowItems("1", "post-rollback"));
        }

        @CommandHandler
        public void handle(NestedUnitOfWorkTest.Oops cmd, CommandGateway gw) {
            gw.sendAndWait(new NestedUnitOfWorkTest.Add("1", "third"));
            throw new RuntimeException();
        }
    }
}

