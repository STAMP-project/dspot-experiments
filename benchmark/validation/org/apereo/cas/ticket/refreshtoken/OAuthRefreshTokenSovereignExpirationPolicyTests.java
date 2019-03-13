package org.apereo.cas.ticket.refreshtoken;


import lombok.val;
import org.apereo.cas.ticket.BaseOAuthExpirationPolicyTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link OAuthRefreshTokenSovereignExpirationPolicyTests}.
 *
 * @since 5.3.0
 */
@TestPropertySource(properties = "cas.logout.removeDescendantTickets=false")
public class OAuthRefreshTokenSovereignExpirationPolicyTests extends BaseOAuthExpirationPolicyTests {
    @Test
    public void verifyRefreshTokenExpiryWhenTgtIsExpired() {
        val tgt = BaseOAuthExpirationPolicyTests.newTicketGrantingTicket();
        val at = newAccessToken(tgt);
        val rt = newRefreshToken(at);
        Assertions.assertFalse(rt.isExpired(), "Refresh token should not be expired");
        tgt.markTicketExpired();
        Assertions.assertFalse(rt.isExpired(), "Refresh token must not expired when TGT is expired");
    }
}

