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


import HealthStatus.ALERT;
import HealthStatus.HEALTHY;
import HealthStatus.UNHEALTHY;
import HealthStatus.UNKNOWN;
import HostResourceProvider.HOST_CLUSTER_NAME_PROPERTY_ID;
import HostResourceProvider.HOST_HOST_NAME_PROPERTY_ID;
import HostResourceProvider.HOST_HOST_STATUS_PROPERTY_ID;
import MaintenanceState.OFF;
import Resource.Type;
import com.google.inject.Injector;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.ambari.server.HostNotFoundException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.HostRequest;
import org.apache.ambari.server.controller.HostResponse;
import org.apache.ambari.server.controller.ResourceProviderFactory;
import org.apache.ambari.server.controller.ServiceComponentHostResponse;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ComponentInfo;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.HostHealthStatus;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * HostResourceProvider tests.
 */
public class HostResourceProviderTest extends EasyMockSupport {
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
    public void testGetResources_Status_NoCluster() throws Exception {
        Resource.Type type = Type.Host;
        Injector injector = createInjector();
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = createMock(Cluster.class);
        HostHealthStatus healthStatus = createNiceMock(HostHealthStatus.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        ComponentInfo componentInfo = createNiceMock(ComponentInfo.class);
        Host host100 = createMockHost("Host100", "Cluster100", null, "HEALTHY", "RECOVERABLE", null);
        HostResponse hostResponse1 = createNiceMock(HostResponse.class);
        ResourceProviderFactory resourceProviderFactory = createNiceMock(ResourceProviderFactory.class);
        ResourceProvider hostResourceProvider = HostResourceProviderTest.getHostProvider(injector);
        AbstractControllerResourceProvider.init(resourceProviderFactory);
        Set<Cluster> clusterSet = new HashSet<>();
        clusterSet.add(cluster);
        ServiceComponentHostResponse shr1 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component100", "Component 100", "Host100", "Host100", "STARTED", "", null, null, null, null);
        ServiceComponentHostResponse shr2 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component102", "Component 102", "Host100", "Host100", "STARTED", "", null, null, null, null);
        ServiceComponentHostResponse shr3 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component103", "Component 103", "Host100", "Host100", "STARTED", "", null, null, null, null);
        Set<ServiceComponentHostResponse> responses = new HashSet<>();
        responses.add(shr1);
        responses.add(shr2);
        responses.add(shr3);
        // set expectations
        expect(host100.getMaintenanceState(2)).andReturn(OFF).anyTimes();
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(managementController.getHostComponents(EasyMock.anyObject())).andReturn(responses).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(clusters.getClustersForHost("Host100")).andReturn(clusterSet).anyTimes();
        expect(clusters.getHosts()).andReturn(Arrays.asList(host100)).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(cluster.getDesiredConfigs()).andReturn(new HashMap()).anyTimes();
        expect(hostResponse1.getClusterName()).andReturn("").anyTimes();
        expect(hostResponse1.getHostname()).andReturn("Host100").anyTimes();
        expect(hostResponse1.getStatus()).andReturn(HEALTHY.name()).anyTimes();
        expect(hostResponse1.getHealthReport()).andReturn("HEALTHY").anyTimes();
        expect(ambariMetaInfo.getComponent(((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())))).andReturn(componentInfo).anyTimes();
        expect(componentInfo.getCategory()).andReturn("MASTER").anyTimes();
        expect(resourceProviderFactory.getHostResourceProvider(eq(managementController))).andReturn(hostResourceProvider).anyTimes();
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(HOST_CLUSTER_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_STATUS_PROPERTY_ID);
        Predicate predicate = buildPredicate("Cluster100", null);
        Request request = PropertyHelper.getReadRequest(propertyIds);
        // replay
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            String clusterName = ((String) (resource.getPropertyValue(HOST_CLUSTER_NAME_PROPERTY_ID)));
            Assert.assertEquals("Cluster100", clusterName);
            String status = ((String) (resource.getPropertyValue(HOST_HOST_STATUS_PROPERTY_ID)));
            Assert.assertEquals("HEALTHY", status);
        }
        // verify
        verifyAll();
    }

