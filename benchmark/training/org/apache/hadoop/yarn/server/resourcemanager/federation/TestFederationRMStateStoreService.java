/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.federation;


import HAServiceProtocol.RequestSource;
import HAServiceProtocol.StateChangeRequestInfo;
import SubClusterState.SC_NEW;
import SubClusterState.SC_RUNNING;
import SubClusterState.SC_UNREGISTERED;
import YarnConfiguration.FEDERATION_ENABLED;
import YarnConfiguration.RM_CLUSTER_ID;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONUnmarshaller;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.federation.store.FederationStateStore;
import org.apache.hadoop.yarn.server.federation.store.records.GetSubClusterInfoRequest;
import org.apache.hadoop.yarn.server.federation.store.records.GetSubClusterInfoResponse;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterDeregisterRequest;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterId;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for FederationStateStoreService.
 */
public class TestFederationRMStateStoreService {
    private final StateChangeRequestInfo requestInfo = new org.apache.hadoop.ha.HAServiceProtocol.StateChangeRequestInfo(RequestSource.REQUEST_BY_USER);

    private final SubClusterId subClusterId = SubClusterId.newInstance("SC-1");

    private final GetSubClusterInfoRequest request = GetSubClusterInfoRequest.newInstance(subClusterId);

    private Configuration conf;

    private FederationStateStore stateStore;

    private long lastHearbeatTS = 0;

    private JSONJAXBContext jc;

    private JSONUnmarshaller unmarshaller;

    @Test
    public void testFederationStateStoreService() throws Exception {
        conf.setBoolean(FEDERATION_ENABLED, true);
        conf.set(RM_CLUSTER_ID, subClusterId.getId());
        final MockRM rm = new MockRM(conf);
        // Initially there should be no entry for the sub-cluster
        rm.init(conf);
        stateStore = getFederationStateStoreService().getStateStoreClient();
        GetSubClusterInfoResponse response = stateStore.getSubCluster(request);
        Assert.assertNull(response);
        // Validate if sub-cluster is registered
        start();
        String capability = checkSubClusterInfo(SC_NEW);
        Assert.assertTrue(capability.isEmpty());
        // Heartbeat to see if sub-cluster transitions to running
        FederationStateStoreHeartbeat storeHeartbeat = getFederationStateStoreService().getStateStoreHeartbeatThread();
        storeHeartbeat.run();
        capability = checkSubClusterInfo(SC_RUNNING);
        checkClusterMetricsInfo(capability, 0);
        // heartbeat again after adding a node.
        rm.registerNode("127.0.0.1:1234", (4 * 1024));
        storeHeartbeat.run();
        capability = checkSubClusterInfo(SC_RUNNING);
        checkClusterMetricsInfo(capability, 1);
        // Validate sub-cluster deregistration
        getFederationStateStoreService().deregisterSubCluster(SubClusterDeregisterRequest.newInstance(subClusterId, SC_UNREGISTERED));
        checkSubClusterInfo(SC_UNREGISTERED);
        // check after failover
        explicitFailover(rm);
        capability = checkSubClusterInfo(SC_NEW);
        Assert.assertTrue(capability.isEmpty());
        // Heartbeat to see if sub-cluster transitions to running
        storeHeartbeat = getFederationStateStoreService().getStateStoreHeartbeatThread();
        storeHeartbeat.run();
        capability = checkSubClusterInfo(SC_RUNNING);
        checkClusterMetricsInfo(capability, 0);
        // heartbeat again after adding a node.
        rm.registerNode("127.0.0.1:1234", (4 * 1024));
        storeHeartbeat.run();
        capability = checkSubClusterInfo(SC_RUNNING);
        checkClusterMetricsInfo(capability, 1);
        stop();
    }
}

