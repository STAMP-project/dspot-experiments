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
package org.axonframework.eventhandling;


import UnitOfWork.Phase;
import UnitOfWork.Phase.COMMIT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import junit.framework.TestCase;
import org.axonframework.common.Registration;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static AbstractEventBus.Builder.<init>;


/**
 *
 *
 * @author Rene de Waele
 */
public class AbstractEventBusTest {
    private UnitOfWork<?> unitOfWork;

    private AbstractEventBusTest.StubPublishingEventBus testSubject;

    @Test
    public void testConsumersRegisteredWithUnitOfWorkWhenFirstEventIsPublished() {
        EventMessage<?> event = AbstractEventBusTest.newEvent();
        testSubject.publish(event);
        Mockito.verify(unitOfWork).onPrepareCommit(ArgumentMatchers.any());
        Mockito.verify(unitOfWork).onCommit(ArgumentMatchers.any());
        // the monitor callback is also registered
        Mockito.verify(unitOfWork, Mockito.times(2)).afterCommit(ArgumentMatchers.any());
        TestCase.assertEquals(Collections.emptyList(), testSubject.committedEvents);
        unitOfWork.commit();
        TestCase.assertEquals(Collections.singletonList(event), testSubject.committedEvents);
    }

    @Test
    public void testNoMoreConsumersRegisteredWithUnitOfWorkWhenSecondEventIsPublished() {
        EventMessage<?> event = AbstractEventBusTest.newEvent();
        testSubject.publish(event);
        Mockito.verify(unitOfWork).onPrepareCommit(ArgumentMatchers.any());
        Mockito.verify(unitOfWork).onCommit(ArgumentMatchers.any());
        // the monitor callback is also registered
        Mockito.verify(unitOfWork, Mockito.times(2)).afterCommit(ArgumentMatchers.any());
        Mockito.reset(unitOfWork);
        testSubject.publish(event);
        Mockito.verify(unitOfWork, Mockito.never()).onPrepareCommit(ArgumentMatchers.any());
        Mockito.verify(unitOfWork, Mockito.never()).onCommit(ArgumentMatchers.any());
        // the monitor callback should still be registered
        Mockito.verify(unitOfWork).afterCommit(ArgumentMatchers.any());
        unitOfWork.commit();
        List<EventMessage<?>> actual = testSubject.committedEvents;
        TestCase.assertEquals(Arrays.asList(event, event), actual);
    }

    @Test
    public void testCommitOnUnitOfWork() {
        EventMessage<?> event = AbstractEventBusTest.newEvent();
        testSubject.publish(event);
        unitOfWork.commit();
        TestCase.assertEquals(Collections.singletonList(event), testSubject.committedEvents);
    }

    @Test
    public void testPublicationOrder() {
        EventMessage<?> eventA = AbstractEventBusTest.newEvent();
        EventMessage<?> eventB = AbstractEventBusTest.newEvent();
        testSubject.publish(eventA);
        testSubject.publish(eventB);
        unitOfWork.commit();
        TestCase.assertEquals(Arrays.asList(eventA, eventB), testSubject.committedEvents);
    }

    @Test
    public void testPublicationWithNestedUow() {
        testSubject.publish(AbstractEventBusTest.numberedEvent(5));
        unitOfWork.commit();
        TestCase.assertEquals(Arrays.asList(AbstractEventBusTest.numberedEvent(5), AbstractEventBusTest.numberedEvent(4), AbstractEventBusTest.numberedEvent(3), AbstractEventBusTest.numberedEvent(2), AbstractEventBusTest.numberedEvent(1), AbstractEventBusTest.numberedEvent(0)), testSubject.committedEvents);
        Mockito.verify(testSubject, Mockito.times(6)).prepareCommit(ArgumentMatchers.any());
        Mockito.verify(testSubject, Mockito.times(6)).commit(ArgumentMatchers.any());
        Mockito.verify(testSubject, Mockito.times(6)).afterCommit(ArgumentMatchers.any());
        // each UoW will register onPrepareCommit on the parent
        Mockito.verify(unitOfWork, Mockito.times(1)).onPrepareCommit(ArgumentMatchers.any());
        // each UoW will register onCommit with the root
        Mockito.verify(unitOfWork, Mockito.times(6)).onCommit(ArgumentMatchers.any());
    }

