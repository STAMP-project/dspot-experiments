package org.apereo.cas.web.flow;


import CasWebflowConstants.TRANSITION_ID_AUP_MUST_ACCEPT;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link AcceptableUsagePolicyVerifyActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@DirtiesContext
public class AcceptableUsagePolicyVerifyActionTests extends BaseAcceptableUsagePolicyActionTests {
    @Autowired
    @Qualifier("acceptableUsagePolicyVerifyAction")
    private Action acceptableUsagePolicyVerifyAction;

    @Test
    public void verifyAction() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putCredential(context, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        WebUtils.putTicketGrantingTicketInScopes(context, new MockTicketGrantingTicket("casuser"));
        WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication(), context);
        Assertions.assertEquals(TRANSITION_ID_AUP_MUST_ACCEPT, acceptableUsagePolicyVerifyAction.execute(context).getId());
    }
}

