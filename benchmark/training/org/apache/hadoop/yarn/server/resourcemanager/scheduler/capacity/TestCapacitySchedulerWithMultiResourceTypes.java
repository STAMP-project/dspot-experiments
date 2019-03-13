/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import ResourceInformation.MEMORY_MB;
import ResourceInformation.MEMORY_URI;
import ResourceInformation.VCORES;
import ResourceInformation.VCORES_URI;
import ResourceTypes.COUNTABLE;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB;
import YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES;
import YarnConfiguration.RESOURCE_TYPES;
import YarnConfiguration.RM_SCHEDULER;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.resource.TestResourceProfiles;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.DominantResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Capacity Scheduler with multiple resource types.
 */
public class TestCapacitySchedulerWithMultiResourceTypes {
    private static String RESOURCE_1 = "res1";

    @Test
    public void testMaximumAllocationRefreshWithMultipleResourceTypes() throws Exception {
        // Initialize resource map
        Map<String, ResourceInformation> riMap = new HashMap<>();
        // Initialize mandatory resources
        ResourceInformation memory = ResourceInformation.newInstance(MEMORY_MB.getName(), MEMORY_MB.getUnits(), DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB);
        ResourceInformation vcores = ResourceInformation.newInstance(VCORES.getName(), VCORES.getUnits(), DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES, DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES);
        riMap.put(MEMORY_URI, memory);
        riMap.put(VCORES_URI, vcores);
        riMap.put(TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1, ResourceInformation.newInstance(TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1, "", 0, COUNTABLE, 0, 3333L));
        ResourceUtils.initializeResourcesFromResourceInformationMap(riMap);
        CapacitySchedulerConfiguration csconf = new CapacitySchedulerConfiguration();
        csconf.setMaximumApplicationMasterResourcePerQueuePercent("root", 100.0F);
        csconf.setMaximumAMResourcePercentPerPartition("root", "", 100.0F);
        csconf.setMaximumApplicationMasterResourcePerQueuePercent("root.default", 100.0F);
        csconf.setMaximumAMResourcePercentPerPartition("root.default", "", 100.0F);
        csconf.setResourceComparator(DominantResourceCalculator.class);
        csconf.set(RESOURCE_TYPES, TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1);
        csconf.setInt(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1)) + ".maximum-allocation"), 3333);
        YarnConfiguration conf = new YarnConfiguration(csconf);
        // Don't reset resource types since we have already configured resource
        // types
        conf.setBoolean(TestResourceProfiles.TEST_CONF_RESET_RESOURCE_TYPES, false);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        MockRM rm = new MockRM(conf);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        Assert.assertEquals(3333L, cs.getMaximumResourceCapability().getResourceValue(TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1));
        Assert.assertEquals(3333L, cs.getMaximumAllocation().getResourceValue(TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumResourceCapability().getResourceValue(MEMORY_URI));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumAllocation().getResourceValue(MEMORY_URI));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumResourceCapability().getResourceValue(VCORES_URI));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumAllocation().getResourceValue(VCORES_URI));
        // Set RES_1 to 3332 (less than 3333) and refresh CS, failures expected.
        csconf.set(RESOURCE_TYPES, TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1);
        csconf.setInt(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1)) + ".maximum-allocation"), 3332);
        boolean exception = false;
        try {
            cs.reinitialize(csconf, getRMContext());
        } catch (IOException e) {
            exception = true;
        }
        Assert.assertTrue("Should have exception in CS", exception);
        // Maximum allocation won't be updated
        Assert.assertEquals(3333L, cs.getMaximumResourceCapability().getResourceValue(TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1));
        Assert.assertEquals(3333L, cs.getMaximumAllocation().getResourceValue(TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumResourceCapability().getResourceValue(MEMORY_URI));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumAllocation().getResourceValue(MEMORY_URI));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumResourceCapability().getResourceValue(VCORES_URI));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumAllocation().getResourceValue(VCORES_URI));
        // Set RES_1 to 3334 and refresh CS, should success
        csconf.set(RESOURCE_TYPES, TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1);
        csconf.setInt(((((YarnConfiguration.RESOURCE_TYPES) + ".") + (TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1)) + ".maximum-allocation"), 3334);
        cs.reinitialize(csconf, getRMContext());
        // Maximum allocation will be updated
        Assert.assertEquals(3334, cs.getMaximumResourceCapability().getResourceValue(TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1));
        // Since we haven't updated the real configuration of ResourceUtils,
        // cs.getMaximumAllocation won't be updated.
        Assert.assertEquals(3333, cs.getMaximumAllocation().getResourceValue(TestCapacitySchedulerWithMultiResourceTypes.RESOURCE_1));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumResourceCapability().getResourceValue(MEMORY_URI));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, cs.getMaximumAllocation().getResourceValue(MEMORY_URI));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumResourceCapability().getResourceValue(VCORES_URI));
        Assert.assertEquals(DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, cs.getMaximumAllocation().getResourceValue(VCORES_URI));
        close();
    }

    @Test
    public void testDefaultResourceCalculatorWithThirdResourceTypes() throws Exception {
        CapacitySchedulerConfiguration csconf = new CapacitySchedulerConfiguration();
        csconf.setResourceComparator(DefaultResourceCalculator.class);
        YarnConfiguration conf = new YarnConfiguration(csconf);
        String[] res1 = new String[]{ "resource1", "M" };
        String[] res2 = new String[]{ "resource2", "G" };
        String[] res3 = new String[]{ "resource3", "H" };
        String[][] test = new String[][]{ res1, res2, res3 };
        String resSt = "";
        for (String[] resources : test) {
            resSt += (resources[0]) + ",";
        }
        resSt = resSt.substring(0, ((resSt.length()) - 1));
        conf.set(RESOURCE_TYPES, resSt);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        boolean exception = false;
        try {
            MockRM rm = new MockRM(conf);
        } catch (YarnRuntimeException e) {
            exception = true;
        }
        Assert.assertTrue("Should have exception in CS", exception);
    }
}