    @Test
    public void testGetResources_Status_Healthy() throws Exception {
        Resource.Type type = Type.Host;
        Injector injector = createInjector();
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = createMock(Cluster.class);
        HostHealthStatus healthStatus = createNiceMock(HostHealthStatus.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        ComponentInfo componentInfo = createNiceMock(ComponentInfo.class);
        Host host100 = createMockHost("Host100", "Cluster100", null, "HEALTHY", "RECOVERABLE", null);
        ResourceProviderFactory resourceProviderFactory = createNiceMock(ResourceProviderFactory.class);
        ResourceProvider hostResourceProvider = HostResourceProviderTest.getHostProvider(injector);
        AbstractControllerResourceProvider.init(resourceProviderFactory);
        Set<Cluster> clusterSet = new HashSet<>();
        clusterSet.add(cluster);
        ServiceComponentHostResponse shr1 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component100", "Component 100", "Host100", "Host100", "STARTED", "", null, null, null, null);
        ServiceComponentHostResponse shr2 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component102", "Component 102", "Host100", "Host100", "STARTED", "", null, null, null, null);
        ServiceComponentHostResponse shr3 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component103", "Component 103", "Host100", "Host100", "STARTED", "", null, null, null, null);
        Set<ServiceComponentHostResponse> responses = new HashSet<>();
        responses.add(shr1);
        responses.add(shr2);
        responses.add(shr3);
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(managementController.getHostComponents(EasyMock.anyObject())).andReturn(responses).anyTimes();
        expect(host100.getMaintenanceState(2)).andReturn(OFF).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(clusters.getClustersForHost("Host100")).andReturn(clusterSet).anyTimes();
        expect(clusters.getHosts()).andReturn(Arrays.asList(host100)).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(cluster.getDesiredConfigs()).andReturn(new HashMap()).anyTimes();
        expect(healthStatus.getHealthStatus()).andReturn(HostHealthStatus.HealthStatus.HEALTHY).anyTimes();
        expect(healthStatus.getHealthReport()).andReturn("HEALTHY").anyTimes();
        expect(ambariMetaInfo.getComponent(((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())))).andReturn(componentInfo).anyTimes();
        expect(componentInfo.getCategory()).andReturn("MASTER").anyTimes();
        expect(resourceProviderFactory.getHostResourceProvider(eq(managementController))).andReturn(hostResourceProvider).anyTimes();
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(HOST_CLUSTER_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_STATUS_PROPERTY_ID);
        Predicate predicate = buildPredicate("Cluster100", null);
        Request request = PropertyHelper.getReadRequest(propertyIds);
        // replay
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            String clusterName = ((String) (resource.getPropertyValue(HOST_CLUSTER_NAME_PROPERTY_ID)));
            Assert.assertEquals("Cluster100", clusterName);
            String status = ((String) (resource.getPropertyValue(HOST_HOST_STATUS_PROPERTY_ID)));
            Assert.assertEquals("HEALTHY", status);
        }
        // verify
        verifyAll();
    }

    @Test
    public void testGetResources_Status_Unhealthy() throws Exception {
        Resource.Type type = Type.Host;
        Injector injector = createInjector();
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = createMock(Cluster.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        ComponentInfo componentInfo = createNiceMock(ComponentInfo.class);
        HostResponse hostResponse1 = createNiceMock(HostResponse.class);
        ResourceProviderFactory resourceProviderFactory = createNiceMock(ResourceProviderFactory.class);
        ResourceProvider hostResourceProvider = HostResourceProviderTest.getHostProvider(injector);
        Host host100 = createMockHost("Host100", "Cluster100", null, "UNHEALTHY", "RECOVERABLE", null);
        AbstractControllerResourceProvider.init(resourceProviderFactory);
        Set<Cluster> clusterSet = new HashSet<>();
        clusterSet.add(cluster);
        ServiceComponentHostResponse shr1 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component100", "Component 100", "Host100", "Host100", "STARTED", "", null, null, null, null);
        ServiceComponentHostResponse shr2 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component102", "Component 102", "Host100", "Host100", "INSTALLED", "", null, null, null, null);
        ServiceComponentHostResponse shr3 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component103", "Component 103", "Host100", "Host100", "STARTED", "", null, null, null, null);
        Set<ServiceComponentHostResponse> responses = new HashSet<>();
        responses.add(shr1);
        responses.add(shr2);
        responses.add(shr3);
        // set expectations
        expect(host100.getMaintenanceState(2)).andReturn(OFF).anyTimes();
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(managementController.getHostComponents(EasyMock.anyObject())).andReturn(responses).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(clusters.getClustersForHost("Host100")).andReturn(clusterSet).anyTimes();
        expect(clusters.getHosts()).andReturn(Arrays.asList(host100)).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(cluster.getDesiredConfigs()).andReturn(new HashMap()).anyTimes();
        expect(hostResponse1.getClusterName()).andReturn("Cluster100").anyTimes();
        expect(hostResponse1.getHostname()).andReturn("Host100").anyTimes();
        expect(hostResponse1.getStatus()).andReturn(UNHEALTHY.name()).anyTimes();
        expect(hostResponse1.getHealthReport()).andReturn("HEALTHY").anyTimes();
        expect(ambariMetaInfo.getComponent(((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())))).andReturn(componentInfo).anyTimes();
        expect(componentInfo.getCategory()).andReturn("MASTER").anyTimes();
        expect(resourceProviderFactory.getHostResourceProvider(eq(managementController))).andReturn(hostResourceProvider).anyTimes();
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(HOST_CLUSTER_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_STATUS_PROPERTY_ID);
        Predicate predicate = buildPredicate("Cluster100", null);
        Request request = PropertyHelper.getReadRequest(propertyIds);
        // replay
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            String clusterName = ((String) (resource.getPropertyValue(HOST_CLUSTER_NAME_PROPERTY_ID)));
            Assert.assertEquals("Cluster100", clusterName);
            String status = ((String) (resource.getPropertyValue(HOST_HOST_STATUS_PROPERTY_ID)));
            Assert.assertEquals("UNHEALTHY", status);
        }
        // verify
        verifyAll();
    }

