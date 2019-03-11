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


import YarnConfiguration.DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE;
import YarnConfiguration.NODELABEL_CONFIGURATION_TYPE;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.event.InlineDispatcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class TestFileSystemNodeLabelsStore extends NodeLabelTestBase {
    TestFileSystemNodeLabelsStore.MockNodeLabelManager mgr = null;

    Configuration conf = null;

    String storeClassName = null;

    private static class MockNodeLabelManager extends CommonNodeLabelsManager {
        @Override
        protected void initDispatcher(Configuration conf) {
            super.dispatcher = new InlineDispatcher();
        }

        @Override
        protected void startDispatcher() {
            // do nothing
        }

        @Override
        protected void stopDispatcher() {
            // do nothing
        }
    }

    public TestFileSystemNodeLabelsStore(String className) {
        this.storeClassName = className;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(timeout = 10000)
    public void testRecoverWithMirror() throws Exception {
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p4"));
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p5", "p6"));
        replaceLabelsOnNode(((java.util.Map) (ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1"), toNodeId("n2"), NodeLabelTestBase.toSet("p2")))));
        replaceLabelsOnNode(((java.util.Map) (ImmutableMap.of(toNodeId("n3"), NodeLabelTestBase.toSet("p3"), toNodeId("n4"), NodeLabelTestBase.toSet("p4"), toNodeId("n5"), NodeLabelTestBase.toSet("p5"), toNodeId("n6"), NodeLabelTestBase.toSet("p6"), toNodeId("n7"), NodeLabelTestBase.toSet("p6")))));
        /* node -> partition p1: n1 p2: n2 p3: n3 p4: n4 p5: n5 p6: n6, n7 */
        removeFromClusterNodeLabels(NodeLabelTestBase.toSet("p1"));
        removeFromClusterNodeLabels(Arrays.asList("p3", "p5"));
        /* After removed p2: n2 p4: n4 p6: n6, n7 */
        // shutdown mgr and start a new mgr
        stop();
        mgr = new TestFileSystemNodeLabelsStore.MockNodeLabelManager();
        mgr.init(conf);
        start();
        // check variables
        Assert.assertEquals(3, getClusterNodeLabelNames().size());
        Assert.assertTrue(getClusterNodeLabelNames().containsAll(Arrays.asList("p2", "p4", "p6")));
        NodeLabelTestBase.assertMapContains(getNodeLabels(), ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p2"), toNodeId("n4"), NodeLabelTestBase.toSet("p4"), toNodeId("n6"), NodeLabelTestBase.toSet("p6"), toNodeId("n7"), NodeLabelTestBase.toSet("p6")));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(), ImmutableMap.of("p6", NodeLabelTestBase.toSet(toNodeId("n6"), toNodeId("n7")), "p4", NodeLabelTestBase.toSet(toNodeId("n4")), "p2", NodeLabelTestBase.toSet(toNodeId("n2"))));
        // stutdown mgr and start a new mgr
        stop();
        mgr = new TestFileSystemNodeLabelsStore.MockNodeLabelManager();
        mgr.init(conf);
        start();
        // check variables
        Assert.assertEquals(3, getClusterNodeLabelNames().size());
        Assert.assertTrue(getClusterNodeLabelNames().containsAll(Arrays.asList("p2", "p4", "p6")));
        NodeLabelTestBase.assertMapContains(getNodeLabels(), ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p2"), toNodeId("n4"), NodeLabelTestBase.toSet("p4"), toNodeId("n6"), NodeLabelTestBase.toSet("p6"), toNodeId("n7"), NodeLabelTestBase.toSet("p6")));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(), ImmutableMap.of("p6", NodeLabelTestBase.toSet(toNodeId("n6"), toNodeId("n7")), "p4", NodeLabelTestBase.toSet(toNodeId("n4")), "p2", NodeLabelTestBase.toSet(toNodeId("n2"))));
        stop();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(timeout = 10000)
    public void testRecoverWithDistributedNodeLabels() throws Exception {
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p4"));
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p5", "p6"));
        replaceLabelsOnNode(((java.util.Map) (ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1"), toNodeId("n2"), NodeLabelTestBase.toSet("p2")))));
        replaceLabelsOnNode(((java.util.Map) (ImmutableMap.of(toNodeId("n3"), NodeLabelTestBase.toSet("p3"), toNodeId("n4"), NodeLabelTestBase.toSet("p4"), toNodeId("n5"), NodeLabelTestBase.toSet("p5"), toNodeId("n6"), NodeLabelTestBase.toSet("p6"), toNodeId("n7"), NodeLabelTestBase.toSet("p6")))));
        removeFromClusterNodeLabels(NodeLabelTestBase.toSet("p1"));
        removeFromClusterNodeLabels(Arrays.asList("p3", "p5"));
        stop();
        mgr = new TestFileSystemNodeLabelsStore.MockNodeLabelManager();
        Configuration cf = new Configuration(conf);
        cf.set(NODELABEL_CONFIGURATION_TYPE, DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE);
        mgr.init(cf);
        start();
        // check variables
        Assert.assertEquals(3, getClusterNodeLabels().size());
        Assert.assertTrue(getClusterNodeLabelNames().containsAll(Arrays.asList("p2", "p4", "p6")));
        Assert.assertTrue(("During recovery in distributed node-labels setup, " + "node to labels mapping should not be recovered "), ((getNodeLabels().size()) == 0));
        stop();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(timeout = 10000)
    public void testEditlogRecover() throws Exception {
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p1", "p2", "p3"));
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p4"));
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p5", "p6"));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1"), toNodeId("n2"), NodeLabelTestBase.toSet("p2")));
        replaceLabelsOnNode(((java.util.Map) (ImmutableMap.of(toNodeId("n3"), NodeLabelTestBase.toSet("p3"), toNodeId("n4"), NodeLabelTestBase.toSet("p4"), toNodeId("n5"), NodeLabelTestBase.toSet("p5"), toNodeId("n6"), NodeLabelTestBase.toSet("p6"), toNodeId("n7"), NodeLabelTestBase.toSet("p6")))));
        /* node -> partition p1: n1 p2: n2 p3: n3 p4: n4 p5: n5 p6: n6, n7 */
        removeFromClusterNodeLabels(NodeLabelTestBase.toSet("p1"));
        removeFromClusterNodeLabels(Arrays.asList("p3", "p5"));
        /* After removed p2: n2 p4: n4 p6: n6, n7 */
        // shutdown mgr and start a new mgr
        stop();
        mgr = new TestFileSystemNodeLabelsStore.MockNodeLabelManager();
        mgr.init(conf);
        start();
        // check variables
        Assert.assertEquals(3, getClusterNodeLabelNames().size());
        Assert.assertTrue(getClusterNodeLabelNames().containsAll(Arrays.asList("p2", "p4", "p6")));
        NodeLabelTestBase.assertMapContains(getNodeLabels(), ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p2"), toNodeId("n4"), NodeLabelTestBase.toSet("p4"), toNodeId("n6"), NodeLabelTestBase.toSet("p6"), toNodeId("n7"), NodeLabelTestBase.toSet("p6")));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(), ImmutableMap.of("p6", NodeLabelTestBase.toSet(toNodeId("n6"), toNodeId("n7")), "p4", NodeLabelTestBase.toSet(toNodeId("n4")), "p2", NodeLabelTestBase.toSet(toNodeId("n2"))));
        stop();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(timeout = 10000)
    public void testSerilizationAfterRecovery() throws Exception {
        // Add to cluster node labels, p2/p6 are non-exclusive.
        mgr.addToCluserNodeLabels(Arrays.asList(NodeLabel.newInstance("p1", true), NodeLabel.newInstance("p2", false), NodeLabel.newInstance("p3", true), NodeLabel.newInstance("p4", true), NodeLabel.newInstance("p5", true), NodeLabel.newInstance("p6", false)));
        mgr.replaceLabelsOnNode(ImmutableMap.of(toNodeId("n1"), NodeLabelTestBase.toSet("p1"), toNodeId("n2"), NodeLabelTestBase.toSet("p2")));
        replaceLabelsOnNode(((java.util.Map) (ImmutableMap.of(toNodeId("n3"), NodeLabelTestBase.toSet("p3"), toNodeId("n4"), NodeLabelTestBase.toSet("p4"), toNodeId("n5"), NodeLabelTestBase.toSet("p5"), toNodeId("n6"), NodeLabelTestBase.toSet("p6"), toNodeId("n7"), NodeLabelTestBase.toSet("p6")))));
        /* node -> labels 
        p1: n1 
        p2: n2 
        p3: n3
        p4: n4 
        p5: n5 
        p6: n6, n7
         */
        removeFromClusterNodeLabels(NodeLabelTestBase.toSet("p1"));
        removeFromClusterNodeLabels(Arrays.asList("p3", "p5"));
        /* After removed 
        p2: n2 
        p4: n4 
        p6: n6, n7
         */
        // shutdown mgr and start a new mgr
        stop();
        mgr = new TestFileSystemNodeLabelsStore.MockNodeLabelManager();
        mgr.init(conf);
        start();
        // check variables
        Assert.assertEquals(3, getClusterNodeLabelNames().size());
        Assert.assertTrue(getClusterNodeLabelNames().containsAll(Arrays.asList("p2", "p4", "p6")));
        NodeLabelTestBase.assertMapContains(getNodeLabels(), ImmutableMap.of(toNodeId("n2"), NodeLabelTestBase.toSet("p2"), toNodeId("n4"), NodeLabelTestBase.toSet("p4"), toNodeId("n6"), NodeLabelTestBase.toSet("p6"), toNodeId("n7"), NodeLabelTestBase.toSet("p6")));
        NodeLabelTestBase.assertLabelsToNodesEquals(getLabelsToNodes(), ImmutableMap.of("p6", NodeLabelTestBase.toSet(toNodeId("n6"), toNodeId("n7")), "p4", NodeLabelTestBase.toSet(toNodeId("n4")), "p2", NodeLabelTestBase.toSet(toNodeId("n2"))));
        Assert.assertFalse(isExclusiveNodeLabel("p2"));
        Assert.assertTrue(isExclusiveNodeLabel("p4"));
        Assert.assertFalse(isExclusiveNodeLabel("p6"));
        /* Add label p7,p8 then shutdown */
        mgr = new TestFileSystemNodeLabelsStore.MockNodeLabelManager();
        mgr.init(conf);
        start();
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p7", "p8"));
        stop();
        /* Restart, add label p9 and shutdown */
        mgr = new TestFileSystemNodeLabelsStore.MockNodeLabelManager();
        mgr.init(conf);
        start();
        addToCluserNodeLabelsWithDefaultExclusivity(NodeLabelTestBase.toSet("p9"));
        stop();
        /* Recovery, and see if p9 added */
        mgr = new TestFileSystemNodeLabelsStore.MockNodeLabelManager();
        mgr.init(conf);
        start();
        // check variables
        Assert.assertEquals(6, getClusterNodeLabelNames().size());
        Assert.assertTrue(getClusterNodeLabelNames().containsAll(Arrays.asList("p2", "p4", "p6", "p7", "p8", "p9")));
        stop();
    }

    @Test
    public void testRootMkdirOnInitStore() throws Exception {
        final FileSystem mockFs = Mockito.mock(FileSystem.class);
        FileSystemNodeLabelsStore mockStore = new FileSystemNodeLabelsStore() {
            public void initFileSystem(Configuration config) throws IOException {
                setFs(mockFs);
            }
        };
        mockStore.setFs(mockFs);
        verifyMkdirsCount(mockStore, true, 1);
        verifyMkdirsCount(mockStore, false, 2);
        verifyMkdirsCount(mockStore, true, 3);
        verifyMkdirsCount(mockStore, false, 4);
    }
}

