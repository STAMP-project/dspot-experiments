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
package org.apache.ambari.server.controller.internal;


import BaseClusterRequest.PROVISION_ACTION_PROPERTY;
import ClusterResourceProvider.CLUSTER_NAME_PROPERTY_ID;
import ClusterResourceProvider.CLUSTER_VERSION_PROPERTY_ID;
import ProvisionClusterRequest.REPO_VERSION_PROPERTY;
import Request.REQUEST_INFO_BODY_PROPERTY;
import Resource.Type;
import Resource.Type.Cluster;
import ResourceProviderEvent.Type.Create;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.ClusterRequest;
import org.apache.ambari.server.controller.RequestStatusResponse;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.RequestStatus;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.topology.Blueprint;
import org.apache.ambari.server.topology.BlueprintFactory;
import org.apache.ambari.server.topology.InvalidTopologyException;
import org.apache.ambari.server.topology.SecurityConfiguration;
import org.apache.ambari.server.topology.SecurityConfigurationFactory;
import org.apache.ambari.server.topology.TopologyManager;
import org.apache.ambari.server.topology.TopologyRequestFactory;
import org.apache.ambari.server.utils.RetryHelper;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.apache.ambari.server.controller.internal.AbstractResourceProviderTest.Matcher.getClusterRequest;


/**
 * ClusterResourceProvider tests.
 */
public class ClusterResourceProviderTest {
    private static final String CLUSTER_NAME = "cluster_name";

    private static final String BLUEPRINT_NAME = "blueprint_name";

    private ClusterResourceProvider provider;

    private static final AmbariManagementController controller = createNiceMock(AmbariManagementController.class);

    private static final Request request = createNiceMock(Request.class);

    private static final TopologyManager topologyManager = createStrictMock(TopologyManager.class);

    private static final TopologyRequestFactory topologyFactory = createStrictMock(TopologyRequestFactory.class);

    private static final SecurityConfigurationFactory securityFactory = createMock(SecurityConfigurationFactory.class);

    private static final ProvisionClusterRequest topologyRequest = createNiceMock(ProvisionClusterRequest.class);

    private static final BlueprintFactory blueprintFactory = createStrictMock(BlueprintFactory.class);

    private static final Blueprint blueprint = createNiceMock(Blueprint.class);

    private static final RequestStatusResponse requestStatusResponse = createNiceMock(RequestStatusResponse.class);

    private static final Gson gson = new Gson();

