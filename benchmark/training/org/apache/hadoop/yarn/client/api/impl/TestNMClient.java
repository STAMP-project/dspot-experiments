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
package org.apache.hadoop.yarn.client.api.impl;


import FinalApplicationStatus.SUCCEEDED;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMTokenCache;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.nodemanager.ContainerStateTransitionListener;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerImpl;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerState;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNMClient {
    Configuration conf = null;

    MiniYARNCluster yarnCluster = null;

    YarnClientImpl yarnClient = null;

    AMRMClientImpl<ContainerRequest> rmClient = null;

    NMClientImpl nmClient = null;

    List<NodeReport> nodeReports = null;

    ApplicationAttemptId attemptId = null;

    int nodeCount = 3;

    NMTokenCache nmTokenCache = null;

    /**
     * Container State transition listener to track the number of times
     * a container has transitioned into a state.
     */
    public static class DebugSumContainerStateListener implements ContainerStateTransitionListener {
        private static final Logger LOG = LoggerFactory.getLogger(TestNMClient.DebugSumContainerStateListener.class);

        private static final Map<ContainerId, Map<ContainerState, Long>> TRANSITION_COUNTER = new HashMap<>();

        public void init(Context context) {
        }

        public void preTransition(ContainerImpl op, ContainerState beforeState, ContainerEvent eventToBeProcessed) {
        }

        public void postTransition(ContainerImpl op, ContainerState beforeState, ContainerState afterState, ContainerEvent processedEvent) {
            synchronized(TestNMClient.DebugSumContainerStateListener.TRANSITION_COUNTER) {
                if (beforeState != afterState) {
                    ContainerId id = op.getContainerId();
                    TestNMClient.DebugSumContainerStateListener.TRANSITION_COUNTER.putIfAbsent(id, new HashMap());
                    long sum = TestNMClient.DebugSumContainerStateListener.TRANSITION_COUNTER.get(id).compute(afterState, ( state, count) -> count == null ? 1 : count + 1);
                    TestNMClient.DebugSumContainerStateListener.LOG.info(((((((("***** " + id) + " Transition from ") + beforeState) + " to ") + afterState) + "sum:") + sum));
                }
            }
        }

        /**
         * Get the current number of state transitions.
         * This is useful to check, if an event has occurred in unit tests.
         *
         * @param id
         * 		Container id to check
         * @param state
         * 		Return the overall number of transitions to this state
         * @return Number of transitions to the state specified
         */
        static long getTransitionCounter(ContainerId id, ContainerState state) {
            Long ret = TestNMClient.DebugSumContainerStateListener.TRANSITION_COUNTER.getOrDefault(id, new HashMap()).get(state);
            return ret != null ? ret : 0;
        }
    }

    @Test(timeout = 180000)
    public void testNMClientNoCleanupOnStop() throws IOException, YarnException {
        rmClient.registerApplicationMaster("Host", 10000, "");
        testContainerManagement(nmClient, allocateContainers(rmClient, 5));
        rmClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        // don't stop the running containers
        stopNmClient(false);
        Assert.assertFalse(nmClient.startedContainers.isEmpty());
        // now cleanup
        nmClient.cleanupRunningContainers();
        Assert.assertEquals(0, nmClient.startedContainers.size());
    }

    @Test(timeout = 200000)
    public void testNMClient() throws IOException, YarnException {
        rmClient.registerApplicationMaster("Host", 10000, "");
        testContainerManagement(nmClient, allocateContainers(rmClient, 5));
        rmClient.unregisterApplicationMaster(SUCCEEDED, null, null);
        // stop the running containers on close
        Assert.assertFalse(nmClient.startedContainers.isEmpty());
        nmClient.cleanupRunningContainersOnStop(true);
        Assert.assertTrue(nmClient.getCleanupRunningContainers().get());
        nmClient.stop();
    }
}

