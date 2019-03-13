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
package org.axonframework.disruptor.commandhandling;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.common.IdentifierFactory;
import org.axonframework.disruptor.commandhandling.utils.MockException;
import org.axonframework.disruptor.commandhandling.utils.SomethingDoneEvent;
import org.axonframework.eventhandling.AbstractEventBus;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.TrackingEventStream;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.modelling.command.Aggregate;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.Repository;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static AbstractEventBus.Builder.<init>;


/**
 *
 *
 * @author Allard Buijze
 */
public class DisruptorCommandBusTest_MultiThreaded {
    private static final int COMMAND_COUNT = 100;

    private static final int AGGREGATE_COUNT = 10;

    private DisruptorCommandBusTest_MultiThreaded.InMemoryEventStore inMemoryEventStore;

    private DisruptorCommandBus testSubject;

    private Repository<DisruptorCommandBusTest_MultiThreaded.StubAggregate> spiedRepository;

    @SuppressWarnings("unchecked")
    @Test
    public void testDispatchLargeNumberCommandForDifferentAggregates() throws Exception {
        final Map<Object, Object> garbageCollectionPrevention = new ConcurrentHashMap<>();
        Mockito.doAnswer(trackCreateAndLoad(garbageCollectionPrevention)).when(spiedRepository).newInstance(ArgumentMatchers.any());
        Mockito.doAnswer(trackCreateAndLoad(garbageCollectionPrevention)).when(spiedRepository).load(ArgumentMatchers.isA(String.class));
        List<String> aggregateIdentifiers = IntStream.range(0, DisruptorCommandBusTest_MultiThreaded.AGGREGATE_COUNT).mapToObj(( i) -> IdentifierFactory.getInstance().generateIdentifier()).collect(Collectors.toList());
        CommandCallback mockCallback = Mockito.mock(CommandCallback.class);
        Stream<CommandMessage<Object>> commands = generateCommands(aggregateIdentifiers);
        commands.forEach(( c) -> testSubject.dispatch(c, mockCallback));
        testSubject.stop();
        // only the commands executed after the failed ones will cause a readEvents() to occur
        Assert.assertEquals(10, inMemoryEventStore.loadCounter.get());
        Assert.assertEquals(20, garbageCollectionPrevention.size());
        Assert.assertEquals((((DisruptorCommandBusTest_MultiThreaded.COMMAND_COUNT) * (DisruptorCommandBusTest_MultiThreaded.AGGREGATE_COUNT)) + (2 * (DisruptorCommandBusTest_MultiThreaded.AGGREGATE_COUNT))), inMemoryEventStore.storedEventCounter.get());
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(mockCallback, Mockito.times(1010)).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        Assert.assertEquals(10, commandResultMessageCaptor.getAllValues().stream().filter(ResultMessage::isExceptional).map(ResultMessage::exceptionResult).filter(( e) -> e instanceof MockException).count());
    }

    private static class StubAggregate {
        @AggregateIdentifier
        private String identifier;

        private StubAggregate(String identifier) {
            this.identifier = identifier;
            AggregateLifecycle.apply(new SomethingDoneEvent());
        }

        @SuppressWarnings("UnusedDeclaration")
        public StubAggregate() {
        }

        public String getIdentifier() {
            return identifier;
        }

        public void doSomething() {
            AggregateLifecycle.apply(new SomethingDoneEvent());
        }

        public void createFailingEvent() {
            AggregateLifecycle.apply(new DisruptorCommandBusTest_MultiThreaded.FailingEvent());
        }

        @EventSourcingHandler
        protected void handle(EventMessage event) {
            identifier = getAggregateIdentifier();
        }
    }

    private static class InMemoryEventStore extends AbstractEventBus implements EventStore {
        private final Map<String, DomainEventMessage> storedEvents = new ConcurrentHashMap<>();

        private final AtomicInteger storedEventCounter = new AtomicInteger();

        private final AtomicInteger loadCounter = new AtomicInteger();

        private InMemoryEventStore(DisruptorCommandBusTest_MultiThreaded.InMemoryEventStore.Builder builder) {
            super(builder);
        }

        private static DisruptorCommandBusTest_MultiThreaded.InMemoryEventStore.Builder builder() {
            return new DisruptorCommandBusTest_MultiThreaded.InMemoryEventStore.Builder();
        }

