/**
 * Copyright 2008 Web Cohesion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth.provider.filter;


import UserAuthorizationProcessingFilter.CALLBACK_ATTRIBUTE;
import UserAuthorizationProcessingFilter.VERIFIER_ATTRIBUTE;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.security.oauth.provider.verifier.OAuthVerifierServices;


/**
 *
 *
 * @author Ryan Heaton
 */
public class OAuthUserAuthorizationProcessingFilterTests {
    /**
     * tests the attempt to authenticate.
     */
    @Test
    public void testAttemptAuthentication() throws Exception {
        UserAuthorizationProcessingFilter filter = new UserAuthorizationProcessingFilter("/");
        OAuthVerifierServices vs = Mockito.mock(OAuthVerifierServices.class);
        filter.setVerifierServices(vs);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        OAuthProviderTokenServices tokenServices = Mockito.mock(OAuthProviderTokenServices.class);
        filter.setTokenServices(tokenServices);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(request.getParameter("requestToken")).thenReturn("tok");
        OAuthProviderTokenImpl token = new OAuthProviderTokenImpl();
        token.setCallbackUrl("callback");
        Mockito.when(tokenServices.getToken("tok")).thenReturn(token);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);
        try {
            filter.attemptAuthentication(request, response);
            Assert.fail();
        } catch (InsufficientAuthenticationException e) {
        }
        Mockito.verify(request).setAttribute(CALLBACK_ATTRIBUTE, "callback");
        Mockito.reset(request);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(request.getParameter("requestToken")).thenReturn("tok");
        Mockito.when(tokenServices.getToken("tok")).thenReturn(token);
        Mockito.when(vs.createVerifier()).thenReturn("verifier");
        tokenServices.authorizeRequestToken("tok", "verifier", authentication);
        filter.setTokenServices(tokenServices);
        filter.attemptAuthentication(request, response);
        Mockito.verify(request).setAttribute(CALLBACK_ATTRIBUTE, "callback");
        Mockito.verify(request).setAttribute(VERIFIER_ATTRIBUTE, "verifier");
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}

