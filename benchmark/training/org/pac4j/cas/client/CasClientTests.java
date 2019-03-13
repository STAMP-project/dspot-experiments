package org.pac4j.cas.client;


import CasConfiguration.LOGOUT_REQUEST_PARAMETER;
import CasConfiguration.RELAY_STATE_PARAMETER;
import HTTP_METHOD.GET;
import HTTP_METHOD.POST;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * This class tests the {@link CasClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CasClientTests implements TestsConstants {
    private static final String CAS = "/cas";

    private static final String CASBACK = "/casback";

    private static final String HOST = "protocol://myHost";

    private static final String LOGIN = "/login";

    private static final String PREFIX_URL = "http://myserver/";

    private static final String PREFIX_URL_WITHOUT_SLASH = "http://myserver";

    private static final String LOGOUT_MESSAGE = (("\"<samlp:LogoutRequest xmlns:samlp=\\\"urn:oasis:names:tc:SAML:2.0:protocol\\\"" + ("ID=\\\"LR-1-B2b0CVRW5eSvPBZPsAVXdNPj7jee4SWjr9y\\\" Version=\\\"2.0\\\" IssueInstant=\\\"2012-12-19T15:30:55Z\\\">" + "<saml:NameID xmlns:saml=\\\"urn:oasis:names:tc:SAML:2.0:assertion\\\">@NOT_USED@</saml:NameID><samlp:SessionIndex>\"")) + (TICKET)) + "\"</samlp:SessionIndex></samlp:LogoutRequest>\";";

    @Test
    public void testMissingCasUrls() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(casClient.getConfiguration(), "loginUrl, prefixUrl and restUrl cannot be all blank");
    }

    @Test
    public void testMissingSlashOnPrefixUrl() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setPrefixUrl(CasClientTests.PREFIX_URL_WITHOUT_SLASH);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        configuration.init();
        Assert.assertEquals(CasClientTests.PREFIX_URL, configuration.getPrefixUrl());
    }

    @Test
    public void testInitPrefixUrl() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        Assert.assertEquals(null, configuration.getPrefixUrl());
        configuration.init();
        Assert.assertEquals(CasClientTests.PREFIX_URL, configuration.getPrefixUrl());
    }

    @Test
    public void testInitLoginUrl() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setPrefixUrl(CasClientTests.PREFIX_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        Assert.assertEquals(null, configuration.getLoginUrl());
        configuration.init();
        Assert.assertEquals(LOGIN_URL, configuration.getLoginUrl());
    }

    @Test
    public void testCallbackUrlResolver() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setPrefixUrl(CasClientTests.CAS);
        configuration.setLoginUrl(((CasClientTests.CAS) + (CasClientTests.LOGIN)));
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CasClientTests.CASBACK);
        casClient.setUrlResolver(( url, context) -> (HOST) + url);
        casClient.setCallbackUrlResolver(new CallbackUrlResolver() {
            @Override
            public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
                return null;
            }

            @Override
            public boolean matches(final String clientName, final WebContext context) {
                return false;
            }
        });
        casClient.init();
        Assert.assertEquals((((CasClientTests.HOST) + (CasClientTests.CAS)) + (CasClientTests.LOGIN)), configuration.computeFinalLoginUrl(null));
        Assert.assertEquals((((CasClientTests.HOST) + (CasClientTests.CAS)) + "/"), configuration.computeFinalPrefixUrl(null));
    }

    @Test
    public void testRenewMissing() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = ((FoundAction) (casClient.redirect(context).get()));
        Assert.assertFalse(((action.getLocation().indexOf("renew=true")) >= 0));
    }

    @Test
    public void testRenew() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        configuration.setRenew(true);
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = ((FoundAction) (casClient.redirect(context).get()));
        Assert.assertTrue(((action.getLocation().indexOf("renew=true")) >= 0));
    }

    @Test
    public void testGatewayMissing() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = ((FoundAction) (casClient.redirect(context).get()));
        Assert.assertFalse(((action.getLocation().indexOf("gateway=true")) >= 0));
    }

    @Test
    public void testGatewayOK() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        configuration.setGateway(true);
        final FoundAction action = ((FoundAction) (casClient.redirect(context).get()));
        Assert.assertTrue(((action.getLocation().indexOf("gateway=true")) >= 0));
        final Optional<TokenCredentials> credentials = casClient.getCredentials(context);
        Assert.assertFalse(credentials.isPresent());
    }

    @Test
    public void testBackLogout() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init();
        final MockWebContext context = MockWebContext.create().addRequestParameter(LOGOUT_REQUEST_PARAMETER, CasClientTests.LOGOUT_MESSAGE).setRequestMethod(POST.name());
        final HttpAction action = ((HttpAction) (TestsHelper.expectException(() -> casClient.getCredentials(context))));
        Assert.assertEquals(204, action.getCode());
    }

    @Test
    public void testFrontLogout() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init();
        final MockWebContext context = MockWebContext.create().addRequestParameter(LOGOUT_REQUEST_PARAMETER, deflateAndBase64(CasClientTests.LOGOUT_MESSAGE)).setRequestMethod(GET.name());
        Assert.assertFalse(casClient.getCredentials(context).isPresent());
    }

    @Test
    public void testFrontLogoutWithRelayState() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init();
        final MockWebContext context = MockWebContext.create().addRequestParameter(LOGOUT_REQUEST_PARAMETER, deflateAndBase64(CasClientTests.LOGOUT_MESSAGE)).addRequestParameter(RELAY_STATE_PARAMETER, VALUE).setRequestMethod(GET.name());
        final HttpAction action = ((HttpAction) (TestsHelper.expectException(() -> casClient.getCredentials(context))));
        Assert.assertEquals(FOUND, action.getCode());
    }

    @Test
    public void testInitUrlWithLoginString() {
        final String testCasLoginUrl = "https://login.foo.bar/login/login";
        final String testCasPrefixUrl = "https://login.foo.bar/login/";
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(testCasLoginUrl);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        Assert.assertEquals(null, configuration.getPrefixUrl());
        configuration.init();
        Assert.assertEquals(testCasPrefixUrl, configuration.getPrefixUrl());
    }
}

