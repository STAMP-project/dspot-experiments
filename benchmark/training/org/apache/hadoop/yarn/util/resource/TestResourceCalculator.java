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
package org.apache.hadoop.yarn.util.resource;


import org.apache.hadoop.yarn.api.records.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestResourceCalculator {
    private final ResourceCalculator resourceCalculator;

    public TestResourceCalculator(String name, ResourceCalculator rs) {
        this.resourceCalculator = rs;
    }

    @Test(timeout = 10000)
    public void testFitsIn() {
        if ((resourceCalculator) instanceof DefaultResourceCalculator) {
            Assert.assertTrue(resourceCalculator.fitsIn(Resource.newInstance(1, 2), Resource.newInstance(2, 1)));
            Assert.assertTrue(resourceCalculator.fitsIn(Resource.newInstance(1, 2), Resource.newInstance(2, 2)));
            Assert.assertTrue(resourceCalculator.fitsIn(Resource.newInstance(1, 2), Resource.newInstance(1, 2)));
            Assert.assertTrue(resourceCalculator.fitsIn(Resource.newInstance(1, 2), Resource.newInstance(1, 1)));
            Assert.assertFalse(resourceCalculator.fitsIn(Resource.newInstance(2, 1), Resource.newInstance(1, 2)));
        } else
            if ((resourceCalculator) instanceof DominantResourceCalculator) {
                Assert.assertFalse(resourceCalculator.fitsIn(Resource.newInstance(1, 2), Resource.newInstance(2, 1)));
                Assert.assertTrue(resourceCalculator.fitsIn(Resource.newInstance(1, 2), Resource.newInstance(2, 2)));
                Assert.assertTrue(resourceCalculator.fitsIn(Resource.newInstance(1, 2), Resource.newInstance(1, 2)));
                Assert.assertFalse(resourceCalculator.fitsIn(Resource.newInstance(1, 2), Resource.newInstance(1, 1)));
                Assert.assertFalse(resourceCalculator.fitsIn(Resource.newInstance(2, 1), Resource.newInstance(1, 2)));
            }

    }

    @Test
    public void testCompareWithOnlyMandatory() {
        // This test is necessary because there are optimizations that are only
        // triggered when only the mandatory resources are configured.
        // Keep cluster resources even so that the numbers are easy to understand
        Resource cluster = newResource(4, 4);
        assertComparison(cluster, newResource(1, 1), newResource(1, 1), 0);
        assertComparison(cluster, newResource(0, 0), newResource(0, 0), 0);
        assertComparison(cluster, newResource(2, 2), newResource(1, 1), 1);
        assertComparison(cluster, newResource(2, 2), newResource(0, 0), 1);
        if ((resourceCalculator) instanceof DefaultResourceCalculator) {
            testCompareDefaultWithOnlyMandatory(cluster);
        } else
            if ((resourceCalculator) instanceof DominantResourceCalculator) {
                testCompareDominantWithOnlyMandatory(cluster);
            }

    }

    @Test
    public void testCompare() {
        // Test with 3 resources
        TestResourceCalculator.setupExtraResource();
        // Keep cluster resources even so that the numbers are easy to understand
        Resource cluster = newResource(4L, 4, 4);
        assertComparison(cluster, newResource(1, 1, 1), newResource(1, 1, 1), 0);
        assertComparison(cluster, newResource(0, 0, 0), newResource(0, 0, 0), 0);
        assertComparison(cluster, newResource(2, 2, 2), newResource(1, 1, 1), 1);
        assertComparison(cluster, newResource(2, 2, 2), newResource(0, 0, 0), 1);
        if ((resourceCalculator) instanceof DefaultResourceCalculator) {
            testCompareDefault(cluster);
        } else
            if ((resourceCalculator) instanceof DominantResourceCalculator) {
                testCompareDominant(cluster);
            }

    }

    @Test(timeout = 10000)
    public void testCompareWithEmptyCluster() {
        Resource clusterResource = Resource.newInstance(0, 0);
        // For lhs == rhs
        Resource lhs = Resource.newInstance(0, 0);
        Resource rhs = Resource.newInstance(0, 0);
        assertResourcesOperations(clusterResource, lhs, rhs, false, true, false, true, lhs, lhs);
        // lhs > rhs
        lhs = Resource.newInstance(1, 1);
        rhs = Resource.newInstance(0, 0);
        assertResourcesOperations(clusterResource, lhs, rhs, false, false, true, true, lhs, rhs);
        // For lhs < rhs
        lhs = Resource.newInstance(0, 0);
        rhs = Resource.newInstance(1, 1);
        assertResourcesOperations(clusterResource, lhs, rhs, true, true, false, false, rhs, lhs);
        if (!((resourceCalculator) instanceof DominantResourceCalculator)) {
            return;
        }
        // verify for 2 dimensional resources i.e memory and cpu
        // dominant resource types
        lhs = Resource.newInstance(1, 0);
        rhs = Resource.newInstance(0, 1);
        assertResourcesOperations(clusterResource, lhs, rhs, false, true, false, true, lhs, lhs);
        lhs = Resource.newInstance(0, 1);
        rhs = Resource.newInstance(1, 0);
        assertResourcesOperations(clusterResource, lhs, rhs, false, true, false, true, lhs, lhs);
        lhs = Resource.newInstance(1, 1);
        rhs = Resource.newInstance(1, 0);
        assertResourcesOperations(clusterResource, lhs, rhs, false, false, true, true, lhs, rhs);
        lhs = Resource.newInstance(0, 1);
        rhs = Resource.newInstance(1, 1);
        assertResourcesOperations(clusterResource, lhs, rhs, true, true, false, false, rhs, lhs);
    }

    /**
     * Test resource normalization.
     */
    @Test(timeout = 10000)
    public void testNormalize() {
        // requested resources value cannot be an arbitrary number.
        Resource ask = Resource.newInstance(1111, 2);
        Resource min = Resource.newInstance(1024, 1);
        Resource max = Resource.newInstance((8 * 1024), 8);
        Resource increment = Resource.newInstance(1024, 4);
        if ((resourceCalculator) instanceof DefaultResourceCalculator) {
            Resource result = Resources.normalize(resourceCalculator, ask, min, max, increment);
            Assert.assertEquals((2 * 1024), result.getMemorySize());
        } else
            if ((resourceCalculator) instanceof DominantResourceCalculator) {
                Resource result = Resources.normalize(resourceCalculator, ask, min, max, increment);
                Assert.assertEquals((2 * 1024), result.getMemorySize());
                Assert.assertEquals(4, result.getVirtualCores());
            }

        // if resources asked are less than minimum resource, then normalize it to
        // minimum resource.
        ask = Resource.newInstance(512, 0);
        min = Resource.newInstance((2 * 1024), 2);
        max = Resource.newInstance((8 * 1024), 8);
        increment = Resource.newInstance(1024, 1);
        if ((resourceCalculator) instanceof DefaultResourceCalculator) {
            Resource result = Resources.normalize(resourceCalculator, ask, min, max, increment);
            Assert.assertEquals((2 * 1024), result.getMemorySize());
        } else
            if ((resourceCalculator) instanceof DominantResourceCalculator) {
                Resource result = Resources.normalize(resourceCalculator, ask, min, max, increment);
                Assert.assertEquals((2 * 1024), result.getMemorySize());
                Assert.assertEquals(2, result.getVirtualCores());
            }

        // if resources asked are larger than maximum resource, then normalize it to
        // maximum resources.
        ask = Resource.newInstance((9 * 1024), 9);
        min = Resource.newInstance((2 * 1024), 2);
        max = Resource.newInstance((8 * 1024), 8);
        increment = Resource.newInstance(1024, 1);
        if ((resourceCalculator) instanceof DefaultResourceCalculator) {
            Resource result = Resources.normalize(resourceCalculator, ask, min, max, increment);
            Assert.assertEquals((8 * 1024), result.getMemorySize());
        } else
            if ((resourceCalculator) instanceof DominantResourceCalculator) {
                Resource result = Resources.normalize(resourceCalculator, ask, min, max, increment);
                Assert.assertEquals((8 * 1024), result.getMemorySize());
                Assert.assertEquals(8, result.getVirtualCores());
            }

        // if increment is 0, use minimum resource as the increment resource.
        ask = Resource.newInstance(1111, 2);
        min = Resource.newInstance((2 * 1024), 2);
        max = Resource.newInstance((8 * 1024), 8);
        increment = Resource.newInstance(0, 0);
        if ((resourceCalculator) instanceof DefaultResourceCalculator) {
            Resource result = Resources.normalize(resourceCalculator, ask, min, max, increment);
            Assert.assertEquals((2 * 1024), result.getMemorySize());
        } else
            if ((resourceCalculator) instanceof DominantResourceCalculator) {
                Resource result = Resources.normalize(resourceCalculator, ask, min, max, increment);
                Assert.assertEquals((2 * 1024), result.getMemorySize());
                Assert.assertEquals(2, result.getVirtualCores());
            }

    }

    @Test
    public void testDivisionByZeroRatioDenominatorIsZero() {
        float ratio = resourceCalculator.ratio(newResource(1, 1), newResource(0, 0));
        Assert.assertEquals(Float.POSITIVE_INFINITY, ratio, 1.0E-5);
    }

    @Test
    public void testDivisionByZeroRatioNumeratorAndDenominatorIsZero() {
        float ratio = resourceCalculator.ratio(newResource(0, 0), newResource(0, 0));
        Assert.assertEquals(0.0, ratio, 1.0E-5);
    }
}

