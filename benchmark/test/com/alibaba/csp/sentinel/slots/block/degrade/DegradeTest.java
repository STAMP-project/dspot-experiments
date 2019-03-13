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
package com.alibaba.csp.sentinel.slots.block.degrade;


import RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT;
import RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author jialiang.linjl
 */
public class DegradeTest {
    @Test
    public void testAverageRtDegrade() throws InterruptedException {
        String key = "test_degrade_average_rt";
        ClusterNode cn = Mockito.mock(ClusterNode.class);
        ClusterBuilderSlot.getClusterNodeMap().put(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(key, EntryType.IN), cn);
        Context context = Mockito.mock(Context.class);
        DefaultNode node = Mockito.mock(DefaultNode.class);
        Mockito.when(node.getClusterNode()).thenReturn(cn);
        Mockito.when(cn.avgRt()).thenReturn(2L);
        DegradeRule rule = new DegradeRule();
        rule.setCount(1);
        rule.setResource(key);
        rule.setTimeWindow(2);
        for (int i = 0; i < 4; i++) {
            Assert.assertTrue(rule.passCheck(context, node, 1));
        }
        // The third time will fail.
        Assert.assertFalse(rule.passCheck(context, node, 1));
        Assert.assertFalse(rule.passCheck(context, node, 1));
        // Restore.
        TimeUnit.MILLISECONDS.sleep(2200);
        Assert.assertTrue(rule.passCheck(context, node, 1));
    }

    @Test
    public void testExceptionRatioModeDegrade() throws Throwable {
        String key = "test_degrade_exception_ratio";
        ClusterNode cn = Mockito.mock(ClusterNode.class);
        Mockito.when(cn.exceptionQps()).thenReturn(2L);
        // Indicates that there are QPS more than min threshold.
        Mockito.when(cn.totalQps()).thenReturn(12L);
        ClusterBuilderSlot.getClusterNodeMap().put(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(key, EntryType.IN), cn);
        Context context = Mockito.mock(Context.class);
        DefaultNode node = Mockito.mock(DefaultNode.class);
        Mockito.when(node.getClusterNode()).thenReturn(cn);
        DegradeRule rule = new DegradeRule();
        rule.setCount(0.15);
        rule.setResource(key);
        rule.setTimeWindow(2);
        rule.setGrade(DEGRADE_GRADE_EXCEPTION_RATIO);
        Mockito.when(cn.successQps()).thenReturn(8L);
        // Will fail.
        Assert.assertFalse(rule.passCheck(context, node, 1));
        // Restore from the degrade timeout.
        TimeUnit.MILLISECONDS.sleep(2200);
        Mockito.when(cn.successQps()).thenReturn(20L);
        // Will pass.
        Assert.assertTrue(rule.passCheck(context, node, 1));
    }

    @Test
    public void testExceptionCountModeDegrade() throws Throwable {
        String key = "test_degrade_exception_count";
        ClusterNode cn = Mockito.mock(ClusterNode.class);
        Mockito.when(cn.totalException()).thenReturn(10L);
        ClusterBuilderSlot.getClusterNodeMap().put(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(key, EntryType.IN), cn);
        Context context = Mockito.mock(Context.class);
        DefaultNode node = Mockito.mock(DefaultNode.class);
        Mockito.when(node.getClusterNode()).thenReturn(cn);
        DegradeRule rule = new DegradeRule();
        rule.setCount(4);
        rule.setResource(key);
        rule.setTimeWindow(2);
        rule.setGrade(DEGRADE_GRADE_EXCEPTION_COUNT);
        Mockito.when(cn.totalException()).thenReturn(4L);
        // Will fail.
        Assert.assertFalse(rule.passCheck(context, node, 1));
        // Restore from the degrade timeout.
        TimeUnit.MILLISECONDS.sleep(2200);
        Mockito.when(cn.totalException()).thenReturn(0L);
        // Will pass.
        Assert.assertTrue(rule.passCheck(context, node, 1));
    }
}

