/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.security.oauth2.client.servlet;


import ClientRegistration.Builder;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link OAuth2WebSecurityConfiguration}.
 *
 * @author Madhura Bhave
 */
public class OAuth2WebSecurityConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void securityConfigurerConfiguresOAuth2Login() {
        this.contextRunner.withUserConfiguration(OAuth2WebSecurityConfigurationTests.ClientRegistrationRepositoryConfiguration.class, OAuth2WebSecurityConfiguration.class).run(( context) -> {
            ClientRegistrationRepository expected = context.getBean(.class);
            ClientRegistrationRepository actual = ((ClientRegistrationRepository) (ReflectionTestUtils.getField(getFilters(context, .class).get(0), "clientRegistrationRepository")));
            assertThat(isEqual(expected.findByRegistrationId("first"), actual.findByRegistrationId("first"))).isTrue();
            assertThat(isEqual(expected.findByRegistrationId("second"), actual.findByRegistrationId("second"))).isTrue();
        });
    }

    @Test
    public void securityConfigurerConfiguresAuthorizationCode() {
        this.contextRunner.withUserConfiguration(OAuth2WebSecurityConfigurationTests.ClientRegistrationRepositoryConfiguration.class, OAuth2WebSecurityConfiguration.class).run(( context) -> {
            ClientRegistrationRepository expected = context.getBean(.class);
            ClientRegistrationRepository actual = ((ClientRegistrationRepository) (ReflectionTestUtils.getField(getFilters(context, .class).get(0), "clientRegistrationRepository")));
            assertThat(isEqual(expected.findByRegistrationId("first"), actual.findByRegistrationId("first"))).isTrue();
            assertThat(isEqual(expected.findByRegistrationId("second"), actual.findByRegistrationId("second"))).isTrue();
        });
    }

    @Test
    public void securityConfigurerBacksOffWhenClientRegistrationBeanAbsent() {
        this.contextRunner.withUserConfiguration(OAuth2WebSecurityConfigurationTests.TestConfig.class, OAuth2WebSecurityConfiguration.class).run(( context) -> {
            assertThat(getFilters(context, .class)).isEmpty();
            assertThat(getFilters(context, .class)).isEmpty();
        });
    }

    @Test
    public void configurationRegistersAuthorizedClientServiceBean() {
        this.contextRunner.withUserConfiguration(OAuth2WebSecurityConfigurationTests.ClientRegistrationRepositoryConfiguration.class, OAuth2WebSecurityConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void configurationRegistersAuthorizedClientRepositoryBean() {
        this.contextRunner.withUserConfiguration(OAuth2WebSecurityConfigurationTests.ClientRegistrationRepositoryConfiguration.class, OAuth2WebSecurityConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void securityConfigurerBacksOffWhenOtherWebSecurityAdapterPresent() {
        this.contextRunner.withUserConfiguration(OAuth2WebSecurityConfigurationTests.TestWebSecurityConfigurerConfig.class, OAuth2WebSecurityConfiguration.class).run(( context) -> {
            assertThat(getFilters(context, .class)).isEmpty();
            assertThat(getFilters(context, .class)).isEmpty();
            assertThat(context).getBean(.class).isNotNull();
        });
    }

    @Test
    public void authorizedClientServiceBeanIsConditionalOnMissingBean() {
        this.contextRunner.withUserConfiguration(OAuth2WebSecurityConfigurationTests.OAuth2AuthorizedClientServiceConfiguration.class, OAuth2WebSecurityConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("testAuthorizedClientService");
        });
    }

    @Test
    public void authorizedClientRepositoryBeanIsConditionalOnMissingBean() {
        this.contextRunner.withUserConfiguration(OAuth2WebSecurityConfigurationTests.OAuth2AuthorizedClientRepositoryConfiguration.class, OAuth2WebSecurityConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("testAuthorizedClientRepository");
        });
    }

    @Configuration
    @EnableWebSecurity
    protected static class TestConfig {
        @Bean
        public TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }
    }

    @Configuration
    @Import(OAuth2WebSecurityConfigurationTests.TestConfig.class)
    static class ClientRegistrationRepositoryConfiguration {
        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            List<ClientRegistration> registrations = new ArrayList<>();
            registrations.add(getClientRegistration("first", "http://user-info-uri.com"));
            registrations.add(getClientRegistration("second", "http://other-user-info"));
            return new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(registrations);
        }

        private ClientRegistration getClientRegistration(String id, String userInfoUri) {
            ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(id);
            builder.clientName("foo").clientId("foo").clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.BASIC).authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE).scope("read").clientSecret("secret").redirectUriTemplate("http://redirect-uri.com").authorizationUri("http://authorization-uri.com").tokenUri("http://token-uri.com").userInfoUri(userInfoUri).userNameAttributeName("login");
            return builder.build();
        }
    }

    @Configuration
    @Import(OAuth2WebSecurityConfigurationTests.ClientRegistrationRepositoryConfiguration.class)
    static class TestWebSecurityConfigurerConfig extends WebSecurityConfigurerAdapter {}

    @Configuration
    @Import(OAuth2WebSecurityConfigurationTests.ClientRegistrationRepositoryConfiguration.class)
    static class OAuth2AuthorizedClientServiceConfiguration {
        @Bean
        public OAuth2AuthorizedClientService testAuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
            return new org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        }
    }

    @Configuration
    @Import(OAuth2WebSecurityConfigurationTests.ClientRegistrationRepositoryConfiguration.class)
    static class OAuth2AuthorizedClientRepositoryConfiguration {
        @Bean
        public OAuth2AuthorizedClientRepository testAuthorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
            return new org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
        }
    }
}

