/**
 * Copyright 2012,2013 Vaughn Vernon
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.saasovation.common.event;


import junit.framework.TestCase;


public class EventSerializerTest extends TestCase {
    public EventSerializerTest() {
        super();
    }

    public void testDefaultFormat() throws Exception {
        EventSerializer serializer = EventSerializer.instance();
        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));
        TestCase.assertTrue(serializedEvent.contains("\"id\""));
        TestCase.assertTrue(serializedEvent.contains("\"occurredOn\""));
        TestCase.assertFalse(serializedEvent.contains("\n"));
        TestCase.assertTrue(serializedEvent.contains("null"));
    }

    public void testCompact() throws Exception {
        EventSerializer serializer = new EventSerializer(true);
        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));
        TestCase.assertTrue(serializedEvent.contains("\"id\""));
        TestCase.assertTrue(serializedEvent.contains("\"occurredOn\""));
        TestCase.assertFalse(serializedEvent.contains("\n"));
        TestCase.assertFalse(serializedEvent.contains("null"));
    }

    public void testPrettyAndCompact() throws Exception {
        EventSerializer serializer = new EventSerializer(true, true);
        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));
        TestCase.assertTrue(serializedEvent.contains("\"id\""));
        TestCase.assertTrue(serializedEvent.contains("\"occurredOn\""));
        TestCase.assertTrue(serializedEvent.contains("\n"));
        TestCase.assertFalse(serializedEvent.contains("null"));
    }

    public void testDeserializeDefault() throws Exception {
        EventSerializer serializer = EventSerializer.instance();
        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));
        TestableDomainEvent event = serializer.deserialize(serializedEvent, TestableDomainEvent.class);
        TestCase.assertTrue(serializedEvent.contains("null"));
        TestCase.assertEquals(1, event.id());
        TestCase.assertEquals(null, event.name());
        TestCase.assertNotNull(event.occurredOn());
    }

    public void testDeserializeCompactNotNull() throws Exception {
        EventSerializer serializer = new EventSerializer(true);
        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, "test"));
        TestableDomainEvent event = serializer.deserialize(serializedEvent, TestableDomainEvent.class);
        TestCase.assertFalse(serializedEvent.contains("null"));
        TestCase.assertTrue(serializedEvent.contains("\"test\""));
        TestCase.assertEquals(1, event.id());
        TestCase.assertEquals("test", event.name());
        TestCase.assertNotNull(event.occurredOn());
    }

    public void testDeserializeCompactNull() throws Exception {
        EventSerializer serializer = new EventSerializer(true);
        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));
        TestableDomainEvent event = serializer.deserialize(serializedEvent, TestableDomainEvent.class);
        TestCase.assertFalse(serializedEvent.contains("null"));
        TestCase.assertEquals(1, event.id());
        TestCase.assertEquals(null, event.name());
        TestCase.assertNotNull(event.occurredOn());
    }

    public void testDeserializePrettyAndCompactNull() throws Exception {
        EventSerializer serializer = new EventSerializer(true, true);
        String serializedEvent = serializer.serialize(new TestableDomainEvent(1, null));
        TestableDomainEvent event = serializer.deserialize(serializedEvent, TestableDomainEvent.class);
        TestCase.assertFalse(serializedEvent.contains("null"));
        TestCase.assertTrue(serializedEvent.contains("\n"));
        TestCase.assertEquals(1, event.id());
        TestCase.assertEquals(null, event.name());
        TestCase.assertNotNull(event.occurredOn());
    }
}

