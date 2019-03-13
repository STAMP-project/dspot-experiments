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
package com.saasovation.common.notification;


import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.event.TestableDomainEvent;
import com.saasovation.common.event.TestableNavigableDomainEvent;
import junit.framework.TestCase;


public class NotificationReaderTest extends TestCase {
    public NotificationReaderTest() {
        super();
    }

    public void testReadBasicProperties() throws Exception {
        DomainEvent domainEvent = new TestableDomainEvent(100, "testing");
        Notification notification = new Notification(1, domainEvent);
        NotificationSerializer serializer = NotificationSerializer.instance();
        String serializedNotification = serializer.serialize(notification);
        NotificationReader reader = new NotificationReader(serializedNotification);
        TestCase.assertEquals(1L, reader.notificationId());
        TestCase.assertEquals("1", reader.notificationIdAsString());
        TestCase.assertEquals(domainEvent.occurredOn(), reader.occurredOn());
        TestCase.assertEquals(notification.typeName(), reader.typeName());
        TestCase.assertEquals(notification.version(), reader.version());
        TestCase.assertEquals(domainEvent.eventVersion(), reader.version());
    }

    public void testReadDomainEventProperties() throws Exception {
        TestableDomainEvent domainEvent = new TestableDomainEvent(100, "testing");
        Notification notification = new Notification(1, domainEvent);
        NotificationSerializer serializer = NotificationSerializer.instance();
        String serializedNotification = serializer.serialize(notification);
        NotificationReader reader = new NotificationReader(serializedNotification);
        TestCase.assertEquals(("" + (domainEvent.eventVersion())), reader.eventStringValue("eventVersion"));
        TestCase.assertEquals(("" + (domainEvent.eventVersion())), reader.eventStringValue("/eventVersion"));
        TestCase.assertEquals(("" + (domainEvent.id())), reader.eventStringValue("id"));
        TestCase.assertEquals(("" + (domainEvent.id())), reader.eventStringValue("/id"));
        TestCase.assertEquals(("" + (domainEvent.name())), reader.eventStringValue("name"));
        TestCase.assertEquals(("" + (domainEvent.name())), reader.eventStringValue("/name"));
        TestCase.assertEquals(("" + (domainEvent.occurredOn().getTime())), reader.eventStringValue("occurredOn"));
        TestCase.assertEquals(("" + (domainEvent.occurredOn().getTime())), reader.eventStringValue("/occurredOn"));
    }

    public void testReadNestedDomainEventProperties() throws Exception {
        TestableNavigableDomainEvent domainEvent = new TestableNavigableDomainEvent(100, "testing");
        Notification notification = new Notification(1, domainEvent);
        NotificationSerializer serializer = NotificationSerializer.instance();
        String serializedNotification = serializer.serialize(notification);
        NotificationReader reader = new NotificationReader(serializedNotification);
        TestCase.assertEquals(("" + (domainEvent.eventVersion())), reader.eventStringValue("eventVersion"));
        TestCase.assertEquals(("" + (domainEvent.eventVersion())), reader.eventStringValue("/eventVersion"));
        TestCase.assertEquals(domainEvent.eventVersion(), reader.eventIntegerValue("eventVersion").intValue());
        TestCase.assertEquals(domainEvent.eventVersion(), reader.eventIntegerValue("/eventVersion").intValue());
        TestCase.assertEquals(("" + (domainEvent.nestedEvent().eventVersion())), reader.eventStringValue("nestedEvent", "eventVersion"));
        TestCase.assertEquals(("" + (domainEvent.nestedEvent().eventVersion())), reader.eventStringValue("/nestedEvent/eventVersion"));
        TestCase.assertEquals(domainEvent.nestedEvent().eventVersion(), reader.eventIntegerValue("nestedEvent", "eventVersion").intValue());
        TestCase.assertEquals(domainEvent.nestedEvent().eventVersion(), reader.eventIntegerValue("/nestedEvent/eventVersion").intValue());
        TestCase.assertEquals(("" + (domainEvent.nestedEvent().id())), reader.eventStringValue("nestedEvent", "id"));
        TestCase.assertEquals(("" + (domainEvent.nestedEvent().id())), reader.eventStringValue("/nestedEvent/id"));
        TestCase.assertEquals(domainEvent.nestedEvent().id(), reader.eventLongValue("nestedEvent", "id").longValue());
        TestCase.assertEquals(domainEvent.nestedEvent().id(), reader.eventLongValue("/nestedEvent/id").longValue());
        TestCase.assertEquals(("" + (domainEvent.nestedEvent().name())), reader.eventStringValue("nestedEvent", "name"));
        TestCase.assertEquals(("" + (domainEvent.nestedEvent().name())), reader.eventStringValue("/nestedEvent/name"));
        TestCase.assertEquals(("" + (domainEvent.nestedEvent().occurredOn().getTime())), reader.eventStringValue("nestedEvent", "occurredOn"));
        TestCase.assertEquals(("" + (domainEvent.nestedEvent().occurredOn().getTime())), reader.eventStringValue("/nestedEvent/occurredOn"));
        TestCase.assertEquals(domainEvent.nestedEvent().occurredOn(), reader.eventDateValue("nestedEvent", "occurredOn"));
        TestCase.assertEquals(domainEvent.nestedEvent().occurredOn(), reader.eventDateValue("/nestedEvent/occurredOn"));
        TestCase.assertEquals(("" + (domainEvent.occurredOn().getTime())), reader.eventStringValue("occurredOn"));
        TestCase.assertEquals(("" + (domainEvent.occurredOn().getTime())), reader.eventStringValue("/occurredOn"));
        TestCase.assertEquals(domainEvent.occurredOn(), reader.eventDateValue("occurredOn"));
        TestCase.assertEquals(domainEvent.occurredOn(), reader.eventDateValue("/occurredOn"));
    }

