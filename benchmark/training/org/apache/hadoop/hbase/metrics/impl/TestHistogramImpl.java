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
package org.apache.hadoop.hbase.metrics.impl;


import java.util.stream.IntStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.metrics.Snapshot;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test case for {@link HistogramImpl}
 */
@Category(SmallTests.class)
public class TestHistogramImpl {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHistogramImpl.class);

    @Test
    public void testUpdate() {
        HistogramImpl histogram = new HistogramImpl();
        Assert.assertEquals(0, histogram.getCount());
        histogram.update(0);
        Assert.assertEquals(1, histogram.getCount());
        histogram.update(10);
        Assert.assertEquals(2, histogram.getCount());
        histogram.update(20);
        histogram.update(30);
        Assert.assertEquals(4, histogram.getCount());
    }

    @Test
    public void testSnapshot() {
        HistogramImpl histogram = new HistogramImpl();
        IntStream.range(0, 100).forEach(histogram::update);
        Snapshot snapshot = histogram.snapshot();
        Assert.assertEquals(100, snapshot.getCount());
        Assert.assertEquals(50, snapshot.getMedian());
        Assert.assertEquals(49, snapshot.getMean());
        Assert.assertEquals(0, snapshot.getMin());
        Assert.assertEquals(99, snapshot.getMax());
        Assert.assertEquals(25, snapshot.get25thPercentile());
        Assert.assertEquals(75, snapshot.get75thPercentile());
        Assert.assertEquals(90, snapshot.get90thPercentile());
        Assert.assertEquals(95, snapshot.get95thPercentile());
        Assert.assertEquals(98, snapshot.get98thPercentile());
        Assert.assertEquals(99, snapshot.get99thPercentile());
        Assert.assertEquals(99, snapshot.get999thPercentile());
        Assert.assertEquals(51, snapshot.getCountAtOrBelow(50));
        // check that histogram is reset.
        Assert.assertEquals(100, histogram.getCount());// count does not reset

        // put more data after reset
        IntStream.range(100, 200).forEach(histogram::update);
        Assert.assertEquals(200, histogram.getCount());
        snapshot = histogram.snapshot();
        Assert.assertEquals(100, snapshot.getCount());// only 100 more events

        Assert.assertEquals(150, snapshot.getMedian());
        Assert.assertEquals(149, snapshot.getMean());
        Assert.assertEquals(100, snapshot.getMin());
        Assert.assertEquals(199, snapshot.getMax());
        Assert.assertEquals(125, snapshot.get25thPercentile());
        Assert.assertEquals(175, snapshot.get75thPercentile());
        Assert.assertEquals(190, snapshot.get90thPercentile());
        Assert.assertEquals(195, snapshot.get95thPercentile());
        Assert.assertEquals(198, snapshot.get98thPercentile());
        Assert.assertEquals(199, snapshot.get99thPercentile());
        Assert.assertEquals(199, snapshot.get999thPercentile());
    }
}

