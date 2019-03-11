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


public class TestResources {
    private static final String INVALID_RESOURCE_MSG = "Invalid resource value";

    static class ExtendedResources extends Resources {
        public static Resource unbounded() {
            return new FixedValueResource("UNBOUNDED", Long.MAX_VALUE);
        }

        public static Resource none() {
            return new FixedValueResource("NONE", 0L);
        }
    }

    private static final String EXTRA_RESOURCE_TYPE = "resource2";

    private String resourceTypesFile;

    @Test(timeout = 10000)
    public void testCompareToWithUnboundedResource() {
        unsetExtraResourceType();
        Resource unboundedClone = Resources.clone(TestResources.ExtendedResources.unbounded());
        Assert.assertTrue(((unboundedClone.compareTo(createResource(Long.MAX_VALUE, Integer.MAX_VALUE))) == 0));
        Assert.assertTrue(((unboundedClone.compareTo(createResource(Long.MAX_VALUE, 0))) > 0));
        Assert.assertTrue(((unboundedClone.compareTo(createResource(0, Integer.MAX_VALUE))) > 0));
    }

    @Test(timeout = 10000)
    public void testCompareToWithNoneResource() {
        Assert.assertTrue(((Resources.none().compareTo(createResource(0, 0))) == 0));
        Assert.assertTrue(((Resources.none().compareTo(createResource(1, 0))) < 0));
        Assert.assertTrue(((Resources.none().compareTo(createResource(0, 1))) < 0));
        Assert.assertTrue(((Resources.none().compareTo(createResource(0, 0, 0))) == 0));
        Assert.assertTrue(((Resources.none().compareTo(createResource(1, 0, 0))) < 0));
        Assert.assertTrue(((Resources.none().compareTo(createResource(0, 1, 0))) < 0));
        Assert.assertTrue(((Resources.none().compareTo(createResource(0, 0, 1))) < 0));
    }

    @Test(timeout = 1000)
    public void testFitsIn() {
        Assert.assertTrue(Resources.fitsIn(createResource(1, 1), createResource(2, 2)));
        Assert.assertTrue(Resources.fitsIn(createResource(2, 2), createResource(2, 2)));
        Assert.assertFalse(Resources.fitsIn(createResource(2, 2), createResource(1, 1)));
        Assert.assertFalse(Resources.fitsIn(createResource(1, 2), createResource(2, 1)));
        Assert.assertFalse(Resources.fitsIn(createResource(2, 1), createResource(1, 2)));
        Assert.assertTrue(Resources.fitsIn(createResource(1, 1, 1), createResource(2, 2, 2)));
        Assert.assertTrue(Resources.fitsIn(createResource(1, 1, 0), createResource(2, 2, 0)));
        Assert.assertTrue(Resources.fitsIn(createResource(1, 1, 1), createResource(2, 2, 2)));
    }

    @Test(timeout = 1000)
    public void testComponentwiseMin() {
        Assert.assertEquals(createResource(1, 1), Resources.componentwiseMin(createResource(1, 1), createResource(2, 2)));
        Assert.assertEquals(createResource(1, 1), Resources.componentwiseMin(createResource(2, 2), createResource(1, 1)));
        Assert.assertEquals(createResource(1, 1), Resources.componentwiseMin(createResource(1, 2), createResource(2, 1)));
        Assert.assertEquals(createResource(1, 1, 1), Resources.componentwiseMin(createResource(1, 1, 1), createResource(2, 2, 2)));
        Assert.assertEquals(createResource(1, 1, 0), Resources.componentwiseMin(createResource(2, 2, 2), createResource(1, 1)));
        Assert.assertEquals(createResource(1, 1, 2), Resources.componentwiseMin(createResource(1, 2, 2), createResource(2, 1, 3)));
    }

