/**
 * Copyright 2002-2017 the original author or authors.
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


import AnonymousAuthenticationWebFilterTests.HttpMeController;
import SecurityWebFiltersOrder.SECURITY_CONTEXT_SERVER_WEB_EXCHANGE;
import ServerHttpSecurity.AuthorizeExchangeSpec;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.http.HttpHeaders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.web.reactive.server.WebTestClientBuilder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.LogoutWebFilter;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.context.SecurityContextServerWebExchangeWebFilter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.CsrfServerLogoutHandler;
import org.springframework.security.web.server.csrf.CsrfWebFilter;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.publisher.TestPublisher;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ServerHttpSecurityTests {
    @Mock
    private ServerSecurityContextRepository contextRepository;

    @Mock
    private ReactiveAuthenticationManager authenticationManager;

    @Mock
    private ServerCsrfTokenRepository csrfTokenRepository;

    private ServerHttpSecurity http;

    @Test
    public void defaults() {
        TestPublisher<SecurityContext> securityContext = TestPublisher.create();
        Mockito.when(this.contextRepository.load(ArgumentMatchers.any())).thenReturn(securityContext.mono());
        this.http.securityContextRepository(this.contextRepository);
        WebTestClient client = buildClient();
        FluxExchangeResult<String> result = client.get().uri("/").exchange().expectHeader().valueMatches(HttpHeaders.CACHE_CONTROL, ".+").returnResult(String.class);
        assertThat(result.getResponseCookies()).isEmpty();
        // there is no need to try and load the SecurityContext by default
        securityContext.assertWasNotSubscribed();
    }

    @Test
    public void basic() {
        BDDMockito.given(this.authenticationManager.authenticate(ArgumentMatchers.any())).willReturn(Mono.just(new TestingAuthenticationToken("rob", "rob", "ROLE_USER", "ROLE_ADMIN")));
        this.http.securityContextRepository(new WebSessionServerSecurityContextRepository());
        this.http.httpBasic();
        this.http.authenticationManager(this.authenticationManager);
        ServerHttpSecurity.AuthorizeExchangeSpec authorize = this.http.authorizeExchange();
        authorize.anyExchange().authenticated();
        WebTestClient client = buildClient();
        EntityExchangeResult<String> result = client.get().uri("/").headers(( headers) -> headers.setBasicAuth("rob", "rob")).exchange().expectStatus().isOk().expectHeader().valueMatches(HttpHeaders.CACHE_CONTROL, ".+").expectBody(String.class).consumeWith(( b) -> assertThat(b.getResponseBody()).isEqualTo("ok")).returnResult();
        assertThat(result.getResponseCookies().getFirst("SESSION")).isNull();
    }

    @Test
    public void basicWhenNoCredentialsThenUnauthorized() {
        this.http.authorizeExchange().anyExchange().authenticated();
        WebTestClient client = buildClient();
        client.get().uri("/").exchange().expectStatus().isUnauthorized().expectHeader().valueMatches(HttpHeaders.CACHE_CONTROL, ".+").expectBody().isEmpty();
    }

    @Test
    public void buildWhenServerWebExchangeFromContextThenFound() {
        SecurityWebFilterChain filter = this.http.build();
        WebTestClient client = WebTestClient.bindToController(new ServerHttpSecurityTests.SubscriberContextController()).webFilter(new org.springframework.security.web.server.WebFilterChainProxy(filter)).build();
        client.get().uri("/foo/bar").exchange().expectBody(String.class).isEqualTo("/foo/bar");
    }

    @Test
    public void csrfServerLogoutHandlerNotAppliedIfCsrfIsntEnabled() {
        SecurityWebFilterChain securityWebFilterChain = this.http.csrf().disable().build();
        assertThat(getWebFilter(securityWebFilterChain, CsrfWebFilter.class)).isNotPresent();
        Optional<ServerLogoutHandler> logoutHandler = getWebFilter(securityWebFilterChain, LogoutWebFilter.class).map(( logoutWebFilter) -> ((ServerLogoutHandler) (ReflectionTestUtils.getField(logoutWebFilter, .class, "logoutHandler"))));
        assertThat(logoutHandler).get().isExactlyInstanceOf(SecurityContextServerLogoutHandler.class);
    }

    @Test
    public void csrfServerLogoutHandlerAppliedIfCsrfIsEnabled() {
        SecurityWebFilterChain securityWebFilterChain = this.http.csrf().csrfTokenRepository(this.csrfTokenRepository).and().build();
        assertThat(getWebFilter(securityWebFilterChain, CsrfWebFilter.class)).get().extracting(( csrfWebFilter) -> ReflectionTestUtils.getField(csrfWebFilter, "csrfTokenRepository")).isEqualTo(this.csrfTokenRepository);
        Optional<ServerLogoutHandler> logoutHandler = getWebFilter(securityWebFilterChain, LogoutWebFilter.class).map(( logoutWebFilter) -> ((ServerLogoutHandler) (ReflectionTestUtils.getField(logoutWebFilter, .class, "logoutHandler"))));
        assertThat(logoutHandler).get().isExactlyInstanceOf(DelegatingServerLogoutHandler.class).extracting(( delegatingLogoutHandler) -> ((List<ServerLogoutHandler>) (ReflectionTestUtils.getField(delegatingLogoutHandler, .class, "delegates"))).stream().map(ServerLogoutHandler::getClass).collect(Collectors.toList())).isEqualTo(Arrays.asList(SecurityContextServerLogoutHandler.class, CsrfServerLogoutHandler.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void addFilterAfterIsApplied() {
        SecurityWebFilterChain securityWebFilterChain = this.http.addFilterAfter(new ServerHttpSecurityTests.TestWebFilter(), SECURITY_CONTEXT_SERVER_WEB_EXCHANGE).build();
        java.util.List filters = securityWebFilterChain.getWebFilters().map(WebFilter::getClass).collectList().block();
        assertThat(filters).isNotNull().isNotEmpty().containsSequence(SecurityContextServerWebExchangeWebFilter.class, ServerHttpSecurityTests.TestWebFilter.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void addFilterBeforeIsApplied() {
        SecurityWebFilterChain securityWebFilterChain = this.http.addFilterBefore(new ServerHttpSecurityTests.TestWebFilter(), SECURITY_CONTEXT_SERVER_WEB_EXCHANGE).build();
        java.util.List filters = securityWebFilterChain.getWebFilters().map(WebFilter::getClass).collectList().block();
        assertThat(filters).isNotNull().isNotEmpty().containsSequence(ServerHttpSecurityTests.TestWebFilter.class, SecurityContextServerWebExchangeWebFilter.class);
    }

    @Test
    public void anonymous() {
        SecurityWebFilterChain securityFilterChain = this.http.anonymous().and().build();
        WebTestClient client = WebTestClientBuilder.bindToControllerAndWebFilters(HttpMeController.class, securityFilterChain).build();
        client.get().uri("/me").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("anonymousUser");
    }

    @Test
    public void basicWithAnonymous() {
        BDDMockito.given(this.authenticationManager.authenticate(ArgumentMatchers.any())).willReturn(Mono.just(new TestingAuthenticationToken("rob", "rob", "ROLE_USER", "ROLE_ADMIN")));
        this.http.securityContextRepository(new WebSessionServerSecurityContextRepository());
        this.http.httpBasic().and().anonymous();
        this.http.authenticationManager(this.authenticationManager);
        ServerHttpSecurity.AuthorizeExchangeSpec authorize = this.http.authorizeExchange();
        authorize.anyExchange().hasAuthority("ROLE_ADMIN");
        WebTestClient client = buildClient();
        EntityExchangeResult<String> result = client.get().uri("/").headers(( headers) -> headers.setBasicAuth("rob", "rob")).exchange().expectStatus().isOk().expectHeader().valueMatches(HttpHeaders.CACHE_CONTROL, ".+").expectBody(String.class).consumeWith(( b) -> assertThat(b.getResponseBody()).isEqualTo("ok")).returnResult();
        assertThat(result.getResponseCookies().getFirst("SESSION")).isNull();
    }

    @Test
    public void basicWithCustomRealmName() {
        this.http.securityContextRepository(new WebSessionServerSecurityContextRepository());
        HttpBasicServerAuthenticationEntryPoint authenticationEntryPoint = new HttpBasicServerAuthenticationEntryPoint();
        authenticationEntryPoint.setRealm("myrealm");
        this.http.httpBasic().authenticationEntryPoint(authenticationEntryPoint);
        this.http.authenticationManager(this.authenticationManager);
        ServerHttpSecurity.AuthorizeExchangeSpec authorize = this.http.authorizeExchange();
        authorize.anyExchange().authenticated();
        WebTestClient client = buildClient();
        EntityExchangeResult<String> result = client.get().uri("/").exchange().expectStatus().isUnauthorized().expectHeader().value(HttpHeaders.WWW_AUTHENTICATE, ( value) -> assertThat(value).contains("myrealm")).expectBody(String.class).returnResult();
        assertThat(result.getResponseCookies().getFirst("SESSION")).isNull();
    }

    @RestController
    private static class SubscriberContextController {
        @GetMapping("/**")
        Mono<String> pathWithinApplicationFromContext() {
            return Mono.subscriberContext().filter(( c) -> c.hasKey(.class)).map(( c) -> c.get(.class)).map(( e) -> e.getRequest().getPath().pathWithinApplication().value());
        }
    }

    private static class TestWebFilter implements WebFilter {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            return chain.filter(exchange);
        }
    }
}

