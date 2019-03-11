/**
 * Copyright (c) 2010-2019. Axon Framework
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
package org.axonframework.integrationtests.eventhandling;


import NoTransactionManager.INSTANCE;
import TrackingEventProcessor.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import junit.framework.TestCase;
import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.UnableToClaimTokenException;
import org.axonframework.eventhandling.tokenstore.inmemory.InMemoryTokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.integrationtests.utils.AssertUtils;
import org.axonframework.integrationtests.utils.EventTestUtils;
import org.axonframework.integrationtests.utils.MockException;
import org.axonframework.messaging.StreamableMessageSource;
import org.axonframework.serialization.SerializationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.annotation.DirtiesContext;


/**
 *
 *
 * @author Rene de Waele
 * @author Nakul Mishra
 */
public class TrackingEventProcessorTest {
    private TrackingEventProcessor testSubject;

    private EmbeddedEventStore eventBus;

    private TokenStore tokenStore;

    private EventHandlerInvoker eventHandlerInvoker;

    private EventMessageHandler mockHandler;

    private List<Long> sleepInstructions;

    private TransactionManager mockTransactionManager;

    private Transaction mockTransaction;

    @Test
    public void testPublishedEventsGetPassedToHandler() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Mockito.doAnswer(( invocation) -> {
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        eventBus.publish(EventTestUtils.createEvents(2));
        TestCase.assertTrue("Expected Handler to have received 2 published events", countDownLatch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testHandlerIsInvokedInTransactionScope() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger();
        AtomicInteger counterAtHandle = new AtomicInteger();
        Mockito.when(mockTransactionManager.startTransaction()).thenAnswer(( i) -> {
            counter.incrementAndGet();
            return mockTransaction;
        });
        Mockito.doAnswer(( i) -> counter.decrementAndGet()).when(mockTransaction).rollback();
        Mockito.doAnswer(( i) -> counter.decrementAndGet()).when(mockTransaction).commit();
        Mockito.doAnswer(( invocation) -> {
            counterAtHandle.set(counter.get());
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        eventBus.publish(EventTestUtils.createEvents(2));
        TestCase.assertTrue("Expected Handler to have received 2 published events", countDownLatch.await(5, TimeUnit.SECONDS));
        TestCase.assertEquals(1, counterAtHandle.get());
    }

    @Test
    public void testProcessorStopsOnNonTransientExceptionWhenLoadingToken() {
        Mockito.doThrow(new SerializationException("Faking a serialization issue")).when(tokenStore).fetchToken("test", 0);
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertFalse("Expected processor to have stopped", testSubject.isRunning()));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertTrue("Expected processor to set the error flag", testSubject.isError()));
        TestCase.assertEquals(Collections.emptyList(), sleepInstructions);
    }

    @Test
    public void testProcessorRetriesOnTransientExceptionWhenLoadingToken() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mockito.doAnswer(( invocation) -> {
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        Mockito.doThrow(new RuntimeException("Faking a recoverable issue")).doCallRealMethod().when(tokenStore).fetchToken("test", 0);
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        eventBus.publish(EventTestUtils.createEvent());
        TestCase.assertTrue("Expected Handler to have received published event", countDownLatch.await(5, TimeUnit.SECONDS));
        TestCase.assertTrue(testSubject.isRunning());
        TestCase.assertFalse(testSubject.isError());
        TestCase.assertEquals(Collections.singletonList(5000L), sleepInstructions);
    }

    @Test
    public void testTokenIsStoredWhenEventIsRead() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.onCleanup(( uow) -> countDownLatch.countDown());
            return interceptorChain.proceed();
        });
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        eventBus.publish(EventTestUtils.createEvent());
        TestCase.assertTrue("Expected Unit of Work to have reached clean up phase", countDownLatch.await(5, TimeUnit.SECONDS));
        Mockito.verify(tokenStore).extendClaim(ArgumentMatchers.eq(testSubject.getName()), ArgumentMatchers.anyInt());
        Mockito.verify(tokenStore).storeToken(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        TestCase.assertNotNull(tokenStore.fetchToken(testSubject.getName(), 0));
    }

    @Test
    public void testTokenIsStoredOncePerEventBatch() throws Exception {
        testSubject = TrackingEventProcessor.builder().name("test").eventHandlerInvoker(eventHandlerInvoker).messageSource(eventBus).tokenStore(tokenStore).transactionManager(INSTANCE).build();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.onCleanup(( uow) -> countDownLatch.countDown());
            return interceptorChain.proceed();
        });
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        eventBus.publish(EventTestUtils.createEvents(2));
        TestCase.assertTrue("Expected Unit of Work to have reached clean up phase for 2 messages", countDownLatch.await(5, TimeUnit.SECONDS));
        InOrder inOrder = Mockito.inOrder(tokenStore);
        inOrder.verify(tokenStore, Mockito.times(1)).extendClaim(ArgumentMatchers.eq(testSubject.getName()), ArgumentMatchers.anyInt());
        inOrder.verify(tokenStore, Mockito.times(1)).storeToken(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        TestCase.assertNotNull(tokenStore.fetchToken(testSubject.getName(), 0));
    }

