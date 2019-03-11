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
import java.util.UUID;
import java.util.stream.Collectors;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventsourcing.utils.EventStoreTestUtils;
import org.axonframework.messaging.MetaData;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Rene de Waele
 */
@Transactional
public abstract class EventStorageEngineTest {
    private EventStorageEngine testSubject;

    @Test
    public void testStoreAndLoadEvents() {
        testSubject.appendEvents(EventStoreTestUtils.createEvents(4));
        Assert.assertEquals(4, testSubject.readEvents(EventStoreTestUtils.AGGREGATE).asStream().count());
        testSubject.appendEvents(EventStoreTestUtils.createEvent("otherAggregate", 0));
        Assert.assertEquals(4, testSubject.readEvents(EventStoreTestUtils.AGGREGATE).asStream().count());
        Assert.assertEquals(1, testSubject.readEvents("otherAggregate").asStream().count());
    }

    @Test
    public void testStoreAndLoadEventsArray() {
        testSubject.appendEvents(EventStoreTestUtils.createEvent(0), EventStoreTestUtils.createEvent(1));
        Assert.assertEquals(2, testSubject.readEvents(EventStoreTestUtils.AGGREGATE).asStream().count());
    }

    @Test
    public void testStoreAndLoadApplicationEvent() {
        testSubject.appendEvents(new org.axonframework.eventhandling.GenericEventMessage("application event", MetaData.with("key", "value")));
        Assert.assertEquals(1, testSubject.readEvents(null, false).count());
        EventMessage<?> message = testSubject.readEvents(null, false).findFirst().get();
        Assert.assertEquals("application event", message.getPayload());
        Assert.assertEquals(MetaData.with("key", "value"), message.getMetaData());
    }

    @Test
    public void testReturnedEventMessageBehavior() {
        testSubject.appendEvents(EventStoreTestUtils.createEvent().withMetaData(Collections.singletonMap("key", "value")));
        DomainEventMessage<?> messageWithMetaData = testSubject.readEvents(EventStoreTestUtils.AGGREGATE).next();
        // / we make sure persisted events have the same MetaData alteration logic
        DomainEventMessage<?> altered = messageWithMetaData.withMetaData(Collections.singletonMap("key2", "value"));
        DomainEventMessage<?> combined = messageWithMetaData.andMetaData(Collections.singletonMap("key2", "value"));
        Assert.assertTrue(altered.getMetaData().containsKey("key2"));
        altered.getPayload();
        Assert.assertFalse(altered.getMetaData().containsKey("key"));
        Assert.assertTrue(altered.getMetaData().containsKey("key2"));
        Assert.assertTrue(combined.getMetaData().containsKey("key"));
        Assert.assertTrue(combined.getMetaData().containsKey("key2"));
        Assert.assertNotNull(messageWithMetaData.getPayload());
        Assert.assertNotNull(messageWithMetaData.getMetaData());
        Assert.assertFalse(messageWithMetaData.getMetaData().isEmpty());
    }

