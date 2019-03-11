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
package org.axonframework.disruptor.commandhandling;


import java.io.Serializable;
import java.lang.reflect.Parameter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.common.caching.Cache;
import org.axonframework.deadline.DeadlineMessage;
import org.axonframework.deadline.GenericDeadlineMessage;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.messaging.annotation.ClasspathParameterResolverFactory;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateScopeDescriptor;
import org.axonframework.modelling.command.Repository;
import org.axonframework.modelling.command.inspection.AnnotatedAggregateMetaModelFactory;
import org.axonframework.modelling.saga.SagaScopeDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CommandHandlerInvokerTest {
    private CommandHandlerInvoker testSubject;

    private EventStore mockEventStore;

    private Cache mockCache;

    private CommandHandlingEntry commandHandlingEntry;

    private String aggregateIdentifier;

    private CommandMessage<?> mockCommandMessage;

    private MessageHandler<CommandMessage<?>> mockCommandHandler;

    private SnapshotTriggerDefinition snapshotTriggerDefinition;

    private SnapshotTrigger mockTrigger;

    private static AtomicInteger messageHandlingCounter;

    @Test
    public void usesProvidedParameterResolverFactoryToResolveParameters() {
        ParameterResolverFactory parameterResolverFactory = Mockito.spy(ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, parameterResolverFactory);
        // The StubAggregate has three 'handle()' functions, hence verifying this 3 times
        Mockito.verify(parameterResolverFactory, Mockito.times(3)).createInstance(ArgumentMatchers.argThat(( item) -> "handle".equals(item.getName())), ArgumentMatchers.isA(Parameter[].class), ArgumentMatchers.anyInt());
        Mockito.verifyNoMoreInteractions(parameterResolverFactory);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadFromRepositoryStoresLoadedAggregateInCache() throws Exception {
        final Repository<CommandHandlerInvokerTest.StubAggregate> repository = testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        Mockito.when(mockCommandHandler.handle(ArgumentMatchers.eq(mockCommandMessage))).thenAnswer(( invocationOnMock) -> repository.load(aggregateIdentifier));
        Mockito.when(mockEventStore.readEvents(ArgumentMatchers.any())).thenReturn(DomainEventStream.of(new org.axonframework.eventhandling.GenericDomainEventMessage("type", aggregateIdentifier, 0, aggregateIdentifier)));
        testSubject.onEvent(commandHandlingEntry, 0, true);
        Mockito.verify(mockCache).get(aggregateIdentifier);
        Mockito.verify(mockCache).put(ArgumentMatchers.eq(aggregateIdentifier), ArgumentMatchers.notNull());
        Mockito.verify(mockEventStore).readEvents(ArgumentMatchers.eq(aggregateIdentifier));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadFromRepositoryLoadsFromCache() throws Exception {
        final Repository<CommandHandlerInvokerTest.StubAggregate> repository = testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        Mockito.when(mockCommandHandler.handle(ArgumentMatchers.eq(mockCommandMessage))).thenAnswer(( invocationOnMock) -> repository.load(aggregateIdentifier));
        Mockito.when(mockCache.get(aggregateIdentifier)).thenAnswer(( invocationOnMock) -> new AggregateCacheEntry<>(EventSourcedAggregate.initialize(new org.axonframework.disruptor.commandhandling.StubAggregate(aggregateIdentifier), AnnotatedAggregateMetaModelFactory.inspectAggregate(.class), mockEventStore, mockTrigger)));
        testSubject.onEvent(commandHandlingEntry, 0, true);
        Mockito.verify(mockCache).get(aggregateIdentifier);
        Mockito.verify(mockEventStore, Mockito.never()).readEvents(ArgumentMatchers.eq(aggregateIdentifier));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddToRepositoryAddsInCache() throws Exception {
        final Repository<CommandHandlerInvokerTest.StubAggregate> repository = testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        Mockito.when(mockCommandHandler.handle(ArgumentMatchers.eq(mockCommandMessage))).thenAnswer(( invocationOnMock) -> {
            Aggregate<org.axonframework.disruptor.commandhandling.StubAggregate> aggregate = repository.newInstance(() -> new org.axonframework.disruptor.commandhandling.StubAggregate(aggregateIdentifier));
            aggregate.execute(org.axonframework.disruptor.commandhandling.StubAggregate::doSomething);
            return aggregate.invoke(Function.identity());
        });
        testSubject.onEvent(commandHandlingEntry, 0, true);
        Mockito.verify(mockEventStore, Mockito.never()).readEvents(ArgumentMatchers.eq(aggregateIdentifier));
        Mockito.verify(mockEventStore, Mockito.never()).readEvents(ArgumentMatchers.eq(aggregateIdentifier));
        Mockito.verify(mockEventStore).publish(ArgumentMatchers.<DomainEventMessage<?>[]>any());
    }

    @Test
    public void testCacheEntryInvalidatedOnRecoveryEntry() {
        commandHandlingEntry.resetAsRecoverEntry(aggregateIdentifier);
        testSubject.onEvent(commandHandlingEntry, 0, true);
        Mockito.verify(mockCache).remove(aggregateIdentifier);
        Mockito.verify(mockEventStore, Mockito.never()).readEvents(ArgumentMatchers.eq(aggregateIdentifier));
    }

    @Test
    public void testCreateRepositoryReturnsSameInstanceOnSecondInvocation() {
        final Repository<CommandHandlerInvokerTest.StubAggregate> repository1 = testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        final Repository<CommandHandlerInvokerTest.StubAggregate> repository2 = testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        Assert.assertSame(repository1, repository2);
    }

    @Test
    public void testCanResolveReturnsTrueForMatchingAggregateDescriptor() {
        Repository<CommandHandlerInvokerTest.StubAggregate> testRepository = testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        Assert.assertTrue(testRepository.canResolve(new AggregateScopeDescriptor(CommandHandlerInvokerTest.StubAggregate.class.getSimpleName(), "some-identifier")));
    }

    @Test
    public void testCanResolveReturnsFalseNonAggregateScopeDescriptorImplementation() {
        Repository<CommandHandlerInvokerTest.StubAggregate> testRepository = testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        Assert.assertFalse(testRepository.canResolve(new SagaScopeDescriptor("some-saga-type", "some-identifier")));
    }

    @Test
    public void testCanResolveReturnsFalseForNonMatchingAggregateType() {
        Repository<CommandHandlerInvokerTest.StubAggregate> testRepository = testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        Assert.assertFalse(testRepository.canResolve(new AggregateScopeDescriptor("other-non-matching-type", "some-identifier")));
    }

    @Test
    public void testSendDeliversMessageAtDescribedAggregateInstance() throws Exception {
        String testAggregateId = "some-identifier";
        DeadlineMessage<CommandHandlerInvokerTest.DeadlinePayload> testMsg = GenericDeadlineMessage.asDeadlineMessage("deadline-name", new CommandHandlerInvokerTest.DeadlinePayload());
        AggregateScopeDescriptor testDescriptor = new AggregateScopeDescriptor(CommandHandlerInvokerTest.StubAggregate.class.getSimpleName(), testAggregateId);
        Repository<CommandHandlerInvokerTest.StubAggregate> testRepository = testSubject.createRepository(mockEventStore, new GenericAggregateFactory(CommandHandlerInvokerTest.StubAggregate.class), snapshotTriggerDefinition, ClasspathParameterResolverFactory.forClass(CommandHandlerInvokerTest.StubAggregate.class));
        Mockito.when(mockEventStore.readEvents(ArgumentMatchers.any())).thenReturn(DomainEventStream.of(new org.axonframework.eventhandling.GenericDomainEventMessage(CommandHandlerInvokerTest.StubAggregate.class.getSimpleName(), testAggregateId, 0, testAggregateId)));
        commandHandlingEntry.start();
        try {
            testRepository.send(testMsg, testDescriptor);
        } finally {
            commandHandlingEntry.pause();
        }
        Assert.assertEquals(1, CommandHandlerInvokerTest.messageHandlingCounter.get());
    }

    private static class FailingPayload {}

    private static class DeadlinePayload {}

    @SuppressWarnings("unused")
    public static class StubAggregate implements Serializable {
        @AggregateIdentifier
        private String id;

        public StubAggregate() {
        }

        public StubAggregate(String id) {
            this.id = id;
        }

        public void doSomething() {
            apply(id);
        }

        @DeadlineHandler
        public void handle(CommandHandlerInvokerTest.FailingPayload deadline) {
            throw new IllegalArgumentException();
        }

        @DeadlineHandler
        public void handle(CommandHandlerInvokerTest.DeadlinePayload deadline) {
            CommandHandlerInvokerTest.messageHandlingCounter.getAndIncrement();
        }

        @EventSourcingHandler
        public void handle(String id) {
            this.id = id;
        }
    }
}