    public void testDotNotation() throws Exception {
        TestableNavigableDomainEvent domainEvent = new TestableNavigableDomainEvent(100, "testing");
        Notification notification = new Notification(1, domainEvent);
        NotificationSerializer serializer = NotificationSerializer.instance();
        String serializedNotification = serializer.serialize(notification);
        NotificationReader reader = new NotificationReader(serializedNotification);
        TestCase.assertEquals(("" + (domainEvent.nestedEvent().eventVersion())), reader.eventStringValue("nestedEvent.eventVersion"));
        TestCase.assertEquals(domainEvent.nestedEvent().eventVersion(), reader.eventIntegerValue("nestedEvent.eventVersion").intValue());
    }

    public void testReadBogusProperties() throws Exception {
        TestableNavigableDomainEvent domainEvent = new TestableNavigableDomainEvent(100L, "testing");
        Notification notification = new Notification(1, domainEvent);
        NotificationSerializer serializer = NotificationSerializer.instance();
        String serializedNotification = serializer.serialize(notification);
        NotificationReader reader = new NotificationReader(serializedNotification);
        boolean mustThrow = false;
        try {
            reader.eventStringValue("eventVersion.version");
        } catch (Exception e) {
            mustThrow = true;
        }
        TestCase.assertTrue(mustThrow);
    }

    public void testReadNullProperties() throws Exception {
        TestableNullPropertyDomainEvent domainEvent = new TestableNullPropertyDomainEvent(100L, "testingNulls");
        Notification notification = new Notification(1, domainEvent);
        NotificationSerializer serializer = NotificationSerializer.instance();
        String serializedNotification = serializer.serialize(notification);
        NotificationReader reader = new NotificationReader(serializedNotification);
        TestCase.assertNull(reader.eventStringValue("textMustBeNull"));
        TestCase.assertNull(reader.eventStringValue("textMustBeNull2"));
        TestCase.assertNull(reader.eventIntegerValue("numberMustBeNull"));
        TestCase.assertNull(reader.eventStringValue("nested.nestedTextMustBeNull"));
        TestCase.assertNull(reader.eventStringValue("nullNested.nestedTextMustBeNull"));
        TestCase.assertNull(reader.eventStringValue("nested.nestedDeeply.nestedDeeplyTextMustBeNull"));
        TestCase.assertNull(reader.eventStringValue("nested.nestedDeeply.nestedDeeplyTextMustBeNull2"));
        TestCase.assertNull(reader.eventStringValue("nested.nullNestedDeeply.nestedDeeplyTextMustBeNull"));
        TestCase.assertNull(reader.eventStringValue("nested.nullNestedDeeply.nestedDeeplyTextMustBeNull2"));
    }
}

