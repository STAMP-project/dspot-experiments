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
package org.apache.ambari.server.topology;


import RepositoryType.STANDARD;
import ServiceResourceProvider.SERVICE_CLUSTER_NAME_PROPERTY_ID;
import ServiceResourceProvider.SERVICE_SERVICE_STATE_PROPERTY_ID;
import TopologyManager.INITIAL_CONFIG_TAG;
import TopologyManager.TOPOLOGY_RESOLVED_TAG;
import VersionDefinitionResourceProvider.VERSION_DEF_ID;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.ClusterRequest;
import org.apache.ambari.server.controller.ConfigGroupRequest;
import org.apache.ambari.server.controller.ServiceComponentHostRequest;
import org.apache.ambari.server.controller.ServiceComponentRequest;
import org.apache.ambari.server.controller.ServiceRequest;
import org.apache.ambari.server.controller.internal.ComponentResourceProvider;
import org.apache.ambari.server.controller.internal.ConfigGroupResourceProvider;
import org.apache.ambari.server.controller.internal.HostComponentResourceProvider;
import org.apache.ambari.server.controller.internal.HostResourceProvider;
import org.apache.ambari.server.controller.internal.ServiceResourceProvider;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.controller.internal.VersionDefinitionResourceProvider;
import org.apache.ambari.server.controller.spi.ClusterController;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.RequestStatus;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Config;
import org.apache.ambari.server.state.ConfigFactory;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.DesiredConfig;
import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.configgroup.ConfigGroup;
import org.apache.ambari.server.utils.Assertions;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * AmbariContext unit tests
 */
// todo: switch over to EasyMockSupport
public class AmbariContextTest {
    private static final String BP_NAME = "testBP";

    private static final String CLUSTER_NAME = "testCluster";

    private static final long CLUSTER_ID = 1L;

    private static final String STACK_NAME = "testStack";

    private static final String STACK_VERSION = "testVersion";

    private static final String HOST_GROUP_1 = "group1";

    private static final String HOST_GROUP_2 = "group2";

    private static final String HOST1 = "host1";

    private static final String HOST2 = "host2";

    private static final StackId STACK_ID = new StackId(AmbariContextTest.STACK_NAME, AmbariContextTest.STACK_VERSION);

    private static final AmbariContext context = new AmbariContext();

    private static final AmbariManagementController controller = createNiceMock(AmbariManagementController.class);

    private static final ClusterController clusterController = createStrictMock(ClusterController.class);

    private static final HostResourceProvider hostResourceProvider = createStrictMock(HostResourceProvider.class);

    private static final ServiceResourceProvider serviceResourceProvider = createStrictMock(ServiceResourceProvider.class);

    private static final ComponentResourceProvider componentResourceProvider = createStrictMock(ComponentResourceProvider.class);

    private static final HostComponentResourceProvider hostComponentResourceProvider = createStrictMock(HostComponentResourceProvider.class);

    private static final ConfigGroupResourceProvider configGroupResourceProvider = createStrictMock(ConfigGroupResourceProvider.class);

    private static final ClusterTopology topology = createNiceMock(ClusterTopology.class);

    private static final Blueprint blueprint = createNiceMock(Blueprint.class);

    private static final Stack stack = createNiceMock(Stack.class);

    private static final Clusters clusters = createNiceMock(Clusters.class);

    private static final Cluster cluster = createNiceMock(Cluster.class);

    private static final HostGroupInfo group1Info = createNiceMock(HostGroupInfo.class);

    private static final ConfigHelper configHelper = createNiceMock(ConfigHelper.class);

    private static final ConfigGroup configGroup1 = createMock(ConfigGroup.class);

    private static final ConfigGroup configGroup2 = createMock(ConfigGroup.class);

    private static final Host host1 = createNiceMock(Host.class);

    private static final Host host2 = createNiceMock(Host.class);

    private static final ConfigFactory configFactory = createNiceMock(ConfigFactory.class);

    private static final Service mockService1 = createStrictMock(Service.class);

    private static final Collection<String> blueprintServices = new HashSet<>();

    private static final Map<String, Service> clusterServices = new HashMap<>();

