package org.apereo.cas.ticket.proxy.support;


import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.proxy.ProxyHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class Cas10ProxyHandlerTests {
    private final ProxyHandler proxyHandler = new Cas10ProxyHandler();

    @Test
    public void verifyNoCredentialsOrProxy() {
        Assertions.assertNull(this.proxyHandler.handle(null, null));
    }

    @Test
    public void verifyCredentialsAndProxy() {
        val proxyGrantingTicket = Mockito.mock(TicketGrantingTicket.class);
        Mockito.when(proxyGrantingTicket.getId()).thenReturn("proxyGrantingTicket");
        Assertions.assertNull(this.proxyHandler.handle(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), proxyGrantingTicket));
    }
}

