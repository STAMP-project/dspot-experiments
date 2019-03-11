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
package org.apache.hadoop.yarn.server.resourcemanager.nodelabels;


import RMDelegatedNodeLabelsUpdater.DISABLE_DELEGATED_NODE_LABELS_UPDATE;
import YarnConfiguration.RM_NODE_LABELS_PROVIDER_CONFIG;
import YarnConfiguration.RM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.nodelabels.NodeLabelTestBase;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.junit.Assert;
import org.junit.Test;


public class TestRMDelegatedNodeLabelsUpdater extends NodeLabelTestBase {
    private YarnConfiguration conf;

    private static Map<NodeId, Set<NodeLabel>> nodeLabelsMap = Maps.newHashMap();

    @Test
    public void testRMNodeLabelsMappingProviderConfiguration() {
        conf.unset(RM_NODE_LABELS_PROVIDER_CONFIG);
        try {
            MockRM rm = new MockRM(conf);
            rm.init(conf);
            start();
            Assert.fail("Expected an exception");
        } catch (Exception e) {
            // expected an exception
            Assert.assertTrue(e.getMessage().contains("RMNodeLabelsMappingProvider should be configured"));
        }
    }

    @Test
    public void testWithNodeLabelUpdateEnabled() throws Exception {
        conf.setLong(RM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS, 1000);
        MockRM rm = new MockRM(conf);
        rm.init(conf);
        getRMContext().getRMDelegatedNodeLabelsUpdater().nodeLabelsUpdateInterval = 3 * 1000;
        start();
        RMNodeLabelsManager mgr = getRMContext().getNodeLabelManager();
        mgr.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y"));
        NodeId nodeId = toNodeId("h1:1234");
        Assert.assertEquals(0, mgr.getLabelsOnNode(nodeId).size());
        updateNodeLabels(nodeId, "x");
        registerNode(rm, nodeId);
        Thread.sleep(4000);
        assertCollectionEquals(ImmutableSet.of("x"), mgr.getLabelsOnNode(nodeId));
        // Ensure that node labels are updated if NodeLabelsProvider
        // gives different labels
        updateNodeLabels(nodeId, "y");
        Thread.sleep(4000);
        assertCollectionEquals(ImmutableSet.of("y"), mgr.getLabelsOnNode(nodeId));
        stop();
    }

    @Test
    public void testWithNodeLabelUpdateDisabled() throws Exception {
        conf.setLong(RM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS, DISABLE_DELEGATED_NODE_LABELS_UPDATE);
        MockRM rm = new MockRM(conf);
        rm.init(conf);
        getRMContext().getRMDelegatedNodeLabelsUpdater().nodeLabelsUpdateInterval = 3 * 1000;
        start();
        RMNodeLabelsManager mgr = getRMContext().getNodeLabelManager();
        mgr.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x"));
        NodeId nodeId = toNodeId("h1:1234");
        updateNodeLabels(nodeId, "x");
        registerNode(rm, nodeId);
        Thread.sleep(4000);
        // Ensure that even though timer is not run, node labels are fetched
        // when node is registered
        assertCollectionEquals(ImmutableSet.of("x"), mgr.getLabelsOnNode(nodeId));
        stop();
    }

    public static class DummyRMNodeLabelsMappingProvider extends RMNodeLabelsMappingProvider {
        public DummyRMNodeLabelsMappingProvider() {
            super("DummyRMNodeLabelsMappingProvider");
        }

        @Override
        public Map<NodeId, Set<NodeLabel>> getNodeLabels(Set<NodeId> nodes) {
            Map<NodeId, Set<NodeLabel>> nodeLabels = Maps.newHashMap();
            for (NodeId node : nodes) {
                nodeLabels.put(node, TestRMDelegatedNodeLabelsUpdater.nodeLabelsMap.get(node));
            }
            return nodeLabels;
        }
    }
}

