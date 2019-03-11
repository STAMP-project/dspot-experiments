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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.axonframework.eventhandling.GenericEventMessage;
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
public class SerializationAwareTest {
    private GenericEventMessage<String> testSubject;

    @Test
    public void testIsSerializedAsGenericEventMessage() throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(testSubject);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Object read = ois.readObject();
        Assert.assertEquals(GenericEventMessage.class, read.getClass());
    }

    @Test
    public void testSerializePayloadTwice() {
        Serializer serializer = Mockito.mock(Serializer.class);
        Converter converter = new ChainingConverter();
        Mockito.when(serializer.getConverter()).thenReturn(converter);
        final SimpleSerializedObject<byte[]> serializedObject = new SimpleSerializedObject("payload".getBytes(), byte[].class, "String", "0");
        Mockito.when(serializer.serialize("payload", byte[].class)).thenReturn(serializedObject);
        SerializedObject<byte[]> actual1 = testSubject.serializePayload(serializer, byte[].class);
        SerializedObject<byte[]> actual2 = testSubject.serializePayload(serializer, byte[].class);
        Assert.assertSame(actual1, actual2);
        Mockito.verify(serializer, Mockito.times(1)).serialize("payload", byte[].class);
        Mockito.verify(serializer).getConverter();
        Mockito.verifyNoMoreInteractions(serializer);
    }

    @Test
    public void testSerializePayloadTwice_DifferentRepresentations() {
        Serializer serializer = Mockito.mock(Serializer.class);
        Converter converter = new ChainingConverter();
        Mockito.when(serializer.getConverter()).thenReturn(converter);
        final SimpleSerializedObject<byte[]> serializedObject = new SimpleSerializedObject("payload".getBytes(), byte[].class, "String", "0");
        Mockito.when(serializer.serialize("payload", byte[].class)).thenReturn(serializedObject);
        SerializedObject<byte[]> actual1 = testSubject.serializePayload(serializer, byte[].class);
        SerializedObject<String> actual2 = testSubject.serializePayload(serializer, String.class);
        Assert.assertNotSame(actual1, actual2);
        Assert.assertEquals(String.class, actual2.getContentType());
        Mockito.verify(serializer, Mockito.times(1)).serialize("payload", byte[].class);
        Mockito.verify(serializer).getConverter();
        Mockito.verifyNoMoreInteractions(serializer);
    }

    @Test
    public void testSerializeMetaDataTwice() {
        Serializer serializer = Mockito.mock(Serializer.class);
        Converter converter = new ChainingConverter();
        Mockito.when(serializer.getConverter()).thenReturn(converter);
        final SimpleSerializedObject<byte[]> serializedObject = new SimpleSerializedObject("payload".getBytes(), byte[].class, "String", "0");
        Mockito.when(serializer.serialize(ArgumentMatchers.isA(MetaData.class), ArgumentMatchers.eq(byte[].class))).thenReturn(serializedObject);
        testSubject.serializeMetaData(serializer, byte[].class);
        testSubject.serializeMetaData(serializer, byte[].class);
        Mockito.verify(serializer, Mockito.times(1)).serialize(ArgumentMatchers.isA(MetaData.class), ArgumentMatchers.eq(byte[].class));
        Mockito.verify(serializer).getConverter();
        Mockito.verifyNoMoreInteractions(serializer);
    }

    @Test
    public void testSerializeMetaDataTwice_DifferentRepresentations() {
        Serializer serializer = Mockito.mock(Serializer.class);
        Converter converter = new ChainingConverter();
        Mockito.when(serializer.getConverter()).thenReturn(converter);
        final SimpleSerializedObject<byte[]> serializedObject = new SimpleSerializedObject("payload".getBytes(), byte[].class, "String", "0");
        Mockito.when(serializer.serialize(ArgumentMatchers.isA(MetaData.class), ArgumentMatchers.eq(byte[].class))).thenReturn(serializedObject);
        SerializedObject<byte[]> actual1 = testSubject.serializeMetaData(serializer, byte[].class);
        SerializedObject<String> actual2 = testSubject.serializeMetaData(serializer, String.class);
        Assert.assertNotSame(actual1, actual2);
        Assert.assertEquals(String.class, actual2.getContentType());
        Mockito.verify(serializer, Mockito.times(1)).serialize(ArgumentMatchers.isA(MetaData.class), ArgumentMatchers.eq(byte[].class));
        Mockito.verify(serializer).getConverter();
        Mockito.verifyNoMoreInteractions(serializer);
    }
}

