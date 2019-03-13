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
package com.alibaba.csp.sentinel.slots.nodeselector;


import Constants.ROOT;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.EntranceNode;
import com.alibaba.csp.sentinel.node.Node;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class NodeSelectorTest {
    @Test
    public void testSingleEntrance() throws Exception {
        final String contextName = "entry_SingleEntrance";
        ContextUtil.enter(contextName);
        EntranceNode entranceNode = null;
        for (Node node : ROOT.getChildList()) {
            entranceNode = ((EntranceNode) (node));
            if (entranceNode.getId().getName().equals(contextName)) {
                break;
            } else {
                System.out.println(("Single entry: " + (entranceNode.getId().getName())));
            }
        }
        Assert.assertNotNull(entranceNode);
        Assert.assertTrue(entranceNode.getId().getName().equalsIgnoreCase(contextName));
        final String resName = "nodeA";
        Entry nodeA = SphU.entry(resName);
        Assert.assertNotNull(ContextUtil.getContext().getCurNode());
        Assert.assertEquals(resName, getId().getName());
        boolean hasNode = false;
        for (Node node : entranceNode.getChildList()) {
            if (getId().getName().equals(resName)) {
                hasNode = true;
            }
        }
        Assert.assertTrue(hasNode);
        if (nodeA != null) {
            nodeA.exit();
        }
        ContextUtil.exit();
    }

    @Test
    public void testMultipleEntrance() throws Exception {
        final String firstEntry = "entry_multiple_one";
        final String anotherEntry = "entry_multiple_another";
        final String resName = "nodeA";
        Node firstNode;
        Node anotherNode;
        ContextUtil.enter(firstEntry);
        Entry nodeA = SphU.entry(resName);
        firstNode = ContextUtil.getContext().getCurNode();
        if (nodeA != null) {
            nodeA.exit();
        }
        ContextUtil.exit();
        ContextUtil.enter(anotherEntry);
        nodeA = SphU.entry(resName);
        anotherNode = ContextUtil.getContext().getCurNode();
        if (nodeA != null) {
            nodeA.exit();
        }
        Assert.assertNotSame(firstNode, anotherNode);
        for (Node node : ROOT.getChildList()) {
            EntranceNode firstEntrance = ((EntranceNode) (node));
            if (firstEntrance.getId().getName().equals(firstEntry)) {
                Assert.assertEquals(1, firstEntrance.getChildList().size());
                for (Node child : firstEntrance.getChildList()) {
                    Assert.assertEquals(resName, getId().getName());
                }
            } else
                if (firstEntrance.getId().getName().equals(anotherEntry)) {
                    Assert.assertEquals(1, firstEntrance.getChildList().size());
                    for (Node child : firstEntrance.getChildList()) {
                        Assert.assertEquals(resName, getId().getName());
                    }
                } else {
                    System.out.println(("Multiple entries: " + (firstEntrance.getId().getName())));
                }

        }
        ContextUtil.exit();
    }
}

