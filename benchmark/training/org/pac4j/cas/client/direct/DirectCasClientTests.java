package org.pac4j.cas.client.direct;


import CasConfiguration.TICKET_PARAMETER;
import org.jasig.cas.client.validation.AssertionImpl;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * Tests the {@link DirectCasClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class DirectCasClientTests implements TestsConstants {
    @Test
    public void testInitOk() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final DirectCasClient client = new DirectCasClient(configuration);
        client.init();
    }

    @Test
    public void testInitMissingConfiguration() {
        final DirectCasClient client = new DirectCasClient();
        TestsHelper.expectException(() -> client.init(), TechnicalException.class, "configuration cannot be null");
    }

    @Test
    public void testInitGatewayForbidden() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setGateway(true);
        final DirectCasClient client = new DirectCasClient(configuration);
        TestsHelper.expectException(() -> client.init(), TechnicalException.class, "the DirectCasClient can not support gateway to avoid infinite loops");
    }

    @Test
    public void testNoTokenRedirectionExpected() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final DirectCasClient client = new DirectCasClient(configuration);
        final MockWebContext context = MockWebContext.create();
        context.setFullRequestURL(CALLBACK_URL);
        final HttpAction action = ((HttpAction) (TestsHelper.expectException(() -> client.getCredentials(context))));
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals(addParameter(LOGIN_URL, CasConfiguration.SERVICE_PARAMETER, CALLBACK_URL), getLocation());
    }

    @Test
    public void testTicketExistsValidationOccurs() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setDefaultTicketValidator(( ticket, service) -> {
            if ((TICKET.equals(ticket)) && (CALLBACK_URL.equals(service))) {
                return new <TICKET>AssertionImpl();
            }
            throw new TechnicalException("Bad ticket or service");
        });
        final DirectCasClient client = new DirectCasClient(configuration);
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

