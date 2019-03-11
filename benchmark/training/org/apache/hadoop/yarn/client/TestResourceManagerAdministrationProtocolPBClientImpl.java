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
package org.apache.hadoop.yarn.client;


import DecommissionType.NORMAL;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceResponse;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test ResourceManagerAdministrationProtocolPBClientImpl. Test a methods and the proxy without  logic.
 */
public class TestResourceManagerAdministrationProtocolPBClientImpl {
    private static ResourceManager resourceManager;

    private static final Logger LOG = LoggerFactory.getLogger(TestResourceManagerAdministrationProtocolPBClientImpl.class);

    private final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    private static ResourceManagerAdministrationProtocol client;

    /**
     * Test method refreshQueues. This method is present and it works.
     */
    @Test
    public void testRefreshQueues() throws Exception {
        RefreshQueuesRequest request = recordFactory.newRecordInstance(RefreshQueuesRequest.class);
        RefreshQueuesResponse response = TestResourceManagerAdministrationProtocolPBClientImpl.client.refreshQueues(request);
        Assert.assertNotNull(response);
    }

    /**
     * Test method refreshNodes. This method is present and it works.
     */
    @Test
    public void testRefreshNodes() throws Exception {
        TestResourceManagerAdministrationProtocolPBClientImpl.resourceManager.getClientRMService();
        RefreshNodesRequest request = RefreshNodesRequest.newInstance(NORMAL);
        RefreshNodesResponse response = TestResourceManagerAdministrationProtocolPBClientImpl.client.refreshNodes(request);
        Assert.assertNotNull(response);
    }

    /**
     * Test method refreshSuperUserGroupsConfiguration. This method present and it works.
     */
    @Test
    public void testRefreshSuperUserGroupsConfiguration() throws Exception {
        RefreshSuperUserGroupsConfigurationRequest request = recordFactory.newRecordInstance(RefreshSuperUserGroupsConfigurationRequest.class);
        RefreshSuperUserGroupsConfigurationResponse response = TestResourceManagerAdministrationProtocolPBClientImpl.client.refreshSuperUserGroupsConfiguration(request);
        Assert.assertNotNull(response);
    }

    /**
     * Test method refreshUserToGroupsMappings. This method is present and it works.
     */
    @Test
    public void testRefreshUserToGroupsMappings() throws Exception {
        RefreshUserToGroupsMappingsRequest request = recordFactory.newRecordInstance(RefreshUserToGroupsMappingsRequest.class);
        RefreshUserToGroupsMappingsResponse response = TestResourceManagerAdministrationProtocolPBClientImpl.client.refreshUserToGroupsMappings(request);
        Assert.assertNotNull(response);
    }

    /**
     * Test method refreshAdminAcls. This method is present and it works.
     */
    @Test
    public void testRefreshAdminAcls() throws Exception {
        RefreshAdminAclsRequest request = recordFactory.newRecordInstance(RefreshAdminAclsRequest.class);
        RefreshAdminAclsResponse response = TestResourceManagerAdministrationProtocolPBClientImpl.client.refreshAdminAcls(request);
        Assert.assertNotNull(response);
    }

    @Test
    public void testUpdateNodeResource() throws Exception {
        UpdateNodeResourceRequest request = recordFactory.newRecordInstance(UpdateNodeResourceRequest.class);
        UpdateNodeResourceResponse response = TestResourceManagerAdministrationProtocolPBClientImpl.client.updateNodeResource(request);
        Assert.assertNotNull(response);
    }

    @Test
    public void testRefreshServiceAcls() throws Exception {
        RefreshServiceAclsRequest request = recordFactory.newRecordInstance(RefreshServiceAclsRequest.class);
        RefreshServiceAclsResponse response = TestResourceManagerAdministrationProtocolPBClientImpl.client.refreshServiceAcls(request);
        Assert.assertNotNull(response);
    }
}

