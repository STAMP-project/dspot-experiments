/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.security.oauth2.resource.reactive;


import java.io.IOException;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;


/**
 * Tests for {@link ReactiveOAuth2ResourceServerAutoConfiguration}.
 *
 * @author Madhura Bhave
 * @author Artsiom Yudovin
 */
public class ReactiveOAuth2ResourceServerAutoConfigurationTests {
    private ReactiveWebApplicationContextRunner contextRunner = new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(ReactiveOAuth2ResourceServerAutoConfiguration.class)).withUserConfiguration(ReactiveOAuth2ResourceServerAutoConfigurationTests.TestConfig.class);

    private MockWebServer server;

    @Test
    public void autoConfigurationShouldConfigureResourceServer() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").run(( context) -> {
            assertThat(context.getBean(.class)).isInstanceOf(.class);
            assertFilterConfiguredWithJwtAuthenticationManager(context);
        });
    }

    @Test
    public void autoConfigurationShouldConfigureResourceServerUsingOidcIssuerUri() throws IOException {
        this.server = new MockWebServer();
        this.server.start();
        String issuer = this.server.url("").toString();
        String cleanIssuerPath = cleanIssuerPath(issuer);
        setupMockResponse(cleanIssuerPath);
        this.contextRunner.withPropertyValues(((("spring.security.oauth2.resourceserver.jwt.issuer-uri=http://" + (this.server.getHostName())) + ":") + (this.server.getPort()))).run(( context) -> {
            assertThat(context.getBean(.class)).isInstanceOf(.class);
            assertFilterConfiguredWithJwtAuthenticationManager(context);
        });
    }

    @Test
    public void autoConfigurationWhenBothSetUriAndIssuerUriPresentShouldUseSetUri() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com", "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://jwk-oidc-issuer-location.com").run(( context) -> {
            assertThat(context.getBean(.class)).isInstanceOf(.class);
            assertFilterConfiguredWithJwtAuthenticationManager(context);
            assertThat(context.containsBean("jwtDecoder")).isTrue();
            assertThat(context.containsBean("jwtDecoderByIssuerUri")).isFalse();
        });
    }

    @Test
    public void autoConfigurationWhenJwkSetUriNullShouldNotFail() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(BeanIds.SPRING_SECURITY_FILTER_CHAIN));
    }

    @Test
    public void jwtDecoderBeanIsConditionalOnMissingBean() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").withUserConfiguration(ReactiveOAuth2ResourceServerAutoConfigurationTests.JwtDecoderConfig.class).run(this::assertFilterConfiguredWithJwtAuthenticationManager);
    }

    @Test
    public void jwtDecoderByIssuerUriBeanIsConditionalOnMissingBean() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.issuer-uri=http://jwk-oidc-issuer-location.com").withUserConfiguration(ReactiveOAuth2ResourceServerAutoConfigurationTests.JwtDecoderConfig.class).run(this::assertFilterConfiguredWithJwtAuthenticationManager);
    }

    @Test
    public void autoConfigurationShouldBeConditionalOnBearerTokenAuthenticationTokenClass() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").withUserConfiguration(ReactiveOAuth2ResourceServerAutoConfigurationTests.JwtDecoderConfig.class).withClassLoader(new FilteredClassLoader(BearerTokenAuthenticationToken.class)).run(( context) -> assertThat(context).doesNotHaveBean(BeanIds.SPRING_SECURITY_FILTER_CHAIN));
    }

    @Test
    public void autoConfigurationShouldBeConditionalOnReactiveJwtDecoderClass() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").withUserConfiguration(ReactiveOAuth2ResourceServerAutoConfigurationTests.JwtDecoderConfig.class).withClassLoader(new FilteredClassLoader(ReactiveJwtDecoder.class)).run(( context) -> assertThat(context).doesNotHaveBean(BeanIds.SPRING_SECURITY_FILTER_CHAIN));
    }

    @Test
    public void autoConfigurationWhenSecurityWebFilterChainConfigPresentShouldNotAddOne() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").withUserConfiguration(ReactiveOAuth2ResourceServerAutoConfigurationTests.SecurityWebFilterChainConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("testSpringSecurityFilterChain");
        });
    }

    @EnableWebFluxSecurity
    static class TestConfig {
        @Bean
        public MapReactiveUserDetailsService userDetailsService() {
            return Mockito.mock(MapReactiveUserDetailsService.class);
        }
    }

    @Configuration
    static class JwtDecoderConfig {
        @Bean
        public ReactiveJwtDecoder decoder() {
            return Mockito.mock(ReactiveJwtDecoder.class);
        }
    }

    @Configuration
    static class SecurityWebFilterChainConfig {
        @Bean
        SecurityWebFilterChain testSpringSecurityFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder decoder) {
            http.authorizeExchange().pathMatchers("/message/**").hasRole("ADMIN").anyExchange().authenticated().and().oauth2ResourceServer().jwt().jwtDecoder(decoder);
            return http.build();
        }
    }
}

