package org.apereo.cas.support.oauth.authenticator;


import OAuth20Constants.CODE;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import lombok.val;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.support.HardTimeoutExpirationPolicy;
import org.apereo.cas.util.DigestUtils;
import org.apereo.cas.util.EncodingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link OAuth20ProofKeyCodeExchangeAuthenticatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class OAuth20ProofKeyCodeExchangeAuthenticatorTests extends BaseOAuth20AuthenticatorTests {
    protected OAuth20ProofKeyCodeExchangeAuthenticator authenticator;

    @Test
    public void verifyAuthenticationPlain() {
        val credentials = new UsernamePasswordCredentials("client", "ABCD123");
        val request = new MockHttpServletRequest();
        ticketRegistry.addTicket(new org.apereo.cas.ticket.code.OAuthCodeImpl("CODE-1234567890", RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getAuthentication(), new HardTimeoutExpirationPolicy(10), new MockTicketGrantingTicket("casuser"), new ArrayList(), "ABCD123", "plain"));
        request.addParameter(CODE, "CODE-1234567890");
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        authenticator.validate(credentials, ctx);
        Assertions.assertNotNull(credentials.getUserProfile());
        Assertions.assertEquals("client", credentials.getUserProfile().getId());
    }

    @Test
    public void verifyAuthenticationHashed() {
        val hash = EncodingUtils.encodeUrlSafeBase64(DigestUtils.sha256("ABCD1234").getBytes(StandardCharsets.UTF_8));
        val credentials = new UsernamePasswordCredentials("client", "ABCD1234");
        val request = new MockHttpServletRequest();
        val ticket = new org.apereo.cas.ticket.code.OAuthCodeImpl("CODE-1234567890", RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getAuthentication(), new HardTimeoutExpirationPolicy(10), new MockTicketGrantingTicket("casuser"), new ArrayList(), hash, "s256");
        ticketRegistry.addTicket(ticket);
        request.addParameter(CODE, ticket.getId());
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        authenticator.validate(credentials, ctx);
        Assertions.assertNotNull(credentials.getUserProfile());
        Assertions.assertEquals("client", credentials.getUserProfile().getId());
    }

    @Test
    public void verifyAuthenticationNotHashedCorrectly() {
        val credentials = new UsernamePasswordCredentials("client", "ABCD1234");
        val request = new MockHttpServletRequest();
        val ticket = new org.apereo.cas.ticket.code.OAuthCodeImpl("CODE-1234567890", RegisteredServiceTestUtils.getService(), RegisteredServiceTestUtils.getAuthentication(), new HardTimeoutExpirationPolicy(10), new MockTicketGrantingTicket("casuser"), new ArrayList(), "something-else", "s256");
        ticketRegistry.addTicket(ticket);
        request.addParameter(CODE, ticket.getId());
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        Assertions.assertThrows(CredentialsException.class, () -> authenticator.validate(credentials, ctx));
    }
}

