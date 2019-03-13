/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.serialization.impl;


import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * MorphingPortableReader Tester.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MorphingPortableReaderTest {
    private SerializationServiceV1 service1;

    private SerializationServiceV1 service2;

    private PortableReader reader;

    @Test
    public void testReadInt() throws Exception {
        int aByte = reader.readInt("byte");
        int aShort = reader.readInt("short");
        int aChar = reader.readInt("char");
        int aInt = reader.readInt("int");
        Assert.assertEquals(1, aByte);
        Assert.assertEquals(3, aShort);
        Assert.assertEquals(2, aChar);
        Assert.assertEquals(4, aInt);
        Assert.assertEquals(0, reader.readInt("NO SUCH FIELD"));
    }

    @Test
    public void testReadLong() throws Exception {
        long aByte = reader.readLong("byte");
        long aShort = reader.readLong("short");
        long aChar = reader.readLong("char");
        long aInt = reader.readLong("int");
        long aLong = reader.readLong("long");
        Assert.assertEquals(1, aByte);
        Assert.assertEquals(3, aShort);
        Assert.assertEquals(2, aChar);
        Assert.assertEquals(4, aInt);
        Assert.assertEquals(5, aLong);
        Assert.assertEquals(0, reader.readLong("NO SUCH FIELD"));
    }

    @Test
    public void testReadUTF() throws Exception {
        String aString = reader.readUTF("string");
        Assert.assertEquals("test", aString);
        Assert.assertNull(reader.readUTF("NO SUCH FIELD"));
    }

    @Test
    public void testReadBoolean() throws Exception {
        boolean aBoolean = reader.readBoolean("boolean");
        Assert.assertTrue(aBoolean);
        Assert.assertFalse(reader.readBoolean("NO SUCH FIELD"));
    }

    @Test
    public void testReadByte() throws Exception {
        byte aByte = reader.readByte("byte");
        Assert.assertEquals(1, aByte);
        Assert.assertEquals(0, reader.readByte("NO SUCH FIELD"));
    }

    @Test
    public void testReadChar() throws Exception {
        char aChar = reader.readChar("char");
        Assert.assertEquals(2, aChar);
        Assert.assertEquals(0, reader.readChar("NO SUCH FIELD"));
    }

    @Test
    public void testReadDouble() throws Exception {
        double aByte = reader.readDouble("byte");
        double aShort = reader.readDouble("short");
        double aChar = reader.readDouble("char");
        double aInt = reader.readDouble("int");
        double aFloat = reader.readDouble("float");
        double aLong = reader.readDouble("long");
        double aDouble = reader.readDouble("double");
        Assert.assertEquals(1, aByte, 0);
        Assert.assertEquals(3, aShort, 0);
        Assert.assertEquals(2, aChar, 0);
        Assert.assertEquals(4, aInt, 0);
        Assert.assertEquals(5, aLong, 0);
        Assert.assertEquals(1.0F, aFloat, 0);
        Assert.assertEquals(2.0, aDouble, 0);
        Assert.assertEquals(0, reader.readDouble("NO SUCH FIELD"), 0);
    }

    @Test
    public void testReadFloat() throws Exception {
        float aByte = reader.readFloat("byte");
        float aShort = reader.readFloat("short");
        float aChar = reader.readFloat("char");
        float aInt = reader.readFloat("int");
        float aFloat = reader.readFloat("float");
        Assert.assertEquals(1, aByte, 0);
        Assert.assertEquals(3, aShort, 0);
        Assert.assertEquals(2, aChar, 0);
        Assert.assertEquals(4, aInt, 0);
        Assert.assertEquals(1.0F, aFloat, 0);
        Assert.assertEquals(0, reader.readFloat("NO SUCH FIELD"), 0);
    }

    @Test
    public void testReadShort() throws Exception {
        int aByte = reader.readShort("byte");
        int aShort = reader.readShort("short");
        Assert.assertEquals(1, aByte);
        Assert.assertEquals(3, aShort);
        Assert.assertEquals(0, reader.readShort("NO SUCH FIELD"));
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadInt_IncompatibleClass() throws Exception {
        reader.readInt("string");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadLong_IncompatibleClass() throws Exception {
        reader.readLong("string");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadUTF_IncompatibleClass() throws Exception {
        reader.readUTF("byte");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadBoolean_IncompatibleClass() throws Exception {
        reader.readBoolean("string");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadByte_IncompatibleClass() throws Exception {
        reader.readByte("string");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadChar_IncompatibleClass() throws Exception {
        reader.readChar("string");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadDouble_IncompatibleClass() throws Exception {
        reader.readDouble("string");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadFloat_IncompatibleClass() throws Exception {
        reader.readFloat("string");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadShort_IncompatibleClass() throws Exception {
        reader.readShort("string");
    }

    @Test
    public void testReadByteArray() throws Exception {
        Assert.assertNull(reader.readByteArray("NO SUCH FIELD"));
    }

    @Test
    public void testReadCharArray() throws Exception {
        Assert.assertNull(reader.readCharArray("NO SUCH FIELD"));
    }

    @Test
    public void testReadIntArray() throws Exception {
        Assert.assertNull(reader.readIntArray("NO SUCH FIELD"));
    }

    @Test
    public void testReadLongArray() throws Exception {
        Assert.assertNull(reader.readLongArray("NO SUCH FIELD"));
    }

    @Test
    public void testReadDoubleArray() throws Exception {
        Assert.assertNull(reader.readDoubleArray("NO SUCH FIELD"));
    }

    @Test
    public void testReadFloatArray() throws Exception {
        Assert.assertNull(reader.readFloatArray("NO SUCH FIELD"));
    }

    @Test
    public void testReadShortArray() throws Exception {
        Assert.assertNull(reader.readShortArray("NO SUCH FIELD"));
    }

    @Test
    public void testReadUTFArray() throws Exception {
        Assert.assertNull(reader.readUTFArray("NO SUCH FIELD"));
    }

    @Test
    public void testReadPortable() throws Exception {
        Assert.assertNull(reader.readPortable("NO SUCH FIELD"));
    }

    @Test
    public void testReadPortableArray() throws Exception {
        Assert.assertNull(reader.readPortableArray("NO SUCH FIELD"));
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadByteArray_IncompatibleClass() throws Exception {
        reader.readByteArray("byte");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadCharArray_IncompatibleClass() throws Exception {
        reader.readCharArray("byte");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadIntArray_IncompatibleClass() throws Exception {
        reader.readIntArray("byte");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadLongArray_IncompatibleClass() throws Exception {
        reader.readByteArray("byte");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadDoubleArray_IncompatibleClass() throws Exception {
        reader.readByteArray("byte");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadFloatArray_IncompatibleClass() throws Exception {
        reader.readByteArray("byte");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadShortArray_IncompatibleClass() throws Exception {
        reader.readByteArray("byte");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadPortable_IncompatibleClass() throws Exception {
        reader.readByteArray("byte");
    }

    @Test(expected = IncompatibleClassChangeError.class)
    public void testReadPortableArray_IncompatibleClass() throws Exception {
        reader.readByteArray("byte");
    }
}

