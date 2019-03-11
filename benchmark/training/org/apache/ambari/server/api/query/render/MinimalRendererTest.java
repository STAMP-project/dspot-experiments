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


import Resource.Type.Alert;
import Resource.Type.Artifact;
import Resource.Type.Cluster;
import Resource.Type.Component;
import Resource.Type.Host;
import Resource.Type.HostComponent;
import Resource.Type.Service;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.query.QueryInfo;
import org.apache.ambari.server.api.resources.ComponentResourceDefinition;
import org.apache.ambari.server.api.resources.ServiceResourceDefinition;
import org.apache.ambari.server.api.services.Result;
import org.apache.ambari.server.api.services.ResultImpl;
import org.apache.ambari.server.api.util.TreeNode;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.Schema;
import org.apache.ambari.server.controller.spi.SchemaFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * MinimalRenderer unit tests.
 */
public class MinimalRendererTest {
    @Test
    public void testFinalizeProperties__instance_noProperties() {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema schema = createNiceMock(Schema.class);
        // schema expectations
        expect(schemaFactory.getSchema(Component)).andReturn(schema).anyTimes();
        expect(schemaFactory.getSchema(Alert)).andReturn(schema).anyTimes();
        expect(schemaFactory.getSchema(Artifact)).andReturn(schema).anyTimes();
        expect(schema.getKeyPropertyId(Component)).andReturn("ServiceComponentInfo/component_name").anyTimes();
        replay(schemaFactory, schema);
        QueryInfo rootQuery = new QueryInfo(new ServiceResourceDefinition(), new HashSet());
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Service");
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        TreeNode<Set<String>> propertyTree = renderer.finalizeProperties(queryTree, false);
        // no properties should have been added
        Assert.assertTrue(propertyTree.getObject().isEmpty());
        Assert.assertEquals(3, propertyTree.getChildren().size());
        TreeNode<Set<String>> componentNode = propertyTree.getChild("Component");
        Assert.assertEquals(1, componentNode.getObject().size());
        Assert.assertTrue(componentNode.getObject().contains("ServiceComponentInfo/component_name"));
        verify(schemaFactory, schema);
    }

    @Test
    public void testFinalizeProperties__instance_properties() {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema schema = createNiceMock(Schema.class);
        // schema expectations
        expect(schemaFactory.getSchema(Service)).andReturn(schema).anyTimes();
        expect(schema.getKeyPropertyId(Service)).andReturn("ServiceInfo/service_name").anyTimes();
        replay(schemaFactory, schema);
        HashSet<String> serviceProperties = new HashSet<>();
        serviceProperties.add("foo/bar");
        QueryInfo rootQuery = new QueryInfo(new ServiceResourceDefinition(), serviceProperties);
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Service");
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        TreeNode<Set<String>> propertyTree = renderer.finalizeProperties(queryTree, false);
        Assert.assertEquals(2, propertyTree.getObject().size());
        Assert.assertTrue(propertyTree.getObject().contains("ServiceInfo/service_name"));
        Assert.assertTrue(propertyTree.getObject().contains("foo/bar"));
        Assert.assertEquals(0, propertyTree.getChildren().size());
        verify(schemaFactory, schema);
    }

    @Test
    public void testFinalizeProperties__collection_noProperties() {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema schema = createNiceMock(Schema.class);
        // schema expectations
        expect(schemaFactory.getSchema(Service)).andReturn(schema).anyTimes();
        expect(schema.getKeyPropertyId(Service)).andReturn("ServiceInfo/service_name").anyTimes();
        replay(schemaFactory, schema);
        HashSet<String> serviceProperties = new HashSet<>();
        QueryInfo rootQuery = new QueryInfo(new ServiceResourceDefinition(), serviceProperties);
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Service");
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        TreeNode<Set<String>> propertyTree = renderer.finalizeProperties(queryTree, true);
        Assert.assertEquals(1, propertyTree.getObject().size());
        Assert.assertTrue(propertyTree.getObject().contains("ServiceInfo/service_name"));
        Assert.assertEquals(0, propertyTree.getChildren().size());
        verify(schemaFactory, schema);
    }

