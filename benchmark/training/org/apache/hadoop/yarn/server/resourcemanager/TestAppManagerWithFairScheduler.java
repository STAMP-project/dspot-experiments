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


import YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB;
import org.apache.hadoop.yarn.MockApps;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.InvalidResourceRequestException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.resourcemanager.placement.ApplicationPlacementContext;
import org.apache.hadoop.yarn.server.resourcemanager.placement.PlacementManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Testing applications being retired from RM with fair scheduler.
 */
public class TestAppManagerWithFairScheduler extends AppManagerTestBase {
    private static final String TEST_FOLDER = "test-queues";

    private static YarnConfiguration conf = new YarnConfiguration();

    @Test
    public void testQueueSubmitWithHighQueueContainerSize() throws YarnException {
        ApplicationId appId = MockApps.newAppID(1);
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        Resource resource = Resources.createResource(DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB);
        ApplicationSubmissionContext asContext = recordFactory.newRecordInstance(ApplicationSubmissionContext.class);
        asContext.setApplicationId(appId);
        asContext.setResource(resource);
        asContext.setPriority(Priority.newInstance(0));
        asContext.setAMContainerSpec(TestAppManagerWithFairScheduler.mockContainerLaunchContext(recordFactory));
        asContext.setQueue("queueA");
        QueueInfo mockDefaultQueueInfo = Mockito.mock(QueueInfo.class);
        // Setup a PlacementManager returns a new queue
        PlacementManager placementMgr = Mockito.mock(PlacementManager.class);
        Mockito.doAnswer(new Answer<ApplicationPlacementContext>() {
            @Override
            public ApplicationPlacementContext answer(InvocationOnMock invocation) throws Throwable {
                return new ApplicationPlacementContext("queueA");
            }
        }).when(placementMgr).placeApplication(ArgumentMatchers.any(ApplicationSubmissionContext.class), ArgumentMatchers.matches("test1"));
        Mockito.doAnswer(new Answer<ApplicationPlacementContext>() {
            @Override
            public ApplicationPlacementContext answer(InvocationOnMock invocation) throws Throwable {
                return new ApplicationPlacementContext("queueB");
            }
        }).when(placementMgr).placeApplication(ArgumentMatchers.any(ApplicationSubmissionContext.class), ArgumentMatchers.matches("test2"));
        MockRM newMockRM = new MockRM(TestAppManagerWithFairScheduler.conf);
        RMContext newMockRMContext = getRMContext();
        newMockRMContext.setQueuePlacementManager(placementMgr);
        ApplicationMasterService masterService = new ApplicationMasterService(newMockRMContext, newMockRMContext.getScheduler());
        AppManagerTestBase.TestRMAppManager newAppMonitor = new AppManagerTestBase.TestRMAppManager(newMockRMContext, new ClientToAMTokenSecretManagerInRM(), newMockRMContext.getScheduler(), masterService, new org.apache.hadoop.yarn.server.security.ApplicationACLsManager(TestAppManagerWithFairScheduler.conf), TestAppManagerWithFairScheduler.conf);
        // only user test has permission to submit to 'test' queue
        try {
            newAppMonitor.submitApplication(asContext, "test1");
            Assert.fail("Test should fail on too high allocation!");
        } catch (InvalidResourceRequestException e) {
            Assert.assertEquals(GREATER_THEN_MAX_ALLOCATION, e.getInvalidResourceType());
        }
        // Should not throw exception
        newAppMonitor.submitApplication(asContext, "test2");
    }
}

