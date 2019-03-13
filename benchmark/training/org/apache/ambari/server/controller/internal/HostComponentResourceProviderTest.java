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


import HostComponentResourceProvider.CLUSTER_NAME;
import HostComponentResourceProvider.COMPONENT_NAME;
import HostComponentResourceProvider.STATE;
import Resource.Type;
import State.INSTALLED;
import State.STARTED;
import com.google.inject.Injector;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.AmbariManagementControllerImpl;
import org.apache.ambari.server.controller.MaintenanceStateHelper;
import org.apache.ambari.server.controller.ResourceProviderFactory;
import org.apache.ambari.server.controller.ServiceComponentHostResponse;
import org.apache.ambari.server.controller.spi.NoSuchResourceException;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.dao.HostVersionDAO;
import org.apache.ambari.server.security.TestAuthenticationFactory;
import org.apache.ambari.server.security.authorization.AuthorizationException;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.State;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * HostComponentResourceProvider tests.
 */
public class HostComponentResourceProviderTest {
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
    public void testCheckPropertyIds() throws Exception {
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        Injector injector = createNiceMock(Injector.class);
        HostComponentResourceProvider provider = new HostComponentResourceProvider(managementController);
        Set<String> unsupported = provider.checkPropertyIds(Collections.singleton(PropertyHelper.getPropertyId("HostRoles", "cluster_name")));
        Assert.assertTrue(unsupported.isEmpty());
        // note that key is not in the set of known property ids.  We allow it if its parent is a known property.
        // this allows for Map type properties where we want to treat the entries as individual properties
        Assert.assertTrue(provider.checkPropertyIds(Collections.singleton(PropertyHelper.getPropertyId("HostRoles/service_name", "key"))).isEmpty());
        unsupported = provider.checkPropertyIds(Collections.singleton("bar"));
        Assert.assertEquals(1, unsupported.size());
        Assert.assertTrue(unsupported.contains("bar"));
        unsupported = provider.checkPropertyIds(Collections.singleton(PropertyHelper.getPropertyId("HostRoles", "component_name")));
        Assert.assertTrue(unsupported.isEmpty());
        unsupported = provider.checkPropertyIds(Collections.singleton("HostRoles"));
        Assert.assertTrue(unsupported.isEmpty());
        unsupported = provider.checkPropertyIds(Collections.singleton("config"));
        Assert.assertTrue(unsupported.isEmpty());
        unsupported = provider.checkPropertyIds(Collections.singleton("config/unknown_property"));
        Assert.assertTrue(unsupported.isEmpty());
    }