        @Override
        protected void commit(List<? extends EventMessage<?>> events) {
            if ((events == null) || (events.isEmpty())) {
                return;
            }
            String key = getAggregateIdentifier();
            DomainEventMessage<?> lastEvent = null;
            for (EventMessage<?> event : events) {
                storedEventCounter.incrementAndGet();
                lastEvent = ((DomainEventMessage<?>) (event));
                if (DisruptorCommandBusTest_MultiThreaded.FailingEvent.class.isAssignableFrom(lastEvent.getPayloadType())) {
                    throw new MockException("This is a failing event. EventStore refuses to store that");
                }
            }
            storedEvents.put(key, lastEvent);
        }

        @Override
        public DomainEventStream readEvents(String identifier) {
            loadCounter.incrementAndGet();
            DomainEventMessage<?> message = storedEvents.get(identifier);
            return message == null ? DomainEventStream.of() : DomainEventStream.of(message);
        }

        @Override
        public void storeSnapshot(DomainEventMessage<?> snapshot) {
        }

        @Override
        public TrackingEventStream openStream(TrackingToken trackingToken) {
            throw new UnsupportedOperationException();
        }

        private static class Builder extends AbstractEventBus.Builder {
            private DisruptorCommandBusTest_MultiThreaded.InMemoryEventStore build() {
                return new DisruptorCommandBusTest_MultiThreaded.InMemoryEventStore(this);
            }
        }
    }

    private static class StubCommand {
        @TargetAggregateIdentifier
        private Object aggregateIdentifier;

        private StubCommand(Object aggregateIdentifier) {
            this.aggregateIdentifier = aggregateIdentifier;
        }

        private Object getAggregateIdentifier() {
            return aggregateIdentifier;
        }
    }

    private static class ErrorCommand extends DisruptorCommandBusTest_MultiThreaded.StubCommand {
        private ErrorCommand(Object aggregateIdentifier) {
            super(aggregateIdentifier);
        }
    }

    private static class ExceptionCommand extends DisruptorCommandBusTest_MultiThreaded.StubCommand {
        private final Exception exception;

        public ExceptionCommand(Object aggregateIdentifier, Exception exception) {
            super(aggregateIdentifier);
            this.exception = exception;
        }

        public Exception getException() {
            return exception;
        }
    }

    private static class CreateCommand extends DisruptorCommandBusTest_MultiThreaded.StubCommand {
        private CreateCommand(Object aggregateIdentifier) {
            super(aggregateIdentifier);
        }
    }

    private static class StubHandler implements MessageHandler<CommandMessage<?>> {
        private Repository<DisruptorCommandBusTest_MultiThreaded.StubAggregate> repository;

        private StubHandler() {
        }

        @Override
        public Object handle(CommandMessage<?> command) throws Exception {
            DisruptorCommandBusTest_MultiThreaded.StubCommand payload = ((DisruptorCommandBusTest_MultiThreaded.StubCommand) (command.getPayload()));
            if (DisruptorCommandBusTest_MultiThreaded.ExceptionCommand.class.isAssignableFrom(command.getPayloadType())) {
                throw ((DisruptorCommandBusTest_MultiThreaded.ExceptionCommand) (command.getPayload())).getException();
            } else
                if (DisruptorCommandBusTest_MultiThreaded.CreateCommand.class.isAssignableFrom(command.getPayloadType())) {
                    Aggregate<DisruptorCommandBusTest_MultiThreaded.StubAggregate> aggregate = repository.newInstance(() -> new org.axonframework.disruptor.commandhandling.StubAggregate(payload.getAggregateIdentifier().toString()));
                    aggregate.execute(DisruptorCommandBusTest_MultiThreaded.StubAggregate::doSomething);
                } else {
                    Aggregate<DisruptorCommandBusTest_MultiThreaded.StubAggregate> aggregate = repository.load(payload.getAggregateIdentifier().toString());
                    if (DisruptorCommandBusTest_MultiThreaded.ErrorCommand.class.isAssignableFrom(command.getPayloadType())) {
                        aggregate.execute(DisruptorCommandBusTest_MultiThreaded.StubAggregate::createFailingEvent);
                    } else {
                        aggregate.execute(DisruptorCommandBusTest_MultiThreaded.StubAggregate::doSomething);
                    }
                }

            return Void.TYPE;
        }

        public void setRepository(Repository<DisruptorCommandBusTest_MultiThreaded.StubAggregate> repository) {
            this.repository = repository;
        }
    }

    private static class FailingEvent {}
}

