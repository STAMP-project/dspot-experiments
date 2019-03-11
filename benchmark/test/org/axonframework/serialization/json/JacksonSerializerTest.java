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
package org.axonframework.serialization.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.ContentTypeConverter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE;


/**
 *
 *
 * @author Allard Buijze
 */
public class JacksonSerializerTest {
    private JacksonSerializer testSubject;

    private Instant time;

    private ObjectMapper objectMapper;

    @Test
    public void testCanSerializeToStringByteArrayAndInputStream() {
        Assert.assertTrue(testSubject.canSerializeTo(byte[].class));
        Assert.assertTrue(testSubject.canSerializeTo(String.class));
        Assert.assertTrue(testSubject.canSerializeTo(InputStream.class));
    }

    @Test
    public void testSerializeAndDeserializeObject_StringFormat() {
        JacksonSerializerTest.SimpleSerializableType toSerialize = new JacksonSerializerTest.SimpleSerializableType("first", time, new JacksonSerializerTest.SimpleSerializableType("nested"));
        SerializedObject<String> serialized = testSubject.serialize(toSerialize, String.class);
        JacksonSerializerTest.SimpleSerializableType actual = testSubject.deserialize(serialized);
        Assert.assertEquals(toSerialize.getValue(), actual.getValue());
        Assert.assertEquals(toSerialize.getNested().getValue(), actual.getNested().getValue());
    }

    @Test
    public void testSerializeAndDeserializeArray() {
        JacksonSerializerTest.SimpleSerializableType toSerialize = new JacksonSerializerTest.SimpleSerializableType("first", time, new JacksonSerializerTest.SimpleSerializableType("nested"));
        SerializedObject<String> serialized = testSubject.serialize(new JacksonSerializerTest.SimpleSerializableType[]{ toSerialize }, String.class);
        JacksonSerializerTest.SimpleSerializableType[] actual = testSubject.deserialize(serialized);
        Assert.assertEquals(1, actual.length);
        Assert.assertEquals(toSerialize.getValue(), actual[0].getValue());
        Assert.assertEquals(toSerialize.getNested().getValue(), actual[0].getNested().getValue());
    }

    @Test
    public void testSerializeAndDeserializeList() {
        objectMapper.enableDefaultTyping(OBJECT_AND_NON_CONCRETE, PROPERTY);
        JacksonSerializerTest.SimpleSerializableType toSerialize = new JacksonSerializerTest.SimpleSerializableType("first", time, new JacksonSerializerTest.SimpleSerializableType("nested"));
        SerializedObject<String> serialized = testSubject.serialize(Collections.singletonList(toSerialize), String.class);
        List<JacksonSerializerTest.SimpleSerializableType> actual = testSubject.deserialize(serialized);
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(toSerialize.getValue(), actual.get(0).getValue());
        Assert.assertEquals(toSerialize.getNested().getValue(), actual.get(0).getNested().getValue());
    }

    @Test
    public void testSerializeAndDeserializeObject_ByteArrayFormat() {
        JacksonSerializerTest.SimpleSerializableType toSerialize = new JacksonSerializerTest.SimpleSerializableType("first", time, new JacksonSerializerTest.SimpleSerializableType("nested"));
        SerializedObject<byte[]> serialized = testSubject.serialize(toSerialize, byte[].class);
        JacksonSerializerTest.SimpleSerializableType actual = testSubject.deserialize(serialized);
        Assert.assertEquals(toSerialize.getValue(), actual.getValue());
        Assert.assertEquals(toSerialize.getNested().getValue(), actual.getNested().getValue());
    }

    @Test
    public void testSerializeAndDeserializeObjectUnknownType() {
        JacksonSerializerTest.SimpleSerializableType toSerialize = new JacksonSerializerTest.SimpleSerializableType("first", time, new JacksonSerializerTest.SimpleSerializableType("nested"));
        SerializedObject<byte[]> serialized = testSubject.serialize(toSerialize, byte[].class);
        Object actual = testSubject.deserialize(new SimpleSerializedObject(serialized.getData(), byte[].class, "someUnknownType", "42.1"));
        Assert.assertTrue((actual instanceof UnknownSerializedType));
        UnknownSerializedType actualUnknown = ((UnknownSerializedType) (actual));
        Assert.assertTrue(actualUnknown.supportsFormat(JsonNode.class));
        JsonNode actualJson = actualUnknown.readData(JsonNode.class);
        Assert.assertEquals("first", actualJson.get("value").asText());
        Assert.assertEquals("nested", actualJson.path("nested").path("value").asText());
    }

    @Test
    public void testSerializeAndDeserializeObject_JsonNodeFormat() {
        JacksonSerializerTest.SimpleSerializableType toSerialize = new JacksonSerializerTest.SimpleSerializableType("first", time, new JacksonSerializerTest.SimpleSerializableType("nested"));
        SerializedObject<JsonNode> serialized = testSubject.serialize(toSerialize, JsonNode.class);
        JacksonSerializerTest.SimpleSerializableType actual = testSubject.deserialize(serialized);
        Assert.assertEquals(toSerialize.getValue(), actual.getValue());
        Assert.assertEquals(toSerialize.getNested().getValue(), actual.getNested().getValue());
    }

