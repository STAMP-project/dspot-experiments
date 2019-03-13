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


import RuleConstant.FLOW_GRADE_THREAD;
import com.alibaba.csp.sentinel.EntryType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link ParamFlowRuleManager}.
 *
 * @author Eric Zhao
 * @since 0.2.0
 */
public class ParamFlowRuleManagerTest {
    @Test
    public void testLoadHotParamRulesClearingUnusedMetrics() {
        final String resA = "resA";
        ParamFlowRule ruleA = new ParamFlowRule(resA).setCount(1).setParamIdx(0);
        ParamFlowRuleManager.loadRules(Collections.singletonList(ruleA));
        ParamFlowSlot.getMetricsMap().put(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resA, EntryType.IN), new ParameterMetric());
        Assert.assertNotNull(ParamFlowSlot.getHotParamMetricForName(resA));
        final String resB = "resB";
        ParamFlowRule ruleB = new ParamFlowRule(resB).setCount(2).setParamIdx(1);
        ParamFlowRuleManager.loadRules(Collections.singletonList(ruleB));
        Assert.assertNull("The unused hot param metric should be cleared", ParamFlowSlot.getHotParamMetricForName(resA));
    }

    @Test
    public void testLoadHotParamRulesAndGet() {
        final String resA = "abc";
        final String resB = "foo";
        final String resC = "baz";
        // Rule A to C is for resource A.
        // Rule A is invalid.
        ParamFlowRule ruleA = new ParamFlowRule(resA).setCount(10);
        ParamFlowRule ruleB = new ParamFlowRule(resA).setCount(28).setParamIdx(1);
        ParamFlowRule ruleC = new ParamFlowRule(resA).setCount(8).setParamIdx(1).setGrade(FLOW_GRADE_THREAD);
        // Rule D is for resource B.
        ParamFlowRule ruleD = new ParamFlowRule(resB).setCount(9).setParamIdx(0).setParamFlowItemList(Arrays.asList(ParamFlowItem.newItem(7L, 6), ParamFlowItem.newItem(9L, 4)));
        ParamFlowRuleManager.loadRules(Arrays.asList(ruleA, ruleB, ruleC, ruleD));
        // Test for ParamFlowRuleManager#hasRules
        Assert.assertTrue(ParamFlowRuleManager.hasRules(resA));
        Assert.assertTrue(ParamFlowRuleManager.hasRules(resB));
        Assert.assertFalse(ParamFlowRuleManager.hasRules(resC));
        // Test for ParamFlowRuleManager#getRulesOfResource
        List<ParamFlowRule> rulesForResA = ParamFlowRuleManager.getRulesOfResource(resA);
        Assert.assertEquals(2, rulesForResA.size());
        Assert.assertFalse(rulesForResA.contains(ruleA));
        Assert.assertTrue(rulesForResA.contains(ruleB));
        Assert.assertTrue(rulesForResA.contains(ruleC));
        List<ParamFlowRule> rulesForResB = ParamFlowRuleManager.getRulesOfResource(resB);
        Assert.assertEquals(1, rulesForResB.size());
        Assert.assertEquals(ruleD, rulesForResB.get(0));
        // Test for ParamFlowRuleManager#getRules
        List<ParamFlowRule> allRules = ParamFlowRuleManager.getRules();
        Assert.assertFalse(allRules.contains(ruleA));
        Assert.assertTrue(allRules.contains(ruleB));
        Assert.assertTrue(allRules.contains(ruleC));
        Assert.assertTrue(allRules.contains(ruleD));
    }
}