    private static final Map<Long, ConfigGroup> configGroups = new HashMap<>();

    private Configuration bpConfiguration = null;

    private Configuration group1Configuration = null;

    private static final Collection<String> group1Hosts = Arrays.asList(AmbariContextTest.HOST1, AmbariContextTest.HOST2);

    private Capture<Set<ConfigGroupRequest>> configGroupRequestCapture = EasyMock.newCapture();

    @Test
    public void testCreateAmbariResources() throws Exception {
        // expectations
        Capture<ClusterRequest> clusterRequestCapture = EasyMock.newCapture();
        AmbariContextTest.controller.createCluster(capture(clusterRequestCapture));
        expectLastCall().once();
        expect(AmbariContextTest.cluster.getServices()).andReturn(AmbariContextTest.clusterServices).anyTimes();
        Capture<Set<ServiceRequest>> serviceRequestCapture = EasyMock.newCapture();
        Capture<Set<ServiceComponentRequest>> serviceComponentRequestCapture = EasyMock.newCapture();
        AmbariContextTest.serviceResourceProvider.createServices(capture(serviceRequestCapture));
        expectLastCall().once();
        AmbariContextTest.componentResourceProvider.createComponents(capture(serviceComponentRequestCapture));
        expectLastCall().once();
        Capture<Request> serviceInstallRequestCapture = EasyMock.newCapture();
        Capture<Request> serviceStartRequestCapture = EasyMock.newCapture();
        Capture<Predicate> installPredicateCapture = EasyMock.newCapture();
        Capture<Predicate> startPredicateCapture = EasyMock.newCapture();
        expect(AmbariContextTest.serviceResourceProvider.updateResources(capture(serviceInstallRequestCapture), capture(installPredicateCapture))).andReturn(null).once();
        expect(AmbariContextTest.serviceResourceProvider.updateResources(capture(serviceStartRequestCapture), capture(startPredicateCapture))).andReturn(null).once();
        replayAll();
        // test
        AmbariContextTest.context.createAmbariResources(AmbariContextTest.topology, AmbariContextTest.CLUSTER_NAME, null, null, null);
        // assertions
        ClusterRequest clusterRequest = clusterRequestCapture.getValue();
        Assert.assertEquals(AmbariContextTest.CLUSTER_NAME, clusterRequest.getClusterName());
        Assert.assertEquals(String.format("%s-%s", AmbariContextTest.STACK_NAME, AmbariContextTest.STACK_VERSION), clusterRequest.getStackVersion());
        Collection<ServiceRequest> serviceRequests = serviceRequestCapture.getValue();
        Assert.assertEquals(2, serviceRequests.size());
        Collection<String> servicesFound = new HashSet<>();
        for (ServiceRequest serviceRequest : serviceRequests) {
            servicesFound.add(serviceRequest.getServiceName());
            Assert.assertEquals(AmbariContextTest.CLUSTER_NAME, serviceRequest.getClusterName());
        }
        Assert.assertTrue((((servicesFound.size()) == 2) && (servicesFound.containsAll(Arrays.asList("service1", "service2")))));
        Collection<ServiceComponentRequest> serviceComponentRequests = serviceComponentRequestCapture.getValue();
        Assert.assertEquals(3, serviceComponentRequests.size());
        Map<String, Collection<String>> foundServiceComponents = new HashMap<>();
        for (ServiceComponentRequest componentRequest : serviceComponentRequests) {
            Assert.assertEquals(AmbariContextTest.CLUSTER_NAME, componentRequest.getClusterName());
            String serviceName = componentRequest.getServiceName();
            Collection<String> serviceComponents = foundServiceComponents.get(serviceName);
            if (serviceComponents == null) {
                serviceComponents = new HashSet<>();
                foundServiceComponents.put(serviceName, serviceComponents);
            }
            serviceComponents.add(componentRequest.getComponentName());
        }
        Assert.assertEquals(2, foundServiceComponents.size());
        Collection<String> service1Components = foundServiceComponents.get("service1");
        Assert.assertEquals(2, service1Components.size());
        Assert.assertTrue(service1Components.containsAll(Arrays.asList("s1Component1", "s1Component2")));
        Collection<String> service2Components = foundServiceComponents.get("service2");
        Assert.assertEquals(1, service2Components.size());
        Assert.assertTrue(service2Components.contains("s2Component1"));
        Request installRequest = serviceInstallRequestCapture.getValue();
        Set<Map<String, Object>> installPropertiesSet = installRequest.getProperties();
        Assert.assertEquals(1, installPropertiesSet.size());
        Map<String, Object> installProperties = installPropertiesSet.iterator().next();
        Assert.assertEquals(AmbariContextTest.CLUSTER_NAME, installProperties.get(SERVICE_CLUSTER_NAME_PROPERTY_ID));
        Assert.assertEquals("INSTALLED", installProperties.get(SERVICE_SERVICE_STATE_PROPERTY_ID));
        Assert.assertEquals(new org.apache.ambari.server.controller.predicate.EqualsPredicate(ServiceResourceProvider.SERVICE_CLUSTER_NAME_PROPERTY_ID, AmbariContextTest.CLUSTER_NAME), installPredicateCapture.getValue());
        Request startRequest = serviceStartRequestCapture.getValue();
        Set<Map<String, Object>> startPropertiesSet = startRequest.getProperties();
        Assert.assertEquals(1, startPropertiesSet.size());
        Map<String, Object> startProperties = startPropertiesSet.iterator().next();
        Assert.assertEquals(AmbariContextTest.CLUSTER_NAME, startProperties.get(SERVICE_CLUSTER_NAME_PROPERTY_ID));
        Assert.assertEquals("STARTED", startProperties.get(SERVICE_SERVICE_STATE_PROPERTY_ID));
        Assert.assertEquals(new org.apache.ambari.server.controller.predicate.EqualsPredicate(ServiceResourceProvider.SERVICE_CLUSTER_NAME_PROPERTY_ID, AmbariContextTest.CLUSTER_NAME), installPredicateCapture.getValue());
    }

