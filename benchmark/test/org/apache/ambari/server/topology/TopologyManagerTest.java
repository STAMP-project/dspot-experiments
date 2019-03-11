/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.topology;


import HostResourceProvider.BLUEPRINT_PROPERTY_ID;
import HostResourceProvider.HOST_CLUSTER_NAME_PROPERTY_ID;
import HostResourceProvider.HOST_GROUP_PROPERTY_ID;
import HostResourceProvider.HOST_HOST_NAME_PROPERTY_ID;
import HostRoleStatus.COMPLETED;
import HostRoleStatus.FAILED;
import HostRoleStatus.IN_PROGRESS;
import QuickLinksProfile.SETTING_NAME_QUICKLINKS_PROFILE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.controller.ClusterRequest;
import org.apache.ambari.server.controller.ConfigurationRequest;
import org.apache.ambari.server.controller.RequestStatusResponse;
import org.apache.ambari.server.controller.ShortTaskStatus;
import org.apache.ambari.server.controller.internal.ProvisionClusterRequest;
import org.apache.ambari.server.controller.internal.ScaleClusterRequest;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.controller.spi.ClusterController;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.events.ClusterProvisionStartedEvent;
import org.apache.ambari.server.events.ClusterProvisionedEvent;
import org.apache.ambari.server.events.RequestFinishedEvent;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.apache.ambari.server.orm.dao.SettingDAO;
import org.apache.ambari.server.orm.entities.SettingEntity;
import org.apache.ambari.server.security.encryption.CredentialStoreService;
import org.apache.ambari.server.stack.NoSuchStackException;
import org.apache.ambari.server.topology.tasks.ConfigureClusterTask;
import org.apache.ambari.server.topology.tasks.ConfigureClusterTaskFactory;
import org.apache.ambari.server.topology.validators.TopologyValidatorService;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * TopologyManager unit tests
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ TopologyManager.class })
public class TopologyManagerTest {
    private static final String CLUSTER_NAME = "test-cluster";

    private static final long CLUSTER_ID = 1;

    private static final String BLUEPRINT_NAME = "test-bp";

    private static final String STACK_NAME = "test-stack";

    private static final String STACK_VERSION = "test-stack-version";

    private static final String SAMPLE_QUICKLINKS_PROFILE_1 = "{\"filters\":[{\"visible\":true}],\"services\":[]}";

    private static final String SAMPLE_QUICKLINKS_PROFILE_2 = "{\"filters\":[],\"services\":[{\"name\":\"HDFS\",\"components\":[],\"filters\":[{\"visible\":true}]}]}";

    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    @TestSubject
    private TopologyManager topologyManager = new TopologyManager();

    @TestSubject
    private TopologyManager topologyManagerReplay = new TopologyManager();

    @Mock(type = MockType.NICE)
    private Blueprint blueprint;

    @Mock(type = MockType.NICE)
    private Stack stack;

    @Mock(type = MockType.NICE)
    private ProvisionClusterRequest request;

    private final PersistedTopologyRequest persistedTopologyRequest = new PersistedTopologyRequest(1, request);

    @Mock(type = MockType.STRICT)
    private LogicalRequestFactory logicalRequestFactory;

    @Mock(type = MockType.DEFAULT)
    private LogicalRequest logicalRequest;

    @Mock(type = MockType.NICE)
    private AmbariContext ambariContext;

    @Mock(type = MockType.NICE)
    private ConfigurationRequest configurationRequest;

    @Mock(type = MockType.NICE)
    private ConfigurationRequest configurationRequest2;

    @Mock(type = MockType.NICE)
    private ConfigurationRequest configurationRequest3;

    @Mock(type = MockType.NICE)
    private RequestStatusResponse requestStatusResponse;

    @Mock(type = MockType.STRICT)
    private ExecutorService executor;

    @Mock(type = MockType.NICE)
    private PersistedState persistedState;

    @Mock(type = MockType.NICE)
    private HostGroup group1;

