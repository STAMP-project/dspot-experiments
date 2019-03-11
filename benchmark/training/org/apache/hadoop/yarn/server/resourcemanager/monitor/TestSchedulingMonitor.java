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
package org.apache.hadoop.yarn.server.resourcemanager.monitor;


import YarnConfiguration.DEFAULT_RM_SCHEDULER_ENABLE_MONITORS;
import YarnConfiguration.RM_SCHEDULER;
import YarnConfiguration.RM_SCHEDULER_ENABLE_MONITORS;
import YarnConfiguration.RM_SCHEDULER_MONITOR_POLICIES;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity.ProportionalCapacityPreemptionPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestSchedulingMonitor {
    @Test(timeout = 10000)
    public void testRMStarts() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(RM_SCHEDULER_ENABLE_MONITORS, true);
        conf.set(RM_SCHEDULER_MONITOR_POLICIES, ProportionalCapacityPreemptionPolicy.class.getCanonicalName());
        ResourceManager rm = new MockRM();
        rm.init(conf);
        SchedulingEditPolicy mPolicy = Mockito.mock(SchedulingEditPolicy.class);
        Mockito.when(mPolicy.getMonitoringInterval()).thenReturn(1000L);
        SchedulingMonitor monitor = new SchedulingMonitor(rm.getRMContext(), mPolicy);
        monitor.serviceInit(conf);
        monitor.serviceStart();
        Mockito.verify(mPolicy, Mockito.timeout(10000)).editSchedule();
        monitor.close();
        rm.close();
    }

    @Test(timeout = 10000)
    public void testRMUpdateSchedulingEditPolicy() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        conf.setBoolean(RM_SCHEDULER_ENABLE_MONITORS, true);
        MockRM rm = new MockRM(conf);
        start();
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        SchedulingMonitorManager smm = cs.getSchedulingMonitorManager();
        // runningSchedulingMonitors should not be empty when initialize RM
        // scheduler monitor
        cs.reinitialize(conf, getRMContext());
        Assert.assertFalse(smm.isRSMEmpty());
        // make sure runningSchedulingPolicies contains all the configured policy
        // in YARNConfiguration
        String[] configuredPolicies = conf.getStrings(RM_SCHEDULER_MONITOR_POLICIES);
        Set<String> configurePoliciesSet = new HashSet<>();
        for (String s : configuredPolicies) {
            configurePoliciesSet.add(s);
        }
        Assert.assertTrue(smm.isSameConfiguredPolicies(configurePoliciesSet));
        // disable RM scheduler monitor
        conf.setBoolean(RM_SCHEDULER_ENABLE_MONITORS, DEFAULT_RM_SCHEDULER_ENABLE_MONITORS);
        cs.reinitialize(conf, getRMContext());
        Assert.assertTrue(smm.isRSMEmpty());
    }
}