    @Test
    public void testCreateResource_blueprint_asAdministrator() throws Exception {
        testCreateResource_blueprint(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResource_blueprint__NonAdministrator() throws Exception {
        testCreateResource_blueprint(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testCreateResource_blueprint_With_ProvisionAction() throws Exception {
        Set<Map<String, Object>> requestProperties = createBlueprintRequestProperties(ClusterResourceProviderTest.CLUSTER_NAME, ClusterResourceProviderTest.BLUEPRINT_NAME);
        Map<String, Object> properties = requestProperties.iterator().next();
        properties.put(PROVISION_ACTION_PROPERTY, "INSTALL_ONLY");
        Map<String, String> requestInfoProperties = new HashMap<>();
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, "{}");
        // set expectations
        expect(ClusterResourceProviderTest.request.getProperties()).andReturn(requestProperties).anyTimes();
        expect(ClusterResourceProviderTest.request.getRequestInfoProperties()).andReturn(requestInfoProperties).anyTimes();
        expect(ClusterResourceProviderTest.securityFactory.createSecurityConfigurationFromRequest(EasyMock.anyObject(), anyBoolean())).andReturn(null).once();
        expect(ClusterResourceProviderTest.topologyFactory.createProvisionClusterRequest(properties, null)).andReturn(ClusterResourceProviderTest.topologyRequest).once();
        expect(ClusterResourceProviderTest.topologyManager.provisionCluster(ClusterResourceProviderTest.topologyRequest)).andReturn(ClusterResourceProviderTest.requestStatusResponse).once();
        expect(ClusterResourceProviderTest.requestStatusResponse.getRequestId()).andReturn(5150L).anyTimes();
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        RequestStatus requestStatus = provider.createResources(ClusterResourceProviderTest.request);
        Assert.assertEquals(5150L, requestStatus.getRequestResource().getPropertyValue(PropertyHelper.getPropertyId("Requests", "id")));
        Assert.assertEquals(Resource.Type.Request, requestStatus.getRequestResource().getType());
        Assert.assertEquals("Accepted", requestStatus.getRequestResource().getPropertyValue(PropertyHelper.getPropertyId("Requests", "status")));
        verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateResource_blueprint_withInvalidSecurityConfiguration() throws Exception {
        Set<Map<String, Object>> requestProperties = createBlueprintRequestProperties(ClusterResourceProviderTest.CLUSTER_NAME, ClusterResourceProviderTest.BLUEPRINT_NAME);
        Map<String, Object> properties = requestProperties.iterator().next();
        Map<String, String> requestInfoProperties = new HashMap<>();
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, ("{\"security\" : {\n\"type\" : \"NONE\"," + ("\n\"kerberos_descriptor_reference\" : " + "\"testRef\"\n}}")));
        SecurityConfiguration blueprintSecurityConfiguration = SecurityConfiguration.withReference("testRef");
        SecurityConfiguration securityConfiguration = SecurityConfiguration.NONE;
        // set expectations
        expect(ClusterResourceProviderTest.request.getProperties()).andReturn(requestProperties).anyTimes();
        expect(ClusterResourceProviderTest.request.getRequestInfoProperties()).andReturn(requestInfoProperties).anyTimes();
        expect(ClusterResourceProviderTest.securityFactory.createSecurityConfigurationFromRequest(EasyMock.anyObject(), anyBoolean())).andReturn(securityConfiguration).once();
        expect(ClusterResourceProviderTest.topologyFactory.createProvisionClusterRequest(properties, securityConfiguration)).andReturn(ClusterResourceProviderTest.topologyRequest).once();
        expect(ClusterResourceProviderTest.topologyRequest.getBlueprint()).andReturn(ClusterResourceProviderTest.blueprint).anyTimes();
        expect(ClusterResourceProviderTest.blueprint.getSecurity()).andReturn(blueprintSecurityConfiguration).anyTimes();
        expect(ClusterResourceProviderTest.requestStatusResponse.getRequestId()).andReturn(5150L).anyTimes();
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        RequestStatus requestStatus = provider.createResources(ClusterResourceProviderTest.request);
    }

    @Test
    public void testCreateResource_blueprint_withSecurityConfiguration() throws Exception {
        Set<Map<String, Object>> requestProperties = createBlueprintRequestProperties(ClusterResourceProviderTest.CLUSTER_NAME, ClusterResourceProviderTest.BLUEPRINT_NAME);
        Map<String, Object> properties = requestProperties.iterator().next();
        SecurityConfiguration securityConfiguration = SecurityConfiguration.withReference("testRef");
        Map<String, String> requestInfoProperties = new HashMap<>();
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, ("{\"security\" : {\n\"type\" : \"KERBEROS\",\n\"kerberos_descriptor_reference\" : " + "\"testRef\"\n}}"));
        // set expectations
        expect(ClusterResourceProviderTest.request.getProperties()).andReturn(requestProperties).anyTimes();
        expect(ClusterResourceProviderTest.request.getRequestInfoProperties()).andReturn(requestInfoProperties).anyTimes();
        expect(ClusterResourceProviderTest.topologyFactory.createProvisionClusterRequest(properties, securityConfiguration)).andReturn(ClusterResourceProviderTest.topologyRequest).once();
        expect(ClusterResourceProviderTest.securityFactory.createSecurityConfigurationFromRequest(EasyMock.anyObject(), anyBoolean())).andReturn(securityConfiguration).once();
        expect(ClusterResourceProviderTest.topologyManager.provisionCluster(ClusterResourceProviderTest.topologyRequest)).andReturn(ClusterResourceProviderTest.requestStatusResponse).once();
        expect(ClusterResourceProviderTest.requestStatusResponse.getRequestId()).andReturn(5150L).anyTimes();
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        RequestStatus requestStatus = provider.createResources(ClusterResourceProviderTest.request);
        Assert.assertEquals(5150L, requestStatus.getRequestResource().getPropertyValue(PropertyHelper.getPropertyId("Requests", "id")));
        Assert.assertEquals(Resource.Type.Request, requestStatus.getRequestResource().getType());
        Assert.assertEquals("Accepted", requestStatus.getRequestResource().getPropertyValue(PropertyHelper.getPropertyId("Requests", "status")));
        verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateResource_blueprint__InvalidRequest() throws Exception {
        Set<Map<String, Object>> requestProperties = createBlueprintRequestProperties(ClusterResourceProviderTest.CLUSTER_NAME, ClusterResourceProviderTest.BLUEPRINT_NAME);
        Map<String, Object> properties = requestProperties.iterator().next();
        // set expectations
        expect(ClusterResourceProviderTest.request.getProperties()).andReturn(requestProperties).anyTimes();
        // throw exception from topology request factory an assert that the correct exception is thrown from resource provider
        expect(ClusterResourceProviderTest.topologyFactory.createProvisionClusterRequest(properties, null)).andThrow(new InvalidTopologyException("test"));
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        provider.createResources(ClusterResourceProviderTest.request);
    }

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsNonAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testCreateResourcesWithRetry() throws Exception {
        Clusters clusters = createMock(Clusters.class);
        EasyMock.replay(clusters);
        RetryHelper.init(clusters, 3);
        Resource.Type type = Type.Cluster;
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        RequestStatusResponse response = createNiceMock(RequestStatusResponse.class);
        managementController.createCluster(getClusterRequest(null, "Cluster100", "HDP-0.1", null));
        expectLastCall().andThrow(new DatabaseException("test") {}).once().andVoid().atLeastOnce();
        // replay
        replay(managementController, response);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        // add the property map to a set for the request.  add more maps for multiple creates
        Set<Map<String, Object>> propertySet = new LinkedHashSet<>();
        // Cluster 1: create a map of properties for the request
        Map<String, Object> properties = new LinkedHashMap<>();
        // add the cluster name to the properties map
        properties.put(CLUSTER_NAME_PROPERTY_ID, "Cluster100");
        // add the version to the properties map
        properties.put(CLUSTER_VERSION_PROPERTY_ID, "HDP-0.1");
        propertySet.add(properties);
        // create the request
        Request request = PropertyHelper.getCreateRequest(propertySet, null);
        provider.createResources(request);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Cluster, lastEvent.getResourceType());
        Assert.assertEquals(Create, lastEvent.getType());
        Assert.assertEquals(request, lastEvent.getRequest());
        Assert.assertNull(lastEvent.getPredicate());
        // verify
        verify(managementController, response);
        RetryHelper.init(clusters, 0);
    }

    @Test
    public void testGetResourcesAsAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetResourcesAsNonAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testUpdateResourcesAsAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testUpdateResourcesAsClusterAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateResourcesAsServiceOperator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testUpdateWithConfigurationAsAdministrator() throws Exception {
        testUpdateWithConfiguration(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testUpdateWithConfigurationAsClusterAdministrator() throws Exception {
        testUpdateWithConfiguration(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateWithConfigurationAsServiceOperator() throws Exception {
        testUpdateWithConfiguration(TestAuthenticationFactory.createServiceOperator());
    }

    @Test
    public void testDeleteResourcesAsAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsNonAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testCreateWithRepository() throws Exception {
        Authentication authentication = TestAuthenticationFactory.createAdministrator();
        Resource.Type type = Type.Cluster;
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Capture<ClusterRequest> cap = Capture.newInstance();
        managementController.createCluster(capture(cap));
        expectLastCall();
        // replay
        replay(managementController);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        // add the property map to a set for the request.  add more maps for multiple creates
        Set<Map<String, Object>> propertySet = new LinkedHashSet<>();
        // Cluster 1: create a map of properties for the request
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put(CLUSTER_NAME_PROPERTY_ID, "Cluster100");
        properties.put(CLUSTER_VERSION_PROPERTY_ID, "HDP-0.1");
        propertySet.add(properties);
        // create the request
        Request request = PropertyHelper.getCreateRequest(propertySet, null);
        provider.createResources(request);
        // verify
        verify(managementController);
        Assert.assertTrue(cap.hasCaptured());
        Assert.assertNotNull(cap.getValue());
    }

    @Test
    public void testCreateResource_blueprint_withRepoVersion() throws Exception {
        Authentication authentication = TestAuthenticationFactory.createAdministrator();
        Set<Map<String, Object>> requestProperties = createBlueprintRequestProperties(ClusterResourceProviderTest.CLUSTER_NAME, ClusterResourceProviderTest.BLUEPRINT_NAME);
        Map<String, Object> properties = requestProperties.iterator().next();
        properties.put(REPO_VERSION_PROPERTY, "2.1.1");
        Map<String, String> requestInfoProperties = new HashMap<>();
        requestInfoProperties.put(REQUEST_INFO_BODY_PROPERTY, "{}");
        // set expectations
        expect(ClusterResourceProviderTest.request.getProperties()).andReturn(requestProperties).anyTimes();
        expect(ClusterResourceProviderTest.request.getRequestInfoProperties()).andReturn(requestInfoProperties).anyTimes();
        expect(ClusterResourceProviderTest.securityFactory.createSecurityConfigurationFromRequest(EasyMock.anyObject(), anyBoolean())).andReturn(null).once();
        expect(ClusterResourceProviderTest.topologyFactory.createProvisionClusterRequest(properties, null)).andReturn(ClusterResourceProviderTest.topologyRequest).once();
        expect(ClusterResourceProviderTest.topologyManager.provisionCluster(ClusterResourceProviderTest.topologyRequest)).andReturn(ClusterResourceProviderTest.requestStatusResponse).once();
        expect(ClusterResourceProviderTest.requestStatusResponse.getRequestId()).andReturn(5150L).anyTimes();
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        RequestStatus requestStatus = provider.createResources(ClusterResourceProviderTest.request);
        Assert.assertEquals(5150L, requestStatus.getRequestResource().getPropertyValue(PropertyHelper.getPropertyId("Requests", "id")));
        Assert.assertEquals(Resource.Type.Request, requestStatus.getRequestResource().getType());
        Assert.assertEquals("Accepted", requestStatus.getRequestResource().getPropertyValue(PropertyHelper.getPropertyId("Requests", "status")));
        verifyAll();
    }
}

