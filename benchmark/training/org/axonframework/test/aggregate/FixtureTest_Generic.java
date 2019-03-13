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
package org.axonframework.test.aggregate;


import java.util.UUID;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.IncompatibleAggregateException;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStoreException;
import org.axonframework.test.FixtureExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class FixtureTest_Generic {
    private FixtureConfiguration<StandardAggregate> fixture;

    private AggregateFactory<StandardAggregate> mockAggregateFactory;

    @SuppressWarnings("unchecked")
    @Test
    public void testConfigureCustomAggregateFactory() {
        fixture.registerAggregateFactory(mockAggregateFactory);
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus()));
        fixture.given(new MyEvent("id1", 1)).when(new TestCommand("id1"));
        Mockito.verify(mockAggregateFactory).createAggregateRoot(ArgumentMatchers.eq("id1"), ArgumentMatchers.isA(DomainEventMessage.class));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IncompatibleAggregateException.class)
    public void testConfigurationOfRequiredCustomAggregateFactoryNotProvided_FailureOnGiven() {
        fixture.given(new MyEvent("id1", 1));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IncompatibleAggregateException.class)
    public void testConfigurationOfRequiredCustomAggregateFactoryNotProvided_FailureOnGetRepository() {
        fixture.getRepository();
    }

    @Test
    public void testAggregateIdentifier_ServerGeneratedIdentifier() {
        fixture.registerAggregateFactory(mockAggregateFactory);
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus()));
        fixture.givenNoPriorActivity().when(new CreateAggregateCommand());
    }

    @Test
    public void testStoringExistingAggregateGeneratesException() {
        fixture.registerAggregateFactory(mockAggregateFactory);
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus()));
        fixture.given(new MyEvent("aggregateId", 1)).when(new CreateAggregateCommand("aggregateId")).expectException(EventStoreException.class);
    }

    @Test(expected = FixtureExecutionException.class)
    public void testInjectResources_CommandHandlerAlreadyRegistered() {
        fixture.registerAggregateFactory(mockAggregateFactory);
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus()));
        fixture.registerInjectableResource("I am injectable");
    }

    @Test
    public void testAggregateIdentifier_IdentifierAutomaticallyDeducted() {
        fixture.registerAggregateFactory(mockAggregateFactory);
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus()));
        fixture.given(new MyEvent("AggregateId", 1), new MyEvent("AggregateId", 2)).when(new TestCommand("AggregateId")).expectEvents(new MyEvent("AggregateId", 3));
        DomainEventStream events = fixture.getEventStore().readEvents("AggregateId");
        for (int t = 0; t < 3; t++) {
            Assert.assertTrue(events.hasNext());
            DomainEventMessage next = events.next();
            Assert.assertEquals("AggregateId", next.getAggregateIdentifier());
            Assert.assertEquals(t, next.getSequenceNumber());
        }
    }

    @Test
    public void testReadAggregate_WrongIdentifier() {
        fixture.registerAggregateFactory(mockAggregateFactory);
        fixture.registerAnnotatedCommandHandler(new MyCommandHandler(fixture.getRepository(), fixture.getEventBus()));
        TestExecutor exec = fixture.given(new MyEvent("AggregateId", 1));
        try {
            exec.when(new TestCommand("OtherIdentifier"));
            Assert.fail("Expected an AssertionError");
        } catch (AssertionError e) {
            Assert.assertTrue(("Wrong message. Was: " + (e.getMessage())), e.getMessage().contains("OtherIdentifier"));
            Assert.assertTrue(("Wrong message. Was: " + (e.getMessage())), e.getMessage().contains("AggregateId"));
        }
    }

    @Test(expected = EventStoreException.class)
    public void testFixtureGeneratesExceptionOnWrongEvents_DifferentAggregateIdentifiers() {
        fixture.getEventStore().publish(new org.axonframework.eventhandling.GenericDomainEventMessage("test", UUID.randomUUID().toString(), 0, new FixtureTest_Generic.StubDomainEvent()), new org.axonframework.eventhandling.GenericDomainEventMessage("test", UUID.randomUUID().toString(), 0, new FixtureTest_Generic.StubDomainEvent()));
    }

    @Test(expected = EventStoreException.class)
    public void testFixtureGeneratesExceptionOnWrongEvents_WrongSequence() {
        String identifier = UUID.randomUUID().toString();
        fixture.getEventStore().publish(new org.axonframework.eventhandling.GenericDomainEventMessage("test", identifier, 0, new FixtureTest_Generic.StubDomainEvent()), new org.axonframework.eventhandling.GenericDomainEventMessage("test", identifier, 2, new FixtureTest_Generic.StubDomainEvent()));
    }

    private class StubDomainEvent {
        public StubDomainEvent() {
        }
    }
}

