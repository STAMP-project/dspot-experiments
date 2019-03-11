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
package org.apache.ambari.server.api.services.stackadvisor;


import ConfigRecommendationStrategy.ALWAYS_APPLY;
import ConfigRecommendationStrategy.ALWAYS_APPLY_DONT_OVERRIDE_CUSTOM_VALUES;
import ConfigRecommendationStrategy.ONLY_STACK_DEFAULTS_APPLY;
import StackAdvisorBlueprintProcessor.INVALID_RESPONSE;
import StackAdvisorBlueprintProcessor.RECOMMENDATION_FAILED;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.services.stackadvisor.recommendations.RecommendationResponse;
import org.apache.ambari.server.controller.internal.ConfigurationTopologyException;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.topology.AdvisedConfiguration;
import org.apache.ambari.server.topology.BlueprintImpl;
import org.apache.ambari.server.topology.ClusterTopology;
import org.apache.ambari.server.topology.Configuration;
import org.apache.ambari.server.topology.HostGroup;
import org.junit.Assert;
import org.junit.Test;


public class StackAdvisorBlueprintProcessorTest {
    private StackAdvisorBlueprintProcessor underTest = new StackAdvisorBlueprintProcessor();

    private ClusterTopology clusterTopology = createMock(ClusterTopology.class);

    private BlueprintImpl blueprint = createMock(BlueprintImpl.class);

    private Stack stack = createMock(Stack.class);

    private HostGroup hostGroup = createMock(HostGroup.class);

    private Configuration configuration = createMock(Configuration.class);

    private static StackAdvisorHelper stackAdvisorHelper = createMock(StackAdvisorHelper.class);