    @Test
    public void testFinalizeProperties__collection_properties() {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema schema = createNiceMock(Schema.class);
        // schema expectations
        expect(schemaFactory.getSchema(Service)).andReturn(schema).anyTimes();
        expect(schema.getKeyPropertyId(Service)).andReturn("ServiceInfo/service_name").anyTimes();
        replay(schemaFactory, schema);
        HashSet<String> serviceProperties = new HashSet<>();
        serviceProperties.add("foo/bar");
        QueryInfo rootQuery = new QueryInfo(new ServiceResourceDefinition(), serviceProperties);
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Service");
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        TreeNode<Set<String>> propertyTree = renderer.finalizeProperties(queryTree, true);
        Assert.assertEquals(2, propertyTree.getObject().size());
        Assert.assertTrue(propertyTree.getObject().contains("ServiceInfo/service_name"));
        Assert.assertTrue(propertyTree.getObject().contains("foo/bar"));
        Assert.assertEquals(0, propertyTree.getChildren().size());
        verify(schemaFactory, schema);
    }

    @Test
    public void testFinalizeProperties__instance_subResource_noProperties() {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema serviceSchema = createNiceMock(Schema.class);
        Schema componentSchema = createNiceMock(Schema.class);
        // schema expectations
        expect(schemaFactory.getSchema(Service)).andReturn(serviceSchema).anyTimes();
        expect(serviceSchema.getKeyPropertyId(Service)).andReturn("ServiceInfo/service_name").anyTimes();
        expect(schemaFactory.getSchema(Component)).andReturn(componentSchema).anyTimes();
        expect(componentSchema.getKeyPropertyId(Component)).andReturn("ServiceComponentInfo/component_name").anyTimes();
        replay(schemaFactory, serviceSchema, componentSchema);
        HashSet<String> serviceProperties = new HashSet<>();
        QueryInfo rootQuery = new QueryInfo(new ServiceResourceDefinition(), serviceProperties);
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Service");
        queryTree.addChild(new QueryInfo(new ComponentResourceDefinition(), new HashSet()), "Component");
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        TreeNode<Set<String>> propertyTree = renderer.finalizeProperties(queryTree, false);
        Assert.assertEquals(1, propertyTree.getChildren().size());
        Assert.assertEquals(1, propertyTree.getObject().size());
        Assert.assertTrue(propertyTree.getObject().contains("ServiceInfo/service_name"));
        TreeNode<Set<String>> componentNode = propertyTree.getChild("Component");
        Assert.assertEquals(0, componentNode.getChildren().size());
        Assert.assertEquals(1, componentNode.getObject().size());
        Assert.assertTrue(componentNode.getObject().contains("ServiceComponentInfo/component_name"));
        verify(schemaFactory, serviceSchema, componentSchema);
    }

    @Test
    public void testFinalizeProperties__instance_subResource_properties() {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema serviceSchema = createNiceMock(Schema.class);
        Schema componentSchema = createNiceMock(Schema.class);
        // schema expectations
        expect(schemaFactory.getSchema(Service)).andReturn(serviceSchema).anyTimes();
        expect(serviceSchema.getKeyPropertyId(Service)).andReturn("ServiceInfo/service_name").anyTimes();
        expect(schemaFactory.getSchema(Component)).andReturn(componentSchema).anyTimes();
        expect(componentSchema.getKeyPropertyId(Component)).andReturn("ServiceComponentInfo/component_name").anyTimes();
        replay(schemaFactory, serviceSchema, componentSchema);
        HashSet<String> serviceProperties = new HashSet<>();
        serviceProperties.add("foo/bar");
        QueryInfo rootQuery = new QueryInfo(new ServiceResourceDefinition(), serviceProperties);
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Service");
        HashSet<String> componentProperties = new HashSet<>();
        componentProperties.add("goo/car");
        queryTree.addChild(new QueryInfo(new ComponentResourceDefinition(), componentProperties), "Component");
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        TreeNode<Set<String>> propertyTree = renderer.finalizeProperties(queryTree, false);
        Assert.assertEquals(1, propertyTree.getChildren().size());
        Assert.assertEquals(2, propertyTree.getObject().size());
        Assert.assertTrue(propertyTree.getObject().contains("ServiceInfo/service_name"));
        Assert.assertTrue(propertyTree.getObject().contains("foo/bar"));
        TreeNode<Set<String>> componentNode = propertyTree.getChild("Component");
        Assert.assertEquals(0, componentNode.getChildren().size());
        Assert.assertEquals(2, componentNode.getObject().size());
        Assert.assertTrue(componentNode.getObject().contains("ServiceComponentInfo/component_name"));
        Assert.assertTrue(componentNode.getObject().contains("goo/car"));
        verify(schemaFactory, serviceSchema, componentSchema);
    }

