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
package org.apache.hadoop.yarn.server.resourcemanager;


import ContainerState.COMPLETE;
import ContainerState.RUNNING;
import RMAppAttemptState.ALLOCATED;
import RMAppAttemptState.SCHEDULED;
import RMAppState.ACCEPTED;
import RMContainerState.COMPLETED;
import YarnConfiguration.RECOVERY_ENABLED;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_STORE;
import YarnConfiguration.RM_WORK_PRESERVING_RECOVERY_ENABLED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.MemoryRMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AggregateAppResourceUsage;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.junit.Assert;
import org.junit.Test;


public class TestContainerResourceUsage {
    private YarnConfiguration conf;

    @Test(timeout = 120000)
    public void testUsageWithOneAttemptAndOneContainer() throws Exception {
        MockRM rm = new MockRM(conf);
        start();
        MockNM nm = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm.registerNode();
        RMApp app0 = rm.submitApp(200);
        RMAppMetrics rmAppMetrics = app0.getRMAppMetrics();
        Assert.assertTrue(("Before app submittion, memory seconds should have been 0 but was " + (rmAppMetrics.getMemorySeconds())), ((rmAppMetrics.getMemorySeconds()) == 0));
        Assert.assertTrue(("Before app submission, vcore seconds should have been 0 but was " + (rmAppMetrics.getVcoreSeconds())), ((rmAppMetrics.getVcoreSeconds()) == 0));
        RMAppAttempt attempt0 = app0.getCurrentAppAttempt();
        nm.nodeHeartbeat(true);
        MockAM am0 = rm.sendAMLaunched(attempt0.getAppAttemptId());
        am0.registerAppAttempt();
        RMContainer rmContainer = getResourceScheduler().getRMContainer(attempt0.getMasterContainer().getId());
        // Allow metrics to accumulate.
        int sleepInterval = 1000;
        int cumulativeSleepTime = 0;
        while (((rmAppMetrics.getMemorySeconds()) <= 0) && (cumulativeSleepTime < 5000)) {
            Thread.sleep(sleepInterval);
            cumulativeSleepTime += sleepInterval;
        } 
        rmAppMetrics = app0.getRMAppMetrics();
        Assert.assertTrue(("While app is running, memory seconds should be >0 but is " + (rmAppMetrics.getMemorySeconds())), ((rmAppMetrics.getMemorySeconds()) > 0));
        Assert.assertTrue(("While app is running, vcore seconds should be >0 but is " + (rmAppMetrics.getVcoreSeconds())), ((rmAppMetrics.getVcoreSeconds()) > 0));
        MockRM.finishAMAndVerifyAppState(app0, rm, nm, am0);
        AggregateAppResourceUsage ru = calculateContainerResourceMetrics(rmContainer);
        rmAppMetrics = app0.getRMAppMetrics();
        Assert.assertEquals("Unexpected MemorySeconds value", ru.getMemorySeconds(), rmAppMetrics.getMemorySeconds());
        Assert.assertEquals("Unexpected VcoreSeconds value", ru.getVcoreSeconds(), rmAppMetrics.getVcoreSeconds());
        stop();
    }

