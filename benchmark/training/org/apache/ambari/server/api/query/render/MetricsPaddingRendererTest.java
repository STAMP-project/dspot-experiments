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


import Resource.Type.Cluster;
import Resource.Type.Service;
import java.util.HashSet;
import java.util.Set;
import org.apache.ambari.server.api.query.QueryInfo;
import org.apache.ambari.server.api.resources.ServiceResourceDefinition;
import org.apache.ambari.server.api.util.TreeNode;
import org.apache.ambari.server.controller.spi.Schema;
import org.apache.ambari.server.controller.spi.SchemaFactory;
import org.junit.Assert;
import org.junit.Test;


public class MetricsPaddingRendererTest {
    @Test
    public void testFinalizeProperties__NullPadding_property() {
        SchemaFactory schemaFactory = createNiceMock(SchemaFactory.class);
        Schema schema = createNiceMock(Schema.class);
        // schema expectations
        expect(schemaFactory.getSchema(Service)).andReturn(schema).anyTimes();
        expect(schema.getKeyPropertyId(Service)).andReturn("ServiceInfo/service_name").anyTimes();
        expect(schema.getKeyPropertyId(Cluster)).andReturn("ServiceInfo/cluster_name").anyTimes();
        replay(schemaFactory, schema);
        HashSet<String> serviceProperties = new HashSet<>();
        serviceProperties.add("foo/bar");
        QueryInfo rootQuery = new QueryInfo(new ServiceResourceDefinition(), serviceProperties);
        TreeNode<QueryInfo> queryTree = new org.apache.ambari.server.api.util.TreeNodeImpl(null, rootQuery, "Service");
        MetricsPaddingRenderer renderer = new MetricsPaddingRenderer("null_padding");
        renderer.init(schemaFactory);
        TreeNode<Set<String>> propertyTree = renderer.finalizeProperties(queryTree, false);
        Assert.assertEquals(4, propertyTree.getObject().size());
        Assert.assertTrue(propertyTree.getObject().contains("ServiceInfo/service_name"));
        Assert.assertTrue(propertyTree.getObject().contains("ServiceInfo/cluster_name"));
        Assert.assertTrue(propertyTree.getObject().contains("foo/bar"));
        Assert.assertTrue(propertyTree.getObject().contains("params/padding/NULLS"));
        Assert.assertEquals(0, propertyTree.getChildren().size());
        verify(schemaFactory, schema);
    }
}

