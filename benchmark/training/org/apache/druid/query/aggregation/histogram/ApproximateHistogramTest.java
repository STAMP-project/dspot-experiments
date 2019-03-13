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
package org.apache.druid.query.aggregation.histogram;


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


public class ApproximateHistogramTest {
    static final float[] VALUES = new float[]{ 23, 19, 10, 16, 36, 2, 9, 32, 30, 45 };

    static final float[] VALUES2 = new float[]{ 23, 19, 10, 16, 36, 2, 1, 9, 32, 30, 45, 46 };

    static final float[] VALUES3 = new float[]{ 20, 16, 19, 27, 17, 20, 18, 20, 28, 14, 17, 21, 20, 21, 10, 25, 23, 17, 21, 18, 14, 20, 18, 12, 19, 20, 23, 25, 15, 22, 14, 17, 15, 23, 23, 15, 27, 20, 17, 15 };

    static final float[] VALUES4 = new float[]{ 27.489F, 3.085F, 3.722F, 66.875F, 30.998F, -8.193F, 5.395F, 5.109F, 10.944F, 54.75F, 14.092F, 15.604F, 52.856F, 66.034F, 22.004F, -14.682F, -50.985F, 2.872F, 61.013F, -21.766F, 19.172F, 62.882F, 33.537F, 21.081F, 67.115F, 44.789F, 64.1F, 20.911F, -6.553F, 2.178F };

    static final float[] VALUES5 = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    static final float[] VALUES6 = new float[]{ 1.0F, 1.5F, 2.0F, 2.5F, 3.0F, 3.5F, 4.0F, 4.5F, 5.0F, 5.5F, 6.0F, 6.5F, 7.0F, 7.5F, 8.0F, 8.5F, 9.0F, 9.5F, 10.0F };