    @Test(timeout = 120000)
    public void testUsageWithMultipleContainersAndRMRestart() throws Exception {
        // Set max attempts to 1 so that when the first attempt fails, the app
        // won't try to start a new one.
        conf.setInt(RM_AM_MAX_ATTEMPTS, 1);
        conf.setBoolean(RECOVERY_ENABLED, true);
        conf.setBoolean(RM_WORK_PRESERVING_RECOVERY_ENABLED, false);
        conf.set(RM_STORE, MemoryRMStateStore.class.getName());
        MockRM rm0 = new MockRM(conf);
        start();
        MockMemoryRMStateStore memStore = ((MockMemoryRMStateStore) (rm0.getRMStateStore()));
        MockNM nm = new MockNM("127.0.0.1:1234", 65536, getResourceTrackerService());
        nm.registerNode();
        RMApp app0 = rm0.submitApp(200);
        rm0.waitForState(app0.getApplicationId(), ACCEPTED);
        RMAppAttempt attempt0 = app0.getCurrentAppAttempt();
        ApplicationAttemptId attemptId0 = attempt0.getAppAttemptId();
        rm0.waitForState(attemptId0, SCHEDULED);
        nm.nodeHeartbeat(true);
        rm0.waitForState(attemptId0, ALLOCATED);
        MockAM am0 = rm0.sendAMLaunched(attempt0.getAppAttemptId());
        am0.registerAppAttempt();
        int NUM_CONTAINERS = 2;
        am0.allocate("127.0.0.1", 1000, NUM_CONTAINERS, new ArrayList<ContainerId>());
        nm.nodeHeartbeat(true);
        List<Container> conts = am0.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
        while ((conts.size()) != NUM_CONTAINERS) {
            nm.nodeHeartbeat(true);
            conts.addAll(am0.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
            Thread.sleep(500);
        } 
        // launch the 2nd and 3rd containers.
        for (Container c : conts) {
            nm.nodeHeartbeat(attempt0.getAppAttemptId(), c.getId().getContainerId(), RUNNING);
            rm0.waitForState(nm, c.getId(), RMContainerState.RUNNING);
        }
        // Get the RMContainers for all of the live containers, to be used later
        // for metrics calculations and comparisons.
        Collection<RMContainer> rmContainers = rm0.scheduler.getSchedulerAppInfo(attempt0.getAppAttemptId()).getLiveContainers();
        // Allow metrics to accumulate.
        int sleepInterval = 1000;
        int cumulativeSleepTime = 0;
        while (((app0.getRMAppMetrics().getMemorySeconds()) <= 0) && (cumulativeSleepTime < 5000)) {
            Thread.sleep(sleepInterval);
            cumulativeSleepTime += sleepInterval;
        } 
        // Stop all non-AM containers
        for (Container c : conts) {
            if ((c.getId().getContainerId()) == 1)
                continue;

            nm.nodeHeartbeat(attempt0.getAppAttemptId(), c.getId().getContainerId(), COMPLETE);
            rm0.waitForState(nm, c.getId(), COMPLETED);
        }
        // After all other containers have completed, manually complete the master
        // container in order to trigger a save to the state store of the resource
        // usage metrics. This will cause the attempt to fail, and, since the max
        // attempt retries is 1, the app will also fail. This is intentional so
        // that all containers will complete prior to saving.
        ContainerId cId = ContainerId.newContainerId(attempt0.getAppAttemptId(), 1);
        nm.nodeHeartbeat(attempt0.getAppAttemptId(), cId.getContainerId(), COMPLETE);
        rm0.waitForState(nm, cId, COMPLETED);
        // Check that the container metrics match those from the app usage report.
        long memorySeconds = 0;
        long vcoreSeconds = 0;
        for (RMContainer c : rmContainers) {
            AggregateAppResourceUsage ru = calculateContainerResourceMetrics(c);
            memorySeconds += ru.getMemorySeconds();
            vcoreSeconds += ru.getVcoreSeconds();
        }
        RMAppMetrics metricsBefore = app0.getRMAppMetrics();
        Assert.assertEquals("Unexpected MemorySeconds value", memorySeconds, metricsBefore.getMemorySeconds());
        Assert.assertEquals("Unexpected VcoreSeconds value", vcoreSeconds, metricsBefore.getVcoreSeconds());
        // create new RM to represent RM restart. Load up the state store.
        MockRM rm1 = new MockRM(conf, memStore);
        start();
        RMApp app0After = getRMContext().getRMApps().get(app0.getApplicationId());
        // Compare container resource usage metrics from before and after restart.
        RMAppMetrics metricsAfter = app0After.getRMAppMetrics();
        Assert.assertEquals("Vcore seconds were not the same after RM Restart", metricsBefore.getVcoreSeconds(), metricsAfter.getVcoreSeconds());
        Assert.assertEquals("Memory seconds were not the same after RM Restart", metricsBefore.getMemorySeconds(), metricsAfter.getMemorySeconds());
        stop();
        close();
        stop();
        close();
    }

    @Test(timeout = 60000)
    public void testUsageAfterAMRestartWithMultipleContainers() throws Exception {
        amRestartTests(false);
    }

    @Test(timeout = 60000)
    public void testUsageAfterAMRestartKeepContainers() throws Exception {
        amRestartTests(true);
    }
}

