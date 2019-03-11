package org.apereo.cas.oidc.token;


import OAuth20ResponseTypes.CODE;
import Pac4jConstants.USER_PROFILES;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.support.oauth.OAuth20Constants;
import org.apereo.cas.support.oauth.util.OAuth20Utils;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * This is {@link OidcIdTokenGeneratorServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcIdTokenGeneratorServiceTests extends AbstractOidcTests {
    @Test
    public void verifyTokenGeneration() {
        val request = new MockHttpServletRequest();
        val profile = new CommonProfile();
        profile.setClientName("OIDC");
        profile.setId("casuser");
        request.setAttribute(USER_PROFILES, profile);
        val response = new MockHttpServletResponse();
        val tgt = Mockito.mock(TicketGrantingTicket.class);
        val callback = (((casProperties.getServer().getPrefix()) + (OAuth20Constants.BASE_OAUTH20_URL)) + '/') + (OAuth20Constants.CALLBACK_AUTHORIZE_URL_DEFINITION);
        val service = new WebApplicationServiceFactory().createService(callback);
        Mockito.when(tgt.getServices()).thenReturn(CollectionUtils.wrap("service", service));
        Mockito.when(tgt.getAuthentication()).thenReturn(CoreAuthenticationTestUtils.getAuthentication());
        val accessToken = Mockito.mock(AccessToken.class);
        Mockito.when(accessToken.getAuthentication()).thenReturn(CoreAuthenticationTestUtils.getAuthentication("casuser"));
        Mockito.when(accessToken.getTicketGrantingTicket()).thenReturn(tgt);
        Mockito.when(accessToken.getId()).thenReturn(getClass().getSimpleName());
        val idToken = oidcIdTokenGenerator.generate(request, response, accessToken, 30, CODE, OAuth20Utils.getRegisteredOAuthServiceByClientId(this.servicesManager, "clientid"));
        Assertions.assertNotNull(idToken);
    }

    @Test
    public void verifyTokenGenerationFailsWithoutProfile() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            val request = new MockHttpServletRequest();
            val response = new MockHttpServletResponse();
            val accessToken = Mockito.mock(AccessToken.class);
            oidcIdTokenGenerator.generate(request, response, accessToken, 30, CODE, OAuth20Utils.getRegisteredOAuthServiceByClientId(this.servicesManager, "clientid"));
        });
    }
}