    // Based on the example from https://metamarkets.com/2013/histograms/
    // This dataset can make getQuantiles() return values exceeding max
    // for example: q=0.95 returns 25.16 when max=25
    static final float[] VALUES7 = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 12, 12, 15, 20, 25, 25, 25 };

    @Test
    public void testOffer() {
        ApproximateHistogram h = buildHistogram(5, ApproximateHistogramTest.VALUES);
        // (2, 1), (9.5, 2), (19.33, 3), (32.67, 3), (45, 1)
        Assert.assertArrayEquals("final bin positions match expected positions", new float[]{ 2, 9.5F, 19.33F, 32.67F, 45.0F }, h.positions(), 0.1F);
        Assert.assertArrayEquals("final bin positions match expected positions", new long[]{ 1, 2, 3, 3, 1 }, h.bins());
        Assert.assertEquals("min value matches expexted min", 2, h.min(), 0);
        Assert.assertEquals("max value matches expexted max", 45, h.max(), 0);
        Assert.assertEquals("bin count matches expected bin count", 5, h.binCount());
    }

    @Test
    public void testFold() {
        ApproximateHistogram merged = new ApproximateHistogram(0);
        ApproximateHistogram mergedFast = new ApproximateHistogram(0);
        ApproximateHistogram h1 = new ApproximateHistogram(5);
        ApproximateHistogram h2 = new ApproximateHistogram(10);
        for (int i = 0; i < 5; ++i) {
            h1.offer(ApproximateHistogramTest.VALUES[i]);
        }
        for (int i = 5; i < (ApproximateHistogramTest.VALUES.length); ++i) {
            h2.offer(ApproximateHistogramTest.VALUES[i]);
        }
        merged.fold(h1);
        merged.fold(h2);
        mergedFast.foldFast(h1);
        mergedFast.foldFast(h2);
        Assert.assertArrayEquals("final bin positions match expected positions", new float[]{ 2, 9.5F, 19.33F, 32.67F, 45.0F }, merged.positions(), 0.1F);
        Assert.assertArrayEquals("final bin positions match expected positions", new float[]{ 11.2F, 30.25F, 45.0F }, mergedFast.positions(), 0.1F);
        Assert.assertArrayEquals("final bin counts match expected counts", new long[]{ 1, 2, 3, 3, 1 }, merged.bins());
        Assert.assertArrayEquals("final bin counts match expected counts", new long[]{ 5, 4, 1 }, mergedFast.bins());
        Assert.assertEquals("merged max matches expected value", 45.0F, merged.max(), 0.1F);
        Assert.assertEquals("mergedfast max matches expected value", 45.0F, mergedFast.max(), 0.1F);
        Assert.assertEquals("merged min matches expected value", 2.0F, merged.min(), 0.1F);
        Assert.assertEquals("mergedfast min matches expected value", 2.0F, mergedFast.min(), 0.1F);
        // fold where merged bincount is less than total bincount
        ApproximateHistogram a = buildHistogram(10, new float[]{ 1, 2, 3, 4, 5, 6 });
        ApproximateHistogram aFast = buildHistogram(10, new float[]{ 1, 2, 3, 4, 5, 6 });
        ApproximateHistogram b = buildHistogram(5, new float[]{ 3, 4, 5, 6 });
        a.fold(b);
        aFast.foldFast(b);
        Assert.assertEquals(new ApproximateHistogram(6, new float[]{ 1, 2, 3, 4, 5, 6, 0, 0, 0, 0 }, new long[]{ 1, 1, 2, 2, 2, 2, 0, 0, 0, 0 }, 1, 6), a);
        Assert.assertEquals(new ApproximateHistogram(6, new float[]{ 1, 2, 3, 4, 5, 6, 0, 0, 0, 0 }, new long[]{ 1, 1, 2, 2, 2, 2, 0, 0, 0, 0 }, 1, 6), aFast);
        ApproximateHistogram h3 = new ApproximateHistogram(10);
        ApproximateHistogram h4 = new ApproximateHistogram(10);
        for (float v : ApproximateHistogramTest.VALUES3) {
            h3.offer(v);
        }
        for (float v : ApproximateHistogramTest.VALUES4) {
            h4.offer(v);
        }
        h3.fold(h4);
        Assert.assertArrayEquals("final bin positions match expected positions", new float[]{ -50.98F, -21.77F, -9.81F, 3.73F, 13.72F, 20.1F, 29.0F, 44.79F, 53.8F, 64.67F }, h3.positions(), 0.1F);
        Assert.assertArrayEquals("final bin counts match expected counts", new long[]{ 1, 1, 3, 6, 12, 32, 6, 1, 2, 6 }, h3.bins());
    }

    @Test
    public void testFoldNothing() {
        ApproximateHistogram h1 = new ApproximateHistogram(10);
        ApproximateHistogram h2 = new ApproximateHistogram(10);
        h1.fold(h2);
        h1.foldFast(h2);
    }

    @Test
    public void testFoldNothing2() {
        ApproximateHistogram h1 = new ApproximateHistogram(10);
        ApproximateHistogram h1Fast = new ApproximateHistogram(10);
        ApproximateHistogram h2 = new ApproximateHistogram(10);
        ApproximateHistogram h3 = new ApproximateHistogram(10);
        ApproximateHistogram h4 = new ApproximateHistogram(10);
        ApproximateHistogram h4Fast = new ApproximateHistogram(10);
        for (float v : ApproximateHistogramTest.VALUES3) {
            h3.offer(v);
            h4.offer(v);
            h4Fast.offer(v);
        }
        h1.fold(h3);
        h4.fold(h2);
        h1Fast.foldFast(h3);
        h4Fast.foldFast(h2);
        Assert.assertEquals(h3, h1);
        Assert.assertEquals(h4, h3);
        Assert.assertEquals(h3, h1Fast);
        Assert.assertEquals(h3, h4Fast);
    }

    @Test
    public void testSum() {
        ApproximateHistogram h = buildHistogram(5, ApproximateHistogramTest.VALUES);
        Assert.assertEquals(0.0F, h.sum(0), 0.01);
        Assert.assertEquals(1.0F, h.sum(2), 0.01);
        Assert.assertEquals(1.16F, h.sum(5), 0.01);
        Assert.assertEquals(3.28F, h.sum(15), 0.01);
        Assert.assertEquals(ApproximateHistogramTest.VALUES.length, h.sum(45), 0.01);
        Assert.assertEquals(ApproximateHistogramTest.VALUES.length, h.sum(46), 0.01);
        ApproximateHistogram h2 = buildHistogram(5, ApproximateHistogramTest.VALUES2);
        Assert.assertEquals(0.0F, h2.sum(0), 0.01);
        Assert.assertEquals(0.0F, h2.sum(1.0F), 0.01);
        Assert.assertEquals(1.0F, h2.sum(1.5F), 0.01);
        Assert.assertEquals(1.125F, h2.sum(2.0F), 0.001);
        Assert.assertEquals(2.0625F, h2.sum(5.75F), 0.001);
        Assert.assertEquals(3.0F, h2.sum(9.5F), 0.01);
        Assert.assertEquals(11.0F, h2.sum(45.5F), 0.01);
        Assert.assertEquals(12.0F, h2.sum(46.0F), 0.01);
        Assert.assertEquals(12.0F, h2.sum(47.0F), 0.01);
    }

    @Test
    public void testSerializeCompact() {
        ApproximateHistogram h = buildHistogram(5, ApproximateHistogramTest.VALUES);
        Assert.assertEquals(h, ApproximateHistogram.fromBytes(h.toBytes()));
        ApproximateHistogram h2 = new ApproximateHistogram(50).fold(h);
        Assert.assertEquals(h2, ApproximateHistogram.fromBytes(h2.toBytes()));
    }

    @Test
    public void testSerializeDense() {
        ApproximateHistogram h = buildHistogram(5, ApproximateHistogramTest.VALUES);
        ByteBuffer buf = ByteBuffer.allocate(h.getDenseStorageSize());
        h.toBytesDense(buf);
        Assert.assertEquals(h, ApproximateHistogram.fromBytes(buf.array()));
    }

    @Test
    public void testSerializeSparse() {
        ApproximateHistogram h = buildHistogram(5, ApproximateHistogramTest.VALUES);
        ByteBuffer buf = ByteBuffer.allocate(h.getSparseStorageSize());
        h.toBytesSparse(buf);
        Assert.assertEquals(h, ApproximateHistogram.fromBytes(buf.array()));
    }

    @Test
    public void testSerializeCompactExact() {
        ApproximateHistogram h = buildHistogram(50, new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        Assert.assertEquals(h, ApproximateHistogram.fromBytes(h.toBytes()));
        h = buildHistogram(5, new float[]{ 1.0F, 2.0F, 3.0F });
        Assert.assertEquals(h, ApproximateHistogram.fromBytes(h.toBytes()));
        h = new ApproximateHistogram(40).fold(h);
        Assert.assertEquals(h, ApproximateHistogram.fromBytes(h.toBytes()));
    }

    @Test
    public void testSerializeEmpty() {
        ApproximateHistogram h = new ApproximateHistogram(50);
        Assert.assertEquals(h, ApproximateHistogram.fromBytes(h.toBytes()));
    }

    @Test
    public void testQuantileSmaller() {
        ApproximateHistogram h = buildHistogram(20, ApproximateHistogramTest.VALUES5);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 5.0F }, h.getQuantiles(new float[]{ 0.5F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 3.33F, 6.67F }, h.getQuantiles(new float[]{ 0.333F, 0.666F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 2.5F, 5.0F, 7.5F }, h.getQuantiles(new float[]{ 0.25F, 0.5F, 0.75F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 2.0F, 4.0F, 6.0F, 8.0F }, h.getQuantiles(new float[]{ 0.2F, 0.4F, 0.6F, 0.8F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F }, h.getQuantiles(new float[]{ 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F }), 0.1F);
    }

    @Test
    public void testQuantileEqualSize() {
        ApproximateHistogram h = buildHistogram(10, ApproximateHistogramTest.VALUES5);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 5.0F }, h.getQuantiles(new float[]{ 0.5F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 3.33F, 6.67F }, h.getQuantiles(new float[]{ 0.333F, 0.666F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 2.5F, 5.0F, 7.5F }, h.getQuantiles(new float[]{ 0.25F, 0.5F, 0.75F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 2.0F, 4.0F, 6.0F, 8.0F }, h.getQuantiles(new float[]{ 0.2F, 0.4F, 0.6F, 0.8F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F }, h.getQuantiles(new float[]{ 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F }), 0.1F);
    }

    @Test
    public void testQuantileBetweenMinMax() {
        ApproximateHistogram h = buildHistogram(20, ApproximateHistogramTest.VALUES7);
        Assert.assertTrue("min value incorrect", ((ApproximateHistogramTest.VALUES7[0]) == (h.min())));
        Assert.assertTrue("max value incorrect", ((ApproximateHistogramTest.VALUES7[((ApproximateHistogramTest.VALUES7.length) - 1)]) == (h.max())));
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 1.8F, 3.6F, 5.4F, 7.2F, 9.0F, 11.05F, 12.37F, 17.0F, 23.5F }, h.getQuantiles(new float[]{ 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F }), 0.1F);
        // Test for outliers (0.05f and 0.95f, which should be min <= value <= max)
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ h.min(), h.max() }, h.getQuantiles(new float[]{ 0.05F, 0.95F }), 0.1F);
    }

    @Test
    public void testQuantileBigger() {
        ApproximateHistogram h = buildHistogram(5, ApproximateHistogramTest.VALUES5);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 4.5F }, h.getQuantiles(new float[]{ 0.5F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 2.83F, 6.17F }, h.getQuantiles(new float[]{ 0.333F, 0.666F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 2.0F, 4.5F, 7.0F }, h.getQuantiles(new float[]{ 0.25F, 0.5F, 0.75F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 1.5F, 3.5F, 5.5F, 7.5F }, h.getQuantiles(new float[]{ 0.2F, 0.4F, 0.6F, 0.8F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 1.0F, 1.5F, 2.5F, 3.5F, 4.5F, 5.5F, 6.5F, 7.5F, 8.5F }, h.getQuantiles(new float[]{ 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F }), 0.1F);
    }

    @Test
    public void testQuantileBigger2() {
        float[] thousand = new float[1000];
        for (int i = 1; i <= 1000; ++i) {
            thousand[(i - 1)] = i;
        }
        ApproximateHistogram h = buildHistogram(100, thousand);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 493.5F }, h.getQuantiles(new float[]{ 0.5F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 327.5F, 662.0F }, h.getQuantiles(new float[]{ 0.333F, 0.666F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 244.5F, 493.5F, 746.0F }, h.getQuantiles(new float[]{ 0.25F, 0.5F, 0.75F }), 0.1F);
        Assert.assertArrayEquals("expected quantiles match actual quantiles", new float[]{ 96.5F, 196.53F, 294.5F, 395.5F, 493.5F, 597.0F, 696.0F, 795.0F, 895.25F }, h.getQuantiles(new float[]{ 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F }), 0.1F);
    }

    @Test
    public void testLimitSum() {
        final float lowerLimit = 0.0F;
        final float upperLimit = 10.0F;
        ApproximateHistogram h = buildHistogram(15, ApproximateHistogramTest.VALUES6, lowerLimit, upperLimit);
        for (int i = 1; i <= 20; ++i) {
            ApproximateHistogram hLow = new ApproximateHistogram(5);
            ApproximateHistogram hHigh = new ApproximateHistogram(5);
            hLow.offer((lowerLimit - i));
            hHigh.offer((upperLimit + i));
            h.foldFast(hLow);
            h.foldFast(hHigh);
        }
        Assert.assertEquals(20.0F, h.sum(lowerLimit), 0.7F);
        Assert.assertEquals(((ApproximateHistogramTest.VALUES6.length) + 20.0F), h.sum(upperLimit), 0.01);
    }

    @Test
    public void testBuckets() {
        final float[] values = new float[]{ -5.0F, 0.01F, 0.02F, 0.06F, 0.12F, 1.0F, 2.0F };
        ApproximateHistogram h = buildHistogram(50, values, 0.0F, 1.0F);
        Histogram h2 = h.toHistogram(0.05F, 0.0F);
        Assert.assertArrayEquals("expected counts match actual counts", new double[]{ 1.0F, 2.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F }, h2.getCounts(), 0.1F);
        Assert.assertArrayEquals("expected breaks match actual breaks", new double[]{ -5.05F, 0.0F, 0.05F, 0.1F, 0.15F, 0.95F, 1.0F, 2.0F }, h2.getBreaks(), 0.1F);
    }

    @Test
    public void testBuckets2() {
        final float[] values = new float[]{ -5.0F, 0.01F, 0.02F, 0.06F, 0.12F, 0.94F, 1.0F, 2.0F };
        ApproximateHistogram h = buildHistogram(50, values, 0.0F, 1.0F);
        Histogram h2 = h.toHistogram(0.05F, 0.0F);
        Assert.assertArrayEquals("expected counts match actual counts", new double[]{ 1.0F, 2.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F }, h2.getCounts(), 0.1F);
        Assert.assertArrayEquals("expected breaks match actual breaks", new double[]{ -5.05F, 0.0F, 0.05F, 0.1F, 0.15F, 0.9F, 0.95F, 1.0F, 2.05F }, h2.getBreaks(), 0.1F);
    }

    @Test
    public void testBuckets3() {
        final float[] values = new float[]{ 0.0F, 0.0F, 0.02F, 0.06F, 0.12F, 0.94F };
        ApproximateHistogram h = buildHistogram(50, values, 0.0F, 1.0F);
        Histogram h2 = h.toHistogram(1.0F, 0.0F);
        Assert.assertArrayEquals("expected counts match actual counts", new double[]{ 2.0F, 4.0F }, h2.getCounts(), 0.1F);
        Assert.assertArrayEquals("expected breaks match actual breaks", new double[]{ -1.0F, 0.0F, 1.0F }, h2.getBreaks(), 0.1F);
    }

    @Test
    public void testBuckets4() {
        final float[] values = new float[]{ 0.0F, 0.0F, 0.01F, 0.51F, 0.6F, 0.8F };
        ApproximateHistogram h = buildHistogram(50, values, 0.5F, 1.0F);
        Histogram h3 = h.toHistogram(0.2F, 0);
        Assert.assertArrayEquals("Expected counts match actual counts", new double[]{ 3.0F, 2.0F, 1.0F }, h3.getCounts(), 0.1F);
        Assert.assertArrayEquals("expected breaks match actual breaks", new double[]{ -0.2F, 0.5F, 0.7F, 0.9F }, h3.getBreaks(), 0.1F);
    }

    @Test
    public void testBuckets5() {
        final float[] values = new float[]{ 0.1F, 0.5F, 0.6F };
        ApproximateHistogram h = buildHistogram(50, values, 0.0F, 1.0F);
        Histogram h4 = h.toHistogram(0.5F, 0);
        Assert.assertArrayEquals("Expected counts match actual counts", new double[]{ 2, 1 }, h4.getCounts(), 0.1F);
        Assert.assertArrayEquals("Expected breaks match actual breaks", new double[]{ 0.0F, 0.5F, 1.0F }, h4.getBreaks(), 0.1F);
    }

    @Test
    public void testEmptyHistogram() {
        ApproximateHistogram h = new ApproximateHistogram(50);
        Assert.assertArrayEquals(new float[]{ Float.NaN, Float.NaN }, h.getQuantiles(new float[]{ 0.8F, 0.9F }), 1.0E-9F);
    }
}

