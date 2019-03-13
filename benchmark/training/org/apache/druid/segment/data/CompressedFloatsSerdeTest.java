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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CompressedFloatsSerdeTest {
    private static final double DELTA = 1.0E-5;

    protected final CompressionStrategy compressionStrategy;

    protected final ByteOrder order;

    private final float[] values0 = new float[]{  };

    private final float[] values1 = new float[]{ 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F };

    private final float[] values2 = new float[]{ 13.2F, 6.1F, 0.001F, 123.0F, 12572.0F, 123.1F, 784.4F, 6892.8633F, 8.341111F };

    private final float[] values3 = new float[]{ 0.001F, 0.001F, 0.001F, 0.001F, 0.001F, 100.0F, 100.0F, 100.0F, 100.0F, 100.0F };

    private final float[] values4 = new float[]{ 0.0F, 0.0F, 0.0F, 0.0F, 0.01F, 0.0F, 0.0F, 0.0F, 21.22F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F };

    private final float[] values5 = new float[]{ 123.16F, 1.12F, 62.0F, 462.12F, 517.71F, 56.54F, 971.32F, 824.22F, 472.12F, 625.26F };

    private final float[] values6 = new float[]{ 1000000.0F, 1000001.0F, 1000002.0F, 1000003.0F, 1000004.0F, 1000005.0F, 1000006.0F, 1000007.0F, 1000008.0F };

    private final float[] values7 = new float[]{ Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, 12378.573F, -1.2718244E7F, -9.3653656E7F, 1.2743153E7F, 21431.414F, 6.5487434E13F, -4.3734528E13F };

    public CompressedFloatsSerdeTest(CompressionStrategy compressionStrategy, ByteOrder order) {
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
    }

    @Test
    public void testChunkSerde() throws Exception {
        float[] chunk = new float[10000];
        for (int i = 0; i < 10000; i++) {
            chunk[i] = i;
        }
        testWithValues(chunk);
    }
}

