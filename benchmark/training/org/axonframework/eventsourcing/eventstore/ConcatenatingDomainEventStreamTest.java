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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.axonframework.eventhandling.DomainEventMessage;
import org.junit.Assert;
import org.junit.Test;


public class ConcatenatingDomainEventStreamTest {
    private DomainEventMessage event1;

    private DomainEventMessage event2;

    private DomainEventMessage event3;

    private DomainEventMessage event4;

    private DomainEventMessage event5;

    @Test
    public void testForEachRemaining() {
        List<DomainEventMessage> expectedMessages = Arrays.asList(event1, event2, event3, event4, event5);
        DomainEventStream concat = // Initial stream - add all elements
        // No overlap with previous stream - add all elements
        // Complete overlap with previous stream - add no elements
        // Partial overlap with previous stream - add some elements
        new ConcatenatingDomainEventStream(DomainEventStream.of(event1, event2), DomainEventStream.of(event3, event4), DomainEventStream.of(event3, event4), DomainEventStream.of(event4, event5));
        List<DomainEventMessage<?>> actualMessages = new ArrayList<>();
        concat.forEachRemaining(actualMessages::add);
        Assert.assertEquals(expectedMessages, actualMessages);
    }

    @Test
    public void testForEachRemainingKeepsDuplicateSequenceIdEventsInSameStream() {
        List<DomainEventMessage> expectedMessages = Arrays.asList(event1, event1, event2, event3, event4, event4, event5);
        DomainEventStream concat = new ConcatenatingDomainEventStream(DomainEventStream.of(event1, event1, event2), DomainEventStream.of(event2, event3), DomainEventStream.empty(), DomainEventStream.of(event3, event3), DomainEventStream.of(event3, event4, event4), DomainEventStream.of(event4, event5));
        List<DomainEventMessage<?>> actualMessages = new ArrayList<>();
        concat.forEachRemaining(actualMessages::add);
        Assert.assertEquals(expectedMessages, actualMessages);
    }

    @Test
    public void testConcatSkipsDuplicateEvents() {
        DomainEventStream concat = new ConcatenatingDomainEventStream(DomainEventStream.of(event1, event2), DomainEventStream.of(event2, event3), DomainEventStream.of(event3, event4));
        Assert.assertTrue(concat.hasNext());
        Assert.assertSame(event1.getPayload(), concat.next().getPayload());
        Assert.assertSame(event2.getPayload(), concat.next().getPayload());
        Assert.assertSame(event3.getPayload(), concat.peek().getPayload());
        Assert.assertSame(event3.getPayload(), concat.next().getPayload());
        Assert.assertSame(event4.getPayload(), concat.peek().getPayload());
        Assert.assertSame(event4.getPayload(), concat.next().getPayload());
        Assert.assertFalse(concat.hasNext());
    }

    @Test
    public void testLastKnownSequenceReturnsTheLastEventItsSequence() {
        DomainEventStream concat = new ConcatenatingDomainEventStream(DomainEventStream.of(event1), DomainEventStream.of(event2, event3), DomainEventStream.of(event4, event5));
        // This is still null if we have not traversed the stream yet.
        Assert.assertNull(concat.getLastSequenceNumber());
        Assert.assertTrue(concat.hasNext());
        Assert.assertSame(event1.getPayload(), concat.next().getPayload());
        Assert.assertEquals(((Long) (0L)), concat.getLastSequenceNumber());
        Assert.assertSame(event2.getPayload(), concat.next().getPayload());
        Assert.assertEquals(((Long) (1L)), concat.getLastSequenceNumber());
        Assert.assertSame(event3.getPayload(), concat.next().getPayload());
        Assert.assertEquals(((Long) (2L)), concat.getLastSequenceNumber());
        Assert.assertSame(event4.getPayload(), concat.next().getPayload());
        Assert.assertEquals(((Long) (3L)), concat.getLastSequenceNumber());
        Assert.assertSame(event5.getPayload(), concat.next().getPayload());
        Assert.assertEquals(((Long) (4L)), concat.getLastSequenceNumber());
        Assert.assertFalse(concat.hasNext());
    }

    @Test
    public void testLastKnownSequenceReturnsTheLastEventItsSequenceEventIfEventsHaveGaps() {
        DomainEventStream concat = new ConcatenatingDomainEventStream(DomainEventStream.of(event1, event3), DomainEventStream.of(event4, event5));
        // This is still null if we have not traversed the stream yet.
        Assert.assertNull(concat.getLastSequenceNumber());
        Assert.assertTrue(concat.hasNext());
        Assert.assertSame(event1.getPayload(), concat.next().getPayload());
        Assert.assertEquals(((Long) (0L)), concat.getLastSequenceNumber());
        Assert.assertSame(event3.getPayload(), concat.next().getPayload());
        Assert.assertEquals(((Long) (2L)), concat.getLastSequenceNumber());
        Assert.assertSame(event4.getPayload(), concat.next().getPayload());
        Assert.assertEquals(((Long) (3L)), concat.getLastSequenceNumber());
        Assert.assertSame(event5.getPayload(), concat.next().getPayload());
        Assert.assertEquals(((Long) (4L)), concat.getLastSequenceNumber());
        Assert.assertFalse(concat.hasNext());
    }
}

