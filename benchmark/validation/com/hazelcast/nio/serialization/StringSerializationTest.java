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
package com.hazelcast.nio.serialization;


import SerializationConstants.CONSTANT_TYPE_STRING_ARRAY;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class StringSerializationTest {
    private static final String TEST_DATA_TURKISH = "Pijamal? hasta, ya??z ?of?re ?abucak g?vendi.";

    private static final String TEST_DATA_JAPANESE = "??????? ????? ?????? ?????";

    private static final String TEST_DATA_ASCII = "The quick brown fox jumps over the lazy dog";

    private static final String TEST_DATA_ALL = ((StringSerializationTest.TEST_DATA_TURKISH) + (StringSerializationTest.TEST_DATA_JAPANESE)) + (StringSerializationTest.TEST_DATA_ASCII);

    private static final int TEST_STR_SIZE = 1 << 20;

    private static final byte[] TEST_DATA_BYTES_ALL = StringSerializationTest.TEST_DATA_ALL.getBytes(Charset.forName("utf8"));

    private static final char[] allChars;

    private InternalSerializationService serializationService;

    static {
        CharBuffer cb = CharBuffer.allocate(Character.MAX_VALUE);
        for (char c = 0; c < (Character.MAX_VALUE); c++) {
            if (Character.isLetter(c)) {
                cb.append(c);
            }
        }
        allChars = cb.array();
    }

    @Test
    public void testStringEncode() {
        byte[] expected = toDataByte(StringSerializationTest.TEST_DATA_BYTES_ALL, StringSerializationTest.TEST_DATA_ALL.length());
        byte[] actual = serializationService.toBytes(StringSerializationTest.TEST_DATA_ALL);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testStringDecode() {
        Data data = new HeapData(toDataByte(StringSerializationTest.TEST_DATA_BYTES_ALL, StringSerializationTest.TEST_DATA_ALL.length()));
        String actualStr = serializationService.toObject(data);
        Assert.assertEquals(StringSerializationTest.TEST_DATA_ALL, actualStr);
    }

    @Test
    public void testStringAllCharLetterEncode() {
        String allStr = new String(StringSerializationTest.allChars);
        byte[] expected = allStr.getBytes(Charset.forName("utf8"));
        byte[] bytes = serializationService.toBytes(allStr);
        byte[] actual = Arrays.copyOfRange(bytes, ((HeapData.DATA_OFFSET) + (Bits.INT_SIZE_IN_BYTES)), bytes.length);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testLargeStringEncodeDecode() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int j = 0;
        while (j < (StringSerializationTest.TEST_STR_SIZE)) {
            int ch = (i++) % (Character.MAX_VALUE);
            if (Character.isLetter(ch)) {
                sb.append(ch);
                j++;
            }
        } 
        String actualStr = sb.toString();
        byte[] strBytes = actualStr.getBytes(Charset.forName("utf8"));
        byte[] actualDataBytes = serializationService.toBytes(actualStr);
        byte[] expectedDataByte = toDataByte(strBytes, actualStr.length());
        String decodedStr = serializationService.toObject(new HeapData(expectedDataByte));
        Assert.assertArrayEquals("Deserialized byte array do not match utf-8 encoding", expectedDataByte, actualDataBytes);
        Assert.assertEquals(decodedStr, actualStr);
    }

    @Test
    public void testNullStringEncodeDecode() {
        Data nullData = serializationService.toData(null);
        String decodedStr = serializationService.toObject(nullData);
        Assert.assertNull(decodedStr);
    }

    @Test
    public void testNullStringEncodeDecode2() throws Exception {
        BufferObjectDataOutput objectDataOutput = serializationService.createObjectDataOutput();
        objectDataOutput.writeUTF(null);
        byte[] bytes = objectDataOutput.toByteArray();
        objectDataOutput.close();
        BufferObjectDataInput objectDataInput = serializationService.createObjectDataInput(bytes);
        String decodedStr = objectDataInput.readUTF();
        Assert.assertNull(decodedStr);
    }

    @Test
    public void testStringAllCharLetterDecode() {
        String allStr = new String(StringSerializationTest.allChars);
        byte[] expected = allStr.getBytes(Charset.forName("utf8"));
        Data data = new HeapData(toDataByte(expected, allStr.length()));
        String actualStr = serializationService.toObject(data);
        Assert.assertEquals(allStr, actualStr);
    }

    @Test
    public void testStringArrayEncodeDecode() {
        String[] stringArray = new String[100];
        for (int i = 0; i < (stringArray.length); i++) {
            stringArray[i] = (StringSerializationTest.TEST_DATA_ALL) + i;
        }
        Data dataStrArray = serializationService.toData(stringArray);
        String[] actualStr = serializationService.toObject(dataStrArray);
        Assert.assertEquals(CONSTANT_TYPE_STRING_ARRAY, dataStrArray.getType());
        Assert.assertArrayEquals(stringArray, actualStr);
    }
}