    @Test
    public void testGetResources_Status_Unknown() throws Exception {
        Resource.Type type = Type.Host;
        Injector injector = createInjector();
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = createMock(Cluster.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        HostResponse hostResponse1 = createNiceMock(HostResponse.class);
        ResourceProviderFactory resourceProviderFactory = createNiceMock(ResourceProviderFactory.class);
        ResourceProvider hostResourceProvider = HostResourceProviderTest.getHostProvider(injector);
        AbstractControllerResourceProvider.init(resourceProviderFactory);
        Host host100 = createMockHost("Host100", "Cluster100", null, "UNKNOWN", "RECOVERABLE", null);
        Set<Cluster> clusterSet = new HashSet<>();
        clusterSet.add(cluster);
        // set expectations
        expect(host100.getMaintenanceState(2)).andReturn(OFF).anyTimes();
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(clusters.getClustersForHost("Host100")).andReturn(clusterSet).anyTimes();
        expect(clusters.getHosts()).andReturn(Arrays.asList(host100)).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(cluster.getDesiredConfigs()).andReturn(new HashMap()).anyTimes();
        expect(hostResponse1.getClusterName()).andReturn("Cluster100").anyTimes();
        expect(hostResponse1.getHostname()).andReturn("Host100").anyTimes();
        expect(hostResponse1.getStatus()).andReturn(UNKNOWN.name()).anyTimes();
        expect(hostResponse1.getHealthReport()).andReturn("UNKNOWN").anyTimes();
        expect(resourceProviderFactory.getHostResourceProvider(eq(managementController))).andReturn(hostResourceProvider).anyTimes();
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(HOST_CLUSTER_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_STATUS_PROPERTY_ID);
        Predicate predicate = buildPredicate("Cluster100", null);
        Request request = PropertyHelper.getReadRequest(propertyIds);
        // replay
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            String clusterName = ((String) (resource.getPropertyValue(HOST_CLUSTER_NAME_PROPERTY_ID)));
            Assert.assertEquals("Cluster100", clusterName);
            String status = ((String) (resource.getPropertyValue(HOST_HOST_STATUS_PROPERTY_ID)));
            Assert.assertEquals("UNKNOWN", status);
        }
        // verify
        verifyAll();
    }

