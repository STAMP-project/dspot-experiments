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


import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Eric Zhao
 */
public class FlowSlotTest {
    @Test
    public void testCheckFlowPass() throws Exception {
        FlowSlot flowSlot = Mockito.mock(FlowSlot.class);
        Context context = Mockito.mock(Context.class);
        DefaultNode node = Mockito.mock(DefaultNode.class);
        Mockito.doCallRealMethod().when(flowSlot).checkFlow(ArgumentMatchers.any(ResourceWrapper.class), ArgumentMatchers.any(Context.class), ArgumentMatchers.any(DefaultNode.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        String resA = "resAK";
        String resB = "resBK";
        FlowRule rule1 = new FlowRule(resA).setCount(10);
        FlowRule rule2 = new FlowRule(resB).setCount(10);
        // Here we only load rules for resA.
        FlowRuleManager.loadRules(Collections.singletonList(rule1));
        Mockito.when(flowSlot.canPassCheck(ArgumentMatchers.eq(rule1), ArgumentMatchers.any(Context.class), ArgumentMatchers.any(DefaultNode.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(flowSlot.canPassCheck(ArgumentMatchers.eq(rule2), ArgumentMatchers.any(Context.class), ArgumentMatchers.any(DefaultNode.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean())).thenReturn(false);
        flowSlot.checkFlow(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resA, EntryType.IN), context, node, 1, false);
        flowSlot.checkFlow(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resB, EntryType.IN), context, node, 1, false);
    }

    @Test(expected = FlowException.class)
    public void testCheckFlowBlock() throws Exception {
        FlowSlot flowSlot = Mockito.mock(FlowSlot.class);
        Context context = Mockito.mock(Context.class);
        DefaultNode node = Mockito.mock(DefaultNode.class);
        Mockito.doCallRealMethod().when(flowSlot).checkFlow(ArgumentMatchers.any(ResourceWrapper.class), ArgumentMatchers.any(Context.class), ArgumentMatchers.any(DefaultNode.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        String resA = "resAK";
        FlowRule rule = new FlowRule(resA).setCount(10);
        FlowRuleManager.loadRules(Collections.singletonList(rule));
        Mockito.when(flowSlot.canPassCheck(ArgumentMatchers.any(FlowRule.class), ArgumentMatchers.any(Context.class), ArgumentMatchers.any(DefaultNode.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean())).thenReturn(false);
        flowSlot.checkFlow(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resA, EntryType.IN), context, node, 1, false);
    }
}

