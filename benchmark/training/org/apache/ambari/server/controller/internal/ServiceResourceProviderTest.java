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


import RepositoryType.PATCH;
import ResourceProviderEvent.Type.Delete;
import ServiceResourceProvider.SERVICE_CLUSTER_NAME_PROPERTY_ID;
import ServiceResourceProvider.SERVICE_DESIRED_STACK_PROPERTY_ID;
import ServiceResourceProvider.SERVICE_SERVICE_NAME_PROPERTY_ID;
import ServiceResourceProvider.SERVICE_SERVICE_STATE_PROPERTY_ID;
import State.INSTALLED;
import State.STARTED;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.KerberosHelper;
import org.apache.ambari.server.controller.MaintenanceStateHelper;
import org.apache.ambari.server.controller.ServiceResponse;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.serveraction.kerberos.KerberosAdminAuthenticationException;
import org.apache.ambari.server.serveraction.kerberos.KerberosMissingAdminCredentialsException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.ServiceFactory;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.State;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * ServiceResourceProvider tests.
 */
public class ServiceResourceProviderTest {
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
    public void testGetResources_KerberosSpecificProperties() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service0 = createNiceMock(Service.class);
        ServiceResponse serviceResponse0 = createNiceMock(ServiceResponse.class);
        StackId stackId = createNiceMock(StackId.class);
        ServiceFactory serviceFactory = createNiceMock(ServiceFactory.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        KerberosHelper kerberosHeper = createStrictMock(KerberosHelper.class);
        Map<String, Service> allResponseMap = new HashMap<>();
        allResponseMap.put("KERBEROS", service0);
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(managementController.getHostComponents(EasyMock.anyObject())).andReturn(Collections.emptySet()).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(cluster.getServices()).andReturn(allResponseMap).anyTimes();
        expect(cluster.getService("KERBEROS")).andReturn(service0);
        expect(service0.convertToResponse()).andReturn(serviceResponse0).anyTimes();
        expect(service0.getName()).andReturn("Service100").anyTimes();
        expect(serviceResponse0.getClusterName()).andReturn("Cluster100").anyTimes();
        expect(serviceResponse0.getServiceName()).andReturn("KERBEROS").anyTimes();
        kerberosHeper.validateKDCCredentials(cluster);
        // replay
        replay(managementController, clusters, cluster, service0, serviceResponse0, ambariMetaInfo, stackId, serviceFactory, kerberosHeper);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController);
        // set kerberos helper on provider
        Class<?> c = provider.getClass();
        Field f = c.getDeclaredField("kerberosHelper");
        f.setAccessible(true);
        f.set(provider, kerberosHeper);
        // create the request
        Predicate predicate = new PredicateBuilder().property(SERVICE_CLUSTER_NAME_PROPERTY_ID).equals("Cluster100").and().property(SERVICE_SERVICE_NAME_PROPERTY_ID).equals("KERBEROS").toPredicate();
        Request request = PropertyHelper.getReadRequest("ServiceInfo", "Services");
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            Assert.assertEquals("Cluster100", resource.getPropertyValue(SERVICE_CLUSTER_NAME_PROPERTY_ID));
            Assert.assertEquals("KERBEROS", resource.getPropertyValue(SERVICE_SERVICE_NAME_PROPERTY_ID));
            Assert.assertEquals("OK", resource.getPropertyValue("Services/attributes/kdc_validation_result"));
            Assert.assertEquals("", resource.getPropertyValue("Services/attributes/kdc_validation_failure_details"));
        }
        // verify
        verify(managementController, clusters, cluster, service0, serviceResponse0, ambariMetaInfo, stackId, serviceFactory, kerberosHeper);
    }

    @Test
    public void testGetResources_KerberosSpecificProperties_NoKDCValidation() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service0 = createNiceMock(Service.class);
        ServiceResponse serviceResponse0 = createNiceMock(ServiceResponse.class);
        StackId stackId = createNiceMock(StackId.class);
        ServiceFactory serviceFactory = createNiceMock(ServiceFactory.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        KerberosHelper kerberosHelper = createStrictMock(KerberosHelper.class);
        Map<String, Service> allResponseMap = new HashMap<>();
        allResponseMap.put("KERBEROS", service0);
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(managementController.getHostComponents(EasyMock.anyObject())).andReturn(Collections.emptySet()).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(cluster.getServices()).andReturn(allResponseMap).anyTimes();
        expect(cluster.getService("KERBEROS")).andReturn(service0);
        expect(service0.convertToResponse()).andReturn(serviceResponse0).anyTimes();
        expect(service0.getName()).andReturn("Service100").anyTimes();
        expect(serviceResponse0.getClusterName()).andReturn("Cluster100").anyTimes();
        expect(serviceResponse0.getServiceName()).andReturn("KERBEROS").anyTimes();
        // The following call should NOT be made
        // kerberosHelper.validateKDCCredentials(cluster);
        // replay
        replay(managementController, clusters, cluster, service0, serviceResponse0, ambariMetaInfo, stackId, serviceFactory, kerberosHelper);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController);
        // set kerberos helper on provider
        Class<?> c = provider.getClass();
        Field f = c.getDeclaredField("kerberosHelper");
        f.setAccessible(true);
        f.set(provider, kerberosHelper);
        // create the request
        Predicate predicate = new PredicateBuilder().property(SERVICE_CLUSTER_NAME_PROPERTY_ID).equals("Cluster100").and().property(SERVICE_SERVICE_NAME_PROPERTY_ID).equals("KERBEROS").toPredicate();
        Request request = PropertyHelper.getReadRequest("ServiceInfo");
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            Assert.assertEquals("Cluster100", resource.getPropertyValue(SERVICE_CLUSTER_NAME_PROPERTY_ID));
            Assert.assertEquals("KERBEROS", resource.getPropertyValue(SERVICE_SERVICE_NAME_PROPERTY_ID));
        }
        // verify
        verify(managementController, clusters, cluster, service0, serviceResponse0, ambariMetaInfo, stackId, serviceFactory, kerberosHelper);
    }

    @Test
    public void testGetResources_KerberosSpecificProperties_KDCInvalidCredentials() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service0 = createNiceMock(Service.class);
        ServiceResponse serviceResponse0 = createNiceMock(ServiceResponse.class);
        StackId stackId = createNiceMock(StackId.class);
        ServiceFactory serviceFactory = createNiceMock(ServiceFactory.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        KerberosHelper kerberosHeper = createStrictMock(KerberosHelper.class);
        Map<String, Service> allResponseMap = new HashMap<>();
        allResponseMap.put("KERBEROS", service0);
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(managementController.getHostComponents(EasyMock.anyObject())).andReturn(Collections.emptySet()).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(cluster.getServices()).andReturn(allResponseMap).anyTimes();
        expect(cluster.getService("KERBEROS")).andReturn(service0);
        expect(service0.convertToResponse()).andReturn(serviceResponse0).anyTimes();
        expect(service0.getName()).andReturn("Service100").anyTimes();
        expect(serviceResponse0.getClusterName()).andReturn("Cluster100").anyTimes();
        expect(serviceResponse0.getServiceName()).andReturn("KERBEROS").anyTimes();
        kerberosHeper.validateKDCCredentials(cluster);
        expectLastCall().andThrow(new KerberosAdminAuthenticationException("Invalid KDC administrator credentials."));
        // replay
        replay(managementController, clusters, cluster, service0, serviceResponse0, ambariMetaInfo, stackId, serviceFactory, kerberosHeper);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController);
        // set kerberos helper on provider
        Class<?> c = provider.getClass();
        Field f = c.getDeclaredField("kerberosHelper");
        f.setAccessible(true);
        f.set(provider, kerberosHeper);
        // create the request
        Predicate predicate = new PredicateBuilder().property(SERVICE_CLUSTER_NAME_PROPERTY_ID).equals("Cluster100").and().property(SERVICE_SERVICE_NAME_PROPERTY_ID).equals("KERBEROS").toPredicate();
        Request request = PropertyHelper.getReadRequest("ServiceInfo", "Services");
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            Assert.assertEquals("Cluster100", resource.getPropertyValue(SERVICE_CLUSTER_NAME_PROPERTY_ID));
            Assert.assertEquals("KERBEROS", resource.getPropertyValue(SERVICE_SERVICE_NAME_PROPERTY_ID));
            Assert.assertEquals("INVALID_CREDENTIALS", resource.getPropertyValue("Services/attributes/kdc_validation_result"));
            Assert.assertEquals("Invalid KDC administrator credentials.", resource.getPropertyValue("Services/attributes/kdc_validation_failure_details"));
        }
        // verify
        verify(managementController, clusters, cluster, service0, serviceResponse0, ambariMetaInfo, stackId, serviceFactory, kerberosHeper);
    }

    @Test
    public void testGetResources_KerberosSpecificProperties_KDCMissingCredentials() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service0 = createNiceMock(Service.class);
        ServiceResponse serviceResponse0 = createNiceMock(ServiceResponse.class);
        StackId stackId = createNiceMock(StackId.class);
        ServiceFactory serviceFactory = createNiceMock(ServiceFactory.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        KerberosHelper kerberosHeper = createStrictMock(KerberosHelper.class);
        Map<String, Service> allResponseMap = new HashMap<>();
        allResponseMap.put("KERBEROS", service0);
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(managementController.getHostComponents(EasyMock.anyObject())).andReturn(Collections.emptySet()).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(cluster.getServices()).andReturn(allResponseMap).anyTimes();
        expect(cluster.getService("KERBEROS")).andReturn(service0);
        expect(service0.convertToResponse()).andReturn(serviceResponse0).anyTimes();
        expect(service0.getName()).andReturn("Service100").anyTimes();
        expect(serviceResponse0.getClusterName()).andReturn("Cluster100").anyTimes();
        expect(serviceResponse0.getServiceName()).andReturn("KERBEROS").anyTimes();
        kerberosHeper.validateKDCCredentials(cluster);
        expectLastCall().andThrow(new KerberosMissingAdminCredentialsException("Missing KDC administrator credentials."));
        // replay
        replay(managementController, clusters, cluster, service0, serviceResponse0, ambariMetaInfo, stackId, serviceFactory, kerberosHeper);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController);
        // set kerberos helper on provider
        Class<?> c = provider.getClass();
        Field f = c.getDeclaredField("kerberosHelper");
        f.setAccessible(true);
        f.set(provider, kerberosHeper);
        // create the request
        Predicate predicate = new PredicateBuilder().property(SERVICE_CLUSTER_NAME_PROPERTY_ID).equals("Cluster100").and().property(SERVICE_SERVICE_NAME_PROPERTY_ID).equals("KERBEROS").toPredicate();
        Request request = PropertyHelper.getReadRequest("ServiceInfo", "Services");
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            Assert.assertEquals("Cluster100", resource.getPropertyValue(SERVICE_CLUSTER_NAME_PROPERTY_ID));
            Assert.assertEquals("KERBEROS", resource.getPropertyValue(SERVICE_SERVICE_NAME_PROPERTY_ID));
            Assert.assertEquals("MISSING_CREDENTIALS", resource.getPropertyValue("Services/attributes/kdc_validation_result"));
            Assert.assertEquals("Missing KDC administrator credentials.", resource.getPropertyValue("Services/attributes/kdc_validation_failure_details"));
        }
        // verify
        verify(managementController, clusters, cluster, service0, serviceResponse0, ambariMetaInfo, stackId, serviceFactory, kerberosHeper);
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
    public void testReconfigureClientsFlagAsAdministrator() throws Exception {
        testReconfigureClientsFlag(TestAuthenticationFactory.createAdministrator());
    }

    @Test
    public void testReconfigureClientsFlagAsClusterAdministrator() throws Exception {
        testReconfigureClientsFlag(TestAuthenticationFactory.createAdministrator("clusterAdmin"));
    }

    @Test
    public void testReconfigureClientsFlagAsServiceAdministrator() throws Exception {
        testReconfigureClientsFlag(TestAuthenticationFactory.createServiceAdministrator());
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
    public void testDeleteResourcesBadServiceState() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service = createNiceMock(Service.class);
        String serviceName = "Service100";
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(cluster.getService(serviceName)).andReturn(service).anyTimes();
        expect(service.getDesiredState()).andReturn(STARTED).anyTimes();
        expect(service.getName()).andReturn(serviceName).anyTimes();
        expect(service.getServiceComponents()).andReturn(new HashMap());
        expect(service.getCluster()).andReturn(cluster);
        cluster.deleteService(eq(serviceName), anyObject(DeleteHostComponentStatusMetaData.class));
        // replay
        replay(managementController, clusters, cluster, service);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        // delete the service named Service100
        Predicate predicate = new PredicateBuilder().property(SERVICE_CLUSTER_NAME_PROPERTY_ID).equals("Cluster100").and().property(SERVICE_SERVICE_NAME_PROPERTY_ID).equals(serviceName).toPredicate();
        provider.deleteResources(new RequestImpl(null, null, null, null), predicate);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Resource.Type.Service, lastEvent.getResourceType());
        Assert.assertEquals(Delete, lastEvent.getType());
        Assert.assertEquals(predicate, lastEvent.getPredicate());
        Assert.assertNull(lastEvent.getRequest());
        // verify
        verify(managementController, clusters, cluster, service);
    }

    @Test
    public void testDeleteResourcesBadComponentState() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service = createNiceMock(Service.class);
        ServiceComponent sc = createNiceMock(ServiceComponent.class);
        Map<String, ServiceComponent> scMap = new HashMap<>();
        scMap.put("Component100", sc);
        State componentState = State.STARTED;
        ServiceComponentHost sch = createNiceMock(ServiceComponentHost.class);
        Map<String, ServiceComponentHost> schMap = new HashMap<>();
        schMap.put("Host1", sch);
        State schState = State.STARTED;
        String serviceName = "Service100";
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(cluster.getService(serviceName)).andReturn(service).anyTimes();
        expect(service.getDesiredState()).andReturn(INSTALLED).anyTimes();
        expect(service.getName()).andReturn(serviceName).anyTimes();
        expect(service.getServiceComponents()).andReturn(scMap);
        expect(sc.getDesiredState()).andReturn(componentState).anyTimes();
        expect(sc.getName()).andReturn("Component100").anyTimes();
        expect(sc.canBeRemoved()).andReturn(componentState.isRemovableState()).anyTimes();
        expect(sc.getServiceComponentHosts()).andReturn(schMap).anyTimes();
        expect(sch.getDesiredState()).andReturn(schState).anyTimes();
        expect(sch.canBeRemoved()).andReturn(schState.isRemovableState()).anyTimes();
        // replay
        replay(managementController, clusters, cluster, service, sc, sch);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        // delete the service named Service100
        Predicate predicate = new PredicateBuilder().property(SERVICE_CLUSTER_NAME_PROPERTY_ID).equals("Cluster100").and().property(SERVICE_SERVICE_NAME_PROPERTY_ID).equals(serviceName).toPredicate();
        try {
            provider.deleteResources(new RequestImpl(null, null, null, null), predicate);
            Assert.fail("Expected exception deleting a service in a non-removable state.");
        } catch (SystemException e) {
            // expected
        }
    }

    /* If the host components of a service are in a removable state, the service should be removable even if it's state is non-removable */
    @Test
    public void testDeleteResourcesStoppedHostComponentState() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service = createNiceMock(Service.class);
        // 
        // Data structure for holding ServiceComponent information
        // 
        class TestComponent {
            public String Name;

            public ServiceComponent Component;

            public State DesiredState;

            public TestComponent(String name, ServiceComponent component, State desiredState) {
                Name = name;
                Component = component;
                DesiredState = desiredState;
            }
        }
        // Set up three components in STARTED state.
        // 
        TestComponent component1 = new TestComponent("Component100", createNiceMock(ServiceComponent.class), State.STARTED);
        TestComponent component2 = new TestComponent("Component101", createNiceMock(ServiceComponent.class), State.STARTED);
        TestComponent component3 = new TestComponent("Component102", createNiceMock(ServiceComponent.class), State.STARTED);
        Map<String, ServiceComponent> scMap = new HashMap<>();
        scMap.put(component1.Name, component1.Component);
        scMap.put(component2.Name, component2.Component);
        scMap.put(component3.Name, component3.Component);
        Map<String, ServiceComponentHost> schMap1 = new HashMap<>();
        ServiceComponentHost sch1 = createNiceMock(ServiceComponentHost.class);
        schMap1.put("Host1", sch1);
        Map<String, ServiceComponentHost> schMap2 = new HashMap<>();
        ServiceComponentHost sch2 = createNiceMock(ServiceComponentHost.class);
        schMap2.put("Host2", sch2);
        Map<String, ServiceComponentHost> schMap3 = new HashMap<>();
        ServiceComponentHost sch3 = createNiceMock(ServiceComponentHost.class);
        schMap3.put("Host3", sch3);
        String clusterName = "Cluster100";
        String serviceName = "Service100";
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster(clusterName)).andReturn(cluster).anyTimes();
        expect(cluster.getService(serviceName)).andReturn(service).anyTimes();
        expect(service.getDesiredState()).andReturn(STARTED).anyTimes();// Service is in a non-removable state

        expect(service.getName()).andReturn(serviceName).anyTimes();
        expect(service.getServiceComponents()).andReturn(scMap).anyTimes();
        expect(component1.Component.getDesiredState()).andReturn(component1.DesiredState).anyTimes();
        expect(component2.Component.getDesiredState()).andReturn(component2.DesiredState).anyTimes();
        expect(component3.Component.getDesiredState()).andReturn(component3.DesiredState).anyTimes();
        expect(component1.Component.canBeRemoved()).andReturn(component1.DesiredState.isRemovableState()).anyTimes();
        expect(component2.Component.canBeRemoved()).andReturn(component2.DesiredState.isRemovableState()).anyTimes();
        expect(component3.Component.canBeRemoved()).andReturn(component3.DesiredState.isRemovableState()).anyTimes();
        expect(component1.Component.getServiceComponentHosts()).andReturn(schMap1).anyTimes();
        expect(component2.Component.getServiceComponentHosts()).andReturn(schMap2).anyTimes();
        expect(component3.Component.getServiceComponentHosts()).andReturn(schMap3).anyTimes();
        // Put the SCH in INSTALLED state so that the service can be deleted,
        // no matter what state the service component is in.
        State sch1State = State.INSTALLED;
        expect(sch1.getDesiredState()).andReturn(sch1State).anyTimes();
        expect(sch1.canBeRemoved()).andReturn(sch1State.isRemovableState()).anyTimes();
        State sch2State = State.INSTALLED;
        expect(sch2.getDesiredState()).andReturn(sch2State).anyTimes();
        expect(sch2.canBeRemoved()).andReturn(sch2State.isRemovableState()).anyTimes();
        State sch3State = State.INSTALLED;
        expect(sch3.getDesiredState()).andReturn(sch3State).anyTimes();
        expect(sch3.canBeRemoved()).andReturn(sch3State.isRemovableState()).anyTimes();
        expect(service.getCluster()).andReturn(cluster);
        cluster.deleteService(eq(serviceName), anyObject(DeleteHostComponentStatusMetaData.class));
        // replay
        replay(managementController, clusters, cluster, service, component1.Component, component2.Component, component3.Component, sch1, sch2, sch3);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController);
        AbstractResourceProviderTest.TestObserver observer = new AbstractResourceProviderTest.TestObserver();
        addObserver(observer);
        // delete the service named Service100
        Predicate predicate = new PredicateBuilder().property(SERVICE_CLUSTER_NAME_PROPERTY_ID).equals(clusterName).and().property(SERVICE_SERVICE_NAME_PROPERTY_ID).equals(serviceName).toPredicate();
        provider.deleteResources(new RequestImpl(null, null, null, null), predicate);
        ResourceProviderEvent lastEvent = observer.getLastEvent();
        Assert.assertNotNull(lastEvent);
        Assert.assertEquals(Resource.Type.Service, lastEvent.getResourceType());
        Assert.assertEquals(Delete, lastEvent.getType());
        Assert.assertEquals(predicate, lastEvent.getPredicate());
        Assert.assertNull(lastEvent.getRequest());
        // verify
        verify(managementController, clusters, cluster, service, component1.Component, component2.Component, component3.Component, sch1, sch2, sch3);
    }

    @Test
    public void testCheckPropertyIds() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        MaintenanceStateHelper maintenanceStateHelperMock = createNiceMock(MaintenanceStateHelper.class);
        RepositoryVersionDAO repositoryVersionDAO = createNiceMock(RepositoryVersionDAO.class);
        replay(maintenanceStateHelperMock, repositoryVersionDAO);
        AbstractResourceProvider provider = new ServiceResourceProvider(managementController, maintenanceStateHelperMock, repositoryVersionDAO);
        Set<String> unsupported = provider.checkPropertyIds(Collections.singleton(SERVICE_CLUSTER_NAME_PROPERTY_ID));
        Assert.assertTrue(unsupported.isEmpty());
        // note that key is not in the set of known property ids.  We allow it if its parent is a known property.
        // this allows for Map type properties where we want to treat the entries as individual properties
        String subKey = PropertyHelper.getPropertyId(SERVICE_CLUSTER_NAME_PROPERTY_ID, "key");
        unsupported = provider.checkPropertyIds(Collections.singleton(subKey));
        Assert.assertTrue(unsupported.isEmpty());
        unsupported = provider.checkPropertyIds(Collections.singleton("bar"));
        Assert.assertEquals(1, unsupported.size());
        Assert.assertTrue(unsupported.contains("bar"));
        for (String propertyId : provider.getPKPropertyIds()) {
            unsupported = provider.checkPropertyIds(Collections.singleton(propertyId));
            Assert.assertTrue(unsupported.isEmpty());
        }
    }

    @Test
    public void testCreateWithNoRepositoryId() throws Exception {
        AmbariManagementController managementController = createNiceMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service1 = createNiceMock(Service.class);
        Service service2 = createNiceMock(Service.class);
        RepositoryVersionEntity repoVersion = createNiceMock(RepositoryVersionEntity.class);
        expect(repoVersion.getId()).andReturn(500L).anyTimes();
        expect(service1.getDesiredRepositoryVersion()).andReturn(repoVersion).atLeastOnce();
        StackId stackId = new StackId("HDP-2.5");
        ServiceFactory serviceFactory = createNiceMock(ServiceFactory.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        ServiceInfo serviceInfo = createNiceMock(ServiceInfo.class);
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(cluster.addService(eq("Service200"), EasyMock.anyObject(RepositoryVersionEntity.class))).andReturn(service2);
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(cluster.getServices()).andReturn(ImmutableMap.<String, Service>builder().put("Service100", service1).build()).atLeastOnce();
        expect(cluster.getDesiredStackVersion()).andReturn(stackId).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(ambariMetaInfo.isValidService(((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())))).andReturn(true);
        expect(ambariMetaInfo.getService(((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())))).andReturn(serviceInfo).anyTimes();
        // replay
        replay(managementController, clusters, cluster, service1, service2, ambariMetaInfo, serviceFactory, serviceInfo, repoVersion);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        Capture<Long> pkCapture = Capture.newInstance();
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController, true, pkCapture);
        // add the property map to a set for the request.  add more maps for multiple creates
        Set<Map<String, Object>> propertySet = new LinkedHashSet<>();
        // Service 1: create a map of properties for the request
        Map<String, Object> properties = new LinkedHashMap<>();
        // add properties to the request map
        properties.put(SERVICE_CLUSTER_NAME_PROPERTY_ID, "Cluster100");
        properties.put(SERVICE_SERVICE_NAME_PROPERTY_ID, "Service200");
        properties.put(SERVICE_SERVICE_STATE_PROPERTY_ID, "INIT");
        properties.put(SERVICE_DESIRED_STACK_PROPERTY_ID, "HDP-1.1");
        propertySet.add(properties);
        // create the request
        Request request = PropertyHelper.getCreateRequest(propertySet, null);
        provider.createResources(request);
        // verify
        verify(managementController, clusters, cluster, service1, service2, ambariMetaInfo, serviceFactory, serviceInfo);
        Assert.assertTrue(pkCapture.hasCaptured());
        Assert.assertEquals(Long.valueOf(500L), pkCapture.getValue());
    }

    @Test
    public void testCreateWithNoRepositoryIdAndPatch() throws Exception {
        AmbariManagementController managementController = createNiceMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service1 = createNiceMock(Service.class);
        Service service2 = createNiceMock(Service.class);
        RepositoryVersionEntity repoVersion = createNiceMock(RepositoryVersionEntity.class);
        expect(repoVersion.getId()).andReturn(500L).anyTimes();
        expect(repoVersion.getParentId()).andReturn(600L).anyTimes();
        expect(repoVersion.getType()).andReturn(PATCH).anyTimes();
        expect(service1.getDesiredRepositoryVersion()).andReturn(repoVersion).atLeastOnce();
        StackId stackId = new StackId("HDP-2.5");
        ServiceFactory serviceFactory = createNiceMock(ServiceFactory.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        ServiceInfo serviceInfo = createNiceMock(ServiceInfo.class);
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(cluster.addService(eq("Service200"), EasyMock.anyObject(RepositoryVersionEntity.class))).andReturn(service2);
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(cluster.getServices()).andReturn(ImmutableMap.<String, Service>builder().put("Service100", service1).build()).atLeastOnce();
        expect(cluster.getDesiredStackVersion()).andReturn(stackId).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(ambariMetaInfo.isValidService(((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())))).andReturn(true);
        expect(ambariMetaInfo.getService(((String) (anyObject())), ((String) (anyObject())), ((String) (anyObject())))).andReturn(serviceInfo).anyTimes();
        // replay
        replay(managementController, clusters, cluster, service1, service2, ambariMetaInfo, serviceFactory, serviceInfo, repoVersion);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        Capture<Long> pkCapture = Capture.newInstance();
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController, true, pkCapture);
        // add the property map to a set for the request.  add more maps for multiple creates
        Set<Map<String, Object>> propertySet = new LinkedHashSet<>();
        // Service 1: create a map of properties for the request
        Map<String, Object> properties = new LinkedHashMap<>();
        // add properties to the request map
        properties.put(SERVICE_CLUSTER_NAME_PROPERTY_ID, "Cluster100");
        properties.put(SERVICE_SERVICE_NAME_PROPERTY_ID, "Service200");
        properties.put(SERVICE_SERVICE_STATE_PROPERTY_ID, "INIT");
        properties.put(SERVICE_DESIRED_STACK_PROPERTY_ID, "HDP-1.1");
        propertySet.add(properties);
        // create the request
        Request request = PropertyHelper.getCreateRequest(propertySet, null);
        provider.createResources(request);
        // verify
        verify(managementController, clusters, cluster, service1, service2, ambariMetaInfo, serviceFactory, serviceInfo);
        Assert.assertTrue(pkCapture.hasCaptured());
        Assert.assertEquals(Long.valueOf(600L), pkCapture.getValue());
    }

    @Test
    public void testCreateWithNoRepositoryIdAndMultiBase() throws Exception {
        AmbariManagementController managementController = createNiceMock(AmbariManagementController.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service1 = createNiceMock(Service.class);
        Service service2 = createNiceMock(Service.class);
        Service service3 = createNiceMock(Service.class);
        RepositoryVersionEntity repoVersion1 = createNiceMock(RepositoryVersionEntity.class);
        expect(repoVersion1.getId()).andReturn(500L).anyTimes();
        expect(service1.getDesiredRepositoryVersion()).andReturn(repoVersion1).atLeastOnce();
        RepositoryVersionEntity repoVersion2 = createNiceMock(RepositoryVersionEntity.class);
        expect(repoVersion2.getId()).andReturn(600L).anyTimes();
        expect(service2.getDesiredRepositoryVersion()).andReturn(repoVersion2).atLeastOnce();
        StackId stackId = new StackId("HDP-2.5");
        ServiceFactory serviceFactory = createNiceMock(ServiceFactory.class);
        AmbariMetaInfo ambariMetaInfo = createNiceMock(AmbariMetaInfo.class);
        ServiceInfo serviceInfo = createNiceMock(ServiceInfo.class);
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.getAmbariMetaInfo()).andReturn(ambariMetaInfo).anyTimes();
        expect(clusters.getCluster("Cluster100")).andReturn(cluster).anyTimes();
        expect(cluster.getServices()).andReturn(ImmutableMap.<String, Service>builder().put("Service100", service1).put("Service200", service2).build()).atLeastOnce();
        expect(cluster.getDesiredStackVersion()).andReturn(stackId).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        // replay
        replay(managementController, clusters, cluster, service1, service2, service3, ambariMetaInfo, serviceFactory, serviceInfo, repoVersion1, repoVersion2);
        SecurityContextHolder.getContext().setAuthentication(TestAuthenticationFactory.createAdministrator());
        Capture<Long> pkCapture = Capture.newInstance();
        ResourceProvider provider = ServiceResourceProviderTest.getServiceProvider(managementController, true, pkCapture);
        // add the property map to a set for the request.  add more maps for multiple creates
        Set<Map<String, Object>> propertySet = new LinkedHashSet<>();
        // Service 1: create a map of properties for the request
        Map<String, Object> properties = new LinkedHashMap<>();
        // add properties to the request map
        properties.put(SERVICE_CLUSTER_NAME_PROPERTY_ID, "Cluster100");
        properties.put(SERVICE_SERVICE_NAME_PROPERTY_ID, "Service200");
        properties.put(SERVICE_SERVICE_STATE_PROPERTY_ID, "INIT");
        properties.put(SERVICE_DESIRED_STACK_PROPERTY_ID, "HDP-1.1");
        propertySet.add(properties);
        // create the request
        Request request = PropertyHelper.getCreateRequest(propertySet, null);
        try {
            provider.createResources(request);
            Assert.fail("Expected an exception when more than one base version was found");
        } catch (IllegalArgumentException expected) {
            // !!! expected
        }
        // verify
        verify(managementController, clusters, cluster, service1, service2, service3, ambariMetaInfo, serviceFactory, serviceInfo);
    }
}