    @Test
    public void testGetRecoveryReportAsAdministrator() throws Exception {
        testGetRecoveryReport(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetRecoveryReportAsClusterAdministrator() throws Exception {
        testGetRecoveryReport(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetRecoveryReportAsServiceAdministrator() throws Exception {
        testGetRecoveryReport(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetResources_Status_Alert() throws Exception {
        Resource.Type type = Type.Host;
        Injector injector = createInjector();
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = createMock(Cluster.class);
        HostHealthStatus healthStatus = createNiceMock(HostHealthStatus.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        ComponentInfo componentInfo = createNiceMock(ComponentInfo.class);
        HostResponse hostResponse1 = createNiceMock(HostResponse.class);
        ResourceProviderFactory resourceProviderFactory = createNiceMock(ResourceProviderFactory.class);
        ResourceProvider hostResourceProvider = HostResourceProviderTest.getHostProvider(injector);
        Host host100 = createMockHost("Host100", "Cluster100", null, "ALERT", "RECOVERABLE", null);
        AbstractControllerResourceProvider.init(resourceProviderFactory);
        Set<Cluster> clusterSet = new HashSet<>();
        clusterSet.add(cluster);
        ServiceComponentHostResponse shr1 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component100", "Component 100", "Host100", "Host100", "STARTED", "", null, null, null, null);
        ServiceComponentHostResponse shr2 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component102", "Component 102", "Host100", "Host100", "INSTALLED", "", null, null, null, null);
        ServiceComponentHostResponse shr3 = new ServiceComponentHostResponse("Cluster100", "Service100", "Component103", "Component 103", "Host100", "Host100", "STARTED", "", null, null, null, null);
        Set<ServiceComponentHostResponse> responses = new HashSet<>();
        responses.add(shr1);
        responses.add(shr2);
        responses.add(shr3);
        // set expectations
        expect(host100.getMaintenanceState(2)).andReturn(OFF).anyTimes();
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(managementController.getHostComponents(EasyMock.anyObject())).andReturn(responses).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(clusters.getClustersForHost("Host100")).andReturn(clusterSet).anyTimes();
        expect(clusters.getHosts()).andReturn(Arrays.asList(host100)).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(cluster.getDesiredConfigs()).andReturn(new HashMap()).anyTimes();
        expect(hostResponse1.getClusterName()).andReturn("Cluster100").anyTimes();
        expect(hostResponse1.getHostname()).andReturn("Host100").anyTimes();
        expect(hostResponse1.getStatus()).andReturn(ALERT.name()).anyTimes();
        expect(hostResponse1.getHealthReport()).andReturn("HEALTHY").anyTimes();
        expect(ambariMetaInfo.getComponent(((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())))).andReturn(componentInfo).anyTimes();
        expect(componentInfo.getCategory()).andReturn("SLAVE").anyTimes();
        expect(resourceProviderFactory.getHostResourceProvider(eq(managementController))).andReturn(hostResourceProvider).anyTimes();
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(HOST_CLUSTER_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_NAME_PROPERTY_ID);
        propertyIds.add(HOST_HOST_STATUS_PROPERTY_ID);
        Predicate predicate = buildPredicate("Cluster100", null);
        Request request = PropertyHelper.getReadRequest(propertyIds);
        // replay
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = AbstractControllerResourceProvider.getResourceProvider(type, managementController);
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            String clusterName = ((String) (resource.getPropertyValue(HOST_CLUSTER_NAME_PROPERTY_ID)));
            Assert.assertEquals("Cluster100", clusterName);
            String status = ((String) (resource.getPropertyValue(HOST_HOST_STATUS_PROPERTY_ID)));
            Assert.assertEquals("ALERT", status);
        }
        // verify
        verifyAll();
    }

    @Test
    public void testUpdateDesiredConfigAsAdministrator() throws Exception {
        testUpdateDesiredConfig(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testUpdateDesiredConfigAsClusterAdministrator() throws Exception {
        testUpdateDesiredConfig(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testUpdateDesiredConfigAsServiceAdministrator() throws Exception {
        testUpdateDesiredConfig(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testUpdateDesiredConfigAsServiceOperator() throws Exception {
        testUpdateDesiredConfig(TestAuthenticationFactory.createServiceOperator());
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
    public void testUpdateResourcesAsServiceAdministrator() throws Exception {
        testUpdateResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testDeleteResourcesAsAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testDeleteResourcesAsClusterAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test(expected = AuthorizationException.class)
    public void testDeleteResourcesAsServiceAdministrator() throws Exception {
        testDeleteResources(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetHostsAsAdministrator() throws Exception {
        testGetHosts(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testGetHostsAsClusterAdministrator() throws Exception {
        testGetHosts(TestAuthenticationFactory.createClusterAdministrator());
    }

    @Test
    public void testGetHostsAsServiceAdministrator() throws Exception {
        testGetHosts(TestAuthenticationFactory.createServiceAdministrator());
    }

    @Test
    public void testGetHostsAsServiceOperator() throws Exception {
        testGetHosts(TestAuthenticationFactory.createServiceOperator());
    }

    /**
     * Ensure that HostNotFoundException is propagated in case where there is a single request.
     */
    @Test(expected = HostNotFoundException.class)
    public void testGetHosts___HostNotFoundException() throws Exception {
        // member state mocks
        Injector injector = createInjector();
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = createMock(Cluster.class);
        // requests
        HostRequest request1 = new HostRequest("host1", "cluster1");
        Set<HostRequest> setRequests = Collections.singleton(request1);
        // expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("cluster1")).andReturn(cluster);
        expect(clusters.getHost("host1")).andThrow(new HostNotFoundException("host1"));
        // replay mocks
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        HostResourceProviderTest.getHosts(managementController, setRequests);
        verifyAll();
    }

    /**
     * Ensure that HostNotFoundException is propagated in case where there is a single request.
     */
    @Test(expected = HostNotFoundException.class)
    public void testGetHosts___HostNotFoundException_HostNotAssociatedWithCluster() throws Exception {
        // member state mocks
        Injector injector = createInjector();
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = createMock(Cluster.class);
        Host host = createNiceMock(Host.class);
        // requests
        HostRequest request1 = new HostRequest("host1", "cluster1");
        Set<HostRequest> setRequests = Collections.singleton(request1);
        // expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("cluster1")).andReturn(cluster);
        expect(clusters.getHost("host1")).andReturn(host);
        expect(cluster.getDesiredConfigs()).andReturn(new HashMap()).anyTimes();
        expect(host.getHostName()).andReturn("host1").anyTimes();
        // because cluster is not in set will result in HostNotFoundException
        expect(clusters.getClustersForHost("host1")).andReturn(Collections.emptySet());
        // replay mocks
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        HostResourceProviderTest.getHosts(managementController, setRequests);
        verifyAll();
    }

    /**
     * Ensure that HostNotFoundException is handled where there are multiple requests as would be the
     * case when an OR predicate is provided in the query.
     */
    @Test
    public void testGetHosts___OR_Predicate_HostNotFoundException() throws Exception {
        // member state mocks
        Injector injector = createInjector();
        AmbariManagementController managementController = injector.getInstance(AmbariManagementController.class);
        Clusters clusters = injector.getInstance(Clusters.class);
        Cluster cluster = createMock(Cluster.class);
        Host host1 = createNiceMock(Host.class);
        Host host2 = createNiceMock(Host.class);
        HostResponse response = createNiceMock(HostResponse.class);
        HostResponse response2 = createNiceMock(HostResponse.class);
        // requests
        HostRequest request1 = new HostRequest("host1", "cluster1");
        HostRequest request2 = new HostRequest("host2", "cluster1");
        HostRequest request3 = new HostRequest("host3", "cluster1");
        HostRequest request4 = new HostRequest("host4", "cluster1");
        Set<HostRequest> setRequests = new HashSet<>();
        setRequests.add(request1);
        setRequests.add(request2);
        setRequests.add(request3);
        setRequests.add(request4);
        // expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("cluster1")).andReturn(cluster).times(4);
        expect(clusters.getHost("host1")).andReturn(host1);
        expect(host1.getHostName()).andReturn("host1").anyTimes();
        expect(clusters.getClustersForHost("host1")).andReturn(Collections.singleton(cluster));
        expect(host1.convertToResponse()).andReturn(response);
        response.setClusterName("cluster1");
        expect(clusters.getHost("host2")).andReturn(host2);
        expect(host2.getHostName()).andReturn("host2").anyTimes();
        expect(clusters.getClustersForHost("host2")).andReturn(Collections.singleton(cluster));
        expect(host2.convertToResponse()).andReturn(response2);
        response2.setClusterName("cluster1");
        expect(clusters.getHost("host3")).andThrow(new HostNotFoundException("host3"));
        expect(clusters.getHost("host4")).andThrow(new HostNotFoundException("host4"));
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(cluster.getDesiredConfigs()).andReturn(new HashMap()).anyTimes();
        // replay mocks
        replayAll();
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        // test
        Set<HostResponse> setResponses = HostResourceProviderTest.getHosts(managementController, setRequests);
        // assert and verify
        Assert.assertEquals(2, setResponses.size());
        Assert.assertTrue(setResponses.contains(response));
        Assert.assertTrue(setResponses.contains(response2));
        verifyAll();
    }
}

