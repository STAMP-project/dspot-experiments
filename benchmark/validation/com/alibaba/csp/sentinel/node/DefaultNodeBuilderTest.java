/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.node;


import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link DefaultNodeBuilder}.
 *
 * @author cdfive
 */
public class DefaultNodeBuilderTest {
    @Test
    public void testBuildTreeNode() {
        DefaultNodeBuilder builder = new DefaultNodeBuilder();
        ResourceWrapper id = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("resA", EntryType.IN);
        ClusterNode clusterNode = new ClusterNode();
        DefaultNode defaultNode = builder.buildTreeNode(id, clusterNode);
        Assert.assertNotNull(defaultNode);
        Assert.assertEquals(id, defaultNode.getId());
        Assert.assertEquals(clusterNode, defaultNode.getClusterNode());
        // verify each call returns a different instance
        DefaultNode defaultNode2 = builder.buildTreeNode(id, clusterNode);
        Assert.assertNotNull(defaultNode2);
        Assert.assertNotSame(defaultNode, defaultNode2);
        // now DefaultNode#equals(Object) is not implemented, they are not equal
        Assert.assertNotEquals(defaultNode, defaultNode2);
    }

    @Test
    public void testBuildTreeNodeNullClusterNode() {
        DefaultNodeBuilder builder = new DefaultNodeBuilder();
        ResourceWrapper id = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("resA", EntryType.IN);
        DefaultNode defaultNode = builder.buildTreeNode(id, null);
        Assert.assertNotNull(defaultNode);
        Assert.assertEquals(id, defaultNode.getId());
        Assert.assertNull(defaultNode.getClusterNode());
        // verify each call returns a different instance
        DefaultNode defaultNode2 = builder.buildTreeNode(id, null);
        Assert.assertNotNull(defaultNode2);
        Assert.assertNotSame(defaultNode, defaultNode2);
        // now DefaultNode#equals(Object) is not implemented, they are not equal
        Assert.assertNotEquals(defaultNode, defaultNode2);
    }

    @Test
    public void testBuildClusterNode() {
        DefaultNodeBuilder builder = new DefaultNodeBuilder();
        ClusterNode clusterNode = builder.buildClusterNode();
        Assert.assertNotNull(clusterNode);
        // verify each call returns a different instance
        ClusterNode clusterNode2 = builder.buildClusterNode();
        Assert.assertNotNull(clusterNode2);
        Assert.assertNotSame(clusterNode, clusterNode2);
        // as new a ClusterNode instance in DefaultNodeBuilder#buildClusterNode(), they are not equal
        Assert.assertNotEquals(clusterNode, clusterNode2);
    }
}