    @Test
    public void testCreateAmbariHostResources() throws Exception {
        // expectations
        expect(AmbariContextTest.cluster.getServices()).andReturn(AmbariContextTest.clusterServices).anyTimes();
        AmbariContextTest.hostResourceProvider.createHosts(anyObject(Request.class));
        expectLastCall().once();
        expect(AmbariContextTest.cluster.getService("service1")).andReturn(AmbariContextTest.mockService1).times(2);
        expect(AmbariContextTest.cluster.getService("service2")).andReturn(AmbariContextTest.mockService1).once();
        Capture<Set<ServiceComponentHostRequest>> requestsCapture = EasyMock.newCapture();
        AmbariContextTest.controller.createHostComponents(capture(requestsCapture), eq(true));
        expectLastCall().once();
        replayAll();
        // test
        Map<String, Collection<String>> componentsMap = new HashMap<>();
        Collection<String> components = new ArrayList<>();
        components.add("component1");
        components.add("component2");
        componentsMap.put("service1", components);
        components = new ArrayList<>();
        components.add("component3");
        componentsMap.put("service2", components);
        AmbariContextTest.context.createAmbariHostResources(AmbariContextTest.CLUSTER_ID, "host1", componentsMap);
        Assert.assertEquals(requestsCapture.getValue().size(), 3);
    }

    @Test
    public void testCreateAmbariHostResourcesWithMissingService() throws Exception {
        // expectations
        expect(AmbariContextTest.cluster.getServices()).andReturn(AmbariContextTest.clusterServices).anyTimes();
        AmbariContextTest.hostResourceProvider.createHosts(anyObject(Request.class));
        expectLastCall().once();
        expect(AmbariContextTest.cluster.getService("service1")).andReturn(AmbariContextTest.mockService1).times(2);
        Capture<Set<ServiceComponentHostRequest>> requestsCapture = EasyMock.newCapture();
        AmbariContextTest.controller.createHostComponents(capture(requestsCapture), eq(true));
        expectLastCall().once();
        replayAll();
        // test
        Map<String, Collection<String>> componentsMap = new HashMap<>();
        Collection<String> components = new ArrayList<>();
        components.add("component1");
        components.add("component2");
        componentsMap.put("service1", components);
        components = new ArrayList<>();
        components.add("component3");
        componentsMap.put("service2", components);
        AmbariContextTest.context.createAmbariHostResources(AmbariContextTest.CLUSTER_ID, "host1", componentsMap);
        Assert.assertEquals(requestsCapture.getValue().size(), 2);
    }

