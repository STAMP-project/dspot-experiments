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
package org.axonframework.test.saga;


import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.test.matchers.Matchers;
import org.axonframework.test.utils.CallbackBehavior;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class AnnotatedSagaTest {
    @Test
    public void testFixtureApi_WhenEventOccurs() {
        String aggregate1 = UUID.randomUUID().toString();
        String aggregate2 = UUID.randomUUID().toString();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        FixtureExecutionResult validator = fixture.givenAggregate(aggregate1).published(GenericEventMessage.asEventMessage(new TriggerSagaStartEvent(aggregate1)), new TriggerExistingSagaEvent(aggregate1)).andThenAggregate(aggregate2).published(new TriggerSagaStartEvent(aggregate2)).whenAggregate(aggregate1).publishes(new TriggerSagaEndEvent(aggregate1));
        validator.expectActiveSagas(1);
        validator.expectAssociationWith("identifier", aggregate2);
        validator.expectNoAssociationWith("identifier", aggregate1);
        validator.expectScheduledEventOfType(Duration.ofMinutes(10), TimerTriggeredEvent.class);
        validator.expectScheduledEventMatching(Duration.ofMinutes(10), messageWithPayload(CoreMatchers.any(TimerTriggeredEvent.class)));
        validator.expectScheduledEvent(Duration.ofMinutes(10), new TimerTriggeredEvent(aggregate1));
        validator.expectScheduledEventOfType(fixture.currentTime().plusSeconds(600), TimerTriggeredEvent.class);
        validator.expectScheduledEventMatching(fixture.currentTime().plusSeconds(600), messageWithPayload(CoreMatchers.any(TimerTriggeredEvent.class)));
        validator.expectScheduledEvent(fixture.currentTime().plusSeconds(600), new TimerTriggeredEvent(aggregate1));
        validator.expectDispatchedCommands();
        validator.expectNoDispatchedCommands();
        validator.expectPublishedEventsMatching(noEvents());
        validator.expectNoScheduledDeadlines();
    }

    @Test
    public void testFixtureApi_AggregatePublishedEvent_NoHistoricActivity() {
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.givenNoPriorActivity().whenAggregate("id").publishes(new TriggerSagaStartEvent("id")).expectActiveSagas(1).expectNoScheduledDeadlines().expectAssociationWith("identifier", "id");
    }

    @Test
    public void testFixtureApi_AggregatePublishedEventWithMetaData_NoHistoricActivity() {
        String extraIdentifier = UUID.randomUUID().toString();
        Map<String, String> metaData = new HashMap<>();
        metaData.put("extraIdentifier", extraIdentifier);
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.givenNoPriorActivity().whenAggregate("id").publishes(new TriggerSagaStartEvent("id"), metaData).expectActiveSagas(1).expectNoScheduledDeadlines().expectAssociationWith("identifier", "id").expectAssociationWith("extraIdentifier", extraIdentifier);
    }

    @Test
    public void testFixtureApi_NonTransientResourceInjected() {
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.registerResource(new NonTransientResource());
        fixture.givenNoPriorActivity();
        try {
            fixture.whenAggregate("id").publishes(new TriggerSagaStartEvent("id")).expectNoScheduledDeadlines();
            Assert.fail("Expected error");
        } catch (AssertionError e) {
            Assert.assertTrue(("Got unexpected error: " + (e.getMessage())), e.getMessage().contains("StubSaga.nonTransientResource"));
            Assert.assertTrue(("Got unexpected error: " + (e.getMessage())), e.getMessage().contains("transient"));
        }
    }

    @Test
    public void testFixtureApi_NonTransientResourceInjected_CheckDisabled() {
        FixtureConfiguration fixture = new SagaTestFixture(StubSaga.class).withTransienceCheckDisabled();
        fixture.registerResource(new NonTransientResource());
        fixture.givenNoPriorActivity().whenAggregate("id").publishes(new TriggerSagaStartEvent("id")).expectNoScheduledDeadlines();
    }

    // testing issue AXON-279
    @Test
    public void testFixtureApi_PublishedEvent_NoHistoricActivity() {
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.givenNoPriorActivity().whenPublishingA(new GenericEventMessage(new TriggerSagaStartEvent("id"))).expectActiveSagas(1).expectAssociationWith("identifier", "id").expectNoScheduledDeadlines();
    }

    @Test
    public void testFixtureApi_PublishedEventWithMetaData_NoHistoricActivity() {
        String extraIdentifier = UUID.randomUUID().toString();
        Map<String, String> metaData = new HashMap<>();
        metaData.put("extraIdentifier", extraIdentifier);
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.givenNoPriorActivity().whenPublishingA(new TriggerSagaStartEvent("id"), metaData).expectActiveSagas(1).expectAssociationWith("identifier", "id").expectAssociationWith("extraIdentifier", extraIdentifier).expectNoScheduledDeadlines();
    }

    @Test
    public void testFixtureApi_WithApplicationEvents() throws Exception {
        String aggregate1 = UUID.randomUUID().toString();
        String aggregate2 = UUID.randomUUID().toString();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.givenAPublished(new TimerTriggeredEvent(UUID.randomUUID().toString())).andThenAPublished(new TimerTriggeredEvent(UUID.randomUUID().toString())).whenPublishingA(new TimerTriggeredEvent(UUID.randomUUID().toString())).expectActiveSagas(0).expectNoAssociationWith("identifier", aggregate2).expectNoAssociationWith("identifier", aggregate1).expectNoScheduledEvents().expectNoScheduledDeadlines().expectDispatchedCommands().expectPublishedEvents();
    }

    @Test
    public void testFixtureApi_WhenEventIsPublishedToEventBus() {
        String aggregate1 = UUID.randomUUID().toString();
        String aggregate2 = UUID.randomUUID().toString();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        FixtureExecutionResult validator = fixture.givenAggregate(aggregate1).published(new TriggerSagaStartEvent(aggregate1), new TriggerExistingSagaEvent(aggregate1)).whenAggregate(aggregate1).publishes(new TriggerExistingSagaEvent(aggregate1));
        validator.expectActiveSagas(1);
        validator.expectAssociationWith("identifier", aggregate1);
        validator.expectNoAssociationWith("identifier", aggregate2);
        validator.expectScheduledEventMatching(Duration.ofMinutes(10), Matchers.messageWithPayload(CoreMatchers.any(Object.class)));
        validator.expectDispatchedCommands();
        validator.expectPublishedEventsMatching(listWithAnyOf(messageWithPayload(CoreMatchers.any(SagaWasTriggeredEvent.class))));
        validator.expectNoScheduledDeadlines();
    }

    @Test
    public void testFixtureApi_ElapsedTimeBetweenEventsHasEffectOnScheduler() throws Exception {
        String aggregate1 = UUID.randomUUID().toString();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        FixtureExecutionResult validator = // when time shifts to t0+10
        // reset event schedules a TriggerEvent after 10 minutes from t0+5
        // time shifts to t0+5
        // event schedules a TriggerEvent after 10 minutes from t0
        fixture.givenAggregate(aggregate1).published(new TriggerSagaStartEvent(aggregate1)).andThenTimeElapses(Duration.ofMinutes(5)).andThenAggregate(aggregate1).published(new ResetTriggerEvent(aggregate1)).whenTimeElapses(Duration.ofMinutes(6));
        validator.expectActiveSagas(1);
        validator.expectAssociationWith("identifier", aggregate1);
        // 6 minutes have passed since the 10minute timer was reset,
        // so expect the timer to be scheduled for 4 minutes (t0 + 15)
        validator.expectScheduledEventMatching(Duration.ofMinutes(4), Matchers.messageWithPayload(CoreMatchers.any(Object.class)));
        validator.expectNoDispatchedCommands();
        validator.expectPublishedEvents();
        validator.expectNoScheduledDeadlines();
    }

    @Test
    public void testFixtureApi_WhenTimeElapses_UsingMockGateway() {
        String identifier = UUID.randomUUID().toString();
        String identifier2 = UUID.randomUUID().toString();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        final StubGateway gateway = Mockito.mock(StubGateway.class);
        fixture.registerCommandGateway(StubGateway.class, gateway);
        Mockito.when(gateway.send(ArgumentMatchers.eq("Say hi!"))).thenReturn("Hi again!");
        fixture.givenAggregate(identifier).published(new TriggerSagaStartEvent(identifier)).andThenAggregate(identifier2).published(new TriggerExistingSagaEvent(identifier2)).whenTimeElapses(Duration.ofMinutes(35)).expectActiveSagas(1).expectAssociationWith("identifier", identifier).expectNoAssociationWith("identifier", identifier2).expectNoScheduledEvents().expectNoScheduledDeadlines().expectDispatchedCommands("Say hi!", "Hi again!").expectPublishedEventsMatching(noEvents());
        Mockito.verify(gateway).send("Say hi!");
        Mockito.verify(gateway).send("Hi again!");
    }

    @Test
    public void testSchedulingEventsAsMessage() {
        UUID identifier = UUID.randomUUID();
        SagaTestFixture fixture = new SagaTestFixture(StubSaga.class);
        fixture.registerCommandGateway(StubGateway.class);
        // this will create a message with a timestamp from the real time. It should be converted to fixture-time
        fixture.givenNoPriorActivity().whenPublishingA(GenericEventMessage.asEventMessage(new TriggerSagaStartEvent(identifier.toString()))).expectScheduledEventOfType(Duration.ofMinutes(10), TimerTriggeredEvent.class).expectNoScheduledDeadlines();
    }

    @Test
    public void testSchedulingEventsAsDomainEventMessage() {
        UUID identifier = UUID.randomUUID();
        SagaTestFixture fixture = new SagaTestFixture(StubSaga.class);
        fixture.registerCommandGateway(StubGateway.class);
        // this will create a message with a timestamp from the real time. It should be converted to fixture-time
        fixture.givenNoPriorActivity().whenAggregate(UUID.randomUUID().toString()).publishes(GenericEventMessage.asEventMessage(new TriggerSagaStartEvent(identifier.toString()))).expectScheduledEventOfType(Duration.ofMinutes(10), TimerTriggeredEvent.class).expectNoScheduledDeadlines();
    }

    @Test
    public void testScheduledEventsInPastAsDomainEventMessage() {
        UUID identifier = UUID.randomUUID();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.registerCommandGateway(StubGateway.class);
        fixture.givenAggregate(UUID.randomUUID().toString()).published(GenericEventMessage.asEventMessage(new TriggerSagaStartEvent(identifier.toString()))).whenTimeElapses(Duration.ofMinutes(1)).expectScheduledEventOfType(Duration.ofMinutes(9), TimerTriggeredEvent.class).expectNoScheduledDeadlines();
    }

    @Test
    public void testScheduledEventsInPastAsEventMessage() {
        UUID identifier = UUID.randomUUID();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.registerCommandGateway(StubGateway.class);
        fixture.givenAPublished(GenericEventMessage.asEventMessage(new TriggerSagaStartEvent(identifier.toString()))).whenTimeElapses(Duration.ofMinutes(1)).expectScheduledEventOfType(Duration.ofMinutes(9), TimerTriggeredEvent.class).expectNoScheduledDeadlines();
    }

    @Test
    public void testFixtureApi_givenCurrentTime() {
        String identifier = UUID.randomUUID().toString();
        Instant fourDaysAgo = Instant.now().minus(4, ChronoUnit.DAYS);
        Instant fourDaysMinusTenMinutesAgo = fourDaysAgo.plus(10, ChronoUnit.MINUTES);
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.givenCurrentTime(fourDaysAgo).whenPublishingA(new TriggerSagaStartEvent(identifier)).expectScheduledEvent(fourDaysMinusTenMinutesAgo, new TimerTriggeredEvent(identifier)).expectNoScheduledDeadlines();
    }

    @Test
    public void testFixtureApi_WhenTimeElapses_UsingDefaults() {
        String identifier = UUID.randomUUID().toString();
        String identifier2 = UUID.randomUUID().toString();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.registerCommandGateway(StubGateway.class);
        // since we return null for the command, the other is never sent...
        fixture.givenAggregate(identifier).published(new TriggerSagaStartEvent(identifier)).andThenAggregate(identifier2).published(new TriggerExistingSagaEvent(identifier2)).whenTimeElapses(Duration.ofMinutes(35)).expectActiveSagas(1).expectAssociationWith("identifier", identifier).expectNoAssociationWith("identifier", identifier2).expectNoScheduledEvents().expectNoScheduledDeadlines().expectDispatchedCommands("Say hi!").expectPublishedEventsMatching(noEvents());
    }

    @Test
    public void testFixtureApi_WhenTimeElapses_UsingCallbackBehavior() throws Exception {
        String identifier = UUID.randomUUID().toString();
        String identifier2 = UUID.randomUUID().toString();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        CallbackBehavior commandHandler = Mockito.mock(CallbackBehavior.class);
        Mockito.when(commandHandler.handle(ArgumentMatchers.eq("Say hi!"), ArgumentMatchers.isA(MetaData.class))).thenReturn("Hi again!");
        fixture.setCallbackBehavior(commandHandler);
        fixture.registerCommandGateway(StubGateway.class);
        fixture.givenAggregate(identifier).published(new TriggerSagaStartEvent(identifier)).andThenAggregate(identifier2).published(new TriggerExistingSagaEvent(identifier2)).whenTimeElapses(Duration.ofMinutes(35)).expectActiveSagas(1).expectAssociationWith("identifier", identifier).expectNoAssociationWith("identifier", identifier2).expectNoScheduledEvents().expectNoScheduledDeadlines().expectDispatchedCommands("Say hi!", "Hi again!").expectPublishedEventsMatching(noEvents());
        Mockito.verify(commandHandler, Mockito.times(2)).handle(ArgumentMatchers.isA(Object.class), ArgumentMatchers.eq(MetaData.emptyInstance()));
    }

    @Test
    public void testFixtureApi_WhenTimeAdvances() {
        String identifier = UUID.randomUUID().toString();
        String identifier2 = UUID.randomUUID().toString();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.registerCommandGateway(StubGateway.class);
        fixture.givenAggregate(identifier).published(new TriggerSagaStartEvent(identifier)).andThenAggregate(identifier2).published(new TriggerExistingSagaEvent(identifier2)).whenTimeAdvancesTo(Instant.now().plus(Duration.ofDays(1))).expectActiveSagas(1).expectAssociationWith("identifier", identifier).expectNoAssociationWith("identifier", identifier2).expectNoScheduledEvents().expectNoScheduledDeadlines().expectDispatchedCommands("Say hi!");
    }

    @Test
    public void testLastResourceEvaluatedFirst() {
        String identifier = UUID.randomUUID().toString();
        String identifier2 = UUID.randomUUID().toString();
        SagaTestFixture<StubSaga> fixture = new SagaTestFixture(StubSaga.class);
        fixture.registerCommandGateway(StubGateway.class);
        StubGateway mock = Mockito.mock(StubGateway.class);
        fixture.registerCommandGateway(StubGateway.class, mock);
        fixture.givenAggregate(identifier).published(new TriggerSagaStartEvent(identifier)).andThenAggregate(identifier2).published(new TriggerExistingSagaEvent(identifier2)).whenTimeAdvancesTo(Instant.now().plus(Duration.ofDays(1))).expectActiveSagas(1).expectAssociationWith("identifier", identifier).expectNoAssociationWith("identifier", identifier2).expectNoScheduledEvents().expectNoScheduledDeadlines().expectDispatchedCommands("Say hi!");
        Mockito.verify(mock).send(ArgumentMatchers.anyString());
    }
}

