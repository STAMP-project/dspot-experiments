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
package org.apache.ambari.server.api.query.render;


import ClusterBlueprintRenderer.COMPONENT_SETTINGS;
import ClusterBlueprintRenderer.CREDENTIAL_STORE_ENABLED;
import ClusterBlueprintRenderer.FALSE;
import ClusterBlueprintRenderer.RECOVERY_ENABLED;
import ClusterBlueprintRenderer.SERVICE_SETTINGS;
import ClusterBlueprintRenderer.TRUE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.query.QueryInfo;
import org.apache.ambari.server.api.resources.ClusterResourceDefinition;
import org.apache.ambari.server.api.resources.HostComponentResourceDefinition;
import org.apache.ambari.server.api.resources.HostResourceDefinition;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.api.services.ResultImpl;
import org.apache.ambari.server.api.util.TreeNode;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.AmbariManagementControllerImpl;
import org.apache.ambari.server.controller.AmbariServer;
import org.apache.ambari.server.controller.KerberosHelper;
import org.apache.ambari.server.controller.KerberosHelperImpl;
import org.apache.ambari.server.controller.internal.BlueprintExportType;
import org.apache.ambari.server.controller.internal.ClusterControllerImpl;
import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.controller.spi.ClusterController;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.DesiredConfig;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.cluster.ClustersImpl;
import org.apache.ambari.server.state.kerberos.KerberosDescriptor;
import org.apache.ambari.server.topology.AmbariContext;
import org.apache.ambari.server.topology.Blueprint;
import org.apache.ambari.server.topology.ClusterTopology;
import org.apache.ambari.server.topology.Configuration;
import org.apache.ambari.server.topology.HostGroup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * ClusterBlueprintRenderer unit tests.
 */
@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AmbariContext.class, AmbariServer.class })
public class ClusterBlueprintRendererTest {
    private static final ClusterTopology topology = createNiceMock(ClusterTopology.class);

    private static final ClusterController clusterController = createNiceMock(ClusterControllerImpl.class);

    private static final AmbariContext ambariContext = createNiceMock(AmbariContext.class);

    private static final Cluster cluster = createNiceMock(Cluster.class);

    private static final Clusters clusters = createNiceMock(ClustersImpl.class);

    private static final AmbariManagementController controller = createNiceMock(AmbariManagementControllerImpl.class);

    private static final KerberosHelper kerberosHelper = createNiceMock(KerberosHelperImpl.class);

    private static final KerberosDescriptor kerberosDescriptor = createNiceMock(KerberosDescriptor.class);

    private static final Blueprint blueprint = createNiceMock(Blueprint.class);

    private static final Stack stack = createNiceMock(Stack.class);

    private static final HostGroup group1 = createNiceMock(HostGroup.class);

    private static final HostGroup group2 = createNiceMock(HostGroup.class);

    private static final Configuration emptyConfiguration = new Configuration(new HashMap(), new HashMap());

    private static final Map<String, Map<String, String>> clusterProps = new HashMap<>();

    private static final Map<String, Map<String, Map<String, String>>> clusterAttributes = new HashMap<>();

    private static final Configuration clusterConfig = new Configuration(ClusterBlueprintRendererTest.clusterProps, ClusterBlueprintRendererTest.clusterAttributes);

    private final ClusterBlueprintRenderer minimalRenderer = new ClusterBlueprintRenderer(BlueprintExportType.MINIMAL);

    private final ClusterBlueprintRenderer fullRenderer = new ClusterBlueprintRenderer(BlueprintExportType.FULL);