    @Test
    public void testRegisterHostWithConfigGroup_createNewConfigGroup() throws Exception {
        // test specific expectations
        expect(AmbariContextTest.cluster.getConfigGroups()).andReturn(Collections.emptyMap()).once();
        expect(AmbariContextTest.clusterController.ensureResourceProvider(Resource.Type.ConfigGroup)).andReturn(AmbariContextTest.configGroupResourceProvider).once();
        // todo: for now not using return value so just returning null
        expect(AmbariContextTest.configGroupResourceProvider.createResources(capture(configGroupRequestCapture))).andReturn(null).once();
        // replay all mocks
        replayAll();
        // test
        AmbariContextTest.context.registerHostWithConfigGroup(AmbariContextTest.HOST1, AmbariContextTest.topology, AmbariContextTest.HOST_GROUP_1);
        // assertions
        Set<ConfigGroupRequest> configGroupRequests = configGroupRequestCapture.getValue();
        Assert.assertEquals(1, configGroupRequests.size());
        ConfigGroupRequest configGroupRequest = configGroupRequests.iterator().next();
        Assert.assertEquals(AmbariContextTest.CLUSTER_NAME, configGroupRequest.getClusterName());
        Assert.assertEquals("testBP:group1", configGroupRequest.getGroupName());
        Assert.assertEquals("service1", configGroupRequest.getTag());
        Assert.assertEquals("Host Group Configuration", configGroupRequest.getDescription());
        Collection<String> requestHosts = configGroupRequest.getHosts();
        requestHosts.retainAll(AmbariContextTest.group1Hosts);
        Assert.assertEquals(AmbariContextTest.group1Hosts.size(), requestHosts.size());
        Map<String, Config> requestConfig = configGroupRequest.getConfigs();
        Assert.assertEquals(1, requestConfig.size());
        Config type1Config = requestConfig.get("type1");
        // todo: other properties such as cluster name are not currently being explicitly set on config
        Assert.assertEquals("type1", type1Config.getType());
        Assert.assertEquals("group1", type1Config.getTag());
        Map<String, String> requestProps = type1Config.getProperties();
        Assert.assertEquals(3, requestProps.size());
        // 1.2 is overridden value
        Assert.assertEquals("val1.2", requestProps.get("prop1"));
        Assert.assertEquals("val2", requestProps.get("prop2"));
        Assert.assertEquals("val3", requestProps.get("prop3"));
    }