    @Test
    public void testAdviseConfiguration() throws AmbariException, StackAdvisorException, ConfigurationTopologyException {
        // GIVEN
        Map<String, Map<String, String>> props = createProps();
        Map<String, AdvisedConfiguration> advisedConfigurations = new HashMap<>();
        expect(clusterTopology.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(clusterTopology.getHostGroupInfo()).andReturn(createHostGroupInfo()).anyTimes();
        expect(clusterTopology.getAdvisedConfigurations()).andReturn(advisedConfigurations).anyTimes();
        expect(clusterTopology.getConfiguration()).andReturn(configuration).anyTimes();
        expect(clusterTopology.isClusterKerberosEnabled()).andReturn(false).anyTimes();
        expect(clusterTopology.getConfigRecommendationStrategy()).andReturn(ALWAYS_APPLY).anyTimes();
        expect(blueprint.getStack()).andReturn(stack).anyTimes();
        expect(stack.getVersion()).andReturn("2.3").anyTimes();
        expect(stack.getName()).andReturn("HDP").anyTimes();
        expect(stack.getConfiguration(Arrays.asList("HDFS", "YARN", "HIVE"))).andReturn(createStackDefaults()).anyTimes();
        expect(blueprint.getServices()).andReturn(Arrays.asList("HDFS", "YARN", "HIVE")).anyTimes();
        expect(blueprint.getHostGroups()).andReturn(createHostGroupMap()).anyTimes();
        expect(blueprint.isValidConfigType("core-site")).andReturn(true).anyTimes();
        expect(hostGroup.getComponentNames()).andReturn(Arrays.asList("comp1", "comp2")).anyTimes();
        expect(StackAdvisorBlueprintProcessorTest.stackAdvisorHelper.recommend(anyObject(StackAdvisorRequest.class))).andReturn(createRecommendationResponse());
        expect(configuration.getFullProperties()).andReturn(props).anyTimes();
        replay(clusterTopology, blueprint, stack, hostGroup, configuration, StackAdvisorBlueprintProcessorTest.stackAdvisorHelper);
        // WHEN
        underTest.adviseConfiguration(clusterTopology, props);
        // THEN
        Assert.assertTrue(advisedConfigurations.get("core-site").getProperties().containsKey("dummyKey1"));
        Assert.assertTrue(advisedConfigurations.get("core-site").getProperties().containsKey("dummyKey3"));
        Assert.assertTrue(advisedConfigurations.get("core-site").getPropertyValueAttributes().containsKey("dummyKey2"));
        Assert.assertTrue(advisedConfigurations.get("core-site").getPropertyValueAttributes().containsKey("dummyKey3"));
        Assert.assertEquals("dummyValue", advisedConfigurations.get("core-site").getProperties().get("dummyKey1"));
        Assert.assertEquals(Boolean.toString(true), advisedConfigurations.get("core-site").getPropertyValueAttributes().get("dummyKey2").getDelete());
    }

    @Test
    public void testAdviseConfigurationWithOnlyStackDefaultsApply() throws AmbariException, StackAdvisorException, ConfigurationTopologyException {
        // GIVEN
        Map<String, Map<String, String>> props = createProps();
        Map<String, AdvisedConfiguration> advisedConfigurations = new HashMap<>();
        expect(clusterTopology.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(clusterTopology.getHostGroupInfo()).andReturn(createHostGroupInfo()).anyTimes();
        expect(clusterTopology.getAdvisedConfigurations()).andReturn(advisedConfigurations).anyTimes();
        expect(clusterTopology.getConfiguration()).andReturn(configuration).anyTimes();
        expect(clusterTopology.isClusterKerberosEnabled()).andReturn(false).anyTimes();
        expect(clusterTopology.getConfigRecommendationStrategy()).andReturn(ONLY_STACK_DEFAULTS_APPLY);
        expect(blueprint.getStack()).andReturn(stack).anyTimes();
        expect(stack.getVersion()).andReturn("2.3").anyTimes();
        expect(stack.getName()).andReturn("HDP").anyTimes();
        expect(stack.getConfiguration(Arrays.asList("HDFS", "YARN", "HIVE"))).andReturn(createStackDefaults()).anyTimes();
        expect(blueprint.getServices()).andReturn(Arrays.asList("HDFS", "YARN", "HIVE")).anyTimes();
        expect(blueprint.getHostGroups()).andReturn(createHostGroupMap()).anyTimes();
        expect(blueprint.isValidConfigType("core-site")).andReturn(true).anyTimes();
        expect(hostGroup.getComponentNames()).andReturn(Arrays.asList("comp1", "comp2")).anyTimes();
        expect(StackAdvisorBlueprintProcessorTest.stackAdvisorHelper.recommend(anyObject(StackAdvisorRequest.class))).andReturn(createRecommendationResponse());
        expect(configuration.getFullProperties()).andReturn(props).anyTimes();
        replay(clusterTopology, blueprint, stack, hostGroup, configuration, StackAdvisorBlueprintProcessorTest.stackAdvisorHelper);
        // WHEN
        underTest.adviseConfiguration(clusterTopology, props);
        // THEN
        Assert.assertTrue(advisedConfigurations.get("core-site").getProperties().containsKey("dummyKey1"));
        Assert.assertFalse(advisedConfigurations.get("core-site").getProperties().containsKey("dummyKey3"));
        Assert.assertTrue(advisedConfigurations.get("core-site").getPropertyValueAttributes().containsKey("dummyKey2"));
        Assert.assertFalse(advisedConfigurations.get("core-site").getPropertyValueAttributes().containsKey("dummyKey3"));
        Assert.assertEquals("dummyValue", advisedConfigurations.get("core-site").getProperties().get("dummyKey1"));
        Assert.assertEquals(Boolean.toString(true), advisedConfigurations.get("core-site").getPropertyValueAttributes().get("dummyKey2").getDelete());
    }

    @Test
    public void testAdviseConfigurationWithOnlyStackDefaultsApplyWhenNoUserInputForDefault() throws AmbariException, StackAdvisorException, ConfigurationTopologyException {
        // GIVEN
        Map<String, Map<String, String>> props = createProps();
        props.get("core-site").put("dummyKey3", "stackDefaultValue");
        Map<String, AdvisedConfiguration> advisedConfigurations = new HashMap<>();
        expect(clusterTopology.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(clusterTopology.getHostGroupInfo()).andReturn(createHostGroupInfo()).anyTimes();
        expect(clusterTopology.getAdvisedConfigurations()).andReturn(advisedConfigurations).anyTimes();
        expect(clusterTopology.getConfiguration()).andReturn(configuration).anyTimes();
        expect(clusterTopology.isClusterKerberosEnabled()).andReturn(false).anyTimes();
        expect(clusterTopology.getConfigRecommendationStrategy()).andReturn(ONLY_STACK_DEFAULTS_APPLY);
        expect(blueprint.getStack()).andReturn(stack).anyTimes();
        expect(stack.getVersion()).andReturn("2.3").anyTimes();
        expect(stack.getName()).andReturn("HDP").anyTimes();
        expect(stack.getConfiguration(Arrays.asList("HDFS", "YARN", "HIVE"))).andReturn(createStackDefaults()).anyTimes();
        expect(blueprint.getServices()).andReturn(Arrays.asList("HDFS", "YARN", "HIVE")).anyTimes();
        expect(blueprint.getHostGroups()).andReturn(createHostGroupMap()).anyTimes();
        expect(blueprint.isValidConfigType("core-site")).andReturn(true).anyTimes();
        expect(hostGroup.getComponentNames()).andReturn(Arrays.asList("comp1", "comp2")).anyTimes();
        expect(StackAdvisorBlueprintProcessorTest.stackAdvisorHelper.recommend(anyObject(StackAdvisorRequest.class))).andReturn(createRecommendationResponse());
        expect(configuration.getFullProperties()).andReturn(props).anyTimes();
        replay(clusterTopology, blueprint, stack, hostGroup, configuration, StackAdvisorBlueprintProcessorTest.stackAdvisorHelper);
        // WHEN
        underTest.adviseConfiguration(clusterTopology, props);
        // THEN
        Assert.assertTrue(advisedConfigurations.get("core-site").getProperties().containsKey("dummyKey1"));
        Assert.assertTrue(advisedConfigurations.get("core-site").getPropertyValueAttributes().containsKey("dummyKey2"));
        Assert.assertEquals("dummyValue", advisedConfigurations.get("core-site").getProperties().get("dummyKey1"));
        Assert.assertEquals(Boolean.toString(true), advisedConfigurations.get("core-site").getPropertyValueAttributes().get("dummyKey2").getDelete());
    }

    @Test
    public void testAdviseConfigurationWith_ALWAYS_APPLY_DONT_OVERRIDE_CUSTOM_VALUES() throws AmbariException, StackAdvisorException, ConfigurationTopologyException {
        // GIVEN
        Map<String, Map<String, String>> props = createProps();
        Map<String, AdvisedConfiguration> advisedConfigurations = new HashMap<>();
        expect(clusterTopology.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(clusterTopology.getHostGroupInfo()).andReturn(createHostGroupInfo()).anyTimes();
        expect(clusterTopology.getAdvisedConfigurations()).andReturn(advisedConfigurations).anyTimes();
        expect(clusterTopology.getConfiguration()).andReturn(configuration).anyTimes();
        expect(clusterTopology.isClusterKerberosEnabled()).andReturn(false).anyTimes();
        expect(clusterTopology.getConfigRecommendationStrategy()).andReturn(ALWAYS_APPLY_DONT_OVERRIDE_CUSTOM_VALUES).anyTimes();
        expect(blueprint.getStack()).andReturn(stack).anyTimes();
        expect(stack.getVersion()).andReturn("2.3").anyTimes();
        expect(stack.getName()).andReturn("HDP").anyTimes();
        expect(stack.getConfiguration(Arrays.asList("HDFS", "YARN", "HIVE"))).andReturn(createStackDefaults()).anyTimes();
        expect(blueprint.getServices()).andReturn(Arrays.asList("HDFS", "YARN", "HIVE")).anyTimes();
        expect(blueprint.getHostGroups()).andReturn(createHostGroupMap()).anyTimes();
        expect(blueprint.isValidConfigType("core-site")).andReturn(true).anyTimes();
        expect(hostGroup.getComponentNames()).andReturn(Arrays.asList("comp1", "comp2")).anyTimes();
        expect(StackAdvisorBlueprintProcessorTest.stackAdvisorHelper.recommend(anyObject(StackAdvisorRequest.class))).andReturn(createRecommendationResponse());
        expect(configuration.getFullProperties()).andReturn(props).anyTimes();
        replay(clusterTopology, blueprint, stack, hostGroup, configuration, StackAdvisorBlueprintProcessorTest.stackAdvisorHelper);
        // WHEN
        underTest.adviseConfiguration(clusterTopology, props);
        // THEN
        Assert.assertTrue(advisedConfigurations.get("core-site").getProperties().containsKey("dummyKey1"));
        Assert.assertTrue(advisedConfigurations.get("core-site").getPropertyValueAttributes().containsKey("dummyKey2"));
        Assert.assertEquals("dummyValue", advisedConfigurations.get("core-site").getProperties().get("dummyKey1"));
        Assert.assertEquals(Boolean.toString(true), advisedConfigurations.get("core-site").getPropertyValueAttributes().get("dummyKey2").getDelete());
    }

    @Test
    public void testAdviseConfigurationWhenConfigurationRecommendFails() throws AmbariException, StackAdvisorException, ConfigurationTopologyException {
        // GIVEN
        Map<String, Map<String, String>> props = createProps();
        Map<String, AdvisedConfiguration> advisedConfigurations = new HashMap<>();
        expect(clusterTopology.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(clusterTopology.getHostGroupInfo()).andReturn(createHostGroupInfo()).anyTimes();
        expect(clusterTopology.getAdvisedConfigurations()).andReturn(advisedConfigurations).anyTimes();
        expect(clusterTopology.getConfiguration()).andReturn(configuration).anyTimes();
        expect(clusterTopology.isClusterKerberosEnabled()).andReturn(false).anyTimes();
        expect(blueprint.getStack()).andReturn(stack).anyTimes();
        expect(stack.getVersion()).andReturn("2.3").anyTimes();
        expect(stack.getName()).andReturn("HDP").anyTimes();
        expect(blueprint.getHostGroups()).andReturn(createHostGroupMap()).anyTimes();
        expect(hostGroup.getComponentNames()).andReturn(Arrays.asList("comp1", "comp2")).anyTimes();
        expect(blueprint.getServices()).andReturn(Arrays.asList("HDFS", "YARN", "HIVE")).anyTimes();
        expect(StackAdvisorBlueprintProcessorTest.stackAdvisorHelper.recommend(anyObject(StackAdvisorRequest.class))).andThrow(new StackAdvisorException("ex"));
        expect(configuration.getFullProperties()).andReturn(props);
        replay(clusterTopology, blueprint, stack, hostGroup, configuration, StackAdvisorBlueprintProcessorTest.stackAdvisorHelper);
        // WHEN
        try {
            underTest.adviseConfiguration(clusterTopology, props);
            Assert.fail("Invalid state");
        } catch (ConfigurationTopologyException e) {
            Assert.assertEquals(RECOMMENDATION_FAILED, e.getMessage());
        }
    }

    @Test
    public void testAdviseConfigurationWhenConfigurationRecommendHasInvalidResponse() throws AmbariException, StackAdvisorException, ConfigurationTopologyException {
        // GIVEN
        Map<String, Map<String, String>> props = createProps();
        Map<String, AdvisedConfiguration> advisedConfigurations = new HashMap<>();
        expect(clusterTopology.getBlueprint()).andReturn(blueprint).anyTimes();
        expect(clusterTopology.getHostGroupInfo()).andReturn(createHostGroupInfo()).anyTimes();
        expect(clusterTopology.getAdvisedConfigurations()).andReturn(advisedConfigurations).anyTimes();
        expect(clusterTopology.getConfiguration()).andReturn(configuration).anyTimes();
        expect(clusterTopology.isClusterKerberosEnabled()).andReturn(false).anyTimes();
        expect(blueprint.getStack()).andReturn(stack).anyTimes();
        expect(stack.getVersion()).andReturn("2.3").anyTimes();
        expect(stack.getName()).andReturn("HDP").anyTimes();
        expect(blueprint.getServices()).andReturn(Arrays.asList("HDFS", "YARN", "HIVE")).anyTimes();
        expect(blueprint.getHostGroups()).andReturn(createHostGroupMap()).anyTimes();
        expect(hostGroup.getComponentNames()).andReturn(Arrays.asList("comp1", "comp2")).anyTimes();
        expect(StackAdvisorBlueprintProcessorTest.stackAdvisorHelper.recommend(anyObject(StackAdvisorRequest.class))).andReturn(new RecommendationResponse());
        expect(configuration.getFullProperties()).andReturn(props);
        replay(clusterTopology, blueprint, stack, hostGroup, configuration, StackAdvisorBlueprintProcessorTest.stackAdvisorHelper);
        // WHEN
        try {
            underTest.adviseConfiguration(clusterTopology, props);
            Assert.fail("Invalid state");
        } catch (ConfigurationTopologyException e) {
            Assert.assertEquals(INVALID_RESPONSE, e.getMessage());
        }
    }
}

