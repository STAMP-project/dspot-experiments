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
package org.apache.ambari.server.topology.addservice;


import AddServiceInfo.Builder;
import ConfigRecommendationStrategy.ALWAYS_APPLY;
import ConfigRecommendationStrategy.ALWAYS_APPLY_DONT_OVERRIDE_CUSTOM_VALUES;
import ConfigRecommendationStrategy.NEVER_APPLY;
import ConfigRecommendationStrategy.ONLY_STACK_DEFAULTS_APPLY;
import RecommendationResponse.BlueprintConfigurations;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.stackadvisor.StackAdvisorHelper;
import org.apache.ambari.server.api.services.stackadvisor.recommendations.RecommendationResponse;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.testutils.TestCollectionUtils;
import org.apache.ambari.server.topology.Configuration;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class StackAdvisorAdapterTest {
    @Mock
    private AmbariManagementController managementController;

    @Mock
    private StackAdvisorHelper stackAdvisorHelper;

    @Mock
    private Configuration serverConfig;

    @Mock
    private Injector injector;

    @Mock
    private Stack stack;

    @TestSubject
    private StackAdvisorAdapter adapter = new StackAdvisorAdapter();

    private static final Map<String, Set<String>> COMPONENT_HOST_MAP = ImmutableMap.<String, Set<String>>builder().put("NAMENODE", ImmutableSet.of("c7401", "c7402")).put("DATANODE", ImmutableSet.of("c7403", "c7404", "c7405", "c7406")).put("HDFS_CLIENT", ImmutableSet.of("c7403", "c7404", "c7405", "c7406")).put("ZOOKEEPER_SERVER", ImmutableSet.of("c7401", "c7402")).put("ZOOKEEPER_CLIENT", ImmutableSet.of("c7401", "c7402", "c7403", "c7404", "c7405", "c7406")).build();

    private static final Map<String, Map<String, Set<String>>> SERVICE_COMPONENT_HOST_MAP_1 = ImmutableMap.of("HDFS", ImmutableMap.of("NAMENODE", ImmutableSet.of("c7401", "c7402"), "DATANODE", ImmutableSet.of("c7403", "c7404", "c7405", "c7406"), "HDFS_CLIENT", ImmutableSet.of("c7403", "c7404", "c7405", "c7406")), "ZOOKEEPER", ImmutableMap.of("ZOOKEEPER_SERVER", ImmutableSet.of("c7401", "c7402"), "ZOOKEEPER_CLIENT", ImmutableSet.of("c7401", "c7402", "c7403", "c7404", "c7405", "c7406")));

    private static final Map<String, Map<String, Set<String>>> SERVICE_COMPONENT_HOST_MAP_2 = ImmutableMap.<String, Map<String, Set<String>>>builder().putAll(StackAdvisorAdapterTest.SERVICE_COMPONENT_HOST_MAP_1).put("HIVE", Collections.emptyMap()).put("SPARK2", Collections.emptyMap()).build();

    private static final Map<String, Set<String>> HOST_COMPONENT_MAP = ImmutableMap.<String, Set<String>>builder().put("c7401", ImmutableSet.of("NAMENODE", "ZOOKEEPER_SERVER", "ZOOKEEPER_CLIENT")).put("c7402", ImmutableSet.of("NAMENODE", "ZOOKEEPER_SERVER", "ZOOKEEPER_CLIENT")).put("c7403", ImmutableSet.of("DATANODE", "HDFS_CLIENT", "ZOOKEEPER_CLIENT")).put("c7404", ImmutableSet.of("DATANODE", "HDFS_CLIENT", "ZOOKEEPER_CLIENT")).put("c7405", ImmutableSet.of("DATANODE", "HDFS_CLIENT", "ZOOKEEPER_CLIENT")).put("c7406", ImmutableSet.of("DATANODE", "HDFS_CLIENT", "ZOOKEEPER_CLIENT")).build();

    private final Builder addServiceInfoBuilder = new AddServiceInfo.Builder().setClusterName("c1");

    @Test
    public void getHostComponentMap() {
        Assert.assertEquals(StackAdvisorAdapterTest.HOST_COMPONENT_MAP, StackAdvisorAdapter.getHostComponentMap(StackAdvisorAdapterTest.COMPONENT_HOST_MAP));
    }

    @Test
    public void getComponentHostMap() {
        Assert.assertEquals(StackAdvisorAdapterTest.COMPONENT_HOST_MAP, StackAdvisorAdapter.getComponentHostMap(StackAdvisorAdapterTest.SERVICE_COMPONENT_HOST_MAP_2));
    }

    @Test
    public void getRecommendedLayout() {
        Map<String, Set<String>> hostGroups = ImmutableMap.of("host_group1", ImmutableSet.of("c7401", "c7402"), "host_group2", ImmutableSet.of("c7403", "c7404", "c7405", "c7406"));
        Map<String, Set<String>> hostGroupComponents = ImmutableMap.of("host_group1", ImmutableSet.of("NAMENODE", "ZOOKEEPER_SERVER", "ZOOKEEPER_CLIENT"), "host_group2", ImmutableSet.of("DATANODE", "HDFS_CLIENT", "ZOOKEEPER_CLIENT"));
        Map<String, String> serviceToComponent = ImmutableMap.<String, String>builder().put("NAMENODE", "HDFS").put("DATANODE", "HDFS").put("HDFS_CLIENT", "HDFS").put("ZOOKEEPER_SERVER", "ZOOKEEPER").put("ZOOKEEPER_CLIENT", "ZOOKEEPER").build();
        Assert.assertEquals(StackAdvisorAdapterTest.SERVICE_COMPONENT_HOST_MAP_1, StackAdvisorAdapter.getRecommendedLayout(hostGroups, hostGroupComponents, serviceToComponent::get));
    }

    @Test
    public void mergeDisjunctMaps() {
        Map<String, String> map1 = ImmutableMap.of("key1", "value1", "key2", "value2");
        Map<String, String> map2 = ImmutableMap.of("key3", "value3", "key4", "value4");
        Assert.assertEquals(ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4"), StackAdvisorAdapter.mergeDisjunctMaps(map1, map2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergeDisjunctMaps_invalidInput() {
        Map<String, String> map1 = ImmutableMap.of("key1", "value1", "key2", "value2");
        Map<String, String> map2 = ImmutableMap.of("key2", "value2", "key3", "value3");
        StackAdvisorAdapter.mergeDisjunctMaps(map1, map2);
    }

    @Test
    public void keepNewServicesOnly() {
        Map<String, Map<String, Set<String>>> newServices = ImmutableMap.of("KAFKA", Collections.emptyMap(), "PIG", Collections.emptyMap());
        Map<String, Map<String, Set<String>>> expectedNewServiceRecommendations = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7405")), "PIG", ImmutableMap.of("PIG_CLIENT", ImmutableSet.of("c7405", "c7406")));
        Map<String, Map<String, Set<String>>> recommendations = new HashMap<>(StackAdvisorAdapterTest.SERVICE_COMPONENT_HOST_MAP_1);
        recommendations.putAll(expectedNewServiceRecommendations);
        Map<String, Map<String, Set<String>>> newServiceRecommendations = StackAdvisorAdapter.keepNewServicesOnly(recommendations, newServices);
        Assert.assertEquals(expectedNewServiceRecommendations, newServiceRecommendations);
    }

    @Test
    public void recommendLayout() {
        Map<String, Map<String, Set<String>>> newServices = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", Collections.emptySet()));
        AddServiceInfo info = addServiceInfoBuilder.setStack(stack).setConfig(Configuration.newEmpty()).setNewServices(newServices).build();
        AddServiceInfo infoWithRecommendations = adapter.recommendLayout(info);
        Map<String, Map<String, Set<String>>> expectedNewLayout = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7402")));
        Assert.assertEquals(expectedNewLayout, infoWithRecommendations.newServices());
    }

    @Test
    public void recommendConfigurations_noLayoutInfo() {
        Map<String, Map<String, Set<String>>> newServices = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7401")), "SPARK2", ImmutableMap.of("SPARK2_JOBHISTORYSERVER", ImmutableSet.of("c7402"), "SPARK2_CLIENT", ImmutableSet.of("c7403", "c7404")), "OOZIE", ImmutableMap.of("OOZIE_SERVER", ImmutableSet.of("c7401"), "OOZIE_CLIENT", ImmutableSet.of("c7403", "c7404")));
        Configuration stackConfig = Configuration.newEmpty();
        Configuration clusterConfig = new Configuration(TestCollectionUtils.map("oozie-env", TestCollectionUtils.map("oozie_heapsize", "1024", "oozie_permsize", "256")), Collections.emptyMap());
        Configuration userConfig = Configuration.newEmpty();
        userConfig.setParentConfiguration(clusterConfig);
        clusterConfig.setParentConfiguration(stackConfig);
        AddServiceRequest request = StackAdvisorAdapterTest.request(ALWAYS_APPLY);
        AddServiceInfo info = addServiceInfoBuilder.setRequest(request).setStack(stack).setConfig(userConfig).setNewServices(newServices).build();// No LayoutRecommendationInfo

        AddServiceInfo infoWithConfig = adapter.recommendConfigurations(info);
        Configuration recommendedConfig = infoWithConfig.getConfig();
        Assert.assertSame(userConfig, recommendedConfig.getParentConfiguration());
        Assert.assertSame(clusterConfig, userConfig.getParentConfiguration());
        Assert.assertSame(stackConfig, clusterConfig.getParentConfiguration());
        // Yarn/Mapred config is excpected to be removed as it does not belong to newly added services
        Assert.assertEquals(ImmutableMap.of("kafka-broker", ImmutableMap.of("log.dirs", "/kafka-logs", "offsets.topic.replication.factor", "1"), "spark2-defaults", ImmutableMap.of("spark.yarn.queue", "default"), "oozie-env", // unit updates should happen
        ImmutableMap.of("oozie_heapsize", "1024m", "oozie_permsize", "256m")), recommendedConfig.getProperties());
        Assert.assertEquals(ImmutableMap.of("kafka-broker", ImmutableMap.of("maximum", ImmutableMap.of("offsets.topic.replication.factor", "10"))), recommendedConfig.getAttributes());
    }

    @Test
    public void recommendConfigurations_alwaysApply() {
        Map<String, Map<String, Set<String>>> newServices = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7401")), "SPARK2", ImmutableMap.of("SPARK2_JOBHISTORYSERVER", ImmutableSet.of("c7402"), "SPARK2_CLIENT", ImmutableSet.of("c7403", "c7404")), "OOZIE", ImmutableMap.of("OOZIE_SERVER", ImmutableSet.of("c7401"), "OOZIE_CLIENT", ImmutableSet.of("c7403", "c7404")));
        Configuration stackConfig = Configuration.newEmpty();
        Configuration clusterConfig = new Configuration(TestCollectionUtils.map("oozie-env", TestCollectionUtils.map("oozie_heapsize", "1024", "oozie_permsize", "256")), Collections.emptyMap());
        Configuration userConfig = Configuration.newEmpty();
        userConfig.setParentConfiguration(clusterConfig);
        clusterConfig.setParentConfiguration(stackConfig);
        LayoutRecommendationInfo layoutRecommendationInfo = new LayoutRecommendationInfo(new HashMap(), new HashMap());// contents doesn't matter for the test

        AddServiceRequest request = StackAdvisorAdapterTest.request(ALWAYS_APPLY);
        AddServiceInfo info = addServiceInfoBuilder.setRequest(request).setStack(stack).setConfig(userConfig).setNewServices(newServices).setRecommendationInfo(layoutRecommendationInfo).build();
        AddServiceInfo infoWithConfig = adapter.recommendConfigurations(info);
        Configuration recommendedConfig = infoWithConfig.getConfig();
        Assert.assertSame(userConfig, recommendedConfig.getParentConfiguration());
        Assert.assertSame(clusterConfig, userConfig.getParentConfiguration());
        Assert.assertSame(stackConfig, clusterConfig.getParentConfiguration());
        // Yarn/Mapred config is excpected to be removed as it does not belong to newly added services
        Assert.assertEquals(ImmutableMap.of("kafka-broker", ImmutableMap.of("log.dirs", "/kafka-logs", "offsets.topic.replication.factor", "1"), "spark2-defaults", ImmutableMap.of("spark.yarn.queue", "default"), "oozie-env", // unit updates should happen
        ImmutableMap.of("oozie_heapsize", "1024m", "oozie_permsize", "256m")), recommendedConfig.getProperties());
        Assert.assertEquals(ImmutableMap.of("kafka-broker", ImmutableMap.of("maximum", ImmutableMap.of("offsets.topic.replication.factor", "10"))), recommendedConfig.getAttributes());
    }

    @Test
    public void recommendConfigurations_alwaysDoNotOverrideCustomValues() {
        Map<String, Map<String, Set<String>>> newServices = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7401")), "SPARK2", ImmutableMap.of("SPARK2_JOBHISTORYSERVER", ImmutableSet.of("c7402"), "SPARK2_CLIENT", ImmutableSet.of("c7403", "c7404")), "OOZIE", ImmutableMap.of("OOZIE_SERVER", ImmutableSet.of("c7401"), "OOZIE_CLIENT", ImmutableSet.of("c7403", "c7404")));
        Configuration stackConfig = Configuration.newEmpty();
        Configuration clusterConfig = new Configuration(TestCollectionUtils.map("oozie-env", TestCollectionUtils.map("oozie_heapsize", "1024", "oozie_permsize", "256")), Collections.emptyMap());
        Configuration userConfig = Configuration.newEmpty();
        userConfig.setParentConfiguration(clusterConfig);
        clusterConfig.setParentConfiguration(stackConfig);
        LayoutRecommendationInfo layoutRecommendationInfo = new LayoutRecommendationInfo(new HashMap(), new HashMap());// contents doesn't matter for the test

        AddServiceRequest request = StackAdvisorAdapterTest.request(ALWAYS_APPLY_DONT_OVERRIDE_CUSTOM_VALUES);
        AddServiceInfo info = addServiceInfoBuilder.setRequest(request).setStack(stack).setConfig(userConfig).setNewServices(newServices).setRecommendationInfo(layoutRecommendationInfo).build();
        AddServiceInfo infoWithConfig = adapter.recommendConfigurations(info);
        Assert.assertSame(userConfig, infoWithConfig.getConfig());// user config stays top priority

        Configuration recommendedConfig = userConfig.getParentConfiguration();
        Assert.assertSame(clusterConfig, recommendedConfig.getParentConfiguration());
        Assert.assertSame(stackConfig, clusterConfig.getParentConfiguration());
        // Yarn/Mapred config is excpected to be removed as it does not belong to newly added services
        Assert.assertEquals(ImmutableMap.of("kafka-broker", ImmutableMap.of("log.dirs", "/kafka-logs", "offsets.topic.replication.factor", "1"), "spark2-defaults", ImmutableMap.of("spark.yarn.queue", "default")), recommendedConfig.getProperties());
        // the result of unit updates always happen in the top level config
        Assert.assertEquals(ImmutableMap.of("oozie-env", ImmutableMap.of("oozie_heapsize", "1024m", "oozie_permsize", "256m")), userConfig.getProperties());
        Assert.assertEquals(ImmutableMap.of("kafka-broker", ImmutableMap.of("maximum", ImmutableMap.of("offsets.topic.replication.factor", "10"))), recommendedConfig.getAttributes());
    }

    @Test
    public void recommendConfigurations_neverApply() {
        Map<String, Map<String, Set<String>>> newServices = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7401")), "SPARK2", ImmutableMap.of("SPARK2_JOBHISTORYSERVER", ImmutableSet.of("c7402"), "SPARK2_CLIENT", ImmutableSet.of("c7403", "c7404")), "OOZIE", ImmutableMap.of("OOZIE_SERVER", ImmutableSet.of("c7401"), "OOZIE_CLIENT", ImmutableSet.of("c7403", "c7404")));
        Configuration stackConfig = Configuration.newEmpty();
        Configuration clusterConfig = new Configuration(TestCollectionUtils.map("oozie-env", TestCollectionUtils.map("oozie_heapsize", "1024", "oozie_permsize", "256")), Collections.emptyMap());
        Configuration userConfig = Configuration.newEmpty();
        userConfig.setParentConfiguration(clusterConfig);
        clusterConfig.setParentConfiguration(stackConfig);
        LayoutRecommendationInfo layoutRecommendationInfo = new LayoutRecommendationInfo(new HashMap(), new HashMap());// contents doesn't matter for the test

        AddServiceRequest request = StackAdvisorAdapterTest.request(NEVER_APPLY);
        AddServiceInfo info = addServiceInfoBuilder.setRequest(request).setStack(stack).setConfig(userConfig).setNewServices(newServices).setRecommendationInfo(layoutRecommendationInfo).build();
        AddServiceInfo infoWithConfig = adapter.recommendConfigurations(info);
        // No recommended config, no stack config
        Assert.assertSame(userConfig, infoWithConfig.getConfig());
        Assert.assertSame(clusterConfig, userConfig.getParentConfiguration());
        Assert.assertNotNull(clusterConfig.getParentConfiguration());
        // the result of unit updates always happen in the top level config
        Assert.assertEquals(ImmutableMap.of("oozie-env", ImmutableMap.of("oozie_heapsize", "1024m", "oozie_permsize", "256m")), userConfig.getProperties());
    }

    @Test
    public void recommendConfigurations_onlyStackDefaultsApply() {
        Map<String, Map<String, Set<String>>> newServices = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7401")), "SPARK2", ImmutableMap.of("SPARK2_JOBHISTORYSERVER", ImmutableSet.of("c7402"), "SPARK2_CLIENT", ImmutableSet.of("c7403", "c7404")), "OOZIE", ImmutableMap.of("OOZIE_SERVER", ImmutableSet.of("c7401"), "OOZIE_CLIENT", ImmutableSet.of("c7403", "c7404")));
        Configuration stackConfig = new Configuration(ImmutableMap.of("kafka-broker", ImmutableMap.of("log.dirs", "/kafka-logs-stackdefault")), ImmutableMap.of());
        Configuration clusterConfig = new Configuration(ImmutableMap.of("oozie-env", ImmutableMap.of("oozie_heapsize", "1024", "oozie_permsize", "256")), Collections.emptyMap());
        Configuration userConfig = Configuration.newEmpty();
        userConfig.setParentConfiguration(clusterConfig);
        clusterConfig.setParentConfiguration(stackConfig);
        LayoutRecommendationInfo layoutRecommendationInfo = new LayoutRecommendationInfo(new HashMap(), new HashMap());// contents doesn't matter for the test

        AddServiceRequest request = StackAdvisorAdapterTest.request(ONLY_STACK_DEFAULTS_APPLY);
        AddServiceInfo info = addServiceInfoBuilder.setRequest(request).setStack(stack).setConfig(userConfig).setNewServices(newServices).setRecommendationInfo(layoutRecommendationInfo).build();
        AddServiceInfo infoWithConfig = adapter.recommendConfigurations(info);
        Configuration recommendedConfig = infoWithConfig.getConfig().getParentConfiguration();
        // No recommended config
        Assert.assertSame(userConfig, infoWithConfig.getConfig());// user config is top level in this case

        Assert.assertSame(clusterConfig, recommendedConfig.getParentConfiguration());
        Assert.assertSame(stackConfig, clusterConfig.getParentConfiguration());
        Assert.assertEquals(ImmutableMap.of("kafka-broker", ImmutableMap.of("log.dirs", "/kafka-logs")), recommendedConfig.getProperties());
        // the result of unit updates always happen in the top level config
        Assert.assertEquals(ImmutableMap.of("oozie-env", ImmutableMap.of("oozie_heapsize", "1024m", "oozie_permsize", "256m")), userConfig.getProperties());
    }

    @Test
    public void removeNonStackConfigRecommendations() {
        Map<String, Map<String, String>> stackProperties = ImmutableMap.of("kafka-broker", ImmutableMap.of("log.dirs", "/kafka-logs", "offsets.topic.replication.factor", "1"), "spark2-defaults", ImmutableMap.of("spark.yarn.queue", "default"));
        Map<String, Map<String, Map<String, String>>> stackAttributes = ImmutableMap.of("oozie-env", ImmutableMap.of("miniumum", ImmutableMap.of("oozie_heapsize", "1024", "oozie_permsize", "256")));
        Configuration stackConfig = new Configuration(stackProperties, stackAttributes);
        Map<String, RecommendationResponse.BlueprintConfigurations> recommendedConfigs = TestCollectionUtils.map("hdfs-site", BlueprintConfigurations.create(TestCollectionUtils.map("dfs.namenode.name.dir", "/hadoop/hdfs/namenode"), TestCollectionUtils.map("visible", ImmutableMap.of("dfs.namenode.name.dir", "false"))), "oozie-env", BlueprintConfigurations.create(TestCollectionUtils.map("oozie_heapsize", "2048"), new HashMap()), "spark2-defaults", BlueprintConfigurations.create(TestCollectionUtils.map("spark.yarn.queue", "spark2"), new HashMap()));
        Map<String, RecommendationResponse.BlueprintConfigurations> recommendedConfigsForStackDefaults = ImmutableMap.of("oozie-env", BlueprintConfigurations.create(ImmutableMap.of("oozie_heapsize", "2048"), ImmutableMap.of()), "spark2-defaults", BlueprintConfigurations.create(ImmutableMap.of("spark.yarn.queue", "spark2"), ImmutableMap.of()));
        StackAdvisorAdapter.removeNonStackConfigRecommendations(stackConfig, recommendedConfigs);
        Assert.assertEquals(recommendedConfigsForStackDefaults, recommendedConfigs);
    }

    @Test
    public void getLayoutRecommendationInfo() {
        Map<String, Map<String, Set<String>>> newServices = ImmutableMap.of("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7401")), "SPARK2", ImmutableMap.of("SPARK2_JOBHISTORYSERVER", ImmutableSet.of("c7402"), "SPARK2_CLIENT", ImmutableSet.of("c7403", "c7404")), "OOZIE", ImmutableMap.of("OOZIE_SERVER", ImmutableSet.of("c7401"), "OOZIE_CLIENT", ImmutableSet.of("c7403", "c7404")));
        AddServiceRequest request = StackAdvisorAdapterTest.request(ALWAYS_APPLY);
        AddServiceInfo info = addServiceInfoBuilder.setRequest(request).setStack(stack).setConfig(Configuration.newEmpty()).setNewServices(newServices).build();// No LayoutReommendationInfo -> needs to be calculated

        LayoutRecommendationInfo layoutRecommendationInfo = adapter.getLayoutRecommendationInfo(info);
        layoutRecommendationInfo.getAllServiceLayouts();
        Assert.assertEquals(ImmutableMap.of("host_group_1", ImmutableSet.of("c7401"), "host_group_2", ImmutableSet.of("c7402"), "host_group_3", ImmutableSet.of("c7403", "c7404")), layoutRecommendationInfo.getHostGroups());
        Assert.assertEquals(ImmutableMap.<String, Map<String, Set<String>>>builder().put("KAFKA", ImmutableMap.of("KAFKA_BROKER", ImmutableSet.of("c7401"))).put("SPARK2", ImmutableMap.of("SPARK2_JOBHISTORYSERVER", ImmutableSet.of("c7402"), "SPARK2_CLIENT", ImmutableSet.of("c7403", "c7404"))).put("OOZIE", ImmutableMap.of("OOZIE_SERVER", ImmutableSet.of("c7401"), "OOZIE_CLIENT", ImmutableSet.of("c7403", "c7404"))).put("HDFS", ImmutableMap.of("NAMENODE", ImmutableSet.of("c7401"), "HDFS_CLIENT", ImmutableSet.of("c7401", "c7402"))).put("ZOOKEEPER", ImmutableMap.of("ZOOKEEPER_SERVER", ImmutableSet.of("c7401"), "ZOOKEEPER_CLIENT", ImmutableSet.of("c7401", "c7402"))).put("MAPREDUCE2", ImmutableMap.of("HISTORYSERVER", ImmutableSet.of("c7401"))).build(), layoutRecommendationInfo.getAllServiceLayouts());
    }
}

