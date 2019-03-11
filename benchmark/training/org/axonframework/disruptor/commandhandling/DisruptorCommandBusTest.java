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


import ProducerType.SINGLE;
import com.lmax.disruptor.SleepingWaitStrategy;
import java.lang.reflect.Executable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import junit.framework.TestCase;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.NoHandlerForCommandException;
import org.axonframework.common.Registration;
import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.deadline.DeadlineMessage;
import org.axonframework.deadline.GenericDeadlineMessage;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.disruptor.commandhandling.utils.MockException;
import org.axonframework.disruptor.commandhandling.utils.SomethingDoneEvent;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.TrackingEventStream;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.eventsourcing.GenericAggregateFactory;
import org.axonframework.eventsourcing.SnapshotTrigger;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.InterceptorChain;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.messaging.annotation.MessageHandlerInvocationException;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.command.Aggregate;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateScopeDescriptor;
import org.axonframework.modelling.command.Repository;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.modelling.saga.SagaScopeDescriptor;
import org.axonframework.monitoring.MessageMonitor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Allard Buijze
 */
public class DisruptorCommandBusTest {
    private static final int COMMAND_COUNT = 100 * 1000;

    private DisruptorCommandBusTest.StubHandler stubHandler;

    private DisruptorCommandBusTest.InMemoryEventStore eventStore;

    private DisruptorCommandBus testSubject;

    private String aggregateIdentifier;

    private TransactionManager mockTransactionManager;

    private ParameterResolverFactory parameterResolverFactory;

    private static AtomicInteger messageHandlingCounter;

