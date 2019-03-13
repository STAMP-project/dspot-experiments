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
package org.springframework.security.oauth2.client.web.server;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 */
public class AuthenticatedPrincipalServerOAuth2AuthorizedClientRepositoryTests {
    private String registrationId = "registrationId";

    private String principalName = "principalName";

    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    private ServerOAuth2AuthorizedClientRepository anonymousAuthorizedClientRepository;

    private AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    private MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

    @Test
    public void constructorWhenAuthorizedClientServiceIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setAuthorizedClientRepositoryWhenAuthorizedClientRepositoryIsNullThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> this.authorizedClientRepository.setAnonymousAuthorizedClientRepository(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void loadAuthorizedClientWhenAuthenticatedPrincipalThenLoadFromService() {
        Mockito.when(this.authorizedClientService.loadAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        Authentication authentication = this.createAuthenticatedPrincipal();
        this.authorizedClientRepository.loadAuthorizedClient(this.registrationId, authentication, this.exchange).block();
        Mockito.verify(this.authorizedClientService).loadAuthorizedClient(this.registrationId, this.principalName);
    }

    @Test
    public void loadAuthorizedClientWhenAnonymousPrincipalThenLoadFromAnonymousRepository() {
        Mockito.when(this.anonymousAuthorizedClientRepository.loadAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        Authentication authentication = this.createAnonymousPrincipal();
        this.authorizedClientRepository.loadAuthorizedClient(this.registrationId, authentication, this.exchange).block();
        Mockito.verify(this.anonymousAuthorizedClientRepository).loadAuthorizedClient(this.registrationId, authentication, this.exchange);
    }

    @Test
    public void saveAuthorizedClientWhenAuthenticatedPrincipalThenSaveToService() {
        Mockito.when(this.authorizedClientService.saveAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        Authentication authentication = this.createAuthenticatedPrincipal();
        OAuth2AuthorizedClient authorizedClient = Mockito.mock(OAuth2AuthorizedClient.class);
        this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, authentication, this.exchange).block();
        Mockito.verify(this.authorizedClientService).saveAuthorizedClient(authorizedClient, authentication);
    }

    @Test
    public void saveAuthorizedClientWhenAnonymousPrincipalThenSaveToAnonymousRepository() {
        Mockito.when(this.anonymousAuthorizedClientRepository.saveAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        Authentication authentication = this.createAnonymousPrincipal();
        OAuth2AuthorizedClient authorizedClient = Mockito.mock(OAuth2AuthorizedClient.class);
        this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, authentication, this.exchange).block();
        Mockito.verify(this.anonymousAuthorizedClientRepository).saveAuthorizedClient(authorizedClient, authentication, this.exchange);
    }

    @Test
    public void removeAuthorizedClientWhenAuthenticatedPrincipalThenRemoveFromService() {
        Mockito.when(this.authorizedClientService.removeAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        Authentication authentication = this.createAuthenticatedPrincipal();
        this.authorizedClientRepository.removeAuthorizedClient(this.registrationId, authentication, this.exchange).block();
        Mockito.verify(this.authorizedClientService).removeAuthorizedClient(this.registrationId, this.principalName);
    }

    @Test
    public void removeAuthorizedClientWhenAnonymousPrincipalThenRemoveFromAnonymousRepository() {
        Mockito.when(this.anonymousAuthorizedClientRepository.removeAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        Authentication authentication = this.createAnonymousPrincipal();
        this.authorizedClientRepository.removeAuthorizedClient(this.registrationId, authentication, this.exchange).block();
        Mockito.verify(this.anonymousAuthorizedClientRepository).removeAuthorizedClient(this.registrationId, authentication, this.exchange);
    }
}