    @Test
    public void testFinalizeProperties__collection_subResource_noProperties() {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema serviceSchema = createNiceMock(Schema.class);
        Schema componentSchema = createNiceMock(Schema.class);
        // schema expectations
        expect(schemaFactory.getSchema(Service)).andReturn(serviceSchema).anyTimes();
        expect(serviceSchema.getKeyPropertyId(Service)).andReturn("ServiceInfo/service_name").anyTimes();
        expect(schemaFactory.getSchema(Component)).andReturn(componentSchema).anyTimes();
        expect(componentSchema.getKeyPropertyId(Component)).andReturn("ServiceComponentInfo/component_name").anyTimes();
        replay(schemaFactory, serviceSchema, componentSchema);
        HashSet<String> serviceProperties = new HashSet<>();
        QueryInfo rootQuery = new QueryInfo(new ServiceResourceDefinition(), serviceProperties);
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Service");
        queryTree.addChild(new QueryInfo(new ComponentResourceDefinition(), new HashSet()), "Component");
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        TreeNode<Set<String>> propertyTree = renderer.finalizeProperties(queryTree, true);
        Assert.assertEquals(1, propertyTree.getChildren().size());
        Assert.assertEquals(1, propertyTree.getObject().size());
        Assert.assertTrue(propertyTree.getObject().contains("ServiceInfo/service_name"));
        TreeNode<Set<String>> componentNode = propertyTree.getChild("Component");
        Assert.assertEquals(0, componentNode.getChildren().size());
        Assert.assertEquals(1, componentNode.getObject().size());
        Assert.assertTrue(componentNode.getObject().contains("ServiceComponentInfo/component_name"));
        verify(schemaFactory, serviceSchema, componentSchema);
    }

    @Test
    public void testFinalizeProperties__collection_subResource_propertiesTopLevelOnly() {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema serviceSchema = createNiceMock(Schema.class);
        Schema componentSchema = createNiceMock(Schema.class);
        // schema expectations
        expect(schemaFactory.getSchema(Service)).andReturn(serviceSchema).anyTimes();
        expect(serviceSchema.getKeyPropertyId(Service)).andReturn("ServiceInfo/service_name").anyTimes();
        expect(schemaFactory.getSchema(Component)).andReturn(componentSchema).anyTimes();
        expect(componentSchema.getKeyPropertyId(Component)).andReturn("ServiceComponentInfo/component_name").anyTimes();
        replay(schemaFactory, serviceSchema, componentSchema);
        HashSet<String> serviceProperties = new HashSet<>();
        serviceProperties.add("foo/bar");
        QueryInfo rootQuery = new QueryInfo(new ServiceResourceDefinition(), serviceProperties);
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Service");
        queryTree.addChild(new QueryInfo(new ComponentResourceDefinition(), new HashSet()), "Component");
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        TreeNode<Set<String>> propertyTree = renderer.finalizeProperties(queryTree, true);
        Assert.assertEquals(1, propertyTree.getChildren().size());
        Assert.assertEquals(2, propertyTree.getObject().size());
        Assert.assertTrue(propertyTree.getObject().contains("ServiceInfo/service_name"));
        Assert.assertTrue(propertyTree.getObject().contains("foo/bar"));
        TreeNode<Set<String>> componentNode = propertyTree.getChild("Component");
        Assert.assertEquals(0, componentNode.getChildren().size());
        Assert.assertEquals(1, componentNode.getObject().size());
        Assert.assertTrue(componentNode.getObject().contains("ServiceComponentInfo/component_name"));
        verify(schemaFactory, serviceSchema, componentSchema);
    }

