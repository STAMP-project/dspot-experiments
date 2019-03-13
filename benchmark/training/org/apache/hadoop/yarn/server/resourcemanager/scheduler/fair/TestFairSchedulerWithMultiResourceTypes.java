/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;


import ResourceInformation.MEMORY_MB;
import ResourceInformation.VCORES;
import YarnConfiguration.RESOURCE_TYPES;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class TestFairSchedulerWithMultiResourceTypes extends FairSchedulerTestBase {
    private static final String CUSTOM_RESOURCE = "custom-resource";

    @Test
    public void testMaximumAllocationRefresh() throws IOException {
        conf.set(RESOURCE_TYPES, TestFairSchedulerWithMultiResourceTypes.CUSTOM_RESOURCE);
        conf.set(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (TestFairSchedulerWithMultiResourceTypes.CUSTOM_RESOURCE)) + (UNITS)), "k");
        conf.setInt(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (TestFairSchedulerWithMultiResourceTypes.CUSTOM_RESOURCE)) + (MAXIMUM_ALLOCATION)), 10000);
        conf.setInt(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (VCORES.getName())) + (MAXIMUM_ALLOCATION)), 4);
        conf.setInt(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (MEMORY_MB.getName())) + (MAXIMUM_ALLOCATION)), 512);
        scheduler.init(conf);
        scheduler.reinitialize(conf, null);
        Resource maxAllowedAllocation = scheduler.getNodeTracker().getMaxAllowedAllocation();
        ResourceInformation customResource = maxAllowedAllocation.getResourceInformation(TestFairSchedulerWithMultiResourceTypes.CUSTOM_RESOURCE);
        Assert.assertEquals(512, maxAllowedAllocation.getMemorySize());
        Assert.assertEquals(4, maxAllowedAllocation.getVirtualCores());
        Assert.assertEquals(10000, customResource.getValue());
        conf = new YarnConfiguration();
        conf.set(RESOURCE_TYPES, TestFairSchedulerWithMultiResourceTypes.CUSTOM_RESOURCE);
        conf.set(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (TestFairSchedulerWithMultiResourceTypes.CUSTOM_RESOURCE)) + (UNITS)), "k");
        conf.setInt(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (TestFairSchedulerWithMultiResourceTypes.CUSTOM_RESOURCE)) + (MAXIMUM_ALLOCATION)), 20000);
        conf.setInt(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (VCORES.getName())) + (MAXIMUM_ALLOCATION)), 8);
        conf.setInt(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (MEMORY_MB.getName())) + (MAXIMUM_ALLOCATION)), 2048);
        scheduler.reinitialize(conf, null);
        maxAllowedAllocation = scheduler.getNodeTracker().getMaxAllowedAllocation();
        customResource = maxAllowedAllocation.getResourceInformation(TestFairSchedulerWithMultiResourceTypes.CUSTOM_RESOURCE);
        Assert.assertEquals(2048, maxAllowedAllocation.getMemorySize());
        Assert.assertEquals(8, maxAllowedAllocation.getVirtualCores());
        Assert.assertEquals(20000, customResource.getValue());
    }
}

