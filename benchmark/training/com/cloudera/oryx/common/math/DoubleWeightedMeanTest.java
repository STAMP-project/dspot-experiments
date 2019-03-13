/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.math;


import com.cloudera.oryx.common.OryxTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link DoubleWeightedMean}.
 */
public final class DoubleWeightedMeanTest extends OryxTest {
    @Test
    public void testNone() {
        DoubleWeightedMean mean = new DoubleWeightedMean();
        Assert.assertEquals(0, mean.getN());
        OryxTest.assertNaN(mean.getResult());
    }

    @Test
    public void testOne() {
        DoubleWeightedMean mean = new DoubleWeightedMean();
        mean.increment(1.5);
        Assert.assertEquals(1, mean.getN());
        Assert.assertEquals(1.5, mean.getResult());
        Assert.assertEquals("1.5", mean.toString());
    }

    @Test
    public void testWeighted() {
        DoubleWeightedMean mean = new DoubleWeightedMean();
        mean.increment(0.2, 4.0);
        mean.increment((-0.1), 2.0);
        Assert.assertEquals(2, mean.getN());
        Assert.assertEquals(0.1, mean.getResult());
    }

    @Test
    public void testNegative() {
        DoubleWeightedMean mean = new DoubleWeightedMean();
        mean.increment((-0.1), 2.1);
        mean.increment(0.1, 2.1);
        Assert.assertEquals(2, mean.getN());
        Assert.assertEquals(0.0, mean.getResult());
    }

    @Test
    public void testComplex() {
        DoubleWeightedMean mean = new DoubleWeightedMean();
        for (int i = 1; i <= 5; i++) {
            mean.increment((1.0 / (i + 1)), i);
        }
        Assert.assertEquals(5, mean.getN());
        Assert.assertEquals(((((((1.0 / 2.0) + (2.0 / 3.0)) + (3.0 / 4.0)) + (4.0 / 5.0)) + (5.0 / 6.0)) / 15.0), mean.getResult());
    }

    @Test
    public void testCopyEquals() {
        DoubleWeightedMean mean = new DoubleWeightedMean();
        mean.increment(0.2, 4.0);
        mean.increment((-0.1), 2.0);
        DoubleWeightedMean copy = mean.copy();
        Assert.assertEquals(mean, copy);
        Assert.assertEquals(mean.hashCode(), copy.hashCode());
        DoubleWeightedMean zero = new DoubleWeightedMean();
        mean.clear();
        Assert.assertEquals(zero, mean);
    }
}

