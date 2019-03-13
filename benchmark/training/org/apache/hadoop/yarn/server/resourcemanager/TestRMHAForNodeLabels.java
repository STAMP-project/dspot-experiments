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
package org.apache.hadoop.yarn.server.resourcemanager;


import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRMHAForNodeLabels extends RMHATestBase {
    public static final Logger LOG = LoggerFactory.getLogger(TestRMHAForNodeLabels.class);

    @Test
    public void testRMHARecoverNodeLabels() throws Exception {
        // start two RMs, and transit rm1 to active, rm2 to standby
        startRMs();
        // Add labels to rm1
        getRMContext().getNodeLabelManager().addToCluserNodeLabels(Arrays.asList(NodeLabel.newInstance("a"), NodeLabel.newInstance("b"), NodeLabel.newInstance("c")));
        Map<NodeId, Set<String>> nodeToLabels = new HashMap<>();
        nodeToLabels.put(NodeId.newInstance("host1", 0), ImmutableSet.of("a"));
        nodeToLabels.put(NodeId.newInstance("host2", 0), ImmutableSet.of("b"));
        getRMContext().getNodeLabelManager().replaceLabelsOnNode(nodeToLabels);
        // Do the failover
        explicitFailover();
        // Check labels in rm2
        Assert.assertTrue(getRMContext().getNodeLabelManager().getClusterNodeLabelNames().containsAll(ImmutableSet.of("a", "b", "c")));
        Assert.assertTrue(getRMContext().getNodeLabelManager().getNodeLabels().get(NodeId.newInstance("host1", 0)).contains("a"));
        Assert.assertTrue(getRMContext().getNodeLabelManager().getNodeLabels().get(NodeId.newInstance("host2", 0)).contains("b"));
    }
}

