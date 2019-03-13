/**
 * Copyright 2014 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zxing.common.detector;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link MathUtils}.
 */
public final class MathUtilsTestCase extends Assert {
    private static final float EPSILON = 1.0E-8F;

    @Test
    public void testRound() {
        Assert.assertEquals((-1), MathUtils.round((-1.0F)));
        Assert.assertEquals(0, MathUtils.round(0.0F));
        Assert.assertEquals(1, MathUtils.round(1.0F));
        Assert.assertEquals(2, MathUtils.round(1.9F));
        Assert.assertEquals(2, MathUtils.round(2.1F));
        Assert.assertEquals(3, MathUtils.round(2.5F));
        Assert.assertEquals((-2), MathUtils.round((-1.9F)));
        Assert.assertEquals((-2), MathUtils.round((-2.1F)));
        Assert.assertEquals((-3), MathUtils.round((-2.5F)));// This differs from Math.round()

        Assert.assertEquals(Integer.MAX_VALUE, MathUtils.round(Integer.MAX_VALUE));
        Assert.assertEquals(Integer.MIN_VALUE, MathUtils.round(Integer.MIN_VALUE));
        Assert.assertEquals(Integer.MAX_VALUE, MathUtils.round(Float.POSITIVE_INFINITY));
        Assert.assertEquals(Integer.MIN_VALUE, MathUtils.round(Float.NEGATIVE_INFINITY));
        Assert.assertEquals(0, MathUtils.round(Float.NaN));
    }

    @Test
    public void testDistance() {
        Assert.assertEquals(((float) (Math.sqrt(8.0))), MathUtils.distance(1.0F, 2.0F, 3.0F, 4.0F), MathUtilsTestCase.EPSILON);
        Assert.assertEquals(0.0F, MathUtils.distance(1.0F, 2.0F, 1.0F, 2.0F), MathUtilsTestCase.EPSILON);
        Assert.assertEquals(((float) (Math.sqrt(8.0))), MathUtils.distance(1, 2, 3, 4), MathUtilsTestCase.EPSILON);
        Assert.assertEquals(0.0F, MathUtils.distance(1, 2, 1, 2), MathUtilsTestCase.EPSILON);
    }

    @Test
    public void testSum() {
        Assert.assertEquals(0, MathUtils.sum(new int[]{  }));
        Assert.assertEquals(1, MathUtils.sum(new int[]{ 1 }));
        Assert.assertEquals(4, MathUtils.sum(new int[]{ 1, 3 }));
        Assert.assertEquals(0, MathUtils.sum(new int[]{ -1, 1 }));
    }
}