    @Test
    public void testRegisterHostWithConfigGroup_createNewConfigGroupWithPendingHosts() throws Exception {
        // test specific expectations
        expect(AmbariContextTest.cluster.getConfigGroups()).andReturn(Collections.emptyMap()).once();
        expect(AmbariContextTest.clusterController.ensureResourceProvider(Resource.Type.ConfigGroup)).andReturn(AmbariContextTest.configGroupResourceProvider).once();
        // todo: for now not using return value so just returning null
        expect(AmbariContextTest.configGroupResourceProvider.createResources(capture(configGroupRequestCapture))).andReturn(null).once();
        reset(AmbariContextTest.group1Info);
        expect(AmbariContextTest.group1Info.getConfiguration()).andReturn(group1Configuration).anyTimes();
        Collection<String> groupHosts = ImmutableList.of(AmbariContextTest.HOST1, AmbariContextTest.HOST2, "pending_host");// pending_host is not registered with the cluster

        expect(AmbariContextTest.group1Info.getHostNames()).andReturn(groupHosts).anyTimes();// there are 3 hosts for the host group

        // replay all mocks
        replayAll();
        // test
        AmbariContextTest.context.registerHostWithConfigGroup(AmbariContextTest.HOST1, AmbariContextTest.topology, AmbariContextTest.HOST_GROUP_1);
        // assertions
        Set<ConfigGroupRequest> configGroupRequests = configGroupRequestCapture.getValue();
        Assert.assertEquals(1, configGroupRequests.size());
        ConfigGroupRequest configGroupRequest = configGroupRequests.iterator().next();
        Assert.assertEquals(AmbariContextTest.CLUSTER_NAME, configGroupRequest.getClusterName());
        Assert.assertEquals("testBP:group1", configGroupRequest.getGroupName());
        Assert.assertEquals("service1", configGroupRequest.getTag());
        Assert.assertEquals("Host Group Configuration", configGroupRequest.getDescription());
        Collection<String> requestHosts = configGroupRequest.getHosts();
        // we expect only HOST1 and HOST2 in the config group request as the third host "pending_host" hasn't registered yet with the cluster
        Assert.assertEquals(2, requestHosts.size());
        Assert.assertTrue(requestHosts.contains(AmbariContextTest.HOST1));
        Assert.assertTrue(requestHosts.contains(AmbariContextTest.HOST2));
        Map<String, Config> requestConfig = configGroupRequest.getConfigs();
        Assert.assertEquals(1, requestConfig.size());
        Config type1Config = requestConfig.get("type1");
        // todo: other properties such as cluster name are not currently being explicitly set on config
        Assert.assertEquals("type1", type1Config.getType());
        Assert.assertEquals("group1", type1Config.getTag());
        Map<String, String> requestProps = type1Config.getProperties();
        Assert.assertEquals(3, requestProps.size());
        // 1.2 is overridden value
        Assert.assertEquals("val1.2", requestProps.get("prop1"));
        Assert.assertEquals("val2", requestProps.get("prop2"));
        Assert.assertEquals("val3", requestProps.get("prop3"));
    }

    @Test
    public void testRegisterHostWithConfigGroup_registerWithExistingConfigGroup() throws Exception {
        // test specific expectations
        expect(AmbariContextTest.cluster.getConfigGroups()).andReturn(AmbariContextTest.configGroups).once();
        expect(AmbariContextTest.configGroup1.getHosts()).andReturn(Collections.singletonMap(2L, AmbariContextTest.host2)).once();
        AmbariContextTest.configGroup1.addHost(AmbariContextTest.host1);
        // replay all mocks
        replayAll();
        // test
        AmbariContextTest.context.registerHostWithConfigGroup(AmbariContextTest.HOST1, AmbariContextTest.topology, AmbariContextTest.HOST_GROUP_1);
    }

    @Test
    public void testRegisterHostWithConfigGroup_registerWithExistingConfigGroup_hostAlreadyRegistered() throws Exception {
        // test specific expectations
        expect(AmbariContextTest.cluster.getConfigGroups()).andReturn(AmbariContextTest.configGroups).once();
        expect(AmbariContextTest.configGroup1.getHosts()).andReturn(Collections.singletonMap(1L, AmbariContextTest.host1)).once();
        // addHost and persistHostMapping shouldn't be called since host is already registerd with group
        // replay all mocks
        replayAll();
        // test
        AmbariContextTest.context.registerHostWithConfigGroup(AmbariContextTest.HOST1, AmbariContextTest.topology, AmbariContextTest.HOST_GROUP_1);
    }

    @Test
    public void testWaitForTopologyResolvedStateWithEmptyUpdatedSet() throws Exception {
        replayAll();
        // verify that wait returns successfully with empty updated list passed in
        AmbariContextTest.context.waitForConfigurationResolution(AmbariContextTest.CLUSTER_NAME, Collections.emptySet());
    }

