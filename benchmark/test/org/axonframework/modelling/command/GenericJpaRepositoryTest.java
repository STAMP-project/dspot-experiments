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
package org.axonframework.modelling.command;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Version;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.DomainEventSequenceAware;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.Returns;


/**
 *
 *
 * @author Allard Buijze
 */
public class GenericJpaRepositoryTest {
    private EntityManager mockEntityManager;

    private GenericJpaRepository<GenericJpaRepositoryTest.StubJpaAggregate> testSubject;

    private String aggregateId;

    private GenericJpaRepositoryTest.StubJpaAggregate aggregate;

    private Function<String, ?> identifierConverter;

    private EventBus eventBus;

    @Test
    public void testAggregateStoredBeforeEventsPublished() throws Exception {
        // noinspection unchecked
        Consumer<List<? extends EventMessage<?>>> mockConsumer = Mockito.mock(Consumer.class);
        eventBus.subscribe(mockConsumer);
        testSubject.newInstance(() -> new org.axonframework.modelling.command.StubJpaAggregate("test", "payload1", "payload2"));
        CurrentUnitOfWork.commit();
        InOrder inOrder = Mockito.inOrder(mockEntityManager, mockConsumer);
        inOrder.verify(mockEntityManager).persist(ArgumentMatchers.any());
        inOrder.verify(mockConsumer).accept(ArgumentMatchers.anyList());
    }

    @Test
    public void testLoadAggregate() {
        Aggregate<GenericJpaRepositoryTest.StubJpaAggregate> actualResult = testSubject.load(aggregateId);
        Assert.assertSame(aggregate, actualResult.invoke(Function.identity()));
    }

    @Test
    public void testLoadAggregateWithConverter() {
        Mockito.when(identifierConverter.apply("original")).thenAnswer(new Returns(aggregateId));
        Aggregate<GenericJpaRepositoryTest.StubJpaAggregate> actualResult = testSubject.load("original");
        Assert.assertSame(aggregate, actualResult.invoke(Function.identity()));
    }

    @Test
    public void testAggregateCreatesSequenceNumbersForNewAggregatesWhenUsingDomainEventSequenceAwareEventBus() {
        GenericJpaRepositoryTest.DomainSequenceAwareEventBus testEventBus = new GenericJpaRepositoryTest.DomainSequenceAwareEventBus();
        testSubject = GenericJpaRepository.builder(GenericJpaRepositoryTest.StubJpaAggregate.class).entityManagerProvider(new org.axonframework.common.jpa.SimpleEntityManagerProvider(mockEntityManager)).eventBus(testEventBus).identifierConverter(identifierConverter).build();
        DefaultUnitOfWork.startAndGet(null).executeWithResult(() -> {
            Aggregate<org.axonframework.modelling.command.StubJpaAggregate> agg = testSubject.newInstance(() -> new org.axonframework.modelling.command.StubJpaAggregate("id", "test1", "test2"));
            agg.execute(( e) -> e.doSomething("test3"));
            return null;
        });
        CurrentUnitOfWork.commit();
        List<EventMessage> publishedEvents = testEventBus.getPublishedEvents();
        Assert.assertEquals(3, publishedEvents.size());
        EventMessage eventOne = publishedEvents.get(0);
        Assert.assertTrue((eventOne instanceof DomainEventMessage));
        DomainEventMessage domainEventOne = ((DomainEventMessage) (eventOne));
        Assert.assertEquals("test1", domainEventOne.getPayload());
        Assert.assertEquals(0, domainEventOne.getSequenceNumber());
        Assert.assertEquals("id", domainEventOne.getAggregateIdentifier());
        EventMessage eventTwo = publishedEvents.get(1);
        Assert.assertTrue((eventTwo instanceof DomainEventMessage));
        DomainEventMessage domainEventTwo = ((DomainEventMessage) (eventTwo));
        Assert.assertEquals("test2", domainEventTwo.getPayload());
        Assert.assertEquals(1, domainEventTwo.getSequenceNumber());
        Assert.assertEquals("id", domainEventTwo.getAggregateIdentifier());
        EventMessage eventThree = publishedEvents.get(2);
        Assert.assertTrue((eventThree instanceof DomainEventMessage));
        DomainEventMessage domainEventThree = ((DomainEventMessage) (eventThree));
        Assert.assertEquals("test3", domainEventThree.getPayload());
        Assert.assertEquals(2, domainEventThree.getSequenceNumber());
        Assert.assertEquals("id", domainEventThree.getAggregateIdentifier());
    }

