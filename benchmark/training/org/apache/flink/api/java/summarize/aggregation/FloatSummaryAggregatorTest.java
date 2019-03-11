/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0f (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0f
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.summarize.aggregation;


import org.apache.flink.api.java.summarize.NumericColumnSummary;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link FloatSummaryAggregator}.
 */
public class FloatSummaryAggregatorTest {
    /**
     * Use some values from Anscombe's Quartet for testing.
     *
     * <p>There was no particular reason to use these except they have known means and variance.
     *
     * <p>https://en.wikipedia.org/wiki/Anscombe%27s_quartet
     */
    @Test
    public void testAnscomesQuartetXValues() throws Exception {
        final Float[] q1x = new Float[]{ 10.0F, 8.0F, 13.0F, 9.0F, 11.0F, 14.0F, 6.0F, 4.0F, 12.0F, 7.0F, 5.0F };
        final Float[] q4x = new Float[]{ 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 19.0F, 8.0F, 8.0F, 8.0F };
        NumericColumnSummary<Float> q1 = summarize(q1x);
        NumericColumnSummary<Float> q4 = summarize(q4x);
        Assert.assertEquals(9.0, q1.getMean().doubleValue(), 0.0F);
        Assert.assertEquals(9.0, q4.getMean().doubleValue(), 0.0F);
        Assert.assertEquals(11.0, q1.getVariance().doubleValue(), 1.0E-10);
        Assert.assertEquals(11.0, q4.getVariance().doubleValue(), 1.0E-10);
        double stddev = Math.sqrt(11.0F);
        Assert.assertEquals(stddev, q1.getStandardDeviation().doubleValue(), 1.0E-10);
        Assert.assertEquals(stddev, q4.getStandardDeviation().doubleValue(), 1.0E-10);
    }

    /**
     * Use some values from Anscombe's Quartet for testing.
     *
     * <p>There was no particular reason to use these except they have known means and variance.
     *
     * <p>https://en.wikipedia.org/wiki/Anscombe%27s_quartet
     */
    @Test
    public void testAnscomesQuartetYValues() throws Exception {
        final Float[] q1y = new Float[]{ 8.04F, 6.95F, 7.58F, 8.81F, 8.33F, 9.96F, 7.24F, 4.26F, 10.84F, 4.82F, 5.68F };
        final Float[] q2y = new Float[]{ 9.14F, 8.14F, 8.74F, 8.77F, 9.26F, 8.1F, 6.13F, 3.1F, 9.13F, 7.26F, 4.74F };
        final Float[] q3y = new Float[]{ 7.46F, 6.77F, 12.74F, 7.11F, 7.81F, 8.84F, 6.08F, 5.39F, 8.15F, 6.42F, 5.73F };
        final Float[] q4y = new Float[]{ 6.58F, 5.76F, 7.71F, 8.84F, 8.47F, 7.04F, 5.25F, 12.5F, 5.56F, 7.91F, 6.89F };
        NumericColumnSummary<Float> q1 = summarize(q1y);
        NumericColumnSummary<Float> q2 = summarize(q2y);
        NumericColumnSummary<Float> q3 = summarize(q3y);
        NumericColumnSummary<Float> q4 = summarize(q4y);
        // the y values are have less precisely matching means and variances
        Assert.assertEquals(7.5, q1.getMean().doubleValue(), 0.001);
        Assert.assertEquals(7.5, q2.getMean().doubleValue(), 0.001);
        Assert.assertEquals(7.5, q3.getMean().doubleValue(), 0.001);
        Assert.assertEquals(7.5, q4.getMean().doubleValue(), 0.001);
        Assert.assertEquals(4.12, q1.getVariance().doubleValue(), 0.01);
        Assert.assertEquals(4.12, q2.getVariance().doubleValue(), 0.01);
        Assert.assertEquals(4.12, q3.getVariance().doubleValue(), 0.01);
        Assert.assertEquals(4.12, q4.getVariance().doubleValue(), 0.01);
    }

