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
package org.apache.hadoop.yarn.server.metrics;


import ContainerUpdateType.PROMOTE_EXECUTION_TYPE;
import ExecutionType.GUARANTEED;
import RequestType.Guaranteed;
import RequestType.Promote;
import ResourceRequest.ANY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.UpdateContainerRequest;
import org.apache.hadoop.yarn.api.records.UpdatedContainer;
import org.apache.hadoop.yarn.exceptions.ApplicationMasterNotRegisteredException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.AMRMClientRelayer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for AMRMClientRelayer.
 */
public class TestAMRMClientRelayerMetrics {
    /**
     * Mock AMS for easier testing and mocking of request/responses.
     */
    public static class MockApplicationMasterService implements ApplicationMasterProtocol {
        private boolean failover = false;

        private boolean exception = false;

        private List<ResourceRequest> lastAsk;

        private List<ContainerId> lastRelease;

        private List<UpdateContainerRequest> lastUpdates;

        private List<String> lastBlacklistAdditions;

        private List<String> lastBlacklistRemovals;

        private AllocateResponse response = AllocateResponse.newInstance(0, null, null, new ArrayList<org.apache.hadoop.yarn.api.records.NodeReport>(), Resource.newInstance(0, 0), null, 0, null, null);

        @Override
        public RegisterApplicationMasterResponse registerApplicationMaster(RegisterApplicationMasterRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public FinishApplicationMasterResponse finishApplicationMaster(FinishApplicationMasterRequest request) throws IOException, YarnException {
            if (this.failover) {
                this.failover = false;
                throw new ApplicationMasterNotRegisteredException("Mock RM restarted");
            }
            return null;
        }

        @Override
        public AllocateResponse allocate(AllocateRequest request) throws IOException, YarnException {
            if (this.failover) {
                this.failover = false;
                throw new ApplicationMasterNotRegisteredException("Mock RM restarted");
            }
            if (this.exception) {
                this.exception = false;
                throw new YarnException("Mock RM encountered exception");
            }
            this.lastAsk = request.getAskList();
            this.lastRelease = request.getReleaseList();
            this.lastUpdates = request.getUpdateRequests();
            this.lastBlacklistAdditions = request.getResourceBlacklistRequest().getBlacklistAdditions();
            this.lastBlacklistRemovals = request.getResourceBlacklistRequest().getBlacklistRemovals();
            return response;
        }

        public void setFailoverFlag() {
            this.failover = true;
        }
    }

    private Configuration conf;

    private TestAMRMClientRelayerMetrics.MockApplicationMasterService mockAMS;

    private String homeID = "home";

    private AMRMClientRelayer homeRelayer;

    private String uamID = "uam";

    private AMRMClientRelayer uamRelayer;

    private List<ResourceRequest> asks = new ArrayList<>();

    private List<ContainerId> releases = new ArrayList<>();

    private List<UpdateContainerRequest> updates = new ArrayList<>();

    private List<String> blacklistAdditions = new ArrayList<>();

    private List<String> blacklistRemoval = new ArrayList<>();

