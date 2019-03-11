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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;


import InvalidResourceRequestException.InvalidResourceType.GREATER_THEN_MAX_ALLOCATION;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.InvalidResourceRequestException;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Application master service using Fair scheduler.
 */
public class TestApplicationMasterServiceWithFS {
    private static final Logger LOG = LoggerFactory.getLogger(TestApplicationMasterServiceWithFS.class);

    private static final int GB = 1024;

    private static final int MEMORY_ALLOCATION = 3 * (TestApplicationMasterServiceWithFS.GB);

    private static final String TEST_FOLDER = "test-queues";

    private AllocateResponse allocateResponse;

    private static YarnConfiguration configuration;

    @Test(timeout = 3000000)
    public void testQueueLevelContainerAllocationFail() throws Exception {
        MockRM rm = new MockRM(TestApplicationMasterServiceWithFS.configuration);
        start();
        // Register node1
        MockNM nm1 = rm.registerNode("127.0.0.1:1234", (6 * (TestApplicationMasterServiceWithFS.GB)));
        // Submit an application
        RMApp app1 = rm.submitApp((2 * (TestApplicationMasterServiceWithFS.GB)), "queueA");
        // kick the scheduling
        nm1.nodeHeartbeat(true);
        RMAppAttempt attempt1 = app1.getCurrentAppAttempt();
        MockAM am1 = rm.sendAMLaunched(attempt1.getAppAttemptId());
        am1.registerAppAttempt();
        am1.addRequests(new String[]{ "127.0.0.1" }, TestApplicationMasterServiceWithFS.MEMORY_ALLOCATION, 1, 1);
        try {
            allocateResponse = am1.schedule();// send the request

            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof InvalidResourceRequestException));
            Assert.assertEquals(GREATER_THEN_MAX_ALLOCATION, getInvalidResourceType());
        } finally {
            stop();
        }
    }

    @Test(timeout = 3000000)
    public void testQueueLevelContainerAllocationSuccess() throws Exception {
        testFairSchedulerContainerAllocationSuccess("queueB");
    }

    @Test(timeout = 3000000)
    public void testSchedulerLevelContainerAllocationSuccess() throws Exception {
        testFairSchedulerContainerAllocationSuccess("queueC");
    }
}

