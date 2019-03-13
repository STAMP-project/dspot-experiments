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
package org.springframework.boot.autoconfigure.security.oauth2.client.reactive;


import ClientRegistration.Builder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import reactor.core.publisher.Flux;


/**
 * Tests for {@link ReactiveOAuth2ClientAutoConfiguration}.
 *
 * @author Madhura Bhave
 */
public class ReactiveOAuth2ClientAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ReactiveOAuth2ClientAutoConfiguration.class));

    private static final String REGISTRATION_PREFIX = "spring.security.oauth2.client.registration";

    @Test
    public void autoConfigurationShouldBackOffForServletEnvironments() {
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(ReactiveOAuth2ClientAutoConfiguration.class)).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void clientRegistrationRepositoryBeanShouldNotBeCreatedWhenPropertiesAbsent() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void clientRegistrationRepositoryBeanShouldBeCreatedWhenPropertiesPresent() {
        this.contextRunner.withPropertyValues(((ReactiveOAuth2ClientAutoConfigurationTests.REGISTRATION_PREFIX) + ".foo.client-id=abcd"), ((ReactiveOAuth2ClientAutoConfigurationTests.REGISTRATION_PREFIX) + ".foo.client-secret=secret"), ((ReactiveOAuth2ClientAutoConfigurationTests.REGISTRATION_PREFIX) + ".foo.provider=github")).run(( context) -> {
            ReactiveClientRegistrationRepository repository = context.getBean(.class);
            ClientRegistration registration = repository.findByRegistrationId("foo").block(Duration.ofSeconds(30));
            assertThat(registration).isNotNull();
            assertThat(registration.getClientSecret()).isEqualTo("secret");
        });
    }

    @Test
    public void authorizedClientServiceBeanIsConditionalOnClientRegistrationRepository() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void configurationRegistersAuthorizedClientServiceBean() {
        this.contextRunner.withUserConfiguration(ReactiveOAuth2ClientAutoConfigurationTests.ReactiveClientRepositoryConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void authorizedClientServiceBeanIsConditionalOnMissingBean() {
        this.contextRunner.withUserConfiguration(ReactiveOAuth2ClientAutoConfigurationTests.ReactiveOAuth2AuthorizedClientRepositoryConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("testAuthorizedClientService");
        });
    }

    @Test
    public void authorizedClientRepositoryBeanIsConditionalOnAuthorizedClientService() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void configurationRegistersAuthorizedClientRepositoryBean() {
        this.contextRunner.withUserConfiguration(ReactiveOAuth2ClientAutoConfigurationTests.ReactiveOAuth2AuthorizedClientServiceConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void authorizedClientRepositoryBeanIsConditionalOnMissingBean() {
        this.contextRunner.withUserConfiguration(ReactiveOAuth2ClientAutoConfigurationTests.ReactiveOAuth2AuthorizedClientRepositoryConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("testAuthorizedClientRepository");
        });
    }

    @Test
    public void autoConfigurationConditionalOnClassFlux() {
        assertWhenClassNotPresent(Flux.class);
    }

    @Test
    public void autoConfigurationConditionalOnClassEnableWebFluxSecurity() {
        assertWhenClassNotPresent(EnableWebFluxSecurity.class);
    }

    @Test
    public void autoConfigurationConditionalOnClassClientRegistration() {
        assertWhenClassNotPresent(ClientRegistration.class);
    }

    @Configuration
    static class ReactiveClientRepositoryConfiguration {
        @Bean
        public ReactiveClientRegistrationRepository clientRegistrationRepository() {
            List<ClientRegistration> registrations = new ArrayList<>();
            registrations.add(getClientRegistration("first", "http://user-info-uri.com"));
            registrations.add(getClientRegistration("second", "http://other-user-info"));
            return new org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository(registrations);
        }

        private ClientRegistration getClientRegistration(String id, String userInfoUri) {
            ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(id);
            builder.clientName("foo").clientId("foo").clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.BASIC).authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE).scope("read").clientSecret("secret").redirectUriTemplate("http://redirect-uri.com").authorizationUri("http://authorization-uri.com").tokenUri("http://token-uri.com").userInfoUri(userInfoUri).userNameAttributeName("login");
            return builder.build();
        }
    }

    @Configuration
    @Import(ReactiveOAuth2ClientAutoConfigurationTests.ReactiveClientRepositoryConfiguration.class)
    static class ReactiveOAuth2AuthorizedClientServiceConfiguration {
        @Bean
        public ReactiveOAuth2AuthorizedClientService testAuthorizedClientService(ReactiveClientRegistrationRepository clientRegistrationRepository) {
            return new org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
        }
    }

    @Configuration
    @Import(ReactiveOAuth2ClientAutoConfigurationTests.ReactiveOAuth2AuthorizedClientServiceConfiguration.class)
    static class ReactiveOAuth2AuthorizedClientRepositoryConfiguration {
        @Bean
        public ServerOAuth2AuthorizedClientRepository testAuthorizedClientRepository(ReactiveOAuth2AuthorizedClientService authorizedClientService) {
            return new org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(authorizedClientService);
        }
    }
}

