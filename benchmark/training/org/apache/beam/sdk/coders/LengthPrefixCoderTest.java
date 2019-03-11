/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.coders;


import Coder.Context.NESTED;
import GlobalWindow.Coder.INSTANCE;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link LengthPrefixCoder}.
 */
@RunWith(JUnit4.class)
public class LengthPrefixCoderTest {
    private static final StructuredCoder<byte[]> TEST_CODER = LengthPrefixCoder.of(ByteArrayCoder.of());

    private static final List<byte[]> TEST_VALUES = Arrays.asList(new byte[]{ 10, 11, 12 }, new byte[]{ 13, 3 }, new byte[]{ 13, 14 }, new byte[]{  });

    /**
     * Generated data to check that the wire format has not changed. To regenerate, see {@link org.apache.beam.sdk.coders.PrintBase64Encodings}.
     */
    private static final ImmutableList<String> TEST_ENCODINGS = ImmutableList.of("AwoLDA", "Ag0D", "Ag0O", "AA");

    @Test
    public void testCoderSerializable() throws Exception {
        CoderProperties.coderSerializable(LengthPrefixCoderTest.TEST_CODER);
    }

    @Test
    public void testCoderIsSerializableWithWellKnownCoderType() throws Exception {
        CoderProperties.coderSerializable(LengthPrefixCoder.of(INSTANCE));
    }

    @Test
    public void testEncodedSize() throws Exception {
        Assert.assertEquals(5L, LengthPrefixCoderTest.TEST_CODER.getEncodedElementByteSize(LengthPrefixCoderTest.TEST_VALUES.get(0)));
    }

    @Test
    public void testObserverIsCheap() throws Exception {
        LengthPrefixCoder<Double> coder = LengthPrefixCoder.of(DoubleCoder.of());
        Assert.assertTrue(coder.isRegisterByteSizeObserverCheap(5.0));
    }

    @Test
    public void testObserverIsNotCheap() throws Exception {
        LengthPrefixCoder<List<String>> coder = LengthPrefixCoder.of(ListCoder.of(StringUtf8Coder.of()));
        Assert.assertFalse(coder.isRegisterByteSizeObserverCheap(ImmutableList.of("hi", "test")));
    }

    @Test
    public void testDecodeEncodeEquals() throws Exception {
        for (byte[] value : LengthPrefixCoderTest.TEST_VALUES) {
            CoderProperties.coderDecodeEncodeEqual(LengthPrefixCoderTest.TEST_CODER, value);
        }
    }

    @Test
    public void testRegisterByteSizeObserver() throws Exception {
        CoderProperties.testByteCount(LengthPrefixCoder.of(VarIntCoder.of()), NESTED, new Integer[]{ 0, 10, 1000 });
    }

    @Test
    public void testStructuralValueConsistentWithEquals() throws Exception {
        for (byte[] value1 : LengthPrefixCoderTest.TEST_VALUES) {
            for (byte[] value2 : LengthPrefixCoderTest.TEST_VALUES) {
                CoderProperties.structuralValueConsistentWithEquals(LengthPrefixCoderTest.TEST_CODER, value1, value2);
            }
        }
    }

    @Test
    public void testWireFormatEncode() throws Exception {
        CoderProperties.coderEncodesBase64(LengthPrefixCoderTest.TEST_CODER, LengthPrefixCoderTest.TEST_VALUES, LengthPrefixCoderTest.TEST_ENCODINGS);
    }

    @Test
    public void testMultiCoderCycle() throws Exception {
        LengthPrefixCoder<Long> lengthPrefixedValueCoder = LengthPrefixCoder.of(BigEndianLongCoder.of());
        LengthPrefixCoder<byte[]> lengthPrefixedBytesCoder = LengthPrefixCoder.of(ByteArrayCoder.of());
        // [0x08, 0, 0, 0, 0, 0, 0, 0, 0x16]
        byte[] userEncoded = CoderUtils.encodeToByteArray(lengthPrefixedValueCoder, 22L);
        // [0, 0, 0, 0, 0, 0, 0, 0x16]
        byte[] decodedToBytes = CoderUtils.decodeFromByteArray(lengthPrefixedBytesCoder, userEncoded);
        // [0x08, 0, 0, 0, 0, 0, 0, 0, 0x16]
        byte[] reencodedBytes = CoderUtils.encodeToByteArray(lengthPrefixedBytesCoder, decodedToBytes);
        long userDecoded = CoderUtils.decodeFromByteArray(lengthPrefixedValueCoder, reencodedBytes);
        Assert.assertFalse("Length-prefix decoding to bytes should drop the length", Arrays.equals(userEncoded, decodedToBytes));
        Assert.assertArrayEquals(userEncoded, reencodedBytes);
        Assert.assertEquals(22L, userDecoded);
    }
}