    @Test
    public void testFinalizeResult() throws Exception {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema clusterSchema = createNiceMock(Schema.class);
        Schema hostSchema = createNiceMock(Schema.class);
        Schema hostComponentSchema = createNiceMock(Schema.class);
        // mock expectations
        expect(schemaFactory.getSchema(Cluster)).andReturn(clusterSchema).anyTimes();
        expect(schemaFactory.getSchema(Host)).andReturn(hostSchema).anyTimes();
        expect(schemaFactory.getSchema(HostComponent)).andReturn(hostComponentSchema).anyTimes();
        expect(clusterSchema.getKeyPropertyId(Cluster)).andReturn("Clusters/cluster_name").anyTimes();
        expect(hostSchema.getKeyPropertyId(Cluster)).andReturn("Hosts/cluster_name").anyTimes();
        expect(hostSchema.getKeyPropertyId(Host)).andReturn("Hosts/host_name").anyTimes();
        expect(hostComponentSchema.getKeyPropertyId(Cluster)).andReturn("HostRoles/cluster_name").anyTimes();
        expect(hostComponentSchema.getKeyPropertyId(Host)).andReturn("HostRoles/host_name").anyTimes();
        expect(hostComponentSchema.getKeyPropertyId(HostComponent)).andReturn("HostRoles/component_name").anyTimes();
        replay(schemaFactory, clusterSchema, hostSchema, hostComponentSchema);
        Result result = new ResultImpl(true);
        createResultTree(result.getResultTree());
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        // call finalizeProperties so that renderer know which properties are requested by user
        renderer.finalizeProperties(createPropertyTree(), false);
        TreeNode<Resource> resultTree = renderer.finalizeResult(result).getResultTree();
        Assert.assertNull(resultTree.getStringProperty("isCollection"));
        Assert.assertEquals(1, resultTree.getChildren().size());
        TreeNode<Resource> clusterNode = resultTree.getChildren().iterator().next();
        Resource clusterResource = clusterNode.getObject();
        Map<String, Map<String, Object>> clusterProperties = clusterResource.getPropertiesMap();
        Assert.assertEquals(2, clusterProperties.size());
        Assert.assertEquals(3, clusterProperties.get("Clusters").size());
        Assert.assertEquals("testCluster", clusterProperties.get("Clusters").get("cluster_name"));
        Assert.assertEquals("HDP-1.3.3", clusterProperties.get("Clusters").get("version"));
        Assert.assertEquals("value1", clusterProperties.get("Clusters").get("prop1"));
        Assert.assertEquals(1, clusterProperties.get("").size());
        Assert.assertEquals("bar", clusterProperties.get("").get("foo"));
        TreeNode<Resource> hosts = clusterNode.getChildren().iterator().next();
        for (TreeNode<Resource> hostNode : hosts.getChildren()) {
            Resource hostResource = hostNode.getObject();
            Map<String, Map<String, Object>> hostProperties = hostResource.getPropertiesMap();
            Assert.assertEquals(1, hostProperties.size());
            Assert.assertEquals(1, hostProperties.get("Hosts").size());
            Assert.assertTrue(hostProperties.get("Hosts").containsKey("host_name"));
            for (TreeNode<Resource> componentNode : hostNode.getChildren().iterator().next().getChildren()) {
                Resource componentResource = componentNode.getObject();
                Map<String, Map<String, Object>> componentProperties = componentResource.getPropertiesMap();
                Assert.assertEquals(1, componentProperties.size());
                Assert.assertEquals(1, componentProperties.get("HostRoles").size());
                Assert.assertTrue(componentProperties.get("HostRoles").containsKey("component_name"));
            }
        }
    }

