/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.metrics.stats;


import org.apache.kafka.common.metrics.stats.Histogram.BinScheme;
import org.apache.kafka.common.metrics.stats.Histogram.ConstantBinScheme;
import org.apache.kafka.common.metrics.stats.Histogram.LinearBinScheme;
import org.junit.Assert;
import org.junit.Test;


public class HistogramTest {
    private static final double EPS = 1.0E-7;

    @Test
    public void testHistogram() {
        BinScheme scheme = new ConstantBinScheme(10, (-5), 5);
        Histogram hist = new Histogram(scheme);
        for (int i = -5; i < 5; i++)
            hist.record(i);

        for (int i = 0; i < 10; i++)
            Assert.assertEquals(scheme.fromBin(i), hist.value(((i / 10.0) + (HistogramTest.EPS))), HistogramTest.EPS);

    }

    @Test
    public void testConstantBinScheme() {
        ConstantBinScheme scheme = new ConstantBinScheme(5, (-5), 5);
        Assert.assertEquals("A value below the lower bound should map to the first bin", 0, scheme.toBin((-5.01)));
        Assert.assertEquals("A value above the upper bound should map to the last bin", 4, scheme.toBin(5.01));
        Assert.assertEquals("Check boundary of bucket 0", 0, scheme.toBin((-5.0001)));
        Assert.assertEquals("Check boundary of bucket 0", 0, scheme.toBin((-5.0)));
        Assert.assertEquals("Check boundary of bucket 0", 0, scheme.toBin((-4.99999)));
        Assert.assertEquals("Check boundary of bucket 0", 0, scheme.toBin((-3.00001)));
        Assert.assertEquals("Check boundary of bucket 1", 1, scheme.toBin((-3)));
        Assert.assertEquals("Check boundary of bucket 1", 1, scheme.toBin((-1.00001)));
        Assert.assertEquals("Check boundary of bucket 2", 2, scheme.toBin((-1)));
        Assert.assertEquals("Check boundary of bucket 2", 2, scheme.toBin(0.99999));
        Assert.assertEquals("Check boundary of bucket 3", 3, scheme.toBin(1));
        Assert.assertEquals("Check boundary of bucket 3", 3, scheme.toBin(2.99999));
        Assert.assertEquals("Check boundary of bucket 4", 4, scheme.toBin(3));
        Assert.assertEquals("Check boundary of bucket 4", 4, scheme.toBin(4.9999));
        Assert.assertEquals("Check boundary of bucket 4", 4, scheme.toBin(5.0));
        Assert.assertEquals("Check boundary of bucket 4", 4, scheme.toBin(5.001));
        Assert.assertEquals(Float.NEGATIVE_INFINITY, scheme.fromBin((-1)), 0.001);
        Assert.assertEquals(Float.POSITIVE_INFINITY, scheme.fromBin(5), 0.001);
        Assert.assertEquals((-5.0), scheme.fromBin(0), 0.001);
        Assert.assertEquals((-3.0), scheme.fromBin(1), 0.001);
        Assert.assertEquals((-1.0), scheme.fromBin(2), 0.001);
        Assert.assertEquals(1.0, scheme.fromBin(3), 0.001);
        Assert.assertEquals(3.0, scheme.fromBin(4), 0.001);
        checkBinningConsistency(scheme);
    }

