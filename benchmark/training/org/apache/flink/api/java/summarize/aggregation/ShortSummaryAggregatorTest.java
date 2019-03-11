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
package org.apache.flink.api.java.summarize.aggregation;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ShortSummaryAggregator}.
 */
public class ShortSummaryAggregatorTest {
    @Test
    public void testIsNan() throws Exception {
        ShortSummaryAggregator ag = new ShortSummaryAggregator();
        // always false for Short
        Assert.assertFalse(ag.isNan(((short) (-1))));
        Assert.assertFalse(ag.isNan(((short) (0))));
        Assert.assertFalse(ag.isNan(((short) (23))));
        Assert.assertFalse(ag.isNan(Short.MAX_VALUE));
        Assert.assertFalse(ag.isNan(Short.MIN_VALUE));
        Assert.assertFalse(ag.isNan(null));
    }

    @Test
    public void testIsInfinite() throws Exception {
        ShortSummaryAggregator ag = new ShortSummaryAggregator();
        // always false for Short
        Assert.assertFalse(ag.isInfinite(((short) (-1))));
        Assert.assertFalse(ag.isInfinite(((short) (0))));
        Assert.assertFalse(ag.isInfinite(((short) (23))));
        Assert.assertFalse(ag.isInfinite(Short.MAX_VALUE));
        Assert.assertFalse(ag.isInfinite(Short.MIN_VALUE));
        Assert.assertFalse(ag.isInfinite(null));
    }

    @Test
    public void testMean() throws Exception {
        Assert.assertEquals(50.0, summarize(0, 100).getMean(), 0.0);
        Assert.assertEquals(33.333333, summarize(0, 0, 100).getMean(), 1.0E-5);
        Assert.assertEquals(50.0, summarize(0, 0, 100, 100).getMean(), 0.0);
        Assert.assertEquals(50.0, summarize(0, 100, null).getMean(), 0.0);
        Assert.assertNull(summarize().getMean());
    }

    @Test
    public void testSum() throws Exception {
        Assert.assertEquals(100, summarize(0, 100).getSum().shortValue());
        Assert.assertEquals(15, summarize(1, 2, 3, 4, 5).getSum().shortValue());
        Assert.assertEquals(0, summarize((-100), 0, 100, null).getSum().shortValue());
        Assert.assertEquals(90, summarize((-10), 100, null).getSum().shortValue());
        Assert.assertNull(summarize().getSum());
    }

    @Test
    public void testMax() throws Exception {
        Assert.assertEquals(1001, summarize((-1000), 0, 1, 50, 999, 1001).getMax().shortValue());
        Assert.assertEquals(0, summarize(((int) (Short.MIN_VALUE)), (-1000), 0).getMax().shortValue());
        Assert.assertEquals(11, summarize(1, 8, 7, 6, 9, 10, 2, 3, 5, 0, 11, (-2), 3).getMax().shortValue());
        Assert.assertEquals(11, summarize(1, 8, 7, 6, 9, null, 10, 2, 3, 5, null, 0, 11, (-2), 3).getMax().shortValue());
        Assert.assertNull(summarize().getMax());
    }

    @Test
    public void testMin() throws Exception {
        Assert.assertEquals((-1000), summarize((-1000), 0, 1, 50, 999, 1001).getMin().shortValue());
        Assert.assertEquals(Short.MIN_VALUE, summarize(((int) (Short.MIN_VALUE)), (-1000), 0).getMin().shortValue());
        Assert.assertEquals((-2), summarize(1, 8, 7, 6, 9, 10, 2, 3, 5, 0, 11, (-2), 3).getMin().shortValue());
        Assert.assertEquals((-2), summarize(1, 8, 7, 6, 9, null, 10, 2, 3, 5, null, 0, 11, (-2), 3).getMin().shortValue());
        Assert.assertNull(summarize().getMin());
    }
}