    @Test
    public void testWaitForTopologyResolvedStateWithRequiredUpdatedSet() throws Exception {
        final String topologyResolvedState = "TOPOLOGY_RESOLVED";
        DesiredConfig testHdfsDesiredConfig = new DesiredConfig();
        testHdfsDesiredConfig.setTag(topologyResolvedState);
        DesiredConfig testCoreSiteDesiredConfig = new DesiredConfig();
        testCoreSiteDesiredConfig.setTag(topologyResolvedState);
        DesiredConfig testClusterEnvDesiredConfig = new DesiredConfig();
        testClusterEnvDesiredConfig.setTag(topologyResolvedState);
        Map<String, DesiredConfig> testDesiredConfigs = new HashMap<>();
        testDesiredConfigs.put("hdfs-site", testHdfsDesiredConfig);
        testDesiredConfigs.put("core-site", testCoreSiteDesiredConfig);
        testDesiredConfigs.put("cluster-env", testClusterEnvDesiredConfig);
        expect(AmbariContextTest.cluster.getDesiredConfigs()).andReturn(testDesiredConfigs).atLeastOnce();
        replayAll();
        Set<String> testUpdatedConfigTypes = new HashSet<>();
        testUpdatedConfigTypes.add("hdfs-site");
        testUpdatedConfigTypes.add("core-site");
        testUpdatedConfigTypes.add("cluster-env");
        // verify that wait returns successfully with non-empty list
        // with all configuration types tagged as "TOPOLOGY_RESOLVED"
        AmbariContextTest.context.waitForConfigurationResolution(AmbariContextTest.CLUSTER_NAME, testUpdatedConfigTypes);
    }

    @Test
    public void testIsTopologyResolved_True() throws Exception {
        // Given
        DesiredConfig testHdfsDesiredConfig1 = new DesiredConfig();
        testHdfsDesiredConfig1.setTag(INITIAL_CONFIG_TAG);
        testHdfsDesiredConfig1.setVersion(1L);
        DesiredConfig testHdfsDesiredConfig2 = new DesiredConfig();
        testHdfsDesiredConfig2.setTag(TOPOLOGY_RESOLVED_TAG);
        testHdfsDesiredConfig2.setVersion(2L);
        DesiredConfig testHdfsDesiredConfig3 = new DesiredConfig();
        testHdfsDesiredConfig3.setTag("ver123");
        testHdfsDesiredConfig3.setVersion(3L);
        DesiredConfig testCoreSiteDesiredConfig = new DesiredConfig();
        testCoreSiteDesiredConfig.setTag("ver123");
        testCoreSiteDesiredConfig.setVersion(1L);
        Map<String, Set<DesiredConfig>> testDesiredConfigs = ImmutableMap.<String, Set<DesiredConfig>>builder().put("hdfs-site", ImmutableSet.of(testHdfsDesiredConfig2, testHdfsDesiredConfig3, testHdfsDesiredConfig1)).put("core-site", ImmutableSet.of(testCoreSiteDesiredConfig)).build();
        expect(AmbariContextTest.cluster.getAllDesiredConfigVersions()).andReturn(testDesiredConfigs).atLeastOnce();
        replayAll();
        // When
        boolean topologyResolved = AmbariContextTest.context.isTopologyResolved(AmbariContextTest.CLUSTER_ID);
        // Then
        Assert.assertTrue(topologyResolved);
    }

    @Test
    public void testIsTopologyResolved_WrongOrder_False() throws Exception {
        // Given
        DesiredConfig testHdfsDesiredConfig1 = new DesiredConfig();
        testHdfsDesiredConfig1.setTag(INITIAL_CONFIG_TAG);
        testHdfsDesiredConfig1.setVersion(2L);
        DesiredConfig testHdfsDesiredConfig2 = new DesiredConfig();
        testHdfsDesiredConfig2.setTag(TOPOLOGY_RESOLVED_TAG);
        testHdfsDesiredConfig2.setVersion(1L);
        DesiredConfig testHdfsDesiredConfig3 = new DesiredConfig();
        testHdfsDesiredConfig3.setTag("ver123");
        testHdfsDesiredConfig3.setVersion(3L);
        DesiredConfig testCoreSiteDesiredConfig = new DesiredConfig();
        testCoreSiteDesiredConfig.setTag("ver123");
        testCoreSiteDesiredConfig.setVersion(1L);
        Map<String, Set<DesiredConfig>> testDesiredConfigs = ImmutableMap.<String, Set<DesiredConfig>>builder().put("hdfs-site", ImmutableSet.of(testHdfsDesiredConfig2, testHdfsDesiredConfig3, testHdfsDesiredConfig1)).put("core-site", ImmutableSet.of(testCoreSiteDesiredConfig)).build();
        expect(AmbariContextTest.cluster.getAllDesiredConfigVersions()).andReturn(testDesiredConfigs).atLeastOnce();
        replayAll();
        // When
        boolean topologyResolved = AmbariContextTest.context.isTopologyResolved(AmbariContextTest.CLUSTER_ID);
        // Then due to INITIAL -> TOPOLOGY_RESOLVED not honored
        Assert.assertFalse(topologyResolved);
    }

