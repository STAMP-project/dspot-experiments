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


import HttpStatus.BANDWIDTH_LIMIT_EXCEEDED;
import HttpStatus.I_AM_A_TEAPOT;
import JwsAlgorithms.RS256;
import ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PreDestroy;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.hamcrest.core.StringStartsWith;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.BANDWIDTH_LIMIT_EXCEEDED;
import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;


/**
 * Tests for {@link org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2ResourceServerSpec}
 */
@RunWith(SpringRunner.class)
public class OAuth2ResourceServerSpecTests {
    private String expired = "eyJhbGciOiJSUzI1NiJ9.eyJleHAiOjE1MzUwMzc4OTd9.jqZDDjfc2eysX44lHXEIr9XFd2S8vjIZHCccZU-dRWMRJNsQ1QN5VNnJGklqJBXJR4qgla6cmVqPOLkUHDb0sL0nxM5XuzQaG5ZzKP81RV88shFyAiT0fD-6nl1k-Fai-Fu-VkzSpNXgeONoTxDaYhdB-yxmgrgsApgmbOTE_9AcMk-FQDXQ-pL9kynccFGV0lZx4CA7cyknKN7KBxUilfIycvXODwgKCjj_1WddLTCNGYogJJSg__7NoxzqbyWd3udbHVjqYq7GsMMrGB4_2kBD4CkghOSNcRHbT_DIXowxfAVT7PAg7Q0E5ruZsr2zPZacEUDhJ6-wbvlA0FAOUg";

    private String messageReadToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJtb2NrLXN1YmplY3QiLCJzY29wZSI6Im1lc3NhZ2U6cmVhZCIsImV4cCI6NDY4ODY0MTQxM30.cRl1bv_dDYcAN5U4NlIVKj8uu4mLMwjABF93P4dShiq-GQ-owzaqTSlB4YarNFgV3PKQvT9wxN1jBpGribvISljakoC0E8wDV-saDi8WxN-qvImYsn1zLzYFiZXCfRIxCmonJpydeiAPRxMTPtwnYDS9Ib0T_iA80TBGd-INhyxUUfrwRW5sqKRbjUciRJhpp7fW2ZYXmi9iPt3HDjRQA4IloJZ7f4-spt5Q9wl5HcQTv1t4XrX4eqhVbE5cCoIkFQnKPOc-jhVM44_eazLU6Xk-CCXP8C_UT5pX0luRS2cJrVFfHp2IR_AWxC-shItg6LNEmNFD4Zc-JLZcr0Q86Q";

    private String messageReadTokenWithKid = "eyJraWQiOiJvbmUiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJtb2NrLXN1YmplY3QiLCJzY29wZSI6Im1lc3NhZ2U6cmVhZCIsImV4cCI6NDY4ODY0MTQ2MX0.Arg3IjlNb_nkEIZpcWAQquvoiaeF_apJzO5ZxSzUQEWixH1Y7yrsW2uco452a7OtAKDNT09IplK8126z_hdI_RRk0CXVsGZYe1qppNIVLEPGv4rHxND4bPv1YA91Q8vG-vDk9rod7EvAuZU1tEP_pWkSkZVAmfuP43bP5FQcO6Q31Aba7Yb7O5qWn9U2MjruPSFvTsIx3hSXgTuJxhNCKeHnTCmv2WdjYWatR7-VujBlHd-ZolysXm7-JPz3kI75omnomG2UqnKkI76sczIpm4ieOp3fSyv-QR-i-3Z_eJ9hS3Ox46Y9NJS6Z-y1g3X0fjVyhLiIJkFV3VA5HrSf_A";

    private String unsignedToken = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJleHAiOi0yMDMzMjI0OTcsImp0aSI6IjEyMyIsInR5cCI6IkpXVCJ9.";

