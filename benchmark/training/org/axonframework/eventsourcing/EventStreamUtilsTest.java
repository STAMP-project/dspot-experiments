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
package org.axonframework.eventsourcing;


import NoOpEventUpcaster.INSTANCE;
import java.util.Objects;
import java.util.stream.Stream;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcasterChain;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rene de Waele
 */
public class EventStreamUtilsTest {
    private Serializer serializer;

    @Test
    public void testDomainEventStream_lastSequenceNumberEqualToLastProcessedEntry() {
        DomainEventStream eventStream = EventStreamUtils.upcastAndDeserializeDomainEvents(Stream.of(EventStreamUtilsTest.createEntry(1)), serializer, INSTANCE);
        Assert.assertNull(eventStream.getLastSequenceNumber());
        eventStream.forEachRemaining(Objects::requireNonNull);
        Assert.assertEquals(Long.valueOf(1L), eventStream.getLastSequenceNumber());
    }

    @Test
    public void testDomainEventStream_lastSequenceNumberEqualToLastProcessedEntryAfterIgnoringLastEntry() {
        DomainEventStream eventStream = EventStreamUtils.upcastAndDeserializeDomainEvents(Stream.of(EventStreamUtilsTest.createEntry(1), EventStreamUtilsTest.createEntry(2), EventStreamUtilsTest.createEntry(3)), serializer, new EventUpcasterChain(( e) -> e.filter(( entry) -> (entry.getSequenceNumber().get()) < 2L)));
        Assert.assertNull(eventStream.getLastSequenceNumber());
        Assert.assertTrue(eventStream.hasNext());
        eventStream.forEachRemaining(Objects::requireNonNull);
        Assert.assertEquals(Long.valueOf(3L), eventStream.getLastSequenceNumber());
    }

    @Test
    public void testDomainEventStream_lastSequenceNumberEqualToLastProcessedEntryAfterUpcastingToEmptyStream() {
        DomainEventStream eventStream = EventStreamUtils.upcastAndDeserializeDomainEvents(Stream.of(EventStreamUtilsTest.createEntry(1)), serializer, new EventUpcasterChain(( s) -> s.filter(( e) -> false)));
        Assert.assertNull(eventStream.getLastSequenceNumber());
        Assert.assertFalse(eventStream.hasNext());
        eventStream.forEachRemaining(Objects::requireNonNull);
        Assert.assertEquals(Long.valueOf(1L), eventStream.getLastSequenceNumber());
    }

    @Test(expected = NullPointerException.class)
    public void testDomainEventStream_nullPointerExceptionOnEmptyEventStream() {
        DomainEventStream eventStream = EventStreamUtils.upcastAndDeserializeDomainEvents(Stream.empty(), serializer, INSTANCE);
        long lastSequenceNumber = eventStream.getLastSequenceNumber();
    }
}