    @Test
    public void testGPending() throws IOException, YarnException {
        // Ask for two containers, one with location preference
        this.asks.add(createResourceRequest(0, "node", 2048, 1, 1, GUARANTEED, 1));
        this.asks.add(createResourceRequest(0, "rack", 2048, 1, 1, GUARANTEED, 1));
        this.asks.add(createResourceRequest(0, ANY, 2048, 1, 1, GUARANTEED, 2));
        this.homeRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(0, AMRMClientRelayerMetrics.getInstance().getPendingMetric(uamID, Guaranteed).value());
        // Ask from the uam
        this.uamRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(uamID, Guaranteed).value());
        // Update the any to ask for an extra container
        this.asks.get(2).setNumContainers(3);
        this.homeRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(3, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(uamID, Guaranteed).value());
        // Update the any to ask to pretend a container was allocated
        this.asks.get(2).setNumContainers(2);
        this.homeRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(uamID, Guaranteed).value());
    }

    @Test
    public void testPromotePending() throws IOException, YarnException {
        // Ask to promote 3 containers
        this.updates.add(TestAMRMClientRelayerMetrics.createPromote(1));
        this.updates.add(TestAMRMClientRelayerMetrics.createPromote(2));
        this.updates.add(TestAMRMClientRelayerMetrics.createPromote(3));
        this.homeRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(3, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Promote).value());
        // Demote 2 containers, one of which is pending promote
        this.updates.remove(TestAMRMClientRelayerMetrics.createPromote(3));
        this.updates.add(TestAMRMClientRelayerMetrics.createDemote(3));
        this.updates.add(TestAMRMClientRelayerMetrics.createDemote(4));
        this.homeRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Promote).value());
        // Let the RM respond with two successful promotions, one of which
        // was pending promote
        List<UpdatedContainer> updated = new ArrayList<>();
        updated.add(UpdatedContainer.newInstance(PROMOTE_EXECUTION_TYPE, Container.newInstance(TestAMRMClientRelayerMetrics.createContainerId(2), null, null, null, null, null)));
        updated.add(UpdatedContainer.newInstance(PROMOTE_EXECUTION_TYPE, Container.newInstance(TestAMRMClientRelayerMetrics.createContainerId(5), null, null, null, null, null)));
        this.mockAMS.response.setUpdatedContainers(updated);
        this.homeRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(1, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Promote).value());
        // Remove the promoted container and clean up response
        this.mockAMS.response.getUpdatedContainers().clear();
        this.updates.remove(TestAMRMClientRelayerMetrics.createPromote(2));
        // Let the RM respond with two completed containers, one of which was
        // pending promote
        List<ContainerStatus> completed = new ArrayList<>();
        completed.add(ContainerStatus.newInstance(TestAMRMClientRelayerMetrics.createContainerId(1), null, "", 0));
        completed.add(ContainerStatus.newInstance(TestAMRMClientRelayerMetrics.createContainerId(6), null, "", 0));
        this.mockAMS.response.setCompletedContainersStatuses(completed);
        this.homeRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(0, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Promote).value());
    }

    @Test
    public void testCleanUpOnFinish() throws IOException, YarnException {
        // Ask for two containers, one with location preference
        this.asks.add(createResourceRequest(0, "node", 2048, 1, 1, GUARANTEED, 1));
        this.asks.add(createResourceRequest(0, "rack", 2048, 1, 1, GUARANTEED, 1));
        this.asks.add(createResourceRequest(0, ANY, 2048, 1, 1, GUARANTEED, 2));
        // Ask to promote 3 containers
        this.updates.add(TestAMRMClientRelayerMetrics.createPromote(1));
        this.updates.add(TestAMRMClientRelayerMetrics.createPromote(2));
        this.updates.add(TestAMRMClientRelayerMetrics.createPromote(3));
        // Run the allocate call to start tracking pending
        this.homeRelayer.allocate(getAllocateRequest());
        // After finish, the metrics should reset to zero
        this.homeRelayer.shutdown();
        Assert.assertEquals(0, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(0, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Promote).value());
    }

    @Test
    public void testFailover() throws IOException, YarnException {
        // Ask for two containers, one with location preference
        this.asks.add(createResourceRequest(0, "node", 2048, 1, 1, GUARANTEED, 1));
        this.asks.add(createResourceRequest(0, "rack", 2048, 1, 1, GUARANTEED, 1));
        this.asks.add(createResourceRequest(0, ANY, 2048, 1, 1, GUARANTEED, 2));
        long previousSuccess = AMRMClientRelayerMetrics.getInstance().getHeartbeatSuccessMetric(homeID).value();
        long previousFailover = AMRMClientRelayerMetrics.getInstance().getRMMasterSlaveSwitchMetric(homeID).value();
        // Set failover to trigger
        mockAMS.failover = true;
        this.homeRelayer.allocate(getAllocateRequest());
        // The failover metric should be incremented
        Assert.assertEquals((++previousFailover), AMRMClientRelayerMetrics.getInstance().getRMMasterSlaveSwitchMetric(homeID).value());
        // The success metric should be incremented once
        Assert.assertEquals((++previousSuccess), AMRMClientRelayerMetrics.getInstance().getHeartbeatSuccessMetric(homeID).value());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(0, AMRMClientRelayerMetrics.getInstance().getPendingMetric(uamID, Guaranteed).value());
        // Ask from the uam
        this.uamRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(uamID, Guaranteed).value());
        // Update the any to ask for an extra container
        this.asks.get(2).setNumContainers(3);
        mockAMS.failover = true;
        this.homeRelayer.allocate(getAllocateRequest());
        // The failover metric should be incremented
        Assert.assertEquals((++previousFailover), AMRMClientRelayerMetrics.getInstance().getRMMasterSlaveSwitchMetric(homeID).value());
        // The success metric should be incremented once
        Assert.assertEquals((++previousSuccess), AMRMClientRelayerMetrics.getInstance().getHeartbeatSuccessMetric(homeID).value());
        Assert.assertEquals(3, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(uamID, Guaranteed).value());
        // Update the any to ask to pretend a container was allocated
        this.asks.get(2).setNumContainers(2);
        mockAMS.failover = true;
        this.homeRelayer.allocate(getAllocateRequest());
        // The failover metric should be incremented
        Assert.assertEquals((++previousFailover), AMRMClientRelayerMetrics.getInstance().getRMMasterSlaveSwitchMetric(homeID).value());
        // The success metric should be incremented once
        Assert.assertEquals((++previousSuccess), AMRMClientRelayerMetrics.getInstance().getHeartbeatSuccessMetric(homeID).value());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(2, AMRMClientRelayerMetrics.getInstance().getPendingMetric(uamID, Guaranteed).value());
        long previousFailure = AMRMClientRelayerMetrics.getInstance().getHeartbeatFailureMetric(homeID).value();
        mockAMS.exception = true;
        try {
            this.homeRelayer.allocate(getAllocateRequest());
            Assert.fail();
        } catch (YarnException e) {
        }
        // The failover metric should not be incremented
        Assert.assertEquals(previousFailover, AMRMClientRelayerMetrics.getInstance().getRMMasterSlaveSwitchMetric(homeID).value());
        // The success metric should not be incremented
        Assert.assertEquals(previousSuccess, AMRMClientRelayerMetrics.getInstance().getHeartbeatSuccessMetric(homeID).value());
        // The failure metric should be incremented
        Assert.assertEquals((++previousFailure), AMRMClientRelayerMetrics.getInstance().getHeartbeatFailureMetric(homeID).value());
        mockAMS.failover = true;
        mockAMS.exception = true;
        try {
            this.homeRelayer.allocate(getAllocateRequest());
            Assert.fail();
        } catch (YarnException e) {
        }
        // The failover metric should be incremented
        Assert.assertEquals((++previousFailover), AMRMClientRelayerMetrics.getInstance().getRMMasterSlaveSwitchMetric(homeID).value());
        // The success metric should not be incremented
        Assert.assertEquals(previousSuccess, AMRMClientRelayerMetrics.getInstance().getHeartbeatSuccessMetric(homeID).value());
        // The failure metric should be incremented
        Assert.assertEquals((++previousFailure), AMRMClientRelayerMetrics.getInstance().getHeartbeatFailureMetric(homeID).value());
    }

    @Test
    public void testNewEmptyRequest() throws IOException, YarnException {
        // Ask for zero containers
        this.asks.add(createResourceRequest(1, ANY, 2048, 1, 1, GUARANTEED, 0));
        this.homeRelayer.allocate(getAllocateRequest());
        Assert.assertEquals(0, AMRMClientRelayerMetrics.getInstance().getPendingMetric(homeID, Guaranteed).value());
        Assert.assertEquals(0, AMRMClientRelayerMetrics.getInstance().getPendingMetric(uamID, Guaranteed).value());
    }
}

