package org.apereo.cas.support.openid.authentication.handler.support;


import javax.security.auth.login.FailedLoginException;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.support.openid.AbstractOpenIdTests;
import org.apereo.cas.support.openid.authentication.principal.OpenIdCredential;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.1
 */
public class OpenIdCredentialsAuthenticationHandlerTests extends AbstractOpenIdTests {
    private static final String TGT_ID = "test";

    private static final String USERNAME = "test";

    @Autowired
    @Qualifier("openIdCredentialsAuthenticationHandler")
    private AuthenticationHandler openIdCredentialsAuthenticationHandler;

    @Autowired
    @Qualifier("ticketRegistry")
    private TicketRegistry ticketRegistry;

    @Test
    public void verifySupports() {
        Assertions.assertTrue(this.openIdCredentialsAuthenticationHandler.supports(new OpenIdCredential(OpenIdCredentialsAuthenticationHandlerTests.TGT_ID, OpenIdCredentialsAuthenticationHandlerTests.USERNAME)));
        Assertions.assertFalse(this.openIdCredentialsAuthenticationHandler.supports(new UsernamePasswordCredential()));
    }

    @Test
    @SneakyThrows
    public void verifyTGTWithSameId() {
        val c = new OpenIdCredential(OpenIdCredentialsAuthenticationHandlerTests.TGT_ID, OpenIdCredentialsAuthenticationHandlerTests.USERNAME);
        val t = OpenIdCredentialsAuthenticationHandlerTests.getTicketGrantingTicket();
        this.ticketRegistry.addTicket(t);
        Assertions.assertEquals(OpenIdCredentialsAuthenticationHandlerTests.TGT_ID, this.openIdCredentialsAuthenticationHandler.authenticate(c).getPrincipal().getId());
    }

    @Test
    public void verifyTGTThatIsExpired() {
        val c = new OpenIdCredential(OpenIdCredentialsAuthenticationHandlerTests.TGT_ID, OpenIdCredentialsAuthenticationHandlerTests.USERNAME);
        val t = OpenIdCredentialsAuthenticationHandlerTests.getTicketGrantingTicket();
        this.ticketRegistry.addTicket(t);
        t.markTicketExpired();
        this.ticketRegistry.updateTicket(t);
        Assertions.assertThrows(FailedLoginException.class, () -> this.openIdCredentialsAuthenticationHandler.authenticate(c));
    }

    @Test
    public void verifyTGTWithDifferentId() {
        val c = new OpenIdCredential(OpenIdCredentialsAuthenticationHandlerTests.TGT_ID, "test1");
        val t = OpenIdCredentialsAuthenticationHandlerTests.getTicketGrantingTicket();
        this.ticketRegistry.addTicket(t);
        Assertions.assertThrows(FailedLoginException.class, () -> this.openIdCredentialsAuthenticationHandler.authenticate(c));
    }
}

