package com.alibaba.csp.sentinel.slots.block.authority;


import RuleConstant.AUTHORITY_WHITE;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link AuthorityRuleManager}.
 *
 * @author Eric Zhao
 */
public class AuthorityRuleManagerTest {
    @Test
    public void testLoadRules() {
        String resourceName = "testLoadRules";
        AuthorityRule rule = new AuthorityRule();
        rule.setResource(resourceName);
        rule.setLimitApp("a,b");
        rule.setStrategy(AUTHORITY_WHITE);
        AuthorityRuleManager.loadRules(Collections.singletonList(rule));
        List<AuthorityRule> rules = AuthorityRuleManager.getRules();
        Assert.assertEquals(1, rules.size());
        Assert.assertEquals(rule, rules.get(0));
        AuthorityRuleManager.loadRules(Collections.singletonList(new AuthorityRule()));
        rules = AuthorityRuleManager.getRules();
        Assert.assertEquals(0, rules.size());
    }

    @Test
    public void testIsValidRule() {
        AuthorityRule ruleA = new AuthorityRule();
        AuthorityRule ruleB = null;
        AuthorityRule ruleC = new AuthorityRule();
        ruleC.setResource("abc");
        AuthorityRule ruleD = new AuthorityRule();
        ruleD.setResource("bcd").setLimitApp("abc");
        Assert.assertFalse(AuthorityRuleManager.isValidRule(ruleA));
        Assert.assertFalse(AuthorityRuleManager.isValidRule(ruleB));
        Assert.assertFalse(AuthorityRuleManager.isValidRule(ruleC));
        Assert.assertTrue(AuthorityRuleManager.isValidRule(ruleD));
    }
}

