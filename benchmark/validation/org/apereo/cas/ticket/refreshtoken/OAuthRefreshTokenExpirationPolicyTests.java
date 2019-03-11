package org.apereo.cas.ticket.refreshtoken;


import lombok.val;
import org.apereo.cas.ticket.BaseOAuthExpirationPolicyTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link OAuthRefreshTokenExpirationPolicyTests}.
 *
 * @since 5.3.0
 */
@TestPropertySource(properties = "cas.logout.removeDescendantTickets=true")
public class OAuthRefreshTokenExpirationPolicyTests extends BaseOAuthExpirationPolicyTests {
    @Test
    public void verifyRefreshTokenExpiryWhenTgtIsExpired() {
        val tgt = BaseOAuthExpirationPolicyTests.newTicketGrantingTicket();
        val at = newAccessToken(tgt);
        val rt = newRefreshToken(at);
        Assertions.assertFalse(rt.isExpired(), "Refresh token should not be expired");
        tgt.markTicketExpired();
        Assertions.assertTrue(rt.isExpired(), "Refresh token should not be expired when TGT is expired");
    }

    @Test
    public void verifySerializeAnOAuthRefreshTokenExpirationPolicyToJson() throws Exception {
        val policyWritten = new OAuthRefreshTokenExpirationPolicy(1234L);
        BaseOAuthExpirationPolicyTests.MAPPER.writeValue(BaseOAuthExpirationPolicyTests.JSON_FILE, policyWritten);
        val policyRead = BaseOAuthExpirationPolicyTests.MAPPER.readValue(BaseOAuthExpirationPolicyTests.JSON_FILE, OAuthRefreshTokenExpirationPolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }
}

