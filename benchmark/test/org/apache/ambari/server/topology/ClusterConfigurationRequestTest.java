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


import ConfigRecommendationStrategy.NEVER_APPLY;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorBlueprintProcessor;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.KerberosHelper;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.StackId;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * ClusterConfigurationRequest unit tests
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AmbariContext.class })
public class ClusterConfigurationRequestTest {
    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    @Mock(type = MockType.NICE)
    private Blueprint blueprint;

    @Mock(type = MockType.NICE)
    private AmbariContext ambariContext;

    @Mock(type = MockType.NICE)
    private Cluster cluster;

    @Mock(type = MockType.NICE)
    private Clusters clusters;

    @Mock(type = MockType.NICE)
    private ClusterTopology topology;

    @Mock(type = MockType.NICE)
    private StackAdvisorBlueprintProcessor stackAdvisorBlueprintProcessor;

    @Mock(type = MockType.NICE)
    private Stack stack;

    @Mock(type = MockType.NICE)
    private AmbariManagementController controller;

    @Mock(type = MockType.NICE)
    private KerberosHelper kerberosHelper;

    @Mock(type = MockType.NICE)
    private ConfigHelper configHelper;

    private final String STACK_NAME = "testStack";

    private final String STACK_VERSION = "1";

    private final Map<String, Map<String, String>> stackProperties = new HashMap<>();

    private final Map<String, String> defaultClusterEnvProperties = new HashMap<>();