    @Test
    public void testConstantBinSchemeWithPositiveRange() {
        ConstantBinScheme scheme = new ConstantBinScheme(5, 0, 5);
        Assert.assertEquals("A value below the lower bound should map to the first bin", 0, scheme.toBin((-1.0)));
        Assert.assertEquals("A value above the upper bound should map to the last bin", 4, scheme.toBin(5.01));
        Assert.assertEquals("Check boundary of bucket 0", 0, scheme.toBin((-1.0E-4)));
        Assert.assertEquals("Check boundary of bucket 0", 0, scheme.toBin(0.0));
        Assert.assertEquals("Check boundary of bucket 0", 0, scheme.toBin(1.0E-4));
        Assert.assertEquals("Check boundary of bucket 0", 0, scheme.toBin(0.9999));
        Assert.assertEquals("Check boundary of bucket 1", 1, scheme.toBin(1.0));
        Assert.assertEquals("Check boundary of bucket 1", 1, scheme.toBin(1.0001));
        Assert.assertEquals("Check boundary of bucket 1", 1, scheme.toBin(1.9999));
        Assert.assertEquals("Check boundary of bucket 2", 2, scheme.toBin(2.0));
        Assert.assertEquals("Check boundary of bucket 2", 2, scheme.toBin(2.0001));
        Assert.assertEquals("Check boundary of bucket 2", 2, scheme.toBin(2.9999));
        Assert.assertEquals("Check boundary of bucket 3", 3, scheme.toBin(3.0));
        Assert.assertEquals("Check boundary of bucket 3", 3, scheme.toBin(3.0001));
        Assert.assertEquals("Check boundary of bucket 3", 3, scheme.toBin(3.9999));
        Assert.assertEquals("Check boundary of bucket 4", 4, scheme.toBin(4.0));
        Assert.assertEquals("Check boundary of bucket 4", 4, scheme.toBin(4.9999));
        Assert.assertEquals("Check boundary of bucket 4", 4, scheme.toBin(5.0));
        Assert.assertEquals("Check boundary of bucket 4", 4, scheme.toBin(5.0001));
        Assert.assertEquals(Float.NEGATIVE_INFINITY, scheme.fromBin((-1)), 0.001);
        Assert.assertEquals(Float.POSITIVE_INFINITY, scheme.fromBin(5), 0.001);
        Assert.assertEquals(0.0, scheme.fromBin(0), 0.001);
        Assert.assertEquals(1.0, scheme.fromBin(1), 0.001);
        Assert.assertEquals(2.0, scheme.fromBin(2), 0.001);
        Assert.assertEquals(3.0, scheme.fromBin(3), 0.001);
        Assert.assertEquals(4.0, scheme.fromBin(4), 0.001);
        checkBinningConsistency(scheme);
    }

    @Test
    public void testLinearBinScheme() {
        LinearBinScheme scheme = new LinearBinScheme(10, 10);
        Assert.assertEquals(Float.NEGATIVE_INFINITY, scheme.fromBin((-1)), 0.001);
        Assert.assertEquals(Float.POSITIVE_INFINITY, scheme.fromBin(11), 0.001);
        Assert.assertEquals(0.0, scheme.fromBin(0), 0.001);
        Assert.assertEquals(0.2222, scheme.fromBin(1), 0.001);
        Assert.assertEquals(0.6666, scheme.fromBin(2), 0.001);
        Assert.assertEquals(1.3333, scheme.fromBin(3), 0.001);
        Assert.assertEquals(2.2222, scheme.fromBin(4), 0.001);
        Assert.assertEquals(3.3333, scheme.fromBin(5), 0.001);
        Assert.assertEquals(4.6667, scheme.fromBin(6), 0.001);
        Assert.assertEquals(6.2222, scheme.fromBin(7), 0.001);
        Assert.assertEquals(8.0, scheme.fromBin(8), 0.001);
        Assert.assertEquals(10.0, scheme.fromBin(9), 0.001);
        Assert.assertEquals(0, scheme.toBin(0.0));
        Assert.assertEquals(0, scheme.toBin(0.2221));
        Assert.assertEquals(1, scheme.toBin(0.2223));
        Assert.assertEquals(2, scheme.toBin(0.6667));
        Assert.assertEquals(3, scheme.toBin(1.3334));
        Assert.assertEquals(4, scheme.toBin(2.2223));
        Assert.assertEquals(5, scheme.toBin(3.3334));
        Assert.assertEquals(6, scheme.toBin(4.6667));
        Assert.assertEquals(7, scheme.toBin(6.2223));
        Assert.assertEquals(8, scheme.toBin(8.0));
        Assert.assertEquals(9, scheme.toBin(10.0));
        Assert.assertEquals(9, scheme.toBin(10.001));
        Assert.assertEquals(Float.POSITIVE_INFINITY, scheme.fromBin(10), 0.001);
        checkBinningConsistency(scheme);
    }
}