    @Test
    public void testFinalizeProperties__instance() {
        QueryInfo rootQuery = new QueryInfo(new ClusterResourceDefinition(), new HashSet());
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Cluster");
        rootQuery.getProperties().add("foo/bar");
        rootQuery.getProperties().add("prop1");
        QueryInfo hostInfo = new QueryInfo(new HostResourceDefinition(), new HashSet());
        queryTree.addChild(hostInfo, "Host");
        QueryInfo hostComponentInfo = new QueryInfo(new HostComponentResourceDefinition(), new HashSet());
        queryTree.getChild("Host").addChild(hostComponentInfo, "HostComponent");
        TreeNode<Set<String>> propertyTree = fullRenderer.finalizeProperties(queryTree, false);
        Set<String> rootProperties = propertyTree.getObject();
        Assert.assertEquals(2, rootProperties.size());
        Assert.assertNotNull(propertyTree.getChild("Host"));
        Assert.assertTrue(propertyTree.getChild("Host").getObject().isEmpty());
        Assert.assertNotNull(propertyTree.getChild("Host/HostComponent"));
        Assert.assertEquals(1, propertyTree.getChild("Host/HostComponent").getObject().size());
        Assert.assertTrue(propertyTree.getChild("Host/HostComponent").getObject().contains("HostRoles/component_name"));
    }

    @Test
    public void clusterWithDefaultSettings() {
        Stack stack = ClusterBlueprintRendererTest.stackForSettingsTest();
        TreeNode<Resource> clusterNode = ClusterBlueprintRendererTest.clusterWith(stack, stack.getComponents(), ClusterBlueprintRendererTest.defaultCredentialStoreSettings(), ClusterBlueprintRendererTest.defaultRecoverySettings());
        Collection<Map<String, Object>> settings = fullRenderer.getSettings(clusterNode, stack);
        Assert.assertEquals(Lists.newArrayList(ImmutableMap.of(SERVICE_SETTINGS, ImmutableSet.of()), ImmutableMap.of(COMPONENT_SETTINGS, ImmutableSet.of())), settings);
        Assert.assertEquals(ImmutableList.of(), minimalRenderer.getSettings(clusterNode, stack));
    }

    @Test
    public void clusterWithCustomSettings() {
        Stack stack = ClusterBlueprintRendererTest.stackForSettingsTest();
        TreeNode<Resource> clusterNode = ClusterBlueprintRendererTest.clusterWith(stack, stack.getComponents(), ClusterBlueprintRendererTest.customCredentialStoreSettingFor(stack, "service1", "service2"), ClusterBlueprintRendererTest.customRecoverySettingsFor(stack, "component1", "component2"));
        Collection<Map<String, Object>> settings = fullRenderer.getSettings(clusterNode, stack);
        Assert.assertEquals(Lists.newArrayList(ImmutableMap.of(SERVICE_SETTINGS, ImmutableSet.of(ImmutableMap.of("name", "service1", CREDENTIAL_STORE_ENABLED, FALSE), ImmutableMap.of("name", "service2", CREDENTIAL_STORE_ENABLED, TRUE))), ImmutableMap.of(COMPONENT_SETTINGS, ImmutableSet.of(ImmutableMap.of("name", "component1", RECOVERY_ENABLED, FALSE), ImmutableMap.of("name", "component2", RECOVERY_ENABLED, TRUE)))), settings);
        Assert.assertEquals(settings, minimalRenderer.getSettings(clusterNode, stack));
    }

    @Test
    public void clusterWithRecoveryDisabled() {
        Stack stack = ClusterBlueprintRendererTest.stackForSettingsTest();
        TreeNode<Resource> clusterNode = ClusterBlueprintRendererTest.clusterWith(stack, stack.getComponents(), ClusterBlueprintRendererTest.defaultCredentialStoreSettings(), ClusterBlueprintRendererTest.customRecoverySettingsFor(stack, "component1"));
        Collection<Map<String, Object>> settings = fullRenderer.getSettings(clusterNode, stack);
        Assert.assertEquals(Lists.newArrayList(ImmutableMap.of(SERVICE_SETTINGS, ImmutableSet.of()), ImmutableMap.of(COMPONENT_SETTINGS, ImmutableSet.of(ImmutableMap.of("name", "component1", RECOVERY_ENABLED, FALSE)))), settings);
        Assert.assertEquals(Lists.newArrayList(ImmutableMap.of(COMPONENT_SETTINGS, ImmutableSet.of(ImmutableMap.of("name", "component1", RECOVERY_ENABLED, FALSE)))), minimalRenderer.getSettings(clusterNode, stack));
    }

