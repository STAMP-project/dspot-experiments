package org.apereo.cas.web.flow;


import CasWebflowConstants.TRANSITION_ID_TGT_INVALID;
import CasWebflowConstants.TRANSITION_ID_TGT_NOT_EXISTS;
import CasWebflowConstants.TRANSITION_ID_TGT_VALID;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.web.support.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.webflow.test.MockRequestContext;


/**
 * Handles tests for {@link TicketGrantingTicketCheckAction}.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class TicketGrantingTicketCheckActionTests extends AbstractWebflowActionsTests {
    @Test
    public void verifyNullTicket() throws Exception {
        val ctx = new MockRequestContext();
        val action = new org.apereo.cas.web.flow.login.TicketGrantingTicketCheckAction(getCentralAuthenticationService());
        val event = action.execute(ctx);
        Assertions.assertEquals(TRANSITION_ID_TGT_NOT_EXISTS, event.getId());
    }

    @Test
    public void verifyInvalidTicket() throws Exception {
        val ctx = new MockRequestContext();
        val tgt = new MockTicketGrantingTicket("user");
        WebUtils.putTicketGrantingTicketInScopes(ctx, tgt);
        val action = new org.apereo.cas.web.flow.login.TicketGrantingTicketCheckAction(getCentralAuthenticationService());
        val event = action.execute(ctx);
        Assertions.assertEquals(TRANSITION_ID_TGT_INVALID, event.getId());
    }

    @Test
    public void verifyValidTicket() throws Exception {
        val ctx = new MockRequestContext();
        val ctxAuthN = CoreAuthenticationTestUtils.getAuthenticationResult(getAuthenticationSystemSupport());
        val tgt = getCentralAuthenticationService().createTicketGrantingTicket(ctxAuthN);
        WebUtils.putTicketGrantingTicketInScopes(ctx, tgt);
        val action = new org.apereo.cas.web.flow.login.TicketGrantingTicketCheckAction(getCentralAuthenticationService());
        val event = action.execute(ctx);
        Assertions.assertEquals(TRANSITION_ID_TGT_VALID, event.getId());
    }
}

