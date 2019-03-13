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


import RMAppState.ACCEPTED;
import RMAppState.KILLED;
import RMAppState.NEW;
import RMAppState.RUNNING;
import YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.security.QueueACLsManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMDelegationTokenSecretManager;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestKillApplicationWithRMHA extends RMHATestBase {
    public static final Logger LOG = LoggerFactory.getLogger(TestKillApplicationWithRMHA.class);

    @Test(timeout = 20000)
    public void testKillAppWhenFailoverHappensAtNewState() throws Exception {
        // create a customized RMAppManager
        // During the process of Application submission,
        // the RMAppState will always be NEW.
        // The ApplicationState will not be saved in RMStateStore.
        startRMsWithCustomizedRMAppManager();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // Submit the application
        RMApp app0 = RMHATestBase.rm1.submitApp(200, "", UserGroupInformation.getCurrentUser().getShortUserName(), null, false, null, configuration.getInt(RM_AM_MAX_ATTEMPTS, DEFAULT_RM_AM_MAX_ATTEMPTS), null, null, false, false);
        // failover and kill application
        // When FailOver happens, the state of this application is NEW,
        // and ApplicationState is not saved in RMStateStore. The active RM
        // can not load the ApplicationState of this application.
        // Expected to get ApplicationNotFoundException
        // when receives the KillApplicationRequest
        try {
            failOverAndKillApp(app0.getApplicationId(), NEW);
            Assert.fail("Should get an exception here");
        } catch (ApplicationNotFoundException ex) {
            Assert.assertTrue(ex.getMessage().contains(("Trying to kill an absent application " + (app0.getApplicationId()))));
        }
    }

    @Test(timeout = 20000)
    public void testKillAppWhenFailoverHappensAtRunningState() throws Exception {
        startRMs();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // create app and launch the AM
        RMApp app0 = RMHATestBase.rm1.submitApp(200);
        MockAM am0 = launchAM(app0, RMHATestBase.rm1, nm1);
        // failover and kill application
        // The application is at RUNNING State when failOver happens.
        // Since RMStateStore has already saved ApplicationState, the active RM
        // will load the ApplicationState. After that, the application will be at
        // ACCEPTED State. Because the application is not at Final State,
        // KillApplicationResponse.getIsKillCompleted is expected to return false.
        failOverAndKillApp(app0.getApplicationId(), am0.getApplicationAttemptId(), RUNNING, RMAppAttemptState.RUNNING, ACCEPTED);
    }

    @Test(timeout = 20000)
    public void testKillAppWhenFailoverHappensAtFinalState() throws Exception {
        startRMs();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // create app and launch the AM
        RMApp app0 = RMHATestBase.rm1.submitApp(200);
        MockAM am0 = launchAM(app0, RMHATestBase.rm1, nm1);
        // kill the app.
        RMHATestBase.rm1.killApp(app0.getApplicationId());
        RMHATestBase.rm1.waitForState(app0.getApplicationId(), KILLED);
        RMHATestBase.rm1.waitForState(am0.getApplicationAttemptId(), RMAppAttemptState.KILLED);
        // failover and kill application
        // The application is at Killed State and RMStateStore has already
        // saved this applicationState. After failover happens, the current
        // active RM will load the ApplicationState whose RMAppState is killed.
        // Because this application is at Final State,
        // KillApplicationResponse.getIsKillCompleted is expected to return true.
        failOverAndKillApp(app0.getApplicationId(), am0.getApplicationAttemptId(), KILLED, RMAppAttemptState.KILLED, KILLED);
    }

    @Test(timeout = 20000)
    public void testKillAppWhenFailOverHappensDuringApplicationKill() throws Exception {
        // create a customized ClientRMService
        // When receives the killApplicationRequest, simply return the response
        // and make sure the application will not be KILLED State
        startRMsWithCustomizedClientRMService();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // create app and launch the AM
        RMApp app0 = RMHATestBase.rm1.submitApp(200);
        MockAM am0 = launchAM(app0, RMHATestBase.rm1, nm1);
        // ensure that the app is in running state
        Assert.assertEquals(app0.getState(), RUNNING);
        // kill the app.
        RMHATestBase.rm1.killApp(app0.getApplicationId());
        // failover happens before this application goes to final state.
        // The RMAppState that will be loaded by the active rm
        // should be ACCEPTED.
        failOverAndKillApp(app0.getApplicationId(), am0.getApplicationAttemptId(), RUNNING, RMAppAttemptState.RUNNING, ACCEPTED);
    }

    private static class MyClientRMService extends ClientRMService {
        private RMContext rmContext;

        public MyClientRMService(RMContext rmContext, YarnScheduler scheduler, RMAppManager rmAppManager, ApplicationACLsManager applicationACLsManager, QueueACLsManager queueACLsManager, RMDelegationTokenSecretManager rmDTSecretManager) {
            super(rmContext, scheduler, rmAppManager, applicationACLsManager, queueACLsManager, rmDTSecretManager);
            this.rmContext = rmContext;
        }

        @Override
        protected void serviceStart() {
            // override to not start rpc handler
        }

        @Override
        protected void serviceStop() {
            // don't do anything
        }

        @Override
        public KillApplicationResponse forceKillApplication(KillApplicationRequest request) throws YarnException {
            ApplicationId applicationId = request.getApplicationId();
            RMApp application = this.rmContext.getRMApps().get(applicationId);
            if (application.isAppFinalStateStored()) {
                return KillApplicationResponse.newInstance(true);
            } else {
                return KillApplicationResponse.newInstance(false);
            }
        }
    }
}