    private String jwkSet = "{\n" + ((((((((("  \"keys\":[\n" + "    {\n") + "      \"kty\":\"RSA\",\n") + "      \"e\":\"AQAB\",\n") + "      \"use\":\"sig\",\n") + "      \"kid\":\"one\",\n") + "      \"n\":\"0IUjrPZDz-3z0UE4ppcKU36v7hnh8FJjhu3lbJYj0qj9eZiwEJxi9HHUfSK1DhUQG7mJBbYTK1tPYCgre5EkfKh-64VhYUa-vz17zYCmuB8fFj4XHE3MLkWIG-AUn8hNbPzYYmiBTjfGnMKxLHjsbdTiF4mtn-85w366916R6midnAuiPD4HjZaZ1PAsuY60gr8bhMEDtJ8unz81hoQrozpBZJ6r8aR1PrsWb1OqPMloK9kAIutJNvWYKacp8WYAp2WWy72PxQ7Fb0eIA1br3A5dnp-Cln6JROJcZUIRJ-QvS6QONWeS2407uQmS-i-lybsqaH0ldYC7NBEBA5inPQ\"\n") + "    }\n") + "  ]\n") + "}\n");

    private Jwt jwt = new Jwt("token", Instant.MIN, Instant.MAX, Collections.singletonMap("alg", RS256), Collections.singletonMap("sub", "user"));

    private String clientId = "client";

    private String clientSecret = "secret";

    private String active = "{\n" + (((((((((("      \"active\": true,\n" + "      \"client_id\": \"l238j323ds-23ij4\",\n") + "      \"username\": \"jdoe\",\n") + "      \"scope\": \"read write dolphin\",\n") + "      \"sub\": \"Z5O3upPC88QrAjx00dis\",\n") + "      \"aud\": \"https://protected.example.net/resource\",\n") + "      \"iss\": \"https://server.example.com/\",\n") + "      \"exp\": 1419356238,\n") + "      \"iat\": 1419350238,\n") + "      \"extension_field\": \"twenty-seven\"\n") + "     }");

    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    WebTestClient client;

    @Test
    public void getWhenValidThenReturnsOk() {
        this.spring.register(OAuth2ResourceServerSpecTests.PublicKeyConfig.class, OAuth2ResourceServerSpecTests.RootController.class).autowire();
        this.client.get().headers(( headers) -> headers.setBearerAuth(this.messageReadToken)).exchange().expectStatus().isOk();
    }

    @Test
    public void getWhenExpiredThenReturnsInvalidToken() {
        this.spring.register(OAuth2ResourceServerSpecTests.PublicKeyConfig.class).autowire();
        this.client.get().headers(( headers) -> headers.setBearerAuth(this.expired)).exchange().expectStatus().isUnauthorized().expectHeader().value(HttpHeaders.WWW_AUTHENTICATE, StringStartsWith.startsWith("Bearer error=\"invalid_token\""));
    }

    @Test
    public void getWhenUnsignedThenReturnsInvalidToken() {
        this.spring.register(OAuth2ResourceServerSpecTests.PublicKeyConfig.class).autowire();
        this.client.get().headers(( headers) -> headers.setBearerAuth(this.unsignedToken)).exchange().expectStatus().isUnauthorized().expectHeader().value(HttpHeaders.WWW_AUTHENTICATE, StringStartsWith.startsWith("Bearer error=\"invalid_token\""));
    }

    @Test
    public void getWhenCustomDecoderThenAuthenticatesAccordingly() {
        this.spring.register(OAuth2ResourceServerSpecTests.CustomDecoderConfig.class, OAuth2ResourceServerSpecTests.RootController.class).autowire();
        ReactiveJwtDecoder jwtDecoder = this.spring.getContext().getBean(ReactiveJwtDecoder.class);
        Mockito.when(jwtDecoder.decode(ArgumentMatchers.anyString())).thenReturn(Mono.just(this.jwt));
        this.client.get().headers(( headers) -> headers.setBearerAuth("token")).exchange().expectStatus().isOk();
        Mockito.verify(jwtDecoder).decode(ArgumentMatchers.anyString());
    }

    @Test
    public void getWhenUsingJwkSetUriThenConsultsAccordingly() {
        this.spring.register(OAuth2ResourceServerSpecTests.JwkSetUriConfig.class, OAuth2ResourceServerSpecTests.RootController.class).autowire();
        MockWebServer mockWebServer = this.spring.getContext().getBean(MockWebServer.class);
        mockWebServer.enqueue(new MockResponse().setBody(this.jwkSet));
        this.client.get().headers(( headers) -> headers.setBearerAuth(this.messageReadTokenWithKid)).exchange().expectStatus().isOk();
    }

