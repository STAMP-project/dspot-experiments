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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import CapacitySchedulerConfiguration.ROOT;
import QueueState.DRAINING;
import QueueState.RUNNING;
import QueueState.STOPPED;
import RMAppState.ACCEPTED;
import YarnConfiguration.RECOVERY_ENABLED;
import YarnConfiguration.RM_MAX_COMPLETED_APPLICATIONS;
import YarnConfiguration.RM_STORE;
import YarnConfiguration.RM_WORK_PRESERVING_RECOVERY_ENABLED;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.MemoryRMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;

import static CapacitySchedulerConfiguration.ROOT;


/**
 * Test Queue States.
 */
public class TestQueueState {
    private static final String Q1 = "q1";

    private static final String Q2 = "q2";

    private static final String Q3 = "q3";

    private static final String Q1_PATH = ((ROOT) + ".") + (TestQueueState.Q1);

    private static final String Q2_PATH = ((TestQueueState.Q1_PATH) + ".") + (TestQueueState.Q2);

    private static final String Q3_PATH = ((TestQueueState.Q1_PATH) + ".") + (TestQueueState.Q3);

    private CapacityScheduler cs;

    private YarnConfiguration conf;

    @Test(timeout = 15000)
    public void testQueueState() throws IOException {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setQueues(ROOT, new String[]{ TestQueueState.Q1 });
        csConf.setQueues(TestQueueState.Q1_PATH, new String[]{ TestQueueState.Q2 });
        csConf.setCapacity(TestQueueState.Q1_PATH, 100);
        csConf.setCapacity(TestQueueState.Q2_PATH, 100);
        conf = new YarnConfiguration(csConf);
        cs = new CapacityScheduler();
        RMContext rmContext = TestUtils.getMockRMContext();
        cs.setConf(conf);
        cs.setRMContext(rmContext);
        cs.init(conf);
        // by default, the state of both queues should be RUNNING
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueState.Q1).getState());
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueState.Q2).getState());
        // Change the state of Q1 to STOPPED, and re-initiate the CS
        csConf.setState(TestQueueState.Q1_PATH, STOPPED);
        conf = new YarnConfiguration(csConf);
        cs.reinitialize(conf, rmContext);
        // The state of Q1 and its child: Q2 should be STOPPED
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueState.Q1).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueState.Q2).getState());
        // Change the state of Q1 to RUNNING, and change the state of Q2 to STOPPED
        csConf.setState(TestQueueState.Q1_PATH, RUNNING);
        csConf.setState(TestQueueState.Q2_PATH, STOPPED);
        conf = new YarnConfiguration(csConf);
        // reinitialize the CS, the operation should be successful
        cs.reinitialize(conf, rmContext);
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueState.Q1).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueState.Q2).getState());
        // Change the state of Q1 to STOPPED, and change the state of Q2 to RUNNING
        csConf.setState(TestQueueState.Q1_PATH, STOPPED);
        csConf.setState(TestQueueState.Q2_PATH, RUNNING);
        conf = new YarnConfiguration(csConf);
        // reinitialize the CS, the operation should be failed.
        try {
            cs.reinitialize(conf, rmContext);
            Assert.fail("Should throw an Exception.");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause().getMessage().contains(("The parent queue:q1 state is STOPPED, " + "child queue:q2 state cannot be RUNNING.")));
        }
    }

    @Test(timeout = 15000)
    public void testQueueStateTransit() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setQueues(ROOT, new String[]{ TestQueueState.Q1 });
        csConf.setQueues(TestQueueState.Q1_PATH, new String[]{ TestQueueState.Q2, TestQueueState.Q3 });
        csConf.setCapacity(TestQueueState.Q1_PATH, 100);
        csConf.setCapacity(TestQueueState.Q2_PATH, 50);
        csConf.setCapacity(TestQueueState.Q3_PATH, 50);
        conf = new YarnConfiguration(csConf);
        cs = new CapacityScheduler();
        RMContext rmContext = TestUtils.getMockRMContext();
        cs.setConf(conf);
        cs.setRMContext(rmContext);
        cs.init(conf);
        // by default, the state of ALL queues should be RUNNING
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueState.Q1).getState());
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueState.Q2).getState());
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueState.Q3).getState());
        // submit an application to Q2
        ApplicationId appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        String userName = "testUser";
        cs.getQueue(TestQueueState.Q2).submitApplication(appId, userName, TestQueueState.Q2);
        FiCaSchedulerApp app = getMockApplication(appId, userName, Resources.createResource(4, 0));
        cs.getQueue(TestQueueState.Q2).submitApplicationAttempt(app, userName);
        // set Q2 state to stop and do reinitialize.
        csConf.setState(TestQueueState.Q2_PATH, STOPPED);
        conf = new YarnConfiguration(csConf);
        cs.reinitialize(conf, rmContext);
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueState.Q1).getState());
        Assert.assertEquals(DRAINING, cs.getQueue(TestQueueState.Q2).getState());
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueState.Q3).getState());
        // set Q1 state to stop and do reinitialize.
        csConf.setState(TestQueueState.Q1_PATH, STOPPED);
        conf = new YarnConfiguration(csConf);
        cs.reinitialize(conf, rmContext);
        Assert.assertEquals(DRAINING, cs.getQueue(TestQueueState.Q1).getState());
        Assert.assertEquals(DRAINING, cs.getQueue(TestQueueState.Q2).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueState.Q3).getState());
        // Active Q3, should fail
        csConf.setState(TestQueueState.Q3_PATH, RUNNING);
        conf = new YarnConfiguration(csConf);
        try {
            cs.reinitialize(conf, rmContext);
            Assert.fail("Should throw an Exception.");
        } catch (Exception ex) {
            // Do Nothing
        }
        // stop the app running in q2
        cs.getQueue(TestQueueState.Q2).finishApplicationAttempt(app, TestQueueState.Q2);
        cs.getQueue(TestQueueState.Q2).finishApplication(appId, userName);
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueState.Q1).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueState.Q2).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueState.Q3).getState());
    }

    @Test(timeout = 30000)
    public void testRecoverDrainingStateAfterRMRestart() throws Exception {
        // init conf
        CapacitySchedulerConfiguration newConf = new CapacitySchedulerConfiguration();
        newConf.setBoolean(RECOVERY_ENABLED, true);
        newConf.setBoolean(RM_WORK_PRESERVING_RECOVERY_ENABLED, false);
        newConf.set(RM_STORE, MemoryRMStateStore.class.getName());
        newConf.setInt(RM_MAX_COMPLETED_APPLICATIONS, 1);
        newConf.setQueues(ROOT, new String[]{ TestQueueState.Q1 });
        newConf.setQueues(TestQueueState.Q1_PATH, new String[]{ TestQueueState.Q2 });
        newConf.setCapacity(TestQueueState.Q1_PATH, 100);
        newConf.setCapacity(TestQueueState.Q2_PATH, 100);
        // init state store
        MemoryRMStateStore newMemStore = new MemoryRMStateStore();
        newMemStore.init(newConf);
        // init RM & NMs & Nodes
        MockRM rm = new MockRM(newConf, newMemStore);
        start();
        MockNM nm = rm.registerNode("h1:1234", 204800);
        // submit an app, AM is running on nm1
        RMApp app = rm.submitApp(1024, "appname", "appuser", null, TestQueueState.Q2);
        MockRM.launchAM(app, rm, nm);
        rm.waitForState(app.getApplicationId(), ACCEPTED);
        // update queue state to STOPPED
        newConf.setState(TestQueueState.Q1_PATH, STOPPED);
        CapacityScheduler capacityScheduler = ((CapacityScheduler) (getRMContext().getScheduler()));
        capacityScheduler.reinitialize(newConf, getRMContext());
        // current queue state should be DRAINING
        Assert.assertEquals(DRAINING, capacityScheduler.getQueue(TestQueueState.Q2).getState());
        Assert.assertEquals(DRAINING, capacityScheduler.getQueue(TestQueueState.Q1).getState());
        // RM restart
        rm = new MockRM(newConf, newMemStore);
        start();
        rm.registerNode("h1:1234", 204800);
        // queue state should be DRAINING after app recovered
        rm.waitForState(app.getApplicationId(), ACCEPTED);
        capacityScheduler = ((CapacityScheduler) (getRMContext().getScheduler()));
        Assert.assertEquals(DRAINING, capacityScheduler.getQueue(TestQueueState.Q2).getState());
        Assert.assertEquals(DRAINING, capacityScheduler.getQueue(TestQueueState.Q1).getState());
        // close rm
        close();
    }
}