    /**
     * testConfigType config type should be in updatedConfigTypes, as no custom property in Blueprint
     * ==> Kerberos config property should be updated
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProcessWithKerberos_UpdateKererosConfigProperty_WithNoCustomValue() throws Exception {
        Capture<? extends Set<String>> captureUpdatedConfigTypes = testProcessWithKerberos(null, "defaultTestValue", null);
        Set<String> updatedConfigTypes = captureUpdatedConfigTypes.getValue();
        Assert.assertEquals(2, updatedConfigTypes.size());
    }

    /**
     * testConfigType config type should be in updatedConfigTypes, as testProperty in Blueprint is equal to stack
     * default ==> Kerberos config property should be updated
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProcessWithKerberos_UpdateKererosConfigProperty_WithCustomValueEqualToStackDefault() throws Exception {
        Capture<? extends Set<String>> captureUpdatedConfigTypes = testProcessWithKerberos("defaultTestValue", "defaultTestValue", null);
        Set<String> updatedConfigTypes = captureUpdatedConfigTypes.getValue();
        Assert.assertEquals(2, updatedConfigTypes.size());
    }

    /**
     * testConfigType config type shouldn't be in updatedConfigTypes, as testProperty in Blueprint is different that
     * stack default (custom value) ==> Kerberos config property shouldn't be updated
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProcessWithKerberos_DontUpdateKererosConfigProperty_WithCustomValueDifferentThanStackDefault() throws Exception {
        Capture<? extends Set<String>> captureUpdatedConfigTypes = testProcessWithKerberos("testPropertyValue", "defaultTestValue", null);
        Set<String> updatedConfigTypes = captureUpdatedConfigTypes.getValue();
        Assert.assertEquals(1, updatedConfigTypes.size());
    }

    /**
     * testConfigType config type shouldn't be in updatedConfigTypes, as testProperty in Blueprint is a custom value
     * (no default value in stack for testProperty)
     * ==> Kerberos config property shouldn't be updated
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProcessWithKerberos_DontUpdateKererosConfigProperty_WithCustomValueNoStackDefault() throws Exception {
        Capture<? extends Set<String>> captureUpdatedConfigTypes = testProcessWithKerberos("testPropertyValue", null, null);
        Set<String> updatedConfigTypes = captureUpdatedConfigTypes.getValue();
        Assert.assertEquals(1, updatedConfigTypes.size());
    }

    @Test
    public void testProcessWithKerberos_DontUpdateKererosConfigProperty_WithKerberosConfigSameAsDefault() throws Exception {
        Map<String, Map<String, String>> kerberosConfig = new HashMap<>();
        Map<String, String> properties = new HashMap<>();
        properties.put("testProperty", "defaultTestValue");
        kerberosConfig.put("testConfigType", properties);
        Capture<? extends Set<String>> captureUpdatedConfigTypes = testProcessWithKerberos(null, "defaultTestValue", kerberosConfig);
        Set<String> updatedConfigTypes = captureUpdatedConfigTypes.getValue();
        Assert.assertEquals(1, updatedConfigTypes.size());
    }

    @Test
    public void testProcessWithKerberos_DontUpdateKererosConfigProperty_WithOrphanedKerberosConfigType() throws Exception {
        Map<String, Map<String, String>> kerberosConfig = new HashMap<>();
        Map<String, String> properties = new HashMap<>();
        properties.put("testProperty", "KERBEROStestValue");
        kerberosConfig.put("orphanedTestConfigType", properties);
        Capture<? extends Set<String>> captureUpdatedConfigTypes = testProcessWithKerberos(null, "defaultTestValue", kerberosConfig);
        Set<String> updatedConfigTypes = captureUpdatedConfigTypes.getValue();
        Assert.assertEquals(1, updatedConfigTypes.size());
    }

    @Test
    public void testProcessClusterConfigRequestDontIncludeKererosConfigs() throws Exception {
        Map<String, Map<String, String>> existingConfig = new HashMap<>();
        Configuration stackConfig = new Configuration(existingConfig, new HashMap());
        PowerMock.mockStatic(AmbariContext.class);
        AmbariContext.getController();
        expectLastCall().andReturn(controller).anyTimes();
        expect(controller.getClusters()).andReturn(clusters).anyTimes();
        expect(clusters.getCluster("testCluster")).andReturn(cluster).anyTimes();
        expect(blueprint.getStack()).andReturn(stack).anyTimes();
        expect(stack.getName()).andReturn(STACK_NAME).anyTimes();
        expect(stack.getVersion()).andReturn(STACK_VERSION).anyTimes();
        expect(stack.getAllConfigurationTypes(anyString())).andReturn(Collections.singletonList("testConfigType")).anyTimes();
        expect(stack.getExcludedConfigurationTypes(anyString())).andReturn(Collections.emptySet()).anyTimes();
        expect(stack.getConfigurationPropertiesWithMetadata(anyString(), anyString())).andReturn(Collections.emptyMap()).anyTimes();
        Set<String> services = new HashSet<>();
        services.add("HDFS");
        services.add("KERBEROS");
        services.add("ZOOKEPER");
        expect(blueprint.getServices()).andReturn(services).anyTimes();
        List<String> hdfsComponents = new ArrayList<>();
        hdfsComponents.add("NAMENODE");
        List<String> kerberosComponents = new ArrayList<>();
        kerberosComponents.add("KERBEROS_CLIENT");
        List<String> zookeeperComponents = new ArrayList<>();
        zookeeperComponents.add("ZOOKEEPER_SERVER");
        expect(blueprint.getComponents("HDFS")).andReturn(hdfsComponents).anyTimes();
        expect(blueprint.getComponents("KERBEROS")).andReturn(kerberosComponents).anyTimes();
        expect(blueprint.getComponents("ZOOKEPER")).andReturn(zookeeperComponents).anyTimes();
        expect(topology.getAmbariContext()).andReturn(ambariContext).anyTimes();
        expect(topology.getConfigRecommendationStrategy()).andReturn(NEVER_APPLY).anyTimes();
        expect(topology.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(topology.getConfiguration()).andReturn(stackConfig).anyTimes();
        expect(topology.getHostGroupInfo()).andReturn(Collections.emptyMap()).anyTimes();
        expect(topology.getClusterId()).andReturn(Long.valueOf(1)).anyTimes();
        expect(ambariContext.getConfigHelper()).andReturn(configHelper).anyTimes();
        expect(ambariContext.getClusterName(Long.valueOf(1))).andReturn("testCluster").anyTimes();
        expect(ambariContext.createConfigurationRequests(EasyMock.anyObject())).andReturn(Collections.emptyList()).anyTimes();
        expect(configHelper.getDefaultStackProperties(EasyMock.eq(new StackId(STACK_NAME, STACK_VERSION)))).andReturn(stackProperties).anyTimes();
        PowerMock.replay(stack, blueprint, topology, controller, clusters, ambariContext, AmbariContext.class, configHelper);
        ClusterConfigurationRequest clusterConfigurationRequest = new ClusterConfigurationRequest(ambariContext, topology, false, stackAdvisorBlueprintProcessor);
        clusterConfigurationRequest.process();
        verify(blueprint, topology, ambariContext, controller, configHelper);
    }

    @Test
    public void testProcessClusterConfigRequestRemoveUnusedConfigTypes() throws Exception {
        // GIVEN
        Configuration configuration = createConfigurations();
        Set<String> services = new HashSet<>();
        services.add("HDFS");
        services.add("RANGER");
        Map<String, HostGroupInfo> hostGroupInfoMap = Maps.newHashMap();
        HostGroupInfo hg1 = new HostGroupInfo("hg1");
        hg1.setConfiguration(createConfigurationsForHostGroup());
        hostGroupInfoMap.put("hg1", hg1);
        expect(topology.getAmbariContext()).andReturn(ambariContext).anyTimes();
        expect(topology.getConfiguration()).andReturn(configuration).anyTimes();
        expect(topology.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(topology.getHostGroupInfo()).andReturn(hostGroupInfoMap);
        expect(blueprint.getStack()).andReturn(stack).anyTimes();
        expect(blueprint.getServices()).andReturn(services).anyTimes();
        expect(blueprint.isValidConfigType("hdfs-site")).andReturn(true).anyTimes();
        expect(blueprint.isValidConfigType("admin-properties")).andReturn(true).anyTimes();
        expect(blueprint.isValidConfigType("yarn-site")).andReturn(false).anyTimes();
        expect(blueprint.isValidConfigType("cluster-env")).andReturn(true).anyTimes();
        expect(blueprint.isValidConfigType("global")).andReturn(true).anyTimes();
        expect(ambariContext.getConfigHelper()).andReturn(configHelper).anyTimes();
        expect(configHelper.getDefaultStackProperties(EasyMock.eq(new StackId(STACK_NAME, STACK_VERSION)))).andReturn(stackProperties).anyTimes();
        EasyMock.replay(stack, blueprint, topology, ambariContext, configHelper);
        // WHEN
        new ClusterConfigurationRequest(ambariContext, topology, false, stackAdvisorBlueprintProcessor);
        // THEN
        Assert.assertFalse("YARN service not present in topology config thus 'yarn-site' config type should be removed from config.", configuration.getFullProperties().containsKey("yarn-site"));
        Assert.assertTrue("HDFS service is present in topology host group config thus 'hdfs-site' config type should be left in the config.", configuration.getFullAttributes().containsKey("hdfs-site"));
        Assert.assertTrue("'cluster-env' config type should not be removed from configuration.", configuration.getFullProperties().containsKey("cluster-env"));
        Assert.assertTrue("'global' config type should not be removed from configuration.", configuration.getFullProperties().containsKey("global"));
        Assert.assertFalse("SPARK service not present in topology host group config thus 'spark-env' config type should be removed from config.", hg1.getConfiguration().getFullAttributes().containsKey("spark-env"));
        Assert.assertTrue("HDFS service is present in topology host group config thus 'hdfs-site' config type should be left in the config.", hg1.getConfiguration().getFullAttributes().containsKey("hdfs-site"));
        verify(stack, blueprint, topology, ambariContext, configHelper);
    }

    @Test
    public void testProcessClusterConfigRequestWithOnlyHostGroupConfigRemoveUnusedConfigTypes() throws Exception {
        // Given
        Map<String, Map<String, String>> config = Maps.newHashMap();
        config.put("cluster-env", new HashMap<>());
        config.put("global", new HashMap<>());
        Map<String, Map<String, Map<String, String>>> attributes = Maps.newHashMap();
        Configuration configuration = new Configuration(config, attributes);
        Set<String> services = new HashSet<>();
        services.add("HDFS");
        services.add("RANGER");
        Map<String, HostGroupInfo> hostGroupInfoMap = Maps.newHashMap();
        HostGroupInfo hg1 = new HostGroupInfo("hg1");
        hg1.setConfiguration(createConfigurationsForHostGroup());
        hostGroupInfoMap.put("hg1", hg1);
        expect(topology.getAmbariContext()).andReturn(ambariContext).anyTimes();
        expect(topology.getConfiguration()).andReturn(configuration).anyTimes();
        expect(topology.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(topology.getHostGroupInfo()).andReturn(hostGroupInfoMap);
        expect(blueprint.getStack()).andReturn(stack).anyTimes();
        expect(blueprint.getServices()).andReturn(services).anyTimes();
        expect(blueprint.isValidConfigType("hdfs-site")).andReturn(true).anyTimes();
        expect(blueprint.isValidConfigType("cluster-env")).andReturn(true).anyTimes();
        expect(blueprint.isValidConfigType("global")).andReturn(true).anyTimes();
        expect(ambariContext.getConfigHelper()).andReturn(configHelper).anyTimes();
        expect(configHelper.getDefaultStackProperties(EasyMock.eq(new StackId(STACK_NAME, STACK_VERSION)))).andReturn(stackProperties).anyTimes();
        EasyMock.replay(stack, blueprint, topology, ambariContext, configHelper);
        // When
        new ClusterConfigurationRequest(ambariContext, topology, false, stackAdvisorBlueprintProcessor);
        // Then
        Assert.assertTrue("'cluster-env' config type should not be removed from configuration.", configuration.getFullProperties().containsKey("cluster-env"));
        Assert.assertTrue("'global' config type should not be removed from configuration.", configuration.getFullProperties().containsKey("global"));
        Assert.assertFalse("SPARK service not present in topology host group config thus 'spark-env' config type should be removed from config.", hg1.getConfiguration().getFullAttributes().containsKey("spark-env"));
        Assert.assertTrue("HDFS service is present in topology host group config thus 'hdfs-site' config type should be left in the config.", hg1.getConfiguration().getFullAttributes().containsKey("hdfs-site"));
        verify(stack, blueprint, topology, ambariContext, configHelper);
    }
}

