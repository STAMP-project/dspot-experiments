package org.apereo.cas.ticket.proxy.support;


import java.net.URL;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.DefaultUniqueTicketIdGenerator;
import org.apereo.cas.util.http.SimpleHttpClientFactoryBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class Cas20ProxyHandlerTests {
    private Cas20ProxyHandler handler;

    @Mock
    private TicketGrantingTicket proxyGrantingTicket;

    public Cas20ProxyHandlerTests() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void verifyValidProxyTicketWithoutQueryString() throws Exception {
        Assertions.assertNotNull(this.handler.handle(new org.apereo.cas.authentication.credential.HttpBasedServiceCredential(new URL("https://www.google.com/"), CoreAuthenticationTestUtils.getRegisteredService("https://some.app.edu")), proxyGrantingTicket));
    }

    @Test
    public void verifyValidProxyTicketWithQueryString() throws Exception {
        Assertions.assertNotNull(this.handler.handle(new org.apereo.cas.authentication.credential.HttpBasedServiceCredential(new URL("https://www.google.com/?test=test"), CoreAuthenticationTestUtils.getRegisteredService("https://some.app.edu")), proxyGrantingTicket));
    }

    @Test
    public void verifyNonValidProxyTicket() throws Exception {
        val clientFactory = new SimpleHttpClientFactoryBean();
        clientFactory.setAcceptableCodes(CollectionUtils.wrapList(900));
        this.handler = new Cas20ProxyHandler(clientFactory.getObject(), new DefaultUniqueTicketIdGenerator());
        Assertions.assertNull(this.handler.handle(new org.apereo.cas.authentication.credential.HttpBasedServiceCredential(new URL("http://www.rutgers.edu"), CoreAuthenticationTestUtils.getRegisteredService("https://some.app.edu")), proxyGrantingTicket));
    }
}

