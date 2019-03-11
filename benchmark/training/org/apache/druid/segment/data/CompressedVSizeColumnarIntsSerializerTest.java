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
import java.util.Random;
import org.apache.druid.segment.writeout.OffHeapMemorySegmentWriteOutMedium;
import org.apache.druid.segment.writeout.SegmentWriteOutMedium;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CompressedVSizeColumnarIntsSerializerTest {
    private static final int[] MAX_VALUES = new int[]{ 255, 65535, 16777215, 268435455 };

    private final SegmentWriteOutMedium segmentWriteOutMedium = new OffHeapMemorySegmentWriteOutMedium();

    private final CompressionStrategy compressionStrategy;

    private final ByteOrder byteOrder;

    private final Random rand = new Random(0);

    private int[] vals;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    public CompressedVSizeColumnarIntsSerializerTest(CompressionStrategy compressionStrategy, ByteOrder byteOrder) {
        this.compressionStrategy = compressionStrategy;
        this.byteOrder = byteOrder;
    }

    @Test
    public void testSmallData() throws Exception {
        // less than one chunk
        for (int maxValue : CompressedVSizeColumnarIntsSerializerTest.MAX_VALUES) {
            final int maxChunkSize = CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForValue(maxValue);
            generateVals(rand.nextInt(maxChunkSize), maxValue);
            checkSerializedSizeAndData(maxChunkSize);
        }
    }

    @Test
    public void testLargeData() throws Exception {
        // more than one chunk
        for (int maxValue : CompressedVSizeColumnarIntsSerializerTest.MAX_VALUES) {
            final int maxChunkSize = CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForValue(maxValue);
            generateVals(((((rand.nextInt(5)) + 5) * maxChunkSize) + (rand.nextInt(maxChunkSize))), maxValue);
            checkSerializedSizeAndData(maxChunkSize);
        }
    }

    @Test
    public void testEmpty() throws Exception {
        vals = new int[0];
        checkSerializedSizeAndData(2);
    }

    @Test
    public void testMultiValueFileLargeData() throws Exception {
        for (int maxValue : CompressedVSizeColumnarIntsSerializerTest.MAX_VALUES) {
            final int maxChunkSize = CompressedVSizeColumnarIntsSupplier.maxIntsInBufferForValue(maxValue);
            generateVals(((((rand.nextInt(5)) + 5) * maxChunkSize) + (rand.nextInt(maxChunkSize))), maxValue);
            checkV2SerializedSizeAndData(maxChunkSize);
        }
    }
}

