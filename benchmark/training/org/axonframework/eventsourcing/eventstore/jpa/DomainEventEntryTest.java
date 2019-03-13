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
package org.axonframework.eventsourcing.eventstore.jpa;


import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class DomainEventEntryTest {
    @Test
    public void testDomainEventEntry_WrapEventsCorrectly() {
        Serializer serializer = XStreamSerializer.builder().build();
        String payload = "Payload";
        MetaData metaData = new MetaData(Collections.singletonMap("Key", "Value"));
        DomainEventMessage<String> event = new org.axonframework.eventhandling.GenericDomainEventMessage("type", UUID.randomUUID().toString(), 2L, payload, metaData, UUID.randomUUID().toString(), Instant.now());
        DomainEventEntry eventEntry = new DomainEventEntry(event, serializer);
        Assert.assertEquals(event.getAggregateIdentifier(), eventEntry.getAggregateIdentifier());
        Assert.assertEquals(event.getSequenceNumber(), eventEntry.getSequenceNumber());
        Assert.assertEquals(event.getTimestamp(), eventEntry.getTimestamp());
        Assert.assertEquals(payload, serializer.deserialize(eventEntry.getPayload()));
        Assert.assertEquals(metaData, serializer.deserialize(eventEntry.getMetaData()));
        Assert.assertEquals(byte[].class, eventEntry.getPayload().getContentType());
    }
}

