/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util;


import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled
public class NearlyEqualsTest {
    /**
     * Regular large numbers - generally not problematic
     */
    @Test
    public void big() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(1000000.0F, 1000001.0F));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(1000001.0F, 1000000.0F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(10000.0F, 10001.0F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(10001.0F, 10000.0F));
    }

    /**
     * Negative large numbers
     */
    @Test
    public void bigNeg() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-1000000.0F), (-1000001.0F)));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-1000001.0F), (-1000000.0F)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-10000.0F), (-10001.0F)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-10001.0F), (-10000.0F)));
    }

    /**
     * Numbers around 1
     */
    @Test
    public void mid() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(1.0000001F, 1.0000002F));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(1.0000002F, 1.0000001F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.0002F, 1.0001F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.0001F, 1.0002F));
    }

    /**
     * Numbers around -1
     */
    @Test
    public void midNeg() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-1.000001F), (-1.000002F)));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-1.000002F), (-1.000001F)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-1.0001F), (-1.0002F)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-1.0002F), (-1.0001F)));
    }

    /**
     * Numbers between 1 and 0
     */
    @Test
    public void small() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(1.000001E-9F, 1.000002E-9F));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(1.000002E-9F, 1.000001E-9F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.002E-12F, 1.001E-12F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.001E-12F, 1.002E-12F));
    }

    /**
     * Numbers between -1 and 0
     */
    @Test
    public void smallNeg() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-1.000001E-9F), (-1.000002E-9F)));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-1.000002E-9F), (-1.000001E-9F)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-1.002E-12F), (-1.001E-12F)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-1.001E-12F), (-1.002E-12F)));
    }

    /**
     * Comparisons involving zero
     */
    @Test
    public void zero() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(0.0F, 0.0F));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(0.0F, (-0.0F)));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-0.0F), (-0.0F)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.0E-8F, 0.0F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(0.0F, 1.0E-8F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-1.0E-8F), 0.0F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(0.0F, (-1.0E-8F)));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(0.0F, 1.0E-40F, 0.01F));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(1.0E-40F, 0.0F, 0.01F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.0E-40F, 0.0F, 1.0E-6F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(0.0F, 1.0E-40F, 1.0E-6F));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(0.0F, (-1.0E-40F), 0.1F));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-1.0E-40F), 0.0F, 0.1F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-1.0E-40F), 0.0F, 1.0E-8F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(0.0F, (-1.0E-40F), 1.0E-8F));
    }

    /**
     * Comparisons involving extreme values (overflow potential)
     */
    @Test
    public void extremeMax() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(Float.MAX_VALUE, Float.MAX_VALUE));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.MAX_VALUE, (-(Float.MAX_VALUE))));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-(Float.MAX_VALUE)), Float.MAX_VALUE));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.MAX_VALUE, ((Float.MAX_VALUE) / 2)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.MAX_VALUE, ((-(Float.MAX_VALUE)) / 2)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-(Float.MAX_VALUE)), ((Float.MAX_VALUE) / 2)));
    }

    /**
     * Comparisons involving infinities
     */
    @Test
    public void infinities() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.POSITIVE_INFINITY, Float.MAX_VALUE));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NEGATIVE_INFINITY, (-(Float.MAX_VALUE))));
    }

    /**
     * Comparisons involving NaN values
     */
    @Test
    public void nan() {
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NaN, Float.NaN));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NaN, 0.0F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-0.0F), Float.NaN));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NaN, (-0.0F)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(0.0F, Float.NaN));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NaN, Float.POSITIVE_INFINITY));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.POSITIVE_INFINITY, Float.NaN));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NaN, Float.NEGATIVE_INFINITY));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NEGATIVE_INFINITY, Float.NaN));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NaN, Float.MAX_VALUE));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.MAX_VALUE, Float.NaN));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NaN, (-(Float.MAX_VALUE))));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-(Float.MAX_VALUE)), Float.NaN));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NaN, Float.MIN_VALUE));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.MIN_VALUE, Float.NaN));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.NaN, (-(Float.MIN_VALUE))));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-(Float.MIN_VALUE)), Float.NaN));
    }

    /**
     * Comparisons of numbers on opposite sides of 0
     */
    @Test
    public void opposite() {
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.0F, (-1.0F)));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-1.0F), 1.0F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-1.0F), 1.0F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.0F, (-1.0F)));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((10 * (Float.MIN_VALUE)), (10 * (-(Float.MIN_VALUE)))));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((10000 * (Float.MIN_VALUE)), (10000 * (-(Float.MIN_VALUE)))));
    }

    /**
     * The really tricky part - comparisons of numbers very close to zero.
     */
    @Test
    public void ulp() {
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(Float.MIN_VALUE, Float.MIN_VALUE));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(Float.MIN_VALUE, (-(Float.MIN_VALUE))));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-(Float.MIN_VALUE)), Float.MIN_VALUE));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(Float.MIN_VALUE, 0));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(0, Float.MIN_VALUE));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual((-(Float.MIN_VALUE)), 0));
        Assert.assertTrue(NearlyEqualsTest.nearlyEqual(0, (-(Float.MIN_VALUE))));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.0E-9F, (-(Float.MIN_VALUE))));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(1.0E-9F, Float.MIN_VALUE));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual(Float.MIN_VALUE, 1.0E-9F));
        Assert.assertFalse(NearlyEqualsTest.nearlyEqual((-(Float.MIN_VALUE)), 1.0E-9F));
    }
}