    @Test
    public void getWhenUsingCustomAuthenticationManagerThenUsesItAccordingly() {
        this.spring.register(OAuth2ResourceServerSpecTests.CustomAuthenticationManagerConfig.class).autowire();
        ReactiveAuthenticationManager authenticationManager = this.spring.getContext().getBean(ReactiveAuthenticationManager.class);
        Mockito.when(authenticationManager.authenticate(ArgumentMatchers.any(Authentication.class))).thenReturn(Mono.error(new org.springframework.security.oauth2.core.OAuth2AuthenticationException(new OAuth2Error("mock-failure"))));
        this.client.get().headers(( headers) -> headers.setBearerAuth(this.messageReadToken)).exchange().expectStatus().isUnauthorized().expectHeader().value(HttpHeaders.WWW_AUTHENTICATE, StringStartsWith.startsWith("Bearer error=\"mock-failure\""));
    }

    @Test
    public void postWhenSignedThenReturnsOk() {
        this.spring.register(OAuth2ResourceServerSpecTests.PublicKeyConfig.class, OAuth2ResourceServerSpecTests.RootController.class).autowire();
        this.client.post().headers(( headers) -> headers.setBearerAuth(this.messageReadToken)).exchange().expectStatus().isOk();
    }

    @Test
    public void getWhenTokenHasInsufficientScopeThenReturnsInsufficientScope() {
        this.spring.register(OAuth2ResourceServerSpecTests.DenyAllConfig.class, OAuth2ResourceServerSpecTests.RootController.class).autowire();
        this.client.get().headers(( headers) -> headers.setBearerAuth(this.messageReadToken)).exchange().expectStatus().isForbidden().expectHeader().value(HttpHeaders.WWW_AUTHENTICATE, StringStartsWith.startsWith("Bearer error=\"insufficient_scope\""));
    }

    @Test
    public void postWhenMissingTokenThenReturnsForbidden() {
        this.spring.register(OAuth2ResourceServerSpecTests.PublicKeyConfig.class, OAuth2ResourceServerSpecTests.RootController.class).autowire();
        this.client.post().exchange().expectStatus().isForbidden();
    }

    @Test
    public void getWhenCustomBearerTokenServerAuthenticationConverterThenResponds() {
        this.spring.register(OAuth2ResourceServerSpecTests.CustomBearerTokenServerAuthenticationConverter.class, OAuth2ResourceServerSpecTests.RootController.class).autowire();
        this.client.get().cookie("TOKEN", this.messageReadToken).exchange().expectStatus().isOk();
    }

    @Test
    public void getWhenSignedAndCustomConverterThenConverts() {
        this.spring.register(OAuth2ResourceServerSpecTests.CustomJwtAuthenticationConverterConfig.class, OAuth2ResourceServerSpecTests.RootController.class).autowire();
        this.client.get().headers(( headers) -> headers.setBearerAuth(this.messageReadToken)).exchange().expectStatus().isOk();
    }

    @Test
    public void getWhenCustomBearerTokenEntryPointThenResponds() {
        this.spring.register(OAuth2ResourceServerSpecTests.CustomErrorHandlingConfig.class).autowire();
        this.client.get().uri("/authenticated").exchange().expectStatus().isEqualTo(I_AM_A_TEAPOT);
    }

    @Test
    public void getWhenCustomBearerTokenDeniedHandlerThenResponds() {
        this.spring.register(OAuth2ResourceServerSpecTests.CustomErrorHandlingConfig.class).autowire();
        this.client.get().uri("/unobtainable").headers(( headers) -> headers.setBearerAuth(this.messageReadToken)).exchange().expectStatus().isEqualTo(BANDWIDTH_LIMIT_EXCEEDED);
    }

    @Test
    public void getJwtDecoderWhenBeanWiredAndDslWiredThenDslTakesPrecedence() {
        GenericWebApplicationContext context = autowireWebServerGenericWebApplicationContext();
        ServerHttpSecurity http = new ServerHttpSecurity();
        http.setApplicationContext(context);
        ReactiveJwtDecoder beanWiredJwtDecoder = Mockito.mock(ReactiveJwtDecoder.class);
        ReactiveJwtDecoder dslWiredJwtDecoder = Mockito.mock(ReactiveJwtDecoder.class);
        context.registerBean(ReactiveJwtDecoder.class, () -> beanWiredJwtDecoder);
        ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec jwt = http.oauth2ResourceServer().jwt();
        jwt.jwtDecoder(dslWiredJwtDecoder);
        assertThat(jwt.getJwtDecoder()).isEqualTo(dslWiredJwtDecoder);
    }

