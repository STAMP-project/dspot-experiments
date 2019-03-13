/**
 * Copyright 2002-2019 the original author or authors.
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


import java.net.URI;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.handler.FilteringWebHandler;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuth2AuthorizationRequestRedirectWebFilterTests {
    @Mock
    private ReactiveClientRegistrationRepository clientRepository;

    @Mock
    private ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> authzRequestRepository;

    @Mock
    private ServerRequestCache requestCache;

    private ClientRegistration registration = TestClientRegistrations.clientRegistration().build();

    private OAuth2AuthorizationRequestRedirectWebFilter filter;

    private WebTestClient client;

    @Test
    public void constructorWhenClientRegistrationRepositoryNullThenIllegalArgumentException() {
        this.clientRepository = null;
        assertThatThrownBy(() -> new OAuth2AuthorizationRequestRedirectWebFilter(this.clientRepository)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void filterWhenDoesNotMatchThenClientRegistrationRepositoryNotSubscribed() {
        this.client.get().exchange().expectStatus().isOk();
        Mockito.verifyZeroInteractions(this.clientRepository, this.authzRequestRepository);
    }

    @Test
    public void filterWhenDoesMatchThenClientRegistrationRepositoryNotSubscribed() {
        FluxExchangeResult<String> result = this.client.get().uri("https://example.com/oauth2/authorization/registration-id").exchange().expectStatus().is3xxRedirection().returnResult(String.class);
        result.assertWithDiagnostics(() -> {
            URI location = result.getResponseHeaders().getLocation();
            assertThat(location).hasScheme("https").hasHost("example.com").hasPath("/login/oauth/authorize").hasParameter("response_type", "code").hasParameter("client_id", "client-id").hasParameter("scope", "read:user").hasParameter("state").hasParameter("redirect_uri", "https://example.com/login/oauth2/code/registration-id");
        });
        Mockito.verify(this.authzRequestRepository).saveAuthorizationRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    // gh-5520
    @Test
    public void filterWhenDoesMatchThenResolveRedirectUriExpandedExcludesQueryString() {
        FluxExchangeResult<String> result = this.client.get().uri("https://example.com/oauth2/authorization/registration-id?foo=bar").exchange().expectStatus().is3xxRedirection().returnResult(String.class);
        result.assertWithDiagnostics(() -> {
            URI location = result.getResponseHeaders().getLocation();
            assertThat(location).hasScheme("https").hasHost("example.com").hasPath("/login/oauth/authorize").hasParameter("response_type", "code").hasParameter("client_id", "client-id").hasParameter("scope", "read:user").hasParameter("state").hasParameter("redirect_uri", "https://example.com/login/oauth2/code/registration-id");
        });
    }

    @Test
    public void filterWhenExceptionThenRedirected() {
        FilteringWebHandler webHandler = new FilteringWebHandler(( e) -> Mono.error(new ClientAuthorizationRequiredException(this.registration.getRegistrationId())), Arrays.asList(this.filter));
        this.client = WebTestClient.bindToWebHandler(webHandler).build();
        FluxExchangeResult<String> result = this.client.get().uri("https://example.com/foo").exchange().expectStatus().is3xxRedirection().returnResult(String.class);
    }

    @Test
    public void filterWhenExceptionThenSaveRequestSessionAttribute() {
        this.filter.setRequestCache(this.requestCache);
        Mockito.when(this.requestCache.saveRequest(ArgumentMatchers.any())).thenReturn(Mono.empty());
        FilteringWebHandler webHandler = new FilteringWebHandler(( e) -> Mono.error(new ClientAuthorizationRequiredException(this.registration.getRegistrationId())), Arrays.asList(this.filter));
        this.client = WebTestClient.bindToWebHandler(webHandler).build();
        this.client.get().uri("https://example.com/foo").exchange().expectStatus().is3xxRedirection().returnResult(String.class);
        Mockito.verify(this.requestCache).saveRequest(ArgumentMatchers.any());
    }

    @Test
    public void filterWhenPathMatchesThenRequestSessionAttributeNotSaved() {
        this.filter.setRequestCache(this.requestCache);
        this.client.get().uri("https://example.com/oauth2/authorization/registration-id").exchange().expectStatus().is3xxRedirection().returnResult(String.class);
        Mockito.verifyZeroInteractions(this.requestCache);
    }
}