    @SuppressWarnings("unchecked")
    @Test
    public void testCallbackInvokedBeforeUnitOfWorkCleanup() throws Exception {
        MessageHandlerInterceptor mockHandlerInterceptor = Mockito.mock(MessageHandlerInterceptor.class);
        MessageDispatchInterceptor mockDispatchInterceptor = Mockito.mock(MessageDispatchInterceptor.class);
        Mockito.when(mockDispatchInterceptor.handle(ArgumentMatchers.isA(CommandMessage.class))).thenAnswer(new DisruptorCommandBusTest.Parameter(0));
        ExecutorService customExecutor = Executors.newCachedThreadPool();
        testSubject = DisruptorCommandBus.builder().dispatchInterceptors(Collections.singletonList(mockDispatchInterceptor)).invokerInterceptors(Collections.singletonList(mockHandlerInterceptor)).bufferSize(8).producerType(SINGLE).waitStrategy(new SleepingWaitStrategy()).executor(customExecutor).invokerThreadCount(2).publisherThreadCount(3).build();
        testSubject.subscribe(DisruptorCommandBusTest.StubCommand.class.getName(), stubHandler);
        GenericAggregateFactory<DisruptorCommandBusTest.StubAggregate> aggregateFactory = new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class);
        stubHandler.setRepository(testSubject.createRepository(eventStore, aggregateFactory, parameterResolverFactory));
        Consumer<UnitOfWork<CommandMessage<?>>> mockPrepareCommitConsumer = Mockito.mock(Consumer.class);
        Consumer<UnitOfWork<CommandMessage<?>>> mockAfterCommitConsumer = Mockito.mock(Consumer.class);
        Consumer<UnitOfWork<CommandMessage<?>>> mockCleanUpConsumer = Mockito.mock(Consumer.class);
        Mockito.when(mockHandlerInterceptor.handle(ArgumentMatchers.any(UnitOfWork.class), ArgumentMatchers.any(InterceptorChain.class))).thenAnswer(( invocation) -> {
            final UnitOfWork<CommandMessage<?>> unitOfWork = ((UnitOfWork<CommandMessage<?>>) (invocation.getArguments()[0]));
            unitOfWork.onPrepareCommit(mockPrepareCommitConsumer);
            unitOfWork.afterCommit(mockAfterCommitConsumer);
            unitOfWork.onCleanup(mockCleanUpConsumer);
            return ((InterceptorChain) (invocation.getArguments()[1])).proceed();
        });
        CommandMessage<DisruptorCommandBusTest.StubCommand> command = asCommandMessage(new DisruptorCommandBusTest.StubCommand(aggregateIdentifier));
        CommandCallback mockCallback = Mockito.mock(CommandCallback.class);
        testSubject.dispatch(command, mockCallback);
        testSubject.stop();
        Assert.assertFalse(customExecutor.awaitTermination(250, TimeUnit.MILLISECONDS));
        customExecutor.shutdown();
        TestCase.assertTrue(customExecutor.awaitTermination(5, TimeUnit.SECONDS));
        InOrder inOrder = Mockito.inOrder(mockDispatchInterceptor, mockHandlerInterceptor, mockPrepareCommitConsumer, mockAfterCommitConsumer, mockCleanUpConsumer, mockCallback);
        inOrder.verify(mockDispatchInterceptor).handle(ArgumentMatchers.isA(CommandMessage.class));
        inOrder.verify(mockHandlerInterceptor).handle(ArgumentMatchers.any(UnitOfWork.class), ArgumentMatchers.any(InterceptorChain.class));
        inOrder.verify(mockPrepareCommitConsumer).accept(ArgumentMatchers.isA(UnitOfWork.class));
        inOrder.verify(mockAfterCommitConsumer).accept(ArgumentMatchers.isA(UnitOfWork.class));
        inOrder.verify(mockCleanUpConsumer).accept(ArgumentMatchers.isA(UnitOfWork.class));
        Mockito.verify(mockCallback).onResult(ArgumentMatchers.eq(command), ArgumentMatchers.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPublishUnsupportedCommand() {
        ExecutorService customExecutor = Executors.newCachedThreadPool();
        testSubject = DisruptorCommandBus.builder().bufferSize(8).producerType(SINGLE).waitStrategy(new SleepingWaitStrategy()).executor(customExecutor).invokerThreadCount(2).publisherThreadCount(3).build();
        CommandCallback callback = Mockito.mock(CommandCallback.class);
        testSubject.dispatch(asCommandMessage("Test"), callback);
        customExecutor.shutdownNow();
        ArgumentCaptor<CommandResultMessage<?>> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(callback).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        TestCase.assertTrue(commandResultMessageCaptor.getValue().isExceptional());
        Throwable exceptionResult = commandResultMessageCaptor.getValue().exceptionResult();
        TestCase.assertEquals(NoHandlerForCommandException.class, exceptionResult.getClass());
        TestCase.assertTrue(exceptionResult.getMessage().contains(String.class.getSimpleName()));
    }

    @Test
    public void testEventStreamsDecoratedOnReadAndWrite() throws InterruptedException {
        ExecutorService customExecutor = Executors.newCachedThreadPool();
        testSubject = DisruptorCommandBus.builder().bufferSize(8).producerType(SINGLE).waitStrategy(new SleepingWaitStrategy()).executor(customExecutor).invokerThreadCount(2).publisherThreadCount(3).build();
        testSubject.subscribe(DisruptorCommandBusTest.StubCommand.class.getName(), stubHandler);
        SnapshotTriggerDefinition snapshotTriggerDefinition = Mockito.mock(SnapshotTriggerDefinition.class);
        SnapshotTrigger snapshotTrigger = Mockito.mock(SnapshotTrigger.class);
        Mockito.when(snapshotTriggerDefinition.prepareTrigger(ArgumentMatchers.any())).thenReturn(snapshotTrigger);
        stubHandler.setRepository(testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class), snapshotTriggerDefinition));
        CommandMessage<DisruptorCommandBusTest.StubCommand> command = asCommandMessage(new DisruptorCommandBusTest.StubCommand(aggregateIdentifier));
        CommandCallback mockCallback = Mockito.mock(CommandCallback.class);
        testSubject.dispatch(command, mockCallback);
        testSubject.dispatch(command);
        testSubject.stop();
        Assert.assertFalse(customExecutor.awaitTermination(250, TimeUnit.MILLISECONDS));
        customExecutor.shutdown();
        TestCase.assertTrue(customExecutor.awaitTermination(5, TimeUnit.SECONDS));
        // invoked 3 times, 1 during initialization, and 2 for the executed commands
        InOrder inOrder = Mockito.inOrder(snapshotTrigger);
        inOrder.verify(snapshotTrigger).eventHandled(ArgumentMatchers.isA(DomainEventMessage.class));
        inOrder.verify(snapshotTrigger).initializationFinished();
        inOrder.verify(snapshotTrigger, Mockito.times(2)).eventHandled(ArgumentMatchers.isA(DomainEventMessage.class));
    }

    @Test
    public void usesProvidedParameterResolverFactoryToResolveParameters() {
        testSubject = DisruptorCommandBus.builder().build();
        testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class), parameterResolverFactory);
        Mockito.verify(parameterResolverFactory, Mockito.atLeastOnce()).createInstance(ArgumentMatchers.isA(Executable.class), ArgumentMatchers.isA(java.lang.reflect.Parameter[].class), ArgumentMatchers.anyInt());
        Mockito.verifyNoMoreInteractions(parameterResolverFactory);
    }

    @Test
    public void testEventPublicationExecutedWithinTransaction() throws Exception {
        MessageHandlerInterceptor mockInterceptor = Mockito.mock(MessageHandlerInterceptor.class);
        ExecutorService customExecutor = Executors.newCachedThreadPool();
        Transaction mockTransaction = Mockito.mock(Transaction.class);
        mockTransactionManager = Mockito.mock(TransactionManager.class);
        Mockito.when(mockTransactionManager.startTransaction()).thenReturn(mockTransaction);
        dispatchCommands(mockInterceptor, customExecutor, asCommandMessage(new DisruptorCommandBusTest.ErrorCommand(aggregateIdentifier)));
        Assert.assertFalse(customExecutor.awaitTermination(250, TimeUnit.MILLISECONDS));
        customExecutor.shutdown();
        TestCase.assertTrue(customExecutor.awaitTermination(5, TimeUnit.SECONDS));
        Mockito.verify(mockTransactionManager, Mockito.times(991)).startTransaction();
        Mockito.verify(mockTransaction, Mockito.times(991)).commit();
        Mockito.verifyNoMoreInteractions(mockTransaction, mockTransactionManager);
    }

    @SuppressWarnings({ "unchecked", "Duplicates" })
    @Test(timeout = 10000)
    public void testAggregatesBlacklistedAndRecoveredOnError_WithAutoReschedule() throws Exception {
        MessageHandlerInterceptor mockInterceptor = Mockito.mock(MessageHandlerInterceptor.class);
        ExecutorService customExecutor = Executors.newCachedThreadPool();
        CommandCallback mockCallback = dispatchCommands(mockInterceptor, customExecutor, asCommandMessage(new DisruptorCommandBusTest.ErrorCommand(aggregateIdentifier)));
        Assert.assertFalse(customExecutor.awaitTermination(250, TimeUnit.MILLISECONDS));
        customExecutor.shutdown();
        TestCase.assertTrue(customExecutor.awaitTermination(5, TimeUnit.SECONDS));
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(mockCallback, Mockito.times(1000)).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        TestCase.assertEquals(10, commandResultMessageCaptor.getAllValues().stream().filter(ResultMessage::isExceptional).count());
    }

    @SuppressWarnings({ "unchecked", "Duplicates" })
    @Test(timeout = 10000)
    public void testAggregatesBlacklistedAndRecoveredOnError_WithoutReschedule() throws Exception {
        MessageHandlerInterceptor mockInterceptor = Mockito.mock(MessageHandlerInterceptor.class);
        ExecutorService customExecutor = Executors.newCachedThreadPool();
        CommandCallback mockCallback = dispatchCommands(mockInterceptor, customExecutor, asCommandMessage(new DisruptorCommandBusTest.ErrorCommand(aggregateIdentifier)));
        Assert.assertFalse(customExecutor.awaitTermination(250, TimeUnit.MILLISECONDS));
        customExecutor.shutdown();
        TestCase.assertTrue(customExecutor.awaitTermination(5, TimeUnit.SECONDS));
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(mockCallback, Mockito.times(1000)).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        TestCase.assertEquals(10, commandResultMessageCaptor.getAllValues().stream().filter(ResultMessage::isExceptional).count());
    }

    @Test
    public void testCreateAggregate() {
        eventStore.storedEvents.clear();
        testSubject = DisruptorCommandBus.builder().bufferSize(8).producerType(SINGLE).waitStrategy(new SleepingWaitStrategy()).invokerThreadCount(2).publisherThreadCount(3).build();
        testSubject.subscribe(DisruptorCommandBusTest.StubCommand.class.getName(), stubHandler);
        testSubject.subscribe(DisruptorCommandBusTest.CreateCommand.class.getName(), stubHandler);
        testSubject.subscribe(DisruptorCommandBusTest.ErrorCommand.class.getName(), stubHandler);
        stubHandler.setRepository(testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.CreateCommand(aggregateIdentifier)));
        testSubject.stop();
        DomainEventMessage lastEvent = eventStore.storedEvents.get(aggregateIdentifier);
        // we expect 2 events, 1 from aggregate constructor, one from doSomething method invocation
        TestCase.assertEquals(1, lastEvent.getSequenceNumber());
        TestCase.assertEquals(aggregateIdentifier, lastEvent.getAggregateIdentifier());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMessageMonitoring() {
        eventStore.storedEvents.clear();
        final AtomicLong successCounter = new AtomicLong();
        final AtomicLong failureCounter = new AtomicLong();
        final AtomicLong ignoredCounter = new AtomicLong();
        testSubject = DisruptorCommandBus.builder().bufferSize(8).messageMonitor(( msg) -> new MessageMonitor.MonitorCallback() {
            @Override
            public void reportSuccess() {
                successCounter.incrementAndGet();
            }

            @Override
            public void reportFailure(Throwable cause) {
                failureCounter.incrementAndGet();
            }

            @Override
            public void reportIgnored() {
                ignoredCounter.incrementAndGet();
            }
        }).build();
        testSubject.subscribe(DisruptorCommandBusTest.StubCommand.class.getName(), stubHandler);
        testSubject.subscribe(DisruptorCommandBusTest.CreateCommand.class.getName(), stubHandler);
        testSubject.subscribe(DisruptorCommandBusTest.ErrorCommand.class.getName(), stubHandler);
        stubHandler.setRepository(testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class)));
        String aggregateIdentifier2 = UUID.randomUUID().toString();
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.CreateCommand(aggregateIdentifier)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.StubCommand(aggregateIdentifier)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.ErrorCommand(aggregateIdentifier)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.StubCommand(aggregateIdentifier)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.StubCommand(aggregateIdentifier)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.CreateCommand(aggregateIdentifier2)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.StubCommand(aggregateIdentifier2)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.ErrorCommand(aggregateIdentifier2)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.StubCommand(aggregateIdentifier2)));
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.StubCommand(aggregateIdentifier2)));
        CommandCallback callback = Mockito.mock(CommandCallback.class);
        testSubject.dispatch(asCommandMessage(new DisruptorCommandBusTest.UnknownCommand(aggregateIdentifier2)), callback);
        testSubject.stop();
        TestCase.assertEquals(8, successCounter.get());
        TestCase.assertEquals(3, failureCounter.get());
        TestCase.assertEquals(0, ignoredCounter.get());
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(callback).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        TestCase.assertTrue(commandResultMessageCaptor.getValue().isExceptional());
        TestCase.assertEquals(NoHandlerForCommandException.class, commandResultMessageCaptor.getValue().exceptionResult().getClass());
    }

    @Test(expected = IllegalStateException.class)
    public void testCommandRejectedAfterShutdown() {
        testSubject = DisruptorCommandBus.builder().build();
        testSubject.subscribe(DisruptorCommandBusTest.StubCommand.class.getName(), stubHandler);
        stubHandler.setRepository(testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class)));
        testSubject.stop();
        testSubject.dispatch(asCommandMessage(new Object()));
    }

    @Test(timeout = 10000)
    public void testCommandProcessedAndEventsStored() throws InterruptedException {
        testSubject = DisruptorCommandBus.builder().build();
        testSubject.subscribe(DisruptorCommandBusTest.StubCommand.class.getName(), stubHandler);
        stubHandler.setRepository(testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class)));
        for (int i = 0; i < (DisruptorCommandBusTest.COMMAND_COUNT); i++) {
            CommandMessage<DisruptorCommandBusTest.StubCommand> command = asCommandMessage(new DisruptorCommandBusTest.StubCommand(aggregateIdentifier));
            testSubject.dispatch(command);
        }
        eventStore.countDownLatch.await(5, TimeUnit.SECONDS);
        TestCase.assertEquals("Seems that some events are not stored", 0, eventStore.countDownLatch.getCount());
    }

    @Test
    public void testCanResolveReturnsTrueForMatchingAggregateDescriptor() {
        testSubject = DisruptorCommandBus.builder().build();
        Repository<DisruptorCommandBusTest.StubAggregate> testRepository = testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class), parameterResolverFactory);
        TestCase.assertTrue(testRepository.canResolve(new AggregateScopeDescriptor(DisruptorCommandBusTest.StubAggregate.class.getSimpleName(), aggregateIdentifier)));
    }

    @Test
    public void testCanResolveReturnsFalseNonAggregateScopeDescriptorImplementation() {
        testSubject = DisruptorCommandBus.builder().build();
        Repository<DisruptorCommandBusTest.StubAggregate> testRepository = testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class), parameterResolverFactory);
        Assert.assertFalse(testRepository.canResolve(new SagaScopeDescriptor("some-saga-type", aggregateIdentifier)));
    }

    @Test
    public void testCanResolveReturnsFalseForNonMatchingAggregateType() {
        testSubject = DisruptorCommandBus.builder().build();
        Repository<DisruptorCommandBusTest.StubAggregate> testRepository = testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class), parameterResolverFactory);
        Assert.assertFalse(testRepository.canResolve(new AggregateScopeDescriptor("other-non-matching-type", aggregateIdentifier)));
    }

    @Test
    public void testSendDeliversMessageAtDescribedAggregateInstance() throws Exception {
        DeadlineMessage<DisruptorCommandBusTest.DeadlinePayload> testMsg = GenericDeadlineMessage.asDeadlineMessage("deadline-name", new DisruptorCommandBusTest.DeadlinePayload());
        AggregateScopeDescriptor testDescriptor = new AggregateScopeDescriptor(DisruptorCommandBusTest.StubAggregate.class.getSimpleName(), aggregateIdentifier);
        testSubject = DisruptorCommandBus.builder().build();
        Repository<DisruptorCommandBusTest.StubAggregate> testRepository = testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class), parameterResolverFactory);
        testRepository.send(testMsg, testDescriptor);
        TestCase.assertEquals(1, DisruptorCommandBusTest.messageHandlingCounter.get());
    }

    @Test(expected = MessageHandlerInvocationException.class)
    public void testSendThrowsMessageHandlerInvocationExceptionIfHandleFails() throws Exception {
        DeadlineMessage<DisruptorCommandBusTest.FailingEvent> testMsg = GenericDeadlineMessage.asDeadlineMessage("deadline-name", new DisruptorCommandBusTest.FailingEvent());
        AggregateScopeDescriptor testDescriptor = new AggregateScopeDescriptor(DisruptorCommandBusTest.StubAggregate.class.getSimpleName(), aggregateIdentifier);
        testSubject = DisruptorCommandBus.builder().build();
        Repository<DisruptorCommandBusTest.StubAggregate> testRepository = testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class), parameterResolverFactory);
        testRepository.send(testMsg, testDescriptor);
    }

    @Test
    public void testSendFailsSilentlyOnAggregateNotFoundException() throws Exception {
        DeadlineMessage<DisruptorCommandBusTest.DeadlinePayload> testMsg = GenericDeadlineMessage.asDeadlineMessage("deadline-name", new DisruptorCommandBusTest.DeadlinePayload());
        AggregateScopeDescriptor testDescriptor = new AggregateScopeDescriptor(DisruptorCommandBusTest.StubAggregate.class.getSimpleName(), "some-other-aggregate-id");
        testSubject = DisruptorCommandBus.builder().build();
        Repository<DisruptorCommandBusTest.StubAggregate> testRepository = testSubject.createRepository(eventStore, new GenericAggregateFactory(DisruptorCommandBusTest.StubAggregate.class), parameterResolverFactory);
        testRepository.send(testMsg, testDescriptor);
        TestCase.assertEquals(0, DisruptorCommandBusTest.messageHandlingCounter.get());
    }

    private static class DeadlinePayload {}

    @SuppressWarnings("unused")
    private static class StubAggregate {
        @AggregateIdentifier
        private String identifier;

        private StubAggregate(String identifier) {
            this.identifier = identifier;
            apply(new SomethingDoneEvent());
        }

        @SuppressWarnings("UnusedDeclaration")
        public StubAggregate() {
        }

        public String getIdentifier() {
            return identifier;
        }

        public void doSomething() {
            apply(new SomethingDoneEvent());
        }

        public void createFailingEvent() {
            apply(new DisruptorCommandBusTest.FailingEvent());
        }

        @DeadlineHandler
        public void handle(DisruptorCommandBusTest.FailingEvent deadline) {
            throw new IllegalArgumentException();
        }

        @DeadlineHandler
        public void handle(DisruptorCommandBusTest.DeadlinePayload deadline) {
            DisruptorCommandBusTest.messageHandlingCounter.getAndIncrement();
        }

        @EventSourcingHandler
        protected void handle(EventMessage event) {
            identifier = getAggregateIdentifier();
        }
    }

    private static class InMemoryEventStore implements EventStore {
        private final Map<String, DomainEventMessage> storedEvents = new ConcurrentHashMap<>();

        private final CountDownLatch countDownLatch = new CountDownLatch(((int) ((DisruptorCommandBusTest.COMMAND_COUNT) + 1L)));

        @Override
        public DomainEventStream readEvents(String aggregateIdentifier) {
            DomainEventMessage message = storedEvents.get(aggregateIdentifier);
            return message == null ? DomainEventStream.empty() : DomainEventStream.of(message);
        }

        @Override
        public void publish(List<? extends EventMessage<?>> events) {
            if ((events == null) || (events.isEmpty())) {
                return;
            }
            String key = ((DomainEventMessage<?>) (events.get(0))).getAggregateIdentifier();
            DomainEventMessage<?> lastEvent = null;
            for (EventMessage<?> event : events) {
                countDownLatch.countDown();
                lastEvent = ((DomainEventMessage<?>) (event));
                if (DisruptorCommandBusTest.FailingEvent.class.isAssignableFrom(lastEvent.getPayloadType())) {
                    throw new MockException("This is a failing event. EventStore refuses to store that");
                }
            }
            storedEvents.put(key, lastEvent);
        }

        @Override
        public TrackingEventStream openStream(TrackingToken trackingToken) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Registration subscribe(Consumer<List<? extends EventMessage<?>>> eventProcessor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Registration registerDispatchInterceptor(MessageDispatchInterceptor<? super EventMessage<?>> dispatchInterceptor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void storeSnapshot(DomainEventMessage<?> snapshot) {
        }
    }

    private static class StubCommand {
        @TargetAggregateIdentifier
        private Object aggregateIdentifier;

        public StubCommand(Object aggregateIdentifier) {
            this.aggregateIdentifier = aggregateIdentifier;
        }

        public String getAggregateIdentifier() {
            return aggregateIdentifier.toString();
        }
    }

    private static class ErrorCommand extends DisruptorCommandBusTest.StubCommand {
        public ErrorCommand(Object aggregateIdentifier) {
            super(aggregateIdentifier);
        }
    }

    private static class ExceptionCommand extends DisruptorCommandBusTest.StubCommand {
        private final Exception exception;

        public ExceptionCommand(Object aggregateIdentifier, Exception exception) {
            super(aggregateIdentifier);
            this.exception = exception;
        }

        public Exception getException() {
            return exception;
        }
    }

    private static class CreateCommand extends DisruptorCommandBusTest.StubCommand {
        public CreateCommand(Object aggregateIdentifier) {
            super(aggregateIdentifier);
        }
    }

    private static class UnknownCommand extends DisruptorCommandBusTest.StubCommand {
        public UnknownCommand(Object aggregateIdentifier) {
            super(aggregateIdentifier);
        }
    }

    private static class StubHandler implements MessageHandler<CommandMessage<?>> {
        private Repository<DisruptorCommandBusTest.StubAggregate> repository;

        private StubHandler() {
        }

        @Override
        public Object handle(CommandMessage<?> command) throws Exception {
            DisruptorCommandBusTest.StubCommand payload = ((DisruptorCommandBusTest.StubCommand) (command.getPayload()));
            if (DisruptorCommandBusTest.ExceptionCommand.class.isAssignableFrom(command.getPayloadType())) {
                throw ((DisruptorCommandBusTest.ExceptionCommand) (command.getPayload())).getException();
            } else
                if (DisruptorCommandBusTest.CreateCommand.class.isAssignableFrom(command.getPayloadType())) {
                    repository.newInstance(() -> new org.axonframework.disruptor.commandhandling.StubAggregate(payload.getAggregateIdentifier())).execute(DisruptorCommandBusTest.StubAggregate::doSomething);
                } else {
                    Aggregate<DisruptorCommandBusTest.StubAggregate> aggregate = repository.load(payload.getAggregateIdentifier());
                    if (DisruptorCommandBusTest.ErrorCommand.class.isAssignableFrom(command.getPayloadType())) {
                        aggregate.execute(DisruptorCommandBusTest.StubAggregate::createFailingEvent);
                    } else {
                        aggregate.execute(DisruptorCommandBusTest.StubAggregate::doSomething);
                    }
                }

            return null;
        }

        public void setRepository(Repository<DisruptorCommandBusTest.StubAggregate> repository) {
            this.repository = repository;
        }
    }

    private static class StubDomainEvent {}

    static class FailingEvent {}

    private static class Parameter implements Answer<Object> {
        private final int index;

        private Parameter(int index) {
            this.index = index;
        }

        @Override
        public Object answer(InvocationOnMock invocation) {
            return invocation.getArguments()[index];
        }
    }
}