    @Test(expected = IllegalStateException.class)
    public void testPublicationForbiddenDuringUowCommitPhase() {
        AbstractEventBusTest.StubPublishingEventBus.builder().publicationPhase(COMMIT).startNewUowBeforePublishing(false).build().publish(AbstractEventBusTest.numberedEvent(5));
        unitOfWork.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void testPublicationForbiddenDuringRootUowCommitPhase() {
        testSubject = Mockito.spy(AbstractEventBusTest.StubPublishingEventBus.builder().publicationPhase(COMMIT).build());
        testSubject.publish(AbstractEventBusTest.numberedEvent(1));
        unitOfWork.commit();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDispatchInterceptor() {
        MessageDispatchInterceptor<EventMessage<?>> dispatchInterceptorMock = Mockito.mock(MessageDispatchInterceptor.class);
        String key = "additional";
        String value = "metaData";
        Mockito.when(dispatchInterceptorMock.handle(ArgumentMatchers.anyList())).thenAnswer(( invocation) -> {
            List<EventMessage<?>> eventMessages = ((List<EventMessage<?>>) (invocation.getArguments()[0]));
            return ((BiFunction<Integer, Object, Object>) (( index, message) -> {
                if (eventMessages.get(index).getMetaData().containsKey(key)) {
                    throw new AssertionError("MessageProcessor is asked to process the same event message twice");
                }
                return eventMessages.get(index).andMetaData(Collections.singletonMap(key, value));
            }));
        });
        testSubject.registerDispatchInterceptor(dispatchInterceptorMock);
        testSubject.publish(AbstractEventBusTest.newEvent(), AbstractEventBusTest.newEvent());
        Mockito.verifyZeroInteractions(dispatchInterceptorMock);
        unitOfWork.commit();
        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(dispatchInterceptorMock).handle(argumentCaptor.capture());// prepare commit, commit, and after commit

        TestCase.assertEquals(1, argumentCaptor.getAllValues().size());
        TestCase.assertEquals(2, argumentCaptor.getValue().size());
        TestCase.assertEquals(value, ((EventMessage<?>) (argumentCaptor.getValue().get(0))).getMetaData().get(key));
    }

    private static class StubPublishingEventBus extends AbstractEventBus {
        private final List<EventMessage<?>> committedEvents = new ArrayList<>();

        private final Phase publicationPhase;

        private final boolean startNewUowBeforePublishing;

        private StubPublishingEventBus(AbstractEventBusTest.StubPublishingEventBus.Builder builder) {
            super(builder);
            this.publicationPhase = builder.publicationPhase;
            this.startNewUowBeforePublishing = builder.startNewUowBeforePublishing;
        }

        private static AbstractEventBusTest.StubPublishingEventBus.Builder builder() {
            return new AbstractEventBusTest.StubPublishingEventBus.Builder();
        }

        @Override
        protected void prepareCommit(List<? extends EventMessage<?>> events) {
            if ((publicationPhase) == (Phase.PREPARE_COMMIT)) {
                onEvents(events);
            }
        }

        @Override
        protected void commit(List<? extends EventMessage<?>> events) {
            if ((publicationPhase) == (Phase.COMMIT)) {
                onEvents(events);
            }
        }

        @Override
        protected void afterCommit(List<? extends EventMessage<?>> events) {
            if ((publicationPhase) == (Phase.AFTER_COMMIT)) {
                onEvents(events);
            }
        }

        private void onEvents(List<? extends EventMessage<?>> events) {
            // if the event payload is a number > 0, a new number is published that is 1 smaller than the first number
            Object payload = getPayload();
            if (payload instanceof Integer) {
                int number = ((int) (payload));
                if (number > 0) {
                    EventMessage nextEvent = AbstractEventBusTest.numberedEvent((number - 1));
                    if (startNewUowBeforePublishing) {
                        UnitOfWork<?> nestedUnitOfWork = DefaultUnitOfWork.startAndGet(null);
                        try {
                            publish(nextEvent);
                        } finally {
                            nestedUnitOfWork.commit();
                        }
                    } else {
                        publish(nextEvent);
                    }
                }
            }
            committedEvents.addAll(events);
        }

        @Override
        public Registration subscribe(Consumer<List<? extends EventMessage<?>>> eventProcessor) {
            throw new UnsupportedOperationException();
        }

        private static class Builder extends AbstractEventBus.Builder {
            private Phase publicationPhase = Phase.PREPARE_COMMIT;

            private boolean startNewUowBeforePublishing = true;

            private AbstractEventBusTest.StubPublishingEventBus.Builder publicationPhase(UnitOfWork.Phase publicationPhase) {
                this.publicationPhase = publicationPhase;
                return this;
            }

            private AbstractEventBusTest.StubPublishingEventBus.Builder startNewUowBeforePublishing(boolean startNewUowBeforePublishing) {
                this.startNewUowBeforePublishing = startNewUowBeforePublishing;
                return this;
            }

            private AbstractEventBusTest.StubPublishingEventBus build() {
                return new AbstractEventBusTest.StubPublishingEventBus(this);
            }
        }
    }

    private static class StubNumberedEvent extends GenericEventMessage<Integer> {
        public StubNumberedEvent(Integer payload) {
            super(payload);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            AbstractEventBusTest.StubNumberedEvent that = ((AbstractEventBusTest.StubNumberedEvent) (o));
            return Objects.equals(getPayload(), that.getPayload());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getPayload());
        }

        @Override
        public String toString() {
            return ("StubNumberedEvent{" + (getPayload())) + "}";
        }
    }
}

