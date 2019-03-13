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
package org.axonframework.test.aggregate;


import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStoreException;
import org.axonframework.modelling.command.CommandTargetResolver;
import org.axonframework.modelling.command.VersionedAggregateIdentifier;
import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.FixtureExecutionException;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class FixtureTest_Annotated {
    private FixtureConfiguration<AnnotatedAggregate> fixture;

    @Test
    public void testNullIdentifierIsRejected() {
        try {
            fixture.given(new MyEvent(null, 0)).when(new TestCommand("test")).expectEvents(new MyEvent("test", 1)).expectSuccessfulHandlerExecution();
            Assert.fail("Expected test fixture to report failure");
        } catch (AxonAssertionError error) {
            Assert.assertTrue("Expected test to fail with IncompatibleAggregateException", error.getMessage().contains("IncompatibleAggregateException"));
        }
    }

    @Test
    public void testEventsCarryCorrectTimestamp() {
        fixture.givenCurrentTime(Instant.EPOCH).andGiven(new MyEvent("AggregateId", 1), new MyEvent("AggregateId", 2)).andGivenCommands(new TestCommand("AggregateId")).when(new TestCommand("AggregateId")).expectEventsMatching(new TypeSafeMatcher<List<EventMessage<?>>>() {
            @Override
            protected boolean matchesSafely(List<EventMessage<?>> item) {
                return item.stream().allMatch(( i) -> Instant.EPOCH.equals(i.getTimestamp()));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("list with all events with timestamp at epoch");
            }
        });
        Assert.assertTrue(fixture.getEventStore().readEvents("AggregateId").asStream().allMatch(( i) -> Instant.EPOCH.equals(i.getTimestamp())));
        Assert.assertEquals(1, fixture.getEventStore().readEvents("AggregateId").asStream().map(EventMessage::getTimestamp).distinct().count());
    }

    @Test
    public void testClockStandsStillDuringExecution() {
        fixture.given(new MyEvent("AggregateId", 1), new MyEvent("AggregateId", 2)).when(new TestCommand("AggregateId"));
        Assert.assertEquals(1, fixture.getEventStore().readEvents("AggregateId").asStream().map(EventMessage::getTimestamp).distinct().count());
    }

    @Test
    public void testAggregateCommandHandlersOverwrittenByCustomHandlers() {
        final AtomicBoolean invoked = new AtomicBoolean(false);
        fixture.registerCommandHandler(CreateAggregateCommand.class, ( commandMessage) -> {
            invoked.set(true);
            return null;
        });
        fixture.given().when(new CreateAggregateCommand()).expectEvents();
        Assert.assertTrue("", invoked.get());
    }

    @Test
    public void testAggregateIdentifier_ServerGeneratedIdentifier() {
        fixture.registerInjectableResource(new HardToCreateResource());
        fixture.given().when(new CreateAggregateCommand());
    }

    @Test(expected = FixtureExecutionException.class)
    public void testUnavailableResourcesCausesFailure() {
        fixture.given().when(new CreateAggregateCommand());
    }

    @Test
    public void testAggregateIdentifier_IdentifierAutomaticallyDeducted() {
        fixture.given(new MyEvent("AggregateId", 1), new MyEvent("AggregateId", 2)).when(new TestCommand("AggregateId")).expectEvents(new MyEvent("AggregateId", 3)).expectState(Assert::assertNotNull);
        DomainEventStream events = fixture.getEventStore().readEvents("AggregateId");
        for (int t = 0; t < 3; t++) {
            Assert.assertTrue(events.hasNext());
            DomainEventMessage next = events.next();
            Assert.assertEquals("AggregateId", next.getAggregateIdentifier());
            Assert.assertEquals(t, next.getSequenceNumber());
        }
    }

    @Test(expected = FixtureExecutionException.class)
    public void testFixtureGivenCommands_ResourcesNotAvailable() {
        fixture.givenCommands(new CreateAggregateCommand("aggregateId"));
    }

    @Test
    public void testFixtureGivenCommands_ResourcesAvailable() {
        fixture.registerInjectableResource(new HardToCreateResource());
        fixture.givenCommands(new CreateAggregateCommand("aggregateId"), new TestCommand("aggregateId"), new TestCommand("aggregateId"), new TestCommand("aggregateId")).when(new TestCommand("aggregateId")).expectEvents(new MyEvent("aggregateId", 4));
    }

    @Test
    public void testAggregateIdentifier_CustomTargetResolver() {
        CommandTargetResolver mockCommandTargetResolver = Mockito.mock(CommandTargetResolver.class);
        Mockito.when(mockCommandTargetResolver.resolveTarget(ArgumentMatchers.any())).thenReturn(new VersionedAggregateIdentifier("aggregateId", 0L));
        fixture.registerCommandTargetResolver(mockCommandTargetResolver);
        fixture.registerInjectableResource(new HardToCreateResource());
        fixture.givenCommands(new CreateAggregateCommand("aggregateId")).when(new TestCommand("aggregateId")).expectEvents(new MyEvent("aggregateId", 1));
        Mockito.verify(mockCommandTargetResolver).resolveTarget(ArgumentMatchers.any());
    }

    @Test(expected = FixtureExecutionException.class)
    public void testAggregate_InjectCustomResourceAfterCreatingAnnotatedHandler() {
        // a 'when' will cause command handlers to be registered.
        fixture.registerInjectableResource(new HardToCreateResource());
        fixture.given().when(new CreateAggregateCommand("AggregateId"));
        fixture.registerInjectableResource("I am injectable");
    }

    @Test(expected = EventStoreException.class)
    public void testFixtureGeneratesExceptionOnWrongEvents_DifferentAggregateIdentifiers() {
        fixture.getEventStore().publish(new org.axonframework.eventhandling.GenericDomainEventMessage("test", UUID.randomUUID().toString(), 0, new FixtureTest_Annotated.StubDomainEvent()), new org.axonframework.eventhandling.GenericDomainEventMessage("test", UUID.randomUUID().toString(), 0, new FixtureTest_Annotated.StubDomainEvent()));
    }

    @Test(expected = EventStoreException.class)
    public void testFixtureGeneratesExceptionOnWrongEvents_WrongSequence() {
        String identifier = UUID.randomUUID().toString();
        fixture.getEventStore().publish(new org.axonframework.eventhandling.GenericDomainEventMessage("test", identifier, 0, new FixtureTest_Annotated.StubDomainEvent()), new org.axonframework.eventhandling.GenericDomainEventMessage("test", identifier, 2, new FixtureTest_Annotated.StubDomainEvent()));
    }

    @Test
    public void testFixture_AggregateDeleted() {
        fixture.given(new MyEvent("aggregateId", 5)).when(new DeleteCommand("aggregateId", false)).expectEvents(new MyAggregateDeletedEvent(false));
    }

    @Test
    public void testFixtureDetectsStateChangeOutsideOfHandler_AggregateDeleted() {
        TestExecutor exec = fixture.given(new MyEvent("aggregateId", 5));
        try {
            exec.when(new DeleteCommand("aggregateId", true));
            Assert.fail("Fixture should have failed");
        } catch (AssertionError error) {
            Assert.assertTrue(("Wrong message: " + (error.getMessage())), error.getMessage().contains("considered deleted"));
        }
    }

    @Test
    public void testAndGiven() {
        fixture.registerInjectableResource(new HardToCreateResource());
        fixture.givenCommands(new CreateAggregateCommand("aggregateId")).andGiven(new MyEvent("aggregateId", 1)).when(new TestCommand("aggregateId")).expectEvents(new MyEvent("aggregateId", 2));
    }

    @Test
    public void testAndGivenCommands() {
        fixture.given(new MyEvent("aggregateId", 1)).andGivenCommands(new TestCommand("aggregateId")).when(new TestCommand("aggregateId")).expectEvents(new MyEvent("aggregateId", 3));
    }

    @Test
    public void testMultipleAndGivenCommands() {
        fixture.given(new MyEvent("aggregateId", 1)).andGivenCommands(new TestCommand("aggregateId")).andGivenCommands(new TestCommand("aggregateId")).when(new TestCommand("aggregateId")).expectEvents(new MyEvent("aggregateId", 4));
    }

    private class StubDomainEvent {
        public StubDomainEvent() {
        }
    }
}

