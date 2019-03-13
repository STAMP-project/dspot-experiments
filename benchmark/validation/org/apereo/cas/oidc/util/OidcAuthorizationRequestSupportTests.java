package org.apereo.cas.oidc.util;


import lombok.val;
import org.apereo.cas.oidc.OidcConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.CommonProfile;


/**
 *
 *
 * @author David Rodriguez
 * @since 5.1.0
 */
public class OidcAuthorizationRequestSupportTests {
    @Test
    public void verifyOidcPrompt() {
        val url = ("https://tralala.whapi.com/something?" + (OidcConstants.PROMPT)) + "=value1";
        val authorizationRequest = OidcAuthorizationRequestSupport.getOidcPromptFromAuthorizationRequest(url);
        Assertions.assertEquals("value1", authorizationRequest.toArray()[0]);
    }

    @Test
    public void verifyOidcPromptFromContext() {
        val url = ("https://tralala.whapi.com/something?" + (OidcConstants.PROMPT)) + "=value1";
        val context = Mockito.mock(WebContext.class);
        Mockito.when(context.getFullRequestURL()).thenReturn(url);
        val authorizationRequest = OidcAuthorizationRequestSupport.getOidcPromptFromAuthorizationRequest(context);
        Assertions.assertEquals("value1", authorizationRequest.toArray()[0]);
    }

    @Test
    public void verifyOidcMaxAge() {
        val context = Mockito.mock(WebContext.class);
        Mockito.when(context.getFullRequestURL()).thenReturn((("https://tralala.whapi.com/something?" + (OidcConstants.MAX_AGE)) + "=1000"));
        val age = OidcAuthorizationRequestSupport.getOidcMaxAgeFromAuthorizationRequest(context);
        Assertions.assertTrue(age.isPresent());
        Assertions.assertTrue((1000 == (age.get())));
        Mockito.when(context.getFullRequestURL()).thenReturn((("https://tralala.whapi.com/something?" + (OidcConstants.MAX_AGE)) + "=NA"));
        val age2 = OidcAuthorizationRequestSupport.getOidcMaxAgeFromAuthorizationRequest(context);
        Assertions.assertTrue(age2.isPresent());
        Assertions.assertTrue(((-1) == (age2.get())));
        Mockito.when(context.getFullRequestURL()).thenReturn("https://tralala.whapi.com/something?");
        val age3 = OidcAuthorizationRequestSupport.getOidcMaxAgeFromAuthorizationRequest(context);
        Assertions.assertFalse(age3.isPresent());
    }

    @Test
    public void verifyAuthnProfile() {
        val context = Mockito.mock(WebContext.class);
        Mockito.when(context.getSessionStore()).thenReturn(Mockito.mock(SessionStore.class));
        Mockito.when(context.getRequestAttribute(ArgumentMatchers.anyString())).thenReturn(new CommonProfile());
        Assertions.assertTrue(OidcAuthorizationRequestSupport.isAuthenticationProfileAvailable(context).isPresent());
    }
}

