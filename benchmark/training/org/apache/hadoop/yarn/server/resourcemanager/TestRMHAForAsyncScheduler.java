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


import HAServiceProtocol.HAServiceState;
import HAServiceProtocol.RequestSource;
import HAServiceProtocol.StateChangeRequestInfo;
import RMAppAttemptState.LAUNCHED;
import java.util.Arrays;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.TestCapacitySchedulerAsyncScheduling;
import org.junit.Assert;
import org.junit.Test;


public class TestRMHAForAsyncScheduler extends RMHATestBase {
    private TestCapacitySchedulerAsyncScheduling.NMHeartbeatThread nmHeartbeatThread = null;

    @Test(timeout = 60000)
    public void testAsyncScheduleThreadStateAfterRMHATransit() throws Exception {
        // start two RMs, and transit rm1 to active, rm2 to standby
        startRMs();
        // register NM
        MockNM nm = RMHATestBase.rm1.registerNode("192.1.1.1:1234", 8192, 8);
        // submit app1 and check
        RMApp app1 = submitAppAndCheckLaunched(RMHATestBase.rm1);
        keepNMHeartbeat(Arrays.asList(nm), 1000);
        // failover RM1 to RM2
        explicitFailover();
        checkAsyncSchedulerThreads(Thread.currentThread());
        pauseNMHeartbeat();
        // register NM, kill app1
        nm = RMHATestBase.rm2.registerNode("192.1.1.1:1234", 8192, 8);
        keepNMHeartbeat(Arrays.asList(nm), 1000);
        RMHATestBase.rm2.waitForState(app1.getCurrentAppAttempt().getAppAttemptId(), LAUNCHED);
        RMHATestBase.rm2.killApp(app1.getApplicationId());
        // submit app3 and check
        RMApp app2 = submitAppAndCheckLaunched(RMHATestBase.rm2);
        pauseNMHeartbeat();
        // failover RM2 to RM1
        HAServiceProtocol.StateChangeRequestInfo requestInfo = new HAServiceProtocol.StateChangeRequestInfo(RequestSource.REQUEST_BY_USER);
        TestRMHAForAsyncScheduler.rm2.adminService.transitionToStandby(requestInfo);
        TestRMHAForAsyncScheduler.rm1.adminService.transitionToActive(requestInfo);
        Assert.assertTrue(((getRMContext().getHAServiceState()) == (HAServiceState.STANDBY)));
        Assert.assertTrue(((getRMContext().getHAServiceState()) == (HAServiceState.ACTIVE)));
        // check async schedule threads
        checkAsyncSchedulerThreads(Thread.currentThread());
        // register NM, kill app2
        nm = RMHATestBase.rm1.registerNode("192.1.1.1:1234", 8192, 8);
        keepNMHeartbeat(Arrays.asList(nm), 1000);
        RMHATestBase.rm1.waitForState(app2.getCurrentAppAttempt().getAppAttemptId(), LAUNCHED);
        RMHATestBase.rm1.killApp(app2.getApplicationId());
        // submit app3 and check
        submitAppAndCheckLaunched(RMHATestBase.rm1);
        pauseNMHeartbeat();
        stop();
        stop();
    }
}

