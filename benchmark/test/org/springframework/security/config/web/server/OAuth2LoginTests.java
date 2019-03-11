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
package org.springframework.security.config.web.server;


import CommonOAuth2Provider.GITHUB;
import CommonOAuth2Provider.GOOGLE;
import OidcParameterNames.ID_TOKEN;
import ServerWebExchangeMatcher.MatchResult;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.htmlunit.server.WebTestClientHtmlUnitDriverBuilder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.TestOAuth2AccessTokens;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.TestOAuth2AuthorizationExchanges;
import org.springframework.security.oauth2.core.endpoint.TestOAuth2AuthorizationRequests;
import org.springframework.security.oauth2.core.endpoint.TestOAuth2AuthorizationResponses;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.TestOidcUsers;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.TestOAuth2Users;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoderFactory;
import org.springframework.security.test.web.reactive.server.WebTestClientBuilder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.springframework.security.config.web.server.FormLoginTests.HomePage.to;


/**
 *
 *
 * @author Rob Winch
 * @since 5.1
 */
public class OAuth2LoginTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private WebFilterChainProxy springSecurity;

    private static ClientRegistration github = GITHUB.getBuilder("github").clientId("client").clientSecret("secret").build();

    private static ClientRegistration google = GOOGLE.getBuilder("google").clientId("client").clientSecret("secret").build();

    @Test
    public void defaultLoginPageWithMultipleClientRegistrationsThenLinks() {
        this.spring.register(OAuth2LoginTests.OAuth2LoginWithMultipleClientRegistrations.class).autowire();
        WebTestClient webTestClient = WebTestClientBuilder.bindToWebFilters(this.springSecurity).build();
        WebDriver driver = WebTestClientHtmlUnitDriverBuilder.webTestClientSetup(webTestClient).build();
        FormLoginTests.DefaultLoginPage loginPage = to(driver, FormLoginTests.DefaultLoginPage.class).assertAt().assertLoginFormNotPresent().oauth2Login().assertClientRegistrationByName(this.github.getClientName()).and();
    }

    @EnableWebFluxSecurity
    static class OAuth2LoginWithMultipleClientRegistrations {
        @Bean
        InMemoryReactiveClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryReactiveClientRegistrationRepository(OAuth2LoginTests.github, OAuth2LoginTests.google);
        }
    }

    @Test
    public void defaultLoginPageWithSingleClientRegistrationThenRedirect() {
        this.spring.register(OAuth2LoginTests.OAuth2LoginWithSingleClientRegistrations.class).autowire();
        WebTestClient webTestClient = WebTestClientBuilder.bindToWebFilters(new OAuth2LoginTests.GitHubWebFilter(), this.springSecurity).build();
        WebDriver driver = WebTestClientHtmlUnitDriverBuilder.webTestClientSetup(webTestClient).build();
        driver.get("http://localhost/");
        assertThat(driver.getCurrentUrl()).startsWith("https://github.com/login/oauth/authorize");
    }

    @EnableWebFluxSecurity
    static class OAuth2LoginWithSingleClientRegistrations {
        @Bean
        InMemoryReactiveClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryReactiveClientRegistrationRepository(OAuth2LoginTests.github);
        }
    }

    @Test
    public void oauth2LoginWhenCustomObjectsThenUsed() {
        this.spring.register(OAuth2LoginTests.OAuth2LoginWithSingleClientRegistrations.class, OAuth2LoginTests.OAuth2LoginMockAuthenticationManagerConfig.class).autowire();
        WebTestClient webTestClient = WebTestClientBuilder.bindToWebFilters(this.springSecurity).build();
        OAuth2LoginTests.OAuth2LoginMockAuthenticationManagerConfig config = this.spring.getContext().getBean(OAuth2LoginTests.OAuth2LoginMockAuthenticationManagerConfig.class);
        ServerAuthenticationConverter converter = config.authenticationConverter;
        ReactiveAuthenticationManager manager = config.manager;
        ServerWebExchangeMatcher matcher = config.matcher;
        ServerOAuth2AuthorizationRequestResolver resolver = config.resolver;
        OAuth2AuthorizationExchange exchange = TestOAuth2AuthorizationExchanges.success();
        OAuth2User user = TestOAuth2Users.create();
        OAuth2AccessToken accessToken = TestOAuth2AccessTokens.noScopes();
        OAuth2LoginAuthenticationToken result = new OAuth2LoginAuthenticationToken(OAuth2LoginTests.github, exchange, user, user.getAuthorities(), accessToken);
        Mockito.when(converter.convert(ArgumentMatchers.any())).thenReturn(Mono.just(new TestingAuthenticationToken("a", "b", "c")));
        Mockito.when(manager.authenticate(ArgumentMatchers.any())).thenReturn(Mono.just(result));
        Mockito.when(matcher.matches(ArgumentMatchers.any())).thenReturn(MatchResult.match());
        Mockito.when(resolver.resolve(ArgumentMatchers.any())).thenReturn(Mono.empty());
        webTestClient.get().uri("/login/oauth2/code/github").exchange().expectStatus().is3xxRedirection();
        Mockito.verify(converter).convert(ArgumentMatchers.any());
        Mockito.verify(manager).authenticate(ArgumentMatchers.any());
        Mockito.verify(matcher).matches(ArgumentMatchers.any());
        Mockito.verify(resolver).resolve(ArgumentMatchers.any());
    }

    @Configuration
    static class OAuth2LoginMockAuthenticationManagerConfig {
        ReactiveAuthenticationManager manager = Mockito.mock(ReactiveAuthenticationManager.class);

        ServerAuthenticationConverter authenticationConverter = Mockito.mock(ServerAuthenticationConverter.class);

        ServerWebExchangeMatcher matcher = Mockito.mock(ServerWebExchangeMatcher.class);

        ServerOAuth2AuthorizationRequestResolver resolver = Mockito.mock(ServerOAuth2AuthorizationRequestResolver.class);

        @Bean
        public SecurityWebFilterChain springSecurityFilter(ServerHttpSecurity http) {
            http.authorizeExchange().anyExchange().authenticated().and().oauth2Login().authenticationConverter(authenticationConverter).authenticationManager(manager).authenticationMatcher(matcher).authorizationRequestResolver(resolver);
            return http.build();
        }
    }

    @Test
    public void oauth2LoginWhenCustomBeansThenUsed() {
        this.spring.register(OAuth2LoginTests.OAuth2LoginWithMultipleClientRegistrations.class, OAuth2LoginTests.OAuth2LoginWithCustomBeansConfig.class).autowire();
        WebTestClient webTestClient = WebTestClientBuilder.bindToWebFilters(this.springSecurity).build();
        OAuth2LoginTests.OAuth2LoginWithCustomBeansConfig config = this.spring.getContext().getBean(OAuth2LoginTests.OAuth2LoginWithCustomBeansConfig.class);
        OAuth2AuthorizationRequest request = TestOAuth2AuthorizationRequests.request().scope("openid").build();
        OAuth2AuthorizationResponse response = TestOAuth2AuthorizationResponses.success().build();
        OAuth2AuthorizationExchange exchange = new OAuth2AuthorizationExchange(request, response);
        OAuth2AccessToken accessToken = TestOAuth2AccessTokens.scopes("openid");
        OAuth2AuthorizationCodeAuthenticationToken token = new OAuth2AuthorizationCodeAuthenticationToken(OAuth2LoginTests.google, exchange, accessToken);
        ServerAuthenticationConverter converter = config.authenticationConverter;
        Mockito.when(converter.convert(ArgumentMatchers.any())).thenReturn(Mono.just(token));
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put(ID_TOKEN, "id-token");
        OAuth2AccessTokenResponse accessTokenResponse = OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue()).tokenType(accessToken.getTokenType()).scopes(accessToken.getScopes()).additionalParameters(additionalParameters).build();
        ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenResponseClient = config.tokenResponseClient;
        Mockito.when(tokenResponseClient.getTokenResponse(ArgumentMatchers.any())).thenReturn(Mono.just(accessTokenResponse));
        OidcUser user = TestOidcUsers.create();
        ReactiveOAuth2UserService<OidcUserRequest, OidcUser> userService = config.userService;
        Mockito.when(userService.loadUser(ArgumentMatchers.any())).thenReturn(Mono.just(user));
        webTestClient.get().uri("/login/oauth2/code/google").exchange().expectStatus().is3xxRedirection();
        Mockito.verify(config.jwtDecoderFactory).createDecoder(ArgumentMatchers.any());
        Mockito.verify(tokenResponseClient).getTokenResponse(ArgumentMatchers.any());
    }

    @Configuration
    static class OAuth2LoginWithCustomBeansConfig {
        ServerAuthenticationConverter authenticationConverter = Mockito.mock(ServerAuthenticationConverter.class);

        ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenResponseClient = Mockito.mock(ReactiveOAuth2AccessTokenResponseClient.class);

        ReactiveOAuth2UserService<OidcUserRequest, OidcUser> userService = Mockito.mock(ReactiveOAuth2UserService.class);

        ReactiveJwtDecoderFactory<ClientRegistration> jwtDecoderFactory = Mockito.spy(new OAuth2LoginTests.OAuth2LoginWithCustomBeansConfig.JwtDecoderFactory());

        @Bean
        public SecurityWebFilterChain springSecurityFilter(ServerHttpSecurity http) {
            // @formatter:off
            http.authorizeExchange().anyExchange().authenticated().and().oauth2Login().authenticationConverter(authenticationConverter).authenticationManager(authenticationManager());
            return http.build();
            // @formatter:on
        }

        private ReactiveAuthenticationManager authenticationManager() {
            OidcAuthorizationCodeReactiveAuthenticationManager oidc = new OidcAuthorizationCodeReactiveAuthenticationManager(tokenResponseClient, userService);
            oidc.setJwtDecoderFactory(jwtDecoderFactory());
            return oidc;
        }

        @Bean
        public ReactiveJwtDecoderFactory<ClientRegistration> jwtDecoderFactory() {
            return jwtDecoderFactory;
        }

        @Bean
        public ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
            return tokenResponseClient;
        }

        private static class JwtDecoderFactory implements ReactiveJwtDecoderFactory<ClientRegistration> {
            @Override
            public ReactiveJwtDecoder createDecoder(ClientRegistration clientRegistration) {
                return getJwtDecoder();
            }

            private ReactiveJwtDecoder getJwtDecoder() {
                return ( token) -> {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put(IdTokenClaimNames.SUB, "subject");
                    claims.put(IdTokenClaimNames.ISS, "http://localhost/issuer");
                    claims.put(IdTokenClaimNames.AUD, Collections.singletonList("client"));
                    claims.put(IdTokenClaimNames.AZP, "client");
                    Jwt jwt = new Jwt("id-token", Instant.now(), Instant.now().plusSeconds(3600), Collections.singletonMap("header1", "value1"), claims);
                    return Mono.just(jwt);
                };
            }
        }
    }

    static class GitHubWebFilter implements WebFilter {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            if (exchange.getRequest().getURI().getHost().equals("github.com")) {
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        }
    }
}