    @Test
    public void getJwtDecoderWhenTwoBeansWiredAndDslWiredThenDslTakesPrecedence() {
        GenericWebApplicationContext context = autowireWebServerGenericWebApplicationContext();
        ServerHttpSecurity http = new ServerHttpSecurity();
        http.setApplicationContext(context);
        ReactiveJwtDecoder beanWiredJwtDecoder = Mockito.mock(ReactiveJwtDecoder.class);
        ReactiveJwtDecoder dslWiredJwtDecoder = Mockito.mock(ReactiveJwtDecoder.class);
        context.registerBean("firstJwtDecoder", ReactiveJwtDecoder.class, () -> beanWiredJwtDecoder);
        context.registerBean("secondJwtDecoder", ReactiveJwtDecoder.class, () -> beanWiredJwtDecoder);
        ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec jwt = http.oauth2ResourceServer().jwt();
        jwt.jwtDecoder(dslWiredJwtDecoder);
        assertThat(jwt.getJwtDecoder()).isEqualTo(dslWiredJwtDecoder);
    }

    @Test
    public void getJwtDecoderWhenTwoBeansWiredThenThrowsWiringException() {
        GenericWebApplicationContext context = autowireWebServerGenericWebApplicationContext();
        ServerHttpSecurity http = new ServerHttpSecurity();
        http.setApplicationContext(context);
        ReactiveJwtDecoder beanWiredJwtDecoder = Mockito.mock(ReactiveJwtDecoder.class);
        context.registerBean("firstJwtDecoder", ReactiveJwtDecoder.class, () -> beanWiredJwtDecoder);
        context.registerBean("secondJwtDecoder", ReactiveJwtDecoder.class, () -> beanWiredJwtDecoder);
        ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec jwt = http.oauth2ResourceServer().jwt();
        assertThatCode(() -> jwt.getJwtDecoder()).isInstanceOf(NoUniqueBeanDefinitionException.class);
    }

    @Test
    public void getJwtDecoderWhenNoBeansAndNoDslWiredThenWiringException() {
        GenericWebApplicationContext context = autowireWebServerGenericWebApplicationContext();
        ServerHttpSecurity http = new ServerHttpSecurity();
        http.setApplicationContext(context);
        ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec jwt = http.oauth2ResourceServer().jwt();
        assertThatCode(() -> jwt.getJwtDecoder()).isInstanceOf(NoSuchBeanDefinitionException.class);
    }

    @Test
    public void introspectWhenValidThenReturnsOk() {
        this.spring.register(OAuth2ResourceServerSpecTests.IntrospectionConfig.class, OAuth2ResourceServerSpecTests.RootController.class).autowire();
        this.spring.getContext().getBean(MockWebServer.class).setDispatcher(OAuth2ResourceServerSpecTests.requiresAuth(clientId, clientSecret, active));
        this.client.get().headers(( headers) -> headers.setBearerAuth(this.messageReadToken)).exchange().expectStatus().isOk();
    }

    @EnableWebFlux
    @EnableWebFluxSecurity
    static class PublicKeyConfig {
        @Bean
        SecurityWebFilterChain springSecurity(ServerHttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeExchange().anyExchange().hasAuthority("SCOPE_message:read").and().oauth2ResourceServer().jwt().publicKey(OAuth2ResourceServerSpecTests.publicKey());
            // @formatter:on
            return http.build();
        }
    }

    @EnableWebFlux
    @EnableWebFluxSecurity
    static class JwkSetUriConfig {
        private MockWebServer mockWebServer = new MockWebServer();

        @Bean
        SecurityWebFilterChain springSecurity(ServerHttpSecurity http) {
            String jwkSetUri = mockWebServer().url("/.well-known/jwks.json").toString();
            // @formatter:off
            http.oauth2ResourceServer().jwt().jwkSetUri(jwkSetUri);
            // @formatter:on
            return http.build();
        }

        @Bean
        MockWebServer mockWebServer() {
            return this.mockWebServer;
        }

        @PreDestroy
        void shutdown() throws IOException {
            this.mockWebServer.shutdown();
        }
    }

    @EnableWebFlux
    @EnableWebFluxSecurity
    static class CustomDecoderConfig {
        ReactiveJwtDecoder jwtDecoder = Mockito.mock(ReactiveJwtDecoder.class);