    @Mock(type = MockType.NICE)
    private HostGroup group2;

    @Mock(type = MockType.STRICT)
    private SecurityConfigurationFactory securityConfigurationFactory;

    @Mock(type = MockType.STRICT)
    private CredentialStoreService credentialStoreService;

    @Mock(type = MockType.STRICT)
    private ClusterController clusterController;

    @Mock(type = MockType.STRICT)
    private ResourceProvider resourceProvider;

    @Mock(type = MockType.STRICT)
    private SettingDAO settingDAO;

    @Mock(type = MockType.NICE)
    private ClusterTopology clusterTopologyMock;

    @Mock(type = MockType.NICE)
    private ConfigureClusterTaskFactory configureClusterTaskFactory;

    @Mock(type = MockType.NICE)
    private ConfigureClusterTask configureClusterTask;

    @Mock(type = MockType.NICE)
    private AmbariEventPublisher eventPublisher;

    @Mock(type = MockType.STRICT)
    private Future mockFuture;

    @Mock
    private TopologyValidatorService topologyValidatorService;

    private final Configuration stackConfig = new Configuration(new HashMap(), new HashMap());

    private final Configuration bpConfiguration = new Configuration(new HashMap(), new HashMap(), stackConfig);

    private final Configuration topoConfiguration = new Configuration(new HashMap(), new HashMap(), bpConfiguration);

    private final Configuration bpGroup1Config = new Configuration(new HashMap(), new HashMap(), bpConfiguration);

    private final Configuration bpGroup2Config = new Configuration(new HashMap(), new HashMap(), bpConfiguration);

    // todo: topo config hierarchy is wrong: bpGroupConfigs should extend topo cluster config
    private final Configuration topoGroup1Config = new Configuration(new HashMap(), new HashMap(), bpGroup1Config);

    private final Configuration topoGroup2Config = new Configuration(new HashMap(), new HashMap(), bpGroup2Config);

    private HostGroupInfo group1Info = new HostGroupInfo("group1");

    private HostGroupInfo group2Info = new HostGroupInfo("group2");

    private Map<String, HostGroupInfo> groupInfoMap = new HashMap<>();

    private Collection<Component> group1Components = Arrays.asList(new Component("component1"), new Component("component2"), new Component("component3"));

    private Collection<Component> group2Components = Arrays.asList(new Component("component3"), new Component("component4"));

    private Map<String, Collection<String>> group1ServiceComponents = new HashMap<>();

    private Map<String, Collection<String>> group2ServiceComponents = new HashMap<>();

    private Map<String, Collection<String>> serviceComponents = new HashMap<>();

    private String predicate = "Hosts/host_name=foo";

    private List<TopologyValidator> topologyValidators = new ArrayList<>();

    private Capture<ClusterTopology> clusterTopologyCapture;

    private Capture<Map<String, Object>> configRequestPropertiesCapture;

    private Capture<Map<String, Object>> configRequestPropertiesCapture2;

    private Capture<Map<String, Object>> configRequestPropertiesCapture3;

    private Capture<ClusterRequest> updateClusterConfigRequestCapture;

    private Capture<Runnable> updateConfigTaskCapture;

    @Test
    public void testProvisionCluster() throws Exception {
        expect(persistedState.getAllRequests()).andReturn(Collections.emptyMap()).anyTimes();
        replayAll();
        topologyManager.provisionCluster(request);
        // todo: assertions
    }

    @Test
    public void testBlueprintProvisioningStateEvent() throws Exception {
        expect(persistedState.getAllRequests()).andReturn(Collections.emptyMap()).anyTimes();
        eventPublisher.publish(anyObject(ClusterProvisionStartedEvent.class));
        expectLastCall().once();
        replayAll();
        topologyManager.provisionCluster(request);
    }