    @Test
    public void testFinalizeProperties__instance_noComponentNode() {
        QueryInfo rootQuery = new QueryInfo(new ClusterResourceDefinition(), new HashSet());
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Cluster");
        rootQuery.getProperties().add("foo/bar");
        rootQuery.getProperties().add("prop1");
        TreeNode<Set<String>> propertyTree = fullRenderer.finalizeProperties(queryTree, false);
        Set<String> rootProperties = propertyTree.getObject();
        Assert.assertEquals(2, rootProperties.size());
        Assert.assertNotNull(propertyTree.getChild("Host"));
        Assert.assertTrue(propertyTree.getChild("Host").getObject().isEmpty());
        Assert.assertNotNull(propertyTree.getChild("Host/HostComponent"));
        Assert.assertEquals(1, propertyTree.getChild("Host/HostComponent").getObject().size());
        Assert.assertTrue(propertyTree.getChild("Host/HostComponent").getObject().contains("HostRoles/component_name"));
    }

    @Test
    public void testFinalizeResult_kerberos() throws Exception {
        setupMocksForKerberosEnabledCluster();
        Result result = new ResultImpl(true);
        createClusterResultTree(result.getResultTree());
        ClusterBlueprintRenderer renderer = new ClusterBlueprintRendererTest.TestBlueprintRenderer(ClusterBlueprintRendererTest.topology, BlueprintExportType.FULL);
        Result blueprintResult = renderer.finalizeResult(result);
        TreeNode<Resource> blueprintTree = blueprintResult.getResultTree();
        Assert.assertNull(blueprintTree.getStringProperty("isCollection"));
        Assert.assertEquals(1, blueprintTree.getChildren().size());
        TreeNode<Resource> blueprintNode = blueprintTree.getChildren().iterator().next();
        Assert.assertEquals(0, blueprintNode.getChildren().size());
        Resource blueprintResource = blueprintNode.getObject();
        Map<String, Map<String, Object>> properties = blueprintResource.getPropertiesMap();
        Assert.assertEquals("HDP", properties.get("Blueprints").get("stack_name"));
        Assert.assertEquals("1.3.3", properties.get("Blueprints").get("stack_version"));
        Map<String, Object> securityProperties = ((Map<String, Object>) (properties.get("Blueprints").get("security")));
        Assert.assertEquals("KERBEROS", securityProperties.get("type"));
        Assert.assertNotNull(((Map<String, Object>) (securityProperties.get("kerberos_descriptor"))).get("properties"));
    }

