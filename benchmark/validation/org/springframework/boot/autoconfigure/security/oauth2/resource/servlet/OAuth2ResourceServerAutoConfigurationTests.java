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
package org.springframework.boot.autoconfigure.security.oauth2.resource.servlet;


import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;


/**
 * Tests for {@link OAuth2ResourceServerAutoConfiguration}.
 *
 * @author Madhura Bhave
 * @author Artsiom Yudovin
 */
public class OAuth2ResourceServerAutoConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(OAuth2ResourceServerAutoConfiguration.class)).withUserConfiguration(OAuth2ResourceServerAutoConfigurationTests.TestConfig.class);

    private MockWebServer server;

    @Test
    public void autoConfigurationShouldConfigureResourceServer() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").run(( context) -> {
            assertThat(context.getBean(.class)).isInstanceOf(.class);
            assertThat(getBearerTokenFilter(context)).isNotNull();
        });
    }

    @Test
    public void autoConfigurationShouldMatchDefaultJwsAlgorithm() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").run(( context) -> {
            JwtDecoder jwtDecoder = context.getBean(.class);
            assertThat(jwtDecoder).hasFieldOrPropertyWithValue("jwsAlgorithm", JWSAlgorithm.RS256);
        });
    }

    @Test
    public void autoConfigurationShouldConfigureResourceServerWithJwsAlgorithm() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com", "spring.security.oauth2.resourceserver.jwt.jws-algorithm=HS512").run(( context) -> {
            JwtDecoder jwtDecoder = context.getBean(.class);
            assertThat(jwtDecoder).hasFieldOrPropertyWithValue("jwsAlgorithm", JWSAlgorithm.HS512);
            assertThat(getBearerTokenFilter(context)).isNotNull();
        });
    }

    @Test
    public void autoConfigurationShouldConfigureResourceServerUsingOidcIssuerUri() throws Exception {
        this.server = new MockWebServer();
        this.server.start();
        String issuer = this.server.url("").toString();
        String cleanIssuerPath = cleanIssuerPath(issuer);
        setupMockResponse(cleanIssuerPath);
        this.contextRunner.withPropertyValues(((("spring.security.oauth2.resourceserver.jwt.issuer-uri=http://" + (this.server.getHostName())) + ":") + (this.server.getPort()))).run(( context) -> {
            assertThat(context.getBean(.class)).isInstanceOf(.class);
            assertThat(getBearerTokenFilter(context)).isNotNull();
        });
    }

    @Test
    public void autoConfigurationWhenBothSetUriAndIssuerUriPresentShouldUseSetUri() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.issuer-uri=http://issuer-uri.com", "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").run(( context) -> {
            assertThat(context.getBean(.class)).isInstanceOf(.class);
            assertThat(getBearerTokenFilter(context)).isNotNull();
            assertThat(context.containsBean("jwtDecoderByJwkKeySetUri")).isTrue();
            assertThat(context.containsBean("jwtDecoderByOidcIssuerUri")).isFalse();
        });
    }

    @Test
    public void autoConfigurationWhenJwkSetUriNullShouldNotFail() {
        this.contextRunner.run(( context) -> assertThat(getBearerTokenFilter(context)).isNull());
    }

    @Test
    public void jwtDecoderByJwkSetUriIsConditionalOnMissingBean() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").withUserConfiguration(OAuth2ResourceServerAutoConfigurationTests.JwtDecoderConfig.class).run(( context) -> assertThat(getBearerTokenFilter(context)).isNotNull());
    }

    @Test
    public void jwtDecoderByOidcIssuerUriIsConditionalOnMissingBean() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.issuer-uri=http://jwk-oidc-issuer-location.com").withUserConfiguration(OAuth2ResourceServerAutoConfigurationTests.JwtDecoderConfig.class).run(( context) -> assertThat(getBearerTokenFilter(context)).isNotNull());
    }

    @Test
    public void autoConfigurationShouldBeConditionalOnJwtAuthenticationTokenClass() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").withUserConfiguration(OAuth2ResourceServerAutoConfigurationTests.JwtDecoderConfig.class).withClassLoader(new FilteredClassLoader(JwtAuthenticationToken.class)).run(( context) -> assertThat(getBearerTokenFilter(context)).isNull());
    }

    @Test
    public void autoConfigurationShouldBeConditionalOnJwtDecoderClass() {
        this.contextRunner.withPropertyValues("spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://jwk-set-uri.com").withUserConfiguration(OAuth2ResourceServerAutoConfigurationTests.JwtDecoderConfig.class).withClassLoader(new FilteredClassLoader(JwtDecoder.class)).run(( context) -> assertThat(getBearerTokenFilter(context)).isNull());
    }

    @Configuration
    @EnableWebSecurity
    static class TestConfig {}

    @Configuration
    @EnableWebSecurity
    static class JwtDecoderConfig {
        @Bean
        public JwtDecoder decoder() {
            return Mockito.mock(JwtDecoder.class);
        }
    }
}