    @Test
    public void testLoadNonExistent() {
        Assert.assertEquals(0L, testSubject.readEvents(UUID.randomUUID().toString()).asStream().count());
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testReadPartialStream() {
        testSubject.appendEvents(EventStoreTestUtils.createEvents(5));
        Assert.assertEquals(2L, testSubject.readEvents(EventStoreTestUtils.AGGREGATE, 2).asStream().findFirst().get().getSequenceNumber());
        Assert.assertEquals(4L, testSubject.readEvents(EventStoreTestUtils.AGGREGATE, 2).asStream().reduce(( a, b) -> b).get().getSequenceNumber());
        Assert.assertEquals(3L, testSubject.readEvents(EventStoreTestUtils.AGGREGATE, 2).asStream().count());
    }

    @Test
    public void testStoreAndLoadSnapshot() {
        testSubject.storeSnapshot(EventStoreTestUtils.createEvent(0));
        testSubject.storeSnapshot(EventStoreTestUtils.createEvent(1));
        testSubject.storeSnapshot(EventStoreTestUtils.createEvent(3));
        testSubject.storeSnapshot(EventStoreTestUtils.createEvent(2));
        Assert.assertTrue(testSubject.readSnapshot(EventStoreTestUtils.AGGREGATE).isPresent());
        Assert.assertEquals(3, testSubject.readSnapshot(EventStoreTestUtils.AGGREGATE).get().getSequenceNumber());
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testLoadTrackedEvents() throws InterruptedException {
        testSubject.appendEvents(EventStoreTestUtils.createEvents(4));
        Assert.assertEquals(4, testSubject.readEvents(null, false).count());
        // give the clock some time to make sure the last message is really last
        Thread.sleep(10);
        DomainEventMessage<?> eventMessage = EventStoreTestUtils.createEvent("otherAggregate", 0);
        testSubject.appendEvents(eventMessage);
        Assert.assertEquals(5, testSubject.readEvents(null, false).count());
        Assert.assertEquals(eventMessage.getIdentifier(), testSubject.readEvents(null, false).reduce(( a, b) -> b).get().getIdentifier());
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void testLoadPartialStreamOfTrackedEvents() {
        List<DomainEventMessage<?>> events = EventStoreTestUtils.createEvents(4);
        testSubject.appendEvents(events);
        TrackingToken token = testSubject.readEvents(null, false).findFirst().get().trackingToken();
        Assert.assertEquals(3, testSubject.readEvents(token, false).count());
        Assert.assertEquals(events.subList(1, events.size()).stream().map(EventMessage::getIdentifier).collect(Collectors.toList()), testSubject.readEvents(token, false).map(EventMessage::getIdentifier).collect(Collectors.toList()));
    }

    @Test
    public void testCreateTailToken() {
        DomainEventMessage<String> event1 = EventStoreTestUtils.createEvent(0, Instant.parse("2007-12-03T10:15:00.00Z"));
        DomainEventMessage<String> event2 = EventStoreTestUtils.createEvent(1, Instant.parse("2007-12-03T10:15:40.00Z"));
        DomainEventMessage<String> event3 = EventStoreTestUtils.createEvent(2, Instant.parse("2007-12-03T10:15:35.00Z"));
        testSubject.appendEvents(event1, event2, event3);
        TrackingToken headToken = testSubject.createTailToken();
        List<EventMessage<?>> readEvents = testSubject.readEvents(headToken, false).collect(Collectors.toList());
        assertEventStreamsById(Arrays.asList(event1, event2, event3), readEvents);
    }

    @Test
    public void testCreateHeadToken() {
        DomainEventMessage<String> event1 = EventStoreTestUtils.createEvent(0, Instant.parse("2007-12-03T10:15:00.00Z"));
        DomainEventMessage<String> event2 = EventStoreTestUtils.createEvent(1, Instant.parse("2007-12-03T10:15:40.00Z"));
        DomainEventMessage<String> event3 = EventStoreTestUtils.createEvent(2, Instant.parse("2007-12-03T10:15:35.00Z"));
        testSubject.appendEvents(event1, event2, event3);
        TrackingToken headToken = testSubject.createHeadToken();
        List<EventMessage<?>> readEvents = testSubject.readEvents(headToken, false).collect(Collectors.toList());
        Assert.assertTrue(readEvents.isEmpty());
    }

    @Test
    public void testCreateTokenAt() {
        DomainEventMessage<String> event1 = EventStoreTestUtils.createEvent(0, Instant.parse("2007-12-03T10:15:00.00Z"));
        DomainEventMessage<String> event2 = EventStoreTestUtils.createEvent(1, Instant.parse("2007-12-03T10:15:40.00Z"));
        DomainEventMessage<String> event3 = EventStoreTestUtils.createEvent(2, Instant.parse("2007-12-03T10:15:35.00Z"));
        testSubject.appendEvents(event1, event2, event3);
        TrackingToken tokenAt = testSubject.createTokenAt(Instant.parse("2007-12-03T10:15:30.00Z"));
        List<EventMessage<?>> readEvents = testSubject.readEvents(tokenAt, false).collect(Collectors.toList());
        assertEventStreamsById(Arrays.asList(event2, event3), readEvents);
    }

    @Test
    public void testCreateTokenAtExactTime() {
        DomainEventMessage<String> event1 = EventStoreTestUtils.createEvent(0, Instant.parse("2007-12-03T10:15:30.00Z"));
        DomainEventMessage<String> event2 = EventStoreTestUtils.createEvent(1, Instant.parse("2007-12-03T10:15:40.00Z"));
        DomainEventMessage<String> event3 = EventStoreTestUtils.createEvent(2, Instant.parse("2007-12-03T10:15:35.00Z"));
        testSubject.appendEvents(event1, event2, event3);
        TrackingToken tokenAt = testSubject.createTokenAt(Instant.parse("2007-12-03T10:15:30.00Z"));
        List<EventMessage<?>> readEvents = testSubject.readEvents(tokenAt, false).collect(Collectors.toList());
        assertEventStreamsById(Arrays.asList(event1, event2, event3), readEvents);
    }

    @Test
    public void testCreateTokenWithUnorderedEvents() {
        DomainEventMessage<String> event1 = EventStoreTestUtils.createEvent(0, Instant.parse("2007-12-03T10:15:30.00Z"));
        DomainEventMessage<String> event2 = EventStoreTestUtils.createEvent(1, Instant.parse("2007-12-03T10:15:40.00Z"));
        DomainEventMessage<String> event3 = EventStoreTestUtils.createEvent(2, Instant.parse("2007-12-03T10:15:50.00Z"));
        DomainEventMessage<String> event4 = EventStoreTestUtils.createEvent(3, Instant.parse("2007-12-03T10:15:45.00Z"));
        DomainEventMessage<String> event5 = EventStoreTestUtils.createEvent(4, Instant.parse("2007-12-03T10:15:42.00Z"));
        testSubject.appendEvents(event1, event2, event3, event4, event5);
        TrackingToken tokenAt = testSubject.createTokenAt(Instant.parse("2007-12-03T10:15:45.00Z"));
        List<EventMessage<?>> readEvents = testSubject.readEvents(tokenAt, false).collect(Collectors.toList());
        assertEventStreamsById(Arrays.asList(event3, event4, event5), readEvents);
    }
}

