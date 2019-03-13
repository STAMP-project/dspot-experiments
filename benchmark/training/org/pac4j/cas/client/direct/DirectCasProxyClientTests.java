package org.pac4j.cas.client.direct;


import CasConfiguration.TICKET_PARAMETER;
import CasProtocol.CAS20_PROXY;
import CasProtocol.CAS30_PROXY;
import org.jasig.cas.client.validation.AssertionImpl;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * Tests the {@link DirectCasProxyClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class DirectCasProxyClientTests implements TestsConstants {
    @Test
    public void testInitOk() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setProtocol(CAS20_PROXY);
        final DirectCasProxyClient client = new DirectCasProxyClient(configuration, CALLBACK_URL);
        client.init();
    }

    @Test
    public void testInitMissingConfiguration() {
        final DirectCasProxyClient client = new DirectCasProxyClient();
        client.setServiceUrl(CALLBACK_URL);
        TestsHelper.expectException(() -> client.init(), TechnicalException.class, "configuration cannot be null");
    }

    @Test
    public void testInitMissingServiceUrl() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final DirectCasProxyClient client = new DirectCasProxyClient();
        client.setConfiguration(configuration);
        TestsHelper.expectException(() -> client.init(), TechnicalException.class, "serviceUrl cannot be blank");
    }

    @Test
    public void testInitFailsBadProtocol() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final DirectCasProxyClient client = new DirectCasProxyClient(configuration, CALLBACK_URL);
        TestsHelper.expectException(() -> client.init(), TechnicalException.class, "The DirectCasProxyClient must be configured with a CAS proxy protocol (CAS20_PROXY or CAS30_PROXY)");
    }

    @Test
    public void testNoTicket() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setProtocol(CAS20_PROXY);
        final DirectCasProxyClient client = new DirectCasProxyClient(configuration, CALLBACK_URL);
        Assert.assertFalse(client.getCredentials(MockWebContext.create()).isPresent());
    }

    @Test
    public void testTokenExistsValidationOccurs() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setProtocol(CAS30_PROXY);
        configuration.setDefaultTicketValidator(( ticket, service) -> {
            if ((TICKET.equals(ticket)) && (CALLBACK_URL.equals(service))) {
                return new <TICKET>AssertionImpl();
            }
            throw new TechnicalException("Bad ticket or service");
        });
        final DirectCasProxyClient client = new DirectCasProxyClient(configuration, CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        context.setFullRequestURL((((((CALLBACK_URL) + "?") + (CasConfiguration.TICKET_PARAMETER)) + "=") + (TICKET)));
        context.addRequestParameter(TICKET_PARAMETER, TICKET);
        final TokenCredentials credentials = client.getCredentials(context).get();
        Assert.assertEquals(TICKET, credentials.getToken());
        final CommonProfile profile = credentials.getUserProfile();
        Assert.assertTrue((profile instanceof CasProfile));
        Assert.assertEquals(TICKET, profile.getId());
    }
}

