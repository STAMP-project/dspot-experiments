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


import NodeAttribute.PREFIX_CENTRALIZED;
import NodeAttributeType.STRING;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.NodeAttribute;
import org.apache.hadoop.yarn.event.InlineDispatcher;
import org.apache.hadoop.yarn.nodelabels.AttributeValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test class for FileSystemNodeAttributeStore.
 */
public class TestFileSystemNodeAttributeStore {
    private TestFileSystemNodeAttributeStore.MockNodeAttrbuteManager mgr = null;

    private Configuration conf = null;

    private static class MockNodeAttrbuteManager extends NodeAttributesManagerImpl {
        @Override
        protected void initDispatcher(Configuration conf) {
            super.dispatcher = new InlineDispatcher();
        }

        @Override
        protected void startDispatcher() {
            // Do nothing
        }

        @Override
        protected void stopDispatcher() {
            // Do nothing
        }
    }

    @Test(timeout = 10000)
    public void testEmptyRecoverSkipInternalUdpate() throws Exception {
        // Stop manager
        stop();
        // Start new attribute manager with same path
        mgr = Mockito.spy(new TestFileSystemNodeAttributeStore.MockNodeAttrbuteManager());
        mgr.init(conf);
        start();
        internalUpdateAttributesOnNodes(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test(timeout = 10000)
    public void testRecoverWithMirror() throws Exception {
        // ------host0----
        // add       -GPU & FPGA
        // remove    -GPU
        // replace   -Docker
        // ------host1----
        // add--GPU
        NodeAttribute docker = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "DOCKER", STRING, "docker-0");
        NodeAttribute gpu = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "GPU", STRING, "nvidia");
        NodeAttribute fpga = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "FPGA", STRING, "asus");
        Map<String, Set<NodeAttribute>> toAddAttributes = new HashMap<>();
        toAddAttributes.put("host0", ImmutableSet.of(gpu, fpga));
        toAddAttributes.put("host1", ImmutableSet.of(gpu));
        // Add node attribute
        mgr.addNodeAttributes(toAddAttributes);
        Assert.assertEquals("host0 size", 2, getAttributesForNode("host0").size());
        // Add test to remove
        toAddAttributes.clear();
        toAddAttributes.put("host0", ImmutableSet.of(gpu));
        mgr.removeNodeAttributes(toAddAttributes);
        // replace nodeattribute
        toAddAttributes.clear();
        toAddAttributes.put("host0", ImmutableSet.of(docker));
        mgr.replaceNodeAttributes(PREFIX_CENTRALIZED, toAddAttributes);
        Map<NodeAttribute, AttributeValue> attrs = mgr.getAttributesForNode("host0");
        Assert.assertEquals(attrs.size(), 1);
        Assert.assertEquals(attrs.keySet().toArray()[0], docker);
        stop();
        // Start new attribute manager with same path
        mgr = new TestFileSystemNodeAttributeStore.MockNodeAttrbuteManager();
        mgr.init(conf);
        start();
        getAttributesForNode("host0");
        Assert.assertEquals("host0 size", 1, getAttributesForNode("host0").size());
        Assert.assertEquals("host1 size", 1, getAttributesForNode("host1").size());
        attrs = getAttributesForNode("host0");
        Assert.assertEquals(attrs.size(), 1);
        Assert.assertEquals(attrs.keySet().toArray()[0], docker);
        // ------host0----
        // current       - docker
        // replace       - gpu
        // ----- host1----
        // current       - gpu
        // add           - docker
        toAddAttributes.clear();
        toAddAttributes.put("host0", ImmutableSet.of(gpu));
        mgr.replaceNodeAttributes(PREFIX_CENTRALIZED, toAddAttributes);
        toAddAttributes.clear();
        toAddAttributes.put("host1", ImmutableSet.of(docker));
        mgr.addNodeAttributes(toAddAttributes);
        // Recover from mirror and edit log
        stop();
        mgr = new TestFileSystemNodeAttributeStore.MockNodeAttrbuteManager();
        mgr.init(conf);
        start();
        Assert.assertEquals("host0 size", 1, getAttributesForNode("host0").size());
        Assert.assertEquals("host1 size", 2, getAttributesForNode("host1").size());
        attrs = getAttributesForNode("host0");
        Assert.assertEquals(attrs.size(), 1);
        Assert.assertEquals(attrs.keySet().toArray()[0], gpu);
        attrs = getAttributesForNode("host1");
        Assert.assertTrue(attrs.keySet().contains(docker));
        Assert.assertTrue(attrs.keySet().contains(gpu));
    }

    @Test(timeout = 10000)
    public void testRecoverFromEditLog() throws Exception {
        NodeAttribute docker = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "DOCKER", STRING, "docker-0");
        NodeAttribute gpu = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "GPU", STRING, "nvidia");
        NodeAttribute fpga = NodeAttribute.newInstance(PREFIX_CENTRALIZED, "FPGA", STRING, "asus");
        Map<String, Set<NodeAttribute>> toAddAttributes = new HashMap<>();
        toAddAttributes.put("host0", ImmutableSet.of(gpu, fpga));
        toAddAttributes.put("host1", ImmutableSet.of(docker));
        // Add node attribute
        mgr.addNodeAttributes(toAddAttributes);
        Assert.assertEquals("host0 size", 2, getAttributesForNode("host0").size());
        // Increase editlog operation
        for (int i = 0; i < 5; i++) {
            // Add gpu host1
            toAddAttributes.clear();
            toAddAttributes.put("host0", ImmutableSet.of(gpu));
            mgr.removeNodeAttributes(toAddAttributes);
            // Add gpu host1
            toAddAttributes.clear();
            toAddAttributes.put("host1", ImmutableSet.of(docker));
            mgr.addNodeAttributes(toAddAttributes);
            // Remove GPU replace
            toAddAttributes.clear();
            toAddAttributes.put("host0", ImmutableSet.of(gpu));
            mgr.replaceNodeAttributes(PREFIX_CENTRALIZED, toAddAttributes);
            // Add fgpa host1
            toAddAttributes.clear();
            toAddAttributes.put("host1", ImmutableSet.of(gpu));
            mgr.addNodeAttributes(toAddAttributes);
        }
        stop();
        // Start new attribute manager with same path
        mgr = new TestFileSystemNodeAttributeStore.MockNodeAttrbuteManager();
        mgr.init(conf);
        start();
        Assert.assertEquals("host0 size", 1, getAttributesForNode("host0").size());
        Assert.assertEquals("host1 size", 2, getAttributesForNode("host1").size());
        toAddAttributes.clear();
        NodeAttribute replaced = NodeAttribute.newInstance("GPU2", STRING, "nvidia2");
        toAddAttributes.put("host0", ImmutableSet.of(replaced));
        mgr.replaceNodeAttributes(PREFIX_CENTRALIZED, toAddAttributes);
        stop();
        mgr = new TestFileSystemNodeAttributeStore.MockNodeAttrbuteManager();
        mgr.init(conf);
        start();
        Map<NodeAttribute, AttributeValue> valueMap = mgr.getAttributesForNode("host0");
        Map.Entry<NodeAttribute, AttributeValue> entry = valueMap.entrySet().iterator().next();
        NodeAttribute attribute = entry.getKey();
        Assert.assertEquals("host0 size", 1, getAttributesForNode("host0").size());
        Assert.assertEquals("host1 size", 2, getAttributesForNode("host1").size());
        checkNodeAttributeEqual(replaced, attribute);
    }
}

