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
package com.alibaba.csp.sentinel.slots.clusterbuilder;


import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.node.Node;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jialiang.linjl
 */
public class ClusterNodeBuilderTest {
    @Test
    public void clusterNodeBuilder_normal() throws Exception {
        ContextUtil.enter("entry1", "caller1");
        Entry nodeA = SphU.entry("nodeA");
        Node curNode = nodeA.getCurNode();
        Assert.assertSame(curNode.getClass(), DefaultNode.class);
        DefaultNode dN = ((DefaultNode) (curNode));
        Assert.assertTrue(dN.getClusterNode().getOriginCountMap().containsKey("caller1"));
        Assert.assertSame(nodeA.getOriginNode(), dN.getClusterNode().getOrCreateOriginNode("caller1"));
        if (nodeA != null) {
            nodeA.exit();
        }
        ContextUtil.exit();
        ContextUtil.enter("entry4", "caller2");
        nodeA = SphU.entry("nodeA");
        curNode = nodeA.getCurNode();
        Assert.assertSame(curNode.getClass(), DefaultNode.class);
        DefaultNode dN1 = ((DefaultNode) (curNode));
        Assert.assertTrue(dN1.getClusterNode().getOriginCountMap().containsKey("caller2"));
        Assert.assertNotSame(dN1, dN);
        if (nodeA != null) {
            nodeA.exit();
        }
        ContextUtil.exit();
    }
}