    @Test
    public void testComponentwiseMax() {
        Assert.assertEquals(createResource(2, 2), Resources.componentwiseMax(createResource(1, 1), createResource(2, 2)));
        Assert.assertEquals(createResource(2, 2), Resources.componentwiseMax(createResource(2, 2), createResource(1, 1)));
        Assert.assertEquals(createResource(2, 2), Resources.componentwiseMax(createResource(1, 2), createResource(2, 1)));
        Assert.assertEquals(createResource(2, 2, 2), Resources.componentwiseMax(createResource(1, 1, 1), createResource(2, 2, 2)));
        Assert.assertEquals(createResource(2, 2, 2), Resources.componentwiseMax(createResource(2, 2, 2), createResource(1, 1)));
        Assert.assertEquals(createResource(2, 2, 3), Resources.componentwiseMax(createResource(1, 2, 2), createResource(2, 1, 3)));
        Assert.assertEquals(createResource(2, 2, 1), Resources.componentwiseMax(createResource(2, 2, 0), createResource(2, 1, 1)));
    }

    @Test
    public void testAdd() {
        Assert.assertEquals(createResource(2, 3), Resources.add(createResource(1, 1), createResource(1, 2)));
        Assert.assertEquals(createResource(3, 2), Resources.add(createResource(1, 1), createResource(2, 1)));
        Assert.assertEquals(createResource(2, 2, 0), Resources.add(createResource(1, 1, 0), createResource(1, 1, 0)));
        Assert.assertEquals(createResource(2, 2, 3), Resources.add(createResource(1, 1, 1), createResource(1, 1, 2)));
    }

    @Test
    public void testSubtract() {
        Assert.assertEquals(createResource(1, 0), Resources.subtract(createResource(2, 1), createResource(1, 1)));
        Assert.assertEquals(createResource(0, 1), Resources.subtract(createResource(1, 2), createResource(1, 1)));
        Assert.assertEquals(createResource(2, 2, 0), Resources.subtract(createResource(3, 3, 0), createResource(1, 1, 0)));
        Assert.assertEquals(createResource(1, 1, 2), Resources.subtract(createResource(2, 2, 3), createResource(1, 1, 1)));
    }

    @Test
    public void testClone() {
        Assert.assertEquals(createResource(1, 1), Resources.clone(createResource(1, 1)));
        Assert.assertEquals(createResource(1, 1, 0), Resources.clone(createResource(1, 1)));
        Assert.assertEquals(createResource(1, 1), Resources.clone(createResource(1, 1, 0)));
        Assert.assertEquals(createResource(1, 1, 2), Resources.clone(createResource(1, 1, 2)));
    }

    @Test
    public void testMultiply() {
        Assert.assertEquals(createResource(4, 2), Resources.multiply(createResource(2, 1), 2));
        Assert.assertEquals(createResource(4, 2, 0), Resources.multiply(createResource(2, 1), 2));
        Assert.assertEquals(createResource(2, 4), Resources.multiply(createResource(1, 2), 2));
        Assert.assertEquals(createResource(2, 4, 0), Resources.multiply(createResource(1, 2), 2));
        Assert.assertEquals(createResource(6, 6, 0), Resources.multiply(createResource(3, 3, 0), 2));
        Assert.assertEquals(createResource(4, 4, 6), Resources.multiply(createResource(2, 2, 3), 2));
    }

    @Test(timeout = 10000)
    public void testMultiplyRoundUp() {
        final double by = 0.5;
        final String memoryErrorMsg = "Invalid memory size.";
        final String vcoreErrorMsg = "Invalid virtual core number.";
        Resource resource = Resources.createResource(1, 1);
        Resource result = Resources.multiplyAndRoundUp(resource, by);
        Assert.assertEquals(memoryErrorMsg, result.getMemorySize(), 1);
        Assert.assertEquals(vcoreErrorMsg, result.getVirtualCores(), 1);
        resource = Resources.createResource(2, 2);
        result = Resources.multiplyAndRoundUp(resource, by);
        Assert.assertEquals(memoryErrorMsg, result.getMemorySize(), 1);
        Assert.assertEquals(vcoreErrorMsg, result.getVirtualCores(), 1);
        resource = Resources.createResource(0, 0);
        result = Resources.multiplyAndRoundUp(resource, by);
        Assert.assertEquals(memoryErrorMsg, result.getMemorySize(), 0);
        Assert.assertEquals(vcoreErrorMsg, result.getVirtualCores(), 0);
    }