    @Test
    public void testIsTopologyResolved_False() throws Exception {
        // Given
        DesiredConfig testHdfsDesiredConfig1 = new DesiredConfig();
        testHdfsDesiredConfig1.setTag("ver1222");
        testHdfsDesiredConfig1.setVersion(1L);
        DesiredConfig testCoreSiteDesiredConfig = new DesiredConfig();
        testCoreSiteDesiredConfig.setTag("ver123");
        testCoreSiteDesiredConfig.setVersion(1L);
        Map<String, Set<DesiredConfig>> testDesiredConfigs = ImmutableMap.<String, Set<DesiredConfig>>builder().put("hdfs-site", ImmutableSet.of(testHdfsDesiredConfig1)).put("core-site", ImmutableSet.of(testCoreSiteDesiredConfig)).build();
        expect(AmbariContextTest.cluster.getAllDesiredConfigVersions()).andReturn(testDesiredConfigs).atLeastOnce();
        replayAll();
        // When
        boolean topologyResolved = AmbariContextTest.context.isTopologyResolved(AmbariContextTest.CLUSTER_ID);
        // Then
        Assert.assertFalse(topologyResolved);
    }

    @Test
    public void testCreateAmbariResourcesNoVersions() throws Exception {
        VersionDefinitionResourceProvider vdfResourceProvider = createNiceMock(VersionDefinitionResourceProvider.class);
        Class<AmbariContext> clazz = AmbariContext.class;
        Field f = clazz.getDeclaredField("versionDefinitionResourceProvider");
        f.setAccessible(true);
        f.set(null, vdfResourceProvider);
        Resource resource = createNiceMock(Resource.class);
        expect(resource.getPropertyValue(VERSION_DEF_ID)).andReturn(1L).atLeastOnce();
        RequestStatus requestStatus = createNiceMock(RequestStatus.class);
        expect(requestStatus.getAssociatedResources()).andReturn(Collections.singleton(resource)).atLeastOnce();
        expect(vdfResourceProvider.createResources(EasyMock.anyObject(Request.class))).andReturn(requestStatus);
        RepositoryVersionDAO repositoryVersionDAO = createNiceMock(RepositoryVersionDAO.class);
        RepositoryVersionEntity repositoryVersion = createNiceMock(RepositoryVersionEntity.class);
        expect(repositoryVersion.getId()).andReturn(1L).atLeastOnce();
        expect(repositoryVersion.getVersion()).andReturn("1.1.1.1").atLeastOnce();
        expect(repositoryVersion.getType()).andReturn(STANDARD).atLeastOnce();
        expect(repositoryVersionDAO.findByStack(EasyMock.anyObject(StackId.class))).andReturn(Collections.<RepositoryVersionEntity>emptyList()).atLeastOnce();
        expect(repositoryVersionDAO.findByPK(EasyMock.anyLong())).andReturn(repositoryVersion);
        replay(repositoryVersionDAO, repositoryVersion, resource, requestStatus, vdfResourceProvider);
        AmbariContextTest.context.repositoryVersionDAO = repositoryVersionDAO;
        expectAmbariResourceCreation();
        replayAll();
        // test
        AmbariContextTest.context.createAmbariResources(AmbariContextTest.topology, AmbariContextTest.CLUSTER_NAME, null, null, null);
    }

