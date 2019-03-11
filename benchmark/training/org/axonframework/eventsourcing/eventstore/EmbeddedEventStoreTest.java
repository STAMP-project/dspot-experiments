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
package org.axonframework.eventsourcing.eventstore;


import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import junit.framework.TestCase;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.TrackingEventStream;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventsourcing.utils.EventStoreTestUtils;
import org.axonframework.eventsourcing.utils.MockException;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Rene de Waele
 */
public class EmbeddedEventStoreTest {
    private static final int CACHED_EVENTS = 10;

    private static final long FETCH_DELAY = 1000;

    private static final long CLEANUP_DELAY = 10000;

    private static final boolean OPTIMIZE_EVENT_CONSUMPTION = true;

    private EmbeddedEventStore testSubject;

    private EventStorageEngine storageEngine;

    private ThreadFactory threadFactory;

    @Test
    public void testExistingEventIsPassedToReader() throws Exception {
        DomainEventMessage<?> expected = EventStoreTestUtils.createEvent();
        testSubject.publish(expected);
        TrackingEventStream stream = testSubject.openStream(null);
        Assert.assertTrue(stream.hasNextAvailable());
        TrackedEventMessage<?> actual = stream.nextAvailable();
        Assert.assertEquals(expected.getIdentifier(), actual.getIdentifier());
        Assert.assertEquals(expected.getPayload(), actual.getPayload());
        Assert.assertTrue((actual instanceof DomainEventMessage<?>));
        Assert.assertEquals(expected.getAggregateIdentifier(), ((DomainEventMessage<?>) (actual)).getAggregateIdentifier());
    }

    @Test(timeout = (EmbeddedEventStoreTest.FETCH_DELAY) / 10)
    public void testEventPublishedAfterOpeningStreamIsPassedToReaderImmediately() throws Exception {
        TrackingEventStream stream = testSubject.openStream(null);
        TestCase.assertFalse(stream.hasNextAvailable());
        DomainEventMessage<?> expected = EventStoreTestUtils.createEvent();
        Thread t = new Thread(() -> {
            try {
                Assert.assertEquals(expected.getIdentifier(), getIdentifier());
            } catch (InterruptedException e) {
                TestCase.fail();
            }
        });
        t.start();
        testSubject.publish(expected);
        t.join();
    }

    @Test(timeout = 5000)
    public void testReadingIsBlockedWhenStoreIsEmpty() throws Exception {
        CountDownLatch lock = new CountDownLatch(1);
        TrackingEventStream stream = testSubject.openStream(null);
        Thread t = new Thread(() -> stream.asStream().findFirst().ifPresent(( event) -> lock.countDown()));
        t.start();
        TestCase.assertFalse(lock.await(100, TimeUnit.MILLISECONDS));
        testSubject.publish(EventStoreTestUtils.createEvent());
        t.join();
        Assert.assertEquals(0, lock.getCount());
    }