    @Test
    public void testMultiplyAndRoundUpCustomResources() {
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(5, 2, 8), Resources.multiplyAndRoundUp(createResource(3, 1, 5), 1.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(5, 2, 0), Resources.multiplyAndRoundUp(createResource(3, 1, 0), 1.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(5, 5, 0), Resources.multiplyAndRoundUp(createResource(3, 3, 0), 1.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(8, 3, 13), Resources.multiplyAndRoundUp(createResource(3, 1, 5), 2.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(8, 3, 0), Resources.multiplyAndRoundUp(createResource(3, 1, 0), 2.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(8, 8, 0), Resources.multiplyAndRoundUp(createResource(3, 3, 0), 2.5));
    }

    @Test
    public void testMultiplyAndRoundDown() {
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(4, 1), Resources.multiplyAndRoundDown(createResource(3, 1), 1.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(4, 1, 0), Resources.multiplyAndRoundDown(createResource(3, 1), 1.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(1, 4), Resources.multiplyAndRoundDown(createResource(1, 3), 1.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(1, 4, 0), Resources.multiplyAndRoundDown(createResource(1, 3), 1.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(7, 7, 0), Resources.multiplyAndRoundDown(createResource(3, 3, 0), 2.5));
        Assert.assertEquals(TestResources.INVALID_RESOURCE_MSG, createResource(2, 2, 7), Resources.multiplyAndRoundDown(createResource(1, 1, 3), 2.5));
    }

    @Test
    public void testMultiplyAndAddTo() throws Exception {
        unsetExtraResourceType();
        setupExtraResourceType();
        Assert.assertEquals(createResource(6, 4), Resources.multiplyAndAddTo(createResource(3, 1), createResource(2, 2), 1.5));
        Assert.assertEquals(createResource(6, 4, 0), Resources.multiplyAndAddTo(createResource(3, 1), createResource(2, 2), 1.5));
        Assert.assertEquals(createResource(4, 7), Resources.multiplyAndAddTo(createResource(1, 1), createResource(2, 4), 1.5));
        Assert.assertEquals(createResource(4, 7, 0), Resources.multiplyAndAddTo(createResource(1, 1), createResource(2, 4), 1.5));
        Assert.assertEquals(createResource(6, 4, 0), Resources.multiplyAndAddTo(createResource(3, 1, 0), createResource(2, 2, 0), 1.5));
        Assert.assertEquals(createResource(6, 4, 6), Resources.multiplyAndAddTo(createResource(3, 1, 2), createResource(2, 2, 3), 1.5));
    }

    @Test
    public void testCreateResourceWithSameLongValue() throws Exception {
        unsetExtraResourceType();
        setupExtraResourceType();
        Resource res = ResourceUtils.createResourceWithSameValue(11L);
        Assert.assertEquals(11L, res.getMemorySize());
        Assert.assertEquals(11, res.getVirtualCores());
        Assert.assertEquals(11L, res.getResourceInformation(TestResources.EXTRA_RESOURCE_TYPE).getValue());
    }

    @Test
    public void testCreateResourceWithSameIntValue() throws Exception {
        unsetExtraResourceType();
        setupExtraResourceType();
        Resource res = ResourceUtils.createResourceWithSameValue(11);
        Assert.assertEquals(11, res.getMemorySize());
        Assert.assertEquals(11, res.getVirtualCores());
        Assert.assertEquals(11, res.getResourceInformation(TestResources.EXTRA_RESOURCE_TYPE).getValue());
    }

    @Test
    public void testCreateSimpleResourceWithSameLongValue() {
        Resource res = ResourceUtils.createResourceWithSameValue(11L);
        Assert.assertEquals(11L, res.getMemorySize());
        Assert.assertEquals(11, res.getVirtualCores());
    }

    @Test
    public void testCreateSimpleResourceWithSameIntValue() {
        Resource res = ResourceUtils.createResourceWithSameValue(11);
        Assert.assertEquals(11, res.getMemorySize());
        Assert.assertEquals(11, res.getVirtualCores());
    }
}

