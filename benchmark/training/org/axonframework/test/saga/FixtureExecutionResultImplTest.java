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
package org.axonframework.test.saga;


import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.modelling.saga.AssociationValue;
import org.axonframework.modelling.saga.repository.inmemory.InMemorySagaStore;
import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.deadline.StubDeadlineManager;
import org.axonframework.test.eventscheduler.StubEventScheduler;
import org.axonframework.test.matchers.AllFieldsFilter;
import org.axonframework.test.utils.RecordingCommandBus;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class FixtureExecutionResultImplTest {
    private FixtureExecutionResultImpl<StubSaga> testSubject;

    private RecordingCommandBus commandBus;

    private SimpleEventBus eventBus;

    private StubEventScheduler eventScheduler;

    private StubDeadlineManager deadlineManager;

    private InMemorySagaStore sagaStore;

    private TimerTriggeredEvent applicationEvent;

    private String identifier;

    @Test
    public void testStartRecording() {
        testSubject = new FixtureExecutionResultImpl(sagaStore, eventScheduler, deadlineManager, eventBus, commandBus, StubSaga.class, AllFieldsFilter.instance());
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("First"));
        eventBus.publish(new org.axonframework.eventhandling.GenericEventMessage(new TriggerSagaStartEvent(identifier)));
        testSubject.startRecording();
        TriggerSagaEndEvent endEvent = new TriggerSagaEndEvent(identifier);
        eventBus.publish(new org.axonframework.eventhandling.GenericEventMessage(endEvent));
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("Second"));
        testSubject.expectPublishedEvents(endEvent);
        testSubject.expectPublishedEventsMatching(payloadsMatching(exactSequenceOf(equalTo(endEvent), andNoMore())));
        testSubject.expectDispatchedCommands("Second");
        testSubject.expectDispatchedCommandsMatching(payloadsMatching(exactSequenceOf(equalTo("Second"), andNoMore())));
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectPublishedEvents_WrongCount() {
        eventBus.publish(new org.axonframework.eventhandling.GenericEventMessage(new TriggerSagaEndEvent(identifier)));
        testSubject.expectPublishedEvents(new TriggerSagaEndEvent(identifier), new TriggerExistingSagaEvent(identifier));
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectPublishedEvents_WrongType() {
        eventBus.publish(new org.axonframework.eventhandling.GenericEventMessage(new TriggerSagaEndEvent(identifier)));
        testSubject.expectPublishedEvents(new TriggerExistingSagaEvent(identifier));
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectPublishedEvents_FailedMatcher() {
        eventBus.publish(new org.axonframework.eventhandling.GenericEventMessage(new TriggerSagaEndEvent(identifier)));
        testSubject.expectPublishedEvents(new FixtureExecutionResultImplTest.FailingMatcher<org.axonframework.eventhandling.EventMessage>());
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectDispatchedCommands_FailedCount() {
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("First"));
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("Second"));
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("Third"));
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("Fourth"));
        testSubject.expectDispatchedCommands("First", "Second", "Third");
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectDispatchedCommands_FailedType() {
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("First"));
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("Second"));
        testSubject.expectDispatchedCommands("First", "Third");
    }

    @Test
    public void testExpectDispatchedCommands() {
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("First"));
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("Second"));
        testSubject.expectDispatchedCommands("First", "Second");
    }

    @Test
    public void testExpectDispatchedCommands_ObjectsNotImplementingEquals() {
        commandBus.dispatch(GenericCommandMessage.asCommandMessage(new FixtureExecutionResultImplTest.SimpleCommand("First")));
        commandBus.dispatch(GenericCommandMessage.asCommandMessage(new FixtureExecutionResultImplTest.SimpleCommand("Second")));
        testSubject.expectDispatchedCommands(new FixtureExecutionResultImplTest.SimpleCommand("First"), new FixtureExecutionResultImplTest.SimpleCommand("Second"));
    }

    @Test
    public void testExpectDispatchedCommands_ObjectsNotImplementingEquals_FailedField() {
        commandBus.dispatch(GenericCommandMessage.asCommandMessage(new FixtureExecutionResultImplTest.SimpleCommand("First")));
        commandBus.dispatch(GenericCommandMessage.asCommandMessage(new FixtureExecutionResultImplTest.SimpleCommand("Second")));
        try {
            testSubject.expectDispatchedCommands(new FixtureExecutionResultImplTest.SimpleCommand("Second"), new FixtureExecutionResultImplTest.SimpleCommand("Thrid"));
            Assert.fail("Expected exception");
        } catch (AxonAssertionError e) {
            Assert.assertTrue(("Wrong message: " + (e.getMessage())), e.getMessage().contains("expected <Second>"));
        }
    }

    @Test
    public void testExpectDispatchedCommands_ObjectsNotImplementingEquals_WrongType() {
        commandBus.dispatch(GenericCommandMessage.asCommandMessage(new FixtureExecutionResultImplTest.SimpleCommand("First")));
        commandBus.dispatch(GenericCommandMessage.asCommandMessage(new FixtureExecutionResultImplTest.SimpleCommand("Second")));
        try {
            testSubject.expectDispatchedCommands("Second", new FixtureExecutionResultImplTest.SimpleCommand("Thrid"));
            Assert.fail("Expected exception");
        } catch (AxonAssertionError e) {
            Assert.assertTrue(("Wrong message: " + (e.getMessage())), e.getMessage().contains("Expected <String>"));
        }
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectNoDispatchedCommands_Failed() {
        commandBus.dispatch(GenericCommandMessage.asCommandMessage("First"));
        testSubject.expectNoDispatchedCommands();
    }

    @Test
    public void testExpectNoDispatchedCommands() {
        testSubject.expectNoDispatchedCommands();
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectDispatchedCommands_FailedMatcher() {
        testSubject.expectDispatchedCommands(new FixtureExecutionResultImplTest.FailingMatcher<String>());
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectNoScheduledEvents_EventIsScheduled() {
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(applicationEvent));
        testSubject.expectNoScheduledEvents();
    }

    @Test
    public void testExpectNoScheduledEvents_NoEventScheduled() {
        testSubject.expectNoScheduledEvents();
    }

    @Test
    public void testExpectNoScheduledEvents_ScheduledEventIsTriggered() {
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(applicationEvent));
        eventScheduler.advanceToNextTrigger();
        testSubject.expectNoScheduledEvents();
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectScheduledEvent_WrongDateTime() {
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(applicationEvent));
        eventScheduler.advanceTimeBy(Duration.ofMillis(500), ( i) -> {
        });
        testSubject.expectScheduledEvent(Duration.ofSeconds(1), applicationEvent);
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectScheduledEvent_WrongClass() {
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(applicationEvent));
        eventScheduler.advanceTimeBy(Duration.ofMillis(500), ( i) -> {
        });
        testSubject.expectScheduledEventOfType(Duration.ofSeconds(1), Object.class);
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectScheduledEvent_WrongEvent() {
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(applicationEvent));
        eventScheduler.advanceTimeBy(Duration.ofMillis(500), ( i) -> {
        });
        testSubject.expectScheduledEvent(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(new TimerTriggeredEvent("unexpected")));
    }

    @SuppressWarnings({ "unchecked" })
    @Test(expected = AxonAssertionError.class)
    public void testExpectScheduledEvent_FailedMatcher() {
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(applicationEvent));
        eventScheduler.advanceTimeBy(Duration.ofMillis(500), ( i) -> {
        });
        testSubject.expectScheduledEvent(Duration.ofSeconds(1), new FixtureExecutionResultImplTest.FailingMatcher());
    }

    @Test
    public void testExpectScheduledEvent_Found() {
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(applicationEvent));
        eventScheduler.advanceTimeBy(Duration.ofMillis(500), ( i) -> {
        });
        testSubject.expectScheduledEvent(Duration.ofMillis(500), applicationEvent);
    }

    @Test
    public void testExpectScheduledEvent_FoundInMultipleCandidates() {
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(new TimerTriggeredEvent("unexpected1")));
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(applicationEvent));
        eventScheduler.schedule(Duration.ofSeconds(1), new org.axonframework.eventhandling.GenericEventMessage(new TimerTriggeredEvent("unexpected2")));
        testSubject.expectScheduledEvent(Duration.ofSeconds(1), applicationEvent);
    }

    @Test(expected = AxonAssertionError.class)
    public void testAssociationWith_WrongValue() {
        sagaStore.insertSaga(StubSaga.class, "test", new StubSaga(), Collections.singleton(new AssociationValue("key", "value")));
        testSubject.expectAssociationWith("key", "value2");
    }

    @Test(expected = AxonAssertionError.class)
    public void testAssociationWith_WrongKey() {
        sagaStore.insertSaga(StubSaga.class, "test", new StubSaga(), Collections.singleton(new AssociationValue("key", "value")));
        testSubject.expectAssociationWith("key2", "value");
    }

    @Test
    public void testAssociationWith_Present() {
        sagaStore.insertSaga(StubSaga.class, "test", new StubSaga(), Collections.singleton(new AssociationValue("key", "value")));
        testSubject.expectAssociationWith("key", "value");
    }

    @Test
    public void testNoAssociationWith_WrongValue() {
        sagaStore.insertSaga(StubSaga.class, "test", new StubSaga(), Collections.singleton(new AssociationValue("key", "value")));
        testSubject.expectNoAssociationWith("key", "value2");
    }

    @Test
    public void testNoAssociationWith_WrongKey() {
        sagaStore.insertSaga(StubSaga.class, "test", new StubSaga(), Collections.singleton(new AssociationValue("key", "value")));
        testSubject.expectNoAssociationWith("key2", "value");
    }

    @Test(expected = AxonAssertionError.class)
    public void testNoAssociationWith_Present() {
        sagaStore.insertSaga(StubSaga.class, "test", new StubSaga(), Collections.singleton(new AssociationValue("key", "value")));
        testSubject.expectNoAssociationWith("key", "value");
    }

    @Test(expected = AxonAssertionError.class)
    public void testExpectActiveSagas_WrongCount() {
        sagaStore.insertSaga(StubSaga.class, "test", new StubSaga(), Collections.emptySet());
        testSubject.expectActiveSagas(2);
    }

    @Test
    public void testExpectActiveSagas_CorrectCount() {
        sagaStore.insertSaga(StubSaga.class, "test", new StubSaga(), Collections.emptySet());
        sagaStore.deleteSaga(StubSaga.class, "test", Collections.emptySet());
        sagaStore.insertSaga(StubSaga.class, "test2", new StubSaga(), Collections.emptySet());
        testSubject.expectActiveSagas(1);
    }

    @Test
    public void testStartRecordingCallback() {
        AtomicInteger startRecordingCallbackInvocations = new AtomicInteger();
        testSubject.registerStartRecordingCallback(startRecordingCallbackInvocations::incrementAndGet);
        testSubject.startRecording();
        Assert.assertThat(startRecordingCallbackInvocations.get(), equalTo(1));
    }

    private static class SimpleCommand {
        private final String content;

        public SimpleCommand(String content) {
            this.content = content;
        }
    }

    private class FailingMatcher<T> extends BaseMatcher<List<? extends T>> {
        @Override
        public boolean matches(Object item) {
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("something you'll never be able to deliver");
        }
    }
}

