package org.apereo.cas.support.oauth.web;


import lombok.val;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.ticket.refreshtoken.RefreshToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This class tests the {@link OAuth20AccessTokenEndpointController} class.
 *
 * @author Jerome Leleu
 * @since 3.5.2
 */
public class OAuth20RefreshTokenTests extends AbstractOAuth20Tests {
    @Test
    public void verifyTicketGrantingRemovalDoesNotRemoveAccessToken() throws Exception {
        val service = addRegisteredService();
        service.setGenerateRefreshToken(true);
        val result = assertClientOK(service, true);
        val at = this.ticketRegistry.getTicket(result.getKey(), AccessToken.class);
        Assertions.assertNotNull(at);
        Assertions.assertNotNull(at.getTicketGrantingTicket());
        this.ticketRegistry.deleteTicket(at.getTicketGrantingTicket().getId());
        val at2 = this.ticketRegistry.getTicket(at.getId(), AccessToken.class);
        Assertions.assertNotNull(at2);
        val rt = this.ticketRegistry.getTicket(result.getRight(), RefreshToken.class);
        Assertions.assertNotNull(rt);
        val result2 = assertRefreshTokenOk(service, rt, AbstractOAuth20Tests.createPrincipal());
        Assertions.assertNotNull(result2.getKey());
    }
}

