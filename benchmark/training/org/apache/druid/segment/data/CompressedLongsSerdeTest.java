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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CompressedLongsSerdeTest {
    protected final LongEncodingStrategy encodingStrategy;

    protected final CompressionStrategy compressionStrategy;

    protected final ByteOrder order;

    private final long[] values0 = new long[]{  };

    private final long[] values1 = new long[]{ 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1 };

    private final long[] values2 = new long[]{ 12, 5, 2, 9, 3, 2, 5, 1, 0, 6, 13, 10, 15 };

    private final long[] values3 = new long[]{ 1, 1, 1, 1, 1, 11, 11, 11, 11 };

    private final long[] values4 = new long[]{ 200, 200, 200, 401, 200, 301, 200, 200, 200, 404, 200, 200, 200, 200 };

    private final long[] values5 = new long[]{ 123, 632, 12, 39, 536, 0, 1023, 52, 777, 526, 214, 562, 823, 346 };

    private final long[] values6 = new long[]{ 1000000, 1000001, 1000002, 1000003, 1000004, 1000005, 1000006, 1000007, 1000008 };

    private final long[] values7 = new long[]{ Long.MAX_VALUE, Long.MIN_VALUE, 12378, -12718243, -1236213, 12743153, 21364375452L, 65487435436632L, -43734526234564L };

    private final long[] values8 = new long[]{ Long.MAX_VALUE, 0, 321, 15248425, 13523212136L, 63822, 3426, 96 };

    public CompressedLongsSerdeTest(CompressionFactory.LongEncodingStrategy encodingStrategy, CompressionStrategy compressionStrategy, ByteOrder order) {
        this.encodingStrategy = encodingStrategy;
        this.compressionStrategy = compressionStrategy;
        this.order = order;
    }

    @Test
    public void testValueSerde() throws Exception {
        testWithValues(values0);
        testWithValues(values1);
        testWithValues(values2);
        testWithValues(values3);
        testWithValues(values4);
        testWithValues(values5);
        testWithValues(values6);
        testWithValues(values7);
        testWithValues(values8);
    }

    @Test
    public void testChunkSerde() throws Exception {
        long[] chunk = new long[10000];
        for (int i = 0; i < 10000; i++) {
            chunk[i] = i;
        }
        testWithValues(chunk);
    }
}

