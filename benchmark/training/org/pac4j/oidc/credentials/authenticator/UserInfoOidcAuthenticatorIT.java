package org.pac4j.oidc.credentials.authenticator;


import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;


/**
 * Tests {@link UserInfoOidcAuthenticator}.
 *
 * @author Rakesh Sarangi
 * @since 3.5.0
 */
public class UserInfoOidcAuthenticatorIT implements TestsConstants {
    private static final int PORT = 8088;

    @Test
    public void testOkay() throws URISyntaxException {
        final OidcConfiguration configuration = Mockito.mock(OidcConfiguration.class, Answers.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.findProviderMetadata().getUserInfoEndpointURI()).thenReturn(new URI((("http://localhost:" + (UserInfoOidcAuthenticatorIT.PORT)) + "?r=ok")));
        final UserInfoOidcAuthenticator authenticator = new UserInfoOidcAuthenticator(configuration);
        final TokenCredentials credentials = getCredentials();
        authenticator.validate(credentials, MockWebContext.create());
        final OidcProfile profile = ((OidcProfile) (credentials.getUserProfile()));
        Assert.assertEquals(GOOD_USERNAME, profile.getDisplayName());
        Assert.assertEquals(USERNAME, profile.getUsername());
        Assert.assertEquals(credentials.getToken(), profile.getAccessToken().getValue());
    }

    @Test(expected = TechnicalException.class)
    public void testNotFound() throws URISyntaxException {
        final OidcConfiguration configuration = Mockito.mock(OidcConfiguration.class, Answers.RETURNS_DEEP_STUBS);
        Mockito.when(configuration.findProviderMetadata().getUserInfoEndpointURI()).thenReturn(new URI((("http://localhost:" + (UserInfoOidcAuthenticatorIT.PORT)) + "?r=notfound")));
        final UserInfoOidcAuthenticator authenticator = new UserInfoOidcAuthenticator(configuration);
        final TokenCredentials credentials = getCredentials();
        authenticator.validate(credentials, MockWebContext.create());
    }
}