    @Test
    public void testCreateAmbariResourcesManyVersions() throws Exception {
        RepositoryVersionDAO repositoryVersionDAO = createNiceMock(RepositoryVersionDAO.class);
        RepositoryVersionEntity repositoryVersion1 = createNiceMock(RepositoryVersionEntity.class);
        expect(repositoryVersion1.getId()).andReturn(1L).atLeastOnce();
        expect(repositoryVersion1.getVersion()).andReturn("1.1.1.1").atLeastOnce();
        RepositoryVersionEntity repositoryVersion2 = createNiceMock(RepositoryVersionEntity.class);
        expect(repositoryVersion2.getId()).andReturn(2L).atLeastOnce();
        expect(repositoryVersion2.getVersion()).andReturn("1.1.2.2").atLeastOnce();
        expect(repositoryVersionDAO.findByStack(EasyMock.anyObject(StackId.class))).andReturn(Arrays.asList(repositoryVersion1, repositoryVersion2)).atLeastOnce();
        replay(repositoryVersionDAO, repositoryVersion1, repositoryVersion2);
        AmbariContextTest.context.repositoryVersionDAO = repositoryVersionDAO;
        replayAll();
        // test
        try {
            AmbariContextTest.context.createAmbariResources(AmbariContextTest.topology, AmbariContextTest.CLUSTER_NAME, null, null, null);
            Assert.fail("Expected failure when several versions are found");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Several repositories were found for testStack-testVersion:  1.1.1.1, 1.1.2.2.  Specify the version with 'repository_version'", e.getMessage());
        }
    }

    @Test
    public void testCreateAmbariResourcesBadVersion() throws Exception {
        RepositoryVersionDAO repositoryVersionDAO = createNiceMock(RepositoryVersionDAO.class);
        expect(repositoryVersionDAO.findByStackAndVersion(EasyMock.anyObject(StackId.class), EasyMock.anyString())).andReturn(null).atLeastOnce();
        replay(repositoryVersionDAO);
        AmbariContextTest.context.repositoryVersionDAO = repositoryVersionDAO;
        replayAll();
        // test
        try {
            AmbariContextTest.context.createAmbariResources(AmbariContextTest.topology, AmbariContextTest.CLUSTER_NAME, null, "xyz", null);
            Assert.fail("Expected failure when a bad version is provided");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Could not identify repository version with stack testStack-testVersion and version xyz for installing services. Specify a valid version with 'repository_version'", e.getMessage());
        }
    }

    @Test
    public void testCreateAmbariResourcesVersionOK() {
        RepositoryVersionEntity repoVersion = repositoryInClusterCreationRequest(1L, "3.0.1.2-345", AmbariContextTest.STACK_ID);
        expectAmbariResourceCreation();
        replayAll();
        AmbariContextTest.context.createAmbariResources(AmbariContextTest.topology, AmbariContextTest.CLUSTER_NAME, null, repoVersion.getVersion(), null);
    }

    @Test
    public void testCreateAmbariResourcesVersionByIdOK() {
        RepositoryVersionEntity repoVersion = repositoryInClusterCreationRequest(1L, "3.0.1.2-345", AmbariContextTest.STACK_ID);
        expectAmbariResourceCreation();
        replayAll();
        AmbariContextTest.context.createAmbariResources(AmbariContextTest.topology, AmbariContextTest.CLUSTER_NAME, null, null, repoVersion.getId());
    }

    @Test
    public void testCreateAmbariResourcesVersionMismatch() {
        RepositoryVersionEntity repoVersion = repositoryInClusterCreationRequest(1L, "3.0.1.2-345", new StackId("HDP", "3.0"));
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> AmbariContextTest.context.createAmbariResources(AmbariContextTest.topology, AmbariContextTest.CLUSTER_NAME, null, repoVersion.getVersion(), null));
        Assert.assertTrue(e.getMessage(), e.getMessage().contains("should match"));
    }

    @Test
    public void testCreateAmbariResourcesVersionMismatchById() {
        RepositoryVersionEntity repoVersion = repositoryInClusterCreationRequest(1L, "3.0.1.2-345", new StackId("HDP", "3.0"));
        replayAll();
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> AmbariContextTest.context.createAmbariResources(AmbariContextTest.topology, AmbariContextTest.CLUSTER_NAME, null, null, repoVersion.getId()));
        Assert.assertTrue(e.getMessage(), e.getMessage().contains("should match"));
    }
}