    @Test
    public void testFinalizeResult() throws Exception {
        Result result = new ResultImpl(true);
        createClusterResultTree(result.getResultTree());
        ClusterBlueprintRenderer renderer = new ClusterBlueprintRendererTest.TestBlueprintRenderer(ClusterBlueprintRendererTest.topology, BlueprintExportType.FULL);
        Result blueprintResult = renderer.finalizeResult(result);
        TreeNode<Resource> blueprintTree = blueprintResult.getResultTree();
        Assert.assertNull(blueprintTree.getStringProperty("isCollection"));
        Assert.assertEquals(1, blueprintTree.getChildren().size());
        TreeNode<Resource> blueprintNode = blueprintTree.getChildren().iterator().next();
        Assert.assertEquals(0, blueprintNode.getChildren().size());
        Resource blueprintResource = blueprintNode.getObject();
        Map<String, Map<String, Object>> properties = blueprintResource.getPropertiesMap();
        Assert.assertEquals("HDP", properties.get("Blueprints").get("stack_name"));
        Assert.assertEquals("1.3.3", properties.get("Blueprints").get("stack_version"));
        Collection<Map<String, Object>> host_groups = ((Collection<Map<String, Object>>) (properties.get("").get("host_groups")));
        Assert.assertEquals(2, host_groups.size());
        for (Map<String, Object> hostGroupProperties : host_groups) {
            String host_group_name = ((String) (hostGroupProperties.get("name")));
            if (host_group_name.equals("host_group_1")) {
                Assert.assertEquals("1", hostGroupProperties.get("cardinality"));
                Collection<Map<String, String>> components = ((Collection<Map<String, String>>) (hostGroupProperties.get("components")));
                // 4 specified components and ambari server
                Assert.assertEquals(5, components.size());
                Set<String> expectedValues = ImmutableSet.of("JOBTRACKER", "TASKTRACKER", "NAMENODE", "DATANODE", "AMBARI_SERVER");
                Set<String> actualValues = new HashSet<>();
                for (Map<String, String> componentProperties : components) {
                    Assert.assertEquals(1, componentProperties.size());
                    actualValues.add(componentProperties.get("name"));
                }
                Assert.assertEquals(expectedValues, actualValues);
            } else
                if (host_group_name.equals("host_group_2")) {
                    // cardinality is 2 because 2 hosts share same topology
                    Assert.assertEquals("2", hostGroupProperties.get("cardinality"));
                    Collection<Map<String, String>> components = ((Collection<Map<String, String>>) (hostGroupProperties.get("components")));
                    Assert.assertEquals(2, components.size());
                    Set<String> expectedValues = ImmutableSet.of("TASKTRACKER", "DATANODE");
                    Set<String> actualValues = new HashSet<>();
                    for (Map<String, String> componentProperties : components) {
                        Assert.assertEquals(1, componentProperties.size());
                        actualValues.add(componentProperties.get("name"));
                    }
                    Assert.assertEquals(expectedValues, actualValues);
                }

        }
    }