    @Test
    public void testFinalizeResult_propsSetOnSubResource() throws Exception {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema clusterSchema = createNiceMock(Schema.class);
        Schema hostSchema = createNiceMock(Schema.class);
        Schema hostComponentSchema = createNiceMock(Schema.class);
        // mock expectations
        expect(schemaFactory.getSchema(Cluster)).andReturn(clusterSchema).anyTimes();
        expect(schemaFactory.getSchema(Host)).andReturn(hostSchema).anyTimes();
        expect(schemaFactory.getSchema(HostComponent)).andReturn(hostComponentSchema).anyTimes();
        expect(clusterSchema.getKeyPropertyId(Cluster)).andReturn("Clusters/cluster_name").anyTimes();
        expect(hostSchema.getKeyPropertyId(Cluster)).andReturn("Hosts/cluster_name").anyTimes();
        expect(hostSchema.getKeyPropertyId(Host)).andReturn("Hosts/host_name").anyTimes();
        expect(hostComponentSchema.getKeyPropertyId(Cluster)).andReturn("HostRoles/cluster_name").anyTimes();
        expect(hostComponentSchema.getKeyPropertyId(Host)).andReturn("HostRoles/host_name").anyTimes();
        expect(hostComponentSchema.getKeyPropertyId(HostComponent)).andReturn("HostRoles/component_name").anyTimes();
        replay(schemaFactory, clusterSchema, hostSchema, hostComponentSchema);
        Result result = new ResultImpl(true);
        createResultTree(result.getResultTree());
        MinimalRenderer renderer = new MinimalRenderer();
        renderer.init(schemaFactory);
        // call finalizeProperties so that renderer know which properties are requested by user
        renderer.finalizeProperties(createPropertyTreeWithSubProps(), false);
        TreeNode<Resource> resultTree = renderer.finalizeResult(result).getResultTree();
        Assert.assertNull(resultTree.getStringProperty("isCollection"));
        Assert.assertEquals(1, resultTree.getChildren().size());
        TreeNode<Resource> clusterNode = resultTree.getChildren().iterator().next();
        Resource clusterResource = clusterNode.getObject();
        Map<String, Map<String, Object>> clusterProperties = clusterResource.getPropertiesMap();
        Assert.assertEquals(2, clusterProperties.size());
        Assert.assertEquals(3, clusterProperties.get("Clusters").size());
        Assert.assertEquals("testCluster", clusterProperties.get("Clusters").get("cluster_name"));
        Assert.assertEquals("HDP-1.3.3", clusterProperties.get("Clusters").get("version"));
        Assert.assertEquals("value1", clusterProperties.get("Clusters").get("prop1"));
        Assert.assertEquals(1, clusterProperties.get("").size());
        Assert.assertEquals("bar", clusterProperties.get("").get("foo"));
        TreeNode<Resource> hosts = clusterNode.getChildren().iterator().next();
        for (TreeNode<Resource> hostNode : hosts.getChildren()) {
            Resource hostResource = hostNode.getObject();
            Map<String, Map<String, Object>> hostProperties = hostResource.getPropertiesMap();
            Assert.assertEquals(2, hostProperties.size());
            Assert.assertEquals(1, hostProperties.get("Hosts").size());
            Assert.assertTrue(hostProperties.get("Hosts").containsKey("host_name"));
            Assert.assertEquals(1, hostProperties.get("").size());
            Assert.assertEquals("bar", hostProperties.get("").get("foo"));
            for (TreeNode<Resource> componentNode : hostNode.getChildren().iterator().next().getChildren()) {
                Resource componentResource = componentNode.getObject();
                Map<String, Map<String, Object>> componentProperties = componentResource.getPropertiesMap();
                Assert.assertEquals(1, componentProperties.size());
                Assert.assertEquals(1, componentProperties.get("HostRoles").size());
                Assert.assertTrue(componentProperties.get("HostRoles").containsKey("component_name"));
            }
        }
    }
}

