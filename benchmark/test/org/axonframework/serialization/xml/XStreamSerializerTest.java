/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.serialization.xml;


import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import nu.xom.Document;
import org.axonframework.serialization.Revision;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.SimpleSerializedObject;
import org.axonframework.utils.StubDomainEvent;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class XStreamSerializerTest {
    private XStreamSerializer testSubject;

    private static final String SPECIAL__CHAR__STRING = "Special chars: \'\"&;\n\\<>/\n\t";

    private static final String REGULAR_STRING = "Henk";

    private XStreamSerializerTest.TestEvent testEvent;

    @Test
    public void testSerializeAndDeserializeDomainEvent() {
        SerializedObject<byte[]> serializedEvent = testSubject.serialize(testEvent, byte[].class);
        Object actualResult = testSubject.deserialize(serializedEvent);
        Assert.assertTrue((actualResult instanceof XStreamSerializerTest.TestEvent));
        XStreamSerializerTest.TestEvent actualEvent = ((XStreamSerializerTest.TestEvent) (actualResult));
        Assert.assertEquals(testEvent, actualEvent);
    }

    @Test
    public void testSerializeAndDeserializeDomainEvent_WithXomUpcasters() {
        SerializedObject<Document> serializedEvent = testSubject.serialize(testEvent, Document.class);
        Object actualResult = testSubject.deserialize(serializedEvent);
        Assert.assertEquals(testEvent, actualResult);
    }

    @Test
    public void testSerializeAndDeserializeDomainEvent_WithDom4JUpcasters() {
        SerializedObject<org.dom4j.Document> serializedEvent = testSubject.serialize(testEvent, org.dom4j.Document.class);
        Object actualResult = testSubject.deserialize(serializedEvent);
        Assert.assertEquals(testEvent, actualResult);
    }

    @Test
    public void testSerializeAndDeserializeArray() {
        XStreamSerializerTest.TestEvent toSerialize = new XStreamSerializerTest.TestEvent("first");
        SerializedObject<String> serialized = testSubject.serialize(new XStreamSerializerTest.TestEvent[]{ toSerialize }, String.class);
        Class actualType = testSubject.classForType(serialized.getType());
        Assert.assertTrue(actualType.isArray());
        Assert.assertEquals(XStreamSerializerTest.TestEvent.class, actualType.getComponentType());
        XStreamSerializerTest.TestEvent[] actual = testSubject.deserialize(serialized);
        Assert.assertEquals(1, actual.length);
        Assert.assertEquals(toSerialize.getName(), actual[0].getName());
    }

    @Test
    public void testSerializeAndDeserializeList() {
        XStreamSerializerTest.TestEvent toSerialize = new XStreamSerializerTest.TestEvent("first");
        SerializedObject<String> serialized = testSubject.serialize(Collections.singletonList(toSerialize), String.class);
        List<XStreamSerializerTest.TestEvent> actual = testSubject.deserialize(serialized);
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(toSerialize.getName(), actual.get(0).getName());
    }

    @Test
    public void testDeserializeEmptyBytes() {
        Assert.assertEquals(Void.class, testSubject.classForType(SerializedType.emptyType()));
        Assert.assertNull(testSubject.deserialize(new SimpleSerializedObject(new byte[0], byte[].class, SerializedType.emptyType())));
    }

    @Test
    public void testPackageAlias() throws UnsupportedEncodingException {
        testSubject.addPackageAlias("axoneh", "org.axonframework.utils");
        testSubject.addPackageAlias("axon", "org.axonframework");
        SerializedObject<byte[]> serialized = testSubject.serialize(new StubDomainEvent(), byte[].class);
        String asString = new String(serialized.getData(), StandardCharsets.UTF_8);
        Assert.assertFalse(("Package name found in:" + asString), asString.contains("org.axonframework.domain"));
        StubDomainEvent deserialized = testSubject.deserialize(serialized);
        Assert.assertEquals(StubDomainEvent.class, deserialized.getClass());
        Assert.assertTrue(asString.contains("axoneh"));
    }

    @Test
    public void testDeserializeNullValue() {
        SerializedObject<byte[]> serializedNull = testSubject.serialize(null, byte[].class);
        Assert.assertEquals("empty", serializedNull.getType().getName());
        SimpleSerializedObject<byte[]> serializedNullString = new SimpleSerializedObject(serializedNull.getData(), byte[].class, testSubject.typeForClass(String.class));
        Assert.assertNull(testSubject.deserialize(serializedNull));
        Assert.assertNull(testSubject.deserialize(serializedNullString));
    }

    @Test
    public void testAlias() throws UnsupportedEncodingException {
        testSubject.addAlias("stub", StubDomainEvent.class);
        SerializedObject<byte[]> serialized = testSubject.serialize(new StubDomainEvent(), byte[].class);
        String asString = new String(serialized.getData(), StandardCharsets.UTF_8);
        Assert.assertFalse(asString.contains("org.axonframework.domain"));
        Assert.assertTrue(asString.contains("<stub"));
        StubDomainEvent deserialized = testSubject.deserialize(serialized);
        Assert.assertEquals(StubDomainEvent.class, deserialized.getClass());
    }

    @Test
    public void testFieldAlias() throws UnsupportedEncodingException {
        testSubject.addFieldAlias("relevantPeriod", XStreamSerializerTest.TestEvent.class, "period");
        SerializedObject<byte[]> serialized = testSubject.serialize(testEvent, byte[].class);
        String asString = new String(serialized.getData(), StandardCharsets.UTF_8);
        Assert.assertFalse(asString.contains("period"));
        Assert.assertTrue(asString.contains("<relevantPeriod"));
        XStreamSerializerTest.TestEvent deserialized = testSubject.deserialize(serialized);
        Assert.assertNotNull(deserialized);
    }

    @Test
    public void testRevisionNumber() {
        SerializedObject<byte[]> serialized = testSubject.serialize(new XStreamSerializerTest.RevisionSpecifiedEvent(), byte[].class);
        Assert.assertNotNull(serialized);
        Assert.assertEquals("2", serialized.getType().getRevision());
        Assert.assertEquals(XStreamSerializerTest.RevisionSpecifiedEvent.class.getName(), serialized.getType().getName());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testSerializedTypeUsesClassAlias() {
        testSubject.addAlias("rse", XStreamSerializerTest.RevisionSpecifiedEvent.class);
        SerializedObject<byte[]> serialized = testSubject.serialize(new XStreamSerializerTest.RevisionSpecifiedEvent(), byte[].class);
        Assert.assertNotNull(serialized);
        Assert.assertEquals("2", serialized.getType().getRevision());
        Assert.assertEquals("rse", serialized.getType().getName());
    }

    /**
     * Tests the scenario as described in <a href="http://code.google.com/p/axonframework/issues/detail?id=150">issue
     * #150</a>.
     */
    @Test
    public void testSerializeWithSpecialCharacters_WithDom4JUpcasters() {
        SerializedObject<byte[]> serialized = testSubject.serialize(new XStreamSerializerTest.TestEvent(XStreamSerializerTest.SPECIAL__CHAR__STRING), byte[].class);
        XStreamSerializerTest.TestEvent deserialized = testSubject.deserialize(serialized);
        Assert.assertArrayEquals(XStreamSerializerTest.SPECIAL__CHAR__STRING.getBytes(), deserialized.getName().getBytes());
    }

    @Test
    public void testSerializeNullValue() {
        SerializedObject<byte[]> serialized = testSubject.serialize(null, byte[].class);
        String deserialized = testSubject.deserialize(serialized);
        Assert.assertNull(deserialized);
    }

    /**
     * Tests the scenario as described in <a href="http://code.google.com/p/axonframework/issues/detail?id=150">issue
     * #150</a>.
     */
    @Test
    public void testSerializeWithSpecialCharacters_WithoutUpcasters() {
        SerializedObject<byte[]> serialized = testSubject.serialize(new XStreamSerializerTest.TestEvent(XStreamSerializerTest.SPECIAL__CHAR__STRING), byte[].class);
        XStreamSerializerTest.TestEvent deserialized = testSubject.deserialize(serialized);
        Assert.assertEquals(XStreamSerializerTest.SPECIAL__CHAR__STRING, deserialized.getName());
    }

    @Revision("2")
    public static class RevisionSpecifiedEvent {}

    public static class TestEvent implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;

        private LocalDate date;

        private Instant dateTime;

        private Period period;

        public TestEvent(String name) {
            this.name = name;
            this.date = LocalDate.now();
            this.dateTime = Instant.now();
            this.period = Period.ofDays(100);
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            XStreamSerializerTest.TestEvent testEvent = ((XStreamSerializerTest.TestEvent) (o));
            if ((date) != null ? !(date.equals(testEvent.date)) : (testEvent.date) != null) {
                return false;
            }
            if ((dateTime) != null ? !(dateTime.equals(testEvent.dateTime)) : (testEvent.dateTime) != null) {
                return false;
            }
            if ((name) != null ? !(name.equals(testEvent.name)) : (testEvent.name) != null) {
                return false;
            }
            return (period) != null ? period.equals(testEvent.period) : (testEvent.period) == null;
        }

        @Override
        public int hashCode() {
            int result = ((name) != null) ? name.hashCode() : 0;
            result = (31 * result) + ((date) != null ? date.hashCode() : 0);
            result = (31 * result) + ((dateTime) != null ? dateTime.hashCode() : 0);
            result = (31 * result) + ((period) != null ? period.hashCode() : 0);
            return result;
        }
    }
}

