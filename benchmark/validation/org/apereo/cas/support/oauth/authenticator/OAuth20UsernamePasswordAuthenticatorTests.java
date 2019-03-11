package org.apereo.cas.support.oauth.authenticator;


import OAuth20Constants.CLIENT_ID;
import OAuth20Constants.CLIENT_SECRET;
import lombok.val;
import org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy;
import org.apereo.cas.util.HttpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link OAuth20UsernamePasswordAuthenticatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class OAuth20UsernamePasswordAuthenticatorTests extends BaseOAuth20AuthenticatorTests {
    protected OAuth20UsernamePasswordAuthenticator authenticator;

    @Test
    public void verifyAcceptedCredentialsWithClientId() {
        val credentials = new UsernamePasswordCredentials("casuser", "casuser");
        val request = new MockHttpServletRequest();
        request.addParameter(CLIENT_ID, "client");
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        authenticator.validate(credentials, ctx);
        Assertions.assertNotNull(credentials.getUserProfile());
        Assertions.assertEquals("casuser", credentials.getUserProfile().getId());
    }

    @Test
    public void verifyAcceptedCredentialsWithClientSecret() {
        val credentials = new UsernamePasswordCredentials("casuser", "casuser");
        val request = new MockHttpServletRequest();
        request.addParameter(CLIENT_ID, "client");
        request.addParameter(CLIENT_SECRET, "secret");
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        authenticator.validate(credentials, ctx);
        Assertions.assertNotNull(credentials.getUserProfile());
        Assertions.assertEquals("casuser", credentials.getUserProfile().getId());
    }

    @Test
    public void verifyAcceptedCredentialsWithBadClientSecret() {
        val credentials = new UsernamePasswordCredentials("casuser", "casuser");
        val request = new MockHttpServletRequest();
        request.addParameter(CLIENT_ID, "client");
        request.addParameter(CLIENT_SECRET, "secretnotfound");
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        Assertions.assertThrows(CredentialsException.class, () -> authenticator.validate(credentials, ctx));
    }

    @Test
    public void verifyAcceptedCredentialsWithServiceDisabled() {
        val credentials = new UsernamePasswordCredentials("casuser", "casuser");
        val request = new MockHttpServletRequest();
        request.addParameter(CLIENT_ID, "client");
        service.setAccessStrategy(new DefaultRegisteredServiceAccessStrategy(false, false));
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        Assertions.assertThrows(CredentialsException.class, () -> authenticator.validate(credentials, ctx));
    }

    @Test
    public void verifyAcceptedCredentialsWithBadCredentials() {
        val credentials = new UsernamePasswordCredentials("casuser-something", "casuser");
        val request = new MockHttpServletRequest();
        request.addParameter(CLIENT_ID, "client");
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        Assertions.assertThrows(CredentialsException.class, () -> authenticator.validate(credentials, ctx));
    }

    @Test
    public void verifyAcceptedCredentialsWithoutClientId() {
        val credentials = new UsernamePasswordCredentials("casuser", "casuser");
        val request = new MockHttpServletRequest();
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        Assertions.assertThrows(CredentialsException.class, () -> authenticator.validate(credentials, ctx));
    }

    @Test
    public void verifyAcceptedCredentialsWithClientSecretWithBasicAuth() {
        val credentials = new UsernamePasswordCredentials("casuser", "casuser");
        val request = new MockHttpServletRequest();
        val headers = HttpUtils.createBasicAuthHeaders("client", "secret");
        val authz = headers.get(AUTHORIZATION);
        Assertions.assertNotNull(authz);
        request.addHeader(AUTHORIZATION, authz);
        val ctx = new org.pac4j.core.context.J2EContext(request, new MockHttpServletResponse());
        authenticator.validate(credentials, ctx);
        Assertions.assertNotNull(credentials.getUserProfile());
        Assertions.assertEquals("casuser", credentials.getUserProfile().getId());
    }
}