    @Test(timeout = 5000)
    public void testReadingIsBlockedWhenEndOfStreamIsReached() throws Exception {
        testSubject.publish(EventStoreTestUtils.createEvent());
        CountDownLatch lock = new CountDownLatch(2);
        TrackingEventStream stream = testSubject.openStream(null);
        Thread t = new Thread(() -> stream.asStream().limit(2).forEach(( event) -> lock.countDown()));
        t.start();
        TestCase.assertFalse(lock.await(100, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, lock.getCount());
        testSubject.publish(EventStoreTestUtils.createEvent());
        t.join();
        Assert.assertEquals(0, lock.getCount());
    }

    @Test(timeout = 5000)
    public void testReadingCanBeContinuedUsingLastToken() throws Exception {
        List<? extends EventMessage<?>> events = EventStoreTestUtils.createEvents(2);
        testSubject.publish(events);
        TrackedEventMessage<?> first = testSubject.openStream(null).nextAvailable();
        TrackingToken firstToken = first.trackingToken();
        TrackedEventMessage<?> second = testSubject.openStream(firstToken).nextAvailable();
        Assert.assertEquals(getIdentifier(), first.getIdentifier());
        Assert.assertEquals(getIdentifier(), second.getIdentifier());
    }

    @Test(timeout = 5000)
    public void testEventIsFetchedFromCacheWhenFetchedASecondTime() throws Exception {
        CountDownLatch lock = new CountDownLatch(2);
        List<TrackedEventMessage<?>> events = new CopyOnWriteArrayList<>();
        Thread t = new Thread(() -> testSubject.openStream(null).asStream().limit(2).forEach(( event) -> {
            lock.countDown();
            events.add(event);
        }));
        t.start();
        TestCase.assertFalse(lock.await(100, TimeUnit.MILLISECONDS));
        testSubject.publish(EventStoreTestUtils.createEvents(2));
        t.join();
        TrackedEventMessage<?> second = testSubject.openStream(events.get(0).trackingToken()).nextAvailable();
        Assert.assertSame(events.get(1), second);
    }

    @Test(timeout = 5000)
    public void testPeriodicPollingWhenEventStorageIsUpdatedIndependently() throws Exception {
        newTestSubject(EmbeddedEventStoreTest.CACHED_EVENTS, 20, EmbeddedEventStoreTest.CLEANUP_DELAY, EmbeddedEventStoreTest.OPTIMIZE_EVENT_CONSUMPTION);
        TrackingEventStream stream = testSubject.openStream(null);
        CountDownLatch lock = new CountDownLatch(1);
        Thread t = new Thread(() -> stream.asStream().findFirst().ifPresent(( event) -> lock.countDown()));
        t.start();
        TestCase.assertFalse(lock.await(100, TimeUnit.MILLISECONDS));
        storageEngine.appendEvents(EventStoreTestUtils.createEvent());
        t.join();
        Assert.assertTrue(lock.await(100, TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 5000)
    public void testConsumerStopsTailingWhenItFallsBehindTheCache() throws Exception {
        newTestSubject(EmbeddedEventStoreTest.CACHED_EVENTS, EmbeddedEventStoreTest.FETCH_DELAY, 20, EmbeddedEventStoreTest.OPTIMIZE_EVENT_CONSUMPTION);
        TrackingEventStream stream = testSubject.openStream(null);
        TestCase.assertFalse(stream.hasNextAvailable());// now we should be tailing

        testSubject.publish(EventStoreTestUtils.createEvents(EmbeddedEventStoreTest.CACHED_EVENTS));// triggers event producer to open a stream

        Thread.sleep(100);
        Mockito.reset(storageEngine);
        Assert.assertTrue(stream.hasNextAvailable());
        TrackedEventMessage<?> firstEvent = stream.nextAvailable();
        Mockito.verifyZeroInteractions(storageEngine);
        testSubject.publish(EventStoreTestUtils.createEvent(EmbeddedEventStoreTest.CACHED_EVENTS), EventStoreTestUtils.createEvent(((EmbeddedEventStoreTest.CACHED_EVENTS) + 1)));
        Thread.sleep(100);// allow the cleaner thread to evict the consumer

        Mockito.reset(storageEngine);
        Assert.assertTrue(stream.hasNextAvailable());
        Mockito.verify(storageEngine).readEvents(firstEvent.trackingToken(), false);
    }

    @Test
    public void testLoadWithoutSnapshot() {
        testSubject.publish(EventStoreTestUtils.createEvents(110));
        List<DomainEventMessage<?>> eventMessages = testSubject.readEvents(EventStoreTestUtils.AGGREGATE).asStream().collect(Collectors.toList());
        Assert.assertEquals(110, eventMessages.size());
        Assert.assertEquals(109, eventMessages.get(((eventMessages.size()) - 1)).getSequenceNumber());
    }

    @Test
    public void testLoadWithSnapshot() {
        testSubject.publish(EventStoreTestUtils.createEvents(110));
        storageEngine.storeSnapshot(EventStoreTestUtils.createEvent(30));
        List<DomainEventMessage<?>> eventMessages = testSubject.readEvents(EventStoreTestUtils.AGGREGATE).asStream().collect(Collectors.toList());
        Assert.assertEquals((110 - 30), eventMessages.size());
        Assert.assertEquals(30, eventMessages.get(0).getSequenceNumber());
        Assert.assertEquals(109, eventMessages.get(((eventMessages.size()) - 1)).getSequenceNumber());
    }

    /* Reproduces issue reported in https://github.com/AxonFramework/AxonFramework/issues/485 */
    @Test
    public void testStreamEventsShouldNotReturnDuplicateTokens() throws InterruptedException {
        newTestSubject(0, 1000, 1000, EmbeddedEventStoreTest.OPTIMIZE_EVENT_CONSUMPTION);
        Stream mockStream = Mockito.mock(Stream.class);
        Iterator mockIterator = Mockito.mock(Iterator.class);
        Mockito.when(mockStream.iterator()).thenReturn(mockIterator);
        Mockito.when(storageEngine.readEvents(ArgumentMatchers.any(TrackingToken.class), ArgumentMatchers.eq(false))).thenReturn(mockStream);
        Mockito.when(mockIterator.hasNext()).thenAnswer(new EmbeddedEventStoreTest.SynchronizedBooleanAnswer(false)).thenAnswer(new EmbeddedEventStoreTest.SynchronizedBooleanAnswer(true));
        Mockito.when(mockIterator.next()).thenReturn(new org.axonframework.eventhandling.GenericTrackedEventMessage(new GlobalSequenceTrackingToken(1), EventStoreTestUtils.createEvent()));
        TrackingEventStream stream = testSubject.openStream(null);
        TestCase.assertFalse(stream.hasNextAvailable());
        testSubject.publish(EventStoreTestUtils.createEvent());
        // give some time consumer to consume the event
        Thread.sleep(200);
        // if the stream correctly updates the token internally, it should not find events anymore
        TestCase.assertFalse(stream.hasNextAvailable());
    }

    @Test
    public void testLoadWithFailingSnapshot() {
        testSubject.publish(EventStoreTestUtils.createEvents(110));
        storageEngine.storeSnapshot(EventStoreTestUtils.createEvent(30));
        Mockito.when(storageEngine.readSnapshot(EventStoreTestUtils.AGGREGATE)).thenThrow(new MockException());
        List<DomainEventMessage<?>> eventMessages = testSubject.readEvents(EventStoreTestUtils.AGGREGATE).asStream().collect(Collectors.toList());
        Assert.assertEquals(110, eventMessages.size());
        Assert.assertEquals(0, eventMessages.get(0).getSequenceNumber());
        Assert.assertEquals(109, eventMessages.get(((eventMessages.size()) - 1)).getSequenceNumber());
    }

    @Test
    public void testLoadEventsAfterPublishingInSameUnitOfWork() {
        List<DomainEventMessage<?>> events = EventStoreTestUtils.createEvents(10);
        testSubject.publish(events.subList(0, 2));
        DefaultUnitOfWork.startAndGet(null).execute(() -> {
            Assert.Assert.assertEquals(2, testSubject.readEvents(AGGREGATE).asStream().count());
            testSubject.publish(events.subList(2, events.size()));
            Assert.Assert.assertEquals(10, testSubject.readEvents(AGGREGATE).asStream().count());
        });
    }

    @Test
    public void testLoadEventsWithOffsetAfterPublishingInSameUnitOfWork() {
        List<DomainEventMessage<?>> events = EventStoreTestUtils.createEvents(10);
        testSubject.publish(events.subList(0, 2));
        DefaultUnitOfWork.startAndGet(null).execute(() -> {
            Assert.Assert.assertEquals(2, testSubject.readEvents(AGGREGATE).asStream().count());
            testSubject.publish(events.subList(2, events.size()));
            Assert.Assert.assertEquals(8, testSubject.readEvents(AGGREGATE, 2).asStream().count());
        });
    }

    @Test
    public void testEventsAppendedInvisibleUntilUnitOfWorkIsCommitted() {
        List<DomainEventMessage<?>> events = EventStoreTestUtils.createEvents(10);
        testSubject.publish(events.subList(0, 2));
        DefaultUnitOfWork<Message<?>> unitOfWork = DefaultUnitOfWork.startAndGet(null);
        testSubject.publish(events.subList(2, events.size()));
        CurrentUnitOfWork.clear(unitOfWork);
        // working outside the context of the UoW now
        Assert.assertEquals(2, testSubject.readEvents(EventStoreTestUtils.AGGREGATE).asStream().count());
        CurrentUnitOfWork.set(unitOfWork);
        // Back in the context
        Assert.assertEquals(10, testSubject.readEvents(EventStoreTestUtils.AGGREGATE).asStream().count());
        unitOfWork.rollback();
        Assert.assertEquals(2, testSubject.readEvents(EventStoreTestUtils.AGGREGATE).asStream().count());
    }

    @Test(timeout = 5000)
    public void testCustomThreadFactoryIsUsed() throws Exception {
        CountDownLatch lock = new CountDownLatch(1);
        TrackingEventStream stream = testSubject.openStream(null);
        Thread t = new Thread(() -> stream.asStream().findFirst().ifPresent(( event) -> lock.countDown()));
        t.start();
        TestCase.assertFalse(lock.await(100, TimeUnit.MILLISECONDS));
        testSubject.publish(EventStoreTestUtils.createEvent());
        t.join();
        Assert.assertEquals(0, lock.getCount());
        Mockito.verify(threadFactory, Mockito.atLeastOnce()).newThread(ArgumentMatchers.any(Runnable.class));
    }

    @Test
    public void testOpenStreamReadsEventsFromAnEventProducedByVerifyThreadFactoryOperation() throws InterruptedException {
        TrackingEventStream eventStream = testSubject.openStream(null);
        TestCase.assertFalse(eventStream.hasNextAvailable());// There are no events published yet, so stream will tail

        testSubject.publish(EventStoreTestUtils.createEvents(5));// Publish some events which should be returned to the stream by a producer

        Thread.sleep(100);// Give the Event Producer thread time to fill the cache

        Assert.assertTrue(eventStream.hasNextAvailable());// Stream should contain events again, from the producer

        // Consume events until the end
        while (eventStream.hasNextAvailable()) {
            eventStream.nextAvailable();
        } 
        TestCase.assertFalse(eventStream.hasNextAvailable());// Should have reached the end, hence returned false

        Mockito.verify(threadFactory, Mockito.atLeastOnce()).newThread(ArgumentMatchers.any(Runnable.class));// Verify a producer thread was created

    }

    @Test
    public void testTailingConsumptionThreadIsNeverCreatedIfEventConsumptionOptimizationIsSwitchedOff() throws InterruptedException {
        boolean doNotOptimizeEventConsumption = false;
        // noinspection ConstantConditions
        newTestSubject(EmbeddedEventStoreTest.CACHED_EVENTS, EmbeddedEventStoreTest.FETCH_DELAY, EmbeddedEventStoreTest.CLEANUP_DELAY, doNotOptimizeEventConsumption);
        TrackingEventStream eventStream = testSubject.openStream(null);
        testSubject.publish(EventStoreTestUtils.createEvents(5));
        // Consume some events
        while (eventStream.hasNextAvailable()) {
            eventStream.nextAvailable();
        } 
        // No tailing-consumer Producer thread has ever been created
        Mockito.verifyZeroInteractions(threadFactory);
    }

    @Test
    public void testEventStreamKeepsReturningEventsIfEventConsumptionOptimizationIsSwitchedOff() throws InterruptedException {
        boolean doNotOptimizeEventConsumption = false;
        // noinspection ConstantConditions
        newTestSubject(EmbeddedEventStoreTest.CACHED_EVENTS, EmbeddedEventStoreTest.FETCH_DELAY, EmbeddedEventStoreTest.CLEANUP_DELAY, doNotOptimizeEventConsumption);
        TrackingEventStream eventStream = testSubject.openStream(null);
        TestCase.assertFalse(eventStream.hasNextAvailable());// There are no events published yet, so should be false

        testSubject.publish(EventStoreTestUtils.createEvents(5));// Publish some events which should be returned to the stream

        Assert.assertTrue(eventStream.hasNextAvailable());// There are new events, so should be true

        // Consume until the end
        while (eventStream.hasNextAvailable()) {
            eventStream.nextAvailable();
        } 
        TestCase.assertFalse(eventStream.hasNextAvailable());// Should have no events anymore

    }

    private static class SynchronizedBooleanAnswer implements Answer<Boolean> {
        private final boolean answer;

        private SynchronizedBooleanAnswer(boolean answer) {
            this.answer = answer;
        }

        @Override
        public synchronized Boolean answer(InvocationOnMock invocation) {
            return answer;
        }
    }
}

