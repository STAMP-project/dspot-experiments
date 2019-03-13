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


import RuleConstant.FLOW_GRADE_QPS;
import RuleConstant.FLOW_GRADE_THREAD;
import RuleConstant.STRATEGY_CHAIN;
import RuleConstant.STRATEGY_DIRECT;
import RuleConstant.STRATEGY_RELATE;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slots.block.flow.controller.DefaultController;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author jialiang.linjl
 */
@Ignore("Deprecated test for legacy FlowRule")
public class FlowRuleTest {
    @Test
    public void testFlowRule_grade() {
        FlowRule flowRule = new FlowRule();
        flowRule.setGrade(FLOW_GRADE_QPS);
        flowRule.setCount(1);
        flowRule.setLimitApp("default");
        flowRule.setStrategy(STRATEGY_DIRECT);
        DefaultController defaultController = new DefaultController(1, flowRule.getGrade());
        flowRule.setRater(defaultController);
        Context context = Mockito.mock(Context.class);
        DefaultNode node = Mockito.mock(DefaultNode.class);
        ClusterNode cn = Mockito.mock(ClusterNode.class);
        Mockito.when(context.getOrigin()).thenReturn("");
        Mockito.when(node.getClusterNode()).thenReturn(cn);
        Mockito.when(cn.passQps()).thenReturn(1L);
        Assert.assertTrue(((flowRule.passCheck(context, node, 1, new Object[0])) == false));
        flowRule.setGrade(FLOW_GRADE_THREAD);
        defaultController = new DefaultController(1, flowRule.getGrade());
        flowRule.setRater(defaultController);
        Mockito.when(cn.curThreadNum()).thenReturn(1);
        Assert.assertTrue(((flowRule.passCheck(context, node, 1, new Object[0])) == false));
    }

    @Test
    public void testFlowRule_strategy() {
        FlowRule flowRule = new FlowRule();
        flowRule.setGrade(FLOW_GRADE_QPS);
        flowRule.setCount(1);
        flowRule.setLimitApp("default");
        flowRule.setStrategy(STRATEGY_CHAIN);
        DefaultController defaultController = new DefaultController(1, flowRule.getGrade());
        flowRule.setRater(defaultController);
        flowRule.setRefResource("entry1");
        Context context = Mockito.mock(Context.class);
        DefaultNode dn = Mockito.mock(DefaultNode.class);
        Mockito.when(context.getName()).thenReturn("entry1");
        Mockito.when(dn.passQps()).thenReturn(1L);
        Assert.assertTrue(((flowRule.passCheck(context, dn, 1, new Object[0])) == false));
        Mockito.when(context.getName()).thenReturn("entry2");
        Assert.assertTrue(flowRule.passCheck(context, dn, 1, new Object[0]));
        // Strategy == relate
        flowRule.setStrategy(STRATEGY_CHAIN);
        ClusterNode cn = Mockito.mock(ClusterNode.class);
        Assert.assertTrue(((flowRule.passCheck(context, dn, 1, new Object[0])) == true));
    }

    @Test
    public void testOrigin() {
        FlowRule flowRule = new FlowRule();
        flowRule.setGrade(FLOW_GRADE_QPS);
        flowRule.setCount(1);
        flowRule.setLimitApp("default");
        flowRule.setStrategy(STRATEGY_DIRECT);
        DefaultController defaultController = new DefaultController(1, flowRule.getGrade());
        flowRule.setRater(defaultController);
        flowRule.setRefResource("entry1");
        Context context = Mockito.mock(Context.class);
        DefaultNode dn = Mockito.mock(DefaultNode.class);
        Mockito.when(context.getOrigin()).thenReturn("origin1");
        Mockito.when(dn.passQps()).thenReturn(1L);
        Mockito.when(context.getOriginNode()).thenReturn(dn);
        /* first scenario, limit app as default */
        ClusterNode cn = Mockito.mock(ClusterNode.class);
        Mockito.when(dn.getClusterNode()).thenReturn(cn);
        Mockito.when(cn.passQps()).thenReturn(1L);
        Assert.assertTrue(((flowRule.passCheck(context, dn, 1, new Object[0])) == false));
        Mockito.when(cn.passQps()).thenReturn(0L);
        Assert.assertTrue(flowRule.passCheck(context, dn, 1, new Object[0]));
        flowRule.setStrategy(STRATEGY_CHAIN);
        flowRule.setResource("entry1");
        Mockito.when(context.getName()).thenReturn("entry1");
        Assert.assertTrue(((flowRule.passCheck(context, dn, 1, new Object[0])) == false));
        Mockito.when(context.getName()).thenReturn("entry2");
        Assert.assertTrue(flowRule.passCheck(context, dn, 1, new Object[0]));
        // relate node
        flowRule.setStrategy(STRATEGY_RELATE);
        flowRule.setResource("worong");
        Assert.assertTrue(flowRule.passCheck(context, dn, 1, new Object[0]));
        /* second scenario test a context with the same origin1 */
        flowRule.setLimitApp("origin1");
        Mockito.when(context.getName()).thenReturn("entry1");
        // direct node
        flowRule.setStrategy(STRATEGY_DIRECT);
        Assert.assertTrue(((flowRule.passCheck(context, dn, 1, new Object[0])) == false));
        // chain node
        flowRule.setResource("entry1");
        flowRule.setStrategy(STRATEGY_CHAIN);
        Mockito.when(context.getName()).thenReturn("entry1");
        Assert.assertTrue(((flowRule.passCheck(context, dn, 1, new Object[0])) == false));
        Mockito.when(context.getName()).thenReturn("entry2");
        Assert.assertTrue(flowRule.passCheck(context, dn, 1, new Object[0]));
        // relate node
        flowRule.setStrategy(STRATEGY_RELATE);
        flowRule.setResource("not exits");
        Assert.assertTrue(flowRule.passCheck(context, dn, 1, new Object[0]));
        Mockito.when(context.getOrigin()).thenReturn("origin2");
        Assert.assertTrue(flowRule.passCheck(context, dn, 1, new Object[0]));
        /* limit app= other */
        flowRule.setLimitApp("other");
        flowRule.setResource("hello world");
        flowRule.setStrategy(STRATEGY_DIRECT);
        Assert.assertTrue(((flowRule.passCheck(context, dn, 1, new Object[0])) == false));
        flowRule.setStrategy(STRATEGY_CHAIN);
        flowRule.setResource("entry1");
        Mockito.when(context.getName()).thenReturn("entry1");
        Assert.assertTrue(((flowRule.passCheck(context, dn, 1, new Object[0])) == false));
        Mockito.when(context.getName()).thenReturn("entry2");
        Assert.assertTrue(flowRule.passCheck(context, dn, 1, new Object[0]));
    }
}

