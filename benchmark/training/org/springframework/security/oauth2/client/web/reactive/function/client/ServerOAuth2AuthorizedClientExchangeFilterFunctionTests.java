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
package org.springframework.security.oauth2.client.web.reactive.function.client;


import HttpHeaders.AUTHORIZATION;
import HttpMethod.POST;
import OAuth2AccessToken.TokenType;
import OAuth2AccessToken.TokenType.BEARER;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.client.web.reactive.function.client.OAuth2AuthorizedClientResolver.Request;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class ServerOAuth2AuthorizedClientExchangeFilterFunctionTests {
    @Mock
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @Mock
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private OAuth2AuthorizedClientResolver authorizedClientResolver;

    @Mock
    private ServerWebExchange serverWebExchange;

    @Captor
    private ArgumentCaptor<OAuth2AuthorizedClient> authorizedClientCaptor;

    private ServerOAuth2AuthorizedClientExchangeFilterFunction function;

    private MockExchangeFunction exchange = new MockExchangeFunction();

    private ClientRegistration registration = TestClientRegistrations.clientRegistration().build();

    private OAuth2AccessToken accessToken = new OAuth2AccessToken(TokenType.BEARER, "token-0", Instant.now(), Instant.now().plus(Duration.ofDays(1)));

    @Test
    public void filterWhenAuthorizedClientNullThenAuthorizationHeaderNull() {
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).build();
        this.function.filter(request, this.exchange).block();
        assertThat(this.exchange.getRequest().headers().getFirst(AUTHORIZATION)).isNull();
    }

    @Test
    public void filterWhenAuthorizedClientThenAuthorizationHeader() {
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken);
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient)).build();
        this.function.filter(request, this.exchange).block();
        assertThat(this.exchange.getRequest().headers().getFirst(AUTHORIZATION)).isEqualTo(("Bearer " + (this.accessToken.getTokenValue())));
    }

    @Test
    public void filterWhenExistingAuthorizationThenSingleAuthorizationHeader() {
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken);
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).header(AUTHORIZATION, "Existing").attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient)).build();
        this.function.filter(request, this.exchange).block();
        HttpHeaders headers = this.exchange.getRequest().headers();
        assertThat(headers.get(AUTHORIZATION)).containsOnly(("Bearer " + (this.accessToken.getTokenValue())));
    }

    @Test
    public void filterWhenClientCredentialsTokenExpiredThenGetNewToken() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("test", "this");
        ClientRegistration registration = TestClientRegistrations.clientCredentials().build();
        String clientRegistrationId = registration.getClientId();
        this.function = new ServerOAuth2AuthorizedClientExchangeFilterFunction(this.authorizedClientRepository, this.authorizedClientResolver);
        OAuth2AccessToken newAccessToken = new OAuth2AccessToken(TokenType.BEARER, "new-token", Instant.now(), Instant.now().plus(Duration.ofDays(1)));
        OAuth2AuthorizedClient newAuthorizedClient = new OAuth2AuthorizedClient(registration, "principalName", newAccessToken, null);
        Request r = new Request(clientRegistrationId, authentication, null);
        Mockito.when(this.authorizedClientResolver.clientCredentials(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(newAuthorizedClient));
        Mockito.when(this.authorizedClientResolver.createDefaultedRequest(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(r));
        Mockito.when(this.authorizedClientRepository.saveAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        Instant issuedAt = Instant.now().minus(Duration.ofDays(1));
        Instant accessTokenExpiresAt = issuedAt.plus(Duration.ofHours(1));
        OAuth2AccessToken accessToken = new OAuth2AccessToken(this.accessToken.getTokenType(), this.accessToken.getTokenValue(), issuedAt, accessTokenExpiresAt);
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(registration, "principalName", accessToken, null);
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient)).build();
        this.function.filter(request, this.exchange).subscriberContext(ReactiveSecurityContextHolder.withAuthentication(authentication)).block();
        Mockito.verify(this.authorizedClientRepository).saveAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.eq(authentication), ArgumentMatchers.any());
        Mockito.verify(this.authorizedClientResolver).clientCredentials(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(this.authorizedClientResolver).createDefaultedRequest(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(1);
        ClientRequest request1 = requests.get(0);
        assertThat(request1.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer new-token");
        assertThat(request1.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request1.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request1)).isEmpty();
    }

    @Test
    public void filterWhenClientCredentialsTokenNotExpiredThenUseCurrentToken() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("test", "this");
        ClientRegistration registration = TestClientRegistrations.clientCredentials().build();
        this.function = new ServerOAuth2AuthorizedClientExchangeFilterFunction(this.authorizedClientRepository, this.authorizedClientResolver);
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(registration, "principalName", this.accessToken, null);
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient)).build();
        this.function.filter(request, this.exchange).subscriberContext(ReactiveSecurityContextHolder.withAuthentication(authentication)).block();
        Mockito.verify(this.authorizedClientResolver, Mockito.never()).clientCredentials(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(this.authorizedClientResolver, Mockito.never()).createDefaultedRequest(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(1);
        ClientRequest request1 = requests.get(0);
        assertThat(request1.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer token-0");
        assertThat(request1.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request1.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request1)).isEmpty();
    }

    @Test
    public void filterWhenRefreshRequiredThenRefresh() {
        Mockito.when(this.authorizedClientRepository.saveAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        OAuth2AccessTokenResponse response = OAuth2AccessTokenResponse.withToken("token-1").tokenType(BEARER).expiresIn(3600).refreshToken("refresh-1").build();
        Mockito.when(this.exchange.getResponse().body(ArgumentMatchers.any())).thenReturn(Mono.just(response));
        Instant issuedAt = Instant.now().minus(Duration.ofDays(1));
        Instant accessTokenExpiresAt = issuedAt.plus(Duration.ofHours(1));
        this.accessToken = new OAuth2AccessToken(this.accessToken.getTokenType(), this.accessToken.getTokenValue(), issuedAt, accessTokenExpiresAt);
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", issuedAt);
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken, refreshToken);
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient)).build();
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("test", "this");
        this.function.filter(request, this.exchange).subscriberContext(ReactiveSecurityContextHolder.withAuthentication(authentication)).block();
        Mockito.verify(this.authorizedClientRepository).saveAuthorizedClient(this.authorizedClientCaptor.capture(), ArgumentMatchers.eq(authentication), ArgumentMatchers.any());
        OAuth2AuthorizedClient newAuthorizedClient = authorizedClientCaptor.getValue();
        assertThat(newAuthorizedClient.getAccessToken()).isEqualTo(response.getAccessToken());
        assertThat(newAuthorizedClient.getRefreshToken()).isEqualTo(response.getRefreshToken());
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(2);
        ClientRequest request0 = requests.get(0);
        assertThat(request0.headers().getFirst(AUTHORIZATION)).isEqualTo("Basic Y2xpZW50LWlkOmNsaWVudC1zZWNyZXQ=");
        assertThat(request0.url().toASCIIString()).isEqualTo("https://example.com/login/oauth/access_token");
        assertThat(request0.method()).isEqualTo(POST);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request0)).isEqualTo("grant_type=refresh_token&refresh_token=refresh-token");
        ClientRequest request1 = requests.get(1);
        assertThat(request1.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer token-1");
        assertThat(request1.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request1.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request1)).isEmpty();
    }

    @Test
    public void filterWhenRefreshRequiredThenRefreshAndResponseDoesNotContainRefreshToken() {
        Mockito.when(this.authorizedClientRepository.saveAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        OAuth2AccessTokenResponse response = // .refreshToken(xxx)  // No refreshToken in response
        OAuth2AccessTokenResponse.withToken("token-1").tokenType(BEARER).expiresIn(3600).build();
        Mockito.when(this.exchange.getResponse().body(ArgumentMatchers.any())).thenReturn(Mono.just(response));
        Instant issuedAt = Instant.now().minus(Duration.ofDays(1));
        Instant accessTokenExpiresAt = issuedAt.plus(Duration.ofHours(1));
        this.accessToken = new OAuth2AccessToken(this.accessToken.getTokenType(), this.accessToken.getTokenValue(), issuedAt, accessTokenExpiresAt);
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", issuedAt);
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken, refreshToken);
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient)).build();
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("test", "this");
        this.function.filter(request, this.exchange).subscriberContext(ReactiveSecurityContextHolder.withAuthentication(authentication)).block();
        Mockito.verify(this.authorizedClientRepository).saveAuthorizedClient(this.authorizedClientCaptor.capture(), ArgumentMatchers.eq(authentication), ArgumentMatchers.any());
        OAuth2AuthorizedClient newAuthorizedClient = authorizedClientCaptor.getValue();
        assertThat(newAuthorizedClient.getAccessToken()).isEqualTo(response.getAccessToken());
        assertThat(newAuthorizedClient.getRefreshToken()).isEqualTo(authorizedClient.getRefreshToken());
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(2);
        ClientRequest request0 = requests.get(0);
        assertThat(request0.headers().getFirst(AUTHORIZATION)).isEqualTo("Basic Y2xpZW50LWlkOmNsaWVudC1zZWNyZXQ=");
        assertThat(request0.url().toASCIIString()).isEqualTo("https://example.com/login/oauth/access_token");
        assertThat(request0.method()).isEqualTo(POST);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request0)).isEqualTo("grant_type=refresh_token&refresh_token=refresh-token");
        ClientRequest request1 = requests.get(1);
        assertThat(request1.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer token-1");
        assertThat(request1.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request1.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request1)).isEmpty();
    }

    @Test
    public void filterWhenRefreshRequiredAndEmptyReactiveSecurityContextThenSaved() {
        Mockito.when(this.authorizedClientRepository.saveAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        OAuth2AccessTokenResponse response = OAuth2AccessTokenResponse.withToken("token-1").tokenType(BEARER).expiresIn(3600).refreshToken("refresh-1").build();
        Mockito.when(this.exchange.getResponse().body(ArgumentMatchers.any())).thenReturn(Mono.just(response));
        Instant issuedAt = Instant.now().minus(Duration.ofDays(1));
        Instant accessTokenExpiresAt = issuedAt.plus(Duration.ofHours(1));
        this.accessToken = new OAuth2AccessToken(this.accessToken.getTokenType(), this.accessToken.getTokenValue(), issuedAt, accessTokenExpiresAt);
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", issuedAt);
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken, refreshToken);
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient)).build();
        this.function.filter(request, this.exchange).block();
        Mockito.verify(this.authorizedClientRepository).saveAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(2);
        ClientRequest request0 = requests.get(0);
        assertThat(request0.headers().getFirst(AUTHORIZATION)).isEqualTo("Basic Y2xpZW50LWlkOmNsaWVudC1zZWNyZXQ=");
        assertThat(request0.url().toASCIIString()).isEqualTo("https://example.com/login/oauth/access_token");
        assertThat(request0.method()).isEqualTo(POST);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request0)).isEqualTo("grant_type=refresh_token&refresh_token=refresh-token");
        ClientRequest request1 = requests.get(1);
        assertThat(request1.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer token-1");
        assertThat(request1.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request1.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request1)).isEmpty();
    }

    @Test
    public void filterWhenRefreshTokenNullThenShouldRefreshFalse() {
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken);
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient)).build();
        this.function.filter(request, this.exchange).block();
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(1);
        ClientRequest request0 = requests.get(0);
        assertThat(request0.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer token-0");
        assertThat(request0.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request0.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request0)).isEmpty();
    }

    @Test
    public void filterWhenNotExpiredThenShouldRefreshFalse() {
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", this.accessToken.getIssuedAt());
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken, refreshToken);
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(authorizedClient)).build();
        this.function.filter(request, this.exchange).block();
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(1);
        ClientRequest request0 = requests.get(0);
        assertThat(request0.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer token-0");
        assertThat(request0.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request0.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request0)).isEmpty();
    }

    @Test
    public void filterWhenClientRegistrationIdThenAuthorizedClientResolved() {
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", this.accessToken.getIssuedAt());
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken, refreshToken);
        Mockito.when(this.authorizedClientRepository.loadAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(authorizedClient));
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(this.registration));
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(this.registration.getRegistrationId())).build();
        this.function.filter(request, this.exchange).block();
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(1);
        ClientRequest request0 = requests.get(0);
        assertThat(request0.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer token-0");
        assertThat(request0.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request0.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request0)).isEmpty();
    }

    @Test
    public void filterWhenDefaultClientRegistrationIdThenAuthorizedClientResolved() {
        this.function.setDefaultClientRegistrationId(this.registration.getRegistrationId());
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", this.accessToken.getIssuedAt());
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken, refreshToken);
        Mockito.when(this.authorizedClientRepository.loadAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(authorizedClient));
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(this.registration));
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).build();
        this.function.filter(request, this.exchange).block();
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(1);
        ClientRequest request0 = requests.get(0);
        assertThat(request0.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer token-0");
        assertThat(request0.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request0.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request0)).isEmpty();
    }

    @Test
    public void filterWhenClientRegistrationIdFromAuthenticationThenAuthorizedClientResolved() {
        this.function.setDefaultOAuth2AuthorizedClient(true);
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", this.accessToken.getIssuedAt());
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken, refreshToken);
        Mockito.when(this.authorizedClientRepository.loadAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(authorizedClient));
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(this.registration));
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).build();
        OAuth2User user = new org.springframework.security.oauth2.core.user.DefaultOAuth2User(AuthorityUtils.createAuthorityList("ROLE_USER"), Collections.singletonMap("user", "rob"), "user");
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(user, user.getAuthorities(), "client-id");
        this.function.filter(request, this.exchange).subscriberContext(ReactiveSecurityContextHolder.withAuthentication(authentication)).block();
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(1);
        ClientRequest request0 = requests.get(0);
        assertThat(request0.headers().getFirst(AUTHORIZATION)).isEqualTo("Bearer token-0");
        assertThat(request0.url().toASCIIString()).isEqualTo("https://example.com");
        assertThat(request0.method()).isEqualTo(HttpMethod.GET);
        assertThat(ServerOAuth2AuthorizedClientExchangeFilterFunctionTests.getBody(request0)).isEmpty();
    }

    @Test
    public void filterWhenDefaultOAuth2AuthorizedClientFalseThenEmpty() {
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).build();
        OAuth2User user = new org.springframework.security.oauth2.core.user.DefaultOAuth2User(AuthorityUtils.createAuthorityList("ROLE_USER"), Collections.singletonMap("user", "rob"), "user");
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(user, user.getAuthorities(), "client-id");
        this.function.filter(request, this.exchange).subscriberContext(ReactiveSecurityContextHolder.withAuthentication(authentication)).block();
        List<ClientRequest> requests = this.exchange.getRequests();
        assertThat(requests).hasSize(1);
        Mockito.verifyZeroInteractions(this.clientRegistrationRepository, this.authorizedClientRepository);
    }

    @Test
    public void filterWhenClientRegistrationIdAndServerWebExchangeFromContextThenServerWebExchangeFromContext() {
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", this.accessToken.getIssuedAt());
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(this.registration, "principalName", this.accessToken, refreshToken);
        Mockito.when(this.authorizedClientRepository.loadAuthorizedClient(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.just(authorizedClient));
        Mockito.when(this.clientRegistrationRepository.findByRegistrationId(ArgumentMatchers.any())).thenReturn(Mono.just(this.registration));
        ClientRequest request = ClientRequest.create(GET, URI.create("https://example.com")).attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(this.registration.getRegistrationId())).build();
        this.function.filter(request, this.exchange).subscriberContext(serverWebExchange()).block();
        Mockito.verify(this.authorizedClientRepository).loadAuthorizedClient(ArgumentMatchers.eq(this.registration.getRegistrationId()), ArgumentMatchers.any(), ArgumentMatchers.eq(this.serverWebExchange));
    }
}

