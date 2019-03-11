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
package org.axonframework.integrationtests.eventhandling;


import NoTransactionManager.INSTANCE;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventHandlerInvoker;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.UnableToClaimTokenException;
import org.axonframework.eventhandling.tokenstore.inmemory.InMemoryTokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.integrationtests.utils.AssertUtils;
import org.axonframework.integrationtests.utils.EventTestUtils;
import org.axonframework.integrationtests.utils.MockException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Christophe Bouhier
 */
public class TrackingEventProcessorTest_MultiThreaded {
    private TrackingEventProcessor testSubject;

    private EmbeddedEventStore eventBus;

    private TokenStore tokenStore;

    private EventHandlerInvoker eventHandlerInvoker;

    private EventMessageHandler mockHandler;

    @Test
    public void testProcessorWorkerCount() {
        testSubject.start();
        // give it some time to split segments from the store and submit to executor service.
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> Assert.assertThat(testSubject.activeProcessorThreads(), CoreMatchers.is(2)));
        Assert.assertThat(testSubject.processingStatus().size(), CoreMatchers.is(2));
        TestCase.assertTrue(testSubject.processingStatus().containsKey(0));
        TestCase.assertTrue(testSubject.processingStatus().containsKey(1));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertTrue(testSubject.processingStatus().get(0).isCaughtUp()));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertTrue(testSubject.processingStatus().get(1).isCaughtUp()));
    }

    @Test
    public void testProcessorInitializesMoreTokensThanWorkerCount() throws InterruptedException {
        configureProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(2).andInitialSegmentsCount(4));
        testSubject.start();
        // give it some time to split segments from the store and submit to executor service.
        Thread.sleep(200);
        Assert.assertThat(testSubject.activeProcessorThreads(), CoreMatchers.is(2));
        int[] actual = tokenStore.fetchSegments(testSubject.getName());
        Arrays.sort(actual);
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3 }, actual);
    }

    // Reproduce issue #508 (https://github.com/AxonFramework/AxonFramework/issues/508)
    @Test
    public void testProcessorInitializesAndUsesSameTokens() {
        configureProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(6).andInitialSegmentsCount(6));
        testSubject.start();
        AssertUtils.assertWithin(5, TimeUnit.SECONDS, () -> Assert.assertThat(testSubject.activeProcessorThreads(), CoreMatchers.is(6)));
        int[] actual = tokenStore.fetchSegments(testSubject.getName());
        Arrays.sort(actual);
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4, 5 }, actual);
    }

    @Test
    public void testProcessorWorkerCountWithMultipleSegments() {
        tokenStore.storeToken(new GlobalSequenceTrackingToken(1L), "test", 0);
        tokenStore.storeToken(new GlobalSequenceTrackingToken(2L), "test", 1);
        testSubject.start();
        AssertUtils.assertWithin(20, TimeUnit.SECONDS, () -> Assert.assertThat(testSubject.activeProcessorThreads(), CoreMatchers.is(2)));
        Assert.assertThat(testSubject.processingStatus().size(), CoreMatchers.is(2));
        TestCase.assertTrue(testSubject.processingStatus().containsKey(0));
        TestCase.assertTrue(testSubject.processingStatus().containsKey(1));
        AssertUtils.assertWithin(10, TimeUnit.MILLISECONDS, () -> TestCase.assertEquals(new GlobalSequenceTrackingToken(1L), testSubject.processingStatus().get(0).getTrackingToken()));
        AssertUtils.assertWithin(10, TimeUnit.MILLISECONDS, () -> TestCase.assertEquals(new GlobalSequenceTrackingToken(2L), testSubject.processingStatus().get(1).getTrackingToken()));
    }

    /**
     * This processor won't be able to handle any segments, as claiming a segment will fail.
     */
    @Test
    public void testProcessorWorkerCountWithMultipleSegmentsClaimFails() throws InterruptedException {
        tokenStore.storeToken(new GlobalSequenceTrackingToken(1L), "test", 0);
        tokenStore.storeToken(new GlobalSequenceTrackingToken(2L), "test", 1);
        // Will skip segments.
        Mockito.doThrow(new UnableToClaimTokenException("Failed")).when(tokenStore).extendClaim("test", 0);
        Mockito.doThrow(new UnableToClaimTokenException("Failed")).when(tokenStore).fetchToken("test", 0);
        Mockito.doThrow(new UnableToClaimTokenException("Failed")).when(tokenStore).extendClaim("test", 1);
        Mockito.doThrow(new UnableToClaimTokenException("Failed")).when(tokenStore).fetchToken("test", 1);
        testSubject.start();
        // give it some time to split segments from the store and submit to executor service.
        Thread.sleep(200);
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> Assert.assertThat(testSubject.activeProcessorThreads(), CoreMatchers.is(0)));
    }

    @Test
    public void testProcessorExtendsClaimOnSegment() throws InterruptedException {
        tokenStore.storeToken(new GlobalSequenceTrackingToken(1L), "test", 0);
        tokenStore.storeToken(new GlobalSequenceTrackingToken(2L), "test", 1);
        testSubject.start();
        // give it some time to split segments from the store and submit to executor service.
        Thread.sleep(200);
        eventBus.publish(EventTestUtils.createEvents(10));
        AssertUtils.assertWithin(200, TimeUnit.MILLISECONDS, () -> Mockito.verify(tokenStore, Mockito.atLeast(1)).extendClaim("test", 0));
        AssertUtils.assertWithin(200, TimeUnit.MILLISECONDS, () -> Mockito.verify(tokenStore, Mockito.atLeast(1)).extendClaim("test", 1));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> Assert.assertThat(testSubject.activeProcessorThreads(), CoreMatchers.is(2)));
    }

    @Test
    public void testBlacklistingSegmentWillHaveProcessorClaimAnotherOne() {
        tokenStore.storeToken(new GlobalSequenceTrackingToken(1L), "test", 0);
        tokenStore.storeToken(new GlobalSequenceTrackingToken(2L), "test", 1);
        tokenStore.storeToken(new GlobalSequenceTrackingToken(2L), "test", 2);
        testSubject.start();
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(0, testSubject.availableProcessorThreads()));
        TestCase.assertEquals(new HashSet(Arrays.asList(0, 1)), testSubject.processingStatus().keySet());
        testSubject.releaseSegment(0);
        AssertUtils.assertWithin(5, TimeUnit.SECONDS, () -> TestCase.assertTrue(testSubject.processingStatus().containsKey(2)));
        TestCase.assertEquals(new HashSet(Arrays.asList(2, 1)), testSubject.processingStatus().keySet());
        AssertUtils.assertWithin(2, TimeUnit.SECONDS, () -> TestCase.assertEquals(0, testSubject.availableProcessorThreads()));
    }

    @Test
    public void testProcessorWorkerCountWithMultipleSegmentsWithOneThread() throws InterruptedException {
        tokenStore.storeToken(new GlobalSequenceTrackingToken(1L), "test", 0);
        tokenStore.storeToken(new GlobalSequenceTrackingToken(2L), "test", 1);
        configureProcessor(TrackingEventProcessorConfiguration.forSingleThreadedProcessing());
        testSubject.start();
        // give it some time to split segments from the store and submit to executor service.
        Thread.sleep(200);
        Assert.assertThat(testSubject.activeProcessorThreads(), CoreMatchers.is(1));
    }

    @Test
    public void testMultiThreadSegmentsExceedsWorkerCount() throws Exception {
        configureProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(2).andInitialSegmentsCount(3));
        CountDownLatch countDownLatch = new CountDownLatch(2);
        final TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread acknowledgeByThread = new TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread();
        Mockito.doAnswer(( invocation) -> {
            acknowledgeByThread.addMessage(Thread.currentThread(), ((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject.start();
        eventBus.publish(EventTestUtils.createEvents(3));
        TestCase.assertTrue("Expected Handler to have received (only) 2 out of 3 published events", countDownLatch.await(5, TimeUnit.SECONDS));
        acknowledgeByThread.assertEventsAddUpTo(2);
    }

    @Test
    public void testMultiThreadPublishedEventsGetPassedToHandler() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        final TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread acknowledgeByThread = new TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread();
        Mockito.doAnswer(( invocation) -> {
            acknowledgeByThread.addMessage(Thread.currentThread(), ((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject.start();
        eventBus.publish(EventTestUtils.createEvents(2));
        TestCase.assertTrue("Expected Handler to have received 2 published events", countDownLatch.await(5, TimeUnit.SECONDS));
        acknowledgeByThread.assertEventsAckedByMultipleThreads();
        acknowledgeByThread.assertEventsAddUpTo(2);
    }

    @Test
    public void testMultiThreadTokenIsStoredWhenEventIsRead() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        testSubject.registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.onCleanup(( uow) -> countDownLatch.countDown());
            return interceptorChain.proceed();
        });
        testSubject.start();
        eventBus.publish(EventTestUtils.createEvents(2));
        TestCase.assertTrue("Expected Unit of Work to have reached clean up phase", countDownLatch.await(5, TimeUnit.SECONDS));
        Mockito.verify(tokenStore, Mockito.atLeastOnce()).storeToken(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Assert.assertThat(tokenStore.fetchToken(testSubject.getName(), 0), CoreMatchers.notNullValue());
        Assert.assertThat(tokenStore.fetchToken(testSubject.getName(), 1), CoreMatchers.notNullValue());
    }

    @Test
    public void testMultiThreadContinueFromPreviousToken() throws Exception {
        tokenStore = Mockito.spy(new InMemoryTokenStore());
        eventBus.publish(EventTestUtils.createEvents(10));
        TrackedEventMessage<?> firstEvent = eventBus.openStream(null).nextAvailable();
        tokenStore.storeToken(firstEvent.trackingToken(), testSubject.getName(), 0);
        TestCase.assertEquals(firstEvent.trackingToken(), tokenStore.fetchToken(testSubject.getName(), 0));
        final TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread acknowledgeByThread = new TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread();
        CountDownLatch countDownLatch = new CountDownLatch(9);
        Mockito.doAnswer(( invocation) -> {
            acknowledgeByThread.addMessage(Thread.currentThread(), ((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        configureProcessor(TrackingEventProcessorConfiguration.forParallelProcessing(2));
        testSubject.start();
        TestCase.assertTrue(("Expected 9 invocations on Event Handler by now, missing " + (countDownLatch.getCount())), countDownLatch.await(60, TimeUnit.SECONDS));
        acknowledgeByThread.assertEventsAckedByMultipleThreads();
        acknowledgeByThread.assertEventsAddUpTo(9);
    }

    @Test(timeout = 10000)
    public void testMultiThreadContinueAfterPause() throws Exception {
        final TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread acknowledgeByThread = new TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread();
        final List<DomainEventMessage<?>> events = EventTestUtils.createEvents(4);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Mockito.doAnswer(( invocation) -> {
            acknowledgeByThread.addMessage(Thread.currentThread(), ((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject.start();
        eventBus.publish(events.subList(0, 2));
        TestCase.assertTrue("Expected 2 invocations on Event Handler by now", countDownLatch.await(5, TimeUnit.SECONDS));
        acknowledgeByThread.assertEventsAddUpTo(2);
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(new GlobalSequenceTrackingToken(1), tokenStore.fetchToken("test", 0)));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(new GlobalSequenceTrackingToken(1), tokenStore.fetchToken("test", 1)));
        testSubject.shutDown();
        // The thread may block for 1 second waiting for a next event to pop up
        while ((testSubject.activeProcessorThreads()) > 0) {
            Thread.sleep(1);
            // wait...
        } 
        CountDownLatch countDownLatch2 = new CountDownLatch(2);
        Mockito.doAnswer(( invocation) -> {
            acknowledgeByThread.addMessage(Thread.currentThread(), ((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch2.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        eventBus.publish(events.subList(2, 4));
        TestCase.assertEquals(2, countDownLatch2.getCount());
        testSubject.start();
        TestCase.assertTrue("Expected 4 invocations on Event Handler by now", countDownLatch2.await(5, TimeUnit.SECONDS));
        acknowledgeByThread.assertEventsAddUpTo(4);
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(new GlobalSequenceTrackingToken(3), tokenStore.fetchToken("test", 0)));
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> TestCase.assertEquals(new GlobalSequenceTrackingToken(3), tokenStore.fetchToken("test", 1)));
    }

    @Test
    public void testMultiThreadProcessorGoesToRetryModeWhenOpenStreamFails() throws Exception {
        eventBus = Mockito.spy(eventBus);
        tokenStore = new InMemoryTokenStore();
        eventBus.publish(EventTestUtils.createEvents(5));
        Mockito.when(eventBus.openStream(ArgumentMatchers.any())).thenThrow(new MockException()).thenCallRealMethod();
        final TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread acknowledgeByThread = new TrackingEventProcessorTest_MultiThreaded.AcknowledgeByThread();
        CountDownLatch countDownLatch = new CountDownLatch(5);
        Mockito.doAnswer(( invocation) -> {
            acknowledgeByThread.addMessage(Thread.currentThread(), ((EventMessage<?>) (invocation.getArguments()[0])));
            countDownLatch.countDown();
            return null;
        }).when(mockHandler).handle(ArgumentMatchers.any());
        testSubject = TrackingEventProcessor.builder().name("test").eventHandlerInvoker(eventHandlerInvoker).messageSource(eventBus).tokenStore(tokenStore).transactionManager(INSTANCE).build();
        testSubject.start();
        TestCase.assertTrue("Expected 5 invocations on Event Handler by now", countDownLatch.await(10, TimeUnit.SECONDS));
        acknowledgeByThread.assertEventsAddUpTo(5);
        Mockito.verify(eventBus, Mockito.times(2)).openStream(ArgumentMatchers.any());
    }

    @Test
    public void testMultiThreadTokensAreStoredWhenUnitOfWorkIsRolledBackOnSecondEvent() throws Exception {
        List<? extends EventMessage<?>> events = EventTestUtils.createEvents(2);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        // noinspection Duplicates
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
        eventBus.publish(events);
        TestCase.assertTrue("Expected Unit of Work to have reached clean up phase", countDownLatch.await(5, TimeUnit.SECONDS));
        TestCase.assertNotNull(tokenStore.fetchToken(testSubject.getName(), 0));
        TestCase.assertNotNull(tokenStore.fetchToken(testSubject.getName(), 1));
    }

    // Utility to add up acknowledged messages by Thread (worker) name and assertions facilities.
    class AcknowledgeByThread {
        Map<String, List<EventMessage<?>>> ackedEventsByThreadMap = new ConcurrentHashMap<>();

        void addMessage(Thread handlingThread, EventMessage<?> msg) {
            ackedEventsByThreadMap.computeIfAbsent(handlingThread.getName(), ( k) -> new ArrayList<>()).add(msg);
        }

        void assertEventsAckedByMultipleThreads() {
            ackedEventsByThreadMap.values().forEach(( l) -> assertThat(l.isEmpty(), is(false)));
        }

        void assertEventsAddUpTo(int eventCount) {
            Assert.assertThat(ackedEventsByThreadMap.values().stream().mapToLong(Collection::size).sum(), CoreMatchers.is(Integer.valueOf(eventCount).longValue()));
        }
    }
}

