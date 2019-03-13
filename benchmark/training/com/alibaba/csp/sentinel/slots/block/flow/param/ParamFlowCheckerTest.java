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
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test cases for {@link ParamFlowChecker}.
 *
 * @author Eric Zhao
 */
public class ParamFlowCheckerTest {
    @Test
    public void testHotParamCheckerPassCheckExceedArgs() {
        final String resourceName = "testHotParamCheckerPassCheckExceedArgs";
        final ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        int paramIdx = 1;
        ParamFlowRule rule = new ParamFlowRule();
        rule.setResource(resourceName);
        rule.setCount(10);
        rule.setParamIdx(paramIdx);
        Assert.assertTrue("The rule will pass if the paramIdx exceeds provided args", ParamFlowChecker.passCheck(resourceWrapper, rule, 1, "abc"));
    }

    @Test
    public void testSingleValueCheckQpsWithoutExceptionItems() {
        final String resourceName = "testSingleValueCheckQpsWithoutExceptionItems";
        final ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        int paramIdx = 0;
        long threshold = 5L;
        ParamFlowRule rule = new ParamFlowRule();
        rule.setResource(resourceName);
        rule.setCount(threshold);
        rule.setParamIdx(paramIdx);
        String valueA = "valueA";
        String valueB = "valueB";
        ParameterMetric metric = Mockito.mock(ParameterMetric.class);
        Mockito.when(metric.getPassParamQps(paramIdx, valueA)).thenReturn((((double) (threshold)) - 1));
        Mockito.when(metric.getPassParamQps(paramIdx, valueB)).thenReturn((((double) (threshold)) + 1));
        ParamFlowSlot.getMetricsMap().put(resourceWrapper, metric);
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueA));
        Assert.assertFalse(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueB));
    }

    @Test
    public void testSingleValueCheckQpsWithExceptionItems() {
        final String resourceName = "testSingleValueCheckQpsWithExceptionItems";
        final ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        int paramIdx = 0;
        long globalThreshold = 5L;
        int thresholdB = 3;
        int thresholdD = 7;
        ParamFlowRule rule = new ParamFlowRule();
        rule.setResource(resourceName);
        rule.setCount(globalThreshold);
        rule.setParamIdx(paramIdx);
        String valueA = "valueA";
        String valueB = "valueB";
        String valueC = "valueC";
        String valueD = "valueD";
        // Directly set parsed map for test.
        Map<Object, Integer> map = new HashMap<Object, Integer>();
        map.put(valueB, thresholdB);
        map.put(valueD, thresholdD);
        rule.setParsedHotItems(map);
        ParameterMetric metric = Mockito.mock(ParameterMetric.class);
        Mockito.when(metric.getPassParamQps(paramIdx, valueA)).thenReturn((((double) (globalThreshold)) - 1));
        Mockito.when(metric.getPassParamQps(paramIdx, valueB)).thenReturn((((double) (globalThreshold)) - 1));
        Mockito.when(metric.getPassParamQps(paramIdx, valueC)).thenReturn((((double) (globalThreshold)) - 1));
        Mockito.when(metric.getPassParamQps(paramIdx, valueD)).thenReturn((((double) (globalThreshold)) + 1));
        ParamFlowSlot.getMetricsMap().put(resourceWrapper, metric);
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueA));
        Assert.assertFalse(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueB));
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueC));
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueD));
        Mockito.when(metric.getPassParamQps(paramIdx, valueA)).thenReturn(((double) (globalThreshold)));
        Mockito.when(metric.getPassParamQps(paramIdx, valueB)).thenReturn((((double) (thresholdB)) - 1L));
        Mockito.when(metric.getPassParamQps(paramIdx, valueC)).thenReturn((((double) (globalThreshold)) + 1));
        Mockito.when(metric.getPassParamQps(paramIdx, valueD)).thenReturn((((double) (globalThreshold)) - 1)).thenReturn(((double) (thresholdD)));
        Assert.assertFalse(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueA));
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueB));
        Assert.assertFalse(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueC));
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueD));
        Assert.assertFalse(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueD));
    }

    @Test
    public void testSingleValueCheckThreadCountWithExceptionItems() {
        final String resourceName = "testSingleValueCheckThreadCountWithExceptionItems";
        final ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        int paramIdx = 0;
        long globalThreshold = 5L;
        int thresholdB = 3;
        int thresholdD = 7;
        ParamFlowRule rule = new ParamFlowRule(resourceName).setCount(globalThreshold).setParamIdx(paramIdx).setGrade(FLOW_GRADE_THREAD);
        String valueA = "valueA";
        String valueB = "valueB";
        String valueC = "valueC";
        String valueD = "valueD";
        // Directly set parsed map for test.
        Map<Object, Integer> map = new HashMap<Object, Integer>();
        map.put(valueB, thresholdB);
        map.put(valueD, thresholdD);
        rule.setParsedHotItems(map);
        ParameterMetric metric = Mockito.mock(ParameterMetric.class);
        Mockito.when(metric.getThreadCount(paramIdx, valueA)).thenReturn((globalThreshold - 1));
        Mockito.when(metric.getThreadCount(paramIdx, valueB)).thenReturn((globalThreshold - 1));
        Mockito.when(metric.getThreadCount(paramIdx, valueC)).thenReturn((globalThreshold - 1));
        Mockito.when(metric.getThreadCount(paramIdx, valueD)).thenReturn((globalThreshold + 1));
        ParamFlowSlot.getMetricsMap().put(resourceWrapper, metric);
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueA));
        Assert.assertFalse(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueB));
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueC));
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueD));
        Mockito.when(metric.getThreadCount(paramIdx, valueA)).thenReturn(globalThreshold);
        Mockito.when(metric.getThreadCount(paramIdx, valueB)).thenReturn((thresholdB - 1L));
        Mockito.when(metric.getThreadCount(paramIdx, valueC)).thenReturn((globalThreshold + 1));
        Mockito.when(metric.getThreadCount(paramIdx, valueD)).thenReturn((globalThreshold - 1)).thenReturn(((long) (thresholdD)));
        Assert.assertFalse(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueA));
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueB));
        Assert.assertFalse(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueC));
        Assert.assertTrue(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueD));
        Assert.assertFalse(ParamFlowChecker.passSingleValueCheck(resourceWrapper, rule, 1, valueD));
    }

    @Test
    public void testPassLocalCheckForCollection() {
        final String resourceName = "testPassLocalCheckForCollection";
        final ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        int paramIdx = 0;
        double globalThreshold = 10;
        ParamFlowRule rule = new ParamFlowRule(resourceName).setParamIdx(paramIdx).setCount(globalThreshold);
        String v1 = "a";
        String v2 = "B";
        String v3 = "Cc";
        List<String> list = Arrays.asList(v1, v2, v3);
        ParameterMetric metric = Mockito.mock(ParameterMetric.class);
        Mockito.when(metric.getPassParamQps(paramIdx, v1)).thenReturn((globalThreshold - 2)).thenReturn((globalThreshold - 1));
        Mockito.when(metric.getPassParamQps(paramIdx, v2)).thenReturn((globalThreshold - 2)).thenReturn((globalThreshold - 1));
        Mockito.when(metric.getPassParamQps(paramIdx, v3)).thenReturn((globalThreshold - 1)).thenReturn(globalThreshold);
        ParamFlowSlot.getMetricsMap().put(resourceWrapper, metric);
        Assert.assertTrue(ParamFlowChecker.passCheck(resourceWrapper, rule, 1, list));
        Assert.assertFalse(ParamFlowChecker.passCheck(resourceWrapper, rule, 1, list));
    }

    @Test
    public void testPassLocalCheckForArray() {
        final String resourceName = "testPassLocalCheckForArray";
        final ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        int paramIdx = 0;
        double globalThreshold = 10;
        ParamFlowRule rule = new ParamFlowRule(resourceName).setParamIdx(paramIdx).setCount(globalThreshold);
        String v1 = "a";
        String v2 = "B";
        String v3 = "Cc";
        Object arr = new String[]{ v1, v2, v3 };
        ParameterMetric metric = Mockito.mock(ParameterMetric.class);
        Mockito.when(metric.getPassParamQps(paramIdx, v1)).thenReturn((globalThreshold - 2)).thenReturn((globalThreshold - 1));
        Mockito.when(metric.getPassParamQps(paramIdx, v2)).thenReturn((globalThreshold - 2)).thenReturn((globalThreshold - 1));
        Mockito.when(metric.getPassParamQps(paramIdx, v3)).thenReturn((globalThreshold - 1)).thenReturn(globalThreshold);
        ParamFlowSlot.getMetricsMap().put(resourceWrapper, metric);
        Assert.assertTrue(ParamFlowChecker.passCheck(resourceWrapper, rule, 1, arr));
        Assert.assertFalse(ParamFlowChecker.passCheck(resourceWrapper, rule, 1, arr));
    }
}

