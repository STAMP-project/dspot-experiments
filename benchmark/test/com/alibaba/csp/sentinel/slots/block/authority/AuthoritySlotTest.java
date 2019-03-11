package com.alibaba.csp.sentinel.slots.block.authority;


import RuleConstant.AUTHORITY_BLACK;
import RuleConstant.AUTHORITY_WHITE;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import java.util.Collections;
import org.junit.Test;


/**
 * Test cases for {@link AuthoritySlot}.
 *
 * @author Eric Zhao
 */
public class AuthoritySlotTest {
    private AuthoritySlot authoritySlot = new AuthoritySlot();

    @Test
    public void testCheckAuthorityNoExceptionItemsSuccess() throws Exception {
        String origin = "appA";
        String resourceName = "testCheckAuthorityNoExceptionItemsSuccess";
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        ContextUtil.enter("entrance", origin);
        try {
            AuthorityRule ruleA = new AuthorityRule().setResource(resourceName).setLimitApp((origin + ",appB")).as(AuthorityRule.class).setStrategy(AUTHORITY_WHITE);
            AuthorityRuleManager.loadRules(Collections.singletonList(ruleA));
            authoritySlot.checkBlackWhiteAuthority(resourceWrapper, ContextUtil.getContext());
            AuthorityRule ruleB = new AuthorityRule().setResource(resourceName).setLimitApp("appD").as(AuthorityRule.class).setStrategy(AUTHORITY_BLACK);
            AuthorityRuleManager.loadRules(Collections.singletonList(ruleB));
            authoritySlot.checkBlackWhiteAuthority(resourceWrapper, ContextUtil.getContext());
        } finally {
            ContextUtil.exit();
        }
    }

    @Test(expected = AuthorityException.class)
    public void testCheckAuthorityNoExceptionItemsBlackFail() throws Exception {
        String origin = "appA";
        String resourceName = "testCheckAuthorityNoExceptionItemsBlackFail";
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        ContextUtil.enter("entrance", origin);
        try {
            AuthorityRule ruleA = new AuthorityRule().setResource(resourceName).setLimitApp((origin + ",appC")).as(AuthorityRule.class).setStrategy(AUTHORITY_BLACK);
            AuthorityRuleManager.loadRules(Collections.singletonList(ruleA));
            authoritySlot.checkBlackWhiteAuthority(resourceWrapper, ContextUtil.getContext());
        } finally {
            ContextUtil.exit();
        }
    }

    @Test(expected = AuthorityException.class)
    public void testCheckAuthorityNoExceptionItemsWhiteFail() throws Exception {
        String origin = "appA";
        String resourceName = "testCheckAuthorityNoExceptionItemsWhiteFail";
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, EntryType.IN);
        ContextUtil.enter("entrance", origin);
        try {
            AuthorityRule ruleB = new AuthorityRule().setResource(resourceName).setLimitApp("appB, appE").as(AuthorityRule.class).setStrategy(AUTHORITY_WHITE);
            AuthorityRuleManager.loadRules(Collections.singletonList(ruleB));
            authoritySlot.checkBlackWhiteAuthority(resourceWrapper, ContextUtil.getContext());
        } finally {
            ContextUtil.exit();
        }
    }
}