    @Test
    public void testIsNan() throws Exception {
        FloatSummaryAggregator ag = new FloatSummaryAggregator();
        Assert.assertFalse(ag.isNan((-1.0F)));
        Assert.assertFalse(ag.isNan(0.0F));
        Assert.assertFalse(ag.isNan(23.0F));
        Assert.assertFalse(ag.isNan(Float.MAX_VALUE));
        Assert.assertFalse(ag.isNan(Float.MIN_VALUE));
        Assert.assertTrue(ag.isNan(Float.NaN));
    }

    @Test
    public void testIsInfinite() throws Exception {
        FloatSummaryAggregator ag = new FloatSummaryAggregator();
        Assert.assertFalse(ag.isInfinite((-1.0F)));
        Assert.assertFalse(ag.isInfinite(0.0F));
        Assert.assertFalse(ag.isInfinite(23.0F));
        Assert.assertFalse(ag.isInfinite(Float.MAX_VALUE));
        Assert.assertFalse(ag.isInfinite(Float.MIN_VALUE));
        Assert.assertTrue(ag.isInfinite(Float.POSITIVE_INFINITY));
        Assert.assertTrue(ag.isInfinite(Float.NEGATIVE_INFINITY));
    }

    @Test
    public void testMean() throws Exception {
        Assert.assertEquals(50.0, summarize(0.0F, 100.0F).getMean(), 0.0);
        Assert.assertEquals(33.333333, summarize(0.0F, 0.0F, 100.0F).getMean(), 1.0E-5);
        Assert.assertEquals(50.0, summarize(0.0F, 0.0F, 100.0F, 100.0F).getMean(), 0.0);
        Assert.assertEquals(50.0, summarize(0.0F, 100.0F, null).getMean(), 0.0);
        Assert.assertNull(summarize().getMean());
    }

    @Test
    public void testSum() throws Exception {
        Assert.assertEquals(100.0, summarize(0.0F, 100.0F).getSum().floatValue(), 0.0F);
        Assert.assertEquals(15, summarize(1.0F, 2.0F, 3.0F, 4.0F, 5.0F).getSum().floatValue(), 0.0F);
        Assert.assertEquals(0, summarize((-100.0F), 0.0F, 100.0F, null).getSum().floatValue(), 0.0F);
        Assert.assertEquals(90, summarize((-10.0F), 100.0F, null).getSum().floatValue(), 0.0F);
        Assert.assertNull(summarize().getSum());
    }

    @Test
    public void testMax() throws Exception {
        Assert.assertEquals(1001.0F, summarize((-1000.0F), 0.0F, 1.0F, 50.0F, 999.0F, 1001.0F).getMax().floatValue(), 0.0F);
        Assert.assertEquals(11.0F, summarize(1.0F, 8.0F, 7.0F, 6.0F, 9.0F, 10.0F, 2.0F, 3.0F, 5.0F, 0.0F, 11.0F, (-2.0F), 3.0F).getMax().floatValue(), 0.0F);
        Assert.assertEquals(11.0F, summarize(1.0F, 8.0F, 7.0F, 6.0F, 9.0F, null, 10.0F, 2.0F, 3.0F, 5.0F, null, 0.0F, 11.0F, (-2.0F), 3.0F).getMax().floatValue(), 0.0F);
        Assert.assertEquals((-2.0F), summarize((-8.0F), (-7.0F), (-6.0F), (-9.0F), null, (-10.0F), null, (-2.0F)).getMax().floatValue(), 0.0F);
        Assert.assertNull(summarize().getMax());
    }

    @Test
    public void testMin() throws Exception {
        Assert.assertEquals((-1000), summarize((-1000.0F), 0.0F, 1.0F, 50.0F, 999.0F, 1001.0F).getMin().floatValue(), 0.0F);
        Assert.assertEquals((-2.0F), summarize(1.0F, 8.0F, 7.0F, 6.0F, 9.0F, 10.0F, 2.0F, 3.0F, 5.0F, 0.0F, 11.0F, (-2.0F), 3.0F).getMin().floatValue(), 0.0F);
        Assert.assertEquals((-2.0F), summarize(1.0F, 8.0F, 7.0F, 6.0F, 9.0F, null, 10.0F, 2.0F, 3.0F, 5.0F, null, 0.0F, 11.0F, (-2.0F), 3.0F).getMin().floatValue(), 0.0F);
        Assert.assertNull(summarize().getMin());
    }
}

