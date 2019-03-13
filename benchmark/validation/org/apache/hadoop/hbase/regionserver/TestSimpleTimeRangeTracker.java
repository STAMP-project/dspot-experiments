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
package org.apache.hadoop.hbase.regionserver;


import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.io.ByteArrayOutputStream;
import org.apache.hadoop.hbase.io.TimeRange;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static TimeRangeTracker.INITIAL_MIN_TIMESTAMP;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestSimpleTimeRangeTracker {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSimpleTimeRangeTracker.class);

    @Test
    public void testExtreme() {
        TimeRange tr = TimeRange.allTime();
        Assert.assertTrue(tr.includesTimeRange(TimeRange.allTime()));
        TimeRangeTracker trt = getTimeRangeTracker();
        Assert.assertFalse(trt.includesTimeRange(TimeRange.allTime()));
        trt.includeTimestamp(1);
        trt.includeTimestamp(10);
        Assert.assertTrue(trt.includesTimeRange(TimeRange.allTime()));
    }

    @Test
    public void testTimeRangeInitialized() {
        TimeRangeTracker src = getTimeRangeTracker();
        TimeRange tr = new TimeRange(System.currentTimeMillis());
        Assert.assertFalse(src.includesTimeRange(tr));
    }

    @Test
    public void testTimeRangeTrackerNullIsSameAsTimeRangeNull() throws IOException {
        TimeRangeTracker src = getTimeRangeTracker(1, 2);
        byte[] bytes = TimeRangeTracker.toByteArray(src);
        TimeRange tgt = TimeRangeTracker.parseFrom(bytes).toTimeRange();
        Assert.assertEquals(src.getMin(), tgt.getMin());
        Assert.assertEquals(src.getMax(), tgt.getMax());
    }

    @Test
    public void testSerialization() throws IOException {
        TimeRangeTracker src = getTimeRangeTracker(1, 2);
        TimeRangeTracker tgt = TimeRangeTracker.parseFrom(TimeRangeTracker.toByteArray(src));
        Assert.assertEquals(src.getMin(), tgt.getMin());
        Assert.assertEquals(src.getMax(), tgt.getMax());
    }

    @Test
    public void testLegacySerialization() throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(data);
        output.writeLong(100);
        output.writeLong(200);
        TimeRangeTracker tgt = TimeRangeTracker.parseFrom(data.toByteArray());
        Assert.assertEquals(100, tgt.getMin());
        Assert.assertEquals(200, tgt.getMax());
    }

    @Test
    public void testAlwaysDecrementingSetsMaximum() {
        TimeRangeTracker trr = getTimeRangeTracker();
        trr.includeTimestamp(3);
        trr.includeTimestamp(2);
        trr.includeTimestamp(1);
        Assert.assertTrue(((trr.getMin()) != (INITIAL_MIN_TIMESTAMP)));
        /* The initial max value */
        Assert.assertTrue(((trr.getMax()) != (-1)));
    }

    @Test
    public void testSimpleInRange() {
        TimeRangeTracker trr = getTimeRangeTracker();
        trr.includeTimestamp(0);
        trr.includeTimestamp(2);
        Assert.assertTrue(trr.includesTimeRange(new TimeRange(1)));
    }

    @Test
    public void testRangeConstruction() throws IOException {
        TimeRange defaultRange = TimeRange.allTime();
        Assert.assertEquals(0L, defaultRange.getMin());
        Assert.assertEquals(Long.MAX_VALUE, defaultRange.getMax());
        Assert.assertTrue(defaultRange.isAllTime());
        TimeRange oneArgRange = new TimeRange(0L);
        Assert.assertEquals(0L, oneArgRange.getMin());
        Assert.assertEquals(Long.MAX_VALUE, oneArgRange.getMax());
        Assert.assertTrue(oneArgRange.isAllTime());
        TimeRange oneArgRange2 = new TimeRange(1);
        Assert.assertEquals(1, oneArgRange2.getMin());
        Assert.assertEquals(Long.MAX_VALUE, oneArgRange2.getMax());
        Assert.assertFalse(oneArgRange2.isAllTime());
        TimeRange twoArgRange = new TimeRange(0L, Long.MAX_VALUE);
        Assert.assertEquals(0L, twoArgRange.getMin());
        Assert.assertEquals(Long.MAX_VALUE, twoArgRange.getMax());
        Assert.assertTrue(twoArgRange.isAllTime());
        TimeRange twoArgRange2 = new TimeRange(0L, ((Long.MAX_VALUE) - 1));
        Assert.assertEquals(0L, twoArgRange2.getMin());
        Assert.assertEquals(((Long.MAX_VALUE) - 1), twoArgRange2.getMax());
        Assert.assertFalse(twoArgRange2.isAllTime());
        TimeRange twoArgRange3 = new TimeRange(1, Long.MAX_VALUE);
        Assert.assertEquals(1, twoArgRange3.getMin());
        Assert.assertEquals(Long.MAX_VALUE, twoArgRange3.getMax());
        Assert.assertFalse(twoArgRange3.isAllTime());
    }
}

