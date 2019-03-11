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
package org.axonframework.serialization;


import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.MetaData;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class SerializedMessageTest {
    private SerializedObject<String> serializedPayload = new SimpleSerializedObject("serializedPayload", String.class, "java.lang.Object", "1");

    private SerializedObject<String> serializedMetaData = new SerializedMetaData("serializedMetaData", String.class);

    private Object deserializedPayload = new Object();

    private MetaData deserializedMetaData = MetaData.emptyInstance();

    private Serializer serializer = Mockito.mock(Serializer.class);

    private String eventId = "eventId";

    @Test
    public void testConstructor() {
        SerializedMessage<Object> message1 = new SerializedMessage(eventId, serializedPayload, serializedMetaData, serializer);
        Assert.assertSame(MetaData.emptyInstance(), message1.getMetaData());
        Assert.assertEquals(Object.class, message1.getPayloadType());
        Assert.assertFalse(message1.isPayloadDeserialized());
        Assert.assertEquals(Object.class, message1.getPayload().getClass());
        Assert.assertTrue(message1.isPayloadDeserialized());
    }

    @Test
    public void testWithMetaData() {
        Map<String, Object> metaDataMap = Collections.singletonMap("key", "value");
        MetaData metaData = MetaData.from(metaDataMap);
        Mockito.when(serializer.deserialize(serializedMetaData)).thenReturn(metaData);
        SerializedMessage<Object> message = new SerializedMessage(eventId, serializedPayload, serializedMetaData, serializer);
        Message<Object> message1 = message.withMetaData(MetaData.emptyInstance());
        Message<Object> message2 = message.withMetaData(MetaData.from(Collections.singletonMap("key", ((Object) ("otherValue")))));
        Assert.assertEquals(0, message1.getMetaData().size());
        Assert.assertEquals(1, message2.getMetaData().size());
    }

    @Test
    public void testAndMetaData() {
        Map<String, Object> metaDataMap = Collections.singletonMap("key", "value");
        MetaData metaData = MetaData.from(metaDataMap);
        Mockito.when(serializer.deserialize(serializedMetaData)).thenReturn(metaData);
        Message<Object> message = new SerializedMessage(eventId, serializedPayload, serializedMetaData, serializer);
        Message<Object> message1 = message.andMetaData(MetaData.emptyInstance());
        Message<Object> message2 = message.andMetaData(MetaData.from(Collections.singletonMap("key", ((Object) ("otherValue")))));
        Assert.assertEquals(1, message1.getMetaData().size());
        Assert.assertEquals("value", message1.getMetaData().get("key"));
        Assert.assertEquals(1, message2.getMetaData().size());
        Assert.assertEquals("otherValue", message2.getMetaData().get("key"));
    }

    @Test
    public void testSerializePayloadImmediately() {
        SerializedMessage<Object> message = new SerializedMessage(eventId, serializedPayload, serializedMetaData, serializer);
        SerializedObject<byte[]> actual = message.serializePayload(serializer, byte[].class);
        Assert.assertArrayEquals("serializedPayload".getBytes(Charset.forName("UTF-8")), actual.getData());
        // this call is allowed
        Mockito.verify(serializer, Mockito.atLeast(0)).classForType(ArgumentMatchers.isA(SerializedType.class));
        Mockito.verify(serializer, Mockito.atLeast(0)).getConverter();
        Mockito.verifyNoMoreInteractions(serializer);
    }

    @Test
    public void testSerializeMetaDataImmediately() {
        SerializedMessage<Object> message = new SerializedMessage(eventId, serializedPayload, serializedMetaData, serializer);
        SerializedObject<byte[]> actual = message.serializeMetaData(serializer, byte[].class);
        Assert.assertArrayEquals("serializedMetaData".getBytes(Charset.forName("UTF-8")), actual.getData());
        // this call is allowed
        Mockito.verify(serializer, Mockito.atLeast(0)).classForType(ArgumentMatchers.isA(SerializedType.class));
        Mockito.verify(serializer, Mockito.atLeast(0)).getConverter();
        Mockito.verifyNoMoreInteractions(serializer);
    }
}

