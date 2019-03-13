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


import java.time.Instant;
import java.util.Arrays;
import java.util.function.Predicate;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericDomainEventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.junit.Test;
import org.mockito.Mockito;


public class FilteringEventStorageEngineTest {
    private Predicate<EventMessage<?>> filter;

    private EventStorageEngine mockStorage;

    private FilteringEventStorageEngine testSubject;

    @Test
    public void testEventsFromArrayMatchingAreForwarded() {
        EventMessage<String> event1 = GenericEventMessage.asEventMessage("accept");
        EventMessage<String> event2 = GenericEventMessage.asEventMessage("fail");
        EventMessage<String> event3 = GenericEventMessage.asEventMessage("accept");
        testSubject.appendEvents(event1, event2, event3);
        Mockito.verify(mockStorage).appendEvents(Arrays.asList(event1, event3));
    }

    @Test
    public void testEventsFromListMatchingAreForwarded() {
        EventMessage<String> event1 = GenericEventMessage.asEventMessage("accept");
        EventMessage<String> event2 = GenericEventMessage.asEventMessage("fail");
        EventMessage<String> event3 = GenericEventMessage.asEventMessage("accept");
        testSubject.appendEvents(Arrays.asList(event1, event2, event3));
        Mockito.verify(mockStorage).appendEvents(Arrays.asList(event1, event3));
    }

    @Test
    public void testStoreSnapshotDelegated() {
        GenericDomainEventMessage<Object> snapshot = new GenericDomainEventMessage("type", "id", 0, "fail");
        testSubject.storeSnapshot(snapshot);
        Mockito.verify(mockStorage).storeSnapshot(snapshot);
    }

    @Test
    public void testCreateTailTokenDelegated() {
        testSubject.createTailToken();
        Mockito.verify(mockStorage).createTailToken();
    }

    @Test
    public void testCreateHeadTokenDelegated() {
        testSubject.createHeadToken();
        Mockito.verify(mockStorage).createHeadToken();
    }

    @Test
    public void testCreateTokenAtDelegated() {
        Instant now = Instant.now();
        testSubject.createTokenAt(now);
        Mockito.verify(mockStorage).createTokenAt(now);
    }
}