    @Test
    public void testFinalizeResultWithAttributes() throws Exception {
        ServiceInfo hdfsService = new ServiceInfo();
        hdfsService.setName("HDFS");
        ServiceInfo mrService = new ServiceInfo();
        mrService.setName("MAPREDUCE");
        Result result = new ResultImpl(true);
        Map<String, Object> testDesiredConfigMap = new HashMap<>();
        DesiredConfig testDesiredConfig = new DesiredConfig();
        testDesiredConfig.setTag("test-tag-one");
        testDesiredConfigMap.put("test-type-one", testDesiredConfig);
        createClusterResultTree(result.getResultTree(), testDesiredConfigMap);
        ClusterBlueprintRenderer renderer = new ClusterBlueprintRendererTest.TestBlueprintRenderer(ClusterBlueprintRendererTest.topology, BlueprintExportType.FULL);
        Result blueprintResult = renderer.finalizeResult(result);
        TreeNode<Resource> blueprintTree = blueprintResult.getResultTree();
        Assert.assertNull(blueprintTree.getStringProperty("isCollection"));
        Assert.assertEquals(1, blueprintTree.getChildren().size());
        TreeNode<Resource> blueprintNode = blueprintTree.getChildren().iterator().next();
        Assert.assertEquals(0, blueprintNode.getChildren().size());
        Resource blueprintResource = blueprintNode.getObject();
        Map<String, Map<String, Object>> properties = blueprintResource.getPropertiesMap();
        Assert.assertEquals("HDP", properties.get("Blueprints").get("stack_name"));
        Assert.assertEquals("1.3.3", properties.get("Blueprints").get("stack_version"));
        Collection<Map<String, Object>> host_groups = ((Collection<Map<String, Object>>) (properties.get("").get("host_groups")));
        Assert.assertEquals(2, host_groups.size());
        for (Map<String, Object> hostGroupProperties : host_groups) {
            String host_group_name = ((String) (hostGroupProperties.get("name")));
            if (host_group_name.equals("host_group_1")) {
                Assert.assertEquals("1", hostGroupProperties.get("cardinality"));
                Collection<Map<String, String>> components = ((Collection<Map<String, String>>) (hostGroupProperties.get("components")));
                // 4 specified components and ambari server
                Assert.assertEquals(5, components.size());
                Set<String> expectedValues = ImmutableSet.of("JOBTRACKER", "TASKTRACKER", "NAMENODE", "DATANODE", "AMBARI_SERVER");
                Set<String> actualValues = new HashSet<>();
                for (Map<String, String> componentProperties : components) {
                    Assert.assertEquals(1, componentProperties.size());
                    actualValues.add(componentProperties.get("name"));
                }
                Assert.assertEquals(expectedValues, actualValues);
            } else
                if (host_group_name.equals("host_group_2")) {
                    // cardinality is 2 because 2 hosts share same topology
                    Assert.assertEquals("2", hostGroupProperties.get("cardinality"));
                    Collection<Map<String, String>> components = ((Collection<Map<String, String>>) (hostGroupProperties.get("components")));
                    Assert.assertEquals(2, components.size());
                    Set<String> expectedValues = ImmutableSet.of("TASKTRACKER", "DATANODE");
                    Set<String> actualValues = new HashSet<>();
                    for (Map<String, String> componentProperties : components) {
                        Assert.assertEquals(1, componentProperties.size());
                        actualValues.add(componentProperties.get("name"));
                    }
                    Assert.assertEquals(expectedValues, actualValues);
                }

        }
        List<Map<String, Map<String, Map<String, ?>>>> configurationsResult = ((List<Map<String, Map<String, Map<String, ?>>>>) (blueprintResource.getPropertyValue("configurations")));
        Assert.assertEquals("Incorrect number of config maps added", 1, configurationsResult.size());
        Map<String, Map<String, ?>> configMap = configurationsResult.iterator().next().get("test-type-one");
        Assert.assertNotNull("Expected config map was not included", configMap);
        Assert.assertEquals("Incorrect number of maps added under expected type", 2, configMap.size());
        Assert.assertTrue("Expected properties map was not found", configMap.containsKey("properties"));
        Assert.assertTrue("Expected properties_attributes map was not found", configMap.containsKey("properties_attributes"));
        Map<String, ?> propertiesResult = configMap.get("properties");
        Assert.assertEquals("Incorrect number of config properties found", 1, propertiesResult.size());
        Map<String, ?> attributesResult = configMap.get("properties_attributes");
        Assert.assertEquals("Incorrect number of config attributes found", 1, attributesResult.size());
        // verify the correct properties were added to the exported Blueprint
        Assert.assertEquals("Incorrect property value included", "valueOne", propertiesResult.get("propertyOne"));
        // verify that the expected attributes were added to the exported Blueprint
        Assert.assertNotNull("Expected attribute not found in exported Blueprint", attributesResult.get("final"));
        Assert.assertTrue("Attribute type map was not included", ((attributesResult.get("final")) instanceof Map));
        Map<String, ?> finalMap = ((Map<String, ?>) (attributesResult.get("final")));
        Assert.assertEquals("Attribute value is not correct", "true", finalMap.get("propertyOne"));
    }

    @Test
    public void testClusterRendererDefaults() {
        Assert.assertFalse("ClusterBlueprintRenderer should not require property provider input", fullRenderer.requiresPropertyProviderInput());
    }

    private static class TestBlueprintRenderer extends ClusterBlueprintRenderer {
        private ClusterTopology topology;

        TestBlueprintRenderer(ClusterTopology topology, BlueprintExportType exportType) {
            super(exportType);
            this.topology = topology;
        }

        @Override
        protected ClusterTopology createClusterTopology(TreeNode<Resource> clusterNode) {
            return topology;
        }
    }
}

