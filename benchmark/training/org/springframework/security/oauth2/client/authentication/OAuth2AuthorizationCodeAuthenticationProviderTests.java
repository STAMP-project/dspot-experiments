/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.client.authentication;


import OAuth2ErrorCodes.INVALID_REQUEST;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;


/**
 * Tests for {@link OAuth2AuthorizationCodeAuthenticationProvider}.
 *
 * @author Joe Grandja
 */
public class OAuth2AuthorizationCodeAuthenticationProviderTests {
    private ClientRegistration clientRegistration;

    private OAuth2AuthorizationRequest authorizationRequest;

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

    private OAuth2AuthorizationCodeAuthenticationProvider authenticationProvider;

    @Test
    public void constructorWhenAccessTokenResponseClientIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new OAuth2AuthorizationCodeAuthenticationProvider(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void supportsWhenTypeOAuth2AuthorizationCodeAuthenticationTokenThenReturnTrue() {
        assertThat(this.authenticationProvider.supports(OAuth2AuthorizationCodeAuthenticationToken.class)).isTrue();
    }

    @Test
    public void authenticateWhenAuthorizationErrorResponseThenThrowOAuth2AuthorizationException() {
        OAuth2AuthorizationResponse authorizationResponse = error().errorCode(INVALID_REQUEST).build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(this.authorizationRequest, authorizationResponse);
        assertThatThrownBy(() -> {
            this.authenticationProvider.authenticate(new OAuth2AuthorizationCodeAuthenticationToken(this.clientRegistration, authorizationExchange));
        }).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining(INVALID_REQUEST);
    }

    @Test
    public void authenticateWhenAuthorizationResponseStateNotEqualAuthorizationRequestStateThenThrowOAuth2AuthorizationException() {
        OAuth2AuthorizationResponse authorizationResponse = success().state("67890").build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(this.authorizationRequest, authorizationResponse);
        assertThatThrownBy(() -> {
            this.authenticationProvider.authenticate(new OAuth2AuthorizationCodeAuthenticationToken(this.clientRegistration, authorizationExchange));
        }).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining("invalid_state_parameter");
    }

    @Test
    public void authenticateWhenAuthorizationResponseRedirectUriNotEqualAuthorizationRequestRedirectUriThenThrowOAuth2AuthorizationException() {
        OAuth2AuthorizationResponse authorizationResponse = success().redirectUri("http://example2.com").build();
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(this.authorizationRequest, authorizationResponse);
        assertThatThrownBy(() -> {
            this.authenticationProvider.authenticate(new OAuth2AuthorizationCodeAuthenticationToken(this.clientRegistration, authorizationExchange));
        }).isInstanceOf(OAuth2AuthorizationException.class).hasMessageContaining("invalid_redirect_uri_parameter");
    }

    @Test
    public void authenticateWhenAuthorizationSuccessResponseThenExchangedForAccessToken() {
        OAuth2AccessTokenResponse accessTokenResponse = accessTokenResponse().refreshToken("refresh").build();
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(accessTokenResponse);
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(this.authorizationRequest, success().build());
        OAuth2AuthorizationCodeAuthenticationToken authenticationResult = ((OAuth2AuthorizationCodeAuthenticationToken) (this.authenticationProvider.authenticate(new OAuth2AuthorizationCodeAuthenticationToken(this.clientRegistration, authorizationExchange))));
        assertThat(authenticationResult.isAuthenticated()).isTrue();
        assertThat(authenticationResult.getPrincipal()).isEqualTo(this.clientRegistration.getClientId());
        assertThat(authenticationResult.getCredentials()).isEqualTo(accessTokenResponse.getAccessToken().getTokenValue());
        assertThat(authenticationResult.getAuthorities()).isEqualTo(Collections.emptyList());
        assertThat(authenticationResult.getClientRegistration()).isEqualTo(this.clientRegistration);
        assertThat(authenticationResult.getAuthorizationExchange()).isEqualTo(authorizationExchange);
        assertThat(authenticationResult.getAccessToken()).isEqualTo(accessTokenResponse.getAccessToken());
        assertThat(authenticationResult.getRefreshToken()).isEqualTo(accessTokenResponse.getRefreshToken());
    }
}

