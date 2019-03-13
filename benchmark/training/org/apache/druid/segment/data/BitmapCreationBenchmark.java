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


import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import java.nio.ByteBuffer;
import java.util.Random;
import org.apache.druid.collections.bitmap.BitmapFactory;
import org.apache.druid.collections.bitmap.ImmutableBitmap;
import org.apache.druid.collections.bitmap.MutableBitmap;
import org.apache.druid.java.util.common.logger.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@Ignore
@RunWith(Parameterized.class)
public class BitmapCreationBenchmark extends AbstractBenchmark {
    private static final Logger log = new Logger(BitmapCreationBenchmark.class);

    final BitmapFactory factory;

    public BitmapCreationBenchmark(Class<? extends BitmapSerdeFactory> clazz) throws IllegalAccessException, InstantiationException {
        BitmapSerdeFactory serdeFactory = clazz.newInstance();
        factory = serdeFactory.getBitmapFactory();
    }

    private static final int numBits = 100000;

    static Random random;

    static int[] randIndex = new int[BitmapCreationBenchmark.numBits];

    ImmutableBitmap baseImmutableBitmap;

    MutableBitmap baseMutableBitmap;

    byte[] baseBytes;

    ByteBuffer baseByteBuffer;

    @BenchmarkOptions(warmupRounds = 10, benchmarkRounds = 1000)
    @Test
    public void testLinearAddition() {
        MutableBitmap mutableBitmap = factory.makeEmptyMutableBitmap();
        for (int i = 0; i < (BitmapCreationBenchmark.numBits); ++i) {
            mutableBitmap.add(i);
        }
        Assert.assertEquals(BitmapCreationBenchmark.numBits, mutableBitmap.size());
    }

    @BenchmarkOptions(warmupRounds = 10, benchmarkRounds = 10)
    @Test
    public void testRandomAddition() {
        MutableBitmap mutableBitmap = factory.makeEmptyMutableBitmap();
        for (int i : BitmapCreationBenchmark.randIndex) {
            mutableBitmap.add(i);
        }
        Assert.assertEquals(BitmapCreationBenchmark.numBits, mutableBitmap.size());
    }

    @BenchmarkOptions(warmupRounds = 10, benchmarkRounds = 1000)
    @Test
    public void testLinearAdditionDescending() {
        MutableBitmap mutableBitmap = factory.makeEmptyMutableBitmap();
        for (int i = (BitmapCreationBenchmark.numBits) - 1; i >= 0; --i) {
            mutableBitmap.add(i);
        }
        Assert.assertEquals(BitmapCreationBenchmark.numBits, mutableBitmap.size());
    }

    @BenchmarkOptions(warmupRounds = 10, benchmarkRounds = 1000)
    @Test
    public void testToImmutableByteArray() {
        ImmutableBitmap immutableBitmap = factory.makeImmutableBitmap(baseMutableBitmap);
        Assert.assertArrayEquals(baseBytes, immutableBitmap.toBytes());
    }

    @BenchmarkOptions(warmupRounds = 10, benchmarkRounds = 1000)
    @Test
    public void testFromImmutableByteArray() {
        ImmutableBitmap immutableBitmap = factory.mapImmutableBitmap(baseByteBuffer);
        Assert.assertEquals(BitmapCreationBenchmark.numBits, immutableBitmap.size());
    }
}