    @Test
    public void testUpdateResourcesNothingToUpdate() throws Exception {
        Authentication authentication = TestAuthenticationFactory.createServiceAdministrator();
        Resource.Type type = Type.HostComponent;
        AmbariManagementController managementController = createMock(AmbariManagementController.class);
        HostVersionDAO hostVersionDAO = createMock(HostVersionDAO.class);
        // RequestStatusResponse response = createNiceMock(RequestStatusResponse.class);
        ResourceProviderFactory resourceProviderFactory = createNiceMock(ResourceProviderFactory.class);
        Injector injector = createNiceMock(Injector.class);
        Clusters clusters = createNiceMock(Clusters.class);
        Cluster cluster = createNiceMock(Cluster.class);
        Service service = createNiceMock(Service.class);
        ServiceComponent component = createNiceMock(ServiceComponent.class);
        ServiceComponentHost componentHost = createNiceMock(ServiceComponentHost.class);
        RequestStageContainer stageContainer = createNiceMock(RequestStageContainer.class);
        MaintenanceStateHelper maintenanceStateHelper = createNiceMock(MaintenanceStateHelper.class);
        Map<String, String> mapRequestProps = new HashMap<>();
        mapRequestProps.put("context", "Called from a test");
        Set<ServiceComponentHostResponse> nameResponse = new HashSet<>();
        nameResponse.add(new ServiceComponentHostResponse("Cluster102", "Service100", "Component100", "Component 100", "Host100", "Host100", "INSTALLED", "", "", "", "", null));
        // set expectations
        expect(managementController.getClusters()).andReturn(clusters).anyTimes();
        expect(managementController.findServiceName(cluster, "Component100")).andReturn("Service100").anyTimes();
        expect(clusters.getCluster("Cluster102")).andReturn(cluster).anyTimes();
        expect(cluster.getClusterId()).andReturn(2L).anyTimes();
        expect(cluster.getService("Service100")).andReturn(service).anyTimes();
        expect(service.getServiceComponent("Component100")).andReturn(component).anyTimes();
        expect(component.getServiceComponentHost("Host100")).andReturn(componentHost).anyTimes();
        expect(component.getName()).andReturn("Component100").anyTimes();
        expect(componentHost.getState()).andReturn(INSTALLED).anyTimes();
        // Cluster is default type.  Maintenance mode is not being tested here so the default is returned.
        expect(maintenanceStateHelper.isOperationAllowed(Resource.Type.Cluster, componentHost)).andReturn(true).anyTimes();
        expect(managementController.getHostComponents(EasyMock.anyObject())).andReturn(Collections.emptySet()).once();
        Map<String, Map<State, List<ServiceComponentHost>>> changedHosts = new HashMap<>();
        List<ServiceComponentHost> changedComponentHosts = new ArrayList<>();
        changedComponentHosts.add(componentHost);
        changedHosts.put("Component100", Collections.singletonMap(STARTED, changedComponentHosts));
        HostComponentResourceProviderTest.TestHostComponentResourceProvider provider = new HostComponentResourceProviderTest.TestHostComponentResourceProvider(PropertyHelper.getPropertyIds(type), PropertyHelper.getKeyPropertyIds(type), managementController, injector);
        provider.setFieldValue("maintenanceStateHelper", maintenanceStateHelper);
        provider.setFieldValue("hostVersionDAO", hostVersionDAO);
        expect(resourceProviderFactory.getHostComponentResourceProvider(eq(managementController))).andReturn(provider).anyTimes();
        // replay
        replay(managementController, resourceProviderFactory, clusters, cluster, service, component, componentHost, stageContainer, maintenanceStateHelper);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put(STATE, "STARTED");
        // create the request
        Request request = PropertyHelper.getUpdateRequest(properties, mapRequestProps);
        // update the cluster named Cluster102
        Predicate predicate = new PredicateBuilder().property(CLUSTER_NAME).equals("Cluster102").and().property(STATE).equals("INSTALLED").and().property(COMPONENT_NAME).equals("Component100").toPredicate();
        try {
            provider.updateResources(request, predicate);
            Assert.fail("Expected exception when no resources are found to be updatable");
        } catch (NoSuchResourceException e) {
            // !!! expected
        }
        // verify
        verify(managementController, resourceProviderFactory, stageContainer);
    }

    @Test
    public void doesNotSkipInstallTaskForClient() {
        String component = "SOME_COMPONENT";
        Assert.assertFalse(HostComponentResourceProvider.shouldSkipInstallTaskForComponent(component, true, new HostComponentResourceProviderTest.RequestInfoBuilder().skipInstall(component).build()));
        Assert.assertFalse(HostComponentResourceProvider.shouldSkipInstallTaskForComponent(component, true, new HostComponentResourceProviderTest.RequestInfoBuilder().skipInstall(HostComponentResourceProvider.ALL_COMPONENTS).build()));
    }

    @Test
    public void doesNotSkipInstallTaskForOtherPhase() {
        String component = "SOME_COMPONENT";
        HostComponentResourceProviderTest.RequestInfoBuilder requestInfoBuilder = new HostComponentResourceProviderTest.RequestInfoBuilder().phase("INSTALL");
        Assert.assertFalse(HostComponentResourceProvider.shouldSkipInstallTaskForComponent(component, false, requestInfoBuilder.skipInstall(component).build()));
        Assert.assertFalse(HostComponentResourceProvider.shouldSkipInstallTaskForComponent(component, false, requestInfoBuilder.skipInstall(HostComponentResourceProvider.ALL_COMPONENTS).build()));
    }