    @Test
    public void testAggregateDoesNotCreateSequenceNumbersWhenEventBusIsNotDomainEventSequenceAware() {
        SimpleEventBus testEventBus = Mockito.spy(SimpleEventBus.builder().build());
        testSubject = GenericJpaRepository.builder(GenericJpaRepositoryTest.StubJpaAggregate.class).entityManagerProvider(new org.axonframework.common.jpa.SimpleEntityManagerProvider(mockEntityManager)).eventBus(testEventBus).identifierConverter(identifierConverter).build();
        DefaultUnitOfWork.startAndGet(null).executeWithResult(() -> {
            Aggregate<org.axonframework.modelling.command.StubJpaAggregate> agg = testSubject.load(aggregateId);
            agg.execute(( e) -> e.doSomething("test2"));
            return null;
        });
        CurrentUnitOfWork.commit();
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<? extends EventMessage<?>>> eventCaptor = ArgumentCaptor.forClass(((Class<List<? extends EventMessage<?>>>) ((Class) (List.class))));
        Mockito.verify(testEventBus).publish(eventCaptor.capture());
        List<? extends EventMessage<?>> capturedEvents = eventCaptor.getValue();
        Assert.assertEquals(1, capturedEvents.size());
        EventMessage<?> eventOne = capturedEvents.get(0);
        Assert.assertFalse((eventOne instanceof DomainEventMessage));
        Assert.assertEquals("test2", eventOne.getPayload());
    }

    @Test
    public void testLoadAggregate_NotFound() {
        String aggregateIdentifier = UUID.randomUUID().toString();
        try {
            testSubject.load(aggregateIdentifier);
            Assert.fail("Expected AggregateNotFoundException");
        } catch (AggregateNotFoundException e) {
            Assert.assertEquals(aggregateIdentifier, e.getAggregateIdentifier());
        }
    }

    @Test
    public void testLoadAggregate_WrongVersion() {
        try {
            testSubject.load(aggregateId, 2L);
            Assert.fail("Expected ConflictingAggregateVersionException");
        } catch (ConflictingAggregateVersionException e) {
            Assert.assertEquals(2L, e.getExpectedVersion());
            Assert.assertEquals(0L, e.getActualVersion());
        }
    }

    @Test
    public void testPersistAggregate_DefaultFlushMode() {
        testSubject.doSave(testSubject.load(aggregateId));
        Mockito.verify(mockEntityManager).persist(aggregate);
        Mockito.verify(mockEntityManager).flush();
    }

    @Test
    public void testPersistAggregate_ExplicitFlushModeOn() {
        testSubject.setForceFlushOnSave(true);
        testSubject.doSave(testSubject.load(aggregateId));
        Mockito.verify(mockEntityManager).persist(aggregate);
        Mockito.verify(mockEntityManager).flush();
    }

    @Test
    public void testPersistAggregate_ExplicitFlushModeOff() {
        testSubject.setForceFlushOnSave(false);
        testSubject.doSave(testSubject.load(aggregateId));
        Mockito.verify(mockEntityManager).persist(aggregate);
        Mockito.verify(mockEntityManager, Mockito.never()).flush();
    }

    private class StubJpaAggregate {
        @Id
        private final String identifier;

        @SuppressWarnings("unused")
        @AggregateVersion
        @Version
        private long version;

        private StubJpaAggregate(String identifier) {
            this.identifier = identifier;
        }

        private StubJpaAggregate(String identifier, String... payloads) {
            this(identifier);
            for (String payload : payloads) {
                AggregateLifecycle.apply(payload);
            }
        }

        public void doSomething(String newValue) {
            AggregateLifecycle.apply(newValue);
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    private class DomainSequenceAwareEventBus extends SimpleEventBus implements DomainEventSequenceAware {
        private List<EventMessage> publishedEvents = new ArrayList<>();

        private Map<String, Long> sequencePerAggregate = new HashMap<>();

        protected DomainSequenceAwareEventBus() {
            super(SimpleEventBus.builder());
        }

        @Override
        public void publish(List<? extends EventMessage<?>> events) {
            publishedEvents.addAll(events);
            super.publish(events);
        }

        public List<EventMessage> getPublishedEvents() {
            return publishedEvents;
        }

        @Override
        public Optional<Long> lastSequenceNumberFor(String aggregateIdentifier) {
            if (!(sequencePerAggregate.containsKey(aggregateIdentifier))) {
                sequencePerAggregate.put(aggregateIdentifier, 0L);
            }
            return Optional.ofNullable(sequencePerAggregate.computeIfPresent(aggregateIdentifier, ( aggregateId, seqNo) -> seqNo++));
        }
    }
}

