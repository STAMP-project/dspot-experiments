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
package com.alibaba.csp.sentinel.slots.block.flow;


import RuleConstant.LIMIT_APP_OTHER;
import RuleConstant.STRATEGY_CHAIN;
import RuleConstant.STRATEGY_RELATE;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Eric Zhao
 */
public class FlowRuleCheckerTest {
    @Test
    public void testDefaultLimitAppFlowSelectNode() {
        DefaultNode node = Mockito.mock(DefaultNode.class);
        ClusterNode cn = Mockito.mock(ClusterNode.class);
        Mockito.when(node.getClusterNode()).thenReturn(cn);
        Context context = Mockito.mock(Context.class);
        // limitApp: default
        FlowRule rule = new FlowRule("testDefaultLimitAppFlowSelectNode").setCount(1);
        Assert.assertEquals(cn, FlowRuleChecker.selectNodeByRequesterAndStrategy(rule, context, node));
    }

    @Test
    public void testCustomOriginFlowSelectNode() {
        String origin = "appA";
        String limitAppB = "appB";
        DefaultNode node = Mockito.mock(DefaultNode.class);
        DefaultNode originNode = Mockito.mock(DefaultNode.class);
        ClusterNode cn = Mockito.mock(ClusterNode.class);
        Mockito.when(node.getClusterNode()).thenReturn(cn);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getOrigin()).thenReturn(origin);
        Mockito.when(context.getOriginNode()).thenReturn(originNode);
        FlowRule rule = new FlowRule("testCustomOriginFlowSelectNode").setCount(1);
        rule.setLimitApp(origin);
        // Origin matches, return the origin node.
        Assert.assertEquals(originNode, FlowRuleChecker.selectNodeByRequesterAndStrategy(rule, context, node));
        rule.setLimitApp(limitAppB);
        // Origin mismatch, no node found.
        Assert.assertNull(FlowRuleChecker.selectNodeByRequesterAndStrategy(rule, context, node));
    }

    @Test
    public void testOtherOriginFlowSelectNode() {
        String originA = "appA";
        String originB = "appB";
        DefaultNode node = Mockito.mock(DefaultNode.class);
        DefaultNode originNode = Mockito.mock(DefaultNode.class);
        ClusterNode cn = Mockito.mock(ClusterNode.class);
        Mockito.when(node.getClusterNode()).thenReturn(cn);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getOriginNode()).thenReturn(originNode);
        FlowRule ruleA = new FlowRule("testOtherOriginFlowSelectNode").setCount(1);
        ruleA.setLimitApp(originA);
        FlowRule ruleB = new FlowRule("testOtherOriginFlowSelectNode").setCount(2);
        ruleB.setLimitApp(LIMIT_APP_OTHER);
        FlowRuleManager.loadRules(Arrays.asList(ruleA, ruleB));
        // Origin matches other, return the origin node.
        Mockito.when(context.getOrigin()).thenReturn(originB);
        Assert.assertEquals(originNode, FlowRuleChecker.selectNodeByRequesterAndStrategy(ruleB, context, node));
        // Origin matches limitApp of an existing rule, so no nodes are selected.
        Mockito.when(context.getOrigin()).thenReturn(originA);
        Assert.assertNull(FlowRuleChecker.selectNodeByRequesterAndStrategy(ruleB, context, node));
    }

    @Test
    public void testSelectNodeForEmptyReference() {
        DefaultNode node = Mockito.mock(DefaultNode.class);
        Context context = Mockito.mock(Context.class);
        FlowRule rule = new FlowRule("testSelectNodeForEmptyReference").setCount(1).setStrategy(STRATEGY_CHAIN);
        Assert.assertNull(FlowRuleChecker.selectReferenceNode(rule, context, node));
    }

    @Test
    public void testSelectNodeForRelateReference() {
        String refResource = "testSelectNodeForRelateReference_refResource";
        DefaultNode node = Mockito.mock(DefaultNode.class);
        ClusterNode refCn = Mockito.mock(ClusterNode.class);
        ClusterBuilderSlot.getClusterNodeMap().put(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(refResource, EntryType.IN), refCn);
        Context context = Mockito.mock(Context.class);
        FlowRule rule = new FlowRule("testSelectNodeForRelateReference").setCount(1).setStrategy(STRATEGY_RELATE).setRefResource(refResource);
        Assert.assertEquals(refCn, FlowRuleChecker.selectReferenceNode(rule, context, node));
    }

    @Test
    public void testSelectReferenceNodeForContextEntrance() {
        String contextName = "good_context";
        DefaultNode node = Mockito.mock(DefaultNode.class);
        Context context = Mockito.mock(Context.class);
        FlowRule rule = new FlowRule("testSelectReferenceNodeForContextEntrance").setCount(1).setStrategy(STRATEGY_CHAIN).setRefResource(contextName);
        Mockito.when(context.getName()).thenReturn(contextName);
        Assert.assertEquals(node, FlowRuleChecker.selectReferenceNode(rule, context, node));
        Mockito.when(context.getName()).thenReturn("other_context");
        Assert.assertNull(FlowRuleChecker.selectReferenceNode(rule, context, node));
    }

    @Test
    public void testPassCheckNullLimitApp() {
        FlowRule rule = new FlowRule("abc").setCount(1);
        rule.setLimitApp(null);
        Assert.assertTrue(FlowRuleChecker.passCheck(rule, null, null, 1));
    }

    @Test
    public void testPassCheckSelectEmptyNodeSuccess() {
        FlowRule rule = new FlowRule("abc").setCount(1);
        rule.setLimitApp("abc");
        DefaultNode node = Mockito.mock(DefaultNode.class);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getOrigin()).thenReturn("def");
        Assert.assertTrue(FlowRuleChecker.passCheck(rule, context, node, 1));
    }
}