    @Test
    public void doesNotSkipInstallTaskForExplicitException() {
        String component = "SOME_COMPONENT";
        HostComponentResourceProviderTest.RequestInfoBuilder requestInfoBuilder = new HostComponentResourceProviderTest.RequestInfoBuilder().skipInstall(HostComponentResourceProvider.ALL_COMPONENTS).doNotSkipInstall(component);
        Assert.assertFalse(HostComponentResourceProvider.shouldSkipInstallTaskForComponent(component, false, requestInfoBuilder.build()));
    }

    @Test
    public void skipsInstallTaskIfRequested() {
        String component = "SOME_COMPONENT";
        HostComponentResourceProviderTest.RequestInfoBuilder requestInfoBuilder = new HostComponentResourceProviderTest.RequestInfoBuilder().skipInstall(component);
        Assert.assertTrue(HostComponentResourceProvider.shouldSkipInstallTaskForComponent(component, false, requestInfoBuilder.build()));
    }

    @Test
    public void skipsInstallTaskForAll() {
        HostComponentResourceProviderTest.RequestInfoBuilder requestInfoBuilder = new HostComponentResourceProviderTest.RequestInfoBuilder().skipInstall(HostComponentResourceProvider.ALL_COMPONENTS);
        Assert.assertTrue(HostComponentResourceProvider.shouldSkipInstallTaskForComponent("ANY_COMPONENT", false, requestInfoBuilder.build()));
    }

    @Test
    public void doesNotSkipInstallOfPrefixedComponent() {
        String prefix = "HIVE_SERVER";
        String component = prefix + "_INTERACTIVE";
        Map<String, String> requestInfo = new HostComponentResourceProviderTest.RequestInfoBuilder().skipInstall(component).build();
        Assert.assertTrue(HostComponentResourceProvider.shouldSkipInstallTaskForComponent(component, false, requestInfo));
        Assert.assertFalse(HostComponentResourceProvider.shouldSkipInstallTaskForComponent(prefix, false, requestInfo));
    }

    private static class RequestInfoBuilder {
        private String phase = AmbariManagementControllerImpl.CLUSTER_PHASE_INITIAL_INSTALL;

        private final Collection<String> skipInstall = new LinkedList<>();

        private final Collection<String> doNotSkipInstall = new LinkedList<>();

        public HostComponentResourceProviderTest.RequestInfoBuilder skipInstall(String... components) {
            skipInstall.clear();
            skipInstall.addAll(Arrays.asList(components));
            return this;
        }

        public HostComponentResourceProviderTest.RequestInfoBuilder doNotSkipInstall(String... components) {
            doNotSkipInstall.clear();
            doNotSkipInstall.addAll(Arrays.asList(components));
            return this;
        }

        public HostComponentResourceProviderTest.RequestInfoBuilder phase(String phase) {
            this.phase = phase;
            return this;
        }

        public Map<String, String> build() {
            Map<String, String> info = new HashMap<>();
            if ((phase) != null) {
                info.put(AmbariManagementControllerImpl.CLUSTER_PHASE_PROPERTY, phase);
            }
            HostComponentResourceProvider.addProvisionActionProperties(skipInstall, doNotSkipInstall, info);
            return info;
        }
    }

    private static class TestHostComponentResourceProvider extends HostComponentResourceProvider {
        /**
         * Create a  new resource provider for the given management controller.
         *
         * @param propertyIds
         * 		the property ids
         * @param keyPropertyIds
         * 		the key property ids
         * @param managementController
         * 		the management controller
         */
        public TestHostComponentResourceProvider(Set<String> propertyIds, Map<Resource.Type, String> keyPropertyIds, AmbariManagementController managementController, Injector injector) throws Exception {
            super(managementController);
        }

        public void setFieldValue(String fieldName, Object fieldValue) throws Exception {
            Class<?> c = getClass().getSuperclass();
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(this, fieldValue);
        }
    }
}