    @Test
    public void testCustomObjectMapperRevisionResolverAndConverter() {
        RevisionResolver revisionResolver = Mockito.spy(new AnnotationRevisionResolver());
        ChainingConverter converter = Mockito.spy(new ChainingConverter());
        ObjectMapper objectMapper = Mockito.spy(new ObjectMapper());
        testSubject = JacksonSerializer.builder().revisionResolver(revisionResolver).converter(converter).objectMapper(objectMapper).build();
        SerializedObject<byte[]> serialized = testSubject.serialize(new JacksonSerializerTest.SimpleSerializableType("test"), byte[].class);
        JacksonSerializerTest.SimpleSerializableType actual = testSubject.deserialize(serialized);
        Assert.assertNotNull(actual);
        Mockito.verify(objectMapper).readerFor(JacksonSerializerTest.SimpleSerializableType.class);
        Mockito.verify(objectMapper).writer();
        Mockito.verify(revisionResolver).revisionOf(JacksonSerializerTest.SimpleSerializableType.class);
        Mockito.verify(converter, Mockito.times(2)).registerConverter(ArgumentMatchers.isA(ContentTypeConverter.class));
        Assert.assertSame(objectMapper, testSubject.getObjectMapper());
    }

    @Test
    public void testCustomObjectMapperAndRevisionResolver() {
        ObjectMapper objectMapper = Mockito.spy(new ObjectMapper());
        RevisionResolver revisionResolver = Mockito.spy(new AnnotationRevisionResolver());
        testSubject = JacksonSerializer.builder().revisionResolver(revisionResolver).objectMapper(objectMapper).build();
        SerializedObject<byte[]> serialized = testSubject.serialize(new JacksonSerializerTest.SimpleSerializableType("test"), byte[].class);
        JacksonSerializerTest.SimpleSerializableType actual = testSubject.deserialize(serialized);
        Assert.assertNotNull(actual);
        Assert.assertTrue(((testSubject.getConverter()) instanceof ChainingConverter));
        Mockito.verify(objectMapper).readerFor(JacksonSerializerTest.SimpleSerializableType.class);
        Mockito.verify(objectMapper).writer();
        Mockito.verify(revisionResolver).revisionOf(JacksonSerializerTest.SimpleSerializableType.class);
    }

    @Test
    public void testCustomObjectMapper() {
        ObjectMapper objectMapper = Mockito.spy(new ObjectMapper());
        testSubject = JacksonSerializer.builder().objectMapper(objectMapper).build();
        SerializedObject<byte[]> serialized = testSubject.serialize(new JacksonSerializerTest.SimpleSerializableType("test"), byte[].class);
        JacksonSerializerTest.SimpleSerializableType actual = testSubject.deserialize(serialized);
        Assert.assertNotNull(actual);
        Assert.assertTrue(((testSubject.getConverter()) instanceof ChainingConverter));
        Mockito.verify(objectMapper).readerFor(JacksonSerializerTest.SimpleSerializableType.class);
        Mockito.verify(objectMapper).writer();
    }

    @Test
    public void testSerializeMetaData() {
        testSubject = JacksonSerializer.builder().build();
        SerializedObject<byte[]> serialized = testSubject.serialize(MetaData.from(Collections.singletonMap("test", "test")), byte[].class);
        MetaData actual = testSubject.deserialize(serialized);
        Assert.assertNotNull(actual);
        Assert.assertEquals("test", actual.get("test"));
        Assert.assertEquals(1, actual.size());
    }

    @Test
    public void testSerializeMetaDataWithComplexObjects() {
        // typing must be enabled for this (which we expect end-users to do
        testSubject.getObjectMapper().enableDefaultTypingAsProperty(OBJECT_AND_NON_CONCRETE, "@type");
        MetaData metaData = MetaData.with("myKey", new JacksonSerializerTest.ComplexObject("String1", "String2", 3));
        SerializedObject<byte[]> serialized = testSubject.serialize(metaData, byte[].class);
        MetaData actual = testSubject.deserialize(serialized);
        Assert.assertEquals(metaData, actual);
    }

    @Test
    public void testDeserializeNullValue() {
        SerializedObject<byte[]> serializedNull = testSubject.serialize(null, byte[].class);
        SimpleSerializedObject<byte[]> serializedNullString = new SimpleSerializedObject(serializedNull.getData(), byte[].class, testSubject.typeForClass(String.class));
        Assert.assertNull(testSubject.deserialize(serializedNull));
        Assert.assertNull(testSubject.deserialize(serializedNullString));
    }

    @Test
    public void testDeserializeEmptyBytes() {
        Assert.assertEquals(Void.class, testSubject.classForType(SerializedType.emptyType()));
        Assert.assertNull(testSubject.deserialize(new SimpleSerializedObject(new byte[0], byte[].class, SerializedType.emptyType())));
    }

    public static class ComplexObject {
        private final String value1;

        private final String value2;

        private final int value3;

        @JsonCreator
        public ComplexObject(@JsonProperty("value1")
        String value1, @JsonProperty("value2")
        String value2, @JsonProperty("value3")
        int value3) {
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
        }

        public String getValue1() {
            return value1;
        }

        public String getValue2() {
            return value2;
        }

        public int getValue3() {
            return value3;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            JacksonSerializerTest.ComplexObject that = ((JacksonSerializerTest.ComplexObject) (o));
            return (((value3) == (that.value3)) && (Objects.equals(value1, that.value1))) && (Objects.equals(value2, that.value2));
        }

        @Override
        public int hashCode() {
            return Objects.hash(value1, value2, value3);
        }
    }

    public static class SimpleSerializableType {
        private final String value;

        private final Instant time;

        private final JacksonSerializerTest.SimpleSerializableType nested;

        public SimpleSerializableType(String value) {
            this(value, Instant.now(), null);
        }

        @JsonCreator
        public SimpleSerializableType(@JsonProperty("value")
        String value, @JsonProperty("time")
        Instant time, @JsonProperty("nested")
        JacksonSerializerTest.SimpleSerializableType nested) {
            this.value = value;
            this.time = time;
            this.nested = nested;
        }

        public JacksonSerializerTest.SimpleSerializableType getNested() {
            return nested;
        }

        public String getValue() {
            return value;
        }

        public Instant getTime() {
            return time;
        }
    }
}