        @Bean
        SecurityWebFilterChain springSecurity(ServerHttpSecurity http) {
            // @formatter:off
            http.oauth2ResourceServer().jwt();
            // @formatter:on
            return http.build();
        }

        @Bean
        ReactiveJwtDecoder jwtDecoder() {
            return this.jwtDecoder;
        }
    }

    @EnableWebFlux
    @EnableWebFluxSecurity
    static class DenyAllConfig {
        @Bean
        SecurityWebFilterChain authorization(ServerHttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeExchange().anyExchange().denyAll().and().oauth2ResourceServer().jwt().publicKey(OAuth2ResourceServerSpecTests.publicKey());
            // @formatter:on
            return http.build();
        }
    }

    @EnableWebFlux
    @EnableWebFluxSecurity
    static class CustomAuthenticationManagerConfig {
        @Bean
        SecurityWebFilterChain springSecurity(ServerHttpSecurity http) {
            // @formatter:off
            http.oauth2ResourceServer().jwt().authenticationManager(authenticationManager());
            // @formatter:on
            return http.build();
        }

        @Bean
        ReactiveAuthenticationManager authenticationManager() {
            return Mockito.mock(ReactiveAuthenticationManager.class);
        }
    }

    @EnableWebFlux
    @EnableWebFluxSecurity
    static class CustomBearerTokenServerAuthenticationConverter {
        @Bean
        SecurityWebFilterChain springSecurity(ServerHttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeExchange().anyExchange().hasAuthority("SCOPE_message:read").and().oauth2ResourceServer().bearerTokenConverter(bearerTokenAuthenticationConverter()).jwt().publicKey(OAuth2ResourceServerSpecTests.publicKey());
            // @formatter:on
            return http.build();
        }

        @Bean
        ServerAuthenticationConverter bearerTokenAuthenticationConverter() {
            return ( exchange) -> Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("TOKEN").getValue()).map(BearerTokenAuthenticationToken::new);
        }
    }

    @EnableWebFlux
    @EnableWebFluxSecurity
    static class CustomJwtAuthenticationConverterConfig {
        @Bean
        SecurityWebFilterChain springSecurity(ServerHttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeExchange().anyExchange().hasAuthority("message:read").and().oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter()).publicKey(OAuth2ResourceServerSpecTests.publicKey());
            // @formatter:on
            return http.build();
        }

        @Bean
        Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter() {
                @Override
                protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
                    String[] claims = ((String) (jwt.getClaims().get("scope"))).split(" ");
                    return Stream.of(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                }
            };
            return new org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter(converter);
        }
    }

    @EnableWebFlux
    @EnableWebFluxSecurity
    static class CustomErrorHandlingConfig {
        @Bean
        SecurityWebFilterChain springSecurity(ServerHttpSecurity http) throws Exception {
            // @formatter:off
            http.authorizeExchange().pathMatchers("/authenticated").authenticated().pathMatchers("/unobtainable").hasAuthority("unobtainable").and().oauth2ResourceServer().accessDeniedHandler(new org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler(BANDWIDTH_LIMIT_EXCEEDED)).authenticationEntryPoint(new org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint(I_AM_A_TEAPOT)).jwt().publicKey(OAuth2ResourceServerSpecTests.publicKey());
            // @formatter:on
            return http.build();
        }
    }

    @EnableWebFlux
    @EnableWebFluxSecurity
    static class IntrospectionConfig {
        private MockWebServer mockWebServer = new MockWebServer();

        @Bean
        SecurityWebFilterChain springSecurity(ServerHttpSecurity http) {
            String introspectionUri = mockWebServer().url("/introspect").toString();
            // @formatter:off
            http.oauth2ResourceServer().opaqueToken().introspectionUri(introspectionUri).introspectionClientCredentials("client", "secret");
            // @formatter:on
            return http.build();
        }

        @Bean
        MockWebServer mockWebServer() {
            return this.mockWebServer;
        }

        @PreDestroy
        void shutdown() throws IOException {
            this.mockWebServer.shutdown();
        }
    }

    @RestController
    static class RootController {
        @GetMapping
        Mono<String> get() {
            return Mono.just("ok");
        }

        @PostMapping
        Mono<String> post() {
            return Mono.just("ok");
        }
    }
}

