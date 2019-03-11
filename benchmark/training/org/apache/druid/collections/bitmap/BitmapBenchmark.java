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
package org.apache.druid.collections.bitmap;


import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;
import java.util.Arrays;
import java.util.Random;
import org.apache.druid.extendedset.intset.ImmutableConciseSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.roaringbitmap.buffer.BufferFastAggregation;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;


/**
 * TODO rewrite this benchmark to JMH
 */
@BenchmarkOptions(clock = Clock.NANO_TIME, benchmarkRounds = 50)
public class BitmapBenchmark {
    public static final int LENGTH = 500000;

    public static final int SIZE = 10000;

    static final ImmutableConciseSet[] concise = new ImmutableConciseSet[BitmapBenchmark.SIZE];

    static final ImmutableConciseSet[] offheapConcise = new ImmutableConciseSet[BitmapBenchmark.SIZE];

    static final ImmutableRoaringBitmap[] roaring = new ImmutableRoaringBitmap[BitmapBenchmark.SIZE];

    static final ImmutableRoaringBitmap[] immutableRoaring = new ImmutableRoaringBitmap[BitmapBenchmark.SIZE];

    static final ImmutableRoaringBitmap[] offheapRoaring = new ImmutableRoaringBitmap[BitmapBenchmark.SIZE];

    static final ImmutableBitmap[] genericConcise = new ImmutableBitmap[BitmapBenchmark.SIZE];

    static final ImmutableBitmap[] genericRoaring = new ImmutableBitmap[BitmapBenchmark.SIZE];

    static final ConciseBitmapFactory conciseFactory = new ConciseBitmapFactory();

    static final RoaringBitmapFactory roaringFactory = new RoaringBitmapFactory();

    static Random rand = new Random(0);

    static long totalConciseBytes = 0;

    static long totalRoaringBytes = 0;

    static long conciseCount = 0;

    static long roaringCount = 0;

    static long unionCount = 0;

    static long minIntersection = 0;

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @Test
    @BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 2)
    public void timeConciseUnion() {
        ImmutableConciseSet union = ImmutableConciseSet.union(BitmapBenchmark.concise);
        Assert.assertEquals(BitmapBenchmark.unionCount, union.size());
    }

    @Test
    @BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 2)
    public void timeOffheapConciseUnion() {
        ImmutableConciseSet union = ImmutableConciseSet.union(BitmapBenchmark.offheapConcise);
        Assert.assertEquals(BitmapBenchmark.unionCount, union.size());
    }

    @Test
    @BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 2)
    public void timeGenericConciseUnion() {
        ImmutableBitmap union = BitmapBenchmark.conciseFactory.union(Arrays.asList(BitmapBenchmark.genericConcise));
        Assert.assertEquals(BitmapBenchmark.unionCount, union.size());
    }

    @Test
    @BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 5)
    public void timeGenericConciseIntersection() {
        ImmutableBitmap intersection = BitmapBenchmark.conciseFactory.intersection(Arrays.asList(BitmapBenchmark.genericConcise));
        Assert.assertTrue(((intersection.size()) >= (BitmapBenchmark.minIntersection)));
    }

    @Test
    public void timeRoaringUnion() {
        ImmutableRoaringBitmap union = BufferFastAggregation.horizontal_or(Arrays.asList(BitmapBenchmark.roaring).iterator());
        Assert.assertEquals(BitmapBenchmark.unionCount, union.getCardinality());
    }

    @Test
    public void timeImmutableRoaringUnion() {
        ImmutableRoaringBitmap union = BufferFastAggregation.horizontal_or(Arrays.asList(BitmapBenchmark.immutableRoaring).iterator());
        Assert.assertEquals(BitmapBenchmark.unionCount, union.getCardinality());
    }

    @Test
    public void timeOffheapRoaringUnion() {
        ImmutableRoaringBitmap union = BufferFastAggregation.horizontal_or(Arrays.asList(BitmapBenchmark.offheapRoaring).iterator());
        Assert.assertEquals(BitmapBenchmark.unionCount, union.getCardinality());
    }

    @Test
    public void timeGenericRoaringUnion() {
        ImmutableBitmap union = BitmapBenchmark.roaringFactory.union(Arrays.asList(BitmapBenchmark.genericRoaring));
        Assert.assertEquals(BitmapBenchmark.unionCount, union.size());
    }

    @Test
    public void timeGenericRoaringIntersection() {
        ImmutableBitmap intersection = BitmapBenchmark.roaringFactory.intersection(Arrays.asList(BitmapBenchmark.genericRoaring));
        Assert.assertTrue(((intersection.size()) >= (BitmapBenchmark.minIntersection)));
    }
}

