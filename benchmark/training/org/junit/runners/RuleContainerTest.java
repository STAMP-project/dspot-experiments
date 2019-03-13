package org.junit.runners;


import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;


public class RuleContainerTest {
    private final RuleContainer container = new RuleContainer();

    @Test
    public void methodRulesOnly() {
        container.add(RuleContainerTest.MRule.M1);
        container.add(RuleContainerTest.MRule.M2);
        Assert.assertEquals("[M1, M2]", container.getSortedRules().toString());
        container.setOrder(RuleContainerTest.MRule.M2, 1);
        Assert.assertEquals("[M2, M1]", container.getSortedRules().toString());
    }

    @Test
    public void testRuleAroundMethodRule() {
        container.add(RuleContainerTest.MRule.M1);
        container.add(RuleContainerTest.Rule.A);
        Assert.assertEquals("[M1, A]", container.getSortedRules().toString());
    }

    @Test
    public void ordering1() {
        container.add(RuleContainerTest.MRule.M1);
        container.add(RuleContainerTest.Rule.A);
        container.setOrder(RuleContainerTest.Rule.A, 1);
        Assert.assertEquals("[A, M1]", container.getSortedRules().toString());
    }

    @Test
    public void ordering2() {
        container.add(RuleContainerTest.Rule.A);
        container.add(RuleContainerTest.Rule.B);
        container.add(RuleContainerTest.Rule.C);
        Assert.assertEquals("[A, B, C]", container.getSortedRules().toString());
        container.setOrder(RuleContainerTest.Rule.B, 1);
        container.setOrder(RuleContainerTest.Rule.C, 2);
        Assert.assertEquals("[C, B, A]", container.getSortedRules().toString());
    }

    private enum Rule implements TestRule {

        A,
        B,
        C;
        public Statement apply(Statement base, Description description) {
            return base;
        }
    }

    private enum MRule implements MethodRule {

        M1,
        M2;
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            return base;
        }
    }
}

