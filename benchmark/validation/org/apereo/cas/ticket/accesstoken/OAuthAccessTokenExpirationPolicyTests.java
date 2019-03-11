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
@TestPropertySource(properties = "cas.logout.removeDescendantTickets=true")
public class OAuthAccessTokenExpirationPolicyTests extends BaseOAuthExpirationPolicyTests {
    @Test
    public void verifyAccessTokenExpiryWhenTgtIsExpired() {
        val tgt = BaseOAuthExpirationPolicyTests.newTicketGrantingTicket();
        val at = newAccessToken(tgt);
        Assertions.assertFalse(at.isExpired(), "Access token should not be expired");
        tgt.markTicketExpired();
        Assertions.assertTrue(at.isExpired(), "Access token should not be expired when TGT is expired");
    }

    @Test
    public void verifySerializeAnOAuthAccessTokenExpirationPolicyToJson() throws Exception {
        val policyWritten = new OAuthAccessTokenExpirationPolicy(1234L, 5678L);
        BaseOAuthExpirationPolicyTests.MAPPER.writeValue(BaseOAuthExpirationPolicyTests.JSON_FILE, policyWritten);
        val policyRead = BaseOAuthExpirationPolicyTests.MAPPER.readValue(BaseOAuthExpirationPolicyTests.JSON_FILE, OAuthAccessTokenExpirationPolicy.class);
        Assertions.assertEquals(policyWritten, policyRead);
    }
}

