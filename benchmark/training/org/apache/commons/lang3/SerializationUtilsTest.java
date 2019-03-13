/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.SerializationUtils}.
 */
public class SerializationUtilsTest {
    static final String CLASS_NOT_FOUND_MESSAGE = "ClassNotFoundSerialization.readObject fake exception";

    protected static final String SERIALIZE_IO_EXCEPTION_MESSAGE = "Anonymous OutputStream I/O exception";

    private String iString;

    private Integer iInteger;

    private HashMap<Object, Object> iMap;

    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new SerializationUtils());
        final Constructor<?>[] cons = SerializationUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(SerializationUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(SerializationUtils.class.getModifiers()));
    }

    @Test
    public void testException() {
        SerializationException serEx;
        final Exception ex = new Exception();
        serEx = new SerializationException();
        Assertions.assertSame(null, serEx.getMessage());
        Assertions.assertSame(null, serEx.getCause());
        serEx = new SerializationException("Message");
        Assertions.assertSame("Message", serEx.getMessage());
        Assertions.assertSame(null, serEx.getCause());
        serEx = new SerializationException(ex);
        Assertions.assertEquals("java.lang.Exception", serEx.getMessage());
        Assertions.assertSame(ex, serEx.getCause());
        serEx = new SerializationException("Message", ex);
        Assertions.assertSame("Message", serEx.getMessage());
        Assertions.assertSame(ex, serEx.getCause());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSerializeStream() throws Exception {
        final ByteArrayOutputStream streamTest = new ByteArrayOutputStream();
        SerializationUtils.serialize(iMap, streamTest);
        final ByteArrayOutputStream streamReal = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(streamReal);
        oos.writeObject(iMap);
        oos.flush();
        oos.close();
        final byte[] testBytes = streamTest.toByteArray();
        final byte[] realBytes = streamReal.toByteArray();
        Assertions.assertEquals(testBytes.length, realBytes.length);
        Assertions.assertArrayEquals(realBytes, testBytes);
    }

    @Test
    public void testSerializeStreamUnserializable() {
        final ByteArrayOutputStream streamTest = new ByteArrayOutputStream();
        iMap.put(new Object(), new Object());
        Assertions.assertThrows(SerializationException.class, () -> SerializationUtils.serialize(iMap, streamTest));
    }

    @Test
    public void testSerializeStreamNullObj() throws Exception {
        final ByteArrayOutputStream streamTest = new ByteArrayOutputStream();
        SerializationUtils.serialize(null, streamTest);
        final ByteArrayOutputStream streamReal = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(streamReal);
        oos.writeObject(null);
        oos.flush();
        oos.close();
        final byte[] testBytes = streamTest.toByteArray();
        final byte[] realBytes = streamReal.toByteArray();
        Assertions.assertEquals(testBytes.length, realBytes.length);
        Assertions.assertArrayEquals(realBytes, testBytes);
    }

    @Test
    public void testSerializeStreamObjNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> SerializationUtils.serialize(iMap, null));
    }

    @Test
    public void testSerializeStreamNullNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> SerializationUtils.serialize(null, null));
    }

    @Test
    public void testSerializeIOException() {
        // forces an IOException when the ObjectOutputStream is created, to test not closing the stream
        // in the finally block
        final OutputStream streamTest = new OutputStream() {
            @Override
            public void write(final int arg0) throws IOException {
                throw new IOException(SerializationUtilsTest.SERIALIZE_IO_EXCEPTION_MESSAGE);
            }
        };
        SerializationException e = Assertions.assertThrows(SerializationException.class, () -> SerializationUtils.serialize(iMap, streamTest));
        Assertions.assertEquals(("java.io.IOException: " + (SerializationUtilsTest.SERIALIZE_IO_EXCEPTION_MESSAGE)), e.getMessage());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDeserializeStream() throws Exception {
        final ByteArrayOutputStream streamReal = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(streamReal);
        oos.writeObject(iMap);
        oos.flush();
        oos.close();
        final ByteArrayInputStream inTest = new ByteArrayInputStream(streamReal.toByteArray());
        final Object test = SerializationUtils.deserialize(inTest);
        Assertions.assertNotNull(test);
        Assertions.assertTrue((test instanceof HashMap<?, ?>));
        Assertions.assertNotSame(test, iMap);
        final HashMap<?, ?> testMap = ((HashMap<?, ?>) (test));
        Assertions.assertEquals(iString, testMap.get("FOO"));
        Assertions.assertNotSame(iString, testMap.get("FOO"));
        Assertions.assertEquals(iInteger, testMap.get("BAR"));
        Assertions.assertNotSame(iInteger, testMap.get("BAR"));
        Assertions.assertEquals(iMap, testMap);
    }

    @Test
    public void testDeserializeClassCastException() {
        final String value = "Hello";
        final byte[] serialized = SerializationUtils.serialize(value);
        Assertions.assertEquals(value, SerializationUtils.deserialize(serialized));
        Assertions.assertThrows(ClassCastException.class, () -> {
            // Causes ClassCastException in call site, not in SerializationUtils.deserialize
            // needed to cause Exception
            @SuppressWarnings("unused")
            final Integer i = SerializationUtils.deserialize(serialized);
        });
    }

    @Test
    public void testDeserializeStreamOfNull() throws Exception {
        final ByteArrayOutputStream streamReal = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(streamReal);
        oos.writeObject(null);
        oos.flush();
        oos.close();
        final ByteArrayInputStream inTest = new ByteArrayInputStream(streamReal.toByteArray());
        final Object test = SerializationUtils.deserialize(inTest);
        Assertions.assertNull(test);
    }

    @Test
    public void testDeserializeStreamNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> SerializationUtils.deserialize(((InputStream) (null))));
    }

    @Test
    public void testDeserializeStreamBadStream() {
        Assertions.assertThrows(SerializationException.class, () -> SerializationUtils.deserialize(new ByteArrayInputStream(new byte[0])));
    }

    @Test
    public void testDeserializeStreamClassNotFound() throws Exception {
        final ByteArrayOutputStream streamReal = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(streamReal);
        oos.writeObject(new ClassNotFoundSerialization());
        oos.flush();
        oos.close();
        final ByteArrayInputStream inTest = new ByteArrayInputStream(streamReal.toByteArray());
        SerializationException se = Assertions.assertThrows(SerializationException.class, () -> SerializationUtils.deserialize(inTest));
        Assertions.assertEquals(("java.lang.ClassNotFoundException: " + (SerializationUtilsTest.CLASS_NOT_FOUND_MESSAGE)), se.getMessage());
    }

    @Test
    public void testRoundtrip() {
        final HashMap<Object, Object> newMap = SerializationUtils.roundtrip(iMap);
        Assertions.assertEquals(iMap, newMap);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSerializeBytes() throws Exception {
        final byte[] testBytes = SerializationUtils.serialize(iMap);
        final ByteArrayOutputStream streamReal = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(streamReal);
        oos.writeObject(iMap);
        oos.flush();
        oos.close();
        final byte[] realBytes = streamReal.toByteArray();
        Assertions.assertEquals(testBytes.length, realBytes.length);
        Assertions.assertArrayEquals(realBytes, testBytes);
    }

    @Test
    public void testSerializeBytesUnserializable() {
        iMap.put(new Object(), new Object());
        Assertions.assertThrows(SerializationException.class, () -> SerializationUtils.serialize(iMap));
    }

    @Test
    public void testSerializeBytesNull() throws Exception {
        final byte[] testBytes = SerializationUtils.serialize(null);
        final ByteArrayOutputStream streamReal = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(streamReal);
        oos.writeObject(null);
        oos.flush();
        oos.close();
        final byte[] realBytes = streamReal.toByteArray();
        Assertions.assertEquals(testBytes.length, realBytes.length);
        Assertions.assertArrayEquals(realBytes, testBytes);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDeserializeBytes() throws Exception {
        final ByteArrayOutputStream streamReal = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(streamReal);
        oos.writeObject(iMap);
        oos.flush();
        oos.close();
        final Object test = SerializationUtils.deserialize(streamReal.toByteArray());
        Assertions.assertNotNull(test);
        Assertions.assertTrue((test instanceof HashMap<?, ?>));
        Assertions.assertNotSame(test, iMap);
        final HashMap<?, ?> testMap = ((HashMap<?, ?>) (test));
        Assertions.assertEquals(iString, testMap.get("FOO"));
        Assertions.assertNotSame(iString, testMap.get("FOO"));
        Assertions.assertEquals(iInteger, testMap.get("BAR"));
        Assertions.assertNotSame(iInteger, testMap.get("BAR"));
        Assertions.assertEquals(iMap, testMap);
    }

    @Test
    public void testDeserializeBytesOfNull() throws Exception {
        final ByteArrayOutputStream streamReal = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(streamReal);
        oos.writeObject(null);
        oos.flush();
        oos.close();
        final Object test = SerializationUtils.deserialize(streamReal.toByteArray());
        Assertions.assertNull(test);
    }

    @Test
    public void testDeserializeBytesNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> SerializationUtils.deserialize(((byte[]) (null))));
    }

    @Test
    public void testDeserializeBytesBadStream() {
        Assertions.assertThrows(SerializationException.class, () -> SerializationUtils.deserialize(new byte[0]));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testClone() {
        final Object test = SerializationUtils.clone(iMap);
        Assertions.assertNotNull(test);
        Assertions.assertTrue((test instanceof HashMap<?, ?>));
        Assertions.assertNotSame(test, iMap);
        final HashMap<?, ?> testMap = ((HashMap<?, ?>) (test));
        Assertions.assertEquals(iString, testMap.get("FOO"));
        Assertions.assertNotSame(iString, testMap.get("FOO"));
        Assertions.assertEquals(iInteger, testMap.get("BAR"));
        Assertions.assertNotSame(iInteger, testMap.get("BAR"));
        Assertions.assertEquals(iMap, testMap);
    }

    @Test
    public void testCloneNull() {
        final Object test = SerializationUtils.clone(null);
        Assertions.assertNull(test);
    }

    @Test
    public void testCloneUnserializable() {
        iMap.put(new Object(), new Object());
        Assertions.assertThrows(SerializationException.class, () -> SerializationUtils.clone(iMap));
    }

    @Test
    public void testPrimitiveTypeClassSerialization() {
        final Class<?>[] primitiveTypes = new Class<?>[]{ byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class, void.class };
        for (final Class<?> primitiveType : primitiveTypes) {
            final Class<?> clone = SerializationUtils.clone(primitiveType);
            Assertions.assertEquals(primitiveType, clone);
        }
    }
}

