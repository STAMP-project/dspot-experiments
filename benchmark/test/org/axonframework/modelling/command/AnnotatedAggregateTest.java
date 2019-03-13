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
package org.axonframework.modelling.command;


import java.util.concurrent.Callable;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.modelling.command.inspection.AnnotatedAggregate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class AnnotatedAggregateTest {
    private final String ID = "id";

    private Repository<AnnotatedAggregateTest.AggregateRoot> repository;

    private EventBus eventBus;

    @Test
    public void testApplyingEventInHandlerPublishesInRightOrder() {
        AnnotatedAggregateTest.Command command = new AnnotatedAggregateTest.Command(ID);
        DefaultUnitOfWork<CommandMessage<Object>> uow = DefaultUnitOfWork.startAndGet(asCommandMessage(command));
        Aggregate<AnnotatedAggregateTest.AggregateRoot> aggregate = uow.executeWithResult(() -> repository.newInstance(() -> new org.axonframework.modelling.command.AggregateRoot(command))).getPayload();
        Assert.assertNotNull(aggregate);
        InOrder inOrder = Mockito.inOrder(eventBus);
        inOrder.verify(eventBus).publish(ArgumentMatchers.argThat(((ArgumentMatcher<EventMessage<?>>) (( x) -> AnnotatedAggregateTest.Event_1.class.equals(x.getPayloadType())))));
        inOrder.verify(eventBus).publish(ArgumentMatchers.argThat(((ArgumentMatcher<EventMessage<?>>) (( x) -> AnnotatedAggregateTest.Event_2.class.equals(x.getPayloadType())))));
    }

    private static class Command {
        private final String id;

        private Command(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private static class Event_1 {
        private final String id;

        private Event_1(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private static class Event_2 {
        private final String id;

        public Event_2(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class AggregateRoot {
        @AggregateIdentifier
        private String id;

        public AggregateRoot() {
        }

        @CommandHandler
        public AggregateRoot(AnnotatedAggregateTest.Command command) {
            AggregateLifecycle.apply(new AnnotatedAggregateTest.Event_1(command.getId()));
        }

        @EventHandler
        public void on(AnnotatedAggregateTest.Event_1 event) {
            this.id = event.getId();
            AggregateLifecycle.apply(new AnnotatedAggregateTest.Event_2(event.getId()));
        }

        @EventHandler
        public void on(AnnotatedAggregateTest.Event_2 event) {
        }
    }

    private static class StubRepository extends AbstractRepository<AnnotatedAggregateTest.AggregateRoot, Aggregate<AnnotatedAggregateTest.AggregateRoot>> {
        private final EventBus eventBus;

        private StubRepository(AnnotatedAggregateTest.StubRepository.Builder builder) {
            super(builder);
            this.eventBus = builder.eventBus;
        }

        public static AnnotatedAggregateTest.StubRepository.Builder builder() {
            return new AnnotatedAggregateTest.StubRepository.Builder();
        }

        @Override
        protected Aggregate<AnnotatedAggregateTest.AggregateRoot> doCreateNew(Callable<AnnotatedAggregateTest.AggregateRoot> factoryMethod) throws Exception {
            return AnnotatedAggregate.initialize(factoryMethod, aggregateModel(), eventBus);
        }

        @Override
        protected void doSave(Aggregate<AnnotatedAggregateTest.AggregateRoot> aggregate) {
        }

        @Override
        protected Aggregate<AnnotatedAggregateTest.AggregateRoot> doLoad(String aggregateIdentifier, Long expectedVersion) {
            return null;
        }

        @Override
        protected void doDelete(Aggregate<AnnotatedAggregateTest.AggregateRoot> aggregate) {
        }

        private static class Builder extends AbstractRepository.Builder<AnnotatedAggregateTest.AggregateRoot> {
            private EventBus eventBus;

            private Builder() {
                super(AnnotatedAggregateTest.AggregateRoot.class);
            }

            public AnnotatedAggregateTest.StubRepository.Builder eventBus(EventBus eventBus) {
                this.eventBus = eventBus;
                return this;
            }

            public AnnotatedAggregateTest.StubRepository build() {
                return new AnnotatedAggregateTest.StubRepository(this);
            }
        }
    }
}

