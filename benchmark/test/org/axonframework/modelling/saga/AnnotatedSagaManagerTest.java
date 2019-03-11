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
package org.axonframework.modelling.saga;


import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.annotation.MetaDataValue;
import org.axonframework.modelling.saga.repository.AnnotatedSagaRepository;
import org.axonframework.modelling.saga.repository.inmemory.InMemorySagaStore;
import org.axonframework.modelling.utils.StubDomainEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class AnnotatedSagaManagerTest {
    private AnnotatedSagaRepository<AnnotatedSagaManagerTest.MyTestSaga> sagaRepository;

    private AnnotatedSagaManager<AnnotatedSagaManagerTest.MyTestSaga> manager;

    private InMemorySagaStore sagaStore;

    @Test
    public void testCreationPolicy_NoneExists() throws Exception {
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.StartingEvent("123")));
        Assert.assertEquals(1, repositoryContents("123").size());
    }

    @Test
    public void testCreationPolicy_OneAlreadyExists() throws Exception {
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.StartingEvent("123")));
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.StartingEvent("123")));
        Assert.assertEquals(1, repositoryContents("123").size());
    }

    @Test
    public void testHandleUnrelatedEvent() throws Exception {
        handle(new org.axonframework.eventhandling.GenericEventMessage("Unrelated"));
        Mockito.verify(sagaRepository, Mockito.never()).find(ArgumentMatchers.isNull(AssociationValue.class));
    }

    @Test
    public void testCreationPolicy_CreationForced() throws Exception {
        AnnotatedSagaManagerTest.StartingEvent startingEvent = new AnnotatedSagaManagerTest.StartingEvent("123");
        handle(new org.axonframework.eventhandling.GenericEventMessage(startingEvent));
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.ForcingStartEvent("123")));
        Collection<AnnotatedSagaManagerTest.MyTestSaga> sagas = repositoryContents("123");
        Assert.assertEquals(2, sagas.size());
        for (AnnotatedSagaManagerTest.MyTestSaga saga : sagas) {
            if (saga.getCapturedEvents().contains(startingEvent)) {
                Assert.assertEquals(2, saga.getCapturedEvents().size());
            }
            Assert.assertTrue(((saga.getCapturedEvents().size()) >= 1));
        }
    }

    @Test
    public void testCreationPolicy_SagaNotCreated() throws Exception {
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.MiddleEvent("123")));
        Assert.assertEquals(0, repositoryContents("123").size());
    }

    @Test
    public void testMostSpecificHandlerEvaluatedFirst() throws Exception {
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.StartingEvent("12")));
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.StartingEvent("23")));
        Assert.assertEquals(1, repositoryContents("12").size());
        Assert.assertEquals(1, repositoryContents("23").size());
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.MiddleEvent("12")));
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.MiddleEvent("23"), Collections.singletonMap("catA", "value")));
        Assert.assertEquals(0, repositoryContents("12").iterator().next().getSpecificHandlerInvocations());
        Assert.assertEquals(1, repositoryContents("23").iterator().next().getSpecificHandlerInvocations());
    }

    @Test
    public void testNullAssociationValueIsIgnored() throws Exception {
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.StartingEvent(null)));
        Mockito.verify(sagaRepository, Mockito.never()).find(null);
    }

    @Test
    public void testLifecycle_DestroyedOnEnd() throws Exception {
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.StartingEvent("12")));
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.StartingEvent("23")));
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.MiddleEvent("12")));
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.MiddleEvent("23"), Collections.singletonMap("catA", "value")));
        Assert.assertEquals(1, repositoryContents("12").size());
        Assert.assertEquals(1, repositoryContents("23").size());
        Assert.assertEquals(0, repositoryContents("12").iterator().next().getSpecificHandlerInvocations());
        Assert.assertEquals(1, repositoryContents("23").iterator().next().getSpecificHandlerInvocations());
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.EndingEvent("12")));
        Assert.assertEquals(1, repositoryContents("23").size());
        Assert.assertEquals(0, repositoryContents("12").size());
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.EndingEvent("23")));
        Assert.assertEquals(0, repositoryContents("23").size());
        Assert.assertEquals(0, repositoryContents("12").size());
    }

    @Test
    public void testNullAssociationValueDoesNotThrowNullPointer() throws Exception {
        handle(asEventMessage(new AnnotatedSagaManagerTest.StartingEvent(null)));
    }

    @Test
    public void testLifeCycle_ExistingInstanceIgnoresEvent() throws Exception {
        handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaManagerTest.StartingEvent("12")));
        handle(new org.axonframework.eventhandling.GenericEventMessage(new StubDomainEvent()));
        Assert.assertEquals(1, repositoryContents("12").size());
        Assert.assertEquals(1, repositoryContents("12").iterator().next().getCapturedEvents().size());
    }

    @Test
    public void testLifeCycle_IgnoredEventDoesNotCreateInstance() throws Exception {
        handle(new org.axonframework.eventhandling.GenericEventMessage(new StubDomainEvent()));
        Assert.assertEquals(0, repositoryContents("12").size());
    }

    public static class MyTestSaga {
        private static final long serialVersionUID = -1562911263884220240L;

        private List<Object> capturedEvents = new LinkedList<>();

        private int specificHandlerInvocations = 0;

        @CustomStartingSagaEventHandler
        public void handleSomeEvent(AnnotatedSagaManagerTest.StartingEvent event) {
            capturedEvents.add(event);
        }

        @StartSaga
        @SagaEventHandler(associationProperty = "myIdentifier")
        public void handleSomeEvent(AnnotatedSagaManagerTest.SlowStartingEvent event) throws InterruptedException {
            event.getStartCdl().countDown();
            capturedEvents.add(event);
            Thread.sleep(event.getDuration());
        }

        @StartSaga(forceNew = true)
        @SagaEventHandler(associationProperty = "myIdentifier")
        public void handleSomeEvent(AnnotatedSagaManagerTest.ForcingStartEvent event) {
            capturedEvents.add(event);
        }

        @CustomEndingSagaEventHandler
        public void handleSomeEvent(AnnotatedSagaManagerTest.EndingEvent event) {
            capturedEvents.add(event);
        }

        @SagaEventHandler(associationProperty = "myIdentifier")
        public void handleMiddleEvent(AnnotatedSagaManagerTest.MiddleEvent event) {
            capturedEvents.add(event);
        }

        @SagaEventHandler(associationProperty = "myIdentifier")
        public void handleSpecificMiddleEvent(AnnotatedSagaManagerTest.MiddleEvent event, @MetaDataValue(value = "catA", required = true)
        String category) {
            // this handler is more specific, but requires meta data that not all events might have
            capturedEvents.add(event);
            (specificHandlerInvocations)++;
        }

        public List<Object> getCapturedEvents() {
            return capturedEvents;
        }

        public int getSpecificHandlerInvocations() {
            return specificHandlerInvocations;
        }
    }

    public abstract static class MyIdentifierEvent {
        private String myIdentifier;

        protected MyIdentifierEvent(String myIdentifier) {
            this.myIdentifier = myIdentifier;
        }

        public String getMyIdentifier() {
            return myIdentifier;
        }
    }

    public static class StartingEvent extends AnnotatedSagaManagerTest.MyIdentifierEvent {
        protected StartingEvent(String myIdentifier) {
            super(myIdentifier);
        }
    }

    public static class OtherStartingEvent extends AnnotatedSagaManagerTest.MyIdentifierEvent {
        private final CountDownLatch countDownLatch;

        protected OtherStartingEvent(String myIdentifier) {
            this(myIdentifier, null);
        }

        public OtherStartingEvent(String id, CountDownLatch countDownLatch) {
            super(id);
            this.countDownLatch = countDownLatch;
        }
    }

    public static class SlowStartingEvent extends AnnotatedSagaManagerTest.StartingEvent {
        private final CountDownLatch startCdl;

        private final long duration;

        protected SlowStartingEvent(String myIdentifier, CountDownLatch startCdl, long duration) {
            super(myIdentifier);
            this.startCdl = startCdl;
            this.duration = duration;
        }

        public long getDuration() {
            return duration;
        }

        public CountDownLatch getStartCdl() {
            return startCdl;
        }
    }

    public static class ForcingStartEvent extends AnnotatedSagaManagerTest.MyIdentifierEvent {
        protected ForcingStartEvent(String myIdentifier) {
            super(myIdentifier);
        }
    }

    public static class EndingEvent extends AnnotatedSagaManagerTest.MyIdentifierEvent {
        protected EndingEvent(String myIdentifier) {
            super(myIdentifier);
        }
    }

    public static class MiddleEvent extends AnnotatedSagaManagerTest.MyIdentifierEvent {
        protected MiddleEvent(String myIdentifier) {
            super(myIdentifier);
        }
    }

    private class HandleEventTask implements Runnable {
        private final EventMessage<?> eventMessage;

        public HandleEventTask(EventMessage<?> eventMessage) {
            this.eventMessage = eventMessage;
        }

        @Override
        public void run() {
            try {
                handle(eventMessage);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                TestCase.fail("The handler failed to handle the message");
            }
        }
    }
}

