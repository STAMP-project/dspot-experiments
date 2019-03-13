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
package org.axonframework.eventsourcing;


import java.util.Objects;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.AxonConfigurationException;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.Repository;
import org.axonframework.modelling.command.RepositoryProvider;
import org.axonframework.modelling.command.inspection.AggregateModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests spawning of new aggregate from command handling of different aggregate.
 *
 * @author Milan Savic
 */
@RunWith(MockitoJUnitRunner.class)
public class SpawningNewAggregateTest {
    @Spy
    private SimpleCommandBus commandBus;

    @Mock
    private Repository<SpawningNewAggregateTest.Aggregate1> aggregate1Repository;

    @Mock
    private Repository<SpawningNewAggregateTest.Aggregate2> aggregate2Repository;

    @Mock
    private RepositoryProvider repositoryProvider;

    @Mock
    private EventStore eventStore;

    private AggregateModel<SpawningNewAggregateTest.Aggregate1> aggregate1Model;

    @SuppressWarnings("unchecked")
    @Test
    public void testSpawningNewAggregate() throws Exception {
        initializeAggregate1Repository(repositoryProvider);
        commandBus.dispatch(asCommandMessage(new SpawningNewAggregateTest.CreateAggregate1Command("id", "aggregate2Id")));
        Mockito.verify(aggregate1Repository).newInstance(ArgumentMatchers.any());
        Mockito.verify(repositoryProvider).repositoryFor(SpawningNewAggregateTest.Aggregate2.class);
        Mockito.verify(aggregate2Repository).newInstance(ArgumentMatchers.any());
        ArgumentCaptor<EventMessage<?>> eventCaptor = ArgumentCaptor.forClass(EventMessage.class);
        Mockito.verify(eventStore, Mockito.times(2)).publish(eventCaptor.capture());
        Assert.assertEquals(new SpawningNewAggregateTest.Aggregate2CreatedEvent("aggregate2Id"), eventCaptor.getAllValues().get(0).getPayload());
        Assert.assertEquals(new SpawningNewAggregateTest.Aggregate1CreatedEvent("id"), eventCaptor.getAllValues().get(1).getPayload());
    }

    @Test
    public void testSpawningNewAggregateWhenThereIsNoRepositoryForIt() throws Exception {
        initializeAggregate1Repository(repositoryProvider);
        Mockito.when(repositoryProvider.repositoryFor(SpawningNewAggregateTest.Aggregate2.class)).thenReturn(null);
        commandBus.dispatch(asCommandMessage(new SpawningNewAggregateTest.CreateAggregate1Command("id", "aggregate2Id")), ( commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                Throwable cause = commandResultMessage.exceptionResult();
                assertTrue((cause instanceof IllegalStateException));
                assertEquals("There is no configured repository for org.axonframework.eventsourcing.SpawningNewAggregateTest$Aggregate2", cause.getMessage());
            } else {
                fail("Expected exception");
            }
        });
    }

    @Test
    public void testSpawningNewAggregateWhenThereIsNoRepositoryProviderProvided() throws Exception {
        initializeAggregate1Repository(null);
        commandBus.dispatch(asCommandMessage(new SpawningNewAggregateTest.CreateAggregate1Command("id", "aggregate2Id")), ( commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                Throwable cause = commandResultMessage.exceptionResult();
                assertTrue((cause instanceof AxonConfigurationException));
                assertEquals("Since repository provider is not provided, we cannot spawn a new aggregate for org.axonframework.eventsourcing.SpawningNewAggregateTest$Aggregate2", cause.getMessage());
            } else {
                fail("Expected exception");
            }
        });
    }

    private static class CreateAggregate1Command {
        private final String id;

        private final String aggregate2Id;

        private CreateAggregate1Command(String id, String aggregate2Id) {
            this.id = id;
            this.aggregate2Id = aggregate2Id;
        }

        public String getId() {
            return id;
        }

        public String getAggregate2Id() {
            return aggregate2Id;
        }
    }

    private static class Aggregate1CreatedEvent {
        private final String id;

        private Aggregate1CreatedEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SpawningNewAggregateTest.Aggregate1CreatedEvent that = ((SpawningNewAggregateTest.Aggregate1CreatedEvent) (o));
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private static class Aggregate2CreatedEvent {
        private final String id;

        private Aggregate2CreatedEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SpawningNewAggregateTest.Aggregate2CreatedEvent that = ((SpawningNewAggregateTest.Aggregate2CreatedEvent) (o));
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @SuppressWarnings("unused")
    private static class Aggregate1 {
        @AggregateIdentifier
        private String id;

        public Aggregate1() {
        }

        @CommandHandler
        public Aggregate1(SpawningNewAggregateTest.CreateAggregate1Command command) throws Exception {
            apply(new SpawningNewAggregateTest.Aggregate1CreatedEvent(command.getId()));
            createNew(SpawningNewAggregateTest.Aggregate2.class, () -> new org.axonframework.eventsourcing.Aggregate2(command.getAggregate2Id()));
        }

        @EventSourcingHandler
        public void on(SpawningNewAggregateTest.Aggregate1CreatedEvent event) {
            this.id = event.getId();
        }
    }

    @SuppressWarnings("unused")
    private static class Aggregate2 {
        @AggregateIdentifier
        private String id;

        private String state;

        public Aggregate2() {
        }

        public Aggregate2(String id) {
            apply(new SpawningNewAggregateTest.Aggregate2CreatedEvent(id));
        }

        @EventSourcingHandler
        public void on(SpawningNewAggregateTest.Aggregate2CreatedEvent event) {
            this.id = event.getId();
        }
    }
}

