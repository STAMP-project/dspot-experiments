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


import CompressionFactory.LongEncodingStrategy;
import java.nio.ByteOrder;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CompressedLongsAutoEncodingSerdeTest {
    private static final long[] bitsPerValueParameters = new long[]{ 1, 2, 4, 7, 11, 14, 18, 23, 31, 39, 46, 55, 62 };

    protected final LongEncodingStrategy encodingStrategy = LongEncodingStrategy.AUTO;

    protected final CompressionStrategy compressionStrategy;

    protected final ByteOrder order;

    protected final long bitsPerValue;

    public CompressedLongsAutoEncodingSerdeTest(long bitsPerValue, CompressionStrategy compressionStrategy, ByteOrder order) {
        this.bitsPerValue = bitsPerValue;
        this.compressionStrategy = compressionStrategy;
        this.order = order;
    }

    @Test
    public void testFidelity() throws Exception {
        final long bound = 1L << (bitsPerValue);
        // big enough to have at least 2 blocks, and a handful of sizes offset by 1 from each other
        int blockSize = 1 << 16;
        int numBits = (Long.SIZE) - (Long.numberOfLeadingZeros((1 << ((bitsPerValue) - 1))));
        double numValuesPerByte = 8.0 / ((double) (numBits));
        int numRows = (((int) (blockSize * numValuesPerByte)) * 2) + (ThreadLocalRandom.current().nextInt(1, 101));
        long[] chunk = new long[numRows];
        for (int i = 0; i < numRows; i++) {
            chunk[i] = ThreadLocalRandom.current().nextLong(bound);
        }
        testValues(chunk);
        numRows++;
        chunk = new long[numRows];
        for (int i = 0; i < numRows; i++) {
            chunk[i] = ThreadLocalRandom.current().nextLong(bound);
        }
        testValues(chunk);
    }
}

