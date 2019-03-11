package org.apereo.cas.support.oauth.authenticator;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link OAuth20ClientIdClientSecretAuthenticatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class OAuth20ClientIdClientSecretAuthenticatorTests extends BaseOAuth20AuthenticatorTests {
    protected OAuth20ClientIdClientSecretAuthenticator authenticator;

    @Test
    public void verifyAuthentication() {
        val credentials = new UsernamePasswordCredentials("client", "secret");
        val request = new MockHttpServletRequest();
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        authenticator.validate(credentials, ctx);
        Assertions.assertNotNull(credentials.getUserProfile());
        Assertions.assertEquals("client", credentials.getUserProfile().getId());
    }
}

