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


import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.TrackingToken;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class SequenceEventStorageEngineTest {
    private EventStorageEngine activeStorage;

    private EventStorageEngine historicStorage;

    private SequenceEventStorageEngine testSubject;

    @Test
    public void testPublishEventsSendsToActiveStorageOnly() {
        List<EventMessage<Object>> events = Collections.singletonList(GenericEventMessage.asEventMessage("test"));
        testSubject.appendEvents(events);
        Mockito.verify(historicStorage, Mockito.never()).appendEvents(ArgumentMatchers.anyList());
        Mockito.verify(activeStorage).appendEvents(events);
    }

    @Test
    public void testAggregateEventsAreReadFromHistoricThenActive() {
        DomainEventMessage<String> event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 0, "test1");
        DomainEventMessage<String> event2 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 1, "test2");
        Mockito.when(historicStorage.readEvents(ArgumentMatchers.eq("aggregate"), ArgumentMatchers.anyLong())).thenReturn(DomainEventStream.of(event1));
        Mockito.when(activeStorage.readEvents(ArgumentMatchers.eq("aggregate"), ArgumentMatchers.anyLong())).thenReturn(DomainEventStream.of(event2));
        DomainEventStream actual = testSubject.readEvents("aggregate", 0);
        Assert.assertEquals(0L, ((long) (actual.getLastSequenceNumber())));
        Assert.assertTrue(actual.hasNext());
        Assert.assertSame(event1, actual.peek());
        Assert.assertSame(event1, actual.next());
        Mockito.verify(activeStorage, Mockito.never()).readEvents(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
        Assert.assertTrue(actual.hasNext());
        Assert.assertSame(event2, actual.peek());
        Assert.assertSame(event2, actual.next());
        Assert.assertEquals(1L, ((long) (actual.getLastSequenceNumber())));
        InOrder inOrder = Mockito.inOrder(historicStorage, activeStorage);
        inOrder.verify(historicStorage).readEvents("aggregate", 0);
        inOrder.verify(activeStorage).readEvents("aggregate", 1);
        Assert.assertFalse(actual.hasNext());
    }

    @Test
    public void testAggregateEventsAreReadFromActiveWhenNoHistoricEventsAvailable() {
        DomainEventMessage<String> event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 0, "test1");
        DomainEventMessage<String> event2 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 1, "test2");
        Mockito.when(historicStorage.readEvents(ArgumentMatchers.eq("aggregate"), ArgumentMatchers.anyLong())).thenReturn(DomainEventStream.empty());
        Mockito.when(activeStorage.readEvents(ArgumentMatchers.eq("aggregate"), ArgumentMatchers.anyLong())).thenReturn(DomainEventStream.of(event1, event2));
        DomainEventStream actual = testSubject.readEvents("aggregate", 0);
        Mockito.verify(activeStorage, Mockito.never()).readEvents(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
        Assert.assertSame(event1, actual.peek());
        Assert.assertSame(event1, actual.next());
        Assert.assertSame(event2, actual.peek());
        Assert.assertSame(event2, actual.next());
        InOrder inOrder = Mockito.inOrder(historicStorage, activeStorage);
        inOrder.verify(historicStorage).readEvents("aggregate", 0);
        inOrder.verify(activeStorage).readEvents("aggregate", 0);
        Assert.assertFalse(actual.hasNext());
    }

    @Test
    public void testSnapshotsStoredInActiveStorage() {
        DomainEventMessage<String> event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 0, "test1");
        testSubject.storeSnapshot(event1);
        Mockito.verify(activeStorage).storeSnapshot(event1);
        Mockito.verify(historicStorage, Mockito.never()).storeSnapshot(ArgumentMatchers.any());
    }

    @Test
    public void testEventStreamedFromHistoricThenActive() {
        DomainEventMessage<String> event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 0, "test1");
        DomainEventMessage<String> event2 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 1, "test2");
        TrackingToken token1 = new GlobalSequenceTrackingToken(1);
        TrackingToken token2 = new GlobalSequenceTrackingToken(2);
        TrackedEventMessage<?> trackedEvent1 = new org.axonframework.eventhandling.GenericTrackedDomainEventMessage(token1, event1);
        TrackedEventMessage<?> trackedEvent2 = new org.axonframework.eventhandling.GenericTrackedDomainEventMessage(token2, event2);
        Mockito.doReturn(Stream.of(trackedEvent1)).when(historicStorage).readEvents(ArgumentMatchers.any(TrackingToken.class), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(Stream.of(trackedEvent2)).when(activeStorage).readEvents(ArgumentMatchers.any(TrackingToken.class), ArgumentMatchers.anyBoolean());
        GlobalSequenceTrackingToken startToken = new GlobalSequenceTrackingToken(0);
        Stream<? extends TrackedEventMessage<?>> actual = testSubject.readEvents(startToken, true);
        List<? extends TrackedEventMessage<?>> actualList = actual.collect(Collectors.toList());
        Assert.assertEquals(2, actualList.size());
        Assert.assertEquals(Arrays.asList(trackedEvent1, trackedEvent2), actualList);
        Mockito.verify(historicStorage).readEvents(startToken, true);
        Mockito.verify(activeStorage).readEvents(token1, true);
    }

    @Test
    public void testSnapshotReadFromActiveThenHistoric() {
        DomainEventMessage<String> event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 0, "test1");
        Mockito.when(historicStorage.readSnapshot("aggregate")).thenReturn(Optional.of(event1));
        Mockito.when(activeStorage.readSnapshot("aggregate")).thenReturn(Optional.empty());
        Optional<DomainEventMessage<?>> actual = testSubject.readSnapshot("aggregate");
        Assert.assertTrue(actual.isPresent());
        Assert.assertSame(event1, actual.get());
        InOrder inOrder = Mockito.inOrder(historicStorage, activeStorage);
        inOrder.verify(activeStorage).readSnapshot("aggregate");
        inOrder.verify(historicStorage).readSnapshot("aggregate");
    }

    @Test
    public void testSnapshotReadFromActive() {
        DomainEventMessage<String> event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 0, "test1");
        DomainEventMessage<String> event2 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", "aggregate", 1, "test2");
        Mockito.when(historicStorage.readSnapshot("aggregate")).thenReturn(Optional.of(event2));
        Mockito.when(activeStorage.readSnapshot("aggregate")).thenReturn(Optional.of(event1));
        Optional<DomainEventMessage<?>> actual = testSubject.readSnapshot("aggregate");
        Assert.assertTrue(actual.isPresent());
        Assert.assertSame(event1, actual.get());
        InOrder inOrder = Mockito.inOrder(historicStorage, activeStorage);
        inOrder.verify(activeStorage).readSnapshot("aggregate");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCreateTailToken() {
        testSubject.createTailToken();
        Mockito.verify(historicStorage).createTailToken();
    }

    @Test
    public void testCreateHeadToken() {
        testSubject.createHeadToken();
        Mockito.verify(activeStorage).createHeadToken();
    }

    @Test
    public void testCreateTokenAtWhenIsPresentInActiveStorage() {
        Instant now = Instant.now();
        TrackingToken mockTrackingToken = new GlobalSequenceTrackingToken(3);
        Mockito.when(activeStorage.createTokenAt(now)).thenReturn(mockTrackingToken);
        TrackingToken tokenAt = testSubject.createTokenAt(now);
        Assert.assertEquals(mockTrackingToken, tokenAt);
        Mockito.verify(historicStorage, Mockito.times(0)).createTokenAt(now);
    }

    @Test
    public void testCreateTokenAtWhenIsNotPresentInActiveStorage() {
        Instant now = Instant.now();
        TrackingToken mockTrackingToken = new GlobalSequenceTrackingToken(3);
        Mockito.when(activeStorage.createTokenAt(now)).thenReturn(null);
        Mockito.when(historicStorage.createTokenAt(now)).thenReturn(mockTrackingToken);
        TrackingToken tokenAt = testSubject.createTokenAt(now);
        Assert.assertEquals(mockTrackingToken, tokenAt);
    }
}

