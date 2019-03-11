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
package org.apache.druid.server.coordinator.cost;


import SegmentsCostCache.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.timeline.DataSegment;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class SegmentsCostCacheTest {
    private static final String DATA_SOURCE = "dataSource";

    private static final DateTime REFERENCE_TIME = DateTimes.of("2014-01-01T00:00:00");

    private static final double EPSILON = 1.0E-8;

    @Test
    public void segmentCacheTest() {
        SegmentsCostCache.Builder cacheBuilder = SegmentsCostCache.builder();
        cacheBuilder.addSegment(SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, 0), 100));
        SegmentsCostCache cache = cacheBuilder.build();
        Assert.assertEquals(7.8735899489011E-4, cache.cost(SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, (-2)), 100)), SegmentsCostCacheTest.EPSILON);
    }

    @Test
    public void notInCalculationIntervalCostTest() {
        SegmentsCostCache.Builder cacheBuilder = SegmentsCostCache.builder();
        cacheBuilder.addSegment(SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, 0), 100));
        SegmentsCostCache cache = cacheBuilder.build();
        Assert.assertEquals(0, cache.cost(SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, ((int) (TimeUnit.DAYS.toHours(50)))), 100)), SegmentsCostCacheTest.EPSILON);
    }

    @Test
    public void twoSegmentsCostTest() {
        DataSegment segmentA = SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, 0), 100);
        DataSegment segmentB = SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, (-2)), 100);
        SegmentsCostCache.Bucket.Builder prototype = SegmentsCostCache.Bucket.builder(new org.joda.time.Interval(SegmentsCostCacheTest.REFERENCE_TIME.minusHours(5), SegmentsCostCacheTest.REFERENCE_TIME.plusHours(5)));
        prototype.addSegment(segmentA);
        SegmentsCostCache.Bucket bucket = prototype.build();
        double segmentCost = bucket.cost(segmentB);
        Assert.assertEquals(7.8735899489011E-4, segmentCost, SegmentsCostCacheTest.EPSILON);
    }

    @Test
    public void calculationIntervalTest() {
        DataSegment segmentA = SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, 0), 100);
        DataSegment segmentB = SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, ((int) (TimeUnit.DAYS.toHours(50)))), 100);
        SegmentsCostCache.Bucket.Builder prototype = SegmentsCostCache.Bucket.builder(new org.joda.time.Interval(SegmentsCostCacheTest.REFERENCE_TIME.minusHours(5), SegmentsCostCacheTest.REFERENCE_TIME.plusHours(5)));
        prototype.addSegment(segmentA);
        SegmentsCostCache.Bucket bucket = prototype.build();
        Assert.assertTrue(bucket.inCalculationInterval(segmentA));
        Assert.assertFalse(bucket.inCalculationInterval(segmentB));
    }

    @Test
    public void sameSegmentCostTest() {
        DataSegment segmentA = SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, 0), 100);
        DataSegment segmentB = SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, 0), 100);
        SegmentsCostCache.Bucket.Builder prototype = SegmentsCostCache.Bucket.builder(new org.joda.time.Interval(SegmentsCostCacheTest.REFERENCE_TIME.minusHours(5), SegmentsCostCacheTest.REFERENCE_TIME.plusHours(5)));
        prototype.addSegment(segmentA);
        SegmentsCostCache.Bucket bucket = prototype.build();
        double segmentCost = bucket.cost(segmentB);
        Assert.assertEquals(8.26147353873985E-4, segmentCost, SegmentsCostCacheTest.EPSILON);
    }

    @Test
    public void multipleSegmentsCostTest() {
        DataSegment segmentA = SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, (-2)), 100);
        DataSegment segmentB = SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, 0), 100);
        DataSegment segmentC = SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, 2), 100);
        SegmentsCostCache.Bucket.Builder prototype = SegmentsCostCache.Bucket.builder(new org.joda.time.Interval(SegmentsCostCacheTest.REFERENCE_TIME.minusHours(5), SegmentsCostCacheTest.REFERENCE_TIME.plusHours(5)));
        prototype.addSegment(segmentA);
        prototype.addSegment(segmentC);
        SegmentsCostCache.Bucket bucket = prototype.build();
        double segmentCost = bucket.cost(segmentB);
        Assert.assertEquals(0.001574717989780039, segmentCost, SegmentsCostCacheTest.EPSILON);
    }

    @Test
    public void randomSegmentsCostTest() {
        List<DataSegment> dataSegments = new ArrayList<>(1000);
        Random random = new Random(1);
        for (int i = 0; i < 1000; ++i) {
            dataSegments.add(SegmentsCostCacheTest.createSegment(SegmentsCostCacheTest.DATA_SOURCE, SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, random.nextInt(20)), 100));
        }
        DataSegment referenceSegment = SegmentsCostCacheTest.createSegment("ANOTHER_DATA_SOURCE", SegmentsCostCacheTest.shifted1HInterval(SegmentsCostCacheTest.REFERENCE_TIME, 5), 100);
        SegmentsCostCache.Bucket.Builder prototype = SegmentsCostCache.Bucket.builder(new org.joda.time.Interval(SegmentsCostCacheTest.REFERENCE_TIME.minusHours(1), SegmentsCostCacheTest.REFERENCE_TIME.plusHours(25)));
        dataSegments.forEach(prototype::addSegment);
        SegmentsCostCache.Bucket bucket = prototype.build();
        double cost = bucket.cost(referenceSegment);
        Assert.assertEquals(0.7065117101966677, cost, SegmentsCostCacheTest.EPSILON);
    }
}

