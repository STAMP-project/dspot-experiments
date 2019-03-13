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


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public abstract class AbstractDataStreamIntegrationTest<O extends ObjectDataOutput, I extends ObjectDataInput> {
    @Parameterized.Parameter
    public ByteOrder byteOrder;

    protected O out;

    protected I input;

    protected InternalSerializationService serializationService;

    @Test
    public void testByte() throws IOException {
        out.write(Byte.MAX_VALUE);
        out.write(Byte.MIN_VALUE);
        write(0);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertEquals(Byte.MAX_VALUE, in.readByte());
        Assert.assertEquals(Byte.MIN_VALUE, in.readByte());
        Assert.assertEquals(0, in.readByte());
    }

    @Test
    public void testByteArray() throws IOException {
        byte[] arr = new byte[]{ Byte.MIN_VALUE, Byte.MAX_VALUE, 0 };
        writeByteArray(arr);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertArrayEquals(arr, in.readByteArray());
    }

    @Test
    public void testUnsignedByte() throws IOException {
        write(255);
        write((-1));
        write(((Byte.MAX_VALUE) + 1));
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertEquals(255, in.readUnsignedByte());
        Assert.assertEquals(255, in.readUnsignedByte());
        Assert.assertEquals(((Byte.MAX_VALUE) + 1), in.readUnsignedByte());
    }

    @Test
    public void testShort() throws IOException {
        // negative shorts
        out.writeShort(Short.MIN_VALUE);
        writeShort((-250));
        // positive shorts
        writeShort(132);
        out.writeShort(Short.MAX_VALUE);
        ObjectDataInput input = getDataInputFromOutput();
        Assert.assertEquals(Short.MIN_VALUE, input.readShort());
        Assert.assertEquals((-250), input.readShort());
        Assert.assertEquals(132, input.readShort());
        Assert.assertEquals(Short.MAX_VALUE, input.readShort());
    }

    @Test
    public void testShortArray() throws IOException {
        short[] arr = new short[]{ Short.MIN_VALUE, -250, 132, Short.MAX_VALUE };
        writeShortArray(arr);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertArrayEquals(arr, in.readShortArray());
    }

    @Test
    public void testUnsignedShort() throws IOException {
        // unsigned short, out of range of signed short
        writeShort(((Short.MAX_VALUE) + 200));
        writeShort(((Short.MAX_VALUE) + 1));
        writeShort(65535);
        ObjectDataInput input = getDataInputFromOutput();
        Assert.assertEquals(((Short.MAX_VALUE) + 200), input.readUnsignedShort());
        Assert.assertEquals(((Short.MAX_VALUE) + 1), input.readUnsignedShort());
        Assert.assertEquals(65535, input.readUnsignedShort());
    }

    @Test
    public void testInt() throws IOException {
        writeInt(Integer.MAX_VALUE);
        writeInt(Integer.MIN_VALUE);
        writeInt((-1));
        writeInt(132);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertEquals(Integer.MAX_VALUE, in.readInt());
        Assert.assertEquals(Integer.MIN_VALUE, in.readInt());
        Assert.assertEquals((-1), in.readInt());
        Assert.assertEquals(132, in.readInt());
    }

    @Test
    public void testIntArray() throws IOException {
        int[] arr = new int[]{ Integer.MIN_VALUE, -250, 132, Integer.MAX_VALUE };
        writeIntArray(arr);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertArrayEquals(arr, in.readIntArray());
    }

    @Test
    public void testBoolean() throws IOException {
        writeBoolean(false);
        writeBoolean(true);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertFalse(in.readBoolean());
        Assert.assertTrue(in.readBoolean());
    }

    @Test
    public void testBooleanArray() throws IOException {
        boolean[] arr = new boolean[]{ true, false };
        writeBooleanArray(arr);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertArrayEquals(arr, in.readBooleanArray());
    }

    @Test
    public void testChar() throws IOException {
        writeChar(Character.MIN_VALUE);
        writeChar(Character.MAX_VALUE);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertEquals(Character.MIN_VALUE, in.readChar());
        Assert.assertEquals(Character.MAX_VALUE, in.readChar());
    }

    @Test
    public void testCharArray() throws IOException {
        char[] arr = new char[]{ Character.MIN_VALUE, Character.MAX_VALUE };
        writeCharArray(arr);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertArrayEquals(arr, in.readCharArray());
    }

    @Test
    public void testDouble() throws IOException {
        writeDouble(Double.MIN_VALUE);
        out.writeDouble((-1));
        writeDouble(Math.PI);
        writeDouble(Double.MAX_VALUE);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertEquals(Double.MIN_VALUE, in.readDouble(), 1.0E-10);
        Assert.assertEquals((-1), in.readDouble(), 1.0E-10);
        Assert.assertEquals(Math.PI, in.readDouble(), 1.0E-10);
        Assert.assertEquals(Double.MAX_VALUE, in.readDouble(), 1.0E-10);
    }

    @Test
    public void testDoubleArray() throws IOException {
        double[] arr = new double[]{ Double.MIN_VALUE, -1, Math.PI, Double.MAX_VALUE };
        writeDoubleArray(arr);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertArrayEquals(arr, in.readDoubleArray(), 1.0E-10);
    }

    @Test
    public void testFloat() throws IOException {
        writeFloat(Float.MIN_VALUE);
        out.writeFloat((-1));
        writeFloat(Float.MAX_VALUE);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertEquals(Float.MIN_VALUE, in.readFloat(), 1.0E-10F);
        Assert.assertEquals((-1), in.readFloat(), 1.0E-10F);
        Assert.assertEquals(Float.MAX_VALUE, in.readFloat(), 1.0E-10F);
    }

    @Test
    public void testFloatArray() throws IOException {
        float[] arr = new float[]{ Float.MIN_VALUE, -1, Float.MAX_VALUE };
        writeFloatArray(arr);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertArrayEquals(arr, in.readFloatArray(), 1.0E-10F);
    }

    @Test
    public void testLong() throws IOException {
        writeLong(Long.MIN_VALUE);
        out.writeLong((-1));
        writeLong(Long.MAX_VALUE);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertEquals(Long.MIN_VALUE, in.readLong());
        Assert.assertEquals((-1), in.readLong());
        Assert.assertEquals(Long.MAX_VALUE, in.readLong());
    }

    @Test
    public void testLongArray() throws IOException {
        long[] arr = new long[]{ Long.MIN_VALUE, -1, Long.MAX_VALUE };
        writeLongArray(arr);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertArrayEquals(arr, in.readLongArray());
    }

    @Test
    public void testUTF() throws IOException {
        String s1 = "Vim is a text editor that is upwards compatible to Vi. It can be used to edit all kinds of plain text.";
        String s2 = "??????????????????????????????????????????????????????";
        writeUTF(s1);
        writeUTF(s2);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertEquals(s1, in.readUTF());
        Assert.assertEquals(s2, in.readUTF());
    }

    @Test
    public void testUTFArray() throws IOException {
        String s1 = "Vim is a text editor that is upwards compatible to Vi. It can be used to edit all kinds of plain text.";
        String s2 = "??????????????????????????????????????????????????????";
        String[] arr = new String[]{ s1, s2 };
        writeUTFArray(arr);
        ObjectDataInput in = getDataInputFromOutput();
        Assert.assertArrayEquals(arr, in.readUTFArray());
    }
}

