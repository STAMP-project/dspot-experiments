package org.apereo.cas.web.flow;


import lombok.val;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.web.flow.login.InitialFlowSetupAction;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link InitialFlowSetupActionCookieTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class InitialFlowSetupActionCookieTests extends AbstractWebflowActionsTests {
    private static final String CONST_CONTEXT_PATH = "/test";

    private static final String CONST_CONTEXT_PATH_2 = "/test1";

    @Autowired
    @Qualifier("authenticationServiceSelectionPlan")
    private AuthenticationServiceSelectionPlan authenticationRequestServiceSelectionStrategies;

    @Autowired
    @Qualifier("authenticationEventExecutionPlan")
    private AuthenticationEventExecutionPlan authenticationEventExecutionPlan;

    private InitialFlowSetupAction action;

    private CookieRetrievingCookieGenerator warnCookieGenerator;

    private CookieRetrievingCookieGenerator tgtCookieGenerator;

    @Test
    public void verifySettingContextPath() {
        val request = new MockHttpServletRequest();
        request.setContextPath(InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH);
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        this.action.doExecute(context);
        Assertions.assertEquals(((InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH) + '/'), this.warnCookieGenerator.getCookiePath());
        Assertions.assertEquals(((InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH) + '/'), this.tgtCookieGenerator.getCookiePath());
    }

    @Test
    public void verifyResettingContextPath() {
        val request = new MockHttpServletRequest();
        request.setContextPath(InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH);
        val context = new MockRequestContext();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        this.action.doExecute(context);
        Assertions.assertEquals(((InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH) + '/'), this.warnCookieGenerator.getCookiePath());
        Assertions.assertEquals(((InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH) + '/'), this.tgtCookieGenerator.getCookiePath());
        request.setContextPath(InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH_2);
        this.action.doExecute(context);
        Assertions.assertNotSame(((InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH_2) + '/'), this.warnCookieGenerator.getCookiePath());
        Assertions.assertNotSame(((InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH_2) + '/'), this.tgtCookieGenerator.getCookiePath());
        Assertions.assertEquals(((InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH) + '/'), this.warnCookieGenerator.getCookiePath());
        Assertions.assertEquals(((InitialFlowSetupActionCookieTests.CONST_CONTEXT_PATH) + '/'), this.tgtCookieGenerator.getCookiePath());
    }
}

