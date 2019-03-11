/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.topology;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.AmbariServer;
import org.apache.ambari.server.controller.ClusterRequest;
import org.apache.ambari.server.controller.ConfigurationRequest;
import org.apache.ambari.server.controller.RequestStatusResponse;
import org.apache.ambari.server.controller.internal.ProvisionClusterRequest;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.controller.spi.ClusterController;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.apache.ambari.server.security.encryption.CredentialStoreService;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ComponentInfo;
import org.apache.ambari.server.topology.tasks.ConfigureClusterTask;
import org.apache.ambari.server.topology.tasks.ConfigureClusterTaskFactory;
import org.apache.ambari.server.topology.validators.TopologyValidatorService;
import org.easymock.Capture;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(AmbariServer.class)
public class ClusterDeployWithStartOnlyTest extends EasyMockSupport {
    private static final String CLUSTER_NAME = "test-cluster";

    private static final long CLUSTER_ID = 1;

    private static final String BLUEPRINT_NAME = "test-bp";

    private static final String STACK_NAME = "test-stack";

    private static final String STACK_VERSION = "test-stack-version";

    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    @TestSubject
    private TopologyManager topologyManager = new TopologyManager();

    @Mock(type = MockType.NICE)
    private Blueprint blueprint;

    @Mock(type = MockType.NICE)
    private Stack stack;

    @Mock(type = MockType.NICE)
    private ProvisionClusterRequest request;

    private PersistedTopologyRequest persistedTopologyRequest;

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

    @Mock(type = MockType.STRICT)
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

    @Mock(type = MockType.NICE)
    private AmbariManagementController managementController;

    @Mock(type = MockType.NICE)
    private Clusters clusters;

    @Mock(type = MockType.NICE)
    private Cluster cluster;

    @Mock(type = MockType.NICE)
    private HostRoleCommand hostRoleCommandInstallComponent3;

    @Mock(type = MockType.NICE)
    private HostRoleCommand hostRoleCommandInstallComponent4;

    @Mock(type = MockType.NICE)
    private HostRoleCommand hostRoleCommandStartComponent1;

    @Mock(type = MockType.NICE)
    private HostRoleCommand hostRoleCommandStartComponent2;

    @Mock(type = MockType.NICE)
    private ComponentInfo serviceComponentInfo;

    @Mock(type = MockType.NICE)
    private ComponentInfo clientComponentInfo;

    @Mock(type = MockType.NICE)
    private ConfigureClusterTaskFactory configureClusterTaskFactory;

    @Mock(type = MockType.NICE)
    private ConfigureClusterTask configureClusterTask;

    @Mock(type = MockType.STRICT)
    private Future mockFuture;

    @Mock
    private TopologyValidatorService topologyValidatorServiceMock;

    @Mock(type = MockType.NICE)
    private AmbariEventPublisher eventPublisher;

    private final Configuration stackConfig = new Configuration(new HashMap(), new HashMap());

    private final Configuration bpConfiguration = new Configuration(new HashMap(), new HashMap(), stackConfig);

    private final Configuration topoConfiguration = new Configuration(new HashMap(), new HashMap(), bpConfiguration);

    private final Configuration bpGroup1Config = new Configuration(new HashMap(), new HashMap(), bpConfiguration);

    private final Configuration bpGroup2Config = new Configuration(new HashMap(), new HashMap(), bpConfiguration);

    private final Configuration topoGroup1Config = new Configuration(new HashMap(), new HashMap(), bpGroup1Config);

    private final Configuration topoGroup2Config = new Configuration(new HashMap(), new HashMap(), bpGroup2Config);

    private HostGroupInfo group1Info = new HostGroupInfo("group1");

    private HostGroupInfo group2Info = new HostGroupInfo("group2");

    private Map<String, HostGroupInfo> groupInfoMap = new HashMap<>();

    private Collection<String> group1Components = Arrays.asList("component1", "component2", "component3");

    private Collection<String> group2Components = Arrays.asList("component3", "component4");

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
        topologyManager.provisionCluster(request);
        LogicalRequest request = topologyManager.getRequest(1);
        Assert.assertEquals(request.getHostRequests().size(), 3);
    }
}

