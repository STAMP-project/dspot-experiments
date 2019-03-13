package com.alibaba.csp.sentinel.slots.block.authority;


import RuleConstant.AUTHORITY_BLACK;
import RuleConstant.AUTHORITY_WHITE;
import com.alibaba.csp.sentinel.context.ContextUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link AuthorityRuleChecker}.
 *
 * @author Eric Zhao
 */
public class AuthorityRuleCheckerTest {
    @Test
    public void testPassCheck() {
        String origin = "appA";
        ContextUtil.enter("entrance", origin);
        try {
            String resourceName = "testPassCheck";
            AuthorityRule ruleA = new AuthorityRule().setResource(resourceName).setLimitApp((origin + ",appB")).as(AuthorityRule.class).setStrategy(AUTHORITY_WHITE);
            AuthorityRule ruleB = new AuthorityRule().setResource(resourceName).setLimitApp("appB").as(AuthorityRule.class).setStrategy(AUTHORITY_WHITE);
            AuthorityRule ruleC = new AuthorityRule().setResource(resourceName).setLimitApp(origin).as(AuthorityRule.class).setStrategy(AUTHORITY_BLACK);
            AuthorityRule ruleD = new AuthorityRule().setResource(resourceName).setLimitApp("appC").as(AuthorityRule.class).setStrategy(AUTHORITY_BLACK);
            Assert.assertTrue(AuthorityRuleChecker.passCheck(ruleA, ContextUtil.getContext()));
            Assert.assertFalse(AuthorityRuleChecker.passCheck(ruleB, ContextUtil.getContext()));
            Assert.assertFalse(AuthorityRuleChecker.passCheck(ruleC, ContextUtil.getContext()));
            Assert.assertTrue(AuthorityRuleChecker.passCheck(ruleD, ContextUtil.getContext()));
        } finally {
            ContextUtil.exit();
        }
    }
}

