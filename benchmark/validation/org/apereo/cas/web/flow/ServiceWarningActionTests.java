package org.apereo.cas.web.flow;


import CasWebflowConstants.STATE_ID_REDIRECT;
import ServiceWarningAction.PARAMETER_NAME_IGNORE_WARNING;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link ServiceWarningActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ServiceWarningActionTests extends AbstractWebflowActionsTests {
    @Autowired
    @Qualifier("serviceWarningAction")
    private Action action;

    private MockRequestContext context;

    @Test
    public void verifyAction() throws Exception {
        val request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_NAME_IGNORE_WARNING, "true");
        this.context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        WebUtils.putServiceIntoFlowScope(context, RegisteredServiceTestUtils.getService("https://google.com"));
        WebUtils.putCredential(context, CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        val tgt = new MockTicketGrantingTicket("casuser");
        getTicketRegistry().addTicket(tgt);
        WebUtils.putTicketGrantingTicketInScopes(this.context, tgt);
        Assertions.assertEquals(STATE_ID_REDIRECT, this.action.execute(this.context).getId());
    }
}

