/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.evtx.parser;


import com.google.common.base.Charsets;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class BinaryReaderTest {
    private TestBinaryReaderBuilder testBinaryReaderBuilder;

    @Test
    public void testRead() throws IOException {
        byte b = 35;
        BinaryReader binaryReader = testBinaryReaderBuilder.put(b).build();
        Assert.assertEquals(b, binaryReader.read());
        Assert.assertEquals(1, binaryReader.getPosition());
    }

    @Test
    public void testPeek() throws IOException {
        byte b = 35;
        BinaryReader binaryReader = testBinaryReaderBuilder.put(new byte[]{ b }).build();
        Assert.assertEquals(b, binaryReader.peek());
        Assert.assertEquals(0, binaryReader.getPosition());
    }

    @Test
    public void testReadBytesJustLength() throws IOException {
        byte[] bytes = "Hello world".getBytes(Charsets.US_ASCII);
        BinaryReader binaryReader = testBinaryReaderBuilder.put(bytes).build();
        Assert.assertArrayEquals(Arrays.copyOfRange(bytes, 0, 5), binaryReader.readBytes(5));
        Assert.assertEquals(5, binaryReader.getPosition());
    }

    @Test
    public void testPeekBytes() throws IOException {
        byte[] bytes = "Hello world".getBytes(Charsets.US_ASCII);
        BinaryReader binaryReader = testBinaryReaderBuilder.put(bytes).build();
        Assert.assertArrayEquals(Arrays.copyOfRange(bytes, 0, 5), binaryReader.peekBytes(5));
        Assert.assertEquals(0, binaryReader.getPosition());
    }

    @Test
    public void testReadBytesBufOffsetLength() throws IOException {
        byte[] bytes = "Hello world".getBytes(Charsets.US_ASCII);
        byte[] buf = new byte[5];
        BinaryReader binaryReader = testBinaryReaderBuilder.put(bytes).build();
        binaryReader.readBytes(buf, 0, 5);
        Assert.assertArrayEquals(Arrays.copyOfRange(bytes, 0, 5), buf);
        Assert.assertEquals(5, binaryReader.getPosition());
    }

    @Test
    public void testReadGuid() throws IOException {
        String guid = "33323130-3534-3736-3839-616263646566";
        BinaryReader binaryReader = testBinaryReaderBuilder.putGuid(guid).build();
        Assert.assertEquals(guid, binaryReader.readGuid());
        Assert.assertEquals(16, binaryReader.getPosition());
    }

    @Test(expected = IOException.class)
    public void testReadStringNotNullTerminated() throws IOException {
        String value = "Hello world";
        BinaryReader binaryReader = testBinaryReaderBuilder.put(value.getBytes(Charsets.US_ASCII)).build();
        binaryReader.readString(value.length());
    }

    @Test
    public void testReadString() throws IOException {
        String value = "Hello world";
        BinaryReader binaryReader = testBinaryReaderBuilder.putString(value).build();
        Assert.assertEquals(value, binaryReader.readString(((value.length()) + 1)));
        Assert.assertEquals(((value.length()) + 1), binaryReader.getPosition());
    }

    @Test
    public void testReadWString() throws IOException {
        String value = "Hello world";
        BinaryReader binaryReader = testBinaryReaderBuilder.putWString(value).build();
        Assert.assertEquals(value, binaryReader.readWString(value.length()));
        Assert.assertEquals(((value.length()) * 2), binaryReader.getPosition());
    }

    @Test
    public void testReadQWord() throws IOException {
        UnsignedLong longValue = UnsignedLong.fromLongBits(((Long.MAX_VALUE) + 500));
        BinaryReader binaryReader = testBinaryReaderBuilder.putQWord(longValue).build();
        Assert.assertEquals(longValue, binaryReader.readQWord());
        Assert.assertEquals(8, binaryReader.getPosition());
    }

    @Test
    public void testReadDWord() throws IOException {
        UnsignedInteger intValue = UnsignedInteger.fromIntBits(((Integer.MAX_VALUE) + 500));
        BinaryReader binaryReader = testBinaryReaderBuilder.putDWord(intValue).build();
        Assert.assertEquals(intValue, binaryReader.readDWord());
        Assert.assertEquals(4, binaryReader.getPosition());
    }

    @Test
    public void testReadDWordBE() throws IOException {
        UnsignedInteger intValue = UnsignedInteger.fromIntBits(((Integer.MAX_VALUE) + 500));
        BinaryReader binaryReader = testBinaryReaderBuilder.putDWordBE(intValue).build();
        Assert.assertEquals(intValue, binaryReader.readDWordBE());
        Assert.assertEquals(4, binaryReader.getPosition());
    }

    @Test
    public void testReadWord() throws IOException {
        int intValue = (Short.MAX_VALUE) + 500;
        BinaryReader binaryReader = testBinaryReaderBuilder.putWord(intValue).build();
        Assert.assertEquals(intValue, binaryReader.readWord());
        Assert.assertEquals(2, binaryReader.getPosition());
    }

    @Test
    public void testReadWordBE() throws IOException {
        int intValue = (Short.MAX_VALUE) + 500;
        BinaryReader binaryReader = testBinaryReaderBuilder.putWordBE(intValue).build();
        Assert.assertEquals(intValue, binaryReader.readWordBE());
        Assert.assertEquals(2, binaryReader.getPosition());
    }

    @Test
    public void testReadFileTIme() throws IOException {
        Date date = new Date();
        BinaryReader binaryReader = testBinaryReaderBuilder.putFileTime(date).build();
        Assert.assertEquals(date.getTime(), binaryReader.readFileTime().getTime());
        Assert.assertEquals(8, binaryReader.getPosition());
    }

    @Test
    public void testReadAndBase64EncodeBinary() throws IOException {
        String orig = "Hello World";
        String stringValue = Base64.getEncoder().encodeToString(orig.getBytes(Charsets.US_ASCII));
        BinaryReader binaryReader = testBinaryReaderBuilder.putBase64EncodedBinary(stringValue).build();
        Assert.assertEquals(stringValue, binaryReader.readAndBase64EncodeBinary(orig.length()));
        Assert.assertEquals(orig.length(), binaryReader.getPosition());
    }

    @Test
    public void testSkip() throws IOException {
        BinaryReader binaryReader = new BinaryReader(null);
        binaryReader.skip(10);
        Assert.assertEquals(10, binaryReader.getPosition());
    }

    @Test
    public void testReaderPositionConstructor() throws IOException {
        String value = "Hello world";
        BinaryReader binaryReader = new BinaryReader(new BinaryReader(value.getBytes(Charsets.UTF_16LE)), 2);
        Assert.assertEquals(value.substring(1), binaryReader.readWString(((value.length()) - 1)));
        Assert.assertEquals(((value.length()) * 2), binaryReader.getPosition());
    }

    @Test
    public void testInputStreamSizeConstructor() throws IOException {
        String value = "Hello world";
        BinaryReader binaryReader = new BinaryReader(new ByteArrayInputStream(value.getBytes(Charsets.UTF_16LE)), 10);
        Assert.assertEquals(value.substring(0, 5), binaryReader.readWString(5));
        Assert.assertEquals(10, binaryReader.getPosition());
    }
}

