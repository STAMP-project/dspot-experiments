package org.apereo.cas.ticket.accesstoken;


import lombok.val;
import org.apereo.cas.ticket.BaseOAuthExpirationPolicyTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@TestPropertySource(properties = "cas.logout.removeDescendantTickets=false")
public class OAuthAccessTokenSovereignExpirationPolicyTests extends BaseOAuthExpirationPolicyTests {
    @Test
    public void verifyAccessTokenExpiryWhenTgtIsExpired() {
        val tgt = BaseOAuthExpirationPolicyTests.newTicketGrantingTicket();
        val at = newAccessToken(tgt);
        Assertions.assertFalse(at.isExpired(), "Access token must not be expired");
        tgt.markTicketExpired();
        Assertions.assertFalse(at.isExpired(), "Access token must not be expired when TGT is expired");
    }
}

