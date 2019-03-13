/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.data;


import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static CompressedColumnarIntsSupplier.MAX_INTS_IN_BUFFER;


@RunWith(Parameterized.class)
public class V3CompressedVSizeColumnarMultiIntsSerializerTest {
    private static final int[] OFFSET_CHUNK_FACTORS = new int[]{ 1, 2, 100, MAX_INTS_IN_BUFFER };

    private static final int[] MAX_VALUES = new int[]{ 255, 65535, 16777215, 268435455 };

    private final CompressionStrategy compressionStrategy;

    private final ByteOrder byteOrder;

    private final Random rand = new Random(0);

    private List<int[]> vals;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    public V3CompressedVSizeColumnarMultiIntsSerializerTest(CompressionStrategy compressionStrategy, ByteOrder byteOrder) {
        this.compressionStrategy = compressionStrategy;
        this.byteOrder = byteOrder;
    }

    @Test
    public void testSmallData() throws Exception {
        // less than one chunk
        for (int offsetChunk : V3CompressedVSizeColumnarMultiIntsSerializerTest.OFFSET_CHUNK_FACTORS) {
            for (int maxValue : V3CompressedVSizeColumnarMultiIntsSerializerTest.MAX_VALUES) {
                final int valueChunk = CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForValue(maxValue);
                generateVals(rand.nextInt(valueChunk), maxValue);
                checkSerializedSizeAndData(offsetChunk, valueChunk);
            }
        }
    }

    @Test
    public void testLargeData() throws Exception {
        // more than one chunk
        for (int offsetChunk : V3CompressedVSizeColumnarMultiIntsSerializerTest.OFFSET_CHUNK_FACTORS) {
            for (int maxValue : V3CompressedVSizeColumnarMultiIntsSerializerTest.MAX_VALUES) {
                final int valueChunk = CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForValue(maxValue);
                generateVals(((((rand.nextInt(2)) + 1) * valueChunk) + (rand.nextInt(valueChunk))), maxValue);
                checkSerializedSizeAndData(offsetChunk, valueChunk);
            }
        }
    }

    @Test
    public void testEmpty() throws Exception {
        vals = new ArrayList<>();
        checkSerializedSizeAndData(1, 2);
    }

    @Test
    public void testMultiValueFileLargeData() throws Exception {
        // more than one chunk
        for (int offsetChunk : V3CompressedVSizeColumnarMultiIntsSerializerTest.OFFSET_CHUNK_FACTORS) {
            for (int maxValue : V3CompressedVSizeColumnarMultiIntsSerializerTest.MAX_VALUES) {
                final int valueChunk = CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForValue(maxValue);
                generateVals(((((rand.nextInt(2)) + 1) * valueChunk) + (rand.nextInt(valueChunk))), maxValue);
                checkV2SerializedSizeAndData(offsetChunk, valueChunk);
            }
        }
    }
}