    @Test
    public void testAddKerberosClientAtTopologyInit() throws Exception {
        Map<ClusterTopology, List<LogicalRequest>> allRequests = new HashMap<>();
        List<LogicalRequest> requestList = new ArrayList<>();
        requestList.add(logicalRequest);
        expect(logicalRequest.hasPendingHostRequests()).andReturn(false).anyTimes();
        expect(logicalRequest.isFinished()).andReturn(false).anyTimes();
        allRequests.put(clusterTopologyMock, requestList);
        expect(requestStatusResponse.getTasks()).andReturn(Collections.emptyList()).anyTimes();
        expect(clusterTopologyMock.isClusterKerberosEnabled()).andReturn(true);
        expect(clusterTopologyMock.getClusterId()).andReturn(TopologyManagerTest.CLUSTER_ID).anyTimes();
        expect(clusterTopologyMock.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(persistedState.getAllRequests()).andReturn(allRequests).anyTimes();
        expect(persistedState.getProvisionRequest(TopologyManagerTest.CLUSTER_ID)).andReturn(logicalRequest).anyTimes();
        expect(ambariContext.isTopologyResolved(TopologyManagerTest.CLUSTER_ID)).andReturn(true).anyTimes();
        expect(group1.addComponent("KERBEROS_CLIENT")).andReturn(true).anyTimes();
        expect(group2.addComponent("KERBEROS_CLIENT")).andReturn(true).anyTimes();
        replayAll();
        topologyManager.provisionCluster(request);
        // todo: assertions
    }

    @Test
    public void testBlueprintRequestCompletion() throws Exception {
        List<ShortTaskStatus> tasks = new ArrayList<>();
        ShortTaskStatus t1 = new ShortTaskStatus();
        t1.setStatus(COMPLETED.toString());
        tasks.add(t1);
        ShortTaskStatus t2 = new ShortTaskStatus();
        t2.setStatus(COMPLETED.toString());
        tasks.add(t2);
        ShortTaskStatus t3 = new ShortTaskStatus();
        t3.setStatus(COMPLETED.toString());
        tasks.add(t3);
        expect(requestStatusResponse.getTasks()).andReturn(tasks).anyTimes();
        expect(persistedState.getAllRequests()).andReturn(Collections.emptyMap()).anyTimes();
        expect(persistedState.getProvisionRequest(TopologyManagerTest.CLUSTER_ID)).andReturn(logicalRequest).anyTimes();
        expect(logicalRequest.isFinished()).andReturn(true).anyTimes();
        expect(logicalRequest.isSuccessful()).andReturn(true).anyTimes();
        eventPublisher.publish(anyObject(ClusterProvisionedEvent.class));
        expectLastCall().once();
        replayAll();
        topologyManager.provisionCluster(request);
        requestFinished();
        Assert.assertTrue(topologyManager.isClusterProvisionWithBlueprintFinished(TopologyManagerTest.CLUSTER_ID));
    }

    @Test
    public void testBlueprintRequestCompletion__Failure() throws Exception {
        List<ShortTaskStatus> tasks = new ArrayList<>();
        ShortTaskStatus t1 = new ShortTaskStatus();
        t1.setStatus(FAILED.toString());
        tasks.add(t1);
        ShortTaskStatus t2 = new ShortTaskStatus();
        t2.setStatus(COMPLETED.toString());
        tasks.add(t2);
        ShortTaskStatus t3 = new ShortTaskStatus();
        t3.setStatus(COMPLETED.toString());
        tasks.add(t3);
        expect(requestStatusResponse.getTasks()).andReturn(tasks).anyTimes();
        expect(persistedState.getAllRequests()).andReturn(Collections.emptyMap()).anyTimes();
        expect(persistedState.getProvisionRequest(TopologyManagerTest.CLUSTER_ID)).andReturn(logicalRequest).anyTimes();
        expect(logicalRequest.isFinished()).andReturn(true).anyTimes();
        expect(logicalRequest.isSuccessful()).andReturn(false).anyTimes();
        replayAll();
        topologyManager.provisionCluster(request);
        requestFinished();
        Assert.assertTrue(topologyManager.isClusterProvisionWithBlueprintFinished(TopologyManagerTest.CLUSTER_ID));
    }

    @Test
    public void testBlueprintRequestCompletion__InProgress() throws Exception {
        List<ShortTaskStatus> tasks = new ArrayList<>();
        ShortTaskStatus t1 = new ShortTaskStatus();
        t1.setStatus(IN_PROGRESS.toString());
        tasks.add(t1);
        ShortTaskStatus t2 = new ShortTaskStatus();
        t2.setStatus(COMPLETED.toString());
        tasks.add(t2);
        ShortTaskStatus t3 = new ShortTaskStatus();
        t3.setStatus(COMPLETED.toString());
        tasks.add(t3);
        expect(requestStatusResponse.getTasks()).andReturn(tasks).anyTimes();
        expect(persistedState.getAllRequests()).andReturn(Collections.emptyMap()).anyTimes();
        expect(persistedState.getProvisionRequest(TopologyManagerTest.CLUSTER_ID)).andReturn(logicalRequest).anyTimes();
        expect(logicalRequest.isFinished()).andReturn(false).anyTimes();
        replayAll();
        topologyManager.provisionCluster(request);
        requestFinished();
        Assert.assertFalse(topologyManager.isClusterProvisionWithBlueprintFinished(TopologyManagerTest.CLUSTER_ID));
    }

    @Test
    public void testBlueprintRequestCompletion__NoRequest() throws Exception {
        TopologyManager tm = new TopologyManager();
        tm.onRequestFinished(new RequestFinishedEvent(TopologyManagerTest.CLUSTER_ID, 1));
        Assert.assertFalse(tm.isClusterProvisionWithBlueprintTracked(TopologyManagerTest.CLUSTER_ID));
        replayAll();
    }

    @Test
    public void testBlueprintRequestCompletion__Replay() throws Exception {
        List<ShortTaskStatus> tasks = new ArrayList<>();
        ShortTaskStatus t1 = new ShortTaskStatus();
        t1.setStatus(COMPLETED.toString());
        tasks.add(t1);
        ShortTaskStatus t2 = new ShortTaskStatus();
        t2.setStatus(COMPLETED.toString());
        tasks.add(t2);
        ShortTaskStatus t3 = new ShortTaskStatus();
        t3.setStatus(COMPLETED.toString());
        tasks.add(t3);
        Map<ClusterTopology, List<LogicalRequest>> allRequests = new HashMap<>();
        List<LogicalRequest> logicalRequests = new ArrayList<>();
        logicalRequests.add(logicalRequest);
        ClusterTopology clusterTopologyMock = EasyMock.createNiceMock(ClusterTopology.class);
        expect(clusterTopologyMock.getClusterId()).andReturn(TopologyManagerTest.CLUSTER_ID).anyTimes();
        expect(ambariContext.isTopologyResolved(EasyMock.anyLong())).andReturn(true).anyTimes();
        allRequests.put(clusterTopologyMock, logicalRequests);
        expect(persistedState.getAllRequests()).andReturn(allRequests).anyTimes();
        expect(persistedState.getProvisionRequest(TopologyManagerTest.CLUSTER_ID)).andReturn(logicalRequest).anyTimes();
        expect(logicalRequest.hasPendingHostRequests()).andReturn(true).anyTimes();
        expect(logicalRequest.getCompletedHostRequests()).andReturn(Collections.EMPTY_LIST).anyTimes();
        expect(logicalRequest.isFinished()).andReturn(true).anyTimes();
        expect(requestStatusResponse.getTasks()).andReturn(tasks).anyTimes();
        replayAll();
        EasyMock.replay(clusterTopologyMock);
        topologyManagerReplay.getRequest(1L);// calling ensureInitialized indirectly

        Assert.assertTrue(topologyManagerReplay.isClusterProvisionWithBlueprintFinished(TopologyManagerTest.CLUSTER_ID));
    }

    @Test(expected = InvalidTopologyException.class)
    public void testScaleHosts__alreadyExistingHost() throws AmbariException, NoSuchStackException, InvalidTopologyException, InvalidTopologyTemplateException {
        HashSet<Map<String, Object>> propertySet = new HashSet<>();
        Map<String, Object> properties = new TreeMap<>();
        properties.put(HOST_HOST_NAME_PROPERTY_ID, "host1");
        properties.put(HOST_GROUP_PROPERTY_ID, "group1");
        properties.put(HOST_CLUSTER_NAME_PROPERTY_ID, TopologyManagerTest.CLUSTER_NAME);
        properties.put(BLUEPRINT_PROPERTY_ID, TopologyManagerTest.BLUEPRINT_NAME);
        propertySet.add(properties);
        BlueprintFactory bpfMock = EasyMock.createNiceMock(BlueprintFactory.class);
        EasyMock.expect(bpfMock.getBlueprint(TopologyManagerTest.BLUEPRINT_NAME)).andReturn(blueprint).anyTimes();
        ScaleClusterRequest.init(bpfMock);
        replay(bpfMock);
        expect(persistedState.getAllRequests()).andReturn(Collections.emptyMap()).anyTimes();
        replayAll();
        topologyManager.provisionCluster(request);
        topologyManager.scaleHosts(new ScaleClusterRequest(propertySet));
        Assert.fail("InvalidTopologyException should have been thrown");
    }

    @Test
    public void testProvisionCluster_QuickLinkProfileIsSavedTheFirstTime() throws Exception {
        expect(persistedState.getAllRequests()).andReturn(Collections.emptyMap()).anyTimes();
        // request has a quicklinks profile
        expect(request.getQuickLinksProfileJson()).andReturn(TopologyManagerTest.SAMPLE_QUICKLINKS_PROFILE_1).anyTimes();
        // this means no quicklinks profile exists before calling provisionCluster()
        expect(settingDAO.findByName(SETTING_NAME_QUICKLINKS_PROFILE)).andReturn(null);
        // expect that settingsDao saves the quick links profile with the right content
        final long timeStamp = System.currentTimeMillis();
        mockStatic(System.class);
        expect(System.currentTimeMillis()).andReturn(timeStamp);
        PowerMock.replay(System.class);
        final SettingEntity quickLinksProfile = createQuickLinksSettingEntity(TopologyManagerTest.SAMPLE_QUICKLINKS_PROFILE_1, timeStamp);
        settingDAO.create(eq(quickLinksProfile));
        replayAll();
        topologyManager.provisionCluster(request);
    }

    @Test
    public void testProvisionCluster_ExistingQuickLinkProfileIsOverwritten() throws Exception {
        expect(persistedState.getAllRequests()).andReturn(Collections.emptyMap()).anyTimes();
        // request has a quicklinks profile
        expect(request.getQuickLinksProfileJson()).andReturn(TopologyManagerTest.SAMPLE_QUICKLINKS_PROFILE_2).anyTimes();
        // existing quick links profile returned by dao
        final long timeStamp1 = System.currentTimeMillis();
        SettingEntity originalProfile = createQuickLinksSettingEntity(TopologyManagerTest.SAMPLE_QUICKLINKS_PROFILE_1, timeStamp1);
        expect(settingDAO.findByName(SETTING_NAME_QUICKLINKS_PROFILE)).andReturn(originalProfile);
        // expect that settingsDao overwrites the quick links profile with the new content
        mockStatic(System.class);
        final long timeStamp2 = timeStamp1 + 100;
        expect(System.currentTimeMillis()).andReturn(timeStamp2);
        PowerMock.replay(System.class);
        final SettingEntity newProfile = createQuickLinksSettingEntity(TopologyManagerTest.SAMPLE_QUICKLINKS_PROFILE_2, timeStamp2);
        expect(settingDAO.merge(newProfile)).andReturn(newProfile);
        replayAll();
        topologyManager.provisionCluster(request);
    }
}

