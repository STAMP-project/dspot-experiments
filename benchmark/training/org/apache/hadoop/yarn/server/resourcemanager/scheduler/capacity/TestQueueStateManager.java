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
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueStateManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;

import static CapacitySchedulerConfiguration.ROOT;


/**
 * Test QueueStateManager.
 */
public class TestQueueStateManager {
    private static final String Q1 = "q1";

    private static final String Q2 = "q2";

    private static final String Q3 = "q3";

    private static final String Q1_PATH = ((ROOT) + ".") + (TestQueueStateManager.Q1);

    private static final String Q2_PATH = ((TestQueueStateManager.Q1_PATH) + ".") + (TestQueueStateManager.Q2);

    private static final String Q3_PATH = ((TestQueueStateManager.Q1_PATH) + ".") + (TestQueueStateManager.Q3);

    private CapacityScheduler cs;

    private YarnConfiguration conf;

    @Test
    public void testQueueStateManager() throws AccessControlException, YarnException {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setQueues(ROOT, new String[]{ TestQueueStateManager.Q1 });
        csConf.setQueues(TestQueueStateManager.Q1_PATH, new String[]{ TestQueueStateManager.Q2, TestQueueStateManager.Q3 });
        csConf.setCapacity(TestQueueStateManager.Q1_PATH, 100);
        csConf.setCapacity(TestQueueStateManager.Q2_PATH, 50);
        csConf.setCapacity(TestQueueStateManager.Q3_PATH, 50);
        conf = new YarnConfiguration(csConf);
        cs = new CapacityScheduler();
        RMContext rmContext = TestUtils.getMockRMContext();
        cs.setConf(conf);
        cs.setRMContext(rmContext);
        cs.init(conf);
        @SuppressWarnings("rawtypes")
        QueueStateManager stateManager = cs.getCapacitySchedulerQueueManager().getQueueStateManager();
        // by default, the state of both queues should be RUNNING
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueStateManager.Q1).getState());
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueStateManager.Q2).getState());
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueStateManager.Q3).getState());
        // Stop Q2, and verify that Q2 transmits to STOPPED STATE
        stateManager.stopQueue(TestQueueStateManager.Q2);
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueStateManager.Q2).getState());
        // Stop Q1, and verify that Q1, as well as its child: Q3,
        // transmits to STOPPED STATE
        stateManager.stopQueue(TestQueueStateManager.Q1);
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueStateManager.Q1).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueStateManager.Q3).getState());
        Assert.assertTrue(stateManager.canDelete(TestQueueStateManager.Q1));
        Assert.assertTrue(stateManager.canDelete(TestQueueStateManager.Q2));
        Assert.assertTrue(stateManager.canDelete(TestQueueStateManager.Q3));
        // Active Q2, it will fail.
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueStateManager.Q2).getState());
        // Now active Q1
        stateManager.activateQueue(TestQueueStateManager.Q1);
        // Q1 should be in RUNNING state. Its children: Q2 and Q3
        // should still be in STOPPED state.
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueStateManager.Q1).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueStateManager.Q2).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueStateManager.Q3).getState());
        // Now active Q2 and Q3
        stateManager.activateQueue(TestQueueStateManager.Q2);
        stateManager.activateQueue(TestQueueStateManager.Q3);
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueStateManager.Q2).getState());
        Assert.assertEquals(RUNNING, cs.getQueue(TestQueueStateManager.Q3).getState());
        Assert.assertFalse(stateManager.canDelete(TestQueueStateManager.Q1));
        Assert.assertFalse(stateManager.canDelete(TestQueueStateManager.Q2));
        Assert.assertFalse(stateManager.canDelete(TestQueueStateManager.Q3));
        ApplicationId appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        String userName = "testUser";
        cs.getQueue(TestQueueStateManager.Q2).submitApplication(appId, userName, TestQueueStateManager.Q2);
        FiCaSchedulerApp app = getMockApplication(appId, userName, Resources.createResource(4, 0));
        cs.getQueue(TestQueueStateManager.Q2).submitApplicationAttempt(app, userName);
        stateManager.stopQueue(TestQueueStateManager.Q1);
        Assert.assertEquals(DRAINING, cs.getQueue(TestQueueStateManager.Q1).getState());
        Assert.assertEquals(DRAINING, cs.getQueue(TestQueueStateManager.Q2).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueStateManager.Q3).getState());
        cs.getQueue(TestQueueStateManager.Q2).finishApplicationAttempt(app, TestQueueStateManager.Q2);
        cs.getQueue(TestQueueStateManager.Q2).finishApplication(appId, userName);
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueStateManager.Q1).getState());
        Assert.assertEquals(STOPPED, cs.getQueue(TestQueueStateManager.Q2).getState());
    }
}

