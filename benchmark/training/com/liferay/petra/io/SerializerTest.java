/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.io;


import NewEnv.Type;
import SerializationConstants.TC_BOOLEAN;
import SerializationConstants.TC_BYTE;
import SerializationConstants.TC_CHARACTER;
import SerializationConstants.TC_CLASS;
import SerializationConstants.TC_DOUBLE;
import SerializationConstants.TC_FLOAT;
import SerializationConstants.TC_INTEGER;
import SerializationConstants.TC_LONG;
import SerializationConstants.TC_NULL;
import SerializationConstants.TC_OBJECT;
import SerializationConstants.TC_SHORT;
import SerializationConstants.TC_STRING;
import StringPool.BLANK;
import StringPool.UTF8;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class SerializerTest {
    @ClassRule
    @Rule
    public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(new CodeCoverageAssertor() {
        @Override
        public void appendAssertClasses(List<Class<?>> assertClasses) {
            assertClasses.add(AnnotatedObjectInputStream.class);
            assertClasses.add(AnnotatedObjectOutputStream.class);
            assertClasses.add(SerializationConstants.class);
        }
    }, NewEnvTestRule.INSTANCE);

    @Test
    public void testBufferOutputStream() throws Exception {
        byte[] data = new byte[1024];
        _random.nextBytes(data);
        Serializer serializer = new Serializer();
        SerializerTest.BufferOutputStream bufferOutputStream = new SerializerTest.BufferOutputStream(serializer);
        for (byte b : data) {
            bufferOutputStream.write(b);
        }
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertArrayEquals(data, byteBuffer.array());
        serializer = new Serializer();
        bufferOutputStream = new SerializerTest.BufferOutputStream(serializer);
        bufferOutputStream.write(data);
        byteBuffer = serializer.toByteBuffer();
        Assert.assertArrayEquals(data, byteBuffer.array());
    }

    @Test
    public void testBufferQueue() throws Exception {
        SerializerTest.BufferQueue bufferQueue = new SerializerTest.BufferQueue();
        // Insert into empty queue
        byte[] buffer2 = new byte[2];
        bufferQueue.enqueue(buffer2);
        SerializerTest.BufferNode bufferNode1 = bufferQueue.getHeadBufferNode();
        Assert.assertSame(buffer2, bufferNode1.getBuffer());
        Assert.assertNull(bufferNode1.getNext());
        // Insert to head
        byte[] buffer4 = new byte[4];
        bufferQueue.enqueue(buffer4);
        bufferNode1 = bufferQueue.getHeadBufferNode();
        SerializerTest.BufferNode bufferNode2 = bufferNode1.getNext();
        Assert.assertSame(buffer4, bufferNode1.getBuffer());
        Assert.assertNotNull(bufferNode1.getNext());
        Assert.assertSame(buffer2, bufferNode2.getBuffer());
        Assert.assertNull(bufferNode2.getNext());
        // Insert to second
        byte[] buffer3 = new byte[3];
        bufferQueue.enqueue(buffer3);
        bufferNode1 = bufferQueue.getHeadBufferNode();
        bufferNode2 = bufferNode1.getNext();
        SerializerTest.BufferNode bufferNode3 = bufferNode2.getNext();
        Assert.assertSame(buffer4, bufferNode1.getBuffer());
        Assert.assertNotNull(bufferNode1.getNext());
        Assert.assertSame(buffer3, bufferNode2.getBuffer());
        Assert.assertNotNull(bufferNode2.getNext());
        Assert.assertSame(buffer2, bufferNode3.getBuffer());
        Assert.assertNull(bufferNode3.getNext());
        // Insert to last
        byte[] buffer1 = new byte[1];
        bufferQueue.enqueue(buffer1);
        bufferNode1 = bufferQueue.getHeadBufferNode();
        bufferNode2 = bufferNode1.getNext();
        bufferNode3 = bufferNode2.getNext();
        SerializerTest.BufferNode bufferNode4 = bufferNode3.getNext();
        Assert.assertSame(buffer4, bufferNode1.getBuffer());
        Assert.assertNotNull(bufferNode1.getNext());
        Assert.assertSame(buffer3, bufferNode2.getBuffer());
        Assert.assertNotNull(bufferNode2.getNext());
        Assert.assertSame(buffer2, bufferNode3.getBuffer());
        Assert.assertNotNull(bufferNode3.getNext());
        Assert.assertSame(buffer1, bufferNode4.getBuffer());
        Assert.assertNull(bufferNode4.getNext());
        // Fill up queue to default count limit
        byte[] buffer5 = new byte[5];
        byte[] buffer6 = new byte[6];
        byte[] buffer7 = new byte[7];
        byte[] buffer8 = new byte[8];
        bufferQueue.enqueue(buffer5);
        bufferQueue.enqueue(buffer6);
        bufferQueue.enqueue(buffer7);
        bufferQueue.enqueue(buffer8);
        // Automatically release smallest on insert to head
        byte[] buffer10 = new byte[10];
        bufferQueue.enqueue(buffer10);
        bufferNode1 = bufferQueue.getHeadBufferNode();
        bufferNode2 = bufferNode1.getNext();
        bufferNode3 = bufferNode2.getNext();
        bufferNode4 = bufferNode3.getNext();
        SerializerTest.BufferNode bufferNode5 = bufferNode4.getNext();
        SerializerTest.BufferNode bufferNode6 = bufferNode5.getNext();
        SerializerTest.BufferNode bufferNode7 = bufferNode6.getNext();
        SerializerTest.BufferNode bufferNode8 = bufferNode7.getNext();
        Assert.assertSame(buffer10, bufferNode1.getBuffer());
        Assert.assertNotNull(bufferNode1.getNext());
        Assert.assertSame(buffer8, bufferNode2.getBuffer());
        Assert.assertNotNull(bufferNode2.getNext());
        Assert.assertSame(buffer7, bufferNode3.getBuffer());
        Assert.assertNotNull(bufferNode3.getNext());
        Assert.assertSame(buffer6, bufferNode4.getBuffer());
        Assert.assertNotNull(bufferNode4.getNext());
        Assert.assertSame(buffer5, bufferNode5.getBuffer());
        Assert.assertNotNull(bufferNode5.getNext());
        Assert.assertSame(buffer4, bufferNode6.getBuffer());
        Assert.assertNotNull(bufferNode6.getNext());
        Assert.assertSame(buffer3, bufferNode7.getBuffer());
        Assert.assertNotNull(bufferNode7.getNext());
        Assert.assertSame(buffer2, bufferNode8.getBuffer());
        Assert.assertNull(bufferNode8.getNext());
        // Automatically release smallest on insert to second
        byte[] buffer9 = new byte[9];
        bufferQueue.enqueue(buffer9);
        bufferNode1 = bufferQueue.getHeadBufferNode();
        bufferNode2 = bufferNode1.getNext();
        bufferNode3 = bufferNode2.getNext();
        bufferNode4 = bufferNode3.getNext();
        bufferNode5 = bufferNode4.getNext();
        bufferNode6 = bufferNode5.getNext();
        bufferNode7 = bufferNode6.getNext();
        bufferNode8 = bufferNode7.getNext();
        Assert.assertSame(buffer10, bufferNode1.getBuffer());
        Assert.assertNotNull(bufferNode1.getNext());
        Assert.assertSame(buffer9, bufferNode2.getBuffer());
        Assert.assertNotNull(bufferNode2.getNext());
        Assert.assertSame(buffer8, bufferNode3.getBuffer());
        Assert.assertNotNull(bufferNode3.getNext());
        Assert.assertSame(buffer7, bufferNode4.getBuffer());
        Assert.assertNotNull(bufferNode4.getNext());
        Assert.assertSame(buffer6, bufferNode5.getBuffer());
        Assert.assertNotNull(bufferNode5.getNext());
        Assert.assertSame(buffer5, bufferNode6.getBuffer());
        Assert.assertNotNull(bufferNode6.getNext());
        Assert.assertSame(buffer4, bufferNode7.getBuffer());
        Assert.assertNotNull(bufferNode7.getNext());
        Assert.assertSame(buffer3, bufferNode8.getBuffer());
        Assert.assertNull(bufferNode8.getNext());
    }

    @Test
    public void testConstructor() {
        new SerializationConstants();
    }

    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testCustomizedClassInitialization() throws Exception {
        System.setProperty(((Serializer.class.getName()) + ".thread.local.buffer.count.limit"), String.valueOf(((SerializerTest._THREADLOCAL_BUFFER_COUNT_MIN) + 1)));
        System.setProperty(((Serializer.class.getName()) + ".thread.local.buffer.size.limit"), String.valueOf(((SerializerTest._THREADLOCAL_BUFFER_SIZE_MIN) + 1)));
        Assert.assertEquals(((SerializerTest._THREADLOCAL_BUFFER_COUNT_MIN) + 1), SerializerTest._threadLocalBufferCountLimitField.getInt(null));
        Assert.assertEquals(((SerializerTest._THREADLOCAL_BUFFER_SIZE_MIN) + 1), SerializerTest._threadLocalBufferSizeLimitField.getInt(null));
    }

    @Test
    public void testDefaultClassInitialization() throws Exception {
        Assert.assertEquals(SerializerTest._THREADLOCAL_BUFFER_COUNT_MIN, SerializerTest._threadLocalBufferCountLimitField.getInt(null));
        Assert.assertEquals(SerializerTest._THREADLOCAL_BUFFER_SIZE_MIN, SerializerTest._threadLocalBufferSizeLimitField.getInt(null));
    }

    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testDefendedClassInitialization() throws Exception {
        System.setProperty(((Serializer.class.getName()) + ".thread.local.buffer.count.limit"), String.valueOf(((SerializerTest._THREADLOCAL_BUFFER_COUNT_MIN) - 1)));
        System.setProperty(((Serializer.class.getName()) + ".thread.local.buffer.size.limit"), String.valueOf(((SerializerTest._THREADLOCAL_BUFFER_SIZE_MIN) - 1)));
        Assert.assertEquals(SerializerTest._THREADLOCAL_BUFFER_COUNT_MIN, SerializerTest._threadLocalBufferCountLimitField.getInt(null));
        Assert.assertEquals(SerializerTest._THREADLOCAL_BUFFER_SIZE_MIN, SerializerTest._threadLocalBufferSizeLimitField.getInt(null));
    }

    @Test
    public void testGetBufferGrow() throws Exception {
        Serializer serializer = new Serializer();
        // OOME
        SerializerTest._indexField.set(serializer, 1);
        try {
            SerializerTest._getBufferMethod.invoke(serializer, Integer.MAX_VALUE);
            Assert.fail();
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            Assert.assertTrue(cause.toString(), (cause instanceof OutOfMemoryError));
        }
        // Normal doubling size
        byte[] bytes = new byte[SerializerTest._COUNT];
        _random.nextBytes(bytes);
        SerializerTest._bufferField.set(serializer, bytes);
        SerializerTest._indexField.set(serializer, bytes.length);
        byte[] newBytes = ((byte[]) (SerializerTest._getBufferMethod.invoke(serializer, 1)));
        Assert.assertEquals(Arrays.toString(newBytes), ((bytes.length) * 2), newBytes.length);
        for (int i = 0; i < (bytes.length); i++) {
            Assert.assertEquals(bytes[i], newBytes[i]);
        }
        for (int i = bytes.length; i < (newBytes.length); i++) {
            Assert.assertEquals(0, newBytes[i]);
        }
        // Doubling size is still less than minimum size
        SerializerTest._bufferField.set(serializer, bytes);
        SerializerTest._indexField.set(serializer, bytes.length);
        newBytes = ((byte[]) (SerializerTest._getBufferMethod.invoke(serializer, ((SerializerTest._COUNT) + 1))));
        Assert.assertEquals(Arrays.toString(newBytes), (((bytes.length) * 2) + 1), newBytes.length);
        for (int i = 0; i < (bytes.length); i++) {
            Assert.assertEquals(bytes[i], newBytes[i]);
        }
        for (int i = bytes.length; i < (newBytes.length); i++) {
            Assert.assertEquals(0, newBytes[i]);
        }
    }

    @Test
    public void testReleaseLargeBuffer() throws Exception {
        ThreadLocal<?> bufferQueueThreadLocal = ReflectionTestUtil.getFieldValue(Serializer.class, "_bufferQueueThreadLocal");
        bufferQueueThreadLocal.remove();
        Serializer serializer = new Serializer();
        char[] chars = new char[SerializerTest._THREADLOCAL_BUFFER_SIZE_MIN];
        serializer.writeString(new String(chars));
        serializer.toByteBuffer();
        SerializerTest.BufferQueue bufferQueue = new SerializerTest.BufferQueue(ReflectionTestUtil.invoke(serializer, "_getBufferQueue", new Class<?>[0]));
        Assert.assertEquals(0, bufferQueue.getCount());
        serializer = new Serializer();
        serializer.writeString(new String(chars));
        UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
        serializer.writeTo(unsyncByteArrayOutputStream);
        Assert.assertEquals(0, bufferQueue.getCount());
        Assert.assertEquals((((chars.length) * 2) + 5), unsyncByteArrayOutputStream.size());
    }

    @Test
    public void testWriteBoolean() throws Exception {
        Serializer serializer = new Serializer();
        boolean[] booleans = new boolean[SerializerTest._COUNT];
        for (int i = 0; i < (SerializerTest._COUNT); i++) {
            booleans[i] = _random.nextBoolean();
            serializer.writeBoolean(booleans[i]);
        }
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        byte[] bytes = byteBuffer.array();
        Assert.assertNull(SerializerTest._bufferField.get(serializer));
        for (int i = 0; i < (SerializerTest._COUNT); i++) {
            if (booleans[i]) {
                Assert.assertEquals(1, bytes[i]);
            } else {
                Assert.assertEquals(0, bytes[i]);
            }
        }
    }

    @Test
    public void testWriteByte() {
        Serializer serializer = new Serializer();
        byte[] bytes = new byte[SerializerTest._COUNT];
        _random.nextBytes(bytes);
        for (int i = 0; i < (SerializerTest._COUNT); i++) {
            serializer.writeByte(bytes[i]);
        }
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertArrayEquals(bytes, byteBuffer.array());
    }

    @Test
    public void testWriteChar() {
        Serializer serializer = new Serializer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(((SerializerTest._COUNT) * 2));
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        CharBuffer charBuffer = byteBuffer.asCharBuffer();
        char[] chars = new char[SerializerTest._COUNT];
        for (int i = 0; i < (SerializerTest._COUNT); i++) {
            chars[i] = ((char) (_random.nextInt()));
            serializer.writeChar(chars[i]);
            charBuffer.put(chars[i]);
        }
        ByteBuffer serializerByteBuffer = serializer.toByteBuffer();
        Assert.assertArrayEquals(byteBuffer.array(), serializerByteBuffer.array());
    }

    @Test
    public void testWriteDouble() {
        Serializer serializer = new Serializer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(((SerializerTest._COUNT) * 8));
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        double[] doubles = new double[SerializerTest._COUNT];
        for (int i = 0; i < (SerializerTest._COUNT); i++) {
            doubles[i] = _random.nextDouble();
            serializer.writeDouble(doubles[i]);
            doubleBuffer.put(doubles[i]);
        }
        ByteBuffer serializerByteBuffer = serializer.toByteBuffer();
        Assert.assertArrayEquals(byteBuffer.array(), serializerByteBuffer.array());
    }

    @Test
    public void testWriteFloat() {
        Serializer serializer = new Serializer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(((SerializerTest._COUNT) * 4));
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        float[] floats = new float[SerializerTest._COUNT];
        for (int i = 0; i < (SerializerTest._COUNT); i++) {
            floats[i] = _random.nextFloat();
            serializer.writeFloat(floats[i]);
            floatBuffer.put(floats[i]);
        }
        ByteBuffer serializerByteBuffer = serializer.toByteBuffer();
        Assert.assertArrayEquals(byteBuffer.array(), serializerByteBuffer.array());
    }

    @Test
    public void testWriteInt() {
        Serializer serializer = new Serializer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(((SerializerTest._COUNT) * 4));
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        int[] ints = new int[SerializerTest._COUNT];
        for (int i = 0; i < (SerializerTest._COUNT); i++) {
            ints[i] = _random.nextInt();
            serializer.writeInt(ints[i]);
            intBuffer.put(ints[i]);
        }
        ByteBuffer serializerByteBuffer = serializer.toByteBuffer();
        Assert.assertArrayEquals(byteBuffer.array(), serializerByteBuffer.array());
    }

    @Test
    public void testWriteLong() {
        Serializer serializer = new Serializer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(((SerializerTest._COUNT) * 8));
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        LongBuffer longBuffer = byteBuffer.asLongBuffer();
        long[] longs = new long[SerializerTest._COUNT];
        for (int i = 0; i < (SerializerTest._COUNT); i++) {
            longs[i] = _random.nextLong();
            serializer.writeLong(longs[i]);
            longBuffer.put(longs[i]);
        }
        ByteBuffer serializerByteBuffer = serializer.toByteBuffer();
        Assert.assertArrayEquals(byteBuffer.array(), serializerByteBuffer.array());
    }

    @Test
    public void testWriteObjectBoolean() {
        Serializer serializer = new Serializer();
        serializer.writeObject(Boolean.TRUE);
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(2, byteBuffer.limit());
        Assert.assertEquals(TC_BOOLEAN, byteBuffer.get());
        Assert.assertEquals(1, byteBuffer.get());
    }

    @Test
    public void testWriteObjectByte() {
        Serializer serializer = new Serializer();
        serializer.writeObject(((byte) (101)));
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(2, byteBuffer.limit());
        Assert.assertEquals(TC_BYTE, byteBuffer.get());
        Assert.assertEquals(101, byteBuffer.get());
    }

    @Test
    public void testWriteObjectCharacter() {
        Serializer serializer = new Serializer();
        serializer.writeObject('a');
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(3, byteBuffer.limit());
        Assert.assertEquals(TC_CHARACTER, byteBuffer.get());
        Assert.assertEquals('a', byteBuffer.getChar());
    }

    @Test
    public void testWriteObjectClassWithBlankContextName() throws UnsupportedEncodingException {
        Serializer serializer = new Serializer();
        Class<?> clazz = getClass();
        ClassLoaderPool.register(BLANK, clazz.getClassLoader());
        try {
            serializer.writeObject(clazz);
        } finally {
            ClassLoaderPool.unregister(clazz.getClassLoader());
        }
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        String className = clazz.getName();
        Assert.assertEquals(((className.length()) + 11), byteBuffer.limit());
        Assert.assertEquals(TC_CLASS, byteBuffer.get());
        Assert.assertEquals(1, byteBuffer.get());
        Assert.assertEquals(0, byteBuffer.getInt());
        Assert.assertEquals(1, byteBuffer.get());
        Assert.assertEquals(className.length(), byteBuffer.getInt());
        Assert.assertEquals(className, new String(byteBuffer.array(), byteBuffer.position(), byteBuffer.remaining(), StringPool.UTF8));
    }

    @Test
    public void testWriteObjectClassWithNullContextName() throws UnsupportedEncodingException {
        Serializer serializer = new Serializer();
        Class<?> clazz = getClass();
        serializer.writeObject(clazz);
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        String className = clazz.getName();
        String contextName = StringPool.NULL;
        byte[] contextNameBytes = contextName.getBytes(UTF8);
        Assert.assertEquals((((className.length()) + (contextName.length())) + 11), byteBuffer.limit());
        Assert.assertEquals(TC_CLASS, byteBuffer.get());
        Assert.assertEquals(1, byteBuffer.get());
        Assert.assertEquals(contextName.length(), byteBuffer.getInt());
        Assert.assertEquals(contextName, new String(byteBuffer.array(), byteBuffer.position(), contextNameBytes.length, StringPool.UTF8));
        byteBuffer.position(((byteBuffer.position()) + (contextNameBytes.length)));
        Assert.assertEquals(1, byteBuffer.get());
        Assert.assertEquals(className.length(), byteBuffer.getInt());
        Assert.assertEquals(className, new String(byteBuffer.array(), byteBuffer.position(), byteBuffer.remaining(), StringPool.UTF8));
    }

    @Test
    public void testWriteObjectDouble() {
        Serializer serializer = new Serializer();
        serializer.writeObject(17.58);
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(9, byteBuffer.limit());
        Assert.assertEquals(TC_DOUBLE, byteBuffer.get());
        Assert.assertTrue(((byteBuffer.getDouble()) == 17.58));
    }

    @Test
    public void testWriteObjectFloat() {
        Serializer serializer = new Serializer();
        serializer.writeObject(17.58F);
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(5, byteBuffer.limit());
        Assert.assertEquals(TC_FLOAT, byteBuffer.get());
        Assert.assertTrue(((byteBuffer.getFloat()) == 17.58F));
    }

    @Test
    public void testWriteObjectInteger() {
        Serializer serializer = new Serializer();
        serializer.writeObject(101);
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(5, byteBuffer.limit());
        Assert.assertEquals(TC_INTEGER, byteBuffer.get());
        Assert.assertEquals(101, byteBuffer.getInt());
    }

    @Test
    public void testWriteObjectLong() {
        Serializer serializer = new Serializer();
        serializer.writeObject(Long.valueOf(101));
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(9, byteBuffer.limit());
        Assert.assertEquals(TC_LONG, byteBuffer.get());
        Assert.assertEquals(101, byteBuffer.getLong());
    }

    @Test
    public void testWriteObjectNull() {
        Serializer serializer = new Serializer();
        serializer.writeObject(null);
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(1, byteBuffer.limit());
        Assert.assertEquals(TC_NULL, byteBuffer.get());
    }

    @Test
    public void testWriteObjectOrdinary() throws Exception {
        Serializer serializer = new Serializer();
        Date date = new Date(123456);
        serializer.writeObject(date);
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(TC_OBJECT, byteBuffer.get());
        UnsyncByteArrayInputStream unsyncByteArrayInputStream = new UnsyncByteArrayInputStream(byteBuffer.array(), byteBuffer.position(), byteBuffer.remaining());
        ObjectInputStream objectInputStream = new AnnotatedObjectInputStream(unsyncByteArrayInputStream);
        Object object = objectInputStream.readObject();
        Assert.assertTrue((object instanceof Date));
        Assert.assertEquals(date, object);
    }

    @Test
    public void testWriteObjectOrdinaryNPE() throws Exception {
        Serializer serializer = new Serializer();
        Serializable serializable = new Serializable() {
            private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
                throw new IOException("Forced IOException");
            }
        };
        try {
            serializer.writeObject(serializable);
            Assert.fail();
        } catch (RuntimeException re) {
            String message = re.getMessage();
            Assert.assertTrue(message.startsWith("Unable to write ordinary serializable object "));
            Throwable throwable = re.getCause();
            Assert.assertTrue((throwable instanceof IOException));
            Assert.assertEquals("Forced IOException", throwable.getMessage());
        }
    }

    @Test
    public void testWriteObjectShort() {
        Serializer serializer = new Serializer();
        serializer.writeObject(((short) (101)));
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals(3, byteBuffer.limit());
        Assert.assertEquals(TC_SHORT, byteBuffer.get());
        Assert.assertEquals(101, byteBuffer.getShort());
    }

    @Test
    public void testWriteObjectString() {
        String asciiString = "abcdefghijklmn";
        Serializer serializer = new Serializer();
        serializer.writeObject(asciiString);
        ByteBuffer byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals((6 + (asciiString.length())), byteBuffer.limit());
        Assert.assertEquals(TC_STRING, byteBuffer.get());
        Assert.assertEquals(1, byteBuffer.get());
        Assert.assertEquals(asciiString.length(), byteBuffer.getInt());
        for (int i = 0; i < (asciiString.length()); i++) {
            Assert.assertEquals(asciiString.charAt(i), ((char) (byteBuffer.get())));
        }
        String nonAsciiString = "?ASCII Code????";
        serializer = new Serializer();
        serializer.writeObject(nonAsciiString);
        byteBuffer = serializer.toByteBuffer();
        Assert.assertEquals((6 + ((nonAsciiString.length()) * 2)), byteBuffer.limit());
        Assert.assertEquals(TC_STRING, byteBuffer.get());
        Assert.assertEquals(0, byteBuffer.get());
        Assert.assertEquals(nonAsciiString.length(), byteBuffer.getInt());
        for (int i = 0; i < (nonAsciiString.length()); i++) {
            Assert.assertEquals(nonAsciiString.charAt(i), byteBuffer.getChar());
        }
    }

    @Test
    public void testWriteShort() {
        Serializer serializer = new Serializer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(((SerializerTest._COUNT) * 2));
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        short[] shorts = new short[SerializerTest._COUNT];
        for (int i = 0; i < (SerializerTest._COUNT); i++) {
            shorts[i] = ((short) (_random.nextInt()));
            serializer.writeShort(shorts[i]);
            shortBuffer.put(shorts[i]);
        }
        ByteBuffer serializerByteBuffer = serializer.toByteBuffer();
        Assert.assertArrayEquals(byteBuffer.array(), serializerByteBuffer.array());
    }

    @Test
    public void testWriteString() throws Exception {
        String asciiString = "abcdefghijklmn";
        Serializer serializer = new Serializer();
        SerializerTest._bufferField.set(serializer, new byte[0]);
        serializer.writeString(asciiString);
        Assert.assertEquals(SerializerTest._indexField.getInt(serializer), (5 + (asciiString.length())));
        Assert.assertTrue(BigEndianCodec.getBoolean(((byte[]) (SerializerTest._bufferField.get(serializer))), 0));
        int length = BigEndianCodec.getInt(((byte[]) (SerializerTest._bufferField.get(serializer))), 1);
        Assert.assertEquals(asciiString.length(), length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(asciiString.length());
        for (int i = 0; i < (asciiString.length()); i++) {
            byteBuffer.put(((byte) (asciiString.charAt(i))));
        }
        byteBuffer.flip();
        UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
        serializer.writeTo(unsyncByteArrayOutputStream);
        byte[] data = unsyncByteArrayOutputStream.toByteArray();
        for (int i = 5; i < (data.length); i++) {
            Assert.assertEquals(byteBuffer.get(), data[i]);
        }
        String nonAsciiString = "?ASCII Code????";
        serializer = new Serializer();
        serializer.writeString(nonAsciiString);
        Assert.assertEquals(SerializerTest._indexField.getInt(serializer), (5 + ((nonAsciiString.length()) * 2)));
        Assert.assertFalse(BigEndianCodec.getBoolean(((byte[]) (SerializerTest._bufferField.get(serializer))), 0));
        length = BigEndianCodec.getInt(((byte[]) (SerializerTest._bufferField.get(serializer))), 1);
        Assert.assertEquals(nonAsciiString.length(), length);
        byteBuffer = ByteBuffer.allocate(((nonAsciiString.length()) * 2));
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        CharBuffer charBuffer = byteBuffer.asCharBuffer();
        for (int i = 0; i < (nonAsciiString.length()); i++) {
            charBuffer.put(nonAsciiString.charAt(i));
        }
        unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
        serializer.writeTo(unsyncByteArrayOutputStream);
        data = unsyncByteArrayOutputStream.toByteArray();
        for (int i = 5; i < (data.length); i++) {
            Assert.assertEquals(byteBuffer.get(), data[i]);
        }
    }

    private static final int _COUNT = 1024;

    private static final int _THREADLOCAL_BUFFER_COUNT_MIN = 8;

    private static final int _THREADLOCAL_BUFFER_SIZE_MIN = 16 * 1024;

    private static final Field _bufferField = ReflectionTestUtil.getField(Serializer.class, "_buffer");

    private static final Method _getBufferMethod = ReflectionTestUtil.getMethod(Serializer.class, "_getBuffer", int.class);

    private static final Field _indexField = ReflectionTestUtil.getField(Serializer.class, "_index");

    private static final Field _threadLocalBufferCountLimitField = ReflectionTestUtil.getField(Serializer.class, "_THREADLOCAL_BUFFER_COUNT_LIMIT");

    private static final Field _threadLocalBufferSizeLimitField = ReflectionTestUtil.getField(Serializer.class, "_THREADLOCAL_BUFFER_SIZE_LIMIT");

    private final Random _random = new Random();

    private static class BufferNode {
        public byte[] getBuffer() throws Exception {
            return ((byte[]) (SerializerTest.BufferNode._bufferField.get(_bufferNode)));
        }

        public SerializerTest.BufferNode getNext() throws Exception {
            Object next = SerializerTest.BufferNode._nextField.get(_bufferNode);
            if (next == null) {
                return null;
            }
            return new SerializerTest.BufferNode(next);
        }

        private BufferNode(Object bufferNode) throws Exception {
            _bufferNode = bufferNode;
        }

        private static final Field _bufferField;

        private static final Field _nextField;

        static {
            try {
                Class<?> clazz = Class.forName(((Serializer.class.getName()) + "$BufferNode"));
                _bufferField = ReflectionTestUtil.getField(clazz, "_buffer");
                _nextField = ReflectionTestUtil.getField(clazz, "_next");
            } catch (ClassNotFoundException cnfe) {
                throw new ExceptionInInitializerError(cnfe);
            }
        }

        private final Object _bufferNode;
    }

    private static class BufferOutputStream {
        public void write(byte[] bytes) throws Exception {
            SerializerTest.BufferOutputStream._writeBytesMethod.invoke(_bufferOutputStream, new Object[]{ bytes });
        }

        public void write(int b) throws Exception {
            SerializerTest.BufferOutputStream._writeByteMethod.invoke(_bufferOutputStream, b);
        }

        private BufferOutputStream(Serializer serializer) throws Exception {
            _bufferOutputStream = SerializerTest.BufferOutputStream._constructor.newInstance(serializer);
        }

        private static final Constructor<?> _constructor;

        private static final Method _writeByteMethod;

        private static final Method _writeBytesMethod;

        static {
            try {
                Class<?> clazz = Class.forName(((Serializer.class.getName()) + "$BufferOutputStream"));
                _constructor = clazz.getDeclaredConstructor(Serializer.class);
                SerializerTest.BufferOutputStream._constructor.setAccessible(true);
                _writeByteMethod = ReflectionTestUtil.getMethod(clazz, "write", int.class);
                _writeBytesMethod = ReflectionTestUtil.getMethod(clazz, "write", byte[].class);
            } catch (ReflectiveOperationException roe) {
                throw new ExceptionInInitializerError(roe);
            }
        }

        private final Object _bufferOutputStream;
    }

    private static class BufferQueue {
        public void enqueue(byte[] bytes) throws Exception {
            SerializerTest.BufferQueue._enqueueMethod.invoke(_bufferQueue, new Object[]{ bytes });
        }

        public int getCount() throws Exception {
            return SerializerTest.BufferQueue._countField.getInt(_bufferQueue);
        }

        public SerializerTest.BufferNode getHeadBufferNode() throws Exception {
            Object bufferNode = SerializerTest.BufferQueue._headBufferNodeField.get(_bufferQueue);
            if (bufferNode == null) {
                return null;
            }
            return new SerializerTest.BufferNode(bufferNode);
        }

        private BufferQueue() throws Exception {
            _bufferQueue = SerializerTest.BufferQueue._constructor.newInstance();
        }

        private BufferQueue(Object bufferQueue) throws Exception {
            _bufferQueue = bufferQueue;
        }

        private static final Constructor<?> _constructor;

        private static final Field _countField;

        private static final Method _enqueueMethod;

        private static final Field _headBufferNodeField;

        static {
            try {
                Class<?> clazz = Class.forName(((Serializer.class.getName()) + "$BufferQueue"));
                _constructor = clazz.getDeclaredConstructor();
                SerializerTest.BufferQueue._constructor.setAccessible(true);
                _countField = ReflectionTestUtil.getField(clazz, "_count");
                _headBufferNodeField = ReflectionTestUtil.getField(clazz, "_headBufferNode");
                _enqueueMethod = ReflectionTestUtil.getMethod(clazz, "enqueue", byte[].class);
            } catch (ReflectiveOperationException roe) {
                throw new ExceptionInInitializerError(roe);
            }
        }

        private final Object _bufferQueue;
    }
}

