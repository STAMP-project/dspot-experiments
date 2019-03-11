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
package org.springframework.security.oauth2.client.web.server.authentication;


import ClientRegistration.Builder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.web.server.WebFilterExchange;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuth2LoginAuthenticationWebFilterTests {
    @Mock
    private ReactiveAuthenticationManager authenticationManager;

    @Mock
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    private OAuth2LoginAuthenticationWebFilter filter;

    private WebFilterExchange webFilterExchange;

    private Builder registration = TestClientRegistrations.clientRegistration();

    private OAuth2AuthorizationResponse.Builder authorizationResponseBldr = OAuth2AuthorizationResponse.success("code").state("state");

    @Test
    public void onAuthenticationSuccessWhenOAuth2LoginAuthenticationTokenThenSavesAuthorizedClient() {
        this.filter.onAuthenticationSuccess(loginToken(), this.webFilterExchange).block();
        Mockito.verify(this.authorizedClientRepository).saveAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

