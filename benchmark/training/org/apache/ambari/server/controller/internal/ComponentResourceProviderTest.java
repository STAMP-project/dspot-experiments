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


import State.INSTALLED;
import State.STARTED;
import com.google.inject.Injector;
import java.util.HashSet;
import java.util.Set;
import org.apache.ambari.server.ObjectNotFoundException;
import org.apache.ambari.server.ServiceComponentNotFoundException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.AmbariManagementControllerImplTest;
import org.apache.ambari.server.controller.KerberosHelper;
import org.apache.ambari.server.controller.MaintenanceStateHelper;
import org.apache.ambari.server.controller.ServiceComponentRequest;
import org.apache.ambari.server.controller.ServiceComponentResponse;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ComponentInfo;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.StackId;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the component resource provider.
 */
public class ComponentResourceProviderTest {
    private static final long CLUSTER_ID = 100;

    private static final String CLUSTER_NAME = "Cluster100";

    private static final String SERVICE_NAME = "Service100";

    @Test
    public void testCreateResourcesAsAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testCreateResourcesAsClusterAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testCreateResourcesAsServiceAdministrator() throws Exception {
        testCreateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResourcesAsAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetResourcesAsClusterAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetResourcesAsServiceAdministrator() throws Exception {
        testGetResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testUpdateResourcesAsAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testUpdateResourcesAsClusterAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testUpdateResourcesAsServiceAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testSuccessDeleteResourcesAsAdministrator() throws Exception {
        testSuccessDeleteResources(TestAuthenticationFactory.createAdministrator(), INSTALLED);
    }

    @Test
    public void testSuccessDeleteResourcesAsClusterAdministrator() throws Exception {
        testSuccessDeleteResources(TestAuthenticationFactory.createClusterAdministrator(), INSTALLED);
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsServiceAdministrator() throws Exception {
        testSuccessDeleteResources(TestAuthenticationFactory.createServiceAdministrator(), INSTALLED);
    }

    @Test(expected = SystemException.class)
    public void testDeleteResourcesWithStartedHostComponentState() throws Exception {
        testSuccessDeleteResources(TestAuthenticationFactory.createAdministrator(), STARTED);
    }

    @Test
    public void testDeleteResourcesWithEmptyClusterComponentNamesAsAdministrator() throws Exception {
        testDeleteResourcesWithEmptyClusterComponentNames(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testDeleteResourcesWithEmptyClusterComponentNamesAsClusterAdministrator() throws Exception {
        testDeleteResourcesWithEmptyClusterComponentNames(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesWithEmptyClusterComponentNamesAsServiceAdministrator() throws Exception {
        testDeleteResourcesWithEmptyClusterComponentNames(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testUpdateAutoStartAsAdministrator() throws Exception {
        testUpdateAutoStart(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testUpdateAutoStartAsClusterAdministrator() throws Exception {
        testUpdateAutoStart(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testUpdateAutoStartAsServiceAdministrator() throws Exception {
        testUpdateAutoStart(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateAutoStartAsClusterUser() throws Exception {
        testUpdateAutoStart(TestAuthenticationFactory.createClusterUser());
    }

    @Test
    public void testGetComponents() throws Exception {
        // member state mocks
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        StackId stackId = createNiceMock(StackId.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service = createNiceMock(Service.class);
        ComponentInfo componentInfo = createNiceMock(ComponentInfo.class);
        ServiceComponent component = createNiceMock(ServiceComponent.class);
        ServiceComponentResponse response = createNiceMock(ServiceComponentResponse.class);
        // requests
        ServiceComponentRequest request1 = new ServiceComponentRequest("cluster1", "service1", "component1", null, /* recovery enabled */
        String.valueOf(true));
        Set<ServiceComponentRequest> setRequests = new HashSet<>();
        setRequests.add(request1);
        // expectations
        // constructor init
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(stackId.getStackName()).andReturn("stackName").anyTimes();
        expect(stackId.getStackVersion()).andReturn("1").anyTimes();
        // getComponents
        expect(clusters.getCluster("cluster1")).andReturn(cluster);
        expect(cluster.getService("service1")).andReturn(service);
        expect(service.getName()).andReturn("service1").anyTimes();
        expect(service.getServiceComponent("component1")).andReturn(component);
        expect(ambariMetaInfo.getComponent("stackName", "1", "service1", "component1")).andReturn(componentInfo);
        expect(componentInfo.getCategory()).andReturn(null);
        expect(component.getDesiredStackId()).andReturn(stackId).anyTimes();
        expect(component.convertToResponse()).andReturn(response);
        // replay mocks
        replay(clusters, cluster, service, componentInfo, component, response, ambariMetaInfo, stackId, managementController);
        // test
        Set<ServiceComponentResponse> setResponses = ComponentResourceProviderTest.getComponentResourceProvider(managementController).getComponents(setRequests);
        // assert and verify
        Assert.assertEquals(1, setResponses.size());
        Assert.assertTrue(setResponses.contains(response));
        verify(clusters, cluster, service, componentInfo, component, response, ambariMetaInfo, stackId, managementController);
    }

    /**
     * Ensure that ServiceComponentNotFoundException is handled where there are multiple requests as would be the
     * case when an OR predicate is provided in the query.
     */
    @Test
    public void testGetComponents_OR_Predicate_ServiceComponentNotFoundException() throws Exception {
        // member state mocks
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        StackId stackId = createNiceMock(StackId.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service = createNiceMock(Service.class);
        ComponentInfo component3Info = createNiceMock(ComponentInfo.class);
        ComponentInfo component4Info = createNiceMock(ComponentInfo.class);
        ServiceComponent component1 = createNiceMock(ServiceComponent.class);
        ServiceComponent component2 = createNiceMock(ServiceComponent.class);
        ServiceComponentResponse response1 = createNiceMock(ServiceComponentResponse.class);
        ServiceComponentResponse response2 = createNiceMock(ServiceComponentResponse.class);
        // requests
        ServiceComponentRequest request1 = new ServiceComponentRequest("cluster1", "service1", "component1", null, /* recovery enabled */
        String.valueOf(true));
        ServiceComponentRequest request2 = new ServiceComponentRequest("cluster1", "service1", "component2", null, /* recovery enabled */
        String.valueOf(true));
        ServiceComponentRequest request3 = new ServiceComponentRequest("cluster1", "service1", "component3", null, /* recovery enabled */
        String.valueOf(true));
        ServiceComponentRequest request4 = new ServiceComponentRequest("cluster1", "service1", "component4", null, /* recovery enabled */
        String.valueOf(true));
        ServiceComponentRequest request5 = new ServiceComponentRequest("cluster1", "service2", null, null, /* recovery enabled */
        String.valueOf(true));
        Set<ServiceComponentRequest> setRequests = new HashSet<>();
        setRequests.add(request1);
        setRequests.add(request2);
        setRequests.add(request3);
        setRequests.add(request4);
        setRequests.add(request5);
        // expectations
        // constructor init
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(stackId.getStackName()).andReturn("stackName").anyTimes();
        expect(stackId.getStackVersion()).andReturn("1").anyTimes();
        // getComponents
        expect(clusters.getCluster("cluster1")).andReturn(cluster).anyTimes();
        expect(cluster.getDesiredStackVersion()).andReturn(stackId).anyTimes();
        expect(cluster.getService("service1")).andReturn(service).anyTimes();
        expect(cluster.getService("service2")).andThrow(new ObjectNotFoundException("service2"));
        expect(ambariMetaInfo.getComponent("stackName", "1", "service1", "component3")).andReturn(component3Info);
        expect(ambariMetaInfo.getComponent("stackName", "1", "service1", "component4")).andReturn(component4Info);
        expect(component3Info.getCategory()).andReturn(null);
        expect(component4Info.getCategory()).andReturn(null);
        expect(service.getName()).andReturn("service1").anyTimes();
        expect(service.getServiceComponent("component1")).andThrow(new ServiceComponentNotFoundException("cluster1", "service1", "component1"));
        expect(service.getServiceComponent("component2")).andThrow(new ServiceComponentNotFoundException("cluster1", "service1", "component2"));
        expect(service.getServiceComponent("component3")).andReturn(component1);
        expect(service.getServiceComponent("component4")).andReturn(component2);
        expect(component1.convertToResponse()).andReturn(response1);
        expect(component1.getDesiredStackId()).andReturn(stackId).anyTimes();
        expect(component2.convertToResponse()).andReturn(response2);
        expect(component2.getDesiredStackId()).andReturn(stackId).anyTimes();
        // replay mocks
        replay(clusters, cluster, service, component3Info, component4Info, component1, component2, response1, response2, ambariMetaInfo, stackId, managementController);
        // test
        Set<ServiceComponentResponse> setResponses = ComponentResourceProviderTest.getComponentResourceProvider(managementController).getComponents(setRequests);
        // assert and verify
        Assert.assertEquals(2, setResponses.size());
        Assert.assertTrue(setResponses.contains(response1));
        Assert.assertTrue(setResponses.contains(response2));
        verify(clusters, cluster, service, component3Info, component4Info, component1, component2, response1, response2, ambariMetaInfo, stackId, managementController);
    }

    /**
     * Ensure that ServiceComponentNotFoundException is propagated in case where there is a single request.
     */
    @Test
    public void testGetComponents_ServiceComponentNotFoundException() throws Exception {
        // member state mocks
        Injector injector = createStrictMock(Injector.class);
        Capture<AmbariManagementController> controllerCapture = EasyMock.newCapture();
        Clusters clusters = createNiceMock(Clusters.class);
        MaintenanceStateHelper maintHelper = createNiceMock(MaintenanceStateHelper.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service = createNiceMock(Service.class);
        // requests
        ServiceComponentRequest request1 = new ServiceComponentRequest("cluster1", "service1", "component1", null, /* recovery enabled */
        String.valueOf(true));
        Set<ServiceComponentRequest> setRequests = new HashSet<>();
        setRequests.add(request1);
        // expectations
        // constructor init
        AmbariManagementControllerImplTest.constructorInit(injector, controllerCapture, null, maintHelper, createNiceMock(KerberosHelper.class), null, null);
        // getComponents
        expect(clusters.getCluster("cluster1")).andReturn(cluster);
        expect(cluster.getService("service1")).andReturn(service);
        expect(service.getServiceComponent("component1")).andThrow(new ServiceComponentNotFoundException("cluster1", "service1", "component1"));
        // replay mocks
        replay(maintHelper, injector, clusters, cluster, service);
        // test
        AmbariManagementController controller = new org.apache.ambari.server.controller.AmbariManagementControllerImpl(null, clusters, injector);
        // assert that exception is thrown in case where there is a single request
        try {
            ComponentResourceProviderTest.getComponentResourceProvider(controller).getComponents(setRequests);
            Assert.fail("expected ServiceComponentNotFoundException");
        } catch (ServiceComponentNotFoundException e) {
            // expected
        }
        Assert.assertSame(controller, controllerCapture.getValue());
        verify(injector, clusters, cluster, service);
    }
}

