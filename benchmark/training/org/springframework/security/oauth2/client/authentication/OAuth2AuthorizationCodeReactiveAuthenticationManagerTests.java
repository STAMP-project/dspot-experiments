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


import ClientRegistration.Builder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.TestOAuth2AccessTokenResponses;
import org.springframework.security.oauth2.core.endpoint.TestOAuth2AuthorizationRequests;
import org.springframework.security.oauth2.core.endpoint.TestOAuth2AuthorizationResponses;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuth2AuthorizationCodeReactiveAuthenticationManagerTests {
    @Mock
    private ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

    private OAuth2AuthorizationCodeReactiveAuthenticationManager manager;

    private Builder registration = TestClientRegistrations.clientRegistration();

    private OAuth2AuthorizationRequest.Builder authorizationRequest = TestOAuth2AuthorizationRequests.request();

    private OAuth2AuthorizationResponse.Builder authorizationResponse = TestOAuth2AuthorizationResponses.success();

    private OAuth2AccessTokenResponse.Builder tokenResponse = TestOAuth2AccessTokenResponses.accessTokenResponse();

    @Test
    public void authenticateWhenErrorThenOAuth2AuthorizationException() {
        this.authorizationResponse = TestOAuth2AuthorizationResponses.error();
        assertThatCode(() -> authenticate()).isInstanceOf(OAuth2AuthorizationException.class);
    }

    @Test
    public void authenticateWhenStateNotEqualThenOAuth2AuthorizationException() {
        this.authorizationRequest.state("notequal");
        assertThatCode(() -> authenticate()).isInstanceOf(OAuth2AuthorizationException.class);
    }

    @Test
    public void authenticateWhenRedirectUriNotEqualThenOAuth2AuthorizationException() {
        this.authorizationRequest.redirectUri("https://example.org/notequal");
        assertThatCode(() -> authenticate()).isInstanceOf(OAuth2AuthorizationException.class);
    }

    @Test
    public void authenticateWhenValidThenSuccess() {
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(Mono.just(this.tokenResponse.build()));
        OAuth2AuthorizationCodeAuthenticationToken result = authenticate();
        assertThat(result).isNotNull();
    }

    @Test
    public void authenticateWhenEmptyThenEmpty() {
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(Mono.empty());
        OAuth2AuthorizationCodeAuthenticationToken result = authenticate();
        assertThat(result).isNull();
    }

    @Test
    public void authenticateWhenOAuth2AuthorizationExceptionThenOAuth2AuthorizationException() {
        Mockito.when(this.accessTokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(Mono.error(() -> new OAuth2AuthorizationException(new OAuth2Error("error"))));
        assertThatCode(() -> authenticate()).isInstanceOf(OAuth2AuthorizationException.class);
    }
}

