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
package com.alibaba.csp.sentinel.slots.block.flow.param;


import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test cases for {@link ParamFlowSlot}.
 *
 * @author Eric Zhao
 * @since 0.2.0
 */
public class ParamFlowSlotTest {
    private final ParamFlowSlot paramFlowSlot = new ParamFlowSlot();

    @Test
    public void testNegativeParamIdx() throws Throwable {
        String resourceName = "testNegativeParamIdx";
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        ParamFlowRule rule = new ParamFlowRule(resourceName).setCount(1).setParamIdx((-1));
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
        paramFlowSlot.entry(null, resourceWrapper, null, 1, false, "abc", "def", "ghi");
        Assert.assertEquals(2, rule.getParamIdx().longValue());
        rule.setParamIdx((-100));
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
        paramFlowSlot.entry(null, resourceWrapper, null, 1, false, "abc", "def", "ghi");
        Assert.assertEquals(100, rule.getParamIdx().longValue());
        rule.setParamIdx(0);
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
        paramFlowSlot.entry(null, resourceWrapper, null, 1, false, "abc", "def", "ghi");
        Assert.assertEquals(0, rule.getParamIdx().longValue());
    }

    @Test
    public void testEntryWhenParamFlowRuleNotExists() throws Throwable {
        String resourceName = "testEntryWhenParamFlowRuleNotExists";
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        paramFlowSlot.entry(null, resourceWrapper, null, 1, false, "abc");
        // The parameter metric instance will not be created.
        Assert.assertNull(ParamFlowSlot.getParamMetric(resourceWrapper));
    }

    @Test
    public void testEntryWhenParamFlowExists() throws Throwable {
        String resourceName = "testEntryWhenParamFlowExists";
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        long argToGo = 1L;
        double count = 10;
        ParamFlowRule rule = new ParamFlowRule(resourceName).setCount(count).setParamIdx(0);
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
        ParameterMetric metric = Mockito.mock(ParameterMetric.class);
        // First pass, then blocked.
        Mockito.when(metric.getPassParamQps(rule.getParamIdx(), argToGo)).thenReturn((count - 1)).thenReturn(count);
        // Insert the mock metric to control pass or block.
        ParamFlowSlot.getMetricsMap().put(resourceWrapper, metric);
        // The first entry will pass.
        paramFlowSlot.entry(null, resourceWrapper, null, 1, false, argToGo);
        // The second entry will be blocked.
        try {
            paramFlowSlot.entry(null, resourceWrapper, null, 1, false, argToGo);
        } catch (ParamFlowException ex) {
            Assert.assertEquals(String.valueOf(argToGo), ex.getMessage());
            Assert.assertEquals(resourceName, ex.getResourceName());
            return;
        }
        Assert.fail("The second entry should be blocked");
    }

    @Test
    public void testGetNullParamMetric() {
        Assert.assertNull(ParamFlowSlot.getParamMetric(null));
    }

    @Test
    public void testInitParamMetrics() {
        int index = 1;
        String resourceName = "res-" + (System.currentTimeMillis());
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        Assert.assertNull(ParamFlowSlot.getParamMetric(resourceWrapper));
        paramFlowSlot.initHotParamMetricsFor(resourceWrapper, index);
        ParameterMetric metric = ParamFlowSlot.getParamMetric(resourceWrapper);
        Assert.assertNotNull(metric);
        Assert.assertNotNull(metric.getRollingParameters().get(index));
        Assert.assertNotNull(metric.getThreadCountMap().get(index));
        // Duplicate init.
        paramFlowSlot.initHotParamMetricsFor(resourceWrapper, index);
        Assert.assertSame(metric, ParamFlowSlot.getParamMetric(resourceWrapper));
    }
}

