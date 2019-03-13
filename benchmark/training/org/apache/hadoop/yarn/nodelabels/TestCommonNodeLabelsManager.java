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
package org.apache.hadoop.yarn.nodelabels;


import CommonNodeLabelsManager.EMPTY_STRING_SET;
import CommonNodeLabelsManager.NO_LABEL;
import YarnConfiguration.DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE;
import YarnConfiguration.NODELABEL_CONFIGURATION_TYPE;
import YarnConfiguration.NODE_LABELS_ENABLED;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class TestCommonNodeLabelsManager extends NodeLabelTestBase {
    DummyCommonNodeLabelsManager mgr = null;

    @Test(timeout = 5000)
    public void testAddRemovelabel() throws Exception {
        // Add some label
        addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("hello"));
        verifyNodeLabelAdded(Sets.newHashSet("hello"), mgr.lastAddedlabels);
        addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("world"));
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("hello1", "world1"));
        verifyNodeLabelAdded(Sets.newHashSet("hello1", "world1"), mgr.lastAddedlabels);
        Assert.assertTrue(getClusterNodeLabelNames().containsAll(Sets.newHashSet("hello", "world", "hello1", "world1")));
        try {
            mgr.addToCluserNodeLabels(Arrays.asList(NodeLabel.newInstance("hello1", false)));
            Assert.fail("IOException not thrown on exclusivity change of labels");
        } catch (Exception e) {
            Assert.assertTrue("IOException is expected when exclusivity is modified", (e instanceof IOException));
        }
        try {
            mgr.addToCluserNodeLabels(Arrays.asList(NodeLabel.newInstance("hello1", true)));
        } catch (Exception e) {
            Assert.assertFalse("IOException not expected when no change in exclusivity", (e instanceof IOException));
        }
        // try to remove null, empty and non-existed label, should fail
        for (String p : Arrays.asList(null, NO_LABEL, "xx")) {
            boolean caught = false;
            try {
                removeFromClusterNodeLabels(Arrays.asList(p));
            } catch (IOException e) {
                caught = true;
            }
            Assert.assertTrue(("remove label should fail " + "when label is null/empty/non-existed"), caught);
        }
        // Remove some label
        removeFromClusterNodeLabels(Arrays.asList("hello"));
        NodeLabelTestBase.assertCollectionEquals(Sets.newHashSet("hello"), mgr.lastRemovedlabels);
        Assert.assertTrue(getClusterNodeLabelNames().containsAll(Arrays.asList("world", "hello1", "world1")));
        removeFromClusterNodeLabels(Arrays.asList("hello1", "world1", "world"));
        Assert.assertTrue(mgr.lastRemovedlabels.containsAll(Sets.newHashSet("hello1", "world1", "world")));
        Assert.assertTrue(getClusterNodeLabelNames().isEmpty());
    }

    @Test(timeout = 5000)
    public void testAddlabelWithCase() throws Exception {
        // Add some label, case will not ignore here
        addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("HeLlO"));
        verifyNodeLabelAdded(Sets.newHashSet("HeLlO"), mgr.lastAddedlabels);
        Assert.assertFalse(getClusterNodeLabelNames().containsAll(Arrays.asList("hello")));
    }

    @Test(timeout = 5000)
    public void testAddlabelWithExclusivity() throws Exception {
        // Add some label, case will not ignore here
        mgr.addToCluserNodeLabels(Arrays.asList(NodeLabel.newInstance("a", false), NodeLabel.newInstance("b", true)));
        Assert.assertFalse(isExclusiveNodeLabel("a"));
        Assert.assertTrue(isExclusiveNodeLabel("b"));
    }

    @Test(timeout = 5000)
    public void testAddInvalidlabel() throws IOException {
        boolean caught = false;
        try {
            Set<String> set = new HashSet<String>();
            set.add(null);
            mgr.addToCluserNodeLabelsWithDefaultExclusivity(set);
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue("null label should not add to repo", caught);
        caught = false;
        try {
            mgr.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of(NO_LABEL));
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue("empty label should not add to repo", caught);
        caught = false;
        try {
            addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("-?"));
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue("invalid label character should not add to repo", caught);
        caught = false;
        try {
            addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of(StringUtils.repeat("c", 257)));
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue("too long label should not add to repo", caught);
        caught = false;
        try {
            addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("-aaabbb"));
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue("label cannot start with \"-\"", caught);
        caught = false;
        try {
            addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("_aaabbb"));
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue("label cannot start with \"_\"", caught);
        caught = false;
        try {
            addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("a^aabbb"));
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue("label cannot contains other chars like ^[] ...", caught);
        caught = false;
        try {
            addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("aa[a]bbb"));
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue("label cannot contains other chars like ^[] ...", caught);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(timeout = 5000)
    public void testAddReplaceRemoveLabelsOnNodes() throws Exception {
        // set a label on a node, but label doesn't exist
        boolean caught = false;
        try {
            mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("node"), NodeLabelTestBase.toSet("label")));
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue(("trying to set a label to a node but " + "label doesn't exist in repository should fail"), caught);
        // set a label on a node, but node is null or empty
        try {
            mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId(NO_LABEL), NodeLabelTestBase.toSet("label")));
        } catch (IOException e) {
            caught = true;
        }
        Assert.assertTrue("trying to add a empty node but succeeded", caught);
        // set node->label one by one
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p2")));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p3")));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p2"), toNodeId("n2"), NodeLabelTestBase.toSet("p3")));
        NodeLabelTestBase.assertMapEquals(mgr.lastNodeToLabels, ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p3")));
        // set bunch of node->label
        replaceLabelsOnNode(((Map) (ImmutableMap.of(toNodeId("n3"), NodeLabelTestBase.toSet("p3"), toNodeId("n1"), NodeLabelTestBase.toSet("p1")))));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1"), toNodeId("n2"), NodeLabelTestBase.toSet("p3"), toNodeId("n3"), NodeLabelTestBase.toSet("p3")));
        NodeLabelTestBase.assertMapEquals(mgr.lastNodeToLabels, ImmutableMap.of(toNodeId("n3"), NodeLabelTestBase.toSet("p3"), toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        /* n1: p1 
        n2: p3 
        n3: p3
         */
        // remove label on node
        mgr.removeLabelsFromNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p3"), toNodeId("n3"), NodeLabelTestBase.toSet("p3")));
        NodeLabelTestBase.assertMapEquals(mgr.lastNodeToLabels, ImmutableMap.of(toNodeId("n1"), EMPTY_STRING_SET));
        // add label on node
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1"), toNodeId("n2"), NodeLabelTestBase.toSet("p3"), toNodeId("n3"), NodeLabelTestBase.toSet("p3")));
        NodeLabelTestBase.assertMapEquals(mgr.lastNodeToLabels, ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        // remove labels on node
        mgr.removeLabelsFromNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1"), toNodeId("n2"), NodeLabelTestBase.toSet("p3"), toNodeId("n3"), NodeLabelTestBase.toSet("p3")));
        Assert.assertEquals(0, getNodeLabels().size());
        NodeLabelTestBase.assertMapEquals(mgr.lastNodeToLabels, ImmutableMap.of(toNodeId("n1"), EMPTY_STRING_SET, toNodeId("n2"), EMPTY_STRING_SET, toNodeId("n3"), EMPTY_STRING_SET));
    }

    @Test(timeout = 5000)
    public void testRemovelabelWithNodes() throws Exception {
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p2")));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n3"), NodeLabelTestBase.toSet("p3")));
        removeFromClusterNodeLabels(ImmutableSet.of("p1"));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p2"), toNodeId("n3"), NodeLabelTestBase.toSet("p3")));
        NodeLabelTestBase.assertCollectionEquals(Arrays.asList("p1"), mgr.lastRemovedlabels);
        removeFromClusterNodeLabels(ImmutableSet.of("p2", "p3"));
        Assert.assertTrue(getNodeLabels().isEmpty());
        Assert.assertTrue(getClusterNodeLabelNames().isEmpty());
        NodeLabelTestBase.assertCollectionEquals(Arrays.asList("p2", "p3"), mgr.lastRemovedlabels);
    }

    @Test(timeout = 5000)
    public void testTrimLabelsWhenAddRemoveNodeLabels() throws IOException {
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet(" p1"));
        NodeLabelTestBase.assertCollectionEquals(NodeLabelTestBase.toSet("p1"), getClusterNodeLabelNames());
        removeFromClusterNodeLabels(NodeLabelTestBase.toSet("p1 "));
        Assert.assertTrue(getClusterNodeLabelNames().isEmpty());
    }

    @Test(timeout = 5000)
    public void testTrimLabelsWhenModifyLabelsOnNodes() throws IOException {
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet(" p1", "p2"));
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1 ")));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet(" p2")));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p2")));
        mgr.removeLabelsFromNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("  p2 ")));
        Assert.assertTrue(getNodeLabels().isEmpty());
    }

    @Test(timeout = 5000)
    public void testReplaceLabelsOnHostsShouldUpdateNodesBelongTo() throws IOException {
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        // Replace labels on n1:1 to P2
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1:1"), NodeLabelTestBase.toSet("p2"), toNodeId("n1:2"), NodeLabelTestBase.toSet("p2")));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1"), toNodeId("n1:1"), NodeLabelTestBase.toSet("p2"), toNodeId("n1:2"), NodeLabelTestBase.toSet("p2")));
        // Replace labels on n1 to P1, both n1:1/n1 will be P1 now
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        NodeLabelTestBase.assertMapEquals(getNodeLabels(), ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1"), toNodeId("n1:1"), NodeLabelTestBase.toSet("p1"), toNodeId("n1:2"), NodeLabelTestBase.toSet("p1")));
        // Set labels on n1:1 to P2 again to verify if add/remove works
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1:1"), NodeLabelTestBase.toSet("p2")));
    }

    @Test(timeout = 5000)
    public void testNodeLabelsDisabled() throws IOException {
        DummyCommonNodeLabelsManager mgr = new DummyCommonNodeLabelsManager();
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(NODE_LABELS_ENABLED, false);
        mgr.init(conf);
        start();
        boolean caught = false;
        // add labels
        try {
            addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x"));
        } catch (IOException e) {
            assertNodeLabelsDisabledErrorMessage(e);
            caught = true;
        }
        // check exception caught
        Assert.assertTrue(caught);
        caught = false;
        // remove labels
        try {
            removeFromClusterNodeLabels(ImmutableSet.of("x"));
        } catch (IOException e) {
            assertNodeLabelsDisabledErrorMessage(e);
            caught = true;
        }
        // check exception caught
        Assert.assertTrue(caught);
        caught = false;
        // add labels to node
        try {
            mgr.addLabelsToNode(ImmutableMap.of(NodeId.newInstance("host", 0), EMPTY_STRING_SET));
        } catch (IOException e) {
            assertNodeLabelsDisabledErrorMessage(e);
            caught = true;
        }
        // check exception caught
        Assert.assertTrue(caught);
        caught = false;
        // remove labels from node
        try {
            mgr.removeLabelsFromNode(ImmutableMap.of(NodeId.newInstance("host", 0), EMPTY_STRING_SET));
        } catch (IOException e) {
            assertNodeLabelsDisabledErrorMessage(e);
            caught = true;
        }
        // check exception caught
        Assert.assertTrue(caught);
        caught = false;
        // replace labels on node
        try {
            mgr.replaceLabelsOnNode(ImmutableMap.of(NodeId.newInstance("host", 0), EMPTY_STRING_SET));
        } catch (IOException e) {
            assertNodeLabelsDisabledErrorMessage(e);
            caught = true;
        }
        // check exception caught
        Assert.assertTrue(caught);
        caught = false;
        close();
    }

    @Test(timeout = 5000)
    public void testLabelsToNodes() throws IOException {
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        Map<String, Set<NodeId>> labelsToNodes = mgr.getLabelsToNodes();
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, ImmutableMap.of("p1", NodeLabelTestBase.toSet(toNodeId("n1"))));
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, NodeLabelTestBase.transposeNodeToLabels(getNodeLabels()));
        // Replace labels on n1:1 to P2
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1:1"), NodeLabelTestBase.toSet("p2"), toNodeId("n1:2"), NodeLabelTestBase.toSet("p2")));
        labelsToNodes = mgr.getLabelsToNodes();
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, ImmutableMap.of("p1", NodeLabelTestBase.toSet(toNodeId("n1")), "p2", NodeLabelTestBase.toSet(toNodeId("n1:1"), toNodeId("n1:2"))));
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, NodeLabelTestBase.transposeNodeToLabels(getNodeLabels()));
        // Replace labels on n1 to P1, both n1:1/n1 will be P1 now
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        labelsToNodes = mgr.getLabelsToNodes();
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, ImmutableMap.of("p1", NodeLabelTestBase.toSet(toNodeId("n1"), toNodeId("n1:1"), toNodeId("n1:2"))));
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, NodeLabelTestBase.transposeNodeToLabels(getNodeLabels()));
        // Set labels on n1:1 to P2 again to verify if add/remove works
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1:1"), NodeLabelTestBase.toSet("p2")));
        // Add p3 to n1, should makes n1:1 to be p2/p3, and n1:2 to be p1/p3
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p3")));
        labelsToNodes = mgr.getLabelsToNodes();
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, ImmutableMap.of("p1", NodeLabelTestBase.toSet(toNodeId("n1"), toNodeId("n1:2")), "p2", NodeLabelTestBase.toSet(toNodeId("n1:1")), "p3", NodeLabelTestBase.toSet(toNodeId("n2"))));
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, NodeLabelTestBase.transposeNodeToLabels(getNodeLabels()));
        // Remove P3 from n1, should makes n1:1 to be p2, and n1:2 to be p1
        mgr.removeLabelsFromNode(ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p3")));
        labelsToNodes = mgr.getLabelsToNodes();
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, ImmutableMap.of("p1", NodeLabelTestBase.toSet(toNodeId("n1"), toNodeId("n1:2")), "p2", NodeLabelTestBase.toSet(toNodeId("n1:1"))));
        NodeLabelTestBase.assertLabelsToNodesEquals(labelsToNodes, NodeLabelTestBase.transposeNodeToLabels(getNodeLabels()));
    }

    @Test(timeout = 5000)
    public void testLabelsToNodesForSelectedLabels() throws IOException {
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1:1"), NodeLabelTestBase.toSet("p1"), toNodeId("n1:2"), NodeLabelTestBase.toSet("p2")));
        Set<String> setlabels = new HashSet<String>(Arrays.asList(new String[]{ "p1" }));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(setlabels), ImmutableMap.of("p1", NodeLabelTestBase.toSet(toNodeId("n1:1"))));
        // Replace labels on n1:1 to P3
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p3")));
        Assert.assertTrue(getLabelsToNodes(setlabels).isEmpty());
        setlabels = new HashSet<String>(Arrays.asList(new String[]{ "p2", "p3" }));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(setlabels), ImmutableMap.of("p3", NodeLabelTestBase.toSet(toNodeId("n1"), toNodeId("n1:1"), toNodeId("n1:2"))));
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p2")));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(setlabels), ImmutableMap.of("p2", NodeLabelTestBase.toSet(toNodeId("n2")), "p3", NodeLabelTestBase.toSet(toNodeId("n1"), toNodeId("n1:1"), toNodeId("n1:2"))));
        mgr.removeLabelsFromNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p3")));
        setlabels = new HashSet<String>(Arrays.asList(new String[]{ "p1", "p2", "p3" }));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(setlabels), ImmutableMap.of("p2", NodeLabelTestBase.toSet(toNodeId("n2"))));
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n3"), NodeLabelTestBase.toSet("p1")));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(setlabels), ImmutableMap.of("p1", NodeLabelTestBase.toSet(toNodeId("n3")), "p2", NodeLabelTestBase.toSet(toNodeId("n2"))));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n2:2"), NodeLabelTestBase.toSet("p3")));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(setlabels), ImmutableMap.of("p1", NodeLabelTestBase.toSet(toNodeId("n3")), "p2", NodeLabelTestBase.toSet(toNodeId("n2")), "p3", NodeLabelTestBase.toSet(toNodeId("n2:2"))));
        setlabels = new HashSet<String>(Arrays.asList(new String[]{ "p1" }));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(setlabels), ImmutableMap.of("p1", NodeLabelTestBase.toSet(toNodeId("n3"))));
    }

    @Test(timeout = 5000)
    public void testNoMoreThanOneLabelExistedInOneHost() throws IOException {
        boolean failed = false;
        // As in YARN-2694, we temporarily disable no more than one label existed in
        // one host
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        try {
            mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1", "p2")));
        } catch (IOException e) {
            failed = true;
        }
        Assert.assertTrue("Should failed when set > 1 labels on a host", failed);
        try {
            mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1", "p2")));
        } catch (IOException e) {
            failed = true;
        }
        Assert.assertTrue("Should failed when add > 1 labels on a host", failed);
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        // add a same label to a node, #labels in this node is still 1, shouldn't
        // fail
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        try {
            mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p2")));
        } catch (IOException e) {
            failed = true;
        }
        Assert.assertTrue("Should failed when #labels > 1 on a host after add", failed);
    }

    @Test(timeout = 5000)
    public void testReplaceLabelsOnNodeInDistributedMode() throws Exception {
        // create new DummyCommonNodeLabelsManager than the one got from @before
        stop();
        mgr = new DummyCommonNodeLabelsManager();
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(NODE_LABELS_ENABLED, true);
        conf.set(NODELABEL_CONFIGURATION_TYPE, DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE);
        mgr.init(conf);
        start();
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        Set<String> labelsByNode = mgr.getLabelsByNode(toNodeId("n1"));
        Assert.assertNull("Labels are not expected to be written to the NodeLabelStore", mgr.lastNodeToLabels);
        Assert.assertNotNull("Updated labels should be available from the Mgr", labelsByNode);
        Assert.assertTrue(labelsByNode.contains("p1"));
    }

    @Test(timeout = 5000)
    public void testLabelsInfoToNodes() throws IOException {
        mgr.addToCluserNodeLabels(Arrays.asList(NodeLabel.newInstance("p1", false), NodeLabel.newInstance("p2", true), NodeLabel.newInstance("p3", true)));
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1")));
        Map<NodeLabel, Set<NodeId>> labelsToNodes = getLabelsInfoToNodes();
        NodeLabelTestBase.assertLabelsInfoToNodesEquals(labelsToNodes, ImmutableMap.of(NodeLabel.newInstance("p1", false), NodeLabelTestBase.toSet(toNodeId("n1"))));
    }

    @Test(timeout = 5000)
    public void testGetNodeLabelsInfo() throws IOException {
        mgr.addToCluserNodeLabels(Arrays.asList(NodeLabel.newInstance("p1", false), NodeLabel.newInstance("p2", true), NodeLabel.newInstance("p3", false)));
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p2")));
        mgr.addLabelsToNode(ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p3")));
        NodeLabelTestBase.assertLabelInfoMapEquals(getNodeLabelsInfo(), ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet(NodeLabel.newInstance("p2", true)), toNodeId("n2"), NodeLabelTestBase.toSet(NodeLabel.newInstance("p3", false))));
    }
}

