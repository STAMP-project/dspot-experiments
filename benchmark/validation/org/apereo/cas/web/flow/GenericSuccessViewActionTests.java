package org.apereo.cas.web.flow;


import lombok.val;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.NullPrincipal;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.InvalidTicketException;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link GenericSuccessViewAction}
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class GenericSuccessViewActionTests {
    @Test
    public void verifyValidPrincipal() throws InvalidTicketException {
        val cas = Mockito.mock(CentralAuthenticationService.class);
        val mgr = Mockito.mock(ServicesManager.class);
        val factory = Mockito.mock(ServiceFactory.class);
        val authn = Mockito.mock(Authentication.class);
        Mockito.when(authn.getPrincipal()).thenReturn(CoreAuthenticationTestUtils.getPrincipal("cas"));
        val tgt = Mockito.mock(TicketGrantingTicket.class);
        Mockito.when(tgt.getAuthentication()).thenReturn(authn);
        Mockito.when(cas.getTicket(ArgumentMatchers.any(String.class), ArgumentMatchers.any())).thenReturn(tgt);
        val action = new org.apereo.cas.web.flow.login.GenericSuccessViewAction(cas, mgr, factory, "");
        val p = action.getAuthenticationPrincipal("TGT-1");
        Assertions.assertNotNull(p);
        Assertions.assertEquals("cas", p.getId());
    }

    @Test
    public void verifyPrincipalCanNotBeDetermined() throws InvalidTicketException {
        val cas = Mockito.mock(CentralAuthenticationService.class);
        val mgr = Mockito.mock(ServicesManager.class);
        val factory = Mockito.mock(ServiceFactory.class);
        Mockito.when(cas.getTicket(ArgumentMatchers.any(String.class), ArgumentMatchers.any())).thenThrow(new InvalidTicketException("TGT-1"));
        val action = new org.apereo.cas.web.flow.login.GenericSuccessViewAction(cas, mgr, factory, "");
        val p = action.getAuthenticationPrincipal("TGT-1");
        Assertions.assertNotNull(p);
        Assertions.assertTrue((p instanceof NullPrincipal));
    }
}

