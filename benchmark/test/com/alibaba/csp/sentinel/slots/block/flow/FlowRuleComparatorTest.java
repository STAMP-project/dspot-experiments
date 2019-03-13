package com.alibaba.csp.sentinel.slots.block.flow;


import RuleConstant.LIMIT_APP_DEFAULT;
import RuleConstant.LIMIT_APP_OTHER;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author Eric Zhao
 */
public class FlowRuleComparatorTest {
    @Test
    public void testFlowRuleComparator() {
        FlowRule ruleA = new FlowRule("abc").setCount(10);
        ruleA.setLimitApp(LIMIT_APP_DEFAULT);
        FlowRule ruleB = new FlowRule("abc");
        ruleB.setLimitApp("originA");
        FlowRule ruleC = new FlowRule("abc");
        ruleC.setLimitApp("originB");
        FlowRule ruleD = new FlowRule("abc");
        ruleD.setLimitApp(LIMIT_APP_OTHER);
        FlowRule ruleE = new FlowRule("abc").setCount(20);
        ruleE.setLimitApp(LIMIT_APP_DEFAULT);
        List<FlowRule> list = Arrays.asList(ruleA, ruleB, ruleC, ruleD, ruleE);
        FlowRuleComparator comparator = new FlowRuleComparator();
        Collections.sort(list, comparator);
        List<FlowRule> expected = Arrays.asList(ruleB, ruleC, ruleD, ruleA, ruleE);
        assertOrderEqual(expected.size(), expected, list);
    }
}