    @Test
    public void testTokenIsNotStoredWhenUnitOfWorkIsRolledBack() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.onCommit(( uow) -> {
                throw new MockException();
            });
            return interceptorChain.proceed();
        });
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.onCleanup(( uow) -> countDownLatch.countDown());
            return interceptorChain.proceed();
        });
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        eventBus.publish(EventTestUtils.createEvent());
        TestCase.assertTrue("Expected Unit of Work to have reached clean up phase", countDownLatch.await(5, TimeUnit.SECONDS));
        TestCase.assertNull(tokenStore.fetchToken(testSubject.getName(), 0));
    }

    @Test
    @DirtiesContext
    public void testContinueFromPreviousToken() throws Exception {
        tokenStore = new InMemoryTokenStore();
        eventBus.publish(EventTestUtils.createEvents(10));
        TrackedEventMessage<?> firstEvent = eventBus.openStream(null).nextAvailable();
        tokenStore.storeToken(firstEvent.trackingToken(), testSubject.getName(), 0);
        TestCase.assertEquals(firstEvent.trackingToken(), tokenStore.fetchToken(testSubject.getName(), 0));
        List<EventMessage<?>> ackedEvents = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(9);
        Mockito.doAnswer(( invocation) -> {
            ackedEvents.add(((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject = TrackingEventProcessor.builder().name("test").eventHandlerInvoker(eventHandlerInvoker).messageSource(eventBus).tokenStore(tokenStore).transactionManager(INSTANCE).build();
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        TestCase.assertTrue("Expected 9 invocations on Event Handler by now", countDownLatch.await(5, TimeUnit.SECONDS));
        TestCase.assertEquals(9, ackedEvents.size());
    }

    @Test(timeout = 10000)
    @DirtiesContext
    public void testContinueAfterPause() throws Exception {
        List<EventMessage<?>> ackedEvents = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Mockito.doAnswer(( invocation) -> {
            ackedEvents.add(((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        eventBus.publish(EventTestUtils.createEvents(2));
        TestCase.assertTrue("Expected 2 invocations on Event Handler by now", countDownLatch.await(5, TimeUnit.SECONDS));
        TestCase.assertEquals(2, ackedEvents.size());
        testSubject.shutDown();
        // The thread may block for 1 second waiting for a next event to pop up
        while ((testSubject.activeProcessorThreads()) > 0) {
            Thread.sleep(1);
            // wait...
        } 
        CountDownLatch countDownLatch2 = new CountDownLatch(2);
        Mockito.doAnswer(( invocation) -> {
            ackedEvents.add(((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch2.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        eventBus.publish(EventTestUtils.createEvents(2));
        TestCase.assertEquals(2, countDownLatch2.getCount());
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        TestCase.assertTrue("Expected 4 invocations on Event Handler by now", countDownLatch2.await(5, TimeUnit.SECONDS));
        TestCase.assertEquals(4, ackedEvents.size());
    }

    @Test
    @DirtiesContext
    public void testProcessorGoesToRetryModeWhenOpenStreamFails() throws Exception {
        eventBus = Mockito.spy(eventBus);
        tokenStore = new InMemoryTokenStore();
        eventBus.publish(EventTestUtils.createEvents(5));
        Mockito.when(eventBus.openStream(ArgumentMatchers.any())).thenThrow(new MockException()).thenCallRealMethod();
        List<EventMessage<?>> ackedEvents = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(5);
        Mockito.doAnswer(( invocation) -> {
            ackedEvents.add(((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject = TrackingEventProcessor.builder().name("test").eventHandlerInvoker(eventHandlerInvoker).messageSource(eventBus).tokenStore(tokenStore).transactionManager(INSTANCE).build();
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        TestCase.assertTrue("Expected 5 invocations on Event Handler by now", countDownLatch.await(10, TimeUnit.SECONDS));
        TestCase.assertEquals(5, ackedEvents.size());
        Mockito.verify(eventBus, Mockito.times(2)).openStream(ArgumentMatchers.any());
    }

    @Test
    public void testFirstTokenIsStoredWhenUnitOfWorkIsRolledBackOnSecondEvent() throws Exception {
        List<? extends EventMessage<?>> events = EventTestUtils.createEvents(2);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.onCommit(( uow) -> {
                if (uow.getMessage().equals(events.get(1))) {
                    throw new MockException();
                }
            });
            return interceptorChain.proceed();
        });
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.onCleanup(( uow) -> countDownLatch.countDown());
            return interceptorChain.proceed();
        });
        testSubject.start();
        // give it a bit of time to start
        Thread.sleep(200);
        eventBus.publish(events);
        TestCase.assertTrue("Expected Unit of Work to have reached clean up phase", countDownLatch.await(5, TimeUnit.SECONDS));
        Mockito.verify(tokenStore, Mockito.atLeastOnce()).storeToken(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        TestCase.assertNotNull(tokenStore.fetchToken(testSubject.getName(), 0));
    }

    @Test
    @DirtiesContext
    @SuppressWarnings("unchecked")
    public void testEventsWithTheSameTokenAreProcessedInTheSameBatch() throws Exception {
        eventBus.shutDown();
        eventBus = Mockito.mock(EmbeddedEventStore.class);
        TrackingToken trackingToken = new GlobalSequenceTrackingToken(0);
        List<TrackedEventMessage<?>> events = EventTestUtils.createEvents(2).stream().map(( event) -> asTrackedEventMessage(event, trackingToken)).collect(Collectors.toList());
        Mockito.when(eventBus.openStream(null)).thenReturn(TrackingEventProcessorTest.trackingEventStreamOf(events.iterator()));
        testSubject = TrackingEventProcessor.builder().name("test").eventHandlerInvoker(eventHandlerInvoker).messageSource(eventBus).tokenStore(tokenStore).transactionManager(INSTANCE).build();
        // noinspection Duplicates
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.onCommit(( uow) -> {
                if (uow.getMessage().equals(events.get(1))) {
                    throw new MockException();
                }
            });
            return interceptorChain.proceed();
        });
        CountDownLatch countDownLatch = new CountDownLatch(2);
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.onCleanup(( uow) -> countDownLatch.countDown());
            return interceptorChain.proceed();
        });
        testSubject.start();
        // Give it a bit of time to start
        Thread.sleep(200);
        TestCase.assertTrue("Expected Unit of Work to have reached clean up phase", countDownLatch.await(5, TimeUnit.SECONDS));
        Mockito.verify(tokenStore, Mockito.atLeastOnce()).storeToken(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        TestCase.assertNull(tokenStore.fetchToken(testSubject.getName(), 0));
    }

    @Test
    public void testResetCausesEventsToBeReplayed() throws Exception {
        Mockito.when(mockHandler.supportsReset()).thenReturn(true);
        final List<String> handled = new CopyOnWriteArrayList<>();
        final List<String> handledInRedelivery = new CopyOnWriteArrayList<>();
        // noinspection Duplicates
        Mockito.doAnswer(( i) -> {
            EventMessage message = i.getArgument(0);
            handled.add(message.getIdentifier());
            if (ReplayToken.isReplay(message)) {
                handledInRedelivery.add(message.getIdentifier());
            }
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        eventBus.publish(EventTestUtils.createEvents(4));
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(4, handled.size()));
        testSubject.shutDown();
        testSubject.resetTokens();
        // Resetting twice caused problems (see issue #559)
        testSubject.resetTokens();
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(8, handled.size()));
        TestCase.assertEquals(handled.subList(0, 4), handled.subList(4, 8));
        TestCase.assertEquals(handled.subList(4, 8), handledInRedelivery);
        TestCase.assertTrue(testSubject.processingStatus().get(0).isReplaying());
        eventBus.publish(EventTestUtils.createEvents(1));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertFalse(testSubject.processingStatus().get(0).isReplaying()));
    }

    @Test
    public void testResetToPositionCausesCertainEventsToBeReplayed() throws Exception {
        Mockito.when(mockHandler.supportsReset()).thenReturn(true);
        final List<String> handled = new CopyOnWriteArrayList<>();
        final List<String> handledInRedelivery = new CopyOnWriteArrayList<>();
        // noinspection Duplicates
        Mockito.doAnswer(( i) -> {
            EventMessage message = i.getArgument(0);
            handled.add(message.getIdentifier());
            if (ReplayToken.isReplay(message)) {
                handledInRedelivery.add(message.getIdentifier());
            }
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        eventBus.publish(EventTestUtils.createEvents(4));
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(4, handled.size()));
        testSubject.shutDown();
        testSubject.resetTokens(( source) -> new GlobalSequenceTrackingToken(1L));
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(6, handled.size()));
        TestCase.assertFalse(handledInRedelivery.contains(handled.get(0)));
        TestCase.assertFalse(handledInRedelivery.contains(handled.get(1)));
        TestCase.assertEquals(handled.subList(2, 4), handled.subList(4, 6));
        TestCase.assertEquals(handled.subList(4, 6), handledInRedelivery);
        TestCase.assertTrue(testSubject.processingStatus().get(0).isReplaying());
        eventBus.publish(EventTestUtils.createEvents(1));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertFalse(testSubject.processingStatus().get(0).isReplaying()));
    }

    @Test
    public void testResetOnInitializeWithTokenResetToThatToken() throws Exception {
        TrackingEventProcessorConfiguration config = TrackingEventProcessorConfiguration.forSingleThreadedProcessing().andInitialTrackingToken(( ms) -> new GlobalSequenceTrackingToken(1L));
        TrackingEventProcessor.Builder eventProcessorBuilder = TrackingEventProcessor.builder().name("test").eventHandlerInvoker(eventHandlerInvoker).messageSource(eventBus).tokenStore(tokenStore).transactionManager(INSTANCE).trackingEventProcessorConfiguration(config);
        testSubject = new TrackingEventProcessor(eventProcessorBuilder) {
            @Override
            protected void doSleepFor(long millisToSleep) {
                if (isRunning()) {
                    sleepInstructions.add(millisToSleep);
                }
            }
        };
        Mockito.when(mockHandler.supportsReset()).thenReturn(true);
        final List<String> handled = new CopyOnWriteArrayList<>();
        final List<String> handledInRedelivery = new CopyOnWriteArrayList<>();
        // noinspection Duplicates
        Mockito.doAnswer(( i) -> {
            EventMessage message = i.getArgument(0);
            handled.add(message.getIdentifier());
            if (ReplayToken.isReplay(message)) {
                handledInRedelivery.add(message.getIdentifier());
            }
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        eventBus.publish(EventTestUtils.createEvents(4));
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(2, handled.size()));
        testSubject.shutDown();
        testSubject.resetTokens();
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(4, handled.size()));
        TestCase.assertEquals(handled.subList(0, 2), handled.subList(2, 4));
        TestCase.assertEquals(handled.subList(2, 4), handledInRedelivery);
        TestCase.assertTrue(testSubject.processingStatus().get(0).isReplaying());
        eventBus.publish(EventTestUtils.createEvents(1));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertFalse(testSubject.processingStatus().get(0).isReplaying()));
    }

    @Test
    public void testResetBeforeStartingPerformsANormalRun() throws Exception {
        Mockito.when(mockHandler.supportsReset()).thenReturn(true);
        final List<String> handled = new CopyOnWriteArrayList<>();
        final List<String> handledInRedelivery = new CopyOnWriteArrayList<>();
        // noinspection Duplicates
        Mockito.doAnswer(( i) -> {
            EventMessage message = i.getArgument(0);
            handled.add(message.getIdentifier());
            if (ReplayToken.isReplay(message)) {
                handledInRedelivery.add(message.getIdentifier());
            }
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject.start();
        testSubject.shutDown();
        testSubject.resetTokens();
        testSubject.start();
        eventBus.publish(EventTestUtils.createEvents(4));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(4, handled.size()));
        TestCase.assertEquals(0, handledInRedelivery.size());
        TestCase.assertFalse(testSubject.processingStatus().get(0).isReplaying());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReplayFlagAvailableWhenReplayInDifferentOrder() throws Exception {
        StreamableMessageSource<TrackedEventMessage<?>> stubSource = Mockito.mock(StreamableMessageSource.class);
        testSubject = TrackingEventProcessor.builder().name("test").eventHandlerInvoker(eventHandlerInvoker).messageSource(stubSource).tokenStore(tokenStore).transactionManager(INSTANCE).build();
        Mockito.when(stubSource.openStream(ArgumentMatchers.any())).thenReturn(new TrackingEventProcessorTest.StubTrackingEventStream(0, 1, 2, 5)).thenReturn(new TrackingEventProcessorTest.StubTrackingEventStream(0, 1, 2, 3, 4, 5, 6, 7));
        Mockito.when(eventHandlerInvoker.supportsReset()).thenReturn(true);
        Mockito.doReturn(true).when(eventHandlerInvoker).canHandle(ArgumentMatchers.any(), ArgumentMatchers.any());
        List<TrackingToken> firstRun = new CopyOnWriteArrayList<>();
        List<TrackingToken> replayRun = new CopyOnWriteArrayList<>();
        Mockito.doAnswer(( i) -> {
            firstRun.add(i.<TrackedEventMessage>getArgument(0).trackingToken());
            return null;
        }).when(eventHandlerInvoker).handle(ArgumentMatchers.any(), ArgumentMatchers.any());
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(4, firstRun.size()));
        testSubject.shutDown();
        Mockito.doAnswer(( i) -> {
            replayRun.add(i.<TrackedEventMessage>getArgument(0).trackingToken());
            return null;
        }).when(eventHandlerInvoker).handle(ArgumentMatchers.any(), ArgumentMatchers.any());
        testSubject.resetTokens();
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(8, replayRun.size()));
        TestCase.assertEquals(GapAwareTrackingToken.newInstance(5, Arrays.asList(3L, 4L)), firstRun.get(3));
        TestCase.assertTrue(((replayRun.get(0)) instanceof ReplayToken));
        TestCase.assertTrue(((replayRun.get(5)) instanceof ReplayToken));
        TestCase.assertEquals(GapAwareTrackingToken.newInstance(6, Collections.emptySortedSet()), replayRun.get(6));
    }

    @Test(expected = IllegalStateException.class)
    public void testResetRejectedWhileRunning() {
        testSubject.start();
        testSubject.resetTokens();
    }

    @Test
    public void testResetNotSupportedWhenInvokerDoesNotSupportReset() {
        Mockito.when(mockHandler.supportsReset()).thenReturn(false);
        TestCase.assertFalse(testSubject.supportsReset());
    }

    @Test(expected = IllegalStateException.class)
    public void testResetRejectedWhenInvokerDoesNotSupportReset() {
        Mockito.when(mockHandler.supportsReset()).thenReturn(false);
        testSubject.resetTokens();
    }

    @Test
    public void testResetRejectedIfNotAllTokensCanBeClaimed() {
        tokenStore.initializeTokenSegments("test", 4);
        Mockito.when(tokenStore.fetchToken("test", 3)).thenThrow(new UnableToClaimTokenException("Mock"));
        try {
            testSubject.resetTokens();
            TestCase.fail("Expected exception");
        } catch (UnableToClaimTokenException e) {
            // expected
        }
        Mockito.verify(tokenStore, Mockito.never()).storeToken(ArgumentMatchers.isNull(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
    }

    @Test
    public void testWhenFailureDuringInit() throws InterruptedException {
        Mockito.doThrow(new RuntimeException("Faking issue during fetchSegments")).doCallRealMethod().when(tokenStore).fetchSegments(ArgumentMatchers.anyString());
        // and on further calls
        Mockito.doThrow(new RuntimeException("Faking issue during initializeTokenSegments")).doNothing().when(tokenStore).initializeTokenSegments(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
        testSubject.start();
        for (int i = 0; (i < 250) && ((testSubject.activeProcessorThreads()) < 1); i++) {
            Thread.sleep(10);
        }
        TestCase.assertEquals(1, testSubject.activeProcessorThreads());
    }

    @Test
    public void testUpdateActiveSegmentsWhenBatchIsEmpty() throws Exception {
        StreamableMessageSource<TrackedEventMessage<?>> stubSource = Mockito.mock(StreamableMessageSource.class);
        testSubject = TrackingEventProcessor.builder().name("test").eventHandlerInvoker(eventHandlerInvoker).messageSource(stubSource).tokenStore(tokenStore).transactionManager(INSTANCE).build();
        Mockito.when(stubSource.openStream(ArgumentMatchers.any())).thenReturn(new TrackingEventProcessorTest.StubTrackingEventStream(0, 1, 2, 5));
        Mockito.doReturn(true, false).when(eventHandlerInvoker).canHandle(ArgumentMatchers.any(), ArgumentMatchers.any());
        List<TrackingToken> trackingTokens = new CopyOnWriteArrayList<>();
        Mockito.doAnswer(( i) -> {
            trackingTokens.add(i.<TrackedEventMessage>getArgument(0).trackingToken());
            return null;
        }).when(eventHandlerInvoker).handle(ArgumentMatchers.any(), ArgumentMatchers.any());
        testSubject.start();
        // give it a bit of time to start
        waitForStatus("processor thread started", 200, TimeUnit.MILLISECONDS, ( status) -> status.containsKey(0));
        waitForStatus("Segment 0 caught up", 5, TimeUnit.SECONDS, ( status) -> status.get(0).isCaughtUp());
        EventTrackerStatus eventTrackerStatus = testSubject.processingStatus().get(0);
        GapAwareTrackingToken expectedToken = GapAwareTrackingToken.newInstance(5, Arrays.asList(3L, 4L));
        TrackingToken lastToken = eventTrackerStatus.getTrackingToken();
        TestCase.assertTrue(lastToken.covers(expectedToken));
    }

    @Test
    public void testReleaseSegment() {
        testSubject.start();
        AssertUtils.assertWithin(5, TimeUnit.SECONDS, () -> TestCase.assertEquals(1, testSubject.activeProcessorThreads()));
        testSubject.releaseSegment(0, 1, TimeUnit.SECONDS);
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(0, testSubject.activeProcessorThreads()));
        AssertUtils.assertWithin(5, TimeUnit.SECONDS, () -> TestCase.assertEquals(1, testSubject.activeProcessorThreads()));
    }

    @Test
    public void testHasAvailableSegments() {
        TestCase.assertEquals(1, testSubject.availableProcessorThreads());
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(0, testSubject.availableProcessorThreads()));
        testSubject.releaseSegment(0);
        AssertUtils.assertWithin(2, TimeUnit.SECONDS, () -> TestCase.assertEquals(1, testSubject.availableProcessorThreads()));
    }

    @Test(timeout = 10000)
    public void testSplitSegments() throws InterruptedException {
        tokenStore.initializeTokenSegments(testSubject.getName(), 1);
        testSubject.start();
        waitForSegmentStart(0);
        TestCase.assertTrue("Expected split to succeed", testSubject.splitSegment(0).join());
        Assert.assertArrayEquals(new int[]{ 0, 1 }, tokenStore.fetchSegments(testSubject.getName()));
        waitForSegmentStart(0);
    }

    @Test(timeout = 10000)
    public void testMergeSegments() throws InterruptedException {
        tokenStore.initializeTokenSegments(testSubject.getName(), 2);
        testSubject.start();
        while (testSubject.processingStatus().isEmpty()) {
            Thread.sleep(10);
        } 
        TestCase.assertTrue("Expected merge to succeed", testSubject.mergeSegment(0).join());
        Assert.assertArrayEquals(new int[]{ 0 }, tokenStore.fetchSegments(testSubject.getName()));
        waitForSegmentStart(0);
    }

    @Test(timeout = 10000)
    public void testMergeSegments_BothClaimedByProcessor() throws Exception {
        initProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(2));
        tokenStore.initializeTokenSegments(testSubject.getName(), 2);
        List<EventMessage<?>> handledEvents = new CopyOnWriteArrayList<>();
        Mockito.when(mockHandler.handle(ArgumentMatchers.any())).thenAnswer(( i) -> handledEvents.add(i.getArgument(0)));
        publishEvents(10);
        testSubject.start();
        waitForActiveThreads(2);
        AssertUtils.assertWithin(50, TimeUnit.MILLISECONDS, () -> TestCase.assertTrue("Expected merge to succeed", testSubject.mergeSegment(0).join()));
        Assert.assertArrayEquals(new int[]{ 0 }, tokenStore.fetchSegments(testSubject.getName()));
        waitForSegmentStart(0);
        while (!(testSubject.processingStatus().get(0).isCaughtUp())) {
            Thread.sleep(10);
        } 
        AssertUtils.assertWithin(5, TimeUnit.SECONDS, () -> TestCase.assertEquals("Expected all 10 messages to be handled", 10, handledEvents.stream().map(EventMessage::getIdentifier).distinct().count()));
        Thread.sleep(100);
        TestCase.assertEquals("Number of handler invocations doesn't match number of unique events", 10, handledEvents.size());
    }

    @Test(timeout = 10000)
    public void testMergeSegments_WithExplicitReleaseOther() throws Exception {
        initProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(2));
        tokenStore.initializeTokenSegments(testSubject.getName(), 2);
        List<EventMessage<?>> handledEvents = new CopyOnWriteArrayList<>();
        List<EventMessage<?>> events = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            events.add(EventTestUtils.createEvent(UUID.randomUUID().toString(), 0));
        }
        Mockito.when(mockHandler.handle(ArgumentMatchers.any())).thenAnswer(( i) -> handledEvents.add(i.getArgument(0)));
        eventBus.publish(events);
        testSubject.start();
        waitForActiveThreads(2);
        testSubject.releaseSegment(1);
        waitForSegmentRelease(1);
        AssertUtils.assertWithin(50, TimeUnit.MILLISECONDS, () -> TestCase.assertTrue("Expected merge to succeed", testSubject.mergeSegment(0).join()));
        Assert.assertArrayEquals(new int[]{ 0 }, tokenStore.fetchSegments(testSubject.getName()));
        waitForSegmentStart(0);
        while (!(Optional.ofNullable(testSubject.processingStatus().get(0)).map(EventTrackerStatus::isCaughtUp).orElse(false))) {
            Thread.sleep(10);
        } 
        TestCase.assertEquals(10, handledEvents.size());
    }

    @Test(timeout = 10000)
    public void testDoubleSplitAndMerge() throws Exception {
        tokenStore.initializeTokenSegments(testSubject.getName(), 1);
        List<EventMessage<?>> handledEvents = new CopyOnWriteArrayList<>();
        Mockito.when(mockHandler.handle(ArgumentMatchers.any())).thenAnswer(( i) -> handledEvents.add(i.getArgument(0)));
        publishEvents(10);
        testSubject.start();
        waitForActiveThreads(1);
        AssertUtils.assertWithin(50, TimeUnit.MILLISECONDS, () -> TestCase.assertTrue("Expected split to succeed", testSubject.splitSegment(0).join()));
        waitForActiveThreads(1);
        AssertUtils.assertWithin(50, TimeUnit.MILLISECONDS, () -> TestCase.assertTrue("Expected split to succeed", testSubject.splitSegment(0).join()));
        waitForActiveThreads(1);
        Assert.assertArrayEquals(new int[]{ 0, 1, 2 }, tokenStore.fetchSegments(testSubject.getName()));
        publishEvents(20);
        waitForProcessingStatus(0, EventTrackerStatus::isCaughtUp);
        TestCase.assertTrue("Expected merge to succeed", testSubject.mergeSegment(0).join());
        Assert.assertArrayEquals(new int[]{ 0, 1 }, tokenStore.fetchSegments(testSubject.getName()));
        publishEvents(10);
        waitForSegmentStart(0);
        waitForProcessingStatus(0, ( est) -> ((est.getSegment().getMask()) == 1) && (est.isCaughtUp()));
        TestCase.assertTrue("Expected merge to succeed", testSubject.mergeSegment(0).join());
        Assert.assertArrayEquals(new int[]{ 0 }, tokenStore.fetchSegments(testSubject.getName()));
        AssertUtils.assertWithin(3, TimeUnit.SECONDS, () -> TestCase.assertEquals(40, handledEvents.size()));
    }

    @Test(timeout = 10000)
    public void testMergeSegmentWithDifferentProcessingGroupsAndSequencingPolicies() throws Exception {
        EventMessageHandler otherHandler = Mockito.mock(EventMessageHandler.class);
        Mockito.when(otherHandler.canHandle(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(otherHandler.supportsReset()).thenReturn(true);
        EventHandlerInvoker mockInvoker = SimpleEventHandlerInvoker.builder().eventHandlers(Collections.singleton(otherHandler)).sequencingPolicy(( m) -> 0).build();
        initProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(2).andBatchSize(5), ( builder) -> builder.eventHandlerInvoker(new MultiEventHandlerInvoker(eventHandlerInvoker, mockInvoker)));
        List<EventMessage<?>> handledEvents = new CopyOnWriteArrayList<>();
        Mockito.when(mockHandler.handle(ArgumentMatchers.any())).thenAnswer(( i) -> {
            TrackedEventMessage<?> message = i.getArgument(0);
            return handledEvents.add(message);
        });
        publishEvents(10);
        testSubject.start();
        while (((testSubject.processingStatus().size()) < 2) || (!(testSubject.processingStatus().values().stream().allMatch(EventTrackerStatus::isCaughtUp)))) {
            Thread.sleep(10);
        } 
        System.out.println("Asked to release Segment 1");
        testSubject.releaseSegment(1);
        while (((testSubject.processingStatus().size()) != 1) || (!(testSubject.processingStatus().values().stream().allMatch(EventTrackerStatus::isCaughtUp)))) {
            Thread.sleep(10);
        } 
        publishEvents(10);
        testSubject.mergeSegment(0);
        publishEvents(10);
        while (((testSubject.processingStatus().size()) != 1) || (!(testSubject.processingStatus().values().stream().allMatch(EventTrackerStatus::isCaughtUp)))) {
            Thread.sleep(10);
        } 
        AssertUtils.assertWithin(5, TimeUnit.SECONDS, () -> TestCase.assertEquals(30, handledEvents.size()));
        Thread.sleep(100);
        TestCase.assertEquals(30, handledEvents.size());
    }

    @Test(timeout = 15000)
    public void testMergeSegmentsDuringReplay() throws Exception {
        initProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(2));
        tokenStore.initializeTokenSegments(testSubject.getName(), 2);
        List<EventMessage<?>> handledEvents = new CopyOnWriteArrayList<>();
        List<EventMessage<?>> replayedEvents = new CopyOnWriteArrayList<>();
        Mockito.when(mockHandler.handle(ArgumentMatchers.any())).thenAnswer(( i) -> {
            TrackedEventMessage<?> message = i.getArgument(0);
            if (ReplayToken.isReplay(message)) {
                System.out.println(((("[replay]" + (Thread.currentThread().getName())) + " ") + (message.trackingToken())));
                replayedEvents.add(message);
            } else {
                System.out.println((((Thread.currentThread().getName()) + " ") + (message.trackingToken())));
                handledEvents.add(message);
            }
            return null;
        });
        for (int i = 0; i < 10; i++) {
            eventBus.publish(EventTestUtils.createEvent(UUID.randomUUID().toString(), 0));
        }
        testSubject.start();
        while (((testSubject.processingStatus().size()) < 2) || (!(testSubject.processingStatus().values().stream().allMatch(EventTrackerStatus::isCaughtUp)))) {
            Thread.sleep(10);
        } 
        testSubject.shutDown();
        testSubject.resetTokens();
        testSubject.releaseSegment(1);
        testSubject.start();
        waitForActiveThreads(1);
        Thread.yield();
        CompletableFuture<Boolean> mergeResult = testSubject.mergeSegment(0);
        publishEvents(20);
        System.out.println(("Number of events handled at merge: " + (handledEvents.size())));
        TestCase.assertTrue("Expected merge to succeed", mergeResult.join());
        Assert.assertArrayEquals(new int[]{ 0 }, tokenStore.fetchSegments(testSubject.getName()));
        waitForSegmentStart(0);
        AssertUtils.assertWithin(10, TimeUnit.SECONDS, () -> TestCase.assertEquals(30, handledEvents.size()));
        Thread.sleep(100);
        TestCase.assertEquals(30, handledEvents.size());
        // make sure replay events are only delivered once.
        TestCase.assertEquals(replayedEvents.stream().map(EventMessage::getIdentifier).distinct().count(), replayedEvents.size());
    }

    @Test(timeout = 10000)
    public void testReplayDuringIncompleteMerge() throws Exception {
        initProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(2));
        tokenStore.initializeTokenSegments(testSubject.getName(), 2);
        List<EventMessage<?>> handledEvents = new CopyOnWriteArrayList<>();
        List<EventMessage<?>> events = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            events.add(EventTestUtils.createEvent(UUID.randomUUID().toString(), 0));
        }
        Mockito.when(mockHandler.handle(ArgumentMatchers.any())).thenAnswer(( i) -> {
            TrackedEventMessage<?> message = i.getArgument(0);
            if (ReplayToken.isReplay(message)) {
                System.out.println((((Thread.currentThread().getName()) + " replayed ") + (message.trackingToken())));
                // ignore replays
                return null;
            }
            System.out.println((((Thread.currentThread().getName()) + " ") + (message.trackingToken())));
            return handledEvents.add(message);
        });
        eventBus.publish(events);
        testSubject.start();
        while (((testSubject.processingStatus().size()) < 2) || (!(testSubject.processingStatus().values().stream().allMatch(EventTrackerStatus::isCaughtUp)))) {
            Thread.sleep(10);
        } 
        testSubject.releaseSegment(1);
        while (testSubject.processingStatus().containsKey(1)) {
            Thread.yield();
        } 
        publishEvents(10);
        CompletableFuture<Boolean> mergeResult = testSubject.mergeSegment(0);
        TestCase.assertTrue("Expected split to succeed", mergeResult.join());
        testSubject.shutDown();
        testSubject.resetTokens();
        publishEvents(10);
        testSubject.start();
        waitForActiveThreads(1);
        System.out.println(("Number of events handled at merge: " + (handledEvents.size())));
        Assert.assertArrayEquals(new int[]{ 0 }, tokenStore.fetchSegments(testSubject.getName()));
        waitForSegmentStart(0);
        while (!(testSubject.processingStatus().get(0).isCaughtUp())) {
            Thread.sleep(10);
        } 
        // replayed messages aren't counted
        TestCase.assertEquals(30, handledEvents.size());
    }

    @Test(timeout = 10000)
    public void testMergeWithIncompatibleSegmentRejected() throws InterruptedException {
        initProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(3));
        testSubject.start();
        waitForActiveThreads(3);
        TestCase.assertTrue(testSubject.processingStatus().containsKey(0));
        TestCase.assertTrue(testSubject.processingStatus().containsKey(1));
        TestCase.assertTrue(testSubject.processingStatus().containsKey(2));
        // 1 is not mergeable with 0, because 0 itself was already split
        testSubject.releaseSegment(0);
        testSubject.releaseSegment(2);
        while ((testSubject.processingStatus().size()) > 1) {
            Thread.sleep(10);
        } 
        CompletableFuture<Boolean> actual = testSubject.mergeSegment(1);
        TestCase.assertFalse("Expected merge to be rejected", actual.join());
    }

    @Test(timeout = 10000)
    public void testMergeWithSingleSegmentRejected() throws InterruptedException {
        int numberOfSegments = 1;
        initProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(numberOfSegments));
        testSubject.start();
        waitForActiveThreads(1);
        CompletableFuture<Boolean> actual = testSubject.mergeSegment(0);
        TestCase.assertFalse("Expected merge to be rejected", actual.join());
    }

    private static class StubTrackingEventStream implements TrackingEventStream {
        private final Queue<TrackedEventMessage<?>> eventMessages;

        public StubTrackingEventStream(long... tokens) {
            GapAwareTrackingToken lastToken = GapAwareTrackingToken.newInstance((-1), Collections.emptySortedSet());
            eventMessages = new LinkedList();
            for (Long seq : tokens) {
                lastToken = lastToken.advanceTo(seq, 1000);
                eventMessages.add(new GenericTrackedEventMessage(lastToken, EventTestUtils.createEvent(seq)));
            }
        }

        @Override
        public Optional<TrackedEventMessage<?>> peek() {
            if (eventMessages.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(eventMessages.peek());
        }

        @Override
        public boolean hasNextAvailable(int timeout, TimeUnit unit) {
            return !(eventMessages.isEmpty());
        }

        @Override
        public TrackedEventMessage<?> nextAvailable() {
            return eventMessages.poll();
        }

        @Override
        public void close() {
        }
    }
}

